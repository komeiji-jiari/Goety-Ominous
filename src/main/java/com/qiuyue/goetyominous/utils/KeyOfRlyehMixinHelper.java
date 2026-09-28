package com.qiuyue.goetyominous.utils;

import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class KeyOfRlyehMixinHelper {

    private static final int DEEP_ONE_REPUTATION_MAX = 100;
    private static final int REPUTATION_CHECK_INTERVAL = 20;

    private KeyOfRlyehMixinHelper() {
    }

    public static boolean isKeyOfRlyeh(ItemStack staff) {
        return !staff.isEmpty() && staff.is(AcItems.KEY_OF_RLYEH.get());
    }

    public static void applyDeepOneReputation(Level level, Entity entity) {
        if (level.isClientSide || !(entity instanceof Player player) || player.tickCount % REPUTATION_CHECK_INTERVAL != 0) {
            return;
        }
        ACWorldData worldData = ACWorldData.get(level);
        if (worldData == null) {
            return;
        }
        int current = worldData.getDeepOneReputation(player.getUUID());
        if (current >= DEEP_ONE_REPUTATION_MAX) {
            return;
        }
        worldData.setDeepOneReputation(player.getUUID(), DEEP_ONE_REPUTATION_MAX);
        if (DeepOneReaction.fromReputation(current) != DeepOneReaction.HELPFUL) {
            ACAdvancementTriggerRegistry.DEEP_ONE_HELPFUL.triggerForEntity(player);
            player.displayClientMessage(Component.translatable("entity.alexscaves.deep_one.reaction_helpful"), true);
        }
    }
}