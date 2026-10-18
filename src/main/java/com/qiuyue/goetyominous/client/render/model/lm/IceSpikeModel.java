package com.qiuyue.goetyominous.client.render.model.lm;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceSpikeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart iceSpike;

    public IceSpikeModel(ModelPart root) {
        this.iceSpike = root.getChild("icespike3");
    }

    public static LayerDefinition createLayerDefinition() {
        return net.miauczel.legendary_monsters.entity.client.Model.IceSpikeModel.createLayerDefinition();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.iceSpike.y = 24.0F - (limbSwing + Mth.sin(limbSwing * 2.7F)) * 0.6F * 12.0F;
    }

    @Override
    public ModelPart root() {
        return this.iceSpike;
    }
}
