package com.qiuyue.someillagerservants.common.events;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.entities.projectile.WitchBombEntity;
import com.qiuyue.someillagerservants.common.items.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrewBagFixEvents {

    @SubscribeEvent
    public static void onBrewThrown(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof com.Polarice3.Goety.common.entities.projectiles.ThrownBrew thrown) {
            ItemStack item = thrown.getItem();
            if (item.is(ModItems.WITCH_BOMB.get()) && event.getLevel() instanceof ServerLevel serverLevel) {
                WitchBombEntity bomb = new WitchBombEntity(serverLevel,
                        thrown.getOwner() instanceof LivingEntity le ? le : null);
                bomb.setPos(thrown.getX(), thrown.getY(), thrown.getZ());
                bomb.setDeltaMovement(thrown.getDeltaMovement());
                bomb.setItem(item);
                serverLevel.addFreshEntity(bomb);
                thrown.discard();
                event.setCanceled(true);
            }
        }
    }
}
