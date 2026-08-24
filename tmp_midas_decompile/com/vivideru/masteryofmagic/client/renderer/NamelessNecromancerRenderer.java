/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.AbstractNecromancerRenderer
 *  com.Polarice3.Goety.client.render.AbstractNecromancerRenderer$NecromancerEyesLayer
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.resources.ResourceLocation
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.Polarice3.Goety.client.render.AbstractNecromancerRenderer;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class NamelessNecromancerRenderer
extends AbstractNecromancerRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entity/necromancer/nameless_necromancer_servant.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entity/necromancer/nameless_necromancer_glow.png");

    public NamelessNecromancerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.m_115326_((RenderLayer)new AbstractNecromancerRenderer.NecromancerEyesLayer((RenderLayerParent)this, EYES_TEXTURE));
    }

    public ResourceLocation getTextureLocation(AbstractNecromancer entity) {
        return TEXTURE;
    }
}

