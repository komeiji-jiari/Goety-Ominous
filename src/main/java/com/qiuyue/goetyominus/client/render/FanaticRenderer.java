package com.qiuyue.goetyominus.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.FanaticModel;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Fanatic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FanaticRenderer extends MobRenderer<Fanatic, FanaticModel<Fanatic>> {

    public FanaticRenderer(EntityRendererProvider.Context context) {
        super(context, new FanaticModel<>(context.bakeLayer(ModEntityLayers.FANATIC_LAYER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new ItemInHandLayer<Fanatic, FanaticModel<Fanatic>>(this, context.getItemInHandRenderer()) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Fanatic entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                if (entity.isAggressive()) {
                    super.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(Fanatic entity) {
        return entity.getResourceLocation();
    }
}