package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import com.qiuyue.someillagerservants.config.MobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class StrongPiglinBruteServant extends PiglinBruteServant {

    public StrongPiglinBruteServant(EntityType<? extends PiglinBruteServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get()
                        + AttributesConfig.PiglinBruteServantEvolvedHealthBonus.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinBruteServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinBruteServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinBruteServantDamage.get()
                        + AttributesConfig.PiglinBruteServantEvolvedDamageBonus.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MeleeDamageDealt", this.meleeDamageDealt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.meleeDamageDealt = tag.getInt("MeleeDamageDealt");
    }

    @Override
    protected void onMeleeDamageDealt() {
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float prevHealth = target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0;
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            int dealt = (int) Math.ceil(prevHealth - living.getHealth());
            if (dealt > 0) {
                if (this.meleeDamageDealt >= MobsConfig.StrongPiglinBruteServantEvolutionDamage.get() && !this.level().isClientSide) {
                    ElitePiglinBruteServant elite = new ElitePiglinBruteServant(
                            ModEntityTypes.ELITE_PIGLIN_BRUTE_SERVANT.get(), this.level());
                    elite.copyTrueOwner(this);
                    elite.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    elite.setBaby(this.isBaby());
                    elite.setHealth(elite.getMaxHealth());

                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        ItemStack stack = this.getItemBySlot(slot);
                        if (!stack.isEmpty()) elite.setItemSlot(slot, stack.copy());
                    }
                    for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
                        ItemStack stack = this.getInventory().getItem(i);
                        if (!stack.isEmpty()) elite.getInventory().setItem(i, stack.copy());
                    }
                    if (this.isImmuneToZombification()) elite.setImmuneToZombification(true);
                    elite.setGuaranteedDrop(EquipmentSlot.MAINHAND);

                    this.discard();
                    ((ServerLevel) this.level()).addFreshEntity(elite);
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLASH,
                            this.getX(), this.getY() + 1.0, this.getZ(), 1, 0, 0, 0, 0);
                }
            }
        }
        return result;
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
        StrongZPiglinBruteServant zombified = new StrongZPiglinBruteServant(
                ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }
}