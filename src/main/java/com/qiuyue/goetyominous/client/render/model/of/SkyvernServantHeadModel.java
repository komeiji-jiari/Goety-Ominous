package com.qiuyue.goetyominous.client.render.model.of;

import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.unusualmodding.opposing_force.client.animations.SkyvernAnimations;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import com.unusualmodding.opposing_force.client.models.entity.skyvern.SkyvernModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyvernServantHeadModel extends OPModel<SkyvernServant> {
    private final ModelPart root;

    public SkyvernServantHeadModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        return SkyvernModel.createBodyLayer();
    }

    @Override
    public void setupAnim(@NotNull SkyvernServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateSmooth(entity.flyAnimationState, SkyvernAnimations.HEAD_FLY, ageInTicks);
        this.animateSmooth(entity.attackAnimationState, SkyvernAnimations.HEAD_ATTACK, ageInTicks);
        this.animateSmooth(entity.roarAnimationState, SkyvernAnimations.ROAR, ageInTicks);
        this.animate(entity.rollAnimationState, SkyvernAnimations.HEAD_LOOP1, ageInTicks);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
