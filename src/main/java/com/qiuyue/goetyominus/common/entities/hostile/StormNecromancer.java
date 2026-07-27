package com.qiuyue.goetyominus.common.entities.hostile;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractStormNecromancer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class StormNecromancer extends AbstractStormNecromancer implements Enemy {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(StormNecromancer.class, EntityDataSerializers.INT);
    private final ModServerBossInfo bossInfo;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/storm_necromancer/storm_necromancer.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/storm_necromancer/storm_necromancer2.png"));
    });

    public StormNecromancer(EntityType<? extends AbstractStormNecromancer> type, Level level) {
        super(type, level);
        this.setHostile(true);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public void setCustomName(@javax.annotation.Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber()) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber() {
        return TEXTURE_BY_TYPE.size();
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.bossInfo.removeAllPlayers();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        if (this.level() instanceof ServerLevel serverLevel) {
            this.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get()));

            int boneCount = 1 + this.random.nextInt(2) + pLooting;
            for (int i = 0; i < boneCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.BONE));
            }

            int ectoplasmCount = this.random.nextInt(2) + pLooting;
            for (int i = 0; i < ectoplasmCount; i++) {
                this.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get()));
            }

            int copperCount = 8 + this.random.nextInt(4) + pLooting;
            for (int i = 0; i < copperCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.COPPER_INGOT));
            }

            int diamondCount = 2 + this.random.nextInt(2) + pLooting;
            for (int i = 0; i < diamondCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.DIAMOND));
            }

            if (pRecentlyHit) {
                this.spawnAtLocation(new ItemStack(com.qiuyue.goetyominus.common.items.ModItems.BROKEN_STORM_CROWN.get()));
            }

            double forbiddenChance = 0.25D + pLooting * 0.1D;
            if (this.random.nextDouble() < forbiddenChance) {
                this.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.FORBIDDEN_FRAGMENT.get()));
            }
        }
    }
}
