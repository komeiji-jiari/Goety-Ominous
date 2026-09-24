package com.qiuyue.goetyominous.client.render.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.FireSlimeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.FireSlimeServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class FireSlimeServantRenderer extends MobRenderer<FireSlimeServant, FireSlimeServantModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("opposing_force", "textures/entity/fire_slime/fire_slime.png");

    public FireSlimeServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FireSlimeServantModel(context.bakeLayer(ModEntityLayers.FIRE_SLIME_SERVANT_LAYER)), 0.4F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireSlimeServant entity) {
        return TEXTURE;
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull FireSlimeServant entity, boolean bodyVisible,
                                                 boolean translucent, boolean glowing) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    @Override
    protected void scale(@NotNull FireSlimeServant slime, @NotNull PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.005F, 0.0F);
        float squish = Mth.lerp(partialTicks, slime.oSquish, slime.squish) / 1.5F;
        float squishScale = 1.0F / (squish + 1.0F);
        poseStack.scale(squishScale, 1.0F / squishScale, squishScale);
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireSlimeServant entity, @NotNull BlockPos pos) {
        return 15;
    }
}
