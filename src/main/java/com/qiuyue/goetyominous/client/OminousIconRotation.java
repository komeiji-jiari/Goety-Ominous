package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominous.compat.mod.OpposingForceCompat;
import com.qiuyue.goetyominous.compat.mod.PatchouliCompat;
import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import com.qiuyue.goetyominous.compat.mod.UpgradeAquaticCompat;
import com.qiuyue.goetyominous.compat.patchouli.OminousRotationBookOrder;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class OminousIconRotation {

    public static final int TICKS_PER_ICON = 20;

    private static final ResourceLocation INDEX = new ResourceLocation("goetyominous", "ominous_icon_index");

    private static final Entry[] ENTRIES = new Entry[]{
            new Entry("goetyabnormal", SavageRavageCompat::isSavageRavageLoaded, 0),
            new Entry("goetyabnormal", UpgradeAquaticCompat::isUpgradeAquaticLoaded, 1),
            new Entry("mmcompat", MutantMoreCompat::isMutantMoreLoaded, 2),
            new Entry("amcompat", AlexMobsCompat::isAlexMobsLoaded, 4),
            new Entry("accompat", AlexCavesCompat::isAlexCavesLoaded, 5),
            new Entry("ofcompat", OpposingForceCompat::isOpposingForceLoaded, 6)
    };

    private static Supplier<int[]> order = OminousIconRotation::installedOrder;

    public static void register() {
        if (PatchouliCompat.isLoaded()) {
            order = OminousRotationBookOrder::order;
        }
        ItemProperties.register(ModItems.OMINOUS_ICON.get(), INDEX,
                (stack, level, entity, seed) -> level == null ? -1.0F : indexAt(level.getGameTime()));
    }

    public static int indexOf(String categoryPath) {
        for (Entry entry : ENTRIES) {
            if (entry.categoryPath().equals(categoryPath)) {
                return entry.loaded().getAsBoolean() ? entry.index() : -1;
            }
        }
        return -1;
    }

    private static float indexAt(long gameTime) {
        int[] loaded = order.get();
        if (loaded.length == 0) {
            return -1.0F;
        }
        return loaded[(int) ((gameTime / TICKS_PER_ICON) % loaded.length)];
    }

    private static int[] installedOrder() {
        int[] buffer = new int[ENTRIES.length];
        int count = 0;
        Set<String> seen = new HashSet<>();
        for (Entry entry : ENTRIES) {
            if (entry.loaded().getAsBoolean() && seen.add(entry.categoryPath())) {
                buffer[count++] = entry.index();
            }
        }
        return Arrays.copyOf(buffer, count);
    }

    private record Entry(String categoryPath, BooleanSupplier loaded, int index) {
    }
}
