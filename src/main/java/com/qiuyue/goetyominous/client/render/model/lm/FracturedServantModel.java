package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Fractured.FracturedAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.FracturedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FracturedServantModel<T extends FracturedServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;

    public FracturedServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("body").getChild("neck").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        return FracturedModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float yaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        float pitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.head.yRot = yaw * ((float) Math.PI / 180F);
        this.head.xRot = pitch * ((float) Math.PI / 180F);

        this.animate(entity.idleAnimationState, FracturedAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.sweepAnimationState, FracturedAnimations.sweep, ageInTicks, 1.0F);
        this.animate(entity.deathAnimationState, FracturedAnimations.death, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
