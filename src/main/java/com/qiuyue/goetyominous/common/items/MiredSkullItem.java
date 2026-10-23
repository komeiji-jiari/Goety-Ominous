package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.client.render.item.MiredSkullISTER;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MiredSkullItem extends StandingAndWallBlockItem {

    public MiredSkullItem(Block standingBlock, Block wallBlock, Item.Properties properties) {
        super(standingBlock, wallBlock, properties, Direction.DOWN);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return MiredSkullISTER.get();
            }
        });
    }
}
