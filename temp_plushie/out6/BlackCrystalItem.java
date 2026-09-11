package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.client.render.block.ModISTERs;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class BlackCrystalItem extends BlockItemBase {
    public BlackCrystalItem() {
        super((Block)ModBlocks.BLACK_CRYSTAL.get());
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return stack.m_41613_() == 1 && (enchantment == ModEnchantments.SOUL_EATER.get() || enchantment == ModEnchantments.RADIUS.get());
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return itemStack.m_41793_() ? 1 : super.getMaxStackSize(itemStack);
    }

    public boolean m_8120_(ItemStack stack) {
        return stack.m_41613_() == 1;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ModISTERs.get();
            }
        });
    }
}
