package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.TransposeBrewEffect;
import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBrewParticlesPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PiglinShamanBrew extends ThrownBrew {

    public PiglinShamanBrew(EntityType<PiglinShamanBrew> type, Level level) {
        super(type, level);
    }

    public PiglinShamanBrew(Level level, LivingEntity owner) {
        super(ModEntityTypes.PIGLIN_SHAMAN_BREW.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();
        if (entity == owner) {
            return false;
        }
        if (owner instanceof PiglinShaman shaman && shaman.isOwnSummon(entity)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult result) {
        this.hasImpulse = true;
        if (this.level().isClientSide) {
            return;
        }
        ItemStack itemstack = this.getItem();
        if (itemstack.is(ModItems.GAS_BREW.get())) {
            this.makeBrewGas(itemstack, result);
            ModNetwork.sendToALL(new SAddBrewParticlesPacket(itemstack, this.blockPosition(),
                    PotionUtils.getPotion(itemstack).hasInstantEffects(), BrewUtils.getColor(itemstack)));
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity living ? living : null;
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) result;
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = this.level().getBlockState(pos);
            state.onProjectileHit(this.level(), state, blockHit, this);
            this.applyBlockEffects(pos, blockHit.getDirection(), livingOwner);
        } else if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) result;
            this.applyBlockEffects(entityHit.getEntity().blockPosition(), null, livingOwner);
        }
        List<MobEffectInstance> mobEffects = PotionUtils.getMobEffects(itemstack);
        List<BrewEffectInstance> brewEffects = BrewUtils.getBrewEffects(itemstack);
        if (!mobEffects.isEmpty() || !brewEffects.isEmpty()) {
            this.applySplashFiltered(mobEffects, brewEffects,
                    result.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) result).getEntity() : null);
        }
        ModNetwork.sendToALL(new SAddBrewParticlesPacket(itemstack, this.blockPosition(),
                PotionUtils.getPotion(itemstack).hasInstantEffects(), BrewUtils.getColor(itemstack)));
        this.discard();
    }

    private void makeBrewGas(ItemStack itemstack, HitResult result) {
        int area = BrewUtils.getAreaOfEffect(itemstack);
        int lingering = (int) BrewUtils.getLingering(itemstack);
        BlockPos blockPos = BlockPos.containing(result.getLocation());
        if (result.getType() == HitResult.Type.ENTITY) {
            blockPos = ((EntityHitResult) result).getEntity().blockPosition();
        }
        BrewGas gas = new BrewGas(this.level(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        gas.setGas(PotionUtils.getCustomEffects(itemstack), BrewUtils.getCustomEffects(itemstack),
                120 * (lingering + 1), 3 * (area + 1), owner);
        this.level().addFreshEntity(gas);
        ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, ModSounds.BREW_GAS.get(), 1.0F,
                this.level().random.nextFloat() * 0.1F + 0.9F));
    }

    private void applyBlockEffects(BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity source) {
        ItemStack itemstack = this.getItem();
        int area = BrewUtils.getAreaOfEffect(itemstack);
        for (BrewEffectInstance instance : BrewUtils.getBrewEffects(itemstack)) {
            BrewEffect effect = instance.getEffect();
            if (effect instanceof TransposeBrewEffect transpose) {
                this.applyTransposeFiltered(transpose, pos, instance.getAmplifier(), area);
            } else {
                if (direction != null) {
                    effect.applyDirectionalBlockEffect(this.level(), pos, direction, source,
                            instance.getAmplifier(), area);
                }
                effect.applyBlockEffect(this.level(), pos, source, instance.getDuration(),
                        instance.getAmplifier(), area);
            }
        }
    }

    private void applyTransposeFiltered(TransposeBrewEffect effect, BlockPos pos, int amplifier, int area) {
        Entity owner = this.getOwner();
        for (BlockPos blockPos : effect.getSpherePos(pos, area + 4)) {
            for (Entity entity : this.level().getEntitiesOfClass(Entity.class, new AABB(blockPos))) {
                if (!(entity instanceof ItemEntity) && !(entity instanceof LivingEntity)) {
                    continue;
                }
                if (entity == owner) {
                    continue;
                }
                if (owner instanceof PiglinShaman shaman && shaman.isOwnSummon(entity)) {
                    continue;
                }
                if (entity instanceof LivingEntity living) {
                    EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(
                            living, pos.getX(), pos.getY() + 1, pos.getZ());
                    if (event.isCanceled()) {
                        break;
                    }
                }
                entity.teleportTo(pos.getX(), pos.getY() + 1, pos.getZ());
                this.level().gameEvent(GameEvent.TELEPORT, entity.position(), GameEvent.Context.of(entity));
                if (!entity.isSilent()) {
                    this.level().playSound(null, entity.xo, entity.yo, entity.zo,
                            SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                    entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
                this.level().broadcastEntityEvent(entity, (byte) 46);
            }
        }
    }

    private void applySplashFiltered(List<MobEffectInstance> mobEffects, List<BrewEffectInstance> brewEffects,
                                     @Nullable Entity hitEntity) {
        ItemStack itemstack = this.getItem();
        int area = BrewUtils.getAreaOfEffect(itemstack) + 4;
        int areaSqr = Mth.square(area);
        AABB aabb = this.getBoundingBox().inflate(area, (double) area / 2.0D, area);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity source = this.getEffectSource();
            for (LivingEntity living : list) {
                if (!living.isAffectedByPotions() || !this.canAffect(living)) {
                    continue;
                }
                double d0 = this.distanceToSqr(living);
                if (d0 >= (double) areaSqr) {
                    continue;
                }
                double d1 = 1.0D - Math.sqrt(d0) / (double) areaSqr;
                if (living == hitEntity) {
                    d1 = 1.0D;
                }
                for (MobEffectInstance instance : mobEffects) {
                    MobEffect effect = instance.getEffect();
                    if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(this, this.getOwner(), living, instance.getAmplifier(), d1);
                    } else {
                        int duration = (int) (d1 * (double) instance.getDuration() + 0.5D);
                        if (duration > 20) {
                            living.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(),
                                    instance.isAmbient(), instance.isVisible()), source);
                        }
                    }
                }
                for (BrewEffectInstance instance : brewEffects) {
                    BrewEffect effect = instance.getEffect();
                    if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(this, this.getOwner(), living, instance.getAmplifier(), d1);
                    }
                }
            }
        }
    }

    private boolean canAffect(LivingEntity living) {
        Entity owner = this.getOwner();
        if (living == owner) {
            return false;
        }
        return !(owner instanceof PiglinShaman shaman) || !shaman.isOwnSummon(living);
    }
}
