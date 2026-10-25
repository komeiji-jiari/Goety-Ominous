package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LootResolver {
    private static volatile Map<Item, ItemStack> oreBlockIndex;
    private static final int MAX_FEATURE_DEPTH = 256;
    private static final Set<ConfiguredFeature<?, ?>> SEEN_FEATURES = Collections.newSetFromMap(new IdentityHashMap<>());

    private LootResolver() {
    }

    public static List<ItemStack> sampleTable(ServerLevel level, LootTable table, ResourceLocation tableId, BlockPos origin, ServerPlayer player, long seed) {
        if (table == null || table == LootTable.EMPTY) {
            return List.of();
        }
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin));
        if (player != null) {
            builder.withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.KILLER_ENTITY, player)
                    .withLuck(player.getLuck());
        }
        LootParams params = builder.create(LootContextParamSets.CHEST);
        try {
            return new ArrayList<>(table.getRandomItems(params, seed));
        } catch (RuntimeException failure) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to sample loot table {}: {}", tableId, failure.toString());
            return List.of();
        }
    }

    public static int giveToPlayer(ServerPlayer player, List<ItemStack> stacks) {
        if (player == null || stacks.isEmpty()) {
            return 0;
        }
        int dropped = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
                dropped++;
            }
        }
        player.containerMenu.broadcastChanges();
        player.getInventory().setChanged();
        return dropped;
    }

    public static void invalidateOreIndex() {
        oreBlockIndex = null;
    }

    public static List<ItemStack> sampleOrePool(ServerLevel level, List<ResourceLocation> oreTables, ServerPlayer player, int count, RandomSource random) {
        List<ItemStack> out = new ArrayList<>(count);
        if (oreTables.isEmpty() || count <= 0) {
            return out;
        }
        for (int i = 0; i < count; i++) {
            ResourceLocation tableId = oreTables.get(random.nextInt(oreTables.size()));
            ItemStack oreBlock = oreBlockFor(level, tableId, random);
            if (!oreBlock.isEmpty()) {
                out.add(oreBlock);
            }
        }
        return out;
    }

    private static ItemStack oreBlockFor(ServerLevel level, ResourceLocation tableId, RandomSource random) {
        if (isOreBlockTable(tableId)) {
            ItemStack direct = oreBlockOfTable(tableId);
            if (!direct.isEmpty()) {
                return direct;
            }
        }
        LootTable table = level.getServer().getLootData().getLootTable(tableId);
        for (ItemStack dropped : sampleTable(level, table, tableId, BlockPos.ZERO, null, random.nextLong())) {
            ItemStack ore = oreBlockOf(dropped);
            if (!ore.isEmpty()) {
                return ore;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack oreBlockOfTable(ResourceLocation table) {
        if (table == null) {
            return ItemStack.EMPTY;
        }
        for (Block block : ForgeRegistries.BLOCKS) {
            if (!table.equals(block.getLootTable())) {
                continue;
            }
            Item item = Item.BY_BLOCK.get(block);
            if (item == null || item == Items.AIR) {
                continue;
            }
            return new ItemStack(item);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isOreBlock(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return !oreBlockOfSelf(stack).isEmpty();
    }

    private static Map<Item, ItemStack> oreIndex() {
        Map<Item, ItemStack> local = oreBlockIndex;
        if (local != null) {
            return local;
        }
        Map<Item, ItemStack> built = new HashMap<>();
        for (Block block : ForgeRegistries.BLOCKS) {
            Item item = Item.BY_BLOCK.get(block);
            if (item == null || item == Items.AIR) {
                continue;
            }
            if (isOreBlockTable(block.getLootTable())) {
                built.putIfAbsent(item, new ItemStack(item));
            }
        }
        oreBlockIndex = built;
        return built;
    }

    private static ItemStack oreBlockOfSelf(ItemStack stack) {
        ItemStack cached = oreIndex().get(stack.getItem());
        return cached == null || cached.isEmpty() ? ItemStack.EMPTY : cached.copy();
    }

    public static ItemStack oreBlockOf(ItemStack dropped) {
        if (dropped.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return oreBlockOfSelf(dropped);
    }

    public static boolean isOreBlockTable(ResourceLocation table) {
        if (table == null) {
            return false;
        }
        String path = table.getPath();
        if (path.endsWith("_ore") || path.endsWith("_ore_block")) {
            return true;
        }
        return path.equals("ancient_debris") || path.endsWith("_debris");
    }

    public static boolean producesOreBlock(ServerLevel level, ResourceLocation tableId, RandomSource random) {
        if (isOreBlockTable(tableId)) {
            return true;
        }
        if (!oreBlockOfTable(tableId).isEmpty()) {
            return true;
        }
        LootTable table = level.getServer().getLootData().getLootTable(tableId);
        if (table == null || table == LootTable.EMPTY) {
            return false;
        }
        for (ItemStack stack : sampleTable(level, table, tableId, BlockPos.ZERO, null, random.nextLong())) {
            if (isOreBlock(stack)) {
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> sampleMobPool(ServerLevel level, List<ResourceLocation> mobTables, ServerPlayer player, int count, RandomSource random) {
        List<ItemStack> out = new ArrayList<>();
        if (mobTables.isEmpty()) {
            return out;
        }
        for (int i = 0; i < count; i++) {
            ResourceLocation tableId = mobTables.get(random.nextInt(mobTables.size()));
            LootTable table = level.getServer().getLootData().getLootTable(tableId);
            BlockPos origin = player == null ? BlockPos.ZERO : player.blockPosition();
            out.addAll(sampleTable(level, table, tableId, origin, player, random.nextLong()));
        }
        return out;
    }

    public static void collectOreTables(ConfiguredFeature<?, ?> configured, List<ResourceLocation> out) {
        SEEN_FEATURES.clear();
        try {
            collectOreTables(configured, out, 0);
        } finally {
            SEEN_FEATURES.clear();
        }
    }

    private static void collectOreTables(ConfiguredFeature<?, ?> configured, List<ResourceLocation> out, int depth) {
        if (configured == null) {
            return;
        }
        if (depth > MAX_FEATURE_DEPTH) {
            GoetyOminous.LOGGER.warn("[goetyominous] Worldgen feature nesting exceeded {} levels, stopped descending.", MAX_FEATURE_DEPTH);
            return;
        }
        if (!SEEN_FEATURES.add(configured)) {
            return;
        }
        Feature<?> feature = configured.feature();
        FeatureConfiguration config = configured.config();
        if ((feature == Feature.ORE || feature == Feature.SCATTERED_ORE) && config instanceof OreConfiguration ore) {
            for (OreConfiguration.TargetBlockState target : ore.targetStates) {
                ResourceLocation table = target.state.getBlock().getLootTable();
                if (table == null || table.equals(BuiltInLootTables.EMPTY) || out.contains(table)) {
                    continue;
                }
                out.add(table);
            }
        }
        configured.getFeatures().forEach(child -> collectOreTables(child, out, depth + 1));
    }
}
