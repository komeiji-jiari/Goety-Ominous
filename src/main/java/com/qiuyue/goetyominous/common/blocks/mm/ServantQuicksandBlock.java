package com.qiuyue.goetyominous.common.blocks.mm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.alexander.mutantmore.config.QuicksandCommonConfig;
import com.alexander.mutantmore.init.TagInit;
import com.qiuyue.goetyominous.common.blocks.entities.mm.ServantQuicksandBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.mm.AreaDamage;
import com.qiuyue.goetyominous.common.init.mm.MmBlockEntities;
import com.qiuyue.goetyominous.common.init.mm.MmBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ServantQuicksandBlock extends QuicksandBlock {

    public ServantQuicksandBlock(Properties properties) {
        super(properties);
    }

    public static Block forSandType(String sandType) {
        return "_red_sand".equals(sandType) ? MmBlocks.SERVANT_RED_QUICKSAND.get() : MmBlocks.SERVANT_QUICKSAND.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ServantQuicksandBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MmBlockEntities.SERVANT_QUICKSAND.get(), ServantQuicksandBlockEntity::tick);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player || entity instanceof Summoned) {
            return;
        }
        boolean inGeyser = false;
        for (AreaDamage areaDamage : level.getEntitiesOfClass(AreaDamage.class, entity.getBoundingBox())) {
            if (areaDamage.getActiveType() != 2 && areaDamage.getAreaDamageType() != 2) continue;
            inGeyser = true;
            break;
        }
        boolean wearingImmuneBoots = entity instanceof LivingEntity living
                && living.getItemBySlot(EquipmentSlot.FEET).is(TagInit.Items.QUICKSAND_IMMUNE_BOOTS);
        if (entity.fallDistance <= 5.0F
                && entity.getY() <= (double) pos.getY() + 0.25
                && !entity.getType().is(TagInit.EntityTypes.UNAFFECTED_BY_QUICKSAND)
                && !inGeyser
                && !wearingImmuneBoots) {
            double horizontal = QuicksandCommonConfig.horizontal_movement_multiplication.get();
            entity.makeStuckInBlock(state, new Vec3(horizontal, QuicksandCommonConfig.vertical_movement_multiplication.get(), horizontal));
        }
    }
}
