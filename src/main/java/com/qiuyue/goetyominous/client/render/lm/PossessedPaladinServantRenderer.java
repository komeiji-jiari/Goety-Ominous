package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantDaggerLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantEyesLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantGrabLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantShieldLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantTridentLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantWingsLayer;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantRenderer extends MobRenderer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    private static final ResourceLocation PHASE1 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/new_posessed_paladin.png");
    private static final ResourceLocation PHASE2 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/new_posessed_paladin_p2.png");

    private static final float MODEL_SCALE = 1.75F;

    public PossessedPaladinServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PossessedPaladinServantModel<>(
                context.bakeLayer(ModEntityLayers.POSSESSED_PALADIN_SERVANT_LAYER)), 0.75F);
        this.addLayer(new PossessedPaladinServantEyesLayer(this));
        this.addLayer(new PossessedPaladinServantDaggerLayer(this));
        this.addLayer(new PossessedPaladinServantShieldLayer(this));
        this.addLayer(new PossessedPaladinServantTridentLayer(this));
        this.addLayer(new PossessedPaladinServantWingsLayer(this));
        this.addLayer(new PossessedPaladinServantGrabLayer(this, context.getEntityRenderDispatcher()));
    }

    @Override
    public void render(PossessedPaladinServant entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        this.renderSoulRays(entity, poseStack, buffer, packedLight, partialTicks);
        this.renderTelegraph(entity, poseStack, buffer, packedLight);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSoulRays(PossessedPaladinServant entity, PoseStack poseStack,
                                MultiBufferSource buffer, int packedLight, float partialTicks) {
        if (entity.rayAmount <= 0) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        LmServantSoulRays.render(consumer, poseStack, entity.rayAmount, 1.35F,
                entity.getPhase() >= 2, entity.attackTicks, partialTicks);
    }

    private void renderTelegraph(PossessedPaladinServant entity, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight) {
        float alpha = Mth.clamp(1.0F - Math.min(entity.telegraphFadeAway.getAnimationFraction(), 1.0F) - 0.65F,
                0.0F, 1.0F);
        if (alpha <= 0.0F || !entity.canRenderTelegraph()) {
            return;
        }

        float yaw = -entity.getYRot() + 90.0F;
        VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        LmServantRenderUtils.renderPivotedQuad(8.0F, 2.0F, 0.0D, 0.25D, 0.0D,
                90.0D, (double) yaw, 0.0D, consumer, poseStack,
                OverlayTexture.NO_OVERLAY, packedLight, 1.0F, 0.25F, 0.25F, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(PossessedPaladinServant entity) {
        return entity.getPhase() >= 2 ? PHASE2 : PHASE1;
    }

}
