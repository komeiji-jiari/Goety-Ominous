package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.util.ExtinctionCataclystEntity;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExtinctionCataclystHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity) || itemEntity instanceof ExtinctionCataclystEntity
                || !itemEntity.getItem().is(AcItems.EXTINCTION_CATACLYST.get())) {
            return;
        }
        event.setCanceled(true);
        ItemStack stack = itemEntity.getItem().copy();
        ExtinctionCataclystEntity cataclyst = new ExtinctionCataclystEntity(serverLevel,
                itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack);
        cataclyst.setDeltaMovement(itemEntity.getDeltaMovement());
        cataclyst.setPickUpDelay(40);
        if (itemEntity.getOwner() != null) {
            cataclyst.setThrower(itemEntity.getOwner().getUUID());
        }
        if (itemEntity.hasCustomName()) {
            cataclyst.setCustomName(itemEntity.getCustomName());
        }
        serverLevel.addFreshEntity(cataclyst);
    }
}
