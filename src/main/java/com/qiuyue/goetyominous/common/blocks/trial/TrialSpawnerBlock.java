package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.List;

public class TrialSpawnerBlock extends BaseEntityBlock {
    public static final EnumProperty<TrialSpawnerState> STATE =
            EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);
    public static final BooleanProperty OMINOUS = VaultBlock.OMINOUS;

    public TrialSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, TrialSpawnerState.INACTIVE)
                .setValue(OMINOUS, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, OMINOUS);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        Component spawnName = getSpawnEntityDisplayName(stack);
        if (spawnName != null) {
            tooltip.add(spawnName);
        } else {
            tooltip.add(CommonComponents.EMPTY);
            tooltip.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            tooltip.add(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }
    }

    @Nullable
    private static Component getSpawnEntityDisplayName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("BlockEntityTag", 10)) {
            return null;
        }
        CompoundTag beTag = tag.getCompound("BlockEntityTag");
        if (!beTag.contains(TrialSpawnerData.TAG_SPAWN_DATA, 10)) {
            return null;
        }
        String id = beTag.getCompound(TrialSpawnerData.TAG_SPAWN_DATA).getCompound("entity").getString("id");
        EntityType<?> type = EntityType.byString(id).orElse(null);
        return type == null ? null : type.getDescription().copy().withStyle(ChatFormatting.GRAY);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(type, ModBlockEntities.TRIAL_SPAWNER.get(),
                    (tickLevel, pos, tickState, blockEntity) -> blockEntity.getTrialSpawner()
                            .tickServer(serverLevel, pos, tickState.getValue(OMINOUS)));
        }
        return createTickerHelper(type, ModBlockEntities.TRIAL_SPAWNER.get(),
                (tickLevel, pos, tickState, blockEntity) -> blockEntity.getTrialSpawner()
                        .tickClient(tickLevel, pos, tickState.getValue(OMINOUS)));
    }
}
