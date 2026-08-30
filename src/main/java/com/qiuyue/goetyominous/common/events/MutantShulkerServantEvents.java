package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class MutantShulkerServantEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) return;
        if (event.getEntity().level().isClientSide) return;
        LivingEntity master = getMasterOwner(event.getSource());
        if (master == null) return;
        Entity hurt = event.getEntity();
        if (hurt == master) {
            event.setCanceled(true);
        } else if (MobUtil.areAllies(hurt, master)) {
            event.setCanceled(true);
        } else if (hurt instanceof IOwned owned && owned.getTrueOwner() == master) {
            event.setCanceled(true);
        }
    }

    private static LivingEntity getMasterOwner(DamageSource source) {
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof MutantShulkerServantTrap trap) {
            return trap.getOwner();
        }
        if (attacker instanceof MutantShulkerServantBullet bullet) {
            Entity shooter = bullet.getOwner();
            if (shooter instanceof IOwned owned) {
                return owned.getTrueOwner();
            }
            return shooter instanceof LivingEntity living ? living : null;
        }
        if (attacker instanceof MutantShulkerServant servant) {
            return servant.getTrueOwner();
        }
        return null;
    }
}
