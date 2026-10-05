package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.MaceItem;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class MaceAttackMixin {
    @ModifyVariable(
            method = "attack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;",
                    shift = At.Shift.AFTER,
                    remap = false),
            index = 2,
            require = 1)
    private float goetyominous$maceFallBonus(float damage) {
        return damage + MaceItem.getAttackDamageBonus((Player) (Object) this);
    }
}
