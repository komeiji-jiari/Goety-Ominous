package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.items.BoneCudgelItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BoneCudgelEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;

        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.getItem() instanceof BoneCudgelItem
                    && event.getEntity().getType().is(net.minecraft.tags.EntityTypeTags.RAIDERS)) {
                event.setAmount(event.getAmount() * 1.25F);
            }
        }
    }
}
