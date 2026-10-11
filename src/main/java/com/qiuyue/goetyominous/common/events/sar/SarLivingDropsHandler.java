package com.qiuyue.goetyominous.common.events.sar;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.sar.ExecutionerServant;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class SarLivingDropsHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity killedEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();

        if (!(sourceEntity instanceof ExecutionerServant)) {
            return;
        }

        if (!killedEntity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
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
}
