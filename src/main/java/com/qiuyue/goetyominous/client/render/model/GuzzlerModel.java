package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.animation.GuzzlerAnimation;
import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZGuzzler;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
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
public class GuzzlerModel<T extends Mob> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(GoetyOminous.MOD_ID, "guzzler"), "main");
	private static final float WALK_WEIGHT_GAIN = 4.0F;
	private final ModelPart root;
	private final ModelPart rootBone;
	private final ModelPart body;
	private final ModelPart belly;
	private final ModelPart upbody;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart eye;
	private final ModelPart eye2;
	private final ModelPart leftear;
	private final ModelPart rightear;
	private final ModelPart leftarm;
	private final ModelPart rightarm;
	private final ModelPart leg2;
	private final ModelPart leg;

	public GuzzlerModel(ModelPart root) {
		this.root = root;
		this.rootBone = root.getChild("root");
		this.body = this.rootBone.getChild("body");
		this.belly = this.body.getChild("belly");
		this.upbody = this.body.getChild("upbody");
		this.head = this.upbody.getChild("head");
		this.nose = this.head.getChild("nose");
		this.eye = this.head.getChild("eye");
		this.eye2 = this.head.getChild("eye2");
		this.leftear = this.head.getChild("leftear");
		this.rightear = this.head.getChild("rightear");
		this.leftarm = this.upbody.getChild("leftarm");
		this.rightarm = this.upbody.getChild("rightarm");
		this.leg2 = this.rootBone.getChild("leg2");
		this.leg = this.rootBone.getChild("leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(135, 10).addBox(-12.0F, 0.0F, -9.0F, 24.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
		.texOffs(0, 31).addBox(-3.0F, -3.0F, -10.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -1.0F));

		PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(33, 31).addBox(-12.0F, -9.0F, -8.5F, 24.0F, 18.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -0.5F));

		PartDefinition upbody = body.addOrReplaceChild("upbody", CubeListBuilder.create().texOffs(63, 0).addBox(-11.0F, -15.0F, -7.0F, 22.0F, 17.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(98, 13).addBox(0.0F, -23.0F, -7.0F, 0.0F, 17.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(134, 13).addBox(-1.0F, -23.0F, -6.0F, 0.0F, 17.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(134, 13).addBox(1.0F, -23.0F, -6.0F, 0.0F, 17.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));

		PartDefinition head = upbody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -9.0F, 12.0F, 13.0F, 9.0F, new CubeDeformation(-0.02F))
		.texOffs(47, 0).addBox(-5.0F, 1.0F, -12.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(63, 0).addBox(3.0F, 2.0F, -11.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(71, 0).addBox(3.0F, 1.0F, -12.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(57, 0).addBox(-5.0F, -1.0F, -13.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -7.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(33, 3).addBox(-3.0F, -4.0F, -1.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -10.0F));

		PartDefinition eye = head.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(2, 3).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -1.0F, -9.01F));

		PartDefinition eye2 = head.addOrReplaceChild("eye2", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -1.0F, -9.01F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(10, 22).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.0F, -5.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, -2.0F, -5.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition leftarm = upbody.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(217, 0).mirror().addBox(0.0F, -2.0F, -5.0F, 8.0F, 30.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(11.0F, -10.0F, 0.0F));

		PartDefinition rightarm = upbody.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(217, 40).addBox(-8.0F, -2.0F, -5.0F, 8.0F, 30.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, -10.0F, 0.0F));

		PartDefinition leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(115, 48).mirror().addBox(-4.5F, -4.0F, -5.0F, 9.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.5F, -8.0F, 0.0F));

		PartDefinition leg = root.addOrReplaceChild("leg", CubeListBuilder.create().texOffs(115, 48).addBox(-4.5F, -4.0F, -5.0F, 9.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof Guzzler guzzler) {
			this.setupGuzzler(guzzler, limbSwingAmount, ageInTicks);
			if (!entity.isDeadOrDying() && !guzzler.isSitting()) {
				this.head.yRot += netHeadYaw * ((float) Math.PI / 180F);
				this.head.xRot += headPitch * ((float) Math.PI / 180F);
			}
		} else if (entity instanceof ZGuzzler guzzler) {
			this.setupZGuzzler(guzzler, limbSwingAmount, ageInTicks);
			if (!entity.isDeadOrDying() && !guzzler.isSitting()) {
				this.head.yRot += netHeadYaw * ((float) Math.PI / 180F);
				this.head.xRot += headPitch * ((float) Math.PI / 180F);
			}
		} else if (entity instanceof GuzzlerBoss boss) {
			this.setupGuzzlerBoss(boss, limbSwingAmount, ageInTicks);
			if (!entity.isDeadOrDying() && !boss.isSitting()) {
				this.head.yRot += netHeadYaw * ((float) Math.PI / 180F);
				this.head.xRot += headPitch * ((float) Math.PI / 180F);
			}
		}
	}

	private void setupGuzzler(Guzzler guzzler, float limbSwingAmount, float ageInTicks) {
		switch (guzzler.getAnimState()) {
			case Guzzler.ANIM_SLAM -> this.animate(guzzler.slamAnimationState, GuzzlerAnimation.SLAM_ATTACK, ageInTicks);
			case Guzzler.ANIM_SPIN -> this.animate(guzzler.spinAnimationState, GuzzlerAnimation.SPIN_ATTACK, ageInTicks);
			case Guzzler.ANIM_JUMP_START -> this.animate(guzzler.jumpStartAnimationState, GuzzlerAnimation.JUMP_ATTACK_START, ageInTicks);
			case Guzzler.ANIM_JUMP_LAND -> this.animate(guzzler.jumpLandAnimationState, GuzzlerAnimation.JUMP_ATTACK_LAND, ageInTicks);
			case Guzzler.ANIM_SIT -> this.animate(guzzler.sitAnimationState, GuzzlerAnimation.SIT, ageInTicks);
			case Guzzler.ANIM_REST -> this.animate(guzzler.restAnimationState, GuzzlerAnimation.REST, ageInTicks);
			case Guzzler.ANIM_STAND_UP -> this.animate(guzzler.standUpAnimationState, GuzzlerAnimation.STAND_UP, ageInTicks);
			case Guzzler.ANIM_DEATH -> this.animate(guzzler.deathAnimationState, GuzzlerAnimation.DEATH, ageInTicks);
			default -> this.animateIdle(guzzler.idleAnimationState, guzzler.walkAnimationState, guzzler.tickCount, limbSwingAmount, ageInTicks);
		}
	}

	private void setupGuzzlerBoss(GuzzlerBoss boss, float limbSwingAmount, float ageInTicks) {
		switch (boss.getAnimState()) {
			case GuzzlerBoss.ANIM_SLAM -> this.animate(boss.slamAnimationState, GuzzlerAnimation.SLAM_ATTACK, ageInTicks);
			case GuzzlerBoss.ANIM_SPIN -> this.animate(boss.spinAnimationState, GuzzlerAnimation.SPIN_ATTACK, ageInTicks);
			case GuzzlerBoss.ANIM_JUMP_START -> this.animate(boss.jumpStartAnimationState, GuzzlerAnimation.JUMP_ATTACK_START, ageInTicks);
			case GuzzlerBoss.ANIM_JUMP_LAND -> this.animate(boss.jumpLandAnimationState, GuzzlerAnimation.JUMP_ATTACK_LAND, ageInTicks);
			case GuzzlerBoss.ANIM_SIT -> this.animate(boss.sitAnimationState, GuzzlerAnimation.SIT, ageInTicks);
			case GuzzlerBoss.ANIM_REST -> this.animate(boss.restAnimationState, GuzzlerAnimation.REST, ageInTicks);
			case GuzzlerBoss.ANIM_STAND_UP -> this.animate(boss.standUpAnimationState, GuzzlerAnimation.STAND_UP, ageInTicks);
			case GuzzlerBoss.ANIM_DEATH -> this.animate(boss.deathAnimationState, GuzzlerAnimation.DEATH, ageInTicks);
			default -> this.animateIdle(boss.idleAnimationState, boss.walkAnimationState, boss.tickCount, limbSwingAmount, ageInTicks);
		}
	}

	private void setupZGuzzler(ZGuzzler guzzler, float limbSwingAmount, float ageInTicks) {
		switch (guzzler.getAnimState()) {
			case ZGuzzler.ANIM_SLAM -> this.animate(guzzler.slamAnimationState, GuzzlerAnimation.SLAM_ATTACK, ageInTicks);
			case ZGuzzler.ANIM_SPIN -> this.animate(guzzler.spinAnimationState, GuzzlerAnimation.SPIN_ATTACK, ageInTicks);
			case ZGuzzler.ANIM_JUMP_START -> this.animate(guzzler.jumpStartAnimationState, GuzzlerAnimation.JUMP_ATTACK_START, ageInTicks);
			case ZGuzzler.ANIM_JUMP_LAND -> this.animate(guzzler.jumpLandAnimationState, GuzzlerAnimation.JUMP_ATTACK_LAND, ageInTicks);
			case ZGuzzler.ANIM_SIT -> this.animate(guzzler.sitAnimationState, GuzzlerAnimation.SIT, ageInTicks);
			case ZGuzzler.ANIM_REST -> this.animate(guzzler.restAnimationState, GuzzlerAnimation.REST, ageInTicks);
			case ZGuzzler.ANIM_STAND_UP -> this.animate(guzzler.standUpAnimationState, GuzzlerAnimation.STAND_UP, ageInTicks);
			case ZGuzzler.ANIM_DEATH -> this.animate(guzzler.deathAnimationState, GuzzlerAnimation.DEATH, ageInTicks);
			default -> this.animateIdle(guzzler.idleAnimationState, guzzler.walkAnimationState, guzzler.tickCount, limbSwingAmount, ageInTicks);
		}
	}

	private void animateIdle(AnimationState idle, AnimationState walk, int tickCount, float limbSwingAmount, float ageInTicks) {
		idle.startIfStopped(tickCount);
		walk.startIfStopped(tickCount);
		float walkWeight = Math.min(limbSwingAmount * WALK_WEIGHT_GAIN, 1.0F);
		PoseBlend.animate(this, idle, GuzzlerAnimation.IDLE, ageInTicks, 1.0F - walkWeight);
		PoseBlend.animate(this, walk, GuzzlerAnimation.WALK, ageInTicks, walkWeight);
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
