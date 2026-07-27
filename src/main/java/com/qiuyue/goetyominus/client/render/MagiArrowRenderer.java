package com.qiuyue.goetyominus.client.render;

import com.qiuyue.goetyominus.common.entities.ally.mobs.MagiArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagiArrowRenderer extends ArrowRenderer<MagiArrow> {
    public static final ResourceLocation ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public MagiArrowRenderer(EntityRendererProvider.Context p_173917_) {
        super(p_173917_);
    }

    public ResourceLocation getTextureLocation(MagiArrow magiArrow) {
        return ARROW_LOCATION;
    }
}
