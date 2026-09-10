package com.qiuyue.goetyominous.common.ritual;

import com.Polarice3.Goety.api.ritual.IRitualType;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.common.ritual.RitualChecker;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.util.PureDarkVoid;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public class PureDarkRitualType implements IRitualType {
    private final String name;

    private static final ColorUtil VOID_BLACK = new ColorUtil(0x0A0A0F);
    private static final ColorUtil BLOOD_RED = new ColorUtil(0xB22222);

    private static final ResourceLocation FORLORN_HOLLOWS = new ResourceLocation("alexscaves", "forlorn_hollows");

    private static final long RITUAL_RESYNC_GAP = 40L;

    // 仪式总长 = duration(20s)=400 tick。虚空特效寿命 300 tick,故延后到第 100 tick 才出现,
    // 让其"收拢/吞没"高潮正好撞上产物出炉(onFinish 于 ~400 tick)。
    private static final int VOID_SPAWN_TICK = 100;
    private static final long CRAFT_TOTAL_TICKS = 400L;

    private static final Map<DarkAltarBlockEntity, Long> RITUAL_START_TIME = new WeakHashMap<>();
    private static final Map<DarkAltarBlockEntity, Long> RITUAL_LAST_TICK = new WeakHashMap<>();
    private static final Map<DarkAltarBlockEntity, PureDarkVoid> RITUAL_VOID = new WeakHashMap<>();

    public PureDarkRitualType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack getJeiIcon() {
        return new ItemStack(ACBlockRegistry.PEERING_COPROLITH.get());
    }

    @Override
    public boolean getRequirement(RitualBlockEntity blockEntity, Player player, BlockPos pos, Level level) {
        if (level.getBiome(pos).unwrapKey()
                .filter(key -> key.location().equals(FORLORN_HOLLOWS)).isPresent()) {
            return true;
        }
        RitualChecker checker = new RitualChecker(level, pos,
                state -> false, RitualRequirements.RANGE, 0);

        if (!checker.hasBlocks(state -> state.is(ACBlockRegistry.FORSAKEN_IDOL.get()), 1)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ACBlockRegistry.FORSAKEN_IDOL.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ACBlockRegistry.PEERING_COPROLITH.get()), 8)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ACBlockRegistry.PEERING_COPROLITH.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ACBlockRegistry.THORNWOOD_WOOD.get()), 8)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ACBlockRegistry.THORNWOOD_WOOD.get().getName()), true);
            }
            return false;
        }
        if (!checker.hasBlocks(state -> state.is(ACBlockRegistry.GUANOSTONE.get()), 16)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "info.goety.ritual.structure.noBlocks", ACBlockRegistry.GUANOSTONE.get().getName()), true);
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
        long now = world.getGameTime();
        Long lastTick = RITUAL_LAST_TICK.get(tileEntity);
        boolean fresh = lastTick == null || now - lastTick > RITUAL_RESYNC_GAP;
        RITUAL_LAST_TICK.put(tileEntity, now);
        if (fresh) {
            RITUAL_START_TIME.put(tileEntity, now);
        }
        long elapsed = now - RITUAL_START_TIME.get(tileEntity);

        if (fresh) {
            serverLevel.playSound(null, center.x, center.y + 0.5, center.z,
                    ACSoundRegistry.UNDERZEALOT_CHANT.get(), SoundSource.PLAYERS, 1.4F, 0.85F);
            PureDarkVoid staleVoid = RITUAL_VOID.remove(tileEntity);
            if (staleVoid != null) {
                staleVoid.discard();
            }
        }

        // 先吟唱蓄能(前 100 tick,烟尘/风/诵经在祭坛汇聚),再放虚空特效
        PureDarkVoid activeVoid = RITUAL_VOID.get(tileEntity);
        if (activeVoid == null && elapsed >= VOID_SPAWN_TICK && elapsed < CRAFT_TOTAL_TICKS) {
            Vec3 cloudAt = center.add(0, 3.0, 0);
            PureDarkVoid voidEntity = new PureDarkVoid(AcEntityRegistry.PURE_DARK_VOID.get(), serverLevel);
            voidEntity.setPos(cloudAt.x, cloudAt.y, cloudAt.z);
            voidEntity.setSpawnTime(serverLevel.getGameTime());
            voidEntity.setAltarAnchor(darkAltarPos.immutable());
            serverLevel.addFreshEntity(voidEntity);
            RITUAL_VOID.put(tileEntity, voidEntity);
            serverLevel.playSound(null, cloudAt.x, cloudAt.y, cloudAt.z,
                    ACSoundRegistry.DARK_CLOUD_APPEAR.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
        }

        if (activeVoid == null && elapsed % 20 == 0) {
            for (int i = 0; i < 4; i++) {
                double angle = world.random.nextDouble() * Math.PI * 2;
                double radius = 0.9 + world.random.nextDouble() * 0.5;
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        center.x + Math.cos(angle) * radius, center.y + 0.4,
                        center.z + Math.sin(angle) * radius,
                        1, 0, 0.1, 0, 0.01);
            }
        }

        if (elapsed > 0 && elapsed % 80 == 0) {
            serverLevel.playSound(null, center.x, center.y + 0.5, center.z,
                    ACSoundRegistry.UNDERZEALOT_CHANT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (activeVoid == null && elapsed > 0 && elapsed % 40 == 0) {
            ServerParticleUtil.windParticle(serverLevel, VOID_BLACK, 1.0F, 0.0F, 16,
                    center.add(0, 0.4, 0));
            ServerParticleUtil.windParticle(serverLevel, VOID_BLACK, 0.6F, 0.0F, 10,
                    center.add(0, 0.2, 0));
        }
    }

    @Override
    public void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                               Player castingPlayer, ItemStack activationItem) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        PureDarkVoid voidEntity = RITUAL_VOID.remove(tileEntity);
        if (voidEntity != null) {
            voidEntity.discard();
        }
        RITUAL_START_TIME.remove(tileEntity);
        RITUAL_LAST_TICK.remove(tileEntity);
        Vec3 center = darkAltarPos.getCenter();

        serverLevel.playSound(null, center.x, center.y, center.z,
                ACSoundRegistry.UNDERZEALOT_TRANSFORMATION.get(), SoundSource.PLAYERS, 1.6F, 1.0F);

        for (int i = 0; i < 40; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = world.random.nextDouble() * 3.0;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.4 + world.random.nextDouble() * 2.0,
                    center.z + Math.sin(angle) * radius,
                    2, 0, 0.15, 0, 0.03);
        }

        ServerParticleUtil.sendStretchedGodRay(serverLevel, center.x, center.y, center.z, BLOOD_RED);
    }
}
