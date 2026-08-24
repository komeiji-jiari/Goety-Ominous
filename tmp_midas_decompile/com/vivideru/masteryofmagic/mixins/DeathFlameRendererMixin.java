/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.HellBlastRenderer
 *  com.Polarice3.Goety.client.render.HellBoltRenderer
 *  com.Polarice3.Goety.client.render.HellfireRenderer
 *  com.Polarice3.Goety.common.entities.projectiles.HellBlast
 *  com.Polarice3.Goety.common.entities.projectiles.HellBolt
 *  com.Polarice3.Goety.common.entities.projectiles.Hellfire
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.client.render.HellBlastRenderer;
import com.Polarice3.Goety.client.render.HellBoltRenderer;
import com.Polarice3.Goety.client.render.HellfireRenderer;
import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.entities.projectiles.Hellfire;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class DeathFlameRendererMixin {

    @Mixin(value={HellBlastRenderer.class})
    public static class Blast {
        @Unique
        private boolean gmom$death;

        @Inject(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBlast;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, remap=false)
        private void gmom$head(HellBlast entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
            this.gmom$death = entity.m_19880_().contains("gmom_death_flame");
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBlast;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/HellBlastModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=4, remap=false)
        private float gmom$red(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBlast;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/HellBlastModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=5, remap=false)
        private float gmom$green(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBlast;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/HellBlastModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=6, remap=false)
        private float gmom$blue(float original) {
            return this.gmom$death ? 0.05f : original;
        }
    }

    @Mixin(value={HellBoltRenderer.class})
    public static class Bolt {
        @Unique
        private boolean gmom$death;

        @Inject(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, remap=false)
        private void gmom$head(HellBolt entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
            this.gmom$death = entity.m_19880_().contains("gmom_death_flame");
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/SoulBoltModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=4, remap=false)
        private float gmom$red(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/SoulBoltModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=5, remap=false)
        private float gmom$green(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/HellBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/SoulBoltModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=6, remap=false)
        private float gmom$blue(float original) {
            return this.gmom$death ? 0.05f : original;
        }
    }

    @Mixin(value={HellfireRenderer.class})
    public static class Fire {
        @Unique
        private boolean gmom$death;

        @Inject(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/Hellfire;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, remap=false)
        private void gmom$head(Hellfire entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
            this.gmom$death = entity.m_19880_().contains("gmom_death_flame");
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/Hellfire;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/IceBouquetModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=4, remap=false)
        private float gmom$red(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/Hellfire;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/IceBouquetModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=5, remap=false)
        private float gmom$green(float original) {
            return this.gmom$death ? 0.03f : original;
        }

        @ModifyArg(method={"render(Lcom/Polarice3/Goety/common/entities/projectiles/Hellfire;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/client/render/model/IceBouquetModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index=6, remap=false)
        private float gmom$blue(float original) {
            return this.gmom$death ? 0.05f : original;
        }
    }
}

