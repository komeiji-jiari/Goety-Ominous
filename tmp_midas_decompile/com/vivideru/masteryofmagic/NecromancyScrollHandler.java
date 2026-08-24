/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public class NecromancyScrollHandler {
    @SubscribeEvent(priority=EventPriority.NORMAL)
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (player.m_9236_().m_5776_()) {
            return;
        }
        int current = MasteryData.get(player, MasteryData.MasteryId.NECROMANCY);
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_I.get()) {
            if (current > 0) {
                player.m_5661_((Component)Component.m_237113_((String)"You already have Necromancy Mastery I or higher."), true);
                event.setCanceled(true);
                return;
            }
            if (current == 0 && !NecromancyScrollHandler.hasAdv(player, "goety_mastery_of_magic:necromancy_mastery_i")) {
                MasteryData.set(player, MasteryData.MasteryId.NECROMANCY, 1);
                NecromancyScrollHandler.grantAdv(player, "goety_mastery_of_magic:necromancy_mastery_i");
                stack.m_41774_(1);
                event.setCanceled(true);
                return;
            }
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_II.get()) {
            if (current < 1) {
                player.m_5661_((Component)Component.m_237113_((String)"You need Necromancy Mastery I for Necromancy Mastery II."), true);
                event.setCanceled(true);
                return;
            }
            if (current == 1 && !NecromancyScrollHandler.hasAdv(player, "goety_mastery_of_magic:necromancy_mastery_ii")) {
                MasteryData.set(player, MasteryData.MasteryId.NECROMANCY, 2);
                NecromancyScrollHandler.grantAdv(player, "goety_mastery_of_magic:necromancy_mastery_ii");
                stack.m_41774_(1);
                event.setCanceled(true);
                return;
            }
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_III.get()) {
            if (current < 2) {
                player.m_5661_((Component)Component.m_237113_((String)"You need Necromancy Mastery II for Necromancy Mastery III."), true);
                event.setCanceled(true);
                return;
            }
            if (current == 2 && !NecromancyScrollHandler.hasAdv(player, "goety_mastery_of_magic:necromancy_mastery_iii")) {
                MasteryData.set(player, MasteryData.MasteryId.NECROMANCY, 3);
                NecromancyScrollHandler.grantAdv(player, "goety_mastery_of_magic:necromancy_mastery_iii");
                stack.m_41774_(1);
                event.setCanceled(true);
                return;
            }
        }
    }

    private static boolean hasAdv(Player player, String id) {
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            Advancement adv = sp.f_8924_.m_129889_().m_136041_(new ResourceLocation(id));
            if (adv != null) {
                AdvancementProgress p = sp.m_8960_().m_135996_(adv);
                return p.m_8193_();
            }
        }
        return false;
    }

    private static void grantAdv(Player player, String id) {
        if (player instanceof ServerPlayer) {
            AdvancementProgress p;
            ServerPlayer sp = (ServerPlayer)player;
            Advancement adv = sp.f_8924_.m_129889_().m_136041_(new ResourceLocation(id));
            if (adv != null && !(p = sp.m_8960_().m_135996_(adv)).m_8193_()) {
                for (String c : p.m_8219_()) {
                    sp.m_8960_().m_135988_(adv, c);
                }
            }
        }
    }
}

