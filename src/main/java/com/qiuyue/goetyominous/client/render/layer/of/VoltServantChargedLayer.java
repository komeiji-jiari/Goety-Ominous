package com.qiuyue.goetyominous.client.render.layer.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.VoltServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class VoltServantChargedLayer extends RenderLayer<VoltServant, VoltServantModel> {
    private static final ResourceLocation CHARGED = new ResourceLocation("opposing_force", "textures/entity/volt/charged.png");
    private static final ResourceLocation CHARGED_QUASAR = new ResourceLocation("opposing_force", "textures/entity/volt/charged_quasar.png");
    private final VoltServantModel model;

    public VoltServantChargedLayer(RenderLayerParent<VoltServant, VoltServantModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new VoltServantModel(modelSet.bakeLayer(ModEntityLayers.VOLT_SERVANT_CHARGED_LAYER));
    }

    protected float xOffset(float offset) {
        return offset * 0.01F;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
                       VoltServant volt, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        float f = (float) volt.tickCount + partialTicks;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.energySwirl(
                volt.isElite() ? CHARGED_QUASAR : CHARGED, this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
        EntityModel<VoltServant> entitymodel = this.model;
        entitymodel.prepareMobModel(volt, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().copyPropertiesTo(entitymodel);
        entitymodel.setupAnim(volt, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (volt.isPowered()) {
            if (volt.isElite()) {
                int i = volt.tickCount / 25 + volt.getId();
                int length = DyeColor.values().length;
                int i1 = i % length;
                int i2 = (i + 1) % length;
                float time = ((float) (volt.tickCount % 25) + partialTicks) / 25.0F;
                float[] colorArray = Sheep.getColorArray(DyeColor.byId(i1));
                float[] colorArray1 = Sheep.getColorArray(DyeColor.byId(i2));
                float r = colorArray[0] * (1.0F - time) + colorArray1[0] * time;
                float g = colorArray[1] * (1.0F - time) + colorArray1[1] * time;
                float b = colorArray[2] * (1.0F - time) + colorArray1[2] * time;
                entitymodel.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
            } else {
                entitymodel.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
