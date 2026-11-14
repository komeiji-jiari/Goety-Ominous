package com.example.goetyastrology.astrology;

import com.example.goetyastrology.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 月华 (Lunar Burst) — the splash a 月耀 thread leaves where it lands, as an entity rather than a puff of
 * particles, because it has to be <em>drawn</em>: a churning ball of cyan light with a white-hot core, two
 * crests of light running away from it across the ground, and the flash at the point of contact, all
 * owned by one shader (see {@code LunarBurstRenderer}).
 *
 * <p>It exists for exactly one reason: the visual outlives the thread that caused it. A thread is
 * discarded the tick it lands, so anything parented to it would vanish in the same frame as the impact,
 * and the burst is the part the player actually reads — it has to keep opening after the light that made
 * it is gone.
 *
 * <p>Nothing moves and nothing is saved: the whole object is one position, one radius and a clock. The
 * clock is {@link #tickCount}, which the client advances on its own, so the only thing that has to cross
 * the wire is the radius.
 */
public class LunarBurstEntity extends Entity {

    /**
     * How long the splash plays, in ticks. The shader's own curve is written against this.
     *
     * <p>Thirteen. It has been down from sixteen and down from twenty, and each cut was the same
     * complaint: a splash that stays on screen after the bang has finished is not a bang, it is fog. A
     * salvo lands four times a second, so a splash that outlives the gap to the next one never clears —
     * at thirteen ticks (0.65s) it is gone before the next lance of light arrives.
     */
    public static final int LIFE = 13;

    /** Radius of the ground ring at full extent — the blast's own reach, so the picture tells the truth. */
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(LunarBurstEntity.class, EntityDataSerializers.FLOAT);

    public LunarBurstEntity(EntityType<? extends LunarBurstEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    /** Opens a splash at {@code at}; called from the landing thread, server side only. */
    public static void spawn(ServerLevel level, Vec3 at, float radius) {
        LunarBurstEntity burst = new LunarBurstEntity(ModEntityTypes.LUNAR_FLARE_BURST.get(), level);
        burst.setPos(at.x, at.y, at.z);
        burst.entityData.set(DATA_RADIUS, radius);
        level.addFreshEntity(burst);
    }

    public float radius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, 2.0F);
    }

    @Override
    public void tick() {
        super.tick();
        // The client lets it age out on its own clock as well, so a splash is never left standing if the
        // removal packet is lost — and the two clocks start together, both at zero on spawn.
        if (this.tickCount > LIFE) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", this.radius());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 2.0F);
    }
}
