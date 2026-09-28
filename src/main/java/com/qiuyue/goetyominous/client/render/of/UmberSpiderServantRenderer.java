package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.of.UmberSpiderServantEyesLayer;
import com.qiuyue.goetyominous.client.render.model.of.UmberSpiderServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class UmberSpiderServantRenderer extends MobRenderer<UmberSpiderServant, UmberSpiderServantModel> {
    private static final ResourceLocation UMBER_SPIDER = new ResourceLocation("opposing_force", "textures/entity/umber_spider/umber_spider.png");
    private static final ResourceLocation ELITE_UMBER_SPIDER = new ResourceLocation("opposing_force", "textures/entity/umber_spider/tenebrous_umber_spider.png");

    public UmberSpiderServantRenderer(EntityRendererProvider.Context context) {
        super(context, new UmberSpiderServantModel(context.bakeLayer(ModEntityLayers.UMBER_SPIDER_SERVANT_LAYER)), 0.8F);
        this.addLayer(new UmberSpiderServantEyesLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull UmberSpiderServant entity) {
        return entity.isElite() ? ELITE_UMBER_SPIDER : UMBER_SPIDER;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull UmberSpiderServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    @Override
    protected int getBlockLightLevel(@NotNull UmberSpiderServant entity, @NotNull BlockPos pos) {
        return entity.isElite() ? 15 : super.getBlockLightLevel(entity, pos);
    }
}
