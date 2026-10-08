package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.entities.projectile.WindCharge;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.ModItems;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class OminousItemSpawner extends Entity {
    private static final int SPAWN_ITEM_DELAY_MIN = 60;
    private static final int SPAWN_ITEM_DELAY_MAX = 120;
    private static final String TAG_SPAWN_ITEM_AFTER_TICKS = "spawn_item_after_ticks";
    private static final String TAG_ITEM = "item";
    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(OminousItemSpawner.class, EntityDataSerializers.ITEM_STACK);
    public static final int TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND = 36;
    private static final float PROJECTILE_DEVIATION = 0.11485F;
    private long spawnItemAfterTicks;

    public OminousItemSpawner(EntityType<? extends OminousItemSpawner> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static OminousItemSpawner create(Level level, ItemStack stack) {
        OminousItemSpawner spawner = new OminousItemSpawner(ModEntityTypes.OMINOUS_ITEM_SPAWNER.get(), level);
        spawner.spawnItemAfterTicks = level.random.nextInt(SPAWN_ITEM_DELAY_MIN, SPAWN_ITEM_DELAY_MAX);
        spawner.setItem(stack);
        return spawner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.tickClient();
        } else {
            this.tickServer();
        }
    }

    private void tickServer() {
        if ((long) this.tickCount == this.spawnItemAfterTicks - TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND) {
            this.level().playSound(null, this.blockPosition(), ModSounds.TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM.get(),
                    SoundSource.NEUTRAL);
        }
        if ((long) this.tickCount >= this.spawnItemAfterTicks) {
            this.spawnItem();
            this.kill();
        }
    }

    private void tickClient() {
        if (this.level().getGameTime() % 5L == 0L) {
            this.addParticles();
        }
    }

    private void spawnItem() {
        Level level = this.level();
        ItemStack stack = this.getItem();
        if (stack.isEmpty()) {
            return;
        }
        Entity spawned = this.createSpawnedItem(level, stack);
        if (spawned == null) {
            return;
        }
        level.addFreshEntity(spawned);
        level.levelEvent(3021, this.blockPosition(), 1);
        level.gameEvent(spawned, GameEvent.ENTITY_PLACE, this.position());
        this.setItem(ItemStack.EMPTY);
    }

    @Nullable
    private Entity createSpawnedItem(Level level, ItemStack stack) {
        if (stack.getItem() instanceof ArrowItem) {
            Arrow arrow = new Arrow(level, this.getX(), this.getY(), this.getZ());
            arrow.setEffectsFromItem(stack);
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
            arrow.setOwner(this);
            arrow.shoot(0.0D, -1.0D, 0.0D, 1.1F, 6.0F);
            return arrow;
        }
        if (stack.getItem() instanceof ThrowablePotionItem) {
            ThrownPotion potion = new ThrownPotion(level, this.getX(), this.getY(), this.getZ());
            potion.setItem(stack);
            potion.setOwner(this);
            potion.shoot(0.0D, -1.0D, 0.0D, 1.375F, 3.0F);
            return potion;
        }
        if (stack.is(Items.FIRE_CHARGE)) {
            Vec3 spread = this.downwardSpread();
            SmallFireball fireball = new SmallFireball(level, this.getX(), this.getY(), this.getZ(),
                    spread.x(), spread.y(), spread.z());
            fireball.setItem(stack);
            fireball.setOwner(this);
            return fireball;
        }
        if (stack.is(ModItems.WIND_CHARGE.get())) {
            WindCharge windCharge = new WindCharge(level, this.getX(), this.getY(), this.getZ(),
                    this.downwardSpread());
            windCharge.setOwner(this);
            return windCharge;
        }
        return new ItemEntity(level, this.getX(), this.getY(), this.getZ(), stack);
    }

    private Vec3 downwardSpread() {
        return new Vec3(this.random.triangle(0.0D, PROJECTILE_DEVIATION),
                this.random.triangle(-1.0D, PROJECTILE_DEVIATION),
                this.random.triangle(0.0D, PROJECTILE_DEVIATION));
    }

    private void addParticles() {
        Vec3 pos = this.position();
        int count = this.random.nextInt(1, 3);
        for (int i = 0; i < count; ++i) {
            Vec3 target = new Vec3(this.getX() + 0.4D * (this.random.nextDouble() - this.random.nextDouble()),
                    this.getY() + 0.4D * (this.random.nextDouble() - this.random.nextDouble()),
                    this.getZ() + 0.4D * (this.random.nextDouble() - this.random.nextDouble()));
            Vec3 velocity = pos.vectorTo(target);
            this.level().addParticle(ModParticleTypes.OMINOUS_SPAWNING.get(), pos.x(), pos.y(), pos.z(),
                    velocity.x(), velocity.y(), velocity.z());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ItemStack stack = tag.contains(TAG_ITEM, 10) ? ItemStack.of(tag.getCompound(TAG_ITEM)) : ItemStack.EMPTY;
        this.setItem(stack);
        this.spawnItemAfterTicks = tag.getLong(TAG_SPAWN_ITEM_AFTER_TICKS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItem().isEmpty()) {
            tag.put(TAG_ITEM, this.getItem().save(new CompoundTag()));
        }
        tag.putLong(TAG_SPAWN_ITEM_AFTER_TICKS, this.spawnItemAfterTicks);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    private void setItem(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM, stack);
    }
}
