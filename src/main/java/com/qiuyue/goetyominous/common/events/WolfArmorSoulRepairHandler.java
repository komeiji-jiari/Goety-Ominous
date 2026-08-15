package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.DarkWolfArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WolfArmorSoulRepairHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Wolf)
                && !(entity instanceof BlackWolf)
                && !(entity instanceof SkeletonWolf)) return;
        if (!(entity instanceof IOwned owned) || !(owned.getTrueOwner() instanceof Player owner)) return;
        if (owner.isCreative()) return;

        ItemStack armor = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!(armor.getItem() instanceof DarkWolfArmorItem)) return;
        if (!armor.isDamaged()) return;
        if (!ItemConfig.SoulRepair.get()) return;
        if (!SEHelper.getSoulsContainer(owner)) return;

        int cost = ItemConfig.ItemsRepairAmount.get();
        if (SEHelper.getSoulsAmount(owner, cost) && entity.tickCount % 40 == 0) {
            armor.setDamageValue(armor.getDamageValue() - 1);
            SEHelper.decreaseSouls(owner, cost);
        }
    }
}
