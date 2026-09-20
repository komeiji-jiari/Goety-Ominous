package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerBulletMathAnimations;
import com.alexander.mutantmore.models.entities.MMBaseEntityModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class MutantShulkerServantBulletModel<T extends MutantShulkerServantBullet> extends MMBaseEntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("goetyominous", "mutant_shulker_servant_bullet"), "main");

    public MutantShulkerServantBulletModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();
        PartDefinition everything = root.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 0.0F));
        everything.addOrReplaceChild("transparent", CubeListBuilder.create()
                        .texOffs(0, 83).addBox(-2.0F, -7.0F, -7.0F, 4.0F, 14.0F, 14.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 64).addBox(-7.0F, -7.0F, -2.0F, 14.0F, 14.0F, 4.0F, new CubeDeformation(0.01F))
                        .texOffs(0, 64).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(-0.01F))
                        .texOffs(37, 83).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        everything.addOrReplaceChild("main", CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-2.0F, -7.0F, -7.0F, 4.0F, 14.0F, 14.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 0).addBox(-7.0F, -7.0F, -2.0F, 14.0F, 14.0F, 4.0F, new CubeDeformation(0.01F))
                        .texOffs(0, 0).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(-0.01F))
                        .texOffs(37, 19).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimate(entity, MutantShulkerBulletMathAnimations.IDLE, 0L, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
