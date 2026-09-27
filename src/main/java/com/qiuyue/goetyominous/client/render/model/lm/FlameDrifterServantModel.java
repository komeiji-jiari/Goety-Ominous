package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlameDrifterServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.FlameDrifter.FlameDrifterAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.FlameDrifterModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameDrifterServantModel<T extends FlameDrifterServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart Body;
    private final ModelPart Head;

    public FlameDrifterServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.Body = this.root.getChild("Body");
        this.Head = this.Body.getChild("Head");
    }

    public static LayerDefinition createBodyLayer() {
        return FlameDrifterModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (entity.getAttackState() == 0) {
            this.animateWalk(FlameDrifterAnimations.floatWalk, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        this.animate(entity.getAnimationState("idle"), FlameDrifterAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("right_cannon_shoot"), FlameDrifterAnimations.rightCannonShoot, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_cannon_shoot"), FlameDrifterAnimations.leftCannonShoot, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), FlameDrifterAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("spin_charge"), FlameDrifterAnimations.spinChargeTeleport, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("spin"), FlameDrifterAnimations.spin, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        this.Head.yRot = Mth.clamp(netHeadYaw, -30.0F, 30.0F) * ((float) Math.PI / 180);
        this.Head.xRot = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float) Math.PI / 180);
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
