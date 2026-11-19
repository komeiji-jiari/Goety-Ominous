package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.entities.hostile.Hurricane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class OminousHurricaneSpawnEggItem extends ForgeSpawnEggItem {

    private static final UUID OMINOUS_HEALTH_ID = UUID.fromString("6f2b1c3a-9e4d-4a7b-8c1e-2d3f4a5b6c7d");

    public OminousHurricaneSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor,
                                        int highlightColor, Properties properties) {
        super(type, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState state = level.getBlockState(clickedPos);
        if (state.is(Blocks.SPAWNER) && level.getBlockEntity(clickedPos) instanceof SpawnerBlockEntity spawner) {
            EntityType<?> type = this.getType(stack.getTag());
            spawner.setEntityId(type, level.getRandom());
            spawner.setChanged();
            level.sendBlockUpdated(clickedPos, state, state, 3);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, clickedPos);
            stack.shrink(1);
            return InteractionResult.CONSUME;
        }
        BlockPos spawnPos = state.getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos : clickedPos.relative(direction);
        EntityType<?> type = this.getType(stack.getTag());
        Entity spawned = type.spawn(serverLevel, stack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG,
                true, !Objects.equals(clickedPos, spawnPos) && direction == Direction.UP);
        if (spawned != null) {
            if (spawned instanceof Hurricane hurricane) {
                applyOminous(hurricane);
            }
            stack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, clickedPos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = SpawnEggItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }
        BlockPos pos = hit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(stack);
        }
        if (level.mayInteract(player, pos) && player.mayUseItemAt(pos, hit.getDirection(), stack)) {
            EntityType<?> type = this.getType(stack.getTag());
            Entity spawned = type.spawn(serverLevel, stack, player, pos, MobSpawnType.SPAWN_EGG, false, false);
            if (spawned == null) {
                return InteractionResultHolder.pass(stack);
            }
            if (spawned instanceof Hurricane hurricane) {
                applyOminous(hurricane);
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.ENTITY_PLACE, spawned.position());
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    private static void applyOminous(Hurricane hurricane) {
        hurricane.setOminousBuffed(true);
        AttributeInstance maxHealth = hurricane.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(OMINOUS_HEALTH_ID) == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(OMINOUS_HEALTH_ID,
                    "goetyominous:ominous_health", 1.0D, AttributeModifier.Operation.MULTIPLY_BASE));
            hurricane.setHealth(hurricane.getMaxHealth());
        }
    }
}
