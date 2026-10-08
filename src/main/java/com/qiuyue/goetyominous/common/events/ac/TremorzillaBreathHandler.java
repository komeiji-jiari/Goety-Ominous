package com.qiuyue.goetyominous.common.events.ac;

import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.ac.TremorzillaBreathPacket;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TremorzillaBreathHandler {

    private static final int BEAM_HIT_INTERVAL = 3;
    private static final int BEAM_SYNC_INTERVAL = 2;
    private static final int BEAM_PARTICLE_INTERVAL = 2;
    private static final int BEAM_PROGRESS_TICKS = 5;
    private static final float BEAM_STEP = 5.0F;
    private static final float BEAM_HURT_RADIUS = 6.0F;
    private static final float BEAM_DROP_CHANCE = 0.08F;
    private static final float BREAK_LENGTH_START = 100.0F;
    private static final int IRRADIATED_DURATION = 6000;
    private static final int IRRADIATED_AMPLIFIER = 2;
    private static final double SYNC_RANGE = 128.0D;
    private static final double PARTICLE_SPACING = 2.0D;
    private static final long CLIENT_BEAM_TIMEOUT = 600L;
    private static final double BEAM_ORIGIN_HEIGHT = 5.0D;


    private static final List<Beam> BEAMS = new ArrayList<>();
    private static final Map<Integer, ClientBeam> CLIENT_BEAMS = new HashMap<>();

    private static final class Beam {
        private final ServerLevel level;
        private final UUID casterId;
        private final float damage;
        private final double range;
        private final int maxBeamTime;
        private int beamTime;
        private float breakLength = BREAK_LENGTH_START;

        private Beam(ServerLevel level, UUID casterId, float damage, double range, int maxBeamTime) {
            this.level = level;
            this.casterId = casterId;
            this.damage = damage;
            this.range = range;
            this.maxBeamTime = maxBeamTime;
        }
    }

    public static Vec3 beamOrigin(LivingEntity caster, float partialTick) {
        if (!(caster instanceof Player)) {
            return caster.getPosition(partialTick).add(0.0D, (double) caster.getEyeHeight() - 0.2D, 0.0D);
        }
        return caster.getPosition(partialTick).add(0.0D, (double) caster.getBbHeight() + BEAM_ORIGIN_HEIGHT, 0.0D);
    }

    public record ClientBeam(int casterId, Vec3 prevTo, Vec3 to, float progress, long time) {

        public Vec3 lerpTo(float partialTick) {
            return this.prevTo.add(this.to.subtract(this.prevTo).scale((double) partialTick));
        }
    }

    public static void startBeam(ServerLevel level, LivingEntity caster, float damage, double range, int maxBeamTime) {
        BEAMS.add(new Beam(level, caster.getUUID(), damage, range, maxBeamTime));
        level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                ACSoundRegistry.TREMORZILLA_BEAM_START.get(), caster.getSoundSource(), 8.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BEAMS.isEmpty()) {
            return;
        }
        Iterator<Beam> iterator = BEAMS.iterator();
        while (iterator.hasNext()) {
            if (!tickBeam(iterator.next())) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        BEAMS.clear();
        synchronized (CLIENT_BEAMS) {
            CLIENT_BEAMS.clear();
        }
    }

    private static boolean tickBeam(Beam beam) {
        Entity entity = beam.level.getEntity(beam.casterId);
        if (!(entity instanceof LivingEntity caster) || !caster.isAlive()) {
            return false;
        }

        Vec3 eye = new Vec3(caster.getX(), caster.getEyeY() - 0.2D, caster.getZ());
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 to = eye.add(look.scale(beam.range));
        Vec3 from = beamOrigin(caster, 1.0F);
        Vec3 end = beam.level.clip(new ClipContext(eye, to,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster)).getLocation();

        if (beam.beamTime % BEAM_HIT_INTERVAL == 0) {
            settleAlongBeam(beam, caster, from, end);
        }
        if (beam.beamTime % BEAM_PARTICLE_INTERVAL == 0) {
            spawnBeamParticles(beam.level, from, end);
        }
        if (beam.beamTime % BEAM_SYNC_INTERVAL == 0) {
            syncBeam(beam, caster, end, (float) Math.min(beam.beamTime, BEAM_PROGRESS_TICKS));
        }

        beam.beamTime++;
        if (beam.beamTime > beam.maxBeamTime) {
            beam.level.playSound(null, from.x, from.y, from.z,
                    ACSoundRegistry.TREMORZILLA_BEAM_END.get(), caster.getSoundSource(), 8.0F, 1.0F);
            syncBeam(beam, caster, end, 0.0F);
            return false;
        }
        return true;
    }

    private static void settleAlongBeam(Beam beam, LivingEntity caster, Vec3 from, Vec3 end) {
        Vec3 viewVec = end.subtract(from).normalize();
        Vec3 cursor = from;
        boolean broken = false;
        float furthest = 10.0F;
        float walked = 0.0F;

        while ((double) walked < beam.breakLength) {
            cursor = cursor.add(viewVec.scale(BEAM_STEP * 1.5F));
            if (!broken) {
                broken = breakBlocksAround(beam, caster, cursor, BEAM_STEP);
                furthest = (float) cursor.distanceTo(from);
            }
            hurtAround(beam, caster, cursor, BEAM_STEP + 1.0F, false);
            walked += BEAM_STEP;
        }

        hurtAround(beam, caster, end, BEAM_HURT_RADIUS, false);

        if (broken) {
            beam.breakLength = Math.max(furthest, beam.breakLength - 5.0F);
        }
    }

    private static boolean breakBlocksAround(Beam beam, LivingEntity caster, Vec3 center, float radius) {
        if (!SpellConfig.TremorzillaBreathBreakBlocks.get() || !caster.isShiftKeyDown()) {
            return false;
        }
        if (!(caster instanceof Player) && !ForgeEventFactory.getMobGriefingEvent(beam.level, caster)) {
            return false;
        }
        ServerLevel level = beam.level;
        boolean flag = false;
        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(center.x - (double) radius), Mth.floor(center.y - (double) radius), Mth.floor(center.z - (double) radius),
                Mth.floor(center.x + (double) radius), Mth.floor(center.y + (double) radius), Mth.floor(center.z + (double) radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ACTagRegistry.NUKE_PROOF) || !state.blocksMotion()
                    || state.getBlock().getExplosionResistance() > 15.0F) {
                continue;
            }
            if (!(pos.distToCenterSqr(center.x, center.y, center.z) < (double) (radius * radius))) {
                continue;
            }
            level.destroyBlock(pos, level.random.nextFloat() <= BEAM_DROP_CHANCE);
            flag = true;
        }
        return flag;
    }

    private static void hurtAround(Beam beam, LivingEntity caster, Vec3 center, float radius, boolean stretchY) {
        AABB aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        if (stretchY) {
            aabb.setMinY(caster.getY() - 1.0D);
            aabb.setMaxY(caster.getEyeY() + 3.0D);
        }
        var source = ACDamageTypes.causeTremorzillaBeamDamage(beam.level.registryAccess(), caster);
        for (LivingEntity living : beam.level.getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            boolean sameKind = !(caster instanceof Player) && living.getType() == caster.getType();
            if (living.is(caster) || MobUtil.areAllies(caster, living) || sameKind) {
                continue;
            }
            double y = stretchY ? living.getY() : center.y;
            if (!(living.distanceToSqr(center.x, y, center.z) <= (double) (radius * radius))) {
                continue;
            }
            if (!canBeamReach(beam.level, caster, center, living)) {
                continue;
            }
            if (!living.hurt(source, beam.damage)) {
                continue;
            }
            knockbackTarget(caster, living, 1.0D, caster.getX() - living.getX(), caster.getZ() - living.getZ(),
                    !(living instanceof KaijuMob));
            living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), IRRADIATED_DURATION, IRRADIATED_AMPLIFIER));
        }
    }

    private static void knockbackTarget(LivingEntity caster, Entity target, double strength, double x, double z, boolean ignoreResistance) {
        LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(caster, (float) strength, x, z);
        if (event.isCanceled()) {
            return;
        }
        strength = event.getStrength();
        x = event.getRatioX();
        z = event.getRatioZ();
        if (!ignoreResistance) {
            strength *= 1.0D - caster.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        }
        if (!(strength <= 0.0D)) {
            caster.hasImpulse = true;
            Vec3 vec3 = caster.getDeltaMovement();
            Vec3 vec31 = new Vec3(x, 0.0D, z).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0D - vec31.x,
                    caster.onGround() ? Math.min(0.4D, vec3.y / 2.0D + strength) : vec3.y,
                    vec3.z / 2.0D - vec31.z);
        }
    }

    private static boolean canBeamReach(ServerLevel level, LivingEntity caster, Vec3 center, LivingEntity living) {
        return level.clip(new ClipContext(center, living.position().add(0.0D, living.getBbHeight() * 0.5D, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster)).getType() == HitResult.Type.MISS;
    }

    private static void spawnBeamParticles(ServerLevel level, Vec3 from, Vec3 end) {
        Vec3 vec = end.subtract(from);
        double length = vec.length();
        if (length < 0.5D) {
            return;
        }
        Vec3 step = vec.normalize();
        for (double d = 0.0D; d < length; d += PARTICLE_SPACING) {
            Vec3 pos = from.add(step.scale(d));
            level.sendParticles(ACParticleRegistry.TREMORZILLA_EXPLOSION.get(),
                    pos.x, pos.y, pos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void syncBeam(Beam beam, LivingEntity caster, Vec3 end, float progress) {
        ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        caster.getX(), caster.getY(), caster.getZ(), SYNC_RANGE, beam.level.dimension())),
                new TremorzillaBreathPacket(caster.getId(), end.x, end.y, end.z, progress));
    }

    public static void acceptClientBeam(int casterId, double ex, double ey, double ez, float progress) {
        synchronized (CLIENT_BEAMS) {
            if (progress <= 0.0F) {
                CLIENT_BEAMS.remove(casterId);
                return;
            }
            Vec3 to = new Vec3(ex, ey, ez);
            ClientBeam prev = CLIENT_BEAMS.get(casterId);
            CLIENT_BEAMS.put(casterId, new ClientBeam(casterId,
                    prev == null ? to : prev.to(), to, progress, System.currentTimeMillis()));
        }
    }

    public static List<ClientBeam> clientBeams() {
        long now = System.currentTimeMillis();
        List<ClientBeam> list = new ArrayList<>();
        synchronized (CLIENT_BEAMS) {
            CLIENT_BEAMS.values().removeIf(beam -> now - beam.time() > CLIENT_BEAM_TIMEOUT);
            list.addAll(CLIENT_BEAMS.values());
        }
        return list;
    }

    public static boolean hasClientBeam(int casterId) {
        synchronized (CLIENT_BEAMS) {
            return CLIENT_BEAMS.containsKey(casterId);
        }
    }
}
