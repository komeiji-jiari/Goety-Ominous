package com.qiuyue.goetyominous.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.Vec3;
import com.qiuyue.goetyominous.utils.GoetyOminousWolfArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class WolfTotemBlockEntity extends TrainingBlockEntity {
    private static final TagKey<Block> CRYPT_BLOCKS = BlockTags.create(new ResourceLocation("goety", "crypt_blocks"));
    public static final String SERVANT_LIST = "WolfTotemServants";
    private static final String CREATED_WARG = "CreatedWarg";
    private static final String HEALTH_BONUS = "GoetyOminousWolfTotemHealthBonus";
    private int rawMeat;
    private int bones;
    private final List<LivingEntity> servants = new ArrayList<>();
    private final List<UUID> uuids = new ArrayList<>();
    private CursedCageBlockEntity cursedCageTile;
    private UUID createdWarg;

    public WolfTotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WOLF_TOTEM.get(), pos, state);
        this.trainTimeTotal = 100;
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        ServerLevel serverLevel = level instanceof ServerLevel sl ? sl : null;
        Set<UUID> before = serverLevel != null ? this.collectNearbySummoned(serverLevel) : null;
        super.tick(level, blockPos, blockState, blockEntity);
        if (serverLevel != null) {
            this.applyBonusToFreshSummons(serverLevel, before);
            this.updateReviveServants();
        }
    }

    private Set<UUID> collectNearbySummoned(ServerLevel serverLevel) {
        Set<UUID> uuids = new HashSet<>();
        for (Summoned summoned : serverLevel.getEntitiesOfClass(Summoned.class, new AABB(this.getBlockPos()).inflate(8.0D))) {
            uuids.add(summoned.getUUID());
        }
        return uuids;
    }

    private void applyBonusToFreshSummons(ServerLevel serverLevel, Set<UUID> before) {
        if (this.getTrueOwner() == null) {
            return;
        }
        for (Summoned summoned : serverLevel.getEntitiesOfClass(Summoned.class, new AABB(this.getBlockPos()).inflate(8.0D))) {
            boolean fresh = !before.contains(summoned.getUUID());
            boolean ownerMatch = summoned.getTrueOwner() == this.getTrueOwner();
            boolean noBonus = !summoned.getPersistentData().getBoolean(HEALTH_BONUS);
            if (fresh && ownerMatch && noBonus) {
                applyWolfTotemBonus(summoned);
                GoetyOminousWolfArmorUtil.equipRingGrantedArmor(summoned);
                this.moveToGround(serverLevel, summoned);
            }
        }
    }

    private void moveToGround(ServerLevel serverLevel, LivingEntity entity) {
        Vec3 vec3 = RespawnAnchorBlock.findStandUpPosition(entity.getType(), serverLevel, entity.blockPosition())
                .orElse(entity.blockPosition().getCenter());
        entity.teleportTo(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public LivingEntity getTrueOwner() {
        if (this.level != null && this.getOwnerUUID() != null) {
            LivingEntity owner = EntityFinder.getLivingEntityByUuiD(this.level, this.getOwnerUUID());
            return owner;
        }
        return null;
    }

    @Override
    public void setVariant(ItemStack itemStack, Level level, BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        EntityType<?> entityType = ModEntityType.BLACK_WOLF.get();
        BlockPos spawnPos = blockPos.above();
        if (serverLevel.dimension() == Level.NETHER || serverLevel.getBiome(spawnPos).is(BiomeTags.IS_NETHER)) {
            entityType = ModEntityType.HELLHOUND.get();
        } else if (serverLevel.getBlockState(blockPos.below()).is(CRYPT_BLOCKS)) {
            entityType = ModEntityType.SKELETON_WOLF.get();
        } else if (serverLevel.getBiome(spawnPos).value().coldEnoughToSnow(spawnPos)) {
            entityType = ModEntityType.WINTER_WOLF.get();
        } else if (serverLevel.isThundering() && serverLevel.canSeeSky(spawnPos)) {
            entityType = ModEntityType.STORMHOUND.get();
        }
        this.setEntityType(entityType);
    }

    @Override
    public void startTraining(int amount, ItemStack itemStack) {
        super.startTraining(amount, itemStack);
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ModSounds.GRAVESTONE_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void playSpawnSound() {
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.WOLF_HOWL, SoundSource.BLOCKS, 0.20F, 0.75F);
        }
    }

    @Override
    public boolean placeItem(ItemStack stack) {
        if (this.level == null || this.level.isClientSide || this.trainAmount >= this.maxTrainAmount()) {
            return false;
        }
        boolean accepted = false;
        if (isRawMeat(stack)) {
            ++this.rawMeat;
            accepted = true;
        } else if (stack.is(Items.BONE)) {
            ++this.bones;
            accepted = true;
        }
        if (!accepted) {
            return false;
        }
        stack.shrink(1);
        if (this.rawMeat > 0 && this.bones > 0) {
            --this.rawMeat;
            --this.bones;
            this.startTraining(2, stack);
        }
        this.markUpdated();
        return true;
    }

    @Override
    public void markUpdated() {
        super.markUpdated();
        if (this.level != null && this.getBlockState().hasProperty(BlockStateProperties.POWERED)) {
            boolean powered = this.isTraining() || this.canOfferRevive();
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.POWERED, powered), 3);
            BlockState upper = this.level.getBlockState(this.getBlockPos().above());
            if (upper.is(this.getBlockState().getBlock()) && upper.hasProperty(BlockStateProperties.POWERED)) {
                this.level.setBlock(this.getBlockPos().above(), upper.setValue(BlockStateProperties.POWERED, powered), 3);
            }
        }
    }

    @Override
    public int maxTrainAmount() {
        return 10;
    }

    @Override
    public boolean summonLimit() {
        return false;
    }

    public static void applyWolfTotemBonus(LivingEntity entity) {
        if (entity instanceof Summoned) {
            entity.getPersistentData().putBoolean(HEALTH_BONUS, true);
            restoreWolfTotemBonus(entity, true);
        }
    }

    public static void restoreWolfTotemBonus(LivingEntity entity, boolean healToFull) {
        if (!entity.getPersistentData().getBoolean(HEALTH_BONUS)) {
            return;
        }
        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            double baseHealth = entity instanceof SkeletonWolf
                    ? AttributesConfig.SkeletonWolfHealth.get()
                    : entity instanceof BlackWolf
                    ? AttributesConfig.BlackWolfHealth.get()
                    : health.getBaseValue();
            health.setBaseValue(baseHealth * 2.0D);
            if (healToFull) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    public List<LivingEntity> getServants() {
        return this.servants;
    }

    public void addServant(LivingEntity servant) {
        if (!this.uuids.contains(servant.getUUID())) {
            this.uuids.add(servant.getUUID());
        }
    }

    public void removeServant(LivingEntity servant) {
        this.uuids.remove(servant.getUUID());
        this.servants.remove(servant);
    }

    public boolean hasSpace() {
        return this.getServants().size() < MainConfig.OminousIdolLimit.get();
    }

    public boolean hasCreatedWarg() {
        return this.createdWarg != null;
    }

    public void setCreatedWarg(UUID createdWarg) {
        this.createdWarg = createdWarg;
        this.markUpdated();
    }

    public UUID getCreatedWarg() {
        return this.createdWarg;
    }

    public void releaseWarg(UUID wargId) {
        boolean changed = this.uuids.remove(wargId);
        changed |= this.servants.removeIf(servant -> servant.getUUID().equals(wargId));
        if (wargId.equals(this.createdWarg)) {
            this.createdWarg = null;
            changed = true;
        }
        if (changed) {
            this.markUpdated();
        }
    }

    public boolean canOfferRevive() {
        return this.checkCage() && !this.getServants().isEmpty();
    }

    public int getSoulEnergy() {
        if (this.checkCage() && this.cursedCageTile != null) {
            return this.cursedCageTile.getSouls();
        }
        return 0;
    }

    public void siphonSoulEnergy(int souls) {
        if (this.checkCage() && this.cursedCageTile != null) {
            this.cursedCageTile.decreaseSouls(souls);
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        CompoundTag written = super.writeNetwork(tag);
        written.putInt("RawMeat", this.rawMeat);
        written.putInt("Bones", this.bones);
        ListTag list = new ListTag();
        for (UUID uuid : this.uuids) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        written.put(SERVANT_LIST, list);
        if (this.createdWarg != null) {
            written.putUUID(CREATED_WARG, this.createdWarg);
        }
        return written;
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        this.rawMeat = tag.getInt("RawMeat");
        this.bones = tag.getInt("Bones");
        this.uuids.clear();
        if (tag.contains(SERVANT_LIST)) {
            ListTag list = tag.getList(SERVANT_LIST, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); ++i) {
                this.uuids.add(UUID.fromString(list.getString(i)));
            }
        }
        this.createdWarg = tag.hasUUID(CREATED_WARG) ? tag.getUUID(CREATED_WARG) : null;
    }

    private static boolean isRawMeat(ItemStack stack) {
        return stack.is(Items.BEEF)
                || stack.is(Items.CHICKEN)
                || stack.is(Items.COD)
                || stack.is(Items.MUTTON)
                || stack.is(Items.PORKCHOP)
                || stack.is(Items.RABBIT)
                || stack.is(Items.SALMON);
    }

    private void updateReviveServants() {
        if (!this.uuids.isEmpty()) {
            this.chunkLoadBlock();
            this.uuids.removeIf(uuid -> {
                Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (!(entity instanceof LivingEntity living) || !WolfTotemHooks.canUseTotem(living) || living.isRemoved()) {
                    this.markUpdated();
                    return true;
                }
                WolfTotemBlockEntity totem = WolfTotemHooks.getTotem(living);
                if (totem != null && totem != this) {
                    this.servants.remove(living);
                    this.markUpdated();
                    return true;
                }
                if (!this.servants.contains(living)) {
                    this.servants.add(living);
                    this.markUpdated();
                }
                return false;
            });
            this.servants.removeIf(living -> living.isRemoved() || !this.uuids.contains(living.getUUID()));
        } else if (!this.servants.isEmpty()) {
            this.servants.clear();
            this.markUpdated();
        }
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = this.getBlockPos().below();
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity cage) {
                    this.cursedCageTile = cage;
                    return !cage.getItem().isEmpty();
                }
            }
        }
        this.cursedCageTile = null;
        return false;
    }
}
