package com.qiuyue.goetyominous.client.render.model.curios;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
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
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER_LOCATION =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    new ResourceLocation(GoetyOminous.MOD_ID, "crone_robe"), "main");

    public CroneRobeModel(ModelPart root) {
        super(root, RenderType::entityCutoutNoCull);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.5F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 38).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, new CubeDeformation(0.5F))
                        .texOffs(28, 46).addBox(-6.0F, 8.0F, -4.0F, 12.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(64, 22).addBox(-6.0F, 12.0F, -4.5F, 12.0F, 12.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(32, 1).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(40, 30).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 30).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}
