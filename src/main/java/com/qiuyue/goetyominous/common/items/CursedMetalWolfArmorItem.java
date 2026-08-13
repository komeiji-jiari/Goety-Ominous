package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ModItems;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.WolfArmorCrackiness;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CursedMetalWolfArmorItem extends Item {
    public static final ResourceLocation VANILLA_WOLF_TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/cursed_metal_wolf_armor.png");
    public static final ResourceLocation BLACK_WOLF_TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/cursed_metal_black_wolf_armor.png");

    private final ResourceLocation vanillaWolfTexture;
    private final ResourceLocation blackWolfTexture;

    public CursedMetalWolfArmorItem(Properties properties) {
        this(properties, VANILLA_WOLF_TEXTURE, BLACK_WOLF_TEXTURE, 78);
    }

    protected CursedMetalWolfArmorItem(Properties properties,
                                       ResourceLocation vanillaWolfTexture, ResourceLocation blackWolfTexture, int durability) {
        super(properties.stacksTo(1).durability(durability).fireResistant());
        this.vanillaWolfTexture = vanillaWolfTexture;
        this.blackWolfTexture = blackWolfTexture;
    }

    public ResourceLocation getTexture() { return this.vanillaWolfTexture; }
    public ResourceLocation getBlackWolfTexture() { return this.blackWolfTexture; }

    public static ResourceLocation getCrackTexture(WolfArmorCrackiness crack) {
        String name = switch (crack) {
            case LOW -> "wolf_armor_crackiness_low";
            case MEDIUM -> "wolf_armor_crackiness_medium";
            case HIGH -> "wolf_armor_crackiness_high";
            default -> "wolf_armor";
        };
        return new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/wolf/" + name + ".png");
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.CURSED_METAL_INGOT.get());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
