package com.qiuyue.goetyominous.client.render.model.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.ChoruslingServant;
import net.miauczel.legendary_monsters.entity.animations.ChoruslingAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ChoruslingServantModel extends HierarchicalModel<ChoruslingServant> {

    private final ModelPart root;
    private final ModelPart head;

    public ChoruslingServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("body").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(-1.0F, -16.0F, -3.5F));
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -14.0F, -3.5F, 12.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, 3.5F));
        body.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(40, 0).addBox(-8.0F, 0.001F, 0.0F, 8.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -6.001F, 4.5F, 1.5708F, 0.5236F, 1.5708F));
        body.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(0, 22).addBox(-10.0F, 0.001F, -1.0F, 10.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -11.0F, 4.5F, 1.5708F, 0.5236F, 1.5708F));
        body.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(48, 12).addBox(-4.0F, -8.0F, -7.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -11.0F, -0.5F));
        body.addOrReplaceChild("rightarm",
                CubeListBuilder.create()
                        .texOffs(24, 36).addBox(-3.25F, -1.5F, -3.0F, 6.0F, 31.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(60, 28).addBox(-8.25F, -1.5F, 0.0F, 5.0F, 31.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.75F, -12.5F, -0.5F));
        body.addOrReplaceChild("leftarm",
                CubeListBuilder.create()
                        .texOffs(0, 36).addBox(-2.75F, -1.5F, -3.0F, 6.0F, 31.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(60, 59).addBox(3.25F, -1.5F, 0.0F, 5.0F, 31.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.75F, -12.5F, -0.5F));
        root.addOrReplaceChild("leftleg",
                CubeListBuilder.create().texOffs(48, 28).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 27.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.5F, 12.5F, 4.0F));
        root.addOrReplaceChild("rightleg",
                CubeListBuilder.create().texOffs(48, 58).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 27.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-3.5F, 12.5F, 4.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ChoruslingServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = Mth.clamp(netHeadYaw, -30.0F, 30.0F) * ((float) Math.PI / 180.0F);
        this.head.xRot = Mth.clamp(headPitch, -25.0F, 25.0F) * ((float) Math.PI / 180.0F);
        this.animateWalk(ChoruslingAnimations.WALK2, limbSwing, limbSwingAmount, 1.5F, 2.5F);
        this.animate(entity.idleAnimationState, ChoruslingAnimations.IDLE, ageInTicks);
        this.animate(entity.attackAnimationState, ChoruslingAnimations.ATTACK, ageInTicks);
        this.animate(entity.deathAnimationState, ChoruslingAnimations.DEATH, ageInTicks);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
