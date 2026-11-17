package com.qiuyue.goetyominous.client.render.model;

import com.qiuyue.goetyominous.client.render.model.animation.EliteFungusThrowerAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class FungusThrowerPiglinAnimator {
    private static final float DEG_TO_RAD = (float) Math.PI / 180F;

    private final PlayerModel<?> model;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart synBody;
    private final ModelPart synHead;
    private final ModelPart synLeftEar;
    private final ModelPart synRightEar;
    private final ModelPart synRightArm;
    private final ModelPart synLeftArm;
    private final ModelPart synRightLeg;
    private final ModelPart synLeftLeg;
    private final HierarchicalModel<?> animatable;

    private final Quaternionf bodyRotation = new Quaternionf();
    private final Quaternionf partRotation = new Quaternionf();
    private final Quaternionf totalRotation = new Quaternionf();
    private final Matrix3f rotationMatrix = new Matrix3f();
    private final Vector3f row0 = new Vector3f();
    private final Vector3f row1 = new Vector3f();
    private final Vector3f row2 = new Vector3f();
    private final Vector3f offset = new Vector3f();

    public FungusThrowerPiglinAnimator(PlayerModel<?> model) {
        this.model = model;
        this.leftEar = model.head.getChild("left_ear");
        this.rightEar = model.head.getChild("right_ear");
        this.synLeftEar = createPart(PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));
        this.synRightEar = createPart(PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));
        this.synHead = createPart(PartPose.offset(0.0F, -12.0F, 0.0F),
                Map.of("leftear", this.synLeftEar, "rightear", this.synRightEar));
        this.synRightArm = createPart(PartPose.offset(-5.0F, -10.0F, 0.0F));
        this.synLeftArm = createPart(PartPose.offset(5.0F, -10.0F, 0.0F));
        this.synBody = createPart(PartPose.offset(0.0F, 12.0F, 0.0F),
                Map.of("head", this.synHead, "RightArm", this.synRightArm, "LeftArm", this.synLeftArm));
        this.synRightLeg = createPart(PartPose.offset(-1.9F, 12.0F, 0.0F));
        this.synLeftLeg = createPart(PartPose.offset(1.9F, 12.0F, 0.0F));
        ModelPart root = new ModelPart(List.of(),
                Map.of("Body", this.synBody, "RightLeg", this.synRightLeg, "LeftLeg", this.synLeftLeg));
        this.animatable = new HierarchicalModel<Entity>() {
            @Override
            public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            }

            @Override
            public ModelPart root() {
                return root;
            }
        };
    }

    private static ModelPart createPart(PartPose pose) {
        return createPart(pose, Map.of());
    }

    private static ModelPart createPart(PartPose pose, Map<String, ModelPart> children) {
        ModelPart part = new ModelPart(List.of(), children);
        part.setInitialPose(pose);
        part.loadPose(pose);
        return part;
    }

    public void animate(Entity entity, float ageInTicks, float netHeadYaw, float headPitch,
                        AnimationState idle, AnimationState walk, AnimationState throwingState,
                        boolean throwing, float walkWeight) {
        this.synBody.resetPose();
        this.synHead.resetPose();
        this.synLeftEar.resetPose();
        this.synRightEar.resetPose();
        this.synRightArm.resetPose();
        this.synLeftArm.resetPose();
        this.synRightLeg.resetPose();
        this.synLeftLeg.resetPose();
        this.synHead.yRot = netHeadYaw * DEG_TO_RAD;
        this.synHead.xRot = headPitch * DEG_TO_RAD;
        if (throwing) {
            throwingState.startIfStopped(entity.tickCount);
            PoseBlend.animate(this.animatable, throwingState, EliteFungusThrowerAnimations.THROW, ageInTicks, 1.0F);
        } else {
            idle.startIfStopped(entity.tickCount);
            walk.startIfStopped(entity.tickCount);
            PoseBlend.animate(this.animatable, idle, EliteFungusThrowerAnimations.IDLE, ageInTicks, 1.0F - walkWeight);
            PoseBlend.animate(this.animatable, walk, EliteFungusThrowerAnimations.WALK, ageInTicks, walkWeight);
        }
        this.applyPose();
    }

    private void applyPose() {
        this.bodyRotation.rotationZYX(this.synBody.zRot, this.synBody.yRot, this.synBody.xRot);
        this.applyBody();
        copyPose(this.synRightLeg, this.model.rightLeg);
        copyPose(this.synLeftLeg, this.model.leftLeg);
        this.applyBodyChild(this.synHead, this.model.head);
        this.applyBodyChild(this.synRightArm, this.model.rightArm);
        this.applyBodyChild(this.synLeftArm, this.model.leftArm);
        copyPose(this.synLeftEar, this.leftEar);
        copyPose(this.synRightEar, this.rightEar);
        this.model.leftPants.copyFrom(this.model.leftLeg);
        this.model.rightPants.copyFrom(this.model.rightLeg);
        this.model.leftSleeve.copyFrom(this.model.leftArm);
        this.model.rightSleeve.copyFrom(this.model.rightArm);
        this.model.jacket.copyFrom(this.model.body);
        this.model.hat.copyFrom(this.model.head);
    }

    private void applyBody() {
        this.offset.set(0.0F, 12.0F, 0.0F);
        this.bodyRotation.transform(this.offset);
        this.model.body.x = this.synBody.x - this.offset.x;
        this.model.body.y = this.synBody.y - this.offset.y;
        this.model.body.z = this.synBody.z - this.offset.z;
        this.model.body.xRot = this.synBody.xRot;
        this.model.body.yRot = this.synBody.yRot;
        this.model.body.zRot = this.synBody.zRot;
    }

    private void applyBodyChild(ModelPart source, ModelPart target) {
        this.offset.set(source.x, source.y, source.z);
        this.bodyRotation.transform(this.offset);
        target.x = this.synBody.x + this.offset.x;
        target.y = this.synBody.y + this.offset.y;
        target.z = this.synBody.z + this.offset.z;
        this.partRotation.rotationZYX(source.zRot, source.yRot, source.xRot);
        this.totalRotation.set(this.bodyRotation).mul(this.partRotation);
        this.totalRotation.get(this.rotationMatrix);
        this.rotationMatrix.getRow(0, this.row0);
        this.rotationMatrix.getRow(1, this.row1);
        this.rotationMatrix.getRow(2, this.row2);
        target.yRot = (float) Math.asin(Mth.clamp(-this.row2.x, -1.0F, 1.0F));
        if (Math.abs(this.row2.x) < 0.9999F) {
            target.xRot = (float) Math.atan2(this.row2.y, this.row2.z);
            target.zRot = (float) Math.atan2(this.row1.x, this.row0.x);
        } else {
            target.xRot = (float) Math.atan2(-this.row1.z, this.row1.y);
            target.zRot = 0.0F;
        }
    }

    private static void copyPose(ModelPart source, ModelPart target) {
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
