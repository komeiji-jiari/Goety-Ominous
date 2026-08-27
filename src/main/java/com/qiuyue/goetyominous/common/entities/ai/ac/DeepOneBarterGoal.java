package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneServant;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * 深潜者仆从的交易目标 —— 祭坛中介式,复用 AC 原版渊海祭坛(AbyssalAltar)。
 *
 * 仅在游荡(自由漫游)状态下交易:跟随/停留/守卫/被命令的仆从都不会去交易,避免离队或被命令后跑去祭坛。
 * 聚晶(Goety Focus)召唤的仆从(DeepOneServant#isFocusSummoned)也不能交易。发现 64 格内有祭坛装着珍珠/海洋之心
 * (alexscaves:deep_one_barters)时走过去,把物品直接取进主手并播放交易动画;动画尾声由实体 tick
 * (DeepOneServant#tick)roll 战利品表把奖励放回祭坛 slot 0。
 *
 * 刻意不走原版 AbyssalAltarBlockEntity#queueItemDrop/onEntityInteract —— 原版祭坛的 tick 只把弹出去
 * 的物品塞给 instanceof DeepOneBaseEntity 的实体,本仆从是 Goety Summoned,会被它当作普通掉落吞掉且
 * 不触发交易动画。所以交易全程由本类 + 实体 tick 驱动,祭坛仅充当容器与 POI 锚点。
 */
public class DeepOneBarterGoal extends Goal {

    private final DeepOneServant mob;
    private BlockPos altarPos;
    private int executionCooldown = 10;

    public DeepOneBarterGoal(DeepOneServant mob) {
        this.mob = mob;
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
        if (this.mob.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            return false;
        }
        // 只在游荡(自由漫游)状态下交易;跟随/停留/守卫/被命令时都不交易
        if (!this.mob.isWandering() || this.mob.isCommanded()) {
            return false;
        }
        // 聚晶召唤的仆从不能交易
        if (this.mob.isFocusSummoned()) {
            return false;
        }
        if (this.executionCooldown-- > 0) {
            return false;
        }
        this.executionCooldown = 150 + this.mob.getRandom().nextInt(100);
        BlockPos pos = null;
        if (this.mob.getLastAltarPos() != null) {
            BlockEntity blockEntity = this.mob.level().getBlockEntity(this.mob.getLastAltarPos());
            if (blockEntity instanceof AbyssalAltarBlockEntity altar) {
                this.executionCooldown = 10;
                if (altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                    pos = this.mob.getLastAltarPos();
                }
            } else {
                this.mob.setLastAltarPos(null);
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
            this.mob.setLastAltarPos(this.altarPos);
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
        // 模式中途变化(离开游荡)就停止拉近祭坛;已在播的交易动画仍会由实体 tick 自然收尾
        if (!this.mob.isWandering() || this.mob.isCommanded()) {
            return false;
        }
        return hasPearls(this.mob.level(), this.altarPos) || this.mob.getAnimation() == this.mob.getTradingAnimation();
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
        } else if (this.mob.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.mob.setLastAltarPos(this.altarPos);
            this.mob.getNavigation().stop();
            BlockEntity blockEntity = this.mob.level().getBlockEntity(this.altarPos);
            if (blockEntity instanceof AbyssalAltarBlockEntity altar && altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                ItemStack pearl = altar.getItem(0).copy();
                altar.setItem(0, ItemStack.EMPTY);
                this.mob.swapItemsForAnimation(pearl);
                this.mob.setAnimation(this.mob.getTradingAnimation());
                this.mob.playSound(this.mob.getAdmireSound());
            }
        }
    }
}
