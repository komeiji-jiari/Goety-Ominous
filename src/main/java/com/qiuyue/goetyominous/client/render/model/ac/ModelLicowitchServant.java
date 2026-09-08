package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.ac.LicowitchServant;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLicowitchServant extends AdvancedEntityModel<LicowitchServant> implements ArmedModel {

    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox dress;
    private final AdvancedModelBox head;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox hat;
    private final AdvancedModelBox hat2;
    private final AdvancedModelBox hat3;
    private final AdvancedModelBox hat4;
    private final AdvancedModelBox arms;
    private final AdvancedModelBox armsCrossed;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox left_Hand;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox right_Hand;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox left_Leg;
    private final ModelAnimator animator;

    public ModelLicowitchServant() {
        this.texWidth = 64;
        this.texHeight = 128;
        this.main = new AdvancedModelBox(this);
        this.main.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, -14.0F, 0.75F);
        this.main.addChild(this.body);
        this.body.setTextureOffset(16, 20).addBox(-4.0F, -10.0F, -3.75F, 8.0F, 12.0F, 6.0F, 0.0F, false);
        this.body.setTextureOffset(0, 38).addBox(-4.0F, -10.0F, -3.75F, 8.0F, 18.0F, 6.0F, 0.5F, false);
        this.dress = new AdvancedModelBox(this);
        this.dress.setRotationPoint(0.0F, -1.0F, -2.5F);
        this.body.addChild(this.dress);
        this.dress.setTextureOffset(9, 105).addBox(-8.0F, 0.0F, -1.5F, 16.0F, 12.0F, 11.0F, 0.0F, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, -10.0F, -0.75F);
        this.body.addChild(this.head);
        this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        this.head.setTextureOffset(30, 61).addBox(-5.0F, -4.0F, -1.0F, 10.0F, 6.0F, 7.0F, 0.0F, false);
        this.nose = new AdvancedModelBox(this);
        this.nose.setRotationPoint(0.0F, -3.0F, -4.0F);
        this.head.addChild(this.nose);
        this.nose.setTextureOffset(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
        this.nose.setTextureOffset(0, 0).addBox(0.0F, 2.0F, -2.75F, 1.0F, 1.0F, 1.0F, -0.25F, false);
        this.nose.setTextureOffset(0, 0).addBox(-1.25F, -0.25F, -2.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        this.hat = new AdvancedModelBox(this);
        this.hat.setRotationPoint(0.0F, -7.05F, 0.0F);
        this.head.addChild(this.hat);
        this.hat.setTextureOffset(0, 75).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 2.0F, 16.0F, 0.0F, false);
        this.hat2 = new AdvancedModelBox(this);
        this.hat2.setRotationPoint(0.3571F, -3.0609F, 0.7114F);
        this.hat.addChild(this.hat2);
        this.setRotateAngle(this.hat2, -0.0524F, 0.0F, 0.0262F);
        this.hat2.setTextureOffset(0, 63).addBox(-3.5524F, -4.4966F, -3.6046F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        this.hat2.setTextureOffset(0, 93).addBox(-3.5524F, -4.4966F, -3.6046F, 7.0F, 5.0F, 7.0F, 0.25F, false);
        this.hat3 = new AdvancedModelBox(this);
        this.hat3.setRotationPoint(0.4651F, -4.1322F, 0.9235F);
        this.hat2.addChild(this.hat3);
        this.setRotateAngle(this.hat3, -0.1047F, 0.0F, 0.0524F);
        this.hat3.setTextureOffset(0, 75).addBox(-2.1047F, -4.4863F, -2.2088F, 4.0F, 5.0F, 4.0F, 0.0F, false);
        this.hat4 = new AdvancedModelBox(this);
        this.hat4.setRotationPoint(0.724F, -4.5446F, 1.4457F);
        this.hat3.addChild(this.hat4);
        this.setRotateAngle(this.hat4, -0.2094F, 0.0F, 0.1047F);
        this.hat4.setTextureOffset(0, 84).addBox(-0.6045F, -2.4728F, -0.7068F, 1.0F, 3.0F, 1.0F, 0.25F, false);
        this.arms = new AdvancedModelBox(this);
        this.arms.setRotationPoint(0.0F, -8.0F, -0.75F);
        this.body.addChild(this.arms);
        this.setRotateAngle(this.arms, -0.7854F, 0.0F, 0.0F);
        this.armsCrossed = new AdvancedModelBox(this);
        this.armsCrossed.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.arms.addChild(this.armsCrossed);
        this.armsCrossed.setTextureOffset(40, 38).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);
        this.left_Arm = new AdvancedModelBox(this);
        this.left_Arm.setRotationPoint(4.0F, 0.0F, 0.0F);
        this.arms.addChild(this.left_Arm);
        this.left_Arm.setTextureOffset(44, 22).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        this.left_Arm.setTextureOffset(28, 42).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.5F, false);
        this.left_Hand = new AdvancedModelBox(this);
        this.left_Hand.setRotationPoint(2.0F, 8.0F, 0.0F);
        this.left_Arm.addChild(this.left_Hand);
        this.left_Hand.setTextureOffset(32, 10).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        this.right_Arm = new AdvancedModelBox(this);
        this.right_Arm.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.arms.addChild(this.right_Arm);
        this.right_Arm.setTextureOffset(44, 22).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, true);
        this.right_Arm.setTextureOffset(28, 42).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.5F, true);
        this.right_Hand = new AdvancedModelBox(this);
        this.right_Hand.setRotationPoint(-2.0F, 8.0F, 0.0F);
        this.right_Arm.addChild(this.right_Hand);
        this.right_Hand.setTextureOffset(32, 10).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);
        this.right_Leg = new AdvancedModelBox(this);
        this.right_Leg.setRotationPoint(-2.0F, 2.0F, -0.75F);
        this.body.addChild(this.right_Leg);
        this.right_Leg.setTextureOffset(48, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, true);
        this.right_Leg.setTextureOffset(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        this.left_Leg = new AdvancedModelBox(this);
        this.left_Leg.setRotationPoint(2.0F, 2.0F, -0.75F);
        this.body.addChild(this.left_Leg);
        this.left_Leg.setTextureOffset(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        this.left_Leg.setTextureOffset(48, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.main, this.body, this.left_Leg, this.right_Leg, this.dress, this.head,
                this.nose, this.hat, this.hat2, this.hat3, this.hat4, this.arms, this.armsCrossed,
                this.left_Arm, this.right_Arm, this.left_Hand, this.right_Hand);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.main);
    }

    public void animate(LicowitchServant entity) {
        boolean left = entity.getMainArm() == HumanoidArm.LEFT;
        this.animator.update(entity);
        this.animator.setAnimation(LicowitchServant.ANIMATION_SWING_RIGHT);
        this.animator.startKeyframe(2);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(25.0), (float) Math.toRadians(-15.0), 0.0F);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(4);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-90.0), (float) Math.toRadians(15.0), (float) Math.toRadians(-25.0));
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(1);
        this.animator.resetKeyframe(3);
        this.animator.setAnimation(LicowitchServant.ANIMATION_SWING_LEFT);
        this.animator.startKeyframe(2);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(25.0), (float) Math.toRadians(15.0), 0.0F);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(4);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-90.0), (float) Math.toRadians(-15.0), (float) Math.toRadians(25.0));
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(1);
        this.animator.resetKeyframe(3);
        this.animator.setAnimation(LicowitchServant.ANIMATION_EAT);
        this.animator.startKeyframe(10);
        this.animator.rotate(this.head, (float) Math.toRadians(-10.0), 0.0F, 0.0F);
        this.animator.rotate(this.nose, (float) Math.toRadians(-55.0), 0.0F, 0.0F);
        this.animator.move(this.nose, 0.0F, 0.0F, 2.0F);
        this.animator.move(this.arms, 0.0F, 2.0F, -1.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(80);
        this.animator.resetKeyframe(10);
        this.animator.setAnimation(LicowitchServant.ANIMATION_SPELL_0);
        this.animator.startKeyframe(10);
        this.animator.rotate(this.head, (float) Math.toRadians(-15.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(40.0), (float) Math.toRadians(40.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(-40.0), (float) Math.toRadians(-40.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(4);
        this.animator.rotate(this.head, (float) Math.toRadians(-5.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-90.0), (float) Math.toRadians(-10.0), (float) Math.toRadians(-35.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-90.0), (float) Math.toRadians(10.0), (float) Math.toRadians(35.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(6);
        this.animator.rotate(this.body, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(-15.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(25.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(25.0), 0.0F, 0.0F);
        this.animator.rotate(this.dress, (float) Math.toRadians(25.0), 0.0F, 0.0F);
        this.animator.move(this.body, 0.0F, 1.0F, 1.0F);
        this.animator.move(this.dress, 0.0F, 0.0F, -2.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-50.0), (float) Math.toRadians(-10.0), (float) Math.toRadians(-35.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-50.0), (float) Math.toRadians(10.0), (float) Math.toRadians(35.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(5);
        this.animator.rotate(this.body, (float) Math.toRadians(25.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(-35.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.dress, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.getArmCube(entity), (float) Math.toRadians(-100.0), (float) Math.toRadians(left ? -10.0 : 10.0), (float) Math.toRadians(left ? 35.0 : -35.0));
        this.animator.move(this.getArmCube(entity), 0.0F, 0.0F, -2.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(10);
        this.animator.resetKeyframe(10);
        this.animator.setAnimation(LicowitchServant.ANIMATION_SPELL_1);
        this.animator.startKeyframe(20);
        this.animator.rotate(this.head, (float) Math.toRadians(-15.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(40.0), (float) Math.toRadians(40.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(-40.0), (float) Math.toRadians(-40.0));
        this.animator.move(this.body, 0.0F, -2.0F, 1.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.body, (float) Math.toRadians(65.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(-75.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.dress, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.getArmCube(entity), (float) Math.toRadians(-115.0), (float) Math.toRadians(left ? -10.0 : 10.0), (float) Math.toRadians(left ? 35.0 : -35.0));
        this.animator.move(this.getArmCube(entity), 0.0F, 0.0F, -2.0F);
        this.animator.move(this.body, 0.0F, -2.0F, 1.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(10);
        this.animator.resetKeyframe(10);
    }

    @Override
    public void setupAnim(LicowitchServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity);
        float partialTicks = ageInTicks - (float) entity.tickCount;
        float unfurlArmsProgress = entity.getArmsUncrossedProgress(partialTicks);
        float teleportingProgress = Math.min(entity.getTeleportingProgress(partialTicks) * 5.0F, 1.0F) * unfurlArmsProgress;
        boolean crossedArms = entity.areArmsVisuallyCrossed(partialTicks);
        float walkSpeed = 0.5F;
        float walkDegree = 0.8F;
        this.progressPositionPrev(this.dress, limbSwingAmount, 0.0F, -0.5F, -3.0F, 1.0F);
        this.progressRotationPrev(this.dress, limbSwingAmount, (float) Math.toRadians(10.0), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.left_Arm, unfurlArmsProgress, (float) Math.toRadians(40.0), (float) Math.toRadians(20.0), (float) Math.toRadians(-20.0), 1.0F);
        this.progressRotationPrev(this.right_Arm, unfurlArmsProgress, (float) Math.toRadians(40.0), (float) Math.toRadians(-20.0), (float) Math.toRadians(20.0), 1.0F);
        this.progressPositionPrev(this.left_Arm, teleportingProgress, 0.0F, -2.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.right_Arm, teleportingProgress, 0.0F, -2.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.head, teleportingProgress, (float) Math.toRadians(-20.0), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.left_Arm, teleportingProgress, (float) Math.toRadians(-160.0), (float) Math.toRadians(-40.0), (float) Math.toRadians(40.0), 1.0F);
        this.progressRotationPrev(this.right_Arm, teleportingProgress, (float) Math.toRadians(-160.0), (float) Math.toRadians(40.0), (float) Math.toRadians(-40.0), 1.0F);
        this.flap(this.nose, 0.035F, 0.08F, false, -1.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(this.nose, 0.035F, 0.08F, false, 0.0F, -0.08F, ageInTicks, 1.0F);
        this.flap(this.left_Arm, 0.1F, 0.1F, false, 0.0F, -0.15F, ageInTicks, unfurlArmsProgress);
        this.flap(this.right_Arm, 0.1F, 0.1F, true, 0.0F, -0.15F, ageInTicks, unfurlArmsProgress);
        this.armsCrossed.showModel = crossedArms;
        this.left_Hand.showModel = !crossedArms;
        this.right_Hand.showModel = !crossedArms;
        this.head.rotateAngleY += netHeadYaw * ((float) Math.PI / 180);
        this.head.rotateAngleX += headPitch * ((float) Math.PI / 180);
        this.walk(this.left_Leg, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(this.right_Leg, walkSpeed, walkDegree, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(this.left_Arm, walkSpeed, walkDegree, true, 0.0F, 0.3F, limbSwing, limbSwingAmount * unfurlArmsProgress);
        this.walk(this.right_Arm, walkSpeed, walkDegree, false, 0.0F, -0.3F, limbSwing, limbSwingAmount * unfurlArmsProgress);
        this.swing(this.dress, walkSpeed, walkDegree * 0.1F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(this.body, walkSpeed, walkDegree * 0.05F, true, 2.0F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(this.head, walkSpeed, walkDegree * 0.05F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(this.left_Arm, 0.5F, 0.5F, true, 0.0F, 0.0F, ageInTicks, teleportingProgress);
        this.walk(this.right_Arm, 0.5F, 0.5F, false, 0.0F, 0.0F, ageInTicks, teleportingProgress);
        this.body.rotationPointY += teleportingProgress * (-5.0F - ACMath.walkValue(ageInTicks, 1.0F, 0.25F, 0.0F, 4.0F, false));
        float f = -Math.min(this.left_Leg.rotateAngleX, this.right_Leg.rotateAngleX);
        this.dress.rotationPointZ -= f * 1.5F;
        this.dress.setScale(1.0F, 1.0F, 1.0F + limbSwingAmount * 0.33F);
        float runWalkBob = -Math.abs(ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 1.0F, 1.0F, false));
        this.body.rotationPointY += runWalkBob;
        this.left_Leg.rotationPointY -= runWalkBob;
        this.right_Leg.rotationPointY -= runWalkBob;
        this.dress.rotationPointY -= runWalkBob;
        if (entity.getAnimation() == LicowitchServant.ANIMATION_EAT) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4.0F, LicowitchServant.ANIMATION_EAT, partialTicks, 0);
            this.walk(this.head, 0.75F, 0.1F, false, 0.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.arms, 0.75F, 0.3F, true, 1.0F, 0.2F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == LicowitchServant.ANIMATION_SPELL_0) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 2.0F, LicowitchServant.ANIMATION_SPELL_0, partialTicks, 18, 25);
            AdvancedModelBox arm = this.getArmCube(entity);
            arm.rotationPointZ = (float) (arm.rotationPointZ + (double) animationIntensity * Math.sin(ageInTicks * 2.0F));
            this.walk(arm, 1.0F, 0.1F, false, 0.0F, -0.1F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == LicowitchServant.ANIMATION_SPELL_1) {
            float animationIntensity1 = ACMath.cullAnimationTick(entity.getAnimationTick(), 3.0F, LicowitchServant.ANIMATION_SPELL_1, partialTicks, 0, 40);
            float animationIntensity2 = ACMath.cullAnimationTick(entity.getAnimationTick(), 3.0F, LicowitchServant.ANIMATION_SPELL_1, partialTicks, 25, 25);
            this.walk(this.left_Leg, 0.3F, 0.2F, false, 0.0F, 0.0F, ageInTicks, animationIntensity1);
            this.walk(this.right_Leg, 0.3F, 0.2F, true, 0.0F, 0.0F, ageInTicks, animationIntensity1);
            this.walk(this.body, 0.2F, 0.1F, true, 1.0F, 0.0F, ageInTicks, animationIntensity1);
            this.bob(this.body, 0.4F, 2.0F, false, ageInTicks, animationIntensity1);
            AdvancedModelBox arm = this.getArmCube(entity);
            arm.rotationPointZ = (float) (arm.rotationPointZ + (double) animationIntensity2 * Math.sin(ageInTicks * 2.0F));
            this.walk(arm, 1.0F, 0.1F, false, 0.0F, -0.1F, ageInTicks, animationIntensity2);
        }
    }

    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.main.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.arms.translateAndRotate(poseStack);
        if (humanoidArm == HumanoidArm.RIGHT) {
            this.right_Arm.translateAndRotate(poseStack);
            this.right_Hand.translateAndRotate(poseStack);
        } else {
            this.left_Arm.translateAndRotate(poseStack);
            this.left_Hand.translateAndRotate(poseStack);
        }
    }

    public void translateToCrossedArms(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.main.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.arms.translateAndRotate(poseStack);
        this.armsCrossed.translateAndRotate(poseStack);
    }

    private AdvancedModelBox getArmCube(LivingEntity living) {
        return living.getMainArm() == HumanoidArm.LEFT ? this.left_Arm : this.right_Arm;
    }
}
