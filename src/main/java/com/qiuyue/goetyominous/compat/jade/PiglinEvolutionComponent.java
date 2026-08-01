package com.qiuyue.goetyominous.compat.jade;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum PiglinEvolutionComponent implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        Entity entity = accessor.getEntity();
        int melee = data.getInt("MeleeDamageDealt");
        int ranged = data.getInt("RangedDamageDealt");
        if (melee <= 0 && ranged <= 0) return;

        boolean isBrute = entity instanceof PiglinBruteServant && !(entity instanceof ElitePiglinBruteServant);
        boolean isHunter = entity instanceof PiglinHunterServant && !(entity instanceof ElitePiglinHunterServant);
        boolean isBase = entity instanceof PiglinServant;

        if (isBase) {
            ItemStack mainHand = ((LivingEntity) entity).getMainHandItem();
            boolean holdingMelee = mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof AxeItem;
            boolean holdingCrossbow = mainHand.getItem() instanceof CrossbowItem;

            if (holdingMelee && melee > 0) {
                addMeleeProgress(tooltip, entity, melee);
            } else if (holdingCrossbow && ranged > 0) {
                addRangedProgress(tooltip, entity, ranged);
            }
        } else if (isBrute && melee > 0) {
            addMeleeProgress(tooltip, entity, melee);
        } else if (isHunter && ranged > 0) {
            addRangedProgress(tooltip, entity, ranged);
        }
    }

    private void addMeleeProgress(ITooltip tooltip, Entity entity, int melee) {
        int max = getMeleeMax(entity);
        if (max > 0) {
            tooltip.add(Component.translatable("jade.goetyominous.melee_progress",
                    melee, max));
        }
    }

    private void addRangedProgress(ITooltip tooltip, Entity entity, int ranged) {
        int max = getRangedMax(entity);
        if (max > 0) {
            tooltip.add(Component.translatable("jade.goetyominous.ranged_progress",
                    ranged, max));
        }
    }

    private int getMeleeMax(Entity entity) {
        if (entity instanceof StrongPiglinBruteServant) return MobsConfig.StrongPiglinBruteServantEvolutionDamage.get();
        if (entity instanceof PiglinBruteServant) return MobsConfig.PiglinBruteServantEvolutionDamage.get();
        if (entity instanceof PiglinServant) return MobsConfig.PiglinServantEvolutionDamage.get();
        return 0;
    }

    private int getRangedMax(Entity entity) {
        if (entity instanceof ElitePiglinHunterServant) return MobsConfig.StrongPiglinHunterServantEvolutionDamage.get();
        if (entity instanceof StrongPiglinHunterServant) return MobsConfig.StrongPiglinHunterServantEvolutionDamage.get();
        if (entity instanceof PiglinHunterServant) return MobsConfig.PiglinHunterServantEvolutionDamage.get();
        if (entity instanceof PiglinServant) return MobsConfig.PiglinServantRangedEvolutionDamage.get();
        return 0;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(GoetyOminous.MOD_ID, "piglin_evolution");
    }
}
