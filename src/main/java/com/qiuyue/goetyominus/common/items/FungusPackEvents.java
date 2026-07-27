package com.qiuyue.goetyominus.common.items;

import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import com.Polarice3.Goety.common.entities.projectiles.SnapFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ExplosionUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.qiuyue.goetyominus.GoetyOminous.MOD_ID)
public class FungusPackEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof SnapFungus snap) {
            handleSnapFungus(snap, event);
        } else if (event.getProjectile() instanceof BlastFungus blast) {
            handleBlastFungus(blast, event);
        }
    }

    private static void handleSnapFungus(SnapFungus snap, ProjectileImpactEvent event) {
        if (snap.level().isClientSide) return;
        if (!(snap.getOwner() instanceof LivingEntity owner)) return;
        if (!FungusPackHelper.hasMatchingFungus(owner, ModItems.SNAP_FUNGUS.get())) return;

        event.setCanceled(true);

        ExplosionUtil.fungusExplode(snap.level(), snap,
                snap.getX(), snap.getY(), snap.getZ(), 2.5F, snap.isOnFire());

        var cloud = new com.Polarice3.Goety.common.entities.util.AlliedEffectCloud(
                snap.level(), snap.getX(), snap.getY(), snap.getZ());
        if (owner != null) cloud.setOwner(owner);
        cloud.setRadius(1.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(cloud.getDuration() / 2);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON,
                MathHelper.secondsToTicks(11), 1));
        snap.level().addFreshEntity(cloud);

        snap.discard();
    }

    private static void handleBlastFungus(BlastFungus blast, ProjectileImpactEvent event) {
        if (blast.level().isClientSide) return;
        if (!(blast.getOwner() instanceof LivingEntity owner)) return;
        if (!FungusPackHelper.hasMatchingFungus(owner, ModItems.BLAST_FUNGUS.get())) return;

        event.setCanceled(true);

        ExplosionUtil.fungusExplode(blast.level(), blast,
                blast.getX(), blast.getY(), blast.getZ(), 4.0F, blast.isOnFire());

        blast.discard();
    }
}


