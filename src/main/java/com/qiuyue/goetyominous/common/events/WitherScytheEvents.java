package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.WitherSlash;
import com.qiuyue.goetyominous.common.items.mm.WitherScytheItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WitherScytheEvents {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        var stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof WitherScytheItem) {
            WitherScytheItem.emptyClick(stack);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        var stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof WitherScytheItem) {
            var level = event.getLevel();
            var pos = event.getPos();
            if (level.getBlockState(pos).getDestroySpeed(level, pos) == 0.0F) {
                WitherScytheItem.emptyClick(stack);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        WitherScytheItem.entityClick(event.getEntity(), event.getEntity().level());
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        var source = event.getSource().getDirectEntity();
        if (!(source instanceof WitherSlash slash)) return;
        if (!(slash.getOwner() instanceof LivingEntity)) return;

        LivingEntity target = event.getEntity();

        if (target.hasEffect(MobEffects.WITHER) && target.hasEffect(GoetyEffects.CURSED.get())) {
            if (target.getRandom().nextFloat() < 0.1F) {
                int currentWither = target.getEffect(MobEffects.WITHER).getAmplifier();
                int currentBusted = target.getEffect(GoetyEffects.CURSED.get()).getAmplifier();
                int newWither = Math.min(currentWither + 1, 2);
                int newBusted = Math.min(currentBusted + 1, 2);
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, newWither));
                target.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 400, newBusted));
            }
        }
    }
}
