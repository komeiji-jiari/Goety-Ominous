package com.qiuyue.goetyominus.common.init;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.items.ModItems;
import com.qiuyue.goetyominus.common.items.lm.LmItems;
import com.qiuyue.goetyominus.common.items.mm.MmItems;
import com.qiuyue.goetyominus.common.items.sar.SarItems;
import com.qiuyue.goetyominus.common.items.ua.UaItems;
import com.qiuyue.goetyominus.compat.ias.IasItems;
import com.qiuyue.goetyominus.compat.mod.IllageAndSpillageCompat;
import com.qiuyue.goetyominus.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.goetyominus.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominus.compat.mod.SavageRavageCompat;
import com.qiuyue.goetyominus.compat.mod.UpgradeAquaticCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    private static boolean isWeapon(Item item) {
        if (item instanceof TieredItem) return true;
        if (item instanceof com.qiuyue.goetyominus.common.items.BoneCudgelItem) return true;
        if (item instanceof com.qiuyue.goetyominus.common.items.FirebrandItem) return true;
        if (item instanceof com.qiuyue.goetyominus.common.items.CogCrossbowItem) return true;
        if (item instanceof com.qiuyue.goetyominus.common.items.PiglinPrideItem) return true;
        if (item instanceof com.qiuyue.goetyominus.common.items.PitchforkItem) return true;
        return false;
    }
}
