package com.qiuyue.goetyominous.common.items.spear;

import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.config.ItemConfig;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class SpearItems {

    public static final DeferredRegister<Item> SPEAR_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<Item> DARK_SPEAR = SPEAR_ITEMS.register(
            "dark_spear",
            () -> new DarkSpearItem(ModTiers.DARK,
                    new Item.Properties().durability(ItemConfig.DarkToolsDurability.get())));

    public static void register(IEventBus modEventBus) {
        SPEAR_ITEMS.register(modEventBus);
    }

}
