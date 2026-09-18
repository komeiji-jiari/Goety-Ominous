package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.projectile.ServantTephraEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExtinctionMeteorSpell extends EverChargeSpell {

    private static final float SPREAD = 24.0F;
    private static final float SPREAD_Y = 4.0F;
    private static final int MAX_HEIGHT = 15;
    private static final int CLEAR_TRIES = 6;
    private static final float METEOR_VELOCITY = 1.6F;
    private static final int CLOUD_OUTER = 1706253;
    private static final int CLOUD_INNER = 2362642;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ExtinctionMeteorCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.ExtinctionMeteorChargeUp.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.ExtinctionMeteorInterval.get();
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        int extra = 0;
        if (WandUtil.enchantedFocus(caster)) {
            extra = WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) * 2;
        }
        return SpellConfig.ExtinctionMeteorShots.get() + extra;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ExtinctionMeteorCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(
                ModEnchantments.POTENCY.get(),
                ModEnchantments.RANGE.get(),
                ModEnchantments.BURNING.get(),
                ModEnchantments.RADIUS.get(),
                ModEnchantments.DURATION.get());
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        int range = spellStat.getRange();
        float radius = (float) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            range += WandUtil.getRangeLevel(caster);
            radius += (float) WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        }

        HitResult rayTrace = this.rayTrace(worldIn, caster, range, 3.0D);
        Vec3 location = rayTrace.getLocation();
        LivingEntity target = this.getTarget(caster, range);
        if (target != null) {
            location = target.position();
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(location.x, location.y + 1.5D, location.z);
        for (int i = 0; cursor.getY() < worldIn.getMaxBuildHeight()
                && worldIn.isEmptyBlock(cursor) && i < MAX_HEIGHT; ++i) {
            cursor = cursor.move(Direction.UP);
        }

        if (this.rightStaff(staff)) {
            potency += 2;
        }

        for (int j = 0; j < potency + 1; ++j) {
            Vec3 spot = this.pickSpawn(worldIn, cursor);
            Vec3 puff = spot.offsetRandom(worldIn.getRandom(), 1.5F);
            worldIn.sendParticles(new FoggyCloudParticleOption(new ColorUtil(CLOUD_OUTER), 3.0F, 1),
                    puff.x(), puff.y(), puff.z(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            worldIn.sendParticles(new FoggyCloudParticleOption(new ColorUtil(CLOUD_INNER), 3.0F, 1),
                    spot.x(), spot.y(), spot.z(), 1, 0.0D, 0.0D, 0.0D, 0.0D);

            this.spawnMeteor(worldIn, caster, spot, location, burning, radius);
        }
    }

    private Vec3 pickSpawn(ServerLevel level, BlockPos.MutableBlockPos cursor) {
        Vec3 spot = this.randomSpot(level, cursor);
        for (int tries = 0; tries < CLEAR_TRIES && this.blocked(level, spot); ++tries) {
            spot = this.randomSpot(level, cursor);
        }
        return this.blocked(level, spot) ? cursor.getCenter() : spot;
    }

    private boolean blocked(ServerLevel level, Vec3 spot) {
        BlockPos pos = BlockPos.containing(spot);
        return !level.isEmptyBlock(pos) && level.getFluidState(pos).isEmpty();
    }

    private Vec3 randomSpot(ServerLevel level, BlockPos.MutableBlockPos cursor) {
        return cursor.getCenter().add(
                level.random.nextFloat() * SPREAD - SPREAD / 2.0F,
                level.random.nextFloat() * SPREAD_Y - SPREAD_Y / 2.0F,
                level.random.nextFloat() * SPREAD - SPREAD / 2.0F);
    }

    private void spawnMeteor(ServerLevel level, LivingEntity caster, Vec3 spot, Vec3 aim,
                             int burning, float radius) {
        ServantTephraEntity tephra = new ServantTephraEntity(AcEntityRegistry.SERVANT_TEPHRA.get(), level);
        tephra.setOwner(caster);
        tephra.setPos(spot);
        tephra.setMaxScale(1.0F + 2.0F * level.random.nextFloat() + radius);
        tephra.setScale(tephra.getMaxScale());
        tephra.setFireSeconds(5 * Math.max(1, burning));
        tephra.setFireRadius(0.0F);
        tephra.setStunSeconds(0);

        Vec3 dir = aim.subtract(spot);
        tephra.shoot(dir.x, dir.y, dir.z, METEOR_VELOCITY, 0.0F);

        if (level.addFreshEntity(tephra)) {
            this.playSound(level, tephra, ACSoundRegistry.TEPHRA_WHISTLE.get(), 2.0F,
                    this.projPitch(level.getRandom()));
        }
    }
}
