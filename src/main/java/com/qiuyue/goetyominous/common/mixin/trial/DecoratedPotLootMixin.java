package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.blocks.trial.PotLootHolder;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotLootMixin implements PotLootHolder {
    @Unique
    @Nullable
    private ResourceLocation goetyominous$lootTable;
    @Unique
    private long goetyominous$lootTableSeed;

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void goetyominous$saveLootTable(CompoundTag tag, CallbackInfo ci) {
        if (this.goetyominous$lootTable != null) {
            tag.putString("LootTable", this.goetyominous$lootTable.toString());
            tag.putLong("LootTableSeed", this.goetyominous$lootTableSeed);
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void goetyominous$loadLootTable(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("LootTable", 8)) {
            this.goetyominous$lootTable = ResourceLocation.tryParse(tag.getString("LootTable"));
            this.goetyominous$lootTableSeed = tag.getLong("LootTableSeed");
        }
    }

    @Override
    public void goetyominous$unpackLoot(ServerLevel level, BlockPos pos, @Nullable Player player) {
        ResourceLocation table = this.goetyominous$lootTable;
        if (table == null) {
            return;
        }
        this.goetyominous$lootTable = null;
        LootTable lootTable = level.getServer().getLootData().getLootTable(table);
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, table);
        }
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
        if (player != null) {
            builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
        }
        for (ItemStack stack : lootTable.getRandomItems(builder.create(LootContextParamSets.CHEST), this.goetyominous$lootTableSeed)) {
            if (!stack.isEmpty()) {
                Block.popResource(level, pos, stack);
            }
        }
    }
}
