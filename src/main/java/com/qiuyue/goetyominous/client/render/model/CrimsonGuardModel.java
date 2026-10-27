package com.qiuyue.goetyominous.client.render.model;

import com.qiuyue.goetyominous.client.render.model.animation.CrimsonGuardAnimation;
import com.qiuyue.goetyominous.common.entities.ally.illager.CrimsonGuardServant;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.CrimsonGuard;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrimsonGuardModel<T extends Mob> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart head;

	public CrimsonGuardModel(ModelPart root) {
		this.root = root;
		ModelPart body = root.getChild("body");
		this.head = body.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(60, 20).addBox(-5.0F, -12.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(65, 63).addBox(-1.0F, -2.0F, -1.0F, 10.0F, 6.0F, 4.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(-4.0F, -8.6193F, -4.3061F, 0.5672F, 0.0F, 0.0F));

		PartDefinition RightArm = body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.05F, -1.95F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(61, 45).addBox(-5.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(92, 101).addBox(-5.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.25F))
		.texOffs(10, 75).addBox(-7.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(89, 44).addBox(-5.0F, -4.0F, -3.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -10.0F, 0.0F));

		PartDefinition leftArm_r1 = RightArm.addOrReplaceChild("leftArm_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-3.5F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 47).addBox(-3.5F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -0.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition weapon = RightArm.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(0, 113).addBox(-0.5F, 3.3431F, 1.8432F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 113).addBox(-0.5F, 2.3431F, 2.8432F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 113).addBox(-0.5F, 2.3431F, 0.8431F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, 2.3431F, 1.8432F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -4.6568F, -0.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 117).addBox(-0.5F, -5.6568F, -0.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 117).addBox(-0.5F, 0.3431F, -6.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -4.6568F, -1.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -3.6568F, -2.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -2.6568F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 113).addBox(-0.5F, -5.6568F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -5.6568F, -5.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -8.6568F, -6.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -7.6568F, -7.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -8.6568F, -8.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -9.6568F, -9.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -10.6568F, -10.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 82).addBox(-0.5F, -10.6568F, -11.1569F, 1.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -11.6568F, -11.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 117).addBox(-0.5F, -11.6568F, -12.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 119).addBox(-0.5F, -12.6568F, -13.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -12.6568F, -12.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -13.6568F, -13.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 119).addBox(-0.5F, -13.6568F, -14.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 119).addBox(-0.5F, -13.6568F, -14.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -15.6568F, -15.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, -15.6568F, -16.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -16.6568F, -16.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 113).addBox(-0.5F, -16.6568F, -17.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -14.6568F, -14.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 115).addBox(-0.5F, -14.6568F, -15.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 115).addBox(-0.5F, -5.6568F, -9.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 113).addBox(-0.5F, -2.6568F, -5.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 113).addBox(-0.5F, -3.6568F, -6.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 121).addBox(-0.5F, -4.6568F, -3.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 121).addBox(-0.5F, -4.6568F, -3.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 121).addBox(-0.5F, -3.6568F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -3.6568F, -5.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -4.6568F, -6.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -5.6568F, -7.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -6.6568F, -8.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -7.6568F, -9.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -8.6568F, -10.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -9.6568F, -11.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -10.6568F, -12.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -12.6568F, -14.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -13.6568F, -15.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -14.6568F, -16.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -15.6568F, -17.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 117).addBox(-0.5F, -11.6568F, -13.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 119).addBox(-0.5F, -4.6568F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 119).addBox(-0.5F, -6.6568F, -6.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -1.6569F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 121).addBox(-0.5F, -1.6569F, -3.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 123).addBox(-0.5F, -2.6568F, -3.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 121).addBox(-0.5F, -0.6569F, -4.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 121).addBox(-0.5F, -2.6568F, -2.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 121).addBox(-0.5F, -3.6568F, -1.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -0.6569F, -5.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, 0.3431F, -5.1569F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, -3.6568F, -3.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 115).addBox(-0.5F, 1.3431F, 0.8431F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, 1.3431F, -0.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, 0.3431F, -1.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, -0.6569F, -2.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, 0.3431F, 0.8431F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, -0.6569F, -0.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 123).addBox(-0.5F, -1.6569F, -1.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 123).addBox(-0.5F, -1.6569F, -2.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 123).addBox(-0.5F, -0.6569F, -1.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 123).addBox(-0.5F, 0.3431F, -0.1568F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 113).addBox(-0.5F, 1.3431F, 1.8432F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 8.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition LeftArm = body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-0.95F, -1.95F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(61, 45).mirror().addBox(-1.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(92, 101).mirror().addBox(-1.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.25F)).mirror(false)
		.texOffs(10, 75).mirror().addBox(5.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(89, 44).mirror().addBox(2.0F, -4.0F, -3.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, -10.0F, 0.0F));

		PartDefinition leftArm_r2 = LeftArm.addOrReplaceChild("leftArm_r2", CubeListBuilder.create().texOffs(0, 47).addBox(-3.5F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-3.5F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Head = body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(104, 13).addBox(-2.0F, -13.5F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(4, 71).addBox(-8.0F, -12.5F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(11, 71).addBox(-8.0F, -9.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(4, 71).addBox(7.0F, -12.5F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(11, 71).mirror().addBox(5.0F, -9.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(29, 105).addBox(-5.0F, -10.5F, -5.0F, 10.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = Head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(64, 87).addBox(-6.0F, 0.0F, -1.0F, 12.0F, 22.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 4.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition leg0 = partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(18, 52).addBox(-2.5F, 3.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 56).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition leg1_r1 = leg0.addOrReplaceChild("leg1_r1", CubeListBuilder.create().texOffs(46, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(18, 52).addBox(-2.5F, 3.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 56).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition leg2_r1 = leg1.addOrReplaceChild("leg2_r1", CubeListBuilder.create().texOffs(46, 58).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (!entity.isDeadOrDying()) {
			this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
			this.head.xRot = headPitch * ((float)Math.PI / 180F);
			if (entity instanceof CrimsonGuardServant servant) {
				this.setupCrimsonGuard(servant.isMeleeAttacking(), servant.getCurrentAnimation(), servant.isMoving(),
						servant.idleAnimationState, servant.slamAnimationState, servant.sweepAnimationState,
						limbSwing, limbSwingAmount, ageInTicks);
			} else if (entity instanceof CrimsonGuard guard) {
				this.setupCrimsonGuard(guard.isMeleeAttacking(), guard.getCurrentAnimation(), guard.isMoving(),
						guard.idleAnimationState, guard.slamAnimationState, guard.sweepAnimationState,
						limbSwing, limbSwingAmount, ageInTicks);
			}
		}
	}

	private void setupCrimsonGuard(boolean attacking, int currentAnimation, boolean moving,
								   AnimationState idle, AnimationState slam, AnimationState sweep,
								   float limbSwing, float limbSwingAmount, float ageInTicks) {
		if (attacking) {
			if (currentAnimation == CrimsonGuardServant.ANIM_SLAM) {
				this.animate(slam, CrimsonGuardAnimation.SLAM, ageInTicks);
			} else if (currentAnimation == CrimsonGuardServant.ANIM_SWEEP) {
				this.animate(sweep, CrimsonGuardAnimation.SWEEP, ageInTicks);
			}
		} else if (moving) {
			this.animateWalk(CrimsonGuardAnimation.WALK, limbSwing, limbSwingAmount, 2.5F, 3.5F);
		} else {
			this.animate(idle, CrimsonGuardAnimation.IDLE, ageInTicks);
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
