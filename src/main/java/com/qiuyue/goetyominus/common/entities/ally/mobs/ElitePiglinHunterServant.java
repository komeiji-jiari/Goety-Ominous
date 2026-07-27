package com.qiuyue.goetyominus.common.entities.ally.mobs;

import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.config.AttributesConfig;
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

public class ElitePiglinHunterServant extends StrongPiglinHunterServant {

    public ElitePiglinHunterServant(EntityType<? extends StrongPiglinHunterServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get()
                        + AttributesConfig.PiglinHunterServantEvolvedHealthBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2HealthBonus.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinHunterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinHunterServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinHunterServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get()
                        + AttributesConfig.PiglinHunterServantEvolvedDamageBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2DamageBonus.get());
    }

    @Override
    public float getScale() {
        return 1.25F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        return EntityDimensions.scalable(base.width * 1.25F, base.height * 1.25F - 0.5F);
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        EliteZPiglinHunterServant zombified = new EliteZPiglinHunterServant(
                ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get(), serverLevel);
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
}
