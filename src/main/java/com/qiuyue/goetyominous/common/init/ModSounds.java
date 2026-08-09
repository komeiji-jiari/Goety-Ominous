package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GoetyOminous.MOD_ID);

    public static void init() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SoundEvent> WOLF_ARMOR_EQUIP = create("wolf_armor_equip");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_UNEQUIP = create("wolf_armor_unequip");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_DAMAGE = create("wolf_armor_damage");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_CRACK = create("wolf_armor_crack");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_BREAK = create("wolf_armor_break");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_REPAIR = create("wolf_armor_repair");

    public static final RegistryObject<SoundEvent> DISCIPLE_IDLE_1 = create("disciple_idle_1");
    public static final RegistryObject<SoundEvent> DISCIPLE_IDLE_2 = create("disciple_idle_2");
    public static final RegistryObject<SoundEvent> DISCIPLE_IDLE_3 = create("disciple_idle_3");
    public static final RegistryObject<SoundEvent> DISCIPLE_IDLE_4 = create("disciple_idle_4");
    public static final RegistryObject<SoundEvent> DISCIPLE_IDLE_5 = create("disciple_idle_5");

    public static final RegistryObject<SoundEvent> DISCIPLE_HURT_1 = create("disciple_hurt_1");
    public static final RegistryObject<SoundEvent> DISCIPLE_HURT_2 = create("disciple_hurt_2");
    public static final RegistryObject<SoundEvent> DISCIPLE_HURT_3 = create("disciple_hurt_3");

    public static final RegistryObject<SoundEvent> DISCIPLE_DEATH_1 = create("disciple_death_1");
    public static final RegistryObject<SoundEvent> DISCIPLE_DEATH_2 = create("disciple_death_2");
    public static final RegistryObject<SoundEvent> DISCIPLE_DEATH_3 = create("disciple_death_3");

    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_1 = create("fanatic_ambient_1");
    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_2 = create("fanatic_ambient_2");
    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_3 = create("fanatic_ambient_3");
    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_4 = create("fanatic_ambient_4");
    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_5 = create("fanatic_ambient_5");
    public static final RegistryObject<SoundEvent> FANATIC_AMBIENT_6 = create("fanatic_ambient_6");

    public static final RegistryObject<SoundEvent> FANATIC_HURT_1 = create("fanatic_hurt_1");
    public static final RegistryObject<SoundEvent> FANATIC_HURT_2 = create("fanatic_hurt_2");
    public static final RegistryObject<SoundEvent> FANATIC_HURT_3 = create("fanatic_hurt_3");
    public static final RegistryObject<SoundEvent> FANATIC_HURT_4 = create("fanatic_hurt_4");
    public static final RegistryObject<SoundEvent> FANATIC_HURT_5 = create("fanatic_hurt_5");

    public static final RegistryObject<SoundEvent> FANATIC_DEATH_1 = create("fanatic_death_1");
    public static final RegistryObject<SoundEvent> FANATIC_DEATH_2 = create("fanatic_death_2");
    public static final RegistryObject<SoundEvent> FANATIC_DEATH_3 = create("fanatic_death_3");
    public static final RegistryObject<SoundEvent> FANATIC_DEATH_4 = create("fanatic_death_4");

    public static final RegistryObject<SoundEvent> FANATIC_CELEBRATE_1 = create("fanatic_celebrate_1");
    public static final RegistryObject<SoundEvent> FANATIC_CELEBRATE_2 = create("fanatic_celebrate_2");
    public static final RegistryObject<SoundEvent> FANATIC_CELEBRATE_3 = create("fanatic_celebrate_3");
    public static final RegistryObject<SoundEvent> FANATIC_CELEBRATE_4 = create("fanatic_celebrate_4");

    public static final RegistryObject<SoundEvent> COG_CROSSBOW_SHOOT_1 = create("cog_crossbow_shoot_1");
    public static final RegistryObject<SoundEvent> COG_CROSSBOW_SHOOT_2 = create("cog_crossbow_shoot_2");
    public static final RegistryObject<SoundEvent> COG_CROSSBOW_SHOOT_3 = create("cog_crossbow_shoot_3");

    public static final RegistryObject<SoundEvent> PIGLIN_PRIDE_SHOOT_1 = create("piglin_pride_shoot_1");
    public static final RegistryObject<SoundEvent> PIGLIN_PRIDE_SHOOT_2 = create("piglin_pride_shoot_2");
    public static final RegistryObject<SoundEvent> PIGLIN_PRIDE_SHOOT_3 = create("piglin_pride_shoot_3");

    public static final RegistryObject<SoundEvent> BONE_CUDGEL_1 = create("bone_cudgel_1");
    public static final RegistryObject<SoundEvent> BONE_CUDGEL_2 = create("bone_cudgel_2");
    public static final RegistryObject<SoundEvent> BONE_CUDGEL_3 = create("bone_cudgel_3");

    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_IDLE_1 = create("fungus_thrower_idle_1");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_IDLE_2 = create("fungus_thrower_idle_2");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_IDLE_3 = create("fungus_thrower_idle_3");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_IDLE_4 = create("fungus_thrower_idle_4");

    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_HURT_1 = create("fungus_thrower_hurt_1");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_HURT_2 = create("fungus_thrower_hurt_2");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_HURT_3 = create("fungus_thrower_hurt_3");

    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_DEATH_1 = create("fungus_thrower_death_1");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_DEATH_2 = create("fungus_thrower_death_2");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_DEATH_3 = create("fungus_thrower_death_3");

    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_THROW_1 = create("fungus_thrower_throw_1");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_THROW_2 = create("fungus_thrower_throw_2");
    public static final RegistryObject<SoundEvent> FUNGUS_THROWER_THROW_3 = create("fungus_thrower_throw_3");

    public static final RegistryObject<SoundEvent> URBHADHACH_AMBIENT = create("urbhadhach_ambient");
    public static final RegistryObject<SoundEvent> URBHADHACH_HURT = create("urbhadhach_hurt");
    public static final RegistryObject<SoundEvent> URBHADHACH_ROAR = create("urbhadhach_roar");
    public static final RegistryObject<SoundEvent> URBHADHACH_STRONG_ROAR = create("urbhadhach_strong_roar");
    public static final RegistryObject<SoundEvent> URBHADHACH_ATTACK = create("urbhadhach_attack");
    public static final RegistryObject<SoundEvent> URBHADHACH_STEP = create("urbhadhach_step");
    public static final RegistryObject<SoundEvent> URBHADHACH_DEATH = create("urbhadhach_death");
    public static final RegistryObject<SoundEvent> URBHADHACH_CRY = create("urbhadhach_cry");

    public static final RegistryObject<SoundEvent> THUG_AMBIENT = create("thug_ambient");
    public static final RegistryObject<SoundEvent> THUG_HURT = create("thug_hurt");
    public static final RegistryObject<SoundEvent> THUG_DEATH = create("thug_death");
    public static final RegistryObject<SoundEvent> THUG_STEP = create("thug_step");
    public static final RegistryObject<SoundEvent> THUG_CELEBRATE = create("thug_celebrate");

    public static final RegistryObject<SoundEvent> ARCHGEOMANCER_MUSIC = create("archgeomancer");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, name)));
    }
}