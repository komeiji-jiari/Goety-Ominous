package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.am.ModelIllagerElephantServant;
import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderIllagerElephantServant extends MobRenderer<IllagerElephantServant, ModelIllagerElephantServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/illager_elephant_servant.png");

    public RenderIllagerElephantServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelIllagerElephantServant(0.0f), 1.4f);
        this.addLayer(new LayerElephantServantOverlays(this));
        this.addLayer(new LayerElephantServantItem(this));
    }

    @Override
    protected void scale(IllagerElephantServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.1f, 1.1f, 1.1f);
    }

    @Override
    public ResourceLocation getTextureLocation(IllagerElephantServant entity) {
        return TEXTURE;
    }

    public static class LayerElephantServantOverlays extends RenderLayer<IllagerElephantServant, ModelIllagerElephantServant> {
        private static final ResourceLocation[] ELEPHANT_DECOR_TEXTURES = new ResourceLocation[]{
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/white.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/orange.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/magenta.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/light_blue.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/yellow.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/lime.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/pink.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/gray.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/light_gray.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/cyan.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/purple.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/blue.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/brown.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/green.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/red.png"),
                new ResourceLocation("alexsmobs:textures/entity/elephant/decor/black.png")
        };
        private static final ResourceLocation TEXTURE_CHEST = new ResourceLocation("alexsmobs:textures/entity/elephant/elephant_chest.png");
        private final ModelIllagerElephantServant model = new ModelIllagerElephantServant(0.5f);

        public LayerElephantServantOverlays(RenderIllagerElephantServant renderElephant) {
            super(renderElephant);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, IllagerElephantServant elephant, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (elephant.isChested()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(TEXTURE_CHEST));
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(elephant, 0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            DyeColor color = elephant.getColor();
            if (color != null) {
                ResourceLocation texture = ELEPHANT_DECOR_TEXTURES[color.getId()];
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.setupAnim(elephant, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(texture));
                this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    public static class LayerElephantServantItem extends RenderLayer<IllagerElephantServant, ModelIllagerElephantServant> {
        public LayerElephantServantItem(RenderIllagerElephantServant render) {
            super(render);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, IllagerElephantServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.35f, 0.35f, 0.35f);
                matrixStackIn.translate(0.0, 2.8, 0.0);
            }
            matrixStackIn.pushPose();
            this.translateToHand(matrixStackIn);
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.translate(0.0, 0.2f, -0.22);
            }
            matrixStackIn.translate(-0.0, 1.0, 0.15f);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0f));
            matrixStackIn.scale(1.3f, 1.3f, 1.3f);
            if (Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemstack).isGui3d()) {
                matrixStackIn.translate(-0.05f, -0.1f, -0.15f);
                matrixStackIn.scale(2.0f, 2.0f, 2.0f);
            }
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().head.translateAndRotate(matrixStack);
            this.getParentModel().trunk1.translateAndRotate(matrixStack);
            this.getParentModel().trunk2.translateAndRotate(matrixStack);
        }
    }
}
