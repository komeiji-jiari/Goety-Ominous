package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.hostile.illagers.Conquillager;
import com.Polarice3.Goety.common.entities.hostile.illagers.Inquillager;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.illager.InquillagerServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IllagerNecroConversionEvents {
    private static final List<PendingConversion> PENDING_CONVERSIONS = new ArrayList<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof NecroBolt bolt) || !(bolt.getOwner() instanceof LivingEntity owner)) {
            return;
        }
        EntityType<? extends Mob> type;
        if (entity instanceof Inquillager) {
            type = ModEntityTypes.ZINQUILLAGER_SERVANT.get();
        } else if (entity instanceof Conquillager) {
            type = ModEntityTypes.SKELETON_CONQUILLAGER_SERVANT.get();
        } else {
            return;
        }
        PENDING_CONVERSIONS.add(new PendingConversion((Mob) entity, owner, type));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING_CONVERSIONS.isEmpty()) {
            return;
        }
        Iterator<PendingConversion> iterator = PENDING_CONVERSIONS.iterator();
        while (iterator.hasNext()) {
            PendingConversion pending = iterator.next();
            iterator.remove();
            convert(pending);
        }
    }

    private static void convert(PendingConversion pending) {
        Mob entity = pending.entity;
        if (entity.isRemoved() || !ForgeEventFactory.canLivingConvert(entity, pending.type, timer -> {})) {
            return;
        }
        Mob mob = entity.convertTo(pending.type, true);
        if (mob == null) {
            return;
        }
        if (mob instanceof IOwned owned) {
            owned.setTrueOwner(pending.owner);
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()),
                    MobSpawnType.CONVERSION, null, null);
        }
        if (entity instanceof Inquillager inquillager && mob instanceof InquillagerServant servant) {
            servant.setOutfitType(inquillager.getOutfitType());
        }
        ForgeEventFactory.onLivingConvert(entity, mob);
        if (!mob.isSilent()) {
            mob.level().levelEvent(null, 1026, mob.blockPosition(), 0);
        }
    }

    private static final class PendingConversion {
        private final Mob entity;
        private final LivingEntity owner;
        private final EntityType<? extends Mob> type;

        private PendingConversion(Mob entity, LivingEntity owner, EntityType<? extends Mob> type) {
            this.entity = entity;
            this.owner = owner;
            this.type = type;
        }
    }
}
