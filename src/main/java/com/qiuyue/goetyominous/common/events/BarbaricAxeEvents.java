package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.BarbaricAxeItem;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class BarbaricAxeEvents {

    private static final Direction[] DIRECTIONS = Direction.values();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.isShiftKeyDown()) {
            return;
        }
        if (!(event.getLevel() instanceof Level level)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BarbaricAxeItem)) {
            return;
        }

        BlockState origin = event.getState();
        if (origin == null || !origin.is(BlockTags.LOGS)) {
            return;
        }

        Block block = origin.getBlock();
        int remaining = WeaponConfig.BarbaricAxeChainLimit.get() - 1;
        if (remaining <= 0) {
            return;
        }

        BlockPos originPos = event.getPos();
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        visited.add(originPos);
        queue.add(originPos);

        int broken = 0;
        while (!queue.isEmpty() && broken < remaining) {
            BlockPos current = queue.poll();
            for (Direction direction : DIRECTIONS) {
                BlockPos next = current.relative(direction);
                if (!visited.add(next)) {
                    continue;
                }
                if (level.getBlockState(next).getBlock() != block) {
                    continue;
                }
                if (!level.destroyBlock(next, !player.isCreative(), player)) {
                    continue;
                }
                broken++;
                if (broken >= remaining) {
                    return;
                }
                queue.add(next);
            }
        }
    }
}
