/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.event.entity.player.PlayerEvent$ItemCraftedEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.item.UndeadBloodBucketItem;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class UndeadBloodBucketCraftHandler {
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        boolean fromVials;
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        if (player2.m_9236_().f_46443_) {
            return;
        }
        Container container = event.getInventory();
        if (!(container instanceof CraftingContainer)) {
            return;
        }
        CraftingContainer inv = (CraftingContainer)container;
        ItemStack result = event.getCrafting();
        if (!result.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_BUCKET.get())) {
            return;
        }
        UUID sourceUUID = null;
        String sourceName = null;
        int vialCount = 0;
        int bucketCount = 0;
        for (int i = 0; i < inv.m_6643_(); ++i) {
            ItemStack stack = inv.m_8020_(i);
            if (stack.m_41619_()) continue;
            if (stack.m_150930_(Items.f_42446_)) {
                bucketCount += stack.m_41613_();
                continue;
            }
            if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get())) {
                if (!UndeadBloodVialItem.hasSource(stack)) {
                    return;
                }
                UUID vialUUID = stack.m_41783_().m_128342_("SourceUUID");
                String vialName = stack.m_41783_().m_128461_("SourceName");
                if (sourceUUID == null) {
                    sourceUUID = vialUUID;
                    sourceName = vialName;
                } else if (!sourceUUID.equals(vialUUID)) {
                    return;
                }
                vialCount += stack.m_41613_();
                continue;
            }
            return;
        }
        if (sourceUUID == null) {
            return;
        }
        boolean fromBucket = bucketCount == 1 && vialCount == 6;
        boolean bl = fromVials = bucketCount == 0 && vialCount >= 6 && vialCount % 6 == 0;
        if (!fromBucket && !fromVials) {
            return;
        }
        if (fromBucket) {
            result.m_41764_(1);
            UndeadBloodBucketItem.setSource(result, sourceUUID, sourceName);
            for (int i = 0; i < inv.m_6643_(); ++i) {
                ItemStack stack = inv.m_8020_(i);
                if (!stack.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get())) continue;
                inv.m_6836_(i, new ItemStack((ItemLike)Items.f_42590_));
            }
            return;
        }
        int craftable = vialCount / 6;
        if (player2.m_6144_()) {
            craftable = 1;
        }
        result.m_41764_(craftable);
        UndeadBloodBucketItem.setSource(result, sourceUUID, sourceName);
        int vialsToConsume = craftable * 6;
        for (int i = 0; i < inv.m_6643_(); ++i) {
            ItemStack stack = inv.m_8020_(i);
            if (vialsToConsume <= 0) break;
            if (!stack.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get())) continue;
            int remove = Math.min(stack.m_41613_(), vialsToConsume);
            vialsToConsume -= remove;
            ItemStack bottles = new ItemStack((ItemLike)Items.f_42590_, remove);
            if (stack.m_41613_() == remove) {
                inv.m_6836_(i, bottles);
                continue;
            }
            stack.m_41774_(remove);
            player2.m_150109_().m_150079_(bottles);
        }
    }
}

