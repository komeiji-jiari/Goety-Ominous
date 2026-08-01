package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.mobs.ElitePiglinHunterServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.PiglinHunterServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.PiglinServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.StrongPiglinHunterServant;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PiglinHunterEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof AbstractPiglinServant piglin)) return;
        if (!"arrow".equals(event.getSource().getMsgId())) return;

        if (piglin instanceof StrongPiglinHunterServant && !(piglin instanceof ElitePiglinHunterServant)) {
            event.setAmount(event.getAmount() * 2.0F);
        } else if (piglin instanceof ElitePiglinHunterServant) {
            event.setAmount(event.getAmount() * 3.5F);
        }

        int dealt = (int) Math.ceil(event.getAmount());
        if (dealt <= 0) return;

        piglin.onRangedDamageDealt(dealt);

        if (piglin instanceof ElitePiglinHunterServant) {
        } else if (piglin instanceof StrongPiglinHunterServant strongHunter
                && strongHunter.getRangedDamageDealt() >= MobsConfig.StrongPiglinHunterServantEvolutionDamage.get()
                && strongHunter.getMainHandItem().getItem() instanceof CrossbowItem) {
            ElitePiglinHunterServant elite = new ElitePiglinHunterServant(
                    ModEntityTypes.ELITE_PIGLIN_HUNTER_SERVANT.get(),
                    (ServerLevel) strongHunter.level());
            elite.copyTrueOwner(strongHunter);
            elite.moveTo(strongHunter.getX(), strongHunter.getY(), strongHunter.getZ(), strongHunter.getYRot(), strongHunter.getXRot());
            elite.setBaby(strongHunter.isBaby());
            elite.setHealth(elite.getMaxHealth());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = strongHunter.getItemBySlot(slot);
                if (!stack.isEmpty()) elite.setItemSlot(slot, stack.copy());
            }
            for (int i = 0; i < strongHunter.getInventory().getContainerSize(); i++) {
                ItemStack stack = strongHunter.getInventory().getItem(i);
                if (!stack.isEmpty()) elite.getInventory().setItem(i, stack.copy());
            }
            if (strongHunter.isImmune()) elite.setImmuneToZombification(true);
            elite.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            strongHunter.discard();
            ((ServerLevel) strongHunter.level()).addFreshEntity(elite);
            ((ServerLevel) strongHunter.level()).sendParticles(ParticleTypes.FLASH,
                    strongHunter.getX(), strongHunter.getY() + 1.0, strongHunter.getZ(), 1, 0, 0, 0, 0);
        } else if (piglin instanceof PiglinHunterServant hunter
                && !(piglin instanceof StrongPiglinHunterServant)
                && hunter.getRangedDamageDealt() >= MobsConfig.PiglinHunterServantEvolutionDamage.get()
                && hunter.getMainHandItem().getItem() instanceof CrossbowItem) {

            StrongPiglinHunterServant strong = new StrongPiglinHunterServant(
                    ModEntityTypes.STRONG_PIGLIN_HUNTER_SERVANT.get(),
                    (ServerLevel) hunter.level());
            strong.copyTrueOwner(hunter);
            strong.moveTo(hunter.getX(), hunter.getY(), hunter.getZ(), hunter.getYRot(), hunter.getXRot());
            strong.setBaby(hunter.isBaby());
            strong.setHealth(strong.getMaxHealth());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = hunter.getItemBySlot(slot);
                if (!stack.isEmpty()) strong.setItemSlot(slot, stack.copy());
            }
            for (int i = 0; i < hunter.getInventory().getContainerSize(); i++) {
                ItemStack stack = hunter.getInventory().getItem(i);
                if (!stack.isEmpty()) strong.getInventory().setItem(i, stack.copy());
            }
            if (hunter.isImmune()) strong.setImmuneToZombification(true);
            strong.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            hunter.discard();
            ((ServerLevel) hunter.level()).addFreshEntity(strong);
            ((ServerLevel) hunter.level()).sendParticles(ParticleTypes.FLASH,
                    hunter.getX(), hunter.getY() + 1.0, hunter.getZ(), 1, 0, 0, 0, 0);
        }

        if (piglin instanceof PiglinServant servant
                && piglin.getRangedDamageDealt() >= MobsConfig.PiglinServantRangedEvolutionDamage.get()
                && servant.getMainHandItem().getItem() instanceof CrossbowItem) {
            PiglinHunterServant hunter = new PiglinHunterServant(
                    ModEntityTypes.PIGLIN_HUNTER_SERVANT.get(), servant.level());
            hunter.copyTrueOwner(servant);
            hunter.moveTo(servant.getX(), servant.getY(), servant.getZ(), servant.getYRot(), servant.getXRot());
            hunter.setBaby(servant.isBaby());
            hunter.setHealth(hunter.getMaxHealth());

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = servant.getItemBySlot(slot);
                if (!stack.isEmpty()) hunter.setItemSlot(slot, stack.copy());
            }
            for (int i = 0; i < servant.getInventory().getContainerSize(); i++) {
                ItemStack stack = servant.getInventory().getItem(i);
                if (!stack.isEmpty()) hunter.getInventory().setItem(i, stack.copy());
            }
            if (servant.isImmune()) hunter.setImmuneToZombification(true);
            hunter.setGuaranteedDrop(EquipmentSlot.MAINHAND);

            servant.discard();
            ((ServerLevel) servant.level()).addFreshEntity(hunter);
            ((ServerLevel) servant.level()).sendParticles(ParticleTypes.FLASH,
                    servant.getX(), servant.getY() + 1.0, servant.getZ(), 1, 0, 0, 0, 0);
        }
    }
}