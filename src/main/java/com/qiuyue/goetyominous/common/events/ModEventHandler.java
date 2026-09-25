package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.hostile.HostileBlackWolf;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.HimPlushieRitual;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (entity.getPersistentData().getBoolean("GoetyOminousBetrayed")) {
            return;
        }

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
            return;
        }

        // ⚠️ 必须先确认装了 alexscaves，才允许碰 HullbreakerServant 这个类。
        // 原因：HullbreakerServant 自己 import 了 Citadel 的 IAnimatedEntity / AnimationHandler，
        // 而 build.gradle 里 citadel 是 compileOnly —— 只在编译期存在，运行期 classpath 上没有。
        // JVM 只要链接 HullbreakerServant 就会去解析 Citadel，于是抛
        // NoClassDefFoundError: com/github/alexthe666/citadel/animation/IAnimatedEntity。
        // 而下面这段原本是无条件执行的（只有上面的 Piglin 分支会提前 return），
        // 结果就是「杀任何会掉落的生物都崩」。
        //
        // 用 if 包起来是有效的隔离手段：JVM 解析类是惰性的，只有真正执行到那条字节码才解析。
        // 条件为 false 时大括号里的 instanceof 压根不会被执行，也就不会去加载那个类。
        if (com.qiuyue.goetyominous.compat.mod.AlexCavesCompat.isAlexCavesLoaded()) {
            com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant hullbreaker = null;
            if (sourceEntity instanceof com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant direct) {
                hullbreaker = direct;
            } else if (killedEntity.getLastHurtByMob() instanceof com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant lastHurt) {
                hullbreaker = lastHurt;
            }
            if (hullbreaker != null && hullbreaker.getTrueOwner() != null
                    && com.qiuyue.goetyominous.config.MobsConfig.HullbreakerServantPickUpDrops.get()) {
                hullbreaker.addDrops(event.getDrops());
                event.getDrops().clear();
                return;
            }
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