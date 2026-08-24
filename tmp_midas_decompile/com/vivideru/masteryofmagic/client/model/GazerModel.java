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

public class GazerModel<T extends Entity>
extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goety_mastery_of_magic", "modelgazer"), "main");
    public final ModelPart gazer;
    public final ModelPart body;
    public final ModelPart head;
    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public GazerModel(ModelPart root) {
        this.gazer = root.m_171324_("gazer");
        this.body = this.gazer.m_171324_("body");
        this.head = this.gazer.m_171324_("head");
        this.right_arm = this.gazer.m_171324_("right_arm");
        this.left_arm = this.gazer.m_171324_("left_arm");
        this.right_leg = this.gazer.m_171324_("right_leg");
        this.left_leg = this.gazer.m_171324_("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.m_171576_();
        PartDefinition gazer = partdefinition.m_171599_("gazer", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)0.0f, (float)-15.0f, (float)0.0f));
        PartDefinition body = gazer.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-7.0f, 0.0f, -4.0f, 14.0f, 12.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition head = gazer.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(16, 20).m_171488_(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)0.0f, (float)-1.0f, (float)-3.0f));
        PartDefinition right_arm = gazer.m_171599_("right_arm", CubeListBuilder.m_171558_().m_171514_(0, 20).m_171488_(-2.0f, -2.0f, -2.0f, 4.0f, 38.0f, 4.0f, new CubeDeformation(0.0f)).m_171514_(16, 36).m_171488_(-2.0f, 30.0f, 2.0f, 4.0f, 6.0f, 10.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)-9.0f, (float)0.0f, (float)0.0f));
        PartDefinition left_arm = gazer.m_171599_("left_arm", CubeListBuilder.m_171558_().m_171514_(0, 20).m_171480_().m_171488_(-2.0f, -2.0f, -2.0f, 4.0f, 38.0f, 4.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(16, 36).m_171480_().m_171488_(-2.0f, 30.0f, 2.0f, 4.0f, 6.0f, 10.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171419_((float)9.0f, (float)0.0f, (float)0.0f));
        PartDefinition right_leg = gazer.m_171599_("right_leg", CubeListBuilder.m_171558_().m_171514_(44, 36).m_171488_(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)-2.0f, (float)9.0f, (float)0.0f));
        PartDefinition left_leg = gazer.m_171599_("left_leg", CubeListBuilder.m_171558_().m_171514_(44, 36).m_171480_().m_171488_(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171419_((float)2.0f, (float)9.0f, (float)0.0f));
        return LayerDefinition.m_171565_((MeshDefinition)meshdefinition, (int)128, (int)128);
    }

    public void m_6973_(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.gazer.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

