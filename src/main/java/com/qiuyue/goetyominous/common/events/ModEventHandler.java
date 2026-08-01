package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.HeresiarchServant;
import com.qiuyue.goetyominous.common.entities.ally.sar.ExecutionerServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.curios.ScreamingSkullJar;
import com.qiuyue.goetyominous.compat.ias.IasItems;
import com.qiuyue.goetyominous.compat.mod.GoetyAwakenCompat;
import com.qiuyue.goetyominous.compat.mod.IllageAndSpillageCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
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
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            return;
        }

        LivingEntity entity = event.getEntity();

        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (entityId == null || !entityId.getPath().equals("magispeller")) {
            return;
        }

        ItemStack contractStack = new ItemStack(IasItems.MYSTERIOUS_CONTRACT.get());
        ItemEntity itemEntity = entity.spawnAtLocation(contractStack);
        if (itemEntity != null) {
            itemEntity.setExtendedLifetime();
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity killedEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();

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
            return;
        }

        if (!(sourceEntity instanceof ExecutionerServant executionerServant)) {
            return;
        }

        if (!killedEntity.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
            return;
        }

        EntityType<?> entityType = killedEntity.getType();
        ItemStack headStack = null;
        float dropChance = 0.5F;

        if (entityType == EntityType.SKELETON) {
            headStack = new ItemStack(Items.SKELETON_SKULL);
        } else if (entityType == EntityType.ZOMBIE) {
            headStack = new ItemStack(Items.ZOMBIE_HEAD);
        } else if (entityType == EntityType.CREEPER) {
            headStack = new ItemStack(Items.CREEPER_HEAD);
        } else if (entityType == EntityType.WITHER_SKELETON) {
            headStack = new ItemStack(Items.WITHER_SKELETON_SKULL);
        } else if (entityType == EntityType.PIGLIN) {
            headStack = new ItemStack(Items.PIGLIN_HEAD);
        } else if (entityType.is(EntityTypeTags.RAIDERS)) {
            headStack = new ItemStack(ModBlocks.TALL_SKULL_ITEM.get());
            dropChance = 0.75F;
        } else if (killedEntity instanceof Villager) {
            headStack = new ItemStack(ModBlocks.TALL_SKULL_ITEM.get());
            dropChance = 1.0F;
        } else if (entityType == EntityType.GOAT) {
            headStack = new ItemStack(Items.GOAT_HORN);
            dropChance = 1.0F;
        }

        if (headStack != null && killedEntity.getRandom().nextFloat() < dropChance) {
            ItemEntity itemEntity = new ItemEntity(killedEntity.level(), killedEntity.getX(), killedEntity.getY(), killedEntity.getZ(), headStack);
            event.getDrops().add(itemEntity);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
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