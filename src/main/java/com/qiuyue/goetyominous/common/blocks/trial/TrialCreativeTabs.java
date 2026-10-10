package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousBottleItem;
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
        if (CreativeModeTabs.BUILDING_BLOCKS.equals(event.getTabKey())) {

            before(event, Items.BRICKS,
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
            return;
        }
        if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(event.getTabKey())) {
            after(event, Items.WRITABLE_BOOK, ModItems.WIND_CHARGE.get());
            after(event, Items.MUSIC_DISC_RELIC,
                    ModItems.MUSIC_DISC_CREATOR.get(),
                    ModItems.MUSIC_DISC_CREATOR_MUSIC_BOX.get(),
                    ModItems.MUSIC_DISC_PRECIPICE.get());
            return;
        }
        if (CreativeModeTabs.COMBAT.equals(event.getTabKey())) {
            after(event, Items.TRIDENT, ModItems.MACE.get());
            return;
        }
        if (CreativeModeTabs.INGREDIENTS.equals(event.getTabKey())) {
            after(event, Items.PIGLIN_BANNER_PATTERN,
                    ModItems.FLOW_BANNER_PATTERN.get(),
                    ModItems.GUSTER_BANNER_PATTERN.get());
            after(event, Items.SNORT_POTTERY_SHERD,
                    ModItems.FLOW_POTTERY_SHERD.get(),
                    ModItems.GUSTER_POTTERY_SHERD.get(),
                    ModItems.SCRAPE_POTTERY_SHERD.get());
            after(event, Items.BLAZE_ROD, ModItems.BREEZE_ROD.get(), ModItems.HEAVY_CORE.get());
            after(event, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
                    ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                    ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                    ModItems.TRIAL_KEY.get(),
                    ModItems.OMINOUS_TRIAL_KEY.get());
            return;
        }
        if (CreativeModeTabs.FOOD_AND_DRINKS.equals(event.getTabKey())) {
            ItemStack prev = new ItemStack(Items.HONEY_BOTTLE);
            for (int amplifier = 0; amplifier <= OminousBottleItem.MAX_AMPLIFIER; ++amplifier) {
                ItemStack stack = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
                OminousBottleItem.setAmplifier(stack, amplifier);
                event.getEntries().putAfter(prev, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                prev = stack;
            }
            return;
        }
        if (CreativeModeTabs.SPAWN_EGGS.equals(event.getTabKey())) {
            after(event, Items.SPAWNER, TrialBlocks.TRIAL_SPAWNER.get());
            return;
        }
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
