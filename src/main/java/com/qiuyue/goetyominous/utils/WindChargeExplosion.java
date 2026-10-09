package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.mixin.ExplosionAccessor;
import com.qiuyue.goetyominous.common.network.WindChargeImpulsePacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class WindChargeExplosion extends Explosion {
    private final Level goetyominous$level;
    private final double goetyominous$x;
    private final double goetyominous$y;
    private final double goetyominous$z;
    private final float goetyominous$radius;
    private final ParticleOptions goetyominous$smallParticle;
    private final ParticleOptions goetyominous$largeParticle;
    private final SoundEvent goetyominous$sound;

    public WindChargeExplosion(Level level, Entity source, DamageSource damageSource, ExplosionDamageCalculator calculator,
                               double x, double y, double z, float radius,
                               ParticleOptions smallParticle, ParticleOptions largeParticle, SoundEvent sound) {
        super(level, source, damageSource, calculator, x, y, z, radius, false, BlockInteraction.KEEP);
        this.goetyominous$level = level;
        this.goetyominous$x = x;
        this.goetyominous$y = y;
        this.goetyominous$z = z;
        this.goetyominous$radius = radius;
        this.goetyominous$smallParticle = smallParticle;
        this.goetyominous$largeParticle = largeParticle;
        this.goetyominous$sound = sound;
    }

    @Override
    public void explode() {
        ExplosionAccessor accessor = (ExplosionAccessor) this;
        Level level = this.goetyominous$level;
        double centerX = this.goetyominous$x;
        double centerY = this.goetyominous$y;
        double centerZ = this.goetyominous$z;
        float radius = this.goetyominous$radius;
        ExplosionDamageCalculator calculator = accessor.getDamageCalculator();
        level.gameEvent(accessor.getSource(), GameEvent.EXPLODE, new Vec3(centerX, centerY, centerZ));

        ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
        rayLoop:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    if (i != 0 && i != 15 && j != 0 && j != 15 && k != 0 && k != 15) continue;
                    double dx = (float) i / 15.0F * 2.0F - 1.0F;
                    double dy = (float) j / 15.0F * 2.0F - 1.0F;
                    double dz = (float) k / 15.0F * 2.0F - 1.0F;
                    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    dx /= length;
                    dy /= length;
                    dz /= length;
                    double px = centerX;
                    double py = centerY;
                    double pz = centerZ;
                    for (float power = radius * (0.7F + level.random.nextFloat() * 0.6F); power > 0.0F; power -= 0.22500001F) {
                        BlockPos pos = BlockPos.containing(px, py, pz);
                        if (!level.hasChunkAt(pos)) continue rayLoop;
                        BlockState state = level.getBlockState(pos);
                        FluidState fluid = level.getFluidState(pos);
                        Optional<Float> resistance = calculator.getBlockExplosionResistance(this, level, pos, state, fluid);
                        if (resistance.isPresent()) {
                            power -= (resistance.get() + 0.3F) * 0.3F;
                        }
                        if (power > 0.0F && calculator.shouldBlockExplode(this, level, pos, state, power)) {
                            toBlow.add(pos);
                        }
                        px += dx * 0.3F;
                        py += dy * 0.3F;
                        pz += dz * 0.3F;
                    }
                }
            }
        }
        accessor.getToBlow().addAll(toBlow);

        float diameter = radius * 2.0F;
        int minX = Mth.floor(centerX - (double) diameter - 1.0);
        int maxX = Mth.floor(centerX + (double) diameter + 1.0);
        int minY = Mth.floor(centerY - (double) diameter - 1.0);
        int maxY = Mth.floor(centerY + (double) diameter + 1.0);
        int minZ = Mth.floor(centerZ - (double) diameter - 1.0);
        int maxZ = Mth.floor(centerZ + (double) diameter + 1.0);
        List<Entity> entities = level.getEntities(accessor.getSource(), new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        Vec3 center = new Vec3(centerX, centerY, centerZ);
        for (Entity entity : entities) {
            if (entity.ignoreExplosion()) continue;
            double distanceRatio = Math.sqrt(entity.distanceToSqr(center)) / (double) diameter;
            if (distanceRatio > 1.0) continue;
            double dx = entity.getX() - centerX;
            double dy = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - centerY;
            double dz = entity.getZ() - centerZ;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length == 0.0) continue;
            dx /= length;
            dy /= length;
            dz /= length;

            if (accessor.getSource() instanceof com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge windCharge
                    && !windCharge.shouldAffectEntity(entity)) {
                continue;
            }

            SimpleExplosionDamageCalculator simple = calculator instanceof SimpleExplosionDamageCalculator s ? s : null;
            if (simple == null || simple.shouldDamageEntity(this, entity)) {
                float damage = simple != null ? simple.getEntityDamageMultiplier(this, entity)
                        : (float) (((1.0 - distanceRatio) * (double) Explosion.getSeenPercent(center, entity) * 2.0) + 1.0);
                entity.hurt(this.getDamageSource(), damage);
            }
            double knockback = (1.0 - distanceRatio) * (double) Explosion.getSeenPercent(center, entity)
                    * (simple != null ? simple.getKnockbackMultiplier(entity) : 1.0F);
            double applied = knockback;
            if (entity instanceof LivingEntity living) {
                applied = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, knockback);
            }
            Vec3 impulse = new Vec3(dx * applied, dy * applied, dz * applied);
            entity.setDeltaMovement(entity.getDeltaMovement().add(impulse));
            if (entity instanceof Player player && !player.isSpectator()
                    && (!player.isCreative() || !player.getAbilities().flying)) {
                accessor.getHitPlayers().put(player, impulse);
                if (player instanceof ServerPlayer serverPlayer) {
                    WindChargeImpulsePacket.send(serverPlayer, impulse);
                }
            }
            WindChargeImpulse.onExplosionHit(entity, accessor.getSource());
        }
    }

    @Override
    public void finalizeExplosion(boolean spawnParticles) {
        Level level = this.goetyominous$level;
        if (!level.isClientSide) {
            level.playSound(null, this.goetyominous$x, this.goetyominous$y, this.goetyominous$z, this.goetyominous$sound,
                    SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
        }
        if (spawnParticles) {
            ParticleOptions particle = this.goetyominous$radius < 2.0F || !this.interactsWithBlocks()
                    ? this.goetyominous$smallParticle : this.goetyominous$largeParticle;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particle, this.goetyominous$x, this.goetyominous$y, this.goetyominous$z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        if (!(level instanceof ServerLevel)) return;
        if (!this.canTriggerBlocks()) return;
        ObjectArrayList<BlockPos> blocks = ((ExplosionAccessor) this).getToBlow();
        for (BlockPos pos : blocks) {
            WindChargeBlockTrigger.trigger(level, pos, level.getBlockState(pos));
        }
    }

    private boolean canTriggerBlocks() {
        if (this.goetyominous$level.isClientSide) {
            return false;
        }
        Entity source = ((ExplosionAccessor) this).getSource();
        if (source != null && source.getType() == ModEntityTypes.BREEZE_WIND_CHARGE.get()) {
            return this.goetyominous$level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        return true;
    }

    // 1.21 风弹爆炸是 Level.ExplosionInteraction.TRIGGER：interactsWithBlocks() == true，
    // radius>=2 时选 largeExplosionParticles（GUST_EMITTER_LARGE）。1.20.1 枚举没有 TRIGGER，
    // 构造时只能用 KEEP（触发方块由本类自己走 WindChargeBlockTrigger），但 KEEP 会让基类
    // interactsWithBlocks() 返回 false，粒子会被误选成 small —— 这里显式还原 1.21 语义。
    @Override
    public boolean interactsWithBlocks() {
        return true;
    }

    private static void goetyominous$unused(List<Pair<ItemStack, BlockPos>> drops, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty()) return;
        for (int i = 0; i < drops.size(); ++i) {
            ItemStack existing = drops.get(i).getLeft();
            if (ItemStack.isSameItemSameTags(existing, stack)) {
                int move = Math.min(stack.getCount(), 16 - existing.getCount());
                if (move > 0) {
                    existing.grow(move);
                    stack.shrink(move);
                    if (stack.isEmpty()) return;
                }
            }
        }
        drops.add(Pair.of(stack, pos));
    }
}
