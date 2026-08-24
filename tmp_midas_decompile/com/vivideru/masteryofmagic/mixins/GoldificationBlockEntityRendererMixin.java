/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivideru.masteryofmagic.goldification.client.GoldificationBufferSource;
import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockEntityRenderDispatcher.class})
public abstract class GoldificationBlockEntityRendererMixin {
    private static final ThreadLocal<BlockEntity> GOETY_MASTERY_OF_MAGIC$CURRENT_BLOCK_ENTITY = new ThreadLocal();

    @Inject(method={"setupAndRender(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", "m_112284_(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, at={@At(value="HEAD")}, remap=false, require=0)
    private static void goetyMasteryOfMagic$captureBlockEntity(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo callbackInfo) {
        GOETY_MASTERY_OF_MAGIC$CURRENT_BLOCK_ENTITY.set(blockEntity);
    }

    @Inject(method={"setupAndRender(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", "m_112284_(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, at={@At(value="RETURN")}, remap=false, require=0)
    private static void goetyMasteryOfMagic$releaseBlockEntity(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo callbackInfo) {
        GOETY_MASTERY_OF_MAGIC$CURRENT_BLOCK_ENTITY.remove();
    }

    @ModifyArg(method={"setupAndRender(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"), index=3, remap=false, require=0)
    private static MultiBufferSource goetyMasteryOfMagic$goldificationBufferDev(MultiBufferSource bufferSource) {
        return GoldificationBlockEntityRendererMixin.effectiveBuffer(GOETY_MASTERY_OF_MAGIC$CURRENT_BLOCK_ENTITY.get(), bufferSource);
    }

    @ModifyArg(method={"m_112284_(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;m_6922_(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"), index=3, remap=false, require=0)
    private static MultiBufferSource goetyMasteryOfMagic$goldificationBufferProduction(MultiBufferSource bufferSource) {
        return GoldificationBlockEntityRendererMixin.effectiveBuffer(GOETY_MASTERY_OF_MAGIC$CURRENT_BLOCK_ENTITY.get(), bufferSource);
    }

    private static MultiBufferSource effectiveBuffer(BlockEntity blockEntity, MultiBufferSource original) {
        return blockEntity != null && GoldificationClientState.isBlockGoldified(blockEntity.m_58899_()) ? GoldificationBufferSource.wrap(original) : original;
    }
}

