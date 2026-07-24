package com.qiuyue.someillagerservants.common.entities.ally.mobs;

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

public class StrongPiglinHunterServant extends PiglinHunterServant {

    public StrongPiglinHunterServant(EntityType<? extends PiglinHunterServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get()
                        + AttributesConfig.PiglinHunterServantEvolvedHealthBonus.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinHunterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinHunterServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinHunterServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get()
                        + AttributesConfig.PiglinHunterServantEvolvedDamageBonus.get());
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        StrongZPiglinHunterServant zombified = new StrongZPiglinHunterServant(
                com.qiuyue.someillagerservants.common.init.ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) zombified.setItemSlot(slot, stack.copy());
        }

        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RangedDamageDealt", this.rangedDamageDealt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.rangedDamageDealt = tag.getInt("RangedDamageDealt");
    }
}