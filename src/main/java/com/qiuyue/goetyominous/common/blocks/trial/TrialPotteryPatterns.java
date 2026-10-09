package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ModItems;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TrialPotteryPatterns {
    public static final ResourceKey<String> FLOW = create("flow_pottery_pattern");
    public static final ResourceKey<String> GUSTER = create("guster_pottery_pattern");
    public static final ResourceKey<String> SCRAPE = create("scrape_pottery_pattern");

    @Nullable
    private static Map<Item, ResourceKey<String>> byItem;

    private static ResourceKey<String> create(String name) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERNS, new ResourceLocation(GoetyOminous.MOD_ID, name));
    }

    @SubscribeEvent
    public static void registerPatterns(RegisterEvent event) {
        event.register(Registries.DECORATED_POT_PATTERNS, helper -> {
            helper.register(FLOW, "flow_pottery_pattern");
            helper.register(GUSTER, "guster_pottery_pattern");
            helper.register(SCRAPE, "scrape_pottery_pattern");
        });
    }

    @Nullable
    public static ResourceKey<String> getPattern(Item item) {
        if (byItem == null) {
            byItem = Map.of(
                    ModItems.FLOW_POTTERY_SHERD.get(), FLOW,
                    ModItems.GUSTER_POTTERY_SHERD.get(), GUSTER,
                    ModItems.SCRAPE_POTTERY_SHERD.get(), SCRAPE);
        }
        return byItem.get(item);
    }
}
