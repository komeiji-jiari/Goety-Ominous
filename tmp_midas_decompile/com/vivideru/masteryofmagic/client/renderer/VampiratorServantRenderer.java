/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.ModModelLayer
 *  com.Polarice3.Goety.client.render.model.IllagerServantModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import com.vivideru.masteryofmagic.entity.VampiratorServantEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VampiratorServantRenderer
extends MobRenderer<VampiratorServantEntity, IllagerServantModel<VampiratorServantEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/vampirator_novice.png");

    public VampiratorServantRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new IllagerServantModel(context.m_174023_(ModModelLayer.ILLAGER_SERVANT)), 0.5f);
    }

    public ResourceLocation getTextureLocation(VampiratorServantEntity entity) {
        return TEXTURE;
    }
}

