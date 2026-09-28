package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.entities.ai.attributes.SpellAttribute;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,
            GoetyOminous.MOD_ID);

    public static final RegistryObject<Attribute> FEL_POTENCY =
            ATTRIBUTES.register("fel_potency",
                    () -> SpellAttribute.potency(GoetyOminous.FEL, 0.0D, 0.0D, 2048.0D).setSyncable(true));

    public static final RegistryObject<Attribute> FEL_DISCOUNT =
            ATTRIBUTES.register("fel_discount",
                    () -> SpellAttribute.discount(GoetyOminous.FEL, 0.0D, -1.0D, 1.0D).setSyncable(true));

    public static void init() {
        ModAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
