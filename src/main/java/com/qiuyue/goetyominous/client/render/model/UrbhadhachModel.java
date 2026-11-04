package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.UrbhadhachAnimations;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractUrbhadhach;
import net.minecraft.client.animation.AnimationDefinition;
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

@OnlyIn(Dist.CLIENT)
public class UrbhadhachModel<T extends AbstractUrbhadhach> extends HierarchicalModel<T> {
    private static final float DEG_TO_RAD = (float) Math.PI / 180F;
    private static final float MAX_HEAD_PITCH = 30.0F * DEG_TO_RAD;
    private static final float ACTION_FADE_OUT = 0.3F;
    private static final float SLEEP_BLEND_SECONDS = 0.4F;
    private static final Vector3f VECTOR_CACHE = new Vector3f();
    private final ModelPart root;
    private final ModelPart bear;
    private final ModelPart head;

    public UrbhadhachModel(ModelPart root) {
        this.root = root;
        this.bear = root.getChild("bear");
        ModelPart allbody = this.bear.getChild("allbody");
        ModelPart bodyHead = allbody.getChild("body+head");
        this.head = bodyHead.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bear = partdefinition.addOrReplaceChild("bear", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition allbody = bear.addOrReplaceChild("allbody", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -9.0F, 8.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition bodyHead = allbody.addOrReplaceChild("body+head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -20.0F, 0.0F, 16.0F, 11.0F, 15.0F, new CubeDeformation(0.0F))
                        .texOffs(62, 0).addBox(-5.0F, -9.0F, 0.0F, 14.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = bodyHead.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 26).addBox(-6.5F, -6.0F, -6.0F, 13.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 42).addBox(-4.5F, -2.0F, -9.0F, 9.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-7.5F, -7.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).mirror().addBox(5.5F, -7.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(88, 31).addBox(-6.5F, -6.0F, -2.0F, 13.0F, 10.0F, 2.0F, new CubeDeformation(0.2F))
                        .texOffs(99, 43).addBox(-1.5F, 4.2F, -1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -20.0F, 5.0F, -1.5708F, 0.0F, 0.0F));

        head.addOrReplaceChild("eye", CubeListBuilder.create()
                        .texOffs(0, 3).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.5F, -2.0F, -6.01F));

        head.addOrReplaceChild("eye2", CubeListBuilder.create()
                        .texOffs(0, 3).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.5F, -2.0F, -6.01F));

        head.addOrReplaceChild("jaw", CubeListBuilder.create()
                        .texOffs(4, 50).addBox(-4.5F, -1.0F, -3.0F, 9.0F, 3.0F, 4.0F, new CubeDeformation(-0.1F)),
                PartPose.offset(0.0F, 2.0F, -6.0F));

        allbody.addOrReplaceChild("leg2", CubeListBuilder.create()
                        .texOffs(60, 43).addBox(-3.0F, -3.0F, -10.0F, 6.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.5F, -16.0F, 1.0F));

        allbody.addOrReplaceChild("leg3", CubeListBuilder.create()
                        .texOffs(60, 43).mirror().addBox(-3.0F, -3.0F, -10.0F, 6.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(6.5F, -16.0F, 1.0F));

        bear.addOrReplaceChild("leg0", CubeListBuilder.create()
                        .texOffs(64, 26).addBox(-2.5F, 0.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -10.0F, 7.5F));

        bear.addOrReplaceChild("leg1", CubeListBuilder.create()
                        .texOffs(64, 26).mirror().addBox(-2.5F, 0.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(4.0F, -10.0F, 7.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        int animation = entity.getAnimationState();
        if (animation == AbstractUrbhadhach.ANIM_NONE) {
            float locomotion = 1.0F - this.animateFinishedAction(entity, ageInTicks);
            entity.idleAnimationState.startIfStopped(entity.tickCount);
            this.animateWalkWeighted(limbSwing, limbSwingAmount, locomotion);
            this.animateWeighted(entity.idleAnimationState, UrbhadhachAnimations.IDLE, ageInTicks, locomotion);
            this.head.zRot -= netHeadYaw * DEG_TO_RAD * locomotion;
            this.head.xRot += Mth.clamp(headPitch * DEG_TO_RAD, -MAX_HEAD_PITCH, MAX_HEAD_PITCH) * locomotion;
            return;
        }
        switch (animation) {
            case AbstractUrbhadhach.ANIM_BITE -> this.animate(entity.biteAnimationState, UrbhadhachAnimations.BITE_ATTACK, ageInTicks);
            case AbstractUrbhadhach.ANIM_CLAW -> this.animate(entity.clawAnimationState, UrbhadhachAnimations.CLAW_ATTACK, ageInTicks);
            case AbstractUrbhadhach.ANIM_SIT -> this.animate(entity.sitAnimationState, UrbhadhachAnimations.CROUCH, ageInTicks);
            case AbstractUrbhadhach.ANIM_SLEEP -> this.animateSleep(entity, ageInTicks);
            case AbstractUrbhadhach.ANIM_WAKE_UP -> this.animate(entity.wakeUpAnimationState, UrbhadhachAnimations.WAKE_UP, ageInTicks);
            case AbstractUrbhadhach.ANIM_ROAR -> this.animate(entity.roarAnimationState, UrbhadhachAnimations.ROAR, ageInTicks);
            case AbstractUrbhadhach.ANIM_DASH_START -> this.animate(entity.dashStartAnimationState, UrbhadhachAnimations.DASH_START, ageInTicks);
            case AbstractUrbhadhach.ANIM_DASH_LOOP -> this.animate(entity.dashLoopAnimationState, UrbhadhachAnimations.DASH_LOOP, ageInTicks);
        }
    }

    private void animateSleep(T entity, float ageInTicks) {
        entity.sleepAnimationState.updateTime(ageInTicks, 1.0F);
        entity.sleepAnimationState.ifStarted(state -> {
            long elapsed = state.getAccumulatedTime();
            float blend = Mth.clamp(elapsed / (SLEEP_BLEND_SECONDS * 1000.0F), 0.0F, 1.0F);
            if (blend < 1.0F) {
                long crouchEnd = (long) (UrbhadhachAnimations.CROUCH.lengthInSeconds() * 1000.0F) + 1L;
                KeyframeAnimations.animate(this, UrbhadhachAnimations.CROUCH, crouchEnd, 1.0F - blend, VECTOR_CACHE);
            }
            KeyframeAnimations.animate(this, UrbhadhachAnimations.SLEEP, elapsed, blend, VECTOR_CACHE);
        });
    }

    private float animateFinishedAction(T entity, float ageInTicks) {
        int endTick = entity.getAnimationEndTick();
        long endTime = entity.getAnimationEndTime();
        if (endTick < 0 || endTime < 0L) {
            return 0.0F;
        }
        float elapsed = (ageInTicks - endTick) / 20.0F;
        if (elapsed >= ACTION_FADE_OUT) {
            return 0.0F;
        }
        float weight = 1.0F - Math.max(elapsed, 0.0F) / ACTION_FADE_OUT;
        this.animateFinished(entity.biteAnimationState, UrbhadhachAnimations.BITE_ATTACK, endTime, weight);
        this.animateFinished(entity.clawAnimationState, UrbhadhachAnimations.CLAW_ATTACK, endTime, weight);
        this.animateFinished(entity.sitAnimationState, UrbhadhachAnimations.CROUCH, endTime, weight);
        this.animateFinished(entity.sleepAnimationState, UrbhadhachAnimations.SLEEP, endTime, weight);
        this.animateFinished(entity.wakeUpAnimationState, UrbhadhachAnimations.WAKE_UP, endTime, weight);
        this.animateFinished(entity.roarAnimationState, UrbhadhachAnimations.ROAR, endTime, weight);
        this.animateFinished(entity.dashStartAnimationState, UrbhadhachAnimations.DASH_START, endTime, weight);
        this.animateFinished(entity.dashLoopAnimationState, UrbhadhachAnimations.DASH_LOOP, endTime, weight);
        return weight;
    }

    private void animateFinished(AnimationState state, AnimationDefinition definition, long time, float weight) {
        if (state.isStarted()) {
            KeyframeAnimations.animate(this, definition, time, weight, VECTOR_CACHE);
        }
    }

    private void animateWeighted(AnimationState state, AnimationDefinition definition, float ageInTicks, float weight) {
        state.updateTime(ageInTicks, 1.0F);
        if (weight <= 0.0F) {
            return;
        }
        state.ifStarted(animation -> KeyframeAnimations.animate(this, definition, animation.getAccumulatedTime(), weight, VECTOR_CACHE));
    }

    private void animateWalkWeighted(float limbSwing, float limbSwingAmount, float weight) {
        float walkWeight = Math.min(limbSwingAmount * 2.0F, 1.0F) * weight;
        if (walkWeight <= 0.0F) {
            return;
        }
        KeyframeAnimations.animate(this, UrbhadhachAnimations.WALK, (long) (limbSwing * 90.0F), walkWeight, VECTOR_CACHE);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bear.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
