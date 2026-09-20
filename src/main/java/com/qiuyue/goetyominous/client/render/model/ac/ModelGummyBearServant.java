package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.ac.GummyBearServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGummyBearServant extends AdvancedEntityModel<GummyBearServant> {

    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox left_Ear;
    private final AdvancedModelBox right_Ear;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox left_Leg;
    private float red = 1.0F;
    private float green = 1.0F;
    private float blue = 1.0F;
    private float alpha = 1.0F;
    public boolean ignoreColor;
    private final ModelAnimator animator;

    public ModelGummyBearServant(float scale) {
        this.texWidth = 128;
        this.texHeight = 128;
        this.main = new AdvancedModelBox(this);
        this.main.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, -13.0F, 0.5F);
        this.main.addChild(this.body);
        this.body.setTextureOffset(0, 0).addBox(-8.0F, -7.0F, -10.5F, 16.0F, 14.0F, 21.0F, scale + 0.01F, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, 1.5F, -10.0F);
        this.body.addChild(this.head);
        this.head.setTextureOffset(0, 35).addBox(-6.0F, -4.5F, -7.5F, 12.0F, 9.0F, 7.0F, scale, false);
        this.nose = new AdvancedModelBox(this);
        this.nose.setRotationPoint(0.0F, 1.5F, -7.5F);
        this.head.addChild(this.nose);
        this.nose.setTextureOffset(0, 9).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 5.0F, 3.0F, scale, false);
        this.left_Ear = new AdvancedModelBox(this);
        this.left_Ear.setRotationPoint(3.5F, -4.49F, -4.5F);
        this.head.addChild(this.left_Ear);
        this.left_Ear.setTextureOffset(34, 47).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 2.0F, 4.0F, scale, false);
        this.right_Ear = new AdvancedModelBox(this);
        this.right_Ear.setRotationPoint(-3.5F, -4.49F, -4.5F);
        this.head.addChild(this.right_Ear);
        this.right_Ear.setTextureOffset(34, 47).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 2.0F, 4.0F, scale, true);
        this.tail = new AdvancedModelBox(this);
        this.tail.setRotationPoint(0.0F, 2.0F, 10.0F);
        this.body.addChild(this.tail);
        this.tail.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, 0.5F, 6.0F, 6.0F, 3.0F, scale, false);
        this.left_Arm = new AdvancedModelBox(this);
        this.left_Arm.setRotationPoint(5.0F, 6.5F, -7.5F);
        this.body.addChild(this.left_Arm);
        this.left_Arm.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, false);
        this.right_Arm = new AdvancedModelBox(this);
        this.right_Arm.setRotationPoint(-5.0F, 6.5F, -7.5F);
        this.body.addChild(this.right_Arm);
        this.right_Arm.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, true);
        this.right_Leg = new AdvancedModelBox(this);
        this.right_Leg.setRotationPoint(-5.0F, 6.5F, 7.5F);
        this.body.addChild(this.right_Leg);
        this.right_Leg.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, true);
        this.left_Leg = new AdvancedModelBox(this);
        this.left_Leg.setRotationPoint(5.0F, 6.5F, 7.5F);
        this.body.addChild(this.left_Leg);
        this.left_Leg.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, false);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (alpha * this.alpha > 0.0F || this.ignoreColor) {
            float redIn = this.ignoreColor ? 1.0F : red * this.red;
            float greenIn = this.ignoreColor ? 1.0F : green * this.green;
            float blueIn = this.ignoreColor ? 1.0F : blue * this.blue;
            float alphaIn = this.ignoreColor ? 1.0F : alpha * this.alpha;
            if (this.young) {
                float f = 1.5F;
                this.head.setScale(f, f, f);
                this.head.setShouldScaleChildren(true);
                matrixStackIn.pushPose();
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0.0D);
                this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn));
                matrixStackIn.popPose();
                this.head.setScale(1.0F, 1.0F, 1.0F);
            } else {
                matrixStackIn.pushPose();
                this.parts().forEach(part -> part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn));
                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.main, this.head, this.body, this.nose, this.tail, this.left_Arm, this.right_Arm, this.left_Ear, this.right_Ear, this.right_Leg, this.left_Leg);
    }

    public void animate(GummyBearServant entity) {
        this.animator.update(entity);
        this.animator.setAnimation(GummyBearServant.ANIMATION_FISH);
        this.animator.startKeyframe(15);
        this.animator.move(this.body, 0.0F, 1.0F, 4.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(-10.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(-10.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-30.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-30.0), 0.0F, 0.0F);
        this.animator.move(this.right_Arm, 0.0F, -2.0F, -1.0F);
        this.animator.move(this.left_Arm, 0.0F, -2.0F, -1.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, -10.0F);
        this.animator.move(this.head, 0.0F, -2.0F, 0.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(30.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(30.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-50.0), (float) Math.toRadians(30.0), 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-50.0), (float) Math.toRadians(-30.0), 0.0F);
        this.animator.move(this.right_Arm, 0.0F, 0.0F, -2.0F);
        this.animator.move(this.left_Arm, 0.0F, 0.0F, -2.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(5);
        this.animator.move(this.head, 0.0F, -2.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(20.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-30.0), (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-30.0), (float) Math.toRadians(10.0), 0.0F);
        this.animator.endKeyframe();
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GummyBearServant.ANIMATION_EAT);
        this.animator.startKeyframe(5);
        this.animator.move(this.head, 0.0F, 3.0F, 1.0F);
        this.animator.rotate(this.right_Arm, 0.0F, 0.0F, (float) Math.toRadians(-20.0));
        this.animator.rotate(this.left_Arm, 0.0F, 0.0F, (float) Math.toRadians(20.0));
        this.animator.move(this.right_Arm, -1.0F, 0.0F, 3.0F);
        this.animator.move(this.left_Arm, 1.0F, 0.0F, 3.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(30);
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GummyBearServant.ANIMATION_BACKSCRATCH);
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, 3.0F);
        this.animator.rotate(this.right_Arm, 0.0F, 0.0F, (float) Math.toRadians(20.0));
        this.animator.rotate(this.left_Arm, 0.0F, 0.0F, (float) Math.toRadians(-20.0));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(80);
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GummyBearServant.ANIMATION_MAUL);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(20.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-70.0), 0.0F, (float) Math.toRadians(20.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(-20.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(2);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(-20.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(20.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(-20.0));
        this.animator.endKeyframe();
        this.animator.resetKeyframe(5);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(-20.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(10.0), 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(-20.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-70.0), 0.0F, (float) Math.toRadians(20.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(2);
        this.animator.rotate(this.body, 0.0F, (float) Math.toRadians(20.0), 0.0F);
        this.animator.rotate(this.head, 0.0F, (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(-20.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(20.0));
        this.animator.endKeyframe();
        this.animator.resetKeyframe(6);
        this.animator.setAnimation(GummyBearServant.ANIMATION_SWIPE);
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, -2.0F);
        this.animator.move(this.right_Arm, 0.0F, 0.0F, -2.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(-10.0), (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-60.0), 0.0F, (float) Math.toRadians(-10.0));
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(10.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, -4.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, -2.0F);
        this.animator.move(this.right_Arm, 0.0F, 0.0F, -2.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(-10.0), (float) Math.toRadians(-10.0), 0.0F);
        this.animator.rotate(this.right_Leg, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Leg, (float) Math.toRadians(10.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-60.0), 0.0F, (float) Math.toRadians(10.0));
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(20.0), 0.0F, (float) Math.toRadians(-10.0));
        this.animator.endKeyframe();
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 0.0F, -4.0F);
        this.animator.endKeyframe();
        this.animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(GummyBearServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float animationIntensity;
        this.resetToDefaultPose();
        this.animate(entity);
        boolean november13th = entity.lookForTheGummyBearAlbumInStoresOnNovember13th;
        float partialTicks = ageInTicks - (float) entity.tickCount;
        float walkSpeed = 0.5F;
        float walkDegree = 1.0F;
        float bounceSpeed = 1.0F;
        float bounceDegree = 1.0F;
        float headYawAmount = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float sleepProgress = entity.getSleepProgress(partialTicks);
        float danceProgress = (1.0F - sleepProgress) * entity.getDanceProgress(partialTicks);
        float sitProgress = (1.0F - sleepProgress) * Math.max(entity.getSitProgress(partialTicks), danceProgress);
        float standProgress = (1.0F - sleepProgress) * (1.0F - sitProgress) * (november13th ? Math.min(1.0F, limbSwingAmount * entity.getStandProgress(partialTicks)) : entity.getStandProgress(partialTicks));
        float uprightProgress = Math.max(sitProgress, standProgress);
        float allFoursProgress = 1.0F - uprightProgress;
        this.progressRotationPrev(this.body, uprightProgress, (float) Math.toRadians(-90.0), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.head, uprightProgress, (float) Math.toRadians(90.0), 0.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.head, uprightProgress, 0.0F, -4.0F, -4.0F, 1.0F);
        this.progressPositionPrev(this.body, sitProgress, 0.0F, 2.5F, 2.5F, 1.0F);
        this.progressPositionPrev(this.body, standProgress, 0.0F, -2.8F, 0.0F, 1.0F);
        this.progressPositionPrev(this.left_Leg, standProgress, 0.0F, -2.5F, 2.0F, 1.0F);
        this.progressPositionPrev(this.right_Leg, standProgress, 0.0F, -2.5F, 2.0F, 1.0F);
        this.progressPositionPrev(this.tail, standProgress, 0.0F, -2.5F, 0.0F, 1.0F);
        if (november13th) {
            this.progressRotationPrev(this.left_Leg, standProgress, (float) Math.toRadians(90.0), 0.0F, 0.0F, 1.0F);
            this.progressRotationPrev(this.right_Leg, standProgress, (float) Math.toRadians(90.0), 0.0F, 0.0F, 1.0F);
            this.progressRotationPrev(this.right_Arm, standProgress, 0.0F, 0.0F, (float) Math.toRadians(60.0), 1.0F);
            this.progressRotationPrev(this.left_Arm, standProgress, 0.0F, 0.0F, (float) Math.toRadians(-60.0), 1.0F);
        } else {
            this.progressRotationPrev(this.body, standProgress, (float) Math.toRadians(10.0), 0.0F, 0.0F, 1.0F);
            this.progressRotationPrev(this.head, standProgress, (float) Math.toRadians(-10.0), 0.0F, 0.0F, 1.0F);
            this.progressRotationPrev(this.left_Leg, standProgress, (float) Math.toRadians(80.0), 0.0F, 0.0F, 1.0F);
            this.progressRotationPrev(this.right_Leg, standProgress, (float) Math.toRadians(80.0), 0.0F, 0.0F, 1.0F);
        }
        this.progressRotationPrev(this.right_Arm, sleepProgress, (float) Math.toRadians(-30.0), 0.0F, (float) Math.toRadians(90.0), 1.0F);
        this.progressRotationPrev(this.left_Arm, sleepProgress, (float) Math.toRadians(-30.0), 0.0F, (float) Math.toRadians(-90.0), 1.0F);
        this.progressRotationPrev(this.head, sleepProgress, (float) Math.toRadians(10.0), (float) Math.toRadians(-20.0), (float) Math.toRadians(10.0), 1.0F);
        this.progressRotationPrev(this.right_Leg, sleepProgress, (float) Math.toRadians(30.0), 0.0F, (float) Math.toRadians(90.0), 1.0F);
        this.progressRotationPrev(this.left_Leg, sleepProgress, (float) Math.toRadians(30.0), 0.0F, (float) Math.toRadians(-90.0), 1.0F);
        this.progressPositionPrev(this.body, sleepProgress, 0.0F, 5.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.right_Arm, sleepProgress, -1.0F, -2.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.left_Arm, sleepProgress, 1.0F, -2.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.head, sleepProgress, 0.0F, -1.0F, 1.0F, 1.0F);
        this.progressPositionPrev(this.right_Leg, sleepProgress, -1.0F, -2.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.left_Leg, sleepProgress, 1.0F, -2.0F, 0.0F, 1.0F);
        this.flap(this.tail, 0.06F, 0.1F, false, -1.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.left_Ear, 0.06F, 0.1F, false, -1.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.right_Ear, 0.06F, 0.1F, true, -1.0F, 0.0F, ageInTicks, 1.0F);
        this.bob(this.head, 0.06F, 0.2F, false, ageInTicks, 1.0F);
        this.flap(this.right_Leg, 0.06F, 0.2F, false, -1.0F, 0.2F, ageInTicks, sitProgress);
        this.flap(this.left_Leg, 0.06F, 0.2F, true, -1.0F, 0.2F, ageInTicks, sitProgress);
        this.walk(this.right_Arm, 0.06F, 0.1F, false, -2.0F, 0.2F, ageInTicks, sitProgress);
        this.walk(this.left_Arm, 0.06F, 0.1F, false, -2.0F, 0.2F, ageInTicks, sitProgress);
        this.walk(this.right_Arm, 0.06F, 0.1F, false, -2.0F, 0.4F, ageInTicks, standProgress);
        this.walk(this.left_Arm, 0.06F, 0.1F, false, -2.0F, 0.4F, ageInTicks, standProgress);
        this.flap(this.body, 0.3F, 0.1F, false, -2.0F, 0.0F, ageInTicks, danceProgress);
        this.bob(this.body, 0.3F, 7.0F, true, ageInTicks, danceProgress);
        this.flap(this.head, 0.3F, 0.1F, false, -2.0F, 0.0F, ageInTicks, danceProgress);
        this.swing(this.head, 0.3F, 0.1F, false, -2.0F, 0.0F, ageInTicks, danceProgress);
        this.walk(this.left_Arm, 0.3F, 0.5F, true, -2.0F, 0.5F, ageInTicks, danceProgress);
        this.walk(this.right_Arm, 0.3F, 0.5F, false, -2.0F, -0.5F, ageInTicks, danceProgress);
        this.walk(this.left_Leg, 0.3F, 0.3F, true, -1.5F, -0.1F, ageInTicks, danceProgress);
        this.walk(this.right_Leg, 0.3F, 0.3F, true, -1.5F, -0.1F, ageInTicks, danceProgress);
        if (november13th) {
            this.walk(this.body, bounceSpeed, bounceDegree * 0.1F, true, 1.0F, -0.1F, limbSwing, limbSwingAmount);
            this.walk(this.left_Leg, bounceSpeed, bounceDegree, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(this.right_Leg, bounceSpeed, bounceDegree, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(this.right_Arm, bounceSpeed, bounceDegree, true, 2.0F, 0.3F, limbSwing, limbSwingAmount);
            this.walk(this.left_Arm, bounceSpeed, bounceDegree, true, 2.0F, 0.3F, limbSwing, limbSwingAmount);
            this.body.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, bounceSpeed, 0.4F, bounceDegree * 15.0F, false));
            this.body.rotationPointZ += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, bounceSpeed, 0.8F, bounceDegree * 6.0F, false));
        } else {
            this.flap(this.body, walkSpeed * 2.0F, walkDegree * 0.1F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
            this.flap(this.head, walkSpeed * 2.0F, walkDegree * 0.1F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(this.left_Leg, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.left_Leg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 2.0F, true));
            this.walk(this.right_Leg, walkSpeed, walkDegree, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.right_Leg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 2.0F, false));
            this.walk(this.left_Arm, walkSpeed, walkDegree, false, 2.5F, 0.0F, limbSwing, limbSwingAmount * allFoursProgress);
            this.left_Arm.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount * allFoursProgress, walkSpeed, 1.0F, walkDegree * 2.0F, true));
            this.walk(this.right_Arm, walkSpeed, walkDegree, true, 2.5F, 0.0F, limbSwing, limbSwingAmount * allFoursProgress);
            this.right_Arm.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount * allFoursProgress, walkSpeed, 1.0F, walkDegree * 2.0F, false));
        }
        if (entity.getAnimation() == GummyBearServant.ANIMATION_EAT) {
            animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4.0F, GummyBearServant.ANIMATION_EAT, partialTicks, 0);
            this.walk(this.head, 0.5F, 0.3F, true, 0.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.left_Arm, 0.5F, 0.3F, true, 1.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.right_Arm, 0.5F, 0.3F, true, 1.0F, 0.2F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == GummyBearServant.ANIMATION_BACKSCRATCH) {
            animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4.0F, GummyBearServant.ANIMATION_BACKSCRATCH, partialTicks, 0);
            float bodyScratchUp = animationIntensity * 2.0F - ACMath.walkValue(ageInTicks, animationIntensity, 0.45F, -0.5F, 2.0F, true);
            this.body.rotationPointY += bodyScratchUp;
            this.left_Leg.rotationPointZ -= bodyScratchUp;
            this.right_Leg.rotationPointZ -= bodyScratchUp;
            this.walk(this.body, 0.45F, 0.1F, true, 0.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.left_Leg, 0.45F, 0.1F, false, 0.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.right_Leg, 0.45F, 0.1F, false, 0.0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(this.head, 0.45F, 0.1F, true, 1.0F, 0.1F, ageInTicks, animationIntensity);
            this.flap(this.right_Arm, 0.45F, 0.1F, true, 1.0F, -0.3F, ageInTicks, animationIntensity);
            this.flap(this.left_Arm, 0.45F, 0.1F, true, 1.0F, 0.3F, ageInTicks, animationIntensity);
        }
        this.head.rotateAngleY += headYawAmount * 0.65F;
        this.head.rotateAngleX += headPitchAmount * 0.75F;
    }
}
