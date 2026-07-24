// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class rattled_necromancer<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "rattled_necromancer"), "main");
	private final ModelPart skeleton;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart horns;
	private final ModelPart right_arm;
	private final ModelPart right_pauldron;
	private final ModelPart staff;
	private final ModelPart handle;
	private final ModelPart head2;
	private final ModelPart left_arm;
	private final ModelPart leftItem;
	private final ModelPart left_pauldron;
	private final ModelPart pants;
	private final ModelPart middle;
	private final ModelPart cape;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public rattled_necromancer(ModelPart root) {
		this.skeleton = root.getChild("skeleton");
		this.body = this.skeleton.getChild("body");
		this.head = this.body.getChild("head");
		this.hat = this.head.getChild("hat");
		this.horns = this.hat.getChild("horns");
		this.right_arm = this.body.getChild("right_arm");
		this.right_pauldron = this.right_arm.getChild("right_pauldron");
		this.staff = this.right_arm.getChild("staff");
		this.handle = this.staff.getChild("handle");
		this.head2 = this.staff.getChild("head2");
		this.left_arm = this.body.getChild("left_arm");
		this.leftItem = this.left_arm.getChild("leftItem");
		this.left_pauldron = this.left_arm.getChild("left_pauldron");
		this.pants = this.body.getChild("pants");
		this.middle = this.pants.getChild("middle");
		this.cape = this.body.getChild("cape");
		this.right_leg = this.skeleton.getChild("right_leg");
		this.left_leg = this.skeleton.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition horns = hat.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(24, 92).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

		PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

		PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, 5.0F, 1.3963F, 0.0F, 0.0F));

		PartDefinition handle = staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(10, 89).addBox(-0.5F, -16.0F, 0.0F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.2409F, -0.7541F));

		PartDefinition head2 = staff.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(16, 102).addBox(-2.5F, -23.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 7.2409F, -0.7541F));

		PartDefinition cube_r1 = head2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(3, 74).addBox(-6.5F, -6.5F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -17.0F, 0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));

		PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron", CubeListBuilder.create().texOffs(0, 48).mirror().addBox(-1.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition pants = body.addOrReplaceChild("pants", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition middle = pants.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(40, 36).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F, 0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 1.0F));

		PartDefinition right_leg = skeleton.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition left_leg = skeleton.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		return LayerDefinition.create(meshdefinition, 64, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		skeleton.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}