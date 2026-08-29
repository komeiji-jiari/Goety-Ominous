package com.qiuyue.goetyominous.common.blocks.ac;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.qiuyue.goetyominous.common.blocks.entities.ac.TremorzillaServantEggBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.UUID;

/**
 * 特雷莫兹拉仆从蛋块。孵出 TremorzillaServant(绑定蛋放置者 UUID)。
 * 蛋块属性沿用 AC 原版 TremorzillaEggBlock(抗爆 5000),孵化沿用项目仆从蛋惯例(默认 NEEDS_PLAYER 孵化)。
 */
public class TremorzillaServantEggBlock extends DinosaurEggBlock implements EntityBlock {

    public TremorzillaServantEggBlock(Properties properties) {
        super(properties, ACEntityRegistry.TREMORZILLA, 10, 16);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AcBlockEntityRegistry.TREMORZILLA_SERVANT_EGG.get().create(pos, state);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof TremorzillaServant)) {
            super.stepOn(level, pos, state, entity);
        }
    }

    @Override
    public void spawnDinosaurs(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID owner = null;
        if (level.getBlockEntity(pos) instanceof TremorzillaServantEggBlockEntity eggBe) {
            owner = eggBe.getOwnerUUID();
        }
        if (owner != null && TremorzillaServant.countServants(serverLevel, owner) >= MobsConfig.TremorzillaServantLimit.get()) {
            return;
        }
        level.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
        level.removeBlock(pos, false);
        int born = this.getDinosaursBornFrom(state);
        for (int i = 0; i < born; ++i) {
            serverLevel.levelEvent(2001, pos, Block.getId(state));
            if (owner != null) {
                this.spawnServant(serverLevel, pos, owner, i);
            } else {
                this.spawnWildTremorzilla(serverLevel, pos, i);
            }
        }
    }

    private void spawnServant(ServerLevel level, BlockPos pos, UUID owner, int index) {
        if (TremorzillaServant.countServants(level, owner) >= MobsConfig.TremorzillaServantLimit.get()) {
            return;
        }
        TremorzillaServant baby = AcEntityRegistry.TREMORZILLA_SERVANT.get().create(level);
        if (baby == null) {
            return;
        }
        baby.setAge(-24000);
        baby.setOwnerId(owner);
        baby.setPersistenceRequired();
        baby.moveTo(pos.getX() + 0.3D + index * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
        level.addFreshEntity(baby);
    }

    private void spawnWildTremorzilla(ServerLevel level, BlockPos pos, int index) {
        Entity entity = ACEntityRegistry.TREMORZILLA.get().create(level);
        if (entity == null) {
            return;
        }
        if (entity instanceof AgeableMob ageable) {
            ageable.setAge(-24000);
        }
        entity.moveTo(pos.getX() + 0.3D + index * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
        level.addFreshEntity(entity);
    }
}
