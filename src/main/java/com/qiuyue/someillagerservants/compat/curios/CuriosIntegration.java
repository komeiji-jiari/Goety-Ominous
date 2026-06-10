package com.qiuyue.someillagerservants.compat.curios;

import com.qiuyue.someillagerservants.common.items.ModItems;
import com.qiuyue.someillagerservants.common.items.curios.DarkAnkh;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;

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
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        CuriosApi.registerCurio(ModItems.DARK_ANKH.get(), new DarkAnkh());
    }
}
