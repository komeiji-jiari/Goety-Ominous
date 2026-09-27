package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.WanderingEyeServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.WanderingEyeAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.WanderingEyeModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingEyeServantModel<T extends WanderingEyeServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart eye;

    public WanderingEyeServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.eye = this.root.getChild("Eye");
    }

    public static LayerDefinition createBodyLayer() {
        return WanderingEyeModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        this.animate(entity.getAnimationState("idle"), WanderingEyeAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("roll"), WanderingEyeAnimations.roll, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), WanderingEyeAnimations.death, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.eye.yRot = netHeadYaw * ((float) Math.PI / 180);
        this.eye.xRot = headPitch * ((float) Math.PI / 180);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
