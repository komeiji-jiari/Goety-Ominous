/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.common.items.magic.MagicFocus
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.vivideru.masteryofmagic.magic.spells.DodgingSpell;
import com.vivideru.masteryofmagic.magic.spells.FadingSpell;
import com.vivideru.masteryofmagic.magic.spells.FireshotSpell;
import com.vivideru.masteryofmagic.magic.spells.FocusWildfireSpell;
import com.vivideru.masteryofmagic.magic.spells.IceMonarchSpell;
import com.vivideru.masteryofmagic.magic.spells.MagicCounterSpell;
import com.vivideru.masteryofmagic.magic.spells.MiningCurseSpell;
import com.vivideru.masteryofmagic.magic.spells.TerraformingSpell;
import com.vivideru.masteryofmagic.magic.spells.TimeStopSpell;
import com.vivideru.masteryofmagic.spells.SoulBarrierSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class ModFocuses {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<Item> MINING_CURSE_FOCUS = ITEMS.register("mining_curse_focus", () -> new MagicFocus((ISpell)new MiningCurseSpell()));
    public static final RegistryObject<Item> FIRESHOT_FOCUS = ITEMS.register("fireshot_focus", () -> new MagicFocus((ISpell)new FireshotSpell()));
    public static final RegistryObject<Item> FOCUS_WILDFIRE_FOCUS = ITEMS.register("focus_wildfire_focus", () -> new MagicFocus((ISpell)new FocusWildfireSpell()));
    public static final RegistryObject<Item> ICE_MONARCH_FOCUS = ITEMS.register("ice_monarch_focus", () -> new MagicFocus((ISpell)new IceMonarchSpell()));
    public static final RegistryObject<Item> TIME_STOP_FOCUS = ITEMS.register("time_stop_focus", () -> new MagicFocus((ISpell)new TimeStopSpell()));
    public static final RegistryObject<Item> TERRAFORMING_FOCUS = ITEMS.register("terraforming_focus", () -> new MagicFocus((ISpell)new TerraformingSpell()));
    public static final RegistryObject<Item> SOUL_BARRIER_FOCUS = ITEMS.register("soul_barrier_focus", () -> new MagicFocus((ISpell)new SoulBarrierSpell()));
    public static final RegistryObject<Item> MAGIC_COUNTER_FOCUS = ITEMS.register("magic_counter_focus", () -> new MagicFocus((ISpell)new MagicCounterSpell()));
    public static final RegistryObject<Item> DODGING_FOCUS = ITEMS.register("dodging_focus", () -> new MagicFocus((ISpell)new DodgingSpell()));
    public static final RegistryObject<Item> FADING_FOCUS = ITEMS.register("fading_focus", () -> new MagicFocus((ISpell)new FadingSpell()));
    public static final RegistryObject<Item> NECROMANCER_FOCUS = null;

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

