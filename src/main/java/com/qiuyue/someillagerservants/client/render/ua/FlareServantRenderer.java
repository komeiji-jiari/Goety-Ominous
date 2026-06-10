package com.qiuyue.someillagerservants.client.render.ua;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.someillagerservants.client.render.layer.ua.FlareServantEyesRenderLayer;
import com.qiuyue.someillagerservants.client.render.model.ua.FlareServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.ua.FlareServant;
import com.teamabnormals.upgrade_aquatic.core.UpgradeAquatic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlareServantRenderer extends MobRenderer<FlareServant, FlareServantModel<FlareServant>> {

    public FlareServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FlareServantModel<>(context.bakeLayer(FlareServantModel.LOCATION)), 0.9F);
        this.addLayer(new FlareServantEyesRenderLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FlareServant entity) {
        return new ResourceLocation(UpgradeAquatic.MOD_ID, "textures/entity/flare/flare.png");
    }

    @Override
    protected void scale(FlareServant flare, PoseStack matrixStack, float partialTickTime) {
        int i = flare.getPhantomSize();
        float f = 1.0F + 0.15F * (float) i;
        matrixStack.scale(f, f, f);
    }

    @Override
    protected void setupRotations(FlareServant flare, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(flare, matrixStack, ageInTicks, rotationYaw, partialTicks);
        matrixStack.mulPose(Axis.XP.rotationDegrees(flare.getXRot()));
    }

}
