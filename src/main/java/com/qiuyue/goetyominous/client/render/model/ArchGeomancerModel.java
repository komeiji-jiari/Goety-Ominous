package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.animation.ArchGeomancerAnimation;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ArchGeomancerEntity;
import net.minecraft.client.model.HierarchicalModel;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArchGeomancerModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GoetyOminous.MOD_ID, "arch_geomancer"), "main");
	private final ModelPart root;
	private final ModelPart group2;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart waist;
	private final ModelPart Body;
	private final ModelPart head;
	private final ModelPart RightArm;
	private final ModelPart Staff;
	private final ModelPart LeftArm;

	public ArchGeomancerModel(ModelPart root) {
		this.root = root;
		this.group2 = root.getChild("group2");
		this.RightLeg = this.group2.getChild("RightLeg");
		this.LeftLeg = this.group2.getChild("LeftLeg");
		this.waist = this.group2.getChild("waist");
		this.Body = this.waist.getChild("Body");
		this.head = this.waist.getChild("head");
		this.RightArm = this.waist.getChild("RightArm");
		this.Staff = this.RightArm.getChild("Staff");
		this.LeftArm = this.waist.getChild("LeftArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition group2 = partdefinition.addOrReplaceChild("group2", CubeListBuilder.create(), PartPose.offset(0.0F, 11.6667F, 0.0F));

		PartDefinition RightLeg = group2.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.3333F, 0.0F));

		PartDefinition LeftLeg = group2.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.3333F, 0.0F));

		PartDefinition waist = group2.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, -0.6667F, 0.0F));

		PartDefinition Body = waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 19.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition all_neck = Body.addOrReplaceChild("all_neck", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(4.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 4).addBox(-6.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition neck = all_neck.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(13, 21).addBox(-6.0F, -2.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, 1.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(13, 21).addBox(-1.0F, 3.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, 1.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(13, 21).addBox(4.0F, -2.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition neck2 = all_neck.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(13, 21).addBox(-6.0F, -2.0F, 2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, 1.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(13, 21).addBox(-1.0F, 3.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, 1.0F, 3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(13, 21).addBox(4.0F, -2.0F, 2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition head = waist.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 100).addBox(-4.0F, -11.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition group = head.addOrReplaceChild("group", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -0.1F, -1.0F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition r_wing = head.addOrReplaceChild("r_wing", CubeListBuilder.create().texOffs(46, 0).addBox(-4.0F, -2.0F, -1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(47, 0).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -4.0F, -4.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition l_wing = head.addOrReplaceChild("l_wing", CubeListBuilder.create().texOffs(46, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(47, 0).mirror().addBox(0.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, -4.0F, -4.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition RightArm = waist.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(44, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(10, 70).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-5.0F, -9.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition Staff = RightArm.addOrReplaceChild("Staff", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 9.0F, 9.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition handle = Staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(32, 91).addBox(0.0F, -16.0F, 0.0F, 1.0F, 25.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition group3 = Staff.addOrReplaceChild("group3", CubeListBuilder.create().texOffs(36, 109).addBox(-0.5F, -16.0F, -0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 122).addBox(-0.5F, 9.0F, -0.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = group3.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 105).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -16.5F, 0.5F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r2 = group3.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(46, 104).mirror().addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -15.5F, -0.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r3 = group3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(58, 103).mirror().addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -15.5F, 1.5F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r4 = group3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(52, 104).mirror().addBox(-2.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, -15.5F, 0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r5 = group3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(52, 104).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -15.5F, 0.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition head2 = Staff.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(36, 99).addBox(-1.0F, -20.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(47, 110).addBox(-0.5F, -24.5F, -2.5F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r6 = head2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(52, 104).addBox(-2.828F, -13.1691F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.5F, -15.5F, 0.5F, 0.0F, 0.0F, -0.8727F));

		PartDefinition cube_r7 = head2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(52, 104).mirror().addBox(-6.2426F, -4.7426F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, -15.5F, 0.5F, 0.0F, 0.0F, 0.8727F));

		PartDefinition cube_r8 = head2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(36, 85).mirror().addBox(-0.5F, -20.9318F, -0.0262F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(43, 84).addBox(-0.5F, -17.0639F, 5.0695F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -15.5F, -19.5F, -0.9599F, 0.0F, 0.0F));

		PartDefinition cube_r9 = head2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(54, 87).mirror().addBox(-0.5F, -5.3679F, 6.9242F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(48, 86).mirror().addBox(-0.5F, -1.5F, 2.8284F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -15.5F, 1.5F, 0.9599F, 0.0F, 0.0F));

		PartDefinition LeftArm = waist.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(44, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(44, 67).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false), PartPose.offset(5.0F, -9.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		if (entity instanceof ArchGeomancerEntity geomancer) {
			this.animate(geomancer.idleAnimationState, ArchGeomancerAnimation.IDLE, ageInTicks, 1.0F);
			this.animate(geomancer.attackAnimationState, ArchGeomancerAnimation.ATTACK, ageInTicks, 1.0F);
			this.animate(geomancer.summonAnimationState, ArchGeomancerAnimation.SUMMON, ageInTicks, 1.0F);
			this.animate(geomancer.bigAttackAnimationState, ArchGeomancerAnimation.BIG_ATTACK, ageInTicks, 1.0F);
			this.animate(geomancer.spellAttackAnimationState, ArchGeomancerAnimation.SPELL_ATTACK, ageInTicks, 1.0F);
			if (geomancer.getCurrentAnimation() == 0) {
				this.animateWalk(ArchGeomancerAnimation.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
			}
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.group2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
