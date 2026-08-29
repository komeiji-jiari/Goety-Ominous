package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CroneRobeEvents {

    @SubscribeEvent
    public static void onTarget(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalTarget();
        if (attacker instanceof Mob mobAttacker && target instanceof Player) {
            if (MobUtil.isWitchType(mobAttacker) && CroneCuriosUtil.hasCroneSet(target)) {
                if (mobAttacker.getLastHurtByMob() != target) {
                    if (event.getTargetType() == LivingChangeTargetEvent.LivingTargetType.MOB_TARGET) {
                        event.setNewTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                } else {
                    mobAttacker.setLastHurtByMob(target);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        float finalDamage = event.getAmount();
        if (CroneCuriosUtil.hasCroneRobe(victim) && event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            finalDamage *= 0.15F;
        }
        if (finalDamage != event.getAmount()) {
            event.setAmount(finalDamage);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && CroneCuriosUtil.hasCroneSet(entity)) {
            entity.spawnAtLocation(Items.STICK);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        LivingEntity owner = MobUtil.getOwner(entity);
        if (owner != null && CroneCuriosUtil.hasCroneRobe(owner)
                && entity.getType().is(ModTags.EntityTypes.FEL_HEAL)
                && entity.getHealth() < entity.getMaxHealth()
                && entity.tickCount % 100 == 0) {
            entity.heal(1.0F);
        }

        if (owner != null && CroneCuriosUtil.hasCroneHat(owner)
                && entity.getType().is(ModTags.EntityTypes.FEL_HEAL)
                && entity instanceof com.Polarice3.Goety.api.entities.ally.IServant servant) {
            servant.setHasLifespan(false);
        }
    }

    @SubscribeEvent
    public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && CroneCuriosUtil.hasCroneSet(player) && player.tickCount % 5 == 0) {
            player.level().addParticle(ParticleTypes.WITCH,
                    player.getX(), player.getY() + player.getBbHeight() + 0.3D, player.getZ(),
                    0.0D, 0.02D, 0.0D);
        }
    }
}
