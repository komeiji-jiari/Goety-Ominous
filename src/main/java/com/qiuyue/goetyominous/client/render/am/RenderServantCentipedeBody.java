package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.am.ModelServantCaveCentipede;
import com.qiuyue.goetyominous.common.entities.ally.am.ServantCentipedeBody;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderServantCentipedeBody extends MobRenderer<ServantCentipedeBody, AdvancedEntityModel<ServantCentipedeBody>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderServantCentipedeBody(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelServantCaveCentipede(1), 0.5F);
    }

    protected float getFlipDegrees(ServantCentipedeBody centipede) {
        return 180.0F;
    }

    protected void setupRotations(ServantCentipedeBody entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        float newYaw = entity.yHeadRot;
        if (this.isShaking(entity)) {
            newYaw += (float)(Math.cos((double)entity.tickCount * 3.25) * Math.PI * 0.4000000059604645);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - newYaw));
            stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.translate(0.0F, f * 1.15F, 0.0F);
            stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
                stack.translate(0.0, (double)(entity.getBbHeight() + 0.1F), 0.0);
                stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }

    }

    public ResourceLocation getTextureLocation(ServantCentipedeBody entity) {
        return TEXTURE;
    }
}
