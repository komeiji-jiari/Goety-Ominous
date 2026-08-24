/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.api.magic.IChargingSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  top.theillusivec4.curios.api.CuriosApi
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.capability.SpellRingItemHandler;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import top.theillusivec4.curios.api.CuriosApi;

public class SpellRingHelper {
    public static ItemStack findSpellRing(Player player) {
        if (player.m_21205_().m_150930_((Item)GoetyMasteryOfMagicModItems.SPELL_RING.get())) {
            return player.m_21205_();
        }
        if (player.m_21206_().m_150930_((Item)GoetyMasteryOfMagicModItems.SPELL_RING.get())) {
            return player.m_21206_();
        }
        AtomicReference<ItemStack> result = new AtomicReference<ItemStack>(ItemStack.f_41583_);
        CuriosApi.getCuriosInventory((LivingEntity)player).ifPresent(handler -> handler.findFirstCurio(stack -> stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SPELL_RING.get())).ifPresent(slotResult -> result.set(slotResult.stack())));
        return result.get();
    }

    public static void setActiveSlot(ItemStack ring, int slot) {
        ring.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof SpellRingItemHandler) {
                SpellRingItemHandler spellRingHandler = (SpellRingItemHandler)((Object)handler);
                spellRingHandler.setActiveSlot(slot);
            }
        });
    }

    public static ItemStack getFocus(ItemStack ring, int slot) {
        AtomicReference<ItemStack> result = new AtomicReference<ItemStack>(ItemStack.f_41583_);
        ring.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof SpellRingItemHandler) {
                SpellRingItemHandler spellRingHandler = (SpellRingItemHandler)((Object)handler);
                if (slot >= 0 && slot < spellRingHandler.getSlots()) {
                    result.set(spellRingHandler.getStackInSlot(slot));
                }
            }
        });
        return result.get();
    }

    public static ItemStack getActiveFocus(ItemStack ring) {
        AtomicReference<ItemStack> result = new AtomicReference<ItemStack>(ItemStack.f_41583_);
        ring.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            if (handler instanceof SpellRingItemHandler) {
                SpellRingItemHandler spellRingHandler = (SpellRingItemHandler)((Object)handler);
                result.set(spellRingHandler.getSlot());
            }
        });
        return result.get();
    }

    public static void tickCooldowns(ItemStack ring) {
        CompoundTag tag = ring.m_41784_();
        for (int i = 0; i < 3; ++i) {
            String key = "SpellRingCooldown" + i;
            int cooldown = tag.m_128451_(key);
            if (cooldown <= 0) continue;
            tag.m_128405_(key, cooldown - 1);
        }
    }

    public static boolean isSlotOnCooldown(ItemStack ring, int slot) {
        return ring.m_41784_().m_128451_("SpellRingCooldown" + slot) > 0;
    }

    public static void setSlotCooldown(ItemStack ring, int slot, int cooldown) {
        ring.m_41784_().m_128405_("SpellRingCooldown" + slot, Math.max(0, cooldown));
    }

    public static void releaseSlot(ServerPlayer player, ItemStack ring, DarkWand wand, ISpell spell, int slot) {
        CompoundTag tag = ring.m_41784_();
        String castKey = "SpellRingCastTime" + slot;
        String intervalKey = "SpellRingInterval" + slot;
        String shotsKey = "SpellRingShots" + slot;
        String castingKey = "SpellRingCasting" + slot;
        int castTime = tag.m_128451_(castKey);
        System.out.println("=== RELEASE SLOT DEBUG ===");
        System.out.println("slot: " + slot);
        System.out.println("spell: " + spell.getClass().getName());
        System.out.println("castTime: " + castTime);
        if (castTime <= 0) {
            System.out.println("castTime <= 0, abort");
            return;
        }
        int shots = tag.m_128451_(shotsKey);
        System.out.println("shots: " + shots);
        tag.m_128405_("Shots", shots);
        System.out.println("Calling stopSpell...");
        spell.stopSpell(player.m_284548_(), (LivingEntity)player, ring, SpellRingHelper.getFocus(ring, slot), castTime, WandUtil.getStats((LivingEntity)player, (ISpell)spell));
        System.out.println("stopSpell called");
        if (spell instanceof IChargingSpell) {
            IChargingSpell chargingSpell = (IChargingSpell)spell;
            System.out.println("charging spell");
            int cooldown = Mth.m_14143_((float)chargingSpell.spellCooldown((LivingEntity)player));
            System.out.println("cooldown: " + cooldown);
            SpellRingHelper.setSlotCooldown(ring, slot, cooldown);
        } else {
            System.out.println("normal spell");
            int cooldown = spell.spellCooldown((LivingEntity)player);
            System.out.println("cooldown: " + cooldown);
            SpellRingHelper.setSlotCooldown(ring, slot, cooldown);
        }
        tag.m_128379_(castingKey, false);
        tag.m_128405_(castKey, 0);
        tag.m_128405_(intervalKey, 0);
        tag.m_128405_(shotsKey, 0);
        tag.m_128405_("Shots", 0);
        System.out.println("release completed");
    }

    public static boolean isCastingAnySlot(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        Player player = (Player)entity;
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (ring.m_41619_()) {
            return false;
        }
        CompoundTag tag = ring.m_41784_();
        for (int i = 0; i < 3; ++i) {
            if (!tag.m_128471_("SpellRingCasting" + i)) continue;
            return true;
        }
        return false;
    }

    public static int getCastingSlot(ItemStack ring) {
        CompoundTag tag = ring.m_41784_();
        for (int i = 0; i < 3; ++i) {
            if (!tag.m_128471_("SpellRingCasting" + i)) continue;
            return i;
        }
        return -1;
    }

    public static int getSlotCastTime(ItemStack ring, int slot) {
        return ring.m_41784_().m_128451_("SpellRingCastTime" + slot);
    }

    public static int getSlotShots(ItemStack ring, int slot) {
        return ring.m_41784_().m_128451_("SpellRingShots" + slot);
    }

    public static boolean isCastingSpell(LivingEntity entity, Class<?> spellClass) {
        if (!(entity instanceof Player)) {
            return false;
        }
        Player player = (Player)entity;
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (ring.m_41619_()) {
            return false;
        }
        CompoundTag tag = ring.m_41784_();
        for (int i = 0; i < 3; ++i) {
            IFocus magicFocus;
            ISpell spell;
            ItemStack focus;
            Item item;
            if (!tag.m_128471_("SpellRingCasting" + i) || !((item = (focus = SpellRingHelper.getFocus(ring, i)).m_41720_()) instanceof IFocus) || (spell = (magicFocus = (IFocus)item).getSpell()) == null || !spellClass.isInstance(spell)) continue;
            return true;
        }
        return false;
    }

    public static ItemStack getCastingFocus(Player player) {
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (ring.m_41619_()) {
            return ItemStack.f_41583_;
        }
        int slot = SpellRingHelper.getCastingSlot(ring);
        if (slot < 0) {
            return ItemStack.f_41583_;
        }
        return SpellRingHelper.getFocus(ring, slot);
    }

    public static ItemStack getCastingRing(Player player) {
        ItemStack ring = SpellRingHelper.findSpellRing(player);
        if (ring.m_41619_()) {
            return ItemStack.f_41583_;
        }
        if (SpellRingHelper.getCastingSlot(ring) < 0) {
            return ItemStack.f_41583_;
        }
        return ring;
    }
}

