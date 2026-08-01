package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.DiscipleServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyominous")
public class SabbathRitualHandler {

    @SubscribeEvent
    public static void onApostleSpawn(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.dimension() != Level.NETHER) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Apostle apostle)) {
            return;
        }

        if (!MobsConfig.ApostleSpawnDisciple.get()) {
            return;
        }

        if (apostle.getPersistentData().getBoolean("HasSpawnedDisciples")) {
            return;
        }

        apostle.getPersistentData().putBoolean("HasSpawnedDisciples", true);

        BlockPos apostlePos = apostle.blockPosition();

        spawnDiscipleServants(serverLevel, apostle, apostlePos);
    }

    private static void spawnDiscipleServants(ServerLevel serverLevel, Apostle apostle, BlockPos centerPos) {
        RandomSource random = serverLevel.getRandom();
        int discipleCount = random.nextInt(3);

        for (int i = 0; i < discipleCount; i++) {
            BlockPos offset = new BlockPos(
                    random.nextIntBetweenInclusive(-5, 5),
                    0,
                    random.nextIntBetweenInclusive(-5, 5)
            );
            BlockPos targetPos = centerPos.offset(offset);
            BlockPos spawnPos = BlockFinder.SummonPosition(apostle, targetPos);

            if (spawnPos != null) {
                spawnSingleDiscipleServant(serverLevel, spawnPos, apostle);
            }
        }
    }

    private static void spawnSingleDiscipleServant(ServerLevel serverLevel, BlockPos spawnPos, Apostle owner) {
        DiscipleServant discipleServant = ModEntityTypes.DISCIPLE_SERVANT.get().create(serverLevel);
        if (discipleServant == null) {
            return;
        }

        discipleServant.moveTo(
                (double) spawnPos.getX() + 0.5,
                spawnPos.getY(),
                (double) spawnPos.getZ() + 0.5,
                serverLevel.random.nextFloat() * 360.0F,
                0.0F
        );

        discipleServant.setTrueOwner(owner);
        discipleServant.setHostile(true);

        discipleServant.finalizeSpawn(
                serverLevel,
                serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED,
                null,
                null
        );

        ServerParticleUtil.circularParticles(
                serverLevel,
                ModParticleTypes.RISING_ENCHANT.get(),
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                1.0,
                0.0,
                0.0,
                2.0F
        );

        serverLevel.addFreshEntity(discipleServant);
    }
}