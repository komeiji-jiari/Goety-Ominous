package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.api.entities.IBreathing;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.BreathingAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FireBreathSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModMobType;
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
    private static final double RIDER_FORWARD_OFFSET = -1.36D;
    private static final double RIDER_HEIGHT = 1.8D;
    private static final double RIDER_BOUNCE = 0.08D;

    private final FireBreathSpell breathSpell = new FireBreathSpell();

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
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.SADDLE)) {
            return InteractionResult.PASS;
        }
        if (held.isEmpty() && this.isOwnedByPlayer(player) && this.getPassengers().isEmpty()) {
            if (!this.level().isClientSide) {
                player.setYRot(this.getYRot());
                player.setXRot(this.getXRot());
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && passenger instanceof Player;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof Player player && this.isOwnedByPlayer(player) ? player : null;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public void onPlayerJump(int strength) {
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            Vec3 facing = Vec3.directionFromRotation(0.0F, this.getYRot());
            Vec3 seatOffset = facing.scale(RIDER_FORWARD_OFFSET);
            double bounce = RIDER_BOUNCE * Mth.cos(this.walkAnimation.position() * 0.7F) * this.walkAnimation.speed();
            moveFunction.accept(passenger, this.getX() + seatOffset.x,
                    this.rideHeight + RIDER_HEIGHT + bounce, this.getZ() + seatOffset.z);
        }
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
        if (target.hurt(ModDamageSource.fireBreath(this, this), damage)) {
            target.setSecondsOnFire(5 * stats.getBurning());
        }
    }

    private void updateCerberusAnimationStates() {
        boolean breathing = this.isBreathing();
        boolean biting = !breathing && this.getAttackTicks() > 0 && this.getAttackType() == ATTACK_BITE;
        boolean grounded = !breathing && !biting && this.onGround();
        setAnimation(this.fireBreathAnimationState, breathing);
        setAnimation(this.biteAnimationState, biting);
        setAnimation(this.jumpAnimationState, !breathing && !biting && !this.onGround());
        setAnimation(this.groundedAnimationState, grounded && this.isSitting());
        setAnimation(this.walkAnimationState, grounded && !this.isSitting() && this.walkAnimation.speed() > 0.05F);
        setAnimation(this.idleAnimationState, grounded && !this.isSitting() && this.walkAnimation.speed() <= 0.05F);
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
            return target != null && target.isAlive() && this.attackTarget == target && super.canContinueToUse();
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
