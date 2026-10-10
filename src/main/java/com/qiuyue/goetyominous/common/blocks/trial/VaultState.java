package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum VaultState implements StringRepresentable {
    INACTIVE("inactive", LightLevel.HALF_LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            sharedData.setDisplayItem(ItemStack.EMPTY);
            level.levelEvent(3016, pos, ominous ? 1 : 0);
        }
    },
    ACTIVE("active", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            if (!sharedData.hasDisplayItem()) {
                VaultBlockEntity.Server.cycleDisplayItemFromLootTable(level, this, config, sharedData, pos);
            }
            level.levelEvent(3015, pos, ominous ? 1 : 0);
        }
    },
    UNLOCKING("unlocking", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS);
        }
    },
    EJECTING("ejecting", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
        }

        @Override
        protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            level.playSound(null, pos, ModSounds.VAULT_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
        }
    };

    private static final int DELAY_BETWEEN_EJECTIONS_TICKS = 20;
    private static final int DELAY_AFTER_LAST_EJECTION_TICKS = 20;
    private static final int DELAY_BEFORE_FIRST_EJECTION_TICKS = 20;
    private final String stateName;
    private final LightLevel lightLevel;

    VaultState(String stateName, LightLevel lightLevel) {
        this.stateName = stateName;
        this.lightLevel = lightLevel;
    }

    @Override
    public String getSerializedName() {
        return this.stateName;
    }

    public int lightLevel() {
        return this.lightLevel.value;
    }

    public VaultState tickAndGetNext(ServerLevel level, BlockPos pos, VaultConfig config,
                                     VaultServerData serverData, VaultSharedData sharedData) {
        return switch (this) {
            case INACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.activationRange());
            case ACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
            case UNLOCKING -> {
                serverData.pauseStateUpdatingUntil(level.getGameTime() + DELAY_BEFORE_FIRST_EJECTION_TICKS);
                yield EJECTING;
            }
            case EJECTING -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.markEjectionFinished();
                    yield updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
                }
                float progress = serverData.ejectionProgress();
                ejectResultItem(level, pos, serverData.popNextItemToEject(), progress);
                sharedData.setDisplayItem(serverData.getNextItemToEject());
                int delay = serverData.getItemsToEject().isEmpty()
                        ? DELAY_AFTER_LAST_EJECTION_TICKS
                        : DELAY_BETWEEN_EJECTIONS_TICKS;
                serverData.pauseStateUpdatingUntil(level.getGameTime() + delay);
                yield EJECTING;
            }
        };
    }

    private static VaultState updateStateForConnectedPlayers(ServerLevel level, BlockPos pos, VaultConfig config,
                                                             VaultServerData serverData, VaultSharedData sharedData,
                                                             double deactivationRange) {
        sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, deactivationRange);
        serverData.pauseStateUpdatingUntil(level.getGameTime() + 20L);
        return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onTransition(ServerLevel level, BlockPos pos, VaultState state, VaultConfig config,
                             VaultSharedData sharedData, boolean isOminous) {
        this.onExit(level, pos, config, sharedData);
        state.onEnter(level, pos, config, sharedData, isOminous);
    }

    protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
    }

    protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
    }

    private void ejectResultItem(ServerLevel level, BlockPos pos, ItemStack stack, float ejectionProgress) {
        DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.UP,
                Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2D));
        level.levelEvent(3017, pos, 0);
        level.playSound(null, pos, ModSounds.VAULT_EJECT_ITEM.get(), SoundSource.BLOCKS,
                1.0F, 0.8F + 0.4F * ejectionProgress);
    }

    enum LightLevel {
        HALF_LIT(6),
        LIT(12);

        final int value;

        LightLevel(int value) {
            this.value = value;
        }
    }
}
