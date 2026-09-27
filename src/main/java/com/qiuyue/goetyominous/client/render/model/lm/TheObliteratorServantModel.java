package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations10;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations11;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations12;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations13;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations14;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations2;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations3;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations4;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations5;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations6;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations7;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations8;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.TheObliterator.TheObliteratorAnimations9;
import net.miauczel.legendary_monsters.entity.client.Model.TheObliteratorModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorServantModel<T extends TheObliteratorServant> extends HierarchicalModel<T> {

    public final ModelPart root;
    public final ModelPart body;
    public final ModelPart bigCube;
    private final ModelPart theEye;
    private final ModelPart bodyNeutralRotation;
    private final ModelPart[] rightArmPath;
    private final ModelPart[] leftArmPath;

    public TheObliteratorServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.bodyNeutralRotation = this.root.getChild("body_neutral_rotation");
        this.body = this.bodyNeutralRotation.getChild("body");
        this.bigCube = this.body.getChild("big_cube");
        this.theEye = this.bigCube.getChild("inner_cube_body").getChild("the_eye");

        ModelPart rightArm = this.body.getChild("right_arm");
        ModelPart mainRotator = rightArm.getChild("main_rotator");
        ModelPart shoulderRight = mainRotator.getChild("shoulder_right");
        ModelPart forearmRight = shoulderRight.getChild("forearm_right");
        this.rightArmPath = new ModelPart[]{rightArm, mainRotator, shoulderRight, forearmRight, forearmRight.getChild("finger_right")};

        ModelPart leftArm = this.body.getChild("left_arm");
        ModelPart mainRotator2 = leftArm.getChild("main_rotator2");
        ModelPart shoulderLeft = mainRotator2.getChild("shoulder_left");
        ModelPart forearmLeft = shoulderLeft.getChild("forearm_left");
        this.leftArmPath = new ModelPart[]{leftArm, mainRotator2, shoulderLeft, forearmLeft, forearmLeft.getChild("finger_left")};
    }

    public void translateModelToRightArm(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.bodyNeutralRotation.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        for (ModelPart part : this.rightArmPath) {
            part.translateAndRotate(poseStack);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    }

    public void translateModelToLeftArm(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.bodyNeutralRotation.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        for (ModelPart part : this.leftArmPath) {
            part.translateAndRotate(poseStack);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    }

    public static LayerDefinition createBodyLayer() {
        return TheObliteratorModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.theEye.yRot = netHeadYaw * ((float) Math.PI / 180);
        this.theEye.xRot = headPitch * ((float) Math.PI / 180);

        this.animate(entity.getAnimationState("idle"), TheObliteratorAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("spin_smash"), TheObliteratorAnimations3.rightSpinSmash2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("spin_smash_left"), TheObliteratorAnimations3.leftArmSpinSmash2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shoot_double"), TheObliteratorAnimations.powerBallShootDouble, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shoot_once"), TheObliteratorAnimations9.powerBallShootPre2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shoot_once_end"), TheObliteratorAnimations9.PowerBallShootEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shoot_double_end"), TheObliteratorAnimations9.PowerBallShootDoubleEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_hook_db_combo"), TheObliteratorAnimations.LeftHookComboFaster, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("roar_teleport"), TheObliteratorAnimations.roarTPNew, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("land"), TheObliteratorAnimations.land, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("land_again"), TheObliteratorAnimations13.landTeleport, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fall"), TheObliteratorAnimations.fall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ambush"), TheObliteratorAnimations14.ambush2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ambush_longer"), TheObliteratorAnimations14.ambushCut2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ambush_swap"), TheObliteratorAnimations14.ambushReinforced, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_combo_right"), TheObliteratorAnimations10.SlashComboRight2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slash_combo"), TheObliteratorAnimations10.SlashComboLeft2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_slash_combo"), TheObliteratorAnimations10.teleportSlashComboLeftSlower5, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_slash_combo_right"), TheObliteratorAnimations10.teleportSlashComboRightSlower4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_uppercut"), TheObliteratorAnimations2.teleportUppercut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_pre"), TheObliteratorAnimations2.teleportGrabPre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_success"), TheObliteratorAnimations2.teleportGrabSuccess, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_fall"), TheObliteratorAnimations2.teleportGrabFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_land"), TheObliteratorAnimations12.teleportGrabLand, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_fail_rise_up"), TheObliteratorAnimations2.teleport_grab_fail_rise_up, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_fail_cross_slash"), TheObliteratorAnimations13.tpGrabFail2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_fail_0"), TheObliteratorAnimations14.tpGrabFail4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jump_teleport"), TheObliteratorAnimations13.jumpTeleport6, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("clone_burst_grab"), TheObliteratorAnimations2.clone_burst_grab_failNew, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("kick_smash_grab"), TheObliteratorAnimations3.kick_smash_grab, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("right_spin_teleport_smash"), TheObliteratorAnimations3.rightSpinTeleportSmash, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep_teleport_slash_p2"), TheObliteratorAnimations3.teleportTrackingBallsP2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep_stomp_left"), TheObliteratorAnimations12.backstep_left_stomp, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep_stomp_right"), TheObliteratorAnimations12.backstep_right_stomp, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("tracking_ball_charge"), TheObliteratorAnimations4.trackingBallCharge, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp"), TheObliteratorAnimations4.stomp_main, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_stun_teleport"), TheObliteratorAnimations13.stun_after_stompNew3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_kick"), TheObliteratorAnimations14.kick_after_stomp_new3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_after_kick_right"), TheObliteratorAnimations4.stomp_after_kick_stomp_right1, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_after_kick_left"), TheObliteratorAnimations4.stomp_after_kick_stomp_left, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("spawn"), TheObliteratorAnimations13.spawn3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), TheObliteratorAnimations13.death5, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("p2"), TheObliteratorAnimations6.p2New, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("kick_smash"), TheObliteratorAnimations11.kick_smash_no_grab2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_after_kick_smash"), TheObliteratorAnimations6.grab_after_kick_smash_cut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("kick_smash_bye_bye"), TheObliteratorAnimations10.kick_smash_bye_bye3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_grab_after_kick_smash"), TheObliteratorAnimations5.teleport_grab_after_kick_smash_cut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("crush_grab_success"), TheObliteratorAnimations9.crushGrabSuccessThrowRight, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("crush_grab_fail"), TheObliteratorAnimations5.crush_grab_fail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_shot_laser"), TheObliteratorAnimations10.singleLaserShot2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_shot_laser_end"), TheObliteratorAnimations9.singleShotLaserEnd2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_shot_laser_teleport_end"), TheObliteratorAnimations9.singleShotLaserTeleportEnd2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_shot_laser_after_stomp"), TheObliteratorAnimations6.single_shot_laser_after_stomp, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_away_single_shot_laser"), TheObliteratorAnimations10.teleportAwaySingleLaserShot2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_shot_laser_quad_end"), TheObliteratorAnimations11.singleShotLaserQuadEndVariant2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("close_pre_teleport_grab"), TheObliteratorAnimations7.close_teleport_grab_pre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep_stomp_right_spin"), TheObliteratorAnimations7.backstep_stomp_right_spin, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("p3"), TheObliteratorAnimations7.p3_2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_grab_after_kick_smash"), TheObliteratorAnimations7.left_grab_after_kick_smash, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_crush_grab_success"), TheObliteratorAnimations9.crushGrabSuccessThrowLeft, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_crush_grab_fail"), TheObliteratorAnimations7.left_crush_grab_fail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("arm_block"), TheObliteratorAnimations8.block, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ultimate"), TheObliteratorAnimations8.UltimateFlame12, ageInTicks, 1.0F);
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
