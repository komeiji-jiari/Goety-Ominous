package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantShulkerTrapKeyframeAnimations;
import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerTrapMathAnimations;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerRewardsCommonConfig;
import com.alexander.mutantmore.models.entities.MMBaseEntityModel;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantShulkerServantTrapModel<T extends MutantShulkerServantTrap> extends MMBaseEntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyOminous.MOD_ID, "mutant_shulker_servant_trap"), "main");

    public MutantShulkerServantTrapModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0f, 20.0f, 0.0f));
        everything.addOrReplaceChild("side1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -6.0f, 0.0f, 16.0f, 6.0f, 8.0f, new CubeDeformation(-0.01f)).texOffs(0, 46).addBox(-8.0f, -2.0f, 0.0f, 16.0f, 1.0f, 8.0f, new CubeDeformation(-0.01f)), PartPose.offset(0.0f, 4.0f, 0.0f));
        everything.addOrReplaceChild("side2", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0f, -6.0f, -8.0f, 16.0f, 6.0f, 8.0f, new CubeDeformation(0.0f)).texOffs(0, 55).addBox(-8.0f, -2.0f, -8.0f, 16.0f, 1.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 4.0f, 0.0f));
        everything.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 1.0f, 0.0f));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (!pEntity.onGround() && pEntity.isSpawnedByMutantShulker()) {
            this.mathAnimate(pEntity, MutantShulkerTrapMathAnimations.AIRBORNE, 0L, 1.0f, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }
        this.mathAnimateState(pEntity, pEntity.idleAnimation, MutantShulkerTrapMathAnimations.IDLE, 1.0f, 1.0f, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (pEntity.isSpawnedByMutantShulker() && MutantShulkerRewardsCommonConfig.trap_mutant_shulker_multiuse.get()
                || !pEntity.isSpawnedByMutantShulker() && MutantShulkerRewardsCommonConfig.trap_player_multiuse.get()) {
            this.animate(pEntity.activateAnimation, MutantShulkerTrapKeyframeAnimations.PLAYER_ACTIVATE, pAgeInTicks);
        } else {
            this.animate(pEntity.activateAnimation, MutantShulkerTrapKeyframeAnimations.ACTIVATE, pAgeInTicks);
        }
        this.animate(pEntity.vanishAnimation, MutantShulkerTrapKeyframeAnimations.VANISH, pAgeInTicks);
    }
}
