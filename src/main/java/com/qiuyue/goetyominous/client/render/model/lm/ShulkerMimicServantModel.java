package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.ShulkerMimic.NewShulkerMimicAnimations;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ShulkerMimicServantModel<T extends ShulkerMimicServant> extends HierarchicalModel<T> {

    private static final AnimationDefinition SLEEP_IN = reverse(NewShulkerMimicAnimations.awaken);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart body;
    public final ModelPart head;

    private static float lastTimestamp(AnimationDefinition definition) {
        float last = 0.0F;
        for (List<AnimationChannel> channels : definition.boneAnimations().values()) {
            for (AnimationChannel channel : channels) {
                Keyframe[] frames = channel.keyframes();
                if (frames.length > 0) {
                    last = Math.max(last, frames[frames.length - 1].timestamp());
                }
            }
        }
        return last;
    }

    private static AnimationDefinition reverse(AnimationDefinition definition) {
        float end = lastTimestamp(definition);
        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(definition.lengthInSeconds());
        definition.boneAnimations().forEach((bone, channels) -> {
            for (AnimationChannel channel : channels) {
                Keyframe[] frames = channel.keyframes();
                Keyframe[] flipped = new Keyframe[frames.length];
                for (int i = 0; i < frames.length; ++i) {
                    Keyframe frame = frames[frames.length - 1 - i];
                    flipped[i] = new Keyframe(end - frame.timestamp(), frame.target(), frame.interpolation());
                }
                builder.addAnimation(bone, new AnimationChannel(channel.target(), flipped));
            }
        });
        return builder.build();
    }

    private void animateSleep(T entity, float ageInTicks) {
        AnimationState sleep = entity.getAnimationState("sleep");
        sleep.updateTime(ageInTicks, 1.0F);
        sleep.ifStarted(state -> KeyframeAnimations.animate(this, SLEEP_IN, state.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
    }

    private void animateAwake(T entity, float ageInTicks) {
        AnimationState awake = entity.getAnimationState("awake");
        AnimationState sleep = entity.getAnimationState("sleep");
        awake.updateTime(ageInTicks, 1.0F);
        long sleptMillis = sleep.isStarted() ? 0L : sleep.getAccumulatedTime();
        long skippedMillis = sleptMillis > 0L ? Math.max(0L, SLEEP_IN_MILLIS - sleptMillis) : 0L;
        awake.ifStarted(state -> KeyframeAnimations.animate(this, NewShulkerMimicAnimations.awaken,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public ShulkerMimicServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -1.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 1.0F));
        head.addOrReplaceChild("headmini", CubeListBuilder.create().texOffs(0, 94).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(32, 94).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, -6.1F, -1.0F));
        PartDefinition upperboxinnerrotation = head.addOrReplaceChild("upperboxinnerrotation", CubeListBuilder.create(), PartPose.offset(0.0F, -13.2F, -0.5F));
        PartDefinition upperbox = upperboxinnerrotation.addOrReplaceChild("upperbox", CubeListBuilder.create().texOffs(0, 67).addBox(-10.0F, -9.8F, -20.0F, 20.0F, 7.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(80, 66).addBox(-10.0F, -2.8F, -20.0F, 20.0F, 6.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 10.0F));
        upperbox.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(64, 94).addBox(8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.8F, 2.122F, -3.1416F, 0.7854F, -3.1416F));
        upperbox.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(64, 94).addBox(8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.1213F, -6.8F, -10.0007F, 0.0F, 0.7854F, 0.0F));
        upperbox.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(64, 94).mirror().addBox(-8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(12.1213F, -6.8F, -10.0007F, 0.0F, -0.7854F, 0.0F));
        upperbox.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(64, 94).mirror().addBox(-8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -6.8F, 2.122F, -3.1416F, -0.7854F, 3.1416F));
        PartDefinition lowerboxinnerrotation = head.addOrReplaceChild("lowerboxinnerrotation", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));
        PartDefinition lowerbox = lowerboxinnerrotation.addOrReplaceChild("lowerbox", CubeListBuilder.create().texOffs(0, 40).addBox(-10.0F, 2.8F, -20.0F, 20.0F, 7.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(80, 40).addBox(-10.0F, -3.2F, -20.0F, 20.0F, 6.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.8F, 9.5F));
        lowerbox.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(76, 104).mirror().addBox(-8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(12.1213F, 6.8F, -10.0007F, 0.0F, -0.7854F, 0.0F));
        lowerbox.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(76, 104).addBox(8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.1213F, 6.8F, -10.0007F, 0.0F, 0.7854F, 0.0F));
        lowerbox.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(76, 104).addBox(8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.8F, 2.122F, 3.1416F, 0.7854F, 3.1416F));
        lowerbox.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(76, 104).mirror().addBox(-8.5706F, -5.0F, -11.5706F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 6.8F, 2.122F, 3.1416F, -0.7854F, -3.1416F));
        PartDefinition lowerbody = body.addOrReplaceChild("lowerbody", CubeListBuilder.create().texOffs(80, 92).addBox(-5.0F, 15.0F, -4.5F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.0F));
        lowerbody.addOrReplaceChild("rotors", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, 0.0F, -20.0F, 40.0F, 0.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.5F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void translateModel(PoseStack poseStack) {
        this.body.translateAndRotate(poseStack);
        this.root.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = Mth.clamp(netHeadYaw, -30.0F, 30.0F) * ((float) Math.PI / 180);
        this.head.xRot = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float) Math.PI / 180);
        if (entity.getAttackState() == 0) {
            this.animateWalk(NewShulkerMimicAnimations.walk, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        this.animate(entity.getAnimationState("idle"), NewShulkerMimicAnimations.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("bite"), NewShulkerMimicAnimations.bite3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep"), NewShulkerMimicAnimations.backStep, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("backstep_charge"), NewShulkerMimicAnimations.backstepCharge, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("triple_shoot"), NewShulkerMimicAnimations.tripleShootBullets2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_shoot"), NewShulkerMimicAnimations.doubleShootBullets, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("triple_shoot_tp1"), NewShulkerMimicAnimations.tripleShootBulletsTp3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("triple_shoot_tp2"), NewShulkerMimicAnimations.tripleShootBulletsTp4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("big_shoot"), NewShulkerMimicAnimations.bigShoot2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("pull_hit"), NewShulkerMimicAnimations.pushAway2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_slash"), NewShulkerMimicAnimations.doubleSlash3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), NewShulkerMimicAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("firework_grab_pre"), NewShulkerMimicAnimations.fireworkGrabPre2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("firework_grab_fail"), NewShulkerMimicAnimations.fireworkGrabFail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("firework_grab_success"), NewShulkerMimicAnimations.fireworkGrabSuccess, ageInTicks, 1.0F);
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
