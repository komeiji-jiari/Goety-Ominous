package com.qiuyue.someillagerservants.common.world;

import com.mojang.serialization.Codec;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.config.MobsConfig;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModMobSpawnBiomeModifier implements BiomeModifier {

    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER =
            RegistryObject.create(
                    new ResourceLocation(SomeIllagerServants.MOD_ID, "mob_spawns"),
                    ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    SomeIllagerServants.MOD_ID
            );

    @Override
    public void modify(Holder<Biome> biome,
                       Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            addBiomeSpawns(biome, builder);
        }
    }

    private void addBiomeSpawns(Holder<Biome> biome,
                                ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ResourceLocation key = biome.unwrapKey().map(k -> k.location()).orElse(null);
        if (!biome.is(net.minecraft.tags.BiomeTags.IS_OVERWORLD)) return;
        if (key != null && (key.equals(new ResourceLocation("minecraft", "mushroom_fields"))
                || key.equals(new ResourceLocation("minecraft", "deep_dark")))) return;

        var spawnerList = builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER);
        boolean alreadyExists = spawnerList.stream()
                .anyMatch(s -> s.type == ModEntityTypes.BELDAM.get()
                        || s.type == ModEntityTypes.FANATIC.get()
                        || s.type == ModEntityTypes.ZEALOT.get());
        if (alreadyExists) return;

        int uWeight = MobsConfig.UrbhadhachSpawnWeight.get();
        if (uWeight > 0 && biome.get().getBaseTemperature() < 0.15F) {
            spawnerList.add(new MobSpawnSettings.SpawnerData(ModEntityTypes.URBHADHACH.get(),
                    uWeight,
                    MobsConfig.UrbhadhachSpawnMinCount.get(),
                    MobsConfig.UrbhadhachSpawnMaxCount.get()));
        }

        addSpawn(spawnerList, ModEntityTypes.BELDAM.get(),
                MobsConfig.BeldamSpawnWeight.get(),
                MobsConfig.BeldamSpawnMinCount.get(),
                MobsConfig.BeldamSpawnMaxCount.get());
        addSpawn(spawnerList, ModEntityTypes.FANATIC.get(),
                MobsConfig.FanaticSpawnWeight.get(),
                MobsConfig.FanaticSpawnMinCount.get(),
                MobsConfig.FanaticSpawnMaxCount.get());
        addSpawn(spawnerList, ModEntityTypes.ZEALOT.get(),
                MobsConfig.ZealotSpawnWeight.get(),
                MobsConfig.ZealotSpawnMinCount.get(),
                MobsConfig.ZealotSpawnMaxCount.get());
    }

    private void addSpawn(List<MobSpawnSettings.SpawnerData> list,
                          EntityType<? extends Raider> type, int weight, int min, int max) {
        if (weight > 0) {
            list.add(new MobSpawnSettings.SpawnerData(type, weight, min, max));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<ModMobSpawnBiomeModifier> makeCodec() {
        return Codec.unit(ModMobSpawnBiomeModifier::new);
    }
}
