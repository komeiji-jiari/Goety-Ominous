package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class ZInquillagerServant extends InquillagerServant {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING =
            SynchedEntityData.defineId(ZInquillagerServant.class, EntityDataSerializers.BOOLEAN);
    public static final Map<Integer, ResourceLocation> ZOMBIE_TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager.png"));
        map.put(1, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager_2.png"));
        map.put(2, new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/zombie_inquillager_3.png"));
    });

    public int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;

    public ZInquillagerServant(EntityType<? extends ZInquillagerServant> type, Level level) {
        super(type, level);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ZOMBIE_TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), ZOMBIE_TEXTURE_BY_TYPE.get(0));
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public EntityType<? extends Mob> getNecroConvert() {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            compound.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null,
                    compound.getInt("ConversionTime"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isAlive() && this.isConverting()) {
                this.villagerConversionTime -= this.getConversionProgress();
                if (this.villagerConversionTime <= 0
                        && ForgeEventFactory.canLivingConvert(this, ModEntityTypes.INQUILLAGER_SERVANT.get(),
                        timer -> this.villagerConversionTime = timer)) {
                    this.finishConversion(serverLevel);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (stack.is(Items.ROTTEN_FLESH) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D),
                                this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5D);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (!this.level().isClientSide && this.canConvert() && stack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    public boolean canConvert() {
        return this.isNatural();
    }

    public boolean isConverting() {
        return this.entityData.get(DATA_CONVERTING);
    }

    private void startConverting(@Nullable UUID starter, int time) {
        this.conversionStarter = starter;
        this.villagerConversionTime = time;
        this.entityData.set(DATA_CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time,
                Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte) 16);
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 16) {
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(),
                        1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    private void finishConversion(ServerLevel level) {
        Player player = this.conversionStarter != null ? level.getPlayerByUUID(this.conversionStarter) : null;
        if (MobUtil.convertTo(this, ModEntityTypes.INQUILLAGER_SERVANT.get(), true, player)
                instanceof InquillagerServant servant) {
            servant.finalizeSpawn(level, level.getCurrentDifficultyAt(servant.blockPosition()),
                    MobSpawnType.CONVERSION, null, null);
            servant.setOutfitType(this.getOutfitType());
            servant.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            if (!this.isSilent()) {
                level.levelEvent(null, 1027, this.blockPosition(), 0);
            }
            ForgeEventFactory.onLivingConvert(this, servant);
        }
    }

    private int getConversionProgress() {
        int progress = 1;
        if (this.random.nextFloat() < 0.01F) {
            int count = 0;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = (int) this.getX() - 4; x < (int) this.getX() + 4 && count < 14; ++x) {
                for (int y = (int) this.getY() - 4; y < (int) this.getY() + 4 && count < 14; ++y) {
                    for (int z = (int) this.getZ() - 4; z < (int) this.getZ() + 4 && count < 14; ++z) {
                        BlockState state = this.level().getBlockState(pos.set(x, y, z));
                        if (state.is(Blocks.IRON_BARS) || state.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++progress;
                            }
                            ++count;
                        }
                    }
                }
            }
        }
        return progress;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }
}
