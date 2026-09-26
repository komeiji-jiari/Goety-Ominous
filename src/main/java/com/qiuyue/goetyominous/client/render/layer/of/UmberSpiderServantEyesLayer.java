package com.qiuyue.goetyominous.client.render.layer.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.of.UmberSpiderServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 阴影蜘蛛仆从发光眼睛层：复刻 OF 原版 UmberSpiderEyesLayer。
 * 用 RenderType.eyes 把整套模型按"发光的眼睛"方式再画一遍（只显示发光部分），
 * 精英用另一张贴图。15728640 = 满格光照，让眼睛永远亮着。
 */
@OnlyIn(Dist.CLIENT)
public class UmberSpiderServantEyesLayer extends RenderLayer<UmberSpiderServant, UmberSpiderServantModel> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation("opposing_force", "textures/entity/umber_spider/umber_spider_eyes.png"));
    private static final RenderType ELITE_EYES = RenderType.eyes(new ResourceLocation("opposing_force", "textures/entity/umber_spider/tenebrous_umber_spider_eyes.png"));

    public UmberSpiderServantEyesLayer(RenderLayerParent<UmberSpiderServant, UmberSpiderServantModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       UmberSpiderServant entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer consumer = bufferSource.getBuffer(this.renderType(entity));
        ((UmberSpiderServantModel) this.getParentModel()).renderToBuffer(poseStack, consumer,
                15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private RenderType renderType(UmberSpiderServant entity) {
        return entity.isElite() ? ELITE_EYES : EYES;
    }
}
