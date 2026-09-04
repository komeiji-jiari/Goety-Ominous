package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelVesperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VesperServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 荒墟暗蝠仆从渲染:沿用 AC 原版贴图(alexscaves:textures/entity/vesper.png)与基础 MobRenderer。
 * 与原版 VesperRenderer 一致——无额外 Layer、阴影 0.35F,材质走默认 entityCutoutNoCull 管线,
 * 依赖贴图自带的透明裁剪形成翼膜边缘,不引入混合透明层。
 */
@OnlyIn(Dist.CLIENT)
public class RenderVesperServant extends MobRenderer<VesperServant, ModelVesperServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/vesper.png");

    public RenderVesperServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelVesperServant(), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(VesperServant entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(VesperServant entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }
}
