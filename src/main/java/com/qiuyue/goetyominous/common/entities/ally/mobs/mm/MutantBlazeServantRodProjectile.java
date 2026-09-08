package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.util.MiscUtils;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MutantBlazeServantRodProjectile extends SpellThrowableProjectile {
    private static final EntityDataAccessor<Boolean> BROKEN_BLOCK =
            SynchedEntityData.defineId(MutantBlazeServantRodProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RODLING =
            SynchedEntityData.defineId(MutantBlazeServantRodProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHIELDS =
            SynchedEntityData.defineId(MutantBlazeServantRodProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARMOUR =
            SynchedEntityData.defineId(MutantBlazeServantRodProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COLLECTABLE =
            SynchedEntityData.defineId(MutantBlazeServantRodProjectile.class, EntityDataSerializers.BOOLEAN);

    public int despawnTimer;
    public boolean summonedRodling;
    public boolean landed;
    public float damage = 0.0F;
    public int fireLength = 0;
    public boolean ignoresInvulTime = true;
    public int despawnTime = 1200;
    public boolean glows = false;
    public boolean griefing = true;
    public boolean griefingDropsBlocks = false;
    public boolean fireGriefing = false;
    public int spellLifespanMultiplier = 0;

    public MutantBlazeServantRodProjectile(EntityType<? extends MutantBlazeServantRodProjectile> type, Level level) {
        super(type, level);
    }

    public MutantBlazeServantRodProjectile(Level level, LivingEntity shooter) {
        super(MmEntityRegistry.MUTANT_BLAZE_SERVANT_ROD_PROJECTILE.get(), shooter, level);
    }

    public MutantBlazeServantRodProjectile(Level level, double x, double y, double z) {
        super(MmEntityRegistry.MUTANT_BLAZE_SERVANT_ROD_PROJECTILE.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BROKEN_BLOCK, false);
        this.entityData.define(RODLING, false);
        this.entityData.define(SHIELDS, false);
        this.entityData.define(ARMOUR, false);
        this.entityData.define(COLLECTABLE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWater() && this.isCollectable()) {
            this.setDeltaMovement(0.0D, 0.05D, 0.0D);
        }
        if (!this.level().isClientSide) {
            ++this.despawnTimer;
            if (this.tickCount >= 100 || this.despawnTimer >= (this.isCollectable() ? 6000 : this.despawnTime)) {
                this.discard();
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        if (!this.canHarm(entity)) {
            return;
        }
        if (this.landed) {
            return;
        }
        if (entity instanceof LivingEntity living && living.isBlocking()) {
            entity.hurt(MMDamageTypes.mutantBlazeRodProjectile(this.damageSources(), this, this.getOwner()), this.damage);
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.25D, 0.5D, -0.25D));
            return;
        }
        if (this.ignoresInvulTime) {
            entity.invulnerableTime = 0;
        }
        entity.hurt(MMDamageTypes.mutantBlazeRodProjectile(this.damageSources(), this, this.getOwner()), this.damage);
        entity.setSecondsOnFire(this.fireLength);
        if (this.isRodling()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.25D, 0.5D, -0.25D));
        } else if (!this.isCollectable()) {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        BlockPos pos = result.getBlockPos();
        if (!this.hasBrokenBlock()) {
            this.setHasBrokenBlock(true);
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.25D, 0.5D, -0.25D));
            if (!this.level().isClientSide && this.griefing && !this.isCollectable()) {
                BlockState state = this.level().getBlockState(pos);
                if (!state.is(Blocks.UNBREAKABLE)) {
                    this.level().destroyBlock(pos, this.griefingDropsBlocks);
                }
            }
        } else {
            if (this.isRodling() && !this.isCollectable() && !this.summonedRodling) {
                this.becomeRodling(null);
            }
            this.level().getBlockState(pos).entityInside(this.level(), pos, this);
            this.landed = true;
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.level().getBlockState(this.blockPosition()).isAir() && this.fireGriefing
                    && !this.level().isClientSide && !this.isCollectable()) {
                this.level().setBlockAndUpdate(this.blockPosition(), BaseFireBlock.getState(this.level(), this.blockPosition()));
            }
            if (!this.level().isClientSide && !this.isCollectable() && !this.isRodling()) {
                this.discard();
            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isCollectable()) {
            if (this.isRodling()) {
                this.becomeRodling(player);
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide) {
                this.spawnAtLocation(Items.BLAZE_ROD);
                this.discard();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void becomeRodling(@Nullable Player player) {
        if (this.level().isClientSide || this.summonedRodling) {
            return;
        }
        LivingEntity owner = player;
        if (owner == null && this.getOwner() != null) {
            owner = this.getOwner();
        }
        if (player == null && owner instanceof MutantBlazeServant blaze
                && blaze.rodlingServantCount() >= MutantBlazeServant.MAX_RODLING_SERVANTS) {
            this.summonedRodling = true;
            this.discard();
            return;
        }
        RodlingServant rodling = new RodlingServant(MmEntityRegistry.RODLING_SERVANT.get(), this.level());
        if (owner != null) {
            rodling.setOwnerId(owner.getUUID());
        }
        if (this.hasShields()) {
            rodling.setShields(2);
        }
        if (this.hasArmour()) {
            rodling.setHasHelmet(true);
        }
        if (owner instanceof MutantBlazeServant blaze && blaze.hasBlazingHelm()) {
            rodling.setShields(2);
            rodling.setHasHelmet(true);
        }
        rodling.moveTo(this.getX(), this.getY(), this.getZ());
        if (player == null && owner != null) {
            if (owner instanceof MutantBlazeServant) {
                rodling.setSummonedByMutantBlaze(true);
            }
            int life = com.Polarice3.Goety.utils.MobUtil.getSummonLifespan(this.level());
            if (this.spellLifespanMultiplier > 1) {
                life *= this.spellLifespanMultiplier;
            }
            rodling.setLimitedLife(life);
        }
        this.level().addFreshEntity(rodling);
        this.summonedRodling = true;
        this.discard();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof MutantBlazeServantShieldPart && this.tickCount <= 10) {
            return;
        }
        if (hitResult instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof MutantBlazeServantRodProjectile) {
            return;
        }
        if (!this.landed && !this.hasBrokenBlock()) {
            this.playSound(SoundEventInit.MUTANT_BLAZE_ROD_IMPACT.get(), 1.0F, MiscUtils.randomSoundPitch());
        }
        super.onHit(hitResult);
    }

    boolean canHarm(Entity target) {
        if (target instanceof MutantBlazeServant) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        return owner == null || !MobUtil.areAllies(owner, target);
    }

    boolean canHit(Entity target) {
        if (target instanceof MutantBlazeServant) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        return owner == null || !MobUtil.areAllies(owner, target);
    }

    public boolean hasBrokenBlock() { return this.entityData.get(BROKEN_BLOCK); }
    public void setHasBrokenBlock(boolean value) { this.entityData.set(BROKEN_BLOCK, value); }
    public boolean isRodling() { return this.entityData.get(RODLING); }
    public void setIsRodling(boolean value) { this.entityData.set(RODLING, value); }
    public boolean hasShields() { return this.entityData.get(SHIELDS); }
    public void setHasShields(boolean value) { this.entityData.set(SHIELDS, value); }
    public boolean hasArmour() { return this.entityData.get(ARMOUR); }
    public void setHasArmour(boolean value) { this.entityData.set(ARMOUR, value); }
    public boolean isCollectable() { return this.entityData.get(COLLECTABLE); }
    public void setCollectable(boolean value) { this.entityData.set(COLLECTABLE, value); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasBrokenBlock", this.hasBrokenBlock());
        tag.putBoolean("IsRodling", this.isRodling());
        tag.putBoolean("HasShields", this.hasShields());
        tag.putBoolean("HasArmour", this.hasArmour());
        tag.putBoolean("Collectable", this.isCollectable());
        tag.putFloat("Damage", this.damage);
        tag.putInt("FireLength", this.fireLength);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putInt("DespawnTime", this.despawnTime);
        tag.putBoolean("Glows", this.glows);
        tag.putBoolean("Griefing", this.griefing);
        tag.putBoolean("GriefingDropsBlocks", this.griefingDropsBlocks);
        tag.putBoolean("FireGriefing", this.fireGriefing);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HasBrokenBlock")) this.setHasBrokenBlock(tag.getBoolean("HasBrokenBlock"));
        if (tag.contains("IsRodling")) this.setIsRodling(tag.getBoolean("IsRodling"));
        if (tag.contains("HasShields")) this.setHasShields(tag.getBoolean("HasShields"));
        if (tag.contains("HasArmour")) this.setHasArmour(tag.getBoolean("HasArmour"));
        if (tag.contains("Collectable")) this.setCollectable(tag.getBoolean("Collectable"));
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("FireLength")) this.fireLength = tag.getInt("FireLength");
        if (tag.contains("IgnoresInvulTime")) this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        if (tag.contains("DespawnTime")) this.despawnTime = tag.getInt("DespawnTime");
        if (tag.contains("Glows")) this.glows = tag.getBoolean("Glows");
        if (tag.contains("Griefing")) this.griefing = tag.getBoolean("Griefing");
        if (tag.contains("GriefingDropsBlocks")) this.griefingDropsBlocks = tag.getBoolean("GriefingDropsBlocks");
        if (tag.contains("FireGriefing")) this.fireGriefing = tag.getBoolean("FireGriefing");
    }
}
