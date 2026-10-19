package com.qiuyue.goetyominous.common.entities.ally.ua;

import com.qiuyue.goetyominous.common.items.ua.UaItems;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import com.qiuyue.goetyominous.config.uasr.UasrConfig;

public class GreatThrasherServant extends ThrasherServant {
    private static final EntityDimensions DEFAULT_SIZE = EntityDimensions.fixed(2.8F, 1.575F);

    public GreatThrasherServant(EntityType<? extends ThrasherServant> type, Level world) {
        super(type, world);
        this.xpReward = 0;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return UasrConfig.GreatThrasherServantLimit.get();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, UasrConfig.GreatThrasherServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, UasrConfig.GreatThrasherServantDamage.get())
                .add(Attributes.ARMOR, UasrConfig.GreatThrasherServantArmor.get());
    }

    @Override
    public float getMountDistance() {
        return 2.1F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.875D;
    }

    @Override
    protected double getStunDamageThreshold() {
        return 8.0F;
    }

    @Override
    protected EntityDimensions getDefaultSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.75F;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(UaItems.GREAT_THRASHER_SERVANT_SPAWN_EGG.get());
    }
}
