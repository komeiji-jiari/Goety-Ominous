package com.qiuyue.someillagerservants.common.mixin;

import com.Polarice3.Goety.common.events.IllagerSpawner;
import com.qiuyue.someillagerservants.common.items.curios.ScreamingSkullJar;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.WeakHashMap;

@Mixin(value = IllagerSpawner.class, remap = false)
public abstract class IllagerSpawnerMixin {

    private static final WeakHashMap<ServerPlayer, Boolean> ASSAULT_PREVENTED = new WeakHashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickStart(net.minecraft.server.level.ServerLevel pLevel, CallbackInfoReturnable<Integer> cir) {
        ASSAULT_PREVENTED.clear();
    }

    @Inject(method = "spawnRaider", at = @At("HEAD"), cancellable = true)
    private void beforeSpawnRaider(com.Polarice3.Goety.common.events.IllagerSpawner.IllagerDataType dataType, EntityType<?> entityType, net.minecraft.server.level.ServerLevel worldIn, net.minecraft.core.BlockPos pos, net.minecraft.util.RandomSource random, int soulAmount, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null || player.isSpectator() || player.isCreative()) {
            return;
        }

        if (ASSAULT_PREVENTED.getOrDefault(player, false)) {
            cir.setReturnValue(false);
            return;
        }

        Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof ScreamingSkullJar))
                .orElse(Optional.empty());

        if (slotResult.isPresent()) {
            ItemStack stack = slotResult.get().stack();
            if (stack.getItem() instanceof ScreamingSkullJar && ScreamingSkullJar.hasSkulls(stack)) {
                if (ScreamingSkullJar.consumeSkull(stack)) {
                    ASSAULT_PREVENTED.put(player, true);

                    worldIn.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 2.0F, 0.5F);

                    cir.setReturnValue(false);
                }
            }
        }
    }
}