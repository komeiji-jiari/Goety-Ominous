package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.EliteFungusThrowerAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import com.qiuyue.goetyominous.common.entities.ally.mobs.EliteFungusThrower;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZEliteFungusThrower;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EliteFungusThrowerModel<T extends Entity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyominous", "elite_fungus_thrower"), "main");
    private final ModelPart root;
	private final ModelPart Body;
	private final ModelPart bag;
	private final ModelPart mushroom2;
	private final ModelPart mushroom;
	private final ModelPart mushroom3;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart leftear;
	private final ModelPart rightear;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public EliteFungusThrowerModel(ModelPart root) {
		this.root = root.getChild("root");
		this.Body = this.root.getChild("Body");
		this.bag = this.Body.getChild("bag");
		this.mushroom2 = this.bag.getChild("mushroom2");
		this.mushroom = this.bag.getChild("mushroom");
		this.mushroom3 = this.bag.getChild("mushroom3");
		this.head = this.Body.getChild("head");
		this.nose = this.head.getChild("nose");
		this.leftear = this.head.getChild("leftear");
		this.rightear = this.head.getChild("rightear");
		this.RightArm = this.Body.getChild("RightArm");
		this.LeftArm = this.Body.getChild("LeftArm");
		this.RightLeg = this.root.getChild("RightLeg");
		this.LeftLeg = this.root.getChild("LeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bag = Body.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(32, 48).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 3.0F));

		PartDefinition mushroom2 = bag.addOrReplaceChild("mushroom2", CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, -2.25F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.3F))
		.texOffs(4, 66).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-2.0F, -4.95F, 1.0F, -0.1314F, -0.0865F, -0.2067F));

		PartDefinition mushroom = bag.addOrReplaceChild("mushroom", CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, -2.25F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.4F))
		.texOffs(4, 66).addBox(-1.0F, -0.25F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(2.0F, -4.05F, 3.0F, 0.0F, -0.0403F, 0.3003F));

		PartDefinition mushroom3 = bag.addOrReplaceChild("mushroom3", CubeListBuilder.create().texOffs(16, 67).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F))
		.texOffs(16, 61).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(-0.1F))
		.texOffs(20, 66).addBox(-1.5F, -3.2F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.5F, -3.5F, 4.5F, -0.2245F, -0.2932F, -0.1128F));

		PartDefinition head = Body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(31, 1).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 4).addBox(-3.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(51, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(39, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition RightLeg = root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 48).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

		PartDefinition LeftLeg = root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 48).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        if (entity instanceof EliteFungusThrower thrower) {
            this.setupThrower(entity.tickCount, ageInTicks,
                    thrower.idleAnimationState, thrower.walkAnimationState,
                    thrower.throwAnimationState, thrower.healAnimationState,
                    thrower.isThrowing(), thrower.isHealing(), thrower.getWalkWeight());
        } else if (entity instanceof ZEliteFungusThrower thrower) {
            this.setupThrower(entity.tickCount, ageInTicks,
                    thrower.idleAnimationState, thrower.walkAnimationState,
                    thrower.throwAnimationState, thrower.healAnimationState,
                    thrower.isThrowing(), thrower.isHealing(), thrower.getWalkWeight());
        }
    }

    private void setupThrower(int tickCount, float ageInTicks,
            AnimationState idle, AnimationState walk, AnimationState throwingState, AnimationState heal,
            boolean throwing, boolean healing, float walkWeight) {
        if (throwing) {
            throwingState.startIfStopped(tickCount);
            this.animate(throwingState, EliteFungusThrowerAnimations.THROW, ageInTicks);
        } else if (healing) {
            heal.startIfStopped(tickCount);
            this.animate(heal, EliteFungusThrowerAnimations.HEAL_THROW, ageInTicks);
        } else {
            idle.startIfStopped(tickCount);
            walk.startIfStopped(tickCount);
            PoseBlend.animate(this, idle, EliteFungusThrowerAnimations.IDLE, ageInTicks, 1.0F - walkWeight);
            PoseBlend.animate(this, walk, EliteFungusThrowerAnimations.WALK, ageInTicks, walkWeight);
        }
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