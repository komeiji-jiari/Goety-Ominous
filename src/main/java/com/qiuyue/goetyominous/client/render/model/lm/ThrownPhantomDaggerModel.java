package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;

public class ThrownPhantomDaggerModel extends HierarchicalModel<ThrownPhantomDagger> {

    private final ModelPart dagger;

    public ThrownPhantomDaggerModel(ModelPart root) {
        super(RenderType::entityTranslucentEmissive);
        this.dagger = root.getChild("dagger");
    }

    public static LayerDefinition createBodyLayer() {
        return net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.ThrownPhantomDaggerModel.createBodyLayer();
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
