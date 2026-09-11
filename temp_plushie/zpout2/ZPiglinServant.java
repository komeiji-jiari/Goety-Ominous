package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags.Structures;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class ZPiglinServant extends ZombieServant {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, Operation.ADDITION);
    private int playFirstAngerSoundIn;

    public ZPiglinServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.m_21441_(BlockPathTypes.LAVA, 8.0F);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21346_.m_25352_(1, (new HurtByTargetGoal(this, new Class[0])).m_26044_(new Class[0]));
        this.f_21346_.m_25352_(1, (new HurtByTargetGoal(this, new Class[0])).m_26044_(new Class[]{ZombifiedPiglin.class}));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, AttributesConfig.ZPiglinServantHealth.get()).m_22268_(Attributes.f_22277_, 35.0D).m_22268_(Attributes.f_22279_, 0.23D).m_22268_(Attributes.f_22281_, AttributesConfig.ZPiglinServantDamage.get()).m_22268_(Attributes.f_22284_, AttributesConfig.ZPiglinServantArmor.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22276_), AttributesConfig.ZPiglinServantHealth.get());
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22284_), AttributesConfig.ZPiglinServantArmor.get());
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22281_), AttributesConfig.ZPiglinServantDamage.get());
    }

    public double m_6049_() {
        return this.m_6162_() ? -0.05D : -0.45D;
    }

    protected float m_6431_(Pose p_259553_, EntityDimensions p_259614_) {
        return this.m_6162_() ? 0.96999997F : 1.79F;
    }

    protected boolean convertsInWater() {
        return false;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        EntityType<?> entityType1 = (EntityType)ModEntityType.ZPIGLIN_SERVANT.get();
        if (level instanceof ServerLevel serverLevel) {
            if (level.f_46441_.m_188501_() <= 0.25F && BlockFinder.findStructure(serverLevel, blockPos, Structures.CAN_SUMMON_BRUTES)) {
                entityType1 = (EntityType)ModEntityType.ZPIGLIN_BRUTE_SERVANT.get();
            }
        }

        return entityType1;
    }

    @Nullable
    public SpawnGroupData m_6518_(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        RandomSource randomSource = pLevel.m_213780_();
        this.m_213945_(randomSource, pDifficulty);
        this.m_213946_(randomSource, pDifficulty);
        if (this.getTrueOwner() instanceof Enemy || this.isHostile()) {
            this.setWandering(true);
        }

        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.m_21409_(equipmentslottype, 0.0F);
        }

        return pSpawnData;
    }

    protected void m_8024_() {
        AttributeInstance modifiableattributeinstance = this.m_21051_(Attributes.f_22279_);
        if (this.m_5912_()) {
            if (!modifiableattributeinstance.m_22109_(SPEED_MODIFIER_ATTACKING)) {
                modifiableattributeinstance.m_22118_(SPEED_MODIFIER_ATTACKING);
            }

            this.maybePlayFirstAngerSound();
        } else if (modifiableattributeinstance.m_22109_(SPEED_MODIFIER_ATTACKING)) {
            modifiableattributeinstance.m_22130_(SPEED_MODIFIER_ATTACKING);
        }

        super.m_8024_();
    }

    private void maybePlayFirstAngerSound() {
        if (this.playFirstAngerSoundIn > 0) {
            --this.playFirstAngerSoundIn;
            if (this.playFirstAngerSoundIn == 0) {
                this.playAngerSound();
            }
        }

    }

    private void playAngerSound() {
        this.m_5496_(SoundEvents.f_12611_, this.m_6121_() * 2.0F, this.m_6100_() * 1.8F);
    }

    public boolean m_6469_(DamageSource pSource, float pAmount) {
        if (pSource.m_7639_() != null) {
            Entity var4 = pSource.m_7639_();
            if (var4 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)var4;
                if (!(livingEntity instanceof ZombifiedPiglin) && !livingEntity.m_7307_(this)) {
                    for(ZombifiedPiglin zombifiedPiglin : this.f_19853_.m_45976_(ZombifiedPiglin.class, this.m_20191_().m_82400_(10.0D))) {
                        if (zombifiedPiglin.m_5448_() != livingEntity && zombifiedPiglin.m_6779_(livingEntity)) {
                            zombifiedPiglin.m_6825_();
                            zombifiedPiglin.m_6925_(livingEntity.m_20148_());
                            zombifiedPiglin.m_6710_(livingEntity);
                        }
                    }
                }
            }
        }

        return !this.m_6673_(pSource) && super.m_6469_(pSource, pAmount);
    }

    protected SoundEvent m_7515_() {
        return this.m_5912_() ? SoundEvents.f_12611_ : SoundEvents.f_12610_;
    }

    protected SoundEvent m_7975_(DamageSource pDamageSource) {
        return SoundEvents.f_12613_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_12612_;
    }

    public SoundEvent getStepSound() {
        return SoundEvents.f_12614_;
    }

    protected void m_7355_(BlockPos pos, BlockState blockIn) {
        this.m_5496_(this.getStepSound(), 0.15F, 1.0F);
    }

    protected void m_213945_(RandomSource p_217055_, DifficultyInstance p_217056_) {
        if (this.canSpawnArmor()) {
            this.populateDefaultArmor(p_217055_, p_217056_);
        }

        this.populateDefaultWeapons(p_217055_, p_217056_);
    }

    public void populateDefaultWeapons(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack(Items.f_42430_));
    }
}
