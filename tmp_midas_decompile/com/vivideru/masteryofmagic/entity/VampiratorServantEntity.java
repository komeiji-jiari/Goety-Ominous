/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant
 *  com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant$IllagerServantSpell
 *  com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant$SpellcasterCastingSpellGoal
 *  com.Polarice3.Goety.common.entities.neutral.Owned
 *  com.Polarice3.Goety.common.magic.spells.necromancy.LeechingSpell
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.AvoidEntityGoal
 *  net.minecraft.world.entity.ai.goal.FleeSunGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.RestrictSunGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.monster.AbstractIllager
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.magic.spells.necromancy.LeechingSpell;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class VampiratorServantEntity
extends SpellcasterIllagerServant {
    private static final LeechingSpell LEECH = new LeechingSpell();
    private static final int MAX_BLOODCOUNT = 3000;
    private int leechCooldownTicks = 0;
    private int bloodCount = 0;
    private int healTick = 0;

    public VampiratorServantEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<? extends Owned>)((EntityType)GoetyMasteryOfMagicModEntities.VAMPIRATOR_SERVANT.get()), world);
    }

    public VampiratorServantEntity(EntityType<? extends Owned> type, Level world) {
        super(type, world);
        this.m_21530_();
        this.f_21364_ = 0;
    }

    public static void init() {
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public int getBloodCount() {
        return this.bloodCount;
    }

    public void setBloodCount(int value) {
        this.bloodCount = Math.max(0, Math.min(3000, value));
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128405_("BloodCount", this.bloodCount);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.bloodCount = tag.m_128451_("BloodCount");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22279_, 0.33).m_22268_(Attributes.f_22276_, 40.0).m_22268_(Attributes.f_22284_, 2.0).m_22268_(Attributes.f_22281_, 10.0).m_22268_(Attributes.f_22277_, 24.0).m_22268_(Attributes.f_22278_, 0.1);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(0, (Goal)new RestrictSunGoal((PathfinderMob)this));
        this.f_21345_.m_25352_(1, (Goal)new FleeSunGoal((PathfinderMob)this, 1.2));
        this.f_21345_.m_25352_(2, (Goal)new SpellcasterIllagerServant.SpellcasterCastingSpellGoal((SpellcasterIllagerServant)this));
        this.f_21345_.m_25352_(3, (Goal)new VampiricLeechSpellGoal());
        this.f_21345_.m_25352_(4, (Goal)new MoveTowardsTargetGoal((PathfinderMob)this, 1.1, 24.0f));
        this.f_21345_.m_25352_(5, (Goal)new AvoidEntityGoal((PathfinderMob)this, LivingEntity.class, 10.0f, 1.3, 1.5, e -> e == this.m_5448_() && this.m_21223_() <= this.m_21233_() * 0.5f));
        this.f_21345_.m_25352_(6, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.2, false){

            protected double m_6639_(LivingEntity target) {
                return 9.0;
            }
        });
        this.f_21345_.m_25352_(7, (Goal)new RandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Villager.class, true));
        this.f_21346_.m_25352_(3, (Goal)new NearestAttackableTargetGoal((Mob)this, AbstractIllager.class, true));
        this.f_21346_.m_25352_(4, (Goal)new NearestAttackableTargetGoal((Mob)this, IronGolem.class, true));
    }

    public void m_8119_() {
        super.m_8119_();
        if (!this.m_9236_().f_46443_) {
            if (this.leechCooldownTicks > 0) {
                --this.leechCooldownTicks;
            }
            ++this.healTick;
            if (this.healTick >= 20) {
                this.healTick = 0;
                if (this.m_21223_() < this.m_21233_() && this.bloodCount > 0) {
                    this.m_5634_(1.0f);
                    this.setBloodCount(this.bloodCount - 1);
                }
            }
        }
    }

    public void m_8107_() {
        super.m_8107_();
        if (this.m_9236_().f_46443_) {
            return;
        }
        if (this.m_6084_() && this.m_9236_().m_46461_() && !this.m_9236_().m_46471_() && this.m_9236_().m_45527_(this.m_20183_())) {
            this.m_20254_(8);
        }
    }

    public MobType m_6336_() {
        return MobType.f_21641_;
    }

    public boolean isUndead() {
        return true;
    }

    protected SoundEvent m_7515_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vindicator.ambient"));
    }

    protected SoundEvent m_7975_(DamageSource ds) {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vindicator.hurt"));
    }

    protected SoundEvent m_5592_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.vindicator.death"));
    }

    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public void addBloodCount(int value) {
        this.setBloodCount(this.bloodCount + value);
    }

    private class VampiricLeechSpellGoal
    extends Goal {
        private int leechTime;

        private VampiricLeechSpellGoal() {
        }

        public boolean m_8036_() {
            LivingEntity target = VampiratorServantEntity.this.m_5448_();
            if (target == null || !target.m_6084_()) {
                return false;
            }
            if (VampiratorServantEntity.this.isCastingSpell()) {
                return false;
            }
            if (VampiratorServantEntity.this.leechCooldownTicks > 0) {
                return false;
            }
            return VampiratorServantEntity.this.m_142582_((Entity)target) && (double)VampiratorServantEntity.this.m_20270_((Entity)target) <= 12.0;
        }

        public boolean m_8045_() {
            LivingEntity target = VampiratorServantEntity.this.m_5448_();
            return target != null && target.m_6084_() && VampiratorServantEntity.this.m_142582_((Entity)target) && (double)VampiratorServantEntity.this.m_20270_((Entity)target) <= 12.0 && this.leechTime > 0;
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8056_() {
            LivingEntity target = VampiratorServantEntity.this.m_5448_();
            if (target != null) {
                VampiratorServantEntity.this.m_21563_().m_24960_((Entity)target, (float)VampiratorServantEntity.this.m_8085_(), (float)VampiratorServantEntity.this.m_8132_());
            }
            VampiratorServantEntity.this.m_21573_().m_26573_();
            VampiratorServantEntity.this.spellCastingTickCount = 30;
            VampiratorServantEntity.this.setIsCastingSpell(SpellcasterIllagerServant.IllagerServantSpell.RAVAGING);
            this.leechTime = 30;
        }

        public void m_8041_() {
            VampiratorServantEntity.this.setIsCastingSpell(SpellcasterIllagerServant.IllagerServantSpell.NONE);
            VampiratorServantEntity.this.leechCooldownTicks = 60;
            this.leechTime = 0;
        }

        public void m_8037_() {
            LivingEntity target = VampiratorServantEntity.this.m_5448_();
            if (target == null) {
                return;
            }
            VampiratorServantEntity.this.m_21563_().m_24960_((Entity)target, (float)VampiratorServantEntity.this.m_8085_(), (float)VampiratorServantEntity.this.m_8132_());
            if (this.leechTime > 0) {
                --this.leechTime;
                if (this.leechTime < 25 && VampiratorServantEntity.this.m_9236_() instanceof ServerLevel && VampiratorServantEntity.this.m_142582_((Entity)target)) {
                    LEECH.mobSpellResult((LivingEntity)VampiratorServantEntity.this, ItemStack.f_41583_, LEECH.defaultStats().setPotency(LEECH.defaultStats().getPotency() + 3));
                }
            }
        }
    }
}

