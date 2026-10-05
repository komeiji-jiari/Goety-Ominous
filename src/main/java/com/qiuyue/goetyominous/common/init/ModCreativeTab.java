package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousIconItem;
import com.qiuyue.goetyominous.common.items.PlushieBlockItem;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.common.items.of.OfItems;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import com.qiuyue.goetyominous.common.items.spear.SpearItems;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import com.qiuyue.goetyominous.compat.ias.IasItems;
import com.qiuyue.goetyominous.compat.lm.LmCompatManager;
import com.qiuyue.goetyominous.compat.mod.*;
import com.qiuyue.goetyominous.compat.spear.SpearBackportCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GoetyOminous.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register(GoetyOminous.MOD_ID, () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.DARK_ANKH.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + GoetyOminous.MOD_ID))
                    .displayItems((parameters, output) -> {
                        Buckets buckets = Buckets.collect();
                        buckets.other.forEach(output::accept);
                        buckets.weapons.forEach(output::accept);
                        buckets.foci.forEach(output::accept);
                        buckets.blocks.forEach(output::accept);
                    }).build());

    public static final RegistryObject<CreativeModeTab> SPAWN_EGG_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_spawn_eggs", () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.HURRICANE_SPAWN_EGG.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.spawn_eggs"))
                    .withTabsBefore(MAIN_TAB.getKey())
                    .displayItems((parameters, output) ->
                            Buckets.collect().spawnEggs.forEach(output::accept)).build());

    public static final RegistryObject<CreativeModeTab> PLUSHIE_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_plushies", () -> CreativeModeTab.builder()
                    .icon(() -> ModBlocks.PLUSHIE_SPDISH.get().asItem().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.plushies"))
                    .withTabsBefore(SPAWN_EGG_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        ModBlocks.PLUSHIES.forEach(block -> output.accept(block.get()));
                    }).build());

    private static final class Buckets {
        final List<Item> spawnEggs = new ArrayList<>();
        final List<Item> foci = new ArrayList<>();
        final List<Item> weapons = new ArrayList<>();
        final List<Item> other = new ArrayList<>();
        final List<Item> blocks = new ArrayList<>();

        static Buckets collect() {
            Buckets buckets = new Buckets();

            collectFrom(ModItems.ITEMS, buckets);

            if (SpearBackportCompat.isSpearBackportLoaded()) {
                collectFrom(SpearItems.SPEAR_ITEMS, buckets);
            }

            if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
                collectFrom(IasItems.IAS_ITEMS, buckets);
            }

            if (SavageRavageCompat.isSavageRavageLoaded()) {
                collectFrom(SarItems.SAR_ITEMS, buckets);
            }

            if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
                collectFrom(UaItems.UA_ITEMS, buckets);
            }

            if (MutantMoreCompat.isMutantMoreLoaded()) {
                collectFrom(MmItems.MM_ITEMS, buckets);
                moveAfter(buckets.other, MmItems.SHULKER_EMBRYO.get(), ModItems.COLD_HEART.get());
            }

            if (LmCompatManager.ENABLE_LM_ITEMS) {
                collectFrom(LmItems.LM_ITEMS, buckets);
            }

            if (OpposingForceCompat.isOpposingForceLoaded()) {
                collectFrom(OfItems.OF_ITEMS, buckets);
            }

            if (AlexMobsCompat.isAlexMobsLoaded()) {
                collectFrom(AmItems.AM_ITEMS, buckets);
                moveAfter(buckets.other, AmItems.WARPED_STEROIDS.get(), ModItems.NETHER_WART_POTION.get());
            }

            if (AlexCavesCompat.isAlexCavesLoaded()) {
                collectFrom(AcItems.AC_ITEMS, buckets);
                moveAfter(buckets.other, AcItems.RAYCAT_AMULET.get(), ModItems.SCREAMING_SKULL_JAR.get());
                moveAfter(buckets.other, AcItems.KEY_OF_RLYEH.get(), ModItems.FEL_STAFF.get());
            }

            return buckets;
        }
    }

    private static void collectFrom(DeferredRegister<Item> registry, Buckets buckets) {
        registry.getEntries().forEach(entry -> {
            if (entry.isPresent()) {
                Item item = entry.get();
                if (item instanceof SpawnEggItem) {
                    buckets.spawnEggs.add(item);
                } else if (item instanceof MagicFocus) {
                    buckets.foci.add(item);
                } else if (isWeapon(item)) {
                    buckets.weapons.add(item);
                } else if (!(item instanceof PlushieBlockItem) && !(item instanceof OminousIconItem)) {
                    if (item instanceof BlockItem) {
                        buckets.blocks.add(item);
                    } else {
                        buckets.other.add(item);
                    }
                }
            }
        });
    }

    private static void moveAfter(List<Item> list, Item item, Item after) {
        if (item == null || after == null) return;
        if (list.remove(item)) {
            int index = list.indexOf(after);
            if (index >= 0) {
                list.add(index + 1, item);
            } else {
                list.add(item);
            }
        }
    }

    private static boolean isWeapon(Item item) {
        if (item instanceof TieredItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.BoneCudgelItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.FirebrandItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.CogCrossbowItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.PiglinPrideItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.PitchforkItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.WitchBowItem) return true;
        return false;
    }
}
