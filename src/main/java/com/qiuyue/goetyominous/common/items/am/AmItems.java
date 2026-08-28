package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.am.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AmItems {

    public static final DeferredRegister<Item> AM_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> CENTIPEDE_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("servant_centipede_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.SERVANT_CENTIPEDE_HEAD, 13064994, 3810323, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MURMUR_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("murmur_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.MURMUR_SERVANT, 0x445566, 0x8899AA, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRIMSON_MOSQUITO_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("crimson_mosquito_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.CRIMSON_MOSQUITO_SERVANT, 0x53403F, 0xC11A1A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WARPED_MOSCO_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("warped_mosco_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.WARPED_MOSCO_SERVANT, 0x322F58, 0x5B5EF1, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FARSEER_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("farseer_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.FARSEER_SERVANT, 0x33374F, 0x91FF59, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TUSKLIN_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("tusklin_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.TUSKLIN_SERVANT, 0x5A3B24, 0xC1A15C, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FROSTSTALKER_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("froststalker_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.FROSTSTALKER_SERVANT, 0x788AC1, 0xA1C3FF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ROCKY_ROLLER_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("rocky_roller_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.ROCKY_ROLLER_SERVANT, 0x8F8F8F, 0xC7A86B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_CROCODILE_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("zombie_crocodile_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT, 0x4E5B31, 0xD6C77E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BUNFUNGUS_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("bunfungus_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.BUNFUNGUS_SERVANT, 0x6F6D91, 0xC92B29, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ILLAGER_ELEPHANT_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("illager_elephant_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT, 0x3E2E2F, 0xEDE5D1, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKELEWAG_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("skelewag_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.SKELEWAG_SERVANT, 0xD4D4AC, 0x9C9C74, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WITHER_SKELEWAG_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("wither_skelewag_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.WITHER_SKELEWAG_SERVANT, 0x262626, 0x6E1414, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRAY_SKELEWAG_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("stray_skelewag_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.STRAY_SKELEWAG_SERVANT, 0xC8D8D8, 0x6E8FA0, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DROPBEAR_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("dropbear_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.DROPBEAR_SERVANT, 0x701F30, 0xBD4B4B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUSTER_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("guster_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.GUSTER_SERVANT, 0xF3C389, 0xC66127, egg()));

    public static final RegistryObject<WarpedSteroidsItem> WARPED_STEROIDS =
            AM_ITEMS.register("warped_steroids", WarpedSteroidsItem::new);

    public static final RegistryObject<MagicFocus> MURMUR_FOCUS = AM_ITEMS.register(
            "murmur_focus",
            () -> new MagicFocus(new MurmurSpell()));

    public static final RegistryObject<MagicFocus> CRIMSON_FOCUS = AM_ITEMS.register(
            "crimson_focus",
            () -> new MagicFocus(new CrimsonSpell()));

    public static final RegistryObject<MagicFocus> BLOODSPRAY_FOCUS = AM_ITEMS.register(
            "bloodspray_focus",
            () -> new MagicFocus(new BloodSpraySpell()));

    public static final RegistryObject<MagicFocus> FARSEER_FOCUS = AM_ITEMS.register(
            "farseer_focus",
            () -> new MagicFocus(new FarseerSpell()));

    public static final RegistryObject<MagicFocus> FROSTSTALKER_FOCUS = AM_ITEMS.register(
            "froststalker_focus",
            () -> new MagicFocus(new FrostStalkerSpell()));

    public static final RegistryObject<MagicFocus> ROLLER_FOCUS = AM_ITEMS.register(
            "roller_focus",
            () -> new MagicFocus(new RollerSpell()));

    public static final RegistryObject<MagicFocus> SKELEWAG_FOCUS = AM_ITEMS.register(
            "skelewag_focus",
            () -> new MagicFocus(new SkelewagSpell()));

    public static final RegistryObject<MagicFocus> GUSTER_FOCUS = AM_ITEMS.register(
            "guster_focus",
            () -> new MagicFocus(new GusterSpell()));

    public static final RegistryObject<MagicFocus> DROPBEAR_FOCUS = AM_ITEMS.register(
            "dropbear_focus",
            () -> new MagicFocus(new DropBearSpell()));

    public static final RegistryObject<MagicFocus> SAND_FOCUS = AM_ITEMS.register(
            "sand_focus",
            () -> new MagicFocus(new SandSpell()));

    public static final RegistryObject<MagicFocus> VOIDSHOT_FOCUS = AM_ITEMS.register(
            "voidshot_focus",
            () -> new MagicFocus(new VoidShotSpell()));

    public static final RegistryObject<MagicFocus> CENTIPEDE_FOCUS = AM_ITEMS.register(
            "centipede_focus",
            () -> new MagicFocus(new CentipedeSpell()));


    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        AM_ITEMS.register(modEventBus);
    }
}
