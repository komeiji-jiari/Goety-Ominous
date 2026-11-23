package com.qiuyue.goetyominous.common.items.curios;

import com.Polarice3.Goety.common.items.curios.EternalCauldronItem;
import com.Polarice3.Goety.common.items.handler.EternalCauldronItemHandler;
import com.qiuyue.goetyominous.common.inventory.container.NetherEternalCauldronContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class NetherEternalCauldronItem extends EternalCauldronItem {

    @Override
    public void activate(Level level, Player player, ItemStack itemStack) {
        if (itemStack.is(this) && !level.isClientSide) {
            if (player.isShiftKeyDown()) {
                SimpleMenuProvider provider = new SimpleMenuProvider(
                        (id, inventory, playerIn) -> new NetherEternalCauldronContainer(id, inventory,
                                EternalCauldronItemHandler.get(itemStack), itemStack),
                        this.getName(itemStack));
                NetworkHooks.openScreen((ServerPlayer) player, provider, buffer -> {
                });
            } else {
                super.activate(level, player, itemStack);
            }
        }
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, player) -> new NetherEternalCauldronContainer(id, inventory,
                            EternalCauldronItemHandler.get(itemstack), itemstack),
                    this.getName(itemstack));
            NetworkHooks.openScreen((ServerPlayer) playerIn, provider, buffer -> {
            });
        }
        return InteractionResultHolder.success(itemstack);
    }
}
