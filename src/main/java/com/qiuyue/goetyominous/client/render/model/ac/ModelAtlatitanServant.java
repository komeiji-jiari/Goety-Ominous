package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.entity.util.LuxtructosaurusLegSolver;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelAtlatitanServant extends AdvancedEntityModel<AtlatitanServant> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox hips;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail3;
    private final AdvancedModelBox left_Leg;
    private final AdvancedModelBox left_Foot;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox right_Foot;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox right_Hand;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox left_Hand;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox neck2;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox dewlap;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;
    private final AdvancedModelBox cube_r9;
    private final AdvancedModelBox cube_r10;
    private final AdvancedModelBox cube_r11;
    private final AdvancedModelBox cube_r12;
    private final ModelAnimator animator;

    public ModelAtlatitanServant() {
        this.texWidth = 512;
        this.texHeight = 512;
        this.root = new AdvancedModelBox(this);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.root.addChild(this.body);
        this.hips = new AdvancedModelBox(this);
        this.hips.setRotationPoint(0.0F, -65.0F, 0.5F);
        this.body.addChild(this.hips);
        this.hips.setTextureOffset(230, 149).addBox(-19.0F, -24.0F, -3.5F, 38.0F, 48.0F, 41.0F, 0.0F, false);
        this.tail = new AdvancedModelBox(this);
        this.tail.setRotationPoint(0.0F, 6.5F, 33.0F);
        this.hips.addChild(this.tail);
        this.tail.setTextureOffset(0, 246).addBox(-12.0F, -14.5F, 2.5F, 24.0F, 29.0F, 49.0F, 0.0F, false);
        this.tail2 = new AdvancedModelBox(this);
        this.tail2.setRotationPoint(0.0F, 1.5F, 49.0F);
        this.tail.addChild(this.tail2);
        this.tail2.setTextureOffset(245, 238).addBox(-8.0F, -10.0F, -6.5F, 16.0F, 20.0F, 57.0F, 0.0F, false);
        this.tail3 = new AdvancedModelBox(this);
        this.tail3.setRotationPoint(0.0F, 0.5F, 48.5F);
        this.tail2.addChild(this.tail3);
        this.tail3.setTextureOffset(138, 174).addBox(-5.0F, -6.5F, -14.0F, 10.0F, 13.0F, 72.0F, 0.0F, false);
        this.left_Leg = new AdvancedModelBox(this);
        this.left_Leg.setRotationPoint(18.0F, 12.0F, 22.5F);
        this.hips.addChild(this.left_Leg);
        this.left_Leg.setTextureOffset(139, 0).addBox(-9.5F, -7.0F, -15.0F, 19.0F, 35.0F, 27.0F, 0.0F, false);
        this.left_Foot = new AdvancedModelBox(this);
        this.left_Foot.setRotationPoint(0.0F, 23.0F, 5.0F);
        this.left_Leg.addChild(this.left_Foot);
        this.left_Foot.setTextureOffset(270, 315).addBox(-6.5F, -5.0F, -5.0F, 13.0F, 35.0F, 17.0F, 0.0F, false);
        this.left_Foot.setTextureOffset(153, 149).addBox(-6.5F, 26.0F, -9.0F, 13.0F, 4.0F, 4.0F, 0.25F, false);
        this.left_Foot.setTextureOffset(153, 157).addBox(-6.5F, 26.0F, -9.0F, 13.0F, 4.0F, 4.0F, 0.0F, false);
        this.right_Leg = new AdvancedModelBox(this);
        this.right_Leg.setRotationPoint(-18.0F, 12.0F, 22.5F);
        this.hips.addChild(this.right_Leg);
        this.right_Leg.setTextureOffset(139, 0).addBox(-9.5F, -7.0F, -15.0F, 19.0F, 35.0F, 27.0F, 0.0F, true);
        this.right_Foot = new AdvancedModelBox(this);
        this.right_Foot.setRotationPoint(0.0F, 23.0F, 5.0F);
        this.right_Leg.addChild(this.right_Foot);
        this.right_Foot.setTextureOffset(270, 315).addBox(-6.5F, -5.0F, -5.0F, 13.0F, 35.0F, 17.0F, 0.0F, true);
        this.right_Foot.setTextureOffset(153, 149).addBox(-6.5F, 26.0F, -9.0F, 13.0F, 4.0F, 4.0F, 0.25F, true);
        this.right_Foot.setTextureOffset(153, 157).addBox(-6.5F, 26.0F, -9.0F, 13.0F, 4.0F, 4.0F, 0.0F, true);
        this.chest = new AdvancedModelBox(this);
        this.chest.setRotationPoint(0.0F, -9.0F, 0.0F);
        this.hips.addChild(this.chest);
        this.chest.setTextureOffset(0, 123).addBox(-24.0F, -33.0F, -56.5F, 48.0F, 66.0F, 57.0F, 0.01F, false);
        this.right_Arm = new AdvancedModelBox(this);
        this.right_Arm.setRotationPoint(-23.0F, -3.0F, -37.5F);
        this.chest.addChild(this.right_Arm);
        this.right_Arm.setTextureOffset(0, 0).addBox(-13.0F, -10.0F, -11.0F, 14.0F, 44.0F, 21.0F, 0.0F, true);
        this.right_Hand = new AdvancedModelBox(this);
        this.right_Hand.setRotationPoint(-3.0F, 32.0F, -9.0F);
        this.right_Arm.addChild(this.right_Hand);
        this.right_Hand.setTextureOffset(264, 0).addBox(-15.0F, -2.0F, -2.75F, 24.0F, 47.0F, 29.0F, 0.0F, true);
        this.right_Hand.setTextureOffset(20, 238).addBox(-8.0F, -2.0F, -12.75F, 0.0F, 47.0F, 10.0F, 0.0F, true);
        this.right_Hand.setTextureOffset(20, 238).addBox(2.0F, -2.0F, -12.75F, 0.0F, 47.0F, 10.0F, 0.0F, true);
        this.right_Hand.setTextureOffset(49, 0).addBox(9.0F, 37.0F, 13.25F, 8.0F, 8.0F, 8.0F, 0.0F, true);
        this.left_Arm = new AdvancedModelBox(this);
        this.left_Arm.setRotationPoint(23.0F, -3.0F, -37.5F);
        this.chest.addChild(this.left_Arm);
        this.left_Arm.setTextureOffset(0, 0).addBox(-1.0F, -10.0F, -11.0F, 14.0F, 44.0F, 21.0F, 0.0F, false);
        this.left_Hand = new AdvancedModelBox(this);
        this.left_Hand.setRotationPoint(3.0F, 32.0F, -9.0F);
        this.left_Arm.addChild(this.left_Hand);
        this.left_Hand.setTextureOffset(264, 0).addBox(-9.0F, -2.0F, -2.75F, 24.0F, 47.0F, 29.0F, 0.0F, false);
        this.left_Hand.setTextureOffset(20, 238).addBox(8.0F, -2.0F, -12.75F, 0.0F, 47.0F, 10.0F, 0.0F, false);
        this.left_Hand.setTextureOffset(20, 238).addBox(-2.0F, -2.0F, -12.75F, 0.0F, 47.0F, 10.0F, 0.0F, false);
        this.left_Hand.setTextureOffset(49, 0).addBox(-17.0F, 37.0F, 13.25F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        this.neck = new AdvancedModelBox(this);
        this.neck.setRotationPoint(0.5F, -31.0F, -33.0F);
        this.chest.addChild(this.neck);
        this.neck.setTextureOffset(0, 0).addBox(-13.5F, -14.0F, -76.5F, 26.0F, 36.0F, 87.0F, 0.0F, false);
        this.neck2 = new AdvancedModelBox(this);
        this.neck2.setRotationPoint(-0.5F, -6.0F, -75.5F);
        this.neck.addChild(this.neck2);
        this.neck2.setTextureOffset(153, 44).addBox(-8.0F, -2.0F, -80.0F, 16.0F, 26.0F, 79.0F, 0.0F, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.8F, 8.0F, -75.0F);
        this.neck2.addChild(this.head);
        this.head.setTextureOffset(198, 315).addBox(-9.8F, -4.0F, -15.0F, 18.0F, 22.0F, 18.0F, 0.0F, false);
        this.head.setTextureOffset(0, 324).addBox(-5.8F, -13.0F, -23.0F, 10.0F, 20.0F, 20.0F, 0.0F, false);
        this.head.setTextureOffset(264, 76).addBox(-11.8F, 6.0F, -28.0F, 22.0F, 7.0F, 21.0F, 0.0F, false);
        this.head.setTextureOffset(0, 65).addBox(-11.3F, 11.0F, -27.5F, 21.0F, 6.0F, 13.0F, 0.0F, false);
        this.jaw = new AdvancedModelBox(this);
        this.jaw.setRotationPoint(-0.8F, 10.5F, -8.5F);
        this.head.addChild(this.jaw);
        this.jaw.setTextureOffset(331, 83).addBox(-11.0F, -1.5F, -19.5F, 22.0F, 9.0F, 21.0F, -0.01F, false);
        this.jaw.setTextureOffset(360, 0).addBox(-11.0F, 3.0F, -19.5F, 22.0F, 2.0F, 17.0F, -0.001F, false);
        this.dewlap = new AdvancedModelBox(this);
        this.dewlap.setRotationPoint(0.0F, 24.0F, -57.5F);
        this.neck2.addChild(this.dewlap);
        this.dewlap.setTextureOffset(97, 194).addBox(0.0F, -4.0F, -32.5F, 0.0F, 26.0F, 65.0F, 0.0F, false);
        this.cube_r1 = new AdvancedModelBox(this);
        this.cube_r1.setRotationPoint(-24.0F, -33.0F, -39.0F);
        this.chest.addChild(this.cube_r1);
        this.setRotateAngle(this.cube_r1, 0.0F, 0.0F, -0.7854F);
        this.cube_r1.setTextureOffset(0, 123).addBox(-6.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, true);
        this.cube_r2 = new AdvancedModelBox(this);
        this.cube_r2.setRotationPoint(-24.0F, -33.0F, -2.0F);
        this.chest.addChild(this.cube_r2);
        this.setRotateAngle(this.cube_r2, -0.7854F, 0.0F, -0.7854F);
        this.cube_r2.setTextureOffset(0, 123).addBox(-6.0F, -15.0F, 0.5F, 11.0F, 38.0F, 11.0F, 0.0F, true);
        this.cube_r3 = new AdvancedModelBox(this);
        this.cube_r3.setRotationPoint(24.0F, -33.0F, -2.0F);
        this.chest.addChild(this.cube_r3);
        this.setRotateAngle(this.cube_r3, -0.7854F, 0.0F, 0.7854F);
        this.cube_r3.setTextureOffset(0, 123).addBox(-5.0F, -15.0F, 0.5F, 11.0F, 38.0F, 11.0F, 0.0F, false);
        this.cube_r4 = new AdvancedModelBox(this);
        this.cube_r4.setRotationPoint(24.0F, -33.0F, -39.0F);
        this.chest.addChild(this.cube_r4);
        this.setRotateAngle(this.cube_r4, 0.0F, 0.0F, 0.7854F);
        this.cube_r4.setTextureOffset(0, 123).addBox(-5.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, false);
        this.cube_r5 = new AdvancedModelBox(this);
        this.cube_r5.setRotationPoint(-24.0F, -33.0F, -39.0F);
        this.chest.addChild(this.cube_r5);
        this.setRotateAngle(this.cube_r5, 0.3927F, 0.0F, -0.7854F);
        this.cube_r5.setTextureOffset(146, 285).addBox(-7.0F, -14.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, true);
        this.cube_r6 = new AdvancedModelBox(this);
        this.cube_r6.setRotationPoint(24.0F, -33.0F, -39.0F);
        this.chest.addChild(this.cube_r6);
        this.setRotateAngle(this.cube_r6, 0.3927F, 0.0F, 0.7854F);
        this.cube_r6.setTextureOffset(146, 285).addBox(-6.0F, -14.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, false);
        this.cube_r7 = new AdvancedModelBox(this);
        this.cube_r7.setRotationPoint(8.9991F, 25.5F, -2.7496F);
        this.right_Hand.addChild(this.cube_r7);
        this.setRotateAngle(this.cube_r7, 0.0F, -0.3927F, 0.0F);
        this.cube_r7.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);
        this.cube_r8 = new AdvancedModelBox(this);
        this.cube_r8.setRotationPoint(-15.0F, 25.5F, -2.75F);
        this.right_Hand.addChild(this.cube_r8);
        this.setRotateAngle(this.cube_r8, 0.0F, 0.3927F, 0.0F);
        this.cube_r8.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);
        this.cube_r9 = new AdvancedModelBox(this);
        this.cube_r9.setRotationPoint(-8.9991F, 25.5F, -2.7496F);
        this.left_Hand.addChild(this.cube_r9);
        this.setRotateAngle(this.cube_r9, 0.0F, 0.3927F, 0.0F);
        this.cube_r9.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);
        this.cube_r10 = new AdvancedModelBox(this);
        this.cube_r10.setRotationPoint(15.0F, 25.5F, -2.75F);
        this.left_Hand.addChild(this.cube_r10);
        this.setRotateAngle(this.cube_r10, 0.0F, -0.3927F, 0.0F);
        this.cube_r10.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);
        this.cube_r11 = new AdvancedModelBox(this);
        this.cube_r11.setRotationPoint(-7.0F, -2.0F, -69.0F);
        this.neck2.addChild(this.cube_r11);
        this.setRotateAngle(this.cube_r11, 0.0F, 0.0F, -0.7854F);
        this.cube_r11.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
        this.cube_r11.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 36.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
        this.cube_r11.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 1.0F, 8.0F, 17.0F, 8.0F, 0.0F, true);
        this.cube_r11.setTextureOffset(139, 62).addBox(-4.0F, -8.0F, 51.0F, 8.0F, 17.0F, 8.0F, 0.0F, true);
        this.cube_r12 = new AdvancedModelBox(this);
        this.cube_r12.setRotationPoint(7.0F, -2.0F, -69.0F);
        this.neck2.addChild(this.cube_r12);
        this.setRotateAngle(this.cube_r12, 0.0F, 0.0F, 0.7854F);
        this.cube_r12.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 36.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);
        this.cube_r12.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);
        this.cube_r12.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 2.0F, 8.0F, 17.0F, 8.0F, 0.0F, false);
        this.cube_r12.setTextureOffset(139, 62).addBox(-4.0F, -10.0F, 51.0F, 8.0F, 17.0F, 8.0F, 0.0F, false);
        this.left_Foot.setShouldScaleChildren(true);
        this.right_Foot.setShouldScaleChildren(true);
        this.left_Hand.setShouldScaleChildren(true);
        this.right_Hand.setShouldScaleChildren(true);
        this.jaw.setScale(0.99F, 0.99F, 0.99F);
        this.animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    public void animate(AtlatitanServant entity) {
        this.animator.update(entity);
        this.animator.setAnimation(AtlatitanServant.ANIMATION_SPEAK);
        this.animator.startKeyframe(3);
        this.animator.rotate(this.head, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(7);
        this.animator.rotate(this.head, (float) Math.toRadians(-10.0), 0.0F, 0.0F);
        this.animator.rotate(this.jaw, (float) Math.toRadians(40.0), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(AtlatitanServant.ANIMATION_STOMP);
        this.animator.startKeyframe(20);
        this.animator.move(this.body, 0.0F, -25.0F, -23.0F);
        this.animator.move(this.chest, 0.0F, 2.0F, 4.0F);
        this.animator.move(this.head, 0.0F, 5.0F, -5.0F);
        this.animator.move(this.left_Leg, 0.0F, 0.0F, -5.0F);
        this.animator.move(this.right_Leg, 0.0F, 0.0F, -5.0F);
        this.animator.move(this.left_Arm, 0.0F, 10.0F, -5.0F);
        this.animator.move(this.right_Arm, 0.0F, 10.0F, -5.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(-40.0), 0.0F, 0.0F);
        this.animator.rotate(this.neck, (float) Math.toRadians(50.0), 0.0F, 0.0F);
        this.animator.rotate(this.neck2, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.tail, (float) Math.toRadians(20.0), 0.0F, 0.0F);
        this.animator.rotate(this.tail3, (float) Math.toRadians(20.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(30.0), 0.0F, 0.0F);
        this.animator.rotate(this.chest, (float) Math.toRadians(-20.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(40.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(40.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(-20.0), (float) Math.toRadians(-20.0));
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-20.0), (float) Math.toRadians(20.0), (float) Math.toRadians(20.0));
        this.animator.rotate(this.left_Hand, (float) Math.toRadians(50.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Hand, (float) Math.toRadians(50.0), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 10.0F, 0.0F);
        this.animator.move(this.right_Leg, 0.0F, -10.0F, 0.0F);
        this.animator.move(this.left_Leg, 0.0F, -10.0F, 0.0F);
        this.animator.move(this.right_Arm, -2.0F, -10.0F, -7.0F);
        this.animator.move(this.left_Arm, 2.0F, -10.0F, -7.0F);
        this.animator.rotate(this.left_Arm, 0.0F, 0.0F, (float) Math.toRadians(-20.0));
        this.animator.rotate(this.right_Arm, 0.0F, 0.0F, (float) Math.toRadians(20.0));
        this.animator.rotate(this.left_Hand, 0.0F, 0.0F, (float) Math.toRadians(20.0));
        this.animator.rotate(this.right_Hand, 0.0F, 0.0F, (float) Math.toRadians(-20.0));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(10);
        this.animator.resetKeyframe(10);
        this.animator.setAnimation(AtlatitanServant.ANIMATION_LEFT_KICK);
        this.animator.startKeyframe(4);
        this.animator.move(this.left_Arm, 3.0F, 3.0F, -3.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-30.0), (float) Math.toRadians(-40.0), 0.0F);
        this.animator.rotate(this.left_Hand, (float) Math.toRadians(40.0), 0.0F, (float) Math.toRadians(10.0));
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.tail, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.neck, 0.0F, (float) Math.toRadians(-5.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(6);
        this.animator.move(this.left_Arm, 0.0F, -5.0F, -3.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-80.0), (float) Math.toRadians(0.0), 0.0F);
        this.animator.rotate(this.left_Hand, (float) Math.toRadians(10.0), 0.0F, (float) Math.toRadians(10.0));
        this.animator.endKeyframe();
        this.animator.resetKeyframe(10);
        this.animator.setAnimation(AtlatitanServant.ANIMATION_RIGHT_KICK);
        this.animator.startKeyframe(5);
        this.animator.move(this.right_Arm, -3.0F, 3.0F, -3.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-30.0), (float) Math.toRadians(40.0), 0.0F);
        this.animator.rotate(this.right_Hand, (float) Math.toRadians(40.0), 0.0F, (float) Math.toRadians(-10.0));
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.tail, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.neck, 0.0F, (float) Math.toRadians(5.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(4);
        this.animator.move(this.right_Arm, 0.0F, -5.0F, -3.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-80.0), (float) Math.toRadians(0.0), 0.0F);
        this.animator.rotate(this.right_Hand, (float) Math.toRadians(10.0), 0.0F, (float) Math.toRadians(-10.0));
        this.animator.endKeyframe();
        this.animator.resetKeyframe(6);
        this.animator.setAnimation(AtlatitanServant.ANIMATION_EAT_LEAVES);
        this.animator.startKeyframe(15);
        this.animator.rotate(this.neck, (float) Math.toRadians(-20.0), 0.0F, 0.0F);
        this.animator.rotate(this.neck2, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.jaw, (float) Math.toRadians(40.0), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(10);
        this.animator.rotate(this.head, (float) Math.toRadians(20.0), 0.0F, 0.0F);
        this.animator.rotate(this.jaw, (float) Math.toRadians(-10.0), 0.0F, 0.0F);
        this.animator.move(this.jaw, 0.0F, 0.0F, 1.0F);
        this.animator.endKeyframe();
        this.animator.resetKeyframe(10);
    }

    public void setupAnim(AtlatitanServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity);
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            this.setupAnimForAnimation(entity, entity.getAnimation(), ageInTicks);
        }
        float partialTicks = ageInTicks - (float) entity.tickCount;
        float idleSpeed = 0.05F;
        float walkSpeed = 0.05F;
        float walkDegree = 3.0F;
        float walk = entity.getWalkAnimPosition(partialTicks);
        float armsWalkAmount = Math.min(entity.getWalkAnimSpeed(partialTicks), 1.0F);
        float walkAmount = armsWalkAmount;
        float raiseArmsAmount = entity.getRaiseArmsAmount(partialTicks);
        float legBack = entity.getLegBackAmount(partialTicks);
        this.positionNeckAndTail(entity, netHeadYaw, headPitch, partialTicks);
        this.articulateLegs(entity.legSolver, raiseArmsAmount, partialTicks);
        this.walk(this.neck, idleSpeed, 0.03F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(this.neck2, idleSpeed, 0.02F, true, -1.0F, -0.03F, ageInTicks, 1.0F);
        this.walk(this.head, idleSpeed, 0.01F, true, -2.0F, 0.02F, ageInTicks, 1.0F);
        this.flap(this.dewlap, 0.14F, 0.1F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(this.dewlap, 0.14F, 0.05F, true, 1.0F, -0.1F, ageInTicks, 1.0F);
        this.walk(this.tail, idleSpeed, 0.03F, true, 3.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(this.tail2, idleSpeed, 0.03F, true, 2.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(this.tail3, idleSpeed, 0.03F, true, 2.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.tail, idleSpeed, 0.03F, true, 4.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.tail2, idleSpeed, 0.03F, true, 3.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.tail3, idleSpeed, 0.03F, true, 2.0F, 0.0F, ageInTicks, 1.0F);
        this.dewlap.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F, -1.5F, 1.0F, false);
        float legAnimSeperation = 0.5F;
        this.animateLegWalking(this.right_Arm, this.right_Hand, legAnimSeperation * 3.0F, walkSpeed, walkDegree, walk, armsWalkAmount, true, false, legBack);
        this.animateLegWalking(this.right_Leg, this.right_Foot, legAnimSeperation * 2.0F, walkSpeed, walkDegree, walk, walkAmount, false, false, legBack);
        this.animateLegWalking(this.left_Arm, this.left_Hand, legAnimSeperation, walkSpeed, walkDegree, walk, armsWalkAmount, true, true, legBack);
        this.animateLegWalking(this.left_Leg, this.left_Foot, 0.0F, walkSpeed, walkDegree, walk, walkAmount, false, true, legBack);
    }

    private void setupAnimForAnimation(AtlatitanServant entity, Animation animation, float ageInTicks) {
        float partialTick = ageInTicks - (float) entity.tickCount;
        if (entity.getAnimation() == AtlatitanServant.ANIMATION_EAT_LEAVES) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3.0F, animation, partialTick, 35);
            this.jaw.walk(0.5F, 0.1F, false, 1.0F, 0.1F, ageInTicks, animationIntensity);
            this.head.rotateAngleX += ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 2.0F, 0.05F, false);
            this.jaw.rotationPointZ += animationIntensity * 2.0F + ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 0.5F, 1.0F, false);
        }
    }

    private void positionNeckAndTail(AtlatitanServant entity, float netHeadYaw, float headPitch, float partialTicks) {
        if (!entity.isFakeEntity()) {
            float neckPart1Pitch = (float) Math.toRadians(entity.neckPart1.calculateAnimationAngle(partialTicks, true)) * 0.5F;
            float neckPart2Pitch = (float) Math.toRadians(entity.neckPart2.calculateAnimationAngle(partialTicks, true)) * 0.5F;
            float neckPart3Pitch = (float) Math.toRadians(entity.neckPart3.calculateAnimationAngle(partialTicks, true)) * 0.5F;
            float tailPart1Pitch = (float) Math.toRadians(entity.tailPart1.calculateAnimationAngle(partialTicks, true)) + 0.141F;
            float tailPart2Pitch = (float) Math.toRadians(entity.tailPart2.calculateAnimationAngle(partialTicks, true)) + 0.076F;
            float tailPart3Pitch = (float) Math.toRadians(entity.tailPart3.calculateAnimationAngle(partialTicks, true)) * 0.5F;
            float neckPart2Yaw = entity.neckPart2.calculateAnimationAngle(partialTicks, false);
            float pitchAmount = Mth.clamp(headPitch, -30.0F, 30.0F) / 57.295776F;
            float headApproach = Mth.approachDegrees(neckPart2Yaw, entity.headPart.calculateAnimationAngle(partialTicks, false), 45.0F) - neckPart2Yaw;
            this.neck.rotateAngleX -= neckPart1Pitch + neckPart2Pitch;
            this.neck.rotateAngleY = (float) ((double) this.neck.rotateAngleY + (Math.toRadians(180.0F + entity.neckPart1.calculateAnimationAngle(partialTicks, false)) - (double) this.chest.rotateAngleY - (double) this.body.rotateAngleY - (double) this.root.rotateAngleY));
            this.neck2.rotateAngleX -= neckPart2Pitch;
            this.neck2.rotateAngleY = (float) ((double) this.neck2.rotateAngleY + Math.toRadians(180.0F + neckPart2Yaw));
            this.head.rotateAngleX += pitchAmount + neckPart1Pitch + neckPart2Pitch + neckPart3Pitch - (float) Math.toRadians(entity.headPart.calculateAnimationAngle(partialTicks, true)) * 0.2F;
            this.head.rotateAngleY = (float) ((double) this.head.rotateAngleY + Math.toRadians(headApproach));
            if (neckPart2Pitch > 0.0F) {
                this.neck2.rotationPointZ += Math.min(neckPart2Pitch * 50.0F, 50.0F);
            }
            this.tail.rotateAngleY = (float) ((double) this.tail.rotateAngleY + Math.toRadians(entity.tailPart1.calculateAnimationAngle(partialTicks, false)));
            this.tail2.rotateAngleY = (float) ((double) this.tail2.rotateAngleY + Math.toRadians(entity.tailPart2.calculateAnimationAngle(partialTicks, false)));
            this.tail3.rotateAngleY = (float) ((double) this.tail3.rotateAngleY + Math.toRadians(entity.tailPart3.calculateAnimationAngle(partialTicks, false) - entity.tailPart2.calculateAnimationAngle(partialTicks, false)));
            this.tail.rotateAngleX += tailPart1Pitch;
            this.tail2.rotateAngleX += tailPart2Pitch;
            this.tail3.rotateAngleX += tailPart3Pitch;
        }
    }

    private void animateLegWalking(AdvancedModelBox leg, AdvancedModelBox foot, float offset, float speed, float degree, float limbSwing, float limbSwingAmount, boolean front, boolean left, float legBack) {
        float leg1 = Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, speed, (float) Math.PI * (offset + 0.3333F), 1.0F, true) + 0.75F) * 4.0F;
        float leg1Delayed = Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, speed, (float) Math.PI * offset, 1.0F, true) + 0.75F) * 4.0F;
        float leg1Prev = Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, speed, (float) Math.PI * (offset + 0.6666F), 1.0F, true) + 0.75F) * 4.0F;
        float leg1Squish = 1.0F - 0.15F * (float) Math.pow(Math.min(leg1Delayed - leg1, 0.0F), 3.0);
        float legInactivityAmount = 1.0F - Math.abs(leg1);
        this.walk(leg, speed, degree * 0.3F, false, (float) Math.PI * offset + 1.0F, 0.0F, limbSwing, leg1);
        this.walk(foot, speed, degree * 0.2F, false, (float) Math.PI * offset - 2.0F, -0.25F, limbSwing, leg1);
        if (front) {
            this.swing(leg, speed, degree * -0.2F, left, (float) Math.PI * offset + 1.0F, 0.0F, limbSwing, leg1Prev);
            leg.rotationPointZ += leg1Prev * 8.0F;
        }
        leg.rotationPointY += leg1 * 10.0F;
        leg.rotationPointZ += leg1Delayed * 16.0F;
        leg.rotationPointZ += legBack * legInactivityAmount * 16.0F;
        float raisedBody = leg1 * 8.0F;
        this.body.rotationPointY += raisedBody;
        this.left_Leg.rotationPointY -= raisedBody;
        this.right_Leg.rotationPointY -= raisedBody;
        this.left_Arm.rotationPointY -= raisedBody;
        this.right_Arm.rotationPointY -= raisedBody;
        this.tail.rotationPointY -= raisedBody * 0.5F;
        this.neck.rotationPointY -= raisedBody * 0.5F;
        float squish2 = 2.0F - leg1Squish;
        foot.setScale(leg1Squish * leg1Squish, squish2, leg1Squish);
        leg.rotationPointY -= (squish2 - 1.0F) * 30.0F;
    }

    private void articulateLegs(LuxtructosaurusLegSolver legs, float raiseArmsAmount, float partialTick) {
        float armsArticulateAmount = 1.0F - raiseArmsAmount;
        float heightBackLeft = legs.backLeft.getHeight(partialTick);
        float heightBackRight = legs.backRight.getHeight(partialTick);
        float heightFrontLeft = legs.frontLeft.getHeight(partialTick);
        float heightFrontRight = legs.frontRight.getHeight(partialTick);
        float max = Math.max(Math.max(heightBackLeft, heightBackRight), armsArticulateAmount * Math.max(heightFrontLeft, heightFrontRight)) * 0.75F;
        this.body.rotationPointY += max * 16.0F;
        this.right_Arm.rotationPointY += (heightFrontRight - max) * armsArticulateAmount * 16.0F;
        this.left_Arm.rotationPointY += (heightFrontLeft - max) * armsArticulateAmount * 16.0F;
        this.right_Leg.rotationPointY += (heightBackRight - max) * 16.0F;
        this.left_Leg.rotationPointY += (heightBackLeft - max) * 16.0F;
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 2.0F;
            this.head.setScale(f, f, f);
            this.head.setShouldScaleChildren(true);
            this.head.setRotationPoint(0.8F, 3.0F, -75.0F);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.15F, 0.15F, 0.15F);
            matrixStackIn.translate(0.0, 8.55F, 0.0);
            this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.popPose();
            this.head.setRotationPoint(0.8F, 8.0F, -75.0F);
            this.head.setScale(1.0F, 1.0F, 1.0F);
        } else {
            matrixStackIn.pushPose();
            this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.popPose();
        }
    }

    public Vec3 getRiderPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        this.root.translateAndRotate(translationStack);
        this.body.translateAndRotate(translationStack);
        this.chest.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3;
    }

    public Vec3 getMouthPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        this.root.translateAndRotate(translationStack);
        this.body.translateAndRotate(translationStack);
        this.chest.translateAndRotate(translationStack);
        this.neck.translateAndRotate(translationStack);
        this.neck2.translateAndRotate(translationStack);
        this.head.translateAndRotate(translationStack);
        this.jaw.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(-armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3.add(0.0, 5.0, -1.0);
    }

    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.root);
    }

    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.root, this.body, this.chest, this.hips, this.tail, this.tail2, this.tail3,
                this.left_Leg, this.left_Foot, this.right_Leg, this.right_Foot, this.left_Arm, this.left_Hand,
                this.right_Arm, this.right_Hand, this.neck, this.neck2, this.head, this.jaw, this.dewlap,
                this.cube_r1, this.cube_r2, this.cube_r3, this.cube_r4, this.cube_r5, this.cube_r6,
                this.cube_r7, this.cube_r8, this.cube_r9, this.cube_r10, this.cube_r11, this.cube_r12);
    }
}
