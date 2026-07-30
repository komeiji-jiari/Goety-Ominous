package com.qiuyue.goetyominus.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.client.render.model.am.ModelMurmurServantBody;
import com.qiuyue.goetyominus.client.render.model.am.ModelMurmurServantHead;
import com.qiuyue.goetyominus.client.render.model.am.ModelMurmurServantNeck;
import com.qiuyue.goetyominus.common.entities.ally.am.MurmurServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMurmurServantBody extends MobRenderer<MurmurServant, ModelMurmurServantBody> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/murmur.png");
    public static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/murmur_angry.png");
    public static final ResourceLocation SEKIBANKI_TEXTURE = new ResourceLocation("goetyominus", "textures/entity/sekibanki.png");
    public static boolean renderWithHead = false;
    private static final ModelMurmurServantNeck NECK_MODEL = new ModelMurmurServantNeck();
    private static final ModelMurmurServantHead HEAD_MODEL = new ModelMurmurServantHead();

    public RenderMurmurServantBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelMurmurServantBody(), 0.5F);
    }

    protected void scale(MurmurServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    public void render(MurmurServant body, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(body, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (renderWithHead || body.shouldRenderFakeHead()) {
            float f = Mth.rotLerp(partialTicks, body.yBodyRotO, body.yBodyRot);
            float f7 = this.getBob(body, partialTicks);
            ResourceLocation loc = this.getTextureLocation(body);
            int overlayCoords = getOverlayCoords(body, this.getWhiteOverlayProgress(body, partialTicks));
            matrixStackIn.pushPose();
            this.setupRotations(body, matrixStackIn, f7, f, partialTicks);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0F, -2.9F, 0.0F);
            this.scale(body, matrixStackIn, partialTicks);
            HEAD_MODEL.resetToDefaultPose();
            HEAD_MODEL.animateHair(f7);
            HEAD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc)), packedLightIn, overlayCoords, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.translate(0.0F, 0.5F, 0.0F);
            NECK_MODEL.resetToDefaultPose();
            NECK_MODEL.setAttributes(0.5F, 0.0F, 0.0F, 0.0F);
            NECK_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc)), packedLightIn, overlayCoords, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

    }

    public ResourceLocation getTextureLocation(MurmurServant entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomName().getString().toLowerCase();
            if (name.equals("sekibanki")) {
                return SEKIBANKI_TEXTURE;
            }
        }
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
