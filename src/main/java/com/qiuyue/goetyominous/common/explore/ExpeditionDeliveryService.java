package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ExpeditionDeliveryService {
    private ExpeditionDeliveryService() {
    }

    public static Component deliver(ServerPlayer player, UUID expeditionId, boolean recall) {
        ExpeditionData data = ExpeditionData.get(player.getServer());
        ExpeditionData.ActiveExpedition active = data.expedition(player.getUUID(), expeditionId);
        if (active == null) {
            return fail("noExpedition");
        }
        ExpeditionDelivery delivery = data.delivery(player.getUUID(), expeditionId);
        if (delivery == null && !recall && !active.settled()) {
            return fail("notFinished");
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return fail("level");
        }
        if (delivery == null) {
            try {
                List<CompoundTag> cats = new ArrayList<>();
                for (CompoundTag snapshot : active.snapshots()) {
                    cats.add(snapshot.copy());
                }
                List<UUID> origins = new ArrayList<>(active.memberUuids());
                while (origins.size() < cats.size()) {
                    origins.add(UUID.randomUUID());
                }
                int lost = active.dead().size();
                if (recall && ExpeditionTuning.fromConfig().recallLosesTravelers()) {
                    List<CompoundTag> keptCats = new ArrayList<>();
                    List<UUID> keptOrigins = new ArrayList<>();
                    for (int i = 0; i < cats.size(); i++) {
                        UUID origin = i < origins.size() ? origins.get(i) : null;
                        if (level.random.nextFloat() < active.deathChance()) {
                            lost++;
                            if (origin != null) {
                                data.markDeadBinding(origin);
                            }
                            continue;
                        }
                        keptCats.add(cats.get(i));
                        keptOrigins.add(origin == null ? UUID.randomUUID() : origin);
                    }
                    cats = keptCats;
                    origins = keptOrigins;
                }
                int rewardCount = 0;
                if (!recall && !cats.isEmpty()) {
                    ServerLevel targetLevel = player.getServer().getLevel(active.dimension());
                    if (targetLevel == null) {
                        targetLevel = level;
                    }
                    ExpeditionManager.ExpeditionRewards rewards =
                            ExpeditionManager.rollRewards(targetLevel, level, active, player);
                    List<Integer> all = new ArrayList<>(cats.size());
                    List<Integer> pickaxes = new ArrayList<>();
                    List<Integer> crossbows = new ArrayList<>();
                    for (int i = 0; i < cats.size(); i++) {
                        all.add(i);
                        if (ExpeditionManager.snapshotHasPickaxe(cats.get(i))) {
                            pickaxes.add(i);
                        }
                        if (ExpeditionManager.snapshotHasCrossbow(cats.get(i))) {
                            crossbows.add(i);
                        }
                    }
                    rewardCount += distribute(cats, all, rewards.structure(), "structure");
                    rewardCount += distribute(cats, pickaxes, rewards.ores(), "ore");
                    rewardCount += distribute(cats, crossbows, rewards.mobs(), "mob");
                }
                delivery = ExpeditionDelivery.create(recall, lost, cats, origins, rewardCount);
                data.putDelivery(player.getUUID(), expeditionId, delivery);
                checkpoint(player, data);
            } catch (RuntimeException | StackOverflowError broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Failed to prepare delivery for expedition {}, original team retained: {}", expeditionId, broken.toString());
                return Component.translatable("message.goetyominous.expedition.deliveryPending");
            }
        }
        checkpoint(player, data);

        List<CompoundTag> travelers = delivery.travelers();
        List<UUID> origins = delivery.origins();
        int index = 0;
        while (index < travelers.size()) {
            CompoundTag tag = travelers.get(index);
            try {
                UUID current;
                if (!tag.contains("UUID")) {
                    current = UUID.randomUUID();
                    tag.putUUID("UUID", current);
                    checkpoint(player, data);
                } else {
                    current = tag.getUUID("UUID");
                }
                Entity existing = ExpeditionManager.findLoaded(player.getServer(), current);
                UUID deliveredId = current;
                if (existing == null) {
                    Entity entity = EntityType.loadEntityRecursive(tag.copy(), level, created -> created);
                    if (entity == null) {
                        GoetyOminous.LOGGER.warn("[goetyominous] Could not restore traveler type {} for expedition {}, snapshot retained.",
                                tag.getString("id"), expeditionId);
                        index++;
                        continue;
                    }
                    int position = delivery.returned();
                    double angle = (double) position * 2.399963229728653D;
                    entity.moveTo(player.getX() + Math.cos(angle) * 2.0D, player.getY(),
                            player.getZ() + Math.sin(angle) * 2.0D, entity.getYRot(), entity.getXRot());
                    if (!level.addFreshEntity(entity)) {
                        GoetyOminous.LOGGER.warn("[goetyominous] World rejected returning traveler {} for expedition {}, snapshot retained with a new UUID.",
                                current, expeditionId);
                        entity.discard();
                        tag.putUUID("UUID", UUID.randomUUID());
                        checkpoint(player, data);
                        index++;
                        continue;
                    }
                    deliveredId = entity.getUUID();
                    if (!deliveredId.equals(current)) {
                        tag.putUUID("UUID", deliveredId);
                        checkpoint(player, data);
                    }
                } else {
                    deliveredId = existing.getUUID();
                }
                UUID origin = index < origins.size() ? origins.get(index) : null;
                if (origin != null && !origin.equals(deliveredId)) {
                    data.addRemap(origin, deliveredId);
                }
                travelers.remove(index);
                if (index < origins.size()) {
                    origins.remove(index);
                }
                delivery.travelerReturned();
                checkpoint(player, data);
            } catch (RuntimeException broken) {
                GoetyOminous.LOGGER.warn("[goetyominous] Traveler delivery for expedition {} failed, snapshot retained: {}", expeditionId, broken.toString());
                index++;
            }
        }

        if (!delivery.complete()) {
            return Component.translatable("message.goetyominous.expedition.deliveryPending");
        }
        data.removeExpedition(player.getUUID(), expeditionId);
        checkpoint(player, data);
        if (delivery.recall()) {
            player.displayClientMessage(Component.translatable("message.goetyominous.expedition.recalled",
                    delivery.returned(), delivery.lost()), false);
        } else {
            player.displayClientMessage(Component.translatable("message.goetyominous.expedition.claimed",
                    delivery.returned(), delivery.lost(), delivery.rewardCount()), false);
            if (delivery.rewardCount() > 0 && delivery.returned() > 0) {
                player.displayClientMessage(Component.translatable("message.goetyominous.expedition.lootCarried"), false);
            }
        }
        return null;
    }

    private static int distribute(List<CompoundTag> cats, List<Integer> indexes, List<ItemStack> rewards, String kind) {
        if (rewards.isEmpty()) {
            return 0;
        }
        if (indexes.isEmpty()) {
            GoetyOminous.LOGGER.warn("[goetyominous] No returning traveler can carry {} rewards; discarded.", kind);
            return 0;
        }
        int count = 0;
        for (int i = 0; i < rewards.size(); i++) {
            ItemStack stack = rewards.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag cat = cats.get(indexes.get(i % indexes.size()));
            ExpeditionLootStorage.append(cat, stack);
            count++;
        }
        return count;
    }

    private static void checkpoint(ServerPlayer player, ExpeditionData data) {
        data.setDirty();
        ExpeditionManager.flush(player.getServer());
    }

    private static Component fail(String key, Object... args) {
        return Component.translatable("screen.goetyominous.expedition.fail." + key, args);
    }
}
