package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.GOCultistHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public abstract class AbstractGOCultist extends Cultist {

    private static final EntityDataAccessor<Float> DATA_REINFORCEMENT_CHANCE =
            SynchedEntityData.defineId(AbstractGOCultist.class, EntityDataSerializers.FLOAT);

    protected AbstractGOCultist(EntityType<? extends AbstractGOCultist> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 10;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_REINFORCEMENT_CHANCE, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("ReinforcementChance", this.getReinforcementChance());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ReinforcementChance")) {
            this.setReinforcementChance(compound.getFloat("ReinforcementChance"));
        }
    }

    public void setReinforcementChance(float chance) {
        this.entityData.set(DATA_REINFORCEMENT_CHANCE, chance);
    }

    public float getReinforcementChance() {
        return this.entityData.get(DATA_REINFORCEMENT_CHANCE);
    }

    protected void randomizeReinforcementsChance() {
        this.setReinforcementChance((float) (this.random.nextDouble() * 0.1D));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isPassenger() && this.getVehicle() instanceof Monster) {
            if (source == this.damageSources().inWall()) {
                return false;
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this instanceof ICultist) {
                LivingEntity livingentity = this.getTarget();
                if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                    livingentity = (LivingEntity) source.getEntity();
                }

                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY());
                int k = Mth.floor(this.getZ());

                if (livingentity != null
                        && source.getEntity() instanceof Player
                        && this.level().getDifficulty() == Difficulty.HARD
                        && this.random.nextFloat() < this.getReinforcementChance()
                        && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {

                    AbstractGOCultist cultist = null;
                    int rand = this.random.nextInt(4);
                    cultist = switch (rand) {
                        case 0 -> ModEntityTypes.BELDAM.get().create(serverLevel);
                        case 1 -> ModEntityTypes.FANATIC.get().create(serverLevel);
                        case 2 -> ModEntityTypes.ZEALOT.get().create(serverLevel);
                        case 3 -> ModEntityTypes.THUG.get().create(serverLevel);
                        default -> null;
                    };

                    if (cultist != null) {
                        for (int l = 0; l < 50; ++l) {
                            int i1 = i + Mth.nextInt(this.random, 7, 16) * Mth.nextInt(this.random, -1, 1);
                            int k1 = k + Mth.nextInt(this.random, 7, 16) * Mth.nextInt(this.random, -1, 1);
                            BlockPos blockpos = serverLevel.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(i1, j, k1));
                            EntityType<?> entitytype = cultist.getType();
                            if (SpawnPlacements.checkSpawnRules(entitytype, serverLevel, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) {
                                cultist.setPos(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);
                                if (!this.level().hasNearbyAlivePlayer(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 7.0D)
                                        && this.level().isUnobstructed(cultist)
                                        && this.level().noCollision(cultist)
                                        && !this.level().containsAnyLiquid(cultist.getBoundingBox())) {
                                    cultist.setTarget(livingentity);
                                    cultist.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(cultist.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                                    serverLevel.addFreshEntityWithPassengers(cultist);
                                    this.setReinforcementChance(this.getReinforcementChance() - 0.05F);
                                    if (cultist instanceof ICultist) {
                                        cultist.setReinforcementChance(cultist.getReinforcementChance() - 0.05F);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        this.alertWitches();
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this instanceof ICultist) {
            this.conversion();
        }
        if (this.getReinforcementChance() < 0.0F) {
            this.setReinforcementChance(0.0F);
        }
    }

    public void conversion() {
        if (MobsConfig.CultistSpread.get()) {
            int timer = 1200;
            if (this.hasActiveRaid()) {
                timer = 400;
            }
            if (this.tickCount % timer == 0) {
                GOCultistHelper.secretConversion(this);
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (this instanceof ICultist) {
            this.randomizeReinforcementsChance();
            if (this.random.nextFloat() < difficultyIn.getSpecialMultiplier() * 0.05F) {
                this.setReinforcementChance(
                        this.getReinforcementChance() + (float) (this.random.nextDouble() * 0.25D + 0.5D)
                );
            }
        }

        if ((reason == MobSpawnType.NATURAL || reason == MobSpawnType.STRUCTURE)
                && worldIn.getLevel().dimension() == Level.NETHER) {
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        }

        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        ItemStack head = this.getItemBySlot(EquipmentSlot.HEAD);
        if (!head.isEmpty() && ItemStack.matches(head, Raid.getLeaderBannerInstance())) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
        return data;
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        } else if (entityIn instanceof Witch) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof AbstractGOCultist) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof AbstractPiglin) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof Raider) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        return false;
    }

    public static boolean spawnCultistsRules(EntityType<?> pType, ServerLevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, java.util.Random pRandom) {
        return pLevel.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pPos) <= 8
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && (pReason == MobSpawnType.SPAWNER || pLevel.getBlockState(pPos.below()).isValidSpawn(pLevel, pPos.below(), pType));
    }
}
