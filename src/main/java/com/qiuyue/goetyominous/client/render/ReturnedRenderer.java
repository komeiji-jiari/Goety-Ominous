package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.BoundIllagerRenderer;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.BoundIllagerModel;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Returned;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ReturnedRenderer extends BoundIllagerRenderer<Returned> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/returned.png");
    protected static final ResourceLocation CASTING = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/returned.png");

    public ReturnedRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BoundIllagerModel(renderManagerIn.bakeLayer(ModModelLayer.BOUND_ILLAGER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, new VillagerArmorModel(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)), new VillagerArmorModel(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)), renderManagerIn.getModelManager()));
        this.addLayer(new ItemInHandLayer<Returned, BoundIllagerModel<Returned>>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, Returned p_116355_, float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_, float p_116361_) {
                if (p_116355_.getArmPose() != BoundArmPose.CROSSED) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
                }

            }
        });
    }

    public ResourceLocation getTextureLocation(Returned p_114482_) {
        return p_114482_.isCastingSpell() ? CASTING : TEXTURE;
    }
}
