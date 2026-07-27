package com.qiuyue.goetyominus.client.render;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.util.MiscUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.model.mm.MutantWitherSkeletonServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantWitherSkeletonServantRenderer extends MobRenderer<MutantWitherSkeletonServant, MutantWitherSkeletonServantModel<MutantWitherSkeletonServant>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_wither_skeleton_servant.png");
    public static final ResourceLocation APRIL_FOOLS = new ResourceLocation("mutantmore", "textures/entities/mutant_wither_skeleton_april_fools.png");

    public MutantWitherSkeletonServantRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantWitherSkeletonServantModel(context.bakeLayer(com.qiuyue.goetyominus.client.render.model.mm.MutantWitherSkeletonServantModel.MAIN)), 1.25F);
        AdvancedArmourLayer.addCustomArmourLayers(this, context, com.qiuyue.goetyominus.client.render.model.mm.MutantWitherSkeletonServantModel.INNER_ARMOUR, com.qiuyue.goetyominus.client.render.model.mm.MutantWitherSkeletonServantModel.OUTER_ARMOUR);
        this.addLayer(new ItemInHandLayer(this, context.getItemInHandRenderer()));
    }


    protected void scale(MutantWitherSkeletonServant p_115314_, PoseStack p_115315_, float p_115316_) {
        super.scale(p_115314_, p_115315_, p_115316_);
        float scaleFactor = 1.2F;
        p_115315_.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    protected void setupRotations(MutantWitherSkeletonServant p_115685_, PoseStack p_115686_, float p_115687_, float p_115688_, float p_115689_) {
        super.setupRotations(p_115685_, p_115686_, p_115687_, p_115688_, p_115689_);
        p_115685_.lungingXRot = (float)Mth.lerp((double)p_115689_, (double)p_115685_.lungingXRot, p_115685_.getAnimation("lunging").isPlaying() ? Mth.clamp(p_115685_.getDeltaMovement().y * 30.0, -90.0, 90.0) : 0.0);
        p_115686_.mulPose(Axis.XP.rotationDegrees(p_115685_.lungingXRot));
    }

    protected float getFlipDegrees(MutantWitherSkeletonServant p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantWitherSkeletonServant entity) {
        return getTexture();
    }

    public static ResourceLocation getTexture() {
        return MiscUtils.isAprilFools() ? APRIL_FOOLS : TEXTURE;
    }
}