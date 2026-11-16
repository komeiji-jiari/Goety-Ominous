package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.trial.TrialBlocks;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousIconItem;
import com.qiuyue.goetyominous.common.items.PlushieBlockItem;
import com.qiuyue.goetyominous.common.items.WargSpawnEggItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModCreativeTab {

    private static final List<CompatItemSource> COMPAT_ITEM_SOURCES = new ArrayList<>();
    private static final List<CompatItemSource> MAIN_TAB_ITEM_SOURCES = new ArrayList<>();

    public static void registerCompatItems(DeferredRegister<Item> registry) {
        registerCompatItems(CompatItemSource.of(registry));
    }

    public static void registerCompatItems(CompatItemSource source) {
        COMPAT_ITEM_SOURCES.add(source);
    }

    public static void registerMainTabItems(DeferredRegister<Item> registry) {
        MAIN_TAB_ITEM_SOURCES.add(CompatItemSource.of(registry));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GoetyOminous.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register(GoetyOminous.MOD_ID, () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.DARK_ANKH.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + GoetyOminous.MOD_ID))
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        Buckets buckets = Buckets.collect();
                        buckets.staves.forEach(output::accept);
                        buckets.weapons.forEach(output::accept);
                        buckets.curios.forEach(output::accept);
                        buckets.armor.forEach(output::accept);
                        buckets.revive.forEach(output::accept);
                        buckets.materials.forEach(output::accept);
                        buckets.other.forEach(output::accept);
                        buckets.spawnEggs.forEach(output::accept);
                    }).build());

    public static final RegistryObject<CreativeModeTab> BLOCKS_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_blocks", () -> CreativeModeTab.builder()
                    .icon(() -> ModBlocks.CULT_STATUE.get().asItem().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.blocks"))
                    .withTabsBefore(MAIN_TAB.getKey())
                    .withSearchBar()
                    .displayItems((parameters, output) ->
                            Buckets.collect().blocks.forEach(output::accept)).build());

    public static final RegistryObject<CreativeModeTab> SERVANT_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_servants", () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.STORM_SOUL_JAR.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.servants"))
                    .withTabsBefore(BLOCKS_TAB.getKey())
                    .withSearchBar()
                    .displayItems((parameters, output) ->
                            Buckets.collect().servantEggs.forEach(output::accept)).build());

    public static final RegistryObject<CreativeModeTab> FOCUS_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_focuses", () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.SPIDER_FOCUS.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.focuses"))
                    .withTabsBefore(SERVANT_TAB.getKey())
                    .withSearchBar()
                    .displayItems((parameters, output) ->
                            Buckets.collect().foci.forEach(output::accept)).build());

    public static final RegistryObject<CreativeModeTab> PLUSHIE_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_plushies", () -> CreativeModeTab.builder()
                    .icon(() -> ModBlocks.PLUSHIE_HIM.get().asItem().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.plushies"))
                    .withTabsBefore(FOCUS_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        ModBlocks.PLUSHIES.forEach(block -> output.accept(block.get()));
                    }).build());

    public static final RegistryObject<CreativeModeTab> COMPAT_TAB = CREATIVE_MODE_TABS
            .register("goetyominous_compat", () -> CreativeModeTab.builder()
                    .icon(() -> com.Polarice3.Goety.common.items.ModItems.HUNGER_CORE.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.goetyominous.compat"))
                    .withTabsBefore(PLUSHIE_TAB.getKey())
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

    private static final class Buckets {
        final List<Item> spawnEggs = new ArrayList<>();
        final List<Item> servantEggs = new ArrayList<>();
        final List<Item> foci = new ArrayList<>();
        final List<Item> staves = new ArrayList<>();
        final List<Item> weapons = new ArrayList<>();
        final List<Item> curios = new ArrayList<>();
        final List<Item> armor = new ArrayList<>();
        final List<Item> materials = new ArrayList<>();
        final List<Item> revive = new ArrayList<>();
        final List<Item> other = new ArrayList<>();
        final List<Item> blocks = new ArrayList<>();

        static Buckets collect() {
            Buckets buckets = new Buckets();
            collectFrom(ModItems.ITEMS, buckets);
            MAIN_TAB_ITEM_SOURCES.forEach(source -> collectFrom(source.registry(), buckets));
            Item croneHat = ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "crone_hat"));
            if (croneHat != null) {
                int robeIndex = buckets.curios.indexOf(com.goety_soul_witch.registry.GoetyOminousItems.CRONE_ROBE.get());
                buckets.curios.add(robeIndex < 0 ? buckets.curios.size() : robeIndex, croneHat);
            }
            sortMaterials(buckets.materials);
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
        final List<Item> reviveLast = new ArrayList<>();
        final List<Item> summons = new ArrayList<>();
        final List<Item> blocks = new ArrayList<>();
        final List<Item> dinoEggs = new ArrayList<>();
        final List<Item> spawnEggs = new ArrayList<>();

        static CompatBuckets collect() {
            CompatBuckets buckets = new CompatBuckets();

            COMPAT_ITEM_SOURCES.forEach(source -> collectFrom(source.registry(), source, buckets));

            buckets.revive.addAll(buckets.reviveLast);
            return buckets;
        }

        private static void collectFrom(DeferredRegister<Item> registry, CompatItemSource source, CompatBuckets buckets) {
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
                    } else if (source.isRevive(item)) {
                        if (source.reviveLast(item)) {
                            buckets.reviveLast.add(item);
                        } else {
                            buckets.revive.add(item);
                        }
                    } else if (item instanceof BlockItem blockItem && source.isSummon(blockItem.getBlock())) {
                        buckets.summons.add(item);
                    } else if (item instanceof BlockItem blockItem && source.isDinosaurEgg(blockItem.getBlock())) {
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
                if (isVanillaTabbed(item)
                        || item instanceof PlushieBlockItem
                        || item instanceof OminousIconItem) {
                    return;
                }
                if (item instanceof SpawnEggItem) {
                    if (isServantEgg(item)) {
                        buckets.servantEggs.add(item);
                    } else {
                        buckets.spawnEggs.add(item);
                    }
                } else if (item instanceof DarkStaff) {
                    buckets.staves.add(item);
                } else if (item instanceof ICurioItem) {
                    buckets.curios.add(item);
                } else if (item instanceof MagicFocus) {
                    buckets.foci.add(item);
                } else if (isWeapon(item)) {
                    buckets.weapons.add(item);
                } else if (isMobArmor(item)) {
                    buckets.armor.add(item);
                } else if (item instanceof ReviveServantItem) {
                    buckets.revive.add(item);
                } else if (isMaterial(item)) {
                    buckets.materials.add(item);
                } else if (item instanceof BlockItem) {
                    buckets.blocks.add(item);
                } else {
                    buckets.other.add(item);
                }
            }
        });
    }

    private static void sortMaterials(List<Item> items) {
        List<Item> order = new ArrayList<>();
        order.add(com.goety_soul_witch.registry.GoetyOminousItems.FEL_CORE.get());
        order.add(com.goety_soul_witch.registry.GoetyOminousItems.HARMONIOUS_DIAMOND.get());
        order.add(com.goety_soul_witch.registry.GoetyOminousItems.WOLF_TONGUE.get());
        order.add(com.goety_soul_witch.registry.GoetyOminousItems.BAT_WING.get());
        order.add(com.goety_soul_witch.registry.GoetyOminousItems.WORMY_APPLE.get());
        order.add(ModItems.COLD_HEART.get());
        order.add(ModItems.NETHER_WART_POTION.get());
        order.add(ModItems.BROKEN_STORM_CROWN.get());

        items.sort(Comparator.comparingInt(item -> {
            int index = order.indexOf(item);
            return index < 0 ? order.size() : index;
        }));
    }

    private static void sortOther(List<Item> items) {
        List<Item> order = new ArrayList<>();
        order.add(ModItems.THUNDER_HORN.get());
        order.add(ModItems.BASTION_SCROLL.get());
        order.add(ModItems.ACID_FUNGUS.get());
        order.add(ModItems.BURNING_POTION.get());
        order.add(ModItems.WITCH_BOMB.get());

        items.sort(Comparator.comparingInt(item -> {
            int index = order.indexOf(item);
            return index < 0 ? order.size() : index;
        }));
    }

    private static Set<Item> materialItems;

    private static boolean isMaterial(Item item) {
        if (materialItems == null) {
            Set<Item> items = new HashSet<>();
            items.add(com.goety_soul_witch.registry.GoetyOminousItems.FEL_CORE.get());
            items.add(com.goety_soul_witch.registry.GoetyOminousItems.HARMONIOUS_DIAMOND.get());
            items.add(com.goety_soul_witch.registry.GoetyOminousItems.WOLF_TONGUE.get());
            items.add(com.goety_soul_witch.registry.GoetyOminousItems.BAT_WING.get());
            items.add(com.goety_soul_witch.registry.GoetyOminousItems.WORMY_APPLE.get());
            items.add(ModItems.COLD_HEART.get());
            items.add(ModItems.NETHER_WART_POTION.get());
            items.add(ModItems.BROKEN_STORM_CROWN.get());
            materialItems = items;
        }
        return materialItems.contains(item);
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
        if (item instanceof com.qiuyue.goetyominous.common.items.WindBowItem) return true;
        if (item instanceof com.qiuyue.goetyominous.common.items.MaceItem) return true;
        return false;
    }

    private static boolean isMobArmor(Item item) {
        return item instanceof ArmorItem;
    }

    private static boolean isServantEgg(Item item) {
        return item instanceof ServantSpawnEggItem
                && !(item instanceof WargSpawnEggItem warg && warg.isHostile());
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
