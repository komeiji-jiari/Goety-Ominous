package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.ac.CaniacServant;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCaniacServant extends AdvancedEntityModel<CaniacServant> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox pelvis;
    private final AdvancedModelBox spine;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox right_Eye;
    private final AdvancedModelBox left_Eye;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox left_Leg;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final ModelAnimator animator;

    public ModelCaniacServant() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.root = new AdvancedModelBox(this);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, -19.0F, -3.0F);
        this.root.addChild(this.body);
        this.pelvis = new AdvancedModelBox(this);
        this.pelvis.setRotationPoint(0.0F, 3.5F, 3.0F);
        this.body.addChild(this.pelvis);
        this.pelvis.setTextureOffset(12, 40).addBox(-5.0F, -1.5F, -2.0F, 10.0F, 3.0F, 4.0F, 0.0F, false);
        this.spine = new AdvancedModelBox(this);
        this.spine.setRotationPoint(0.0F, -1.5F, 2.0F);
        this.pelvis.addChild(this.spine);
        this.cube_r1 = new AdvancedModelBox(this);
        this.cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spine.addChild(this.cube_r1);
        this.setRotateAngle(this.cube_r1, 0.3927F, 0.0F, 0.0F);
        this.cube_r1.setTextureOffset(0, 6).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);
        this.chest = new AdvancedModelBox(this);
        this.chest.setRotationPoint(0.0F, -3.7F, -1.54F);
        this.spine.addChild(this.chest);
        this.cube_r2 = new AdvancedModelBox(this);
        this.cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.chest.addChild(this.cube_r2);
        this.setRotateAngle(this.cube_r2, 0.3927F, 0.0F, 0.0F);
        this.cube_r2.setTextureOffset(0, 0).addBox(-8.0F, -11.9959F, -9.9898F, 16.0F, 12.0F, 10.0F, 0.0F, false);
        this.neck = new AdvancedModelBox(this);
        this.neck.setRotationPoint(0.0F, -11.05F, -4.56F);
        this.chest.addChild(this.neck);
        this.neck.setTextureOffset(32, 0).addBox(-2.0F, 0.0F, -10.0F, 4.0F, 0.0F, 10.0F, 0.0F, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, 0.0F, -9.4F);
        this.neck.addChild(this.head);
        this.head.setTextureOffset(36, 22).addBox(-7.0F, -7.25F, -4.25F, 14.0F, 14.0F, 4.0F, 0.0F, false);
        this.head.setTextureOffset(0, 22).addBox(-7.0F, -7.25F, -4.25F, 14.0F, 14.0F, 4.0F, 0.25F, false);
        this.right_Eye = new AdvancedModelBox(this);
        this.right_Eye.setRotationPoint(-2.5F, -0.75F, -4.3F);
        this.head.addChild(this.right_Eye);
        this.right_Eye.setTextureOffset(0, 78).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, true);
        this.left_Eye = new AdvancedModelBox(this);
        this.left_Eye.setRotationPoint(2.5F, -0.75F, -4.3F);
        this.head.addChild(this.left_Eye);
        this.left_Eye.setTextureOffset(0, 78).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        this.left_Arm = new AdvancedModelBox(this);
        this.left_Arm.setRotationPoint(7.75F, -8.8F, -6.96F);
        this.chest.addChild(this.left_Arm);
        this.left_Arm.setTextureOffset(20, 47).addBox(0.0F, 22.5F, 8.5F, 3.0F, 7.0F, 3.0F, 0.0F, false);
        this.left_Arm.setTextureOffset(33, 40).addBox(0.0F, 26.5F, 1.5F, 3.0F, 3.0F, 7.0F, 0.0F, false);
        this.left_Arm.setTextureOffset(0, 40).addBox(0.0F, -5.5F, -1.5F, 3.0F, 35.0F, 3.0F, 0.0F, false);
        this.right_Arm = new AdvancedModelBox(this);
        this.right_Arm.setRotationPoint(-7.75F, -8.8F, -6.96F);
        this.chest.addChild(this.right_Arm);
        this.right_Arm.setTextureOffset(20, 47).addBox(-3.0F, 22.5F, 8.5F, 3.0F, 7.0F, 3.0F, 0.0F, true);
        this.right_Arm.setTextureOffset(33, 40).addBox(-3.0F, 26.5F, 1.5F, 3.0F, 3.0F, 7.0F, 0.0F, true);
        this.right_Arm.setTextureOffset(0, 40).addBox(-3.0F, -5.5F, -1.5F, 3.0F, 35.0F, 3.0F, 0.0F, true);
        this.left_Leg = new AdvancedModelBox(this);
        this.left_Leg.setRotationPoint(3.0F, 1.5F, 0.0F);
        this.pelvis.addChild(this.left_Leg);
        this.left_Leg.setTextureOffset(12, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
        this.left_Leg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.left_Leg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        this.right_Leg = new AdvancedModelBox(this);
        this.right_Leg.setRotationPoint(-3.0F, 1.5F, 0.0F);
        this.pelvis.addChild(this.right_Leg);
        this.right_Leg.setTextureOffset(12, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, true);
        this.right_Leg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);
        this.right_Leg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.root, this.body, this.pelvis, this.chest, this.spine, this.chest, this.neck, this.head, this.right_Eye, this.right_Arm, this.left_Eye, this.left_Arm, this.right_Leg, this.left_Leg, this.cube_r1, this.cube_r2);
    }

    public void animate(CaniacServant entity) {
        this.animator.update(entity);
        this.animator.setAnimation(CaniacServant.ANIMATION_LUNGE);
        this.animator.startKeyframe(10);
        this.animator.move(this.body, 0.0F, 0.0F, 4.0F);
        this.animator.rotate(this.spine, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.head, (float) Math.toRadians(-25.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-185.0), (float) Math.toRadians(-25.0), (float) Math.toRadians(45.0));
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-185.0), (float) Math.toRadians(25.0), (float) Math.toRadians(-45.0));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.startKeyframe(5);
        this.animator.move(this.body, 0.0F, 4.0F, -15.0F);
        this.animator.move(this.left_Arm, 0.0F, -3.0F, -8.0F);
        this.animator.move(this.right_Arm, 0.0F, -3.0F, -8.0F);
        this.animator.rotate(this.body, (float) Math.toRadians(30.0), 0.0F, 0.0F);
        this.animator.rotate(this.left_Arm, (float) Math.toRadians(-110.0), (float) Math.toRadians(-15.0), (float) Math.toRadians(60.0));
        this.animator.rotate(this.right_Arm, (float) Math.toRadians(-110.0), (float) Math.toRadians(15.0), (float) Math.toRadians(-60.0));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.resetKeyframe(10);
    }

    @Override
    public void setupAnim(CaniacServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity);
        float walkSpeed = 0.7F;
        float walkDegree = 1.0F;
        float runSpeed = 0.5F;
        float runDegree = 1.0F;
        float partialTick = ageInTicks - entity.tickCount;
        float runProgress = entity.getRunProgress(partialTick);
        float walkProgress = 1.0F - runProgress;
        float walkAmount = limbSwingAmount * walkProgress;
        float runAmount = limbSwingAmount * runProgress;
        float leftArmAngle = (float) Math.toRadians(Mth.wrapDegrees(entity.getArmAngle(true, partialTick)));
        float rightArmAngle = (float) Math.toRadians(Mth.wrapDegrees(entity.getArmAngle(false, partialTick)));
        float headYawAmount = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        this.progressRotationPrev(this.spine, walkAmount, (float) Math.toRadians(-25.0), 0.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.left_Arm, walkAmount, 0.0F, 4.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.right_Arm, walkAmount, 0.0F, 4.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.spine, runAmount, (float) Math.toRadians(15.0), 0.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.body, runAmount, 0.0F, 0.0F, 4.0F, 1.0F);
        this.walk(this.neck, 0.06F, 0.1F, false, 0.0F, 0.1F, ageInTicks, 1.0F);
        this.walk(this.head, 0.06F, 0.1F, true, 0.0F, 0.1F, ageInTicks, 1.0F);
        this.walk(this.chest, 0.06F, 0.05F, true, 1.0F, 0.05F, ageInTicks, 1.0F);
        this.walk(this.left_Arm, 0.06F, 0.05F, false, 1.0F, 0.05F, ageInTicks, 1.0F);
        this.walk(this.right_Arm, 0.06F, 0.05F, false, 1.0F, 0.05F, ageInTicks, 1.0F);
        this.walk(this.left_Leg, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, walkAmount);
        this.walk(this.right_Leg, walkSpeed, walkDegree, true, 1.0F, 0.0F, limbSwing, walkAmount);
        this.walk(this.chest, walkSpeed, walkDegree * 0.1F, false, 2.0F, -0.1F, limbSwing, walkAmount);
        if (entity.getAnimation() != CaniacServant.ANIMATION_LUNGE) {
            this.walk(this.left_Arm, walkSpeed, walkDegree * 0.05F, false, 3.0F, 1.0F, limbSwing, walkAmount);
            this.walk(this.right_Arm, walkSpeed, walkDegree * 0.05F, true, 3.0F, -1.0F, limbSwing, walkAmount);
        }
        float bodyWalkBob = -Math.abs(ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1.5F, 3.0F, false));
        float runWalkBob = -Math.abs(ACMath.walkValue(limbSwing, runAmount, runSpeed, -1.5F, 3.0F, false));
        this.body.rotationPointY += bodyWalkBob + runWalkBob;
        this.walk(this.left_Leg, runSpeed, runDegree, false, 1.0F, 0.0F, limbSwing, runAmount);
        this.walk(this.right_Leg, runSpeed, runDegree, true, 1.0F, 0.0F, limbSwing, runAmount);
        this.walk(this.chest, runSpeed, runDegree * 0.15F, false, 1.0F, -0.1F, limbSwing, runAmount);
        this.swing(this.spine, runSpeed, runDegree * 0.15F, false, 0.0F, 0.0F, limbSwing, runAmount);
        this.swing(this.neck, runSpeed, runDegree * 0.15F, true, 0.0F, 0.0F, limbSwing, runAmount);
        this.right_Arm.rotateAngleX += leftArmAngle;
        this.left_Arm.rotateAngleX += rightArmAngle;
        this.head.rotateAngleY += headYawAmount * 0.35F;
        this.neck.rotateAngleY += headYawAmount * 0.35F;
        this.head.rotateAngleX += headPitchAmount * 0.15F;
        this.neck.rotateAngleX += headPitchAmount * 0.15F;
        Entity look = Minecraft.getInstance().getCameraEntity();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            double d0 = vector3d.y - vector3d1.y;
            float f1 = (float) Mth.clamp(-d0, -1.0, 1.0);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0, vector3d2.z);
            Vec3 vector3d3 = new Vec3(vector3d1.x - vector3d.x, 0.0, vector3d1.z - vector3d.z).normalize().yRot(1.5707964F);
            double d1 = vector3d2.dot(vector3d3);
            double d2 = Mth.sqrt((float) Math.abs(d1 * 2.0)) * Math.signum(d1);
            this.left_Eye.rotationPointX = (float) (this.left_Eye.rotationPointX + (d2 - this.head.rotateAngleZ));
            this.left_Eye.rotationPointY += f1;
            this.right_Eye.rotationPointX = (float) (this.right_Eye.rotationPointX + (d2 - this.head.rotateAngleZ));
            this.right_Eye.rotationPointY += f1;
        }
    }
}
