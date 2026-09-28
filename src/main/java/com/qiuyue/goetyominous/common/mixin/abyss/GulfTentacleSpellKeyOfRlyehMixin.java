package com.qiuyue.goetyominous.common.mixin.abyss;

import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import com.qiuyue.goetyominous.utils.KeyOfRlyehMixinHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(GulfTentacle.class)
public class GulfTentacleSpellKeyOfRlyehMixin {

    @Unique
    private static final String GOETYOMINOUS_KEY = "GoetyOminousWhipKey";
    @Unique
    private static final String GOETYOMINOUS_DEPTH = "GoetyOminousWhipDepth";
    @Unique
    private static final String GOETYOMINOUS_DONE = "GoetyOminousWhipDone";

    @Unique
    private GulfTentacle goetyominous$spawnedTentacle;

    @Unique
    private static boolean goetyominous$isKeyChain(GulfTentacle tentacle) {
        return tentacle.getPersistentData().getBoolean(GOETYOMINOUS_KEY);
    }

    @Unique
    private static int goetyominous$getDepth(GulfTentacle tentacle) {
        return tentacle.getPersistentData().getInt(GOETYOMINOUS_DEPTH);
    }

    @Inject(method = "setStaff", at = @At("HEAD"), remap = false)
    private void goetyominous$markRoot(boolean staff, CallbackInfo ci) {
        if (!staff) {
            return;
        }
        GulfTentacle self = (GulfTentacle) (Object) this;
        if (goetyominous$isKeyChain(self)) {
            return;
        }
        boolean isKey = self.getTrueOwner() != null
                && KeyOfRlyehMixinHelper.isKeyOfRlyeh(self.getTrueOwner().getMainHandItem());
        self.getPersistentData().putBoolean(GOETYOMINOUS_KEY, isKey);
        self.getPersistentData().putInt(GOETYOMINOUS_DEPTH, 0);
    }

    @ModifyVariable(method = "damageEntities", at = @At("STORE"),
            name = "gulfTentacle", require = 1, remap = false)
    private GulfTentacle goetyominous$recordSpawned(GulfTentacle gulfTentacle) {
        this.goetyominous$spawnedTentacle = gulfTentacle;
        return gulfTentacle;
    }

    @Inject(method = "damageEntities", at = @At("RETURN"), remap = false)
    private void goetyominous$keyOfRlyehChain(Set<LivingEntity> entities, CallbackInfo ci) {
        GulfTentacle self = (GulfTentacle) (Object) this;
        GulfTentacle spawned = this.goetyominous$spawnedTentacle;
        this.goetyominous$spawnedTentacle = null;

        if (spawned == null || !goetyominous$isKeyChain(self)) {
            return;
        }
        if (self.getPersistentData().getBoolean(GOETYOMINOUS_DONE)) {
            return;
        }

        int childDepth = goetyominous$getDepth(self) + 1;
        if (childDepth > 2) {
            return;
        }
        self.getPersistentData().putBoolean(GOETYOMINOUS_DONE, true);
        spawned.getPersistentData().putBoolean(GOETYOMINOUS_KEY, true);
        spawned.getPersistentData().putInt(GOETYOMINOUS_DEPTH, childDepth);
        spawned.setStaff(true);
    }
}
