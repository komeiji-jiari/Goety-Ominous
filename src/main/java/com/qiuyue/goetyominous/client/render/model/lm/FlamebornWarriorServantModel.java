package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornWarriorServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.FlamebornGuardAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.Flameborn.FlamebornWarriorAnimations;
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
public class FlamebornWarriorServantModel<T extends FlamebornWarriorServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart Body;
    private final ModelPart Head;

    public FlamebornWarriorServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.Body = this.root.getChild("Body");
        this.Head = this.Body.getChild("Head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 0.0F));
        PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(48, 32).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(48, 48).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(40, 16).addBox(-4.0F, -7.95F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -12.05F, 0.0F));
        Head.addOrReplaceChild("upperhead", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -7.8F, -9.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 32).addBox(-5.0F, -8.8F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 52).addBox(5.0F, -3.8F, -9.0F, 5.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 52).mirror().addBox(-10.0F, -3.8F, -9.0F, 5.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 0).addBox(0.0F, -18.8F, -14.0F, 0.0F, 12.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.15F, 5.0F));
        PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(26, 52).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 60).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(-4.0F, -10.0F, 0.0F));
        PartDefinition lowerRightArm = RightArm.addOrReplaceChild("lowerRightArm", CubeListBuilder.create().texOffs(16, 60).addBox(-1.0F, -0.6667F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.6667F, 0.0F));
        lowerRightArm.addOrReplaceChild("RightWeapon", CubeListBuilder.create().texOffs(48, 64).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(56, 64).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(34, 64).addBox(-3.0F, -12.0F, 0.0F, 7.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 12.8333F, 0.0F, 1.5708F, 0.0F, 1.5708F));
        PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(26, 52).mirror().addBox(0.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 60).mirror().addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(4.0F, -10.0F, 0.0F));
        PartDefinition lowerLeftArm = LeftArm.addOrReplaceChild("lowerLeftArm", CubeListBuilder.create().texOffs(16, 60).mirror().addBox(-1.0F, -0.6667F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 13.6667F, 0.0F));
        lowerLeftArm.addOrReplaceChild("LeftWeapon", CubeListBuilder.create().texOffs(48, 64).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(56, 64).mirror().addBox(-2.0F, 2.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(34, 64).mirror().addBox(-4.0F, -12.0F, 0.0F, 7.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.1F, 12.8333F, 0.0F, 1.5708F, 0.0F, -1.5708F));
        root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(40, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 7.0F, 0.0F));
        root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(40, 32).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 7.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (entity.getAttackState() == 0) {
            this.animateWalk(FlamebornGuardAnimations.walk2, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        this.applyHeadRotation(netHeadYaw, headPitch);
        this.animate(entity.getAnimationState("idle"), FlamebornGuardAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), FlamebornGuardAnimations.death2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("hit"), FlamebornWarriorAnimations.hit, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("hit2"), FlamebornWarriorAnimations.hit2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("hit_double"), FlamebornWarriorAnimations.hitDouble, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("hit_double2"), FlamebornWarriorAnimations.hitDouble2, ageInTicks, 1.0F);
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
