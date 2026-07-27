package com.qiuyue.goetyominus.common.events;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.hostile.UrbhadhachEntity;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.common.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UrbhadhachSpawnEvents {

    private static long globalCooldown = 0;
    private static final int COOLDOWN = 6000;
    private static final int MIN_BABY = 4;
    private static final int MAX_COUNT = 3;
    private static final float CHANCE = 0.35F;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!(event.level instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;
        if (level.getMoonPhase() != 0) return;
        if (level.getGameTime() % 200 != 0) return;
        if (level.getGameTime() - globalCooldown < COOLDOWN) return;

        for (ServerPlayer player : level.players()) {
            BlockPos pos = player.blockPosition();

            boolean hasAdults = level.getEntitiesOfClass(Villager.class,
                            player.getBoundingBox().inflate(64)).stream()
                    .anyMatch(v -> !v.isBaby());
            if (!hasAdults) continue;

            long babies = level.getEntitiesOfClass(Villager.class,
                            player.getBoundingBox().inflate(64)).stream()
                    .filter(Villager::isBaby).count();
            if (babies < MIN_BABY) continue;

            long existing = level.getEntitiesOfClass(UrbhadhachEntity.class,
                            player.getBoundingBox().inflate(64)).stream()
                    .filter(e -> e.isAlive()).count();
            if (existing >= MAX_COUNT) continue;

            if (level.random.nextFloat() >= CHANCE) continue;

            for (int i = 0; i < 10; i++) {
                int x = pos.getX() + level.random.nextInt(64) - 32;
                int z = pos.getZ() + level.random.nextInt(64) - 32;
                BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        new BlockPos(x, 0, z));

                UrbhadhachEntity entity = ModEntityTypes.URBHADHACH.get().create(level);
                if (entity != null) {
                    entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            level.random.nextFloat() * 360, 0);
                    entity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                            MobSpawnType.NATURAL, null, null);
                    if (level.addFreshEntity(entity)) {
                        player.addEffect(new MobEffectInstance(GoetyEffects.SENSE_LOSS.get(), 100, 1));
                        globalCooldown = level.getGameTime() + level.random.nextInt(200);
                        level.playSound(null, player.blockPosition(), ModSounds.URBHADHACH_CRY.get(), net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.0F);
                        player.sendSystemMessage(Component.translatable(
                                        "message.goetyominus.urbhadhach.spawn")
                                .withStyle(ChatFormatting.DARK_BLUE));
                        break;
                    }
                }
            }
        }
    }
}
