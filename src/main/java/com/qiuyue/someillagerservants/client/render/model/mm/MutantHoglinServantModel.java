package com.qiuyue.someillagerservants.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantHoglinKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.qiuyue.someillagerservants.client.render.model.animation.mm.MutantHoglinSineWaveAnimations;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.MutantHoglinServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
        Vec3 velocity = entity.getDeltaMovement();
        float speed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
        float f = Math.min((float)entity.getDeltaMovement().lengthSqr() * 50.0F, 8.0F);
        boolean shouldPlayChargingAnimation = entity.charging && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayWalkAnimation = MiscUtils.isMovingOnLand(entity) && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        this.animateHeadLookTarget(netHeadYaw, headPitch);
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
        } else {
            MutantHoglinSineWaveAnimations.mutantHoglinChargingAnimation(this, SineWaveAnimationUtils.getTick(entity.tickCount, true), speed * 8.0F, shouldPlayChargingAnimation ? 1.0F : 0.0F);
            MutantHoglinSineWaveAnimations.mutantHoglinWalkAnimation(this, SineWaveAnimationUtils.getTick(entity.tickCount, true), f, shouldPlayWalkAnimation ? 1.0F : 0.0F);
            MutantHoglinSineWaveAnimations.mutantHoglinIdleAnimation(this, SineWaveAnimationUtils.getTick(entity.tickCount, true), 1.0F, shouldPlayIdleAnimation ? 1.0F : 0.0F);
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
