package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ModItems;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedBlackBeastArmorItem extends Item {
    private final ResourceLocation texture;

    public static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/black_beast_cursed_armor.png");

    public CursedBlackBeastArmorItem(Item.Properties properties) {
        this(properties, TEXTURE, 78);
    }

    protected CursedBlackBeastArmorItem(Item.Properties properties, ResourceLocation texture, int durability) {
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
