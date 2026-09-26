package com.qiuyue.goetyominous.common.items.of;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.ac.ExtinctionBreathSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.DicerLaserSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.DicerSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.SkyvernSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.TerrorSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.TremblerSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.VoltBoltSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.VoltSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OfItems {

    public static final DeferredRegister<Item> OF_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> RAMBLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "rambler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.RAMBLER_SERVANT, 0xEDEDCF, 0x685944, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DICER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "dicer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.DICER_SERVANT, 0x1C0D1C, 0x3850F9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VOLT_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "volt_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.VOLT_SERVANT, 0x2C1538, 0x00BFFF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TREMBLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "trembler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.TREMBLER_SERVANT, 0x465641, 0x0D0E0D, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TERROR_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "terror_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.TERROR_SERVANT, 0x074230, 0xFF0000, egg()));


    public static final RegistryObject<ServantSpawnEggItem> FIRE_SLIME_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "fire_slime_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.FIRE_SLIME_SERVANT, 0xFB921B, 0xDB3709, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUZZLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "guzzler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.GUZZLER_SERVANT, 0x160E2C, 0x8956C2, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKYVERN_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "skyvern_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.SKYVERN_SERVANT, 0xF0E2E7, 0x124077, egg()));

    public static final RegistryObject<Item> VOLT_FOCUS = OF_ITEMS.register("volt_focus",
            () -> new MagicFocus(new VoltSpell()));

    public static final RegistryObject<Item> VOLT_BOLT_FOCUS = OF_ITEMS.register("volt_bolt_focus",
            () -> new MagicFocus(new VoltBoltSpell()));

    public static final RegistryObject<Item> DICER_LASER_FOCUS = OF_ITEMS.register("dicer_laser_focus",
            () -> new MagicFocus(new DicerLaserSpell()));

    public static final RegistryObject<Item> DICER_FOCUS = OF_ITEMS.register("dicer_focus",
            () -> new MagicFocus(new DicerSpell()));

    public static final RegistryObject<Item> TERROR_FOCUS = OF_ITEMS.register("terror_focus",
            () -> new MagicFocus(new TerrorSpell()));

    public static final RegistryObject<Item> SKYVERN_FOCUS = OF_ITEMS.register("skyvern_focus",
            () -> new MagicFocus(new SkyvernSpell()));

    public static final RegistryObject<Item> TREMBLER_FOCUS = OF_ITEMS.register("trembler_focus",
            () -> new MagicFocus(new TremblerSpell()));



    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        OF_ITEMS.register(modEventBus);
    }
}
