package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Raycat护符(Alex's Caves 联动)
 * 功能与 Goety 的 Feline Amulet(猫的护身符)完全一致:
 * 佩戴后附近的苦力怕会逃跑,并且苦力怕/幻翼不会将佩戴者设为攻击目标。
 * 额外复刻 Alex's Caves 雷猫的能力:
 * 核能苦力怕(含其他玩家的仆从与无主仆从)会被驱赶并退引信;
 * 佩戴者自身与友军受到的辐照会被转化为生命恢复 I(15秒)。
 */
public class RaycatAmuletItem extends Item implements ICurioItem {

    public RaycatAmuletItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("info.goetyominous.raycat_amulet").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("info.goetyominous.raycat_amulet.radiation").withStyle(ChatFormatting.GRAY));
    }

    /**
     * 判断实体是否佩戴了 Raycat 护符。
     * 该物品仅在 Alex's Caves 加载时注册(AcItems),未加载时 isPresent() 为 false。
     */
    public static boolean hasAmulet(LivingEntity entity) {
        if (!AcItems.RAYCAT_AMULET.isPresent()) {
            return false;
        }
        return CuriosFinder.hasCurio(entity, AcItems.RAYCAT_AMULET.get());
    }
}
