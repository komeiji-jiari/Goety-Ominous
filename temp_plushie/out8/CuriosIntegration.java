package com.Polarice3.Goety.compat.curios;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosIntegration implements ICompatable {
    private static final Map<Item, String> TYPES = ImmutableMap.builder().put((Item)ModItems.RING_OF_WANT.get(), "ring").put((Item)ModItems.RING_OF_THIRST.get(), "ring").put((Item)ModItems.RING_OF_FORCE.get(), "ring").put((Item)ModItems.RING_OF_THE_FORGE.get(), "ring").put((Item)ModItems.RING_OF_THE_DRAGON.get(), "ring").put((Item)ModItems.DARK_HAT.get(), "head").put((Item)ModItems.GRAND_TURBAN.get(), "head").put((Item)ModItems.FROST_CROWN.get(), "head").put((Item)ModItems.WIND_CROWN.get(), "head").put((Item)ModItems.STORM_CROWN.get(), "head").put((Item)ModItems.WILD_CROWN.get(), "head").put((Item)ModItems.ABYSS_CROWN.get(), "head").put((Item)ModItems.VOID_CROWN.get(), "head").put((Item)ModItems.NETHER_CROWN.get(), "head").put((Item)ModItems.WITCH_HAT.get(), "head").put((Item)ModItems.WITCH_HAT_HEDGE.get(), "head").put((Item)ModItems.CRONE_HAT.get(), "head").put((Item)ModItems.UNHOLY_HAT.get(), "head").put((Item)ModItems.UNHOLY_HAT_HALO.get(), "head").put((Item)ModItems.NECRO_CROWN.get(), "head").put((Item)ModItems.NAMELESS_CROWN.get(), "head").put((Item)ModItems.TARGETING_MONOCLE.get(), "head").put((Item)ModItems.AMETHYST_NECKLACE.get(), "necklace").put((Item)ModItems.PENDANT_OF_HUNGER.get(), "necklace").put((Item)ModItems.STAR_AMULET.get(), "necklace").put((Item)ModItems.SEA_AMULET.get(), "necklace").put((Item)ModItems.FELINE_AMULET.get(), "necklace").put((Item)ModItems.DARK_ROBE.get(), "body").put((Item)ModItems.DARK_ROBE_FANCY.get(), "body").put((Item)ModItems.GRAND_ROBE.get(), "body").put((Item)ModItems.GEO_ROBE.get(), "body").put((Item)ModItems.FROST_ROBE.get(), "body").put((Item)ModItems.FROST_ROBE_CRYO.get(), "body").put((Item)ModItems.WIND_ROBE.get(), "body").put((Item)ModItems.STORM_ROBE.get(), "body").put((Item)ModItems.WILD_ROBE.get(), "body").put((Item)ModItems.ABYSS_ROBE.get(), "body").put((Item)ModItems.VOID_ROBE.get(), "body").put((Item)ModItems.NETHER_ROBE.get(), "body").put((Item)ModItems.NETHER_ROBE_WARPED.get(), "body").put((Item)ModItems.ILLUSION_ROBE.get(), "body").put((Item)ModItems.ILLUSION_ROBE_MIRROR.get(), "body").put((Item)ModItems.WITCH_ROBE.get(), "body").put((Item)ModItems.WITCH_ROBE_HEDGE.get(), "body").put((Item)ModItems.WARLOCK_ROBE.get(), "body").put((Item)ModItems.WARLOCK_ROBE_DARK.get(), "body").put((Item)ModItems.UNHOLY_ROBE.get(), "body").put((Item)ModItems.NECRO_CAPE.get(), "back").put((Item)ModItems.NAMELESS_CAPE.get(), "back").put((Item)ModItems.ETERNAL_CAULDRON.get(), "back").put((Item)ModItems.GRAVE_GLOVE.get(), "hands").put((Item)ModItems.THRASH_GLOVE.get(), "hands").put((Item)ModItems.TOTEM_OF_ROOTS.get(), "charm").put((Item)ModItems.TOTEM_OF_SOULS.get(), "charm").put((Item)ModItems.ALARMING_CHARM.get(), "charm").put((Item)ModItems.OMINOUS_CHARM.get(), "charm").put((Item)ModItems.FOCUS_BAG.get(), "belt").put((Item)ModItems.FOCUS_PACK.get(), "belt").put((Item)ModItems.BREW_BAG.get(), "belt").put((Item)ModItems.WARLOCK_SASH.get(), "belt").put((Item)ModItems.WAYFARERS_BELT.get(), "belt").put((Item)ModItems.SPITEFUL_BELT.get(), "belt").build();

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addListener(this::registerCapabilities);
    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach((t) -> InterModComms.sendTo("curios", "register_type", () -> (new SlotTypeMessage.Builder(t)).build()));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        TYPES.keySet().forEach((entry) -> {
            if (entry instanceof SingleStackItem item) {
                CuriosApi.registerCurio(item, new SingleStackItem());
            }

        });
    }
}
