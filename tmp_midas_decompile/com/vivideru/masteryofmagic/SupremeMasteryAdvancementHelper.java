/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.GoetyMasteryOfMagicMod;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class SupremeMasteryAdvancementHelper {
    private SupremeMasteryAdvancementHelper() {
    }

    public static void grant(Player player, int level) {
        ServerPlayer serverPlayer;
        block7: {
            block6: {
                if (!(player instanceof ServerPlayer)) break block6;
                serverPlayer = (ServerPlayer)player;
                if (level >= 1 && level <= 3) break block7;
            }
            return;
        }
        String suffix = level == 1 ? "i" : (level == 2 ? "ii" : "iii");
        Advancement advancement = serverPlayer.f_8924_.m_129889_().m_136041_(new ResourceLocation("goety_mastery_of_magic", "wizardry_mastery_" + suffix));
        if (advancement == null) {
            GoetyMasteryOfMagicMod.LOGGER.warn("Supreme Mastery advancement for level {} was not found", (Object)level);
            return;
        }
        AdvancementProgress progress = serverPlayer.m_8960_().m_135996_(advancement);
        if (!progress.m_8193_()) {
            for (String criterion : progress.m_8219_()) {
                serverPlayer.m_8960_().m_135988_(advancement, criterion);
            }
        }
    }

    public static void grantSchool(Player player, MasteryData.SupremeSchool school) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        String path = switch (school) {
            default -> throw new IncompatibleClassChangeError();
            case MasteryData.SupremeSchool.NETHER -> "supreme_nether_mastery";
            case MasteryData.SupremeSchool.SKIES -> "supreme_skies_mastery";
            case MasteryData.SupremeSchool.PLANET -> "supreme_planet_mastery";
        };
        Advancement advancement = serverPlayer.f_8924_.m_129889_().m_136041_(new ResourceLocation("goety_mastery_of_magic", path));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = serverPlayer.m_8960_().m_135996_(advancement);
        for (String criterion : progress.m_8219_()) {
            serverPlayer.m_8960_().m_135988_(advancement, criterion);
        }
    }
}

