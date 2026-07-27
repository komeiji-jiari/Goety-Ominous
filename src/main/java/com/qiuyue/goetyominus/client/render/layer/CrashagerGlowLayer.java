package com.qiuyue.goetyominus.client.render.layer;

import com.qiuyue.goetyominus.client.render.model.CrashagerServantModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class CrashagerGlowLayer<T extends LivingEntity> extends EyesLayer<T, CrashagerServantModel<T>> {
    private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_crashager.png"));
    private static final RenderType CHRISTMAS = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/magispeller/christmas/crashager_christmas.png"));

    public CrashagerGlowLayer(RenderLayerParent<T, CrashagerServantModel<T>> p_i226039_1_) {
        super(p_i226039_1_);
    }

    public RenderType renderType() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1 == 12 ? CHRISTMAS : LAYER;
    }
}
