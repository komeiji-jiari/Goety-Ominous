package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractHurricane;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.init.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class Hurricane extends AbstractHurricane {
    private final ModServerBossInfo bossInfo;

    public Hurricane(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.WHITE, false, false);
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false,
                entity -> entity instanceof IOwned owned && !owned.isHostile() && owned.getTrueOwner() instanceof Player));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
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

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean hitByPlayer) {
        super.dropFromLootTable(source, hitByPlayer);
        if (!this.isOminousBuffed() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LootTable loottable = serverLevel.getServer().getLootData().getLootTable(this.getLootTable());
        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());
        if (hitByPlayer && this.lastHurtByPlayer != null) {
            builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }
        LootParams params = builder.create(LootContextParamSets.ENTITY);
        for (int i = 0; i < 1; i++) {
            for (ItemStack stack : loottable.getRandomItems(params, this.getLootTableSeed())) {
                if (!stack.is(ModBlocks.HEAVY_CORE.get().asItem())) {
                    this.spawnAtLocation(stack);
                }
            }
        }
        this.spawnAtLocation(new ItemStack(Items.DIAMOND, 3 + this.random.nextInt(3)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount % 5 == 0) {
                this.bossInfo.update();
            }
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }
}
