package com.qiuyue.goetyominous.common.entities.ai;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.client.particles.WindParticleOption;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class GuzzlerBossAttackGoal extends Goal {
    private static final int ACTION_NONE = 0;
    private static final int ACTION_SLAM = 1;
    private static final int ACTION_SPIN = 2;
    private static final int ACTION_JUMP_START = 3;
    private static final int ACTION_JUMP_LAND = 4;

    private static final double SLAM_RANGE = 3.5D;
    private static final double SPIN_MIN_RANGE = 3.5D;
    private static final double SPIN_MAX_RANGE = 6.0D;
    private static final double JUMP_MIN_RANGE = 6.0D;
    private static final double JUMP_MAX_RANGE = 12.0D;

    private static final int SLAM_HIT_TICK = 14;
    private static final int SLAM_END_TICK = 40;
    private static final int SLAM_COOLDOWN = 60;
    private static final double SLAM_RADIUS = 3.5D;
    private static final double SLAM_FORWARD_OFFSET = 2.0D;

    private static final int SPIN_START_TICK = 18;
    private static final int SPIN_PULSE_END_TICK = 54;
    private static final int SPIN_PULSE_INTERVAL = 6;
    private static final int SPIN_END_TICK = 78;
    private static final int SPIN_COOLDOWN = 200;
    private static final int SPIN_HIT_COOLDOWN = 10;
    private static final double SPIN_RADIUS = 4.5D;
    private static final double SPIN_SPEED = 0.30D;

    private static final int JUMP_LAUNCH_TICK = 18;
    private static final int JUMP_LAND_HIT_TICK = 13;
    private static final int JUMP_LAND_END_TICK = 55;
    private static final int JUMP_TIMEOUT_TICKS = 80;
    private static final int JUMP_COOLDOWN = 240;
    private static final double JUMP_LAND_RADIUS = 4.5D;
    private static final double JUMP_SPEED_DIVISOR = 9.4D;
    private static final double JUMP_MIN_SPEED = 0.2D;
    private static final double JUMP_MAX_SPEED = 1.35D;

    private static final double HIT_VERTICAL_RANGE = 4.0D;
    private static final ColorUtil IMPACT_COLOR = new ColorUtil(0xB02E26);

    private final GuzzlerBoss mob;
    private final Map<Integer, Integer> spinHitCooldowns = new HashMap<>();
    private int action = ACTION_NONE;
    private int actionTick;
    private boolean slammed;
    private boolean jumped;
    private boolean landed;
    private Vec3 jumpDirection = Vec3.ZERO;
    private double jumpDistance;

    public GuzzlerBossAttackGoal(GuzzlerBoss mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isSitting() || this.mob.isGettingUp()) {
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.action != ACTION_NONE) {
            return true;
        }
        if (this.mob.isSitting() || this.mob.isGettingUp()) {
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
        this.action = ACTION_NONE;
        this.actionTick = 0;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.mob.stopLeap();
        this.action = ACTION_NONE;
        this.actionTick = 0;
        this.slammed = false;
        this.jumped = false;
        this.landed = false;
        this.spinHitCooldowns.clear();
        if (!this.mob.isDeadOrDying()) {
            this.mob.setAnimState(GuzzlerBoss.ANIM_IDLE);
        }
        if (this.mob.getTarget() == null) {
            this.mob.setAggressive(false);
        }
    }

    @Override
    public void tick() {
        if (this.action != ACTION_NONE) {
            this.tickAction();
            return;
        }
        LivingEntity target = this.mob.getTarget();
        if (target == null || this.mob.isSitting() || this.mob.isGettingUp()) {
            return;
        }
        double distance = this.mob.distanceTo(target);
        boolean sight = this.mob.hasLineOfSight(target);
        int level = this.mob.getUpgradeLevel();
        if (distance <= SLAM_RANGE && this.mob.getSlamCooldown() <= 0 && sight) {
            this.startSlam(target);
            return;
        }
        if (level >= 2 && distance >= JUMP_MIN_RANGE && distance <= JUMP_MAX_RANGE
                && this.mob.getJumpCooldown() <= 0 && sight && this.mob.hasLeapHeadroom()) {
            this.startJump(target);
            return;
        }
        if (level >= 1 && distance > SLAM_RANGE && distance <= SPIN_MAX_RANGE
                && this.mob.getSpinCooldown() <= 0 && sight) {
            this.startSpin(target);
            return;
        }
        if (distance > SLAM_RANGE) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.mob.getNavigation().moveTo(target, 1.0D);
        } else {
            this.mob.getNavigation().stop();
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }

    private void tickAction() {
        switch (this.action) {
            case ACTION_SLAM -> this.tickSlam();
            case ACTION_SPIN -> this.tickSpin();
            case ACTION_JUMP_START -> this.tickJumpStart();
            case ACTION_JUMP_LAND -> this.tickJumpLand();
            default -> this.finishAction();
        }
    }

    private void startSlam(LivingEntity target) {
        this.action = ACTION_SLAM;
        this.actionTick = 0;
        this.slammed = false;
        this.mob.getNavigation().stop();
        this.mob.setSlamCooldown(SLAM_COOLDOWN);
        this.mob.setAnimState(GuzzlerBoss.ANIM_SLAM);
        MobUtil.instaLook(this.mob, target);
    }

    private void tickSlam() {
        ++this.actionTick;
        this.mob.getNavigation().stop();
        if (!this.slammed && this.actionTick >= SLAM_HIT_TICK) {
            this.slammed = true;
            Vec3 center = this.getSlamCenter();
            this.areaAttack(SLAM_RADIUS, this.getDamage(1.0F), 0.5D, center);
            this.groundBurst(SLAM_RADIUS, 20.0F, 0.14F, 20, center);
            this.mob.playSlamImpactSound();
        }
        if (this.actionTick >= SLAM_END_TICK) {
            this.finishAction();
        }
    }

    private void startSpin(LivingEntity target) {
        this.action = ACTION_SPIN;
        this.actionTick = 0;
        this.spinHitCooldowns.clear();
        this.mob.getNavigation().stop();
        this.mob.setSpinCooldown(SPIN_COOLDOWN);
        this.mob.setAnimState(GuzzlerBoss.ANIM_SPIN);
        this.mob.playSpinStartSound();
        MobUtil.instaLook(this.mob, target);
    }

    private void tickSpin() {
        ++this.actionTick;
        LivingEntity target = this.mob.getTarget();
        if (this.actionTick >= SPIN_END_TICK) {
            this.finishAction();
            return;
        }
        if (this.actionTick <= SPIN_START_TICK) {
            this.mob.getNavigation().stop();
            if (this.actionTick == SPIN_START_TICK) {
                this.mob.playSpinSound();
            }
            if (target != null) {
                MobUtil.instaLook(this.mob, target);
            }
            return;
        }
        this.tickSpinHitCooldowns();
        if (this.actionTick <= SPIN_PULSE_END_TICK
                && (this.actionTick - SPIN_START_TICK) % SPIN_PULSE_INTERVAL == 0) {
            this.spinPulse();
        }
        this.tickSpinMovement(target);
        if (this.mob.level() instanceof ServerLevel serverLevel) {
            this.spawnSpinWindParticles(serverLevel);
        }
        if (this.mob.onGround() && this.actionTick % 10 == 0) {
            CameraShake.cameraShake(this.mob.level(), this.mob.position(), 8.0F, 0.08F, 0, 10);
        }
    }

    private void spawnSpinWindParticles(ServerLevel serverLevel) {
        if (this.actionTick % 2 != 0 || this.actionTick > SPIN_PULSE_END_TICK) {
            return;
        }
        for (int i = 0; i < 2; ++i) {
            float width = 1.9F + serverLevel.random.nextFloat() * 0.5F;
            float height = 0.4F + serverLevel.random.nextFloat() * 3.0F;
            int life = 12 + serverLevel.random.nextInt(7);
            serverLevel.sendParticles(new WindParticleOption(ColorUtil.WHITE, width, height, life, this.mob.getId()),
                    this.mob.getX(), this.mob.getY(), this.mob.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void tickSpinMovement(LivingEntity target) {
        if (target == null || this.actionTick > SPIN_PULSE_END_TICK) {
            return;
        }
        Vec3 delta = target.position().subtract(this.mob.position());
        Vec3 flat = new Vec3(delta.x, 0.0D, delta.z);
        if (flat.lengthSqr() < 4.0D) {
            return;
        }
        if (this.mob.horizontalCollision) {
            this.mob.setDeltaMovement(0.0D, this.mob.getDeltaMovement().y, 0.0D);
            return;
        }
        MobUtil.instaLook(this.mob, target);
        Vec3 direction = flat.normalize();
        this.mob.setDeltaMovement(direction.x * SPIN_SPEED, this.mob.getDeltaMovement().y, direction.z * SPIN_SPEED);
        this.mob.hasImpulse = true;
    }

    private void tickSpinHitCooldowns() {
        if (this.spinHitCooldowns.isEmpty()) {
            return;
        }
        this.spinHitCooldowns.replaceAll((id, value) -> value - 1);
        this.spinHitCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    private void spinPulse() {
        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean hit = false;
        for (LivingEntity target : this.getTargets(SPIN_RADIUS)) {
            int id = target.getId();
            Integer cooldown = this.spinHitCooldowns.get(id);
            if (cooldown != null && cooldown > 0) {
                continue;
            }
            if (this.hurtTarget(target, this.getDamage(0.5F), 0.35D)) {
                this.spinHitCooldowns.put(id, SPIN_HIT_COOLDOWN);
                hit = true;
            }
        }
        if (hit) {
            this.mob.playSpinHitSound();
        }
    }

    private void startJump(LivingEntity target) {
        this.action = ACTION_JUMP_START;
        this.actionTick = 0;
        this.jumped = false;
        this.landed = false;
        Vec3 delta = target.position().subtract(this.mob.position());
        Vec3 flat = new Vec3(delta.x, 0.0D, delta.z);
        double distance = flat.length();
        this.jumpDirection = distance < 1.0E-4D ? this.mob.getLookAngle() : flat.scale(1.0D / distance);
        this.jumpDistance = distance;
        this.mob.getNavigation().stop();
        this.mob.setJumpCooldown(JUMP_COOLDOWN);
        this.mob.setAnimState(GuzzlerBoss.ANIM_JUMP_START);
        MobUtil.instaLook(this.mob, target);
    }

    private void tickJumpStart() {
        ++this.actionTick;
        this.mob.getNavigation().stop();
        LivingEntity target = this.mob.getTarget();
        if (!this.jumped && target != null) {
            MobUtil.instaLook(this.mob, target);
        }
        if (!this.jumped && this.actionTick >= JUMP_LAUNCH_TICK) {
            Vec3 direction = this.jumpDirection;
            double speed = Mth.clamp(this.jumpDistance / JUMP_SPEED_DIVISOR, JUMP_MIN_SPEED, JUMP_MAX_SPEED);
            if (target != null && target.isAlive()) {
                Vec3 delta = target.position().subtract(this.mob.position());
                Vec3 flat = new Vec3(delta.x, 0.0D, delta.z);
                double distance = flat.length();
                if (distance > 1.0E-4D) {
                    direction = flat.scale(1.0D / distance);
                    speed = Mth.clamp(distance / JUMP_SPEED_DIVISOR, JUMP_MIN_SPEED, JUMP_MAX_SPEED);
                }
            }
            this.mob.startLeap(direction, speed);
            this.jumped = true;
            this.mob.playJumpLaunchSound();
        }
        if (this.jumped && this.mob.isLeaping()) {
            if (this.mob.onGround() && this.mob.getLeapTicks() > 1) {
                this.beginLanding();
                this.stopLeapMotion();
                return;
            }
            if (this.mob.getDeltaMovement().y < 0.0D && this.ticksToGround() <= JUMP_LAND_HIT_TICK) {
                this.beginLanding();
                return;
            }
        }
        if (this.actionTick > JUMP_TIMEOUT_TICKS) {
            this.beginLanding();
        }
    }

    private void beginLanding() {
        this.action = ACTION_JUMP_LAND;
        this.actionTick = 0;
        this.landed = false;
        this.mob.getNavigation().stop();
        this.mob.setAnimState(GuzzlerBoss.ANIM_JUMP_LAND);
    }

    private void stopLeapMotion() {
        this.mob.stopLeap();
        this.mob.setDeltaMovement(0.0D, Math.min(this.mob.getDeltaMovement().y, 0.0D), 0.0D);
    }

    private int ticksToGround() {
        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return 0;
        }
        double groundY = BlockFinder.findGroundY(serverLevel, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        double y = this.mob.getY();
        double vy = this.mob.getDeltaMovement().y;
        for (int ticks = 0; ticks < 40; ++ticks) {
            y += vy;
            if (y <= groundY) {
                return ticks;
            }
            vy = (vy - 0.08D) * 0.98D;
        }
        return 40;
    }

    private void tickJumpLand() {
        ++this.actionTick;
        this.mob.getNavigation().stop();
        if (this.mob.isLeaping() && this.mob.onGround() && this.mob.getLeapTicks() > 1) {
            this.stopLeapMotion();
        }
        if (!this.landed && (this.actionTick >= JUMP_LAND_HIT_TICK + 4
                || (this.actionTick >= JUMP_LAND_HIT_TICK && this.mob.onGround()))) {
            this.landed = true;
            this.areaAttack(JUMP_LAND_RADIUS, this.getDamage(1.5F), 0.9D);
            this.groundBurst(JUMP_LAND_RADIUS, 28.0F, 0.18F, 24);
            this.mob.playJumpLandSound();
        }
        if (this.actionTick >= JUMP_LAND_END_TICK) {
            this.finishAction();
        }
    }

    private void finishAction() {
        this.action = ACTION_NONE;
        this.actionTick = 0;
        this.slammed = false;
        this.jumped = false;
        this.landed = false;
        this.spinHitCooldowns.clear();
        this.mob.getNavigation().stop();
        this.mob.stopLeap();
        LivingEntity target = this.mob.getTarget();
        if (target != null && (!target.isAlive() || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target))) {
            this.mob.setTarget(null);
        }
        if (!this.mob.isDeadOrDying()) {
            this.mob.setAnimState(GuzzlerBoss.ANIM_IDLE);
        }
    }

    private float getDamage(float multiplier) {
        return (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
    }

    private Vec3 getSlamCenter() {
        Vec3 look = this.mob.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0.0D, look.z);
        if (flat.lengthSqr() < 1.0E-6D) {
            return this.mob.position();
        }
        return this.mob.position().add(flat.normalize().scale(SLAM_FORWARD_OFFSET));
    }

    private void areaAttack(double radius, float damage, double knockback) {
        this.areaAttack(radius, damage, knockback, this.mob.position());
    }

    private void areaAttack(double radius, float damage, double knockback, Vec3 center) {
        for (LivingEntity target : this.getTargets(radius, center)) {
            this.hurtTarget(target, damage, knockback, center);
        }
    }

    private java.util.List<LivingEntity> getTargets(double radius) {
        return this.getTargets(radius, this.mob.position());
    }

    private java.util.List<LivingEntity> getTargets(double radius, Vec3 center) {
        AABB area = new AABB(
                center.x - radius, center.y - HIT_VERTICAL_RANGE, center.z - radius,
                center.x + radius, center.y + HIT_VERTICAL_RANGE, center.z + radius);
        java.util.List<LivingEntity> targets = new java.util.ArrayList<>();
        for (LivingEntity target : this.mob.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (target == this.mob || !target.isAlive()) {
                continue;
            }
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) || MobUtil.areAllies(this.mob, target)) {
                continue;
            }
            double dx = target.getX() - center.x;
            double dz = target.getZ() - center.z;
            if (dx * dx + dz * dz > radius * radius) {
                continue;
            }
            if (Math.abs(target.getY() - center.y) > HIT_VERTICAL_RANGE) {
                continue;
            }
            targets.add(target);
        }
        return targets;
    }

    private boolean hurtTarget(LivingEntity target, float damage, double knockback) {
        return this.hurtTarget(target, damage, knockback, this.mob.position());
    }

    private boolean hurtTarget(LivingEntity target, float damage, double knockback, Vec3 center) {
        if (target.hurt(this.mob.damageSources().mobAttack(this.mob), damage)) {
            Vec3 offset = target.position().subtract(center);
            Vec3 flat = new Vec3(offset.x, 0.0D, offset.z);
            if (flat.lengthSqr() > 1.0E-6D) {
                flat = flat.normalize();
                target.push(flat.x * knockback, knockback * 0.5D, flat.z * knockback);
            }
            if (target instanceof Player player && player.isBlocking()) {
                player.disableShield(true);
            }
            return true;
        }
        return false;
    }

    private void groundBurst(double radius, float shakeRadius, float shakeMagnitude, int shakeTicks) {
        this.groundBurst(radius, shakeRadius, shakeMagnitude, shakeTicks, this.mob.position());
    }

    private void groundBurst(double radius, float shakeRadius, float shakeMagnitude, int shakeTicks, Vec3 center) {
        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double groundY = BlockFinder.findGroundY(serverLevel, center.x, center.y, center.z);
        BlockState state = Blocks.CRIMSON_NYLIUM.defaultBlockState();
        BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
        ServerParticleUtil.circularParticles(serverLevel, option, center.x, groundY + 0.25D, center.z, (float) radius);
        for (int i = 0; i < 4; ++i) {
            double angle = Math.PI * 0.5D * (double) i;
            double offsetX = Math.cos(angle) * radius * 0.55D;
            double offsetZ = Math.sin(angle) * radius * 0.55D;
            ServerParticleUtil.circularParticles(serverLevel, option,
                    center.x + offsetX, groundY + 0.25D, center.z + offsetZ, (float) (radius * 0.45D));
        }
        ColorUtil color = IMPACT_COLOR;
        serverLevel.sendParticles(new SmashParticleOption(color, (float) (radius + 1.5D), 12),
                center.x, groundY, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new CircleExplodeParticleOption(color, (float) (radius * 1.3D), 1),
                center.x, groundY + 0.05D, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        for (int i = 0; i < 8; ++i) {
            double angle = Math.PI * 0.25D * (double) i;
            double directionX = Math.cos(angle);
            double directionZ = Math.sin(angle);
            for (int ring = 0; ring < 2; ++ring) {
                double distance = ring == 0 ? radius * 0.6D : radius;
                serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(),
                        center.x + directionX * distance, groundY + 0.25D, center.z + directionZ * distance,
                        0, (double) color.red(), (double) color.green(), (double) color.blue(), 0.5D);
            }
        }
        CameraShake.cameraShake(serverLevel, center, shakeRadius, shakeMagnitude, 0, shakeTicks);
    }

}
