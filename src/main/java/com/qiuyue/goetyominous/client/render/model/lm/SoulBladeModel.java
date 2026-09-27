package com.qiuyue.goetyominous.client.render.model.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulBlade;
import net.miauczel.legendary_monsters.entity.animations.SoulBladeAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.SoulBladeUndergroundModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class SoulBladeModel extends HierarchicalModel<SoulBlade> {

    private final ModelPart bbMain;

    public SoulBladeModel(ModelPart root) {
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        return SoulBladeUndergroundModel.createBodyLayer();
    }

    @Override
    public void setupAnim(SoulBlade entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.emergeAnimationState, SoulBladeAnimations.emerge, ageInTicks, 1.0F);
    }

    @Override
    public ModelPart root() {
        return this.bbMain;
    }
}
