package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.worldgen.LootInjectorProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModProcessorTypes {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, GoetyOminous.MOD_ID);

    public static final RegistryObject<StructureProcessorType<LootInjectorProcessor>> LOOT_INJECTOR =
            PROCESSOR_TYPES.register("loot_injector", () -> () -> LootInjectorProcessor.CODEC);

    public static void register(IEventBus modEventBus) {
        PROCESSOR_TYPES.register(modEventBus);
    }
}
