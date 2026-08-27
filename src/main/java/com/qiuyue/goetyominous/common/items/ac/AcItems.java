package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AcItems {

    public static final DeferredRegister<Item> AC_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> GROTTOCERATOPS_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("grottoceratops_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.GROTTOCERATOPS_SERVANT, 0xAC3B03, 0x3B1C14, egg()));

    public static final RegistryObject<BlockItem> GROTTOCERATOPS_SERVANT_EGG =
            AC_ITEMS.register("grottoceratops_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.GROTTOCERATOPS_SERVANT_EGG.get(), egg()));

    public static final RegistryObject<ServantSpawnEggItem> TREMORSAURUS_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("tremorsaurus_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.TREMORSAURUS_SERVANT, 0xBA8604, 0x3F6E14, egg()));

    public static final RegistryObject<BlockItem> TREMORSAURUS_SERVANT_EGG =
            AC_ITEMS.register("tremorsaurus_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.TREMORSAURUS_SERVANT_EGG.get(), egg()));

    public static final RegistryObject<ServantSpawnEggItem> VALLUMRAPTOR_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("vallumraptor_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.VALLUMRAPTOR_SERVANT, 0x22389A, 0xEEE5AB, egg()));

    public static final RegistryObject<BlockItem> VALLUMRAPTOR_SERVANT_EGG =
            AC_ITEMS.register("vallumraptor_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.VALLUMRAPTOR_SERVANT_EGG.get(), egg()));

    public static final RegistryObject<ServantSpawnEggItem> NUCLEEPER_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("nucleeper_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.NUCLEEPER_SERVANT, 0x95A1A5, 0xFF00, egg()));

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static final RegistryObject<ServantSpawnEggItem> BRAINIAC_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("brainiac_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.BRAINIAC_SERVANT, 0x3A4A2E, 0xE35FA0, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MINE_GUARDIAN_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("mine_guardian_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.MINE_GUARDIAN_SERVANT, 0x243B52, 0x8AC3D6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HULLBREAKER_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("hullbreaker_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.HULLBREAKER_SERVANT, 0x182538, 0x76FFFD, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DEEP_ONE_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("deep_one_servant_spawn_egg",
                    // 贴图采样 deep_one.png:藏青身体(#081828)+ 青绿腮/背点缀(#085840)
                    () -> new ServantSpawnEggItem(AcEntityRegistry.DEEP_ONE_SERVANT, 0x081828, 0x085840, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DEEP_ONE_KNIGHT_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("deep_one_knight_servant_spawn_egg",
                    // 贴图采样 deep_one_knight.png:酒红外套(#402838)+ 奶油脸/牙(#d0c8c0)
                    () -> new ServantSpawnEggItem(AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT, 0x402838, 0xd0c8c0, egg()));

    public static final RegistryObject<Item> RAYCAT_AMULET =
            AC_ITEMS.register("raycat_amulet", () -> new RaycatAmuletItem());

    public static void register(IEventBus modEventBus) {
        AC_ITEMS.register(modEventBus);
    }
}
