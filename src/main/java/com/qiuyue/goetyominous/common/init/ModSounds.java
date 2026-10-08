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

    public static final RegistryObject<SoundEvent> BREEZE_IDLE_GROUND = create("breeze_idle_ground");
    public static final RegistryObject<SoundEvent> BREEZE_IDLE_AIR = create("breeze_idle_air");
    public static final RegistryObject<SoundEvent> BREEZE_CHARGE = create("breeze_charge");
    public static final RegistryObject<SoundEvent> BREEZE_DEATH = create("breeze_death");
    public static final RegistryObject<SoundEvent> BREEZE_DEFLECT = create("breeze_deflect");
    public static final RegistryObject<SoundEvent> BREEZE_HURT = create("breeze_hurt");
    public static final RegistryObject<SoundEvent> BREEZE_INHALE = create("breeze_inhale");
    public static final RegistryObject<SoundEvent> BREEZE_JUMP = create("breeze_jump");
    public static final RegistryObject<SoundEvent> BREEZE_LAND = create("breeze_land");
    public static final RegistryObject<SoundEvent> BREEZE_SHOOT = create("breeze_shoot");
    public static final RegistryObject<SoundEvent> BREEZE_SLIDE = create("breeze_slide");
    public static final RegistryObject<SoundEvent> BREEZE_WHIRL = create("breeze_whirl");

    public static final RegistryObject<SoundEvent> OMINOUS_BOTTLE_DISPOSE = create("ominous_bottle_dispose");
    public static final RegistryObject<SoundEvent> APPLY_EFFECT_BAD_OMEN = create("apply_effect_bad_omen");
    public static final RegistryObject<SoundEvent> APPLY_EFFECT_TRIAL_OMEN = create("apply_effect_trial_omen");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OMINOUS_ACTIVATE = create("trial_spawner_ominous_activate");

    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_BREAK = create("trial_spawner_break");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_STEP = create("trial_spawner_step");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_PLACE = create("trial_spawner_place");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_HIT = create("trial_spawner_hit");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_FALL = create("trial_spawner_fall");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_MOB = create("trial_spawner_spawn_mob");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM = create("trial_spawner_about_to_spawn_item");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_ITEM = create("trial_spawner_spawn_item");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_ITEM_BEGIN = create("trial_spawner_spawn_item_begin");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER = create("trial_spawner_detect_player");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT = create("trial_spawner_ambient");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_OMINOUS = create("trial_spawner_ambient_ominous");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OPEN_SHUTTER = create("trial_spawner_open_shutter");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_CLOSE_SHUTTER = create("trial_spawner_close_shutter");
    public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_EJECT_ITEM = create("trial_spawner_eject_item");

    public static final RegistryObject<SoundEvent> HEAVY_CORE_BREAK = create("heavy_core_break");
    public static final RegistryObject<SoundEvent> HEAVY_CORE_STEP = create("heavy_core_step");
    public static final RegistryObject<SoundEvent> HEAVY_CORE_PLACE = create("heavy_core_place");
    public static final RegistryObject<SoundEvent> HEAVY_CORE_HIT = create("heavy_core_hit");
    public static final RegistryObject<SoundEvent> HEAVY_CORE_FALL = create("heavy_core_fall");

    public static final RegistryObject<SoundEvent> MACE_SMASH_AIR = create("mace_smash_air");
    public static final RegistryObject<SoundEvent> MACE_SMASH_GROUND_HEAVY = create("mace_smash_ground_heavy");
    public static final RegistryObject<SoundEvent> MACE_SMASH_GROUND = create("mace_smash_ground");

    public static final RegistryObject<SoundEvent> BOGGED_AMBIENT = create("bogged_ambient");
    public static final RegistryObject<SoundEvent> BOGGED_HURT = create("bogged_hurt");
    public static final RegistryObject<SoundEvent> BOGGED_DEATH = create("bogged_death");
    public static final RegistryObject<SoundEvent> BOGGED_STEP = create("bogged_step");

    public static final RegistryObject<SoundEvent> WOLF_ARMOR_EQUIP = create("wolf_armor_equip");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_UNEQUIP = create("wolf_armor_unequip");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_DAMAGE = create("wolf_armor_damage");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_CRACK = create("wolf_armor_crack");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_BREAK = create("wolf_armor_break");
    public static final RegistryObject<SoundEvent> WOLF_ARMOR_REPAIR = create("wolf_armor_repair");

    public static final RegistryObject<SoundEvent> WIND_CHARGE_THROW = create("wind_charge_throw");
    public static final RegistryObject<SoundEvent> WIND_CHARGE_BURST = create("wind_charge_burst");
    public static final RegistryObject<SoundEvent> BREEZE_WIND_CHARGE_BURST = create("breeze_wind_charge_burst");

    public static final RegistryObject<SoundEvent> DREDEN_IDLE = create("dreden_idle");
    public static final RegistryObject<SoundEvent> DREDEN_HURT = create("dreden_hurt");
    public static final RegistryObject<SoundEvent> DREDEN_DEATH = create("dreden_death");
    public static final RegistryObject<SoundEvent> DREDEN_FLY = create("dreden_fly");
    public static final RegistryObject<SoundEvent> DREDEN_SHOOT = create("dreden_shoot");

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

    public static final RegistryObject<SoundEvent> MUSIC_DISC_PRECIPICE = create("music_disc_precipice");

    public static final RegistryObject<SoundEvent> MUSIC_DISC_CREATOR = create("music_disc_creator");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_CREATOR_MUSIC_BOX = create("music_disc_creator_music_box");

    public static final RegistryObject<SoundEvent> VAULT_ACTIVATE = create("vault_activate");
    public static final RegistryObject<SoundEvent> VAULT_AMBIENT = create("vault_ambient");
    public static final RegistryObject<SoundEvent> VAULT_BREAK = create("vault_break");
    public static final RegistryObject<SoundEvent> VAULT_CLOSE_SHUTTER = create("vault_close_shutter");
    public static final RegistryObject<SoundEvent> VAULT_DEACTIVATE = create("vault_deactivate");
    public static final RegistryObject<SoundEvent> VAULT_EJECT_ITEM = create("vault_eject_item");
    public static final RegistryObject<SoundEvent> VAULT_FALL = create("vault_fall");
    public static final RegistryObject<SoundEvent> VAULT_HIT = create("vault_hit");
    public static final RegistryObject<SoundEvent> VAULT_INSERT_ITEM = create("vault_insert_item");
    public static final RegistryObject<SoundEvent> VAULT_INSERT_ITEM_FAIL = create("vault_insert_item_fail");
    public static final RegistryObject<SoundEvent> VAULT_OPEN_SHUTTER = create("vault_open_shutter");
    public static final RegistryObject<SoundEvent> VAULT_PLACE = create("vault_place");
    public static final RegistryObject<SoundEvent> VAULT_REJECT_REWARDED_PLAYER = create("vault_reject_rewarded_player");
    public static final RegistryObject<SoundEvent> VAULT_STEP = create("vault_step");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GoetyOminous.MOD_ID, name)));
    }
}