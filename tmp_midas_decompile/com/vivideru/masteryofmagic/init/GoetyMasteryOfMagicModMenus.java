/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraftforge.common.extensions.IForgeMenuType
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.client.inventory.container.MasterStaffContainer;
import com.vivideru.masteryofmagic.client.inventory.container.SpellRingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.MENU_TYPES, (String)"goety_mastery_of_magic");
    public static final RegistryObject<MenuType<SpellRingContainer>> SPELL_RING = REGISTRY.register("spell_ring", () -> IForgeMenuType.create(SpellRingContainer::createContainerClientSide));
    public static final RegistryObject<MenuType<MasterStaffContainer>> MASTER_STAFF = REGISTRY.register("master_staff", () -> IForgeMenuType.create(MasterStaffContainer::createContainerClientSide));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

