package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leapleaf.class)
public class LeapleafWaterConversionMixin {

    @Unique
    private int goetyominous$inWaterTime;
    @Unique
    private int goetyominous$conversionTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void goetyominous$convertInWater(CallbackInfo ci) {
        if ((Object) this instanceof Leapkelp) {
            return;
        }
        Leapleaf self = (Leapleaf) (Object) this;
        Level level = self.level();
        if (level.isClientSide || !self.isAlive() || self.isNoAi()) {
            return;
        }
        if (this.goetyominous$conversionTime > 0) {
            --this.goetyominous$conversionTime;
            if (this.goetyominous$conversionTime <= 0
                    && ForgeEventFactory.canLivingConvert(self, ModEntityTypes.LEAPKELP.get(), timer -> this.goetyominous$conversionTime = timer)) {
                goetyominous$convert(self);
            }
            return;
        }
        if (self.isInWaterOrBubble()) {
            if (++this.goetyominous$inWaterTime >= 600) {
                this.goetyominous$inWaterTime = 0;
                this.goetyominous$conversionTime = 300;
            }
        } else {
            this.goetyominous$inWaterTime = -1;
        }
    }

    @Unique
    private static void goetyominous$convert(Leapleaf self) {
        Entity converted = MobUtil.convertTo(self, ModEntityTypes.LEAPKELP.get(), false, null);
        if (!(converted instanceof Leapkelp leapkelp)) {
            return;
        }
        if (self.getTrueOwner() != null) {
            leapkelp.setTrueOwner(self.getTrueOwner());
        }
        if (self.isNatural()) {
            leapkelp.setNatural(true);
        }
        if (self.isHostile()) {
            leapkelp.setHostile(true);
        }
        if (self.limitedLifeTicks > 0) {
            leapkelp.setLimitedLife(self.limitedLifeTicks);
        }
        ForgeEventFactory.onLivingConvert(self, leapkelp);
    }
}
