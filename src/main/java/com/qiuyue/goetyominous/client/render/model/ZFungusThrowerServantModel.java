package com.qiuyue.goetyominous.client.render.model;

import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.qiuyue.goetyominous.common.entities.FungusThrowerAnimated;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZFungusThrowerServantModel<T extends Mob> extends ZPiglinModel<T> {
    private final FungusThrowerPiglinAnimator animator;

    public ZFungusThrowerServantModel(ModelPart root) {
        super(root);
        this.animator = new FungusThrowerPiglinAnimator(this);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof FungusThrowerAnimated thrower) {
            this.animator.animate(entity, ageInTicks, netHeadYaw, headPitch,
                    thrower.getIdleAnimationState(), thrower.getWalkAnimationState(), thrower.getThrowAnimationState(),
                    thrower.isThrowing(), thrower.getWalkWeight());
        }
    }
}
