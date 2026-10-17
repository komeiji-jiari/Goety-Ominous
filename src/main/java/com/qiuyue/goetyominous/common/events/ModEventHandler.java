package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.hostile.HostileBlackWolf;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.HimPlushieRitual;
import com.qiuyue.goetyominous.common.entities.ally.mobs.HeresiarchServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.curios.ScreamingSkullJar;
import com.qiuyue.goetyominous.compat.mod.GoetyAwakenCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Collection;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class ModEventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof ItemEntity itemEntity
                && itemEntity.getItem().is(com.qiuyue.goetyominous.common.init.ModBlocks.PLUSHIE_HIM.get().asItem())) {
            itemEntity.setInvulnerable(true);
            itemEntity.setUnlimitedLifetime();
            return;
        }

        if (entity instanceof LightningBolt bolt) {
            HimPlushieRitual.onLightningStrike((ServerLevel) event.getLevel(), bolt.blockPosition());
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!GoetyAwakenCompat.isLoaded()) {
            return;
        }

        if (!GoetyAwakenCompat.isGoetyAwakenHeresiarchServant(livingEntity)) {
            return;
        }

        try {
            ServerLevel serverLevel = (ServerLevel) event.getLevel();

            HeresiarchServant newServant = ModEntityTypes.HERESIARCH_SERVANT.get().create(serverLevel);

            if (newServant != null) {
                newServant.copyPosition(livingEntity);
                newServant.setYRot(livingEntity.getYRot());
                newServant.setXRot(livingEntity.getXRot());
                newServant.setYHeadRot(livingEntity.getYHeadRot());

                if (livingEntity.hasCustomName()) {
                    newServant.setCustomName(livingEntity.getCustomName());
                    newServant.setCustomNameVisible(livingEntity.isCustomNameVisible());
                }

                if (livingEntity instanceof net.minecraft.world.entity.Mob mob && mob.isPersistenceRequired()) {
                    newServant.setPersistenceRequired();
                }

                if (livingEntity instanceof com.Polarice3.Goety.common.entities.neutral.Owned ownedOriginal) {
                    LivingEntity originalOwner = ownedOriginal.getTrueOwner();
                    if (originalOwner != null) {
                        newServant.setTrueOwner(originalOwner);
                    }
                }

                newServant.setInvulnerable(livingEntity.isInvulnerable());

                serverLevel.addFreshEntity(newServant);

                livingEntity.discard();

                serverLevel.gameEvent(GameEvent.ENTITY_PLACE, newServant.blockPosition(),
                        GameEvent.Context.of(newServant));

                event.setCanceled(true);
            }
        } catch (Exception e) {
            System.err.println("[GoetyOminous] Failed to convert GoetyAwaken HeresiarchServant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity killedEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();

        if (killedEntity instanceof HostileBlackWolf wolf) {
            Entity killer = event.getSource().getDirectEntity();
            if (killer instanceof Player player) {
                boolean boline = player.getMainHandItem().is(
                        com.Polarice3.Goety.common.items.ModItems.WICKED_BOLINE.get())
                        || player.getOffhandItem().is(
                        com.Polarice3.Goety.common.items.ModItems.WICKED_BOLINE.get());
                if (boline && wolf.getRandom().nextFloat() < 0.5F) {
                    event.getDrops().add(new ItemEntity(wolf.level(),
                            wolf.getX(), wolf.getY(), wolf.getZ(),
                            new ItemStack(ModItems.WOLF_TONGUE.get(), 1)));
                }
            }
        }

        com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant piglin = null;
        if (sourceEntity instanceof com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant direct) {
            piglin = direct;
        } else if (sourceEntity instanceof com.Polarice3.Goety.api.entities.IOwned owned
                && owned.getTrueOwner() instanceof com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant owner) {
            piglin = owner;
        }
        if (piglin != null && piglin.getTrueOwner() != null) {
            Collection<ItemEntity> drops = event.getDrops();
            for (ItemEntity item : drops) {
                ItemStack stack = item.getItem();
                if (piglin.getInventory().canAddItem(stack)) {
                    piglin.getInventory().addItem(stack.copyAndClear());
                }
            }
            drops.clear();
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        if (event.getSource().getDirectEntity() instanceof com.Polarice3.Goety.common.entities.projectiles.AcidPool pool
                && pool.getPersistentData().getBoolean(com.qiuyue.goetyominous.common.magic.spells.AcidPoolSpell.FEL_MARKER)) {
            LivingEntity owner = pool.getOwner();
            if (owner != null) {
                int amplifier = 0;
                if (com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneRobe(owner)) {
                    amplifier += 1;
                }
                event.getEntity().addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 200, amplifier), owner);
            }
            return;
        }

        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Player player)) {
            return;
        }

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        if (!livingAttacker.getType().is(EntityTypeTags.RAIDERS)) {
            return;
        }

        Optional<SlotResult> slotResult = CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findFirstCurio(ModItems.SCREAMING_SKULL_JAR.get()))
                .orElse(Optional.empty());

        if (slotResult.isPresent()) {
            ItemStack stack = slotResult.get().stack();
            if (!stack.isEmpty() && stack.getItem() instanceof ScreamingSkullJar) {
                float originalDamage = event.getAmount();
                float reducedDamage = originalDamage * 0.85F;
                event.setAmount(reducedDamage);
            }
        }
    }
}