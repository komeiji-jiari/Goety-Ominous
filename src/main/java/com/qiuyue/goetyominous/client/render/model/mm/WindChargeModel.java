package com.qiuyue.goetyominous.client.render.model.mm;

import com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class WindChargeModel extends HierarchicalModel<AbstractWindCharge> {
    private final ModelPart bone;
    private final ModelPart wind;
    private final ModelPart windCharge;

    public WindChargeModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.bone = root.getChild("bone");
        this.wind = this.bone.getChild("wind");
        this.windCharge = this.bone.getChild("wind_charge");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild("wind", CubeListBuilder.create()
                        .texOffs(15, 20).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, CubeDeformation.NONE)
                        .texOffs(0, 9).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        bone.addOrReplaceChild("wind_charge", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(AbstractWindCharge entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.windCharge.yRot = -ageInTicks * 16.0F * ((float) Math.PI / 180);
        this.wind.yRot = ageInTicks * 16.0F * ((float) Math.PI / 180);
    }

    @Override
    public ModelPart root() {
        return this.bone;
    }
}
