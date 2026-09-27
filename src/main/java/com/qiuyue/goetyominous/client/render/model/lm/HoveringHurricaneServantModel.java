package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import net.miauczel.legendary_monsters.entity.animations.HoveringHurricaneAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.HoveringHurricaneModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoveringHurricaneServantModel<T extends HoveringHurricaneServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;

    public HoveringHurricaneServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = root.getChild("root").getChild("body").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        return HoveringHurricaneModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        this.animateWalk(HoveringHurricaneAnimations.walk, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        this.animate(entity.getAnimationState("idle"), HoveringHurricaneAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("slam"), HoveringHurricaneAnimations.slam, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shoot"), HoveringHurricaneAnimations.tornadoShoot, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 25.0F);
        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
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
