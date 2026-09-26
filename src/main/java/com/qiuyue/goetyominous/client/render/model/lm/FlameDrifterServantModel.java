package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlameDrifterServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.FlameDrifter.FlameDrifterAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));
        PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -10.0F, -9.5F, 26.0F, 19.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftCannonRotator = Body.addOrReplaceChild("leftCannonRotator", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition LeftCannon = leftCannonRotator.addOrReplaceChild("LeftCannon", CubeListBuilder.create().texOffs(64, 71).mirror().addBox(-3.0F, -4.0F, -4.0F, 6.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(64, 38).mirror().addBox(3.0F, -11.5F, -5.0F, 10.0F, 23.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(18.0F, -0.5F, 0.0F));
        LeftCannon.addOrReplaceChild("LeftRingTop", CubeListBuilder.create().texOffs(0, 38).mirror().addBox(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(8.0F, -14.5F, 0.0F));
        LeftCannon.addOrReplaceChild("LeftRingBottom", CubeListBuilder.create().texOffs(0, 38).mirror().addBox(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(8.0F, 14.5F, 0.0F));
        PartDefinition rightCannonRotator = Body.addOrReplaceChild("rightCannonRotator", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition RightCannon = rightCannonRotator.addOrReplaceChild("RightCannon", CubeListBuilder.create().texOffs(64, 71).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(64, 38).addBox(-13.0F, -11.5F, -5.0F, 10.0F, 23.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-18.0F, -0.5F, 0.0F));
        RightCannon.addOrReplaceChild("RightRingTop", CubeListBuilder.create().texOffs(0, 38).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -14.5F, 0.0F));
        RightCannon.addOrReplaceChild("RightRingBottom", CubeListBuilder.create().texOffs(0, 38).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 14.5F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
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
