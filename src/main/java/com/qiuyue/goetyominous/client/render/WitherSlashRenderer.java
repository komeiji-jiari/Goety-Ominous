package com.qiuyue.goetyominous.client.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.mm.WitherSlashModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.WitherSlash;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherSlashRenderer<T extends WitherSlash> extends EntityRenderer<T> {
    public static final ResourceLocation[] TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("mutantmore", "textures/entities/wither_slash_0.png"), new ResourceLocation("mutantmore", "textures/entities/wither_slash_1.png")};
    private final WitherSlashModel<T> model;

    public WitherSlashRenderer(EntityRendererProvider.Context p_174296_) {
        super(p_174296_);
        this.model = new WitherSlashModel(p_174296_.bakeLayer(WitherSlashModel.LAYER_LOCATION));
    }

    public void render(T p_115373_, float p_115374_, float p_115375_, PoseStack p_115376_, MultiBufferSource p_115377_, int p_115378_) {
        p_115376_.pushPose();
        p_115376_.scale(-1.0F, -1.0F, 1.0F);
        p_115376_.scale(p_115373_.getSize(), p_115373_.getSize(), p_115373_.getSize());
        p_115376_.translate(0.0, -1.5, 0.0);
        float f1 = -Mth.lerp(p_115375_, p_115373_.xRotO, p_115373_.getXRot());
        VertexConsumer vertexconsumer = p_115377_.getBuffer(this.model.renderType(this.getTextureLocation(p_115373_)));
        this.model.rotateInDirection(-(180.0F - p_115373_.getFixedYaw()), f1);
        this.model.renderToBuffer(p_115376_, vertexconsumer, p_115378_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_115376_.popPose();
        super.render(p_115373_, p_115374_, p_115375_, p_115376_, p_115377_, p_115378_);
    }

    public ResourceLocation getTextureLocation(WitherSlash entity) {
        return TEXTURE_LOCATIONS[entity.textureChange % TEXTURE_LOCATIONS.length];
    }
}
