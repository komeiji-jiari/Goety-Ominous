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

/**
 * Trembler 仆从模型：直接复用原版 TremblerModel 的几何（纯几何，与实体类型无关）。
 * 部件查找链照搬原版 TremblerModel（javap 反汇编确认），动画直接用 OF 的 TremblerAnimations。
 */
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
        // 照搬原版 TremblerModel.setupAnim：
        // 1) 先播腿部滑动动画（走动时 SLIDE 接管）
        this.animateWalk(TremblerAnimations.SLIDE, limbSwing, limbSwingAmount, 4.0F, 8.0F);
        // 2) idle 权重 = 1 - |limbSwingAmount*4|：站定不动时满权重，走动时退给 SLIDE。
        //    （之前写死 1.0F,1.0F 会算出权重 0，idle 永远不播）
        this.animateIdle(entity.idleAnimationState, TremblerAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.rollAnimationState, TremblerAnimations.ROLL, ageInTicks);
        this.animate(entity.stunnedAnimationState, TremblerAnimations.STUNNED, ageInTicks);
        // 3) 没晕时头跟着视线转（只转一半角度，脖子模型承担另一半）
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
