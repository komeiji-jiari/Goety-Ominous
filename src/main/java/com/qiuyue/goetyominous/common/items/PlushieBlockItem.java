package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.client.render.item.PlushieISTER;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class PlushieBlockItem extends BlockItem implements ICurioItem {

    public PlushieBlockItem(Block block) {
        super(block, new Item.Properties());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return PlushieISTER.get();
            }
        });
    }
}
