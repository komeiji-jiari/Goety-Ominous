package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.ModModelLayers;
import com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ServantWindChargeRenderer extends EntityRenderer<AbstractWindCharge> {
    private static final float MAX_RENDER_DISTANCE = Mth.square(3.5F);
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/projectile/wind_charge.png");
    private final ModelPart model;

    public ServantWindChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = context.bakeLayer(ModModelLayers.SERVANT_WIND_CHARGE);
    }

    @Override
    public void render(AbstractWindCharge entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (com.qiuyue.goetyominous.client.render.WindChargeShaders.breezeWind() == null) {
            return;
        }
        if (entity.tickCount < 2 && this.entityRenderDispatcher.distanceToSqr(entity) < (double) MAX_RENDER_DISTANCE) {
            return;
        }
        float age = (float) entity.tickCount + partialTick;
        VertexConsumer consumer = buffer.getBuffer(WindChargeRenderTypes.breezeWind(TEXTURE, this.getOffset(age) % 1.0F, 0.0F));
        this.model.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    protected float getOffset(float age) {
        return age * 0.03F;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractWindCharge entity) {
        return TEXTURE;
    }
}
