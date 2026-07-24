package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class StrongZPiglinHunterServant extends ZPiglinHunterServant {

    public StrongZPiglinHunterServant(EntityType<? extends ZPiglinHunterServant> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean isSunBurnTick() { return false; }
}