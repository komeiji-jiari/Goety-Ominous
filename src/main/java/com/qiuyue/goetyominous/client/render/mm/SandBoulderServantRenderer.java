package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveMotionTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.SandBoulderServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.SandBoulderServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SandBoulderServantRenderer extends EntityRenderer<SandBoulderServant> {
    private static final String SERVANT_TEXTURE = "textures/entity/mutant_husk_servant";

    private final SandBoulderServantModel model;

    public SandBoulderServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SandBoulderServantModel(context.bakeLayer(ModEntityLayers.SAND_BOULDER_SERVANT_LAYER));
    }

    @Override
    public void render(SandBoulderServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getSizeScale();
        poseStack.scale(1.25F * scale, 1.25F * scale, 1.25F * scale);
        poseStack.translate(0.0, -0.15F, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.sandBlock.xRot = 0.0F;
        this.model.sandBlock.yRot = MiscUtils.degToRad(90.0F);
        Vec3 velocity = entity.getDeltaMovement();
        float speed = Mth.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
        SineWaveAnimationUtils.addConstantMotionToModelPart(this.model.sandBlock, speed * 600.0F, SineWaveAnimationUtils.getTick(entity.tickCount, true), 1.0F, 1.0F, SineWaveMotionTypes.ROTATION_X);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SandBoulderServant entity) {
        return new ResourceLocation(GoetyOminous.MOD_ID, SERVANT_TEXTURE + entity.getSandType() + ".png");
    }
}
