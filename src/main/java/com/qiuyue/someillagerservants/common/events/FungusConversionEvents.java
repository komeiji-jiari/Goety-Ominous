package com.qiuyue.someillagerservants.common.events;

import com.google.common.collect.Maps;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FungusConversionEvents {

    private static final Map<BlockPos, Boolean> PENDING_IMMUNITY = Maps.newHashMap();

    @SubscribeEvent
    public static void onFungusThrowerJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof com.qiuyue.someillagerservants.common.entities.ally.mobs.FungusThrower fungus) {
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

            Boolean immune = PENDING_IMMUNITY.remove(fungus.blockPosition());
            if (immune != null && immune) {
                fungus.setImmune(true);
            }
        }
    }
}