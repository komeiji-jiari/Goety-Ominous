package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonPillagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class SkeletonConquillagerServant extends SkeletonPillagerServant {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/skeleton_conquillager.png");

    public SkeletonConquillagerServant(EntityType<? extends AbstractSkeletonServant> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.ConquillagerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ConquillagerArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ConquillagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ConquillagerArmor.get());
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        return ModEntityTypes.SKELETON_CONQUILLAGER_SERVANT.get();
    }

    @Override
    public EntityType<?> getVariant(@Nullable Player player, Level level, BlockPos blockPos) {
        return ModEntityTypes.SKELETON_CONQUILLAGER_SERVANT.get();
    }

    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(item -> item.getItem() instanceof CrossbowItem)) {
            return AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_HOLD;
        } else {
            return this.isAggressive() ? AbstractIllagerServant.IllagerServantArmPose.ATTACKING
                    : AbstractIllagerServant.IllagerServantArmPose.NEUTRAL;
        }
    }

    @Override
    public ItemStack getProjectile(ItemStack shootable) {
        int difficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getDifficulty().getId();
        return MobUtil.createFirework(difficulty * 2, DyeColor.values());
    }

    @Override
    public void tick() {
        super.tick();

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(8.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (entity.isAlive() && !MobUtil.areAllies(this, entity)
                    && entity.getMobType() != MobType.UNDEAD
                    && !(entity instanceof Player)
                    && !(entity instanceof Owned)
                    && !(entity instanceof Summoned)) {
                if (this.tickCount % 100 == 0 && this.getRandom().nextInt(20) == 0) {
                    entity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), 2000, 0, false, false));
                }
            }
        }

        if (this.level().isClientSide) {
            if (this.tickCount % 20 == 0) {
                for (int i = 0; i < 8; ++i) {
                    this.level().addParticle(ModParticleTypes.PLAGUE_EFFECT.get(), this.getRandomX(0.5D),
                            this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.5D, 0.0D);
                }
            }
        }
    }
}
