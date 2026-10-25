package com.qiuyue.goetyominous.common.entities.ally.pp;

import com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.qiuyue.goetyominous.common.entities.ai.ExpeditionLootChestGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.TravelerKickGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.TravelerSitGoal;
import com.qiuyue.goetyominous.common.entities.ai.pp.LookAtPlayerWhenIdleGoal;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.explore.ExpeditionLootStorage;
import com.qiuyue.goetyominous.common.explore.IExpeditionLootHolder;
import com.qiuyue.goetyominous.common.init.pp.PpEntityRegistry;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import com.qiuyue.goetyominous.config.pp.PpConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import tallestred.piglinproliferation.client.PPSounds;
import tallestred.piglinproliferation.common.entities.PPEntityTypes;
import tallestred.piglinproliferation.common.entities.PiglinTraveler;
import tallestred.piglinproliferation.common.items.TravelersCompassItem;
import tallestred.piglinproliferation.common.loot.PPLoot;
import tallestred.piglinproliferation.common.tags.EitherTag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PiglinTravelerServant extends AbstractPiglinServant implements CrossbowAttackMob, TravelerAnimatable, IExpeditionLootHolder {

    protected static final EntityDataAccessor<Integer> KICK_TICKS =
            SynchedEntityData.defineId(PiglinTravelerServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> KICK_COOLDOWN =
            SynchedEntityData.defineId(PiglinTravelerServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(PiglinTravelerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(PiglinTravelerServant.class, EntityDataSerializers.BOOLEAN);
    public static final int DEFAULT_EXPIRY_TIME = 24000;

    public Map<EitherTag.Location, Integer> alreadyLocatedObjects = new HashMap<>();
    @Nullable
    public Map.Entry<EitherTag.Location, BlockPos> currentlyLocatedObject;
    private final List<ItemStack> expeditionLoot = new ArrayList<>();

    private ModMeleeAttackGoal meleeGoal;
    private BackawayCrossbowGoal<PiglinTravelerServant> crossbowGoal;

    public PiglinTravelerServant(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, PpConfig.PiglinTravelerServantHealth.get())
                .add(Attributes.ARMOR, PpConfig.PiglinTravelerServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, PpConfig.PiglinTravelerServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, PpConfig.PiglinTravelerServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, PpConfig.PiglinTravelerServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(KICK_TICKS, 0);
        this.entityData.define(KICK_COOLDOWN, 0);
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.meleeGoal = new ModMeleeAttackGoal(this, 1.0D, true);
        this.crossbowGoal = new BackawayCrossbowGoal<>(this, 1.0D, 15.0F);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, this.crossbowGoal);
        this.goalSelector.addGoal(2, new TravelerKickGoal<>(this));
        this.goalSelector.addGoal(4, new ExpeditionLootChestGoal<>(this));
        this.goalSelector.addGoal(6, new TravelerSitGoal<>(this));
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerWhenIdleGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerWhenIdleGoal(this, Mob.class, 15.0F));
    }

    @Override
    public List<ItemStack> getExpeditionLoot() {
        return this.expeditionLoot;
    }

    @Override
    public void addExpeditionLoot(ItemStack stack) {
        if (!stack.isEmpty()) {
            this.expeditionLoot.add(stack.copy());
        }
    }

    @Override
    public void clearExpeditionLoot() {
        this.expeditionLoot.clear();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!this.level().isClientSide && held.isEmpty() && player.getUUID().equals(this.getOwnerId())
                && this.hasExpeditionLoot() && !this.hasUsableChestLink()) {
            int count = 0;
            for (ItemStack stack : this.expeditionLoot) {
                if (!stack.isEmpty()) {
                    BehaviorUtils.throwItem(this, stack.copy(), player.position());
                    count++;
                }
            }
            this.expeditionLoot.clear();
            if (count > 0) {
                player.displayClientMessage(Component.translatable("message.goetyominous.expedition.lootHanded", count), true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected boolean isAcceptedWeapon(Item item) {
        return item instanceof CrossbowItem || item instanceof SwordItem || item instanceof PickaxeItem;
    }

    public void reassessWeaponGoal() {
        if (!this.level().isClientSide && this.meleeGoal != null && this.crossbowGoal != null) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.crossbowGoal);
            if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
                this.goalSelector.addGoal(2, this.crossbowGoal);
            } else {
                this.goalSelector.addGoal(2, this.meleeGoal);
            }
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            this.reassessWeaponGoal();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, random.nextFloat() < PpConfig.PiglinTravelerServantCrossbowChance.get()
                ? new ItemStack(Items.CROSSBOW)
                : new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected void customServerAiStep() {
        if (this.getKickCoolDown() > 0) {
            this.setKickCoolDown(this.getKickCoolDown() - 1);
        }
        if (this.getKickTicks() > 0) {
            this.setKickTicks(this.getKickTicks() - 1);
        }
        this.alreadyLocatedObjects.replaceAll((key, value) -> value - 1);
        this.alreadyLocatedObjects.entrySet().removeIf(entry -> entry.getValue() <= 0);
        super.customServerAiStep();
    }

    @Nullable
    @Override
    protected EntityType<? extends Mob> getZombieVariantType() {
        return PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT.get();
    }

    @Override
    protected void afterZombieConversion(Mob zombie) {
        if (zombie instanceof ZPiglinTravelerServant zombified) {
            zombified.setItemSlot(EquipmentSlot.MAINHAND, this.getMainHandItem().copy());
        }
    }

    @Override
    protected EntityType<? extends Mob> getConversionTarget() {
        return PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT.get();
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        ZPiglinTravelerServant zombified = new ZPiglinTravelerServant(
                PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.setBaby(this.isBaby());
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        zombified.setItemSlot(EquipmentSlot.MAINHAND, this.getMainHandItem().copy());
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    protected void playAdmireSound() {
        this.playSound(PPSounds.TRAVELER_ADMIRE.get(), 1.0F, 1.0F);
    }

    @Override
    protected void startAdmiring() {
        super.startAdmiring();
        this.sit(false);
    }

    @Override
    protected void onBarterLoot(ItemStack drop) {
        if (drop.getItem() instanceof TravelersCompassItem) {
            this.playBarteringAnimation();
            this.playSound(PPSounds.MAKE_COMPASS.get(), 1.0F, 1.0F);
        }
    }

    public void playBarteringAnimation() {
        this.swing(InteractionHand.MAIN_HAND, true);
        Vec3 vec3 = new Vec3((this.random.nextDouble() - 0.5D) * 0.1D, this.random.nextDouble() * 0.1D + 0.1D, (this.random.nextDouble() - 0.5D) * 0.1D);
        vec3 = vec3.xRot(-this.getXRot() * ((float) Math.PI / 180.0F));
        vec3 = vec3.yRot(-this.getYRot() * ((float) Math.PI / 180.0F));
        double d0 = -this.random.nextDouble() * 0.6D - 0.3D;
        Vec3 vec31 = new Vec3((this.random.nextDouble() - 0.5D) * 0.8D, d0, 1.0D + (this.random.nextDouble() - 0.5D) * 0.4D);
        vec31 = vec31.yRot(-this.yBodyRot * ((float) Math.PI / 180.0F));
        vec31 = vec31.add(this.getX(), this.getEyeY() + 1.0D, this.getZ());
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.GOLD_INGOT));
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
        } else {
            this.level().addParticle(particle, vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }
    }

    @Override
    protected List<ItemStack> rollBarterLoot(ServerLevel serverLevel) {
        PiglinTraveler proxy = new PiglinTraveler(PPEntityTypes.PIGLIN_TRAVELER.get(), serverLevel);
        proxy.moveTo(this.getX(), this.getY(), this.getZ());
        proxy.alreadyLocatedObjects = new HashMap<>(this.alreadyLocatedObjects);
        this.currentlyLocatedObject = null;
        proxy.currentlyLocatedObject = null;
        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, proxy)
                .withParameter(LootContextParams.ORIGIN, proxy.position())
                .create(LootContextParamSets.PIGLIN_BARTER);
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(PPLoot.TRAVELER_BARTER);
        List<ItemStack> drops = lootTable.getRandomItems(lootParams);
        this.alreadyLocatedObjects = new HashMap<>(proxy.alreadyLocatedObjects);
        this.currentlyLocatedObject = proxy.currentlyLocatedObject;
        proxy.currentlyLocatedObject = null;
        return drops;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag map = new CompoundTag();
        this.alreadyLocatedObjects.forEach((key, value) -> map.putInt(key.serialise(), value));
        compound.put("AlreadyLocatedObjects", map);
        ExpeditionLootStorage.write(compound, this.expeditionLoot);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.alreadyLocatedObjects = new HashMap<>();
        CompoundTag map = compound.getCompound("AlreadyLocatedObjects");
        for (String key : map.getAllKeys()) {
            this.alreadyLocatedObjects.put(EitherTag.Location.deserialise(key), map.getInt(key));
        }
        this.expeditionLoot.clear();
        this.expeditionLoot.addAll(ExpeditionLootStorage.read(compound));
        this.reassessWeaponGoal();
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void sit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }

    public int getKickTicks() {
        return this.entityData.get(KICK_TICKS);
    }

    public void setKickTicks(int kickTicks) {
        this.entityData.set(KICK_TICKS, kickTicks);
    }

    public int getKickCoolDown() {
        return this.entityData.get(KICK_COOLDOWN);
    }

    public void setKickCoolDown(int kickCoolDown) {
        this.entityData.set(KICK_COOLDOWN, kickCoolDown);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float angle) {
        this.shootCrossbowProjectile(this, target, projectile, angle, 1.6F);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof CrossbowItem;
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, charging);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public PiglinArmPose getArmPose() {
        if (this.getOffhandItem().is(Items.GOLD_INGOT)) {
            return PiglinArmPose.ADMIRING_ITEM;
        }
        if (this.isChargingCrossbow()) {
            return PiglinArmPose.CROSSBOW_CHARGE;
        }
        if (this.isAggressive() && this.getMainHandItem().getItem() instanceof CrossbowItem) {
            return PiglinArmPose.CROSSBOW_HOLD;
        }
        return PiglinArmPose.DEFAULT;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(PpItems.PIGLIN_TRAVELER_SERVANT_SPAWN_EGG.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return PPSounds.TRAVELER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return PPSounds.TRAVELER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PPSounds.TRAVELER_DEATH.get();
    }

    @Override
    protected void playConvertedSound() {
        this.playSound(PPSounds.TRAVELER_CONVERTED.get(), this.getSoundVolume(), this.getVoicePitch() / 0.10F);
    }
}
