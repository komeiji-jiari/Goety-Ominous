package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public abstract class OminousCandleShieldCooldownMixin {

    @ModifyConstant(method = "disableShield", constant = @Constant(intValue = 100))
    private int goetyominous$halfShieldCooldown(int original, boolean byShield) {
        Player self = (Player) (Object) this;
        return self.getUseItem().is(ModItems.OMINOUS_CANDLE.get()) ? original / 2 : original;
    }
}
