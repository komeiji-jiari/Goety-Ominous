package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.GuzzlerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.GuzzlerServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class GuzzlerServantRenderer extends MobRenderer<GuzzlerServant, GuzzlerServantModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("opposing_force", "textures/entity/guzzler/guzzler.png");

    public GuzzlerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new GuzzlerServantModel(context.bakeLayer(ModEntityLayers.GUZZLER_SERVANT_LAYER)), 1.1F);
        this.addLayer(new GuzzlerServantGlowLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GuzzlerServant entity) {
        return TEXTURE;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull GuzzlerServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(TEXTURE);
    }
}
