package com.qiuyue.goetyominous.client.render.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.ZombieResurrectionServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.ZombieResurrectionServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieResurrectionServantRenderer extends EntityRenderer<ZombieResurrectionServant> {
    public ResourceLocation texture = null;
    private final ZombieResurrectionServantModel<ZombieResurrectionServant> model;

    public ZombieResurrectionServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ZombieResurrectionServantModel(context.bakeLayer(ModEntityLayers.ZOMBIE_RESURRECTION_SERVANT_LAYER));
    }

    @Override
    public void render(ZombieResurrectionServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getEntityType() != null) {
            Entity spawned = entity.getEntityType().create(entity.level());
            if (spawned != null) {
                EntityRenderer renderer = this.entityRenderDispatcher.getRenderer(spawned);
                if (renderer != null) {
                    this.texture = renderer.getTextureLocation(spawned);
                }
            }
        }
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0, -1.501F, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        if (entity.getEntityType() != null && entity.getEntityType() == EntityType.HUSK) {
            poseStack.translate(0.0, -0.1, 0.0);
            poseStack.scale(1.0625F, 1.0625F, 1.0625F);
        }
        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        if (entity.resurrectAnimationState.isStarted()) {
            VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieResurrectionServant entity) {
        return this.texture != null ? this.texture : new ResourceLocation("textures/entity/zombie/zombie.png");
    }
}
