/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package com.vivideru.masteryofmagic.client;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModBlockRenderLayers {
    public static void register(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)((Block)GoetyMasteryOfMagicModBlocks.BLACKSTONE_CHALICE.get()), (RenderType)RenderType.m_110463_()));
    }
}

