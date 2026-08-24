package com.qiuyue.goetyominous.client.render.layer.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.of.DicerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.unusualmodding.opposing_force.OpposingForce;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Dicer 仆从的发光面罩层：照搬原版 DicerVisorLayer，
 * 发射激光时换成亮起的 visor_lasering 贴图，精英变体用 arch 系列，命名含 gigan 用 gigan 系列。
 * 使用 15728640（FULL_BRIGHT）强度使其自发光。
 */
@OnlyIn(Dist.CLIENT)
public class DicerServantVisorLayer extends RenderLayer<DicerServant, DicerServantModel> {
    private static final RenderType VISOR = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/visor.png"));
    private static final RenderType VISOR_LASERING = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/visor_lasering.png"));
    private static final RenderType ARCH_VISOR = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/arch_visor.png"));
    private static final RenderType ARCH_VISOR_LASERING = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/arch_visor_lasering.png"));
    private static final RenderType GIGAN_VISOR = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/gigan_visor.png"));
    private static final RenderType GIGAN_VISOR_LASERING = RenderType.entityTranslucent(OpposingForce.modPrefix("textures/entity/dicer/gigan_visor_lasering.png"));

    public DicerServantVisorLayer(RenderLayerParent<DicerServant, DicerServantModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, DicerServant entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        RenderType renderType;
        if (entity.isLasering()) {
            renderType = entity.isElite() ? ARCH_VISOR_LASERING : VISOR_LASERING;
        } else {
            renderType = entity.isElite() ? ARCH_VISOR : VISOR;
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        if (entity.getName().getString().contains("gigan")) {
            vertexConsumer = bufferSource.getBuffer(entity.isLasering() ? GIGAN_VISOR_LASERING : GIGAN_VISOR);
        }

        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
