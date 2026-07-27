package com.qiuyue.goetyominus.client.render.model;

import com.Polarice3.Goety.client.render.animation.NecromancerAnimations;
import com.Polarice3.Goety.utils.MathHelper;
import com.qiuyue.goetyominus.client.render.model.animation.SunkenNecromancerAnimation;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractSunkenNecromancer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SunkenNecromancerModel<T extends AbstractSunkenNecromancer> extends HierarchicalModel<T> {
    public final ModelPart root;
    public final ModelPart skeleton;
    public final ModelPart body;
    public final ModelPart head;
    public final ModelPart cape;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public SunkenNecromancerModel(ModelPart root) {
        this.root = root;
        this.skeleton = root.getChild("skeleton");
        this.body = this.skeleton.getChild("body");
        this.head = this.body.getChild("head");
        this.cape = this.body.getChild("cape");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = this.skeleton.getChild("right_leg");
        this.leftLeg = this.skeleton.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 53).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition horns = hat.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(24, 92).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

        PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

        PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 7.0F, 1.3963F, 0.0F, 0.0F));

        PartDefinition handle = staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(60, 41).addBox(-0.5F, -16.0F, 0.0F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.2561F, -0.9277F));

        PartDefinition group = staff.addOrReplaceChild("group", CubeListBuilder.create().texOffs(48, 16).addBox(-13.5F, -23.0F, 0.5F, 4.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(48, 21).addBox(-14.0F, -24.0F, 1.0F, 0.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(56, 22).addBox(-14.0F, -23.0F, -4.0F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(56, 16).addBox(-18.5F, -24.0F, 0.5F, 4.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, 5.2561F, -0.9277F));

        PartDefinition head2 = staff.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(48, 50).addBox(-2.5F, -19.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.2561F, -0.9277F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));

        PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron", CubeListBuilder.create().texOffs(0, 48).mirror().addBox(-1.0F, -4.0F, -3.0F, 5.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition middle = pants.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(40, 36).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F, 0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 1.0F));

        PartDefinition right_leg = skeleton.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

        PartDefinition left_leg = skeleton.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (!entity.isDeadOrDying()){
            if (entity.cantDo > 0){
                this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
                this.head.xRot = 0.4F;
            } else {
                this.animateHeadLookTarget(netHeadYaw, headPitch);
            }
        }
        this.animate(entity.idleAnimationState, NecromancerAnimations.ALERT, ageInTicks);
        if (this.riding){
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float)Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float)Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        } else {
            if (entity.isSwimming()) {
                this.animateWalk(SunkenNecromancerAnimation.SWIM, limbSwing, limbSwingAmount, 2.5F, 20.0F);
            } else if (!entity.isIdleOrNoAnimation()){
                this.animateWalk(entity, limbSwing, limbSwingAmount);
            } else {
                this.animateWalk(NecromancerAnimations.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
            }
        }
        this.animate(entity.attackAnimationState, NecromancerAnimations.ATTACK, ageInTicks, entity.getAttackSpeed());
        this.animate(entity.summonAnimationState, NecromancerAnimations.SUMMON, ageInTicks);
        this.animate(entity.spellAnimationState, NecromancerAnimations.SPELL, ageInTicks);
    }

    private void animateWalk(T entity, float limbSwing, float limbSwingAmount){
        float f = 1.0F;
        if (entity.getFallFlyingTicks() > 4) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f = f / 0.2F;
            f = f * f * f;
        }
        if (f < 1.0F) {
            f = 1.0F;
        }
        this.cape.xRot = MathHelper.modelDegrees(10.0F) + Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount / f);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
    }

    private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
