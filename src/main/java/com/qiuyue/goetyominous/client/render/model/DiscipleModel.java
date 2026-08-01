package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.DiscipleAnimations;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Disciple;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscipleModel<T extends Disciple> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart robe;
    private final ModelPart ring;

    public DiscipleModel(ModelPart root) {
        this.root = root;
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
        this.body = root.getChild("body");
        this.arms = this.body.getChild("arms");
        this.head = this.body.getChild("head");
        this.nose = this.head.getChild("nose");
        this.robe = this.body.getChild("robe");
        this.ring = this.body.getChild("ring");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 65).addBox(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -11.0F, -2.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 38).addBox(-4.0F, -11.0F, -2.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.4F)),
                PartPose.offset(0.0F, 11.0F, -1.0F));

        PartDefinition arms = body.addOrReplaceChild("arms",
                CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 22).addBox(4.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 38).addBox(-4.0F, 3.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 47).addBox(-8.5F, -2.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 47).addBox(3.5F, -2.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.6F)),
                PartPose.offset(0.0F, -11.0F, 1.0F));

        PartDefinition nose = head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-1.0F, 2.0F, -2.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F)),
                PartPose.offset(0.0F, -3.0F, -4.0F));

        PartDefinition robe = body.addOrReplaceChild("robe",
                CubeListBuilder.create().texOffs(17, 68).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 7.0F, 6.0F, new CubeDeformation(0.8F))
                        .texOffs(0, 83).addBox(-4.0F, 1.0F, -3.0F, 8.0F, 19.0F, 6.0F, new CubeDeformation(0.6F)),
                PartPose.offset(0.0F, -11.0F, 1.0F));

        PartDefinition ring = body.addOrReplaceChild("ring",
                CubeListBuilder.create().texOffs(46, 78).addBox(-1.0F, 5.3333F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.1F))
                        .texOffs(55, 86).addBox(-1.0F, -13.6667F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, -10.3333F, 12.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition cube_r1 = ring.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(55, 106).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-6.0F, -0.6667F, 0.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition cube_r2 = ring.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(55, 116).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(9.0F, -2.6667F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition cube_r3 = ring.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(55, 96).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(6.0F, -0.6667F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition cube_r4 = ring.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(29, 87).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(29, 96).addBox(-5.0F, -5.0F, -1.0F, 10.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -0.6667F, 0.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float f = 0.01F * (float)(entity.getId() % 10);
        this.nose.xRot = Mth.sin((float)entity.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
        this.nose.yRot = 0.0F;
        this.nose.zRot = Mth.cos((float)entity.tickCount * f) * 2.5F * ((float)Math.PI / 180F);

        this.animateWalk(DiscipleAnimations.WALK, limbSwing, limbSwingAmount, 2.0F, 20.0F);
        this.animate(entity.idleAnimationState, DiscipleAnimations.IDLE, ageInTicks);
        this.animate(entity.attackAnimationState, DiscipleAnimations.ATTACK, ageInTicks);
        this.animate(entity.summonAnimationState, DiscipleAnimations.SUMMON, ageInTicks);

        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}