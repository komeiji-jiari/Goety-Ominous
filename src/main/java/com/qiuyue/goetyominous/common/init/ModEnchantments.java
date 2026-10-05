package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.enchantment.BreachEnchantment;
import com.qiuyue.goetyominous.common.enchantment.DensityEnchantment;
import com.qiuyue.goetyominous.common.enchantment.WindBurstEnchantment;
import com.qiuyue.goetyominous.common.items.MaceItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GoetyOminous.MOD_ID);

    public static final EnchantmentCategory MACE =
            EnchantmentCategory.create("MACE", item -> item instanceof MaceItem);

    public static final RegistryObject<Enchantment> DENSITY =
            ENCHANTMENTS.register("density", DensityEnchantment::new);
    public static final RegistryObject<Enchantment> BREACH =
            ENCHANTMENTS.register("breach", BreachEnchantment::new);
    public static final RegistryObject<Enchantment> WIND_BURST =
            ENCHANTMENTS.register("wind_burst", WindBurstEnchantment::new);

    public static void init() {
        ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
