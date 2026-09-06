package com.qiuyue.goetyominous.compat.ias;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.revive.MysteriousContract;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IasItems {

    public static final DeferredRegister<Item> IAS_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<MysteriousContract> MYSTERIOUS_CONTRACT = IAS_ITEMS.register(
            "mysterious_contract",
            () -> new MysteriousContract());

    public static final RegistryObject<ServantSpawnEggItem> TWITTOLLAGER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "twittollager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.TWITTOLLAGER_SERVANT, 0xD3D3D3, 0x5C121B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ABSORBER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "absorber_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.ABSORBER_SERVANT, 0x8B8000, 0x4A235A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MAGISPELLER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "magispeller_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.MAGISPELLER_SERVANT, 0x2E0854, 0xE6E6FA, egg()));

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        IAS_ITEMS.register(modEventBus);
    }
}
