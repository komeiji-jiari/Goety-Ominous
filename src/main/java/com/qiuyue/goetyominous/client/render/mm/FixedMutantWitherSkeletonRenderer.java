package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.entities.MutantWitherSkeleton;
import com.alexander.mutantmore.models.entities.MutantWitherSkeletonModel;
import com.alexander.mutantmore.renderers.entities.MutantWitherSkeletonRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantWitherSkeletonModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantMobAnimationState;
import com.qiuyue.goetyominous.client.render.model.mm.MutantMobAnimationStates;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantWitherSkeletonRenderer extends MobRenderer<MutantWitherSkeleton, MutantWitherSkeletonModel<MutantWitherSkeleton>> {
    public FixedMutantWitherSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantWitherSkeletonModel(context.bakeLayer(MutantWitherSkeletonModel.MAIN)), 1.25F);
        AdvancedArmourLayer.addCustomArmourLayers(this, context, MutantWitherSkeletonModel.INNER_ARMOUR, MutantWitherSkeletonModel.OUTER_ARMOUR);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    protected void scale(MutantWitherSkeleton entity, PoseStack poseStack, float partialTicks) {
        super.scale(entity, poseStack, partialTicks);
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    protected void setupRotations(MutantWitherSkeleton entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        MutantMobAnimationState state = MutantMobAnimationStates.get(entity);
        if (state.lastAnimTick != entity.tickCount) {
            state.lastAnimTick = entity.tickCount;
            state.lungingXRotO = state.lungingXRot;
            state.lungingXRot = entity.getAnimation("lunging").isPlaying() ? (float) Mth.clamp(entity.getDeltaMovement().y * 30.0, -90.0, 90.0) : 0.0F;
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, state.lungingXRotO, state.lungingXRot)));
    }

    protected float getFlipDegrees(MutantWitherSkeleton entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantWitherSkeleton entity) {
        return MutantWitherSkeletonRenderer.getTexture();
    }
}
