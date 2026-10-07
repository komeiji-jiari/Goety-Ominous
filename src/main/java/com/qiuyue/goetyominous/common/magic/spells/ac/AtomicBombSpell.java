package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.SirenLightBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.events.ac.NucleeperNukeKillHandler;
import com.qiuyue.goetyominous.common.events.ac.NucleeperNukeProtectionHandler;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AtomicBombSpell extends Spell {

    private static final int RANGE_PER_LEVEL = 5;
    private static final int SIREN_RADIUS = 256;
    private static final int SIREN_INTERVAL = 10;
    private static final int LIGHT_SCAN = 2;
    private static final float NUCLEEPER_SIZE = 1.0F;
    private static final float BOMB_POTENCY_LEVEL = 2.0F;
    private static final float MAX_SIZE = 4.0F;

    private record Tracked(ServerLevel level, Set<BlockPos> sirens, Set<BlockPos> lights) {
    }

    private static final Map<UUID, Tracked> TRACKED = new HashMap<>();

    @Nullable
    private static Field nearestBombField;
    private static boolean nearestBombFieldChecked;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.AtomicBombCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.AtomicBombCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.AtomicBombCoolDown.get();
    }

    @Override
    public @Nullable SoundEvent CastingSound() {
        return ACSoundRegistry.NUCLEAR_BOMB_PLACE.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(SpellConfig.AtomicBombRange.get());
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        clearSirens(worldIn, caster);
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        if (castTime % SIREN_INTERVAL != 0) {
            return;
        }
        triggerSirens(worldIn, caster, aimPoint(worldIn, caster, aimRange(caster, spellStat)));
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        clearSirens(worldIn, caster);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = aimRange(caster, spellStat);
        Vec3 center = aimPoint(worldIn, caster, range);

        clearSirens(worldIn, caster);

        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }
        float size = nukeSize(potency);

        Set<UUID> ownerIds = new HashSet<>();
        UUID ownerId = ownerIdOf(caster);
        if (ownerId != null) {
            ownerIds.add(ownerId);
        }

        NucleeperNukeProtectionHandler.registerProtection(worldIn, center, size, ownerIds);
        NucleeperNukeProtectionHandler.syncZoneToClients(worldIn, center, size, ownerIds);

        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(worldIn);
        if (explosion != null) {
            explosion.moveTo(center.x, center.y, center.z);
            explosion.setSize(size);
            explosion.setNoGriefing(!caster.isShiftKeyDown());
            NucleeperNukeProtectionHandler.suppressVanillaCloud(explosion);
            worldIn.addFreshEntity(explosion);
        }

        spawnSurfaceCloud(worldIn, center, size);

        if (ownerId != null) {
            NucleeperNukeKillHandler.register(worldIn, ownerId, center, size);
        }
    }

    private static float nukeSize(int potency) {
        float bombSize = AlexsCaves.COMMON_CONFIG.nukeExplosionSizeModifier.get().floatValue();
        if (potency <= 0) {
            return NUCLEEPER_SIZE;
        }
        float size = NUCLEEPER_SIZE + (bombSize - NUCLEEPER_SIZE) * (float) potency / BOMB_POTENCY_LEVEL;
        return Math.min(size, MAX_SIZE);
    }

    private static int aimRange(LivingEntity caster, SpellStat spellStat) {
        return spellStat.getRange() + WandUtil.getLevels(ModEnchantments.RANGE.get(), caster) * RANGE_PER_LEVEL;
    }

    private Vec3 aimPoint(ServerLevel level, LivingEntity caster, int range) {
        Vec3 center = this.rayTrace(level, caster, range, 3.0D).getLocation();
        LivingEntity target = this.getTarget(caster, range);
        return target != null ? target.position() : center;
    }

    private static void triggerSirens(ServerLevel level, LivingEntity caster, Vec3 center) {
        UUID id = caster.getUUID();

        Tracked tracked = TRACKED.get(id);
        if (tracked != null && tracked.level() != level) {
            clearSirens(id);
            tracked = null;
        }
        if (tracked == null) {
            tracked = new Tracked(level, new HashSet<>(), new HashSet<>());
            TRACKED.put(id, tracked);
        }
        final Tracked active = tracked;

        PoiManager poiManager = level.getPoiManager();
        poiManager.findAll(holder -> holder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()), pos -> true,
                BlockPos.containing(center.x, center.y, center.z), SIREN_RADIUS, PoiManager.Occupancy.ANY).forEach(pos -> {
            if (!(level.getBlockEntity(pos) instanceof NuclearSirenBlockEntity siren)) {
                return;
            }
            siren.setNearestNuclearBomb(caster);
            active.sirens().add(pos.immutable());
            for (BlockPos lightPos : BlockPos.betweenClosed(
                    pos.offset(-LIGHT_SCAN, -LIGHT_SCAN, -LIGHT_SCAN),
                    pos.offset(LIGHT_SCAN, LIGHT_SCAN, LIGHT_SCAN))) {
                BlockState state = level.getBlockState(lightPos);
                if (state.is(ACBlockRegistry.SIREN_LIGHT.get()) && !state.getValue(SirenLightBlock.POWERED)) {
                    level.setBlock(lightPos, state.setValue(SirenLightBlock.POWERED, true), 3);
                    active.lights().add(lightPos.immutable());
                }
            }
        });
    }

    private static void clearSirens(ServerLevel level, LivingEntity caster) {
        clearSirens(caster.getUUID());
    }

    public static void clearCaster(UUID id) {
        clearSirens(id);
    }

    public static void clearAll() {
        for (UUID id : new ArrayList<>(TRACKED.keySet())) {
            clearSirens(id);
        }
    }

    private static void clearSirens(UUID id) {
        Tracked tracked = TRACKED.remove(id);
        if (tracked == null) {
            return;
        }
        ServerLevel level = tracked.level();
        for (BlockPos pos : tracked.sirens()) {
            if (level.getBlockEntity(pos) instanceof NuclearSirenBlockEntity siren) {
                stopSirenTracking(siren);
            }
        }
        for (BlockPos pos : tracked.lights()) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ACBlockRegistry.SIREN_LIGHT.get()) && state.getValue(SirenLightBlock.POWERED)) {
                level.setBlock(pos, state.setValue(SirenLightBlock.POWERED, false), 3);
            }
        }
    }

    private static void stopSirenTracking(NuclearSirenBlockEntity siren) {
        if (!nearestBombFieldChecked) {
            nearestBombFieldChecked = true;
            try {
                nearestBombField = NuclearSirenBlockEntity.class.getDeclaredField("nearestNuclearBomb");
                nearestBombField.setAccessible(true);
            } catch (Throwable t) {
                nearestBombField = null;
                GoetyOminous.LOGGER.error("[AtomicBomb] Can't Reflect NuclearSirenBlockEntity.nearestNuclearBomb", t);
            }
        }
        if (nearestBombField == null) {
            return;
        }
        try {
            nearestBombField.set(siren, null);
        } catch (Throwable ignored) {
        }
    }

    private static void spawnSurfaceCloud(ServerLevel level, Vec3 center, float size) {
        double y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(center.x), Mth.floor(center.z));
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, (SimpleParticleType) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(), true,
                    center.x, y, center.z, 0, 1.0D, 0.0D, 0.0D, size);
        }
    }

    private static UUID ownerIdOf(LivingEntity caster) {
        if (caster instanceof Player player) {
            return player.getUUID();
        }
        if (caster instanceof IOwned owned) {
            if (owned.getOwnerId() != null) {
                return owned.getOwnerId();
            }
            if (owned.getMasterOwner() != null) {
                return owned.getMasterOwner().getUUID();
            }
        }
        return null;
    }
}
