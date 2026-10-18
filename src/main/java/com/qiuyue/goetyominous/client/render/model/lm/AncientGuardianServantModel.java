package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.AncientGuardianServant;
import net.miauczel.legendary_monsters.entity.animations.AncientGuardianAnimations;
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
public class AncientGuardianServantModel<T extends AncientGuardianServant> extends HierarchicalModel<T> {
    private static final AnimationDefinition SLEEP_IN = reverse(AncientGuardianAnimations.awaken);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart head;

    public AncientGuardianServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = root.getChild("root").getChild("bone").getChild("body").getChild("bone3");
    }

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
                KeyframeAnimations.animate(this, AncientGuardianAnimations.sleep,
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
        awake.ifStarted(state -> KeyframeAnimations.animate(this, AncientGuardianAnimations.awaken,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset((float)0.0f, (float)-13.0f, (float)0.0f));
        PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(90, 0).addBox(-4.5f, -0.5f, -4.5f, 9.0f, 11.0f, 9.0f, new CubeDeformation(0.0f)), PartPose.offset((float)6.5f, (float)19.0f, (float)0.5f));
        PartDefinition rightleg = root.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(92, 20).addBox(-4.5f, -0.5f, -4.5f, 9.0f, 11.0f, 9.0f, new CubeDeformation(0.0f)), PartPose.offset((float)-6.5f, (float)19.0f, (float)0.5f));
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset((float)0.0f, (float)19.0f, (float)0.5f));
        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0556f, -18.6667f, -10.7778f, 26.0f, 18.0f, 19.0f, new CubeDeformation(0.0f)).texOffs(0, 57).addBox(-13.0556f, -30.6667f, -6.7778f, 26.0f, 12.0f, 0.0f, new CubeDeformation(0.0f)).texOffs(54, 37).addBox(-9.0556f, -30.6667f, -10.7778f, 0.0f, 12.0f, 19.0f, new CubeDeformation(0.0f)).texOffs(54, 37).mirror().addBox(8.9444f, -30.6667f, -10.7778f, 0.0f, 12.0f, 19.0f, new CubeDeformation(0.0f)).mirror(false).texOffs(52, 68).addBox(-13.0556f, -30.6667f, 4.2222f, 26.0f, 12.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offset((float)0.0556f, (float)-5.8333f, (float)1.2778f));
        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 80).addBox(-5.0f, -5.0f, -5.0f, 10.0f, 10.0f, 10.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-0.0556f, (float)-9.6667f, (float)8.2222f, (float)-0.7418f, (float)0.0f, (float)0.0f));
        PartDefinition rightarm = body.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(76, 80).addBox(-11.0f, 12.5f, -4.5f, 9.0f, 13.0f, 9.0f, new CubeDeformation(0.0f)).texOffs(0, 69).addBox(-11.0f, -6.5f, -4.5f, 9.0f, 16.0f, 9.0f, new CubeDeformation(0.0f)).texOffs(48, 100).mirror().addBox(-8.0f, -15.5f, -3.5f, 3.0f, 9.0f, 7.0f, new CubeDeformation(0.0f)).mirror(false).texOffs(20, 100).addBox(-10.0f, 9.5f, -3.5f, 7.0f, 3.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(96, 102).addBox(-2.0f, -3.5f, -3.5f, 2.0f, 7.0f, 7.0f, new CubeDeformation(0.0f)), PartPose.offset((float)-13.0556f, (float)-13.1667f, (float)-1.2778f));
        PartDefinition l = rightarm.addOrReplaceChild("l", CubeListBuilder.create(), PartPose.offset((float)-1.75f, (float)22.25f, (float)0.0f));
        PartDefinition cube_r2 = l.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 94).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(36, 69).addBox(-2.5f, -5.0f, -5.5f, 5.0f, 7.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-3.75f, (float)5.25f, (float)0.0f, (float)0.0f, (float)-1.5708f, (float)0.0f));
        PartDefinition r = rightarm.addOrReplaceChild("r", CubeListBuilder.create(), PartPose.offset((float)-11.25f, (float)22.25f, (float)0.0f));
        PartDefinition cube_r3 = r.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 94).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(36, 69).addBox(-2.5f, -5.0f, -5.5f, 5.0f, 7.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)3.75f, (float)5.25f, (float)0.0f, (float)0.0f, (float)1.5708f, (float)0.0f));
        PartDefinition leftarm = body.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(0, 69).addBox(1.0f, -6.5f, -4.5f, 9.0f, 16.0f, 9.0f, new CubeDeformation(0.0f)).texOffs(48, 100).addBox(4.0f, -15.5f, -3.5f, 3.0f, 9.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(68, 108).mirror().addBox(1.0f, 11.5f, -3.5f, 2.0f, 7.0f, 7.0f, new CubeDeformation(0.0f)).mirror(false).texOffs(68, 108).addBox(8.0f, 11.5f, -3.5f, 2.0f, 7.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(92, 57).addBox(1.0f, 9.5f, -3.5f, 9.0f, 2.0f, 7.0f, new CubeDeformation(0.0f)).texOffs(104, 66).addBox(-1.0f, -3.5f, -3.5f, 2.0f, 7.0f, 7.0f, new CubeDeformation(0.0f)), PartPose.offset((float)13.9444f, (float)-13.1667f, (float)-1.2778f));
        PartDefinition anchor = leftarm.addOrReplaceChild("anchor", CubeListBuilder.create().texOffs(0, 94).addBox(-2.5f, -2.5f, -2.5f, 5.0f, 20.0f, 5.0f, new CubeDeformation(0.0f)).texOffs(68, 102).addBox(-5.5f, -1.5f, -1.5f, 11.0f, 3.0f, 3.0f, new CubeDeformation(0.0f)).texOffs(112, 80).addBox(-2.0f, 12.5f, 2.5f, 4.0f, 4.0f, 3.0f, new CubeDeformation(0.0f)).texOffs(20, 110).addBox(-2.0f, 8.5f, 5.5f, 4.0f, 8.0f, 4.0f, new CubeDeformation(0.0f)).texOffs(112, 87).addBox(-2.0f, 8.5f, 9.5f, 4.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offset((float)5.5f, (float)15.0f, (float)0.0f));
        PartDefinition anchor_r1 = anchor.addOrReplaceChild("anchor_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-3.0f, 3.5f, -7.0f, 4.0f, 4.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.0f, (float)9.0f, (float)-9.5f, (float)-3.1416f, (float)0.0f, (float)3.1416f));
        PartDefinition anchor_r2 = anchor.addOrReplaceChild("anchor_r2", CubeListBuilder.create().texOffs(20, 110).addBox(-3.0f, -0.5f, -9.0f, 4.0f, 8.0f, 4.0f, new CubeDeformation(0.0f)).texOffs(112, 87).addBox(-3.0f, -0.5f, -5.0f, 4.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.0f, (float)9.0f, (float)-14.5f, (float)-3.1416f, (float)0.0f, (float)3.1416f));
        PartDefinition bone3 = body.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(92, 40).addBox(-6.0f, -7.0f, -3.5f, 12.0f, 14.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.offset((float)-0.0556f, (float)-9.6667f, (float)-10.2778f));
        PartDefinition ripcage = bone3.addOrReplaceChild("ripcage", CubeListBuilder.create(), PartPose.offset((float)3.0f, (float)-5.0f, (float)-7.5f));
        PartDefinition ripcageright = ripcage.addOrReplaceChild("ripcageright", CubeListBuilder.create().texOffs(86, 108).addBox(0.0f, -5.0f, 0.0f, 4.0f, 11.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-7.0f, (float)4.0f, (float)4.0f, (float)0.0f, (float)0.2182f, (float)0.0f));
        PartDefinition ripcageleft = ripcage.addOrReplaceChild("ripcageleft", CubeListBuilder.create(), PartPose.offset((float)1.0f, (float)4.0f, (float)4.0f));
        PartDefinition ripcageleft_r1 = ripcageleft.addOrReplaceChild("ripcageleft_r1", CubeListBuilder.create().texOffs(36, 110).addBox(-2.0f, -5.5f, 0.0f, 4.0f, 11.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation((float)-1.9526f, (float)0.5f, (float)-0.4329f, (float)0.0f, (float)-0.2182f, (float)0.0f));
        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 37).addBox(-7.0f, -6.5f, -6.5f, 14.0f, 7.0f, 13.0f, new CubeDeformation(0.0f)), PartPose.offset((float)0.0f, (float)0.0f, (float)0.0f));
        return LayerDefinition.create((MeshDefinition)meshdefinition, (int)128, (int)128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (entity.getAttackState() != 6 && !entity.isSleep()) {
            this.animateWalk(AncientGuardianAnimations.walk4, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        if (!entity.isSleep()) {
            this.applyHeadRotation(netHeadYaw, headPitch);
        }
        this.animate(entity.getAnimationState("death"), AncientGuardianAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("idle"), AncientGuardianAnimations.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("attackarmright"), AncientGuardianAnimations.attack4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackarmleft"), AncientGuardianAnimations.attack3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackcombo"), AncientGuardianAnimations.attackcombo1, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab"), AncientGuardianAnimations.grab, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("runpre"), AncientGuardianAnimations.jump2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("run"), AncientGuardianAnimations.jump3, ageInTicks, 2.0F);
        this.animate(entity.getAnimationState("postrun"), AncientGuardianAnimations.jump4, ageInTicks, 1.0F);
        this.root.xScale = 1.25F;
        this.root.yScale = 1.25F;
        this.root.zScale = 1.25F;
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
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
