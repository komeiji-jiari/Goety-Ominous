package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import com.unusualmodding.opposing_force.client.animations.TremblerAnimations;
import com.unusualmodding.opposing_force.client.models.entity.TremblerModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TremblerServantModel extends OPModel<TremblerServant> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart shell;
    private final ModelPart shell_rotation;

    public TremblerServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail = this.body.getChild("tail");
        this.shell = this.root.getChild("shell");
        this.shell_rotation = this.shell.getChild("shell_rotation");
    }

    public static LayerDefinition createBodyLayer() {
        return TremblerModel.createBodyLayer();
    }

    public void setupAnim(TremblerServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(TremblerAnimations.SLIDE, limbSwing, limbSwingAmount, 4.0F, 8.0F);
        this.animateIdle(entity.idleAnimationState, TremblerAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.rollAnimationState, TremblerAnimations.ROLL, ageInTicks);
        this.animate(entity.stunnedAnimationState, TremblerAnimations.STUNNED, ageInTicks);
        if (entity.getStunnedTicks() <= 0) {
            this.head.xRot += headPitch * Mth.DEG_TO_RAD - (headPitch * Mth.DEG_TO_RAD / 2.0F);
            this.head.yRot += netHeadYaw * Mth.DEG_TO_RAD - (netHeadYaw * Mth.DEG_TO_RAD / 2.0F);
        }
    }

    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public @NotNull ModelPart root() {
        return this.root;
    }
}
