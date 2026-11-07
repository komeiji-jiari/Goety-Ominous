package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.PiglinDrummerAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.PoseBlend;
import com.qiuyue.goetyominous.common.entities.ally.mobs.PiglinDrummer;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZPiglinDrummer;
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
public class PiglinDrummerModel<T extends Entity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyominous", "piglin_drummer"), "main");

    private final ModelPart root;
    private final ModelPart Body;
    private final ModelPart drum;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart leftear;
    private final ModelPart rightear;
    private final ModelPart RightArm;
    private final ModelPart bone;
    private final ModelPart LeftArm;
    private final ModelPart bone2;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public PiglinDrummerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.Body = this.root.getChild("Body");
        this.drum = this.Body.getChild("drum");
        this.head = this.Body.getChild("head");
        this.nose = this.head.getChild("nose");
        this.leftear = this.head.getChild("leftear");
        this.rightear = this.head.getChild("rightear");
        this.RightArm = this.Body.getChild("RightArm");
        this.bone = this.RightArm.getChild("bone");
        this.LeftArm = this.Body.getChild("LeftArm");
        this.bone2 = this.LeftArm.getChild("bone2");
        this.RightLeg = this.root.getChild("RightLeg");
        this.LeftLeg = this.root.getChild("LeftLeg");
    }
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition drum = Body.addOrReplaceChild("drum", CubeListBuilder.create().texOffs(0, 59).addBox(-5.0F, -3.5F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, -7.0F));

		PartDefinition head = Body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(31, 1).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 4).addBox(2.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(-3.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create().texOffs(51, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create().texOffs(39, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition bone = RightArm.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(1, 77).addBox(-0.5F, 2.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 2.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).addBox(-0.5F, -5.0F, -5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).addBox(-0.5F, -6.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 9.0F, 0.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 85).addBox(-1.5F, -1.5F, -2.3F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5F, -5.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition bone2 = LeftArm.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(1, 77).addBox(-0.5F, 2.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 2.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 77).addBox(-0.5F, -3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 79).addBox(-0.5F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 77).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, -4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 79).addBox(-0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).addBox(-0.5F, -5.0F, -5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).addBox(-0.5F, -6.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 9.0F, 0.0F));

		PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 85).addBox(-1.5F, -1.5F, -2.3F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5F, -5.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition RightLeg = root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offset(-1.9F, -12.0F, 0.0F));

		PartDefinition LeftLeg = root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        if (entity instanceof PiglinDrummer drummer) {
            this.setupDrummer(entity.tickCount, ageInTicks,
                    drummer.idleAnimationState, drummer.walkAnimationState,
                    drummer.drumAnimationState, drummer.cheerAnimationState,
                    drummer.isDrumming(), drummer.isCheering(), drummer.getWalkWeight());
        } else if (entity instanceof ZPiglinDrummer drummer) {
            this.setupDrummer(entity.tickCount, ageInTicks,
                    drummer.idleAnimationState, drummer.walkAnimationState,
                    drummer.drumAnimationState, drummer.cheerAnimationState,
                    drummer.isDrumming(), drummer.isCheering(), drummer.getWalkWeight());
        }
    }

    private void setupDrummer(int tickCount, float ageInTicks,
            AnimationState idle, AnimationState walk, AnimationState drum, AnimationState cheer,
            boolean drumming, boolean cheering, float walkWeight) {
        idle.updateTime(ageInTicks, 1.0F);
        walk.updateTime(ageInTicks, 1.0F);
        if (drumming) {
            drum.startIfStopped(tickCount);
            this.animate(drum, PiglinDrummerAnimations.DRUM, ageInTicks);
        } else if (cheering) {
            cheer.startIfStopped(tickCount);
            this.animate(cheer, PiglinDrummerAnimations.CHEER, ageInTicks);
        } else {
            idle.startIfStopped(tickCount);
            walk.startIfStopped(tickCount);
            PoseBlend.animate(this, idle, PiglinDrummerAnimations.IDLE, ageInTicks, 1.0F - walkWeight);
            PoseBlend.animate(this, walk, PiglinDrummerAnimations.WALK, ageInTicks, walkWeight);
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