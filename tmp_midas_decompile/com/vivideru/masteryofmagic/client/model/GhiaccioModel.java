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

public class GhiaccioModel<T extends Entity>
extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goety_mastery_of_magic", "model_ghiaccio_entity"), "main");
    public final ModelPart Ghiaccio;
    public final ModelPart testa;
    public final ModelPart head;
    public final ModelPart corpo;
    public final ModelPart body;
    public final ModelPart braccio_sinistro;
    public final ModelPart braccio_destro;
    public final ModelPart gamba_sinistra;
    public final ModelPart left_leg;
    public final ModelPart chain;
    public final ModelPart gamba_destra;
    public final ModelPart right_leg;
    public final ModelPart chain2;

    public GhiaccioModel(ModelPart root) {
        this.Ghiaccio = root.m_171324_("Ghiaccio");
        this.testa = this.Ghiaccio.m_171324_("testa");
        this.head = this.testa.m_171324_("head");
        this.corpo = this.Ghiaccio.m_171324_("corpo");
        this.body = this.corpo.m_171324_("body");
        this.braccio_sinistro = this.Ghiaccio.m_171324_("braccio_sinistro");
        this.braccio_destro = this.Ghiaccio.m_171324_("braccio_destro");
        this.gamba_sinistra = this.Ghiaccio.m_171324_("gamba_sinistra");
        this.left_leg = this.gamba_sinistra.m_171324_("left_leg");
        this.chain = this.gamba_sinistra.m_171324_("chain");
        this.gamba_destra = this.Ghiaccio.m_171324_("gamba_destra");
        this.right_leg = this.gamba_destra.m_171324_("right_leg");
        this.chain2 = this.gamba_destra.m_171324_("chain2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.m_171576_();
        PartDefinition Ghiaccio = partdefinition.m_171599_("Ghiaccio", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)0.0f, (float)23.0f, (float)0.0f));
        PartDefinition testa = Ghiaccio.m_171599_("testa", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)0.0f, (float)-24.0f, (float)0.0f));
        PartDefinition head = testa.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new CubeDeformation(0.0f)).m_171514_(0, 60).m_171488_(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, new CubeDeformation(0.0f)).m_171514_(64, 0).m_171488_(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new CubeDeformation(0.5f)), PartPose.m_171419_((float)0.0f, (float)-1.0f, (float)0.0f));
        PartDefinition head_r1 = head.m_171599_("head_r1", CubeListBuilder.m_171558_().m_171514_(32, 52).m_171480_().m_171488_(0.0f, -4.0f, 0.0f, 5.0f, 7.0f, 0.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171423_((float)4.0f, (float)-5.0f, (float)-2.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        PartDefinition head_r2 = head.m_171599_("head_r2", CubeListBuilder.m_171558_().m_171514_(32, 52).m_171488_(-5.0f, -4.0f, 0.0f, 5.0f, 7.0f, 0.0f, new CubeDeformation(0.0f)), PartPose.m_171423_((float)-4.0f, (float)-5.0f, (float)-2.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        PartDefinition corpo = Ghiaccio.m_171599_("corpo", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)0.0f, (float)-25.0f, (float)0.0f));
        PartDefinition body = corpo.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(0, 18).m_171488_(-4.0f, 0.0f, -2.0f, 8.0f, 13.0f, 4.0f, new CubeDeformation(0.0f)).m_171514_(44, 38).m_171488_(-4.5f, 0.0f, -3.0f, 9.0f, 6.0f, 6.0f, new CubeDeformation(0.1f)).m_171514_(32, 59).m_171488_(-2.0f, 10.0f, -3.0f, 4.0f, 4.0f, 1.0f, new CubeDeformation(0.0f)).m_171514_(24, 18).m_171488_(-4.0f, 0.0f, -2.0f, 8.0f, 13.0f, 4.0f, new CubeDeformation(0.25f)), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition braccio_sinistro = Ghiaccio.m_171599_("braccio_sinistro", CubeListBuilder.m_171558_().m_171514_(16, 35).m_171480_().m_171488_(-2.25f, -1.5f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(0, 66).m_171480_().m_171488_(-2.75f, -2.5f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)).m_171555_(false).m_171514_(0, 35).m_171488_(-2.25f, -1.5f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.4f)).m_171514_(28, 64).m_171480_().m_171488_(-2.25f, 6.5f, -2.0f, 4.0f, 5.0f, 4.0f, new CubeDeformation(0.25f)).m_171555_(false), PartPose.m_171419_((float)7.0f, (float)-23.5f, (float)0.0f));
        PartDefinition braccio_destro = Ghiaccio.m_171599_("braccio_destro", CubeListBuilder.m_171558_().m_171514_(16, 35).m_171488_(-1.75f, -1.5f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.0f)).m_171514_(28, 64).m_171488_(-1.75f, 6.5f, -2.0f, 4.0f, 5.0f, 4.0f, new CubeDeformation(0.25f)).m_171514_(0, 66).m_171488_(-2.25f, -2.5f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)).m_171514_(0, 35).m_171480_().m_171488_(-1.75f, -1.5f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.4f)).m_171555_(false), PartPose.m_171419_((float)-7.0f, (float)-23.5f, (float)0.0f));
        PartDefinition gamba_sinistra = Ghiaccio.m_171599_("gamba_sinistra", CubeListBuilder.m_171558_().m_171514_(48, 17).m_171488_(-2.0f, 0.0f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.25f)), PartPose.m_171419_((float)2.0f, (float)-12.0f, (float)0.0f));
        PartDefinition left_leg = gamba_sinistra.m_171599_("left_leg", CubeListBuilder.m_171558_().m_171514_(48, 0).m_171488_(-2.0f, 0.0f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition chain = gamba_sinistra.m_171599_("chain", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)1.75f, (float)1.25f, (float)0.0f));
        PartDefinition chain_r1 = chain.m_171599_("chain_r1", CubeListBuilder.m_171558_().m_171514_(32, 0).m_171480_().m_171488_(-2.0f, -2.5f, -2.0f, 3.0f, 8.0f, 4.0f, new CubeDeformation(0.2f)).m_171555_(false), PartPose.m_171423_((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.1309f));
        PartDefinition gamba_destra = Ghiaccio.m_171599_("gamba_destra", CubeListBuilder.m_171558_().m_171514_(48, 17).m_171480_().m_171488_(-2.0f, 0.0f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.25f)).m_171555_(false), PartPose.m_171419_((float)-2.0f, (float)-12.0f, (float)0.0f));
        PartDefinition right_leg = gamba_destra.m_171599_("right_leg", CubeListBuilder.m_171558_().m_171514_(48, 0).m_171480_().m_171488_(-2.0f, 0.0f, -2.0f, 4.0f, 13.0f, 4.0f, new CubeDeformation(0.0f)).m_171555_(false), PartPose.m_171419_((float)0.0f, (float)0.0f, (float)0.0f));
        PartDefinition chain2 = gamba_destra.m_171599_("chain2", CubeListBuilder.m_171558_(), PartPose.m_171419_((float)-1.75f, (float)1.25f, (float)0.0f));
        PartDefinition chain_r2 = chain2.m_171599_("chain_r2", CubeListBuilder.m_171558_().m_171514_(32, 0).m_171488_(-1.0f, -2.5f, -2.0f, 3.0f, 8.0f, 4.0f, new CubeDeformation(0.2f)), PartPose.m_171423_((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.1309f));
        return LayerDefinition.m_171565_((MeshDefinition)meshdefinition, (int)128, (int)128);
    }

    public void m_7695_(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.Ghiaccio.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void m_6973_(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}

