package com.qiuyue.goetyominous.client.render.model.of;

import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernSegmentServant;
import com.unusualmodding.opposing_force.client.animations.SkyvernAnimations;
import com.unusualmodding.opposing_force.client.models.entity.skyvern.SkyvernBodyModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyvernServantBodyModel extends SkyvernTranslucentModel<SkyvernSegmentServant> {
    private final ModelPart root;
    private final ModelPart left_arm;
    private final ModelPart right_arm;

    public SkyvernServantBodyModel(ModelPart root) {
        this.root = root.getChild("root");
        ModelPart segment = this.root.getChild("roll_control").getChild("segment");
        this.left_arm = segment.getChild("left_arm");
        this.right_arm = segment.getChild("right_arm");
    }

    public static LayerDefinition createBodyLayer() {
        return SkyvernBodyModel.createBodyLayer();
    }

    @Override
    public void setupAnim(@NotNull SkyvernSegmentServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.setGhostAlpha(entity.isGhost());
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateSmooth(entity.fly1AnimationState, SkyvernAnimations.BODY_FLY1, ageInTicks);
        this.animateSmooth(entity.fly2AnimationState, SkyvernAnimations.BODY_FLY2, ageInTicks);
        this.animateSmooth(entity.attackAnimationState, SkyvernAnimations.BODY_ATTACK, ageInTicks);
        this.animate(entity.rollAnimationState, SkyvernAnimations.BODY_LOOP1, ageInTicks);
        boolean arms = entity.hasArms();
        this.left_arm.visible = arms;
        this.right_arm.visible = arms;
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
