package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class ZombieResurrectionServant extends Entity {
    private static final EntityDataAccessor<String> ENTITY_TYPE =
            SynchedEntityData.defineId(ZombieResurrectionServant.class, EntityDataSerializers.STRING);

    public int startDelay;
    public AnimationState resurrectAnimationState = new AnimationState();
    public int resurrectAnimationLength = 92;
    public int resurrectAnimationTick = 0;
    public LivingEntity owner;

    public ZombieResurrectionServant(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ZombieResurrectionServant(Level level, int startDelay, LivingEntity owner) {
        super(MmEntityRegistry.ZOMBIE_RESURRECTION_SERVANT.get(), level);
        this.startDelay = startDelay;
        this.owner = owner;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ENTITY_TYPE, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("EntityType")) {
            this.entityData.set(ENTITY_TYPE, tag.getString("EntityType"));
        }
        if (tag.contains("StartDelay")) {
            this.startDelay = tag.getInt("StartDelay");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("EntityType", this.entityData.get(ENTITY_TYPE));
        tag.putInt("StartDelay", this.startDelay);
    }

    public void setEntityType(EntityType<?> type) {
        this.entityData.set(ENTITY_TYPE, EntityType.getKey(type).toString());
    }

    public EntityType<?> getEntityType() {
        return this.entityData.get(ENTITY_TYPE) != null && !this.entityData.get(ENTITY_TYPE).isEmpty() ? ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(this.entityData.get(ENTITY_TYPE))) : null;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.resurrectAnimationTick = this.resurrectAnimationLength;
            this.resurrectAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.resurrectAnimationTick > 0 && this.random.nextInt(4) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level().getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getRandomX(1.0), this.getY() + 0.1, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            }
        }
        if (!this.level().isClientSide) {
            if (this.tickCount >= this.startDelay && this.resurrectAnimationTick <= 0) {
                this.resurrectAnimationTick = this.resurrectAnimationLength;
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.playSound(SoundEventInit.ZOMBIE_RESURRECTION.get());
            }
            if (this.resurrectAnimationTick > 0) {
                --this.resurrectAnimationTick;
            }
            if (this.resurrectAnimationTick == 1) {
                if (this.getEntityType() != null) {
                    Entity entity = this.getEntityType().create(this.level());
                    entity.moveTo(this.position());
                    if (this.owner != null && this.owner.getTeam() != null) {
                        Scoreboard scoreboard = this.owner.level().getScoreboard();
                        scoreboard.addPlayerToTeam(entity.getScoreboardName(), scoreboard.getPlayerTeam(this.owner.getTeam().getName()));
                    }
                    if (this.owner != null && this.owner instanceof MutantHuskServant husk) {
                        husk.resurrectedMobs.add(entity);
                    }
                    if (this.owner != null && this.owner instanceof MutantFrozenZombieServant frozenZombie) {
                        frozenZombie.resurrectedMobs.add(entity);
                    }
                    if (entity instanceof Owned owned && this.owner != null) {
                        owned.setTrueOwner(this.owner);
                        owned.setLimitedLife(MobUtil.getSummonLifespan((ServerLevel) this.level()));
                    }
                    if (this.owner != null && this.owner instanceof Mob && ((Mob) this.owner).getTarget() != null && entity instanceof Mob) {
                        ((Mob) entity).setTarget(((Mob) this.owner).getTarget());
                    }
                    entity.lookAt(Anchor.EYES, PositionUtils.getOffsetPos(this, 0.0, this.getEyeHeight(), 2.0, 0.0F, this.getYRot() + 180.0F));
                    if (entity instanceof Mob mob) {
                        mob.finalizeSpawn((ServerLevel) this.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        mob.setBaby(false);
                        mob.playAmbientSound();
                    }
                    ((ServerLevel) this.level()).addFreshEntityWithPassengers(entity);
                }
                this.discard();
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
