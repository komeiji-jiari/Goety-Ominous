package com.qiuyue.goetyominous.compat.ac;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.google.common.base.Suppliers;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Supplier;

public class GoetyOminousPatchouliIntegration {

    public static final Supplier<IMultiblock> NUCLEEPER = Suppliers.memoize(() -> {
        IStateMatcher sirenLight = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.SIREN_LIGHT.get(),
                state -> state.is(ACBlockRegistry.SIREN_LIGHT.get()));
        IStateMatcher component = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get(),
                state -> state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()));
        IStateMatcher uranium = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.BLOCK_OF_URANIUM.get(),
                state -> state.is(ACBlockRegistry.BLOCK_OF_URANIUM.get()));
        IStateMatcher scrap = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.SCRAP_METAL.get(),
                state -> state.is(ACBlockRegistry.SCRAP_METAL.get()));
        return PatchouliAPI.get().makeMultiblock(
                new String[][]{{"L"}, {"F"}, {"0"}, {"F"}, {"M"}},
                new Object[]{'L', sirenLight, 'F', component, '0', uranium, 'M', scrap});
    });

    public static final Supplier<IMultiblock> NUCLEAR_GUARDIAN = Suppliers.memoize(() -> {
        IStateMatcher plate = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.SCRAP_METAL_PLATE.get(),
                state -> state.is(ACBlockRegistry.SCRAP_METAL_PLATE.get()));
        IStateMatcher scrap = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.SCRAP_METAL.get(),
                state -> state.is(ACBlockRegistry.SCRAP_METAL.get()));
        IStateMatcher component = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get(),
                state -> state.is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()));
        IStateMatcher drum = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.WASTE_DRUM.get(),
                state -> state.is(ACBlockRegistry.WASTE_DRUM.get()));
        IStateMatcher uranium = PatchouliAPI.get().predicateMatcher(ACBlockRegistry.BLOCK_OF_URANIUM.get(),
                state -> state.is(ACBlockRegistry.BLOCK_OF_URANIUM.get()));
        IStateMatcher dispenser = PatchouliAPI.get().predicateMatcher(Blocks.DISPENSER,
                state -> state.is(Blocks.DISPENSER));
        return PatchouliAPI.get().makeMultiblock(
                new String[][]{
                        {"OXO", "XYX", "OXO"},
                        {"OZO", "Z0Z", "OPO"},
                        {"OXO", "XYX", "OXO"}},
                new Object[]{'O', plate, 'X', scrap, 'Y', component, 'Z', drum, '0', uranium, 'P', dispenser});
    });

    public static void setup(FMLCommonSetupEvent event) {
        PatchouliAPI.get().registerMultiblock(
                new ResourceLocation(GoetyOminous.MOD_ID, "nucleeper"), NUCLEEPER.get());
        PatchouliAPI.get().registerMultiblock(
                new ResourceLocation(GoetyOminous.MOD_ID, "nuclear_guardian"), NUCLEAR_GUARDIAN.get());
    }
}
