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
 *  net.minecraft.util.Mth
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class MasterStaffModel
extends EntityModel<Entity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goety_mastery_of_magic", "master_staff"), "main");
    private final ModelPart bone;
    private final ModelPart rotating;
    private final ModelPart upperCrystal;
    private final ModelPart lowerCrystal;

    public MasterStaffModel(ModelPart root) {
        this.bone = root.m_171324_("bone");
        this.rotating = this.bone.m_171324_("rotating");
        this.upperCrystal = this.rotating.m_171324_("bone2");
        this.lowerCrystal = this.rotating.m_171324_("bone4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.m_171576_();
        PartDefinition bone = root.m_171599_("bone", CubeListBuilder.m_171558_().m_171514_(0, 15).m_171488_(-8.5f, -34.0f, 7.5f, 1.0f, 32.0f, 1.0f, new CubeDeformation(0.0f)).m_171514_(4, 21).m_171488_(-9.0f, -7.0f, 7.0f, 2.0f, 1.0f, 2.0f, new CubeDeformation(0.0f)).m_171514_(4, 24).m_171488_(-9.0f, -5.0f, 7.0f, 2.0f, 1.0f, 2.0f, new CubeDeformation(0.0f)).m_171514_(16, 20).m_171488_(-9.0f, -32.0f, 7.0f, 2.0f, 2.0f, 2.0f, new CubeDeformation(0.0f)).m_171514_(12, 24).m_171488_(-9.0f, -29.0f, 7.0f, 2.0f, 1.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)8.0f, (float)24.0f, (float)-8.0f));
        bone.m_171599_("cube_r1", CubeListBuilder.m_171558_().m_171514_(16, 15).m_171488_(-1.5f, -1.5f, -1.0f, 3.0f, 3.0f, 2.0f, new CubeDeformation(0.25f)), PartPose.m_171423_((float)-8.0f, (float)-2.5f, (float)8.0f, (float)0.0f, (float)0.0f, (float)0.7854f));
        bone.m_171599_("cube_r2", CubeListBuilder.m_171558_().m_171514_(4, 15).m_171488_(-2.5f, -2.5f, -1.0f, 4.0f, 4.0f, 2.0f, new CubeDeformation(0.25f)), PartPose.m_171423_((float)-8.0f, (float)-33.5f, (float)8.0f, (float)0.0f, (float)0.0f, (float)0.7854f));
        bone.m_171599_("cube_r3", CubeListBuilder.m_171558_().m_171514_(0, 49).m_171488_(-7.5f, -7.5f, 0.0f, 15.0f, 15.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(0, 0).m_171488_(-7.5f, -7.5f, 0.5f, 15.0f, 15.0f, 0.0f, new CubeDeformation(0.0f)).m_171514_(0, 0).m_171488_(-7.5f, -7.5f, -0.5f, 15.0f, 15.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-8.0f, (float)-36.25f, (float)8.0f, (float)0.0f, (float)0.0f, (float)0.7854f));
        PartDefinition planes = bone.m_171599_("bone3", CubeListBuilder.m_171558_().m_171514_(31, -3).m_171488_(0.0f, -4.0f, -1.5f, 0.0f, 6.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-8.0f, (float)-37.0f, (float)8.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        planes.m_171599_("cube_r4", CubeListBuilder.m_171558_().m_171514_(31, -3).m_171488_(0.0f, -3.0f, -2.0f, 0.0f, 6.0f, 3.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)0.5f, (float)-1.0f, (float)0.0f, (float)0.0f, (float)1.5708f, (float)0.0f));
        PartDefinition rotating = bone.m_171599_("rotating", CubeListBuilder.m_171558_(), PartPose.m_171423_((float)-8.0f, (float)-24.0f, (float)8.0f, (float)0.0f, (float)1.1781f, (float)0.0f));
        rotating.m_171599_("bone2", CubeListBuilder.m_171558_().m_171514_(44, 2).m_171488_(-2.5f, -2.5f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(-0.2f)), PartPose.m_171423_((float)0.0f, (float)-2.5f, (float)0.0f, (float)0.0f, (float)-0.3927f, (float)0.0f));
        rotating.m_171599_("bone4", CubeListBuilder.m_171558_().m_171514_(44, 14).m_171488_(-2.5f, -2.5f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(-0.6f)), PartPose.m_171423_((float)0.0f, (float)2.5f, (float)0.0f, (float)0.0f, (float)0.3927f, (float)0.0f));
        return LayerDefinition.m_171565_((MeshDefinition)meshDefinition, (int)64, (int)64);
    }

    public void animate(float ageInTicks) {
        float cycle = ageInTicks * 0.07853982f;
        this.rotating.f_104204_ = 1.1781f + cycle;
        this.rotating.f_104201_ = -24.0f + (Mth.m_14031_((float)(cycle * 0.5f)) + 1.0f) * 0.625f;
        this.upperCrystal.f_104201_ = -2.5f + Mth.m_14031_((float)cycle) * 0.13f;
        this.lowerCrystal.f_104201_ = 2.5f - Mth.m_14031_((float)cycle) * 0.25f;
    }

    public void renderBase(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        this.bone.m_104306_(poseStack, consumer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderAccents(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue) {
        this.bone.m_104306_(poseStack, consumer, packedLight, packedOverlay, red, green, blue, 1.0f);
    }

    public void m_6973_(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animate(ageInTicks);
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bone.m_104306_(poseStack, consumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

