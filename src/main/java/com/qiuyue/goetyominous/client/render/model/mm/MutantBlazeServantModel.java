package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantBlazeKeyframeAnimations;
import com.alexander.mutantmore.animation.math_animation.definition.MutantBlazeMathAnimations;
import com.alexander.mutantmore.models.entities.MMBaseEntityModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class MutantBlazeServantModel<T extends MutantBlazeServant> extends MMBaseEntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("mutantmore", "mutant_blaze"), "main");
    private final ModelPart head;
    private final ModelPart shield1;
    private final ModelPart shield2;
    private final ModelPart shield3;
    private final ModelPart shield4;
    private final ModelPart shield5;
    private final ModelPart shield6;
    private final ModelPart shield7;

    public MutantBlazeServantModel(ModelPart root) {
        super(root);
        this.head = root.getChild("everything").getChild("head");
        ModelPart shieldsLayer1 = root.getChild("everything").getChild("shields").getChild("shieldLayer1Rot").getChild("shieldLayer1");
        this.shield1 = shieldsLayer1.getChild("shield");
        this.shield2 = shieldsLayer1.getChild("shield2");
        this.shield3 = shieldsLayer1.getChild("shield3");
        ModelPart shieldsLayer2 = root.getChild("everything").getChild("shields").getChild("shieldLayer2Rot").getChild("shieldLayer2");
        this.shield4 = shieldsLayer2.getChild("shield4");
        this.shield5 = shieldsLayer2.getChild("shield5");
        this.shield6 = shieldsLayer2.getChild("shield6");
        this.shield7 = shieldsLayer2.getChild("shield7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));
        everything.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(50, 68).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(50, 53).addBox(-4.5F, -9.5F, -4.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.0F));
        PartDefinition rods = everything.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 32.0F, 0.0F));
        PartDefinition rodLayer1Rot = rods.addOrReplaceChild("rodLayer1Rot", CubeListBuilder.create(), PartPose.offset(0.0F, -44.0F, 0.0F));
        PartDefinition rodLayer1 = rodLayer1Rot.addOrReplaceChild("rodLayer1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rod = rodLayer1.addOrReplaceChild("rod", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        rod.addOrReplaceChild("rodRotation", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod2 = rodLayer1.addOrReplaceChild("rod2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.4189F, 0.0F));
        rod2.addOrReplaceChild("rodRotation2", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod3 = rodLayer1.addOrReplaceChild("rod3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.8378F, 0.0F));
        rod3.addOrReplaceChild("rodRotation3", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod4 = rodLayer1.addOrReplaceChild("rod4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.2566F, 0.0F));
        rod4.addOrReplaceChild("rodRotation4", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod5 = rodLayer1.addOrReplaceChild("rod5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.6755F, 0.0F));
        rod5.addOrReplaceChild("rodRotation5", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod6 = rodLayer1.addOrReplaceChild("rod6", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));
        rod6.addOrReplaceChild("rodRotation6", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod7 = rodLayer1.addOrReplaceChild("rod7", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.5133F, 0.0F));
        rod7.addOrReplaceChild("rodRotation7", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod8 = rodLayer1.addOrReplaceChild("rod8", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.9322F, 0.0F));
        rod8.addOrReplaceChild("rodRotation8", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod9 = rodLayer1.addOrReplaceChild("rod9", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.9322F, 0.0F));
        rod9.addOrReplaceChild("rodRotation9", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod10 = rodLayer1.addOrReplaceChild("rod10", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.5133F, 0.0F));
        rod10.addOrReplaceChild("rodRotation10", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod11 = rodLayer1.addOrReplaceChild("rod11", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));
        rod11.addOrReplaceChild("rodRotation11", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod12 = rodLayer1.addOrReplaceChild("rod12", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.6755F, 0.0F));
        rod12.addOrReplaceChild("rodRotation12", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod13 = rodLayer1.addOrReplaceChild("rod13", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.2566F, 0.0F));
        rod13.addOrReplaceChild("rodRotation13", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod14 = rodLayer1.addOrReplaceChild("rod14", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.8378F, 0.0F));
        rod14.addOrReplaceChild("rodRotation14", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rod15 = rodLayer1.addOrReplaceChild("rod15", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.4189F, 0.0F));
        rod15.addOrReplaceChild("rodRotation15", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -23.0F));
        PartDefinition rodLayer2Rot = rods.addOrReplaceChild("rodLayer2Rot", CubeListBuilder.create(), PartPose.offset(0.0F, -30.0F, 0.0F));
        PartDefinition rodLayer2 = rodLayer2Rot.addOrReplaceChild("rodLayer2", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition rod16 = rodLayer2.addOrReplaceChild("rod16", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));
        rod16.addOrReplaceChild("rodRotation16", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod17 = rodLayer2.addOrReplaceChild("rod17", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.6283F, 0.0F));
        rod17.addOrReplaceChild("rodRotation17", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod18 = rodLayer2.addOrReplaceChild("rod18", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -1.2566F, 0.0F));
        rod18.addOrReplaceChild("rodRotation18", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod19 = rodLayer2.addOrReplaceChild("rod19", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -1.885F, 0.0F));
        rod19.addOrReplaceChild("rodRotation19", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod20 = rodLayer2.addOrReplaceChild("rod20", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -2.5133F, 0.0F));
        rod20.addOrReplaceChild("rodRotation20", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod21 = rodLayer2.addOrReplaceChild("rod21", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        rod21.addOrReplaceChild("rodRotation21", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod22 = rodLayer2.addOrReplaceChild("rod22", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 2.5133F, 0.0F));
        rod22.addOrReplaceChild("rodRotation22", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod23 = rodLayer2.addOrReplaceChild("rod23", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.885F, 0.0F));
        rod23.addOrReplaceChild("rodRotation23", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod24 = rodLayer2.addOrReplaceChild("rod24", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.2566F, 0.0F));
        rod24.addOrReplaceChild("rodRotation24", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rod25 = rodLayer2.addOrReplaceChild("rod25", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.6283F, 0.0F));
        rod25.addOrReplaceChild("rodRotation25", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));
        PartDefinition rodLayer3Rot = rods.addOrReplaceChild("rodLayer3Rot", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 0.0F));
        PartDefinition rodLayer3 = rodLayer3Rot.addOrReplaceChild("rodLayer3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rod26 = rodLayer3.addOrReplaceChild("rod26", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        rod26.addOrReplaceChild("rodRotation26", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod27 = rodLayer3.addOrReplaceChild("rod27", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.8976F, 0.0F));
        rod27.addOrReplaceChild("rodRotation27", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod28 = rodLayer3.addOrReplaceChild("rod28", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.7952F, 0.0F));
        rod28.addOrReplaceChild("rodRotation28", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod29 = rodLayer3.addOrReplaceChild("rod29", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.6928F, 0.0F));
        rod29.addOrReplaceChild("rodRotation29", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod30 = rodLayer3.addOrReplaceChild("rod30", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.6928F, 0.0F));
        rod30.addOrReplaceChild("rodRotation30", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod31 = rodLayer3.addOrReplaceChild("rod31", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.7952F, 0.0F));
        rod31.addOrReplaceChild("rodRotation31", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition rod32 = rodLayer3.addOrReplaceChild("rod32", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.8976F, 0.0F));
        rod32.addOrReplaceChild("rodRotation32", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -19.0F));
        PartDefinition shields = everything.addOrReplaceChild("shields", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition shieldLayer1Rot = shields.addOrReplaceChild("shieldLayer1Rot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shieldLayer1 = shieldLayer1Rot.addOrReplaceChild("shieldLayer1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield = shieldLayer1.addOrReplaceChild("shield", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, -14.0F, 0.1309F, 0.0F, 0.0F));
        shield.addOrReplaceChild("shieldRotation", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5F, -10.0F, -1.0F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield2 = shieldLayer1.addOrReplaceChild("shield2", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.0F, -1.0F, 6.0F, -3.0107F, 1.0472F, 3.1416F));
        shield2.addOrReplaceChild("shieldRotation2", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5718F, -10.0F, -0.8756F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield3 = shieldLayer1.addOrReplaceChild("shield3", CubeListBuilder.create(), PartPose.offsetAndRotation(12.0F, -1.0F, 7.0F, -3.0107F, -1.0472F, 3.1416F));
        shield3.addOrReplaceChild("shieldRotation3", CubeListBuilder.create().texOffs(8, 16).addBox(-6.4282F, -10.0F, -0.8756F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shieldLayer2Rot = shields.addOrReplaceChild("shieldLayer2Rot", CubeListBuilder.create(), PartPose.offset(0.0F, -18.0F, 0.0F));
        PartDefinition shieldLayer2 = shieldLayer2Rot.addOrReplaceChild("shieldLayer2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition shield4 = shieldLayer2.addOrReplaceChild("shield4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, -19.0F, 0.0873F, 0.0F, 0.0F));
        shield4.addOrReplaceChild("shieldRotation4", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5F, -10.0F, -1.0F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield5 = shieldLayer2.addOrReplaceChild("shield5", CubeListBuilder.create(), PartPose.offsetAndRotation(-19.0F, -1.0F, 0.0F, 0.0F, 1.5708F, -0.0873F));
        shield5.addOrReplaceChild("shieldRotation5", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5F, -10.0F, -1.0F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield6 = shieldLayer2.addOrReplaceChild("shield6", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.0F, 19.0F, -3.0543F, 0.0F, 3.1416F));
        shield6.addOrReplaceChild("shieldRotation6", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5F, -10.0F, -1.0F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield7 = shieldLayer2.addOrReplaceChild("shield7", CubeListBuilder.create(), PartPose.offsetAndRotation(19.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.0873F));
        shield7.addOrReplaceChild("shieldRotation7", CubeListBuilder.create().texOffs(8, 16).addBox(-6.5F, -10.0F, -1.0F, 13.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition coreParts = everything.addOrReplaceChild("coreParts", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition core = coreParts.addOrReplaceChild("core", CubeListBuilder.create().texOffs(33, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition coreExterior = core.addOrReplaceChild("coreExterior", CubeListBuilder.create().texOffs(38, 12).addBox(-4.5F, -4.5F, 0.0F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        coreExterior.addOrReplaceChild("coreExterior_r1", CubeListBuilder.create().texOffs(38, 12).addBox(0.0F, -9.0F, 0.0F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5F, -4.5F, 0.0F, -1.5708F, 0.0F));
        coreExterior.addOrReplaceChild("coreExterior_r2", CubeListBuilder.create().texOffs(38, 12).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5F, -4.5F, -1.5708F, 0.0F, 0.0F));
        coreParts.addOrReplaceChild("coreRings", CubeListBuilder.create().texOffs(0, 40).addBox(-10.0F, 0.0F, -10.0F, 20.0F, 1.0F, 20.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 86, 86);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        float walkSpeed = (float)pEntity.getDeltaMovement().horizontalDistance() * 15.0F;
        this.mathAnimateState(pEntity, pEntity.introAnimation, MutantBlazeMathAnimations.INTRO, 1.0F, walkSpeed, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (!pEntity.introAnimation.isStarted() && !pEntity.stunnedAnimation.isStarted()) {
            this.mathAnimate(pEntity, MutantBlazeMathAnimations.IDLE, 0L, walkSpeed, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }

        if (pEntity.isCrouching()) {
            this.applyStatic(MutantBlazeKeyframeAnimations.CROUCH);
        }

        this.animate(pEntity.noveltyAnimation, MutantBlazeKeyframeAnimations.NOVELTY, pAgeInTicks);
        this.animate(pEntity.shootAnimation, MutantBlazeKeyframeAnimations.SHOOT, pAgeInTicks);
        if (pEntity.rodShotAnimation.isStarted()) {
            this.animate(pEntity.rodShotAnimation, MutantBlazeKeyframeAnimations.ROD_SHOT_KEYFRAME, pAgeInTicks);
            this.mathAnimate(pEntity, MutantBlazeMathAnimations.ROD_SHOT_MATH, pEntity.rodShotAnimation.getAccumulatedTime(), 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }

        this.mathAnimateState(pEntity, pEntity.stunnedAnimation, MutantBlazeMathAnimations.STUNNED, 1.0F, walkSpeed, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (pEntity.deathAnimation.isStarted()) {
            this.animate(pEntity.deathAnimation, MutantBlazeKeyframeAnimations.DEATH_KEYFRAME, pAgeInTicks);
            this.mathAnimate(pEntity, MutantBlazeMathAnimations.DEATH_MATH, pEntity.deathAnimation.getAccumulatedTime(), 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }

        ModelPart var10000 = this.head;
        var10000.xRot += (float)Math.toRadians((double)pHeadPitch);
        var10000 = this.head;
        var10000.yRot += (float)Math.toRadians((double)pNetHeadYaw);
        this.shield1.visible = !pEntity.isShieldBroken(5);
        this.shield2.visible = !pEntity.isShieldBroken(6);
        this.shield3.visible = !pEntity.isShieldBroken(4);
        this.shield4.visible = !pEntity.isShieldBroken(1);
        this.shield5.visible = !pEntity.isShieldBroken(2);
        this.shield6.visible = !pEntity.isShieldBroken(3);
        this.shield7.visible = !pEntity.isShieldBroken(0);
    }
}
