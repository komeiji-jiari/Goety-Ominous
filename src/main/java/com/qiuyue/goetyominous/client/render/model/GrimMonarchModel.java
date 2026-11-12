package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.GrimMonarchAnimator;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchAction;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GrimMonarchModel extends EntityModel<GrimMonarchEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyOminous.MOD_ID, "grim_monarch"), "main");
    private final ModelPart root;
    private final Map<String, ModelPart> parts = new LinkedHashMap<>();
    private float bodyAlpha = 1.0F;

    public GrimMonarchModel(ModelPart root) {
        super(net.minecraft.client.renderer.RenderType::entityCutoutNoCull);
        this.root = root;
        ModelPart jRoot = root.getChild("J_Root");
        ModelPart hip = jRoot.getChild("hip");
        ModelPart leftLeg = hip.getChild("left_leg");
        ModelPart rightLeg = hip.getChild("right_leg");
        ModelPart body = hip.getChild("body");
        ModelPart shoulders = body.getChild("shoulders");
        ModelPart neck = shoulders.getChild("neck");
        ModelPart head = neck.getChild("head");
        ModelPart leftArm = shoulders.getChild("left_arm");
        ModelPart leftClaw = leftArm.getChild("left_claw");
        ModelPart rightArm = shoulders.getChild("right_arm");
        ModelPart rightClaw = rightArm.getChild("right_claw");
        this.parts.put("root", jRoot);
        this.parts.put("hip", hip);
        this.parts.put("left_leg", leftLeg);
        this.parts.put("right_leg", rightLeg);
        this.parts.put("body", body);
        this.parts.put("shoulders", shoulders);
        this.parts.put("neck", neck);
        this.parts.put("head", head);
        this.parts.put("left_arm", leftArm);
        this.parts.put("left_claw", leftClaw);
        this.parts.put("right_arm", rightArm);
        this.parts.put("right_claw", rightClaw);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition jRoot = partdefinition.addOrReplaceChild("J_Root", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition hip = jRoot.addOrReplaceChild("hip", CubeListBuilder.create(),
                PartPose.offset(0.0F, -47.0F, 0.0F));

        PartDefinition leftLeg = hip.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(88, 31).mirror().addBox(-1.0F, 0.0F, 0.2505F, 2.0F, 47.0F, 2.0F,
                        new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(3.0F, 0.0F, -0.0789F));

        PartDefinition rightLeg = hip.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(88, 31).addBox(-1.0F, 0.0F, 0.1716F, 2.0F, 47.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 0.0F, 0.0F));

        PartDefinition body = hip.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition shoulders = body.addOrReplaceChild("shoulders", CubeListBuilder.create(),
                PartPose.offset(0.0F, -17.0F, 0.0F));

        PartDefinition Shoulders_0_r1 = shoulders.addOrReplaceChild("Shoulders_0_r1", CubeListBuilder.create()
                .texOffs(40, 23).addBox(-5.8787F, -14.0F, -5.8787F, 12.0F, 15.0F, 12.0F, new CubeDeformation(0.1F))
                .texOffs(0, 18).addBox(-4.8787F, -20.0F, -4.8787F, 10.0F, 21.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 3.1416F));

        PartDefinition neck = shoulders.addOrReplaceChild("neck", CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(),
                PartPose.offset(0.0F, -0.0001F, 0.0046F));

        PartDefinition Head_14_r1 = head.addOrReplaceChild("Head_14_r1", CubeListBuilder.create()
                .texOffs(37, 1).addBox(-5.618F, -5.618F, -10.9999F, 11.0F, 11.0F, 11.0F, new CubeDeformation(0.1F))
                .texOffs(189, 0).addBox(-10.618F, 1.382F, -19.9867F, 3.0F, 3.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(184, 0).addBox(-9.618F, 6.382F, -14.9867F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(158, 0).addBox(-10.618F, 1.382F, -8.9867F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(144, 0).addBox(-8.618F, 1.382F, -8.9867F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(184, 0).addBox(6.382F, -9.618F, -14.9867F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(217, 0).addBox(1.382F, -10.618F, -21.9867F, 3.0F, 3.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(170, 0).addBox(1.382F, -10.618F, -8.9867F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(132, 0).addBox(1.382F, -8.618F, -8.9867F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.618F, -4.618F, -8.9867F, 9.0F, 9.0F, 8.9868F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.7854F, 0.0F));

        PartDefinition leftArm = shoulders.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(9.2263F, 0.0F, 0.4645F));

        PartDefinition L_Arm_0_r1 = leftArm.addOrReplaceChild("L_Arm_0_r1", CubeListBuilder.create()
                .texOffs(112, 72).mirror().addBox(-7.024F, -2.976F, -34.0F, 16.0F, 4.0F, 12.0F,
                        new CubeDeformation(0.0F)).mirror(false)
                .texOffs(100, 28).mirror().addBox(-7.024F, -2.976F, -22.0F, 16.0F, 4.0F, 24.0F,
                        new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.7071F, -0.024F, -2.7071F, -1.5708F, 0.7854F, -3.1416F));

        PartDefinition leftClaw = leftArm.addOrReplaceChild("left_claw", CubeListBuilder.create(),
                PartPose.offset(1.4831F, 22.0F, -1.4831F));

        PartDefinition L_Claw_0_r1 = leftClaw.addOrReplaceChild("L_Claw_0_r1", CubeListBuilder.create()
                .texOffs(-42, 49).addBox(-6.024F, -1.2787F, -44.0F, 12.0F, 0.0F, 46.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-1.0F, -1.0F, -2.0F, -1.5708F, 0.7854F, -3.1416F));

        PartDefinition rightArm = shoulders.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                PartPose.offset(-8.1415F, 0.0F, 0.5493F));

        PartDefinition R_Arm_0_r1 = rightArm.addOrReplaceChild("R_Arm_0_r1", CubeListBuilder.create()
                .texOffs(112, 56).addBox(-6.976F, -4.096F, -34.0F, 16.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(76, 0).addBox(-6.976F, -4.096F, -22.0F, 16.0F, 4.0F, 24.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.096F, 0.0F, 1.5708F, 0.7854F, 0.0F));

        PartDefinition rightClaw = rightArm.addOrReplaceChild("right_claw", CubeListBuilder.create(),
                PartPose.offset(-1.5679F, 22.0F, -1.5679F));

        PartDefinition R_Claw_0_r1 = rightClaw.addOrReplaceChild("R_Claw_0_r1", CubeListBuilder.create()
                .texOffs(-30, 49).addBox(-4.976F, 0.1213F, -44.0F, 12.0F, 0.0F, 46.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 1.5708F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 128);
    }

    @Override
    public void setupAnim(GrimMonarchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        GrimMonarchAnimator.setup(this, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float partial = ageInTicks - (float) entity.tickCount;
        this.bodyAlpha = entity.isEcho() ? 0.72F : 1.0F;
        if (entity.action() == GrimMonarchAction.ECHO_SPAWN) {
            this.bodyAlpha *= net.minecraft.util.Mth.clamp(
                    entity.actionSeconds(partial) / 1.5F, 0.0F, 1.0F);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay,
                red, green, blue, alpha * this.bodyAlpha);
    }

    public ModelPart root() {
        return this.root;
    }

    public ModelPart part(String name) {
        return this.parts.get(name);
    }

    public Map<String, ModelPart> parts() {
        return this.parts;
    }
}
