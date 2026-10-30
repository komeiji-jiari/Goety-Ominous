package com.qiuyue.goetyominous.client.render.model;

import com.qiuyue.goetyominous.client.render.model.animation.ImperialChasseurAnimation;
import com.qiuyue.goetyominous.common.entities.ally.illager.ImperialChasseurServant;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ImperialChasseur;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class ImperialChasseurModel<T extends Mob> extends HierarchicalModel<T> {
    private static final Vector3f ANIMATION_VEC = new Vector3f();
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart bone;
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
    private final ModelPart RightArm;
    private final ModelPart bone5;
    private final ModelPart bone4;
    private final ModelPart LeftArm;
    private final ModelPart bone6;
    private final ModelPart bone7;
    private final ModelPart bone8;
    private final ModelPart bone9;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart leg0;
    private final ModelPart leg1;

    public ImperialChasseurModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.bone = this.body.getChild("bone");
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
        this.RightArm = this.body.getChild("RightArm");
        this.bone5 = this.RightArm.getChild("bone5");
        this.bone4 = this.bone5.getChild("bone4");
        this.LeftArm = this.body.getChild("LeftArm");
        this.bone6 = this.LeftArm.getChild("bone6");
        this.bone7 = this.bone6.getChild("bone7");
        this.bone8 = this.bone7.getChild("bone8");
        this.bone9 = this.bone7.getChild("bone9");
        this.bone2 = this.body.getChild("bone2");
        this.bone3 = this.body.getChild("bone3");
        this.leg0 = this.root.getChild("leg0");
        this.leg1 = this.root.getChild("leg1");
    }

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-4.0F, -11.75F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bone = body.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(56, 40).addBox(-2.5F, -1.5F, -1.25F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -7.0F, -4.0F, 0.0F, 0.0F, 0.6545F));

		PartDefinition Head = body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition right_eyebrows = Head.addOrReplaceChild("right_eyebrows", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -0.05F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -4.0F, -4.0F));

		PartDefinition left_eyebrows = Head.addOrReplaceChild("left_eyebrows", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, -2.0F, -0.05F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -4.0F, -4.0F));

		PartDefinition nose = Head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(2.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -2.0F, 0.0F));

		PartDefinition right_eye = Head.addOrReplaceChild("right_eye", CubeListBuilder.create(), PartPose.offset(-2.0F, -3.5F, -4.0F));

		PartDefinition right_eye1 = right_eye.addOrReplaceChild("right_eye1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -0.5F, -0.02F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_eye2 = right_eye.addOrReplaceChild("right_eye2", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -0.5F, -0.035F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.0F));

		PartDefinition left_eye = Head.addOrReplaceChild("left_eye", CubeListBuilder.create(), PartPose.offset(2.0F, -3.5F, -4.0F));

		PartDefinition left_eye1 = left_eye.addOrReplaceChild("left_eye1", CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, -0.5F, -0.02F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_eye2 = left_eye.addOrReplaceChild("left_eye2", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, -0.5F, -0.035F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.0F));

		PartDefinition RightArm = body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(44, 22).addBox(-3.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 46).addBox(-3.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
		.texOffs(76, 21).mirror().addBox(-6.0F, -3.0F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition bone5 = RightArm.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(-1.0F, 8.5F, -1.5F));

		PartDefinition bone4 = bone5.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(75, 52).addBox(-3.0F, -13.5F, 0.5F, 19.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(75, 52).addBox(-3.0F, -13.5F, -0.5F, 19.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(1, 75).addBox(-3.0F, 1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 75).addBox(-2.0F, 2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 75).addBox(-1.0F, 1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 75).addBox(-1.0F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 75).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 75).addBox(1.0F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 75).addBox(0.0F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 75).addBox(0.0F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 75).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 75).addBox(-2.0F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 73).addBox(2.0F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 71).addBox(4.0F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 71).addBox(5.0F, -7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 71).addBox(6.0F, -8.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 69).addBox(7.0F, -9.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 69).addBox(8.0F, -10.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 69).addBox(13.0F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 69).addBox(14.0F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 69).addBox(12.0F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 69).addBox(10.0F, -12.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 69).addBox(9.0F, -11.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 69).addBox(11.0F, -12.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 71).addBox(3.0F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 73).addBox(3.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 73).addBox(3.0F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 73).addBox(3.0F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 73).addBox(0.0F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 73).addBox(0.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 73).addBox(2.0F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 71).addBox(1.0F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(4.0F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(5.0F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(6.0F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(7.0F, -7.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(8.0F, -8.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(9.0F, -9.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(10.0F, -10.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(11.0F, -11.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(12.0F, -11.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(13.0F, -12.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(14.0F, -12.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 71).addBox(15.0F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -1.5708F, -0.7854F, -1.5708F));

		PartDefinition LeftArm = body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(60, 22).mirror().addBox(-0.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(56, 46).mirror().addBox(-0.5F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false)
		.texOffs(76, 21).addBox(0.0F, -3.0F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition bone6 = LeftArm.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(2.0F, 10.0F, -2.5F));

		PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(78, 33).addBox(-13.0F, -13.0F, 0.5F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(78, 33).addBox(-13.0F, -13.0F, -0.5F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-13.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-1.0F, -13.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-12.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-12.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-4.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-3.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-12.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-12.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-11.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(-10.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(-11.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(-1.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 4).addBox(-2.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(-2.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(-3.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(0.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(0.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 4).addBox(-9.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 4).addBox(-8.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 2).addBox(-7.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 2).addBox(-4.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 4).addBox(-3.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 6).addBox(-12.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 6).addBox(-12.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 6).addBox(-5.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 4).addBox(-11.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(-6.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-11.0F, -7.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-7.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-10.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-10.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-10.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(-10.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(-4.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-9.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-8.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-6.0F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-5.0F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 2).addBox(-4.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 2).addBox(-1.0F, -4.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-1.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(0.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(1.0F, 2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(2.0F, 1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(2.0F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(1.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 0).addBox(-2.0F, -5.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 0).addBox(-3.0F, -6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 0).addBox(-5.0F, -8.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 0).addBox(-6.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 2).addBox(-5.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-8.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-3.0F, -10.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(-2.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(0.0F, -12.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 6).addBox(0.0F, -11.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-9.0F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.3562F));

		PartDefinition bone8 = bone7.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(82, 2).addBox(1.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(3.0F, -7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(5.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(4.0F, -10.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(3.0F, -11.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 2).addBox(2.0F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(4.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(6.0F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(5.0F, -5.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(6.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 0).addBox(7.0F, -7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 2).addBox(2.0F, -10.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 2).addBox(2.0F, -10.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 1.5F));

		PartDefinition bone9 = bone7.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(23, 66).addBox(1.0F, -11.0F, -3.0F, 7.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(1.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(3.0F, -7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(5.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(4.0F, -10.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(3.0F, -11.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(2.0F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 0).addBox(4.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 0).addBox(6.0F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 0).addBox(5.0F, -5.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 0).addBox(6.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 0).addBox(7.0F, -7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 2).addBox(2.0F, -10.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, 1.0F, 0.5F));

		PartDefinition bone2 = body.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(100, 6).addBox(-0.5F, 0.5F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1222F));

		PartDefinition bone3 = body.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(100, 32).addBox(-7.5F, 0.5F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1222F));

		PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(29, 39).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.25F))
		.texOffs(65, 10).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
		.texOffs(82, 15).addBox(-2.5F, 4.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(29, 39).mirror().addBox(-2.0F, 0.0F, -2.5F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false)
		.texOffs(65, 10).mirror().addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
		.texOffs(82, 15).mirror().addBox(-2.5F, 4.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.isDeadOrDying()) {
            return;
        }
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.Head.xRot = headPitch * ((float) Math.PI / 180F);
        int animState;
        AnimationState idleState;
        AnimationState reloadState;
        AnimationState meleeState;
        AnimationState meleeAimState;
        AnimationState shootState;
        AnimationState returnState;
        AnimationState aimReloadState;
        if (entity instanceof ImperialChasseurServant servant) {
            animState = servant.getAnimState();
            idleState = servant.idleAnimationState;
            reloadState = servant.reloadAnimationState;
            meleeState = servant.meleeAnimationState;
            meleeAimState = servant.meleeAimAnimationState;
            shootState = servant.shootAnimationState;
            returnState = servant.returnIdleAnimationState;
            aimReloadState = servant.aimReloadAnimationState;
        } else if (entity instanceof ImperialChasseur chasseur) {
            animState = chasseur.getAnimState();
            idleState = chasseur.idleAnimationState;
            reloadState = chasseur.reloadAnimationState;
            meleeState = chasseur.meleeAnimationState;
            meleeAimState = chasseur.meleeAimAnimationState;
            shootState = chasseur.shootAnimationState;
            returnState = chasseur.returnIdleAnimationState;
            aimReloadState = chasseur.aimReloadAnimationState;
        } else {
            return;
        }
        if (animState != ImperialChasseurServant.ANIM_NONE) {
            switch (animState) {
                case ImperialChasseurServant.ANIM_RELOAD_AIM -> {
                    this.animate(reloadState, ImperialChasseurAnimation.RELOAD_AIM, ageInTicks);
                }
                case ImperialChasseurServant.ANIM_MELEE -> {
                    this.animate(meleeState, ImperialChasseurAnimation.MELEE, ageInTicks);
                }
                case ImperialChasseurServant.ANIM_MELEE_AIM -> {
                    this.animate(meleeAimState, ImperialChasseurAnimation.MELEE_AIM, ageInTicks);
                }
                case ImperialChasseurServant.ANIM_SHOOT -> {
                    this.animate(shootState, ImperialChasseurAnimation.SHOOT, ageInTicks);
                }
                case ImperialChasseurServant.ANIM_RETURN_IDLE -> {
                    this.animate(returnState, ImperialChasseurAnimation.RETURN_IDLE, ageInTicks);
                }
                case ImperialChasseurServant.ANIM_AIM_RELOAD -> {
                    this.animate(aimReloadState, ImperialChasseurAnimation.AIM_RELOAD, ageInTicks);
                }
                default -> {
                }
            }
            return;
        }
        float walkWeight = Mth.clamp(limbSwingAmount * 3.5F, 0.0F, 1.0F);
        KeyframeAnimations.animate(this, ImperialChasseurAnimation.WALK,
                (long) (limbSwing * 50.0F * 2.5F), walkWeight, ANIMATION_VEC);
        idleState.updateTime(ageInTicks, 1.0F);
        idleState.ifStarted(currentIdle -> KeyframeAnimations.animate(this,
                ImperialChasseurAnimation.IDLE, currentIdle.getAccumulatedTime(), 1.0F - walkWeight, ANIMATION_VEC));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}