package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.magic.spells.SoulHealSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

public class ZInquillagerServant extends ZombieServant {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData
            .defineId(ZInquillagerServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING = SynchedEntityData
            .defineId(ZInquillagerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CASTING = SynchedEntityData
            .defineId(ZInquillagerServant.class, EntityDataSerializers.BOOLEAN);
    public static final Map<Integer, ResourceLocation> ZOMBIE_TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager_2.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager_3.png"));
    });
    public int coolDown;
    public int healTimes;
    public int villagerConversionTime;
    protected int spellCastingTickCount;
    @Nullable
    private UUID conversionStarter;

    public ZInquillagerServant(EntityType<? extends ZombieServant> type, Level level) {
        super(type, level);
        this.coolDown = 0;
        this.healTimes = 0;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.InquillagerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.InquillagerArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.InquillagerDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.InquillagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.InquillagerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.InquillagerDamage.get());
    }

    public ResourceLocation getResourceLocation() {
        return ZOMBIE_TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), ZOMBIE_TEXTURE_BY_TYPE.get(0));
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        return ModEntityTypes.ZINQUILLAGER_SERVANT.get();
    }

    @Override
    public EntityType<?> getVariant(@Nullable Player player, Level level, BlockPos blockPos) {
        return ModEntityTypes.ZINQUILLAGER_SERVANT.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(DATA_CONVERTING, false);
        this.entityData.define(DATA_CASTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Cooldown", this.coolDown);
        compound.putInt("HealTimes", this.healTimes);
        compound.putInt("SpellTicks", this.spellCastingTickCount);
        compound.putInt("Outfit", this.getOutfitType());
        compound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            compound.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.coolDown = compound.getInt("Cooldown");
        this.healTimes = compound.getInt("HealTimes");
        this.spellCastingTickCount = compound.getInt("SpellTicks");
        if (compound.contains("Outfit")) {
            this.setOutfitType(compound.getInt("Outfit"));
        }
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null,
                    compound.getInt("ConversionTime"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.spellParticles();
        if (this.coolDown > 0) {
            --this.coolDown;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isAlive() && this.isConverting()) {
                this.villagerConversionTime -= this.getConversionProgress();
                if (this.villagerConversionTime <= 0
                        && ForgeEventFactory.canLivingConvert(this, ModEntityTypes.INQUILLAGER_SERVANT.get(),
                        timer -> this.villagerConversionTime = timer)) {
                    this.finishConversion(serverLevel);
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new HealingSelfSpellGoal());
        this.goalSelector.addGoal(2, new ThrowPotionGoal(this));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }
    }

    public void spellParticles() {
        if (this.level().isClientSide && this.isCastingSpell()) {
            double d0 = 0.7D;
            double d1 = 0.7D;
            double d2 = 0.8D;
            float f = this.yBodyRot * ((float) Math.PI / 180.0F)
                    + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double) f1 * 0.6D,
                    this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, d0, d1, d2);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double) f1 * 0.6D,
                    this.getY() + 1.8D, this.getZ() - (double) f2 * 0.6D, d0, d1, d2);
        }
    }

    public boolean isCastingSpell() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_CASTING);
        }
        return this.spellCastingTickCount > 0;
    }

    public void setCastingSpell(boolean casting) {
        this.entityData.set(DATA_CASTING, casting);
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING;
        }
        return this.isAggressive() ? AbstractIllagerServant.IllagerServantArmPose.ATTACKING
                : AbstractIllagerServant.IllagerServantArmPose.NEUTRAL;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        if (this.canSpawnArmor()) {
            super.populateDefaultEquipmentSlots(randomSource, difficulty);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    protected void enchantSpawnedWeapon(RandomSource randomSource, float difficulty) {
        super.enchantSpawnedWeapon(randomSource, difficulty);
        ItemStack itemstack = this.getMainHandItem();
        if (itemstack.getItem() == Items.IRON_SWORD) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.FIRE_ASPECT, 2);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return spawnGroupData;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.canConvert() && stack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isUnderWaterConverting() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean childZombie) {
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    public boolean canConvert() {
        return this.isNatural();
    }

    public boolean isConverting() {
        return this.entityData.get(DATA_CONVERTING);
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int type) {
        if (type < 0 || type >= this.OutfitTypeNumber() + 1) {
            type = this.random.nextInt(this.OutfitTypeNumber());
        }
        this.entityData.set(DATA_TYPE_ID, type);
    }

    public int OutfitTypeNumber() {
        return ZOMBIE_TEXTURE_BY_TYPE.size();
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return this.coolDown;
    }

    public void increaseHealTimes() {
        ++this.healTimes;
    }

    public void setHealTimes(int healTimes) {
        this.healTimes = healTimes;
    }

    public int getHealTimes() {
        return this.healTimes;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    private void startConverting(@Nullable UUID starter, int time) {
        this.conversionStarter = starter;
        this.villagerConversionTime = time;
        this.entityData.set(DATA_CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time,
                Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte) 16);
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 16) {
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(),
                        1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    private void finishConversion(ServerLevel level) {
        Player player = this.conversionStarter != null ? level.getPlayerByUUID(this.conversionStarter) : null;
        if (MobUtil.convertTo(this, ModEntityTypes.INQUILLAGER_SERVANT.get(), true, player)
                instanceof InquillagerServant servant) {
            servant.finalizeSpawn(level, level.getCurrentDifficultyAt(servant.blockPosition()),
                    MobSpawnType.CONVERSION, null, null);
            servant.setOutfitType(this.getOutfitType());
            servant.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            if (!this.isSilent()) {
                level.levelEvent(null, 1027, this.blockPosition(), 0);
            }
            ForgeEventFactory.onLivingConvert(this, servant);
        }
    }

    private int getConversionProgress() {
        int progress = 1;
        if (this.random.nextFloat() < 0.01F) {
            int count = 0;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = (int) this.getX() - 4; x < (int) this.getX() + 4 && count < 14; ++x) {
                for (int y = (int) this.getY() - 4; y < (int) this.getY() + 4 && count < 14; ++y) {
                    for (int z = (int) this.getZ() - 4; z < (int) this.getZ() + 4 && count < 14; ++z) {
                        BlockState state = this.level().getBlockState(pos.set(x, y, z));
                        if (state.is(Blocks.IRON_BARS) || state.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++progress;
                            }
                            ++count;
                        }
                    }
                }
            }
        }
        return progress;
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return ZInquillagerServant.this.getSpellCastingTime() > 0;
        }

        @Override
        public void start() {
            super.start();
            ZInquillagerServant.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            super.stop();
            ZInquillagerServant.this.setCastingSpell(false);
        }

        @Override
        public void tick() {
            LivingEntity target = ZInquillagerServant.this.getTarget();
            if (target != null) {
                ZInquillagerServant.this.getLookControl().setLookAt(target,
                        (float) ZInquillagerServant.this.getMaxHeadYRot(),
                        (float) ZInquillagerServant.this.getMaxHeadXRot());
            }
        }
    }

    class HealingSelfSpellGoal extends Goal {
        private int attackWarmupDelay;
        private int nextAttackTickCount;

        @Override
        public boolean canUse() {
            LivingEntity target = ZInquillagerServant.this.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            if (ZInquillagerServant.this.isCastingSpell()) {
                return false;
            }
            if (ZInquillagerServant.this.getHealth() >= ZInquillagerServant.this.getMaxHealth() / 2.0F) {
                return false;
            }
            if (ZInquillagerServant.this.getCoolDown() > 0) {
                return false;
            }
            return ZInquillagerServant.this.tickCount >= this.nextAttackTickCount;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = ZInquillagerServant.this.getTarget();
            return target != null && target.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(40);
            this.nextAttackTickCount = ZInquillagerServant.this.tickCount + 20;
            ZInquillagerServant.this.spellCastingTickCount = 40;
            ZInquillagerServant.this.setCastingSpell(true);
            ZInquillagerServant.this.getNavigation().stop();
            ZInquillagerServant.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                ZInquillagerServant.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
            }
        }

        protected void performSpellCasting() {
            if (ZInquillagerServant.this.level() instanceof ServerLevel) {
                new SoulHealSpell().mobSpellResult(ZInquillagerServant.this, ItemStack.EMPTY);
                if (ZInquillagerServant.this.getHealTimes() > 3) {
                    ZInquillagerServant.this.setHealTimes(0);
                    ZInquillagerServant.this.setCoolDown(1000);
                } else {
                    ZInquillagerServant.this.increaseHealTimes();
                    ZInquillagerServant.this.setCoolDown(200);
                }
            }
        }
    }

    static class ThrowPotionGoal extends Goal {
        public int bombTimer;
        public ZInquillagerServant inquillagerservant;

        public ThrowPotionGoal(ZInquillagerServant inquillagerservant) {
            this.inquillagerservant = inquillagerservant;
        }

        @Override
        public boolean canUse() {
            if (this.inquillagerservant.getTarget() != null) {
                LivingEntity livingEntity = this.inquillagerservant.getTarget();
                return this.inquillagerservant.distanceTo(livingEntity) > 4.0
                        && this.inquillagerservant.distanceTo(livingEntity) <= 10
                        && this.inquillagerservant.getSensing().hasLineOfSight(livingEntity);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.inquillagerservant.getTarget() != null && !this.inquillagerservant.getTarget().isDeadOrDying();
        }

        @Override
        public void stop() {
            this.bombTimer = 0;
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingEntity = this.inquillagerservant.getTarget();
            if (livingEntity != null) {
                ++this.bombTimer;
                if (this.bombTimer >= 60) {
                    Vec3 vector3d = livingEntity.getDeltaMovement();
                    double d0 = livingEntity.getX() + vector3d.x - this.inquillagerservant.getX();
                    double d1 = livingEntity.getEyeY() - (double) 1.1F - this.inquillagerservant.getY();
                    double d2 = livingEntity.getZ() + vector3d.z - this.inquillagerservant.getZ();
                    float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    Potion potion;
                    if (livingEntity.isInvertedHealAndHarm()) {
                        potion = Potions.HEALING;
                    } else {
                        potion = Potions.HARMING;
                    }
                    ThrownPotion potionentity = new ThrownPotion(this.inquillagerservant.level(),
                            this.inquillagerservant);
                    potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
                    potionentity.setXRot(potionentity.getXRot() - -20.0F);
                    potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
                    if (!this.inquillagerservant.isSilent()) {
                        this.inquillagerservant.level().playSound((Player) null, this.inquillagerservant.getX(),
                                this.inquillagerservant.getY(), this.inquillagerservant.getZ(), SoundEvents.WITCH_THROW,
                                this.inquillagerservant.getSoundSource(), 1.0F,
                                0.8F + this.inquillagerservant.random.nextFloat() * 0.4F);
                    }
                    this.inquillagerservant.level().addFreshEntity(potionentity);
                    this.bombTimer = 0;
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }
}
