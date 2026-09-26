package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import net.miauczel.legendary_monsters.entity.animations.CloudGolemAnimations;
import net.miauczel.legendary_monsters.entity.animations.replacer.CGAnims;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CloudGolemServantModel<T extends CloudGolemServant> extends HierarchicalModel<T> {

    private static final float WALK_UPPER_BODY_SWAY = 0.3F;
    private static final AnimationDefinition SLEEP_IN = reverse(CloudGolemAnimations.awake);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart u1;
    private final ModelPart u2;
    private final ModelPart body;
    private final ModelPart runeLower;
    private final ModelPart runeUpper;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

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
        sleep.ifStarted(state -> {
            long elapsed = state.getAccumulatedTime();
            if (elapsed < SLEEP_IN_MILLIS) {
                KeyframeAnimations.animate(this, SLEEP_IN, elapsed, 1.0F, ANIMATION_VECTOR_CACHE);
            } else {
                KeyframeAnimations.animate(this, CloudGolemAnimations.sleep,
                        elapsed - SLEEP_IN_MILLIS, 1.0F, ANIMATION_VECTOR_CACHE);
            }
        });
    }

    private void animateAwake(T entity, float ageInTicks) {
        AnimationState awake = entity.getAnimationState("awake");
        AnimationState sleep = entity.getAnimationState("sleep");
        awake.updateTime(ageInTicks, 1.0F);
        long sleptMillis = sleep.isStarted() ? 0L : sleep.getAccumulatedTime();
        long skippedMillis = sleptMillis > 0L ? Math.max(0L, SLEEP_IN_MILLIS - sleptMillis) : 0L;
        awake.ifStarted(state -> KeyframeAnimations.animate(this, CloudGolemAnimations.awake,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public CloudGolemServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.u1 = root.getChild("root").getChild("body").getChild("left_arm").getChild("upper");
        this.u2 = root.getChild("root").getChild("body").getChild("rightarm").getChild("upper2");
        this.body = root.getChild("root").getChild("body");
        this.head = this.body.getChild("head");
        this.runeLower = this.body.getChild("rune_lower");
        this.runeUpper = this.body.getChild("rune_upper");
        this.rightArm = this.body.getChild("rightarm");
        this.leftArm = this.body.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 6.9999F, 0.0826F));

        PartDefinition rune_lower = body.addOrReplaceChild("rune_lower", CubeListBuilder.create().texOffs(0, 0).addBox(-17.75F, -9.0F, -4.5F, 36.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(44, 82).addBox(-17.75F, -22.0F, -4.5F, 9.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, 0.0F, 0.5F));

        PartDefinition rune_upper = body.addOrReplaceChild("rune_upper", CubeListBuilder.create().texOffs(0, 18).addBox(-18.25F, 0.0F, -4.5F, 36.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(80, 82).addBox(8.75F, 9.0F, -4.5F, 9.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, -35.0F, 0.5F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-2.0F, 14.0F, -5.0F));

        PartDefinition rightarm = body.addOrReplaceChild("rightarm", CubeListBuilder.create(), PartPose.offset(-25.2F, -20.0F, 3.1F));

        PartDefinition upper2 = rightarm.addOrReplaceChild("upper2", CubeListBuilder.create().texOffs(44, 36).mirror().addBox(-5.8F, -3.0F, -8.1F, 11.0F, 13.0F, 11.0F, new CubeDeformation(0.3F)).mirror(false)
                .texOffs(0, 36).mirror().addBox(-5.8F, -3.0F, -8.1F, 11.0F, 13.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition lower2 = rightarm.addOrReplaceChild("lower2", CubeListBuilder.create().texOffs(90, 0).mirror().addBox(-3.5F, -1.6667F, -3.8333F, 7.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 60).mirror().addBox(-3.5F, 9.3333F, -3.8333F, 7.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(90, 18).mirror().addBox(-3.5F, 4.3333F, 6.1667F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-0.3F, 15.6667F, -2.2667F));

        PartDefinition beam_outer = lower2.addOrReplaceChild("beam_outer", CubeListBuilder.create().texOffs(80, 104).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(4.3F, 9.3333F, 2.2667F));

        PartDefinition beam_inner = beam_outer.addOrReplaceChild("beam_inner", CubeListBuilder.create().texOffs(34, 104).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(25.5F, -20.25F, 2.6667F));

        PartDefinition upper = left_arm.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(44, 36).addBox(-5.5F, -6.5F, -5.5F, 11.0F, 13.0F, 11.0F, new CubeDeformation(0.3F))
                .texOffs(0, 36).addBox(-5.5F, -6.5F, -5.5F, 11.0F, 13.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.75F, -2.1667F));

        PartDefinition lower = left_arm.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(90, 0).addBox(-3.5F, -1.6667F, -3.8333F, 7.0F, 11.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 60).addBox(-3.5F, 9.3333F, -3.8333F, 7.0F, 8.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(90, 18).addBox(-3.5F, 4.3333F, 6.1667F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.9167F, -1.8333F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 83).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(88, 36).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, -16.5F, 0.5F));

        PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(80, 60).addBox(-4.5F, -0.5F, -4.5F, 9.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 8.4999F, 0.5826F));

        PartDefinition rightleg = root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(44, 60).addBox(-4.5F, -0.5F, -4.5F, 9.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 8.4999F, 0.5826F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (entity.getAttackState() != 28) {
            this.animateWalk(CloudGolemAnimations.walk4, limbSwing, limbSwingAmount, 1.5F, 2.5F);
            this.body.yRot *= WALK_UPPER_BODY_SWAY;
            this.runeLower.xRot *= WALK_UPPER_BODY_SWAY;
            this.runeLower.zRot *= WALK_UPPER_BODY_SWAY;
            this.runeUpper.xRot *= WALK_UPPER_BODY_SWAY;
            this.runeUpper.zRot *= WALK_UPPER_BODY_SWAY;
            this.rightArm.yRot *= WALK_UPPER_BODY_SWAY;
            this.rightArm.zRot *= WALK_UPPER_BODY_SWAY;
            this.leftArm.yRot *= WALK_UPPER_BODY_SWAY;
            this.leftArm.zRot *= WALK_UPPER_BODY_SWAY;
        }
        this.animate(entity.getAnimationState("death"), CloudGolemAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("idle"), CloudGolemAnimations.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("p2"), CloudGolemAnimations.stompp2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("cloudattackbig"), CloudGolemAnimations.BigSummon, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("blockhitdb"), CloudGolemAnimations.blockhitDB2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("blockhit"), CloudGolemAnimations.blockhit, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("explode"), CGAnims.explode2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fractureland"), CGAnims.LandFracture2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("aendcharge"), CGAnims.chargeEndAggresive, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge"), CGAnims.charge, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("precharge"), CGAnims.chargePre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("block"), CloudGolemAnimations.block, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stompleft"), CGAnims.stompsLeft, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp"), CGAnims.stomps3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fly"), CloudGolemAnimations.fly, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("land"), CloudGolemAnimations.heroFallEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("prefracturefall"), CloudGolemAnimations.heroFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fall"), CloudGolemAnimations.heroFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("laser"), CloudGolemAnimations.laser4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("mhit"), CGAnims.lightningSummon5, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -300.0F, 300.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -180.0F, 220.0F);
        this.head.yRot = pNetHeadYaw * 0.00826735F;
        this.head.xRot = pHeadPitch * ((float) Math.PI / 360);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
