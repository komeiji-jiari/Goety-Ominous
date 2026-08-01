package com.qiuyue.goetyominous.client.render.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.sar.RunePrisonModel;
import com.qiuyue.goetyominous.common.entities.ally.sar.RunePrison;
import com.teamabnormals.blueprint.client.BlueprintRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RunePrisonRenderer extends EntityRenderer<RunePrison> {
    public static final ResourceLocation[] RUNE_PRISON_FRAMES = new ResourceLocation[]{
            new ResourceLocation("savage_and_ravage", "textures/entity/rune_prison/rune_prison_0.png"),
            new ResourceLocation("savage_and_ravage", "textures/entity/rune_prison/rune_prison_1.png"),
            new ResourceLocation("savage_and_ravage", "textures/entity/rune_prison/rune_prison_2.png"),
            new ResourceLocation("savage_and_ravage", "textures/entity/rune_prison/rune_prison_3.png"),
            new ResourceLocation("savage_and_ravage", "textures/entity/rune_prison/rune_prison_4.png"),
    };
    private final RunePrisonModel model;

    public RunePrisonRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        model = new RunePrisonModel(context.bakeLayer(ModEntityLayers.RUNE_PRISON_LAYER));
    }

    @Override
    public void render(RunePrison entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.15F, -0.7F, 0.15F);
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(BlueprintRenderTypes.getUnshadedTranslucentEntity(getTextureLocation(entityIn), false));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(RunePrison entity) {
        return RUNE_PRISON_FRAMES[entity.getCurrentFrame()];
    }
}
