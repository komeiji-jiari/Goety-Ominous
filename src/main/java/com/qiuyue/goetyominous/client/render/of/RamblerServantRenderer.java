package com.qiuyue.goetyominous.client.render.of;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.of.RamblerServantSkullLayer;
import com.qiuyue.goetyominous.client.render.model.of.RamblerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.unusualmodding.opposing_force.OpposingForce;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class RamblerServantRenderer extends MobRenderer<RamblerServant, RamblerServantModel> {
    private static final ResourceLocation RAMBLER = OpposingForce.modPrefix("textures/entity/rambler/rambler.png");

    public RamblerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new RamblerServantModel(context.bakeLayer(ModEntityLayers.RAMBLER_SERVANT_LAYER)), 0.8F);
        this.addLayer(new RamblerServantSkullLayer(this,
                (model) -> model.middle_skull,
                (entity) -> skullTexture(RamblerServant.RamblerSkulls.getVariantId(entity.getMiddleSkull()))));
        this.addLayer(new RamblerServantSkullLayer(this,
                (model) -> model.left_skull,
                (entity) -> skullTexture(RamblerServant.RamblerSkulls.getVariantId(entity.getLeftSkull()))));
        this.addLayer(new RamblerServantSkullLayer(this,
                (model) -> model.right_skull,
                (entity) -> skullTexture(RamblerServant.RamblerSkulls.getVariantId(entity.getRightSkull()))));
    }

    private static ResourceLocation skullTexture(RamblerServant.RamblerSkulls skull) {
        return OpposingForce.modPrefix("textures/entity/rambler/skulls/" + skull.getSerializedName() + ".png");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RamblerServant entity) {
        return RAMBLER;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull RamblerServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(RAMBLER);
    }
}