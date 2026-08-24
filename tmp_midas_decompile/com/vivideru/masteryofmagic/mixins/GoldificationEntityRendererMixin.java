/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivideru.masteryofmagic.goldification.client.GoldificationBufferSource;
import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityRenderDispatcher.class})
public abstract class GoldificationEntityRendererMixin {
    @Redirect(method={"render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), remap=false, require=0)
    private void goetyMasteryOfMagic$renderGoldifiedEntityDev(EntityRenderer renderer, Entity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        renderer.m_7392_(entity, yaw, partialTick, poseStack, GoldificationEntityRendererMixin.effectiveBuffer(entity, bufferSource), packedLight);
    }

    @Redirect(method={"m_114384_(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderer;m_7392_(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), remap=false, require=0)
    private void goetyMasteryOfMagic$renderGoldifiedEntityProduction(EntityRenderer renderer, Entity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        renderer.m_7392_(entity, yaw, partialTick, poseStack, GoldificationEntityRendererMixin.effectiveBuffer(entity, bufferSource), packedLight);
    }

    private static MultiBufferSource effectiveBuffer(Entity entity, MultiBufferSource original) {
        return GoldificationClientState.isEntityGoldified(entity.m_19879_()) ? GoldificationBufferSource.wrap(original) : original;
    }
}

