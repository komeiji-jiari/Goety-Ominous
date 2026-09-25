package com.qiuyue.goetyominous.client.render.model.of;

import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernSegmentServant;
import com.unusualmodding.opposing_force.client.animations.SkyvernAnimations;
import com.unusualmodding.opposing_force.client.models.entity.skyvern.SkyvernTailModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyvernServantTailModel extends SkyvernTranslucentModel<SkyvernSegmentServant> {
    private final ModelPart root;

    public SkyvernServantTailModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        return SkyvernTailModel.createBodyLayer();
    }

    @Override
    public void setupAnim(@NotNull SkyvernSegmentServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.setGhostAlpha(entity.isGhost());
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateSmooth(entity.fly1AnimationState, SkyvernAnimations.TAIL_FLY, ageInTicks);
        this.animateSmooth(entity.attackAnimationState, SkyvernAnimations.TAIL_ATTACK, ageInTicks);
        this.animate(entity.rollAnimationState, SkyvernAnimations.TAIL_LOOP1, ageInTicks);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
