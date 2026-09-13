package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathResult;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LuxtructosaurusServantMeleeGoal extends Goal {
    private final LuxtructosaurusServant luxtructosaurus;
    private int navigationCheckCooldown;
    private int flamesCooldown;
    private int successfulJumpCooldown;

    public LuxtructosaurusServantMeleeGoal(LuxtructosaurusServant luxtructosaurus) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.luxtructosaurus = luxtructosaurus;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.luxtructosaurus.getTarget();
        return target != null && target.isAlive()
                && !this.luxtructosaurus.isStaying() && !this.luxtructosaurus.isCommanded()
                && !this.luxtructosaurus.isSteeredByPlayer() && !this.luxtructosaurus.isImmobile();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.luxtructosaurus.getTarget() != null
                && !this.luxtructosaurus.isStaying() && !this.luxtructosaurus.isCommanded()
                && !this.luxtructosaurus.isSteeredByPlayer() && !this.luxtructosaurus.isImmobile();
    }

    @Override
    public void start() {
        this.navigationCheckCooldown = 0;
        this.flamesCooldown = 0;
    }

    @Override
    public void stop() {
        this.luxtructosaurus.turningFast = false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.luxtructosaurus.getTarget();
        boolean ridden = this.luxtructosaurus.isRiddenByPlayer();
        if (target != null && target.isAlive()) {
            double distance = this.luxtructosaurus.distanceTo(target);
            double attackDistance = this.luxtructosaurus.getBbWidth() + target.getBbWidth();
            if (this.luxtructosaurus.getAnimation() == LuxtructosaurusServant.ANIMATION_SPEW_FLAMES
                    || this.luxtructosaurus.getAnimation() == LuxtructosaurusServant.ANIMATION_LEFT_KICK
                    || this.luxtructosaurus.getAnimation() == LuxtructosaurusServant.ANIMATION_RIGHT_KICK
                    || this.luxtructosaurus.getAnimation() == LuxtructosaurusServant.ANIMATION_LEFT_WHIP
                    || this.luxtructosaurus.getAnimation() == LuxtructosaurusServant.ANIMATION_RIGHT_WHIP) {
                this.luxtructosaurus.turningFast = true;
                Vec3 vec3 = target.position().subtract(this.luxtructosaurus.position());
                this.luxtructosaurus.yBodyRotO = this.luxtructosaurus.yBodyRot = Mth.approachDegrees(
                        this.luxtructosaurus.yBodyRot,
                        -((float) Mth.atan2(vec3.x, vec3.z)) * 57.295776F, 15.0F);
                this.luxtructosaurus.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            } else {
                this.luxtructosaurus.turningFast = false;
            }
            if (distance > attackDistance) {
                this.luxtructosaurus.getNavigation().moveTo(target, 1.0D);
            }
            if (this.luxtructosaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                if (distance < attackDistance + 4.0D) {
                    float random = this.luxtructosaurus.getRandom().nextFloat();
                    if (random < 0.33F && this.luxtructosaurus.onGround()) {
                        this.luxtructosaurus.setAnimation(LuxtructosaurusServant.ANIMATION_STOMP);
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ATTACK_STOMP.get(), 3.0F,
                                this.luxtructosaurus.getVoicePitch());
                    } else if (random < 0.66F && distance < attackDistance + 1.0D) {
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_KICK.get(), 3.0F,
                                this.luxtructosaurus.getVoicePitch());
                        this.luxtructosaurus.setAnimation(this.luxtructosaurus.getRandom().nextBoolean()
                                ? LuxtructosaurusServant.ANIMATION_LEFT_KICK : LuxtructosaurusServant.ANIMATION_RIGHT_KICK);
                    } else {
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_TAIL.get(), 3.0F,
                                this.luxtructosaurus.getVoicePitch());
                        this.luxtructosaurus.setAnimation(this.luxtructosaurus.getRandom().nextBoolean()
                                ? LuxtructosaurusServant.ANIMATION_RIGHT_WHIP : LuxtructosaurusServant.ANIMATION_LEFT_WHIP);
                    }
                } else if (this.luxtructosaurus.isEnraged() && this.flamesCooldown == 0) {
                    this.flamesCooldown = 200 + this.luxtructosaurus.getRandom().nextInt(300);
                    this.luxtructosaurus.setAnimation(LuxtructosaurusServant.ANIMATION_SPEW_FLAMES);
                }
            }
            if (!ridden && this.successfulJumpCooldown <= 0 && this.navigationCheckCooldown-- < 0
                    && (this.luxtructosaurus.onGround() || this.luxtructosaurus.isInLava())) {
                this.navigationCheckCooldown = 20 + this.luxtructosaurus.getRandom().nextInt(40);
                if (!this.canReach(target) && this.luxtructosaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    this.luxtructosaurus.setAnimation(LuxtructosaurusServant.ANIMATION_JUMP);
                    this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_JUMP.get(), 5.0F,
                            this.luxtructosaurus.getVoicePitch());
                    this.luxtructosaurus.jumpTarget = target.position();
                    this.successfulJumpCooldown = 100 + this.luxtructosaurus.getRandom().nextInt(200);
                }
            }
        }
        if (this.flamesCooldown > 0) {
            --this.flamesCooldown;
        }
        if (this.successfulJumpCooldown > 0) {
            --this.successfulJumpCooldown;
        }
    }

    private boolean canReach(LivingEntity target) {
        if (target.distanceTo(this.luxtructosaurus) > 50.0F) {
            return false;
        }
        if (!(this.luxtructosaurus.getNavigation() instanceof AdvancedPathNavigate navigate)) {
            return true;
        }
        PathResult<?> pathResult = navigate.moveToLivingEntity(target, 1.0D);
        if (pathResult == null || pathResult.getPath() == null) {
            return false;
        }
        Node node = pathResult.getPath().getEndNode();
        if (node == null) {
            return false;
        }
        int i = node.x - target.getBlockX();
        int j = node.y - target.getBlockY();
        int k = node.z - target.getBlockZ();
        return (double) (i * i + j * j + k * k) <= 3.0D;
    }
}
