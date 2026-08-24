/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.potion.TimeFreezeMobEffect;
import com.vivideru.masteryofmagic.potion.VulnerableMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.MOB_EFFECTS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<MobEffect> VULNERABLE = REGISTRY.register("vulnerable", () -> new VulnerableMobEffect());
    public static final RegistryObject<MobEffect> TIME_FREEZE_EFFECT = REGISTRY.register("time_freeze_effect", () -> new TimeFreezeMobEffect());
}

