package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class SkyvernTranslucentModel<E extends Entity> extends OPModel<E> {
    private static final float GHOST_ALPHA = 0.5F;

    private float alpha = 1.0F;

    protected void setGhostAlpha(boolean ghost) {
        this.alpha = ghost ? GHOST_ALPHA : 1.0F;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha * this.alpha);
    }
}
