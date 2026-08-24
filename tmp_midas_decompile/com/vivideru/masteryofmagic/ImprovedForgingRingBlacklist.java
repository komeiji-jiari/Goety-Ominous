/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.GoetyMasteryOfMagicMod;
import com.vivideru.masteryofmagic.config.GameplayConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class ImprovedForgingRingBlacklist {
    private static volatile List<String> cachedEntries = List.of();
    private static volatile List<EntryMatcher> cachedMatchers = List.of();

    private ImprovedForgingRingBlacklist() {
    }

    public static boolean matches(ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return false;
        }
        List<EntryMatcher> matchers = ImprovedForgingRingBlacklist.getMatchers();
        if (matchers.isEmpty()) {
            return false;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (itemId == null) {
            return false;
        }
        for (EntryMatcher matcher : matchers) {
            if (!matcher.matches(stack, itemId)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<EntryMatcher> getMatchers() {
        ArrayList<String> configuredEntries = new ArrayList<String>();
        for (String entry : (List)GameplayConfig.IMPROVED_FORGING_RING_BLACKLIST.get()) {
            configuredEntries.add(entry);
        }
        if (configuredEntries.equals(cachedEntries)) {
            return cachedMatchers;
        }
        Class<ImprovedForgingRingBlacklist> clazz = ImprovedForgingRingBlacklist.class;
        synchronized (ImprovedForgingRingBlacklist.class) {
            if (configuredEntries.equals(cachedEntries)) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return cachedMatchers;
            }
            ArrayList<EntryMatcher> parsedMatchers = new ArrayList<EntryMatcher>();
            for (String entry : configuredEntries) {
                EntryMatcher matcher = ImprovedForgingRingBlacklist.parse(entry);
                if (matcher == null) continue;
                parsedMatchers.add(matcher);
            }
            cachedEntries = List.copyOf(configuredEntries);
            cachedMatchers = List.copyOf(parsedMatchers);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return cachedMatchers;
        }
    }

    private static EntryMatcher parse(String rawEntry) {
        if (rawEntry == null) {
            ImprovedForgingRingBlacklist.warnInvalid("null", "entry is null");
            return null;
        }
        String entry = rawEntry.trim();
        if (entry.isEmpty()) {
            ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "entry is empty");
            return null;
        }
        if (entry.charAt(0) == '#') {
            String tagName = entry.substring(1);
            if (tagName.indexOf(42) >= 0) {
                ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "wildcards are not supported in tag names");
                return null;
            }
            ResourceLocation tagId = ResourceLocation.m_135820_((String)tagName);
            if (tagId == null || !tagName.equals(tagId.toString())) {
                ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "invalid item tag ID");
                return null;
            }
            TagKey tag = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)tagId);
            return (stack, itemId) -> stack.m_204117_(tag);
        }
        int separator = entry.indexOf(58);
        if (separator <= 0 || separator != entry.lastIndexOf(58) || separator == entry.length() - 1) {
            ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "expected namespace:path");
            return null;
        }
        String namespace = entry.substring(0, separator);
        String path = entry.substring(separator + 1);
        if (!namespace.matches("[a-z0-9_.-]+")) {
            ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "invalid namespace");
            return null;
        }
        if (!path.matches("[a-z0-9/._*\\-]+")) {
            ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "invalid item path or unsupported wildcard");
            return null;
        }
        if (path.indexOf(42) < 0) {
            ResourceLocation itemId2 = ResourceLocation.m_135820_((String)entry);
            if (itemId2 == null || !entry.equals(itemId2.toString())) {
                ImprovedForgingRingBlacklist.warnInvalid(rawEntry, "invalid item ID");
                return null;
            }
            return (stack, candidateId) -> Objects.equals(itemId2, candidateId);
        }
        Pattern pathPattern = ImprovedForgingRingBlacklist.compilePathWildcard(path);
        return (stack, itemId) -> namespace.equals(itemId.m_135827_()) && pathPattern.matcher(itemId.m_135815_()).matches();
    }

    private static Pattern compilePathWildcard(String path) {
        String[] parts = path.split("\\*", -1);
        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < parts.length; ++i) {
            if (i > 0) {
                regex.append(".*");
            }
            regex.append(Pattern.quote(parts[i]));
        }
        regex.append('$');
        return Pattern.compile(regex.toString());
    }

    private static void warnInvalid(String entry, String reason) {
        GoetyMasteryOfMagicMod.LOGGER.warn("Ignoring invalid improved forging ring blacklist entry '{}': {}", (Object)entry, (Object)reason);
    }

    @FunctionalInterface
    private static interface EntryMatcher {
        public boolean matches(ItemStack var1, ResourceLocation var2);
    }
}

