/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LecternBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.phys.AABB
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.vivideru.masteryofmagic.GoetyMasteryOfMagicMod;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.config.SupremeMasteryConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;

public final class SupremeRitualRequirements {
    private static final Map<UUID, CachedValidation> CACHE = new ConcurrentHashMap<UUID, CachedValidation>();
    private static final Map<Class<?>, Boolean> CUSTOM_ENCHANT_POWER_CLASSES = new ConcurrentHashMap();

    private SupremeRitualRequirements() {
    }

    public static Component validate(ServerLevel level, BlockPos altar, Player player, int targetLevel) {
        CachedValidation cached = CACHE.get(player.m_20148_());
        if (cached != null && cached.expiresAt >= level.m_46467_() && cached.level == targetLevel && cached.altar.equals((Object)altar)) {
            return cached.failure;
        }
        Component failure = SupremeRitualRequirements.validateUncached(level, altar, player, targetLevel);
        CACHE.put(player.m_20148_(), new CachedValidation(altar.m_7949_(), targetLevel, level.m_46467_() + 20L, failure));
        return failure;
    }

    private static Component validateUncached(ServerLevel level, BlockPos altar, Player player, int targetLevel) {
        if (!((Boolean)SupremeMasteryConfig.ENABLED.get()).booleanValue()) {
            return Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_ritual.disabled");
        }
        if (MasteryData.getWizardry(player) != targetLevel - 1) {
            return Component.m_237110_((String)"message.goety_mastery_of_magic.wizardry_wrong_level", (Object[])new Object[]{targetLevel - 1});
        }
        int radius = (Integer)SupremeMasteryConfig.RITUAL_RADIUS.get();
        for (int cx = altar.m_123341_() - radius >> 4; cx <= altar.m_123341_() + radius >> 4; ++cx) {
            for (int cz = altar.m_123343_() - radius >> 4; cz <= altar.m_123343_() + radius >> 4; ++cz) {
                level.m_6325_(cx, cz);
            }
        }
        Map<ResourceLocation, Integer> servantScores = SupremeRitualRequirements.parseScores((Iterable)SupremeMasteryConfig.SERVANT_SCORES.get());
        Set<ResourceLocation> magicTypes = SupremeRitualRequirements.parseIds((Iterable)SupremeMasteryConfig.MAGIC_SERVANTS.get());
        HashMap<ResourceLocation, Integer> servantCounts = new HashMap<ResourceLocation, Integer>();
        int evokers = 0;
        int magic = 0;
        AABB area = new AABB(altar).m_82400_((double)radius);
        for (LivingEntity entity : level.m_45976_(LivingEntity.class, area)) {
            IOwned owned;
            if (!(entity instanceof IOwned) || (owned = (IOwned)entity).getOwnerId() == null || !owned.getOwnerId().equals(player.m_20148_())) continue;
            ResourceLocation id = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_());
            if (servantScores.getOrDefault(id, 0) > 0) {
                servantCounts.merge(id, 1, Integer::sum);
            }
            if ("evoker_servant".equals(id.m_135815_())) {
                ++evokers;
            }
            if (!magicTypes.contains(id)) continue;
            ++magic;
        }
        int score = SupremeRitualRequirements.calculateServantScore(servantScores, servantCounts);
        Counts counts = SupremeRitualRequirements.scanBlocks(level, altar, radius);
        int required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.SERVANT_SCORE_REQUIRED, targetLevel);
        if (score < required) {
            return SupremeRitualRequirements.missing("servant_score", score, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.EVOKERS_REQUIRED, targetLevel);
        if (evokers < required) {
            return SupremeRitualRequirements.missing("evokers", evokers, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.MAGIC_SERVANTS_REQUIRED, targetLevel);
        if (magic < required) {
            return SupremeRitualRequirements.missing("magic_servants", magic, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.ENCHANTING_POWER_REQUIRED, targetLevel);
        if (counts.enchantingPower < required) {
            return SupremeRitualRequirements.missing("enchanting_power", counts.enchantingPower, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.LECTERNS_REQUIRED, targetLevel);
        if (counts.lecterns < required) {
            return SupremeRitualRequirements.missing("lecterns", counts.lecterns, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.CHARGED_LAZETHYST_REQUIRED, targetLevel);
        if (counts.lazethyst < required) {
            return SupremeRitualRequirements.missing("charged_lazethyst", counts.lazethyst, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.OMINOUS_LAZETHYST_REQUIRED, targetLevel);
        if (counts.ominous < required) {
            return SupremeRitualRequirements.missing("ominous_lazethyst", counts.ominous, required);
        }
        required = SupremeMasteryConfig.forLevel(SupremeMasteryConfig.MAGIC_VESSELS_REQUIRED, targetLevel);
        if (counts.vessels < required) {
            return SupremeRitualRequirements.missing("magic_vessels", counts.vessels, required);
        }
        if (targetLevel >= 3 && !counts.fullBeacon) {
            return Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_ritual.missing.beacon");
        }
        return null;
    }

    private static Counts scanBlocks(ServerLevel level, BlockPos altar, int radius) {
        Counts result = new Counts();
        Map<ResourceLocation, Integer> power = SupremeRitualRequirements.parseScores((Iterable)SupremeMasteryConfig.ENCHANTING_POWER_BLOCKS.get());
        Set<ResourceLocation> vessels = SupremeRitualRequirements.parseIds((Iterable)SupremeMasteryConfig.MAGIC_VESSEL_BLOCKS.get());
        int radiusSq = radius * radius;
        for (int cx = altar.m_123341_() - radius >> 4; cx <= altar.m_123341_() + radius >> 4; ++cx) {
            for (int cz = altar.m_123343_() - radius >> 4; cz <= altar.m_123343_() + radius >> 4; ++cz) {
                LevelChunk chunk = level.m_6325_(cx, cz);
                for (Map.Entry entry : chunk.m_62954_().entrySet()) {
                    ChargedRunedLazethystBlockEntity lazethyst;
                    Object v;
                    BlockPos pos = (BlockPos)entry.getKey();
                    if (pos.m_123331_((Vec3i)altar) > (double)radiusSq) continue;
                    BlockState state2 = level.m_8055_(pos);
                    ResourceLocation id = BuiltInRegistries.f_256975_.m_7981_((Object)state2.m_60734_());
                    if (state2.m_60713_(Blocks.f_50624_) && ((Boolean)state2.m_61143_((Property)LecternBlock.f_54467_)).booleanValue()) {
                        ++result.lecterns;
                    }
                    if (vessels.contains(id)) {
                        ++result.vessels;
                    }
                    if ((v = entry.getValue()) instanceof ChargedRunedLazethystBlockEntity && (lazethyst = (ChargedRunedLazethystBlockEntity)((Object)v)).hasStoredSpell()) {
                        ++result.lazethyst;
                        if (id.m_135815_().contains("ominous")) {
                            ++result.ominous;
                        }
                    }
                    if (!state2.m_60713_(Blocks.f_50273_) || !SupremeRitualRequirements.isFullBeacon(level, pos)) continue;
                    result.fullBeacon = true;
                }
                LevelChunkSection[] sections = chunk.m_7103_();
                for (int sectionIndex = 0; sectionIndex < sections.length; ++sectionIndex) {
                    LevelChunkSection section = sections[sectionIndex];
                    if (section.m_188008_() || !section.m_63002_(state -> SupremeRitualRequirements.mayProvideEnchantingPower(state, level, altar, power))) continue;
                    int sectionY = level.m_151568_(sectionIndex) << 4;
                    for (int y = 0; y < 16; ++y) {
                        for (int x = 0; x < 16; ++x) {
                            for (int z = 0; z < 16; ++z) {
                                BlockPos pos = new BlockPos((cx << 4) + x, sectionY + y, (cz << 4) + z);
                                if (pos.m_123331_((Vec3i)altar) > (double)radiusSq) continue;
                                BlockState state3 = section.m_62982_(x, y, z);
                                ResourceLocation id = BuiltInRegistries.f_256975_.m_7981_((Object)state3.m_60734_());
                                int bonus = (int)state3.getEnchantPowerBonus((LevelReader)level, pos);
                                result.enchantingPower = result.enchantingPower + Math.max(0, bonus > 0 ? bonus : power.getOrDefault(id, 0));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean mayProvideEnchantingPower(BlockState state, ServerLevel level, BlockPos sample, Map<ResourceLocation, Integer> fallbackPower) {
        ResourceLocation id = BuiltInRegistries.f_256975_.m_7981_((Object)state.m_60734_());
        if (fallbackPower.containsKey(id)) {
            return true;
        }
        try {
            if (state.getEnchantPowerBonus((LevelReader)level, sample) > 0.0f) {
                return true;
            }
        }
        catch (RuntimeException exception) {
            return true;
        }
        return CUSTOM_ENCHANT_POWER_CLASSES.computeIfAbsent(state.m_60734_().getClass(), blockClass -> {
            try {
                Class<?> owner = blockClass.getMethod("getEnchantPowerBonus", BlockState.class, LevelReader.class, BlockPos.class).getDeclaringClass();
                return owner != Block.class && !owner.isInterface();
            }
            catch (ReflectiveOperationException exception) {
                return false;
            }
        });
    }

    private static boolean isFullBeacon(ServerLevel level, BlockPos beacon) {
        for (int layer = 1; layer <= 4; ++layer) {
            for (int x = -layer; x <= layer; ++x) {
                for (int z = -layer; z <= layer; ++z) {
                    BlockState state = level.m_8055_(beacon.m_7918_(x, -layer, z));
                    if (state.m_60713_(Blocks.f_50268_) || state.m_60713_(Blocks.f_50090_) || state.m_60713_(Blocks.f_50721_)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private static Map<ResourceLocation, Integer> parseScores(Iterable<? extends String> entries) {
        HashMap<ResourceLocation, Integer> values = new HashMap<ResourceLocation, Integer>();
        for (String string : entries) {
            try {
                int split = string.lastIndexOf(61);
                values.put(new ResourceLocation(string.substring(0, split)), Integer.parseInt(string.substring(split + 1)));
            }
            catch (RuntimeException ex) {
                GoetyMasteryOfMagicMod.LOGGER.warn("Ignoring invalid Supreme Mastery score entry: {}", (Object)string);
            }
        }
        return values;
    }

    private static int calculateServantScore(Map<ResourceLocation, Integer> baseScores, Map<ResourceLocation, Integer> servantCounts) {
        long total = 0L;
        boolean useVariety = (Boolean)SupremeMasteryConfig.SERVANT_VARIETY_ENABLED.get();
        int fullValueCopies = (Integer)SupremeMasteryConfig.SERVANT_FULL_VALUE_COPIES.get();
        double decay = (Double)SupremeMasteryConfig.SERVANT_DUPLICATE_SCORE_DECAY.get();
        for (Map.Entry<ResourceLocation, Integer> entry : servantCounts.entrySet()) {
            int baseScore = Math.max(0, baseScores.getOrDefault(entry.getKey(), 0));
            int count = Math.max(0, entry.getValue());
            if (baseScore == 0 || count == 0) continue;
            if (!useVariety) {
                total += (long)baseScore * (long)count;
            } else {
                int fullCopies = Math.min(count, fullValueCopies);
                double typeScore = (double)baseScore * (double)fullCopies;
                double multiplier = decay;
                for (int copy = fullCopies; copy < count && multiplier > 1.0E-7; multiplier *= decay, ++copy) {
                    typeScore += (double)baseScore * multiplier;
                }
                total += (long)Math.floor(typeScore + 1.0E-7);
            }
            if (total < Integer.MAX_VALUE) continue;
            return Integer.MAX_VALUE;
        }
        return (int)total;
    }

    private static Set<ResourceLocation> parseIds(Iterable<? extends String> entries) {
        HashSet<ResourceLocation> values = new HashSet<ResourceLocation>();
        for (String string : entries) {
            try {
                values.add(new ResourceLocation(string));
            }
            catch (RuntimeException ex) {
                GoetyMasteryOfMagicMod.LOGGER.warn("Ignoring invalid Supreme Mastery id: {}", (Object)string);
            }
        }
        return values;
    }

    private static Component missing(String requirement, int actual, int required) {
        return Component.m_237110_((String)("message.goety_mastery_of_magic.supreme_ritual.missing." + requirement), (Object[])new Object[]{required, actual});
    }

    private record CachedValidation(BlockPos altar, int level, long expiresAt, Component failure) {
    }

    private static final class Counts {
        int enchantingPower;
        int lecterns;
        int lazethyst;
        int ominous;
        int vessels;
        boolean fullBeacon;

        private Counts() {
        }
    }
}

