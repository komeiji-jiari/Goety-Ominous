package com.qiuyue.someillagerservants.common.init;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.items.ModItems;
import com.qiuyue.someillagerservants.common.items.lm.LmItems;
import com.qiuyue.someillagerservants.common.items.mm.MmItems;
import com.qiuyue.someillagerservants.common.items.sar.SarItems;
import com.qiuyue.someillagerservants.common.items.ua.UaItems;
import com.qiuyue.someillagerservants.compat.ias.IasItems;
import com.qiuyue.someillagerservants.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.someillagerservants.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.someillagerservants.compat.mod.MutantMoreCompat;
import com.qiuyue.someillagerservants.compat.mod.SavageRavageCompat;
import com.qiuyue.someillagerservants.compat.mod.UpgradeAquaticCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
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
            .create(Registries.CREATIVE_MODE_TAB, SomeIllagerServants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register(SomeIllagerServants.MOD_ID, () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.DARK_ANKH.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup." + SomeIllagerServants.MOD_ID))
                    .displayItems((parameters, output) -> {
                        List<Item> spawnEggs = new ArrayList<>();
                        List<Item> foci = new ArrayList<>();
                        List<Item> otherItems = new ArrayList<>();

                        collectFrom(ModItems.ITEMS, spawnEggs, foci, otherItems);

                        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
                            collectFrom(IasItems.IAS_ITEMS, spawnEggs, foci, otherItems);
                        }

                        if (SavageRavageCompat.isSavageRavageLoaded()) {
                            collectFrom(SarItems.SAR_ITEMS, spawnEggs, foci, otherItems);
                        }

                        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
                            collectFrom(UaItems.UA_ITEMS, spawnEggs, foci, otherItems);
                        }

                        if (MutantMoreCompat.isMutantMoreLoaded()) {
                            collectFrom(MmItems.MM_ITEMS, spawnEggs, foci, otherItems);
                        }

                        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
                            collectFrom(LmItems.LM_ITEMS, spawnEggs, foci, otherItems);
                        }

                        spawnEggs.forEach(output::accept);
                        foci.forEach(output::accept);
                        otherItems.forEach(output::accept);
                    }).build());

    /**
     * 将注册表中的物品按类别归类
     */
    private static void collectFrom(DeferredRegister<Item> registry,
                                     List<Item> spawnEggs, List<Item> foci, List<Item> other) {
        registry.getEntries().forEach(entry -> {
            if (entry.isPresent()) {
                Item item = entry.get();
                if (item instanceof SpawnEggItem) {
                    spawnEggs.add(item);
                } else if (item instanceof MagicFocus) {
                    foci.add(item);
                } else {
                    other.add(item);
                }
            }
        });
    }
}
