package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.audio.soundinstances.EntityLoopingSoundInstance;
import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class MutantBlazeServantFireball extends Fireball {
    public boolean spawnedByDyingMutantBlaze;
    public float damage = 0.0F;
    public float explosionDamage = 0.0F;
    public float explosionRadius = 1.0F;
    public int fireLength = 0;
    public boolean ignoresInvulTime = true;
    public boolean griefing = true;
    public boolean griefingDropsBlocks = false;
    public boolean explosionFire = true;

    public MutantBlazeServantFireball(EntityType<? extends MutantBlazeServantFireball> type, Level level) {
        super(type, level);
    }

    public MutantBlazeServantFireball(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(MmEntityRegistry.MUTANT_BLAZE_SERVANT_FIREBALL.get(), shooter, dx, dy, dz, level);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("ExplosionDamage")) this.explosionDamage = tag.getFloat("ExplosionDamage");
        if (tag.contains("ExplosionRadius")) this.explosionRadius = tag.getFloat("ExplosionRadius");
        if (tag.contains("FireLength")) this.fireLength = tag.getInt("FireLength");
        if (tag.contains("IgnoresInvulTime")) this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        if (tag.contains("Griefing")) this.griefing = tag.getBoolean("Griefing");
        if (tag.contains("GriefingDropsBlocks")) this.griefingDropsBlocks = tag.getBoolean("GriefingDropsBlocks");
        if (tag.contains("ExplosionFire")) this.explosionFire = tag.getBoolean("ExplosionFire");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putFloat("ExplosionDamage", this.explosionDamage);
        tag.putFloat("ExplosionRadius", this.explosionRadius);
        tag.putInt("FireLength", this.fireLength);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putBoolean("Griefing", this.griefing);
        tag.putBoolean("GriefingDropsBlocks", this.griefingDropsBlocks);
        tag.putBoolean("ExplosionFire", this.explosionFire);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 120) {
            this.discard();
        }
        if (this.level().isClientSide) {
            ShakeCameraEvent.shake(this.level(), 4, 0.0075F, this.blockPosition(), 7);
            for (int i = 0; i < 2; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypeInit.FIRE_TRAIL.get(),
                        this.getRandomX(1.0D), this.getY(), this.getRandomZ(1.0D), d0, d1, d2);
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().play(
                    new EntityLoopingSoundInstance(this, SoundEventInit.MUTANT_WITHER_SKELETON_FIRE_SLASH_LOOP.get(),
                            this.getSoundSource(), 0.5F, 3.0F, 1.25F, false, 3.0F, 1.0F, false));
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServantShieldPart
                    && this.tickCount <= 10) {
                return;
            }
            if (this.spawnedByDyingMutantBlaze && hitResult instanceof EntityHitResult entityHit
                    && (entityHit.getEntity() instanceof MutantBlazeServantRodProjectile
                    || entityHit.getEntity() instanceof MutantBlazeServantFireball)) {
                return;
            }
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServantShieldPart
                    && this.tickCount > 10) {
                this.shieldExplosion();
                this.discard();
                return;
            }
            if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof MutantBlazeServant blaze
                    && this.tickCount > 10 && MutantBlazeCommonConfig.stunnable.get()) {
                blaze.playSound(SoundEventInit.MUTANT_BLAZE_STUN_START.get(), 2.0F, MiscUtils.randomSoundPitch());
                blaze.stunnedTicks = blaze.stunnedLength;
                this.level().broadcastEntityEvent(blaze, (byte) 12);
                this.shieldExplosion();
                this.discard();
                return;
            }
            super.onHit(hitResult);
            Entity owner = this.getOwner();
            MiscUtils.customExplosion(this.level(), owner != null ? owner : this,
                    this.damageSources().explosion(this, owner instanceof LivingEntity ? owner : null),
                    null, this.getX(), this.getY(), this.getZ(),
                    Mth.clamp(this.explosionRadius, 1.0F, Float.MAX_VALUE), this.explosionFire,
                    this.explosionBlockInteraction(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(),
                    ParticleTypes.EXPLOSION, ParticleTypeInit.FIRE_TRAIL.get(), this.explosionDamage, true, false);
            this.discard();
        }
    }

    private void shieldExplosion() {
        Entity owner = this.getOwner();
        MiscUtils.customExplosion(this.level(), owner != null ? owner : this,
                this.damageSources().explosion(this, owner instanceof LivingEntity ? owner : null),
                null, this.getX(), this.getY(), this.getZ(), 1.5F, false,
                BlockInteraction.KEEP, SoundEvents.GENERIC_EXPLODE, this.getSoundSource(),
                ParticleTypes.EXPLOSION, ParticleTypeInit.FIRE_TRAIL.get(), 1.0F, true, false);
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockGetter, BlockPos pos, BlockState state, float power) {
        return state.getExplosionResistance(blockGetter, pos, explosion) <= 7.0F && !state.is(Blocks.UNBREAKABLE);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof MutantBlazeServantShieldPart) {
            return;
        }
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide) {
            Entity entity = hitResult.getEntity();
            Entity owner = this.getOwner();
            if (this.canHarm(entity)) {
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                entity.hurt(MMDamageTypes.fireResBypassingFireball(this.damageSources(), this, owner), this.damage);
                entity.setSecondsOnFire(this.fireLength);
                if (owner instanceof LivingEntity livingOwner) {
                    this.doEnchantDamageEffects(livingOwner, entity);
                }
            }
        }
    }

    boolean canHarm(Entity target) {
        if (target instanceof MutantBlazeServant) {
            return false;
        }
        Entity owner = this.getOwner();
        return owner == null || !(owner instanceof LivingEntity livingOwner) || !MobUtil.areAllies(livingOwner, target);
    }

    boolean canHit(Entity target) {
        if (target instanceof MutantBlazeServant) {
            return false;
        }
        Entity owner = this.getOwner();
        return owner == null || !(owner instanceof LivingEntity livingOwner) || !MobUtil.areAllies(livingOwner, target);
    }

    public Explosion.BlockInteraction explosionBlockInteraction() {
        return Explosion.BlockInteraction.KEEP;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
