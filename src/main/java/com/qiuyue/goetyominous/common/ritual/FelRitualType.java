package com.qiuyue.goetyominous.common.ritual;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

public class FelRitualType implements IRitualType {
    private final String name;

    private static final ColorUtil FEL_PURPLE = new ColorUtil(0x8A2BE2);
    private static final ColorUtil DEEP_PURPLE = new ColorUtil(0x4B0082);
    private static final ColorUtil BRIGHT_PURPLE = new ColorUtil(0x9B30FF);
    private static final ColorUtil ACID_GREEN = new ColorUtil(0x5C7A3E);
    private static final ColorUtil FEL_BLACK = new ColorUtil(0x0F0F0F);

    private static final Map<BlockPos, Long> RITUAL_START_TIME = new HashMap<>();

    public FelRitualType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ModBlocks.BREWING_CAULDRON.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity blockEntity, Player player, BlockPos pos, Level level) {
        if (level.getBiome(pos).is(Tags.Biomes.IS_SWAMP)) {
            return true;
        }
        RitualChecker checker = new RitualChecker(level, pos,
                state -> false, RitualRequirements.RANGE, 0);

        if (!checker.hasBlocks(state -> state.is(ModBlocks.BREWING_CAULDRON.get()), 1)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.BREWING_CAULDRON.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ModBlocks.CRYSTAL_BALL.get()), 1)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.CRYSTAL_BALL.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ModBlocks.ROTTEN_BOOKSHELF.get()), 6)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ModBlocks.ROTTEN_BOOKSHELF.get().getName()), true);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPerformRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                Player castingPlayer, ItemStack activationItem) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = darkAltarPos.getCenter();
        long gameTime = world.getGameTime();

        if (!RITUAL_START_TIME.containsKey(darkAltarPos)) {
            RITUAL_START_TIME.put(darkAltarPos, gameTime);
            serverLevel.playSound(null, center.x, center.y, center.z,
                    ModSounds.ALTAR_START.get(), SoundSource.PLAYERS, 0.9F, 0.9F);
            serverLevel.playSound(null, center.x, center.y, center.z,
                    ModSounds.HERETIC_CHANT.get(), SoundSource.PLAYERS, 0.7F, 0.8F);
            serverLevel.playSound(null, center.x, center.y, center.z,
                    ModSounds.CRONE_AMBIENT.get(), SoundSource.PLAYERS, 0.6F, 0.9F);
        }

        long elapsed = gameTime - RITUAL_START_TIME.get(darkAltarPos);

        if (elapsed > 0 && elapsed % 25 == 0) {
            serverLevel.playSound(null, center.x, center.y + 1.0, center.z,
                    ModSounds.CAULDRON_BUBBLES.get(), SoundSource.BLOCKS, 0.6F, 0.9F);
        }
        if (elapsed > 0 && elapsed % 40 == 0) {
            serverLevel.playSound(null, center.x, center.y, center.z,
                    ModSounds.BREW_GAS.get(), SoundSource.BLOCKS, 0.5F, 0.8F);
        }
        if (elapsed > 0 && elapsed % 90 == 0) {
            serverLevel.playSound(null, center.x, center.y, center.z,
                    ModSounds.POTION_DRINK.get(), SoundSource.PLAYERS, 0.5F, 0.85F);
        }

        double time = gameTime * 0.15;

        for (int i = 0; i < 6; i++) {
            double angle = time + i * (Math.PI * 2 / 6) + world.random.nextDouble() * 0.5;
            double radius = 1.5 + Math.sin(time * 0.8 + i) * 0.8 + world.random.nextDouble() * 0.5;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y + 1.0 + Math.sin(time * 0.6 + i) * 0.8;
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    x, y, z, 2, 0.15, 0.15, 0.15, 0.03);
        }

        for (int i = 0; i < 8; i++) {
            double angle = gameTime * 0.12 + i * (Math.PI * 2 / 8);
            double radius = 3.0 + Math.sin(gameTime * 0.3 + i) * 1.2;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            if (i % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        x, center.y + 1.8 + Math.sin(gameTime * 0.2 + i) * 0.5, z,
                        1, 0, 0.08, 0, 0.03);
            } else {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        x, center.y + 1.8 + Math.sin(gameTime * 0.2 + i) * 0.5, z,
                        1, 0, 0.08, 0, 0.03);
            }
        }

        for (int i = 0; i < 4; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3.0;
            serverLevel.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    center.x + offsetX,
                    center.y + 0.8 + world.random.nextDouble() * 1.0,
                    center.z + offsetZ,
                    1, 0, 0.08, 0, 0.03);
        }

        if (world.random.nextFloat() < 0.4F) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3.5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3.5;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    center.x + offsetX,
                    center.y + 0.5 + world.random.nextDouble() * 1.0,
                    center.z + offsetZ,
                    1, 0, 0.06, 0, 0.03);
        }

        if (world.random.nextFloat() < 0.35F) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.5;
            serverLevel.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    center.x + offsetX,
                    center.y + 1.5 + world.random.nextDouble() * 0.5,
                    center.z + offsetZ,
                    2, 0, -0.15, 0, 0.04);
        }

        ServerParticleUtil.windParticle(serverLevel, BRIGHT_PURPLE, 1.0F, 0.0F, 20,
                center.add(0, 0.5, 0));
        ServerParticleUtil.windParticle(serverLevel, FEL_PURPLE, 0.8F, 0.0F, 15,
                center.add(0, 0.3, 0));
        ServerParticleUtil.windParticle(serverLevel, ACID_GREEN, 0.6F, 0.0F, 10,
                center.add(0, 0.2, 0));
        ServerParticleUtil.windParticle(serverLevel, FEL_BLACK, 0.5F, 0.0F, 8,
                center.add(0, 0.1, 0));
    }

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                               Player castingPlayer, ItemStack activationItem) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = darkAltarPos.getCenter();
        RITUAL_START_TIME.remove(darkAltarPos);

        serverLevel.playSound(null, center.x, center.y, center.z,
                ModSounds.ALTAR_FINISH.get(), SoundSource.PLAYERS, 1.0F, 0.9F);
        serverLevel.playSound(null, center.x, center.y, center.z,
                ModSounds.HERETIC_CELEBRATE.get(), SoundSource.PLAYERS, 0.8F, 0.9F);
        serverLevel.playSound(null, center.x, center.y + 1.0, center.z,
                ModSounds.TOWER_WRAITH_ACID_VOCAL.get(), SoundSource.PLAYERS, 0.6F, 0.8F);
        serverLevel.playSound(null, center.x, center.y, center.z,
                ModSounds.DEAD_MOAN.get(), SoundSource.PLAYERS, 0.5F, 0.8F);

        for (int i = 0; i < 100; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = world.random.nextDouble() * 4.0;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y + 0.5 + world.random.nextDouble() * 3.5;
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    x, y, z, 2, 0.2, 0.3, 0.2, 0.05);
        }

        for (int i = 0; i < 50; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = world.random.nextDouble() * 3.5;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.4 + world.random.nextDouble() * 1.5,
                    center.z + Math.sin(angle) * radius,
                    2, 0, 0.15, 0, 0.05);
        }

        for (int i = 0; i < 60; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = world.random.nextDouble() * 3.0;
            serverLevel.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.6 + world.random.nextDouble() * 2.0,
                    center.z + Math.sin(angle) * radius,
                    2, 0, 0.25, 0, 0.06);
        }

        for (int ring = 0; ring < 5; ring++) {
            double baseRadius = 1.0 + ring * 1.0;
            int particles = (int) (baseRadius * 20);
            for (int i = 0; i < particles; i++) {
                double angle = 2 * Math.PI * i / particles + world.random.nextDouble() * 0.3;
                if (i % 2 == 0) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                            center.x + Math.cos(angle) * baseRadius,
                            center.y + 1.0 + world.random.nextDouble() * 1.0,
                            center.z + Math.sin(angle) * baseRadius,
                            1, 0, 0.08, 0, 0.04);
                } else {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            center.x + Math.cos(angle) * baseRadius,
                            center.y + 1.0 + world.random.nextDouble() * 1.0,
                            center.z + Math.sin(angle) * baseRadius,
                            1, 0, 0.08, 0, 0.04);
                }
            }
        }

        for (int i = 0; i < 120; i++) {
            double progress = (double) i / 120;
            double y = center.y + 0.2 + progress * 6.0;
            double angle = progress * Math.PI * 12 + world.random.nextDouble() * 0.8;
            double radius = 0.3 + progress * 1.5;
            if (i % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        center.x + Math.cos(angle) * radius,
                        y,
                        center.z + Math.sin(angle) * radius,
                        1, 0, 0.25, 0, 0.06);
            } else {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        center.x + Math.cos(angle) * radius,
                        y,
                        center.z + Math.sin(angle) * radius,
                        1, 0, 0.25, 0, 0.06);
            }
        }

        ServerParticleUtil.sendStretchedGodRay(serverLevel, center.x, center.y, center.z, BRIGHT_PURPLE);
        ServerParticleUtil.sendStretchedGodRay(serverLevel, center.x, center.y + 0.5, center.z, FEL_PURPLE);
        ServerParticleUtil.sendStretchedGodRay(serverLevel, center.x, center.y + 1.0, center.z, DEEP_PURPLE);
    }
}
