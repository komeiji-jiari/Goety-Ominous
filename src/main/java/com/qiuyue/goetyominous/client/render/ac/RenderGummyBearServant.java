package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGummyBearServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GummyBearServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderGummyBearServant extends MobRenderer<GummyBearServant, ModelGummyBearServant> {

    public static final ModelGummyBearServant OUTSIDE_MODEL = new ModelGummyBearServant(0.0F);
    private static final ResourceLocation TEXTURE_RED = new ResourceLocation("alexscaves:textures/entity/gummy_bear_red.png");
    private static final ResourceLocation TEXTURE_GREEN = new ResourceLocation("alexscaves:textures/entity/gummy_bear_green.png");
    private static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation("alexscaves:textures/entity/gummy_bear_yellow.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexscaves:textures/entity/gummy_bear_blue.png");
    private static final ResourceLocation TEXTURE_PINK = new ResourceLocation("alexscaves:textures/entity/gummy_bear_pink.png");
    private static final ResourceLocation TEXTURE_INNARDS = new ResourceLocation("alexscaves:textures/entity/gummy_bear_innards.png");

    public RenderGummyBearServant(EntityRendererProvider.Context context) {
        super(context, new ModelGummyBearServant(-1.8F), 0.85F);
        this.addLayer(new LayerOutside());
    }

    @Override
    protected void scale(GummyBearServant mob, PoseStack matrixStackIn, float partialTicks) {
        float r = mob.getStomachRed();
        float g = mob.getStomachGreen();
        float b = mob.getStomachBlue();
        float alpha = mob.getStomachAlpha(partialTicks);
        ((ModelGummyBearServant) this.model).setColor(r, g, b, alpha);
        // 繁殖出的幼体由模型 young 缩放(整体 0.5 倍 + 头部 1.5 倍);LayerOutside 经
        // copyPropertiesTo 自动同步 young,故只需在此设主模型。
        this.model.young = mob.isBaby();
    }

    @Override
    public ResourceLocation getTextureLocation(GummyBearServant entity) {
        return TEXTURE_INNARDS;
    }

    @Override
    public void render(GummyBearServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = entity.isBaby() ? 0.4F : 0.85F;
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    @Nullable
    protected RenderType getRenderType(GummyBearServant entity, boolean notInvisible, boolean renderAsItemCull, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(entity);
        if (renderAsItemCull) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        }
        if (notInvisible) {
            return RenderType.entityTranslucent(resourcelocation);
        }
        return outline ? RenderType.outline(resourcelocation) : null;
    }

    public ResourceLocation getOutsideTextureLocation(GummyBearServant entity) {
        switch (entity.getGummyColor()) {
            case GREEN:
                return TEXTURE_GREEN;
            case YELLOW:
                return TEXTURE_YELLOW;
            case BLUE:
                return TEXTURE_BLUE;
            case PINK:
                return TEXTURE_PINK;
            case RED:
            default:
                return TEXTURE_RED;
        }
    }

    public class LayerOutside extends RenderLayer<GummyBearServant, ModelGummyBearServant> {

        public LayerOutside() {
            super(RenderGummyBearServant.this);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GummyBearServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                ((ModelGummyBearServant) this.getParentModel()).copyPropertiesTo(OUTSIDE_MODEL);
                OUTSIDE_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                OUTSIDE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(RenderGummyBearServant.this.getOutsideTextureLocation(entitylivingbaseIn))), packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity) entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
