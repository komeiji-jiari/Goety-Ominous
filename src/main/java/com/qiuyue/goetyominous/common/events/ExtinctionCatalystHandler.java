package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.util.ExtinctionCatalystEntity;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExtinctionCatalystHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity) || itemEntity instanceof ExtinctionCatalystEntity
                || !itemEntity.getItem().is(AcItems.EXTINCTION_CATALYST.get())) {
            return;
        }
        event.setCanceled(true);
        ItemStack stack = itemEntity.getItem().copy();
        ExtinctionCatalystEntity catalyst = new ExtinctionCatalystEntity(serverLevel,
                itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack);
        catalyst.setDeltaMovement(itemEntity.getDeltaMovement());
        catalyst.setPickUpDelay(40);
        if (itemEntity.getOwner() != null) {
            catalyst.setThrower(itemEntity.getOwner().getUUID());
        }
        if (itemEntity.hasCustomName()) {
            catalyst.setCustomName(itemEntity.getCustomName());
        }
        serverLevel.addFreshEntity(catalyst);
    }
}
