package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.ally.Hellhound;
import com.Polarice3.Goety.common.items.ModItems;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class CerberusFusionEvents {

    @SubscribeEvent
    public static void onHellhoundOffering(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }
        if (!(event.getTarget() instanceof Hellhound hellhound)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SHADOW_ESSENCE.get())) {
            return;
        }
        Player player = event.getEntity();
        if (hellhound.getTrueOwner() != player) {
            return;
        }
        if (WolfTotemHooks.tryFuseCerberus(player, hellhound, event.getHand())) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
