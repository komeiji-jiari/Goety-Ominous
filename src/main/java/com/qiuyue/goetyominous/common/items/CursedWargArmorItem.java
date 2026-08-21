package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ModItems;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedWargArmorItem extends Item {
    private final ResourceLocation texture;

    public static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/warg_cursed_metal_armor.png");

    public CursedWargArmorItem(Item.Properties properties) {
        this(properties, TEXTURE, WeaponConfig.WargArmorDurability.get());
    }

    protected CursedWargArmorItem(Item.Properties properties, ResourceLocation texture, int durability) {
        super(properties.stacksTo(1).durability(durability).fireResistant());
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.CURSED_METAL_INGOT.get());
    }
}