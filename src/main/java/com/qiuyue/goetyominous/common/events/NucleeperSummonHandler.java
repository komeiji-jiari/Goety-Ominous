package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 注意:不能加 @Mod.EventBusSubscriber 注解——该注解会被 Forge 在 mod 构造时无条件
 * Class.forName,而本类直接引用 Alex's Caves 类型(ACBlockRegistry),AC 未加载时会
 * NoClassDefFoundError。由 GoetyOminous 构造器的 isAlexCavesLoaded() 门内手动
 * MinecraftForge.EVENT_BUS.register 本类。
 *
 * 核能苦力怕仆从多方块召唤:
 * 用 goety:animation_core 右键 alexscaves:block_of_uranium。若以铀块为中心,向上/下
 * 各搭 2 层的 1×1×5 垂直柱(从上到下:siren_light / nuclear_furnace_component /
 * block_of_uranium / nuclear_furnace_component / scrap_metal),则召唤一只
 * NucleeperServant(主人为右键玩家),并打碎整根结构、消耗 1 个动画核心。
 */
public class NucleeperSummonHandler {

    private static final Item ANIMATION_CORE =
            ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "animation_core"));

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }
        // 与 Goety 原版 AnimationCore.useOn 一致,只在主手生效。
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (ANIMATION_CORE == null || !stack.is(ANIMATION_CORE)) {
            return;
        }
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        // 被点方块必须是 block_of_uranium。
        if (!level.getBlockState(pos).is(ACBlockRegistry.BLOCK_OF_URANIUM.get())) {
            return;
        }
        // 该组合专属本功能,拦截掉 Goety 原版 AnimationCore.useOn 的后续逻辑。
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!underLimit(serverLevel, player)) {
            return;
        }
        if (!checkStructure(level, pos)) {
            return;
        }
        summon(serverLevel, level, pos, stack, player, event);
    }

    /** 1×1×5 结构:上 2 层为 siren_light / nuclear_furnace_component,下 2 层为 nuclear_furnace_component / scrap_metal。 */
    private static boolean checkStructure(Level level, BlockPos center) {
        return level.getBlockState(center.above(2)).is(ACBlockRegistry.SIREN_LIGHT.get())
                && level.getBlockState(center.above(1)).is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())
                && level.getBlockState(center.below(1)).is(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get())
                && level.getBlockState(center.below(2)).is(ACBlockRegistry.SCRAP_METAL.get());
    }

    private static boolean underLimit(ServerLevel level, Player player) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof NucleeperServant servant
                    && servant.isAlive()
                    && servant.getTrueOwner() == player) {
                count++;
            }
        }
        return count < MobsConfig.NucleeperServantLimit.get();
    }

    private static void summon(ServerLevel serverLevel, Level level, BlockPos pos, ItemStack stack,
                               Player player, PlayerInteractEvent.RightClickBlock event) {
        NucleeperServant servant = AcEntityRegistry.NUCLEEPER_SERVANT.get().create(level);
        if (servant == null) {
            return;
        }
        servant.setTrueOwner(player);
        servant.moveTo(pos.getX() + 0.5D, pos.getY() + 0.05D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        servant.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(servant.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null, null);
        if (serverLevel.addFreshEntity(servant)) {
            removeStructure(level, pos);
            stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, servant);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    /** 打碎整根 1×1×5 结构。 */
    private static void removeStructure(Level level, BlockPos center) {
        BlockPos[] positions = {
                center.above(2), center.above(1), center, center.below(1), center.below(2)
        };
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            level.levelEvent(2001, pos, Block.getId(state));
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
