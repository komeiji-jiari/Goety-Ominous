package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.VoltServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class VoltServantRenderer extends MobRenderer<VoltServant, VoltServantModel> {
    private static final ResourceLocation VOLT = new ResourceLocation("opposing_force", "textures/entity/volt/volt.png");
    private static final ResourceLocation QUASAR_VOLT = new ResourceLocation("opposing_force", "textures/entity/volt/quasar_volt.png");

    public VoltServantRenderer(EntityRendererProvider.Context context) {
        super(context, new VoltServantModel(context.bakeLayer(ModEntityLayers.VOLT_SERVANT_LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull VoltServant entity) {
        return entity.isElite() ? QUASAR_VOLT : VOLT;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull VoltServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }
}
