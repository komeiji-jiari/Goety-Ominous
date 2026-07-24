package com.qiuyue.someillagerservants.common.items;

import com.qiuyue.someillagerservants.common.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class PiglinPrideItem extends CogCrossbowItem {

    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    public PiglinPrideItem() {
        super(1860);
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    protected float getVelocityMultiplier() {
        return 1.5F;
    }

    @Override
    protected int getExtraPiercing() {
        return 4;
    }

    @Override
    protected SoundEvent[] getShootSounds() {
        return new SoundEvent[]{ModSounds.PIGLIN_PRIDE_SHOOT_1.get(),
                ModSounds.PIGLIN_PRIDE_SHOOT_2.get(), ModSounds.PIGLIN_PRIDE_SHOOT_3.get()};
    }
}
