package com.qiuyue.goetyominous.common.blocks.ac;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.qiuyue.goetyominous.common.blocks.entities.ac.GrottoceratopsServantEggBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
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


public class GrottoceratopsServantEggBlock extends DinosaurEggBlock implements EntityBlock {

    public GrottoceratopsServantEggBlock(Properties properties) {
                super(properties, ACEntityRegistry.GROTTOCERATOPS, 8, 10);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AcBlockEntityRegistry.GROTTOCERATOPS_SERVANT_EGG.get().create(pos, state);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        // 仆从不在 AC 的 dinosaurs 标签里，原版 stepOn 的 tryTrample 会放行它，导致它踩坏自己下的蛋；
        // 仆从不触发踩踏（对应 AC 原版恐龙不踩蛋的行为），其余实体保持原版（canTrample 为 private 不可覆写）
        if (!(entity instanceof GrottoceratopsServant)) {
            super.stepOn(level, pos, state, entity);
        }
    }

    @Override
    public void spawnDinosaurs(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID owner = null;
        if (level.getBlockEntity(pos) instanceof GrottoceratopsServantEggBlockEntity eggBe) {
            owner = eggBe.getOwnerUUID();
        }
        // 已达仆从上限时不破壳：蛋块保留在 hatch=2 状态，随机刻会重试，等数量降下来再孵化，
        // 避免蛋块被移除却什么都没生成的静默损耗
        if (owner != null && GrottoceratopsServant.countServants(serverLevel, owner) >= MobsConfig.GrottoceratopsServantLimit.get()) {
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
                this.spawnWildGrottoceratops(serverLevel, pos, i);
            }
        }
    }

    private void spawnServant(ServerLevel level, BlockPos pos, UUID owner, int index) {
        if (GrottoceratopsServant.countServants(level, owner) >= MobsConfig.GrottoceratopsServantLimit.get()) {
            return;
        }
        GrottoceratopsServant baby = AcEntityRegistry.GROTTOCERATOPS_SERVANT.get().create(level);
        if (baby == null) {
            return;
        }
        baby.setAge(-24000);
        baby.setOwnerId(owner);
        baby.setPersistenceRequired();
        baby.moveTo(pos.getX() + 0.3D + index * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
        level.addFreshEntity(baby);
    }

    private void spawnWildGrottoceratops(ServerLevel level, BlockPos pos, int index) {
        Entity entity = ACEntityRegistry.GROTTOCERATOPS.get().create(level);
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
