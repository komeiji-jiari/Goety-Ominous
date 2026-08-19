package com.qiuyue.goetyominous.compat.curios;

import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.common.items.curios.DarkAnkh;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class CuriosIntegration {

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        MinecraftForge.EVENT_BUS.addListener(this::registerCapabilities);
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        Supplier<top.theillusivec4.curios.api.SlotTypeMessage> messageSupplier =
                () -> new top.theillusivec4.curios.api.SlotTypeMessage.Builder("charm").build();
        InterModComms.sendTo("curios", top.theillusivec4.curios.api.SlotTypeMessage.REGISTER_TYPE, messageSupplier);
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("back").build());
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        CuriosApi.registerCurio(ModItems.DARK_ANKH.get(), new DarkAnkh());
        CuriosApi.registerCurio(ModItems.FUNGUS_PACK.get(), (ICurioItem) ModItems.FUNGUS_PACK.get());
        if (AlexMobsCompat.isAlexMobsLoaded()) {
            CuriosApi.registerCurio(AmItems.JERBOA_AMULET.get(), (ICurioItem) AmItems.JERBOA_AMULET.get());
        }
    }
}
