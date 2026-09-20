package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.TremblerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class TremblerServantRenderer extends MobRenderer<TremblerServant, TremblerServantModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opposing_force", "textures/entity/trembler/trembler.png");
    private static final ResourceLocation ELITE_TEXTURE = new ResourceLocation("opposing_force", "textures/entity/trembler/turbo_trembler.png");

    public TremblerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new TremblerServantModel(context.bakeLayer(ModEntityLayers.TREMBLER_SERVANT_LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TremblerServant entity) {
        return entity.isElite() ? ELITE_TEXTURE : TEXTURE;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull TremblerServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }
}
