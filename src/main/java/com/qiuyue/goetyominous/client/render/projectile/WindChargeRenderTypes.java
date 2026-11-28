package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class WindChargeRenderTypes {
    public static VertexConsumer breezeWind(MultiBufferSource buffer, ResourceLocation texture, float uOffset, float vOffset) {
        return new ScrollingVertexConsumer(buffer.getBuffer(RenderType.entityTranslucent(texture)), uOffset, vOffset);
    }

    private static class ScrollingVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float uOffset;
        private final float vOffset;

        private ScrollingVertexConsumer(VertexConsumer delegate, float uOffset, float vOffset) {
            this.delegate = delegate;
            this.uOffset = uOffset;
            this.vOffset = vOffset;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            this.delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            this.delegate.color(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            this.delegate.uv(u + this.uOffset, v + this.vOffset);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            this.delegate.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            this.delegate.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            this.delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            this.delegate.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            this.delegate.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            this.delegate.unsetDefaultColor();
        }
    }
}
