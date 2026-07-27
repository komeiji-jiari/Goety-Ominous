package com.qiuyue.goetyominus.common.mixin;

import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public class RaidMixin {

    @Shadow @Final private ServerLevel level;

    @Shadow public int getGroupsSpawned() { return 0; }

    @Inject(method = "spawnGroup", at = @At("RETURN"))
    private void addCustomRaiders(BlockPos pos, CallbackInfo ci) {
        Raid raid = (Raid)(Object)this;
        int count = 3 + this.level.random.nextInt(5);

        for (int i = 0; i < count; i++) {
            int roll = this.level.random.nextInt(100);
            EntityType<? extends Raider> type;
            if (roll < 40) {
                type = ModEntityTypes.FANATIC.get();
            } else if (roll < 65) {
                type = ModEntityTypes.ZEALOT.get();
            } else if (roll < 85) {
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
    }
}
