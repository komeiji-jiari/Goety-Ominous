package com.qiuyue.goetyominus.common.entities.ally.ua.goals;

import com.qiuyue.goetyominus.common.entities.ally.ua.ThrasherServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class ThrasherServantThrashGoal extends Goal {
    public ThrasherServant thrasher;
    private float originalYaw;
    private int thrashedTicks;

    public ThrasherServantThrashGoal(ThrasherServant thrasher) {
        this.thrasher = thrasher;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Entity passenger = this.thrasher.getFirstPassenger();
        if (passenger instanceof Player player) {
            if (player.isCreative() || passenger.isSpectator()) {
                return false;
            }
        }
        return !this.thrasher.isStunned() && passenger != null && this.thrasher.isNoEndimationPlaying() && this.thrasher.getRandom().nextFloat() < 0.1F;
    }

    @Override
    public boolean canContinueToUse() {
        Entity passenger = this.thrasher.getFirstPassenger();
        if (passenger instanceof Player player) {
            if (player.isCreative() || passenger.isSpectator()) {
                return false;
            }
        }
        return !this.thrasher.isStunned() && this.thrashedTicks <= 55 && passenger != null;
    }

    @Override
    public void start() {
        this.originalYaw = this.thrasher.getYRot();
        this.thrasher.setHitsTillStun(this.thrasher.getRandom().nextInt(2) + 2);
        com.teamabnormals.blueprint.core.util.NetworkUtil.setPlayingAnimation(this.thrasher, com.teamabnormals.upgrade_aquatic.core.registry.UAPlayableEndimations.THRASHER_THRASH);
    }

    @Override
    public void stop() {
        this.originalYaw = 0;
        this.thrashedTicks = 0;
        com.teamabnormals.blueprint.core.endimator.PlayableEndimation blank = com.teamabnormals.blueprint.core.endimator.PlayableEndimation.BLANK;
        com.teamabnormals.blueprint.core.util.NetworkUtil.setPlayingAnimation(this.thrasher, blank);
    }

    @Override
    public void tick() {
        this.thrashedTicks++;
        this.thrasher.getNavigation().stop();
        this.thrasher.yRotO = this.thrasher.getYRot();
        this.thrasher.yBodyRot = (this.originalYaw) + 75 * Mth.cos(this.thrasher.tickCount * 0.5F) * 1F;
        this.thrasher.setYRot((this.originalYaw) + 75 * Mth.cos(this.thrasher.tickCount * 0.5F) * 1F);

        Entity entity = this.thrasher.getFirstPassenger();
        if (entity instanceof Player player) {
            this.disablePlayersShield(player);
        }

        if (entity != null) {
            entity.setShiftKeyDown(false);
        }

        if (this.thrashedTicks % 5 == 0 && this.thrashedTicks > 0) {
            this.thrasher.playSound(this.thrasher.getThrashingSound(), 1.0F, Math.max(0.75F, this.thrasher.getRandom().nextFloat()));
            if (entity != null) {
                entity.hurt(entity.damageSources().mobAttack(this.thrasher), (float) this.thrasher.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void disablePlayersShield(Player player) {
        player.getCooldowns().addCooldown(Items.SHIELD, 30);
    }
}