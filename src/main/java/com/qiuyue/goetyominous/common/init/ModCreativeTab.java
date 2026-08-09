package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.common.items.lm.LmItems;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import com.qiuyue.goetyominous.common.items.spear.SpearItems;
import com.qiuyue.goetyominous.common.items.ua.UaItems;
import com.qiuyue.goetyominous.compat.ias.IasItems;
import com.qiuyue.goetyominous.compat.mod.*;
import com.qiuyue.goetyominous.compat.spear.SpearBackportCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 模组创造模式物品栏注册类
 * 负责创建本模组的创造模式物品栏标签页
 */
public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GoetyOminous.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register(GoetyOminous.MOD_ID, () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.DARK_ANKH.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + GoetyOminous.MOD_ID))
                    .displayItems((parameters, output) -> {
                        List<Item> spawnEggs = new ArrayList<>();
                        List<Item> foci = new ArrayList<>();
                        List<Item> weapons = new ArrayList<>();
                        List<Item> otherItems = new ArrayList<>();

                        collectFrom(ModItems.ITEMS, spawnEggs, foci, weapons, otherItems);

                        if (SpearBackportCompat.isSpearBackportLoaded()) {
                            collectFrom(SpearItems.SPEAR_ITEMS, spawnEggs, foci, weapons, otherItems);
                        }

                        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
                            collectFrom(IasItems.IAS_ITEMS, spawnEggs, foci, weapons, otherItems);
                        }

                        if (SavageRavageCompat.isSavageRavageLoaded()) {
                            collectFrom(SarItems.SAR_ITEMS, spawnEggs, foci, weapons, otherItems);
                        }

                        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
                            collectFrom(UaItems.UA_ITEMS, spawnEggs, foci, weapons, otherItems);
                        }

                        if (MutantMoreCompat.isMutantMoreLoaded()) {
                            collectFrom(MmItems.MM_ITEMS, spawnEggs, foci, weapons, otherItems);
                        }

                        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
                            collectFrom(LmItems.LM_ITEMS, spawnEggs, foci, weapons, otherItems);
                            moveAfter(otherItems, ModItems.ARCHGEOMANCER_MUSIC_DISC.get(), LmItems.LM_MUSIC_DISC.get());
                        }

                        if (AlexMobsCompat.isAlexMobsLoaded()) {
                            collectFrom(AmItems.AM_ITEMS, spawnEggs, foci, weapons, otherItems);
                            moveAfter(otherItems, AmItems.WARPED_STEROIDS.get(), ModItems.NETHER_WART_POTION.get());
                        }

                        spawnEggs.forEach(output::accept);
                        foci.forEach(output::accept);
                        weapons.forEach(output::accept);
                        otherItems.forEach(output::accept);
                    }).build());

    /**
     * 将注册表中的物品按类别归类
     */
    private static void collectFrom(DeferredRegister<Item> registry,
                                     List<Item> spawnEggs, List<Item> foci,
                                     List<Item> weapons, List<Item> other) {
        registry.getEntries().forEach(entry -> {
            if (entry.isPresent()) {
                Item item = entry.get();
                if (item instanceof SpawnEggItem) {
                    spawnEggs.add(item);
                } else if (item instanceof MagicFocus) {
                    foci.add(item);
                } else if (isWeapon(item)) {
                    weapons.add(item);
                } else {
                    other.add(item);
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
