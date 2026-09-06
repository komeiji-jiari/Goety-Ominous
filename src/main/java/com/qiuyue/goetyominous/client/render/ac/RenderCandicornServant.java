package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelCandicornServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.CandicornServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 糖果独角兽仆从渲染:按 getVariant() 切 5 张 AC candicorn 纹理。骑手由 MC 原生骑乘渲染画在鞍点,
 * 不需要 AC 的 CandicornRiderLayer/巫婆附身层(本仆从无附身体系)。
 */
@OnlyIn(Dist.CLIENT)
public class RenderCandicornServant extends MobRenderer<CandicornServant, ModelCandicornServant> {

    public RenderCandicornServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCandicornServant(), 0.8F);
    }

    @Override
    protected void scale(CandicornServant entity, PoseStack matrixStack, float partialTicks) {
        // 繁殖产下的幼体由模型 young 置位触发整身缩半渲染。
        this.model.young = entity.isBaby();
    }

    @Override
    public void render(CandicornServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        // 幼体碰撞/模型缩小一半,阴影半径同步减小。
        this.shadowRadius = entity.isBaby() ? 0.4F : 0.8F;
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CandicornServant entity) {
        int variant = Math.max(0, Math.min(4, entity.getVariant()));
        return new ResourceLocation("alexscaves:textures/entity/candicorn_" + variant + ".png");
    }
}
