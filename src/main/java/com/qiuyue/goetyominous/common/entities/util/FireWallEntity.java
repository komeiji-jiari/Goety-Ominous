package com.qiuyue.goetyominous.common.entities.util;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FireWallEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final ResourceKey<DamageType> FIRE_WALL_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GoetyOminous.MOD_ID, "fire_wall"));
    private static final float WALL_HEIGHT = 4.0F;
    private static final float PART_WIDTH = 0.55F;
    private static final double PART_STEP = 0.8D;
    private static final int DAMAGE_INTERVAL = 10;

    private final List<Vec3> partPositions = new ArrayList<>();
    private FireWallPart[] parts = new FireWallPart[0];
    private List<Vec3> points = new ArrayList<>();
    private float damage = 4.0F;
    private int lifetime = 140;
    private final UUID slowModifierId = UUID.randomUUID();
    private final Map<UUID, LivingEntity> slowedTargets = new HashMap<>();
    @Nullable
    private UUID ownerUUID;
    private int ownerId;
    @Nullable
    private LivingEntity cachedOwner;

    public FireWallEntity(EntityType<? extends FireWallEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public FireWallEntity(Level level, LivingEntity owner, List<Vec3> points, float damage, int lifetime) {
        this(ModEntityTypes.FIRE_WALL.get(), level);
        this.points = new ArrayList<>(points);
        this.damage = damage;
        this.lifetime = lifetime;
        this.setOwner(owner);
        this.setCenterFromPoints();
        this.createParts();
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.ownerUUID = owner != null ? owner.getUUID() : null;
        this.ownerId = owner != null ? owner.getId() : 0;
        this.cachedOwner = owner;
    }

    @Nullable
    public LivingEntity getOwnerEntity() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }
        if (this.ownerId != 0) {
            Entity entity = this.level().getEntity(this.ownerId);
            if (entity instanceof LivingEntity living) {
                this.cachedOwner = living;
                return living;
            }
        }
        if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity living) {
                this.cachedOwner = living;
                return living;
            }
        }
        return null;
    }

    public boolean isOwnerSide(@Nullable Entity shooter) {
        if (shooter == null) {
            return false;
        }
        LivingEntity owner = this.getOwnerEntity();
        if (owner == null) {
            return false;
        }
        if (shooter == owner || MobUtil.areAllies(owner, shooter)) {
            return true;
        }
        if (isHostileIllager(owner) && isHostileIllager(shooter)) {
            return true;
        }
        Entity shooterMaster = shooter instanceof IOwned owned ? owned.getMasterOwner() : null;
        Entity ownerMaster = owner instanceof IOwned owned ? owned.getMasterOwner() : null;
        if (shooterMaster != null && (shooterMaster == owner || MobUtil.areAllies(owner, shooterMaster))) {
            return true;
        }
        if (ownerMaster != null && (ownerMaster == shooter || MobUtil.areAllies(ownerMaster, shooter))) {
            return true;
        }
        return shooterMaster != null && ownerMaster != null && MobUtil.areAllies(shooterMaster, ownerMaster);
    }

    private void setCenterFromPoints() {
        if (this.points.isEmpty()) {
            return;
        }
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        for (Vec3 point : this.points) {
            x += point.x;
            y += point.y;
            z += point.z;
        }
        double size = (double) this.points.size();
        this.setPos(x / size, y / size, z / size);
    }

    private void createParts() {
        this.partPositions.clear();
        List<FireWallPart> list = new ArrayList<>();
        for (int i = 0; i < this.points.size() - 1; i++) {
            Vec3 start = this.points.get(i);
            Vec3 end = this.points.get(i + 1);
            double length = start.distanceTo(end);
            if (length < 1.0E-4D) {
                continue;
            }
            int steps = Math.max(1, (int) Math.ceil(length / PART_STEP));
            for (int j = 0; j < steps; j++) {
                Vec3 raw = start.add(end.subtract(start).scale((double) j / (double) steps));
                Vec3 point = FireWallShape.snapToGround(this.level(), raw);
                list.add(this.createPart(point));
                this.partPositions.add(point);
            }
        }
        if (this.points.size() >= 2) {
            Vec3 point = FireWallShape.snapToGround(this.level(), this.points.get(this.points.size() - 1));
            list.add(this.createPart(point));
            this.partPositions.add(point);
        }
        this.parts = list.toArray(new FireWallPart[0]);
        this.updatePartIds();
    }

    private FireWallPart createPart(Vec3 point) {
        FireWallPart part = new FireWallPart(this, PART_WIDTH, WALL_HEIGHT);
        part.setPos(point.x, point.y, point.z);
        part.setOldPosAndRot();
        return part;
    }

    private void updatePartIds() {
        int id = this.getId();
        for (int i = 0; i < this.parts.length; i++) {
            this.parts[i].setId(id + i + 1);
        }
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        this.updatePartIds();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.points.size() < 2 || this.parts.length == 0) {
            if (!this.level().isClientSide) {
                this.discard();
            }
            return;
        }
        for (int i = 0; i < this.parts.length; i++) {
            Vec3 point = this.partPositions.get(i);
            FireWallPart part = this.parts[i];
            part.setPos(point.x, point.y, point.z);
            part.setOldPosAndRot();
        }
        if (this.level().isClientSide) {
            this.ambientParticles();
            return;
        }
        this.applyEffects();
        if (--this.lifetime < 0) {
            this.discard();
        }
    }

    private void ambientParticles() {
        for (int i = 0; i < this.partPositions.size() - 1; i++) {
            Vec3 point = this.partPositions.get(i);
            Vec3 next = this.partPositions.get(i + 1);
            int count = 2 + this.random.nextInt(2);
            for (int j = 0; j < count; j++) {
                double t = (j + 0.5D) / (double) count;
                double baseY = point.y + (next.y - point.y) * t;
                double x = point.x + (next.x - point.x) * t + (this.random.nextDouble() - 0.5D) * 0.2D;
                double y = baseY + 0.1D + this.random.nextDouble() * (WALL_HEIGHT - 0.1D);
                double z = point.z + (next.z - point.z) * t + (this.random.nextDouble() - 0.5D) * 0.2D;
                double vy = this.random.nextDouble() * 0.25D + 0.05D;
                if (this.random.nextBoolean()) {
                    this.level().addParticle(ModParticleTypes.BIG_FIRE_DROP.get(), x, y, z, 0.0D, vy, 0.0D);
                } else {
                    this.level().addParticle(ModParticleTypes.BIG_FIRE.get(), x, y, z, 0.0D, vy, 0.0D);
                }
            }
        }
    }

    private void applyEffects() {
        LivingEntity owner = this.getOwnerEntity();
        if (owner == null && this.ownerUUID != null) {
            this.clearSlows();
            return;
        }
        DamageSource source = ModDamageSource.noKnockbackDamageSource(this.level(), FIRE_WALL_DAMAGE, this, owner);
        Set<UUID> touching = new HashSet<>();
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getWallArea())) {
            if (this.isOwnerSide(target)) {
                continue;
            }
            if (!this.isTouching(target)) {
                continue;
            }
            touching.add(target.getUUID());
            if (!this.slowedTargets.containsKey(target.getUUID())) {
                AttributeInstance speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null) {
                    speed.addTransientModifier(new AttributeModifier(this.slowModifierId,
                            "pyrologer_fire_wall_slow", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    this.slowedTargets.put(target.getUUID(), target);
                }
            }
            if (this.tickCount % DAMAGE_INTERVAL == 0 && !target.fireImmune()) {
                target.hurt(source, this.damage);
                target.setSecondsOnFire(4);
            }
        }
        Iterator<Map.Entry<UUID, LivingEntity>> iterator = this.slowedTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LivingEntity> entry = iterator.next();
            if (!touching.contains(entry.getKey())) {
                this.removeSlow(entry.getValue());
                iterator.remove();
            }
        }
    }

    private static boolean isHostileIllager(Entity entity) {
        return entity instanceof Raider && !(entity instanceof IOwned);
    }

    private void removeSlow(LivingEntity target) {
        AttributeInstance speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(this.slowModifierId);
        }
    }

    private void clearSlows() {
        for (LivingEntity target : this.slowedTargets.values()) {
            this.removeSlow(target);
        }
        this.slowedTargets.clear();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        this.clearSlows();
        super.remove(reason);
    }

    private boolean isTouching(LivingEntity target) {
        AABB targetBox = target.getBoundingBox();
        for (FireWallPart part : this.parts) {
            if (part.getBoundingBox().inflate(0.2D).intersects(targetBox)) {
                return true;
            }
        }
        return false;
    }

    private AABB getWallArea() {
        if (this.partPositions.isEmpty()) {
            return this.getBoundingBox();
        }
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (Vec3 point : this.partPositions) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            maxZ = Math.max(maxZ, point.z);
        }
        return new AABB(minX - 1.0D, minY - 0.5D, minZ - 1.0D,
                maxX + 1.0D, maxY + WALL_HEIGHT + 1.0D, maxZ + 1.0D);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getWallArea();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.parts;
    }

    public List<Vec3> getPartPositions() {
        return this.partPositions;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.damage = tag.getFloat("Damage");
        this.lifetime = tag.getInt("Lifetime");
        this.ownerUUID = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        this.cachedOwner = null;
        this.ownerId = 0;
        this.points = new ArrayList<>();
        ListTag list = tag.getList("Points", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag value = list.getCompound(i);
            this.points.add(new Vec3(value.getDouble("X"), value.getDouble("Y"), value.getDouble("Z")));
        }
        this.setCenterFromPoints();
        this.createParts();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", this.damage);
        tag.putInt("Lifetime", this.lifetime);
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
        ListTag list = new ListTag();
        for (Vec3 point : this.points) {
            CompoundTag value = new CompoundTag();
            value.putDouble("X", point.x);
            value.putDouble("Y", point.y);
            value.putDouble("Z", point.z);
            list.add(value);
        }
        tag.put("Points", list);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(this.points.size());
        for (Vec3 point : this.points) {
            buffer.writeDouble(point.x);
            buffer.writeDouble(point.y);
            buffer.writeDouble(point.z);
        }
        buffer.writeInt(this.ownerId);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        this.points = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.points.add(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
        }
        this.ownerId = buffer.readInt();
        this.setCenterFromPoints();
        this.createParts();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
