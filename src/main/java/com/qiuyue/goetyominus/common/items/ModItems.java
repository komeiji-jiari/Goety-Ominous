package com.qiuyue.goetyominus.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.item.BoneCudgelRenderer;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.common.items.curios.DarkAnkh;
import com.qiuyue.goetyominus.common.items.curios.FungusPackItem;
import com.qiuyue.goetyominus.common.items.curios.RaggedFungusPackItem;
import com.qiuyue.goetyominus.common.items.curios.ScreamingSkullJar;
import com.qiuyue.goetyominus.common.items.revive.BrokenStormCrown;
import com.qiuyue.goetyominus.common.items.revive.StormSoulJar;
import com.qiuyue.goetyominus.common.items.revive.SunkenSoulJar;
import com.qiuyue.goetyominus.common.items.revive.ThunderHorn;
import com.qiuyue.goetyominus.common.research.ResearchList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

    public static final RegistryObject<Item> COLD_HEART = ITEMS.register("cold_heart",
            () -> new ColdHeartItem());

    public static final RegistryObject<Item> ACID_FUNGUS = ITEMS.register("acid_fungus",
            () -> new AcidFungusItem());

    public static final RegistryObject<Item> BURNING_POTION = ITEMS.register("burning_potion",
            () -> new BurningPotionItem());

    public static final RegistryObject<Item> WITCH_BOMB = ITEMS.register("witch_bomb",
            () -> new WitchBombItem());

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

    public static final RegistryObject<Item> FUNGUS_PACK = ITEMS.register("fungus_pack",
            () -> new FungusPackItem());

    public static final RegistryObject<Item> RAGGED_FUNGUS_PACK = ITEMS.register("ragged_fungus_pack",
            () -> new RaggedFungusPackItem());

    public static final RegistryObject<Item> NETHER_WART_POTION = ITEMS.register("nether_wart_potion",
            () -> new NetherWartPotion());

    public static final RegistryObject<Item> BROOD_FOCUS = ITEMS.register("brood_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominus.common.magic.spells.BroodSpell()));

    public static final RegistryObject<Item> SPIDER_FOCUS = ITEMS.register("spider_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominus.common.magic.spells.SpiderSpell()));

    public static final RegistryObject<Item> REDSTONE_FOCUS = ITEMS.register("redstone_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominus.common.magic.spells.RedstoneSpell()));

    public static final RegistryObject<Item> URBHADHACH_FOCUS = ITEMS.register("urbhadhach_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(new com.qiuyue.goetyominus.common.magic.spells.UrbhadhachSpell()));

    /**
     * 征服者仆从刷怪蛋
     * 使用 ServantSpawnEggItem (这是北极冰的仆从刷怪蛋)类型，继承自 Forge 的原版刷怪蛋
     * 参数说明:
     * - ModEntityTypes.CONQUILLAGER_SERVANT: 对应的实体类型
     * - 0xD3D3D3: 刷怪蛋主颜色
     * - 0x0F0F0F: 刷怪蛋副颜色
     * - egg(): 物品属性配置
     */
    public static final RegistryObject<ServantSpawnEggItem> CONQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "conquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.CONQUILLAGER_SERVANT, 0xD3D3D3, 0x0F0F0F, egg()));

    /**
     * 巡查官仆从刷怪蛋
     * - 0xBCA341: 主颜色
     * - 0x5C121B: 副颜色
     */
    public static final RegistryObject<ServantSpawnEggItem> INQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "inquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.INQUILLAGER_SERVANT, 0xBCA341, 0x5C121B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SUNKEN_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT, 0x4A235A, 0xE67E22, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> SUNKEN_NECROMANCER_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER, 0x1B4F72, 0x7D6608, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> STORM_NECROMANCER_SPAWN_EGG = ITEMS.register(
            "storm_necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.STORM_NECROMANCER, 0x2E4053, 0x85C1E9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> STORM_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
            "storm_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.STORM_NECROMANCER_SERVANT, 0x2E4053, 0x85C1E9, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HERESIARCH_SERVANT_SPAWN_EGG = ITEMS.register(
            "heresiarch_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.HERESIARCH_SERVANT, 0x0f1119, 0x4A235A, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> ACOLYTE_SPAWN_EGG = ITEMS.register(
            "acolyte_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ACOLYTE, 0x1B4F72, 0xE67E22, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ACOLYTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "acolyte_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ACOLYTE_SERVANT, 0x1B4F72, 0xE6E6FA, egg()));

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

    public static final RegistryObject<ForgeSpawnEggItem> MARTYR_SPAWN_EGG = ITEMS.register(
            "martyr_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.MARTYR, 0x1a1a2e, 0x8b0000, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> CHANNELLER_SPAWN_EGG = ITEMS.register(
            "channeller_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.CHANNELLER, 0x4a0e4e, 0xe5c07f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRIMSON_SPIDER_SERVANT_SPAWN_EGG = ITEMS.register(
            "crimson_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.CRIMSON_SPIDER_SERVANT, 0xCC0000, 0xFFAA00, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> URBHADHACH_SPAWN_EGG = ITEMS.register(
            "urbhadhach_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.URBHADHACH, 0x484848, 0x1c302f, egg()));

    public static final RegistryObject<ServantSpawnEggItem> URBHADHACH_SERVANT_SPAWN_EGG = ITEMS.register(
            "urbhadhach_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.URBHADHACH_SERVANT, 0x484848, 0x88CCFF, egg()));

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
