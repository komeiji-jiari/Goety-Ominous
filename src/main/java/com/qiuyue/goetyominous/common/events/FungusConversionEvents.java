package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.FungusThrower;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FungusConversionEvents {

    @SubscribeEvent
    public static void onFungusThrowerJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof FungusThrower fungus) {
            if (fungus.getPersistentData().getBoolean("sis_dropped")) return;
            fungus.getPersistentData().putBoolean("sis_dropped", true);

            ItemStack mainHand = fungus.getMainHandItem();
            if (!mainHand.isEmpty()) {
                fungus.spawnAtLocation(mainHand);
                fungus.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            ItemStack chest = fungus.getItemBySlot(EquipmentSlot.CHEST);
            if (!chest.isEmpty()) {
                fungus.spawnAtLocation(chest);
                fungus.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            }

            for (AbstractPiglinServant old : event.getLevel().getEntitiesOfClass(
                    AbstractPiglinServant.class,
                    fungus.getBoundingBox().inflate(3.0),
                    old -> old.isAlive()
                            && old.getTrueOwner() != null
                            && fungus.getTrueOwner() != null
                            && old.getTrueOwner().getUUID().equals(fungus.getTrueOwner().getUUID()))) {
                CompoundTag tag = new CompoundTag();
                old.addAdditionalSaveData(tag);
                if (tag.getBoolean("IsImmuneToZombification")) {
                    fungus.setImmune(true);
                }
                break;
            }
        }
    }
}