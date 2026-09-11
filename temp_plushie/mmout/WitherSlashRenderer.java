package com.alexander.mutantmore.renderers.entities;

import com.alexander.mutantmore.entities.WitherSlash;
import com.alexander.mutantmore.models.entities.WitherSlashModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WitherSlashRenderer<T extends WitherSlash> extends EntityRenderer<T> {
    public static final ResourceLocation[] TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("mutantmore", "textures/entities/wither_slash_0.png"), new ResourceLocation("mutantmore", "textures/entities/wither_slash_1.png")};
    private final WitherSlashModel<T> model;

    public WitherSlashRenderer(EntityRendererProvider.Context p_174296_) {
        super(p_174296_);
        this.model = new WitherSlashModel(p_174296_.m_174023_(WitherSlashModel.LAYER_LOCATION));
    }

    public void render(T p_115373_, float p_115374_, float p_115375_, PoseStack p_115376_, MultiBufferSource p_115377_, int p_115378_) {
        p_115376_.m_85836_();
        p_115376_.m_85841_(-1.0F, -1.0F, 1.0F);
        p_115376_.m_85841_(p_115373_.getSize(), p_115373_.getSize(), p_115373_.getSize());
        p_115376_.m_85837_(0.0D, -1.5D, 0.0D);
        float f1 = -Mth.m_14179_(p_115375_, p_115373_.f_19860_, p_115373_.m_146909_());
        VertexConsumer vertexconsumer = p_115377_.m_6299_(this.model.m_103119_(this.getTextureLocation(p_115373_)));
        this.model.rotateInDirection(-(180.0F - p_115373_.getFixedYaw()), f1);
        this.model.m_7695_(p_115376_, vertexconsumer, p_115378_, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
        p_115376_.m_85849_();
        super.m_7392_(p_115373_, p_115374_, p_115375_, p_115376_, p_115377_, p_115378_);
    }

    public ResourceLocation getTextureLocation(WitherSlash entity) {
        return TEXTURE_LOCATIONS[entity.textureChange % TEXTURE_LOCATIONS.length];
    }
}
