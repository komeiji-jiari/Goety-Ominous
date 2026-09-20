package com.qiuyue.goetyominous.client.render.model;

import com.qiuyue.goetyominous.common.entities.ally.mobs.BoggedServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BoggedServantModel<T extends BoggedServant> extends SkeletonModel<T> {

    private final ModelPart mushrooms;

    public BoggedServantModel(ModelPart root) {
        super(root);
        this.mushrooms = root.getChild("head").getChild("mushrooms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition mushrooms = root.getChild("head")
                .addOrReplaceChild("mushrooms", CubeListBuilder.create(), PartPose.ZERO);

        mushrooms.addOrReplaceChild("red_mushroom_1",
                CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, 0.7853982F, 0.0F));
        mushrooms.addOrReplaceChild("red_mushroom_2",
                CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, 2.3561945F, 0.0F));
        mushrooms.addOrReplaceChild("brown_mushroom_1",
                CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(-3.0F, -8.0F, -3.0F, 0.0F, 0.7853982F, 0.0F));
        mushrooms.addOrReplaceChild("brown_mushroom_2",
                CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(-3.0F, -8.0F, -3.0F, 0.0F, 2.3561945F, 0.0F));
        mushrooms.addOrReplaceChild("brown_mushroom_3",
                CubeListBuilder.create().texOffs(50, 28).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(-2.0F, -1.0F, 4.0F, -1.5707964F, 0.0F, 0.7853982F));
        mushrooms.addOrReplaceChild("brown_mushroom_4",
                CubeListBuilder.create().texOffs(50, 28).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F),
                PartPose.offsetAndRotation(-2.0F, -1.0F, 4.0F, -1.5707964F, 0.0F, 2.3561945F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.mushrooms.visible = !entity.isSheared();
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}
