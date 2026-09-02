package com.qiuyue.goetyominous.client.render.model.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CroneRobeModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(GoetyOminous.MOD_ID, "crone_robe"), "main");

    public CroneRobeModel(ModelPart root) {
        super(root, RenderType::entityCutoutNoCull);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0.0F);
        PartDefinition root = meshdefinition.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.5F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.2F))
                        .texOffs(28, 46).addBox(-6.0F, 8.0F, -5.5F, 12.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(64, 22).addBox(-6.0F, 12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(32, 1).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(40, 30).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.26F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 30).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.26F)).mirror(false),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
