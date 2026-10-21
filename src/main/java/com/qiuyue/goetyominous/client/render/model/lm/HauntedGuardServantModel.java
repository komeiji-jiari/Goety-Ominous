package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedGuardServant;
import net.miauczel.legendary_monsters.entity.animations.HauntedGuardAnimations;
import net.miauczel.legendary_monsters.entity.animations.LivingArmorAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.HauntedGuardModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HauntedGuardServantModel<T extends HauntedGuardServant> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;

    public HauntedGuardServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("body_c").getChild("body").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        return HauntedGuardModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float yaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        float pitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.head.yRot = yaw * ((float) Math.PI / 180F);
        this.head.xRot = pitch * ((float) Math.PI / 180F);

        if (entity.getAttackState() == 0) {
            this.animateWalk(LivingArmorAnimations.WALK4, limbSwing, limbSwingAmount, 1.5F, 2.5F);
        }
        this.animate(entity.idleAnimationState, LivingArmorAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.sitAnimationState, HauntedGuardAnimations.sit, ageInTicks, 1.0F);
        this.animate(entity.slashAnimationState, HauntedGuardAnimations.attack1Smooth, ageInTicks, 1.0F);
        this.animate(entity.uppercutAnimationState, HauntedGuardAnimations.upper_cut, ageInTicks, 1.0F);
        this.animate(entity.slamAnimationState, HauntedGuardAnimations.slam, ageInTicks, 1.0F);
        this.animate(entity.combo2AnimationState, HauntedGuardAnimations.attackCombo, ageInTicks, 1.0F);
        this.animate(entity.DeathAnimationState, LivingArmorAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.guardAnimationState, LivingArmorAnimations.guard, ageInTicks, 1.0F);
        this.animate(entity.awakenAnimationState, LivingArmorAnimations.awaken, ageInTicks, 1.0F);

        this.root.xScale = 1.25F;
        this.root.yScale = 1.25F;
        this.root.zScale = 1.25F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
