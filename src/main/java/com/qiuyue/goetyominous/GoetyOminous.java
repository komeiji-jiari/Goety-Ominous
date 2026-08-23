package com.qiuyue.goetyominous;

import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.entities.hostile.Scorch;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Disciple;
import com.qiuyue.goetyominous.common.entities.hostile.UrbhadhachEntity;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.*;
import com.qiuyue.goetyominous.common.init.*;
import com.qiuyue.goetyominous.common.items.CogCrossbowItem;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.research.ResearchList;
import com.qiuyue.goetyominous.common.ritual.FelRitualType;
import com.qiuyue.goetyominous.common.world.ModMobSpawnBiomeModifier;
import com.qiuyue.goetyominous.compat.mod.*;
import com.qiuyue.goetyominous.compat.spear.SpearBackportCompat;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.qiuyue.goetyominous.common.entities.ally.illager.*;
import com.qiuyue.goetyominous.common.entities.ally.illager.train.GoetyOminousType;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;
import com.qiuyue.goetyominous.common.events.NucleeperNukeProtectionHandler;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractStormNecromancer;
import com.qiuyue.goetyominous.common.entities.hostile.SunkenNecromancer;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ArchGeomancerEntity;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.compat.curios.CuriosIntegration;
import com.qiuyue.goetyominous.config.WeaponConfig;
import com.qiuyue.goetyominous.utils.BuiltinPacksRegistry;
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


@Mod(GoetyOminous.MOD_ID)
public class GoetyOminous {


    public static final String MOD_ID = "goetyominous";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static com.Polarice3.Goety.api.magic.SpellType FEL;


    public GoetyOminous() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addAttributes);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(BuiltinPacksRegistry::register);
        modEventBus.addListener(this::onClientSetup);
        ModNetwork.init();
        ModEntityTypes.register(modEventBus);
        ModContainerTypes.register(modEventBus);
        var biomeModifiers = DeferredRegister.create(
                ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, GoetyOminous.MOD_ID);
        biomeModifiers.register(modEventBus);
        biomeModifiers.register("mob_spawns", ModMobSpawnBiomeModifier::makeCodec);
        ModSounds.init();
        ModItems.init();
        ModBlocks.register(modEventBus);
        ModProcessorTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        com.Polarice3.Goety.api.ritual.RitualType.addRitualType("fel",
                new FelRitualType("fel"));
        MinecraftForge.EVENT_BUS.addListener((LivingAttackEvent event) -> {
            if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow
                    && arrow.getPersistentData().getBoolean("goetyominous:no_invul")) {
                event.getEntity().invulnerableTime = 0;
            }
        });

        if (SpearBackportCompat.isSpearBackportLoaded()) {
            com.qiuyue.goetyominous.compat.spear.SpearBackportCompat.init(modEventBus);
        }

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            com.qiuyue.goetyominous.compat.sar.SarCompatManager.init(modEventBus);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.goetyominous.compat.ua.UaCompatManager.init(modEventBus);
        }

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.goetyominous.compat.ias.IasCompatManager.init(modEventBus);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.goetyominous.compat.mm.MmCompatManager.init(modEventBus);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.goetyominous.compat.lm.LmCompatManager.init(modEventBus);
        }

        if (OpposingForceCompat.isOpposingForceLoaded()) {
            com.qiuyue.goetyominous.compat.of.OfCompatManager.init(modEventBus);
        }

        if (AlexMobsCompat.isAlexMobsLoaded()) {
            com.qiuyue.goetyominous.compat.am.AmCompatManager.init(modEventBus);
        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            com.qiuyue.goetyominous.compat.ac.AcCompatManager.init(modEventBus);
            // AC 为可选前置:这三个事件类都直接引用 AC 类型,不能加 @Mod.EventBusSubscriber
            // (会被 Forge 无条件 Class.forName 导致 AC 缺失时 NoClassDefFoundError)。
            // 只在 AC 加载时手动注册到 FORGE 总线。
            MinecraftForge.EVENT_BUS.register(NucleeperNukeProtectionHandler.class);
            MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.events.NucleeperNukeKillHandler.class);
            MinecraftForge.EVENT_BUS.register(com.qiuyue.goetyominous.common.events.RaycatAmuletEvents.class);
        }

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("goetyominous"), "goetyominous");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC,
                "goetyominous/goetyominous-attributes.toml");
        AttributesConfig.loadConfig(AttributesConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MobsConfig.SPEC,
                "goetyominous/goetyominous-mobs.toml");
        MobsConfig.loadConfig(MobsConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-mobs.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpellConfig.SPEC,
                "goetyominous/goetyominous-spells.toml");
        SpellConfig.loadConfig(SpellConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-spells.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WeaponConfig.SPEC,
                "goetyominous/goetyominous-weapons.toml");
        WeaponConfig.loadConfig(WeaponConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("goetyominous/goetyominous-weapons.toml").toString());

        FEL = com.Polarice3.Goety.api.magic.SpellType.create("FEL", "fel");
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        new CuriosIntegration().setup(event);

        event.enqueueWork(() -> {
            net.minecraftforge.common.brewing.BrewingRecipeRegistry.addRecipe(
                    new com.Polarice3.Goety.utils.ModPotionUtil(
                            net.minecraft.world.item.Items.WARPED_FUNGUS.getDefaultInstance(),
                            net.minecraft.world.item.crafting.Ingredient.of(
                                    net.minecraft.world.item.Items.LILY_OF_THE_VALLEY),
                            new net.minecraft.world.item.ItemStack(
                                    com.qiuyue.goetyominous.common.items.ModItems.ACID_FUNGUS.get())
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


    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.CONQUILLAGER_SERVANT.get(), ConquillagerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.INQUILLAGER_SERVANT.get(), InquillagerServant.setCustomAttributes().build());
        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.goetyominous.compat.ias.IasCompatManager.setCustomAttributes(event);
        }
        event.put(ModEntityTypes.URBHADHACH.get(), UrbhadhachEntity.setCustomAttributes().build());
        event.put(ModEntityTypes.URBHADHACH_SERVANT.get(), UrbhadhachServant.setCustomAttributes().build());
        event.put(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancer.setCustomAttributes().build());
        event.put(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServant.setCustomAttributes().build());
        event.put(ModEntityTypes.WARG.get(), Warg.setCustomAttributes().build());
        event.put(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServant.setCustomAttributes().build());
        event.put(ModEntityTypes.DISCIPLE.get(), Disciple.setCustomAttributes().build());
        event.put(ModEntityTypes.CRIMSON_SPIDER_SERVANT.get(), CrimsonSpiderServant.setCustomAttributes().build());
        event.put(ModEntityTypes.BELDAM.get(), Beldam.setCustomAttributes().build());
        event.put(ModEntityTypes.FANATIC.get(), Fanatic.setCustomAttributes().build());
        event.put(ModEntityTypes.ZEALOT.get(), Zealot.setCustomAttributes().build());
        // event.put(ModEntityTypes.MARTYR.get(), Martyr.setCustomAttributes().build());
        event.put(ModEntityTypes.THUG.get(), Thug.setCustomAttributes().build());
        event.put(ModEntityTypes.CHANNELLER.get(), Channeller.setCustomAttributes().build());
        event.put(ModEntityTypes.SCORCH.get(), Scorch.setCustomAttributes().build());
        event.put(ModEntityTypes.RETURNED.get(), Returned.setCustomAttributes().build());
        event.put(ModEntityTypes.AGONY.get(), Agony.setCustomAttributes().build());
        event.put(ModEntityTypes.ARCH_GEOMANCER.get(), ArchGeomancerEntity.setCustomAttributes().build());
        event.put(ModEntityTypes.DISCIPLE_SERVANT.get(), DiscipleServant.setCustomAttributes().build());
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
            com.qiuyue.goetyominous.compat.sar.SarCompatManager.setCustomAttributes(event);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.goetyominous.compat.ua.UaCompatManager.setCustomAttributes(event);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.goetyominous.compat.mm.MmCompatManager.setCustomAttributes(event);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.goetyominous.compat.lm.LmCompatManager.setCustomAttributes(event);
        }

        if (OpposingForceCompat.isOpposingForceLoaded()) {
            com.qiuyue.goetyominous.compat.of.OfCompatManager.setCustomAttributes(event);
        }

        if (AlexMobsCompat.isAlexMobsLoaded()) {
            com.qiuyue.goetyominous.compat.am.AmCompatManager.setCustomAttributes(event);
        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            com.qiuyue.goetyominous.compat.ac.AcCompatManager.setCustomAttributes(event);
        }
    }


    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
                        IllagerType.create("GoetyOminous", new GoetyOminousType());
        });
    }


    public void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.WITCH_BOW.get(), new ResourceLocation("pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null || entity.getUseItem() != stack) return 0.0F;
                        return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
            ItemProperties.register(ModItems.WITCH_BOW.get(), new ResourceLocation("pulling"),
                    (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

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
                com.qiuyue.goetyominous.common.init.ModContainerTypes.FUNGUS_PACK.get(),
                com.qiuyue.goetyominous.client.gui.screen.inventory.FungusPackScreen::new);

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