package com.qiuyue.goetyominus.common.entities.ally.mobs;

import com.qiuyue.goetyominus.common.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class PiglinMerchant extends PathfinderMob implements Merchant {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_PLAYING_SEE =
            net.minecraft.network.syncher.SynchedEntityData.defineId(PiglinMerchant.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_PLAYING_SEE2 =
            net.minecraft.network.syncher.SynchedEntityData.defineId(PiglinMerchant.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);

    public final AnimationState seeAnimationState = new AnimationState();
    public final AnimationState see2AnimationState = new AnimationState();
    public final AnimationState restAnimationState = new AnimationState();
    public final AnimationState rest2AnimationState = new AnimationState();

    private final java.util.Set<java.util.UUID> angryPlayers = new java.util.HashSet<>();
    private boolean tradedRecently;
    private int restTimer;
    private boolean playingRest2;
    private int see2Cooldown;
    private int see2Timer;
    private int seeTimer;
    private int tradeSoundCooldown;
    private net.minecraft.world.item.trading.MerchantOffers offers;
    private Player tradingPlayer;
    private final java.util.Map<MerchantOffer, Integer> baseCosts = new java.util.HashMap<>();
    private final java.util.Map<MerchantOffer, Integer> offerTimers = new java.util.HashMap<>();


    public PiglinMerchant(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setCustomNameVisible(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYING_SEE, false);
        this.entityData.define(DATA_PLAYING_SEE2, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @javax.annotation.Nullable
    private static net.minecraft.world.item.ItemStack getModItem(String modId, String itemId) {
        try {
            net.minecraft.world.item.Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                    new net.minecraft.resources.ResourceLocation(modId, itemId));
            return item == null || item == net.minecraft.world.item.Items.AIR ? null : new ItemStack(item);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;

        if (player.getAbilities().instabuild) {
        } else if (this.angryPlayers.contains(player.getUUID())) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(Items.GOLD_BLOCK)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                this.angryPlayers.remove(player.getUUID());
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.PIGLIN_CELEBRATE, this.getSoundSource(), 1.0F, 1.0F);
                if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1.0, this.getZ(),
                            7, 0.5, 0.5, 0.5, 0.0);
                }
            } else {
                this.entityData.set(DATA_PLAYING_SEE, true);
                return InteractionResult.FAIL;
            }
        }

        this.openTradingScreen(player, this.getDisplayName(), 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setTradingPlayer(@javax.annotation.Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            java.util.List<MerchantOffer> allOffers = new java.util.ArrayList<>();
            this.baseCosts.clear();

            addTrade(allOffers, Items.GOLD_INGOT, 2, Items.NETHER_WART, 3, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 1, Items.COOKED_PORKCHOP, 3, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 4, Items.GHAST_TEAR, 1, 6);
            addTrade(allOffers, Items.GOLD_INGOT, 1, Items.MAGMA_CREAM, 2, 8);
            addTrade(allOffers, Items.GOLD_INGOT, 1, Items.FERMENTED_SPIDER_EYE, 1, 8);
            addTrade(allOffers, Items.GOLD_INGOT, 12, Items.ENDER_PEARL, 3, 4);
            addTrade(allOffers, Items.GOLD_INGOT, 1, Items.BLAZE_ROD, 1, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 10, ModItems.ACID_FUNGUS.get(), 3, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 20, Items.PIGLIN_BANNER_PATTERN, 1, 1);
            addTrade(allOffers, Items.GOLD_INGOT, 2, Items.OBSIDIAN, 1, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 2, Items.GOLDEN_CARROT, 6, 6);
            addTrade(allOffers, Items.GOLD_INGOT, 3, Items.CRYING_OBSIDIAN, 1, 4);
            addTrade(allOffers, Items.GOLD_INGOT, 2, Items.SPECTRAL_ARROW, 4, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 1, Items.QUARTZ, 6, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 2, Items.WITHER_ROSE, 1, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 3, com.Polarice3.Goety.common.items.ModItems.SNAP_FUNGUS.get(), 1, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 6, com.Polarice3.Goety.common.items.ModItems.BLAST_FUNGUS.get(), 1, 12);
            addTrade(allOffers, Items.GOLD_INGOT, 6, com.Polarice3.Goety.common.items.ModItems.BERSERK_FUNGUS.get(), 1, 12);
            addTrade(allOffers, Items.GOLD_BLOCK, 5, Items.WITHER_SKELETON_SKULL, 1, 6);
            addTrade(allOffers, Items.GOLD_BLOCK, 4, Items.LODESTONE, 1, 6);

            addEnchantedBookTrade(allOffers, net.minecraft.world.item.enchantment.Enchantments.SOUL_SPEED, 3, Items.GOLD_INGOT, 20, 2);

            addEnchantedToolTrade(allOffers, Items.GOLDEN_SWORD, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_PICKAXE, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_AXE, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_SHOVEL, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_HOE, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_HELMET, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_CHESTPLATE, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_LEGGINGS, Items.GOLD_BLOCK, 5, 1);
            addEnchantedToolTrade(allOffers, Items.GOLDEN_BOOTS, Items.GOLD_BLOCK, 5, 1);

            addEnchantedToolTrade(allOffers, Items.NETHERITE_SWORD, Items.NETHERITE_INGOT, 2, 1);
            addEnchantedToolTrade(allOffers, Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT, 2, 1);
            addEnchantedToolTrade(allOffers, Items.NETHERITE_AXE, Items.NETHERITE_INGOT, 2, 1);
            addEnchantedToolTrade(allOffers, Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT, 2, 1);
            addEnchantedToolTrade(allOffers, Items.NETHERITE_HOE, Items.NETHERITE_INGOT, 2, 1);

            addModTrade(allOffers, Items.GOLD_INGOT, 3, "alexsmobs", "maggot", 5, 1000);
            addModTrade(allOffers, Items.GOLD_INGOT, 20, "alexsmobs", "bone_serpent_tooth", 1, 4);
            addModTrade(allOffers, Items.GOLD_INGOT, 10, "alexsmobs", "mosquito_proboscis", 1, 3);
            addModTrade(allOffers, Items.GOLD_INGOT, 5, "alexsmobs", "blood_sac", 1, 6);
            addModTrade(allOffers, Items.GOLD_INGOT, 10, "alexsmobs", "stradpole_bucket", 1, 1);
            addModTrade(allOffers, Items.GOLD_INGOT, 20, "alexsmobs", "straddlite", 1, 4);

            addModTrade(allOffers, Items.GOLD_INGOT, 3, "mynethersdelight", "powder_cannon", 4, 12);
            addModTrade(allOffers, Items.GOLD_INGOT, 10, "mynethersdelight", "hoglin_hide", 3, 6);
            addModTrade(allOffers, Items.GOLD_INGOT, 20, "mynethersdelight", "resurgent_soil", 3, 4);

            addModTrade(allOffers, Items.GOLD_INGOT, 10, "irons_spellbooks", "netherward_tincture", 1, 1);
            addModTrade(allOffers, Items.GOLD_INGOT, 30, "irons_spellbooks", "fire_ale", 1, 3);
            addModTrade(allOffers, Items.GOLD_INGOT, 20, "irons_spellbooks", "blood_vial", 3, 4);
            addModTrade(allOffers, Items.GOLD_INGOT, 20, "irons_spellbooks", "hogskin", 3, 6);
            addModTrade(allOffers, Items.GOLD_INGOT, 25, "irons_spellbooks", "bloody_vellum", 3, 4);
            addModTrade(allOffers, Items.GOLD_BLOCK, 5, "irons_spellbooks", "cinder_essence", 2, 3);

            java.util.Collections.shuffle(allOffers, new java.util.Random(this.random.nextInt()));
            this.offers = new MerchantOffers();
            for (int i = 0; i < Math.min(15, allOffers.size()); i++) {
                this.offers.add(allOffers.get(i));
            }
        }
        return this.offers;
    }

    private void addTrade(java.util.List<MerchantOffer> offers, Item costItem, int costCount, Item resultItem, int resultCount, int maxUses) {
        MerchantOffer offer = new MerchantOffer(
                new ItemStack(costItem, costCount),
                ItemStack.EMPTY,
                new ItemStack(resultItem, resultCount),
                maxUses, 0, 0.0F);
        this.baseCosts.put(offer, costCount);
        offers.add(offer);
    }

    private void addEnchantedBookTrade(java.util.List<MerchantOffer> offers, net.minecraft.world.item.enchantment.Enchantment enchantment, int level, Item costItem, int costCount, int maxUses) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.enchant(enchantment, level);
        MerchantOffer offer = new MerchantOffer(
                new ItemStack(costItem, costCount),
                ItemStack.EMPTY,
                book,
                maxUses, 0, 0.0F);
        offers.add(offer);
    }

    private void addModTrade(java.util.List<MerchantOffer> offers, Item costItem, int costCount, String modId, String itemId, int resultCount, int maxUses) {
        ItemStack result = getModItem(modId, itemId);
        if (result == null) return;
        result.setCount(resultCount);
        MerchantOffer offer = new MerchantOffer(
                new ItemStack(costItem, costCount),
                ItemStack.EMPTY,
                result,
                maxUses, 0, 0.0F);
        this.baseCosts.put(offer, costCount);
        offers.add(offer);
    }

    private void addEnchantedToolTrade(java.util.List<MerchantOffer> offers, Item tool, Item costItem, int costCount, int maxUses) {
        ItemStack toolStack = new ItemStack(tool);
        net.minecraft.world.item.enchantment.EnchantmentHelper.enchantItem(
                this.random, toolStack, 30, false);
        MerchantOffer offer = new MerchantOffer(
                new ItemStack(costItem, costCount),
                new ItemStack(tool),
                toolStack,
                maxUses, 0, 0.0F);
        offers.add(offer);
    }

    @Override
    public void overrideOffers(net.minecraft.world.item.trading.MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();

        if (offer.getUses() >= offer.getMaxUses()) {
            this.offerTimers.put(offer, 24000);
        }

        Integer baseCount = this.baseCosts.get(offer);
        if (baseCount != null && baseCount + offer.getUses() <= 64) {
            offer.getCostA().setCount(baseCount + offer.getUses());
        }

        if (!this.seeAnimationState.isStarted()) {
            this.onTradeSuccess();
        }

        if (this.tradeSoundCooldown <= 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.PIGLIN_CELEBRATE, this.getSoundSource(), 1.0F, 1.0F);
            this.tradeSoundCooldown = 5;
        }

        if (this.tradingPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendMerchantOffers(
                    serverPlayer.containerMenu.containerId,
                    this.offers,
                    0, 0, true, true);
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide && this.tradingPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendMerchantOffers(
                    serverPlayer.containerMenu.containerId,
                    this.getOffers(),
                    0, 0, false, false);
        }
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public void openTradingScreen(Player player, Component title, int level) {
        this.setTradingPlayer(player);
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new MerchantMenu(id, inv, this),
                title)).ifPresent(id -> {
            if (player instanceof ServerPlayer sp) {
                sp.sendMerchantOffers(id, this.getOffers(), 0, 0, true, true);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            setupAnimationStates();
        } else if (this.offers != null) {
            if (this.tradeSoundCooldown > 0) this.tradeSoundCooldown--;

            java.util.Iterator<java.util.Map.Entry<MerchantOffer, Integer>> it = this.offerTimers.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                int time = entry.getValue() - 1;
                if (time <= 0) {
                    MerchantOffer offer = entry.getKey();
                    offer.resetUses();
                    Integer baseCount = this.baseCosts.get(offer);
                    if (baseCount != null) {
                        offer.getCostA().setCount(baseCount);
                    }
                    it.remove();
                } else {
                    entry.setValue(time);
                }
            }
        }
    }

    private void setupAnimationStates() {
        boolean moving = this.walkAnimation.isMoving();

        if (moving) {
            stopAllAnimations();
            return;
        }

        if (this.entityData.get(DATA_PLAYING_SEE)) {
            this.seeAnimationState.startIfStopped(this.tickCount);
            this.seeTimer++;
            if (this.seeTimer > 40) {
                this.seeAnimationState.stop();
                this.seeTimer = 0;
                this.entityData.set(DATA_PLAYING_SEE, false);
            }
        }

        if (this.entityData.get(DATA_PLAYING_SEE2)) {
            this.see2AnimationState.startIfStopped(this.tickCount);
            this.see2Timer++;
            if (this.see2Timer > 30) {
                this.see2AnimationState.stop();
                this.see2Timer = 0;
                this.entityData.set(DATA_PLAYING_SEE2, false);
            }
        }

        if (this.see2Cooldown > 0) {
            this.see2Cooldown--;
        }

        if (--this.restTimer <= 0) {
            this.restAnimationState.stop();
            this.rest2AnimationState.stop();
            if (this.random.nextInt(3) == 0) {
                this.playingRest2 = !this.playingRest2;
                if (this.playingRest2) {
                    this.rest2AnimationState.start(this.tickCount);
                } else {
                    this.restAnimationState.start(this.tickCount);
                }
            }
            this.restTimer = 100 + this.random.nextInt(200);
        }
    }

    private void stopAllAnimations() {
        this.seeAnimationState.stop();
        this.see2AnimationState.stop();
        this.restAnimationState.stop();
        this.rest2AnimationState.stop();
    }

    public static boolean isWearingGold(Player player) {
        for (ItemStack armor : player.getInventory().armor) {
            if (!armor.isEmpty() && armor.is(net.minecraft.tags.ItemTags.PIGLIN_LOVED)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTradedRecently() { return this.tradedRecently; }
    public boolean isPlayingRest2() { return this.playingRest2; }
    public boolean isPlayerNearbyWithGold() {
        return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0))
                .stream().anyMatch(PiglinMerchant::isWearingGold);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            this.angryPlayers.add(player.getUUID());
            this.entityData.set(DATA_PLAYING_SEE, true);
        }
        return super.hurt(source, amount);
    }

    public void onTradeSuccess() {
        if (this.see2Cooldown > 0) return;
        this.see2Cooldown = 600;
        this.entityData.set(DATA_PLAYING_SEE2, true);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.offers != null) {
            tag.put("Offers", this.offers.createTag());
        }
        net.minecraft.nbt.ListTag angryList = new net.minecraft.nbt.ListTag();
        for (java.util.UUID uuid : this.angryPlayers) {
            angryList.add(net.minecraft.nbt.NbtUtils.createUUID(uuid));
        }
        tag.put("AngryPlayers", angryList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Offers")) {
            this.offers = new MerchantOffers(tag.getCompound("Offers"));
        }
        this.angryPlayers.clear();
        net.minecraft.nbt.ListTag list = tag.getList("AngryPlayers", 11);
        for (int i = 0; i < list.size(); i++) {
            this.angryPlayers.add(net.minecraft.nbt.NbtUtils.loadUUID(list.get(i)));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_DEATH;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.PIGLIN_JEALOUS;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.PIGLIN_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }
}
