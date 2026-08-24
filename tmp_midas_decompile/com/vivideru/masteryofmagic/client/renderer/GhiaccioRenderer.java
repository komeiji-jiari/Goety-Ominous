/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.vivideru.masteryofmagic.client.model.GhiaccioModel;
import com.vivideru.masteryofmagic.client.model.animations.GhiaccioAnimation;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class GhiaccioRenderer
extends MobRenderer<GhiaccioEntity, GhiaccioModel<GhiaccioEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/ghiaccio.png");

    public GhiaccioRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new AnimatedModel(context.m_174023_(GhiaccioModel.LAYER_LOCATION)), 0.5f);
    }

    public ResourceLocation getTextureLocation(GhiaccioEntity entity) {
        return TEXTURE;
    }

    private static final class AnimatedModel
    extends GhiaccioModel<GhiaccioEntity> {
        private final ModelPart root;
        private final HierarchicalModel<GhiaccioEntity> animator = new HierarchicalModel<GhiaccioEntity>(){

            public ModelPart m_142109_() {
                return root;
            }

            public void setupAnim(GhiaccioEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);
                float walkAmount = Math.min(limbSwingAmount * 2.5f, 1.0f);
                this.m_233385_(entity.idleAnimationState, GhiaccioAnimation.idle, ageInTicks, 1.5f);
                this.m_267799_(GhiaccioAnimation.fast_walk, limbSwing, walkAmount, 1.0f, 2.0f);
                this.m_233385_(entity.punchingFastAnimationState, GhiaccioAnimation.puching_fast, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingStillAnimationState, GhiaccioAnimation.flying_still, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingFrontalAnimationState, GhiaccioAnimation.flying_frontal, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingBackAnimationState, GhiaccioAnimation.flying_back, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingSxAnimationState, GhiaccioAnimation.flying_sx, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingDxAnimationState, GhiaccioAnimation.flying_dx, ageInTicks, 1.0f);
                this.m_233385_(entity.flyingCastingAnimationState, GhiaccioAnimation.flying_casting, ageInTicks, 1.0f);
                this.m_233385_(entity.leMondeAnimationState, GhiaccioAnimation.le_monde, ageInTicks, 1.0f);
            }
        };

        public AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        public void setupAnim(GhiaccioEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.animator.m_6973_((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}

