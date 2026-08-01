package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantWitherSkeletonServant;

import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MutantWitherSkeletonLungeGoal extends Goal {
    public MutantWitherSkeletonServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public final double initialSlowDownSpeed;
    public double slowDownSpeed;
    public AABB targetBbOnLunge;
    public boolean hitTarget;
    public Vec3 lungeMotion;
    public int stunHits;
    public Vec3 lastPos;
    public List<LivingEntity> alreadyHit = Lists.newArrayList();

    public MutantWitherSkeletonLungeGoal(MutantWitherSkeletonServant mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        this.mob = mob;
        this.target = mob.getTarget();
        this.initialSlowDownSpeed = 1.0;
    }

    public boolean isInterruptable() {
        return this.mob.shouldBeStationary();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.mob.tickCount >= this.nextUseTime && MiscUtils.isEntityValid(this.target) && (MiscUtils.isEntityAttackableMin(this.mob, this.target, (Double)MutantWitherSkeletonCommonConfig.min_lunge_distance.get()) || Mth.abs((float)(this.mob.getY() - this.target.getY())) > 5.0F);
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation("lunge").isPlaying() || this.mob.getAnimation("lunging").isPlaying() || this.mob.getAnimation("lunge_land").isPlaying() || this.mob.getAnimation("stunned").isPlaying();
    }

    public void start() {
        this.mob.stopAndLockIdleAnimations(4, true);
        this.mob.getAnimation("lunge").start(0.75F, 4, () -> {
            if (MiscUtils.isEntityValid(this.target)) {
                this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_BIGJUMP.get(), 5.0F, 1.0F);
                ShakeCameraEvent.shake(this.mob.level(), 10, 0.1F, this.mob.blockPosition(), 10);
                this.mob.getAnimation("lunging").startLooping(4);
                double x = this.target.getX() - this.mob.getX();
                double y = this.target.getY() - this.mob.getY();
                double z = this.target.getZ() - this.mob.getZ();
                this.lungeMotion = (new Vec3(0.0, 0.5, 0.0)).add((new Vec3(x, y * 0.55, z)).scale(0.275));
                this.mob.setDeltaMovement(this.lungeMotion);
                this.targetBbOnLunge = this.target.getBoundingBox();
                this.hitTarget = false;
            } else {
                this.mob.unlockIdleAnimations();
            }

        });
        this.alreadyHit.clear();
        this.stunHits = 0;
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_DOUBLEATTACK.get());
    }

    public void tick() {
        this.target = this.mob.getTarget();
        double distanceTravelled = 0.0;
        this.mob.getNavigation().stop();
        List<AABB> boundingBoxes = Lists.newArrayList();
        int breakableBlocks;
        Vec3 pos2;
        if (this.lastPos != null) {
            distanceTravelled = (double)PositionUtils.distanceBetweenVecs(this.lastPos, this.mob.position());

            for(breakableBlocks = 0; (double)breakableBlocks < distanceTravelled; ++breakableBlocks) {
                Vec2 rot = PositionUtils.rotationToFace(this.mob.position(), this.lastPos);
                pos2 = PositionUtils.getOffsetPos(this.mob.position(), 0.0, 0.0, (double)breakableBlocks, rot.x, rot.y).subtract(this.mob.position());
                boundingBoxes.add(this.mob.getBoundingBox().move(pos2));
            }
        }

        this.lastPos = this.mob.position();
        if (MiscUtils.isEntityValid(this.target) && this.mob.getAnimation("lunge").isPlaying()) {
            this.mob.lookAt(Anchor.EYES, this.target.getEyePosition());
        }

        Iterator var12;
        AABB aabb;
        if (this.mob.getAnimation("lunging").isPlaying() && (double)this.mob.getAnimation("lunging").progress() > 0.1) {
            if (this.mob.getDeltaMovement().y > 0.0 && this.lungeMotion.y > Math.max(Math.abs(this.lungeMotion.x), Math.abs(this.lungeMotion.z))) {
                if (false) { // disabled - no block griefing
                    boolean flag = false;
                    var12 = boundingBoxes.iterator();

                    label242:
                    while(var12.hasNext()) {
                        aabb = (AABB)var12.next();
                        AABB inflated = aabb.inflate(-0.1, 2.0, -0.1);
                        Iterator var8 = BlockPos.betweenClosed(Mth.floor(inflated.minX), Mth.floor(aabb.minY + 1.0), Mth.floor(inflated.minZ), Mth.floor(inflated.maxX), Mth.floor(inflated.maxY), Mth.floor(inflated.maxZ)).iterator();

                        while(true) {
                            BlockPos blockpos;
                            BlockState blockstate;
                            do {
                                if (!var8.hasNext()) {
                                    continue label242;
                                }

                                blockpos = (BlockPos)var8.next();
                                blockstate = this.mob.level().getBlockState(blockpos);
                            } while(blockstate.is(Blocks.UNBREAKABLE));

                            flag = this.mob.level().destroyBlock(blockpos, (Boolean)MutantWitherSkeletonCommonConfig.lunge_griefing_drops_blocks.get() || (Boolean)MutantMoreGroupedOptionsCommonConfig.griefing_drops_blocks_on.get(), this.mob) || flag;
                        }
                    }
                }

                if (MiscUtils.isEntityValid(this.target) && this.mob.getY() > this.target.getY() + 2.0) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().x, 0.0, this.mob.getDeltaMovement().z);
                }

                this.mob.noPhysics = true;
            } else {
                this.mob.noPhysics = false;
            }

            if (MiscUtils.isEntityValid(this.target)) {
                double x = this.target.getX() - this.mob.getX();
                double z = this.target.getZ() - this.mob.getZ();
                Vec3 newMotion = (new Vec3(x, 0.0, z)).scale(0.1);
                this.mob.push((double)Mth.abs((float)this.mob.getDeltaMovement().x) <= 0.01 ? newMotion.x : 0.0, 0.0, (double)Mth.abs((float)this.mob.getDeltaMovement().z) <= 0.01 ? newMotion.z : 0.0);
            }

            if (this.mob.verticalCollisionBelow) {
                this.slowDownSpeed = this.initialSlowDownSpeed;
                this.mob.getAnimation("lunging").stop(0);
                this.mob.getAnimation("lunge_land").start(2.75F, 0, () -> {
                    this.mob.unlockIdleAnimations();
                    this.mob.getAnimation("idle").startLooping(0);
                });
                ShakeCameraEvent.shake(this.mob.level(), 20, 0.15F, this.mob.blockPosition(), 15);
                this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_BIGLAND.get(), 5.0F, 1.0F);
            }
        }

        if (this.mob.getAnimation("lunging").isPlaying() || this.mob.getAnimation("lunge_land").isPlaying()) {
            List<LivingEntity> alreadyMoved = Lists.newArrayList();
            var12 = boundingBoxes.iterator();

            while(var12.hasNext()) {
                aabb = (AABB)var12.next();
                Iterator var22 = this.mob.level().getEntitiesOfClass(LivingEntity.class, aabb).iterator();

                while(var22.hasNext()) {
                    LivingEntity entity = (LivingEntity)var22.next();
                    if (entity != this.mob && this.mob.canHarm(entity) && this.mob.getAnimation("lunging").isPlaying() && this.mob.canHarm(entity)) {
                        if (!alreadyMoved.contains(entity)) {
                            entity.hurtMarked = true;
                            entity.push(this.mob.getDeltaMovement().x, 0.01, this.mob.getDeltaMovement().z);
                            alreadyMoved.add(entity);
                        }

                        if (!this.alreadyHit.contains(entity)) {
                            entity.hurt(this.mob.damageSources().mobAttack(this.mob), ((Double)MutantWitherSkeletonCommonConfig.lunge_damage.get()).floatValue());
                            MiscUtils.disableShield(entity, (Integer)MutantWitherSkeletonCommonConfig.lunge_disable_shield_length.get());
                            this.alreadyHit.add(entity);
                        }
                    }
                }
            }
        }

        Iterator var15 = boundingBoxes.iterator();

        while(var15.hasNext()) {
            aabb = (AABB)var15.next();
            if (this.mob.getAnimation("lunging").isPlaying() && this.targetBbOnLunge.intersects(aabb)) {
                this.hitTarget = true;
                break;
            }
        }

        if (this.mob.getAnimation("lunging").isPlaying() && this.hitTarget) {
            this.mob.push(0.0, -1.0, 0.0);
        }

        BlockPos blockpos;
        BlockState blockstate;
        if (this.mob.getAnimation("lunge_land").isPlaying() && this.mob.onGround()) {
            if (this.slowDownSpeed > 0.1) {
                breakableBlocks = Mth.floor(this.mob.getX());
                int j = Mth.floor(this.mob.getY() - 0.20000000298023224);
                int k = Mth.floor(this.mob.getZ());
                blockpos = new BlockPos(breakableBlocks, j, k);
                blockstate = this.mob.level().getBlockState(blockpos);
                if (!blockstate.isAir()) {
                    ((ServerLevel)this.mob.level()).sendParticles((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), this.mob.getX(), this.mob.getY() + 0.1, this.mob.getZ(), 5, 4.0 * ((double)this.mob.getRandom().nextFloat() - 0.5), 0.5, ((double)this.mob.getRandom().nextFloat() - 0.5) * 4.0, (double)this.mob.getBbWidth());
                }
            }

            if (this.mob.getAnimation("lunge_land").progress() >= 0.67F && this.slowDownSpeed > 0.1 && this.lastPos != null) {
                for(float f = 0.0F; (double)f < distanceTravelled; f += 0.1F) {
                    Vec3 pos1 = PositionUtils.getOffsetPos(this.lastPos, (double)this.mob.getBbWidth() * 0.8, 0.001 + (double)(this.mob.getAnimation("lunge_land").progress() - 1.0F) * 0.001, (double)this.mob.getBbWidth() * 0.6 + (double)f, 0.0F, this.mob.yBodyRot);
                    pos2 = PositionUtils.getOffsetPos(this.lastPos, (double)(-this.mob.getBbWidth()) * 0.8, 0.001 + (double)(this.mob.getAnimation("lunge_land").progress() - 1.0F) * 0.001, (double)this.mob.getBbWidth() * 0.6 + (double)f, 0.0F, this.mob.yBodyRot);
                    Color particleColour = new Color(0);
                    ((ServerLevel)this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.GROUND_TRAIL, List.of(0.15F, 0.0F, (float)particleColour.getRGB(), this.mob.yBodyRot, 200.0F, 1.0F, 0.0F)), pos1.x, pos1.y, pos1.z, 1, 0.0, 0.0, 0.0, 0.0);
                    ((ServerLevel)this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.GROUND_TRAIL, List.of(0.15F, 0.0F, (float)particleColour.getRGB(), this.mob.yBodyRot, 200.0F, 1.0F, 0.0F)), pos2.x, pos2.y, pos2.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }

            if (this.willFallToDoom(this.mob)) {
                this.slowDownSpeed = Mth.lerp(0.75, this.slowDownSpeed, 0.0);
            } else if (this.mob.getAnimation("lunge_land").progress() >= 0.67F) {
                this.slowDownSpeed = Mth.lerp((double)Mth.clamp(this.mob.getAnimation("lunge_land").progress() - 0.67F, 0.0F, 1.0F), this.initialSlowDownSpeed / 2.0, 0.0);
            }

            Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.0, 0.0, this.slowDownSpeed, 0.0F, this.mob.yBodyRot);
            this.mob.setDeltaMovement(motion.x, this.mob.getDeltaMovement().y, motion.z);
        }

        if (this.lungeMotion != null && (!(this.mob.getDeltaMovement().y > 0.0) || !(this.lungeMotion.y > Math.max(Math.abs(this.lungeMotion.x), Math.abs(this.lungeMotion.z)))) && (this.mob.getAnimation("lunging").isPlaying() || this.mob.getAnimation("lunge_land").isPlaying() && (double)this.mob.getAnimation("lunge_land").progress() <= 0.5) && this.lungeMotion.y < Math.max(this.lungeMotion.x, this.lungeMotion.z)) {
            breakableBlocks = 0;
            aabb = this.mob.getBoundingBox().deflate(0.1).move(PositionUtils.getOffsetPos(Vec3.ZERO, 0.0, 0.0, (double)(this.mob.getBbWidth() / 2.0F), 0.0F, this.mob.yBodyRot));
            Iterator var26 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

            while(true) {
                do {
                    do {
                        do {
                            do {
                                if (!var26.hasNext()) {
                                    if (breakableBlocks >= 4) {
                                        ShakeCameraEvent.shake(this.mob.level(), 10, 0.5F, this.mob.blockPosition(), 15);
                                        this.slowDownSpeed = this.initialSlowDownSpeed;
                                        this.mob.getAnimation("lunging").stop(0);
                                        this.mob.getAnimation("lunge_land").stop(0);
                                        this.mob.getAnimation("stunned").start(5.5F, 0, () -> {
                                            this.mob.getAnimation("stunned").setTransitionTicks(4);
                                            this.mob.unlockIdleAnimations();
                                        });
                                        this.mob.setDeltaMovement(0.0, 0.0, 0.0);
                                        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_BIGLAND.get(), 5.0F, 1.0F);
                                    }

                                    return;
                                }

                                blockpos = (BlockPos)var26.next();
                                blockstate = this.mob.level().getBlockState(blockpos);
                            } while(blockstate.is(Blocks.UNBREAKABLE));
                        } while(blockstate.isAir());
                    } while(!blockstate.getFluidState().isEmpty());

                    ++breakableBlocks;
                } while(!(Boolean)MutantWitherSkeletonCommonConfig.lunge_griefing.get());

                // this.mob.level().destroyBlock(blockpos, ...); // disabled - no block griefing
            }
        }
    }

    boolean willFallToDoom(MutantWitherSkeletonServant mob) {
        boolean blockBeneath = false;
        boolean lavaBeneath = false;
        BlockPos pos = PositionUtils.getOffsetBlockPos(mob, 0.0, 0.0, 2.0, 0.0F, mob.yBodyRot);

        for(int i = 0; i < 10; ++i) {
            if (!mob.level().getBlockState(pos.offset(0, -i, 0)).isAir() && mob.level().getFluidState(pos.offset(0, -i, 0)).isEmpty()) {
                blockBeneath = true;
            }

            if (!blockBeneath && !mob.level().getFluidState(pos.offset(0, -i, 0)).isEmpty()) {
                lavaBeneath = true;
                break;
            }
        }

        return !mob.level().isClientSide && (!blockBeneath || lavaBeneath);
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer)MutantWitherSkeletonCommonConfig.lunge_cooldown.get();
    }
}