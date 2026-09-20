package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.of.DicerServantVisorLayer;
import com.qiuyue.goetyominous.client.render.model.of.DicerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DicerServantRenderer extends MobRenderer<DicerServant, DicerServantModel> {
    private static final ResourceLocation DICER = new ResourceLocation("opposing_force", "textures/entity/dicer/dicer.png");
    private static final ResourceLocation ARCH_DICER = new ResourceLocation("opposing_force", "textures/entity/dicer/arch_dicer.png");
    private static final ResourceLocation GIGAN = new ResourceLocation("opposing_force", "textures/entity/dicer/gigan.png");

    public DicerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new DicerServantModel(context.bakeLayer(ModEntityLayers.DICER_SERVANT_LAYER)), 0.5F);
        this.addLayer(new DicerServantVisorLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DicerServant entity) {
        if (entity.getName().getString().contains("gigan")) {
            return GIGAN;
        } else if (entity.isElite()) {
            return ARCH_DICER;
        } else {
            return DICER;
        }
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull DicerServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }
}
