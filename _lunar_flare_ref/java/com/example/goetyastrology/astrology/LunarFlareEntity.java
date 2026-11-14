package com.example.goetyastrology.astrology;

import com.example.goetyastrology.AstralMoteOptions;
import com.example.goetyastrology.AstralSparkOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 月耀矢 (Lunar Flare) — one thread of moonlight coming down out of the sky onto the ground the caster
 * aimed at.
 *
 * <p>It falls rather than flies: it is spawned high above the target and travels straight down, which is
 * the whole visual of the focus — a rain of light coming out of the night instead of another bolt thrown
 * across the room. On landing it bursts over a small radius and, per the design, the burst <em>ignores
 * invulnerability frames</em>: several of these land inside a second, and if the frames were respected
 * only the first of each second would ever connect.
 *
 * <p>🔴 <b>What falls is light, not an object.</b> The thread has no sprite of its own any more: it is a
 * column of cyan motes dropped along the path it has travelled, and the fall is drawn by those motes
 * alone. The thread steps nearly two blocks a tick, so the step has to be <em>filled in</em> — one mote
 * per tick would be a string of beads, not a stream.
 *
 * <p>Each bead is two motes, not one: a bright core a third of a block across, and a wide dim sheath
 * around it that only every other bead lays down. One size of mote cannot be both a light beam's spine
 * and its glow — a core wide enough to read as a beam is a soft grey smear on its own, and a halo bright
 * enough to glow is a white bar when it is also the core. Split, each is asked to do only what it is
 * good at. The colour is the focus icon's own cyan (see {@link AstralMoteOptions#LUNAR_BEAM}).
 */
public class LunarFlareEntity extends Entity {

    /** The burst: radius, damage, and how long a thread may fall before it gives up. */
    private static final double BLAST_RADIUS = 2.0D;
    /**
     * 8, not the 12 this used to be: a salvo went from three threads to five, and five times twelve
     * would have been a straight damage buff snuck into a look change. Five eights lands for about what
     * three twelves did.
     */
    private static final float BLAST_DAMAGE = 8.0F;
    private static final int MAX_FALL_TICKS = 120;

    /**
     * Motes laid along the distance covered in one tick. The thread covers {@code FALL_SPEED} (1.9) blocks
     * per tick, so this is the number that turns the step into a continuous line: fourteen beads is one
     * every 0.14 blocks, and a core mote's bright middle is about 0.18 across, so the discs overlap
     * comfortably and the eye reads a rope rather than a string of beads.
     */
    private static final int STREAM_BEADS = 14;
    /** How far a core bead may stray from the exact path, so the column has thickness and breath. */
    private static final double CORE_WOBBLE = 0.075D;
    /** Every Nth bead also drops a sheath mote — the wide, dim halo that turns the core into a beam. */
    private static final int SHEATH_EVERY = 3;
    /** The sheath is eight times wider, so it is kept close to the true path; wobbling it too fuzzes the rope. */
    private static final double SHEATH_WOBBLE = 0.025D;
    /**
     * Loose motes shed beside the stream each tick — the beam's own falling dust.
     *
     * <p>They are what keeps the fall reading as <em>things falling</em> rather than as a drawn line:
     * a few small motes trailing outside the rope, at their own heights, drifting as they go.
     */
    private static final int STREAM_SHED = 3;
    /** How far out the shed motes are thrown, and how much of the beam's height they are spread over. */
    private static final double SHED_SPREAD = 0.42D;
    private static final double SHED_TRAIL = 1.3D;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;
    /**
     * How far this thread sits along the focus's colour drift, 0..1. Synced because the beam is drawn
     * client-side only: the server never lays a mote down, so this is the one thing the client cannot
     * work out for itself.
     */
    private static final EntityDataAccessor<Float> DATA_TINT =
            SynchedEntityData.defineId(LunarFlareEntity.class, EntityDataSerializers.FLOAT);
    /** The two options actually used to draw this thread, rebuilt only when the tint arrives. */
    @Nullable
    private AstralMoteOptions coreOptions;
    @Nullable
    private AstralMoteOptions sheathOptions;
    @Nullable
    private AstralMoteOptions shedOptions;
    private float cachedTint = -1.0F;

    public LunarFlareEntity(EntityType<? extends LunarFlareEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.ownerUUID = owner == null ? null : owner.getUUID();
        this.cachedOwner = owner;
    }

    /** Places this thread along the focus's colour drift; see {@link AstralMoteOptions#tinted}. */
    public void setTint(float tint) {
        this.entityData.set(DATA_TINT, Mth.clamp(tint, 0.0F, 1.0F));
    }

    public float tint() {
        return this.entityData.get(DATA_TINT);
    }

    @Override
    protected void defineSynchedData() {
        // The position and heading need no syncing — they ride the ordinary entity position packets. The
        // owner is read server-side only, to decide who the burst spares. The tint does have to travel:
        // the beam is drawn entirely on the client, so a thread that arrived without its shade would be
        // drawn at whatever the default happened to be, in the middle of four siblings that are not.
        this.entityData.define(DATA_TINT, 0.0F);
    }

    @Nullable
    private LivingEntity owner() {
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }
        if (this.ownerUUID == null || !(this.level() instanceof ServerLevel level)) {
            return null;
        }
        Entity entity = level.getEntity(this.ownerUUID);
        if (entity instanceof LivingEntity living) {
            this.cachedOwner = living;
            return living;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.clientTrail();
            return;
        }
        if (this.tickCount > MAX_FALL_TICKS) {
            this.discard();
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        Vec3 next = this.position().add(motion);
        // A spear stops at the first thing in its way, whatever that is — ground, wall, or a target it
        // clipped on the way down.
        BlockPos at = BlockPos.containing(next);
        boolean hitBlock = this.level().getBlockState(at).blocksMotion();
        boolean hitFloor = this.onGround();
        if (hitBlock || hitFloor) {
            this.burst(this.position());
            return;
        }
        this.setPos(next.x, next.y, next.z);
        // 🔴 No particle send here. Every client already draws the trail for every thread it can see
        // (clientTrail runs off the entity's own tick), so broadcasting motes from the server would lay
        // each bead down twice — and with a beam this wide, twice is the difference between a rope of
        // light and a white bar.
    }

    /**
     * One tick of the thread: the beads that make it visible.
     *
     * <p>The thread steps almost two blocks a tick, so a single bead at the head would leave the fall as
     * a row of separated dots. The stretch covered since the last tick is therefore filled in evenly,
     * and {@link #STREAM_BEADS} is the number that keeps the beads touching. Everything behind the head
     * is last tick's beads, still alive and fading, which is what gives the stream its tapering tail —
     * the oldest are the faintest, so the column is dense at the front and thins into single specks above.
     */
    private void clientTrail() {
        double dx = this.getX() - this.xo;
        double dy = this.getY() - this.yo;
        double dz = this.getZ() - this.zo;
        double step = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // A thread that has just been spawned, or one that was moved by something other than its own
        // fall, has no meaningful step to fill: lay a couple of beads at the head and leave it there
        // rather than drawing a line across the whole sky.
        if (step > 4.0D || step < 1.0E-4D) {
            this.bead(this.getX(), this.getY(), this.getZ(), true);
            this.bead(this.getX(), this.getY(), this.getZ(), false);
            return;
        }
        for (int i = 0; i < STREAM_BEADS; i++) {
            double along = i / (double) STREAM_BEADS;
            this.bead(this.xo + dx * along, this.yo + dy * along, this.zo + dz * along,
                    i % SHEATH_EVERY == 0);
        }
        // The head gets one more core bead than the body: it is where the light is arriving, and a rope
        // whose leading end is the same width as its middle reads as a cut-off rather than a strike.
        this.bead(this.getX(), this.getY(), this.getZ(), true);
        this.shed(dx, dy, dz);
    }

    /**
     * The beam's own falling dust: a few small motes thrown out beside the rope, spread up its last
     * stretch rather than all at the head.
     *
     * <p>They matter more than their number suggests. Without them the thread is a shape travelling
     * down the screen; with them it is a column of light with loose grit coming off it, which is what a
     * fall of anything actually looks like. They are spread over {@link #SHED_TRAIL} of height so they
     * do not arrive as a single ring of dots riding the head.
     *
     * <p>The direction is taken from the step the thread actually took this tick, not from
     * {@code getDeltaMovement()}: an entity's motion is only broadcast past a size threshold, so on the
     * client it is not reliably there at all, and a fall of dust that assumes it is would stack itself
     * into one point.
     */
    private void shed(double dx, double dy, double dz) {
        this.options();
        for (int i = 0; i < STREAM_SHED; i++) {
            double back = this.random.nextDouble() * SHED_TRAIL;
            this.level().addParticle(this.shedOptions,
                    this.getX() - dx * back + this.stray(SHED_SPREAD),
                    this.getY() - dy * back + this.stray(SHED_SPREAD),
                    this.getZ() - dz * back + this.stray(SHED_SPREAD),
                    0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * One bead of the beam: a bright core, and — where the caller asks for it — the wide dim sheath that
     * gives the core something to sit inside.
     */
    private void bead(double x, double y, double z, boolean withSheath) {
        this.options();
        this.level().addParticle(this.coreOptions,
                x + this.stray(CORE_WOBBLE), y + this.stray(CORE_WOBBLE), z + this.stray(CORE_WOBBLE),
                0.0D, 0.0D, 0.0D);
        if (!withSheath) {
            return;
        }
        this.level().addParticle(this.sheathOptions,
                x + this.stray(SHEATH_WOBBLE), y + this.stray(SHEATH_WOBBLE), z + this.stray(SHEATH_WOBBLE),
                0.0D, 0.0D, 0.0D);
    }

    /** Rebuilds the three shades this thread draws with, but only when its tint actually changes. */
    private void options() {
        float tint = this.tint();
        if (this.coreOptions != null && tint == this.cachedTint) {
            return;
        }
        this.cachedTint = tint;
        this.coreOptions = AstralMoteOptions.LUNAR_BEAM.tinted(tint);
        this.sheathOptions = AstralMoteOptions.LUNAR_SHEATH.tinted(tint);
        this.shedOptions = AstralMoteOptions.LUNAR_FLARE.tinted(tint);
    }

    private double stray(double spread) {
        return (this.random.nextDouble() - 0.5D) * spread;
    }

    /** The landing: a two-block burst that does not care about hurt cooldowns. */
    private void burst(Vec3 at) {
        if (!(this.level() instanceof ServerLevel level)) {
            this.discard();
            return;
        }
        LivingEntity owner = this.owner();
        AABB area = AABB.ofSize(at, BLAST_RADIUS * 2.0D, BLAST_RADIUS * 2.0D, BLAST_RADIUS * 2.0D);
        for (LivingEntity victim : PartHitSupport.collect(level, area,
                other -> owner == null ? other.isAlive() : AstrologyTargets.isFoe(owner, other))) {
            if (victim.position().distanceToSqr(at) > BLAST_RADIUS * BLAST_RADIUS) {
                continue;
            }
            // 无视无敌帧: the frames are cleared rather than bypassed with another damage source, so the
            // victim still gets its red flash and its knockback for every thread that lands.
            victim.invulnerableTime = 0;
            victim.hurt(level.damageSources().indirectMagic(this, owner), BLAST_DAMAGE);
        }
        // The burst itself is not drawn here: a shader-owned splash is spawned as its own entity — the
        // ring, the ball of light and the flash all live there. These are only the things that get
        // thrown, and an explosion is as much debris as it is light.
        LunarBurstEntity.spawn(level, at, (float) BLAST_RADIUS);
        // Ejecta: sparks flung out and up, which arc down under their own gravity and burn out. Without
        // them a splash is a clean drawing of a bang rather than a bang.
        level.sendParticles(AstralSparkOptions.LUNAR, at.x, at.y + 0.35D, at.z, 30,
                BLAST_RADIUS * 0.22D, 0.16D, BLAST_RADIUS * 0.22D, 0.36D);
        // The cyan the ring is made of, kicked up and outward from the point of contact.
        level.sendParticles(AstralMoteOptions.LUNAR_FLARE, at.x, at.y + 0.15D, at.z, 20,
                BLAST_RADIUS * 0.45D, 0.16D, BLAST_RADIUS * 0.45D, 0.09D);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS, 0.85F, 1.6F);
        this.discard();
    }

    /**
     * The point a spear should be aimed at, so a target behind a roof is still hit on the roof rather
     * than falling through it: the first solid block under the aimed spot, walked down from the sky.
     */
    public static Vec3 groundUnder(Level level, Vec3 aimed) {
        Vec3 from = aimed.add(0.0D, 32.0D, 0.0D);
        HitResult hit = level.clip(new ClipContext(from, aimed, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, null));
        return hit.getType() == HitResult.Type.MISS ? aimed : hit.getLocation();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }
}
