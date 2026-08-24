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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public class MasteryScrollHandlerAll {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (player.m_9236_().m_5776_()) {
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.WILD, 1, "goety_mastery_of_magic:wild_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.WILD, 2, "goety_mastery_of_magic:wild_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.WILD, 3, "goety_mastery_of_magic:wild_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.FROST, 1, "goety_mastery_of_magic:frost_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.FROST, 2, "goety_mastery_of_magic:frost_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.FROST, 3, "goety_mastery_of_magic:frost_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.DEEP, 1, "goety_mastery_of_magic:deep_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.DEEP, 2, "goety_mastery_of_magic:deep_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.DEEP, 3, "goety_mastery_of_magic:deep_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.END, 1, "goety_mastery_of_magic:void_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.END, 2, "goety_mastery_of_magic:void_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.END, 3, "goety_mastery_of_magic:void_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.NETHER, 1, "goety_mastery_of_magic:nether_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.NETHER, 2, "goety_mastery_of_magic:nether_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.NETHER, 3, "goety_mastery_of_magic:nether_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.SKY, 1, "goety_mastery_of_magic:sky_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.SKY, 2, "goety_mastery_of_magic:sky_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.SKY, 3, "goety_mastery_of_magic:sky_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.GEOTURGY, 1, "goety_mastery_of_magic:geomancy_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.GEOTURGY, 2, "goety_mastery_of_magic:geomancy_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.GEOTURGY, 3, "goety_mastery_of_magic:geomancy_mastery_iii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_I.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.STORM, 1, "goety_mastery_of_magic:storm_mastery_i");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_II.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.STORM, 2, "goety_mastery_of_magic:storm_mastery_ii");
            return;
        }
        if (stack.m_41720_() == GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_III.get()) {
            MasteryScrollHandlerAll.handle(player, stack, MasteryData.MasteryId.STORM, 3, "goety_mastery_of_magic:storm_mastery_iii");
            return;
        }
    }

    private static void handle(Player player, ItemStack stack, MasteryData.MasteryId id, int level, String advId) {
        int current = MasteryData.get(player, id);
        if (current < level - 1) {
            player.m_5661_((Component)Component.m_237113_((String)("You need mastery " + (level - 1) + " for mastery " + level + ".")), true);
            return;
        }
        if (MasteryScrollHandlerAll.hasAdv(player, advId)) {
            player.m_5661_((Component)Component.m_237113_((String)"You have already used this mastery scroll."), true);
            return;
        }
        MasteryData.set(player, id, level);
        MasteryScrollHandlerAll.grantAdv(player, advId);
        stack.m_41774_(1);
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

