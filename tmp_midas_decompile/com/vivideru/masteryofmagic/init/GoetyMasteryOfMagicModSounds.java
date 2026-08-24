/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.SOUND_EVENTS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<SoundEvent> GHIACCIO_PUNCH_1 = REGISTRY.register("ghiaccio_punch_1", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ghiaccio_punch_1")));
    public static final RegistryObject<SoundEvent> GHIACCIO_PUNCH_2 = REGISTRY.register("ghiaccio_punch_2", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ghiaccio_punch_2")));
    public static final RegistryObject<SoundEvent> CLOCKTICKTOCK = REGISTRY.register("clockticktock", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "clockticktock")));
    public static final RegistryObject<SoundEvent> TIMESTOP = REGISTRY.register("timestop", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "timestop")));
    public static final RegistryObject<SoundEvent> TIMERESUME = REGISTRY.register("timeresume", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "timeresume")));
    public static final RegistryObject<SoundEvent> CLOCKTICKSINGLE = REGISTRY.register("clockticksingle", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "clockticksingle")));
    public static final RegistryObject<SoundEvent> THEME_OF_GHIACCIO = REGISTRY.register("theme_of_ghiaccio", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "theme_of_ghiaccio")));
    public static final RegistryObject<SoundEvent> ICE_MONARCH_DEATH = REGISTRY.register("ice_monarch_death", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ice_monarch_death")));
    public static final RegistryObject<SoundEvent> ICE_MONARCH_IDLE = REGISTRY.register("ice_monarch_idle", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ice_monarch_idle")));
    public static final RegistryObject<SoundEvent> ICE_MONARCH_STEP = REGISTRY.register("ice_monarch_step", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ice_monarch_step")));
    public static final RegistryObject<SoundEvent> ICE_MONARCH_HURT = REGISTRY.register("ice_monarch_hurt", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "ice_monarch_hurt")));
    public static final RegistryObject<SoundEvent> MIDAS_ROAR = null;
    public static final RegistryObject<SoundEvent> MIDAS_TRANSMUTE = null;
    public static final RegistryObject<SoundEvent> MIDAS_DAMAGE = null;
    public static final RegistryObject<SoundEvent> MIDAS_GRUNT = null;
    public static final RegistryObject<SoundEvent> MIDAS_PHILOSOPHER_SLASH_LOOP = null;
    public static final RegistryObject<SoundEvent> MIDAS_SLASH_VOCAL = null;
    public static final RegistryObject<SoundEvent> MAGIC_COUNTER_ACTIVATE = REGISTRY.register("magic_counter_activate", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "magic_counter_activate")));
    public static final RegistryObject<SoundEvent> MAGIC_COUNTER_PARRY = REGISTRY.register("magic_counter_parry", () -> SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "magic_counter_parry")));
}

