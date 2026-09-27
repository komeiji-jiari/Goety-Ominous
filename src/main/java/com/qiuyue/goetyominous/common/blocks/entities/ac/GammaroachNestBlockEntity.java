package com.qiuyue.goetyominous.common.blocks.entities.ac;

import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.GammaroachServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GammaroachNestBlockEntity extends TrainingBlockEntity {

    public GammaroachNestBlockEntity(BlockPos pos, BlockState state) {
        super(AcBlockEntityRegistry.GAMMAROACH_NEST.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        super.tick(level, blockPos, blockState, blockEntity);
        if (!(level instanceof ServerLevel serverLevel) || !this.isTraining()) {
            return;
        }
        if (this.trainTime < this.getMaxTrainTime()) {
            if (level.random.nextInt(10) == 0) {
                ServerParticleUtil.blockBreakParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), blockPos, blockState, serverLevel);
            }
            if (this.trainTime == 20) {
                level.playSound(null, blockPos, ACSoundRegistry.GAMMAROACH_IDLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (level.random.nextInt(10) == 0) {
            serverLevel.sendParticles(ACParticleRegistry.GAMMAROACH.get(),
                    blockPos.getX() + level.random.nextDouble(),
                    blockPos.getY() + level.random.nextDouble(),
                    blockPos.getZ() + level.random.nextDouble(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void setVariant(ItemStack itemStack, Level level, BlockPos blockPos) {
        if (this.getTrainMob() != AcEntityRegistry.GAMMAROACH_SERVANT.get()) {
            this.setEntityType(AcEntityRegistry.GAMMAROACH_SERVANT.get());
            this.markUpdated();
        }
    }

    @Override
    public void startTraining(int amount, ItemStack itemStack) {
        super.startTraining(amount, itemStack);
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ACSoundRegistry.GAMMAROACH_ATTACK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void playSpawnSound() {
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ACSoundRegistry.GAMMAROACH_SPRAY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public int maxTrainAmount() {
        return 5;
    }

    @Override
    public boolean summonLimit() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return false;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            return false;
        }
        int count = 0;
        for (Entity entity : serverLevel.getAllEntities()) {
            if (entity instanceof GammaroachServant servant && servant.isAlive() && servant.getTrueOwner() == owner) {
                ++count;
            }
        }
        return count >= MobsConfig.GammaroachServantLimit.get();
    }

    @Override
    public boolean isFuel(ItemStack itemStack) {
        return itemStack.is(ACItemRegistry.SPELUNKIE.get());
    }
}
