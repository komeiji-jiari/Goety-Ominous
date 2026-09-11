package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlushieLootInjection {

    private static final ResourceLocation PLUSHIE_REWARD =
            new ResourceLocation(GoetyOminous.MOD_ID, "gameplay/plushie_reward");

    private static final String TREASURE_POUCH = "goety:gameplay/treasure_pouch";
    private static final float POUCH_CHANCE = 0.2F;

    private static final List<String> CHEST_TABLES = List.of(
            "goety:chests/blacksmith_treasure",
            "goety:chests/dark_manor_treasure",
            "goety:chests/graveyard_treasure",
            "goety:chests/shack_treasure",
            "goety:chests/shrine_treasure",
            "goety:chests/sorcerous_keep_treasure"
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        String name = event.getName().toString();

        if (name.equals(TREASURE_POUCH)) {
            event.getTable().addPool(pool()
                    .when(LootItemRandomChanceCondition.randomChance(POUCH_CHANCE))
                    .build());
        } else if (CHEST_TABLES.contains(name)) {
            event.getTable().addPool(pool().build());
        }
    }

    private static LootPool.Builder pool() {
        return LootPool.lootPool()
                .name("goetyominous_plushie_inject")
                .add(LootTableReference.lootTableReference(PLUSHIE_REWARD));
    }
}
