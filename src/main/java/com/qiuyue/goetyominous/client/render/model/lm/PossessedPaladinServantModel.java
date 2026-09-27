package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.FlamebornGuardAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.PossessedPaladin.PossessedPaladinAnimations1;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.PossessedPaladin.PossessedPaladinAnimations2;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.PossessedPaladin.PossessedPaladinAnimations3;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.PossessedPaladin.PossessedPaladinAnimations4;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.PossessedPaladin.PossessedPaladinAnimations5;
import net.miauczel.legendary_monsters.entity.animations.PPAnims;
import net.miauczel.legendary_monsters.entity.client.Model.NewPossessedPaladinModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantModel<T extends PossessedPaladinServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart lowerbody;
    private final ModelPart body;
    private final ModelPart cape;
    private final ModelPart lower;
    private final ModelPart GimbalRotator;
    private final ModelPart rightArm;
    private final ModelPart lowerarm2;
    private final ModelPart sword;
    private final ModelPart SoulGreatSword;
    private final ModelPart bone;
    private final ModelPart leftArm;
    private final ModelPart lowerarm;
    private final ModelPart bone2;
    private final ModelPart shield;
    private final ModelPart trident;
    private final ModelPart dagger;
    private final ModelPart head;
    private final ModelPart RightWing;
    private final ModelPart RightWingEdge;
    private final ModelPart LeftWing;
    private final ModelPart LeftWingEdge;
    private final ModelPart legs;
    private final ModelPart rightLeg;
    private final ModelPart middleright;
    private final ModelPart lowerlegright;
    private final ModelPart leftLeg;
    private final ModelPart middle;
    private final ModelPart lowerleg;

    public PossessedPaladinServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.lowerbody = this.root.getChild("lowerbody");
        this.body = this.lowerbody.getChild("body");
        this.cape = this.body.getChild("cape");
        this.lower = this.cape.getChild("lower");
        this.GimbalRotator = this.body.getChild("GimbalRotator");
        this.rightArm = this.GimbalRotator.getChild("rightArm");
        this.lowerarm2 = this.rightArm.getChild("lowerarm2");
        this.sword = this.lowerarm2.getChild("sword");
        this.SoulGreatSword = this.sword.getChild("SoulGreatSword");
        this.bone = this.SoulGreatSword.getChild("bone");
        this.leftArm = this.body.getChild("leftArm");
        this.lowerarm = this.leftArm.getChild("lowerarm");
        this.bone2 = this.lowerarm.getChild("bone2");
        this.shield = this.bone2.getChild("shield");
        this.trident = this.lowerarm.getChild("trident");
        this.dagger = this.lowerarm.getChild("dagger");
        this.head = this.body.getChild("head");
        this.RightWing = this.body.getChild("RightWing");
        this.RightWingEdge = this.RightWing.getChild("RightWingEdge");
        this.LeftWing = this.body.getChild("LeftWing");
        this.LeftWingEdge = this.LeftWing.getChild("LeftWingEdge");
        this.legs = this.root.getChild("legs");
        this.rightLeg = this.legs.getChild("rightLeg");
        this.middleright = this.rightLeg.getChild("middleright");
        this.lowerlegright = this.middleright.getChild("lowerlegright");
        this.leftLeg = this.legs.getChild("leftLeg");
        this.middle = this.leftLeg.getChild("middle");
        this.lowerleg = this.middle.getChild("lowerleg");
    }

    public static LayerDefinition createBodyLayer() {
        return NewPossessedPaladinModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        if (entity.getAttackState() == 0 || entity.getAttackState() == 9) {
            this.animateWalk(PPAnims.walk, limbSwing, limbSwingAmount, 1.0F, 4.0F);
        }

        this.animate(entity.getAnimationState("sleep"), PossessedPaladinAnimations4.sleep, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("awaken"), PossessedPaladinAnimations4.awaken3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("idle"), PossessedPaladinAnimations4.newIdle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), FlamebornGuardAnimations.death2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_slash"), PossessedPaladinAnimations1.DoubleSlashVisualEnd4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_slash_end"), PossessedPaladinAnimations1.DoubleSlashSlamVisual2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_slash_slam_end"), PossessedPaladinAnimations3.DoubleSlashSlamVisual4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("sword_slam_cut"), PossessedPaladinAnimations1.swordSlamCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("sword_slam_end"), PossessedPaladinAnimations1.swordSlamEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("sword_slam_counter_end"), PossessedPaladinAnimations1.swordSlamCounterEnd2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("sword_slam_counter_release"), PossessedPaladinAnimations3.swordSlamCounterEnd5, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backflip"), PossessedPaladinAnimations1.backflip, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backflip_end"), PossessedPaladinAnimations3.backflipEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backflip_double"), PossessedPaladinAnimations3.backflipDB, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("parry"), PossessedPaladinAnimations1.parry2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fly_away_slash"), PossessedPaladinAnimations1.flyAwaySlash4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("flip_smash"), PossessedPaladinAnimations2.flipSmashCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("flip_smash_end"), PossessedPaladinAnimations2.flipSmashEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("flip_smash_flip"), PossessedPaladinAnimations2.flipSmashFlipEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from"), PossessedPaladinAnimations2.slashFromCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_end"), PossessedPaladinAnimations2.slashFromEnd2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_stab"), PossessedPaladinAnimations4.slashFromStabEnd8, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_stab_grab_pre"), PossessedPaladinAnimations2.slashStabFromGrabPre2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_stab_grab_fail"), PossessedPaladinAnimations2.slashFromStabGrabFail2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_stab_grab_stab_fail"), PossessedPaladinAnimations4.slashFromStabGrabStabFail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_from_stab_grab_success"), PossessedPaladinAnimations2.slashFromStabGrabSuccess, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jump_pre"), PossessedPaladinAnimations2.JumpPre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jump_fall"), PossessedPaladinAnimations2.JumpFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jump_smash"), PossessedPaladinAnimations2.JumpSlamLonger, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jump_smash_combo"), PossessedPaladinAnimations4.JumpSlamCombo3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("throw"), PossessedPaladinAnimations2.throw3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("throw_double"), PossessedPaladinAnimations3.throw5, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shield_smash"), PossessedPaladinAnimations3.shieldSlam3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("second_phase"), PossessedPaladinAnimations3.secondPhase, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("side_roll_spin"), PossessedPaladinAnimations3.SideRollSpinSlash2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_side_roll_spin"), PossessedPaladinAnimations3.LeftSideRollSpinSlash, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), PossessedPaladinAnimations4.death2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("finisher"), PossessedPaladinAnimations5.finisher2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("trident_throw_spin"), PossessedPaladinAnimations5.trident_throw_spinCombo3, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 25.0F);
        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
    }

    public void translateModel(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.lowerbody.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.GimbalRotator.translateAndRotate(poseStack);
        this.rightArm.translateAndRotate(poseStack);
        this.lowerarm2.translateAndRotate(poseStack);
        this.sword.translateAndRotate(poseStack);
        this.SoulGreatSword.translateAndRotate(poseStack);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
