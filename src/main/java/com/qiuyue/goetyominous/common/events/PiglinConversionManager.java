package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.MCDEffectOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZGuzzler;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.ZGuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.HostileFungusThrower;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PiglinConversionManager {

    private static final String TAG = "GoetyOminousPiglinConversion";
    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

    private PiglinConversionManager() {
    }

    public static boolean isConvertible(LivingEntity target) {
        return getConvertedType(target) != null;
    }

    public static boolean isConverting(LivingEntity target) {
        return target.getPersistentData().contains(TAG);
    }

    public static boolean start(LivingEntity target, AllyPiglinShaman shaman) {
        if (!isConvertible(target) || isConverting(target)) {
            return false;
        }
        LivingEntity owner = shaman.getTrueOwner();
        if (!(owner instanceof Player)) {
            return false;
        }
        CompoundTag data = new CompoundTag();
        data.putInt("Ticks", 60);
        data.putUUID("Owner", owner.getUUID());
        data.putUUID("Shaman", shaman.getUUID());
        target.getPersistentData().put(TAG, data);
        PENDING.add(target.getUUID());
        playSkillConversionSound(target);
        return true;
    }

    @Nullable
    private static EntityType<? extends Mob> getConvertedType(LivingEntity target) {
        if (target instanceof ZGuzzlerBoss) {
            return null;
        }
        if (target instanceof GuzzlerBoss) {
            return ModEntityTypes.ZGUZZLER.get();
        }
        if (target instanceof HostileFungusThrower) {
            return ModEntityTypes.ZFUNGUS_THROWER_SERVANT.get();
        }
        if (target instanceof AbstractPiglinServant servant) {
            return servant.getZombieVariant();
        }
        if (target instanceof Piglin) {
            return ModEntityType.ZPIGLIN_SERVANT.get();
        }
        if (target instanceof PiglinBrute) {
            return ModEntityType.ZPIGLIN_BRUTE_SERVANT.get();
        }
        return null;
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity living && living.getPersistentData().contains(TAG)) {
            PENDING.add(living.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || PENDING.isEmpty()) {
            return;
        }
        for (UUID uuid : new ArrayList<>(PENDING)) {
            Entity entity = findEntity(server, uuid);
            if (!(entity instanceof LivingEntity target)) {
                continue;
            }
            CompoundTag persistent = target.getPersistentData();
            if (!persistent.contains(TAG)) {
                PENDING.remove(uuid);
                continue;
            }
            CompoundTag data = persistent.getCompound(TAG);
            if (target.isRemoved() || target.isDeadOrDying()) {
                cancel(target);
                continue;
            }
            LivingEntity shaman = findEntity(server, data.getUUID("Shaman")) instanceof LivingEntity living ? living : null;
            if (shaman != null && shaman.isAlive() && target instanceof Mob mob && mob.getTarget() == shaman) {
                cancel(target);
                continue;
            }
            if (target instanceof Mob mob) {
                mob.getNavigation().stop();
                mob.getMoveControl().strafe(0.0F, 0.0F);
            }
            int ticks = data.getInt("Ticks");
            if (ticks % 5 == 0) {
                spawnParticles(target, shaman);
            }
            --ticks;
            if (ticks <= 0) {
                convert(server, target, data, shaman);
            } else {
                data.putInt("Ticks", ticks);
            }
        }
    }

    @Nullable
    private static Entity findEntity(MinecraftServer server, @Nullable UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    private static void spawnParticles(LivingEntity target, @Nullable LivingEntity shaman) {
        spawnParticles(target.level(), target, 8);
        if (shaman != null && shaman.isAlive()) {
            spawnParticles(shaman.level(), shaman, 8);
        }
    }

    private static void spawnBurst(LivingEntity target, @Nullable LivingEntity shaman) {
        spawnParticles(target.level(), target, 24);
        if (shaman != null && shaman.isAlive()) {
            spawnParticles(shaman.level(), shaman, 24);
        }
    }

    private static void spawnParticles(Level level, LivingEntity entity, int totemCount) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(),
                entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                totemCount, entity.getBbWidth() * 0.4D, entity.getBbHeight() * 0.3D, entity.getBbWidth() * 0.4D, 0.01D);
    }

    private static void convert(MinecraftServer server, LivingEntity target, CompoundTag data, @Nullable LivingEntity shaman) {
        EntityType<? extends Mob> type = getConvertedType(target);
        if (type == null) {
            cancel(target);
            return;
        }
        UUID ownerId = data.getUUID("Owner");
        ServerPlayer owner = ownerId == null ? null : server.getPlayerList().getPlayer(ownerId);
        if (target instanceof AbstractPiglinServant servant) {
            SimpleContainer inventory = servant.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    target.spawnAtLocation(stack.copy());
                }
            }
        }
        playSkillConvertedSound(target);
        Entity converted = MobUtil.convertTo(target, type, true, owner);
        boolean success = converted instanceof Mob;
        if (success) {
            Mob mob = (Mob) converted;
            mob.setHealth(mob.getMaxHealth());
            if (owner != null) {
                if (mob instanceof IOwned owned) {
                    owned.setTrueOwner(owner);
                }
            } else if (mob instanceof IOwned owned) {
                owned.setOwnerId(ownerId);
                owned.setOwnerClientId(-1);
            }
            mob.setPersistenceRequired();
            if (mob instanceof Summoned summoned) {
                summoned.setWandering(false);
            }
            if (target instanceof GuzzlerBoss && mob instanceof ZGuzzler zombie) {
                zombie.setUpgrade(ZGuzzler.MAX_UPGRADE_LEVEL, 0);
            } else if (target instanceof Guzzler guzzler && mob instanceof ZGuzzler zombie) {
                zombie.setUpgrade(guzzler.getUpgradeLevel(), guzzler.getUpgradePoints());
            }
            spawnBurst(mob, shaman);
            spawnConvertSoul(mob, shaman);
        }
        if (success && shaman instanceof AllyPiglinShaman ally && ally.isAlive()) {
            ally.setPiglinConvertCooldown(100);
        }
        cancel(target);
    }

    public static void spawnConvertSoul(Mob converted, @Nullable LivingEntity shaman) {
        if (shaman == null || !shaman.isAlive() || shaman.level() != converted.level()) {
            return;
        }
        if (!(converted.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 start = new Vec3(converted.getX(),
                converted.getY() + converted.getBbHeight() * 0.7D, converted.getZ());
        Vec3 end = new Vec3(shaman.getX(),
                shaman.getY() + shaman.getBbHeight() * 0.5D, shaman.getZ());
        Vec3 delta = end.subtract(start);
        int life = Math.min(100, Math.max(20, (int) (delta.length() * 3.0D)));
        MCDEffectOption option = new MCDEffectOption(
                com.qiuyue.goetyominous.common.init.ModParticleTypes.SHAMAN_SOUL.get(), 0.5F, life,
                shaman.getId(), 0.0F, 0.0F, false);
        serverLevel.sendParticles(option, start.x, start.y, start.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    private static void playSkillConversionSound(LivingEntity target) {
        if (target instanceof AbstractPiglinServant servant) {
            servant.playSkillConversionSound();
        } else if (target instanceof GuzzlerBoss boss) {
            boss.playSkillConversionSound();
        } else {
            target.playSound(SoundEvents.PIGLIN_RETREAT, 1.0F, 1.0F);
        }
    }

    private static void playSkillConvertedSound(LivingEntity target) {
        if (target instanceof AbstractPiglinServant servant) {
            servant.playSkillConvertedSound();
        } else if (target instanceof GuzzlerBoss boss) {
            boss.playSkillConvertedSound();
        } else if (target instanceof HostileFungusThrower thrower) {
            thrower.playSkillConvertedSound();
        } else if (target instanceof PiglinBrute) {
            target.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, target.getVoicePitch());
        } else if (target instanceof Piglin) {
            target.playSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, target.getVoicePitch());
        }
    }

    private static void cancel(LivingEntity target) {
        target.getPersistentData().remove(TAG);
        PENDING.remove(target.getUUID());
    }
}
