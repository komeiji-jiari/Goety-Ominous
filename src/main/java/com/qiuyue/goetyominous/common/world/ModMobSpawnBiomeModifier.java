package com.qiuyue.goetyominous.common.world;

import com.mojang.serialization.Codec;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModMobSpawnBiomeModifier implements BiomeModifier {

    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER =
            RegistryObject.create(
                    new ResourceLocation(GoetyOminous.MOD_ID, "mob_spawns"),
                    ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    GoetyOminous.MOD_ID
            );

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            addBiomeSpawns(biome, builder);
        }
    }

    private void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ResourceLocation key = biome.unwrapKey().map(k -> k.location()).orElse(null);
        if (!biome.is(net.minecraft.tags.BiomeTags.IS_OVERWORLD)) return;
        if (key != null && (key.equals(new ResourceLocation("minecraft", "mushroom_fields"))
                || key.equals(new ResourceLocation("minecraft", "deep_dark")))) return;
        if (biome.value().getBaseTemperature() >= 0.15F) return;

        MobSpawnSettingsBuilder spawnSettings = builder.getMobSpawnSettings();
        var spawnerList = spawnSettings.getSpawner(MobCategory.MONSTER);

        addSpawn(spawnerList, spawnSettings, ModEntityTypes.DREDEN.get(),
                MobsConfig.DredenSpawnWeight.get(),
                MobsConfig.DredenSpawnMinCount.get(),
                MobsConfig.DredenSpawnMaxCount.get(),
                1.0D, 1.0D);

        addSpawn(spawnerList, spawnSettings, ModEntityTypes.URBHADHACH.get(),
                MobsConfig.UrbhadhachSpawnWeight.get(),
                MobsConfig.UrbhadhachSpawnMinCount.get(),
                MobsConfig.UrbhadhachSpawnMaxCount.get(),
                1.0D, 1.0D);

        addSpawn(spawnerList, spawnSettings, ModEntityTypes.BELDAM.get(),
                MobsConfig.BeldamSpawnWeight.get(),
                MobsConfig.BeldamSpawnMinCount.get(),
                MobsConfig.BeldamSpawnMaxCount.get(),
                1.0D, 1.0D);

        addSpawn(spawnerList, spawnSettings, ModEntityTypes.FANATIC.get(),
                MobsConfig.FanaticSpawnWeight.get(),
                MobsConfig.FanaticSpawnMinCount.get(),
                MobsConfig.FanaticSpawnMaxCount.get(),
                1.0D, 1.0D);

        addSpawn(spawnerList, spawnSettings, ModEntityTypes.ZEALOT.get(),
                MobsConfig.ZealotSpawnWeight.get(),
                MobsConfig.ZealotSpawnMinCount.get(),
                MobsConfig.ZealotSpawnMaxCount.get(),
                1.0D, 1.0D);
    }

    private void addSpawn(List<MobSpawnSettings.SpawnerData> list,
                          MobSpawnSettings.Builder spawnSettings,
                          EntityType<?> type,
                          int weight, int min, int max,
                          double charge, double energyBudget) {
        if (weight <= 0) return;
        if (list.stream().anyMatch(s -> s.type == type)) return;
        list.add(new MobSpawnSettings.SpawnerData(type, weight, min, max));
        spawnSettings.addMobCharge(type, charge, energyBudget);
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<ModMobSpawnBiomeModifier> makeCodec() {
        return Codec.unit(ModMobSpawnBiomeModifier::new);
    }
}