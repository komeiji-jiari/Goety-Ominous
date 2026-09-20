package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.am.EmuServant;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EmuServantDodgeEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!AlexMobsCompat.isAlexMobsLoaded()) return;
        HitResult hitResult = event.getRayTraceResult();
        if (!(hitResult instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof EmuServant emu)) return;
        if (emu.level().isClientSide) return;

        Entity projectile = event.getEntity();
        if (projectile instanceof AbstractArrow arrow) {
            arrow.setPierceLevel((byte) 0);
        }
        if (emu.getAnimation() == EmuServant.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EmuServant.ANIMATION_DODGE_LEFT) {
            if (emu.getAnimationTick() < 7) {
                event.setCanceled(true);
            }
            return;
        }
        Vec3 projectilePos = projectile.position();
        Vec3 rightVector = emu.getLookAngle().yRot(1.5707964F).add(emu.position());
        Vec3 leftVector = emu.getLookAngle().yRot(-1.5707964F).add(emu.position());
        boolean left;
        if (projectilePos.distanceTo(rightVector) < projectilePos.distanceTo(leftVector)) {
            left = false;
        } else if (projectilePos.distanceTo(rightVector) > projectilePos.distanceTo(leftVector)) {
            left = true;
        } else {
            left = emu.getRandom().nextBoolean();
        }
        Vec3 dodgeVector = projectile.getDeltaMovement()
                .yRot((float) ((double) (left ? -0.5F : 0.5F) * Math.PI)).normalize();
        emu.setAnimation(left ? EmuServant.ANIMATION_DODGE_LEFT : EmuServant.ANIMATION_DODGE_RIGHT);
        emu.hasImpulse = true;
        if (!emu.horizontalCollision) {
            emu.move(MoverType.SELF, new Vec3(dodgeVector.x() * 0.25D, 0.1F, dodgeVector.z() * 0.25D));
        }
        emu.setDeltaMovement(emu.getDeltaMovement().add(dodgeVector.x() * 0.5D, 0.32F, dodgeVector.z() * 0.5D));
        event.setCanceled(true);
    }
}
