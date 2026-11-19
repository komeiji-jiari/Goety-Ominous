package com.qiuyue.goetyominous.common.worldgen.windshrine;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class WindShrineStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, GoetyOminous.MOD_ID);
    public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, GoetyOminous.MOD_ID);
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, GoetyOminous.MOD_ID);

    public static final RegistryObject<StructureType<WindShrineStructure>> WIND_SHRINE =
            STRUCTURE_TYPES.register("wind_shrine", () -> () -> WindShrineStructure.CODEC);
    public static final RegistryObject<StructureType<WindShrineExpansionStructure>> WIND_SHRINE_EXPANSION =
            STRUCTURE_TYPES.register("wind_shrine_expansion", () -> () -> WindShrineExpansionStructure.CODEC);
    public static final RegistryObject<StructurePlacementType<WindShrineExpansionPlacement>> WIND_SHRINE_EXPANSION_PLACEMENT =
            PLACEMENT_TYPES.register("wind_shrine_expansion", () -> () -> WindShrineExpansionPlacement.CODEC);
    public static final RegistryObject<StructureProcessorType<WindShrineFoundationProcessor>> WIND_SHRINE_FOUNDATION =
            PROCESSOR_TYPES.register("wind_shrine_foundation", () -> () -> WindShrineFoundationProcessor.CODEC);

    private WindShrineStructures() {
    }
}
