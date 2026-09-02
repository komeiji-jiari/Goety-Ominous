package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.block.BlockItemBase;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.item.BoneCudgelRenderer;
import com.qiuyue.goetyominous.common.init.ModBlocks;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.curios.*;
import com.qiuyue.goetyominous.common.items.revive.BrokenStormCrown;
import com.qiuyue.goetyominous.common.items.revive.StormSoulJar;
import com.qiuyue.goetyominous.common.items.revive.SunkenSoulJar;
import com.qiuyue.goetyominous.common.items.revive.ThunderHorn;
import com.qiuyue.goetyominous.common.research.ResearchList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.qiuyue.goetyominous.config.WeaponConfig.FelStaffDamage;

/**
 * 模组物品注册类
 * 负责注册本模组的所有物品到 Forge 注册表
 * 包括刷怪蛋等特殊物品
 */
public class ModItems {
    /**
     * 物品延迟注册表
     * 使用 ForgeRegistries.ITEMS 指定注册表类型为物品注册表
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            GoetyOminous.MOD_ID);

    /**
     * 初始化方法
     * 将物品注册表注册到模组事件总线
     * 必须在模组构造函数中调用此方法以完成注册
     */
    public static void init() {
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<DarkStaff> FEL_STAFF = ITEMS.register("fel_staff",
            () -> new DarkStaff(FelStaffDamage.get(), GoetyOminous.FEL));

    public static final RegistryObject<Item> COLD_HEART = ITEMS.register("cold_heart",
            () -> new ColdHeartItem());

    public static final RegistryObject<Item> ACID_FUNGUS = ITEMS.register("acid_fungus",
            () -> new AcidFungusItem());

    public static final RegistryObject<Item> BURNING_POTION = ITEMS.register("burning_potion",
            () -> new BurningPotionItem());

    public static final RegistryObject<Item> WITCH_BOMB = ITEMS.register("witch_bomb",
            () -> new WitchBombItem());

    public static final RegistryObject<CursedMetalWolfArmorItem> CURSED_METAL_WOLF_ARMOR = ITEMS.register(
            "cursed_wolf_armor",
            () -> new CursedMetalWolfArmorItem(new Item.Properties()));

    public static final RegistryObject<DarkWolfArmorItem> DARK_WOLF_ARMOR = ITEMS.register(
            "dark_wolf_armor",
            () -> new DarkWolfArmorItem(new Item.Properties()));

    public static final RegistryObject<CursedWargArmorItem> CURSED_WARG_ARMOR = ITEMS.register(
            "warg_cursed_armor", () -> new CursedWargArmorItem(new Item.Properties()));

    public static final RegistryObject<DarkWargArmorItem> DARK_WARG_ARMOR = ITEMS.register(
            "warg_dark_armor", () -> new DarkWargArmorItem(new Item.Properties()));

    public static final RegistryObject<CursedBlackBeastArmorItem> CURSED_BLACK_BEAST_ARMOR = ITEMS.register(
            "black_beast_cursed_armor", () -> new CursedBlackBeastArmorItem(new Item.Properties()));

    public static final RegistryObject<DarkBlackBeastArmorItem> DARK_BLACK_BEAST_ARMOR = ITEMS.register(
            "black_beast_dark_armor", () -> new DarkBlackBeastArmorItem(new Item.Properties()));

    public static final RegistryObject<PitchforkItem> PITCHFORK = ITEMS.register("pitchfork",
            () -> new PitchforkItem());

    public static final RegistryObject<Item> BONE_CUDGEL = ITEMS.register("bone_cudgel",
            () -> new BoneCudgelItem() {
                @Override
                public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
                    consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            Minecraft mc = Minecraft.getInstance();
                            return new BoneCudgelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
                        }
                    });
                }
            });

    public static final RegistryObject<Item> FIREBRAND = ITEMS.register("firebrand",
            () -> new FirebrandItem());

    public static final RegistryObject<WitchBowItem> WITCH_BOW = ITEMS.register("witch_bow",
            () -> new WitchBowItem());

    public static final RegistryObject<Item> COG_CROSSBOW = ITEMS.register("cog_crossbow",
            () -> new CogCrossbowItem());

    public static final RegistryObject<Item> PIGLIN_PRIDE = ITEMS.register("piglin_pride",
            () -> new PiglinPrideItem());

    public static final RegistryObject<Item> BASTION_SCROLL = ITEMS.register("bastion_scroll",
            () -> new ScrollItem(ResearchList.BASTION));

    public static final RegistryObject<Item> SUNKEN_SOUL_JAR = ITEMS.register("sunken_soul_jar",
            () -> new SunkenSoulJar());

    public static final RegistryObject<Item> STORM_SOUL_JAR = ITEMS.register("storm_soul_jar",
            () -> new StormSoulJar());

    public static final RegistryObject<Item> THUNDER_HORN = ITEMS.register("thunder_horn",
            () -> new ThunderHorn());

    public static final RegistryObject<Item> BROKEN_STORM_CROWN = ITEMS.register("broken_storm_crown",
            () -> new BrokenStormCrown());

    public static final RegistryObject<Item> DARK_ANKH = ITEMS.register("dark_ankh",
            () -> new DarkAnkh());

    public static final RegistryObject<Item> SCREAMING_SKULL_JAR = ITEMS.register("screaming_skull_jar",
            () -> new ScreamingSkullJar());

    public static final RegistryObject<CroneRobeItem> CRONE_ROBE = ITEMS.register("crone_robe",
            () -> new CroneRobeItem());

    public static final RegistryObject<CroneRobeItem> CRONE_ROBE_ALT = ITEMS.register("crone_robe_alt",
            () -> new CroneRobeItem());

    public static final RegistryObject<Item> FUNGUS_PACK = ITEMS.register("fungus_pack",
            () -> new FungusPackItem());

    public static final RegistryObject<Item> RAGGED_FUNGUS_PACK = ITEMS.register("ragged_fungus_pack",
            () -> new RaggedFungusPackItem());

    public static final RegistryObject<Item> NETHER_WART_POTION = ITEMS.register("nether_wart_potion",
            () -> new NetherWartPotion());

    public static final RegistryObject<Item> HAUNT_FOCUS = ITEMS.register("haunt_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.HauntSpell()));

    public static final RegistryObject<Item> BROOD_FOCUS = ITEMS.register("brood_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.BroodSpell()));

    public static final RegistryObject<Item> SPIDER_FOCUS = ITEMS.register("spider_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.SpiderSpell()));

    public static final RegistryObject<Item> REDSTONE_FOCUS = ITEMS.register("redstone_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.RedstoneSpell()));

    public static final RegistryObject<Item> URBHADHACH_FOCUS = ITEMS.register("urbhadhach_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.UrbhadhachSpell()));

    public static final RegistryObject<Item> SCORCH_FOCUS = ITEMS.register("scorch_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.ScorchSpell()));

    public static final RegistryObject<Item> BRAINEATER_FOCUS = ITEMS.register("braineater_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.BrainEaterSpell()));

    public static final RegistryObject<Item> POISONBALL_FOCUS = ITEMS.register("poison_ball_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominous.common.magic.spells.PoisonBallSpell()));

    public static final RegistryObject<Item> WOLF_TOTEM = ITEMS.register("wolf_totem",
            () -> new BlockItemBase(ModBlocks.WOLF_TOTEM.get()));



    public static final RegistryObject<ServantSpawnEggItem> AXOLOTL_SERVANT_SPAWN_EGG = ITEMS.register(
            "axolotl_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.AXOLOTL_SERVANT, 0xFBC1E3, 0xA62D74, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CONQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "conquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.CONQUILLAGER_SERVANT, 0xD3D3D3, 0x0F0F0F, egg()));

    public static final RegistryObject<ServantSpawnEggItem> INQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "inquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.INQUILLAGER_SERVANT, 0xBCA341, 0x5C121B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SUNKEN_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT, 0x4A235A, 0xE67E22, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> SUNKEN_NECROMANCER_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER, 0x1B4F72, 0x7D6608, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> ARCH_GEOMANCER_SPAWN_EGG = ITEMS.register(
            "arch_geomancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ARCH_GEOMANCER, 0x2F4F4F, 0xC9A227, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> STORM_NECROMANCER_SPAWN_EGG = ITEMS.register(
            "storm_necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.STORM_NECROMANCER, 0x2E4053, 0x85C1E9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STORM_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
            "storm_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STORM_NECROMANCER_SERVANT, 0x2E4053, 0x85C1E9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HERESIARCH_SERVANT_SPAWN_EGG = ITEMS.register(
            "heresiarch_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.HERESIARCH_SERVANT, 0x0f1119, 0x4A235A, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> DISCIPLE_SPAWN_EGG = ITEMS.register(
            "disciple_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.DISCIPLE, 0x1B4F72, 0xE67E22, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DISCIPLE_SERVANT_SPAWN_EGG = ITEMS.register(
            "disciple_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.DISCIPLE_SERVANT, 0x1B4F72, 0xE6E6FA, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PIGLIN_SERVANT_SPAWN_EGG = ITEMS.register(
            "piglin_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.PIGLIN_SERVANT, 0x9C7A3C, 0xF0D7A7, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "piglin_brute_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.PIGLIN_BRUTE_SERVANT, 0x9C7A3C, 0x4A2800, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRONG_PIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "piglin_brute_servant_strong_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STRONG_PIGLIN_BRUTE_SERVANT, 0x9C7A3C, 0x4A2800, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ELITE_PIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "piglin_brute_servant_elite_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ELITE_PIGLIN_BRUTE_SERVANT, 0x9C7A3C, 0x4A2800, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRONG_ZPIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "strong_zpiglin_brute_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT, 0x5A7A3A, 0x4A6A2A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ELITE_ZPIGLIN_BRUTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "elite_zpiglin_brute_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT, 0x5A7A3A, 0x2D4A1E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "piglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.PIGLIN_HUNTER_SERVANT, 0x9C7A3C, 0x5C3A1E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRONG_PIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "strong_piglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STRONG_PIGLIN_HUNTER_SERVANT, 0x9C7A3C, 0x7A5C2A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ELITE_PIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "elite_piglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ELITE_PIGLIN_HUNTER_SERVANT, 0x9C7A3C, 0x3D1A00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZPIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "zpiglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ZPIGLIN_HUNTER_SERVANT, 0x5A7A3A, 0x3A5A2A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STRONG_ZPIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "strong_zpiglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT, 0x5A7A3A, 0x4A6A2A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ELITE_ZPIGLIN_HUNTER_SERVANT_SPAWN_EGG = ITEMS.register(
            "elite_zpiglin_hunter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT, 0x5A7A3A, 0x2D4A1E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FUNGUS_THROWER_SPAWN_EGG = ITEMS.register(
            "fungus_thrower_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.FUNGUS_THROWER, 0x9C7A3C, 0x3366FF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZFUNGUS_THROWER_SPAWN_EGG = ITEMS.register(
            "zfungus_thrower_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ZFUNGUS_THROWER, 0x5A7A3A, 0x3366FF, egg()));

    public static final RegistryObject<net.minecraftforge.common.ForgeSpawnEggItem> PIGLIN_MERCHANT_SPAWN_EGG = ITEMS.register(
            "piglin_merchant_spawn_egg",
            () -> new net.minecraftforge.common.ForgeSpawnEggItem(ModEntityTypes.PIGLIN_MERCHANT,
                    0x9C7A3C, 0x6B4E2E, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> FANATIC_SPAWN_EGG = ITEMS.register(
            "fanatic_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.FANATIC, 0x8B0000, 0x2D0000, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> ZEALOT_SPAWN_EGG = ITEMS.register(
            "zealot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ZEALOT, 0x5C4033, 0xD4A76A, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> BELDAM_SPAWN_EGG = ITEMS.register(
            "beldam_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.BELDAM, 0x340000, 0x191919, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> THUG_SPAWN_EGG = ITEMS.register(
            "thug_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.THUG, 0x8B0000, 0x191919, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> CHANNELLER_SPAWN_EGG = ITEMS.register(
            "channeller_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.CHANNELLER, 0x4a0e4e, 0xe5c07f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRIMSON_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register(
            "crimson_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.CRIMSON_SPIDER_SERVANT, 0xCC0000, 0xFFAA00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> RETURNED_SPAWN_EGG = ITEMS.register(
            "returned_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.RETURNED, 0x191919, 0x8b0000, egg()));

    public static final RegistryObject<ServantSpawnEggItem> AGONY_SPAWN_EGG = ITEMS.register(
            "agony_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.AGONY, 0x2b0f0f, 0x8b0000, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SCORCH_SPAWN_EGG = ITEMS.register(
            "scorch_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.SCORCH, 0x3b1414, 0xFFD700, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> URBHADHACH_SPAWN_EGG = ITEMS.register(
            "urbhadhach_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.URBHADHACH, 0x484848, 0x1c302f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> URBHADHACH_SERVANT_SPAWN_EGG = ITEMS.register(
            "urbhadhach_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.URBHADHACH_SERVANT, 0x484848, 0x88CCFF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WARG_SPAWN_EGG = ITEMS.register(
            "warg_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.WARG, 0x17141B, 0x6B6572, egg()));

    /**
     * 物品属性配置方法
     * 返回基础的 Item.Properties 对象
     * 可在此处添加堆叠数量、耐久度等属性
     *
     * @return 基础物品属性配置
     */
    public static Item.Properties egg() {
        return new Item.Properties();
    }
}
