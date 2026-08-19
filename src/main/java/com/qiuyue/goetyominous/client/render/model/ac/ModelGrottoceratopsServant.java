package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ModelGrottoceratopsServant extends AdvancedEntityModel<GrottoceratopsServant> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox bodySpikes;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tailSpike;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail2Spike;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox grassBunch;
    private final AdvancedModelBox grass;
    private final AdvancedModelBox grass5;
    private final AdvancedModelBox grass2;
    private final AdvancedModelBox grassBunch2;
    private final AdvancedModelBox grass3;
    private final AdvancedModelBox grass6;
    private final AdvancedModelBox grass4;
    private final ModelAnimator animator;

    public ModelGrottoceratopsServant() {
        this.texWidth = 256;
        this.texHeight = 256;

        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.body.setTextureOffset(0, 0).addBox(-11.0F, -14.0F, -17.5F, 22.0F, 30.0F, 35.0F, 0.0F, false);

        this.bodySpikes = new AdvancedModelBox(this);
        this.bodySpikes.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.body.addChild(this.bodySpikes);
        this.bodySpikes.setTextureOffset(0, 22).addBox(0.0F, -15.5F, -21.5F, 0.0F, 31.0F, 43.0F, 0.0F, false);

        this.rleg = new AdvancedModelBox(this);
        this.rleg.setRotationPoint(-11.0F, 5.0F, 9.5F);
        this.body.addChild(this.rleg);
        this.rleg.setTextureOffset(117, 17).addBox(-5.0F, -2.0F, -8.0F, 9.0F, 15.0F, 12.0F, 0.0F, true);

        this.rleg2 = new AdvancedModelBox(this);
        this.rleg2.setRotationPoint(-0.5F, 8.5F, 2.0F);
        this.rleg.addChild(this.rleg2);
        this.rleg2.setTextureOffset(126, 44).addBox(-3.0F, -1.5F, -4.0F, 6.0F, 13.0F, 8.0F, 0.0F, true);

        this.rfoot = new AdvancedModelBox(this);
        this.rfoot.setRotationPoint(0.0F, 11.5F, -0.5F);
        this.rleg2.addChild(this.rfoot);
        this.rfoot.setTextureOffset(114, 0).addBox(-4.0F, 0.0F, -6.5F, 8.0F, 2.0F, 11.0F, 0.0F, true);

        this.lleg = new AdvancedModelBox(this);
        this.lleg.setRotationPoint(11.0F, 5.0F, 9.5F);
        this.body.addChild(this.lleg);
        this.lleg.setTextureOffset(117, 17).addBox(-4.0F, -2.0F, -8.0F, 9.0F, 15.0F, 12.0F, 0.0F, false);

        this.lleg2 = new AdvancedModelBox(this);
        this.lleg2.setRotationPoint(0.5F, 8.5F, 2.0F);
        this.lleg.addChild(this.lleg2);
        this.lleg2.setTextureOffset(126, 44).addBox(-3.0F, -1.5F, -4.0F, 6.0F, 13.0F, 8.0F, 0.0F, false);

        this.lfoot = new AdvancedModelBox(this);
        this.lfoot.setRotationPoint(0.5F, 20.0F, 1.5F);
        this.lleg.addChild(this.lfoot);
        this.lfoot.setTextureOffset(114, 0).addBox(-4.0F, 0.0F, -6.5F, 8.0F, 2.0F, 11.0F, 0.0F, false);

        this.rarm = new AdvancedModelBox(this);
        this.rarm.setRotationPoint(-10.0F, 4.0F, -12.0F);
        this.body.addChild(this.rarm);
        this.rarm.setTextureOffset(0, 0).addBox(-4.0F, -2.0F, -4.5F, 6.0F, 25.0F, 9.0F, 0.0F, true);
        this.rarm.setTextureOffset(79, 7).addBox(-1.0F, 17.0F, -7.5F, 3.0F, 3.0F, 3.0F, 0.0F, true);
        this.rarm.setTextureOffset(105, 31).addBox(-1.0F, 20.0F, -7.5F, 3.0F, 2.0F, 2.0F, 0.0F, true);

        this.larm = new AdvancedModelBox(this);
        this.larm.setRotationPoint(10.0F, 4.0F, -12.0F);
        this.body.addChild(this.larm);
        this.larm.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -4.5F, 6.0F, 25.0F, 9.0F, 0.0F, false);
        this.larm.setTextureOffset(79, 7).addBox(-2.0F, 17.0F, -7.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        this.larm.setTextureOffset(105, 31).addBox(-2.0F, 20.0F, -7.5F, 3.0F, 2.0F, 2.0F, 0.0F, false);

        this.tail = new AdvancedModelBox(this);
        this.tail.setRotationPoint(0.0F, -1.0F, 16.5F);
        this.body.addChild(this.tail);
        this.tail.setTextureOffset(65, 75).addBox(-6.0F, -5.0F, -1.0F, 12.0F, 12.0F, 21.0F, 0.0F, false);

        this.tailSpike = new AdvancedModelBox(this);
        this.tailSpike.setRotationPoint(0.0F, -5.0F, 10.5F);
        this.tail.addChild(this.tailSpike);
        this.tailSpike.setTextureOffset(0, 98).addBox(0.0F, -4.0F, -9.5F, 0.0F, 12.0F, 19.0F, 0.0F, false);

        this.tail2 = new AdvancedModelBox(this);
        this.tail2.setRotationPoint(0.0F, 0.0F, 19.0F);
        this.tail.addChild(this.tail2);
        this.tail2.setTextureOffset(26, 129).addBox(-10.0F, -3.0F, 14.0F, 7.0F, 3.0F, 3.0F, 0.0F, true);
        this.tail2.setTextureOffset(95, 46).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 7.0F, 19.0F, 0.0F, false);
        this.tail2.setTextureOffset(26, 129).addBox(3.0F, -3.0F, 8.0F, 7.0F, 3.0F, 3.0F, 0.0F, false);
        this.tail2.setTextureOffset(26, 129).addBox(3.0F, -3.0F, 14.0F, 7.0F, 3.0F, 3.0F, 0.0F, false);
        this.tail2.setTextureOffset(26, 129).addBox(-10.0F, -3.0F, 8.0F, 7.0F, 3.0F, 3.0F, 0.0F, true);

        this.tail2Spike = new AdvancedModelBox(this);
        this.tail2Spike.setRotationPoint(0.0F, -3.0F, 9.5F);
        this.tail2.addChild(this.tail2Spike);
        this.tail2Spike.setTextureOffset(131, 83).addBox(0.0F, -4.0F, -8.5F, 0.0F, 8.0F, 17.0F, 0.0F, false);

        this.neck = new AdvancedModelBox(this);
        this.neck.setRotationPoint(0.0F, 5.0F, -16.5F);
        this.body.addChild(this.neck);
        this.neck.setTextureOffset(100, 129).addBox(-5.0F, -7.0F, -16.0F, 10.0F, 14.0F, 20.0F, 0.0F, false);

        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, -6.0F, -14.0F);
        this.neck.addChild(this.head);
        this.head.setTextureOffset(110, 72).addBox(-8.0F, -17.0F, -17.0F, 16.0F, 12.0F, 8.0F, 0.0F, false);
        this.head.setTextureOffset(79, 29).addBox(-4.0F, 10.0F, -17.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);
        this.head.setTextureOffset(46, 108).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 15.0F, 10.0F, 0.0F, false);
        this.head.setTextureOffset(94, 108).addBox(-11.0F, -17.0F, 0.0F, 22.0F, 18.0F, 3.0F, 0.0F, false);
        this.head.setTextureOffset(0, 96).addBox(-14.0F, -20.0F, 0.0F, 28.0F, 21.0F, 0.0F, 0.0F, false);
        this.head.setTextureOffset(79, 0).addBox(-7.0F, -9.0F, -7.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
        this.head.setTextureOffset(21, 0).addBox(4.0F, -9.0F, -7.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
        this.head.setTextureOffset(0, 129).addBox(-4.0F, -5.0F, -17.0F, 8.0F, 7.0F, 10.0F, 0.0F, false);
        this.head.setTextureOffset(36, 142).addBox(-4.0F, 2.0F, -17.0F, 8.0F, 8.0F, 4.0F, 0.0F, false);

        this.jaw = new AdvancedModelBox(this);
        this.jaw.setRotationPoint(0.0F, 2.0F, -6.0F);
        this.head.addChild(this.jaw);
        this.jaw.setTextureOffset(65, 140).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 8.0F, 6.0F, 0.0F, false);

        this.grassBunch = new AdvancedModelBox(this);
        this.grassBunch.setRotationPoint(2.0F, 0.0F, -3.5F);
        this.jaw.addChild(this.grassBunch);

        this.grass = new AdvancedModelBox(this);
        this.grass.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.grassBunch.addChild(this.grass);

        this.grass5 = new AdvancedModelBox(this);
        this.grass5.setRotationPoint(2.4246F, -0.4275F, 0.0F);
        this.grass.addChild(this.grass5);
        this.setRotateAngle(this.grass5, 0.0F, 0.0F, -0.3491F);
        this.grass5.setTextureOffset(24, 165).addBox(-2.4246F, -0.4275F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, false);

        this.grass2 = new AdvancedModelBox(this);
        this.grass2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.grassBunch.addChild(this.grass2);
        this.grass2.setTextureOffset(1, 165).addBox(0.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, false);

        this.grassBunch2 = new AdvancedModelBox(this);
        this.grassBunch2.setRotationPoint(-2.0F, 0.0F, -3.5F);
        this.jaw.addChild(this.grassBunch2);

        this.grass3 = new AdvancedModelBox(this);
        this.grass3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.grassBunch2.addChild(this.grass3);

        this.grass6 = new AdvancedModelBox(this);
        this.grass6.setRotationPoint(-2.4246F, -0.4275F, 0.0F);
        this.grass3.addChild(this.grass6);
        this.setRotateAngle(this.grass6, 0.0F, 0.0F, 0.3491F);
        this.grass6.setTextureOffset(24, 165).addBox(-2.5754F, -0.4275F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, true);

        this.grass4 = new AdvancedModelBox(this);
        this.grass4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.grassBunch2.addChild(this.grass4);
        this.grass4.setTextureOffset(1, 165).addBox(-5.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, true);

        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            this.head.setScale(f, f, f);
            this.head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0.0D);
            this.parts().forEach((p) -> p.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.popPose();
            this.head.setScale(1.0F, 1.0F, 1.0F);
        } else {
            matrixStackIn.pushPose();
            this.parts().forEach((p) -> p.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.popPose();
        }
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.body, this.bodySpikes, this.tail, this.tail2, this.tailSpike, this.tail2Spike, this.neck, this.head, this.jaw, this.larm, this.rarm, this.lleg, this.rleg, this.lleg2, this.lfoot, this.rleg2, this.rfoot, this.grassBunch, this.grassBunch2, this.grass, this.grass2, this.grass3, this.grass4, this.grass5, this.grass6);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.body);
    }

    public void animate(GrottoceratopsServant entity) {
        this.animator.update(entity);
        this.animator.setAnimation(GrottoceratopsServant.ANIMATION_SPEAK_1);
        this.animator.startKeyframe(5);
        this.animator.move(this.jaw, 0.0F, 1.0F, -1.0F);
        this.animator.rotate(this.head, toRad(10.0F), 0.0F, 0.0F);
        this.animator.rotate(this.neck, toRad(-10.0F), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GrottoceratopsServant.ANIMATION_SPEAK_2);
        this.animator.startKeyframe(5);
        this.animator.move(this.jaw, 0.0F, 1.0F, -0.5F);
        this.animator.move(this.neck, 0.0F, 0.0F, -4.0F);
        this.animator.move(this.head, 0.0F, 2.0F, -2.0F);
        this.animator.rotate(this.jaw, toRad(30.0F), 0.0F, 0.0F);
        this.animator.rotate(this.head, toRad(-20.0F), toRad(-20.0F), 0.0F);
        this.animator.rotate(this.neck, toRad(-20.0F), toRad(-10.0F), 0.0F);
        this.animator.endKeyframe();
        this.animator.startKeyframe(10);
        this.animator.move(this.jaw, 0.0F, 1.0F, -0.5F);
        this.animator.move(this.neck, 0.0F, 0.0F, -4.0F);
        this.animator.move(this.head, 0.0F, 2.0F, -2.0F);
        this.animator.rotate(this.jaw, toRad(30.0F), 0.0F, 0.0F);
        this.animator.rotate(this.head, toRad(-20.0F), toRad(20.0F), 0.0F);
        this.animator.rotate(this.neck, toRad(-20.0F), toRad(10.0F), 0.0F);
        this.animator.endKeyframe();
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GrottoceratopsServant.ANIMATION_MELEE_RAM);
        this.animator.startKeyframe(5);
        this.animator.move(this.neck, 0.0F, 0.0F, 2.0F);
        this.animator.move(this.head, 0.0F, 3.0F, -2.0F);
        this.animator.rotate(this.head, toRad(20.0F), 0.0F, 0.0F);
        this.animator.rotate(this.neck, toRad(10.0F), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(5);
        this.animator.startKeyframe(2);
        this.animator.move(this.neck, 0.0F, 0.0F, -4.0F);
        this.animator.move(this.head, 0.0F, 3.0F, -2.0F);
        this.animator.rotate(this.head, toRad(-25.0F), 0.0F, 0.0F);
        this.animator.rotate(this.neck, toRad(-25.0F), 0.0F, 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(3);
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GrottoceratopsServant.ANIMATION_MELEE_TAIL_1);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.neck, 0.0F, toRad(-10.0F), 0.0F);
        this.animator.rotate(this.tail, toRad(20.0F), toRad(20.0F), 0.0F);
        this.animator.rotate(this.tail2, toRad(20.0F), toRad(20.0F), 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(2);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.neck, 0.0F, toRad(10.0F), 0.0F);
        this.animator.rotate(this.tail, toRad(-20.0F), toRad(-50.0F), 0.0F);
        this.animator.rotate(this.tail2, toRad(-20.0F), toRad(-20.0F), toRad(20.0F));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(3);
        this.animator.resetKeyframe(5);
        this.animator.setAnimation(GrottoceratopsServant.ANIMATION_MELEE_TAIL_2);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.neck, 0.0F, toRad(10.0F), 0.0F);
        this.animator.rotate(this.tail, toRad(20.0F), toRad(-20.0F), 0.0F);
        this.animator.rotate(this.tail2, toRad(20.0F), toRad(-20.0F), 0.0F);
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(2);
        this.animator.startKeyframe(5);
        this.animator.rotate(this.neck, 0.0F, toRad(-10.0F), 0.0F);
        this.animator.rotate(this.tail, toRad(-20.0F), toRad(50.0F), 0.0F);
        this.animator.rotate(this.tail2, toRad(-20.0F), toRad(20.0F), toRad(-20.0F));
        this.animator.endKeyframe();
        this.animator.setStaticKeyframe(3);
        this.animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(GrottoceratopsServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.animate(entity);
        float walkSpeed = 0.5F;
        float walkDegree = 1.0F;
        float partialTick = ageInTicks - entity.tickCount;
        float chewProgress = 1.0F - limbSwingAmount;

                this.grassBunch.showModel = false;
        this.grassBunch2.showModel = false;

                this.body.rotateAngleY += Mth.wrapDegrees(entity.getTailSwingRot(partialTick)) / 57.295776F;

                this.progressRotationPrev(this.tail, chewProgress, toRad(-10.0F), 0.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.neck, 1.0F, 0.0F, 0.0F, 5.0F, 1.0F);

                this.tail.walk(0.1F, 0.1F, false, 2.0F, 0.0F, ageInTicks, chewProgress);
        this.tail.swing(0.1F, 0.2F, false, 2.0F, 0.0F, ageInTicks, chewProgress);
        this.tail2.swing(0.1F, 0.1F, false, 1.0F, 0.0F, ageInTicks, chewProgress);
        this.neck.walk(0.1F, 0.03F, false, 2.0F, 0.0F, ageInTicks, chewProgress);
        this.head.walk(0.1F, 0.03F, true, 1.0F, 0.0F, ageInTicks, chewProgress);

                this.body.flap(walkSpeed, walkDegree * 0.1F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.lleg.flap(walkSpeed, walkDegree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rleg.flap(walkSpeed, walkDegree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.larm.flap(walkSpeed, walkDegree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rarm.flap(walkSpeed, walkDegree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.neck.flap(walkSpeed, walkDegree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.tail.swing(walkSpeed, walkDegree * 0.4F, false, -1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.tail2.swing(walkSpeed, walkDegree * 0.2F, false, -1.0F, 0.0F, limbSwing, limbSwingAmount);

        this.articulateLegs(entity.legSolver, partialTick);

        float bob = this.walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, walkDegree * 0.5F, 2.4F, true);
        this.body.rotationPointY += bob;

        this.larm.walk(walkSpeed, walkDegree * 0.4F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.larm.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 5.0F, false)) - bob;
        this.larm.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 1.0F, false);
        this.rarm.walk(walkSpeed, walkDegree * 0.4F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rarm.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 5.0F, true)) - bob;
        this.rarm.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 1.0F, true);

        this.lleg.walk(walkSpeed, walkDegree * 0.3F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.lfoot.walk(walkSpeed, walkDegree * 0.2F, false, 3.0F, 0.0F, limbSwing, limbSwingAmount);
        this.lleg.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5.0F, true)) - bob;
        this.lleg.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 1.0F, true);

        this.rleg.walk(walkSpeed, walkDegree * 0.3F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rfoot.walk(walkSpeed, walkDegree * 0.2F, true, 3.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rleg.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5.0F, false)) - bob;
        this.rleg.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 1.0F, false);

                if (entity.getAnimation() == GrottoceratopsServant.ANIMATION_MELEE_TAIL_1 || entity.getAnimation() == GrottoceratopsServant.ANIMATION_MELEE_TAIL_2) {
            float yawRad = netHeadYaw / 57.295776F;
            float pitchRad = headPitch / 57.295776F;
            this.neck.rotateAngleX += pitchRad * 0.1F;
            this.head.rotateAngleX += pitchRad * 0.2F;
            this.neck.rotateAngleY += yawRad * 0.3F;
            this.head.rotateAngleY += yawRad * 0.4F;
        }
    }

    private void articulateLegs(LegSolverQuadruped legSolver, float partialTick) {
        float backLeftH = legSolver.backLeft.getHeight(partialTick);
        float backRightH = legSolver.backRight.getHeight(partialTick);
        float frontLeftH = legSolver.frontLeft.getHeight(partialTick);
        float frontRightH = legSolver.frontRight.getHeight(partialTick);
        float maxH = Math.max(backLeftH, Math.max(backRightH, Math.max(frontLeftH, frontRightH))) * 0.8F;
        this.body.rotationPointY += maxH * 16.0F;
        this.rarm.rotationPointY += (frontRightH - maxH) * 16.0F;
        this.larm.rotationPointY += (frontLeftH - maxH) * 16.0F;
        this.rleg.rotationPointY += (backRightH - maxH) * 16.0F;
        this.lleg.rotationPointY += (backLeftH - maxH) * 16.0F;
    }


    private float walkValue(float speed, float degree, float f, float offset, float weight, boolean invert) {
        return (invert ? -1.0F : 1.0F) * degree * weight * Mth.cos(speed * f + offset);
    }

    private static float toRad(float degrees) {
        return degrees * Mth.DEG_TO_RAD;
    }

}
