/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class GoetyMasteryOfMagicModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"goety_mastery_of_magic");
    public static final RegistryObject<CreativeModeTab> MASTERYOF_MAGIC = REGISTRY.register("masteryof_magic", () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)"item_group.goety_mastery_of_magic.masteryof_magic")).m_257737_(() -> new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_I.get())).m_257501_((parameters, tabData) -> {
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NECROMANCY_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.FROST_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.NETHER_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.DEEP_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.STORM_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.SKY_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GEOMANCY_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WILD_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.VOID_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_I.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_II.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_III.get());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.NETHER_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.CRYPT_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.VOID_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.SKY_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.DEEP_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.STORM_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.GEOMANCY_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.WILD_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.OMINOUS_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.FROST_RUNED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.POLISHED_LAZETHYST_BLOCK.get()).m_5456_());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.PERMA_LAZETHYST.get());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.BLACKSTONE_CHALICE.get()).m_5456_());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_BUCKET.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOLDEN_GOBLET.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOLDEN_GOBLET_FILLED.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOBLETOF_FLIGHT.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOBLETOF_BATS.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOBLETOF_ANTIFREEZE.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GOBLETOF_SOUL_ENERGY.get());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.MOVEMENT_FORCER.get()).m_5456_());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GLACIAL_UNHOLY_BLOOD.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.CHILLING_TIMES_DISK.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GHIACCIO_SPAWN_EGG.get());
        tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.ICE_MONARCH_SPAWN_EGG.get());
        tabData.m_246326_((ItemLike)((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get()).m_5456_());
    }).m_257652_());

    @SubscribeEvent
    public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
        if (tabData.getTabKey() == CreativeModeTabs.f_256731_) {
            tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.VAMPIRATOR_SERVANT_SPAWN_EGG.get());
            tabData.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.GAZER_SPAWN_EGG.get());
        }
    }
}

