/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.items.IItemHandler
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;

public final class SchoolSupremeRitualRequirements {
    private static final int RADIUS = 96;
    private static final Map<String, Integer> NETHER_SCORES = Map.ofEntries(Map.entry("wither_necromancer_servant", 10), Map.entry("inferno", 10), Map.entry("malghast", 12), Map.entry("damned", 6), Map.entry("blaze_servant", 3), Map.entry("burning_hoglin", 4), Map.entry("hoglin_servant", 2), Map.entry("zpiglin_servant", 2), Map.entry("zpiglin_brute_servant", 4), Map.entry("wither_skeleton_servant", 3), Map.entry("blackguard_servant", 5), Map.entry("warlock_servant", 3), Map.entry("heretic_servant", 3), Map.entry("maverick_servant", 3), Map.entry("reprobate_servant", 3));
    private static final Map<UUID, Cached> CACHE = new ConcurrentHashMap<UUID, Cached>();

    private SchoolSupremeRitualRequirements() {
    }

    public static Component validate(ServerLevel level, BlockPos altar, Player player, RitualKind kind) {
        String key = player.m_20148_() + ":" + kind;
        Cached cached = CACHE.get(player.m_20148_());
        if (cached != null && cached.key.equals(key) && cached.altar.equals((Object)altar) && cached.expiry >= level.m_46467_()) {
            return cached.failure;
        }
        Component failure = SchoolSupremeRitualRequirements.validateNow(level, altar, player, kind);
        CACHE.put(player.m_20148_(), new Cached(key, altar.m_7949_(), level.m_46467_() + 40L, failure));
        return failure;
    }

    private static Component validateNow(ServerLevel level, BlockPos altar, Player player, RitualKind kind) {
        if (kind == RitualKind.NETHER) {
            if (MasteryData.hasSupreme(player, MasteryData.SupremeSchool.NETHER)) {
                return SchoolSupremeRitualRequirements.msg("already_mastered");
            }
            if (MasteryData.get(player, MasteryData.MasteryId.NETHER) < 3) {
                return SchoolSupremeRitualRequirements.msg("nether_mastery");
            }
        } else {
            Structure windShrine;
            if (MasteryData.hasSupreme(player, MasteryData.SupremeSchool.SKIES)) {
                return SchoolSupremeRitualRequirements.msg("already_mastered");
            }
            if (MasteryData.get(player, MasteryData.MasteryId.SKY) < 3) {
                return SchoolSupremeRitualRequirements.msg("sky_mastery");
            }
            if (MasteryData.get(player, MasteryData.MasteryId.STORM) < 3) {
                return SchoolSupremeRitualRequirements.msg("storm_mastery");
            }
            if (kind == RitualKind.SKIES_SECOND) {
                long packed = MasteryData.getPendingSkiesAltar(player, level.m_46472_().m_135782_().toString(), level.m_46467_());
                if (packed == Long.MIN_VALUE) {
                    return SchoolSupremeRitualRequirements.msg("skies_first_altar");
                }
                BlockPos first = BlockPos.m_122022_((long)packed);
                if (first.equals((Object)altar) || first.m_123331_((Vec3i)altar) > 256.0) {
                    return SchoolSupremeRitualRequirements.msg("skies_altar_distance");
                }
                BlockEntity be = level.m_7702_(first);
                if (!(be instanceof DarkAltarBlockEntity)) {
                    return SchoolSupremeRitualRequirements.msg("skies_first_altar");
                }
                DarkAltarBlockEntity darkAltar = (DarkAltarBlockEntity)be;
                IItemHandler handler = (IItemHandler)darkAltar.itemStackHandler.orElse(null);
                if (handler == null || !handler.getStackInSlot(0).m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_ATTUNEMENT_TOKEN.get())) {
                    return SchoolSupremeRitualRequirements.msg("skies_first_altar");
                }
            }
            if ((windShrine = (Structure)level.m_9598_().m_175515_(Registries.f_256944_).m_7745_(new ResourceLocation("goety", "wind_shrine"))) == null || !level.m_215010_().m_220524_(altar, windShrine).m_73603_()) {
                return SchoolSupremeRitualRequirements.msg("wind_shrine");
            }
        }
        for (int cx = altar.m_123341_() - 96 >> 4; cx <= altar.m_123341_() + 96 >> 4; ++cx) {
            for (int cz = altar.m_123343_() - 96 >> 4; cz <= altar.m_123343_() + 96 >> 4; ++cz) {
                level.m_6325_(cx, cz);
            }
        }
        Army army = SchoolSupremeRitualRequirements.scanArmy(level, altar, player);
        Counts counts = SchoolSupremeRitualRequirements.scanBlocks(level, altar, kind == RitualKind.NETHER);
        if (kind == RitualKind.NETHER) {
            if (army.netherScore < 100) {
                return SchoolSupremeRitualRequirements.missing("nether_servant_score", army.netherScore, 100);
            }
            if (army.witherNecromancers < 1) {
                return SchoolSupremeRitualRequirements.missing("wither_necromancers", army.witherNecromancers, 1);
            }
            if (army.cultists < 10) {
                return SchoolSupremeRitualRequirements.missing("cultists", army.cultists, 10);
            }
            if (counts.netherLazethyst < 5) {
                return SchoolSupremeRitualRequirements.missing("nether_lazethyst", counts.netherLazethyst, 5);
            }
            if (counts.netherBricks < 600) {
                return SchoolSupremeRitualRequirements.missing("nether_bricks", counts.netherBricks, 600);
            }
            if (counts.blackstone < 200) {
                return SchoolSupremeRitualRequirements.missing("blackstone", counts.blackstone, 200);
            }
            if (counts.basalt < 100) {
                return SchoolSupremeRitualRequirements.missing("basalt", counts.basalt, 100);
            }
            if (counts.glowstone < 30) {
                return SchoolSupremeRitualRequirements.missing("glowstone", counts.glowstone, 30);
            }
            if (counts.soulSand < 100) {
                return SchoolSupremeRitualRequirements.missing("soul_sand", counts.soulSand, 100);
            }
            if (counts.netherWart < 100) {
                return SchoolSupremeRitualRequirements.missing("nether_wart", counts.netherWart, 100);
            }
        } else {
            if (army.windCallers < 10) {
                return SchoolSupremeRitualRequirements.missing("wind_callers", army.windCallers, 10);
            }
            if (army.stormCallers < 10) {
                return SchoolSupremeRitualRequirements.missing("storm_callers", army.stormCallers, 10);
            }
            if (army.jadeGolems < 2) {
                return SchoolSupremeRitualRequirements.missing("jade_golems", army.jadeGolems, 2);
            }
            if (counts.skyLazethyst < 4) {
                return SchoolSupremeRitualRequirements.missing("sky_lazethyst", counts.skyLazethyst, 4);
            }
            if (counts.stormLazethyst < 4) {
                return SchoolSupremeRitualRequirements.missing("storm_lazethyst", counts.stormLazethyst, 4);
            }
            if (counts.copper < 400) {
                return SchoolSupremeRitualRequirements.missing("copper", counts.copper, 400);
            }
            if (counts.chains < 100) {
                return SchoolSupremeRitualRequirements.missing("chains", counts.chains, 100);
            }
            if (counts.ironBlocks < 50) {
                return SchoolSupremeRitualRequirements.missing("iron_blocks", counts.ironBlocks, 50);
            }
            if (counts.redstone < 100) {
                return SchoolSupremeRitualRequirements.missing("redstone", counts.redstone, 100);
            }
            if (counts.stoneBricks < 400) {
                return SchoolSupremeRitualRequirements.missing("stone_bricks", counts.stoneBricks, 400);
            }
            if (counts.lightningRods < 10) {
                return SchoolSupremeRitualRequirements.missing("lightning_rods", counts.lightningRods, 10);
            }
        }
        return null;
    }

    private static Army scanArmy(ServerLevel level, BlockPos altar, Player player) {
        Army result = new Army();
        for (LivingEntity entity : level.m_45976_(LivingEntity.class, new AABB(altar).m_82400_(96.0))) {
            if (!(entity instanceof IOwned)) continue;
            IOwned owned = (IOwned)entity;
            if (!player.m_20148_().equals(owned.getOwnerId())) continue;
            ResourceLocation id = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_());
            String path = id.m_135815_();
            result.netherScore += NETHER_SCORES.getOrDefault(path, 0).intValue();
            if (path.equals("wither_necromancer_servant")) {
                ++result.witherNecromancers;
            }
            if ((path.contains("cultist") || path.equals("warlock_servant") || path.equals("heretic_servant") || path.equals("maverick_servant") || path.equals("reprobate_servant")) && !path.contains("witch")) {
                ++result.cultists;
            }
            if (path.equals("wind_caller_servant")) {
                ++result.windCallers;
            }
            if (path.equals("storm_caster_servant") || path.equals("storm_caller_servant")) {
                ++result.stormCallers;
            }
            if (!path.equals("squall_golem") && !path.equals("jade_golem")) continue;
            ++result.jadeGolems;
        }
        return result;
    }

    private static Counts scanBlocks(ServerLevel level, BlockPos altar, boolean nether) {
        Counts result = new Counts();
        int r2 = 9216;
        for (int cx = altar.m_123341_() - 96 >> 4; cx <= altar.m_123341_() + 96 >> 4; ++cx) {
            for (int cz = altar.m_123343_() - 96 >> 4; cz <= altar.m_123343_() + 96 >> 4; ++cz) {
                LevelChunk chunk = level.m_6325_(cx, cz);
                for (Map.Entry e : chunk.m_62954_().entrySet()) {
                    ChargedRunedLazethystBlockEntity laz;
                    Object v;
                    if (((BlockPos)e.getKey()).m_123331_((Vec3i)altar) > (double)r2 || !((v = e.getValue()) instanceof ChargedRunedLazethystBlockEntity) || !(laz = (ChargedRunedLazethystBlockEntity)((Object)v)).hasStoredSpell()) continue;
                    String p = BuiltInRegistries.f_256975_.m_7981_((Object)level.m_8055_((BlockPos)e.getKey()).m_60734_()).m_135815_();
                    if (p.contains("nether") && p.contains("charged")) {
                        ++result.netherLazethyst;
                    }
                    if (p.contains("sky") && p.contains("charged")) {
                        ++result.skyLazethyst;
                    }
                    if (!p.contains("storm") || !p.contains("charged")) continue;
                    ++result.stormLazethyst;
                }
                for (int si = 0; si < chunk.m_7103_().length; ++si) {
                    LevelChunkSection section = chunk.m_7103_()[si];
                    if (section.m_188008_()) continue;
                    int sy = level.m_151568_(si) << 4;
                    for (int y = 0; y < 16; ++y) {
                        for (int x = 0; x < 16; ++x) {
                            for (int z = 0; z < 16; ++z) {
                                BlockPos pos = new BlockPos((cx << 4) + x, sy + y, (cz << 4) + z);
                                if (pos.m_123331_((Vec3i)altar) > (double)r2) continue;
                                BlockState state = section.m_62982_(x, y, z);
                                String p = BuiltInRegistries.f_256975_.m_7981_((Object)state.m_60734_()).m_135815_();
                                if (nether) {
                                    if (p.contains("nether_brick")) {
                                        ++result.netherBricks;
                                    }
                                    if (p.contains("blackstone")) {
                                        ++result.blackstone;
                                    }
                                    if (p.contains("basalt")) {
                                        ++result.basalt;
                                    }
                                    if (p.equals("glowstone")) {
                                        ++result.glowstone;
                                    }
                                    if (p.equals("soul_sand")) {
                                        ++result.soulSand;
                                    }
                                    if (!p.contains("nether_wart")) continue;
                                    ++result.netherWart;
                                    continue;
                                }
                                if (p.contains("copper")) {
                                    ++result.copper;
                                }
                                if (p.equals("chain")) {
                                    ++result.chains;
                                }
                                if (p.equals("iron_block")) {
                                    ++result.ironBlocks;
                                }
                                if (SchoolSupremeRitualRequirements.isRedstone(p)) {
                                    ++result.redstone;
                                }
                                if (p.contains("stone_brick")) {
                                    ++result.stoneBricks;
                                }
                                if (!p.equals("lightning_rod")) continue;
                                ++result.lightningRods;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean isRedstone(String p) {
        return p.contains("redstone") || p.contains("repeater") || p.contains("comparator") || p.contains("piston") || p.contains("observer") || p.contains("dispenser") || p.contains("dropper") || p.contains("target") || p.contains("daylight_detector") || p.contains("tripwire") || p.contains("lever") || p.contains("button") || p.contains("pressure_plate") || p.contains("hopper");
    }

    private static Component missing(String key, int actual, int required) {
        return Component.m_237110_((String)("message.goety_mastery_of_magic.school_supreme.missing." + key), (Object[])new Object[]{required, actual});
    }

    private static Component msg(String key) {
        return Component.m_237115_((String)("message.goety_mastery_of_magic.school_supreme." + key));
    }

    public static enum RitualKind {
        NETHER,
        SKIES_FIRST,
        SKIES_SECOND;

    }

    private record Cached(String key, BlockPos altar, long expiry, Component failure) {
    }

    private static final class Army {
        int netherScore;
        int witherNecromancers;
        int cultists;
        int windCallers;
        int stormCallers;
        int jadeGolems;

        private Army() {
        }
    }

    private static final class Counts {
        int netherLazethyst;
        int skyLazethyst;
        int stormLazethyst;
        int netherBricks;
        int blackstone;
        int basalt;
        int glowstone;
        int soulSand;
        int netherWart;
        int copper;
        int chains;
        int ironBlocks;
        int redstone;
        int stoneBricks;
        int lightningRods;

        private Counts() {
        }
    }
}

