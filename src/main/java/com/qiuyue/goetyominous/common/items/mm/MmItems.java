package com.qiuyue.goetyominous.common.items.mm;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.mm.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public class MmItems {

    public static final DeferredRegister<Item> MM_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_WITHER_SKELETON_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_wither_skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT, 0x2C3E50, 0x8E44AD, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_HOGLIN_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_hoglin_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_HOGLIN_SERVANT, 0x4F2A1D, 0xC8A082, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_SHULKER_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_shulker_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_SHULKER_SERVANT, 0x8E5BD6, 0x5E3A9E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_BLAZE_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_blaze_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_BLAZE_SERVANT, 0xF5A623, 0xC2571B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RODLING_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "rodling_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.RODLING_SERVANT, 0xFFB347, 0x8B5A2B, egg()));

    public static final RegistryObject<MagicFocus> WITHER_BREATH_FOCUS = MM_ITEMS.register(
            "wither_breath_focus",
            () -> new MagicFocus(new WitherBreathSpell()));

    public static final RegistryObject<MagicFocus> WITHER_SLASH_FOCUS = MM_ITEMS.register(
            "wither_slash_focus",
            () -> new MagicFocus(new WitherSlashSpell()));

    public static final RegistryObject<MagicFocus> HOG_CHARGE_FOCUS = MM_ITEMS.register(
            "hog_charge_focus",
            () -> new MagicFocus(new HogChargeSpell()));

    public static final RegistryObject<MagicFocus> SHULKER_SCATTER_FOCUS = MM_ITEMS.register(
            "shulker_scatter_focus",
            () -> new MagicFocus(new ShulkerScatterSpell()));

    public static final RegistryObject<MagicFocus> SHULKER_BULLET_FOCUS = MM_ITEMS.register(
            "shulker_bullet_focus",
            () -> new MagicFocus(new ShulkerBulletSpell()));

    public static final RegistryObject<MagicFocus> ROD_STRIKE_FOCUS = MM_ITEMS.register(
            "rod_strike_focus",
            () -> new MagicFocus(new RodStrikeSpell()));

    public static final RegistryObject<WitherScytheItem> WITHER_SCYTHE = MM_ITEMS.register(
            "wither_scythe", WitherScytheItem::new);

    public static final RegistryObject<Item> SHULKER_EMBRYO = MM_ITEMS.register(
            "shulker_embryo",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(64)));



    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        MM_ITEMS.register(modEventBus);
    }
}
