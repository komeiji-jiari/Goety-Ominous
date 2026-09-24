package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.entities.ai.attributes.SpellAttribute;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {

    public static final RegistryObject<Attribute> FEL_POTENCY =
            com.Polarice3.Goety.init.ModAttributes.ATTRIBUTES.register("fel_potency",
                    () -> SpellAttribute.potency(GoetyOminous.FEL, 0.0D, 0.0D, 2048.0D).setSyncable(true));

    public static final RegistryObject<Attribute> FEL_DISCOUNT =
            com.Polarice3.Goety.init.ModAttributes.ATTRIBUTES.register("fel_discount",
                    () -> SpellAttribute.discount(GoetyOminous.FEL, 0.0D, -1.0D, 1.0D).setSyncable(true));

    public static void init() {
    }
}
