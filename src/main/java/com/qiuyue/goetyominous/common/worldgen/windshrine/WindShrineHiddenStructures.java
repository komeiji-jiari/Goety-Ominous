package com.qiuyue.goetyominous.common.worldgen.windshrine;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class WindShrineHiddenStructures {
    private static final List<String> HIDDEN_PREFIXES = List.of("goetyominous:wind_shrine_expansion", "goetyominous:crypt_wing", "goetyominous:crypt_boss", "goetyominous:crypt_vault");

    private WindShrineHiddenStructures() {
    }

    public static boolean isHidden(String id) {
        for (String prefix : HIDDEN_PREFIXES) {
            if (id.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static CompletableFuture<Suggestions> filter(ResourceKey<? extends Registry<?>> registryKey, CompletableFuture<Suggestions> suggestions) {
        if (!registryKey.equals(Registries.STRUCTURE)) {
            return suggestions;
        }
        return suggestions.thenApply(result -> {
            List<Suggestion> kept = result.getList().stream().filter(suggestion -> !isHidden(suggestion.getText())).toList();
            return kept.size() == result.getList().size() ? result : new Suggestions(result.getRange(), kept);
        });
    }
}
