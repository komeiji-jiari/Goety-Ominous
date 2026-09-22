package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.BlackWolfModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.layer.CursedBlackWolfArmorLayer;
import com.qiuyue.goetyominous.common.entities.ally.mobs.SwampWolf;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SwampWolfRenderer extends MobRenderer<SwampWolf, BlackWolfModel<SwampWolf>> {

    private static final ResourceLocation TEXTURE = tex("swamp_wolf.png");
    private static final ResourceLocation EYES = tex("swamp_wolf_eyes.png");
    private static final ResourceLocation FUR = tex("swamp_wolf_fur.png");

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/" + name);
    }

    public SwampWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new BlackWolfModel<>(context.bakeLayer(ModModelLayer.BLACK_WOLF)), 0.5F);
        this.addLayer(new SwampWolfEyesLayer(this));
        this.addLayer(new SwampWolfFurLayer(this, context.getModelSet()));
        this.addLayer(new CursedBlackWolfArmorLayer<>(this, context.getModelSet()));
    }

    @Override
    protected void scale(SwampWolf wolf, PoseStack poseStack, float partialTicks) {
        float f = 1.0F + 0.15F * (wolf.isUpgraded() ? 1 : 0);
        poseStack.scale(f, f, f);
    }

    @Override
    protected float getBob(SwampWolf wolf, float partialTicks) {
        return wolf.getTailAngle();
    }

    @Override
    public void render(SwampWolf wolf, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (wolf.isWet()) {
            float f = wolf.getWetShade(partialTicks);
            this.model.setColor(f, f, f);
        }
        super.render(wolf, entityYaw, partialTicks, poseStack, buffer, packedLight);
        if (wolf.isWet()) {
            this.model.setColor(1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SwampWolf wolf) {
        return TEXTURE;
    }

    private static class SwampWolfEyesLayer extends EyesLayer<SwampWolf, BlackWolfModel<SwampWolf>> {
        private SwampWolfEyesLayer(RenderLayerParent<SwampWolf, BlackWolfModel<SwampWolf>> parent) {
            super(parent);
        }

        @Override
        public @NotNull RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    private static class SwampWolfFurLayer extends RenderLayer<SwampWolf, BlackWolfModel<SwampWolf>> {
        private final BlackWolfModel<SwampWolf> layerModel;

        private SwampWolfFurLayer(RenderLayerParent<SwampWolf, BlackWolfModel<SwampWolf>> parent, EntityModelSet modelSet) {
            super(parent);
            this.layerModel = new BlackWolfModel<>(modelSet.bakeLayer(ModModelLayer.BLACK_WOLF));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SwampWolf wolf,
                           float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                           float netHeadYaw, float headPitch) {
            if (wolf.isUpgraded()) {
                coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, FUR,
                        poseStack, buffer, packedLight, wolf,
                        limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                        1.0F, 1.0F, 1.0F);
            }
        }
    }
}
