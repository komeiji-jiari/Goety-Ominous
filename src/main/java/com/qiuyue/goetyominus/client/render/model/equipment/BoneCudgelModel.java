package com.qiuyue.goetyominus.client.render.model.equipment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.GoetyOminous;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoneCudgelModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyOminous.MOD_ID, "bone_cudgel"), "main");

    private final ModelPart bone;

    public BoneCudgelModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bone", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -39.0F, -2.0F, 8.0F, 28.0F, 4.0F)
                        .texOffs(24, 7).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 6.0F, 2.0F)
                        .texOffs(24, 0).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 3.0F, 4.0F)
                        .texOffs(24, 22).addBox(4.0F, -35.0F, -1.0F, 3.0F, 3.0F, 2.0F)
                        .texOffs(24, 17).addBox(-7.0F, -35.0F, -1.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(-36.0F, 12.0F, 13.0F, 1.5802F, 0.0426F, 1.7892F));
        return LayerDefinition.create(mesh, 40, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
