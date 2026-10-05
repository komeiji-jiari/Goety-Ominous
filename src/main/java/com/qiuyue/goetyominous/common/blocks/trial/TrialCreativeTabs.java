package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TrialCreativeTabs {

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (!CreativeModeTabs.BUILDING_BLOCKS.equals(event.getTabKey())) {
            return;
        }

        before(event, Items.BRICKS,
                Items.TUFF,
                TrialBlocks.TUFF_STAIRS.get(),
                TrialBlocks.TUFF_SLAB.get(),
                TrialBlocks.TUFF_WALL.get(),
                TrialBlocks.CHISELED_TUFF.get(),
                TrialBlocks.POLISHED_TUFF.get(),
                TrialBlocks.POLISHED_TUFF_STAIRS.get(),
                TrialBlocks.POLISHED_TUFF_SLAB.get(),
                TrialBlocks.POLISHED_TUFF_WALL.get(),
                TrialBlocks.TUFF_BRICKS.get(),
                TrialBlocks.TUFF_BRICK_STAIRS.get(),
                TrialBlocks.TUFF_BRICK_SLAB.get(),
                TrialBlocks.TUFF_BRICK_WALL.get(),
                TrialBlocks.CHISELED_TUFF_BRICKS.get());

        before(event, Items.CUT_COPPER,
                TrialBlocks.CHISELED_COPPER.get(),
                TrialBlocks.COPPER_GRATE.get());
        before(event, Items.EXPOSED_COPPER,
                TrialBlocks.COPPER_DOOR.get(),
                TrialBlocks.COPPER_TRAPDOOR.get(),
                TrialBlocks.COPPER_BULB.get());

        before(event, Items.EXPOSED_CUT_COPPER,
                TrialBlocks.EXPOSED_CHISELED_COPPER.get(),
                TrialBlocks.EXPOSED_COPPER_GRATE.get());
        before(event, Items.WEATHERED_COPPER,
                TrialBlocks.EXPOSED_COPPER_DOOR.get(),
                TrialBlocks.EXPOSED_COPPER_TRAPDOOR.get(),
                TrialBlocks.EXPOSED_COPPER_BULB.get());

        before(event, Items.WEATHERED_CUT_COPPER,
                TrialBlocks.WEATHERED_CHISELED_COPPER.get(),
                TrialBlocks.WEATHERED_COPPER_GRATE.get());
        before(event, Items.OXIDIZED_COPPER,
                TrialBlocks.WEATHERED_COPPER_DOOR.get(),
                TrialBlocks.WEATHERED_COPPER_TRAPDOOR.get(),
                TrialBlocks.WEATHERED_COPPER_BULB.get());

        before(event, Items.OXIDIZED_CUT_COPPER,
                TrialBlocks.OXIDIZED_CHISELED_COPPER.get(),
                TrialBlocks.OXIDIZED_COPPER_GRATE.get());
        before(event, Items.WAXED_COPPER_BLOCK,
                TrialBlocks.OXIDIZED_COPPER_DOOR.get(),
                TrialBlocks.OXIDIZED_COPPER_TRAPDOOR.get(),
                TrialBlocks.OXIDIZED_COPPER_BULB.get());

        before(event, Items.WAXED_CUT_COPPER,
                TrialBlocks.WAXED_CHISELED_COPPER.get(),
                TrialBlocks.WAXED_COPPER_GRATE.get());
        before(event, Items.WAXED_EXPOSED_COPPER,
                TrialBlocks.WAXED_COPPER_DOOR.get(),
                TrialBlocks.WAXED_COPPER_TRAPDOOR.get(),
                TrialBlocks.WAXED_COPPER_BULB.get());

        before(event, Items.WAXED_EXPOSED_CUT_COPPER,
                TrialBlocks.WAXED_EXPOSED_CHISELED_COPPER.get(),
                TrialBlocks.WAXED_EXPOSED_COPPER_GRATE.get());
        before(event, Items.WAXED_WEATHERED_COPPER,
                TrialBlocks.WAXED_EXPOSED_COPPER_DOOR.get(),
                TrialBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.get(),
                TrialBlocks.WAXED_EXPOSED_COPPER_BULB.get());

        before(event, Items.WAXED_WEATHERED_CUT_COPPER,
                TrialBlocks.WAXED_WEATHERED_CHISELED_COPPER.get(),
                TrialBlocks.WAXED_WEATHERED_COPPER_GRATE.get());
        before(event, Items.WAXED_OXIDIZED_COPPER,
                TrialBlocks.WAXED_WEATHERED_COPPER_DOOR.get(),
                TrialBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.get(),
                TrialBlocks.WAXED_WEATHERED_COPPER_BULB.get());

        before(event, Items.WAXED_OXIDIZED_CUT_COPPER,
                TrialBlocks.WAXED_OXIDIZED_CHISELED_COPPER.get(),
                TrialBlocks.WAXED_OXIDIZED_COPPER_GRATE.get());
        after(event, Items.WAXED_OXIDIZED_CUT_COPPER_SLAB,
                TrialBlocks.WAXED_OXIDIZED_COPPER_DOOR.get(),
                TrialBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.get(),
                TrialBlocks.WAXED_OXIDIZED_COPPER_BULB.get());
    }

    private static void before(BuildCreativeModeTabContentsEvent event, Item anchor, ItemLike... items) {
        ItemStack key = new ItemStack(anchor);
        for (ItemLike item : items) {
            event.getEntries().putBefore(key, new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private static void after(BuildCreativeModeTabContentsEvent event, Item anchor, ItemLike... items) {
        ItemStack previous = new ItemStack(anchor);
        for (ItemLike item : items) {
            ItemStack stack = new ItemStack(item);
            event.getEntries().putAfter(previous, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            previous = stack;
        }
    }
}
