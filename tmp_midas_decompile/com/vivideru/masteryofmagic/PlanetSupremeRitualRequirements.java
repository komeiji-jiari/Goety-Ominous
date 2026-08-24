/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.phys.AABB
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.vivideru.masteryofmagic.MasteryData;
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
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

public final class PlanetSupremeRitualRequirements {
    private static final int ARMY_RADIUS = 96;
    private static final Map<String, Integer> WILD = Map.ofEntries(Map.entry("jungle_zombie_servant", 3), Map.entry("mossy_skeleton_servant", 3), Map.entry("muck_wraith_servant", 8), Map.entry("mossy_necromancer_servant", 17), Map.entry("slime_servant", 2), Map.entry("crypt_slime_servant", 5), Map.entry("spider_servant", 1), Map.entry("cave_spider_servant", 3), Map.entry("web_spider_servant", 5), Map.entry("black_wolf", 3), Map.entry("bear_servant", 5), Map.entry("snapper", 3), Map.entry("gnasher", 5), Map.entry("carrion_fly", 3), Map.entry("carrion_maggot", 2), Map.entry("black_beast", 23), Map.entry("whisperer", 8), Map.entry("leapleaf", 8), Map.entry("brood_mother_servant", 23), Map.entry("ripper_servant", 17), Map.entry("warg", 17));
    private static final Map<String, Integer> DEEP = Map.ofEntries(Map.entry("drowned_servant", 2), Map.entry("sunken_skeleton_servant", 5), Map.entry("drowned_necromancer_servant", 23), Map.entry("guardian_servant", 8), Map.entry("elder_guardian_servant", 31), Map.entry("tropical_slime_servant", 5), Map.entry("wavewhisperer", 12), Map.entry("quick_growing_kelp", 2), Map.entry("poison_anemone", 5));
    private static final Map<UUID, Cached> CACHE = new ConcurrentHashMap<UUID, Cached>();

    private PlanetSupremeRitualRequirements() {
    }

    public static Component validate(ServerLevel level, BlockPos altar, Player player, Kind kind) {
        Cached c = CACHE.get(player.m_20148_());
        long now = level.m_46467_();
        if (c != null && c.kind == kind && c.pos.equals((Object)altar) && c.expiry >= now) {
            return c.failure;
        }
        Component failure = PlanetSupremeRitualRequirements.validateNow(level, altar, player, kind);
        CACHE.put(player.m_20148_(), new Cached(kind, altar.m_7949_(), now + 40L, failure));
        return failure;
    }

    private static Component validateNow(ServerLevel level, BlockPos altar, Player player, Kind kind) {
        if (MasteryData.hasSupreme(player, MasteryData.SupremeSchool.PLANET)) {
            return PlanetSupremeRitualRequirements.msg("already_mastered");
        }
        if (kind == Kind.WILD && MasteryData.get(player, MasteryData.MasteryId.WILD) < 3) {
            return PlanetSupremeRitualRequirements.msg("wild_mastery");
        }
        if (kind == Kind.GEOMANCY && MasteryData.get(player, MasteryData.MasteryId.GEOTURGY) < 3) {
            return PlanetSupremeRitualRequirements.msg("geomancy_mastery");
        }
        if (kind == Kind.DEEP && MasteryData.get(player, MasteryData.MasteryId.DEEP) < 3) {
            return PlanetSupremeRitualRequirements.msg("deep_mastery");
        }
        if ((MasteryData.getPlanetRituals(player) & kind.bit) != 0) {
            return PlanetSupremeRitualRequirements.msg("planet_rite_complete");
        }
        if (kind == Kind.WILD && !PlanetSupremeRitualRequirements.inside(level, altar, "minecraft", "jungle_pyramid")) {
            return PlanetSupremeRitualRequirements.msg("jungle_temple");
        }
        if (kind == Kind.DEEP && !PlanetSupremeRitualRequirements.inside(level, altar, "minecraft", "monument")) {
            return PlanetSupremeRitualRequirements.msg("ocean_monument");
        }
        if (kind == Kind.GEOMANCY) {
            if (level.m_45527_(altar.m_7494_()) || altar.m_123342_() > level.m_5736_() - 5) {
                return PlanetSupremeRitualRequirements.msg("underground");
            }
            int totems = 0;
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int x = -16; x <= 16; ++x) {
                for (int y = -16; y <= 16; ++y) {
                    for (int z = -16; z <= 16; ++z) {
                        if (x * x + y * y + z * z > 256) continue;
                        cursor.m_122154_((Vec3i)altar, x, y, z);
                        if (!BuiltInRegistries.f_256975_.m_7981_((Object)level.m_8055_((BlockPos)cursor).m_60734_()).m_135815_().equals("creeper_totem")) continue;
                        ++totems;
                    }
                }
            }
            if (totems < 40) {
                return PlanetSupremeRitualRequirements.missing("creeper_totems", totems, 40);
            }
        }
        Army army = new Army();
        for (LivingEntity entity : level.m_45976_(LivingEntity.class, new AABB(altar).m_82400_(96.0))) {
            if (entity.m_20275_((double)altar.m_123341_() + 0.5, (double)altar.m_123342_() + 0.5, (double)altar.m_123343_() + 0.5) > 9216.0 || !(entity instanceof IOwned)) continue;
            IOwned owned = (IOwned)entity;
            if (!player.m_20148_().equals(owned.getOwnerId())) continue;
            String path = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_()).m_135815_();
            if (kind == Kind.WILD) {
                army.wild += WILD.getOrDefault(path, 0).intValue();
                if (path.equals("black_beast")) {
                    ++army.blackBeasts;
                }
                if (!path.equals("bear_servant")) continue;
                ++army.bears;
                continue;
            }
            if (kind == Kind.GEOMANCY) {
                if (!path.equals("geomancer_servant") && !path.equals("bound_geomancer")) continue;
                ++army.geomancers;
                continue;
            }
            army.deep += DEEP.getOrDefault(path, 0).intValue();
            if (!path.equals("drowned_necromancer_servant")) continue;
            ++army.drownedNecromancers;
        }
        if (kind == Kind.WILD) {
            if (army.wild < 100) {
                return PlanetSupremeRitualRequirements.missing("wild_servant_score", army.wild, 100);
            }
            if (army.blackBeasts < 1) {
                return PlanetSupremeRitualRequirements.missing("black_beasts", army.blackBeasts, 1);
            }
            if (army.bears < 2) {
                return PlanetSupremeRitualRequirements.missing("bears", army.bears, 2);
            }
        } else if (kind == Kind.GEOMANCY) {
            if (army.geomancers < 10) {
                return PlanetSupremeRitualRequirements.missing("geomancers", army.geomancers, 10);
            }
        } else {
            if (army.deep < 50) {
                return PlanetSupremeRitualRequirements.missing("deep_servant_score", army.deep, 50);
            }
            if (army.drownedNecromancers < 1) {
                return PlanetSupremeRitualRequirements.missing("drowned_necromancers", army.drownedNecromancers, 1);
            }
        }
        return null;
    }

    private static boolean inside(ServerLevel level, BlockPos pos, String namespace, String path) {
        Structure structure = (Structure)level.m_9598_().m_175515_(Registries.f_256944_).m_7745_(new ResourceLocation(namespace, path));
        return structure != null && level.m_215010_().m_220524_(pos, structure).m_73603_();
    }

    private static Component missing(String key, int actual, int required) {
        return Component.m_237110_((String)("message.goety_mastery_of_magic.school_supreme.missing." + key), (Object[])new Object[]{required, actual});
    }

    private static Component msg(String key) {
        return Component.m_237115_((String)("message.goety_mastery_of_magic.school_supreme." + key));
    }

    private record Cached(Kind kind, BlockPos pos, long expiry, Component failure) {
    }

    public static enum Kind {
        WILD(1),
        GEOMANCY(2),
        DEEP(4);

        public final int bit;

        private Kind(int bit) {
            this.bit = bit;
        }
    }

    private static final class Army {
        int wild;
        int deep;
        int geomancers;
        int blackBeasts;
        int bears;
        int drownedNecromancers;

        private Army() {
        }
    }
}

