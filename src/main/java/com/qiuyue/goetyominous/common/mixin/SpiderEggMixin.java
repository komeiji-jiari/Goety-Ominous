package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.spider.SpiderServant;
import com.Polarice3.Goety.common.entities.neutral.SpiderEgg;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SpiderEgg.class)
public class SpiderEggMixin {

    @Inject(method = "hatchEgg", at = @At("RETURN"), remap = false)
    private void goetyominous$onHatchEgg(CallbackInfo ci) {
        SpiderEgg egg = (SpiderEgg) (Object) this;
        Level level = egg.level();
        if (level.isClientSide) return;

        int lifespan = MobUtil.getSummonLifespan(level) * 1;
        List<SpiderServant> spiders = level.getEntitiesOfClass(SpiderServant.class, egg.getBoundingBox().inflate(2));
        for (SpiderServant spider : spiders) {
            if (spider.getTrueOwner() == egg.getTrueOwner()) {
                spider.setLimitedLife(lifespan);
                break;
            }
        }
    }
}
