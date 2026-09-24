package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class ServantKillCreditHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        if (victim.getKillCredit() instanceof Player) {
            return;
        }
        Player master = masterOf(event.getSource());
        if (master == null || master == victim) {
            return;
        }
        victim.setLastHurtByPlayer(master);
    }

    private static Player masterOf(DamageSource source) {
        Player master = masterOf(source.getEntity());
        return master != null ? master : masterOf(source.getDirectEntity());
    }

    private static Player masterOf(Entity attacker) {
        if (attacker instanceof IOwned owned) {
            return owned.getMasterOwner() instanceof Player player ? player : null;
        }
        if (attacker instanceof Projectile projectile && projectile.getOwner() instanceof IOwned owned) {
            return owned.getMasterOwner() instanceof Player player ? player : null;
        }
        return null;
    }
}
