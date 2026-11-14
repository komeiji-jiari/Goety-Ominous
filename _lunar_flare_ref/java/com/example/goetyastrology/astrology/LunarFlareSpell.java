package com.example.goetyastrology.astrology;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.example.goetyastrology.AstralMoteOptions;
import com.example.goetyastrology.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 月耀聚晶 (Lunar Flare Focus) — calls down the moon's fire as rain: five threads of cyan moonlight every
 * quarter second onto whatever the caster is pointing at.
 *
 * <p>The threads are dropped from above the aim point rather than fired at it, so this reads as weather
 * and not as a barrage. Each burst is small — two blocks — but there are four salvos a second, which is
 * why the design has it ignore invulnerability frames: without that, only the first thread of each
 * second would ever connect.
 *
 * <p>Each thread is drawn as a stream of the focus icon's own cyan rather than as a sprite (see
 * {@link LunarFlareEntity}), and where it lands it leaves a 月华 splash owned by a shader.
 *
 * <p>Costed like the school's other channelled focus: a pulse every five ticks, each pulse billed for a
 * quarter of the "95 souls a second" figure, and the cooldown only starts once the wand is released.
 */
public class LunarFlareSpell extends AstrologyEverChargeSpell {

    /** Wind-up before the rain starts. */
    private static final int CAST_UP = 6;
    /** A quarter of a second between salvos. */
    private static final int PULSE_INTERVAL = 5;
    /** Longest a single hold can keep raining, at a salvo every quarter second. */
    private static final int SHOTS = 2400;
    /** Souls per pulse: a quarter of the design's ninety-five a second. */
    private static final int SOUL_COST = 24;
    /**
     * Threads per salvo.
     *
     * <p>Five rather than the three it used to be, and the per-thread damage came down to match
     * ({@code LunarFlareEntity.BLAST_DAMAGE} 8 instead of 12): a salvo still lands for about what it did
     * (40 against 36), because this is a change of <em>look</em> — a rain of thin cyan streams instead of
     * three solid spears — and not a reason for the focus to hit half again as hard.
     */
    private static final int STREAMS = 5;
    /** How far ahead of the caster the strike zone sits. */
    private static final double AIM_RANGE = 40.0D;
    /**
     * How high a thread starts — a range, not a number.
     *
     * <p>Dropped from one height, a salvo of five reads as a single event: five threads land on the same
     * tick and the "rain" is one hit drawn five times. Spread over twenty blocks they arrive across half
     * a second, which is the difference between a rain and a strike.
     */
    private static final double DROP_HEIGHT_MIN = 13.0D;
    private static final double DROP_HEIGHT_MAX = 33.0D;
    /** How wide the salvo scatters. */
    private static final double SCATTER = 3.2D;
    /**
     * Most a thread leans off vertical, in degrees, in a random direction around the compass.
     *
     * <p>Five perfectly parallel spears read as a barcode. At two degrees over a twenty-block fall the
     * lean is under a block of drift — it does not change where anything lands, it only stops the salvo
     * from looking ruled.
     */
    private static final double TILT_DEGREES = 2.0D;
    /** Fall speed of a thread. */
    private static final double FALL_SPEED = 1.9D;

    @Override
    public int defaultSoulCost() {
        return SOUL_COST;
    }

    @Override
    public int defaultCastUp() {
        return CAST_UP;
    }

    @Override
    public int Cooldown() {
        return PULSE_INTERVAL;
    }

    @Override
    public int shotsNumber() {
        return SHOTS;
    }

    @Override
    public int defaultSpellCooldown() {
        return 200;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(ModEnchantments.RANGE.get(), ModEnchantments.RADIUS.get());
    }

    @Override
    public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack stack, SpellStat stat) {
        Vec3 aim = aimPoint(level, caster, AIM_RANGE + stat.getRange() * 4.0D);
        Vec3 ground = LunarFlareEntity.groundUnder(level, aim);
        for (int i = 0; i < STREAMS; i++) {
            Vec3 target = ground.add((level.random.nextDouble() - 0.5D) * SCATTER * 2.0D,
                    0.0D, (level.random.nextDouble() - 0.5D) * SCATTER * 2.0D);
            double height = DROP_HEIGHT_MIN
                    + level.random.nextDouble() * (DROP_HEIGHT_MAX - DROP_HEIGHT_MIN);
            Vec3 fall = falling(level.random);
            // Laid back along its own lean, so a thread that sets off crooked still arrives exactly where
            // the salvo was aimed — the tilt is free in accuracy terms.
            Vec3 spawn = target.subtract(fall.scale(height));
            LunarFlareEntity flare = new LunarFlareEntity(ModEntityTypes.LUNAR_FLARE.get(), level);
            flare.setOwner(caster);
            // Each thread sits somewhere else along the focus's colour drift, so five of them side by
            // side are five shades of the same cyan rather than one shade stamped five times.
            flare.setTint(level.random.nextFloat());
            flare.setPos(spawn.x, spawn.y, spawn.z);
            flare.setDeltaMovement(fall.scale(FALL_SPEED));
            level.addFreshEntity(flare);
        }
        level.sendParticles(AstralMoteOptions.LUNAR_FLARE,
                ground.x, ground.y + 0.2D, ground.z, 6, 1.0D, 0.2D, 1.0D, 0.0D);
        level.playSound(null, ground.x, ground.y, ground.z,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.8F);
        if (caster.tickCount % 20 == 0) {
            playSound(level, caster, ModSounds.CAST_SPELL.get());
        }
    }

    /** Where the caster is pointing, walked out to the horizon if it hits nothing. */
    private static Vec3 aimPoint(ServerLevel level, LivingEntity caster, double range) {
        Vec3 eye = caster.getEyePosition();
        Vec3 end = eye.add(caster.getViewVector(1.0F).scale(range));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, caster));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    /**
     * A unit vector pointing down, leaning up to {@link #TILT_DEGREES} off vertical in a random compass
     * direction. Already unit length: the lean and the drop are the sine and cosine of the same angle.
     */
    private static Vec3 falling(RandomSource random) {
        double tilt = Math.toRadians(random.nextDouble() * TILT_DEGREES);
        double bearing = random.nextDouble() * Math.PI * 2.0D;
        double lean = Math.sin(tilt);
        return new Vec3(Math.cos(bearing) * lean, -Math.cos(tilt), Math.sin(bearing) * lean);
    }
}
