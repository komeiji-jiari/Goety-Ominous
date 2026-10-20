package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.IcicleSpikeKeyframeAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.IcicleSpikeServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IcicleSpikeServantModel<T extends IcicleSpikeServant> extends HierarchicalModel<T> {
    public final ModelPart root;
    public final ModelPart spike;

    public IcicleSpikeServantModel(ModelPart root) {
        this.root = root;
        this.spike = root.getChild("spike");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition spike = partdefinition.addOrReplaceChild("spike", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -23.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 47.5F, 0.0F, -1.5708F, 0.0F, 0.0F));
        spike.addOrReplaceChild("projectiles_r1", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -1.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 1.5708F));
        return LayerDefinition.create(meshdefinition, 320, 128);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.spikeAnimationState, IcicleSpikeKeyframeAnimations.ICICLE_SPIKE, ageInTicks);
    }

    public ModelPart root() {
        return this.root;
    }
}
