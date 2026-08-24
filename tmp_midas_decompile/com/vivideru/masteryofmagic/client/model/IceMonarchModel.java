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

public class IceMonarchModel<T extends Entity>
extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goety_mastery_of_magic", "modelice_monarch"), "main");
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart RHAND;
    public final ModelPart RHANDINT;
    public final ModelPart LHAND2;
    public final ModelPart LHANDINT2;

    public IceMonarchModel(ModelPart root) {
        this.head = root.m_171324_("head");
        this.body = root.m_171324_("body");
        this.RHAND = root.m_171324_("RHAND");
        this.RHANDINT = this.RHAND.m_171324_("RHANDINT");
        this.LHAND2 = root.m_171324_("LHAND2");
        this.LHANDINT2 = this.LHAND2.m_171324_("LHANDINT2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.m_171576_();
        PartDefinition head = partdefinition.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(58, 60).m_171488_(-4.0f, -8.0f, -5.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).m_171514_(25, 58).m_171488_(-4.0f, -12.0f, -5.0f, 8.0f, 12.0f, 8.0f, new CubeDeformation(0.5f)), PartPose.m_171419_((float)0.0f, (float)-7.0f, (float)5.0f));
        PartDefinition body = partdefinition.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 25).m_171488_(-7.0f, -30.0f, -2.0f, 14.0f, 10.0f, 12.0f, new CubeDeformation(0.0f)).m_171514_(0, 0).m_171488_(-7.0f, -30.0f, -2.0f, 14.0f, 12.0f, 12.0f, new CubeDeformation(0.25f)).m_171514_(92, 111).m_171488_(-5.0f, -20.0f, 0.0f, 10.0f, 7.0f, 8.0f, new CubeDeformation(0.0f)).m_171514_(92, 89).m_171488_(-5.0f, -20.0f, 0.0f, 10.0f, 9.0f, 8.0f, new CubeDeformation(0.25f)).m_171514_(90, 0).m_171488_(-3.0f, -13.0f, 2.0f, 6.0f, 5.0f, 4.0f, new CubeDeformation(0.0f)).m_171514_(0, 86).m_171488_(-3.0f, -13.0f, 2.0f, 6.0f, 8.0f, 4.0f, new CubeDeformation(0.25f)), PartPose.m_171419_((float)0.0f, (float)24.0f, (float)0.0f));
        PartDefinition RHAND = partdefinition.m_171599_("RHAND", CubeListBuilder.m_171558_().m_171514_(0, 112).m_171488_(-8.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).m_171514_(45, 39).m_171488_(-8.0f, -4.0f, -4.0f, 8.0f, 11.0f, 8.0f, new CubeDeformation(0.25f)), PartPose.m_171419_((float)-9.0f, (float)-7.0f, (float)4.0f));
        PartDefinition cc4_r1 = RHAND.m_171599_("cc4_r1", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-4.0f, (float)-4.0f, (float)0.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        PartDefinition cc5_r1 = RHAND.m_171599_("cc5_r1", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-4.0f, (float)-4.0f, (float)0.0f, (float)-3.1416f, (float)0.7854f, (float)-3.1416f));
        PartDefinition cc4_r2 = RHAND.m_171599_("cc4_r2", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-7.5f, (float)0.0f, (float)0.0f, (float)-3.1416f, (float)0.7854f, (float)1.5708f));
        PartDefinition cc3_r1 = RHAND.m_171599_("cc3_r1", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-7.5f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.7854f, (float)-1.5708f));
        PartDefinition RHANDINT = RHAND.m_171599_("RHANDINT", CubeListBuilder.m_171558_().m_171514_(75, 28).m_171488_(-9.9951f, 13.1394f, -4.0061f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).m_171514_(22, 79).m_171488_(-9.9951f, 10.1394f, -4.0061f, 8.0f, 11.0f, 8.0f, new CubeDeformation(0.5f)), PartPose.m_171419_((float)-0.0049f, (float)-0.1394f, (float)0.0061f));
        PartDefinition cc3_r2 = RHANDINT.m_171599_("cc3_r2", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -3.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-5.9951f, (float)10.1394f, (float)-0.0061f, (float)0.0f, (float)0.7854f, (float)0.0f));
        PartDefinition cc2_r1 = RHANDINT.m_171599_("cc2_r1", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171488_(-4.5f, -3.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-5.9951f, (float)10.1394f, (float)-0.0061f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition LHAND2 = partdefinition.m_171599_("LHAND2", CubeListBuilder.m_171558_().m_171514_(0, 112).m_171480_().m_171488_(0.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(45, 39).m_171480_().m_171488_(0.0f, -4.0f, -4.0f, 8.0f, 11.0f, 8.0f, new CubeDeformation(0.25f)).m_171555_(false), PartPose.m_171419_((float)9.0f, (float)-7.0f, (float)4.0f));
        PartDefinition cc4_r3 = LHAND2.m_171599_("cc4_r3", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)4.0f, (float)-4.0f, (float)0.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition cc5_r2 = LHAND2.m_171599_("cc5_r2", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)4.0f, (float)-4.0f, (float)0.0f, (float)-3.1416f, (float)-0.7854f, (float)3.1416f));
        PartDefinition cc4_r4 = LHAND2.m_171599_("cc4_r4", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)7.5f, (float)0.0f, (float)0.0f, (float)-3.1416f, (float)-0.7854f, (float)-1.5708f));
        PartDefinition cc3_r3 = LHAND2.m_171599_("cc3_r3", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -6.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)7.5f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.7854f, (float)1.5708f));
        PartDefinition LHANDINT2 = LHAND2.m_171599_("LHANDINT2", CubeListBuilder.m_171558_().m_171514_(75, 28).m_171480_().m_171488_(1.9951f, 13.1394f, -4.0061f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(22, 79).m_171480_().m_171488_(1.9951f, 10.1394f, -4.0061f, 8.0f, 11.0f, 8.0f, new CubeDeformation(0.5f)).m_171555_(false), PartPose.m_171419_((float)0.0049f, (float)-0.1394f, (float)0.0061f));
        PartDefinition cc3_r4 = LHANDINT2.m_171599_("cc3_r4", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -3.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)5.9951f, (float)10.1394f, (float)-0.0061f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition cc2_r2 = LHANDINT2.m_171599_("cc2_r2", CubeListBuilder.m_171558_().m_171514_(23, 47).m_171480_().m_171488_(-4.5f, -3.0f, 0.0f, 9.0f, 6.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)5.9951f, (float)10.1394f, (float)-0.0061f, (float)0.0f, (float)0.7854f, (float)0.0f));
        return LayerDefinition.m_171565_((MeshDefinition)meshdefinition, (int)128, (int)128);
    }

    public void m_6973_(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.RHAND.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.LHAND2.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

