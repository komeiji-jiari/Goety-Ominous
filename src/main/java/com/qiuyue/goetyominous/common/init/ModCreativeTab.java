package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.blocks.TrainingBlock;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.ac.MineGuardianBlock;
import com.qiuyue.goetyominous.common.blocks.ac.ServantEggBlock;
import com.qiuyue.goetyominous.common.blocks.trial.TrialBlocks;
import com.qiuyue.goetyominous.common.items.CursedBlackBeastArmorItem;
import com.qiuyue.goetyominous.common.items.CursedMetalWolfArmorItem;
import com.qiuyue.goetyominous.common.items.CursedWargArmorItem;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousIconItem;
import com.qiuyue.goetyominous.common.items.PlushieBlockItem;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.common.items.ac.RaycatAmuletItem;
import com.qiuyue.goetyominous.common.items.revive.MysteriousContract;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.common.items.am.WarpedSteroidsItem;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.common.items.of.OfItems;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import com.qiuyue.goetyominous.compat.ias.IasItems;
import com.qiuyue.goetyominous.compat.lm.LmCompatManager;
import com.qiuyue.goetyominous.compat.mod.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GoetyOminous.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register(GoetyOminous.MOD_ID, () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.DARK_ANKH.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + GoetyOminous.MOD_ID))
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        Buckets buckets = Buckets.collect();
                        buckets.weapons.forEach(output::accept);
                        buckets.armor.forEach(output::accept);
                        buckets.foci.forEach(output::accept);
                        buckets.other.forEach(output::accept);
                        buckets.blocks.forEach(output::accept);
                        buckets.spawnEggs.forEach(output::accept);
                    }).build());

    public static final RegistryObject<CreativeModeTab> COMPAT_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_compat", () -> CreativeModeTab.builder()
                    .icon(() -> com.Polarice3.Goety.common.items.ModItems.HUNGER_CORE.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.compat"))
                    .withTabsBefore(MAIN_TAB.getKey())
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        CompatBuckets buckets = CompatBuckets.collect();
                        buckets.staves.forEach(output::accept);
                        buckets.weapons.forEach(output::accept);
                        buckets.foci.forEach(output::accept);
                        buckets.materials.forEach(output::accept);
                        buckets.revive.forEach(output::accept);
                        buckets.summons.forEach(output::accept);
                        buckets.blocks.forEach(output::accept);
                        buckets.dinoEggs.forEach(output::accept);
                        buckets.spawnEggs.forEach(output::accept);
                    }).build());

    public static final RegistryObject<CreativeModeTab> PLUSHIE_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_plushies", () -> CreativeModeTab.builder()
                    .icon(() -> ModBlocks.PLUSHIE_HIM.get().asItem().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.plushies"))
                    .withTabsBefore(COMPAT_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        ModBlocks.PLUSHIES.forEach(block -> output.accept(block.get()));
                    }).build());

    private static final class Buckets {
        final List<Item> spawnEggs = new ArrayList<>();
        final List<Item> foci = new ArrayList<>();
        final List<Item> weapons = new ArrayList<>();
        final List<Item> armor = new ArrayList<>();
        final List<Item> other = new ArrayList<>();
        final List<Item> blocks = new ArrayList<>();

        static Buckets collect() {
            Buckets buckets = new Buckets();
            collectFrom(ModItems.ITEMS, buckets);
            sortOther(buckets.other);
            return buckets;
        }
    }

    private static final class CompatBuckets {
        final List<Item> staves = new ArrayList<>();
        final List<Item> weapons = new ArrayList<>();
        final List<Item> foci = new ArrayList<>();
        final List<Item> materials = new ArrayList<>();
        final List<Item> revive = new ArrayList<>();
        final List<Item> summons = new ArrayList<>();
        final List<Item> blocks = new ArrayList<>();
        final List<Item> dinoEggs = new ArrayList<>();
        final List<Item> spawnEggs = new ArrayList<>();

        static CompatBuckets collect() {
            CompatBuckets buckets = new CompatBuckets();

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
            }

            if (LmCompatManager.ENABLE_LM_ITEMS) {
                collectFrom(LmItems.LM_ITEMS, buckets);
            }

            if (OpposingForceCompat.isOpposingForceLoaded()) {
                collectFrom(OfItems.OF_ITEMS, buckets);
            }

            if (AlexMobsCompat.isAlexMobsLoaded()) {
                collectFrom(AmItems.AM_ITEMS, buckets);
            }

            if (AlexCavesCompat.isAlexCavesLoaded()) {
                collectFrom(AcItems.AC_ITEMS, buckets);
            }

            sortRevive(buckets.revive);
            return buckets;
        }

        private static void collectFrom(DeferredRegister<Item> registry, CompatBuckets buckets) {
            registry.getEntries().forEach(entry -> {
                if (entry.isPresent()) {
                    Item item = entry.get();
                    if (item instanceof SpawnEggItem) {
                        buckets.spawnEggs.add(item);
                    } else if (item instanceof DarkStaff) {
                        buckets.staves.add(item);
                    } else if (isWeapon(item)) {
                        buckets.weapons.add(item);
                    } else if (item instanceof MagicFocus) {
                        buckets.foci.add(item);
                    } else if (isRevive(item)) {
                        buckets.revive.add(item);
                    } else if (isSummon(item)) {
                        buckets.summons.add(item);
                    } else if (isDinosaurEgg(item)) {
                        buckets.dinoEggs.add(item);
                    } else if (item instanceof BlockItem) {
                        buckets.blocks.add(item);
                    } else {
                        buckets.materials.add(item);
                    }
                }
            });
        }
    }

    private static void collectFrom(DeferredRegister<Item> registry, Buckets buckets) {
        registry.getEntries().forEach(entry -> {
            if (entry.isPresent()) {
                Item item = entry.get();
                if (isVanillaTabbed(item)) {
                    return;
                }
                if (item instanceof SpawnEggItem) {
                    buckets.spawnEggs.add(item);
                } else if (item instanceof MagicFocus) {
                    buckets.foci.add(item);
                } else if (isWeapon(item)) {
                    buckets.weapons.add(item);
                } else if (isMobArmor(item)) {
                    buckets.armor.add(item);
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

    private static void sortOther(List<Item> items) {
        List<Item> order = new ArrayList<>();
        order.add(ModItems.FEL_CORE.get());
        order.add(ModItems.HARMONIOUS_DIAMOND.get());
        order.add(ModItems.WOLF_TONGUE.get());
        order.add(ModItems.BAT_WING.get());
        order.add(ModItems.COLD_HEART.get());
        order.add(ModItems.ACID_FUNGUS.get());
        order.add(ModItems.BREEZE_ROD.get());
        order.add(ModItems.HURRICANE_CORE.get());

        order.add(ModItems.TRIAL_KEY.get());
        order.add(ModItems.OMINOUS_TRIAL_KEY.get());
        order.add(ModItems.WIND_CHARGE.get());
        order.add(ModItems.OMINOUS_BOTTLE.get());

        order.add(ModItems.WORMY_APPLE.get());
        order.add(ModItems.BURNING_POTION.get());
        order.add(ModItems.NETHER_WART_POTION.get());
        order.add(ModItems.WITCH_BOMB.get());

        order.add(ModItems.DARK_ANKH.get());
        order.add(ModItems.BASTION_SCROLL.get());
        order.add(ModItems.SUNKEN_SOUL_JAR.get());
        order.add(ModItems.STORM_SOUL_JAR.get());
        order.add(ModItems.THUNDER_HORN.get());
        order.add(ModItems.BROKEN_STORM_CROWN.get());
        order.add(ModItems.SCREAMING_SKULL_JAR.get());
        order.add(ModItems.FUNGUS_PACK.get());
        order.add(ModItems.RAGGED_FUNGUS_PACK.get());

        order.add(ModItems.CRONE_ROBE.get());
        order.add(ModItems.CRONE_ROBE_ALT.get());

        items.sort(Comparator.comparingInt(item -> {
            int index = order.indexOf(item);
            return index < 0 ? order.size() : index;
        }));
    }

    private static boolean isWeapon(Item item) {
        if (item instanceof TieredItem) return true;
        if (item instanceof DarkStaff) return true;
        if (item instanceof com.Polarice3.Goety.common.items.equipment.DarkScytheItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.BoneCudgelItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.FirebrandItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.CogCrossbowItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.PiglinPrideItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.PitchforkItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.WitchBowItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.MaceItem) return true;
        return false;
    }

    private static boolean isMobArmor(Item item) {
        return item instanceof ArmorItem
                || item instanceof CursedMetalWolfArmorItem
                || item instanceof CursedWargArmorItem
                || item instanceof CursedBlackBeastArmorItem;
    }

    private static boolean isRevive(Item item) {
        return item instanceof ReviveServantItem
                || item instanceof MysteriousContract
                || item instanceof WarpedSteroidsItem
                || item instanceof RaycatAmuletItem;
    }

    private static void sortRevive(List<Item> items) {
        items.sort(Comparator.comparingInt(item -> item instanceof RaycatAmuletItem ? 1 : 0));
    }

    private static boolean isSummon(Item item) {
        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof TrainingBlock
                    || blockItem.getBlock() instanceof MineGuardianBlock;
        }
        return false;
    }

    private static boolean isDinosaurEgg(Item item) {
        return item instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ServantEggBlock;
    }

    private static Set<Item> vanillaTabbedItems;

    private static boolean isVanillaTabbed(Item item) {
        if (vanillaTabbedItems == null) {
            Set<Item> items = new HashSet<>();
            items.add(ModItems.FLOW_POTTERY_SHERD.get());
            items.add(ModItems.GUSTER_POTTERY_SHERD.get());
            items.add(ModItems.SCRAPE_POTTERY_SHERD.get());
            items.add(ModItems.FLOW_BANNER_PATTERN.get());
            items.add(ModItems.GUSTER_BANNER_PATTERN.get());
            items.add(ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get());
            items.add(ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get());
            items.add(ModItems.MUSIC_DISC_CREATOR.get());
            items.add(ModItems.MUSIC_DISC_CREATOR_MUSIC_BOX.get());
            items.add(ModItems.MUSIC_DISC_PRECIPICE.get());
            items.add(ModItems.WIND_CHARGE.get());
            items.add(ModItems.MACE.get());
            items.add(ModItems.BREEZE_ROD.get());
            items.add(ModItems.HEAVY_CORE.get());
            items.add(ModItems.TRIAL_KEY.get());
            items.add(ModItems.OMINOUS_TRIAL_KEY.get());
            items.add(ModItems.OMINOUS_BOTTLE.get());
            TrialBlocks.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .filter(Item.BY_BLOCK::containsKey)
                    .map(Item.BY_BLOCK::get)
                    .forEach(items::add);
            vanillaTabbedItems = items;
        }
        return vanillaTabbedItems.contains(item);
    }
}
