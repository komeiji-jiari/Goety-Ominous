package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousBottleItem;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public abstract class RaiderOminousBottleMixin {

    @Inject(method = "die", at = @At("HEAD"), require = 1)
    private void goetyominous$dropOminousBottle(DamageSource source, CallbackInfo ci) {
        Raider raider = (Raider) (Object) this;
        if (raider.level().isClientSide || !MobsConfig.RaidCaptainDropsOminousBottle.get()) {
            return;
        }
        if (!raider.isPatrolLeader()
                || raider.getCurrentRaid() != null
                || ((ServerLevel) raider.level()).getRaidAt(raider.blockPosition()) != null) {
            return;
        }
        ItemStack banner = raider.getItemBySlot(EquipmentSlot.HEAD);
        if (banner.isEmpty() || !ItemStack.matches(banner, Raid.getLeaderBannerInstance())) {
            return;
        }
        ItemStack bottle = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        OminousBottleItem.setAmplifier(bottle,
                raider.getRandom().nextInt(OminousBottleItem.MAX_AMPLIFIER + 1));
        raider.spawnAtLocation(bottle);
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;removeEffectNoUpdate(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"))
    private MobEffectInstance goetyominous$skipBadOmenUpgrade(Player player, MobEffect effect) {
        if (MobsConfig.RaidCaptainDropsOminousBottle.get()) {
            return null;
        }
        return player.removeEffectNoUpdate(effect);
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean goetyominous$skipBadOmen(Player player, MobEffectInstance effect) {
        if (MobsConfig.RaidCaptainDropsOminousBottle.get()) {
            return false;
        }
        return player.addEffect(effect);
    }
}
