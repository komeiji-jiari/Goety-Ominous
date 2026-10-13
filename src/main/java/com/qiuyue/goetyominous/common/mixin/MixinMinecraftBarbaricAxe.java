package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.ac.BarbaricAxeSwingClient;
import com.qiuyue.goetyominous.common.items.ac.BarbaricAxeItem;
import com.qiuyue.goetyominous.common.items.ac.BarbaricAxeSwingGate;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftBarbaricAxe {

    @Inject(method = "startAttack", at = @At("HEAD"), require = 1)
    private void goetyominous$barbaricAxeClickSwing(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof BarbaricAxeItem)) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        boolean block = hit != null && hit.getType() == HitResult.Type.BLOCK;
        BarbaricAxeSwingGate.setBlockSwing(block);
        if (block || player.isHandsBusy()) {
            return;
        }
        if (!WeaponConfig.AnimSwingOnEveryClick.get()) {
            return;
        }
        BarbaricAxeSwingClient.start(player.getId(), BarbaricAxeSwingClient.TYPE_SWEEP);
    }
}
