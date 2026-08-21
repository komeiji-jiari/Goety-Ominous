package com.qiuyue.goetyominous.common.blocks.ac;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.qiuyue.goetyominous.common.blocks.entities.ac.VallumraptorServantEggBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.UUID;


public class VallumraptorServantEggBlock extends DinosaurEggBlock implements EntityBlock {

    public VallumraptorServantEggBlock(Properties properties) {
        super(properties, ACEntityRegistry.VALLUMRAPTOR, 8, 12);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AcBlockEntityRegistry.VALLUMRAPTOR_SERVANT_EGG.get().create(pos, state);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof VallumraptorServant)) {
            super.stepOn(level, pos, state, entity);
        }
    }

    @Override
    public void spawnDinosaurs(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID owner = null;
        if (level.getBlockEntity(pos) instanceof VallumraptorServantEggBlockEntity eggBe) {
            owner = eggBe.getOwnerUUID();
        }
        if (owner != null && VallumraptorServant.countServants(serverLevel, owner) >= MobsConfig.VallumraptorServantLimit.get()) {
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
                this.spawnWildVallumraptor(serverLevel, pos, i);
            }
        }
    }

    private void spawnServant(ServerLevel level, BlockPos pos, UUID owner, int index) {
        if (VallumraptorServant.countServants(level, owner) >= MobsConfig.VallumraptorServantLimit.get()) {
            return;
        }
        VallumraptorServant baby = AcEntityRegistry.VALLUMRAPTOR_SERVANT.get().create(level);
        if (baby == null) {
            return;
        }
        baby.setAge(-24000);
        baby.setOwnerId(owner);
        baby.setElder(baby.getRandom().nextInt(100) < MobsConfig.VallumraptorElderChance.get());
        baby.setPersistenceRequired();
        baby.moveTo(pos.getX() + 0.3D + index * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
        level.addFreshEntity(baby);
    }

    private void spawnWildVallumraptor(ServerLevel level, BlockPos pos, int index) {
        Entity entity = ACEntityRegistry.VALLUMRAPTOR.get().create(level);
        if (entity == null) {
            return;
        }
        if (entity instanceof Animal animal) {
            animal.setAge(-24000);
        }
        entity.moveTo(pos.getX() + 0.3D + index * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
        level.addFreshEntity(entity);
    }
}
