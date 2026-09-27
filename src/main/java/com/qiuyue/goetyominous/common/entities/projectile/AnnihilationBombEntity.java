package com.qiuyue.goetyominous.common.entities.projectile;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.qiuyue.goetyominous.common.blocks.entities.ac.AnnihilationBombBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.events.NucleeperNukeProtectionHandler;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.PlayMessages;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class AnnihilationBombEntity extends NuclearBombEntity {

    private static final String IGNITER_TAG = "Igniter";

    private static final String IDENTITY_TAG = "Identity";

    private static final double SERVANT_MIN_DISTANCE = 4.0;

    private static final double SERVANT_MAX_DISTANCE = 10.0;

    private static final EntityDataAccessor<CompoundTag> DATA_IDENTITY =
            SynchedEntityData.defineId(AnnihilationBombEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private UUID igniter;

    private CompoundTag identity = new CompoundTag();

    private boolean detonated;

    public AnnihilationBombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public AnnihilationBombEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.ANNIHILATION_BOMB.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    public void setIgniter(Player player) {
        this.igniter = player.getUUID();
    }

    public void setIdentity(CompoundTag identity) {
        this.identity = identity.copy();
        this.getEntityData().set(DATA_IDENTITY, identity.copy());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_IDENTITY, new CompoundTag());
    }

    private CompoundTag getIdentity() {
        return this.level().isClientSide ? this.getEntityData().get(DATA_IDENTITY) : this.identity;
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.7, 0.7));
        }
        if ((this.tickCount + this.getId()) % 10 == 0 && this.level() instanceof ServerLevel serverLevel) {
            this.getNearbySirens(serverLevel, 256).forEach(this::activateSiren);
        }
        int time = this.getTime() + 1;
        if (time > MAX_TIME) {
            this.discard();
            if (!this.level().isClientSide && !this.detonated) {
                this.detonate();
                this.detonated = true;
            }
        } else {
            this.setTime(time);
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide && MAX_TIME - time > 10 && this.random.nextFloat() < 0.3F && this.onGround()) {
                Vec3 center = this.getEyePosition();
                this.level().addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
            }
        }
    }

    private void detonate() {
        float size = AlexsCaves.COMMON_CONFIG.nukeExplosionSizeModifier.get().floatValue();
        if (this.level() instanceof ServerLevel serverLevel) {
            Set<UUID> ownerIds = new HashSet<>();
            if (this.igniter != null) {
                ownerIds.add(this.igniter);
            }
            NucleeperNukeProtectionHandler.registerProtection(serverLevel, this.position(), size, ownerIds);
            NucleeperNukeProtectionHandler.syncZoneToClients(serverLevel, this.position(), size, ownerIds);
        }
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(this.level());
        if (explosion != null) {
            explosion.copyPosition(this);
            explosion.setSize(size);
            explosion.setNoGriefing(true);
            NucleeperNukeProtectionHandler.suppressVanillaCloud(explosion);
            this.level().addFreshEntity(explosion);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            this.spawnSurfaceCloud(serverLevel, size);
            this.reviveServant(serverLevel, size);
        }
    }

    private void spawnSurfaceCloud(ServerLevel level, float size) {
        double x = this.getX();
        double z = this.getZ();
        double y = findSurfaceY(level, x, z);
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, (SimpleParticleType) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(), true,
                    x, y, z, 0, 1.0D, 0.0D, 0.0D, size);
        }
    }

    private void reviveServant(ServerLevel level, float size) {
        TremorzillaServant servant = AcEntityRegistry.TREMORZILLA_SERVANT.get().create(level);
        if (servant == null) {
            return;
        }
        Player player = this.igniter == null ? null : level.getServer().getPlayerList().getPlayer(this.igniter);
        if (player != null) {
            if (!RitualRequirements.canSummon(level, player, servant.getType())) {
                return;
            }
            servant.setTrueOwner(player);
        }
        servant.loadIdentity(this.identity);
        servant.setHealth(servant.getMaxHealth());
        servant.beginNukeRecovery(NucleeperNukeProtectionHandler.protectionTicks(size));
        double angle = this.random.nextDouble() * (Math.PI * 2.0);
        double distance = SERVANT_MIN_DISTANCE + this.random.nextDouble() * (SERVANT_MAX_DISTANCE - SERVANT_MIN_DISTANCE);
        double x = this.getX() + Math.cos(angle) * distance;
        double z = this.getZ() + Math.sin(angle) * distance;
        float yaw = this.random.nextFloat() * 360.0F;
        if (player != null) {
            yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x) * (180.0 / Math.PI)) - 90.0F;
        }
        servant.moveTo(x, findSurfaceY(level, x, z), z, yaw, 0.0F);
        level.addFreshEntity(servant);
    }

    private static double findSurfaceY(Level level, double x, double z) {
        int cx = Mth.floor(x);
        int cz = Mth.floor(z);
        int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx, cz);
        for (int dx = -8; dx <= 8; dx += 4) {
            for (int dz = -8; dz <= 8; dz += 4) {
                int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx + dx, cz + dz);
                if (h > surface) {
                    surface = h;
                }
            }
        }
        return surface + 1.0;
    }

    private Stream<BlockPos> getNearbySirens(ServerLevel level, int range) {
        PoiManager poiManager = level.getPoiManager();
        return poiManager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()),
                pos -> true, this.blockPosition(), range, PoiManager.Occupancy.ANY);
    }

    private void activateSiren(BlockPos pos) {
        BlockEntity blockEntity = this.level().getBlockEntity(pos);
        if (blockEntity instanceof NuclearSirenBlockEntity siren) {
            siren.setNearestNuclearBomb(this);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Tags.Items.SHEARS)) {
            return super.interact(player, hand);
        }
        player.swing(hand);
        if (!this.level().isClientSide) {
            this.playSound(ACSoundRegistry.NUCLEAR_BOMB_DEFUSE.get());
            this.remove(Entity.RemovalReason.KILLED);
            this.spawnAtLocation(this.createBombItem());
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getPickResult() {
        return this.createBombItem();
    }

    private ItemStack createBombItem() {
        ItemStack stack = new ItemStack(AcItems.ANNIHILATION_BOMB.get());
        CompoundTag identity = this.getIdentity();
        if (!identity.isEmpty()) {
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.put(AnnihilationBombBlockEntity.IDENTITY_TAG, identity.copy());
            stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
        }
        return stack;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(IGNITER_TAG)) {
            this.igniter = tag.getUUID(IGNITER_TAG);
        }
        if (tag.contains(IDENTITY_TAG)) {
            this.setIdentity(tag.getCompound(IDENTITY_TAG));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.igniter != null) {
            tag.putUUID(IGNITER_TAG, this.igniter);
        }
        if (!this.identity.isEmpty()) {
            tag.put(IDENTITY_TAG, this.identity.copy());
        }
    }
}
