package com.qiuyue.goetyominous.common.entities.ally.spider;

import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import com.Polarice3.Goety.init.ModMobType;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.AbstractGOCultist;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class CrimsonSpiderServant extends SpiderServant {

    public CrimsonSpiderServant(EntityType<? extends CrimsonSpiderServant> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.4F));
    }

    @Override
    public void attackGoal() {
        this.goalSelector.addGoal(2, new CrimsonSpiderAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CrimsonSpiderServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CrimsonSpiderServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.CrimsonSpiderServantHealth.get());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.CrimsonSpiderServantDamage.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isAggressive()) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }
    }

    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living) {
            living.setSecondsOnFire(5);
        }
        return hurt;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) return true;
        if (entity instanceof com.Polarice3.Goety.common.entities.hostile.cultists.Cultist
                || entity instanceof AbstractGOCultist) {
            LivingEntity owner = this.getTrueOwner();
            return owner instanceof com.Polarice3.Goety.common.entities.hostile.cultists.Cultist
                    || owner instanceof AbstractGOCultist;
        }
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        if (pPotioneffect.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(pPotioneffect);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.75D;
    }

    @Override
    public boolean spawnWithEffects() {
        return true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        if (pSpawnData == null) {
            pSpawnData = new CrimsonSpiderEffectsGroupData();
        }

        if (pSpawnData instanceof CrimsonSpiderEffectsGroupData groupData) {
            MobEffectInstance effect = groupData.effect;
            if (effect != null) {
                this.addEffect(effect);
            }
        }

        return pSpawnData;
    }

    public static class CrimsonSpiderEffectsGroupData implements SpawnGroupData {
        @Nullable
        public MobEffectInstance effect;

        public CrimsonSpiderEffectsGroupData() {
            this.setRandomEffect(new Random());
        }

        public void setRandomEffect(Random pRand) {
            int i = pRand.nextInt(5);
            switch (i) {
                case 0 -> {}
                case 1 -> this.effect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE);
                case 2 -> this.effect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE);
                case 3 -> this.effect = new MobEffectInstance(MobEffects.REGENERATION, Integer.MAX_VALUE);
                case 4 -> this.effect = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE);
            }
        }
    }


    static class CrimsonSpiderAttackGoal extends MeleeAttackGoal {
        public CrimsonSpiderAttackGoal(CrimsonSpiderServant pSpider) {
            super(pSpider, 1.0D, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            return 4.0F + pAttackTarget.getBbWidth();
        }
    }
}