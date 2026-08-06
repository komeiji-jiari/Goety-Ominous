package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelCrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCrimsonMosquitoServantBlood extends RenderLayer<CrimsonMosquitoServant, ModelCrimsonMosquitoServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs", "textures/entity/crimson_mosquito_blood.png");
    private static final ResourceLocation TEXTURE_SICK = new ResourceLocation("alexsmobs", "textures/entity/crimson_mosquito_blood_blue.png");

    public LayerCrimsonMosquitoServantBlood(RenderCrimsonMosquitoServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CrimsonMosquitoServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getBloodLevel() > 0) {
            ResourceLocation loc = entity.isSick() ? TEXTURE_SICK : TEXTURE;
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityCutout(loc));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
