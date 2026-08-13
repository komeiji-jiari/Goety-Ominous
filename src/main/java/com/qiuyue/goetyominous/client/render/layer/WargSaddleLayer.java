package com.qiuyue.goetyominous.client.render.layer;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.WargModel;
import com.qiuyue.goetyominous.client.render.model.WargSaddleModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class WargSaddleLayer extends RenderLayer<Warg, WargModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/warg_saddle.png");
    private final WargSaddleModel saddleModel;

    public WargSaddleLayer(RenderLayerParent<Warg, WargModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.saddleModel = new WargSaddleModel(modelSet.bakeLayer(ModEntityLayers.WARG_SADDLE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (warg.isSaddled()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.saddleModel, TEXTURE, poseStack, buffer,
                    packedLight, warg, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}
