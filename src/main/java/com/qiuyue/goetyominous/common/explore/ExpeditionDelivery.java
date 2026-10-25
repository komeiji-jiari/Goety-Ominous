package com.qiuyue.goetyominous.common.explore;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpeditionDelivery {
    private final boolean recall;
    private final int lost;
    private final List<CompoundTag> travelers = new ArrayList<>();
    private final List<UUID> origins = new ArrayList<>();
    private int returned;
    private final int rewardCount;

    private ExpeditionDelivery(boolean recall, int lost, int rewardCount) {
        this.recall = recall;
        this.lost = lost;
        this.rewardCount = rewardCount;
    }

    public static ExpeditionDelivery create(boolean recall, int lost, List<CompoundTag> snapshots, List<UUID> originUuids, int rewardCount) {
        ExpeditionDelivery delivery = new ExpeditionDelivery(recall, lost, rewardCount);
        for (int i = 0; i < snapshots.size(); i++) {
            CompoundTag tag = snapshots.get(i).copy();
            tag.putUUID("UUID", UUID.randomUUID());
            delivery.travelers.add(tag);
            delivery.origins.add(i < originUuids.size() ? originUuids.get(i) : UUID.randomUUID());
        }
        return delivery;
    }

    public static ExpeditionDelivery load(CompoundTag tag) {
        ExpeditionDelivery delivery = new ExpeditionDelivery(tag.getBoolean("Recall"), tag.getInt("Lost"), tag.getInt("RewardCount"));
        delivery.returned = tag.getInt("Returned");
        ListTag travelerTags = tag.getList("Travelers", Tag.TAG_COMPOUND);
        for (int i = 0; i < travelerTags.size(); i++) {
            delivery.travelers.add(travelerTags.getCompound(i).copy());
        }
        ListTag originTags = tag.getList("Origins", Tag.TAG_STRING);
        for (int i = 0; i < originTags.size(); i++) {
            UUID uuid = parseUuid(originTags.getString(i));
            if (uuid != null) {
                delivery.origins.add(uuid);
            }
        }
        while (delivery.origins.size() < delivery.travelers.size()) {
            delivery.origins.add(UUID.randomUUID());
        }
        return delivery;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Recall", recall);
        tag.putInt("Lost", lost);
        tag.putInt("Returned", returned);
        tag.putInt("RewardCount", rewardCount);
        ListTag travelerTags = new ListTag();
        for (CompoundTag traveler : travelers) {
            travelerTags.add(traveler.copy());
        }
        tag.put("Travelers", travelerTags);
        ListTag originTags = new ListTag();
        for (UUID origin : origins) {
            originTags.add(StringTag.valueOf(origin.toString()));
        }
        tag.put("Origins", originTags);
        return tag;
    }

    public boolean recall() {
        return recall;
    }

    public int lost() {
        return lost;
    }

    public List<CompoundTag> travelers() {
        return travelers;
    }

    public List<UUID> origins() {
        return origins;
    }

    public int returned() {
        return returned;
    }

    public int rewardCount() {
        return rewardCount;
    }

    public void travelerReturned() {
        returned++;
    }

    public boolean complete() {
        return travelers.isEmpty();
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException broken) {
            return null;
        }
    }
}
