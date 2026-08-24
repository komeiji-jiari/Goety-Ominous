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

import com.vivideru.masteryofmagic.client.model.IceMonarchModel;
import com.vivideru.masteryofmagic.client.model.animations.IceMonarchAnimation;
import com.vivideru.masteryofmagic.entity.IceMonarchEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class IceMonarchRenderer
extends MobRenderer<IceMonarchEntity, IceMonarchModel<IceMonarchEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/spirit.png");

    public IceMonarchRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new AnimatedModel(context.m_174023_(IceMonarchModel.LAYER_LOCATION)), 0.6f);
    }

    public ResourceLocation getTextureLocation(IceMonarchEntity entity) {
        return TEXTURE;
    }

    private static final class AnimatedModel
    extends IceMonarchModel<IceMonarchEntity> {
        private final ModelPart root;
        private final HierarchicalModel<IceMonarchEntity> animator = new HierarchicalModel<IceMonarchEntity>(){

            public ModelPart m_142109_() {
                return root;
            }

            public void setupAnim(IceMonarchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.m_142109_().m_171331_().forEach(ModelPart::m_233569_);
                float walkAmount = Math.min(limbSwingAmount * 2.5f, 1.0f);
                this.m_233385_(entity.idleAnimationState, IceMonarchAnimation.idle, ageInTicks, 1.0f);
                this.m_267799_(IceMonarchAnimation.walking, limbSwing, walkAmount, 1.0f, 2.0f);
                this.m_233385_(entity.castingAnimationState, IceMonarchAnimation.casting, ageInTicks, 1.0f);
                this.m_233385_(entity.punchingAnimationState, IceMonarchAnimation.punching, ageInTicks, 1.0f);
                this.m_233385_(entity.oraoraAnimationState, IceMonarchAnimation.oraora, ageInTicks, 1.0f);
            }
        };

        public AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        public void setupAnim(IceMonarchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.animator.m_6973_((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}

