package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import com.unusualmodding.opposing_force.client.animations.UmberSpiderAnimations;
import com.unusualmodding.opposing_force.client.models.entity.UmberSpiderModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * 阴影蜘蛛仆从模型：直接复用 OF 原版 UmberSpiderModel 的几何（纯几何，与实体类型无关）。
 * 33 个部件的查找链照搬原版 UmberSpiderModel（javap 反汇编确认），动画直接用 OF 的 UmberSpiderAnimations。
 */
@OnlyIn(Dist.CLIENT)
public class UmberSpiderServantModel extends OPModel<UmberSpiderServant> {
    private final ModelPart root;
    private final ModelPart Body;
    private final ModelPart EyeLayer;
    private final ModelPart CenterHorns;
    private final ModelPart SideHorns1;
    private final ModelPart SideHorns2;
    private final ModelPart Mandible1;
    private final ModelPart Mandible2;
    private final ModelPart Tail;
    private final ModelPart TailPart1;
    private final ModelPart TailPart2;
    private final ModelPart TailPart3;
    private final ModelPart TailPart4;
    private final ModelPart TailPart5;
    private final ModelPart TailPart6;
    private final ModelPart LimbCluster1;
    private final ModelPart ArmBone1;
    private final ModelPart Arm1;
    private final ModelPart LegBone4;
    private final ModelPart Leg4;
    private final ModelPart LegBone2;
    private final ModelPart Leg2;
    private final ModelPart LegBone3;
    private final ModelPart Leg3;
    private final ModelPart LimbCluster2;
    private final ModelPart ArmBone2;
    private final ModelPart Arm2;
    private final ModelPart LegBone5;
    private final ModelPart Leg5;
    private final ModelPart LegBone6;
    private final ModelPart Leg6;
    private final ModelPart LegBone7;
    private final ModelPart Leg7;

    public UmberSpiderServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.Body = this.root.getChild("Body");
        this.EyeLayer = this.Body.getChild("EyeLayer");
        this.CenterHorns = this.Body.getChild("CenterHorns");
        this.SideHorns1 = this.Body.getChild("SideHorns1");
        this.SideHorns2 = this.Body.getChild("SideHorns2");
        this.Mandible1 = this.Body.getChild("Mandible1");
        this.Mandible2 = this.Body.getChild("Mandible2");
        this.Tail = this.Body.getChild("Tail");
        this.TailPart1 = this.Tail.getChild("TailPart1");
        this.TailPart2 = this.TailPart1.getChild("TailPart2");
        this.TailPart3 = this.TailPart2.getChild("TailPart3");
        this.TailPart4 = this.TailPart3.getChild("TailPart4");
        this.TailPart5 = this.TailPart4.getChild("TailPart5");
        this.TailPart6 = this.TailPart5.getChild("TailPart6");
        this.LimbCluster1 = this.root.getChild("LimbCluster1");
        this.ArmBone1 = this.LimbCluster1.getChild("ArmBone1");
        this.Arm1 = this.ArmBone1.getChild("Arm1");
        this.LegBone4 = this.LimbCluster1.getChild("LegBone4");
        this.Leg4 = this.LegBone4.getChild("Leg4");
        this.LegBone2 = this.LimbCluster1.getChild("LegBone2");
        this.Leg2 = this.LegBone2.getChild("Leg2");
        this.LegBone3 = this.LimbCluster1.getChild("LegBone3");
        this.Leg3 = this.LegBone3.getChild("Leg3");
        this.LimbCluster2 = this.root.getChild("LimbCluster2");
        this.ArmBone2 = this.LimbCluster2.getChild("ArmBone2");
        this.Arm2 = this.ArmBone2.getChild("Arm2");
        this.LegBone5 = this.LimbCluster2.getChild("LegBone5");
        this.Leg5 = this.LegBone5.getChild("Leg5");
        this.LegBone6 = this.LimbCluster2.getChild("LegBone6");
        this.Leg6 = this.LegBone6.getChild("Leg6");
        this.LegBone7 = this.LimbCluster2.getChild("LegBone7");
        this.Leg7 = this.LegBone7.getChild("Leg7");
    }

    public static LayerDefinition createBodyLayer() {
        return UmberSpiderModel.createBodyLayer();
    }

    @Override
    public void setupAnim(UmberSpiderServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(UmberSpiderAnimations.SCURRY, limbSwing, limbSwingAmount, 2.0F, 4.0F);
        this.animateIdle(entity.idleAnimationState, UmberSpiderAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
