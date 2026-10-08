package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;

public class TrialLootTables {
    public static final ResourceLocation SPAWNER_TRIAL_CHAMBER_KEY =
            create("spawners/trial_chamber/key");
    public static final ResourceLocation SPAWNER_TRIAL_CHAMBER_CONSUMABLES =
            create("spawners/trial_chamber/consumables");
    public static final ResourceLocation SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS =
            create("spawners/trial_chamber/items_to_drop_when_ominous");
    public static final ResourceLocation SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY =
            create("spawners/ominous/trial_chamber/key");
    public static final ResourceLocation SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES =
            create("spawners/ominous/trial_chamber/consumables");

    private static ResourceLocation create(String path) {
        return new ResourceLocation(GoetyOminous.MOD_ID, path);
    }
}
