package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public abstract class FarTeleportFollowGoal<T extends IAnimatedMonsterServant> extends Summoned.FollowOwnerGoal<T> {

    private static final int TELEPORT_COOLDOWN = 200;

    private int teleportTicks;

    public FarTeleportFollowGoal(T summonedEntity, double speed, float startDistance, float stopDistance) {
        super(summonedEntity, speed, startDistance, stopDistance);
    }

    protected abstract int teleportState();

    protected abstract int teleportDuration();

    protected abstract void saveTeleportPosition(double x, double y, double z);

    @Override
    public boolean canContinueToUse() {
        return this.teleportTicks > 0 || super.canContinueToUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return this.teleportTicks > 0;
    }

    @Override
    public void tick() {
        if (this.teleportTicks > 0) {
            --this.teleportTicks;
            if (this.teleportTicks == 0) {
                this.endTeleport();
            }
            return;
        }
        super.tick();
    }

    @Override
    public void stop() {
        if (this.teleportTicks > 0) {
            this.teleportTicks = 0;
            this.endTeleport();
        }
        super.stop();
    }

    @Override
    protected boolean canTeleport() {
        return this.summonedEntity.followTeleportCooldown <= 0;
    }

    @Override
    protected void tryToTeleportNearEntity() {
        if (this.summonedEntity.followTeleportCooldown > 0) {
            this.navigation.moveTo(this.owner, this.followSpeed);
            return;
        }
        BlockPos ownerPos = this.owner.blockPosition();
        int radius = Math.max(3, Mth.ceil(this.summonedEntity.getBbWidth() / 2.0F) + 2);
        for (int i = 0; i < 20; ++i) {
            int x = this.getRandomNumber(-radius, radius);
            int y = this.getRandomNumber(-1, 1);
            int z = this.getRandomNumber(-radius, radius);
            BlockPos pos = new BlockPos(ownerPos.getX() + x, ownerPos.getY() + y, ownerPos.getZ() + z);
            if (Math.abs((double) pos.getX() - this.owner.getX()) < 2.0 && Math.abs((double) pos.getZ() - this.owner.getZ()) < 2.0) {
                continue;
            }
            if (!this.isTeleportFriendlyBlock(pos)) {
                continue;
            }
            this.summonedEntity.getNavigation().stop();
            this.teleportTicks = this.teleportDuration();
            this.summonedEntity.followTeleportCooldown = TELEPORT_COOLDOWN;
            this.saveTeleportPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            this.summonedEntity.setAttackState(this.teleportState());
            return;
        }
        this.navigation.moveTo(this.owner, this.followSpeed);
    }

    private void endTeleport() {
        if (this.summonedEntity.getAttackState() == this.teleportState()) {
            this.summonedEntity.setAttackState(0);
        }
    }
}
