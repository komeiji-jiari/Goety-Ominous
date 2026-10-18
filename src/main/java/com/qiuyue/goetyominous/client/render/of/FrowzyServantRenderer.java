package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.of.FrowzyServantHelmetLayer;
import com.qiuyue.goetyominous.client.render.model.of.FrowzyServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class FrowzyServantRenderer extends MobRenderer<FrowzyServant, FrowzyServantModel> {

    public FrowzyServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FrowzyServantModel(context.bakeLayer(ModEntityLayers.FROWZY_SERVANT_LAYER)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new FrowzyServantHelmetLayer(this, context));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FrowzyServant entity) {
        return new ResourceLocation("opposing_force",
                "textures/entity/frowzy/" + entity.getVariant().name().toLowerCase(Locale.ROOT) + ".png");
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull FrowzyServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutout(this.getTextureLocation(entity));
    }
}
