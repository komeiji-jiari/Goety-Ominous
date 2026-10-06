package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousBottleItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public class RaiderCaptainDropMixin {

    @Inject(method = "die", at = @At("HEAD"), require = 1)
    private void goetyominous$dropOminousBottleOnCaptainDeath(DamageSource source, CallbackInfo ci) {
        Raider raider = (Raider) (Object) this;
        if (raider.level().isClientSide) {
            return;
        }
        if (!raider.isPatrolLeader()) {
            return;
        }
        if (!ItemStack.matches(raider.getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
            return;
        }
        if (!raider.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return;
        }
        ItemStack bottle = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        OminousBottleItem.setAmplifier(bottle, raider.getRandom().nextInt(OminousBottleItem.MAX_AMPLIFIER + 1));
        raider.spawnAtLocation(bottle);
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;removeEffectNoUpdate(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"), require = 1)
    private MobEffectInstance goetyominous$keepExistingBadOmen(Player player, MobEffect effect) {
        return null;
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), require = 1)
    private boolean goetyominous$cancelBadOmenGrant(Player player, MobEffectInstance effect) {
        return false;
    }
}
