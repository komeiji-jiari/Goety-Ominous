package com.qiuyue.goetyominous.client.render.model.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.FallingSoulBlade;
import net.miauczel.legendary_monsters.entity.animations.SoulBladeAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.SoulBladeFallingModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class FallingSoulBladeModel extends HierarchicalModel<FallingSoulBlade> {

    private final ModelPart bbMain;

    public FallingSoulBladeModel(ModelPart root) {
        this.bbMain = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        return SoulBladeFallingModel.createBodyLayer();
    }

    @Override
    public void setupAnim(FallingSoulBlade entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.emergeAnimationState, SoulBladeAnimations.fall, ageInTicks, 1.0F);
    }

    @Override
    public ModelPart root() {
        return this.bbMain;
    }
}
