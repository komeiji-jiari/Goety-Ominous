/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class UndeadBloodBucketItem
extends Item {
    public static final String TAG_SOURCE_UUID = "SourceUUID";
    public static final String TAG_SOURCE_NAME = "SourceName";

    public UndeadBloodBucketItem() {
        super(new Item.Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON));
    }

    public static void setSource(ItemStack stack, UUID uuid, String playerName) {
        CompoundTag tag = stack.m_41784_();
        tag.m_128362_(TAG_SOURCE_UUID, uuid);
        tag.m_128359_(TAG_SOURCE_NAME, playerName);
    }

    public static boolean hasSource(ItemStack stack) {
        return stack.m_41782_() && stack.m_41783_().m_128403_(TAG_SOURCE_UUID);
    }

    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.m_7373_(stack, level, tooltip, flag);
        if (!stack.m_41782_()) {
            return;
        }
        CompoundTag tag = stack.m_41783_();
        String ownerName = null;
        if (tag.m_128425_(TAG_SOURCE_NAME, 8)) {
            ownerName = tag.m_128461_(TAG_SOURCE_NAME);
        } else if (tag.m_128403_(TAG_SOURCE_UUID) && level != null) {
            try {
                MinecraftServer server = level.m_7654_();
                if (server != null) {
                    UUID uuid = tag.m_128342_(TAG_SOURCE_UUID);
                    Optional profile = server.m_129927_().m_11002_(uuid);
                    if (profile.isPresent()) {
                        ownerName = ((GameProfile)profile.get()).getName();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (ownerName != null && !ownerName.isEmpty()) {
            tooltip.add((Component)Component.m_237113_((String)("Blood of " + ownerName)));
        } else {
            tooltip.add((Component)Component.m_237113_((String)"Blood of an unknown source"));
        }
    }
}

