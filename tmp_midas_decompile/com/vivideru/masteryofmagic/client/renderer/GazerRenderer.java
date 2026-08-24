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

import com.vivideru.masteryofmagic.client.model.GazerModel;
import com.vivideru.masteryofmagic.client.model.animations.GazerAnimation;
import com.vivideru.masteryofmagic.entity.GazerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class GazerRenderer
extends MobRenderer<GazerEntity, GazerModel<GazerEntity>> {
    public GazerRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new AnimatedModel(context.m_174023_(GazerModel.LAYER_LOCATION)), 0.5f);
    }

    public ResourceLocation getTextureLocation(GazerEntity entity) {
        return new ResourceLocation("goety_mastery_of_magic:textures/entities/gazer.png");
    }

    private static final class AnimatedModel
    extends GazerModel<GazerEntity> {
        private final ModelPart root;
        private final HierarchicalModel animator = new HierarchicalModel<GazerEntity>(){

            public ModelPart m_142109_() {
                return root;
            }

            public void setupAnim(GazerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);
                this.m_233385_(entity.idleAnimationState, GazerAnimation.idle, ageInTicks, 1.0f);
                this.m_267799_(GazerAnimation.walk, limbSwing, limbSwingAmount, 1.0f, 1.0f);
                this.m_233385_(entity.attackAnimationState, GazerAnimation.attack, ageInTicks, 1.0f);
                this.m_233385_(entity.teleportattackAnimationState, GazerAnimation.teleportattack, ageInTicks, 1.0f);
                this.m_233385_(entity.dodgeAnimationState, GazerAnimation.dodge, ageInTicks, 1.0f);
            }
        };

        public AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        public void setupAnim(GazerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.animator.m_6973_((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            super.m_6973_(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}

