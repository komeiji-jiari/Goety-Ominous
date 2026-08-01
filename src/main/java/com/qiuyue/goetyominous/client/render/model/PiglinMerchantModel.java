package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.PiglinMerchantAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mobs.PiglinMerchant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinMerchantModel<T extends Entity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyominous", "piglin_merchant"), "main");

    private final ModelPart root;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart head;
    private final ModelPart leftRing;
    private final ModelPart rightRing;
    private final ModelPart ear1;
    private final ModelPart ear2;
    private final ModelPart body;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public PiglinMerchantModel(ModelPart root) {
        this.root = root;
        this.leftLeg = root.getChild("left leg");
        this.rightLeg = root.getChild("right leg");
        this.head = root.getChild("head");
        this.leftRing = this.head.getChild("left ring");
        this.rightRing = this.head.getChild("right ring");
        this.ear1 = this.head.getChild("ear1");
        this.ear2 = this.head.getChild("ear2");
        this.body = root.getChild("body");
        this.bone = this.body.getChild("bone");
        this.bone2 = this.body.getChild("bone2");
        this.leftArm = this.bone2.getChild("left arm");
        this.rightArm = this.bone2.getChild("right arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 16.0F, 7.0F));

        partdefinition.addOrReplaceChild("right leg", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 16.0F, 7.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -7.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(20, 96).addBox(-5.0F, -7.0F, -8.0F, 12.0F, 8.0F, 11.0F, new CubeDeformation(-0.3F))
                .texOffs(29, 2).addBox(-1.0F, -2.0F, -8.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 2).addBox(3.0F, 0.0F, -8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 5).addBox(-2.0F, 0.0F, -8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 8.0F, -1.0F));

        PartDefinition leftRing = head.addOrReplaceChild("left ring", CubeListBuilder.create().texOffs(85, 77).addBox(0.0F, 0.0F, -1.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 2.0F, -3.0F));

        leftRing.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(85, 77).addBox(0.0F, -5.0F, -0.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 5.0F, 2.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition rightRing = head.addOrReplaceChild("right ring", CubeListBuilder.create().texOffs(104, 78).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, -2.0F));

        rightRing.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(104, 78).addBox(1.0F, -6.0F, -1.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 6.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition ear1 = head.addOrReplaceChild("ear1", CubeListBuilder.create().texOffs(40, 0).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(-1.5F, 2.5F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -3.5F, -3.0F, 0.0F, 0.0F, -0.3491F));

        head.addOrReplaceChild("ear2", CubeListBuilder.create().texOffs(40, 0).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -3.5F, -3.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 14.0F, 7.0F));

        body.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 65).addBox(-8.0F, -10.0F, -8.0F, 15.0F, 15.0F, 8.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition bone2 = body.addOrReplaceChild("bone2", CubeListBuilder.create()
                .texOffs(85, 82).addBox(-13.0F, -10.0F, -6.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(85, 77).addBox(-1.0F, -10.0F, -6.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(85, 77).addBox(12.0F, -10.0F, -6.0F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(112, 58).addBox(14.0F, -13.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(112, 66).addBox(-18.0F, -13.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, -3.0F, 0.1745F, 0.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(122, -3).addBox(1.0F, -11.0F, -1.0F, 0.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 1.0F, -2.0F, 0.0F, 1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(122, -3).addBox(1.0F, -11.0F, -1.0F, 0.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 1.0F, -2.0F, 0.0F, 1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(80, 97).addBox(-6.0F, -24.5F, -1.0F, 7.0F, 24.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -9.0F, 1.0F, 0.0F, 0.0F, 1.5708F));

        bone2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 31).addBox(-11.0F, -19.0F, -1.0F, 12.0F, 19.0F, 15.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(6.0F, 19.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(49, 71).addBox(-8.0F, -16.0F, -1.0F, 9.0F, 16.0F, 9.0F, new CubeDeformation(-0.8F)), PartPose.offsetAndRotation(-8.0F, 14.0F, 7.0F, 0.0F, 0.0F, 1.5708F));

        bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(58, 29).addBox(-20.0F, -15.0F, -1.0F, 21.0F, 15.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 7.0F, 15.0F, 0.0F, 3.1416F, 0.0F));

        bone2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(120, 59).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -11.0F, -1.0F, 0.0F, 0.0F, -1.5708F));

        bone2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(85, 77).addBox(0.0F, -5.0F, -0.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -5.0F, -3.0F, 0.0F, -1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(85, 77).addBox(0.0F, -5.0F, -0.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -5.0F, -3.0F, 0.0F, -1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(85, 82).addBox(1.0F, -8.0F, -1.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -2.0F, -4.0F, 0.0F, -1.5708F, 0.0F));

        bone2.addOrReplaceChild("left arm", CubeListBuilder.create().texOffs(40, 13).addBox(-0.5F, 0.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.3F, 1.0F, -1.5F, -0.1731F, -0.0227F, -0.1289F));

        PartDefinition rightArm = bone2.addOrReplaceChild("right arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.3F, 1.0F, -1.5F, -0.1731F, 0.0227F, 0.1289F));

        rightArm.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 89).addBox(-4.2F, -14.0F, -1.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.7F, 14.0F, 1.5F, 0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(PiglinMerchantAnimations.WALK, limbSwing, limbSwingAmount, 2.0F, 2.5F);

        if (entity instanceof PiglinMerchant merchant) {
            boolean moving = merchant.walkAnimation.isMoving();
            if (moving) return;

            if (merchant.seeAnimationState.isStarted()) {
                this.animate(merchant.seeAnimationState, PiglinMerchantAnimations.SEE, ageInTicks);
            } else if (merchant.see2AnimationState.isStarted()) {
                this.animate(merchant.see2AnimationState, PiglinMerchantAnimations.SEE2, ageInTicks);
            } else if (merchant.restAnimationState.isStarted()) {
                this.animate(merchant.restAnimationState, PiglinMerchantAnimations.IDLE, ageInTicks);
            } else if (merchant.rest2AnimationState.isStarted()) {
                this.animate(merchant.rest2AnimationState, PiglinMerchantAnimations.IDLE2, ageInTicks);
            }
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}