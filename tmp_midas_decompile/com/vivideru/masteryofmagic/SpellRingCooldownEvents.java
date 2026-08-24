/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  top.theillusivec4.curios.api.CuriosApi
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.SpellRingHelper;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public class SpellRingCooldownEvents {
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        SpellRingCooldownEvents.tickIfSpellRing(player.m_21205_());
        SpellRingCooldownEvents.tickIfSpellRing(player.m_21206_());
        CuriosApi.getCuriosInventory((LivingEntity)player).ifPresent(handler -> handler.findCurios(stack -> stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SPELL_RING.get())).forEach(slotResult -> SpellRingCooldownEvents.tickIfSpellRing(slotResult.stack())));
    }

    private static void tickIfSpellRing(ItemStack stack) {
        if (stack.m_41619_()) {
            return;
        }
        if (!stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SPELL_RING.get())) {
            return;
        }
        SpellRingHelper.tickCooldowns(stack);
    }
}

