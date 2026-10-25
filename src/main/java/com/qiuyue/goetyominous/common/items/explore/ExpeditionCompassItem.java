package com.qiuyue.goetyominous.common.items.explore;

import com.Polarice3.Goety.api.entities.IOwned;
import com.qiuyue.goetyominous.common.explore.ExpeditionData;
import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import com.qiuyue.goetyominous.common.explore.IExpeditionLootHolder;
import com.qiuyue.goetyominous.common.explore.TravelerStatus;
import com.qiuyue.goetyominous.config.ExpeditionConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ExpeditionCompassItem extends Item {
    private static final String BOUND_TAG = "Bound";

    public ExpeditionCompassItem(Properties properties) {
        super(properties);
    }

    protected abstract boolean acceptsTraveler(LivingEntity entity);

    public boolean canTravelTo(LivingEntity traveler, ServerLevel destination) {
        return true;
    }

    protected UUID getOwnerId(LivingEntity entity) {
        return entity instanceof IOwned owned ? owned.getOwnerId() : null;
    }

    protected boolean isLimitedLife(LivingEntity entity) {
        return entity instanceof IOwned owned && owned.isLimitedLife();
    }

    protected int getBindLimit() {
        return ExpeditionConfig.CompassBindLimit.get();
    }

    public TravelerStatus evaluate(ServerPlayer player, Entity entity) {
        return this.evaluate(player, entity, null);
    }

    public TravelerStatus evaluate(ServerPlayer player, Entity entity, ServerLevel destination) {
        if (entity == null) {
            return TravelerStatus.UNLOADED;
        }
        if (entity.isRemoved() || !entity.isAlive()) {
            return TravelerStatus.DEAD;
        }
        if (!(entity instanceof LivingEntity living) || !acceptsTraveler(living)) {
            return TravelerStatus.UNSUPPORTED;
        }
        UUID ownerId = getOwnerId(living);
        if (ownerId == null || !ownerId.equals(player.getUUID())) {
            return TravelerStatus.NOT_OWNER;
        }
        if (isLimitedLife(living)) {
            return TravelerStatus.LIMITED_LIFE;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return TravelerStatus.UNLOADED;
        }
        ExpeditionData data = ExpeditionData.get(server);
        if (data.isDeadBinding(living.getUUID())) {
            return TravelerStatus.DEAD;
        }
        if (data.isInAnyExpedition(living.getUUID())) {
            return TravelerStatus.IN_EXPEDITION;
        }
        if (!living.level().dimension().equals(player.level().dimension())) {
            return TravelerStatus.TOO_FAR;
        }
        int radius = ExpeditionConfig.DispatchRadius.get();
        if (living.distanceToSqr(player) > (double) radius * (double) radius) {
            return TravelerStatus.TOO_FAR;
        }
        if (living.isPassenger() || living.isVehicle()) {
            return TravelerStatus.BUSY;
        }
        if (living instanceof IExpeditionLootHolder holder && holder.hasExpeditionLoot()) {
            return TravelerStatus.PENDING_LOOT;
        }
        if (destination != null && !this.canTravelTo(living, destination)) {
            return TravelerStatus.NO_IMMUNITY;
        }
        return TravelerStatus.READY;
    }

    public boolean reconcile(ItemStack stack, ServerPlayer player) {
        List<UUID> bound = getBound(stack);
        if (bound.isEmpty()) {
            return false;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }
        ExpeditionData data = ExpeditionData.get(server);
        List<UUID> resolved = new ArrayList<>(bound.size());
        boolean changed = false;
        for (UUID id : bound) {
            UUID current = data.resolveRemap(id);
            if (!current.equals(id)) {
                changed = true;
            }
            if (data.isDeadBinding(current)) {
                changed = true;
                continue;
            }
            if (data.isInActiveSnapshot(current)) {
                if (resolved.contains(current)) {
                    changed = true;
                } else {
                    resolved.add(current);
                }
                continue;
            }
            Entity loaded = findLoaded(server, current);
            if (loaded != null && (loaded.isRemoved() || !loaded.isAlive())) {
                changed = true;
                continue;
            }
            if (resolved.contains(current)) {
                changed = true;
            } else {
                resolved.add(current);
            }
        }
        if (changed) {
            setBound(stack, resolved);
            player.inventoryMenu.broadcastChanges();
        }
        return changed;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !getBound(stack).isEmpty();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!acceptsTraveler(target)) {
            return InteractionResult.PASS;
        }
        UUID ownerId = getOwnerId(target);
        if (ownerId == null || !ownerId.equals(player.getUUID())) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;}
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        ItemStack held = serverPlayer.getItemInHand(hand);
        if (!(held.getItem() instanceof ExpeditionCompassItem)) {
            return InteractionResult.PASS;
        }
        reconcile(held, serverPlayer);
        List<UUID> bound = new ArrayList<>(getBound(held));
        if (bound.remove(target.getUUID())) {
            setBound(held, bound);
            player.displayClientMessage(Component.translatable("message.goetyominous.traveler_compass.unbound"), true);
        } else {
            int limit = getBindLimit();
            if (bound.size() >= limit) {
                player.displayClientMessage(Component.translatable("message.goetyominous.traveler_compass.full", limit), true);
                return InteractionResult.SUCCESS;
            }
            bound.add(target.getUUID());
            setBound(held, bound);
            player.displayClientMessage(Component.translatable("message.goetyominous.traveler_compass.bound"), true);
            playBindEffects(target);
        }
        serverPlayer.inventoryMenu.broadcastChanges();
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ExpeditionViewSessions.open(serverPlayer, hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || level.getGameTime() % 20L != 0L) {
            return;
        }
        reconcile(stack, player);
    }

    protected void playBindEffects(LivingEntity target) {
        target.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
        if (target.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 7; i++) {
                double dx = target.getRandom().nextGaussian() * 0.02D;
                double dy = target.getRandom().nextGaussian() * 0.02D;
                double dz = target.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        target.getRandomX(1.0D), target.getRandomY() + 0.5D, target.getRandomZ(1.0D),
                        0, dx, dy, dz, 0.5D);
            }
        }
    }

    public static List<UUID> getBound(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return List.of();
        }
        ListTag list = stack.getTag().getList(BOUND_TAG, Tag.TAG_STRING);
        if (list.isEmpty()) {
            return List.of();
        }
        List<UUID> out = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            try {
                out.add(UUID.fromString(list.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return out;
    }

    public static void setBound(ItemStack stack, List<UUID> uuids) {
        if (uuids.isEmpty()) {
            if (stack.hasTag()) {
                stack.getTag().remove(BOUND_TAG);
            }
            return;
        }
        ListTag list = new ListTag();
        for (UUID uuid : uuids) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        stack.getOrCreateTag().put(BOUND_TAG, list);
    }

    private static Entity findLoaded(MinecraftServer server, UUID uuid) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }
}
