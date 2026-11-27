package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.animation.PiglinShamanAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllyPiglinShamanModel<T extends AllyPiglinShaman> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(GoetyOminous.MOD_ID, "piglin_shaman"), "main");
	private final ModelPart root;
	private final ModelPart rootBone;
	private final ModelPart head;

	public AllyPiglinShamanModel(ModelPart root) {
		this.root = root;
		this.rootBone = root.getChild("root");
		this.head = this.rootBone.getChild("body+knife").getChild("body").getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bodyKnife = root.addOrReplaceChild("body+knife", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition knife = bodyKnife.addOrReplaceChild("knife", CubeListBuilder.create().texOffs(110, 112).addBox(-0.5F, -6.0F, -6.0F, 1.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(106, 126).addBox(-0.5F, -5.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 126).addBox(-0.5F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 126).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 126).addBox(-0.5F, -5.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 126).addBox(-0.5F, -4.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 126).addBox(-0.5F, -3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 126).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 126).addBox(-0.5F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 126).addBox(-0.5F, -6.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 124).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 124).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 124).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 124).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 124).addBox(-0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 124).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 124).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 124).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 124).addBox(-0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 122).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 122).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -4.0F, -0.5F, 2.3562F, 0.0F, 0.0F));

		PartDefinition body = bodyKnife.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -16.0F, -4.0F, 16.0F, 16.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 40).addBox(-9.0F, -17.0F, -4.5F, 18.0F, 20.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition pot = body.addOrReplaceChild("pot", CubeListBuilder.create().texOffs(54, 69).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(94, 51).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(54, 49).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 9.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
		.texOffs(40, 0).addBox(-6.0F, -6.0F, -9.0F, 12.0F, 12.0F, 10.0F, new CubeDeformation(-0.02F)), PartPose.offset(0.0F, -10.0F, -4.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(29, 2).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 3).addBox(-3.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -8.0F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(40, 0).mirror().addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -3.0F, -4.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(74, 6).addBox(-2.0F, 3.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -3.0F, -4.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition rightarm = body.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(100, 0).mirror().addBox(-4.0F, -2.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, -12.0F, 0.0F));

		PartDefinition bone = rightarm.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 69).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 70).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.2F))
		.texOffs(12, 93).addBox(-1.0F, -12.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(73, 91).addBox(-10.0F, -14.0F, 0.0F, 20.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 93).addBox(-1.0F, 12.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 10.0F, -0.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(41, 87).addBox(-3.0F, -4.0F, -2.0F, 6.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(57, 86).addBox(3.0F, 1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(57, 86).addBox(-4.0F, 1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(61, 85).addBox(0.0F, -7.0F, -4.0F, 0.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -1.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition leftarm = body.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(100, 19).addBox(-1.0F, -2.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -12.0F, 0.0F));

		PartDefinition gold = leftarm.addOrReplaceChild("gold", CubeListBuilder.create().texOffs(0, 111).addBox(-7.6154F, -8.282F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(7.3846F, -2.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(7.3846F, -1.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(7.3846F, -0.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(7.3846F, 0.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(6.3846F, -3.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(6.3846F, 1.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(5.3846F, 1.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(4.3846F, 2.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(3.3846F, 2.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(2.3846F, 2.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(1.3846F, 3.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(0.3846F, 3.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(-0.6154F, 3.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(-1.6154F, 4.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 109).addBox(-2.6154F, 4.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-3.6154F, 4.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-4.6154F, 4.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-5.6154F, 3.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-6.6154F, 2.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, 1.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, 1.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, 0.718F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, -0.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, -1.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-7.6154F, -2.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-6.6154F, -3.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-5.6154F, -3.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-4.6154F, -3.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-3.6154F, -4.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-2.6154F, -4.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-1.6154F, -4.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(-0.6154F, -5.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(0.3846F, -5.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(1.3846F, -5.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(2.3846F, -6.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(3.3846F, -6.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(4.3846F, -5.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 109).addBox(5.3846F, -4.282F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.3846F, 5.282F, -1.0F));

		PartDefinition bottle = leftarm.addOrReplaceChild("bottle", CubeListBuilder.create().texOffs(34, 114).addBox(-4.5F, -7.0F, -0.5F, 9.0F, 13.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(53, 127).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 126).addBox(0.5F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 126).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 124).addBox(-1.5F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 122).addBox(-2.5F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 122).addBox(-2.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 122).addBox(-3.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 122).addBox(-4.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 126).addBox(-2.5F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 126).addBox(-1.5F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 126).addBox(-1.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 126).addBox(1.5F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 126).addBox(1.5F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(1.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(0.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(0.5F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(2.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(3.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(2.5F, 4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(-3.5F, 4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(1.5F, 5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(3.5F, 3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 124).addBox(-2.5F, 5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 4.0F, -1.5F));

		PartDefinition RightLeg = root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(84, 0).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.9F, -8.0F, 0.0F));

		PartDefinition leftleg = root.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(84, 0).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.1F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		int state = entity.getAnimState();
		switch (state) {
			case AllyPiglinShaman.ANIM_MELEE -> PoseBlend.animate(this, entity.meleeAnimationState, PiglinShamanAnimations.MELEE, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_RANGED -> PoseBlend.animate(this, entity.rangedAnimationState, PiglinShamanAnimations.RANGED, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_POTION -> PoseBlend.animate(this, entity.potionAnimationState, PiglinShamanAnimations.POTION_THROW, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_SPELL -> PoseBlend.animate(this, entity.spellAnimationState, PiglinShamanAnimations.SPELL, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_SUMMON -> PoseBlend.animate(this, entity.summonAnimationState, PiglinShamanAnimations.SUMMON, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_DEATH -> PoseBlend.animate(this, entity.deathAnimationState, PiglinShamanAnimations.DEATH, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_TRADE -> PoseBlend.animate(this, entity.tradeAnimationState, PiglinShamanAnimations.TRADE, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_DRINK -> PoseBlend.animate(this, entity.drinkAnimationState, PiglinShamanAnimations.DRINK, ageInTicks, 1.0F);
			case AllyPiglinShaman.ANIM_CONVERT -> PoseBlend.animate(this, entity.convertAnimationState, PiglinShamanAnimations.CONVERT, ageInTicks, 0.5F, 1.0F);
			case AllyPiglinShaman.ANIM_TELEPORT_OUT -> PoseBlend.animate(this, entity.teleportOutAnimationState, PiglinShamanAnimations.TELEPORT_OUT, ageInTicks, 1.0F);
			default -> this.animateIdle(entity.idleAnimationState, entity.walkAnimationState, entity.tickCount, limbSwingAmount, ageInTicks);
		}
		if (state != AllyPiglinShaman.ANIM_DEATH && state != AllyPiglinShaman.ANIM_TELEPORT_OUT) {
			this.head.yRot += netHeadYaw * Mth.DEG_TO_RAD;
			this.head.xRot += headPitch * Mth.DEG_TO_RAD;
		}
	}

	private void animateIdle(AnimationState idle, AnimationState walk, int tickCount, float limbSwingAmount, float ageInTicks) {
		idle.startIfStopped(tickCount);
		walk.startIfStopped(tickCount);
		float walkWeight = Math.min(limbSwingAmount * 4.0F, 1.0F);
		PoseBlend.animate(this, idle, PiglinShamanAnimations.IDLE, ageInTicks, 1.0F - walkWeight);
		PoseBlend.animate(this, walk, PiglinShamanAnimations.WALK, ageInTicks, walkWeight);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
