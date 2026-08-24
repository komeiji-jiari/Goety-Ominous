/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.particles.WindGatherParticleOption
 *  com.Polarice3.Goety.utils.ColorUtil
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.monster.warden.Warden
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.client.particles.WindGatherParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class SummonGhiaccio
extends Entity {
    public SummonGhiaccio(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.f_19794_ = true;
    }

    protected void m_8097_() {
    }

    protected void m_7378_(CompoundTag tag) {
    }

    protected void m_7380_(CompoundTag tag) {
    }

    public void m_8119_() {
        Object serverLevel;
        super.m_8119_();
        if (!this.m_9236_().f_46443_) {
            serverLevel = (ServerLevel)this.m_9236_();
            if (!serverLevel.m_46472_().equals((Object)Level.f_46428_) || !((Biome)serverLevel.m_204166_(this.m_20183_()).m_203334_()).m_198904_(this.m_20183_())) {
                for (Player player : serverLevel.m_45976_(Player.class, this.m_20191_().m_82400_(32.0))) {
                    player.m_5661_((Component)Component.m_237115_((String)"info.goety_mastery_of_magic.ghiaccio.summon_fail"), true);
                }
                this.m_146870_();
                return;
            }
            if (serverLevel.m_46791_() == Difficulty.PEACEFUL) {
                this.m_146870_();
                return;
            }
            if (this.f_19797_ >= 300) {
                serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_175821_, this.m_20208_(0.5), this.m_20187_(), this.m_20262_(0.5), 1, 0.0, 0.0, 0.0, 0.0);
                serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123754_, this.m_20208_(0.5), this.m_20187_(), this.m_20262_(0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
            float progress = Mth.m_14036_((float)((float)this.f_19797_ / 450.0f), (float)0.0f, (float)1.0f);
            int interval = Mth.m_269140_((float)progress, (int)8, (int)2);
            int width = Mth.m_269140_((float)progress, (int)8, (int)15);
            float height = Mth.m_14179_((float)progress, (float)0.3f, (float)0.9f);
            if (this.f_19797_ % interval == 0) {
                double dx = (this.f_19796_.m_188500_() * 3.0 + 2.0) * (this.f_19796_.m_188499_() ? 1.0 : -1.0);
                double dz = (this.f_19796_.m_188500_() * 3.0 + 2.0) * (this.f_19796_.m_188499_() ? 1.0 : -1.0);
                ColorUtil color = new ColorUtil(this.f_19796_.m_188503_(3) == 0 ? ChatFormatting.AQUA : ChatFormatting.WHITE);
                serverLevel.m_8767_((ParticleOptions)new WindGatherParticleOption(color, width, height, 90, this.m_19879_()), this.m_20185_() + dx, this.m_20186_() + (double)this.m_20206_(), this.m_20189_() + dz, 1, 0.0, 0.0, 0.0, 0.0);
            }
            if (this.f_19797_ == 450) {
                for (int k = 0; k < 240; ++k) {
                    float radius = this.f_19796_.m_188501_() * 5.0f;
                    float angle = this.f_19796_.m_188501_() * ((float)Math.PI * 2);
                    double dx = Mth.m_14089_((float)angle) * radius;
                    double dy = 0.01 + this.f_19796_.m_188500_() * 0.65;
                    double dz = Mth.m_14031_((float)angle) * radius;
                    serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_175821_, this.m_20185_() + dx * 0.12, this.m_20186_() + 0.4, this.m_20189_() + dz * 0.12, 0, dx, dy, dz, 0.35);
                }
                serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123813_, this.m_20185_(), this.m_20186_() + 0.5, this.m_20189_(), 4, 0.5, 0.25, 0.5, 0.0);
                serverLevel.m_8606_(6000, 0, false, false);
                GhiaccioEntity ghiaccio = new GhiaccioEntity((EntityType<? extends GhiaccioEntity>)((EntityType)GoetyMasteryOfMagicModEntities.GHIACCIO.get()), this.m_9236_());
                ghiaccio.m_6034_(this.m_20185_(), this.m_20186_(), this.m_20189_());
                ghiaccio.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
                serverLevel.m_7967_((Entity)ghiaccio);
                this.m_146870_();
            }
        }
        if (this.f_19797_ == 150) {
            this.m_5496_((SoundEvent)SoundEvents.f_11848_.get(), 1.0f, 0.75f);
            for (Player player : this.m_9236_().m_45976_(Player.class, this.m_20191_().m_82400_(32.0))) {
                player.m_5661_((Component)Component.m_237115_((String)"info.goety_mastery_of_magic.ghiaccio.summon"), true);
            }
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                serverLevel = (ServerLevel)level;
                Warden.m_219375_((ServerLevel)serverLevel, (Vec3)this.m_20182_(), null, (int)32);
            }
        }
        if (this.f_19797_ == 300) {
            this.m_5496_((SoundEvent)GoetyMasteryOfMagicModSounds.GHIACCIO_PUNCH_1.get(), 1.0f, 0.65f);
        }
        if (this.f_19797_ == 450) {
            this.m_5496_(SoundEvents.f_12090_, 1.0f, 0.55f);
        }
    }

    public PushReaction m_7752_() {
        return PushReaction.IGNORE;
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return new ClientboundAddEntityPacket((Entity)this);
    }
}

