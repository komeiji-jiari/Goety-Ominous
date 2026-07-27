package com.qiuyue.goetyominus;

import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominus.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Acolyte;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Martyr;
import com.qiuyue.goetyominus.common.entities.hostile.UrbhadhachEntity;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.*;
import com.qiuyue.goetyominus.common.init.*;
import com.qiuyue.goetyominus.common.items.CogCrossbowItem;
import com.qiuyue.goetyominus.common.network.ModNetwork;
import com.qiuyue.goetyominus.common.research.ResearchList;
import com.qiuyue.goetyominus.common.world.ModMobSpawnBiomeModifier;
import com.qiuyue.goetyominus.compat.mod.*;
import com.qiuyue.goetyominus.config.MobsConfig;
import com.qiuyue.goetyominus.config.SpellConfig;
import com.qiuyue.goetyominus.common.entities.ally.illager.*;
import com.qiuyue.goetyominus.common.entities.ally.illager.train.GoetyOminousType;
import com.qiuyue.goetyominus.common.entities.ally.mobs.*;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractStormNecromancer;
import com.qiuyue.goetyominus.common.entities.hostile.SunkenNecromancer;
import com.qiuyue.goetyominus.common.items.ModItems;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.compat.curios.CuriosIntegration;
import com.qiuyue.goetyominus.config.WeaponConfig;
import com.qiuyue.goetyominus.utils.BuiltinPacksRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileAlreadyExistsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

/**
 * 模组主类
 *
 * @author qiuyue
 */
@Mod(GoetyOminous.MOD_ID)
public class GoetyOminous {

    /**
     * 模组 ID，用于唯一标识本模组
     */
    public static final String MOD_ID = "goetyominus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * 模组构造函数
     */
    public GoetyOminous() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addAttributes);
        modEventBus.addListener(this::loadComplete); // 注册加载完成事件
        modEventBus.addListener(BuiltinPacksRegistry::register); // 注册内置资源包
        modEventBus.addListener(this::onClientSetup);
        ModNetwork.init();
        ModEntityTypes.register(modEventBus);
        ModContainerTypes.register(modEventBus);
        var biomeModifiers = DeferredRegister.create(
                ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, GoetyOminous.MOD_ID);
        biomeModifiers.register(modEventBus);
        biomeModifiers.register("mob_spawns", ModMobSpawnBiomeModifier::makeCodec);
        ModItems.init();
        ModSounds.init();
        ModBlocks.register(modEventBus);
        ModProcessorTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener((LivingAttackEvent event) -> {
            if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow
                    && arrow.getPersistentData().getBoolean("goetyominus:no_invul")) {
                event.getEntity().invulnerableTime = 0;
            }
        });

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            com.qiuyue.goetyominus.compat.sar.SarCompatManager.init(modEventBus);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.goetyominus.compat.ua.UaCompatManager.init(modEventBus);
        }

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.goetyominus.compat.ias.IasCompatManager.init(modEventBus);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.goetyominus.compat.mm.MmCompatManager.init(modEventBus);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.goetyominus.compat.lm.LmCompatManager.init(modEventBus);
        }

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goetyominus"), "goetyominus");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC,
                "goetyominus/goetyominus-attributes.toml");
        AttributesConfig.loadConfig(AttributesConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominus/goetyominus-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MobsConfig.SPEC,
                "goetyominus/goetyominus-mobs.toml");
        MobsConfig.loadConfig(MobsConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominus/goetyominus-mobs.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpellConfig.SPEC,
                "goetyominus/goetyominus-spells.toml");
        SpellConfig.loadConfig(SpellConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominus/goetyominus-spells.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WeaponConfig.SPEC,
                "goetyominus/goetyominus-weapons.toml");
        WeaponConfig.loadConfig(WeaponConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominus/goetyominus-weapons.toml").toString());
    }

    /**
     * 通用设置方法
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        new CuriosIntegration().setup(event);

        event.enqueueWork(() -> {
            net.minecraftforge.common.brewing.BrewingRecipeRegistry.addRecipe(
                    new com.Polarice3.Goety.utils.ModPotionUtil(
                            net.minecraft.world.item.Items.WARPED_FUNGUS.getDefaultInstance(),
                            net.minecraft.world.item.crafting.Ingredient.of(
                                    net.minecraft.world.item.Items.LILY_OF_THE_VALLEY),
                            new net.minecraft.world.item.ItemStack(
                                    com.qiuyue.goetyominus.common.items.ModItems.ACID_FUNGUS.get())
                    ));
        });
        SpawnPlacements.register(ModEntityTypes.BELDAM.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(ModEntityTypes.FANATIC.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(ModEntityTypes.ZEALOT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(ModEntityTypes.URBHADHACH.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, spawnType, pos, random) -> {
                    if (MobsConfig.UrbhadhachSpawnWeight.get() <= 0) return false;
                    if (level.getMoonPhase() != 0) return false;
                    return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
                });
        ResearchList.register();
    }

    /**
     * 添加实体属性方法
     */
    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.CONQUILLAGER_SERVANT.get(), ConquillagerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.INQUILLAGER_SERVANT.get(), InquillagerServant.setCustomAttributes().build());
        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.goetyominus.compat.ias.IasCompatManager.setCustomAttributes(event);
        }
        event.put(ModEntityTypes.URBHADHACH.get(), UrbhadhachEntity.setCustomAttributes().build());
        event.put(ModEntityTypes.URBHADHACH_SERVANT.get(), UrbhadhachServant.setCustomAttributes().build());
        event.put(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancer.setCustomAttributes().build());
        event.put(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServant.setCustomAttributes().build());
        event.put(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServant.setCustomAttributes().build());
        event.put(ModEntityTypes.ACOLYTE.get(), Acolyte.setCustomAttributes().build());
        event.put(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), CrimsonSpiderServant.setCustomAttributes().build());
        event.put(ModEntityTypes.BELDAM.get(), Beldam.setCustomAttributes().build());
        event.put(ModEntityTypes.FANATIC.get(), Fanatic.setCustomAttributes().build());
        event.put(ModEntityTypes.ZEALOT.get(), Zealot.setCustomAttributes().build());
        event.put(ModEntityTypes.MARTYR.get(), Martyr.setCustomAttributes().build());
        event.put(ModEntityTypes.THUG.get(), Thug.setCustomAttributes().build());
        event.put(ModEntityTypes.CHANNELLER.get(), Channeller.setCustomAttributes().build());
        event.put(ModEntityTypes.ACOLYTE_SERVANT.get(), AcolyteServant.setCustomAttributes().build());
        event.put(ModEntityTypes.STORM_NECROMANCER_SERVANT.get(), AbstractStormNecromancer.setCustomAttributes().build());
        event.put(ModEntityTypes.STORM_NECROMANCER.get(), AbstractStormNecromancer.setCustomAttributes().build());
        event.put(ModEntityTypes.FUNGUS_THROWER.get(), FungusThrower.createAttributes().build());
        event.put(ModEntityTypes.ZFUNGUS_THROWER.get(), ZPiglinServant.setCustomAttributes().build());
        event.put(ModEntityTypes.PIGLIN_MERCHANT.get(), PiglinMerchant.createAttributes().build());
        event.put(ModEntityTypes.PIGLIN_SERVANT.get(), PiglinServant.createAttributes().build());
        event.put(ModEntityTypes.PIGLIN_BRUTE_SERVANT.get(), PiglinBruteServant.createAttributes().build());
        event.put(ModEntityTypes.STRONG_PIGLIN_BRUTE_SERVANT.get(), StrongPiglinBruteServant.createAttributes().build());
        event.put(ModEntityTypes.ELITE_PIGLIN_BRUTE_SERVANT.get(), ElitePiglinBruteServant.createAttributes().build());
        event.put(ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT.get(), Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get()
                        + AttributesConfig.PiglinBruteServantEvolvedHealthBonus.get())
                .add(Attributes.ARMOR, com.Polarice3.Goety.config.AttributesConfig.ZPiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinBruteServantDamage.get()
                        + AttributesConfig.PiglinBruteServantEvolvedDamageBonus.get()).build());
        event.put(ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT.get(), Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get()
                        + AttributesConfig.PiglinBruteServantEvolvedHealthBonus.get()
                        + AttributesConfig.PiglinBruteServantEvolved2HealthBonus.get())
                .add(Attributes.ARMOR, com.Polarice3.Goety.config.AttributesConfig.ZPiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinBruteServantDamage.get()
                        + AttributesConfig.PiglinBruteServantEvolvedDamageBonus.get()
                        + AttributesConfig.PiglinBruteServantEvolved2DamageBonus.get()).build());
        event.put(ModEntityTypes.PIGLIN_HUNTER_SERVANT.get(), PiglinHunterServant.createAttributes().build());
        event.put(ModEntityTypes.STRONG_PIGLIN_HUNTER_SERVANT.get(), StrongPiglinHunterServant.createAttributes().build());
        event.put(ModEntityTypes.ELITE_PIGLIN_HUNTER_SERVANT.get(), ElitePiglinHunterServant.createAttributes().build());
        event.put(ModEntityTypes.ZPIGLIN_HUNTER_SERVANT.get(), ZPiglinHunterServant.createAttributes().build());
        event.put(ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT.get(), Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get()
                        + AttributesConfig.PiglinHunterServantEvolvedHealthBonus.get())
                .add(Attributes.ARMOR, com.Polarice3.Goety.config.AttributesConfig.ZPiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get()
                        + AttributesConfig.PiglinHunterServantEvolvedDamageBonus.get()).build());
        event.put(ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get(), Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get()
                        + AttributesConfig.PiglinHunterServantEvolvedHealthBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2HealthBonus.get())
                .add(Attributes.ARMOR, com.Polarice3.Goety.config.AttributesConfig.ZPiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get()
                        + AttributesConfig.PiglinHunterServantEvolvedDamageBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2DamageBonus.get()).build());

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            com.qiuyue.goetyominus.compat.sar.SarCompatManager.setCustomAttributes(event);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.goetyominus.compat.ua.UaCompatManager.setCustomAttributes(event);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.goetyominus.compat.mm.MmCompatManager.setCustomAttributes(event);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.goetyominus.compat.lm.LmCompatManager.setCustomAttributes(event);
        }
    }

    /**
     * 模组加载完成事件
     * 在所有模组都加载完成后执行
     */
    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            // 注册训练类型
            IllagerType.create("GoetyOminous", new GoetyOminousType());
        });
    }

    /**
     * 客户端设置方法
     */
    public void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.COG_CROSSBOW.get(), new ResourceLocation("pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null || !entity.isUsingItem() || entity.getUseItem() != stack) return 0.0F;
                        return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks())
                                / CogCrossbowItem.getChargeDuration(stack);
                    });
            ItemProperties.register(ModItems.COG_CROSSBOW.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.COG_CROSSBOW.get(), new ResourceLocation("charged"),
                    (stack, level, entity, seed) -> CogCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.COG_CROSSBOW.get(), new ResourceLocation("firework"),
                    (stack, level, entity, seed) ->
                            CogCrossbowItem.isCharged(stack) && CogCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);

            ItemProperties.register(ModItems.PIGLIN_PRIDE.get(), new ResourceLocation("pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null || !entity.isUsingItem() || entity.getUseItem() != stack) return 0.0F;
                        return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks())
                                / CogCrossbowItem.getChargeDuration(stack);
                    });
            ItemProperties.register(ModItems.PIGLIN_PRIDE.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.PIGLIN_PRIDE.get(), new ResourceLocation("charged"),
                    (stack, level, entity, seed) -> CogCrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.PIGLIN_PRIDE.get(), new ResourceLocation("firework"),
                    (stack, level, entity, seed) ->
                            CogCrossbowItem.isCharged(stack) && CogCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);
        });
        net.minecraft.client.gui.screens.MenuScreens.register(
                com.qiuyue.goetyominus.common.init.ModContainerTypes.FUNGUS_PACK.get(),
                com.qiuyue.goetyominus.client.gui.screen.inventory.FungusPackScreen::new);

}

    public static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
        }
        if (!Files.isDirectory(dirPath))
        {
            LOGGER.debug(CORE, "Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.error(CORE, "Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.error(CORE, "Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            LOGGER.debug(CORE, "Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug(CORE, "Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }
}