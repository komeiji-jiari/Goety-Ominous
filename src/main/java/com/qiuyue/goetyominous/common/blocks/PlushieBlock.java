package com.qiuyue.goetyominous.common.blocks;

import com.Polarice3.Goety.init.ModSounds;
import com.qiuyue.goetyominous.common.blocks.entities.PlushieBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlushieBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, Equipable {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D,
            13.0D, 15.0D, 13.0D);

    public String name;
    public static final String EXPLOSION_PROOF_TEXTURE = "him";
    private static final String MESSAGE_KEY_PREFIX = "message.goetyominous.plushie.";
    private static final float PLUSHIE_MESSAGE_CHANCE = 0.3F;

    protected PlushieBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public PlushieBlock(String name) {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.SNOW)
                .instrument(NoteBlockInstrument.GUITAR)
                .strength(0.8F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .instabreak());
        this.name = name;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public PlushieBlock() {
        this("");
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public String getPlushieName() {
        return this.name;
    }

    @Override
    public float getExplosionResistance() {
        if (EXPLOSION_PROOF_TEXTURE.equals(this.name)) {
            return Float.MAX_VALUE;
        }
        return super.getExplosionResistance();
    }

    private SoundEvent getSqueezeSound(Level level) {
        if ("player_7".equals(this.name) || "player_seven".equals(this.name) || "eunice_carrill".equals(this.name)) {
            return level.random.nextBoolean() ? SoundEvents.CAT_HURT : SoundEvents.CAT_DEATH;
        }
        if ("lll152".equals(this.name)) {
            return SoundEvents.WITHER_AMBIENT;
        }
        if ("skillupper".equals(this.name)) {
            return level.random.nextBoolean() ? SoundEvents.AXOLOTL_HURT : SoundEvents.AXOLOTL_DEATH;
        }
        if ("wu1wu2".equals(this.name)) {
            return SoundEvents.GENERIC_SPLASH;
        }
        return ModSounds.PLUSHIE_SQUEEZE.get();
    }

    private void sendPlushieMessage(Player player) {
        if (!"player_7".equals(this.name) && !"player_seven".equals(this.name) && !"eunice_carrill".equals(this.name)) {
            return;
        }
        if (player.getRandom().nextFloat() >= PLUSHIE_MESSAGE_CHANCE) {
            return;
        }
        List<String> messages = new ArrayList<>();
        for (Map.Entry<String, String> entry : Language.getInstance().getLanguageData().entrySet()) {
            if (entry.getKey().startsWith(MESSAGE_KEY_PREFIX) && !entry.getValue().isEmpty()) {
                messages.add(entry.getValue());
            }
        }
        if (!messages.isEmpty()) {
            player.displayClientMessage(Component.literal(
                    messages.get(player.getRandom().nextInt(messages.size()))), false);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof PlushieBlockEntity entity) {
            PlushieBlockEntity.startAnimating(entity);
        }
        if (level.isClientSide) {
            this.sendPlushieMessage(player);
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, this.getSqueezeSound(level), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean flag = level.getFluidState(pos).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(ROTATION, Mth.floor((double) (context.getRotation() * 16.0F / 360.0F) + 0.5D) & 15)
                .setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
            level.setBlock(pos, state.setValue(WATERLOGGED, Boolean.TRUE), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            return true;
        }
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlushieBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (world, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof PlushieBlockEntity plushie) {
                PlushieBlockEntity.animation(plushie);
            }
        };
    }
}
