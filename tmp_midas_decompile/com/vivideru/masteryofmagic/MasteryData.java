/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.player.Player
 */
package com.vivideru.masteryofmagic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class MasteryData {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";
    private static final String MASTERIES_KEY = "masteries";
    private static final String WIZARDRY_KEY = "supreme_wizardry";
    private static final String SKY_ALTAR_DIMENSION = "supreme_skies_altar_dimension";
    private static final String SKY_ALTAR_POS = "supreme_skies_altar_pos";
    private static final String SKY_ALTAR_EXPIRY = "supreme_skies_altar_expiry";
    private static final String PLANET_RITUALS = "supreme_planet_rituals";
    private static final String PLANET_SHAPE = "supreme_planet_shape";
    private static final String PLANET_SAVED_SHAPE = "supreme_planet_saved_shape";
    private static final String PLANET_SHAPE_LOCK = "supreme_planet_shape_lock";
    private static final String PLANET_SHAPE_COOLDOWN = "supreme_planet_shape_cooldown";
    private static final String PLANET_SHAPE_GIFT = "supreme_planet_shape_gift";

    private static CompoundTag getMasteriesTag(Player p) {
        CompoundTag persistentData = p.getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        return modData.m_128469_(MASTERIES_KEY);
    }

    private static void saveMasteriesTag(Player p, CompoundTag masteriesData) {
        CompoundTag persistentData = p.getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        modData.m_128365_(MASTERIES_KEY, (Tag)masteriesData);
        persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
    }

    public static int get(Player p, MasteryId id) {
        CompoundTag masteriesData = MasteryData.getMasteriesTag(p);
        return masteriesData.m_128451_(id.key());
    }

    public static void set(Player p, MasteryId id, int level) {
        if (level < 0) {
            level = 0;
        }
        if (level > 3) {
            level = 3;
        }
        CompoundTag masteriesData = MasteryData.getMasteriesTag(p);
        masteriesData.m_128405_(id.key(), level);
        MasteryData.saveMasteriesTag(p, masteriesData);
    }

    public static void clear(Player p, MasteryId id) {
        CompoundTag masteriesData = MasteryData.getMasteriesTag(p);
        masteriesData.m_128405_(id.key(), 0);
        MasteryData.saveMasteriesTag(p, masteriesData);
    }

    public static void resetAll(Player p) {
        CompoundTag masteriesData = MasteryData.getMasteriesTag(p);
        for (MasteryId id : MasteryId.values()) {
            masteriesData.m_128405_(id.key(), 0);
        }
        MasteryData.saveMasteriesTag(p, masteriesData);
    }

    public static void maxAll(Player p) {
        CompoundTag masteriesData = MasteryData.getMasteriesTag(p);
        for (MasteryId id : MasteryId.values()) {
            masteriesData.m_128405_(id.key(), 3);
        }
        MasteryData.saveMasteriesTag(p, masteriesData);
    }

    public static int getWizardry(Player player) {
        return MasteryData.getMasteriesTag(player).m_128451_(WIZARDRY_KEY);
    }

    public static void setWizardry(Player player, int level) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128405_(WIZARDRY_KEY, Math.max(0, Math.min(3, level)));
        MasteryData.saveMasteriesTag(player, data);
    }

    public static boolean hasSupreme(Player player, SupremeSchool mastery) {
        return MasteryData.getMasteriesTag(player).m_128471_(mastery.key());
    }

    public static void setSupreme(Player player, SupremeSchool mastery, boolean value) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128379_(mastery.key(), value);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static void setPendingSkiesAltar(Player player, String dimension, long position, long expiry) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128359_(SKY_ALTAR_DIMENSION, dimension);
        data.m_128356_(SKY_ALTAR_POS, position);
        data.m_128356_(SKY_ALTAR_EXPIRY, expiry);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static long getPendingSkiesAltar(Player player, String dimension, long now) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        return dimension.equals(data.m_128461_(SKY_ALTAR_DIMENSION)) && data.m_128454_(SKY_ALTAR_EXPIRY) >= now ? data.m_128454_(SKY_ALTAR_POS) : Long.MIN_VALUE;
    }

    public static void clearPendingSkiesAltar(Player player) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128473_(SKY_ALTAR_DIMENSION);
        data.m_128473_(SKY_ALTAR_POS);
        data.m_128473_(SKY_ALTAR_EXPIRY);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static int getPlanetRituals(Player player) {
        return MasteryData.getMasteriesTag(player).m_128451_(PLANET_RITUALS);
    }

    public static void completePlanetRitual(Player player, int bit) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128405_(PLANET_RITUALS, data.m_128451_(PLANET_RITUALS) | bit);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static void clearPlanetRituals(Player player) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128473_(PLANET_RITUALS);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static String getPlanetShape(Player player) {
        return MasteryData.getMasteriesTag(player).m_128461_(PLANET_SHAPE);
    }

    public static void setPlanetShape(Player player, String id) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        if (id == null || id.isBlank()) {
            data.m_128473_(PLANET_SHAPE);
        } else {
            data.m_128359_(PLANET_SHAPE, id);
        }
        MasteryData.saveMasteriesTag(player, data);
    }

    public static String getPlanetSavedShape(Player player) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        String saved = data.m_128461_(PLANET_SAVED_SHAPE);
        if (saved.isBlank() && !(saved = data.m_128461_(PLANET_SHAPE)).isBlank()) {
            data.m_128359_(PLANET_SAVED_SHAPE, saved);
            MasteryData.saveMasteriesTag(player, data);
        }
        return saved;
    }

    public static void setPlanetSavedShape(Player player, String id) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        if (id == null || id.isBlank()) {
            data.m_128473_(PLANET_SAVED_SHAPE);
        } else {
            data.m_128359_(PLANET_SAVED_SHAPE, id);
        }
        MasteryData.saveMasteriesTag(player, data);
    }

    public static long getPlanetShapeLock(Player player) {
        return MasteryData.getMasteriesTag(player).m_128454_(PLANET_SHAPE_LOCK);
    }

    public static void setPlanetShapeLock(Player player, long time) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        if (time <= 0L) {
            data.m_128473_(PLANET_SHAPE_LOCK);
        } else {
            data.m_128356_(PLANET_SHAPE_LOCK, time);
        }
        MasteryData.saveMasteriesTag(player, data);
    }

    public static long getPlanetShapeCooldown(Player player) {
        return MasteryData.getMasteriesTag(player).m_128454_(PLANET_SHAPE_COOLDOWN);
    }

    public static void setPlanetShapeCooldown(Player player, long time) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        if (time <= 0L) {
            data.m_128473_(PLANET_SHAPE_COOLDOWN);
        } else {
            data.m_128356_(PLANET_SHAPE_COOLDOWN, time);
        }
        MasteryData.saveMasteriesTag(player, data);
    }

    public static boolean hasPlanetShapeGift(Player player) {
        return MasteryData.getMasteriesTag(player).m_128471_(PLANET_SHAPE_GIFT);
    }

    public static void setPlanetShapeGift(Player player, boolean value) {
        CompoundTag data = MasteryData.getMasteriesTag(player);
        data.m_128379_(PLANET_SHAPE_GIFT, value);
        MasteryData.saveMasteriesTag(player, data);
    }

    public static enum MasteryId {
        NECROMANCY,
        GEOTURGY,
        SKY,
        NETHER,
        END,
        WILD,
        DEEP,
        FROST,
        STORM;


        public String key() {
            return "mastery_" + this.name().toLowerCase();
        }
    }

    public static enum SupremeSchool {
        NETHER("supreme_nether"),
        SKIES("supreme_skies"),
        PLANET("supreme_planet");

        private final String key;

        private SupremeSchool(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
    }
}

