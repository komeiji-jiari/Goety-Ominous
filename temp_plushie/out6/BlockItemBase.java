package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.blocks.ApparitionDoorBlock;
import com.Polarice3.Goety.common.blocks.ForbiddenGrassBlock;
import com.Polarice3.Goety.common.blocks.FreezeLampBlock;
import com.Polarice3.Goety.common.blocks.HauntedGlassBlock;
import com.Polarice3.Goety.common.blocks.HookBellBlock;
import com.Polarice3.Goety.common.blocks.IceBouquetTrapBlock;
import com.Polarice3.Goety.common.blocks.SculkConverterBlock;
import com.Polarice3.Goety.common.blocks.SculkDevourerBlock;
import com.Polarice3.Goety.common.blocks.SculkGrowerBlock;
import com.Polarice3.Goety.common.blocks.SculkRelayBlock;
import com.Polarice3.Goety.common.blocks.WindBlowerBlock;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    public BlockItemBase(Block blockIn) {
        super(blockIn, new Item.Properties());
    }

    public void m_7373_(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.m_7373_(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.GOLD;
        ChatFormatting secondary = ChatFormatting.LIGHT_PURPLE;
        Item glassBlock = stack.m_41720_();
        if (glassBlock instanceof BlockItemBase base) {
            if (base.m_40614_() instanceof IceBouquetTrapBlock) {
                tooltip.add(Component.m_237115_("info.goety.ice_bouquet_trap").m_130940_(main));
                tooltip.add(Component.m_237115_("info.goety.ice_bouquet_trap_extra").m_130940_(secondary));
            }

            if (base.m_40614_() instanceof WindBlowerBlock) {
                tooltip.add(Component.m_237115_("info.goety.wind_blower").m_130940_(main));
            }

            if (base.m_40614_() instanceof SculkDevourerBlock) {
                tooltip.add(Component.m_237115_("info.goety.sculk_devourer").m_130940_(main));
                tooltip.add(Component.m_237115_("info.goety.sculk_devourer_extra").m_130940_(secondary));
            }

            if (base.m_40614_() instanceof SculkConverterBlock) {
                tooltip.add(Component.m_237115_("info.goety.sculk_converter").m_130940_(main));
                tooltip.add(Component.m_237115_("info.goety.sculk_converter_extra").m_130940_(secondary));
            }

            if (base.m_40614_() instanceof SculkRelayBlock) {
                tooltip.add(Component.m_237115_("info.goety.sculk_relay").m_130940_(main));
            }

            if (base.m_40614_() instanceof SculkGrowerBlock) {
                tooltip.add(Component.m_237115_("info.goety.sculk_grower").m_130940_(main));
                tooltip.add(Component.m_237115_("info.goety.sculk_grower_extra").m_130940_(secondary));
            }

            if (base.m_40614_() instanceof ForbiddenGrassBlock) {
                tooltip.add(Component.m_237115_("info.goety.forbidden_grass").m_130940_(main));
            }

            if (base.m_40614_() instanceof HookBellBlock) {
                tooltip.add(Component.m_237115_("info.goety.hook_bell").m_130940_(main));
            }

            Block var9 = base.m_40614_();
            if (var9 instanceof HauntedGlassBlock glassBlock) {
                if (glassBlock.isPlayerOnly) {
                    if (glassBlock.isTinted) {
                        tooltip.add(Component.m_237115_("info.goety.haunted_glass_tinted").m_130940_(main));
                    } else {
                        tooltip.add(Component.m_237115_("info.goety.haunted_glass").m_130940_(main));
                    }
                } else if (glassBlock.isTinted) {
                    tooltip.add(Component.m_237115_("info.goety.haunted_glass_mob_tinted").m_130940_(main));
                } else {
                    tooltip.add(Component.m_237115_("info.goety.haunted_glass_mob").m_130940_(main));
                }
            }

            if (base.m_40614_() instanceof FreezeLampBlock) {
                tooltip.add(Component.m_237115_("info.goety.freezing_lamp").m_130940_(main));
            }

            if (base.m_40614_() instanceof ApparitionDoorBlock) {
                tooltip.add(Component.m_237115_("info.goety.apparition_door").m_130940_(main));
            }
        }

    }
}
