package com.qiuyue.goetyominous.common.blocks;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.blocks.trial.TrialSoundTypes;
import com.qiuyue.goetyominous.common.entities.hostile.Hurricane;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.utils.HurricaneCoreSummon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WhirlingCageBlock extends Block {
    private static final double CORE_HEIGHT = 0.25D;
    private static final double BREAKER_RADIUS = 6.0D;
    private static final UUID OMINOUS_HEALTH_ID = UUID.fromString("6f2b1c3a-9e4d-4a7b-8c1e-2d3f4a5b6c7d");
    private static final Map<BlockPos, Player> PENDING_BREAKER = new ConcurrentHashMap<>();

    public WhirlingCageBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 1200.0F)
                .sound(TrialSoundTypes.TRIAL_SPAWNER)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false));
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player != null && level instanceof ServerLevel) {
            PENDING_BREAKER.put(pos.immutable(), player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !isMoving && level instanceof ServerLevel serverLevel) {
            Player breaker = PENDING_BREAKER.remove(pos);
            if (breaker != null && breaker.level() != level) {
                breaker = null;
            }
            if (breaker == null) {
                breaker = serverLevel.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, BREAKER_RADIUS, true);
            }
            if (breaker == null || !breaker.isCreative()) {
                this.spawnHurricane(serverLevel, pos, breaker);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void spawnHurricane(ServerLevel level, BlockPos pos, @Nullable Player player) {
        Hurricane hurricane = new Hurricane(ModEntityTypes.HURRICANE.get(), level);
        Vec3 ground = Vec3.atBottomCenterOf(pos);
        hurricane.setPos(ground);
        hurricane.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        if (player != null && MobUtil.validEntity(player)) {
            hurricane.setTarget(player);
        }
        hurricane.setPersistenceRequired();
        if (player != null && (player.hasEffect(MobEffects.BAD_OMEN) || player.hasEffect(ModEffects.TRIAL_OMEN.get()))) {
            AttributeInstance maxHealth = hurricane.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null && maxHealth.getModifier(OMINOUS_HEALTH_ID) == null) {
                maxHealth.addPermanentModifier(new AttributeModifier(OMINOUS_HEALTH_ID, "goetyominous:ominous_health", 1.0D, AttributeModifier.Operation.MULTIPLY_BASE));
                hurricane.setHealth(hurricane.getMaxHealth());
            }
            hurricane.setOminousBuffed(true);
        }
        level.addFreshEntity(new HurricaneCoreSummon(level, ground.add(0.0D, CORE_HEIGHT, 0.0D), ground.y, hurricane));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ModSounds.BREEZE_WHIRL.get(), SoundSource.BLOCKS,
                    0.6F, 0.5F + random.nextFloat() * 0.3F, false);
        }
        double angle = random.nextDouble() * Math.PI * 2.0D;
        level.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5D + Math.cos(angle) * 0.3D, pos.getY() + 0.3D + random.nextDouble() * 0.4D,
                pos.getZ() + 0.5D + Math.sin(angle) * 0.3D, -Math.sin(angle) * 0.03D, 0.01D, Math.cos(angle) * 0.03D);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }
}
