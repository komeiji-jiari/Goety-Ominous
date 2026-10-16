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
public class ThrownIcicleServantModel extends Model {
    private final ModelPart root;
    private final ModelPart projectile;

    public ThrownIcicleServantModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.projectile = root.getChild("projectile");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition projectile = partdefinition.addOrReplaceChild("projectile", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -23.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.5F, 1.0F));
        projectile.addOrReplaceChild("projectiles_r1", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -1.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 1.5708F));
        return LayerDefinition.create(meshdefinition, 320, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
