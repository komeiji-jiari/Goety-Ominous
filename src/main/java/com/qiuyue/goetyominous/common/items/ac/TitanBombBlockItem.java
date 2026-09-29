package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class TitanBombBlockItem extends BlockItem {

    public TitanBombBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getPlayer() != null && SEHelper.isOnCooldown(context.getPlayer(), context.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }
}
