package com.qiuyue.goetyominous.client.render.pp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.pp.PiglinTravelerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinTravelerServantRenderer<T extends PiglinTravelerServant>
        extends HumanoidMobRenderer<T, PiglinTravelerServantModel<T>> {

    protected static final ResourceLocation TEXTURE =
            new ResourceLocation("piglinproliferation", "textures/entity/piglin/traveler/traveler.png");

    public PiglinTravelerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinTravelerServantModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_TRAVELER_SERVANT_LAYER)),
                0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PIGLIN_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_SERVANT_PP_OUTER_ARMOR_LAYER)),
                context.getModelManager()));
    }

    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float ageInTicks,
                                  float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        if (entity.isSitting()) {
            poseStack.translate(0.0F, -0.6F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(T entity) {
        return super.isShaking(entity) || entity.isConverting();
    }
}
