package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import com.qiuyue.goetyominous.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominous.compat.mod.OpposingForceCompat;
import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import com.qiuyue.goetyominous.compat.mod.UpgradeAquaticCompat;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

public class OminousIconRotation {

    public static final int TICKS_PER_ICON = 20;

    private static final ResourceLocation INDEX = new ResourceLocation("goetyominous", "ominous_icon_index");

    private static final Entry[] ENTRIES = new Entry[]{
            new Entry(IllageAndSpillageCompat::isIllageAndSpillageLoaded, 0),
            new Entry(AlexMobsCompat::isAlexMobsLoaded, 1),
            new Entry(MutantMoreCompat::isMutantMoreLoaded, 2),
            new Entry(OpposingForceCompat::isOpposingForceLoaded, 3),
            new Entry(SavageRavageCompat::isSavageRavageLoaded, 4),
            new Entry(UpgradeAquaticCompat::isUpgradeAquaticLoaded, 5)
    };

    private static int[] loadedIndices;

    public static void register() {
        ItemProperties.register(ModItems.OMINOUS_ICON.get(), INDEX,
                (stack, level, entity, seed) -> level == null ? -1.0F : indexAt(level.getGameTime()));
    }

    private static float indexAt(long gameTime) {
        int[] loaded = loadedIndices();
        if (loaded.length == 0) {
            return -1.0F;
        }
        return loaded[(int) ((gameTime / TICKS_PER_ICON) % loaded.length)];
    }

    private static int[] loadedIndices() {
        if (loadedIndices == null) {
            int[] buffer = new int[ENTRIES.length];
            int count = 0;
            for (Entry entry : ENTRIES) {
                if (entry.loaded.getAsBoolean()) {
                    buffer[count++] = entry.index;
                }
            }
            int[] trimmed = new int[count];
            System.arraycopy(buffer, 0, trimmed, 0, count);
            loadedIndices = trimmed;
        }
        return loadedIndices;
    }

    private record Entry(BooleanSupplier loaded, int index) {
    }
}
