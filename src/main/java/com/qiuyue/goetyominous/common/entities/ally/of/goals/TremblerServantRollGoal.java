package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

/**
 * 震颤蜗牛的滚动剧本：逐字节复刻 OF 原版 TremblerRollGoal。
 * 节奏：先在原地转向目标蓄力(12 tick) → 锁定冲刺方向滚过去(0.6速度)
 *      → 撞到就把敌人推飞 + 打伤害 → 落地结束 → 进 60 tick 冷却。
 * 与敌对版唯一差别：撞人时用 Goety 的 MobUtil.areAllies 过滤友军（主人/其他仆从不打）。
 */
public class TremblerServantRollGoal extends RamblerServantAttackGoal {
    protected final TremblerServant trembler;   // 这本剧本是给哪只蜗牛用的
    private Vec3 rollDirection;                 // 锁定后的冲刺方向（tick 12 时算一次）

    public TremblerServantRollGoal(TremblerServant trembler) {
        super(trembler);
        this.rollDirection = Vec3.ZERO;
        this.trembler = trembler;
    }

    // 能不能开始？基类判了"有活目标"，再补：没在眩晕
    @Override
    public boolean canUse() {
        return super.canUse() && this.trembler.getStunnedTicks() <= 0;
    }

    // 开场：原版 start 里只关滚动+关冲刺（真正的"滚起来"是 tick 里条件触发）
    @Override
    public void start() {
        super.start();
        this.trembler.setRolling(false);
        this.trembler.setSprinting(false);
    }

    // 收场：关滚动 + 关冲刺
    @Override
    public void stop() {
        super.stop();
        this.trembler.setRolling(false);
        this.trembler.setSprinting(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.trembler.getTarget();
        if (target != null) {
            // 原版用"平方距离"比较：80.0 是平方值（实际约 8.9 格）
            double distanceSqr = this.trembler.distanceToSqr(target.getX(), target.getY(), target.getZ());
            // 速度修正系数：被缓慢药水减速 / 被迅捷药水加速（原版顺序：速度 - 缓慢）
            float f = 0.15F * (float) (this.getSpeedEffect(MobEffects.MOVEMENT_SPEED) - this.getSpeedEffect(MobEffects.MOVEMENT_SLOWDOWN));
            BlockPos blockPos = this.trembler.blockPosition();

            if (this.trembler.isRolling()) {
                // ===== 正在滚动 =====
                ++this.timer;
                this.trembler.getNavigation().stop();   // 滚的时候不用寻路，闷头冲

                // 前 12 tick：站在原地转向目标（蓄力）
                if (this.timer < 12) {
                    this.trembler.lookAt(target, 360.0F, 30.0F);
                    this.trembler.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }

                // 第 12 tick：锁定冲刺方向（蜗牛位置→目标位置的平面单位向量），开始贴地冲刺
                if (this.timer == 12) {
                    Vec3 targetPos = target.position();
                    this.rollDirection = new Vec3(
                            (double) blockPos.getX() - targetPos.x,
                            0.0D,
                            (double) blockPos.getZ() - targetPos.z).normalize();
                    this.trembler.setSprinting(true);
                }

                // 第 12 tick 之后：沿锁定方向以 0.6 速度（扣除/叠加药水系数）冲，每 tick 撞一次
                if (this.timer >= 12) {
                    this.trembler.setDeltaMovement(
                            this.rollDirection.x * (-0.6D - (double) f),
                            this.trembler.getDeltaMovement().y,
                            this.rollDirection.z * (-0.6D - (double) f));
                    this.tryToHurt();
                }

                // 超过 53 tick 或撞墙：停止冲刺
                // ★ 原版结束条件是 horizontalCollision（撞到东西），不是 onGround！
                //   蜗牛是贴地滚的，onGround 恒真会让滚动 1 tick 就结束，
                //   setRolling(false) 瞬间触发 → ROLL 动画根本没机会播出来。
                if (this.timer > 53 || this.trembler.horizontalCollision) {
                    this.trembler.setSprinting(false);
                    this.trembler.getNavigation().stop();
                    this.rollDirection = Vec3.ZERO;
                }

                // 超过 69 tick 或撞墙：本次滚动结束，进冷却
                if (this.timer > 69 || this.trembler.horizontalCollision) {
                    this.timer = 0;
                    this.trembler.setRolling(false);
                    this.trembler.rollCooldown();
                }
            } else {
                // ===== 还没滚：追目标/准备滚 =====
                // 平方距离 < 80（≈8.9格）+ 冷却好 + 目标在同一高度带(±3格) + 在地面 → 开滚
                if (distanceSqr < 80.0D && this.trembler.getRollCooldown() <= 0
                        && this.trembler.isWithinYRange(target) && this.trembler.onGround()) {
                    this.trembler.setRolling(true);
                } else if (distanceSqr < 16.0D) {
                    // 近：慢慢走近
                    this.trembler.getNavigation().moveTo(target, 1.0);
                } else {
                    // 远：跑步追击
                    this.trembler.getNavigation().moveTo(target, 1.4);
                }
                this.trembler.lookAt(Objects.requireNonNull(target), 30.0F, 30.0F);
                this.trembler.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }
    }

    // 药水速度系数：有该效果就返回 (等级+1)，没有返回 0
    private int getSpeedEffect(MobEffect effect) {
        return this.trembler.hasEffect(effect) ? this.trembler.getEffect(effect).getAmplifier() + 1 : 0;
    }

    // 撞击：只打碰撞盒里第一个非蜗牛实体，推飞+伤害+破盾
    private void tryToHurt() {
        // 用 TargetingConditions.forCombat() 的规则过滤碰撞盒里的实体（跳过创造/观察者玩家等）
        List<LivingEntity> list = this.trembler.level().getEntitiesOfClass(
                LivingEntity.class, this.trembler.getBoundingBox(),
                living -> TargetingConditions.forCombat().test(this.trembler, living));
        if (!list.isEmpty()) {
            LivingEntity victim = list.get(0);
            if (victim instanceof TremblerServant) {
                return;   // 不打同类
            }
            // ★ 唯一与敌对版的差别：友军（主人、其他仆从）不撞
            if (MobUtil.areAllies(this.trembler, victim)) {
                return;
            }

            // 原版顺序：速度 - 缓慢（迅捷药水让击退更强，缓慢更弱）
            float f = 0.15F * (float) (this.getSpeedEffect(MobEffects.MOVEMENT_SPEED) - this.getSpeedEffect(MobEffects.MOVEMENT_SLOWDOWN));
            // 击退力度：原版按"移动速度"算（0.15*1.65≈0.25），夹在 0.2~3 之间再加速度系数
            float dmg = Mth.clamp(this.trembler.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
            boolean blocked = victim.isBlocking();          // 敌人举盾？
            float knockback = blocked ? 1.75F : 2.25F;      // 举盾时推得轻一点

            victim.hurt(victim.damageSources().mobAttack(this.trembler),
                    (float) this.trembler.getAttributeValue(Attributes.ATTACK_DAMAGE));
            victim.push((double) (knockback * dmg * 1.5F), this.rollDirection.x, this.rollDirection.z);
            if (blocked && victim instanceof Player player) {
                player.disableShield(true);   // 破盾
            }
            this.trembler.swing(InteractionHand.MAIN_HAND);
        }
    }
}
