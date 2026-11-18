package com.qiuyue.goetyominous.common.worldgen.crypt;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class CryptStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, GoetyOminous.MOD_ID);
    public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, GoetyOminous.MOD_ID);

    public static final RegistryObject<StructureType<CryptWingStructure>> CRYPT_WING =
            STRUCTURE_TYPES.register("crypt_wing", () -> () -> CryptWingStructure.CODEC);
    public static final RegistryObject<StructurePlacementType<CryptWingPlacement>> CRYPT_WING_PLACEMENT =
            PLACEMENT_TYPES.register("crypt_wing", () -> () -> CryptWingPlacement.CODEC);

    private CryptStructures() {
    }
}
