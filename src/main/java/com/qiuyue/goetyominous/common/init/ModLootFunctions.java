package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.EnchantWithLevelsFunction;
import com.qiuyue.goetyominous.utils.SetOminousBottleAmplifierFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModLootFunctions {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, GoetyOminous.MOD_ID);

    public static final RegistryObject<LootItemFunctionType> SET_OMINOUS_BOTTLE_AMPLIFIER =
            LOOT_FUNCTIONS.register("set_ominous_bottle_amplifier",
                    () -> new LootItemFunctionType(new SetOminousBottleAmplifierFunction.Serializer()));

    public static final RegistryObject<LootItemFunctionType> ENCHANT_WITH_LEVELS =
            LOOT_FUNCTIONS.register("enchant_with_levels",
                    () -> new LootItemFunctionType(new EnchantWithLevelsFunction.Serializer()));

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTIONS.register(modEventBus);
    }
}
