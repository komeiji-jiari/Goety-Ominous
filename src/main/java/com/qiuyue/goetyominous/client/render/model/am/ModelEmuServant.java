package com.qiuyue.goetyominous.client.render.model.am;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.am.EmuServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelEmuServant extends AdvancedEntityModel<EmuServant> {
   private final AdvancedModelBox root;
   private final AdvancedModelBox body;
   private final AdvancedModelBox leg_left;
   private final AdvancedModelBox legfur_left;
   private final AdvancedModelBox foot_left;
   private final AdvancedModelBox leg_right;
   private final AdvancedModelBox legfur_right;
   private final AdvancedModelBox foot_right;
   private final AdvancedModelBox neck1;
   private final AdvancedModelBox neck2;
   private final AdvancedModelBox headPivot;
   private final AdvancedModelBox head;
   private final AdvancedModelBox beak;
   private final AdvancedModelBox tail;
   private ModelAnimator animator;

   public ModelEmuServant() {
      this.texWidth = 128;
      this.texHeight = 128;
      this.root = new AdvancedModelBox(this, "root");
      this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.body = new AdvancedModelBox(this, "body");
      this.body.setRotationPoint(0.0F, -19.625F, -0.125F);
      this.root.addChild(this.body);
      this.body.setTextureOffset(0, 0).addBox(-6.0F, -4.375F, -10.875F, 12.0F, 11.0F, 21.0F, 0.0F, false);
      this.leg_left = new AdvancedModelBox(this, "leg_left");
      this.leg_left.setRotationPoint(3.0F, 6.625F, 0.125F);
      this.body.addChild(this.leg_left);
      this.leg_left.setTextureOffset(0, 55).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);
      this.legfur_left = new AdvancedModelBox(this, "legfur_left");
      this.legfur_left.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.leg_left.addChild(this.legfur_left);
      this.legfur_left.setTextureOffset(31, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, false);
      this.foot_left = new AdvancedModelBox(this, "foot_left");
      this.foot_left.setRotationPoint(0.0F, 11.0F, -1.0F);
      this.leg_left.addChild(this.foot_left);
      this.foot_left.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, 0.0F, false);
      this.leg_right = new AdvancedModelBox(this, "leg_right");
      this.leg_right.setRotationPoint(-3.0F, 6.625F, 0.125F);
      this.body.addChild(this.leg_right);
      this.leg_right.setTextureOffset(0, 55).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, true);
      this.legfur_right = new AdvancedModelBox(this, "legfur_right");
      this.legfur_right.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.leg_right.addChild(this.legfur_right);
      this.legfur_right.setTextureOffset(31, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, true);
      this.foot_right = new AdvancedModelBox(this, "foot_right");
      this.foot_right.setRotationPoint(0.0F, 11.0F, -1.0F);
      this.leg_right.addChild(this.foot_right);
      this.foot_right.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, 0.0F, true);
      this.neck1 = new AdvancedModelBox(this, "neck1");
      this.neck1.setRotationPoint(0.0F, 0.625F, -9.5F);
      this.body.addChild(this.neck1);
      this.neck1.setTextureOffset(41, 41).addBox(-3.0F, -9.0F, -6.0F, 6.0F, 12.0F, 6.0F, 0.0F, false);
      this.neck2 = new AdvancedModelBox(this, "neck2");
      this.neck2.setRotationPoint(0.0F, -8.5F, -2.0F);
      this.neck1.addChild(this.neck2);
      this.neck2.setTextureOffset(46, 0).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
      this.headPivot = new AdvancedModelBox(this, "headPivot");
      this.headPivot.setRotationPoint(-0.5F, -6.5F, 0.0F);
      this.neck2.addChild(this.headPivot);
      this.head = new AdvancedModelBox(this, "head");
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.headPivot.addChild(this.head);
      this.head.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -3.0F, 5.0F, 4.0F, 5.0F, 0.0F, false);
      this.head.setTextureOffset(71, 54).addBox(0.5F, -6.0F, -3.0F, 0.0F, 6.0F, 7.0F, 0.0F, false);
      this.beak = new AdvancedModelBox(this, "beak");
      this.beak.setRotationPoint(0.5F, -1.0F, -3.0F);
      this.head.addChild(this.beak);
      this.beak.setTextureOffset(46, 12).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 3.0F, 0.0F, false);
      this.tail = new AdvancedModelBox(this, "tail");
      this.tail.setRotationPoint(0.0F, -0.875F, 9.125F);
      this.body.addChild(this.tail);
      this.tail.setTextureOffset(0, 33).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 11.0F, 10.0F, 0.0F, false);
      this.updateDefaultPose();
      this.animator = ModelAnimator.create();
   }

   @Override
   public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (this.young) {
         float f = 1.5F;
         this.head.setScale(f, f, f);
         this.head.setShouldScaleChildren(true);
         matrixStackIn.pushPose();
         matrixStackIn.scale(0.35F, 0.35F, 0.35F);
         matrixStackIn.translate(0.0D, 2.8D, 0.0D);
         this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
         matrixStackIn.popPose();
         this.head.setScale(1.0F, 1.0F, 1.0F);
      } else {
         matrixStackIn.pushPose();
         this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
         matrixStackIn.popPose();
      }
   }

   @Override
   public Iterable<AdvancedModelBox> getAllParts() {
      return ImmutableList.of(this.root, this.body, this.leg_left, this.leg_right, this.legfur_left, this.legfur_right,
              this.tail, this.neck1, this.neck2, this.headPivot, this.head, this.beak, this.foot_left, this.foot_right);
   }

   @Override
   public Iterable<BasicModelPart> parts() {
      return ImmutableList.of(this.root);
   }

   public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
      this.resetToDefaultPose();
      this.animator.update(entity);
      this.animator.setAnimation(EmuServant.ANIMATION_DODGE_RIGHT);
      this.animator.startKeyframe(4);
      this.animator.move(this.body, 0.0F, -5.0F, 0.0F);
      this.animator.rotate(this.leg_left, 0.0F, 0.0F, Maths.rad(-60.0D));
      this.animator.rotate(this.leg_right, 0.0F, 0.0F, Maths.rad(-45.0D));
      this.animator.rotate(this.body, 0.0F, 0.0F, Maths.rad(25.0D));
      this.animator.rotate(this.neck1, Maths.rad(-15.0D), 0.0F, Maths.rad(20.0D));
      this.animator.rotate(this.neck2, Maths.rad(-15.0D), 0.0F, Maths.rad(10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-45.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_left, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_right, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(1);
      this.animator.resetKeyframe(5);
      this.animator.setAnimation(EmuServant.ANIMATION_DODGE_LEFT);
      this.animator.startKeyframe(4);
      this.animator.move(this.body, 0.0F, -5.0F, 0.0F);
      this.animator.rotate(this.leg_left, 0.0F, 0.0F, Maths.rad(60.0D));
      this.animator.rotate(this.leg_right, 0.0F, 0.0F, Maths.rad(45.0D));
      this.animator.rotate(this.body, 0.0F, 0.0F, Maths.rad(-25.0D));
      this.animator.rotate(this.neck1, Maths.rad(-15.0D), 0.0F, Maths.rad(-20.0D));
      this.animator.rotate(this.neck2, Maths.rad(-15.0D), 0.0F, Maths.rad(-10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-45.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_left, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_right, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(1);
      this.animator.resetKeyframe(5);
      this.animator.setAnimation(EmuServant.ANIMATION_PUZZLED);
      this.animator.startKeyframe(3);
      this.animator.rotate(this.body, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, Maths.rad(-10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-10.0D), Maths.rad(-10.0D), Maths.rad(-35.0D));
      this.animator.endKeyframe();
      this.animator.startKeyframe(3);
      this.animator.rotate(this.body, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, 0.0F, 0.0F, Maths.rad(10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-10.0D), Maths.rad(10.0D), Maths.rad(35.0D));
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(3);
      this.animator.startKeyframe(3);
      this.animator.rotate(this.body, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(-15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, Maths.rad(-10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-10.0D), Maths.rad(-10.0D), Maths.rad(-35.0D));
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(3);
      this.animator.startKeyframe(3);
      this.animator.rotate(this.body, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(-10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, 0.0F, 0.0F, Maths.rad(10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-10.0D), Maths.rad(10.0D), Maths.rad(35.0D));
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(3);
      this.animator.startKeyframe(3);
      this.animator.rotate(this.body, Maths.rad(-2.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(2.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(2.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(-15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, Maths.rad(-10.0D));
      this.animator.rotate(this.headPivot, Maths.rad(-10.0D), Maths.rad(-10.0D), Maths.rad(-35.0D));
      this.animator.endKeyframe();
      this.animator.resetKeyframe(5);
      this.animator.setAnimation(EmuServant.ANIMATION_PECK_GROUND);
      this.animator.startKeyframe(10);
      this.animator.move(this.neck1, 0.0F, -0.2F, -0.2F);
      this.animator.rotate(this.neck1, Maths.rad(145.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(-60.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      this.animator.move(this.neck1, 0.0F, -0.2F, -0.2F);
      this.animator.rotate(this.neck1, Maths.rad(135.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(-60.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      this.animator.move(this.neck1, 0.0F, -1.0F, -0.2F);
      this.animator.rotate(this.neck1, Maths.rad(145.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(15.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(-60.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.resetKeyframe(5);
      this.animator.setAnimation(EmuServant.ANIMATION_SCRATCH);
      this.animator.startKeyframe(5);
      this.animator.move(this.leg_right, 0.0F, -0.5F, 2.0F);
      this.animator.move(this.leg_left, 0.0F, -0.5F, 2.0F);
      this.animator.rotate(this.body, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(24.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(3);
      this.animator.move(this.leg_right, 0.0F, -0.5F, 2.0F);
      this.animator.rotate(this.body, Maths.rad(-40.0D), 0.0F, Maths.rad(-10.0D));
      this.animator.rotate(this.leg_left, Maths.rad(-70.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_left, Maths.rad(70.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(40.0D), 0.0F, Maths.rad(30.0D));
      this.animator.rotate(this.neck1, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(24.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(4);
      this.animator.move(this.leg_right, 0.0F, -0.5F, 2.0F);
      this.animator.move(this.leg_left, 0.0F, -0.5F, 2.0F);
      this.animator.rotate(this.body, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_right, Maths.rad(20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck1, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(24.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(3);
      this.animator.move(this.leg_right, 0.0F, -0.5F, 2.0F);
      this.animator.rotate(this.body, Maths.rad(-40.0D), 0.0F, Maths.rad(10.0D));
      this.animator.rotate(this.leg_right, Maths.rad(-70.0D), 0.0F, 0.0F);
      this.animator.rotate(this.foot_right, Maths.rad(70.0D), 0.0F, 0.0F);
      this.animator.rotate(this.leg_left, Maths.rad(40.0D), 0.0F, Maths.rad(30.0D));
      this.animator.rotate(this.neck1, Maths.rad(10.0D), 0.0F, 0.0F);
      this.animator.rotate(this.neck2, Maths.rad(-20.0D), 0.0F, 0.0F);
      this.animator.rotate(this.headPivot, Maths.rad(24.0D), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.resetKeyframe(5);
   }

   @Override
   public void setupAnim(EmuServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      float walkSpeed = 0.4F;
      float walkDegree = 0.4F;
      float idleSpeed = 0.05F;
      float idleDegree = 0.1F;
      this.walk(this.neck1, idleSpeed, idleDegree, false, 0.0F, -0.1F, ageInTicks, 1.0F);
      this.walk(this.neck2, idleSpeed, idleDegree, true, 1.0F, 0.15F, ageInTicks, 1.0F);
      this.walk(this.head, idleSpeed, idleDegree, false, 1.0F, 0.25F, ageInTicks, 1.0F);
      this.walk(this.tail, idleSpeed, idleDegree, false, 2.0F, -0.05F, ageInTicks, 1.0F);
      this.walk(this.leg_right, walkSpeed, walkDegree * 2.0F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
      this.walk(this.leg_left, walkSpeed, walkDegree * 2.0F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
      this.walk(this.foot_right, walkSpeed, walkDegree * 1.5F, false, 2.0F, 0.2F, limbSwing, limbSwingAmount);
      this.walk(this.foot_left, walkSpeed, walkDegree * 1.5F, true, 2.0F, 0.2F, limbSwing, limbSwingAmount);
      this.walk(this.neck1, walkSpeed, walkDegree * 1.0F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
      this.walk(this.neck2, walkSpeed, walkDegree * 0.8F, true, 1.3F, 0.0F, limbSwing, limbSwingAmount);
      this.walk(this.head, walkSpeed, walkDegree * 0.25F, true, 1.3F, 0.0F, limbSwing, limbSwingAmount);
      this.walk(this.tail, walkSpeed, walkDegree * 0.4F, true, 1.3F, -0.4F, limbSwing, limbSwingAmount);
      this.bob(this.body, walkSpeed, walkDegree * 14.0F, true, limbSwing, limbSwingAmount);
      this.flap(this.body, walkSpeed, walkDegree * 0.7F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
      this.flap(this.leg_left, walkSpeed, walkDegree * 0.7F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
      this.flap(this.leg_right, walkSpeed, walkDegree * 0.7F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
      this.flap(this.neck1, walkSpeed, walkDegree * 0.8F, true, 3.0F, 0.0F, limbSwing, limbSwingAmount);
      this.flap(this.neck2, walkSpeed, walkDegree * 0.4F, true, 3.2F, 0.0F, limbSwing, limbSwingAmount);
      this.swing(this.tail, walkSpeed, walkDegree * 0.8F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
      this.faceTarget(netHeadYaw, headPitch, 1.0F, new AdvancedModelBox[]{this.neck2, this.head});
      float runProgress = 5.0F * limbSwingAmount;
      if (entity.getAnimation() != EmuServant.ANIMATION_PECK_GROUND) {
         this.progressPositionPrev(this.neck1, runProgress, 0.0F, -3.0F, -1.5F, 5.0F);
      }
      this.progressPositionPrev(this.neck2, runProgress, 0.0F, 0.5F, -1.5F, 5.0F);
      this.progressPositionPrev(this.headPivot, runProgress, 0.0F, 0.5F, -0.5F, 5.0F);
      this.progressRotationPrev(this.neck1, runProgress, Maths.rad(120.0D), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.neck2, runProgress, Maths.rad(-60.0D), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.headPivot, runProgress, Maths.rad(-50.0D), 0.0F, 0.0F, 5.0F);
   }

   public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
      advancedModelBox.rotateAngleX = x;
      advancedModelBox.rotateAngleY = y;
      advancedModelBox.rotateAngleZ = z;
   }
}
