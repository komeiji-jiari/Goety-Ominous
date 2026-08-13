package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.CursedWargArmorItem;
import com.qiuyue.goetyominous.common.items.DarkWargArmorItem;
import com.qiuyue.goetyominous.utils.WolfArmorCrackiness;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WargArmorDamageHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Warg)) return;

        ItemStack armor = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
        if (!(armor.getItem() instanceof CursedWargArmorItem)) return;

        if (event.getSource().is(DamageTypeTags.IS_FIRE)) return;

        float amount = event.getAmount();
        if (amount <= 0.0F) return;

        LivingEntity entity = event.getEntity();
        entity.playSound(ModSounds.WOLF_ARMOR_DAMAGE.get(), 1.0F, 1.0F);

        WolfArmorCrackiness before = WolfArmorCrackiness.byDamage(armor);
        int durabilityDamage = Math.max(1, Mth.floor(amount));
        if (armor.getItem() instanceof DarkWargArmorItem
                && (event.getSource().is(DamageTypeTags.IS_FIRE)
                || event.getSource().is(DamageTypeTags.IS_EXPLOSION)
                || event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO))) {
            durabilityDamage = Math.max(1, durabilityDamage / 2);
        }
        armor.hurtAndBreak(durabilityDamage, entity, (wolf) ->
                wolf.playSound(ModSounds.WOLF_ARMOR_BREAK.get(), 1.0F, 1.0F));

        WolfArmorCrackiness after = WolfArmorCrackiness.byDamage(armor);
        if (after != before && after != WolfArmorCrackiness.NONE) {
            entity.playSound(ModSounds.WOLF_ARMOR_CRACK.get(), 1.0F, 1.0F);
        }

        event.setAmount(0.0F);
    }
}
