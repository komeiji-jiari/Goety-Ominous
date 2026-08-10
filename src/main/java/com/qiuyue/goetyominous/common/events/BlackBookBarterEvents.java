package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.ally.illager.cultist.WitchServant;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ai.BlackBookBarterGoal;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlackBookBarterEvents {

    public static final String BARTER_TIMER = "GoetyOminousBarterTimer";
    public static final String BARTER_TRADER = "GoetyOminousBarterTrader";
    private static final int BARTER_DURATION = 100;

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getSide() != LogicalSide.SERVER) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        if (!(event.getTarget() instanceof Witch)
                && !(event.getTarget() instanceof Crone)
                && !(event.getTarget() instanceof WitchServant)) return;
        if (event.getTarget() instanceof WitchServant witchServant) {
            if (witchServant.getTrueOwner() != player && !CuriosFinder.isWitchFriendly(player)) return;
        } else {
            if (!CuriosFinder.isWitchFriendly(player)) return;
        }
        if (!isBlackBook(event.getItemStack())) return;
        if (player instanceof ServerPlayer serverPlayer && hasAchievement(serverPlayer)) return;

        LivingEntity target = (LivingEntity) event.getTarget();
        if (!target.getMainHandItem().isEmpty() || !target.getOffhandItem().isEmpty()) return;
        if (target.getPersistentData().getInt(BARTER_TIMER) > 0) return;

        ItemStack held = event.getItemStack();
        ItemStack book = held.copy();
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        target.setItemSlot(EquipmentSlot.OFFHAND, book);
        target.getPersistentData().putInt(BARTER_TIMER, BARTER_DURATION);
        target.getPersistentData().putUUID(BARTER_TRADER, player.getUUID());
        ((Mob) target).goalSelector.addGoal(1, new BlackBookBarterGoal((Mob) target));

        SoundEvent celebrate;
        if (target instanceof Witch) {
            celebrate = ((Witch) target).getCelebrateSound();
        } else if (target instanceof Crone) {
            celebrate = ((Crone) target).getCelebrateSound();
        } else {
            celebrate = ((WitchServant) target).getCelebrateSound();
        }
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                celebrate, SoundSource.HOSTILE, 1.0F, 1.0F);

        if (target.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; ++i) {
                double d0 = target.getRandom().nextGaussian() * 0.02;
                double d1 = target.getRandom().nextGaussian() * 0.02;
                double d2 = target.getRandom().nextGaussian() * 0.02;
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        target.getRandomX(1.0), target.getRandomY(), target.getRandomZ(1.0),
                        0, d0, d1, d2, 1.0);
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Witch)
                && !(event.getEntity() instanceof Crone)
                && !(event.getEntity() instanceof WitchServant)) return;

        LivingEntity entity = event.getEntity();
        CompoundTag data = entity.getPersistentData();
        int timer = data.getInt(BARTER_TIMER);
        if (timer <= 0) return;

        Mob mob = (Mob) entity;
        Player trader = data.hasUUID(BARTER_TRADER)
                ? entity.level().getPlayerByUUID(data.getUUID(BARTER_TRADER))
                : null;

        if (mob.hurtTime > 0 || (trader != null && mob.getTarget() == trader)) {
            dropBlackBook(mob);
            data.remove(BARTER_TIMER);
            data.remove(BARTER_TRADER);
            mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            return;
        }

        mob.setTarget(null);
        mob.getNavigation().stop();
        if (trader != null) {
            mob.getLookControl().setLookAt(trader, 30.0F, 30.0F);
        }

        timer--;
        if (timer <= 0) {
            throwBackBlackBook(mob);
            if (trader instanceof ServerPlayer serverTrader) {
                grantAchievement(serverTrader);
            }
            data.remove(BARTER_TIMER);
            data.remove(BARTER_TRADER);
            mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        } else {
            data.putInt(BARTER_TIMER, timer);
        }
    }

    private static void throwBackBlackBook(Mob mob) {
        ItemStack book = mob.getItemBySlot(EquipmentSlot.OFFHAND);
        if (book.isEmpty() || mob.level().isClientSide) return;
        if (mob.level() instanceof ServerLevel serverLevel) {
            ItemEntity item = new ItemEntity(serverLevel, mob.getX(), mob.getY() + 1.0, mob.getZ(), book);
            Player trader = mob.getPersistentData().hasUUID(BARTER_TRADER)
                    ? serverLevel.getPlayerByUUID(mob.getPersistentData().getUUID(BARTER_TRADER))
                    : null;
            if (trader != null) {
                Vec3 to = trader.position().subtract(mob.position()).normalize().scale(0.5D);
                item.setDeltaMovement(to.x, 0.3D, to.z);
            }
            serverLevel.addFreshEntity(item);
        }
    }

    private static void dropBlackBook(Mob mob) {
        ItemStack book = mob.getItemBySlot(EquipmentSlot.OFFHAND);
        if (book.isEmpty() || mob.level().isClientSide) return;
        if (mob.level() instanceof ServerLevel serverLevel) {
            ItemEntity item = new ItemEntity(serverLevel, mob.getX(), mob.getY() + 0.5, mob.getZ(), book);
            serverLevel.addFreshEntity(item);
        }
    }

    private static boolean isBlackBook(ItemStack stack) {
        return stack.getTag() != null
                && "goety:black_book".equals(stack.getTag().getString("patchouli:book"));
    }

    private static boolean hasAchievement(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;
        Advancement advancement = server.getAdvancements()
                .getAdvancement(new ResourceLocation("goetyominous", "goetyominous/witch_lesson"));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static void grantAchievement(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        Advancement advancement = server.getAdvancements()
                .getAdvancement(new ResourceLocation("goetyominous", "goetyominous/witch_lesson"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "traded");
        }
    }
}