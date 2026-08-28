package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.am.ModelServantCaveCentipede;
import com.qiuyue.goetyominous.common.entities.ally.am.ServantCentipedeHead;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderServantCentipedeHead extends MobRenderer<ServantCentipedeHead, AdvancedEntityModel<ServantCentipedeHead>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

    public RenderServantCentipedeHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelServantCaveCentipede(0), 0.5F);
        this.addLayer(new LayerServantCentipedeHeadEyes(this));
    }

    protected void setupRotations(ServantCentipedeHead entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        if (this.isShaking(entity)) {
            yawIn += (float)(Math.cos((double)entity.tickCount * 3.25) * Math.PI * 0.4000000059604645);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.translate(0.0F, f * 1.0F, 0.0F);
            stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
                stack.translate(0.0, (double)(entity.getBbHeight() + 0.1F), 0.0);
                stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }

    }

    protected float getFlipDegrees(ServantCentipedeHead centipede) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(ServantCentipedeHead entity) {
        return TEXTURE;
    }
}
