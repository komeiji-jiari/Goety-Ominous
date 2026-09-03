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

    public static final RegistryObject<ServantSpawnEggItem> TREMORZILLA_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("tremorzilla_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.TREMORZILLA_SERVANT, 0x574D2F, 0x8CFF08, egg()));

    public static final RegistryObject<BlockItem> TREMORZILLA_SERVANT_EGG =
            AC_ITEMS.register("tremorzilla_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.TREMORZILLA_SERVANT_EGG.get(), egg()));

    public static final RegistryObject<ServantSpawnEggItem> NUCLEEPER_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("nucleeper_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.NUCLEEPER_SERVANT, 0x95A1A5, 0xFF00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BRAINIAC_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("brainiac_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.BRAINIAC_SERVANT, 0x3A4A2E, 0xE35FA0, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CANIAC_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("caniac_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.CANIAC_SERVANT, 0xF9F0FF, 0xFF3F56, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GAMMAROACH_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("gammaroach_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.GAMMAROACH_SERVANT, 0xB8C64F, 0x77D60E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CORRODENT_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("corrodent_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.CORRODENT_SERVANT, 0x7A8450, 0x39402A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUMMY_BEAR_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("gummy_bear_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.GUMMY_BEAR_SERVANT, 0xFF463F, 0xFDA09E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CARAMEL_CUBE_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("caramel_cube_servant_spawn_egg",
                    // 焦糖色主体 + 奶油色糖纸(AC CaramelCube 无原版蛋,取自贴图主色)。
                    () -> new ServantSpawnEggItem(AcEntityRegistry.CARAMEL_CUBE_SERVANT, 0xDDAA4E, 0xF6E3C3, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUMBEEPER_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("gumbeeper_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.GUMBEEPER_SERVANT, 0xFF2B44, 0xE7BAFF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MINE_GUARDIAN_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("mine_guardian_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.MINE_GUARDIAN_SERVANT, 0x243B52, 0x8AC3D6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HULLBREAKER_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("hullbreaker_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AcEntityRegistry.HULLBREAKER_SERVANT, 0x182538, 0x76FFFD, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DEEP_ONE_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("deep_one_servant_spawn_egg",
                    
                    () -> new ServantSpawnEggItem(AcEntityRegistry.DEEP_ONE_SERVANT, 0x081828, 0x085840, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DEEP_ONE_KNIGHT_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("deep_one_knight_servant_spawn_egg",

                    () -> new ServantSpawnEggItem(AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT, 0x402838, 0xd0c8c0, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DEEP_ONE_MAGE_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("deep_one_mage_servant_spawn_egg",

                    () -> new ServantSpawnEggItem(AcEntityRegistry.DEEP_ONE_MAGE_SERVANT, 0x96DEF6, 0xD1FF00, egg()));

    public static final RegistryObject<Item> RAYCAT_AMULET =
            AC_ITEMS.register("raycat_amulet", () -> new RaycatAmuletItem());

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        AC_ITEMS.register(modEventBus);
    }
}
