package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.common.blocks.trial.CopperGrateBlock;
import com.qiuyue.goetyominous.common.blocks.trial.TrialBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TrialClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> TrialBlocks.BLOCKS.getEntries().forEach(holder -> {
            Block block = holder.get();
            if (block instanceof DoorBlock || block instanceof TrapDoorBlock || block instanceof CopperGrateBlock) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        }));
    }
}
