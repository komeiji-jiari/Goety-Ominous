package com.qiuyue.goetyominous.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.blocks.trial.SpawnDataWithEquipment;
import com.qiuyue.goetyominous.common.blocks.trial.TrialLootTables;
import com.qiuyue.goetyominous.common.blocks.trial.VaultServerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer,
                                 float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn,
                                 SimpleWeightedRandomList<SpawnDataWithEquipment> spawnPotentialsDefinition,
                                 SimpleWeightedRandomList<ResourceLocation> lootTablesToEject,
                                 ResourceLocation itemsToDropWhenOminous) {
    public static final TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(4, 6.0F, 2.0F, 2.0F, 1.0F, 40,
            SimpleWeightedRandomList.empty(),
            SimpleWeightedRandomList.<ResourceLocation>builder()
                    .add(TrialLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES, 1)
                    .add(TrialLootTables.SPAWNER_TRIAL_CHAMBER_KEY, 1)
                    .build(),
            TrialLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS);

    public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VaultServerData.lenientOptionalFieldOf(Codec.intRange(1, 128), "spawn_range", DEFAULT.spawnRange())
                    .forGetter(TrialSpawnerConfig::spawnRange),
            VaultServerData.lenientOptionalFieldOf(Codec.floatRange(0.0F, Float.MAX_VALUE), "total_mobs", DEFAULT.totalMobs())
                    .forGetter(TrialSpawnerConfig::totalMobs),
            VaultServerData.lenientOptionalFieldOf(Codec.floatRange(0.0F, Float.MAX_VALUE), "simultaneous_mobs", DEFAULT.simultaneousMobs())
                    .forGetter(TrialSpawnerConfig::simultaneousMobs),
            VaultServerData.lenientOptionalFieldOf(Codec.floatRange(0.0F, Float.MAX_VALUE), "total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer())
                    .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
            VaultServerData.lenientOptionalFieldOf(Codec.floatRange(0.0F, Float.MAX_VALUE), "simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer())
                    .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
            VaultServerData.lenientOptionalFieldOf(Codec.intRange(0, Integer.MAX_VALUE), "ticks_between_spawn", DEFAULT.ticksBetweenSpawn())
                    .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
            VaultServerData.lenientOptionalFieldOf(SimpleWeightedRandomList.wrappedCodecAllowingEmpty(SpawnDataWithEquipment.CODEC), "spawn_potentials", SimpleWeightedRandomList.empty())
                    .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
            VaultServerData.lenientOptionalFieldOf(SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceLocation.CODEC), "loot_tables_to_eject", DEFAULT.lootTablesToEject())
                    .forGetter(TrialSpawnerConfig::lootTablesToEject),
            VaultServerData.lenientOptionalFieldOf(ResourceLocation.CODEC, "items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous())
                    .forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
    ).apply(instance, TrialSpawnerConfig::new));

    public int calculateTargetTotalMobs(int players) {
        return Mth.floor(this.totalMobs + this.totalMobsAddedPerPlayer * (float) players);
    }

    public int calculateTargetSimultaneousMobs(int players) {
        return Mth.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float) players);
    }

    public long ticksBetweenItemSpawners() {
        return 160L;
    }
}
