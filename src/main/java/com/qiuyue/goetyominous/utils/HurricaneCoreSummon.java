package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.client.particles.WindBlowParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class HurricaneCoreSummon extends Entity {
    public static final int LIFE_SPAN = 80;
    private static final int RISE_TICKS = 40;
    private static final double RISE_HEIGHT = 2.0D;
    @Nullable
    public Entity entity;
    private double startY = Double.NaN;
    private double groundY = Double.NaN;
    private boolean playedEvent;

    public HurricaneCoreSummon(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public HurricaneCoreSummon(Level level, Vec3 pos, double groundY, Entity entity) {
        this(ModEntityTypes.HURRICANE_CORE_SUMMON.get(), level);
        this.setPos(pos.x, pos.y, pos.z);
        this.startY = pos.y;
        this.groundY = groundY;
        this.entity = entity;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        Entity loaded = EntityType.loadEntityRecursive(tag, this.level(), e -> e);
        if (loaded != null) {
            this.entity = loaded;
        }
        if (tag.contains("CurrentLife")) {
            this.tickCount = tag.getInt("CurrentLife");
        }
        if (tag.contains("StartY")) {
            this.startY = tag.getDouble("StartY");
        }
        if (tag.contains("GroundY")) {
            this.groundY = tag.getDouble("GroundY");
        }
        this.playedEvent = tag.getBoolean("PlayedEvent");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.entity != null) {
            this.entity.save(tag);
        }
        tag.putInt("CurrentLife", this.tickCount);
        tag.putDouble("StartY", this.startY);
        tag.putDouble("GroundY", this.groundY);
        tag.putBoolean("PlayedEvent", this.playedEvent);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public float getRise(float partialTicks) {
        return Mth.clamp(((float) this.tickCount + partialTicks) / (float) RISE_TICKS, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (Double.isNaN(this.startY)) {
            this.startY = this.getY();
        }
        if (Double.isNaN(this.groundY)) {
            this.groundY = this.startY;
        }
        float rise = this.getRise(0.0F);
        double eased = 1.0D - Math.pow(1.0D - rise, 3.0D);
        this.setPos(this.getX(), this.startY + eased * RISE_HEIGHT, this.getZ());
        if (!this.playedEvent) {
            this.playedEvent = true;
            this.playSound(com.Polarice3.Goety.init.ModSounds.BOSS_SUMMON.get(), 16.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BREEZE_CHARGE.get(), SoundSource.HOSTILE, 2.0F, 0.5F);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            float angle = this.tickCount * 0.35F;
            double radius = 1.2D + 0.6D * rise;
            for (int i = 0; i < 3; i++) {
                float a = angle + i * ((float) Math.PI * 2.0F / 3.0F);
                double x = this.getX() + Mth.cos(a) * radius;
                double z = this.getZ() + Mth.sin(a) * radius;
                double y = this.getY() + 0.25D + this.random.nextDouble() * 0.5D;
                double speed = 0.15D + 0.25D * rise;
                int width = this.random.nextIntBetweenInclusive(1, 3);
                serverLevel.sendParticles(new WindBlowParticleOption(ColorUtil.WHITE, width, this.random.nextFloat() * 0.4F),
                        x, y, z, 0, -Mth.sin(a) * speed, 0.02D, Mth.cos(a) * speed, 1.0D);
            }
            if (this.tickCount % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.groundY + 0.1D, this.getZ(), 2, 0.6D, 0.05D, 0.6D, 0.02D);
            }
            if (this.tickCount % 20 == 0) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BREEZE_WHIRL.get(), SoundSource.HOSTILE, 1.5F, 0.5F);
            }
            if (this.tickCount == LIFE_SPAN) {
                serverLevel.sendParticles(ModParticleTypes.GUST_EMITTER_LARGE.get(), this.getX(), this.groundY, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 30, 0.5D, 0.5D, 0.5D, 0.1D);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BREEZE_WIND_CHARGE_BURST.get(), SoundSource.HOSTILE, 2.0F, 0.6F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BREEZE_DEATH.get(), SoundSource.HOSTILE, 2.0F, 0.45F);
                if (this.entity != null) {
                    this.entity.setPos(this.getX(), this.groundY, this.getZ());
                    serverLevel.addFreshEntity(this.entity);
                }
            }
        }
        if (this.tickCount >= LIFE_SPAN) {
            this.discard();
        }
    }
}
