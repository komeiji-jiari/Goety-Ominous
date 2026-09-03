package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelCaramelCubeServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.CaramelCubeServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 焦糖方糖仆从渲染:移植 AC CaramelCubeRenderer(保证与原版外观一模一样),去掉
 * Licowitch 附身/书页 sepia 相关。体型 0/1/2 渲染缩放 1/2/4;外层半透明糖衣(Outside)
 * 用同一模型实例以 entityTranslucent 二次绘制。
 */
@OnlyIn(Dist.CLIENT)
public class RenderCaramelCubeServant extends MobRenderer<CaramelCubeServant, ModelCaramelCubeServant> {

    private static final ResourceLocation TEXTURE_SMALL = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_small.png");
    private static final ResourceLocation TEXTURE_SMALL_OUTSIDE = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_small_outside.png");
    private static final ResourceLocation TEXTURE_MEDIUM = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_medium.png");
    private static final ResourceLocation TEXTURE_MEDIUM_OUTSIDE = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_medium_outside.png");
    private static final ResourceLocation TEXTURE_LARGE = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_large.png");
    private static final ResourceLocation TEXTURE_LARGE_OUTSIDE = new ResourceLocation("alexscaves:textures/entity/caramel_cube/caramel_cube_large_outside.png");

    public RenderCaramelCubeServant(EntityRendererProvider.Context context) {
        super(context, new ModelCaramelCubeServant(), 0.65F);
        this.addLayer(new LayerOutside());
    }

    @Override
    protected void scale(CaramelCubeServant mob, PoseStack matrixStackIn, float partialTicks) {
        int size = mob.getSlimeSize();
        float scaleBy = size == 2 ? 4 : size == 1 ? 2 : 1;
        matrixStackIn.scale(scaleBy, scaleBy, scaleBy);
    }

    @Override
    public ResourceLocation getTextureLocation(CaramelCubeServant entity) {
        switch (entity.getSlimeSize()) {
            case 1:
                return TEXTURE_MEDIUM;
            case 2:
                return TEXTURE_LARGE;
            default:
                return TEXTURE_SMALL;
        }
    }

    public ResourceLocation getOutsideTextureLocation(CaramelCubeServant entity) {
        switch (entity.getSlimeSize()) {
            case 1:
                return TEXTURE_MEDIUM_OUTSIDE;
            case 2:
                return TEXTURE_LARGE_OUTSIDE;
            default:
                return TEXTURE_SMALL_OUTSIDE;
        }
    }

    @Override
    @Nullable
    protected RenderType getRenderType(CaramelCubeServant caramelCubeServant, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(caramelCubeServant);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public class LayerOutside extends RenderLayer<CaramelCubeServant, ModelCaramelCubeServant> {

        public LayerOutside() {
            super(RenderCaramelCubeServant.this);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CaramelCubeServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(RenderCaramelCubeServant.this.getOutsideTextureLocation(entitylivingbaseIn))), packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
