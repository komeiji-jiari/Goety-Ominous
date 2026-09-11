package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ZPiglinBruteServant extends ZPiglinServant {
    public boolean summonExplosion;

    public ZPiglinBruteServant(EntityType<? extends ZPiglinServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, AttributesConfig.ZPiglinBruteServantHealth.get()).m_22268_(Attributes.f_22277_, 35.0D).m_22268_(Attributes.f_22279_, 0.23D).m_22268_(Attributes.f_22281_, AttributesConfig.ZPiglinBruteServantDamage.get()).m_22268_(Attributes.f_22284_, AttributesConfig.ZPiglinBruteServantArmor.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22276_), AttributesConfig.ZPiglinBruteServantHealth.get());
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22284_), AttributesConfig.ZPiglinBruteServantArmor.get());
        MobUtil.setBaseAttributes(this.m_21051_(Attributes.f_22281_), AttributesConfig.ZPiglinBruteServantDamage.get());
    }

    public boolean m_6162_() {
        return false;
    }

    public void m_6863_(boolean pChildZombie) {
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.summonExplosion && this.f_19797_ % 20 == 0) {
            this.summonExplosion = false;
        }

    }

    public int xpReward() {
        return 20;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        return (EntityType)ModEntityType.ZPIGLIN_BRUTE_SERVANT.get();
    }

    public boolean m_6128_() {
        return this.summonExplosion ? true : super.m_6128_();
    }

    @Nullable
    public SpawnGroupData m_6518_(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        RandomSource randomSource = pLevel.m_213780_();
        this.m_213945_(randomSource, pDifficulty);
        this.m_213946_(randomSource, pDifficulty);
        if (pReason == MobSpawnType.MOB_SUMMONED) {
            this.summonExplosion = true;
        }

        for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.m_21409_(equipmentslottype, 0.0F);
        }

        return pSpawnData;
    }

    protected void m_8024_() {
        super.m_8024_();
    }

    public void populateDefaultWeapons(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack(Items.f_42433_));
    }
}
