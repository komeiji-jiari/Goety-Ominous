package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import com.unusualmodding.opposing_force.misc.ElectricExplosion;
import com.unusualmodding.opposing_force.registry.OPMobEffects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class VoltServantElectricExplosion extends ElectricExplosion {
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    private final Entity source;
    private final float radius;
    private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
    private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();
    private final ExplosionDamageCalculator damageCalculator;

    public VoltServantElectricExplosion(Level level, Entity source, double x, double y, double z, float radius) {
        super(level, source, null, null, x, y, z, radius, Explosion.BlockInteraction.KEEP);
        this.level = level;
        this.source = source;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.damageCalculator = source != null
                ? new EntityBasedExplosionDamageCalculator(source)
                : new ExplosionDamageCalculator();
    }

    @Override
    public void explode() {
        this.level.gameEvent(this.source, GameEvent.HIT_GROUND, new Vec3(this.x, this.y, this.z));

        Set<BlockPos> set = new HashSet<>();
        int k;
        for (k = 0; k < 16; ++k) {
            int l;
            for (l = 0; l < 16; ++l) {
                int i1;
                for (i1 = 0; i1 < 16; ++i1) {
                    if (k == 0 || k == 15 || l == 0 || l == 15 || i1 == 0 || i1 == 15) {
                        double d0 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) i1 / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;
                        for (; f > 0.0F; f -= 0.225F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!blockstate.isAir()) {
                                break;
                            }
                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(
                                    this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (f - (optional.get() + 0.3F)) * 0.3F;
                            }
                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(
                                    this, this.level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }
                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }
        this.toBlow.addAll(set);

        float f1 = this.radius * 2.0F;
        int i = Mth.floor(this.x - (double) f1 - 1.0D);
        int j = Mth.floor(this.x + (double) f1 + 1.0D);
        int j1 = Mth.floor(this.y - (double) f1 - 1.0D);
        int k1 = Mth.floor(this.y + (double) f1 + 1.0D);
        int i2 = Mth.floor(this.z - (double) f1 - 1.0D);
        int j2 = Mth.floor(this.z + (double) f1 + 1.0D);
        List<Entity> list = this.level.getEntities(this.source, new AABB(i, j1, i2, j, k1, j2));
        ForgeEventFactory.onExplosionDetonate(this.level, this, list, (double) f1);
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        Entity owner = null;
        if (this.source instanceof Projectile projectile) {
            owner = projectile.getOwner();
        } else if (this.source instanceof LivingEntity living) {
            owner = living;
        }
        for (Entity entity : list) {
            double d13 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f1;
            if (d13 > 1.0D) {
                continue;
            }
            if (owner != null && entity instanceof LivingEntity
                    && MobUtil.areAllies(owner, (LivingEntity) entity)) {
                continue;
            }

            double d15 = entity.getX() - this.x;
            double d17 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
            double d19 = entity.getZ() - this.z;
            double d21 = Math.sqrt(d15 * d15 + d17 * d17 + d19 * d19);
            if (d21 == 0.0D) {
                continue;
            }
            d15 /= d21;
            d17 /= d21;
            d19 /= d21;
            double d23 = getSeenPercent(vec3, entity);
            double d25 = (1.0D - d13) * d23;
            entity.hurt(this.getDamageSource(),
                    (float) ((int) ((d25 * d25 + d25) / 2.0D * 5.0D * (double) f1 + 1.0D)));
            double d27;
            if (entity instanceof LivingEntity livingentity) {
                d27 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d25);
            } else {
                d27 = d25;
            }
            d15 *= d27;
            d17 *= d27;
            d19 *= d27;
            Vec3 vec31 = new Vec3(d15, d17, d19);
            entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
            if (entity instanceof Player player && !player.isSpectator() && !player.isCreative()) {
                this.hitPlayers.put(player, vec31);
            }
            if (entity instanceof LivingEntity livingentity1) {
                livingentity1.addEffect(new MobEffectInstance(OPMobEffects.ELECTRIFIED.get(), 300, 0));
            }
        }
    }

    @Override
    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }

    @Override
    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }

    @Override
    public void clearToBlow() {
        this.toBlow.clear();
    }
}
