package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServantDismountEvents {

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (!player.isCrouching() || !target.isVehicle() || !(target instanceof PlayerRideable)) {
            return;
        }
        Entity firstPassenger = target.getFirstPassenger();
        if (firstPassenger instanceof IServant servant && servant.getTrueOwner() == player) {
            if (event.getSide() != LogicalSide.CLIENT) {
                firstPassenger.stopRiding();
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
