package com.qiuyue.goetyominous.client.render.model.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PitchforkModel extends Model {
    private final ModelPart bone;

    public PitchforkModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -12.0F, -0.5F, 1.0F, 25.0F, 1.0F),
                PartPose.offset(0.0F, 11.0F, 0.0F));
        bone.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(4, 0).addBox(-0.5F, -31.0F, -0.5F, 1.0F, 3.0F, 1.0F)
                        .texOffs(4, 0).addBox(-2.5F, -31.0F, -0.5F, 1.0F, 3.0F, 1.0F)
                        .texOffs(4, 0).addBox(1.5F, -31.0F, -0.5F, 1.0F, 3.0F, 1.0F)
                        .texOffs(4, 4).addBox(-2.5F, -28.0F, -0.5F, 5.0F, 1.0F, 1.0F)
                        .texOffs(8, 0).addBox(-1.5F, -27.0F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}
