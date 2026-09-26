package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.AnnihilationPursuer.AnnihilationPursuerAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.AnnihilationPursuer.AnnihilationPursuerAnimations2;
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
public class AnnihilationPursuerServantModel<T extends AnnihilationPursuerServant> extends HierarchicalModel<T> {

    private static final AnimationDefinition SLEEP_IN = reverse(AnnihilationPursuerAnimations.awaken);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart coreBody;
    private final ModelPart upperBody;
    private final ModelPart jaw;
    private final ModelPart leftUpperArm;
    private final ModelPart forearm2;
    private final ModelPart fist2;

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
                KeyframeAnimations.animate(this, AnnihilationPursuerAnimations.sleep,
                        elapsed - SLEEP_IN_MILLIS, 1.0F, ANIMATION_VECTOR_CACHE);
            }
        });
    }

    private void animateAwaken(T entity, float ageInTicks) {
        AnimationState awaken = entity.getAnimationState("awaken");
        AnimationState sleep = entity.getAnimationState("sleep");
        awaken.updateTime(ageInTicks, 1.0F);
        long sleptMillis = sleep.isStarted() ? 0L : sleep.getAccumulatedTime();
        long skippedMillis = sleptMillis > 0L ? Math.max(0L, SLEEP_IN_MILLIS - sleptMillis) : 0L;
        awaken.ifStarted(state -> KeyframeAnimations.animate(this, AnnihilationPursuerAnimations.awaken,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public AnnihilationPursuerServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.coreBody = this.root.getChild("coreBody");
        this.upperBody = this.coreBody.getChild("upperBody");
        this.jaw = this.upperBody.getChild("neck").getChild("jaw");
        this.leftUpperArm = this.upperBody.getChild("leftUpperArm");
        this.forearm2 = this.leftUpperArm.getChild("forearm2");
        this.fist2 = this.forearm2.getChild("fist2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.5F, -19.0F, -5.0F));
        PartDefinition coreBody = root.addOrReplaceChild("coreBody", CubeListBuilder.create().texOffs(106, 94).addBox(-5.5F, -20.0F, -5.5F, 9.0F, 22.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 11.0F, 3.5F));
        PartDefinition upperBody = coreBody.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(54, 67).addBox(-10.5F, -11.0F, -7.5F, 21.0F, 12.0F, 15.0F, new CubeDeformation(0.0F)).texOffs(160, 82).mirror().addBox(-7.5F, -9.0F, 7.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(32, 117).mirror().addBox(-6.5F, -20.0F, 9.5F, 0.0F, 26.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(32, 117).addBox(6.5F, -20.0F, 9.5F, 0.0F, 26.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(160, 82).addBox(5.5F, -9.0F, 7.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -19.0F, -1.0F));
        PartDefinition neck = upperBody.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -9.9063F, -1.0774F, 0.2618F, 0.0F, 0.0F));
        neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(142, 91).addBox(-2.5F, -8.0F, -3.0F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -5.0937F, 3.5774F, -0.4363F, 0.0F, 0.0F));
        PartDefinition jaw = neck.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0858F, -10.7437F, 7.6345F, 0.2182F, 0.0F, 0.0F));
        jaw.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(104, 22).addBox(-7.5F, -7.5F, -7.5F, 15.0F, 7.0F, 15.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.0858F, 1.75F, -4.0429F, 0.0F, -0.7854F, 0.0F));
        PartDefinition face = jaw.addOrReplaceChild("face", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 5.0F));
        face.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(126, 64).mirror().addBox(-22.5F, -7.5F, -7.5F, 15.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.0858F, 1.75F, -9.0429F, 0.0F, 0.7854F, 0.0F));
        face.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(126, 64).addBox(7.5F, -7.5F, -7.5F, 15.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(104, 0).addBox(-7.5F, -7.5F, -7.5F, 15.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0858F, 1.75F, -9.0429F, 0.0F, -0.7854F, 0.0F));
        PartDefinition toungue = jaw.addOrReplaceChild("toungue", CubeListBuilder.create(), PartPose.offset(-0.4142F, -2.8882F, -3.1701F));
        toungue.addOrReplaceChild("toungue_r1", CubeListBuilder.create().texOffs(166, 97).addBox(-2.5F, -8.0F, -5.1F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.5383F, -0.887F, -0.6981F, 0.0F, 0.0F));
        PartDefinition leftUpperArm = upperBody.addOrReplaceChild("leftUpperArm", CubeListBuilder.create().texOffs(104, 44).mirror().addBox(-0.5F, -3.5F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(138, 142).mirror().addBox(0.5F, 5.5F, -3.5F, 6.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(160, 74).mirror().addBox(9.5F, -6.5F, 4.0F, 7.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(160, 74).mirror().addBox(9.5F, -6.5F, -4.0F, 7.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(10.0F, -8.5F, 0.5F, 0.0F, 0.0F, -0.4363F));
        PartDefinition forearm2 = leftUpperArm.addOrReplaceChild("forearm2", CubeListBuilder.create().texOffs(0, 117).mirror().addBox(-3.0F, -0.5F, -4.5F, 7.0F, 18.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(52, 134).addBox(4.0F, -3.5F, 0.0F, 13.0F, 25.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 19.0F, 0.0F));
        forearm2.addOrReplaceChild("fist2", CubeListBuilder.create().texOffs(24, 153).mirror().addBox(-3.5F, 0.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 17.5F, 0.0F));
        PartDefinition neck3 = leftUpperArm.addOrReplaceChild("neck3", CubeListBuilder.create(), PartPose.offset(-10.0F, -4.4063F, 0.4226F));
        neck3.addOrReplaceChild("head3", CubeListBuilder.create(), PartPose.offset(0.0858F, -6.3437F, 5.5345F));
        PartDefinition rightUpperArm = upperBody.addOrReplaceChild("rightUpperArm", CubeListBuilder.create().texOffs(104, 44).addBox(-9.5F, -3.5F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(138, 142).addBox(-6.5F, 5.5F, -3.5F, 6.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(160, 74).addBox(-16.5F, -6.5F, 4.0F, 7.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(160, 74).addBox(-16.5F, -6.5F, -4.0F, 7.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -8.5F, 0.5F, 0.0F, 0.0F, 0.4363F));
        PartDefinition forearm5 = rightUpperArm.addOrReplaceChild("forearm5", CubeListBuilder.create().texOffs(0, 117).addBox(-4.0F, -0.5F, -4.5F, 7.0F, 18.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(52, 134).mirror().addBox(-17.0F, -3.5F, 0.0F, 13.0F, 25.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.5F, 19.0F, 0.0F));
        PartDefinition fist5 = forearm5.addOrReplaceChild("fist5", CubeListBuilder.create().texOffs(24, 153).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.5F, 0.0F));
        PartDefinition sword = fist5.addOrReplaceChild("sword", CubeListBuilder.create(), PartPose.offsetAndRotation(1.6215F, 2.8648F, -1.0F, 0.0F, 0.0F, 0.9599F));
        sword.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(98, 149).addBox(1.2052F, -0.3418F, 4.0F, 0.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)).texOffs(126, 74).addBox(-0.2948F, 3.6582F, -10.0F, 3.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(1.2052F, -4.0918F, -60.0F, 0.0F, 15.0F, 52.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1215F, -0.1148F, 4.5F, 0.0F, 0.0F, -0.9599F));
        sword.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(142, 114).addBox(-0.7199F, 7.9506F, -5.7469F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1215F, -0.1148F, 4.5F, -0.7854F, 0.0F, -0.9599F));
        PartDefinition neck2 = rightUpperArm.addOrReplaceChild("neck2", CubeListBuilder.create(), PartPose.offset(10.0F, -4.4063F, 0.4226F));
        neck2.addOrReplaceChild("head2", CubeListBuilder.create(), PartPose.offset(-0.0858F, -6.3437F, 5.5345F));
        PartDefinition rightLowerArm = coreBody.addOrReplaceChild("rightLowerArm", CubeListBuilder.create().texOffs(138, 125).addBox(-7.5F, -3.5F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(144, 44).addBox(-6.5F, 5.5F, -3.5F, 6.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -10.5F, -1.0F, 0.0F, 0.0F, 0.4363F));
        PartDefinition forearm4 = rightLowerArm.addOrReplaceChild("forearm4", CubeListBuilder.create().texOffs(106, 125).addBox(-4.0F, -0.5F, -4.5F, 7.0F, 15.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 17.0F, 0.0F));
        PartDefinition fist4 = forearm4.addOrReplaceChild("fist4", CubeListBuilder.create().texOffs(24, 153).addBox(-3.5F, 0.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));
        fist4.addOrReplaceChild("small_sword2", CubeListBuilder.create().texOffs(54, 94).addBox(7.0E-4F, -8.6667F, -32.3333F, 0.0F, 14.0F, 26.0F, new CubeDeformation(0.0F)).texOffs(52, 159).addBox(-1.5003F, -0.6667F, -6.3333F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(120, 149).addBox(-3.0E-4F, -2.6667F, 2.6667F, 0.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5003F, 3.1667F, 1.8333F));
        PartDefinition leftLowerArm = coreBody.addOrReplaceChild("leftLowerArm", CubeListBuilder.create().texOffs(138, 125).mirror().addBox(-0.5F, -3.5F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(144, 44).mirror().addBox(0.5F, 5.5F, -3.5F, 6.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -10.5F, -1.0F, 0.0F, 0.0F, -0.4363F));
        PartDefinition forearm3 = leftLowerArm.addOrReplaceChild("forearm3", CubeListBuilder.create().texOffs(106, 125).mirror().addBox(-3.0F, -0.5F, -4.5F, 7.0F, 15.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.5F, 17.0F, 0.0F));
        PartDefinition fist3 = forearm3.addOrReplaceChild("fist3", CubeListBuilder.create(), PartPose.offset(0.0F, 14.0F, 0.0F));
        fist3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(24, 153).mirror().addBox(-4.0F, -1.5F, -4.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, 2.0F, 1.0F, 0.0F, 0.0F, 0.1309F));
        PartDefinition shield = forearm3.addOrReplaceChild("shield", CubeListBuilder.create(), PartPose.offset(5.75F, 7.0F, 0.0F));
        shield.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(160, 114).addBox(0.25F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 67).addBox(-1.75F, -12.5F, -12.5F, 2.0F, 25.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 144).addBox(-2.5F, 0.0F, -2.5F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5F, 10.0F, 2.35F));
        right_leg.addOrReplaceChild("lower_leg2", CubeListBuilder.create().texOffs(78, 134).addBox(-2.0F, -2.0F, -2.0F, 5.0F, 22.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 1.65F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 144).mirror().addBox(-3.5F, 0.0F, -2.5F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.5F, 10.0F, 2.35F));
        left_leg.addOrReplaceChild("lower_leg3", CubeListBuilder.create().texOffs(78, 134).mirror().addBox(-3.0F, -2.0F, -2.0F, 5.0F, 22.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 13.0F, 1.65F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void translateModel(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.coreBody.translateAndRotate(poseStack);
        this.upperBody.translateAndRotate(poseStack);
        this.leftUpperArm.translateAndRotate(poseStack);
        this.forearm2.translateAndRotate(poseStack);
        this.fist2.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.1F, 0.0F);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.jaw.yRot = Mth.clamp(netHeadYaw, -30.0F, 30.0F) * ((float) Math.PI / 180);
        this.jaw.xRot = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float) Math.PI / 180);
        if (entity.getAttackState() == 0) {
            this.animateWalk(AnnihilationPursuerAnimations.walk, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        this.animate(entity.getAnimationState("idle"), AnnihilationPursuerAnimations.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwaken(entity, ageInTicks);
        this.animate(entity.getAnimationState("stomp_combo"), AnnihilationPursuerAnimations.stompSlashCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_combo_end"), AnnihilationPursuerAnimations2.stompSlashEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp_combo_teleport_end"), AnnihilationPursuerAnimations2.stompSlashTeleportEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash_from"), AnnihilationPursuerAnimations.singleSlashFrom, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash_from_parry"), AnnihilationPursuerAnimations2.singleSlashFromParry3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash_from_fail"), AnnihilationPursuerAnimations.singleSlashFromFail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash"), AnnihilationPursuerAnimations.singleSlashCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash_double"), AnnihilationPursuerAnimations.singleSlashDouble, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("single_slash_fail"), AnnihilationPursuerAnimations.singleSlashEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_pre"), AnnihilationPursuerAnimations2.grabPre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_success"), AnnihilationPursuerAnimations.grabSuccess, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_fail"), AnnihilationPursuerAnimations.grabFail, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_slam"), AnnihilationPursuerAnimations2.teleport_sword_slam2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_chase"), AnnihilationPursuerAnimations2.teleport_chase, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("teleport_chase_next"), AnnihilationPursuerAnimations2.grabPreChase, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("buckshot"), AnnihilationPursuerAnimations2.bucklerShootCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("buckshot_end"), AnnihilationPursuerAnimations2.BucklerShootEnd2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("buckshot_tp"), AnnihilationPursuerAnimations2.BucklerShootTp, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), AnnihilationPursuerAnimations2.death, ageInTicks, 1.0F);
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
