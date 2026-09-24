package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsSpiritEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGrottoceratopsSpirit extends EntityRenderer<GrottoceratopsSpiritEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves", "textures/entity/grottoceratops.png");
    private static final ModelGrottoceratopsServant MODEL = new ModelGrottoceratopsServant();

    public RenderGrottoceratopsSpirit(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GrottoceratopsSpiritEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        LivingEntity owner = entity.getUsingEntity();
        if (owner != null) {
            Vec3 ownerPos = owner.getPosition(partialTicks);
            Vec3 selfPos = entity.getPosition(partialTicks);
            float f = -((float) Mth.atan2(ownerPos.x - selfPos.x, ownerPos.z - selfPos.z)) * (180F / (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(-f));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.XN.rotationDegrees(entity.getViewXRot(partialTicks)));
        float alpha = entity.getFadeIn(partialTicks);

        boolean prevBaby = MODEL.young;
        MODEL.young = false;
        VertexConsumer consumer = buffer.getBuffer(ACRenderTypes.getRedGhost(TEXTURE));
        MODEL.animateSpirit(entity, partialTicks);
        MODEL.renderSpiritToBuffer(poseStack, consumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        MODEL.young = prevBaby;

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GrottoceratopsSpiritEntity entity) {
        return TEXTURE;
    }
}
