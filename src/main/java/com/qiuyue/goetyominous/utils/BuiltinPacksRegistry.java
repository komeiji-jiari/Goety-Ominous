package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.compat.mod.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;

import java.nio.file.Path;

public class BuiltinPacksRegistry {

    public static void register(AddPackFindersEvent event) {
        var modFileInfo = ModList.get().getModFileById("goetyominous");
        if (modFileInfo == null) return;
        var modFile = modFileInfo.getFile();

        // --- 客户端资源包（SAR 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && SavageRavageCompat.isSavageRavageLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/sar_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/sar_compat",
                            Component.literal("SAR Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（UA 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/ua_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/ua_compat",
                            Component.literal("UA Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（MM 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && MutantMoreCompat.isMutantMoreLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/mm_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/mm_compat",
                            Component.literal("MM Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（MM 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && MutantMoreCompat.isMutantMoreLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/mm_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/mm_compat",
                            Component.literal("MM Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（IAS 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/ias_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/ias_compat",
                            Component.literal("IAS Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（AM 联动内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES && AlexMobsCompat.isAlexMobsLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/am_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/am_compat",
                            Component.literal("AlexMobs Compatibility Pack"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 服务端数据包（UA 联动内容）---
        if (event.getPackType() == PackType.SERVER_DATA && UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/ua_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/ua_compat_data",
                            Component.literal("UA Compatibility Data"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.SERVER_DATA,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 客户端资源包（旧版本纹理内容）---
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path packPath = modFile.findResource("resourcepacks/old_textures");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/old_textures",
                            Component.literal("GO Old Textures"),
                            false,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.CLIENT_RESOURCES,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }
        // --- 服务端数据包（SAR 联动内容）---
        if (event.getPackType() == PackType.SERVER_DATA && SavageRavageCompat.isSavageRavageLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/sar_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/sar_compat_data",
                            Component.literal("SAR Compatibility Data"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.SERVER_DATA,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }

        // --- 服务端数据包（AM 联动内容）---
        if (event.getPackType() == PackType.SERVER_DATA && AlexMobsCompat.isAlexMobsLoaded()) {
            Path packPath = modFile.findResource("resourcepacks/am_compat");
            if (packPath != null) {
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(
                            "goetyominous/am_compat_data",
                            Component.literal("AlexMobs Compatibility Data"),
                            true,
                            id -> new PathPackResources(id, packPath, true),
                            PackType.SERVER_DATA,
                            Pack.Position.TOP,
                            PackSource.BUILT_IN
                    );
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }
    }
}