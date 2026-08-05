package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/**
 * 诡异类固醇（Warped Steroids）：由疣猪蚊仆从死亡掉落。
 * <p>
 * 对属于自己的绯红蚊子仆从右键使用时，不再瞬间替换实体，而是复用
 * {@link CrimsonMosquitoServant#tick()} 里患病转化为疣猪蚊仆从的完整流程：
 * 进入患病状态（变蓝/抖动/停战），但把转化阈值从自然患病的 100/160 tick
 * 压缩到 30/80 tick（约 4 秒），渲染过程与"转化成 WarpedMosco"完全一致。
 */
public class WarpedSteroidsItem extends Item {

    public WarpedSteroidsItem() {
        super(new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // Goety 通用冷却：冷却期间直接拦截（对齐 Goety 对 ReviveServantItem 由 ItemEvents 取消交互的门禁）。
        // 客户端与服务端都会走到这里；冷却中返回 FAIL 不发送交互包、不消耗物品、不挥动手臂
        if (SEHelper.isOnCooldown(player, stack)) {
            return InteractionResult.FAIL;
        }
        // 只能对属于自己的绯红蚊子仆从使用
        if (!(target instanceof CrimsonMosquitoServant mosquito) || mosquito.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }
        // 已在患病转化中（例如喂过诡异混合物）：不重复触发，避免打乱转化节奏。
        // 放在客户端判断之前，保证两端返回一致
        if (mosquito.isSick()) {
            return InteractionResult.PASS;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        // 复用患病转化流程：进入患病状态 + 强制落地 + 启用"强化快转"。
        // 后续由 CrimsonMosquitoServant.tick() 完成 变蓝/抖动/变大/爆炸粒子 -> 原地生成 WarpedMoscoServant
        mosquito.startSteroidConversion();

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.swing(hand);
        // Goety 通用冷却（ItemConfig.ReviveSecondsCool，默认 90 秒）：转化成功后进入冷却，
        // 冷却期间 interactLivingEntity 开头的检查会拦截下一次使用
        SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(ItemConfig.ReviveSecondsCool.get()));
        return InteractionResult.SUCCESS;
    }
}
