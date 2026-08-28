package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public class DeepOneBarterGoal extends Goal {

    private final Mob mob;
    private final IDeepOneBarterer barterer;
    private BlockPos altarPos;
    private int executionCooldown = 10;

    public DeepOneBarterGoal(Mob mob) {
        if (!(mob instanceof IDeepOneBarterer barterer)) {
            throw new IllegalArgumentException("DeepOneBarterGoal requires an IDeepOneBarterer entity");
        }
        this.mob = mob;
        this.barterer = barterer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive()) {
            return false;
        }
        if (this.barterer.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            return false;
        }
        
        if (!this.barterer.isWandering() || this.barterer.isCommanded()) {
            return false;
        }
        
        if (this.barterer.isFocusSummoned()) {
            return false;
        }
        if (this.executionCooldown-- > 0) {
            return false;
        }
        this.executionCooldown = 150 + this.mob.getRandom().nextInt(100);
        BlockPos pos = null;
        if (this.barterer.getLastAltarPos() != null) {
            BlockEntity blockEntity = this.mob.level().getBlockEntity(this.barterer.getLastAltarPos());
            if (blockEntity instanceof AbyssalAltarBlockEntity altar) {
                this.executionCooldown = 10;
                if (altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                    pos = this.barterer.getLastAltarPos();
                }
            } else {
                this.barterer.setLastAltarPos(null);
            }
        }
        if (pos == null) {
            List<BlockPos> list = getNearbyAltars(this.mob.blockPosition(), (ServerLevel) this.mob.level(), 64)
                    .sorted(Comparator.comparingDouble(p -> this.mob.blockPosition().distSqr(p)))
                    .toList();
            if (!list.isEmpty()) {
                pos = list.get(0);
            }
        }
        if (pos != null) {
            this.altarPos = pos;
            this.barterer.setLastAltarPos(this.altarPos);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (this.altarPos == null || target != null && target.isAlive()) {
            return false;
        }
        
        if (!this.barterer.isWandering() || this.barterer.isCommanded()) {
            return false;
        }
        return hasPearls(this.mob.level(), this.altarPos) || this.barterer.getAnimation() == this.barterer.getTradingAnimation();
    }

    private static Stream<BlockPos> getNearbyAltars(BlockPos blockPos, ServerLevel world, int range) {
        return world.getPoiManager().findAll(
                poiType -> poiType.is(ACPOIRegistry.ABYSSAL_ALTAR.getKey()),
                pos -> hasPearls(world, pos),
                blockPos, range, PoiManager.Occupancy.ANY);
    }

    private static boolean hasPearls(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbyssalAltarBlockEntity altar) {
            return altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)
                    && altar.getBlockState().getBlock() instanceof AbyssalAltarBlock
                    && !altar.getBlockState().getValue(AbyssalAltarBlock.ACTIVE);
        }
        return false;
    }

    @Override
    public void tick() {
        Vec3 center = Vec3.atCenterOf(this.altarPos);
        double distance = Vec3.atBottomCenterOf(this.altarPos).subtract(this.mob.position()).horizontalDistance();
        if (distance < 8.0) {
            this.mob.getLookControl().setLookAt(center.x, center.y, center.z, 10.0F, this.mob.getMaxHeadXRot());
        }
        if (distance > 3.0) {
            this.mob.getNavigation().moveTo((float) this.altarPos.getX() + 0.5F, this.altarPos.getY(), (float) this.altarPos.getZ() + 0.5F, 1.0);
        } else if (this.barterer.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.barterer.setLastAltarPos(this.altarPos);
            this.mob.getNavigation().stop();
            BlockEntity blockEntity = this.mob.level().getBlockEntity(this.altarPos);
            if (blockEntity instanceof AbyssalAltarBlockEntity altar && altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                ItemStack pearl = altar.getItem(0).copy();
                altar.setItem(0, ItemStack.EMPTY);
                this.barterer.swapItemsForAnimation(pearl);
                this.barterer.setAnimation(this.barterer.getTradingAnimation());
                this.mob.playSound(this.barterer.getAdmireSound());
            }
        }
    }
}
