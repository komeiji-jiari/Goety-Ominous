/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.IChargingSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.SpellRingHelper;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class CSpellRingCastPacket {
    private final int slot;
    private final boolean down;

    public CSpellRingCastPacket(int slot, boolean down) {
        this.slot = slot;
        this.down = down;
    }

    public static void encode(CSpellRingCastPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.slot);
        buffer.writeBoolean(packet.down);
    }

    public static CSpellRingCastPacket decode(FriendlyByteBuf buffer) {
        return new CSpellRingCastPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void consume(CSpellRingCastPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int castDuration;
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            ItemStack ring = SpellRingHelper.findSpellRing((Player)player);
            if (ring.m_41619_()) {
                return;
            }
            Item patt1598$temp = ring.m_41720_();
            if (!(patt1598$temp instanceof DarkWand)) {
                return;
            }
            DarkWand wand = (DarkWand)patt1598$temp;
            Level patt1687$temp = player.m_9236_();
            if (!(patt1687$temp instanceof ServerLevel)) {
                return;
            }
            ServerLevel serverLevel = (ServerLevel)patt1687$temp;
            SpellRingHelper.setActiveSlot(ring, packet.slot);
            ISpell spell = wand.getSpell(ring);
            if (spell == null) {
                return;
            }
            CompoundTag tag = ring.m_41784_();
            String castingKey = "SpellRingCasting" + packet.slot;
            String castKey = "SpellRingCastTime" + packet.slot;
            String intervalKey = "SpellRingInterval" + packet.slot;
            String shotsKey = "SpellRingShots" + packet.slot;
            if (!packet.down) {
                if (tag.m_128471_(castingKey)) {
                    SpellRingHelper.releaseSlot(player, ring, wand, spell, packet.slot);
                }
                tag.m_128379_(castingKey, false);
                tag.m_128405_(castKey, 0);
                tag.m_128405_(intervalKey, 0);
                tag.m_128405_(shotsKey, 0);
                tag.m_128405_("Shots", 0);
                return;
            }
            if (!tag.m_128471_(castingKey)) {
                if (SpellRingHelper.isSlotOnCooldown(ring, packet.slot)) {
                    return;
                }
                if (!spell.conditionsMet(serverLevel, (LivingEntity)player)) {
                    return;
                }
                tag.m_128379_(castingKey, true);
                tag.m_128405_(castKey, 0);
                tag.m_128405_(intervalKey, 0);
                tag.m_128405_(shotsKey, 0);
                tag.m_128405_("Shots", 0);
                spell.startSpell(serverLevel, (LivingEntity)player, ring, WandUtil.getStats((LivingEntity)player, (ISpell)spell));
            }
            int castTime = tag.m_128451_(castKey) + 1;
            tag.m_128405_(castKey, castTime);
            tag.m_128405_("Soul Cost", spell.soulCost((LivingEntity)player, ring));
            tag.m_128405_("Duration", spell.castDuration((LivingEntity)player, ring));
            if (!(spell instanceof IChargingSpell) && (castDuration = spell.castDuration((LivingEntity)player, ring)) <= 0) {
                wand.MagicResults(ring, (Level)serverLevel, (LivingEntity)player, spell);
                SpellRingHelper.setSlotCooldown(ring, packet.slot, spell.spellCooldown((LivingEntity)player));
                tag.m_128379_(castingKey, false);
                tag.m_128405_(castKey, 0);
                tag.m_128405_(intervalKey, 0);
                tag.m_128405_(shotsKey, 0);
                tag.m_128405_("Shots", 0);
                return;
            }
            spell.useSpell(serverLevel, (LivingEntity)player, ring, castTime, WandUtil.getStats((LivingEntity)player, (ISpell)spell));
            if (spell instanceof IChargingSpell) {
                IChargingSpell chargingSpell = (IChargingSpell)spell;
                int castUp = chargingSpell.castUp((LivingEntity)player, ring);
                if (castUp > 0 && castTime < castUp) {
                    return;
                }
                int shots = tag.m_128451_(shotsKey);
                tag.m_128405_("Shots", shots);
                int interval = chargingSpell.Cooldown((LivingEntity)player, ring, shots);
                if (interval <= 0) {
                    interval = 1;
                }
                int currentInterval = tag.m_128451_(intervalKey) + 1;
                tag.m_128405_(intervalKey, currentInterval);
                tag.m_128405_("Cooldown", interval);
                if (currentInterval < interval) {
                    return;
                }
                tag.m_128405_(intervalKey, 0);
                if (chargingSpell.shotsNumber((LivingEntity)player, ring) > 0) {
                    tag.m_128405_(shotsKey, ++shots);
                    tag.m_128405_("Shots", shots);
                }
                wand.MagicResults(ring, (Level)serverLevel, (LivingEntity)player, spell);
                int maxShots = chargingSpell.shotsNumber((LivingEntity)player, ring);
                if (maxShots > 0 && shots >= maxShots) {
                    SpellRingHelper.setSlotCooldown(ring, packet.slot, chargingSpell.spellCooldown((LivingEntity)player));
                    tag.m_128379_(castingKey, false);
                    tag.m_128405_(castKey, 0);
                    tag.m_128405_(intervalKey, 0);
                    tag.m_128405_(shotsKey, 0);
                    tag.m_128405_("Shots", 0);
                    tag.m_128405_("Cool", 0);
                }
                return;
            }
            castDuration = spell.castDuration((LivingEntity)player, ring);
            if (castDuration <= 0 || castTime >= castDuration) {
                wand.MagicResults(ring, (Level)serverLevel, (LivingEntity)player, spell);
                SpellRingHelper.setSlotCooldown(ring, packet.slot, spell.spellCooldown((LivingEntity)player));
                tag.m_128379_(castingKey, false);
                tag.m_128405_(castKey, 0);
                tag.m_128405_(intervalKey, 0);
                tag.m_128405_(shotsKey, 0);
                tag.m_128405_("Shots", 0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

