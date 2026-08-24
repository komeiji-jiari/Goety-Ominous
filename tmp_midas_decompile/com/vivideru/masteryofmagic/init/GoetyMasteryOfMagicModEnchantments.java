/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import com.vivideru.masteryofmagic.enchantment.HomingEnchantment;
import com.vivideru.masteryofmagic.enchantment.LightenedEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENCHANTMENTS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<Enchantment> DARKENED = REGISTRY.register("darkened", () -> new DarkenedEnchantment());
    public static final RegistryObject<Enchantment> LIGHTENED = REGISTRY.register("lightened", () -> new LightenedEnchantment());
    public static final RegistryObject<Enchantment> HOMING = REGISTRY.register("homing", () -> new HomingEnchantment());
}

