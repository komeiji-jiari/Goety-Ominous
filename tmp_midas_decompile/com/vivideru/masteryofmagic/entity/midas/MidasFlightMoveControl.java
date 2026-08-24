/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.MoveControl
 *  net.minecraft.world.entity.ai.control.MoveControl$Operation
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.entity.midas;

import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public final class MidasFlightMoveControl
extends MoveControl {
    private final PhilosopherKingMidasEntity midas;

    public MidasFlightMoveControl(PhilosopherKingMidasEntity midas) {
        super((Mob)midas);
        this.midas = midas;
    }

    public void m_8126_() {
        if (this.f_24981_ != MoveControl.Operation.MOVE_TO) {
            this.midas.m_20256_(Vec3.f_82478_);
            return;
        }
        Vec3 offset = new Vec3(this.f_24975_ - this.midas.m_20185_(), this.f_24976_ - this.midas.m_20186_(), this.f_24977_ - this.midas.m_20189_());
        double distance = offset.m_82553_();
        if (distance < 0.18) {
            this.f_24981_ = MoveControl.Operation.WAIT;
            this.midas.m_20256_(Vec3.f_82478_);
            return;
        }
        double speed = this.midas.m_21133_(Attributes.f_22280_) * this.f_24978_ * (double)this.midas.getCastingMovementMultiplier();
        double actualSpeed = Math.min(speed, distance);
        Vec3 velocity = offset.m_82490_(actualSpeed / distance);
        this.midas.m_20256_(velocity);
        this.midas.m_7910_((float)actualSpeed);
        this.midas.f_19864_ = true;
        if (this.midas.m_5448_() == null && Math.abs(velocity.f_82479_) + Math.abs(velocity.f_82481_) > 1.0E-4) {
            float yaw = (float)(Mth.m_14136_((double)velocity.f_82481_, (double)velocity.f_82479_) * 57.2957763671875) - 90.0f;
            this.midas.m_146922_(this.m_24991_(this.midas.m_146908_(), yaw, 90.0f));
            this.midas.f_20883_ = this.midas.m_146908_();
        }
    }
}

