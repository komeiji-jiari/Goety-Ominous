package com.qiuyue.goetyominus.common.events;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominus.common.entities.ally.mobs.AcolyteServant;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyominus")
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

        if (!MobsConfig.ApostleSpawnAcolyte.get()) {
            return;
        }

        if (apostle.getPersistentData().getBoolean("HasSpawnedAcolytes")) {
            return;
        }

        apostle.getPersistentData().putBoolean("HasSpawnedAcolytes", true);

        BlockPos apostlePos = apostle.blockPosition();

        spawnAcolyteServants(serverLevel, apostle, apostlePos);
    }

    private static void spawnAcolyteServants(ServerLevel serverLevel, Apostle apostle, BlockPos centerPos) {
        RandomSource random = serverLevel.getRandom();
        int acolyteCount = random.nextInt(3);

        for (int i = 0; i < acolyteCount; i++) {
            BlockPos offset = new BlockPos(
                    random.nextIntBetweenInclusive(-5, 5),
                    0,
                    random.nextIntBetweenInclusive(-5, 5)
            );
            BlockPos targetPos = centerPos.offset(offset);
            BlockPos spawnPos = BlockFinder.SummonPosition(apostle, targetPos);

            if (spawnPos != null) {
                spawnSingleAcolyteServant(serverLevel, spawnPos, apostle);
            }
        }
    }

    private static void spawnSingleAcolyteServant(ServerLevel serverLevel, BlockPos spawnPos, Apostle owner) {
        AcolyteServant acolyteServant = ModEntityTypes.ACOLYTE_SERVANT.get().create(serverLevel);
        if (acolyteServant == null) {
            return;
        }

        acolyteServant.moveTo(
                (double) spawnPos.getX() + 0.5,
                spawnPos.getY(),
                (double) spawnPos.getZ() + 0.5,
                serverLevel.random.nextFloat() * 360.0F,
                0.0F
        );

        acolyteServant.setTrueOwner(owner);
        acolyteServant.setHostile(true);

        acolyteServant.finalizeSpawn(
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

        serverLevel.addFreshEntity(acolyteServant);
    }
}