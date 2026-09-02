package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.entities.MutantShulker;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.mm.MmItems;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.ExplosionEvent;
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

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) return;
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getDirectEntity() instanceof MutantShulkerServantBullet) {
            LivingEntity master = getMasterOwner(event.getSource());
            if (master instanceof Player player) {
                event.getEntity().setLastHurtByPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) return;
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof MutantShulker)) return;
        Entity killer = event.getSource().getDirectEntity();
        if (killer instanceof Player player
                && (player.getMainHandItem().is(ModItems.WICKED_BOLINE.get())
                || player.getOffhandItem().is(ModItems.WICKED_BOLINE.get()))) {
            event.getDrops().add(new ItemEntity(event.getEntity().level(),
                    event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                    new ItemStack(MmItems.SHULKER_EMBRYO.get())));
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion() instanceof com.alexander.mutantmore.explosions.CustomExplosion) {
            event.getAffectedEntities().removeIf(e -> e instanceof ItemEntity || e instanceof ExperienceOrb);
        }
    }

    private static LivingEntity getMasterOwner(DamageSource source) {
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof MutantShulkerServantTrap trap) {
            return trap.getOwner();
        }
        if (attacker instanceof MutantShulkerServantBullet bullet) {
            LivingEntity shooter = bullet.getOwner();
            if (shooter instanceof IOwned owned) {
                return owned.getTrueOwner();
            }
            return shooter;
        }
        if (attacker instanceof MutantShulkerServant servant) {
            return servant.getTrueOwner();
        }
        return null;
    }
}
