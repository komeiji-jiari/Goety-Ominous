package com.qiuyue.goetyominous.client.render.model;

import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import com.qiuyue.goetyominous.client.render.model.animation.WargAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WargSaddleModel extends HierarchicalModel<Warg> {
    private final ModelPart root;

    public WargSaddleModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition warg = partdefinition.addOrReplaceChild("warg", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -1.25F));
        PartDefinition upperbody = warg.addOrReplaceChild("upperbody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition mane = upperbody.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -6.0F, -7.5F, 1.5708F, 0.0F, 0.0F));
        PartDefinition group = mane.addOrReplaceChild("group", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.5F, -4.5F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.0F, 11.9031F, -0.1421F, -1.5708F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Warg entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        PoseBlend.animate(this, entity.idleAnimationState, WargAnimations.idle, ageInTicks, entity.getIdleWeight(ageInTicks));
        PoseBlend.animate(this, entity.walkAnimationState, WargAnimations.walk, ageInTicks, entity.getWalkAnimationScale(), entity.getWalkWeight(ageInTicks));
        PoseBlend.animate(this, entity.runAnimationState, WargAnimations.run, ageInTicks, entity.getRunAnimationScale(), entity.getRunWeight(ageInTicks));
        PoseBlend.animate(this, entity.groundedAnimationState, WargAnimations.grounded, ageInTicks, entity.getGroundedWeight(ageInTicks));
        PoseBlend.animate(this, entity.jumpStartAnimationState, WargAnimations.jump_start, ageInTicks, entity.getJumpStartWeight(ageInTicks));
        PoseBlend.animate(this, entity.jumpLoopAnimationState, WargAnimations.jump_loop, ageInTicks, entity.getJumpLoopWeight(ageInTicks));
        PoseBlend.animate(this, entity.jumpLandAnimationState, WargAnimations.jump_land, ageInTicks, entity.getJumpLandWeight(ageInTicks));
        PoseBlend.animate(this, entity.biteAnimationState, WargAnimations.bite, ageInTicks, entity.getBiteWeight(ageInTicks));
        PoseBlend.animate(this, entity.slashAnimationState, WargAnimations.sword_attack, ageInTicks, entity.getSlashWeight(ageInTicks));
        PoseBlend.animate(this, entity.spinAnimationState, WargAnimations.sword_spin_attack, ageInTicks, entity.getSpinWeight(ageInTicks));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
