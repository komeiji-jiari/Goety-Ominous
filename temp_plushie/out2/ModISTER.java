package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.common.blocks.BlackCrystalBlock;
import com.Polarice3.Goety.common.blocks.CryptChestBlock;
import com.Polarice3.Goety.common.blocks.GraveGolemSkullBlock;
import com.Polarice3.Goety.common.blocks.LoftyChestBlock;
import com.Polarice3.Goety.common.blocks.ModChestBlock;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.common.blocks.RedstoneGolemSkullBlock;
import com.Polarice3.Goety.common.blocks.RedstoneMonstrosityHeadBlock;
import com.Polarice3.Goety.common.blocks.SculpturedStatueBlock;
import com.Polarice3.Goety.common.blocks.TallSkullBlock;
import com.Polarice3.Goety.common.blocks.entities.BlackCrystalBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CryptChestBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.LoftyChestBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.ModChestBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SculpturedStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModISTER extends BlockEntityWithoutLevelRenderer {
    private final Map<Block, ModChestBlockEntity> chestEntities = new HashMap();
    private final Map<Block, SculpturedStatueBlockEntity> statueEntities = new HashMap();
    private final Map<Block, BlackCrystalBlockEntity> crystalEntities = new HashMap();

    private ModChestBlockEntity chestEntity(Block block) {
        return (ModChestBlockEntity)this.chestEntities.computeIfAbsent(block, (block1) -> new ModChestBlockEntity(BlockPos.f_121853_, block1.m_49966_()));
    }

    private SculpturedStatueBlockEntity statueEntity(Block block) {
        return (SculpturedStatueBlockEntity)this.statueEntities.computeIfAbsent(block, (block1) -> new SculpturedStatueBlockEntity(BlockPos.f_121853_, block1.m_49966_()));
    }

    private BlackCrystalBlockEntity crystalEntity(Block block) {
        return (BlackCrystalBlockEntity)this.crystalEntities.computeIfAbsent(block, (block1) -> new BlackCrystalBlockEntity(BlockPos.f_121853_, block1.m_49966_()));
    }

    public ModISTER() {
        super(Minecraft.m_91087_().m_167982_(), Minecraft.m_91087_().m_167973_());
    }

    public void m_108829_(ItemStack pStack, ItemDisplayContext pCamera, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pLight, int pOverlay) {
        Item item = pStack.m_41720_();
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.m_40614_();
            if (block instanceof TallSkullBlock) {
                if (pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.m_85836_();
                    pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                    pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                    pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                    pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                    TallSkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.m_85849_();
                } else {
                    TallSkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof RedstoneGolemSkullBlock) {
                if (pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.m_85836_();
                    pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                    pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                    pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                    pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                    RedstoneGolemSkullBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.m_85849_();
                } else {
                    RedstoneGolemSkullBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof GraveGolemSkullBlock) {
                if (pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.m_85836_();
                    pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                    pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                    pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                    pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                    GraveGolemSkullBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.m_85849_();
                } else {
                    GraveGolemSkullBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof RedstoneMonstrosityHeadBlock) {
                if (pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.m_85836_();
                    pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                    pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                    pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                    pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                    RedstoneMonstrosityHeadBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.m_85849_();
                } else {
                    RedstoneMonstrosityHeadBlockEntityRenderer.renderItemSkull(pStack, (Direction)null, 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof BlackCrystalBlock) {
                BlackCrystalBlockEntity crystalBlock = this.crystalEntity(block);
                BlockEntityRenderer<?> renderer = Minecraft.m_91087_().m_167982_().m_112265_(crystalBlock);
                if (renderer instanceof BlackCrystalRenderer) {
                    BlackCrystalRenderer crystalBlockRenderer = (BlackCrystalRenderer)renderer;
                    crystalBlockRenderer.render(crystalBlock, ClientEvents.PARTIAL_TICK, pMatrixStack, pBuffer, pLight, pOverlay);
                }
            } else if (block instanceof CryptChestBlock) {
                Minecraft.m_91087_().m_167982_().m_112272_(new CryptChestBlockEntity(BlockPos.f_121853_, (BlockState)block.m_49966_().m_61124_(CryptChestBlock.LOCKED, false)), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof LoftyChestBlock) {
                Minecraft.m_91087_().m_167982_().m_112272_(new LoftyChestBlockEntity(BlockPos.f_121853_, block.m_49966_()), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof ModChestBlock) {
                Minecraft.m_91087_().m_167982_().m_112272_(this.chestEntity(block), pMatrixStack, pBuffer, pLight, pOverlay);
            } else if (block instanceof PlushieBlock) {
                if (pCamera == ItemDisplayContext.GUI) {
                    pMatrixStack.m_85836_();
                    pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                    pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                    pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                    pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                    pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                    PlushieBlockEntityRenderer.renderItemPlushie(pStack, block.m_49966_(), 180.0F, pMatrixStack, pBuffer, pLight);
                    pMatrixStack.m_85849_();
                } else {
                    PlushieBlockEntityRenderer.renderItemPlushie(pStack, block.m_49966_(), 180.0F, pMatrixStack, pBuffer, pLight);
                }
            } else if (block instanceof SculpturedStatueBlock) {
                SculpturedStatueBlockEntity statueEntity = this.statueEntity(block);
                BlockEntityRenderer<?> renderer = Minecraft.m_91087_().m_167982_().m_112265_(statueEntity);
                if (renderer instanceof SculpturedStatueRenderer) {
                    SculpturedStatueRenderer statueRenderer = (SculpturedStatueRenderer)renderer;
                    if (pCamera == ItemDisplayContext.GUI) {
                        pMatrixStack.m_85836_();
                        pMatrixStack.m_252880_(0.5F, 0.5F, 0.5F);
                        pMatrixStack.m_252781_(Axis.f_252529_.m_252977_(30.0F));
                        pMatrixStack.m_252781_(Axis.f_252392_.m_252977_(-45.0F));
                        pMatrixStack.m_252880_(-0.5F, -0.5F, -0.5F);
                        pMatrixStack.m_252880_(0.0F, 0.25F, 0.0F);
                        statueRenderer.renderItem(block.m_49966_(), 180.0F, pMatrixStack, pBuffer, pLight);
                        pMatrixStack.m_85849_();
                    } else {
                        statueRenderer.renderItem(block.m_49966_(), 180.0F, pMatrixStack, pBuffer, pLight);
                    }
                }
            }
        }

    }
}
