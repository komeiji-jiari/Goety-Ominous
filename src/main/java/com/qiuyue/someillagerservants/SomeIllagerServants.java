package com.qiuyue.someillagerservants;

import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.qiuyue.someillagerservants.common.entities.hostile.Acolyte;
import com.qiuyue.someillagerservants.common.init.ModSounds;
import com.qiuyue.someillagerservants.common.network.ModNetwork;
import com.qiuyue.someillagerservants.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.someillagerservants.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.someillagerservants.compat.mod.MutantMoreCompat;
import com.qiuyue.someillagerservants.config.MobsConfig;
import com.qiuyue.someillagerservants.common.entities.ally.illager.*;
import com.qiuyue.someillagerservants.common.entities.ally.illager.train.SomeIllagerType;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.*;
import com.qiuyue.someillagerservants.common.entities.hostile.SunkenNecromancer;
import com.qiuyue.someillagerservants.common.init.ModCreativeTab;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.common.items.ModItems;
import com.qiuyue.someillagerservants.compat.mod.SavageRavageCompat;
import com.qiuyue.someillagerservants.compat.mod.UpgradeAquaticCompat;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import com.qiuyue.someillagerservants.compat.curios.CuriosIntegration;
import com.qiuyue.someillagerservants.utils.BuiltinPacksRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
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
@Mod(SomeIllagerServants.MOD_ID)
public class SomeIllagerServants {

    /**
     * 模组 ID，用于唯一标识本模组
     */
    public static final String MOD_ID = "someillagerservants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * 模组构造函数
     */
    public SomeIllagerServants() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addAttributes);
        modEventBus.addListener(this::loadComplete); // 注册加载完成事件
        modEventBus.addListener(BuiltinPacksRegistry::register); // 注册内置资源包
        ModNetwork.init();

        ModEntityTypes.register(modEventBus);
        ModItems.init();
        ModSounds.init();
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            com.qiuyue.someillagerservants.compat.sar.SarCompatManager.init(modEventBus);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.someillagerservants.compat.ua.UaCompatManager.init(modEventBus);
        }

        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.someillagerservants.compat.ias.IasCompatManager.init(modEventBus);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.someillagerservants.compat.mm.MmCompatManager.init(modEventBus);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.someillagerservants.compat.lm.LmCompatManager.init(modEventBus);
        }

        getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve("someillagerservants"), "someillagerservants");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC,
                "someillagerservants/someillagerservants-attributes.toml");
        AttributesConfig.loadConfig(AttributesConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("someillagerservants/someillagerservants-attributes.toml").toString());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MobsConfig.SPEC,
                "someillagerservants/someillagerservants-mobs.toml");
        MobsConfig.loadConfig(MobsConfig.SPEC,
                FMLPaths.CONFIGDIR.get().resolve("someillagerservants/someillagerservants-mobs.toml").toString());
    }

    /**
     * 通用设置方法
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        new CuriosIntegration().setup(event);
    }

    /**
     * 添加实体属性方法
     */
    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.CONQUILLAGER_SERVANT.get(), ConquillagerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.INQUILLAGER_SERVANT.get(), InquillagerServant.setCustomAttributes().build());
        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            com.qiuyue.someillagerservants.compat.ias.IasCompatManager.setCustomAttributes(event);
        }
        event.put(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT.get(), SunkenNecromancerServant.setCustomAttributes().build());
        event.put(ModEntityTypes.SUNKEN_NECROMANCER.get(), SunkenNecromancer.setCustomAttributes().build());
        event.put(ModEntityTypes.AXOLOTL_SERVANT.get(), AxolotlServant.setCustomAttributes().build());
        event.put(ModEntityTypes.HERESIARCH_SERVANT.get(), HeresiarchServant.setCustomAttributes().build());
        event.put(ModEntityTypes.ACOLYTE.get(), Acolyte.setCustomAttributes().build());
        event.put(ModEntityTypes.ACOLYTE_SERVANT.get(), AcolyteServant.setCustomAttributes().build());

        if (SavageRavageCompat.isSavageRavageLoaded()) {
            com.qiuyue.someillagerservants.compat.sar.SarCompatManager.setCustomAttributes(event);
        }

        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            com.qiuyue.someillagerservants.compat.ua.UaCompatManager.setCustomAttributes(event);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            com.qiuyue.someillagerservants.compat.mm.MmCompatManager.setCustomAttributes(event);
        }

        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            com.qiuyue.someillagerservants.compat.lm.LmCompatManager.setCustomAttributes(event);
        }
    }

    /**
     * 模组加载完成事件
     * 在所有模组都加载完成后执行
     */
    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            // 注册训练类型
            IllagerType.create("SomeIllagerServant", new SomeIllagerType());
        });
    }

    /**
     * 客户端设置方法
     */
    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        // 客户端专属设置代码
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