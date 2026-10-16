package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantHoglinKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantHoglinSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHoglinServantModel<T extends MutantHoglinServant> extends HierarchicalModel<T> {
    private final ModelPart root;
    public final ModelPart everything;
    public final ModelPart body;
    public final ModelPart chest;
    public final ModelPart neck;
    public final ModelPart head;
    public final ModelPart leftEar;
    public final ModelPart rightEar;
    public final ModelPart tail;
    public final ModelPart leftBackLeg;
    public final ModelPart rightBackLeg;
    public final ModelPart leftFrontLeg;
    public final ModelPart leftFrontLegLower;
    public final ModelPart rightFrontLeg;
    public final ModelPart rightFrontLegLower;

    public MutantHoglinServantModel(ModelPart root) {
        this.root = root;
        this.everything = root.getChild("everything");
        this.body = this.everything.getChild("body");
        this.chest = this.body.getChild("chest");
        this.neck = this.chest.getChild("neck");
        this.head = this.neck.getChild("head");
        this.leftEar = this.head.getChild("leftEar");
        this.rightEar = this.head.getChild("rightEar");
        this.tail = this.body.getChild("tail");
        this.leftBackLeg = this.everything.getChild("leftBackLeg");
        this.rightBackLeg = this.everything.getChild("rightBackLeg");
        this.leftFrontLeg = this.everything.getChild("leftFrontLeg");
        this.leftFrontLegLower = this.leftFrontLeg.getChild("leftFrontLegLower");
        this.rightFrontLeg = this.everything.getChild("rightFrontLeg");
        this.rightFrontLegLower = this.rightFrontLeg.getChild("rightFrontLegLower");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = everything.addOrReplaceChild("body", CubeListBuilder.create().texOffs(67, 0).addBox(-11.0F, -11.0F, -36.0F, 22.0F, 22.0F, 36.0F, new CubeDeformation(0.0F)).texOffs(193, 205).addBox(0.1F, -32.0F, -36.0F, 0.0F, 21.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, 27.0F));
        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(29, 60).addBox(-15.0F, -15.0F, -24.0F, 30.0F, 28.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(192, 163).addBox(0.0F, -39.0F, -24.0F, 0.0F, 24.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -28.0F));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(15, 65).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(203, 151).addBox(-0.1F, -21.0F, -8.0F, 0.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -24.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -6.0F, -19.0F, 14.0F, 6.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 1.1781F, 0.0F, 0.0F));
        head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(47, 0).mirror().addBox(0.0F, 0.0F, -2.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(7.0F, -3.0F, -1.5F, -1.5708F, 0.0F, 0.0F));
        head.addOrReplaceChild("tusks", CubeListBuilder.create().texOffs(87, 162).mirror().addBox(-10.0F, -13.0F, -2.0F, 3.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(87, 162).addBox(7.0F, -13.0F, -2.0F, 3.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(57, 162).addBox(7.0F, -13.0F, 2.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(58, 163).mirror().addBox(-10.0F, -13.0F, 2.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -3.0F, -14.0F));
        head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(47, 0).addBox(-8.0F, 0.0F, -2.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -3.0F, -1.5F, -1.5708F, 0.0F, 0.0F));
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 64).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.5F));
        everything.addOrReplaceChild("leftBackLeg", CubeListBuilder.create().texOffs(2, 28).addBox(-2.0F, -1.0F, -5.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -21.0F, 26.0F));
        everything.addOrReplaceChild("rightBackLeg", CubeListBuilder.create().texOffs(2, 28).mirror().addBox(-10.0F, -1.0F, -5.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, -21.0F, 25.0F));
        PartDefinition leftFrontLeg = everything.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(59, 114).mirror().addBox(-6.0F, -4.0F, -6.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(21.0F, -50.0F, -12.0F));
        leftFrontLeg.addOrReplaceChild("leftFrontLegLower", CubeListBuilder.create().texOffs(109, 116).mirror().addBox(-8.0F, 0.0F, -8.0F, 16.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 26.0F, 0.0F));
        PartDefinition rightFrontLeg = everything.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(59, 114).addBox(-6.0F, -4.0F, -6.0F, 12.0F, 32.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-21.0F, -50.0F, -12.0F));
        rightFrontLeg.addOrReplaceChild("rightFrontLegLower", CubeListBuilder.create().texOffs(109, 116).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float deltaTicks;
        if (entity.walkPhaseAge < 0.0F) {
            deltaTicks = 1.0F;
        } else {
            deltaTicks = Mth.clamp(ageInTicks - entity.walkPhaseAge, 0.0F, 2.0F);
        }
        entity.walkPhaseAge = ageInTicks;

        float walkSpeedFactor = Math.min((float) entity.getDeltaMovement().horizontalDistance() * 8.5F, 6.0F);
        entity.walkPhase += walkSpeedFactor * deltaTicks;
        float walkTick = entity.walkPhase * ((float) Math.PI / 180.0F) / 20.0F;

        boolean movingOnLand = entity.onGround() && !entity.isInWaterOrBubble() && limbSwingAmount > 0.005F;

        if (entity.trotting) {
            if (limbSwingAmount < 0.06F) {
                entity.trotting = false;
            }
        } else if (limbSwingAmount > 0.12F) {
            entity.trotting = true;
        }
        boolean shouldPlayChargingAnimation = entity.charging && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayTrotAnimation = entity.trotting && movingOnLand && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayWalkAnimation = movingOnLand && !shouldPlayTrotAnimation && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayTrotAnimation && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean keyframeActive = entity.deathTime > 0
                || entity.stompAnimationTick > 0
                || entity.prepareChargeAnimationTick > 0
                || entity.kickAnimationTick > 0
                || entity.attackAnimationTick > 0
                || entity.introAnimationTick > 0
                || entity.noveltyAnimationTick > 0;
        float blendSpeed = 0.12F;
        entity.chargeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.chargeAnimationAmount, shouldPlayChargingAnimation, deltaTicks * blendSpeed);
        entity.walkAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.walkAnimationAmount, shouldPlayWalkAnimation, deltaTicks * blendSpeed);
        entity.trotAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.trotAnimationAmount, shouldPlayTrotAnimation, deltaTicks * blendSpeed);
        entity.idleAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.idleAnimationAmount, shouldPlayIdleAnimation, deltaTicks * blendSpeed);
        entity.keyframeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.keyframeAnimationAmount, keyframeActive, deltaTicks * 0.2F);
        this.animateHeadLookTarget(netHeadYaw, headPitch);

        float sineTick = SineWaveAnimationUtils.getTick(entity.tickCount, true);
        MutantHoglinSineWaveAnimations.mutantHoglinChargingAnimation(this, sineTick, 3.2F, entity.chargeAnimationAmount);
        MutantHoglinSineWaveAnimations.mutantHoglinWalkAnimation(this, walkTick, 1.2F, entity.walkAnimationAmount);
        MutantHoglinSineWaveAnimations.mutantHoglinTrotAnimation(this, walkTick, 2.0F, entity.trotAnimationAmount);
        MutantHoglinSineWaveAnimations.mutantHoglinIdleAnimation(this, sineTick, 1.0F, entity.idleAnimationAmount);

        List<ModelPart> parts = this.root().getAllParts().collect(Collectors.toList());
        float k = entity.keyframeAnimationAmount;
        boolean needBlend = k > 0.0F || keyframeActive;
        float[][] pre = null;
        if (needBlend) {
            if (entity.heldPose == null || entity.heldPose.length != parts.size()) {
                entity.heldPose = new float[parts.size()][6];
            }
            pre = new float[parts.size()][6];
            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                pre[i][0] = p.xRot;
                pre[i][1] = p.yRot;
                pre[i][2] = p.zRot;
                pre[i][3] = p.x;
                pre[i][4] = p.y;
                pre[i][5] = p.z;
            }
        }
        if (entity.deathTime > 0) {
            this.animate(entity.deathAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_DEATH, ageInTicks);
            this.animate(entity.danceAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_DANCE, ageInTicks);
        } else if (entity.stompAnimationTick > 0) {
            this.animate(entity.stompAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_STOMP, ageInTicks);
        } else if (entity.prepareChargeAnimationTick > 0) {
            this.animate(entity.prepareChargeAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_PREPARE_CHARGE, ageInTicks, 1.5F);
        } else if (entity.kickAnimationTick > 0) {
            this.animate(entity.kickAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_KICK, ageInTicks);
        } else if (entity.attackAnimationTick > 0) {
            this.animate(entity.attackAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_ATTACK, ageInTicks);
        } else if (entity.introAnimationTick > 0) {
            this.animate(entity.introAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_INTRO, ageInTicks);
        } else if (entity.noveltyAnimationTick > 0) {
            this.animate(entity.noveltyAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_NOVELTY, ageInTicks);
        }
        if (keyframeActive) {

            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                entity.heldPose[i][0] = p.xRot;
                entity.heldPose[i][1] = p.yRot;
                entity.heldPose[i][2] = p.zRot;
                entity.heldPose[i][3] = p.x;
                entity.heldPose[i][4] = p.y;
                entity.heldPose[i][5] = p.z;
            }
        }
        if (needBlend) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                p.xRot = pre[i][0] + k * (entity.heldPose[i][0] - pre[i][0]);
                p.yRot = pre[i][1] + k * (entity.heldPose[i][1] - pre[i][1]);
                p.zRot = pre[i][2] + k * (entity.heldPose[i][2] - pre[i][2]);
                p.x = pre[i][3] + k * (entity.heldPose[i][3] - pre[i][3]);
                p.y = pre[i][4] + k * (entity.heldPose[i][4] - pre[i][4]);
                p.z = pre[i][5] + k * (entity.heldPose[i][5] - pre[i][5]);
            }
        }
    }

    private void animateHeadLookTarget(float yRot, float xRot) {
        this.neck.xRot = xRot * 0.017453292F;
        this.neck.yRot = yRot * 0.017453292F;
    }

    public ModelPart root() {
        return this.root;
    }
}
