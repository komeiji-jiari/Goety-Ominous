package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.AnnihilationPursuer.AnnihilationPursuerAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.AnnihilationPursuer.AnnihilationPursuerAnimations2;
import net.miauczel.legendary_monsters.entity.client.Model.AnnihilationPursuerModel;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
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
        return AnnihilationPursuerModel.createBodyLayer();
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
