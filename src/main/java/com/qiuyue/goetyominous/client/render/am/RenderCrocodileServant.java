package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelCrocodileServant;
import com.qiuyue.goetyominous.common.entities.ally.am.CrocodileServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 鳄鱼仆从渲染器。
 *
 * 注意：不能像其他复用方案那样继承 AlexMobs 原版 RenderCrocodile——它是
 * MobRenderer{@code <EntityCrocodile, ModelCrocodile>}，而 CrocodileServant 继承 Goety 的
 * AnimalSummon、并非 EntityCrocodile。原版渲染器/模型里编译器生成的桥接方法会对传入实体
 * 无条件 checkcast EntityCrocodile，一旦注册给 CrocodileServant，每次渲染都会抛 ClassCastException。
 *
 * 这里改为标准的 MobRenderer{@code <CrocodileServant, ModelCrocodileServant>}，纹理仍用 AlexMobs
 * 原版 crocodile_0.png / crocodile_1.png（沙漠色）以及王冠层 crocodile_crown.png。
 */
@OnlyIn(Dist.CLIENT)
public class RenderCrocodileServant extends MobRenderer<CrocodileServant, ModelCrocodileServant> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/crocodile_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/crocodile_1.png");
    private static final ResourceLocation TEXTURE_CROWN = new ResourceLocation("alexsmobs:textures/entity/crocodile_crown.png");

    public RenderCrocodileServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCrocodileServant(), 0.8F);
        this.addLayer(new CrownLayer(this));
    }

    @Override
    protected void scale(CrocodileServant entity, PoseStack matrixStack, float partialTicks) {
        // 触发模型里已移植的 young 分支：幼崽整体 0.15 缩放 + 头部 1.5 倍大头外观，
        // 与实体 getDimensions() 的 0.15 倍碰撞箱保持一致。
        this.model.young = entity.isBaby();
        matrixStack.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(CrocodileServant entity) {
        return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
    }

    static class CrownLayer extends RenderLayer<CrocodileServant, ModelCrocodileServant> {

        public CrownLayer(RenderCrocodileServant p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CrocodileServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isCrowned()) {
                VertexConsumer shoeBuffer = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_CROWN));
                matrixStackIn.pushPose();
                this.getParentModel().renderToBuffer(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
    }
}
