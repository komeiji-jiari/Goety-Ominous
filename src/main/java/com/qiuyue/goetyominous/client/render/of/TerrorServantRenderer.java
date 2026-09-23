package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.of.TerrorServantGlowLayer;
import com.qiuyue.goetyominous.client.render.model.of.TerrorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class TerrorServantRenderer extends MobRenderer<TerrorServant, TerrorServantModel> {
    private static final ResourceLocation TERROR = new ResourceLocation("opposing_force", "textures/entity/terror/terror.png");
    private static final ResourceLocation ELITE_TEXTURE = new ResourceLocation("opposing_force", "textures/entity/terror/antediluvian_terror.png");

    public TerrorServantRenderer(EntityRendererProvider.Context context) {
        super(context, new TerrorServantModel(context.bakeLayer(ModEntityLayers.TERROR_SERVANT_LAYER)), 0.5F);
        this.addLayer(new TerrorServantGlowLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TerrorServant entity) {
        return entity.isElite() ? ELITE_TEXTURE : TERROR;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull TerrorServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }
}
