package com.qiuyue.goetyominous.common.worldgen.crypt;

import com.Polarice3.Goety.api.blocks.ISeat;
import com.Polarice3.Goety.common.blocks.ThroneBlock;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CryptThroneSitters {

    public static final String TAG = "goety_crypt_throne";
    private static final int RANGE = 20;
    private static final int HEIGHT = 6;
    private static final int RETRY = 20;
    private static final int GIVE_UP = 600;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide || mob.tickCount % RETRY != 0
                || !mob.getTags().contains(TAG)) {
            return;
        }
        if (mob.isPassenger()) {
            mob.removeTag(TAG);
            return;
        }
        Level level = mob.level();
        BlockPos center = mob.blockPosition();
        List<BlockPos> free = new ArrayList<>();
        boolean incomplete = false;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RANGE, -HEIGHT, -RANGE), center.offset(RANGE, HEIGHT, RANGE))) {
            if (!level.isLoaded(pos)) {
                incomplete = true;
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof ThroneBlock throne && state.getValue(ThroneBlock.HALF) == DoubleBlockHalf.LOWER
                    && throne.isThrone(level, pos) && !ISeat.isSeatOccupied(level, pos)) {
                free.add(pos.immutable());
            }
        }
        if (!free.isEmpty()) {
            BlockPos seat = free.get(mob.getRandom().nextInt(free.size()));
            if (level.getBlockState(seat).getBlock() instanceof ThroneBlock throne) {
                mob.moveTo(seat.getX() + 0.5D, seat.getY(), seat.getZ() + 0.5D, mob.getYRot(), mob.getXRot());
                throne.placeSeat(level, seat, mob);
            }
            mob.removeTag(TAG);
        } else if (!incomplete || mob.tickCount >= GIVE_UP) {
            mob.removeTag(TAG);
        }
    }

    private CryptThroneSitters() {
    }
}
