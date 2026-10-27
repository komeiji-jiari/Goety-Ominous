package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class TrialJigsaw {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, GoetyOminous.MOD_ID);

    public static final RegistryObject<StructureType<TrialJigsawStructure>> TRIAL_JIGSAW =
            STRUCTURE_TYPES.register("trial_jigsaw", () -> () -> TrialJigsawStructure.CODEC);

    private TrialJigsaw() {
    }
}
