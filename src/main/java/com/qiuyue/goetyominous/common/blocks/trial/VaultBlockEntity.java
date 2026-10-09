package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.VaultConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class VaultBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VaultServerData serverData = new VaultServerData();
    private final VaultSharedData sharedData = new VaultSharedData();
    private final VaultClientData clientData = new VaultClientData();
    private VaultConfig config;

    public VaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VAULT.get(), pos, state);
        this.config = VaultConfig.defaultFor(state.getValue(VaultBlock.OMINOUS));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return Util.make(new CompoundTag(),
                tag -> tag.put("shared_data", encode(VaultSharedData.CODEC, this.sharedData)));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("config", encode(VaultConfig.CODEC, this.config));
        tag.put("shared_data", encode(VaultSharedData.CODEC, this.sharedData));
        tag.put("server_data", encode(VaultServerData.CODEC, this.serverData));
    }

    private static <T> Tag encode(Codec<T> codec, T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, LOGGER::error);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        DynamicOps<Tag> ops = NbtOps.INSTANCE;
        if (tag.contains("server_data")) {
            VaultServerData.CODEC.parse(ops, tag.get("server_data"))
                    .resultOrPartial(LOGGER::error).ifPresent(this.serverData::set);
        }
        if (tag.contains("config")) {
            VaultConfig.CODEC.parse(ops, tag.get("config"))
                    .resultOrPartial(LOGGER::error).ifPresent(cfg -> this.config = cfg);
        }
        if (tag.contains("shared_data")) {
            VaultSharedData.CODEC.parse(ops, tag.get("shared_data"))
                    .resultOrPartial(LOGGER::error).ifPresent(this.sharedData::set);
        }
    }

    @Nullable
    public VaultServerData getServerData() {
        return this.level != null && !this.level.isClientSide ? this.serverData : null;
    }

    public VaultSharedData getSharedData() {
        return this.sharedData;
    }

    public VaultClientData getClientData() {
        return this.clientData;
    }

    public VaultConfig getConfig() {
        return this.config;
    }

    public void setConfig(VaultConfig config) {
        this.config = config;
    }

    public static final class Server {
        private static final int UNLOCKING_DELAY_TICKS = 14;
        private static final int DISPLAY_CYCLE_TICK_RATE = 20;
        private static final int INSERT_FAIL_SOUND_BUFFER_TICKS = 15;

        public static void tick(ServerLevel level, BlockPos pos, BlockState state, VaultConfig config,
                                VaultServerData serverData, VaultSharedData sharedData) {
            VaultState vaultState = state.getValue(VaultBlock.STATE);
            // ⚠️ 1.21 原版这里只看时间，**没有** state == ACTIVE 这个条件（那是客户端判断要不要
            // 渲染旋转用的）。之前误加进来 → vault 处于 INACTIVE（附近没玩家）时展示物品被清空，
            // 表现就是"宝库里什么都没有在转"。
            if (shouldCycleDisplayItem(level.getGameTime())) {
                cycleDisplayItemFromLootTable(level, vaultState, config, sharedData, pos);
            }
            BlockState blockState = state;
            if (level.getGameTime() >= serverData.stateUpdatingResumesAt()) {
                blockState = state.setValue(VaultBlock.STATE, vaultState.tickAndGetNext(level, pos, config, serverData, sharedData));
                if (!state.equals(blockState)) {
                    setVaultState(level, pos, state, blockState, config, sharedData);
                }
            }
            if (serverData.isDirty || sharedData.isDirty) {
                setChanged(level, pos, state);
                if (sharedData.isDirty) {
                    level.sendBlockUpdated(pos, state, blockState, 2);
                }
                serverData.isDirty = false;
                sharedData.isDirty = false;
            }
        }

        public static void tryInsertKey(ServerLevel level, BlockPos pos, BlockState state, VaultConfig config,
                                        VaultServerData serverData, VaultSharedData sharedData, Player player, ItemStack stack) {
            VaultState vaultState = state.getValue(VaultBlock.STATE);
            if (!canEjectReward(config, vaultState)) {
                return;
            }
            if (!isValidToInsert(config, stack)) {
                playInsertFailSound(level, serverData, pos, ModSounds.VAULT_INSERT_ITEM_FAIL.get());
            } else if (serverData.hasRewardedPlayer(player)) {
                playInsertFailSound(level, serverData, pos, ModSounds.VAULT_REJECT_REWARDED_PLAYER.get());
            } else {
                List<ItemStack> items = resolveItemsToEject(level, config, pos, player);
                if (!items.isEmpty()) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(config.keyItem().getCount());
                    }
                    unlock(level, state, pos, config, serverData, sharedData, items);
                    serverData.addToRewardedPlayers(player);
                    sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, config.deactivationRange());
                }
            }
        }

        static void setVaultState(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState,
                                  VaultConfig config, VaultSharedData sharedData) {
            VaultState oldVaultState = oldState.getValue(VaultBlock.STATE);
            VaultState newVaultState = newState.getValue(VaultBlock.STATE);
            level.setBlock(pos, newState, 3);
            oldVaultState.onTransition(level, pos, newVaultState, config, sharedData, newState.getValue(VaultBlock.OMINOUS));
        }

        static void cycleDisplayItemFromLootTable(ServerLevel level, VaultState state, VaultConfig config,
                                                  VaultSharedData sharedData, BlockPos pos) {
            // ⚠️ 1.21 原版这里**不按状态过滤**（不是 canEjectReward 那套）：INACTIVE 的 vault 也应该
            // 有物品在转。loot table 为空时 getRandomItems 返回空表 → displayItem 自然是 EMPTY。
            ItemStack itemStack = getRandomDisplayItemFromLootTable(level, pos,
                    config.overrideLootTableToDisplay().orElse(config.lootTable()));
            sharedData.setDisplayItem(itemStack);
        }

        private static ItemStack getRandomDisplayItemFromLootTable(ServerLevel level, BlockPos pos, ResourceLocation lootTable) {
            LootTable table = level.getServer().getLootData().getLootTable(lootTable);
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .create(LootContextParamSets.CHEST);
            ObjectArrayList<ItemStack> items = table.getRandomItems(params);
            return items.isEmpty() ? ItemStack.EMPTY : Util.getRandom(items, level.getRandom());
        }

        private static void unlock(ServerLevel level, BlockState state, BlockPos pos, VaultConfig config,
                                   VaultServerData serverData, VaultSharedData sharedData, List<ItemStack> itemsToEject) {
            serverData.setItemsToEject(itemsToEject);
            sharedData.setDisplayItem(serverData.getNextItemToEject());
            serverData.pauseStateUpdatingUntil(level.getGameTime() + UNLOCKING_DELAY_TICKS);
            setVaultState(level, pos, state, state.setValue(VaultBlock.STATE, VaultState.UNLOCKING), config, sharedData);
        }

        private static List<ItemStack> resolveItemsToEject(ServerLevel level, VaultConfig config, BlockPos pos, Player player) {
            LootTable table = level.getServer().getLootData().getLootTable(config.lootTable());
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withLuck(player.getLuck())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.CHEST);
            return table.getRandomItems(params);
        }

        private static boolean canEjectReward(VaultConfig config, VaultState state) {
            return config.lootTable() != BuiltInLootTables.EMPTY && !config.keyItem().isEmpty() && state != VaultState.INACTIVE;
        }

        private static boolean isValidToInsert(VaultConfig config, ItemStack stack) {
            return ItemStack.isSameItemSameTags(stack, config.keyItem()) && stack.getCount() >= config.keyItem().getCount();
        }

        private static boolean shouldCycleDisplayItem(long gameTime) {
            return gameTime % DISPLAY_CYCLE_TICK_RATE == 0L;
        }

        private static void playInsertFailSound(ServerLevel level, VaultServerData serverData, BlockPos pos, SoundEvent sound) {
            if (level.getGameTime() >= serverData.getLastInsertFailTimestamp() + INSERT_FAIL_SOUND_BUFFER_TICKS) {
                level.playSound(null, pos, sound, SoundSource.BLOCKS);
                serverData.setLastInsertFailTimestamp(level.getGameTime());
            }
        }
    }

    public static final class Client {
        private static final int PARTICLE_TICK_RATE = 20;
        private static final float IDLE_PARTICLE_CHANCE = 0.5F;
        private static final float AMBIENT_SOUND_CHANCE = 0.02F;
        private static final int ACTIVATION_PARTICLE_COUNT = 20;
        private static final int DEACTIVATION_PARTICLE_COUNT = 20;

        public static void tick(Level level, BlockPos pos, BlockState state, VaultClientData clientData, VaultSharedData sharedData) {
            clientData.updateDisplayItemSpin();
            if (level.getGameTime() % PARTICLE_TICK_RATE == 0L) {
                emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
            }
            emitIdleParticles(level, pos, sharedData,
                    state.getValue(VaultBlock.OMINOUS) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
            playIdleSounds(level, pos, sharedData);
        }

        public static void emitActivationParticles(Level level, BlockPos pos, BlockState state,
                                                   VaultSharedData sharedData, ParticleOptions particle) {
            emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
            RandomSource random = level.random;
            for (int i = 0; i < ACTIVATION_PARTICLE_COUNT; ++i) {
                Vec3 vec3 = randomPosInsideCage(pos, random);
                level.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
                level.addParticle(particle, vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
            }
        }

        public static void emitDeactivationParticles(Level level, BlockPos pos, ParticleOptions particle) {
            RandomSource random = level.random;
            for (int i = 0; i < DEACTIVATION_PARTICLE_COUNT; ++i) {
                Vec3 vec3 = randomPosCenterOfCage(pos, random);
                Vec3 vel = new Vec3(random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D);
                level.addParticle(particle, vec3.x, vec3.y, vec3.z, vel.x, vel.y, vel.z);
            }
        }

        private static void emitIdleParticles(Level level, BlockPos pos, VaultSharedData sharedData, ParticleOptions particle) {
            RandomSource random = level.getRandom();
            if (random.nextFloat() <= IDLE_PARTICLE_CHANCE) {
                Vec3 vec3 = randomPosInsideCage(pos, random);
                level.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
                if (shouldDisplayActiveEffects(sharedData)) {
                    level.addParticle(particle, vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        private static void emitConnectionParticlesForPlayer(Level level, Vec3 keyhole, Player player) {
            RandomSource random = level.random;
            Vec3 toPlayer = keyhole.vectorTo(player.position().add(0.0D, player.getBbHeight() / 2.0F, 0.0D));
            int count = Mth.nextInt(random, 2, 5);
            for (int i = 0; i < count; ++i) {
                Vec3 velocity = toPlayer.offsetRandom(random, 1.0F);
                level.addParticle(ModParticleTypes.VAULT_CONNECTION.get(), keyhole.x, keyhole.y, keyhole.z,
                        velocity.x, velocity.y, velocity.z);
            }
        }

        private static void emitConnectionParticlesForNearbyPlayers(Level level, BlockPos pos, BlockState state, VaultSharedData sharedData) {
            Set<UUID> connected = sharedData.getConnectedPlayers();
            if (connected.isEmpty()) {
                return;
            }
            Vec3 keyhole = keyholePos(pos, state.getValue(VaultBlock.FACING));
            for (UUID uuid : connected) {
                Player player = level.getPlayerByUUID(uuid);
                if (player == null || !isWithinConnectionRange(pos, sharedData, player)) {
                    continue;
                }
                emitConnectionParticlesForPlayer(level, keyhole, player);
            }
        }

        private static boolean isWithinConnectionRange(BlockPos pos, VaultSharedData sharedData, Player player) {
            return player.blockPosition().distSqr(pos) <= Mth.square(sharedData.connectedParticlesRange());
        }

        private static void playIdleSounds(Level level, BlockPos pos, VaultSharedData sharedData) {
            if (shouldDisplayActiveEffects(sharedData)) {
                RandomSource random = level.getRandom();
                if (random.nextFloat() <= AMBIENT_SOUND_CHANCE) {
                    level.playLocalSound(pos, ModSounds.VAULT_AMBIENT.get(), SoundSource.BLOCKS,
                            random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
                }
            }
        }

        public static boolean shouldDisplayActiveEffects(VaultSharedData sharedData) {
            return sharedData.hasDisplayItem();
        }

        private static Vec3 randomPosCenterOfCage(BlockPos pos, RandomSource random) {
            return Vec3.atLowerCornerOf(pos).add(
                    Mth.nextDouble(random, 0.4D, 0.6D),
                    Mth.nextDouble(random, 0.4D, 0.6D),
                    Mth.nextDouble(random, 0.4D, 0.6D));
        }

        private static Vec3 randomPosInsideCage(BlockPos pos, RandomSource random) {
            return Vec3.atLowerCornerOf(pos).add(
                    Mth.nextDouble(random, 0.1D, 0.9D),
                    Mth.nextDouble(random, 0.25D, 0.75D),
                    Mth.nextDouble(random, 0.1D, 0.9D));
        }

        private static Vec3 keyholePos(BlockPos pos, Direction facing) {
            return Vec3.atBottomCenterOf(pos).add(
                    (double) facing.getStepX() * 0.5D, 1.75D, (double) facing.getStepZ() * 0.5D);
        }
    }
}
