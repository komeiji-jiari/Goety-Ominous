package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.alexander.mutantmore.entities.MutantShulker;
import com.alexander.mutantmore.entities.MutantShulkerBullet;
import com.alexander.mutantmore.entities.MutantShulkerTurret;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class MutantShulkerServantBullet extends MutantShulkerBullet {
    // 浮空效果只对最大生命 ≤ 200 的单位生效,避免控制高血量(如 boss 级)单位。
    private static final float MAX_LEVITATION_TARGET_HEALTH = 200.0F;
    // 浮空时长固定 2 秒(40 tick),覆盖 Mutant More 配置的默认 5 秒;等级仍用发射处设置的 levitationLevel。
    private static final int LEVITATION_DURATION = 40;
    // 导弹存活/射程上限:防止追踪导弹一路追着目标飞出加载区块(区块卸载后实体停摆、丢失)。
    // 最主要的参照是"距最近玩家"(区块加载中心),见 tick() 内的 getMaxPlayerRange()。
    private static final int MAX_LIFETIME_TICKS = 240; // 存活上限 240 tick(12 秒)
    private static final int MAX_TRACK_RANGE = 64;     // 距目标超过 64 格放弃追踪并消散
    private static final int MAX_OWNER_RANGE = 96;     // 距发射者(仆从)超过 96 格直接消散

    public MutantShulkerServantBullet(EntityType<? extends MutantShulkerBullet> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        // 根因:父类每 20 tick 才重算一次目标方向(moveDelay=20),且转向 lerp 只有 0.1。
        // 目标被躲/侧移后导弹转头太慢、冲过头,一头飞出加载范围。
        // 修复:父类 tick 前把重瞄间隔压到 5 tick(免疫期间不动,保留"被打飞后先飞开"的行为);
        //      父类 tick 后再叠加一次强转向,每 tick 朝目标当前方位多转一些。
        Entity preTarget = this.getTarget();
        if (!this.level().isClientSide && preTarget != null && this.immuneTicks <= 0) {
            this.moveDelay = Math.min(this.moveDelay, 5);
        }
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        // 强转向:父类 lerp(0.1)太慢,这里每 tick 以 0.3 的权重朝目标当前方位混合速度。
        if (preTarget != null && this.immuneTicks <= 0) {
            double x = preTarget.getX() - this.getX();
            double y = preTarget.getY(0.5) - this.getY();
            double z = preTarget.getZ() - this.getZ();
            double dist = Math.sqrt(x * x + y * y + z * z);
            if (dist > 1.0E-4) {
                double speed = 1.25;
                double turn = 0.3;
                this.setDeltaMovement(
                        Mth.lerp(turn, this.getDeltaMovement().x, x / dist * speed),
                        Mth.lerp(turn, this.getDeltaMovement().y, y / dist * speed),
                        Mth.lerp(turn, this.getDeltaMovement().z, z / dist * speed));
            }
        }
        // ---- 消散防护(兜底;正常情况下快速转向已让导弹咬住目标不再飞出) ----
        if (this.tickCount > MAX_LIFETIME_TICKS) {
            this.discard();
            return;
        }
        Entity target = this.getTarget();
        if (target != null && this.distanceToSqr(target) > Mth.square(MAX_TRACK_RANGE)) {
            this.discard();
            return;
        }
        // 距最近玩家过远即消散:玩家是区块加载的中心,以此兜底防"导弹追着逃跑目标飘出加载范围"。
        // getNearestPlayer(entity, dist) 在 dist 内找不到任何玩家时返回 null。
        if (this.level().getNearestPlayer(this, this.getMaxPlayerRange()) == null) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        if (owner != null && this.distanceToSqr(owner) > Mth.square(MAX_OWNER_RANGE)) {
            this.discard();
        }
    }

    // 距最近玩家的消散边界 = 服务器模拟距离×16 - 32(留 2 块的安全余量),下限 48、上限 160。
    // 只在"模拟距离内"实体会 tick,所以边界必须按 simulation distance 而不是 view distance 推。
    private int getMaxPlayerRange() {
        MinecraftServer server = this.level().getServer();
        if (server != null) {
            return Math.min(160, Math.max(48, server.getPlayerList().getSimulationDistance() * 16 - 32));
        }
        return 96;
    }

    // 覆写命中逻辑:伤害/爆炸与父类 MutantShulkerBullet.onHitEntity 完全一致,仅在施加浮空前加最大生命门槛。
    // 不能调用 super.onHitEntity(那会执行父类完整逻辑导致重复命中),且父类 canHarm 是
    // com.alexander.mutantmore.entities 包内包私有方法,跨包无法调用,所以用公共的
    // MiscUtils.canHarmBasedOnTeamAndTag 复刻同样的判定(见 canHarmServantBullet)。
    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity damagingMob = this.getOwner() != null ? this.getOwner() : this;
        Entity entity = hitResult.getEntity();
        if (!(entity instanceof MutantShulkerBullet) && !(entity instanceof MutantShulker)) {
            Entity owner = this.getOwner();
            LivingEntity livingOwner = owner instanceof LivingEntity ? (LivingEntity) owner : null;
            if (this.canHarmServantBullet(entity)) {
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                boolean flag = entity.hurt(MMDamageTypes.mutantShulkerBulletAttack(this.damageSources(), this, livingOwner), this.damage);
                MiscUtils.customExplosion(this.level(), damagingMob,
                        this.damageSources().explosion(this, this.getOwner() != null && this.getOwner() instanceof LivingEntity ? this.getOwner() : null),
                        null, this.getX(), this.getY(), this.getZ(),
                        Mth.clamp(this.explosionSize, 1.0F, Float.MAX_VALUE), false,
                        this.explosionBlockInteraction(), SoundEventInit.MUTANT_SHULKER_PROJECTILE_IMPACT.get(),
                        this.getSoundSource(), ParticleTypeInit.MUTANT_SHULKER_BULLET.get(), ParticleTypeInit.MUTANT_SHULKER_BULLET.get(),
                        this.damage, true, false);
                if (flag) {
                    this.doEnchantDamageEffects(livingOwner, entity);
                    // 浮空门槛:仅对最大生命 ≤ 200 的单位施加
                    if (entity instanceof LivingEntity living && living.getMaxHealth() <= MAX_LEVITATION_TARGET_HEALTH) {
                        // 已在浮空中则不重新施加,防止多个弹射物连续命中刷新(重置)漂浮buff时长
                        if (!living.hasEffect(MobEffects.LEVITATION)) {
                            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, this.levitationLevel), owner != null ? owner : this);
                        }
                    }
                }
            }
        } else if (entity instanceof MutantShulkerBullet) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement().scale(0.75)));
        }
    }

    // 复刻父类包私有 canHarm(Entity) 的判定(仆从子弹 owner 不是 MutantShulker,父类各 tag 分支均落空,
    // 实际恒为 true;这里保留完整判定以防后续 tag/队伍逻辑变化)。
    private boolean canHarmServantBullet(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_SHULKER_MUTANT_SHULKER_BULLET_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof MutantShulker)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.PLAYER_MUTANT_SHULKER_BULLET_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof Player)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_SHULKER_TURRET_MUTANT_SHULKER_BULLET_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof MutantShulkerTurret)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantShulker) && !(entity instanceof Player) && !(entity instanceof MutantShulkerTurret));
    }
}
