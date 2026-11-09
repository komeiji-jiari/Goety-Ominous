package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FireBreathSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemBlockEntity;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemHooks;
import com.qiuyue.goetyominous.common.world.CerberusTotemData;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Cerberus extends Warg implements IBreathing {
    private static final EntityDataAccessor<Boolean> BREATHING = SynchedEntityData.defineId(Cerberus.class, EntityDataSerializers.BOOLEAN);
    private static final int BREATH_POTENCY = 5;
    private static final int BREATH_RANGE = 8;
    private static final int MIN_BREATH_TICKS = 80;
    private static final int MAX_BREATH_TICKS = 100;
    private static final int BREATH_COOLDOWN_TICKS = 300;
    private static final double HEAD_YAW_SPREAD = Math.toRadians(35.0D);
    private static final double MUZZLE_HEIGHT = 2.2D;
    private static final double MUZZLE_FORWARD = 2.2D;
    private static final int CERBERUS_POSE_GROUNDED = 0;
    private static final int CERBERUS_POSE_JUMP = 1;
    private static final int CERBERUS_POSE_BITE = 2;
    private static final int CERBERUS_POSE_BREATH = 3;
    private static final int CERBERUS_POSE_COUNT = 4;
    private static final float CERBERUS_LAND_FADE_STEP = 0.1F;
    private static final double CERBERUS_RISE_PER_TICK = 0.05D;

    private final FireBreathSpell breathSpell = new FireBreathSpell();
    private final float[] cerberusPoseWeights = new float[CERBERUS_POSE_COUNT];
    private final float[] cerberusPoseWeightsOld = new float[CERBERUS_POSE_COUNT];
    private float cerberusLocomotionWeight;
    private float cerberusLocomotionWeightOld;
    private float cerberusMoveWeight;
    private float cerberusMoveWeightOld;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState groundedAnimationState = new AnimationState();
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState fireBreathAnimationState = new AnimationState();

    public Cerberus(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new CerberusBreathGoal());
    }

    @Override
    protected float getLeapPower() {
        return 0.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.CerberusHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.CerberusArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CerberusDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CerberusHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CerberusArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CerberusDamage.get());
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            health.setBaseValue(AttributesConfig.CerberusHealth.get() * (upgraded ? 1.5D : 1.0D));
            armor.setBaseValue(AttributesConfig.CerberusArmor.get() + (upgraded ? 1.0D : 0.0D));
            attack.setBaseValue(AttributesConfig.CerberusDamage.get() + (upgraded ? 1.0D : 0.0D));
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BREATHING, false);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void curseTarget(Entity entity) {
        if (!entity.fireImmune()) {
            entity.setSecondsOnFire(6);
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            return;
        }
        super.setItemSlot(slot, stack);
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.SADDLE)) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.releaseCerberusTotemSlot(serverLevel);
        }
    }

    @Override
    public void dismiss() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.releaseCerberusTotemSlot(serverLevel);
        }
        super.dismiss();
    }

    private void releaseCerberusTotemSlot(ServerLevel level) {
        WolfTotemBlockEntity totem = WolfTotemHooks.getTotem((LivingEntity) this);
        if (totem != null) {
            totem.releaseCerberus(this.getUUID());
        }
        CerberusTotemData.get(level).unregister(this.getUUID());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateCerberusAnimationStates();
            if (this.isBreathing()) {
                this.spawnBreathParticles();
            }
        }
    }

    private void spawnBreathParticles() {
        Vec3 look = this.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);
        Vec3 forwardAxis = flatLook.lengthSqr() < 1.0E-4D
                ? Vec3.directionFromRotation(0.0F, this.getYRot())
                : flatLook.normalize();
        double muzzleY = this.getY() + MUZZLE_HEIGHT;
        double forward = MUZZLE_FORWARD;
        for (int head = -1; head <= 1; ++head) {
            Vec3 placement = rotateAroundY(forwardAxis, head * HEAD_YAW_SPREAD);
            Vec3 direction = rotateAroundY(look, head * HEAD_YAW_SPREAD);
            double muzzleX = this.getX() + placement.x * forward;
            double muzzleZ = this.getZ() + placement.z * forward;
            for (int i = 0; i < 6; ++i) {
                Vec3 spread = new Vec3(this.random.nextDouble() - 0.5D, this.random.nextDouble() - 0.5D, this.random.nextDouble() - 0.5D);
                Vec3 velocity = direction.scale(3.0D).add(spread).normalize().scale(0.6D + this.random.nextDouble() * 0.4D);
                this.level().addAlwaysVisibleParticle(ModParticleTypes.DRAGON_FLAME.get(),
                        muzzleX, muzzleY, muzzleZ, velocity.x, velocity.y, velocity.z);
            }
        }
    }

    private static Vec3 rotateAroundY(Vec3 vector, double radians) {
        if (radians == 0.0D) {
            return vector;
        }
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3(vector.x * cos - vector.z * sin, vector.y, vector.x * sin + vector.z * cos);
    }

    private SpellStat breathStats() {
        return this.breathSpell.defaultStats().setPotency(BREATH_POTENCY).setRange(BREATH_RANGE);
    }

    @Override
    public boolean isBreathing() {
        return this.entityData.get(BREATHING);
    }

    @Override
    public void setBreathing(boolean flag) {
        this.entityData.set(BREATHING, flag);
    }

    @Override
    public void doBreathing(Entity target) {
        SpellStat stats = this.breathStats();
        float damage = SpellConfig.FireBreathDamage.get().floatValue() * WandUtil.damageMultiply() + stats.getPotency();
        DamageSource source = ModDamageSource.fireBreath(this, this);
        LivingEntity owner = this.getMasterOwner();
        if (owner != null && CuriosFinder.hasNetherRobe(owner)) {
            source = ModDamageSource.magicFireBreath(this, this);
            if (target.fireImmune()) {
                damage *= 0.5F;
            }
        }
        if (target.hurt(source, damage)) {
            target.setSecondsOnFire(5 * stats.getBurning());
        }
    }

    private void updateCerberusAnimationStates() {
        boolean rising = !this.onGround() && this.getY() - this.yo > CERBERUS_RISE_PER_TICK;
        boolean airborne = this.tickAirborneAnimation() || rising;
        boolean breathing = !airborne && this.isBreathing();
        boolean biting = !airborne && !breathing && this.getAttackTicks() > 0 && this.getAttackType() == ATTACK_BITE;
        boolean grounded = !breathing && !biting && !airborne;
        boolean standing = grounded && !this.isSitting();
        boolean moving = standing && this.walkAnimation.speed() > 0.05F;
        this.cerberusLocomotionWeightOld = this.cerberusLocomotionWeight;
        this.cerberusMoveWeightOld = this.cerberusMoveWeight;
        this.cerberusLocomotionWeight = approach(this.cerberusLocomotionWeight, standing ? 1.0F : 0.0F, POSE_BLEND_STEP);
        this.cerberusMoveWeight = approach(this.cerberusMoveWeight, moving ? 1.0F : 0.0F, POSE_BLEND_STEP);
        this.stepCerberusPose(CERBERUS_POSE_GROUNDED, grounded && this.isSitting() ? 1.0F : 0.0F);
        this.stepCerberusPose(CERBERUS_POSE_JUMP, airborne ? 1.0F : 0.0F, airborne ? POSE_BLEND_STEP : CERBERUS_LAND_FADE_STEP);
        this.stepCerberusPose(CERBERUS_POSE_BITE, biting ? 1.0F : 0.0F);
        this.stepCerberusPose(CERBERUS_POSE_BREATH, breathing ? 1.0F : 0.0F);
        this.setAnimation(this.fireBreathAnimationState, this.cerberusPoseActive(CERBERUS_POSE_BREATH));
        this.setAnimation(this.biteAnimationState, this.cerberusPoseActive(CERBERUS_POSE_BITE));
        this.setAnimation(this.jumpAnimationState, this.cerberusPoseActive(CERBERUS_POSE_JUMP));
        this.setAnimation(this.groundedAnimationState, this.cerberusPoseActive(CERBERUS_POSE_GROUNDED));
        this.setAnimation(this.walkAnimationState, this.cerberusLocomotionActive(this.cerberusMoveWeight, this.cerberusMoveWeightOld));
        this.setAnimation(this.idleAnimationState, this.cerberusLocomotionActive(1.0F - this.cerberusMoveWeight, 1.0F - this.cerberusMoveWeightOld));
    }

    private boolean cerberusPoseActive(int index) {
        return this.cerberusPoseWeights[index] > 0.0F || this.cerberusPoseWeightsOld[index] > 0.0F;
    }

    private boolean cerberusLocomotionActive(float factor, float previousFactor) {
        return this.cerberusLocomotionWeight * factor > 0.0F || this.cerberusLocomotionWeightOld * previousFactor > 0.0F;
    }

    private void stepCerberusPose(int index, float target) {
        this.stepCerberusPose(index, target, POSE_BLEND_STEP);
    }

    private void stepCerberusPose(int index, float target, float step) {
        this.cerberusPoseWeightsOld[index] = this.cerberusPoseWeights[index];
        this.cerberusPoseWeights[index] = approach(this.cerberusPoseWeights[index], target, step);
    }

    private float cerberusPoseWeight(int index, float ageInTicks) {
        return Mth.lerp(this.partialTick(ageInTicks), this.cerberusPoseWeightsOld[index], this.cerberusPoseWeights[index]);
    }

    @Override
    public float getIdleWeight(float ageInTicks) {
        float partial = this.partialTick(ageInTicks);
        float locomotion = Mth.lerp(partial, this.cerberusLocomotionWeightOld, this.cerberusLocomotionWeight);
        float move = Mth.lerp(partial, this.cerberusMoveWeightOld, this.cerberusMoveWeight);
        return locomotion * (1.0F - move);
    }

    @Override
    public float getWalkWeight(float ageInTicks) {
        float partial = this.partialTick(ageInTicks);
        float locomotion = Mth.lerp(partial, this.cerberusLocomotionWeightOld, this.cerberusLocomotionWeight);
        float move = Mth.lerp(partial, this.cerberusMoveWeightOld, this.cerberusMoveWeight);
        return locomotion * move;
    }

    @Override
    public float getGroundedWeight(float ageInTicks) {
        return this.cerberusPoseWeight(CERBERUS_POSE_GROUNDED, ageInTicks);
    }

    public float getJumpWeight(float ageInTicks) {
        return this.cerberusPoseWeight(CERBERUS_POSE_JUMP, ageInTicks);
    }

    @Override
    public float getBiteWeight(float ageInTicks) {
        return this.cerberusPoseWeight(CERBERUS_POSE_BITE, ageInTicks);
    }

    public float getFireBreathWeight(float ageInTicks) {
        return this.cerberusPoseWeight(CERBERUS_POSE_BREATH, ageInTicks);
    }

    private void setAnimation(AnimationState state, boolean running) {
        if (running) {
            state.startIfStopped(this.tickCount);
        } else {
            state.stop();
        }
    }

    private class CerberusBreathGoal extends BreathingAttackGoal<Cerberus> {
        private int cooldownTicks;

        private CerberusBreathGoal() {
            super(Cerberus.this, BREATH_RANGE, MAX_BREATH_TICKS, 1.0F);
        }

        @Override
        public boolean canUse() {
            if (this.cooldownTicks > 0) {
                --this.cooldownTicks;
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Cerberus.this.getTarget();
            return target != null && target.isAlive()
                    && this.attackTarget == target && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            this.durationLeft = MIN_BREATH_TICKS + Cerberus.this.getRandom().nextInt(MAX_BREATH_TICKS - MIN_BREATH_TICKS + 1);
            Cerberus.this.playSound(Cerberus.this.breathSpell.CastingSound(), 2.0F, 1.0F);
        }

        @Override
        public void stop() {
            super.stop();
            this.cooldownTicks = BREATH_COOLDOWN_TICKS;
        }
    }
}
