package com.qiuyue.goetyominous.client.render.model.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SandBoulderServantModel extends Model {
    public final ModelPart sandBlock;
    private final ModelPart root;

    public SandBoulderServantModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.sandBlock = root.getChild("sandBlock");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("sandBlock", CubeListBuilder.create().texOffs(151, 2).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(169, 85).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 14.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
