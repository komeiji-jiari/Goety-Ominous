package com.qiuyue.goetyominous.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.animation.PyrologerAnimation;
import com.qiuyue.goetyominous.common.entities.ally.illager.PyrologerServant;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.Pyrologer;
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

@OnlyIn(Dist.CLIENT)
public class PyrologerModel<T extends Mob> extends HierarchicalModel<T> implements HierarchicalArmor {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GoetyOminous.MOD_ID, "pyrologer"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart Head;
	private final ModelPart right_eyebrows;
	private final ModelPart left_eyebrows;
	private final ModelPart nose;
	private final ModelPart right_eye;
	private final ModelPart right_eye1;
	private final ModelPart right_eye2;
	private final ModelPart left_eye;
	private final ModelPart left_eye1;
	private final ModelPart left_eye2;
	private final ModelPart hat;
	private final ModelPart hat1;
	private final ModelPart hat2;
	private final ModelPart arms;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart leg0;
	private final ModelPart leg1;
	private final ModelPart banner;
	private final ModelPart drop;

	public PyrologerModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.Head = this.body.getChild("Head");
		this.right_eyebrows = this.Head.getChild("right_eyebrows");
		this.left_eyebrows = this.Head.getChild("left_eyebrows");
		this.nose = this.Head.getChild("nose");
		this.right_eye = this.Head.getChild("right_eye");
		this.right_eye1 = this.right_eye.getChild("right_eye1");
		this.right_eye2 = this.right_eye.getChild("right_eye2");
		this.left_eye = this.Head.getChild("left_eye");
		this.left_eye1 = this.left_eye.getChild("left_eye1");
		this.left_eye2 = this.left_eye.getChild("left_eye2");
		this.hat = this.Head.getChild("hat");
		this.hat1 = this.hat.getChild("hat1");
		this.hat2 = this.hat1.getChild("hat2");
		this.arms = this.body.getChild("arms");
		this.RightArm = this.body.getChild("RightArm");
		this.LeftArm = this.body.getChild("LeftArm");
		this.leg0 = this.root.getChild("leg0");
		this.leg1 = this.root.getChild("leg1");
		this.banner = this.body.getChild("banner");
		this.drop = this.banner.getChild("drop");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-4.0F, -11.75F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Head = body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition right_eyebrows = Head.addOrReplaceChild("right_eyebrows", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -0.05F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -4.0F, -4.0F));

		PartDefinition left_eyebrows = Head.addOrReplaceChild("left_eyebrows", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -2.0F, -0.05F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -4.0F, -4.0F));

		PartDefinition nose = Head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(2.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -2.0F, 0.0F));

		PartDefinition right_eye = Head.addOrReplaceChild("right_eye", CubeListBuilder.create(), PartPose.offset(-2.0F, -3.5F, -4.0F));

		PartDefinition right_eye1 = right_eye.addOrReplaceChild("right_eye1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -0.5F, -0.02F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_eye2 = right_eye.addOrReplaceChild("right_eye2", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -0.5F, -0.035F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.0F));

		PartDefinition left_eye = Head.addOrReplaceChild("left_eye", CubeListBuilder.create(), PartPose.offset(2.0F, -3.5F, -4.0F));

		PartDefinition left_eye1 = left_eye.addOrReplaceChild("left_eye1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -0.5F, -0.02F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_eye2 = left_eye.addOrReplaceChild("left_eye2", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -0.5F, -0.035F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.0F));

		PartDefinition hat = Head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -9.6F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.0F, -10.1F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat1 = hat.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(76, 36).addBox(-4.0F, -12.0F, -3.5F, 8.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.5F, 0.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition hat2 = hat1.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(89, 22).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 3.5F, -0.6545F, 0.0F, 0.0F));

		PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 46).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
		.texOffs(56, 46).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
		.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, -1.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition RightArm = body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition LeftArm = body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(56, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));

		PartDefinition banner = body.addOrReplaceChild("banner", CubeListBuilder.create().texOffs(120, 64).addBox(-1.0F, -48.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(120, 82).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(64, 108).addBox(-10.0F, -50.0F, -1.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 6.0F));

		PartDefinition drop = banner.addOrReplaceChild("drop", CubeListBuilder.create().texOffs(0, 80).addBox(-12.0F, 0.0F, 0.0F, 24.0F, 48.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(24, 78).addBox(-12.0F, 0.0F, -1.0F, 24.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(69, 70).addBox(-12.0F, 1.0F, -1.0F, 24.0F, 36.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 77).addBox(-11.0F, 37.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-10.0F, 38.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-9.0F, 39.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-8.0F, 40.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-7.0F, 41.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-6.0F, 42.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 77).addBox(-5.0F, 43.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(-4.0F, 44.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(-3.0F, 45.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(-2.0F, 46.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(-1.0F, 47.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(0.0F, 47.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(1.0F, 46.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(2.0F, 45.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(3.0F, 44.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(4.0F, 43.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(5.0F, 42.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(6.0F, 41.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(7.0F, 40.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(8.0F, 39.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(9.0F, 38.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(9, 77).addBox(10.0F, 37.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 80).addBox(-12.0F, 0.0F, -1.0F, 24.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -50.0F, -1.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root.getAllParts().forEach(ModelPart::resetPose);
		boolean showModelBanner = entity instanceof Pyrologer;
		this.banner.visible = showModelBanner;
		this.drop.visible = showModelBanner;
		this.arms.visible = false;
		this.Head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
		this.Head.xRot = headPitch * ((float) Math.PI / 180.0F);
		int currentAnimation;
		AnimationState idleState;
		AnimationState fireWallState;
		AnimationState fireballState;
		AnimationState fieryHideState;
		AnimationState fireBreathStartState;
		AnimationState fireBreathLoopState;
		AnimationState fireBreathEndState;
		AnimationState deathState;
		if (entity instanceof PyrologerServant servant) {
			currentAnimation = servant.getCurrentAnimation();
			idleState = servant.getIdleState();
			fireWallState = servant.getFireWallState();
			fireballState = servant.getFireballState();
			fieryHideState = servant.getFieryHideState();
			fireBreathStartState = servant.getFireBreathStartState();
			fireBreathLoopState = servant.getFireBreathLoopState();
			fireBreathEndState = servant.getFireBreathEndState();
			deathState = servant.getDeathState();
		} else if (entity instanceof Pyrologer pyrologer) {
			currentAnimation = pyrologer.getCurrentAnimation();
			idleState = pyrologer.getIdleState();
			fireWallState = pyrologer.getFireWallState();
			fireballState = pyrologer.getFireballState();
			fieryHideState = pyrologer.getFieryHideState();
			fireBreathStartState = pyrologer.getFireBreathStartState();
			fireBreathLoopState = pyrologer.getFireBreathLoopState();
			fireBreathEndState = pyrologer.getFireBreathEndState();
			deathState = pyrologer.getDeathState();
		} else {
			return;
		}
		if (deathState.isStarted()) {
			this.animate(deathState, PyrologerAnimation.UNKNOWN, ageInTicks, 1.0F);
			return;
		}
		this.animate(idleState, PyrologerAnimation.IDLE, ageInTicks, 1.0F);
		this.animate(fireWallState, PyrologerAnimation.FIRE_WALL, ageInTicks, 1.0F);
		this.animate(fireballState, PyrologerAnimation.FIREBALL, ageInTicks, 1.0F);
		this.animate(fieryHideState, PyrologerAnimation.FIERY_HIDE, ageInTicks, 1.0F);
		this.animate(fireBreathStartState, PyrologerAnimation.FIRE_BREATH_START, ageInTicks, 1.0F);
		this.animate(fireBreathLoopState, PyrologerAnimation.FIRE_BREATH_LOOP, ageInTicks, 1.0F);
		this.animate(fireBreathEndState, PyrologerAnimation.FIRE_BREATH_END, ageInTicks, 1.0F);
		if (currentAnimation == 0) {
			this.animateWalk(PyrologerAnimation.WALK, limbSwing, limbSwingAmount, 2.5F, 20.0F);
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void translateToHead(ModelPart part, PoseStack poseStack) {
		this.root.translateAndRotate(poseStack);
		this.body.translateAndRotate(poseStack);
		part.translateAndRotate(poseStack);
	}

	@Override
	public void translateToChest(ModelPart part, PoseStack poseStack) {
	}

	@Override
	public void translateToLeg(ModelPart part, PoseStack poseStack) {
	}

	@Override
	public void translateToArms(ModelPart part, PoseStack poseStack) {
	}

	@Override
	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.Head);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}