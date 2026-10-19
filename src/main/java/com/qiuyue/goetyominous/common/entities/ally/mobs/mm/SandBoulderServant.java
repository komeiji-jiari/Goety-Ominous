package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.audio.soundinstances.EntityLoopingSoundInstance;
import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.blocks.mm.ServantQuicksandBlock;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class SandBoulderServant extends ThrowableProjectile {
    private static final EntityDataAccessor<String> SAND_TYPE =
            SynchedEntityData.defineId(SandBoulderServant.class, EntityDataSerializers.STRING);

    public float damage = 0.0F;
    public int lifeSpan = 100;
    public boolean griefing = true;
    public boolean ignoresInvulTime = true;
    public List<Entity> alreadyHit = Lists.newArrayList();

    public SandBoulderServant(EntityType<? extends SandBoulderServant> type, Level level) {
        super(type, level);
    }

    public SandBoulderServant(Level level, LivingEntity owner) {
        super(MmEntityRegistry.SAND_BOULDER_SERVANT.get(), owner, level);
    }

    public SandBoulderServant(Level level, double x, double y, double z) {
        super(MmEntityRegistry.SAND_BOULDER_SERVANT.get(), x, y, z, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(
                    new EntityLoopingSoundInstance(this, SoundEventInit.MUTANT_HUSK_SAND_BOULDER_LOOP.get(),
                            this.getSoundSource(), 0.5F, 3.0F, 1.0F, false, 3.0F, 1.0F, false));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.01));
        }
        if (this.level().isClientSide) {
            MmScreenShake.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        }
        this.level().addParticle(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float) MiscUtils.sandColourForSandType(this.getSandType()))), this.getRandomX(1.0), this.getY(), this.getRandomZ(1.0), 0.0, 0.0, 0.0);
        if (!this.level().isClientSide) {
            AABB aabb = this.getBoundingBox().inflate(0.2);
            if (this.tickCount >= this.lifeSpan) {
                this.discard();
                ((ServerLevel) this.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float) MiscUtils.sandColourForSandType(this.getSandType()))), this.getX(), this.getY(0.5), this.getZ(), 40, this.getBbWidth() / 2.0F, this.getBbHeight(), this.getBbWidth() / 2.0F, 0.0);
            }
            if (Block.isFaceFull(this.level().getBlockState(this.blockPosition()).getCollisionShape(this.level(), this.blockPosition().below()), Direction.UP)) {
                if (this.getDeltaMovement().y < 0.0) {
                    if (this.getDeltaMovement().y < -0.2) {
                        MmScreenShake.shake(this.level(), 10, 0.15F, this.blockPosition(), 10);
                        this.playSound(SoundEventInit.MUTANT_HUSK_THUD.get(), 2.0F, MiscUtils.randomSoundPitch());
                    }
                    this.setDeltaMovement(this.getDeltaMovement().x, -this.getDeltaMovement().y * 0.5, this.getDeltaMovement().z);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().x, 0.05, this.getDeltaMovement().z);
                }
            }
            if (Block.isFaceFull(this.level().getBlockState(this.blockPosition().above()).getCollisionShape(this.level(), this.blockPosition()), Direction.UP)) {
                if (Block.isFaceFull(this.level().getBlockState(this.blockPosition().above().above()).getCollisionShape(this.level(), this.blockPosition().above()), Direction.UP)) {
                    this.discard();
                    ((ServerLevel) this.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float) MiscUtils.sandColourForSandType(this.getSandType()))), this.getX(), this.getY(0.5), this.getZ(), 40, this.getBbWidth() / 2.0F, this.getBbHeight(), this.getBbWidth() / 2.0F, 0.0);
                } else {
                    this.moveTo(this.position().add(0.0, 1.0, 0.0));
                }
            }
            for (Entity entity : this.level().getEntities(this, this.getBoundingBox())) {
                if (this.alreadyHit.contains(entity) || !this.canHit(entity) || this.getOwner() != null && this.getOwner() == entity) {
                    continue;
                }
                this.alreadyHit.add(entity);
                entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
                if (!this.canHarm(entity)) {
                    continue;
                }
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                entity.hurt(MMDamageTypes.sandBoulderAttack(this.damageSources(), this, this.getOwner()), this.damage);
            }
            if (this.griefing) {
                for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() || !QuicksandBlock.survivable(ServantQuicksandBlock.forSandType(this.getSandType()).defaultBlockState(), this.level(), blockpos) || !this.random.nextBoolean()) continue;
                    this.level().setBlockAndUpdate(blockpos, ServantQuicksandBlock.forSandType(this.getSandType()).defaultBlockState());
                }
            }
        }
    }

    boolean canHarm(Entity target) {
        if (this.isAlly(target)) {
            return false;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HUSK_SAND_BOULDER_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof MutantHuskServant)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.PLAYER_SAND_BOULDER_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof Player)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantHuskServant));
    }

    boolean canHit(Entity target) {
        if (this.isAlly(target)) {
            return false;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_HUSK_SAND_BOULDER_CANT_HIT, this, target, this.getOwner(), entity -> entity instanceof MutantHuskServant)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.PLAYER_SAND_BOULDER_CANT_HIT, this, target, this.getOwner(), entity -> entity instanceof Player)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantHuskServant));
    }

    private boolean isAlly(Entity target) {
        Entity owner = this.getOwner();
        return owner != null && MobUtil.areAllies(owner, target);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putInt("LifeSpan", this.lifeSpan);
        tag.putBoolean("Griefing", this.griefing);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putString("SandType", this.getSandType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("LifeSpan")) {
            this.lifeSpan = tag.getInt("LifeSpan");
        }
        if (tag.contains("Griefing")) {
            this.griefing = tag.getBoolean("Griefing");
        }
        if (tag.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        }
        if (tag.contains("SandType")) {
            this.setSandType(tag.getString("SandType"));
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SAND_TYPE, "");
    }

    public String getSandType() {
        return this.entityData.get(SAND_TYPE);
    }

    public void setSandType(String value) {
        this.entityData.set(SAND_TYPE, value);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
