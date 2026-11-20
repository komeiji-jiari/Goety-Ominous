package com.qiuyue.goetyominous.client.render.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousSkullLordModel<T extends Entity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;

    public OminousSkullLordModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        head.addOrReplaceChild("hood_part1_r1", CubeListBuilder.create()
                .texOffs(0, 21).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, -1.0625F, 2.3438F, 0.2182F, 0.0F, 0.0F));

        PartDefinition crown = head.addOrReplaceChild("crown", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        crown.addOrReplaceChild("horn_back_r1", CubeListBuilder.create()
                .texOffs(46, 11).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.125F, -9.1875F, 5.8438F, -1.5708F, -1.2654F, 1.5708F));

        crown.addOrReplaceChild("crown_part1_r1", CubeListBuilder.create()
                .texOffs(40, 9).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -9.2188F, -4.0313F, -0.1309F, 0.0F, 0.0F));

        crown.addOrReplaceChild("crystal_r1", CubeListBuilder.create()
                .texOffs(32, 9).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.0313F, -6.5F, -4.375F, 0.0F, 0.0F, 0.7854F));

        PartDefinition horn_side1 = crown.addOrReplaceChild("horn_side1", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        horn_side1.addOrReplaceChild("horn3_r1", CubeListBuilder.create()
                .texOffs(54, 9).addBox(-3.0F, -5.0F, 0.0F, 5.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0938F, -9.7813F, 4.125F, 0.3655F, -0.7119F, -0.5299F));

        horn_side1.addOrReplaceChild("horn1_r1", CubeListBuilder.create()
                .texOffs(46, 9).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.25F, -9.7813F, -4.2188F, -0.3225F, 0.5475F, -0.4571F));

        PartDefinition horn_side2 = crown.addOrReplaceChild("horn_side2", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        horn_side2.addOrReplaceChild("horn4_r1", CubeListBuilder.create()
                .texOffs(54, 9).mirror().addBox(-2.0F, -5.0F, 0.0F, 5.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-4.0938F, -9.7813F, 4.125F, 0.3655F, 0.7119F, 0.5299F));

        horn_side2.addOrReplaceChild("horn2_r1", CubeListBuilder.create()
                .texOffs(46, 9).mirror().addBox(-2.0F, -4.0F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-3.25F, -9.7813F, -4.2188F, -0.3225F, -0.5475F, 0.4571F));

        PartDefinition candle_side1 = crown.addOrReplaceChild("candle_side1", CubeListBuilder.create()
                .texOffs(33, 18).addBox(4.0F, -6.0F, -1.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(41, 22).addBox(5.375F, -6.6875F, -1.0313F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(53, 17).addBox(5.875F, -10.5938F, -0.5313F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.1F))
                .texOffs(53, 23).addBox(5.875F, -10.8125F, -0.5313F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
                PartPose.offset(0.0F, -0.9063F, 0.0F));

        candle_side1.addOrReplaceChild("fire_example_r1", CubeListBuilder.create()
                .texOffs(32, 13).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.9375F, -12.4063F, 0.4375F, 0.0F, -0.7854F, 0.0F));

        candle_side1.addOrReplaceChild("candle_pillar1_r1", CubeListBuilder.create()
                .texOffs(41, 18).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(5.6563F, -5.125F, 0.5F, 0.0F, 0.0F, -0.7418F));

        PartDefinition candle_side2 = crown.addOrReplaceChild("candle_side2", CubeListBuilder.create()
                .texOffs(33, 18).mirror().addBox(-5.0F, -6.0F, -1.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(41, 22).mirror().addBox(-8.375F, -6.6875F, -1.0313F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(53, 17).mirror().addBox(-7.875F, -10.5938F, -0.5313F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false)
                .texOffs(53, 23).mirror().addBox(-7.875F, -10.8125F, -0.5313F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false),
                PartPose.offset(0.0F, -0.9063F, 0.0F));

        candle_side2.addOrReplaceChild("fire_example_r2", CubeListBuilder.create()
                .texOffs(32, 13).mirror().addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-6.9375F, -12.4063F, 0.4375F, 0.0F, 0.7854F, 0.0F));

        candle_side2.addOrReplaceChild("candle_pillar2_r1", CubeListBuilder.create()
                .texOffs(41, 18).mirror().addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-5.6563F, -5.125F, 0.5F, 0.0F, 0.0F, 0.7418F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
