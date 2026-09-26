package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public class AbstractFlamebornServant extends IAnimatedMobServant {

    public AbstractFlamebornServant(EntityType entity, Level world) {
        super(entity, world);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.fallDistance > 10.0F) {
            LivingEntity anchor = this.targetIsNotNull() ? this.target() : this;
            this.teleportRandomly(anchor, this.targetIsNotNull() ? 10.0F : 25.0F, 15.0F);
        }
        super.tick();
    }

    public void teleportRandomly(LivingEntity entity, float range, float iteractions) {
        Vec3 entityPos = entity.position();
        Level level = this.level();
        for (int i = 0; (float) i < iteractions; ++i) {
            double x = entityPos.x() + (this.getRandom().nextDouble() - 0.5D) * (double) range;
            double z = entityPos.z() + (this.getRandom().nextDouble() - 0.5D) * (double) range;
            double y = entityPos.y();
            BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
            if (level.isEmptyBlock(pos) && !level.getBlockState(pos.below()).isAir()) {
                this.teleport(x, y, z);
                return;
            }
        }
    }

    public boolean teleport(double x, double y, double z) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        while (mutablePos.getY() > level.getMinBuildHeight() && !level.getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }
        BlockState state = level.getBlockState(mutablePos);
        if (!state.blocksMotion()) {
            return false;
        }
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
        if (event.isCanceled()) {
            return false;
        }
        Vec3 oldPos = this.position();
        boolean teleported = this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (teleported) {
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                CameraShakeEntity.cameraShake(level, this.position(), 10.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.SHULKER_TELEPORT, 4.0F, 1.0F);
            }
        }
        return teleported;
    }

    public boolean teleportBoolean(double x, double y, double z, boolean playSound) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        double targetY = y;
        boolean success = false;
        BlockPos pos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(pos)) {
            boolean foundGround = false;
            while (!foundGround && pos.getY() > level.getMinBuildHeight()) {
                BlockPos below = pos.below();
                if (level.getBlockState(below).blocksMotion()) {
                    foundGround = true;
                    continue;
                }
                targetY -= 1.0D;
                pos = below;
            }
            if (foundGround) {
                this.teleportTo(x, targetY, z);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    success = true;
                }
            }
        }
        if (!success) {
            this.teleportTo(oldX, oldY, oldZ);
            return false;
        }
        if (playSound) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }
}
