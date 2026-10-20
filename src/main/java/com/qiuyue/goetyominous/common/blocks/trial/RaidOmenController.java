package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RaidOmenController {

    public static final int RAID_OMEN_TICKS = 600;
    private static final String ORIGIN_TAG = "GoetyOminousRaidOmenOrigin";

    private RaidOmenController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (!MobsConfig.RaidOmen.get() || player.isSpectator()) {
            return;
        }
        MobEffectInstance badOmen = player.getEffect(MobEffects.BAD_OMEN);
        if (badOmen == null) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.isVillage(player.blockPosition())) {
            return;
        }
        Raid current = level.getRaidAt(player.blockPosition());
        if (current != null && current.getBadOmenLevel() >= current.getMaxBadOmenLevel()) {
            return;
        }
        convert(player, badOmen.getAmplifier());
    }

    private static void convert(ServerPlayer player, int amplifier) {
        MobEffectInstance backup = player.getEffect(MobEffects.BAD_OMEN);
        if (backup == null) {
            return;
        }
        MobEffectInstance copy = new MobEffectInstance(backup);
        player.removeEffect(MobEffects.BAD_OMEN);
        player.addEffect(new MobEffectInstance(ModEffects.RAID_OMEN.get(),
                RAID_OMEN_TICKS, Mth.clamp(amplifier, 0, 4), false, true, true));
        if (player.hasEffect(ModEffects.RAID_OMEN.get())) {
            player.getPersistentData().putLong(ORIGIN_TAG, player.blockPosition().asLong());
        } else {
            player.addEffect(copy);
            player.getPersistentData().remove(ORIGIN_TAG);
        }
    }

    public static void finish(ServerPlayer player) {
        long origin = player.getPersistentData().getLong(ORIGIN_TAG);
        player.getPersistentData().remove(ORIGIN_TAG);
        if (!MobsConfig.RaidOmen.get() || origin == 0L || player.isSpectator()
                || !player.hasEffect(ModEffects.RAID_OMEN.get())) {
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos pos = BlockPos.of(origin);
        RaidStartContext.run(pos, () -> level.getRaids().createOrExtendRaid(player));
    }
}
