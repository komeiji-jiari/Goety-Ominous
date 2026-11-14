package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantBlazeServantRodProjectile;
import com.qiuyue.goetyominous.common.entities.ally.mm.RodlingServant;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RodStrikeSpell extends SummonSpell {
    private static final List<PendingRodShot> PENDING_ROD_SHOTS = new ArrayList<>();

    public RodStrikeSpell() {
    }

    public int defaultSoulCost() {
        return MmConfig.RodStrikeSoulCost.get();
    }

    public int defaultCastDuration() {
        return MmConfig.RodStrikeCastDuration.get();
    }

    public int SummonDownDuration() {
        return MmConfig.RodStrikeSummonDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return SoundEventInit.MUTANT_BLAZE_RODSHOT.get();
    }

    public float castingVolume() {
        return 2.0F;
    }

    public int defaultSpellCooldown() {
        return MmConfig.RodStrikeCooldown.get();
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof RodlingServant;
    }

    public int summonLimit() {
        return MmConfig.RodlingServantLimit.get();
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        SoundEvent sound = this.CastingSound(caster);
        if (sound == null || castTime < 1 || castTime >= staff.getUseDuration()) {
            return;
        }
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(sound.getLocation(), SoundSource.PLAYERS);
        for (ServerPlayer player : worldIn.players()) {
            if (player.distanceToSqr(caster) <= 1024.0D) {
                player.connection.send(packet);
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        if (this.isShifting(caster)) {
            return;
        }

        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        boolean rightStaff = this.rightStaff(staff);
        int rodCount = MmConfig.RodStrikeRodCount.get() + (rightStaff ? 3 : 0);
        int rodlingCount = MmConfig.RodStrikeRodlingCount.get() + (rightStaff ? 1 : 0);
        int remaining = Math.max(0, this.summonLimit() - this.countOwnedRodlings(worldIn, caster));
        rodlingCount = Math.min(rodlingCount, Math.min(rodCount, remaining));

        boolean sapSnapshot = this.hasSummonDown(caster);
        int life = MobUtil.getSummonLifespan(worldIn) * duration;
        float damage = MmConfig.RodStrikeRodDamage.get().floatValue() * WandUtil.damageMultiply() + potency;
        int fireLength = MmConfig.RodStrikeRodFireLength.get();
        float speed = MmConfig.RodStrikeRodSpeed.get().floatValue();
        float inaccuracy = MmConfig.RodStrikeRodInaccuracy.get().floatValue();
        int shotPotency = potency;

        HitResult hit = this.rayTrace(worldIn, caster, 32, 0.5D);
        Vec3 aimPos;
        if (hit instanceof EntityHitResult entityHit) {
            aimPos = entityHit.getEntity().getEyePosition(1.0F);
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            aimPos = hit.getLocation();
        } else {
            aimPos = caster.getEyePosition(1.0F).add(caster.getViewVector(1.0F).scale(32.0D));
        }

        Set<Integer> rodlingIndexes = new HashSet<>();
        while (rodlingIndexes.size() < rodlingCount) {
            rodlingIndexes.add(caster.getRandom().nextInt(rodCount));
        }

        MinecraftServer server = worldIn.getServer();
        int startTick = server.getTickCount();
        for (int i = 0; i < rodCount; ++i) {
            boolean rodling = rodlingIndexes.contains(i);
            boolean armour = rodling && caster.getHealth() <= caster.getMaxHealth() * 0.5F && caster.getRandom().nextInt(100) < 50;
            boolean shields = rodling && caster.getHealth() <= caster.getMaxHealth() * 0.25F && caster.getRandom().nextInt(100) < 50;
            Runnable shot = () -> this.shootRod(worldIn, caster, aimPos, rodling, armour, shields, damage, fireLength, speed, inaccuracy, life, shotPotency, sapSnapshot);
            if (i == 0) {
                shot.run();
            } else {
                PENDING_ROD_SHOTS.add(new PendingRodShot(server, startTick + i, shot));
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING_ROD_SHOTS.isEmpty()) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        int now = server.getTickCount();
        Iterator<PendingRodShot> iterator = PENDING_ROD_SHOTS.iterator();
        while (iterator.hasNext()) {
            PendingRodShot pending = iterator.next();
            if (pending.server != server || pending.tick > now) {
                continue;
            }
            iterator.remove();
            pending.shot.run();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PENDING_ROD_SHOTS.clear();
    }

    private static final class PendingRodShot {
        private final MinecraftServer server;
        private final int tick;
        private final Runnable shot;

        private PendingRodShot(MinecraftServer server, int tick, Runnable shot) {
            this.server = server;
            this.tick = tick;
            this.shot = shot;
        }
    }

    private void shootRod(ServerLevel worldIn, LivingEntity caster, Vec3 aimPos, boolean rodling, boolean armour, boolean shields,
                          float damage, int fireLength, float speed, float inaccuracy,
                          int lifespan, int potency, boolean sapSnapshot) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level() != worldIn) {
            return;
        }
        Vec3 spawnPos = caster.getEyePosition(1.0F).add(caster.getViewVector(1.0F).scale(0.5D));
        Vec3 direction = aimPos.subtract(spawnPos);
        MutantBlazeServantRodProjectile rod = new MutantBlazeServantRodProjectile(worldIn, caster);
        rod.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        rod.damage = damage;
        rod.fireLength = fireLength;
        rod.ignoresInvulTime = true;
        rod.griefing = false;
        rod.griefingDropsBlocks = false;
        rod.fireGriefing = true;
        if (rodling) {
            rod.setIsRodling(true);
            rod.setHasArmour(armour);
            rod.setHasShields(shields);
            rod.spellRodlingLifespan = lifespan;
            rod.spellPotency = potency;
            rod.spellSapSnapshot = sapSnapshot;
        }
        rod.shoot(direction.x, direction.y, direction.z, speed, inaccuracy);
        worldIn.addFreshEntity(rod);
    }

    private int countOwnedRodlings(ServerLevel worldIn, LivingEntity owner) {
        return worldIn.getEntitiesOfClass(RodlingServant.class, owner.getBoundingBox().inflate(64.0D),
                rodling -> rodling.isAlive() && owner.getUUID().equals(rodling.getOwnerId())).size();
    }
}
