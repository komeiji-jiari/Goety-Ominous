package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ThrownPhantomDaggerModel extends HierarchicalModel<ThrownPhantomDagger> {

    private final ModelPart dagger;

    public ThrownPhantomDaggerModel(ModelPart root) {
        super(RenderType::entityTranslucentEmissive);
        this.dagger = root.getChild("dagger");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dagger = root.addOrReplaceChild("dagger",
                CubeListBuilder.create()
                        .texOffs(7, 11).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(2, 1).addBox(-1.5F, -12.0F, 0.0F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 7).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 4).addBox(-4.5F, -4.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 1).addBox(0.5F, -4.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        dagger.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(1, 12).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.dagger.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.dagger;
    }

    @Override
    public void setupAnim(ThrownPhantomDagger dagger, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.dagger.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.dagger.xRot = ((float) Math.PI / 2F);
        this.dagger.zRot = headPitch * ((float) Math.PI / 180F);
    }
}
