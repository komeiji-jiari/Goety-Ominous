/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.geom.ModelLayerLocation
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 */
package com.vivideru.masteryofmagic.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MidasBossModel<T extends Entity>
extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goety_mastery_of_magic", "midas_boss"), "main");
    private final ModelPart all;
    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart coat;
    private final ModelPart head;
    private final ModelPart right_eyebrow;
    private final ModelPart right_ear;
    private final ModelPart left_ear;
    private final ModelPart left_eyebrow;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart head2;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart bone3;

    public MidasBossModel(ModelPart root) {
        this.all = root.m_171324_("all");
        this.bone = this.all.m_171324_("bone");
        this.body = this.bone.m_171324_("body");
        this.coat = this.body.m_171324_("coat");
        this.head = this.body.m_171324_("head");
        this.right_eyebrow = this.head.m_171324_("right_eyebrow");
        this.right_ear = this.head.m_171324_("right_ear");
        this.left_ear = this.head.m_171324_("left_ear");
        this.left_eyebrow = this.head.m_171324_("left_eyebrow");
        this.right_arm = this.body.m_171324_("right_arm");
        this.left_arm = this.body.m_171324_("left_arm");
        this.head2 = this.body.m_171324_("head2");
        this.right_leg = this.all.m_171324_("right_leg");
        this.left_leg = this.all.m_171324_("left_leg");
        this.bone3 = root.m_171324_("bone3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.m_171576_();
        PartDefinition all = partdefinition.m_171599_("all", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)0.0f, (float)24.0f, (float)0.0f, (float)0.0f, (float)3.1416f, (float)0.0f));
        PartDefinition bone = all.m_171599_("bone", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition body = bone.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(40, 0).m_171488_(-6.0f, -36.0f, -3.0f, 12.0f, 18.0f, 6.0f, new CubeDeformation(0.0f)).m_171514_(58, 68).m_171488_(-6.0f, -22.0f, -3.0f, 12.0f, 3.0f, 6.0f, new CubeDeformation(0.6f)).m_171514_(24, 55).m_171488_(-2.5f, -22.0f, 3.0f, 5.0f, 3.0f, 1.0f, new CubeDeformation(0.0f)).m_171514_(36, 50).m_171488_(-1.5f, -19.0f, 3.0f, 3.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)).m_171514_(36, 52).m_171488_(-1.5f, -23.0f, 3.0f, 3.0f, 1.0f, 1.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition coat = body.m_171599_("coat", CubeListBuilder.m_171558_().m_171514_(59, 87).m_171488_(-14.5f, -4.5f, -3.0f, 28.0f, 34.0f, 7.0f, new CubeDeformation(0.2f)), PartPose.m_171419_((float)0.5f, (float)-32.0f, (float)-1.0f));
        PartDefinition left_shoulder_r1 = coat.m_171599_("left_shoulder_r1", CubeListBuilder.m_171558_().m_171514_(25, 71).m_171480_().m_171488_(-5.5f, -4.0f, -4.0f, 9.0f, 6.0f, 8.0f, new CubeDeformation(0.25f)).m_171555_(false), PartPose.m_171423_((float)-9.5f, (float)-1.25f, (float)1.0f, (float)0.0f, (float)0.0f, (float)0.1745f));
        PartDefinition right_shoulder_r1 = coat.m_171599_("right_shoulder_r1", CubeListBuilder.m_171558_().m_171514_(25, 71).m_171488_(-3.5f, -4.0f, -4.0f, 9.0f, 6.0f, 8.0f, new CubeDeformation(0.25f)), PartPose.m_171423_((float)8.5f, (float)-1.25f, (float)1.0f, (float)0.0f, (float)0.0f, (float)-0.1745f));
        PartDefinition head = body.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(45, 24).m_171488_(-5.0f, -8.0f, -6.5f, 10.0f, 10.0f, 10.0f, new CubeDeformation(0.0f)).m_171514_(80, 0).m_171488_(-6.0f, -12.0f, -7.5f, 12.0f, 8.0f, 12.0f, new CubeDeformation(0.0f)).m_171514_(0, 0).m_171488_(-5.0f, -8.0f, -6.5f, 10.0f, 21.0f, 10.0f, new CubeDeformation(0.5f)), PartPose.m_171423_((float)0.0f, (float)-38.0f, (float)1.5f, (float)0.0f, (float)0.0436f, (float)0.0f));
        PartDefinition cube_r1 = head.m_171599_("cube_r1", CubeListBuilder.m_171558_().m_171514_(1, 7).m_171480_().m_171488_(-2.0f, -1.5f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(5, 7).m_171480_().m_171488_(-2.0f, -6.5f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(1, 7).m_171480_().m_171488_(-2.0f, 4.4f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)-5.5f, (float)4.0f, (float)4.0f, (float)0.0f, (float)-0.3491f, (float)0.0f));
        PartDefinition cube_r2 = head.m_171599_("cube_r2", CubeListBuilder.m_171558_().m_171514_(1, 7).m_171488_(0.0f, -2.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)4.5f, (float)12.5f, (float)-7.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        PartDefinition cube_r3 = head.m_171599_("cube_r3", CubeListBuilder.m_171558_().m_171514_(1, 7).m_171488_(0.0f, -2.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(5, 7).m_171488_(0.0f, -8.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)5.5f, (float)4.5f, (float)-7.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        PartDefinition cube_r4 = head.m_171599_("cube_r4", CubeListBuilder.m_171558_().m_171514_(5, 7).m_171480_().m_171488_(-2.0f, -2.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(1, 7).m_171480_().m_171488_(-2.0f, 4.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)-5.5f, (float)-1.5f, (float)-7.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition cube_r5 = head.m_171599_("cube_r5", CubeListBuilder.m_171558_().m_171514_(1, 7).m_171480_().m_171488_(-2.0f, -2.0f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)-4.5f, (float)12.5f, (float)-7.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition cube_r6 = head.m_171599_("cube_r6", CubeListBuilder.m_171558_().m_171514_(5, 7).m_171488_(0.0f, -1.5f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(1, 7).m_171488_(0.0f, 9.4f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(1, 7).m_171488_(0.0f, 3.5f, 0.0f, 2.0f, 3.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)5.5f, (float)-1.0f, (float)4.0f, (float)0.0f, (float)0.3491f, (float)0.0f));
        PartDefinition right_eyebrow = head.m_171599_("right_eyebrow", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)3.0f, (float)-4.0f, (float)3.75f, (float)0.0f, (float)0.0f, (float)0.1309f));
        PartDefinition cube_r7 = right_eyebrow.m_171599_("cube_r7", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-3.0f, -2.0f, 1.0f, 4.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)1.0f, (float)1.0f, (float)-1.0f, (float)0.0f, (float)0.0f, (float)-0.1745f));
        PartDefinition right_ear = head.m_171599_("right_ear", CubeListBuilder.m_171558_().m_171514_(24, 50).m_171488_(0.0f, -2.5f, 0.0f, 6.0f, 5.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)5.0f, (float)-2.5f, (float)-1.5f, (float)0.0f, (float)0.6109f, (float)0.0f));
        PartDefinition left_ear = head.m_171599_("left_ear", CubeListBuilder.m_171558_().m_171514_(24, 50).m_171480_().m_171488_(-6.0f, -2.5f, 0.0f, 6.0f, 5.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)-5.0f, (float)-2.5f, (float)-1.5f, (float)0.0f, (float)-0.6109f, (float)0.0f));
        PartDefinition left_eyebrow = head.m_171599_("left_eyebrow", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)-3.0f, (float)-4.0f, (float)3.75f, (float)0.0f, (float)0.0f, (float)-0.1309f));
        PartDefinition cube_r8 = left_eyebrow.m_171599_("cube_r8", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171480_().m_171488_(-1.0f, -2.0f, 1.0f, 4.0f, 2.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)-1.0f, (float)1.0f, (float)-1.0f, (float)0.0f, (float)0.0f, (float)0.1745f));
        PartDefinition right_arm = body.m_171599_("right_arm", CubeListBuilder.m_171558_().m_171514_(0, 50).m_171488_(-3.0f, -2.0f, -3.0f, 6.0f, 18.0f, 6.0f, new CubeDeformation(0.0f)).m_171514_(0, 74).m_171488_(-3.0f, 9.0f, -3.0f, 6.0f, 3.0f, 6.0f, new CubeDeformation(0.5f)), PartPose.m_171419_((float)9.0f, (float)-34.0f, (float)0.0f));
        PartDefinition left_arm = body.m_171599_("left_arm", CubeListBuilder.m_171558_().m_171514_(0, 50).m_171480_().m_171488_(-3.0f, -2.0f, -3.0f, 6.0f, 18.0f, 6.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(0, 74).m_171480_().m_171488_(-3.0f, 9.0f, -3.0f, 6.0f, 3.0f, 6.0f, new CubeDeformation(0.5f)).m_171555_(false), PartPose.m_171419_((float)-9.0f, (float)-34.0f, (float)0.0f));
        PartDefinition head2 = body.m_171599_("head2", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)0.0f, (float)-38.0f, (float)1.5f, (float)0.0f, (float)-0.0436f, (float)0.0f));
        PartDefinition right_leg = all.m_171599_("right_leg", CubeListBuilder.m_171558_().m_171514_(44, 44).m_171488_(-3.0f, -1.0f, -3.0f, 6.0f, 18.0f, 6.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)3.0f, (float)-17.0f, (float)0.0f));
        PartDefinition cube_r9 = right_leg.m_171599_("cube_r9", CubeListBuilder.m_171558_().m_171514_(68, 44).m_171488_(-2.6527f, -1.9696f, -3.0f, 5.0f, 18.0f, 6.0f, new CubeDeformation(0.5f)), PartPose.m_171423_((float)0.5f, (float)-1.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.1745f));
        PartDefinition left_leg = all.m_171599_("left_leg", CubeListBuilder.m_171558_().m_171514_(44, 44).m_171480_().m_171488_(-3.0f, -1.0f, -3.0f, 6.0f, 18.0f, 6.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171419_((float)-3.0f, (float)-17.0f, (float)0.0f));
        PartDefinition cube_r10 = left_leg.m_171599_("cube_r10", CubeListBuilder.m_171558_().m_171514_(68, 44).m_171480_().m_171488_(-2.3473f, -1.9696f, -3.0f, 5.0f, 18.0f, 6.0f, new CubeDeformation(0.5f)).m_171555_(false), PartPose.m_171423_((float)-0.5f, (float)-1.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.1745f));
        PartDefinition bone3 = partdefinition.m_171599_("bone3", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)-5.0f, (float)-18.5f, (float)1.0f, (float)0.0f, (float)0.0f, (float)-0.3054f));
        return LayerDefinition.m_171565_((MeshDefinition)meshdefinition, (int)128, (int)128);
    }

    public void m_6973_(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.all.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.bone3.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

