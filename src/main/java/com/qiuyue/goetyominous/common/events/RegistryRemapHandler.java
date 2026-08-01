package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegistryRemapHandler {

    private static final String OLD_MOD_ID = "goetyominus";

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        if (event.getKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
            event.getMappings(ForgeRegistries.ITEMS.getRegistryKey(), OLD_MOD_ID).forEach(mapping -> {
                Item newItem = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(GoetyOminous.MOD_ID, mapping.getKey().getPath()));
                if (newItem != null) mapping.remap(newItem);
            });
        }

        if (event.getKey().equals(ForgeRegistries.BLOCKS.getRegistryKey())) {
            event.getMappings(ForgeRegistries.BLOCKS.getRegistryKey(), OLD_MOD_ID).forEach(mapping -> {
                Block newBlock = ForgeRegistries.BLOCKS.getValue(
                        new ResourceLocation(GoetyOminous.MOD_ID, mapping.getKey().getPath()));
                if (newBlock != null) mapping.remap(newBlock);
            });
        }

        if (event.getKey().equals(ForgeRegistries.ENTITY_TYPES.getRegistryKey())) {
            event.getMappings(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), OLD_MOD_ID).forEach(mapping -> {
                EntityType<?> newType = ForgeRegistries.ENTITY_TYPES.getValue(
                        new ResourceLocation(GoetyOminous.MOD_ID, mapping.getKey().getPath()));
                if (newType != null) mapping.remap(newType);
            });
        }
    }
}
