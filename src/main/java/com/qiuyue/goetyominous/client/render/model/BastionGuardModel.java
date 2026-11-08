package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.animation.BastionGuardAnimation;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BastionGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZBastionGuardServant;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
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
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BastionGuardModel<T extends Mob> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(GoetyOminous.MOD_ID, "bastion_guard"), "main");
	private static final Vector3f ANIMATION_VECTOR = new Vector3f();
	private static final Vector3f ANIMATION_EULER = new Vector3f();
	private static final Quaternionf ANIMATION_ROTATION_FROM = new Quaternionf();
	private static final Quaternionf ANIMATION_ROTATION_TO = new Quaternionf();
	private final ModelPart root;
	private final ModelPart Body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart leftear;
	private final ModelPart rightear;
	private final ModelPart RightArm;
	private final ModelPart axe;
	private final ModelPart LeftArm;
	private final ModelPart shield;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private List<ModelPart> animationParts;
	private float[] poseSnapshot;

	public BastionGuardModel(ModelPart root) {
		this.root = root.getChild("root");
		this.Body = this.root.getChild("Body");
		this.head = this.Body.getChild("head");
		this.nose = this.head.getChild("nose");
		this.leftear = this.head.getChild("leftear");
		this.rightear = this.head.getChild("rightear");
		this.RightArm = this.Body.getChild("RightArm");
		this.axe = this.RightArm.getChild("axe");
		this.LeftArm = this.Body.getChild("LeftArm");
		this.shield = this.LeftArm.getChild("shield");
		this.RightLeg = this.root.getChild("RightLeg");
		this.LeftLeg = this.root.getChild("LeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F))
		.texOffs(0, 32).addBox(-5.0F, -12.5F, -3.0F, 10.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition cube_r1 = Body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(47, 69).mirror().addBox(-1.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition cube_r2 = Body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(47, 69).addBox(-2.0F, -1.0F, -3.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition Body_r1 = Body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(2, 54).addBox(-5.5F, -1.5F, -2.5F, 11.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.6F, -3.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition head = Body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
		.texOffs(0, 62).addBox(-6.0F, -9.0F, -5.0F, 12.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(34, 67).addBox(-2.0F, -10.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 65).addBox(6.0F, -11.0F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 65).mirror().addBox(-8.0F, -11.0F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(31, 1).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 4).addBox(-3.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(51, 6).mirror().addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(48, 32).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 81).addBox(-4.0F, -3.0F, -2.5F, 6.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(19, 84).addBox(-5.0F, -2.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(36, 81).mirror().addBox(-2.0F, 9.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(22, 81).mirror().addBox(-4.0F, 7.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, -10.0F, 0.0F));

		PartDefinition axe = RightArm.addOrReplaceChild("axe", CubeListBuilder.create().texOffs(78, 81).addBox(-0.5F, -17.0F, -18.0F, 1.0F, 23.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(116, 101).addBox(-0.5F, 3.0F, 3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, 2.0F, 2.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, 1.0F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(120, 96).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(120, 96).addBox(-0.5F, -5.0F, -5.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(120, 96).addBox(-0.5F, -6.0F, -6.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, -7.0F, -7.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 97).addBox(-0.5F, -5.0F, -4.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 97).addBox(-0.5F, -4.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 97).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, -8.0F, -8.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, -9.0F, -9.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(106, 101).addBox(-0.5F, -10.0F, -10.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 103).addBox(-0.5F, 5.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(122, 101).addBox(-0.5F, -11.0F, -12.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 101).addBox(-0.5F, -14.0F, -11.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 101).addBox(-0.5F, -13.0F, -12.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -8.0F, -18.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -7.0F, -18.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -6.0F, -17.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -5.0F, -17.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -4.0F, -16.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -3.0F, -15.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 94).addBox(-0.5F, -15.0F, -15.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -3.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -2.0F, -13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -2.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -2.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -13.0F, -17.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -13.0F, -18.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 94).addBox(-0.5F, -14.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(116, 94).addBox(-0.5F, -2.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(116, 94).addBox(-0.5F, -3.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(116, 94).addBox(-0.5F, -6.0F, -13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(116, 94).addBox(-0.5F, -7.0F, -13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -13.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -2.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -4.0F, -11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -4.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -5.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -12.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -13.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -12.0F, -13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -11.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -14.0F, -13.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -11.0F, -15.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 94).addBox(-0.5F, -12.0F, -16.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -16.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -16.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -16.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-0.5F, -15.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -15.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -15.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -15.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -14.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -11.0F, -9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -3.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -9.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 94).addBox(-0.5F, -8.0F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 9.0F, 0.0F));

		PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).mirror().addBox(-2.0F, -3.0F, -2.5F, 6.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(19, 84).mirror().addBox(3.0F, -2.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(22, 81).addBox(2.0F, 7.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(36, 81).addBox(0.0F, 9.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -10.0F, 0.0F));

		PartDefinition shield = LeftArm.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(6, 105).addBox(-7.0F, -0.5F, -9.6667F, 14.0F, 1.0F, 22.0F, new CubeDeformation(0.0F))
		.texOffs(0, 124).addBox(7.0F, -0.5F, 6.3333F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 124).mirror().addBox(-10.0F, -0.5F, 6.3333F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 119).addBox(8.0F, -0.5F, 2.3333F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 114).addBox(-10.0F, -0.5F, 2.3333F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(10, 119).addBox(-9.0F, -0.5F, -8.6667F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(10, 119).mirror().addBox(7.0F, -0.5F, -8.6667F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 11.5F, 0.0F));

		PartDefinition RightLeg = root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 48).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false)
		.texOffs(48, 59).addBox(-2.1F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F))
		.texOffs(34, 64).addBox(-2.1F, 3.6F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

		PartDefinition LeftLeg = root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(32, 32).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(32, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F))
		.texOffs(48, 59).addBox(-1.9F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F))
		.texOffs(34, 64).addBox(-1.9F, 3.6F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof BastionGuardServant guard) {
			this.setupGuard(guard.hasShield(), guard.isMeleeAttacking(),
					guard.shieldBreakAnimationState.isStarted(), guard.walkAnimation.isMoving(),
					guard.idleAnimationState, guard.noShieldIdleAnimationState,
					guard.attackAnimationState, guard.attackNoShieldAnimationState,
					guard.comboAnimationState, guard.shieldBreakAnimationState,
					guard.getCurrentAnimation() == BastionGuardServant.ANIM_GUARD_IDLE,
					guard.guardIdleAnimationState,
					limbSwing, limbSwingAmount, ageInTicks,
					actionFade(guard.actionFadeStart, BastionGuardServant.ACTION_FADE_TICKS, ageInTicks));
		} else if (entity instanceof ZBastionGuardServant guard) {
			this.setupGuard(guard.hasShield(), guard.isMeleeAttacking(),
					guard.shieldBreakAnimationState.isStarted(), guard.walkAnimation.isMoving(),
					guard.idleAnimationState, guard.noShieldIdleAnimationState,
					guard.attackAnimationState, guard.attackNoShieldAnimationState,
					guard.comboAnimationState, guard.shieldBreakAnimationState,
					guard.getCurrentAnimation() == ZBastionGuardServant.ANIM_GUARD_IDLE,
					guard.guardIdleAnimationState,
					limbSwing, limbSwingAmount, ageInTicks,
					actionFade(guard.actionFadeStart, ZBastionGuardServant.ACTION_FADE_TICKS, ageInTicks));
		}
		if (!entity.isDeadOrDying()) {
			this.head.yRot += netHeadYaw * ((float) Math.PI / 180F);
			this.head.xRot += headPitch * ((float) Math.PI / 180F);
		}
	}

	private void setupGuard(boolean shielded, boolean attacking, boolean breaking, boolean moving,
							AnimationState idle, AnimationState noShieldIdle,
							AnimationState attack, AnimationState attackNoShield,
							AnimationState combo, AnimationState shieldBreak,
							boolean guardIdle, AnimationState guardIdleState,
							float limbSwing, float limbSwingAmount, float ageInTicks, float actionFade) {
		boolean shieldStance = shielded || breaking;
		this.shield.visible = shieldStance;
		AnimationState actionState = null;
		AnimationDefinition actionDefinition = null;
		float actionWeight = 0.0F;
		if (breaking) {
			actionWeight = this.fadeInWeight(shieldBreak, ageInTicks, 4.0F);
			actionState = shieldBreak;
			actionDefinition = BastionGuardAnimation.SHIELD_BREAK;
		} else if (attacking) {
			if (attack.isStarted()) {
				actionWeight = this.fadeInWeight(attack, ageInTicks, 3.0F);
				actionState = attack;
				actionDefinition = BastionGuardAnimation.ATTACK_SHIELD;
			} else if (attackNoShield.isStarted()) {
				actionWeight = this.fadeInWeight(attackNoShield, ageInTicks, 3.0F);
				actionState = attackNoShield;
				actionDefinition = BastionGuardAnimation.ATTACK_NO_SHIELD;
			} else if (combo.isStarted()) {
				actionWeight = this.fadeInWeight(combo, ageInTicks, 3.0F);
				actionState = combo;
				actionDefinition = BastionGuardAnimation.COMBO_NO_SHIELD;
			}
		} else if (actionFade < 1.0F) {
			if (attack.isStarted()) {
				actionState = attack;
				actionDefinition = BastionGuardAnimation.ATTACK_SHIELD;
			} else if (attackNoShield.isStarted()) {
				actionState = attackNoShield;
				actionDefinition = BastionGuardAnimation.ATTACK_NO_SHIELD;
			} else if (combo.isStarted()) {
				actionState = combo;
				actionDefinition = BastionGuardAnimation.COMBO_NO_SHIELD;
			}
			actionWeight = 1.0F - actionFade;
		}
		float walkWeight = moving ? Math.min(1.0F, limbSwingAmount * (shieldStance ? 4.0F : 9.6F)) : 0.0F;
		float idleWeight = 1.0F - walkWeight;
		if (guardIdle && shieldStance && !breaking) {
			guardIdleState.updateTime(ageInTicks, 1.0F);
			if (idleWeight > 0.0F) {
				long guardTime = 3000L + guardIdleState.getAccumulatedTime();
				KeyframeAnimations.animate(this, BastionGuardAnimation.IDLE_SHIELD_GUARD, guardTime, idleWeight, ANIMATION_VECTOR);
			}
		} else {
			this.animateIdle(shieldStance ? idle : noShieldIdle,
					shieldStance ? BastionGuardAnimation.IDLE_SHIELD : BastionGuardAnimation.IDLE_NO_SHIELD,
					ageInTicks, idleWeight);
		}
		if (walkWeight > 0.0F) {
			this.animateWalk(shieldStance ? BastionGuardAnimation.WALK_SHIELD : BastionGuardAnimation.WALK_NO_SHIELD,
					limbSwing, walkWeight, 1.5F, 1.0F);
		}
		if (actionState == null || actionWeight <= 0.0F) {
			return;
		}
		if (actionWeight < 1.0F) {
			this.savePose();
		}
		this.root().getAllParts().forEach(ModelPart::resetPose);
		KeyframeAnimations.animate(this, actionDefinition, actionState.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR);
		if (actionWeight < 1.0F) {
			this.blendPose(actionWeight);
		}
	}

	private static float actionFade(int fadeStart, int fadeTicks, float ageInTicks) {
		if (fadeStart < 0) {
			return 1.0F;
		}
		return Math.max(0.0F, Math.min(1.0F, (ageInTicks - fadeStart) / (float) fadeTicks));
	}

	private float fadeInWeight(AnimationState state, float ageInTicks, float fadeTicks) {
		state.updateTime(ageInTicks, 1.0F);
		if (!state.isStarted()) {
			return 0.0F;
		}
		return Math.min(1.0F, (float) state.getAccumulatedTime() / (50.0F * fadeTicks));
	}

	private void animateIdle(AnimationState state, AnimationDefinition definition, float ageInTicks, float weight) {
		state.updateTime(ageInTicks, 1.0F);
		if (weight > 0.0F && state.isStarted()) {
			KeyframeAnimations.animate(this, definition, state.getAccumulatedTime(), weight, ANIMATION_VECTOR);
		}
	}

	private List<ModelPart> animationParts() {
		if (this.animationParts == null) {
			this.animationParts = this.root().getAllParts().toList();
			this.poseSnapshot = new float[this.animationParts.size() * 9];
		}
		return this.animationParts;
	}

	private void savePose() {
		List<ModelPart> parts = this.animationParts();
		float[] snapshot = this.poseSnapshot;
		int index = 0;
		for (ModelPart part : parts) {
			snapshot[index++] = part.x;
			snapshot[index++] = part.y;
			snapshot[index++] = part.z;
			snapshot[index++] = part.xRot;
			snapshot[index++] = part.yRot;
			snapshot[index++] = part.zRot;
			snapshot[index++] = part.xScale;
			snapshot[index++] = part.yScale;
			snapshot[index++] = part.zScale;
		}
	}

	private void blendPose(float weight) {
		List<ModelPart> parts = this.animationParts();
		float[] snapshot = this.poseSnapshot;
		int index = 0;
		for (ModelPart part : parts) {
			part.x = snapshot[index] + weight * (part.x - snapshot[index]);
			part.y = snapshot[index + 1] + weight * (part.y - snapshot[index + 1]);
			part.z = snapshot[index + 2] + weight * (part.z - snapshot[index + 2]);
			Quaternionf from = ANIMATION_ROTATION_FROM.rotationZYX(snapshot[index + 5], snapshot[index + 4], snapshot[index + 3]);
			Quaternionf to = ANIMATION_ROTATION_TO.rotationZYX(part.zRot, part.yRot, part.xRot);
			from.slerp(to, weight);
			eulerZYX(from, ANIMATION_EULER);
			part.xRot = ANIMATION_EULER.x;
			part.yRot = ANIMATION_EULER.y;
			part.zRot = ANIMATION_EULER.z;
			part.xScale = snapshot[index + 6] + weight * (part.xScale - snapshot[index + 6]);
			part.yScale = snapshot[index + 7] + weight * (part.yScale - snapshot[index + 7]);
			part.zScale = snapshot[index + 8] + weight * (part.zScale - snapshot[index + 8]);
			index += 9;
		}
	}

	private static void eulerZYX(Quaternionf quaternion, Vector3f dest) {
		float x = quaternion.x;
		float y = quaternion.y;
		float z = quaternion.z;
		float w = quaternion.w;
		dest.x = (float) Math.atan2(2.0F * (y * z + w * x), 1.0F - 2.0F * (x * x + y * y));
		dest.y = (float) Math.asin(Math.max(-1.0F, Math.min(1.0F, -2.0F * (x * z - w * y))));
		dest.z = (float) Math.atan2(2.0F * (x * y + w * z), 1.0F - 2.0F * (y * y + z * z));
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}