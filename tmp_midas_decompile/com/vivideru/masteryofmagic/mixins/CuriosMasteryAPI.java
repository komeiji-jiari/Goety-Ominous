/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.utils.CuriosFinder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.utils.CuriosFinder;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={CuriosFinder.class})
public class CuriosMasteryAPI {
    private static boolean gmom_isWearingCarvedPumpkin(Player p) {
        ItemStack head = p.m_150109_().m_36052_(3);
        return !head.m_41619_() && head.m_150930_(Items.f_42047_);
    }

    @Inject(method={"hasWildCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasWildCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.WILD) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasWildRobe"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasWildRobe(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.WILD) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasFrostCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasFrostCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.FROST) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasFrostRobes"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasFrostRobes(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.FROST) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasAbyssCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasAbyssCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.DEEP) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasAbyssRobes"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasAbyssRobes(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.DEEP) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasVoidCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasVoidCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.END) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasVoidRobe"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasVoidRobe(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.END) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasUnholyHat"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasNetherCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NETHER) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasUnholyRobe"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasNetherRobe(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NETHER) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasNamelessCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasNamelessCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NECROMANCY) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasNamelessCape"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasNamelessCape(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NECROMANCY) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasNamelessSet"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasNamelessSet(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NECROMANCY) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasUndeadCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasUndeadCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NECROMANCY) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasUndeadCape"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasUndeadCape(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.NECROMANCY) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasWindyRobes"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasWindyRobes(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.SKY) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasWindCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasWindCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.SKY) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasStormCrown"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasStormCrown(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.STORM) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasStormRobes"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasStormRobes(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.STORM) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasGeoRobe"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasGeoRobe(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.GEOTURGY) >= 3) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Inject(method={"hasAmethystNecklace"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void gmom_hasAmethystNecklace(LivingEntity e, CallbackInfoReturnable<Boolean> cir) {
        Player p;
        if (e instanceof Player && !CuriosMasteryAPI.gmom_isWearingCarvedPumpkin(p = (Player)e) && MasteryData.get(p, MasteryData.MasteryId.GEOTURGY) >= 2) {
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }
}

