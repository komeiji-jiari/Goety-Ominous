package com.qiuyue.goetyominous.common.items.sar;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.sar.ConfusionBoltSpell;
import com.qiuyue.goetyominous.common.magic.spells.sar.RunePrisonSpell;
import com.qiuyue.goetyominous.common.magic.spells.sar.SporeCloudSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SarItems {

    public static final DeferredRegister<Item> SAR_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> CREEPIE_SERVANT_SPAWN_EGG = SAR_ITEMS.register(
            "creepie_servant_spawn_egg",
            () -> new ServantSpawnEggItem(SarEntityRegistry.CREEPIE_SERVANT, 0x50FF50, 0x00AA00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GRIEFER_SERVANT_SPAWN_EGG = SAR_ITEMS.register(
            "griefer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(SarEntityRegistry.GRIEFER_SERVANT, 0x6B8E23, 0x2F4F4F, egg()));

    public static final RegistryObject<ServantSpawnEggItem> EXECUTIONER_SERVANT_SPAWN_EGG = SAR_ITEMS.register(
            "executioner_servant_spawn_egg",
            () -> new ServantSpawnEggItem(SarEntityRegistry.EXECUTIONER_SERVANT, 0x8B0000, 0x2F2F2F, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKELETON_VILLAGER_SERVANT_SPAWN_EGG = SAR_ITEMS.register(
            "skeleton_villager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(SarEntityRegistry.SKELETON_VILLAGER_SERVANT, 0xE0E0E0, 0x8B7355, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TRICKSTER_SERVANT_SPAWN_EGG = SAR_ITEMS.register(
            "trickster_servant_spawn_egg",
            () -> new ServantSpawnEggItem(SarEntityRegistry.TRICKSTER_SERVANT, 0x9B59B6, 0x6C3483, egg()));

    public static final RegistryObject<MagicFocus> SPORE_CLOUD_FOCUS = SAR_ITEMS.register(
            "spore_cloud_focus",
            () -> new MagicFocus(new SporeCloudSpell()));

    public static final RegistryObject<MagicFocus> RUNE_PRISON_FOCUS = SAR_ITEMS.register(
            "rune_prison_focus",
            () -> new MagicFocus(new RunePrisonSpell()));

    public static final RegistryObject<MagicFocus> CONFUSION_BOLT_FOCUS = SAR_ITEMS.register(
            "confusion_bolt_focus",
            () -> new MagicFocus(new ConfusionBoltSpell()));



    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        SAR_ITEMS.register(modEventBus);
    }
}
