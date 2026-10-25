package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.config.ExpeditionConfig;

public record ExpeditionTuning(
        int bindLimit,
        int minTravelers,
        int maxTravelers,
        int baseDraws,
        double extraDrawChance,
        int biomeTicks,
        int ruinTicks,
        double deathChance,
        int deathDecreaseStart,
        double deathDecreasePerTraveler,
        double deathChanceFloor,
        int baseOres,
        int oresPerPickaxe,
        int baseMobDraws,
        int mobDrawsPerCrossbow,
        int dispatchRadius,
        boolean recallLosesTravelers
) {
    public static ExpeditionTuning fromConfig() {
        return new ExpeditionTuning(
                ExpeditionConfig.CompassBindLimit.get(),
                ExpeditionConfig.MinTravelers.get(),
                ExpeditionConfig.MaxTravelers.get(),
                ExpeditionConfig.BaseDraws.get(),
                ExpeditionConfig.ExtraDrawChancePerTraveler.get(),
                ExpeditionConfig.BiomeTicks.get(),
                ExpeditionConfig.RuinTicks.get(),
                ExpeditionConfig.DeathChance.get(),
                ExpeditionConfig.DeathDecreaseStart.get(),
                ExpeditionConfig.DeathDecreasePerTraveler.get(),
                ExpeditionConfig.DeathChanceFloor.get(),
                ExpeditionConfig.BaseOres.get(),
                ExpeditionConfig.OresPerPickaxe.get(),
                ExpeditionConfig.BaseMobDraws.get(),
                ExpeditionConfig.MobDrawsPerCrossbow.get(),
                ExpeditionConfig.DispatchRadius.get(),
                ExpeditionConfig.RecallLosesTravelers.get()
        );
    }

    public int effectiveMaxTravelers() {
        return Math.max(minTravelers, maxTravelers);
    }

    public int ticks(ExpeditionKind kind) {
        return Math.max(1, kind == ExpeditionKind.BIOME ? biomeTicks : ruinTicks);
    }

    public double deathChanceFor(int count) {
        int over = Math.max(0, count - (deathDecreaseStart - 1));
        double chance = deathChance - (double) over * deathDecreasePerTraveler;
        return Math.max(deathChanceFloor, Math.min(1.0, chance));
    }

    public int extraDrawRolls(int count) {
        return Math.max(0, count - minTravelers);
    }
}
