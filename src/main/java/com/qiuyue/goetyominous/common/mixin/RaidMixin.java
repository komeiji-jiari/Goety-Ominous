package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.CrimsonGuard;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ImperialChasseur;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public class RaidMixin {

    @Shadow @Final private ServerLevel level;

    @Shadow public int getGroupsSpawned() { return 0; }

    @Unique private int goetyominous$triggerSe;
    @Unique private boolean goetyominous$hasTrigger;

    @Inject(method = "absorbBadOmen", at = @At("HEAD"))
    private void goetyominous$captureTriggerSe(Player player, CallbackInfo ci) {
        if (!this.goetyominous$hasTrigger) {
            this.goetyominous$triggerSe = SEHelper.getSoulAmountInt(player);
            this.goetyominous$hasTrigger = true;
        }
    }

    @Inject(method = "spawnGroup", at = @At("RETURN"))
    private void addCustomRaiders(BlockPos pos, CallbackInfo ci) {
        Raid raid = (Raid)(Object)this;
        int count = 3 + this.level.random.nextInt(5);

        for (int i = 0; i < count; i++) {
            int roll = this.level.random.nextInt(100);
            EntityType<? extends Raider> type;
            if (roll < 50) {
                type = ModEntityTypes.FANATIC.get();
            } else if (roll < 80) {
                type = ModEntityTypes.ZEALOT.get();
            } else if (roll < 90) {
                type = ModEntityTypes.THUG.get();
            } else if (roll < 95) {
                type = ModEntityTypes.BELDAM.get();
            } else {
                type = ModEntityTypes.CHANNELLER.get();
            }

            Raider raider = type.create(this.level);
            if (raider != null) {
                raider.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                raider.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(pos),
                        MobSpawnType.EVENT, null, null);
                this.level.addFreshEntity(raider);
            }
        }

        int wave = this.getGroupsSpawned();
        if (wave >= 6 && this.goetyominous$hasTrigger && this.goetyominous$triggerSe >= 70000) {
            int guards = 1 + this.level.random.nextInt(2);
            for (int i = 0; i < guards; i++) {
                CrimsonGuard guard = ModEntityTypes.CRIMSON_GUARD.get().create(this.level);
                if (guard != null) {
                    raid.joinRaid(wave, guard, pos, false);
                }
            }

            int chasseurs = 1 + this.level.random.nextInt(2);
            for (int i = 0; i < chasseurs; i++) {
                ImperialChasseur chasseur = ModEntityTypes.IMPERIAL_CHASSEUR.get().create(this.level);
                if (chasseur != null) {
                    raid.joinRaid(wave, chasseur, pos, false);
                }
            }
        }
    }
}
