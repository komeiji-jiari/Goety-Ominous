/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.EyesLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  org.joml.Matrix4f
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.vivideru.masteryofmagic.client.model.MidasBossModel;
import com.vivideru.masteryofmagic.client.model.animations.MidasBossAnimation;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.joml.Matrix4f;

public class PhilosopherKingMidasRenderer
extends MobRenderer<PhilosopherKingMidasEntity, MidasBossModel<PhilosopherKingMidasEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/midas_boss.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/midas_boss_eyes.png");
    private static final int SPHERE_LONGITUDE_SEGMENTS = 32;
    private static final int SPHERE_LATITUDE_SEGMENTS = 16;
    private static final RenderType AURA_RENDER_TYPE = MidasAuraRenderType.createAura();

    public PhilosopherKingMidasRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new AnimatedModel(context.m_174023_(MidasBossModel.LAYER_LOCATION)), 0.8f);
        this.m_115326_((RenderLayer)new EyesLayer<PhilosopherKingMidasEntity, MidasBossModel<PhilosopherKingMidasEntity>>((RenderLayerParent)this){

            public RenderType m_5708_() {
                return RenderType.m_110488_((ResourceLocation)EYES_TEXTURE);
            }
        });
    }

    public ResourceLocation getTextureLocation(PhilosopherKingMidasEntity entity) {
        return TEXTURE;
    }

    public void render(PhilosopherKingMidasEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.m_7392_((Mob)entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        poseStack.m_85836_();
        poseStack.m_85837_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
        float pulseWave = 0.5f + 0.5f * (float)Math.sin(((float)entity.f_19797_ + partialTick) * 0.08f);
        int pulseAlpha = 12 + Math.round(8.0f * pulseWave);
        PhilosopherKingMidasRenderer.renderAuraSphere(poseStack, buffers.m_6299_(AURA_RENDER_TYPE), pulseAlpha, (float)entity.getAuraRadius(partialTick));
        poseStack.m_85849_();
    }

    private static void renderAuraSphere(PoseStack poseStack, VertexConsumer consumer, int alpha, float radius) {
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        int red = 255;
        int green = 128;
        int blue = 255;
        for (int latitude = 0; latitude < 16; ++latitude) {
            double phi0 = -1.5707963267948966 + Math.PI * (double)latitude / 16.0;
            double phi1 = -1.5707963267948966 + Math.PI * (double)(latitude + 1) / 16.0;
            float y0 = radius * (float)Math.sin(phi0);
            float y1 = radius * (float)Math.sin(phi1);
            float ring0 = radius * (float)Math.cos(phi0);
            float ring1 = radius * (float)Math.cos(phi1);
            for (int longitude = 0; longitude < 32; ++longitude) {
                double theta0 = Math.PI * 2 * (double)longitude / 32.0;
                double theta1 = Math.PI * 2 * (double)(longitude + 1) / 32.0;
                PhilosopherKingMidasRenderer.addVertex(consumer, matrix, ring0 * (float)Math.cos(theta0), y0, ring0 * (float)Math.sin(theta0), red, green, blue, alpha);
                PhilosopherKingMidasRenderer.addVertex(consumer, matrix, ring0 * (float)Math.cos(theta1), y0, ring0 * (float)Math.sin(theta1), red, green, blue, alpha);
                PhilosopherKingMidasRenderer.addVertex(consumer, matrix, ring1 * (float)Math.cos(theta1), y1, ring1 * (float)Math.sin(theta1), red, green, blue, alpha);
                PhilosopherKingMidasRenderer.addVertex(consumer, matrix, ring1 * (float)Math.cos(theta0), y1, ring1 * (float)Math.sin(theta0), red, green, blue, alpha);
            }
        }
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int red, int green, int blue, int alpha) {
        consumer.m_252986_(matrix, x, y, z).m_6122_(red, green, blue, alpha).m_5752_();
    }

    private static final class AnimatedModel
    extends MidasBossModel<PhilosopherKingMidasEntity> {
        private final ModelPart root;
        private final HierarchicalModel<PhilosopherKingMidasEntity> animator = new HierarchicalModel<PhilosopherKingMidasEntity>(){

            public ModelPart m_142109_() {
                return root;
            }

            public void setupAnim(PhilosopherKingMidasEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);
                this.m_233385_(entity.idleAnimationState, MidasBossAnimation.idle, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingForwardAnimationState, MidasBossAnimation.flying_frontal, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingBackwardAnimationState, MidasBossAnimation.flying_backwards, ageInTicks, 1.0f);
                this.m_233385_(entity.castingOneAnimationState, MidasBossAnimation.casting1, ageInTicks, entity.isFastSlashAnimationActive() ? 3.5f : 1.0f);
                this.m_233385_(entity.castingTwoAnimationState, MidasBossAnimation.casting2, ageInTicks, 1.0f);
                this.m_233385_(entity.castingThreeAnimationState, MidasBossAnimation.casting3, ageInTicks, 1.0f);
            }
        };

        private AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        public void setupAnim(PhilosopherKingMidasEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.animator.m_6973_((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            super.m_6973_(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    private static abstract class MidasAuraRenderType
    extends RenderType {
        private MidasAuraRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        private static RenderType createAura() {
            return RenderType.m_173215_((String)"midas_translucent_aura", (VertexFormat)DefaultVertexFormat.f_85815_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)512, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173292_(f_173104_).m_110685_(f_110139_).m_110663_(f_110113_).m_110661_(f_110110_).m_110687_(f_110115_).m_110691_(false));
        }
    }
}

