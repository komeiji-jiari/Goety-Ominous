package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.alexander.mutantmore.config.mutant_jungle_zombie.MutantJungleZombieRewardsCommonConfig;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.entities.MutantHusk;
import com.alexander.mutantmore.entities.SentryVine;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class AreaDamage extends Entity {
    private static final EntityDataAccessor<Float> SIZE;
    private static final EntityDataAccessor<Float> SIZE_TO_REACH;
    private static final EntityDataAccessor<Float> GROW_SPEED;
    private static final EntityDataAccessor<Float> Y_SIZE;
    private static final EntityDataAccessor<Float> Y_KNOCKBACK;
    private static final EntityDataAccessor<Integer> TYPE;
    private static final EntityDataAccessor<Integer> ACTIVE_TYPE;
    private static final EntityDataAccessor<Integer> EXTRA_TIME;
    private static final EntityDataAccessor<Integer> START_DELAY;
    private static final EntityDataAccessor<Boolean> DIRECTIONAL_MOTION;
    private static final EntityDataAccessor<String> SAND_TYPE;
    private static final EntityDataAccessor<BlockPos> SENT_FROM;
    public float damage;
    public DamageSource damageSource = this.damageSources().generic();
    public LivingEntity owner;
    public boolean constantDamage;
    public List<Entity> damagedEntities = Lists.newArrayList();
    public List<Entity> blockedByEntities = Lists.newArrayList();
    public boolean friendlyFire;
    public double knockbackAmount;
    public boolean disableShields;
    public int disableShieldTime;
    public int extraTimeTick;
    public boolean ignoresInvulTime;
    public TagKey<EntityType<?>> cantHurtTag;
    public List<com.alexander.mutantmore.entities.AreaDamage> connectedAreaDamages = Lists.newArrayList();

    public int witherBreathBuffLevelBonus = 0;
    public boolean witherBreathUsingNetherStaff = false;

    public AreaDamage(EntityType<? extends AreaDamage> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public static AreaDamage spawnAreaDamage(Level level, Vec3 pos, LivingEntity owner, float damage, net.minecraft.world.damagesource.DamageSource damageSource, float size, float sizeToReach, float growSpeed, float ySize, int extraTime, int startDelay, boolean constantDamage, boolean friendlyFire, double knockbackAmount, double knockbackAmountY, boolean directionalMotion, boolean disableShields, int disableShieldTime, boolean ignoresInvulTime, net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> cantHurtTag, int type) {
        AreaDamage areaDamage = new AreaDamage(com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.AREA_DAMAGE.get(), level);
        areaDamage.moveTo(pos.x, pos.y, pos.z);
        areaDamage.owner = owner;
        areaDamage.damage = damage;
        areaDamage.damageSource = damageSource != null ? damageSource : areaDamage.damageSources().generic();
        areaDamage.setSize(size);
        areaDamage.setSizeToReach(sizeToReach);
        areaDamage.setGrowSpeed(growSpeed);
        areaDamage.setYSize(ySize);
        areaDamage.setStartDelay(startDelay);
        areaDamage.constantDamage = constantDamage;
        areaDamage.friendlyFire = friendlyFire;
        areaDamage.knockbackAmount = knockbackAmount;
        areaDamage.setYKnockback((float)knockbackAmountY);
        areaDamage.disableShields = disableShields;
        areaDamage.disableShieldTime = disableShieldTime;
        areaDamage.setShouldDirectionalMotion(directionalMotion);
        areaDamage.cantHurtTag = cantHurtTag;
        areaDamage.ignoresInvulTime = ignoresInvulTime;
        areaDamage.setAreaDamageType(type);
        areaDamage.setExtraTime(extraTime);
        if (owner != null) {
            if (owner instanceof net.minecraft.world.entity.Mob) {
                areaDamage.setYRot(owner.yBodyRot);
            } else {
                areaDamage.setYRot(owner.yHeadRot);
            }
        }

        return areaDamage;
    }

    public EntityDimensions getDimensions(Pose p_213305_1_) {
        return EntityDimensions.scalable(this.getSize(), this.getYSize());
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public boolean canEntityBeDamaged(Entity entity) {
        if ((this.getAreaDamageType() == 3 || this.getActiveType() == 3) && entity instanceof LivingEntity && !((LivingEntity)entity).canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
            return false;
        } else if (this.owner instanceof SentryVine && !(entity instanceof Enemy) && !entity.getType().is(EntityTypes.SENTRY_VINE_TARGETS) && this.getTeam() == null && entity.getTeam() == null && !(Boolean)MutantJungleZombieRewardsCommonConfig.vine_friendly_fire.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.friendly_fire_on.get()) {
            return false;
        } else if (!this.damagedEntities.isEmpty() && !this.constantDamage && this.damagedEntities.contains(entity)) {
            return false;
        } else {
            boolean canConnectedAreaDamagesHarm;
            Iterator var3;
            com.alexander.mutantmore.entities.AreaDamage areaDamage;
            if (this.cantHurtTag != null) {
                canConnectedAreaDamagesHarm = true;
                if (!this.connectedAreaDamages.isEmpty()) {
                    var3 = this.connectedAreaDamages.iterator();

                    while(var3.hasNext()) {
                        areaDamage = (com.alexander.mutantmore.entities.AreaDamage)var3.next();
                        if (!areaDamage.damagedEntities.isEmpty() && !areaDamage.constantDamage && areaDamage.damagedEntities.contains(entity)) {
                            canConnectedAreaDamagesHarm = false;
                        }
                    }
                }

                return canConnectedAreaDamagesHarm && entity != this.owner && this.canHarm(entity);
            } else if (entity == null) {
                return false;
            } else if (this.owner != null && entity == this.owner) {
                return false;
            } else if (this.owner != null && MobUtil.areAllies(this.owner, entity)) {
                return false;
            } else if (this.connectedAreaDamages.isEmpty()) {
                return true;
            } else {
                canConnectedAreaDamagesHarm = true;
                if (!this.connectedAreaDamages.isEmpty()) {
                    var3 = this.connectedAreaDamages.iterator();

                    while(var3.hasNext()) {
                        areaDamage = (com.alexander.mutantmore.entities.AreaDamage)var3.next();
                        if (!areaDamage.damagedEntities.isEmpty() && !areaDamage.constantDamage && areaDamage.damagedEntities.contains(entity)) {
                            canConnectedAreaDamagesHarm = false;
                        }
                    }
                }

                return canConnectedAreaDamagesHarm;
            }
        }
    }

    boolean canHarm(Entity target) {
        if (this.cantHurtTag != null) {
            if (MiscUtils.canHarmBasedOnTeamAndTag(this.cantHurtTag, this, target, this.owner, (Predicate)null)) {
                return true;
            }

            if (MiscUtils.canHarmBasedOnTeamAndTag(this.cantHurtTag, this, target, this, (Predicate)null)) {
                return true;
            }
        }

        return false;
    }

    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 1) {
            int i;
            for(i = 0; i < 50; ++i) {
                int j = Mth.floor(this.getX());
                int k = Mth.floor(this.getY() - 0.20000000298023224);
                int l = Mth.floor(this.getZ());
                BlockPos pos = new BlockPos(j, k, l);
                BlockState blockstate = this.level().getBlockState(pos);
                if (!blockstate.isAir()) {
                    this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(pos), this.getRandomX(0.5), this.getY() + 0.1, this.getRandomZ(0.5), 0.0, 0.0, 0.0);
                }
            }

            for(i = 0; i < 20; ++i) {
                Vec3 vector3d = this.position();
                Vec3 directionalMotion = PositionUtils.getOffsetMotion(this, 0.0, 0.0, 0.25, 0.0F, this.getYRot());
                this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float)MiscUtils.sandColourForSandType(this.getSandType()))), this.getRandomX(0.75), vector3d.y, this.getRandomZ(0.75), directionalMotion.x + this.random.nextGaussian() * 0.01, (double)this.random.nextFloat() * 0.05, directionalMotion.z + this.random.nextGaussian() * 0.01);
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        if (this.tickCount > this.getStartDelay()) {
            int i;
            if (this.getActiveType() == 3) {
                for(i = 0; (float)i < this.getSize() * 3.0F; ++i) {
                    this.level().addAlwaysVisibleParticle((ParticleOptions)ParticleTypeInit.POISON_CLOUD.get(), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), this.random.nextGaussian() * 0.05, this.random.nextGaussian() * 0.05, this.random.nextGaussian() * 0.05);
                }
            }

            if (this.getActiveType() == 4 && this.tickCount < this.getStartDelay() + this.getExtraTime() - 60) {
                for(i = 0; i < 10; ++i) {
                    Vec3 pos = new Vec3(this.getRandomX(0.5), this.getRandomY() + 0.02, this.getRandomZ(0.5));
                    if (PositionUtils.distanceBetweenVecs(this.position(), pos) <= this.getSize() / 2.0F && PositionUtils.hasLineOfSight(this.level(), new Vec3((double)this.getSentFrom().getX(), (double)this.getSentFrom().getY(), (double)this.getSentFrom().getZ()), pos)) {
                        this.level().addAlwaysVisibleParticle((ParticleOptions)ParticleTypeInit.WITHER_GAS.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                    }
                }
            }

            Iterator var11;
            if (this.getActiveType() == 2) {
                for(i = 0; i < 8; ++i) {
                    this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float)MiscUtils.sandColourForSandType(this.getSandType()))), this.getRandomX(1.0), this.getY(), this.getRandomZ(1.0), this.random.nextGaussian() * 0.01, (double)this.getYKnockback() * 1.5 + this.random.nextGaussian() * 0.5, this.random.nextGaussian() * 0.01);
                }

                this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.SAND_GEYSER, List.of((float)MiscUtils.sandColourForSandType(this.getSandType()))), this.getX() + this.random.nextGaussian() * 0.1, this.getY(), this.getZ() + this.random.nextGaussian() * 0.1, 0.0, (double)this.getYKnockback() * 1.5, 0.0);
                if (!this.level().isClientSide) {
                    AABB aabb = this.getBoundingBox().inflate(2.0);
                    if (this.owner != null && this.owner instanceof MutantHusk && (Boolean)MutantHuskCommonConfig.geyser_attack_griefing.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.quicksand_griefing_off.get()) {
                        var11 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

                        while(var11.hasNext()) {
                            BlockPos blockpos = (BlockPos)var11.next();
                            BlockState blockstate = this.level().getBlockState(blockpos);
                            if (blockstate.isAir() && QuicksandBlock.survivable(MiscUtils.quicksandBlockForSandType(this.getSandType()).defaultBlockState(), this.level(), blockpos) && this.random.nextInt(40) == 0) {
                                this.level().setBlockAndUpdate(blockpos, MiscUtils.quicksandBlockForSandType(this.getSandType()).defaultBlockState());
                            }
                        }
                    }
                }
            }

            if (!this.level().isClientSide) {
                List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), Entity::isAlive);
                if (!list.isEmpty()) {
                    var11 = list.iterator();

                    label172:
                    while(true) {
                        Entity entity;
                        Player player;
                        do {
                            do {
                                if (!var11.hasNext()) {
                                    break label172;
                                }

                                entity = (Entity) var11.next();
                                if (this.damage > 0.0F && this.damageSource != null && this.canEntityBeDamaged(entity) && entity instanceof LivingEntity) {
                                    if (this.ignoresInvulTime) {
                                        entity.invulnerableTime = 0;
                                    }

                                    boolean flag = entity.hurt(this.damageSource, this.getActiveType() == 3 && entity instanceof LivingEntity ? Mth.clamp(this.damage, 0.0F, ((LivingEntity) entity).getHealth() - 1.0F) : this.damage);
                                    if (this.knockbackAmount > 0.0 || this.getYKnockback() > 0.0F) {
                                        entity.hurtMarked = true;
                                        if (this.shouldDirectionalMotion()) {
                                            Vec3 directionalMotion = PositionUtils.getOffsetMotion(this, 0.0, 0.0, this.knockbackAmount, 0.0F, this.getYRot());
                                            entity.push(directionalMotion.x, (double) this.getYKnockback(), directionalMotion.z);
                                        } else {
                                            double d0 = entity.getX() - this.getX();
                                            double d1 = entity.getZ() - this.getZ();
                                            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                                            if ((double) this.distanceTo(entity) >= 0.5) {
                                                entity.push(d0 / d2 * this.knockbackAmount, (double) this.getYKnockback(), d1 / d2 * this.knockbackAmount);
                                            }
                                        }
                                    }

                                    if (entity instanceof LivingEntity && this.disableShields) {
                                        this.disableShield((LivingEntity) entity, this.disableShieldTime);
                                    }

                                    if (!flag) {
                                        this.blockedByEntities.add(entity);
                                    }

                                    this.damagedEntities.add(entity);
                                }
                            } while (this.getActiveType() != 4);

                            if (!(entity instanceof Player)) {
                                break;
                            }

                            player = (Player) entity;
                        } while (player.isCreative() || player.isSpectator());

                        if (this.canEntityBeDamaged(entity) && this.distanceTo(entity) <= this.getSize() / 2.0F && PositionUtils.hasLineOfSight(this.level(), new Vec3((double) this.getSentFrom().getX(), (double) this.getSentFrom().getY(), (double) this.getSentFrom().getZ()), entity.getEyePosition()) && entity instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) entity;
                            int blindnessDuration = (Integer) MutantWitherSkeletonCommonConfig.wither_breath_blindness_length.get();
                            int witherLength = (Integer) MutantWitherSkeletonCommonConfig.wither_breath_wither_length.get();
                            int witherLevel = Math.min((Integer) MutantWitherSkeletonCommonConfig.wither_breath_wither_level.get() + this.witherBreathBuffLevelBonus, 5);
                            int slownessLength = (Integer) MutantWitherSkeletonCommonConfig.wither_breath_slowness_length.get();
                            int slownessLevel = Math.min((Integer) MutantWitherSkeletonCommonConfig.wither_breath_slowness_level.get() + this.witherBreathBuffLevelBonus, 2);

                            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindnessDuration));
                            living.addEffect(new MobEffectInstance(MobEffects.WITHER, witherLength, witherLevel));
                            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessLength, slownessLevel));
                            living.addEffect(new MobEffectInstance((MobEffect) EffectInit.JUMPING_FATIGUE.get(), slownessLength));

                            if (this.witherBreathUsingNetherStaff) {
                                int sappedLength = witherLength;
                                int sappedLevel = Math.min(slownessLength, 2);
                                living.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), sappedLength, sappedLevel));
                            }
                        }
                    }
                }
            }

            if (this.getAreaDamageType() > 0) {
                if (this.level().getBlockState(this.blockPosition().below()).getSoundType() != null && (this.getAreaDamageType() == 1 || this.getAreaDamageType() == 2)) {
                    this.playSound(this.level().getBlockState(this.blockPosition().below()).getSoundType().getBreakSound(), 1.0F, MiscUtils.randomSoundPitch());
                    this.level().broadcastEntityEvent(this, (byte)1);
                }

                if (this.getAreaDamageType() == 2) {
                    this.playSound((SoundEvent)SoundEventInit.MUTANT_HUSK_GEYSER.get(), 2.0F, MiscUtils.randomSoundPitch());
                }

                this.setActiveType(this.getAreaDamageType());
                this.setAreaDamageType(0);
            }

            if (this.getSize() < this.getSizeToReach()) {
                this.setSize(this.getSize() + this.getGrowSpeed());
            }

            if (this.getSize() >= this.getSizeToReach()) {
                ++this.extraTimeTick;
            }

            if (!this.level().isClientSide && (this.getExtraTime() > 0 && this.extraTimeTick >= this.getExtraTime() || this.getExtraTime() <= 0 && this.getSize() >= this.getSizeToReach())) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    public void disableShield(LivingEntity livingEntity, int ticks) {
        if (livingEntity instanceof Player && livingEntity.isBlocking()) {
            ((Player)livingEntity).getCooldowns().addCooldown(livingEntity.getItemInHand(livingEntity.getUsedItemHand()).getItem(), ticks);
            livingEntity.stopUsingItem();
            livingEntity.level().broadcastEntityEvent(livingEntity, (byte)30);
        }

    }

    protected void defineSynchedData() {
        this.entityData.define(SIZE, 0.0F);
        this.entityData.define(SIZE_TO_REACH, 0.0F);
        this.entityData.define(Y_SIZE, 0.0F);
        this.entityData.define(GROW_SPEED, 0.0F);
        this.entityData.define(Y_KNOCKBACK, 0.0F);
        this.entityData.define(TYPE, 0);
        this.entityData.define(ACTIVE_TYPE, 0);
        this.entityData.define(EXTRA_TIME, 0);
        this.entityData.define(START_DELAY, 0);
        this.entityData.define(DIRECTIONAL_MOTION, false);
        this.entityData.define(SAND_TYPE, "");
        this.entityData.define(SENT_FROM, BlockPos.ZERO);
    }

    public float getSize() {
        return (Float)this.entityData.get(SIZE);
    }

    public void setSize(float attached) {
        this.entityData.set(SIZE, attached);
    }

    public boolean shouldDirectionalMotion() {
        return (Boolean)this.entityData.get(DIRECTIONAL_MOTION);
    }

    public void setShouldDirectionalMotion(boolean value) {
        this.entityData.set(DIRECTIONAL_MOTION, value);
    }

    public float getSizeToReach() {
        return (Float)this.entityData.get(SIZE_TO_REACH);
    }

    public void setSizeToReach(float attached) {
        this.entityData.set(SIZE_TO_REACH, attached);
    }

    public float getYSize() {
        return (Float)this.entityData.get(Y_SIZE);
    }

    public void setYSize(float attached) {
        this.entityData.set(Y_SIZE, attached);
    }

    public float getGrowSpeed() {
        return (Float)this.entityData.get(GROW_SPEED);
    }

    public void setGrowSpeed(float attached) {
        this.entityData.set(GROW_SPEED, attached);
    }

    public float getYKnockback() {
        return (Float)this.entityData.get(Y_KNOCKBACK);
    }

    public void setYKnockback(float attached) {
        this.entityData.set(Y_KNOCKBACK, attached);
    }

    public int getAreaDamageType() {
        return (Integer)this.entityData.get(TYPE);
    }

    public void setAreaDamageType(int attached) {
        this.entityData.set(TYPE, attached);
    }

    public int getActiveType() {
        return (Integer)this.entityData.get(ACTIVE_TYPE);
    }

    public void setActiveType(int attached) {
        this.entityData.set(ACTIVE_TYPE, attached);
    }

    public int getExtraTime() {
        return (Integer)this.entityData.get(EXTRA_TIME);
    }

    public void setExtraTime(int attached) {
        this.entityData.set(EXTRA_TIME, attached);
    }

    public int getStartDelay() {
        return (Integer)this.entityData.get(START_DELAY);
    }

    public void setStartDelay(int attached) {
        this.entityData.set(START_DELAY, attached);
    }

    public String getSandType() {
        return (String)this.entityData.get(SAND_TYPE);
    }

    public void setSandType(String value) {
        this.entityData.set(SAND_TYPE, value);
    }

    public BlockPos getSentFrom() {
        return (BlockPos)this.entityData.get(SENT_FROM);
    }

    public void setSentFrom(BlockPos attached) {
        this.entityData.set(SENT_FROM, attached);
    }

    protected void readAdditionalSaveData(CompoundTag p_70037_1_) {
        if (p_70037_1_.contains("Size")) {
            this.setSize(p_70037_1_.getFloat("Size"));
        }

        if (p_70037_1_.contains("SizeToReach")) {
            this.setSizeToReach(p_70037_1_.getFloat("SizeToReach"));
        }

        if (p_70037_1_.contains("YSize")) {
            this.setYSize(p_70037_1_.getFloat("YSize"));
        }

        if (p_70037_1_.contains("GrowSpeed")) {
            this.setGrowSpeed(p_70037_1_.getFloat("GrowSpeed"));
        }

        if (p_70037_1_.contains("AreaDamageType")) {
            this.setAreaDamageType(p_70037_1_.getInt("AreaDamageType"));
        }

        if (p_70037_1_.contains("ActiveType")) {
            this.setActiveType(p_70037_1_.getInt("ActiveType"));
        }

        if (p_70037_1_.contains("ExtraTime")) {
            this.setExtraTime(p_70037_1_.getInt("ExtraTime"));
        }

        if (p_70037_1_.contains("Damage")) {
            this.damage = p_70037_1_.getFloat("Damage");
        }

        if (p_70037_1_.contains("StartDelay")) {
            this.setStartDelay(p_70037_1_.getInt("StartDelay"));
        }

        if (p_70037_1_.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = p_70037_1_.getBoolean("IgnoresInvulTime");
        }

        if (p_70037_1_.contains("ConstantDamage")) {
            this.constantDamage = p_70037_1_.getBoolean("ConstantDamage");
        }

        if (p_70037_1_.contains("KnockbackAmount")) {
            this.knockbackAmount = p_70037_1_.getDouble("KnockbackAmount");
        }

        if (p_70037_1_.contains("KnockbackAmountY")) {
            this.setYKnockback(p_70037_1_.getFloat("KnockbackAmountY"));
        }

        if (p_70037_1_.contains("DisableShieldTime")) {
            this.disableShieldTime = p_70037_1_.getInt("DisableShieldTime");
        }

        if (p_70037_1_.contains("DisableShields")) {
            this.disableShields = p_70037_1_.getBoolean("DisableShields");
        }

    }

    protected void addAdditionalSaveData(CompoundTag p_213281_1_) {
        p_213281_1_.putFloat("Size", this.getSize());
        p_213281_1_.putFloat("SizeToReach", this.getSizeToReach());
        p_213281_1_.putFloat("YSize", this.getYSize());
        p_213281_1_.putFloat("GrowSpeed", this.getGrowSpeed());
        p_213281_1_.putInt("AreaDamageType", this.getAreaDamageType());
        p_213281_1_.putInt("ActiveType", this.getActiveType());
        p_213281_1_.putFloat("Damage", this.damage);
        p_213281_1_.putInt("StartDelay", this.getStartDelay());
        p_213281_1_.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        p_213281_1_.putBoolean("ConstantDamage", this.constantDamage);
        p_213281_1_.putDouble("KnockbackAmount", this.knockbackAmount);
        p_213281_1_.putFloat("KnockbackAmountY", this.getYKnockback());
        p_213281_1_.putInt("DisableShieldTime", this.disableShieldTime);
        p_213281_1_.putBoolean("DisableShields", this.disableShields);
        p_213281_1_.putInt("ExtraTime", this.getExtraTime());
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        SIZE = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.FLOAT);
        SIZE_TO_REACH = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.FLOAT);
        GROW_SPEED = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.FLOAT);
        Y_SIZE = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.FLOAT);
        Y_KNOCKBACK = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.FLOAT);
        TYPE = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.INT);
        ACTIVE_TYPE = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.INT);
        EXTRA_TIME = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.INT);
        START_DELAY = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.INT);
        DIRECTIONAL_MOTION = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.BOOLEAN);
        SAND_TYPE = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.STRING);
        SENT_FROM = SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, EntityDataSerializers.BLOCK_POS);
    }
}
