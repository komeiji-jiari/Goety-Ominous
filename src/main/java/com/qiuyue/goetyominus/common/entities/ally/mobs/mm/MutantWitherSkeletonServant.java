package com.qiuyue.goetyominus.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.equipment.PhilosophersMaceItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServantUtil;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.animation_trackers.AbstractAdvancedAnimation;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.animation_trackers.AdvancedAnimatable;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.animation_trackers.AdvancedAnimationTracker;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.animation_trackers.EntityAdvancedAnimation;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonClientConfig;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.entities.MutantWitherSkeletonBodyPart;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.ItemInit;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantWitherSkeletonServant.*;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeConfigSpec;

public class MutantWitherSkeletonServant extends AbstractMutantServant implements AdvancedAnimatable {
    private static final ForgeConfigSpec.BooleanValue DISABLED;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DISABLED = builder.define("no_grief", false);
        builder.build();
    }
    private static final UUID SPEED_MODIFIER_CROUCHING_UUID = UUID.fromString("adfdf75b-53d7-45c4-bee6-81bcc4a1632b");
    private AttributeModifier SPEED_MODIFIER_CROUCHING;
    public static EntityDimensions crouchingDimensions = EntityDimensions.scalable(1.25F, 2.4F);
    public static EntityDimensions lungingDimensions = EntityDimensions.scalable(1.25F, 1.25F);
    public final AdvancedAnimationTracker animationTracker;
    public Vec3 oldPos;
    public int repeatedHits;
    public float lungingXRot;
    private int lastHurtTick;

    public MutantWitherSkeletonServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
        this.oldPos = Vec3.ZERO;
        this.animationTracker = this.createAnimationTracker();
        this.xpReward = (Integer)MutantWitherSkeletonCommonConfig.exp_reward.get();
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.SPEED_MODIFIER_CROUCHING = new AttributeModifier(SPEED_MODIFIER_CROUCHING_UUID, "Crouching speed decrease", -(Double)MutantWitherSkeletonCommonConfig.crouching_movement_speed_decrease.get(), Operation.ADDITION);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MutantWitherSkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MutantWitherSkeletonServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MutantWitherSkeletonServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MutantWitherSkeletonServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MutantWitherSkeletonServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MutantWitherSkeletonServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MutantWitherSkeletonServantAttackDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.MutantWitherSkeletonServantAttackKnockback.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_block.get()) {
            this.goalSelector.addGoal(2, new MutantWitherSkeletonBlockGoal(this));
        }

        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_lunge.get()) {
            this.goalSelector.addGoal(3, new MutantWitherSkeletonLungeGoal(this));
        }

        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_wither_slash.get()) {
            this.goalSelector.addGoal(4, new MutantWitherSkeletonWitherSlashGoal(this));
        }

        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_rib_crush.get()) {
            this.goalSelector.addGoal(5, new MutantWitherSkeletonRibCrushAttackGoal(this));
        }

        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_melee_attack.get()) {
            this.goalSelector.addGoal(6, new MutantWitherSkeletonMeleeAttackGoal(this));
        }

        if ((Boolean)MutantWitherSkeletonCommonConfig.uses_wither_breath.get()) {
            this.goalSelector.addGoal(7, new MutantWitherSkeletonWitherBreathAttackGoal(this));
        }

        this.goalSelector.addGoal(8, new ApproachTargetGoal(this, (Double)MutantWitherSkeletonCommonConfig.following_target_distance.get(), (Double)MutantWitherSkeletonCommonConfig.following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this, new Class[0])).setUnseenMemoryTicks(6000));
        if ((Boolean)MutantWitherSkeletonCommonConfig.attacks_players.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, (entity) -> {
            return entity.getType().is(EntityTypes.MUTANT_WITHER_SKELETON_TARGETS);
        }) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate((Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get(), (Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get(), (Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get());
            }
        });
        if ((Boolean)MutantWitherSkeletonCommonConfig.attacks_baby_turtles.get()) {
            this.targetSelector.addGoal(3, new AllDirectionsTargetGoal<Turtle>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR) {
                protected AABB getTargetSearchArea(double p_26069_) {
                    return this.mob.getBoundingBox().inflate((Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get(), (Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get(), (Double)MutantWitherSkeletonCommonConfig.follow_non_player_distance.get());
                }
            });
        }

    }

    public AbstractMutantServant.NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return new AbstractMutantServant.NodeEvaluatorDimensions(Mth.floor(this.getBbWidth() + 1.0F), Mth.floor(crouchingDimensions.height), Mth.floor(this.getBbWidth() + 1.0F));
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }


    boolean canTarget(Entity target) {
        if (target == this) {
            return false;
        }

        if (target instanceof LivingEntity livingTarget && MobUtil.areAllies(this, livingTarget)) {
            return false;
        }

        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_WITHER_SKELETON_CANT_TARGET, this, target, this, (Predicate)null);
    }

    public boolean canHarm(Entity target) {
        if (target == this) {
            return false;
        }

        if (target instanceof LivingEntity livingTarget && MobUtil.areAllies(this, livingTarget)) {
            return false;
        }

        return MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_WITHER_SKELETON_CANT_HURT, this, target, this, (Predicate)null);
    }

    public boolean shouldBeStationary() {
        return this.getAnimation("intro").isPlaying();
    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return (SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public float getVoicePitch() {
        return this.isDeadOrDying() ? 1.0F : super.getVoicePitch();
    }

    protected float getSoundVolume() {
        return this.isDeadOrDying() ? 3.0F : 1.5F;
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(this.isInLava() ? (SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_STEP_LAVA.get() : (SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_STEP.get(), 0.75F, 1.0F);
    }

    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        boolean flag = this.getAnimation("block").isPlaying() ? false : super.hurt(p_21016_, p_21017_);
        if (flag) {
            this.getAnimation("hurt").start(0.25F, 0);
            if (!this.level().isClientSide) {
                this.lastHurtTick = this.tickCount;

                if ((Boolean) MutantWitherSkeletonCommonConfig.uses_block.get()
                        && p_21017_ > 0
                        && !this.shouldBeStationary()
                        && !this.getAnimation("lunging").isPlaying()
                        && !this.getAnimation("lunge_land").isPlaying()
                        && !this.getAnimation("stunned").isPlaying()) {
                    this.repeatedHits++;
                    if (this.repeatedHits >= (Integer) MutantWitherSkeletonCommonConfig.block_hit_requirement.get()) {
                        this.repeatedHits = 0;
                        this.stopAndLockAllAnimations(4, List.of("block", "offset_legs", "crouching_pose"));
                        this.getAnimation("block").start(1.63F, 4, this::unlockAllAnimations);
                    }
                }
            }
        }

        return flag;
    }

    public void leechHealth(float amount, Vec3 enemyPos) {
        MiscUtils.witherLeech(this, amount, enemyPos);
    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return (Boolean)MutantWitherSkeletonCommonConfig.walks_on_lava.get() && p_204067_.is(FluidTags.LAVA);
    }

    private static boolean isCataclysmWeapon(Item item) {
        try {
            Class<?> clazz = Class.forName("com.github.L_Ender.cataclysm.items.Cataclysm_Weapon_Item");
            return clazz.isInstance(item);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            this.dropAllDeathLoot(source);

            if (!source.is(DamageTypes.GENERIC_KILL) && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                this.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_DEATH.get(), this.getSoundVolume(), 1.0F);
                this.stopAndLockAllAnimations(2, List.of("death", "offset_legs"));
                this.getAnimation("death").start(0.25F, 2, () -> {
                    if ((Boolean)MutantWitherSkeletonCommonConfig.explode_into_parts.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mutants_explode_into_parts_off.get()) {
                        this.level().broadcastEntityEvent(this, (byte)4);
                        ShakeCameraEvent.shake(this.level(), 20, 0.035F, this.blockPosition(), 25);
                        this.createBodyPart(0, 0.0F, 2.0F, 0.0F);
                        this.createBodyPart(2, 0.0F, 3.5F, 1.0F);
                        this.createBodyPart(1, 0.25F, 2.5F, 0.0F);
                        this.createBodyPart(1, -0.25F, 2.5F, 0.0F);
                        this.createBodyPart(1, 0.25F, 2.75F, 0.0F);
                        this.createBodyPart(1, -0.25F, 2.75F, 0.0F);
                        this.createBodyPart(1, 0.25F, 3.0F, 0.0F);
                        this.createBodyPart(1, -0.25F, 3.0F, 0.0F);
                        this.createBodyPart(3, 0.5F, 0.5F, 0.0F);
                        this.createBodyPart(3, -0.5F, 0.5F, 0.0F);
                        this.createBodyPart(3, 0.5F, 1.5F, 0.0F);
                        this.createBodyPart(3, -0.5F, 1.5F, 0.0F);
                        this.createBodyPart(3, 0.75F, 2.0F, 0.0F);
                        this.createBodyPart(3, -0.75F, 2.0F, 0.0F);
                        this.createBodyPart(3, 0.75F, 3.0F, 0.0F);
                        this.createBodyPart(3, -0.75F, 3.0F, 0.0F);
                        this.createBodyPart(4, 0.5F, 3.25F, 0.5F);
                        this.createBodyPart(4, -0.5F, 3.25F, 0.5F);
                    } else {
                        ResourceLocation resourcelocation = new ResourceLocation("mutantmore", "loot_tables/entities/mutant_wither_skeleton_parts.json");
                        LootTable loottable = this.level().getServer().getLootData().getLootTable(resourcelocation);
                        LootParams.Builder builder = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalParameter(LootContextParams.DAMAGE_SOURCE, this.getLastDamageSource());
                        LootParams ctx = builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach(this::spawnAtLocation);
                    }

                    this.remove(RemovalReason.KILLED);
                });
            } else {
                this.playSound(SoundEvents.HOSTILE_DEATH, this.getSoundVolume(), this.getVoicePitch());
                this.remove(RemovalReason.KILLED);
            }
        }

    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();

        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            EquipmentSlot equipmentslot;

            if (item instanceof ArmorItem) {
                ArmorItem armoritem = (ArmorItem)item;
                equipmentslot = armoritem.getEquipmentSlot();
            } else if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock) {
                equipmentslot = EquipmentSlot.HEAD;
            } else if (itemstack.is(ItemTags.SWORDS) ||
                    itemstack.is(ItemTags.AXES) ||
                    itemstack.is(ItemTags.PICKAXES) ||
                    itemstack.is(ItemTags.SHOVELS) ||
                    itemstack.is(ItemTags.HOES) ||
                    itemstack.is(ItemTags.TOOLS) ||
                    item instanceof PhilosophersMaceItem ||
                    item instanceof TieredItem ||
                    isCataclysmWeapon(item))
            {
                if (pPlayer.isShiftKeyDown()) {
                    equipmentslot = EquipmentSlot.OFFHAND;
                } else {
                    equipmentslot = EquipmentSlot.MAINHAND;
                }
            } else if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 1.25F);
                this.heal(2.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(pPlayer, pHand);
            }

            ItemStack itemstack2 = this.getItemBySlot(equipmentslot);

            boolean canEquip = false;
            if (equipmentslot == EquipmentSlot.MAINHAND || equipmentslot == EquipmentSlot.OFFHAND) {
                canEquip = itemstack.is(ItemTags.SWORDS) ||
                        itemstack.is(ItemTags.AXES) ||
                        itemstack.is(ItemTags.PICKAXES) ||
                        itemstack.is(ItemTags.SHOVELS) ||
                        itemstack.is(ItemTags.HOES) ||
                        itemstack.is(ItemTags.TOOLS) ||
                        item instanceof PhilosophersMaceItem ||
                        item instanceof TieredItem ||
                        item instanceof com.github.L_Ender.cataclysm.items.Cataclysm_Weapon_Item;
            } else if (item instanceof ArmorItem) {
                canEquip = true;
            } else if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock) {
                canEquip = true;
            }

            if (canEquip) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.setItemSlot(equipmentslot, itemstack.copy());
                this.dropEquipment(equipmentslot, itemstack2);
                this.setGuaranteedDrop(equipmentslot);

                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            for(int i = 0; i < 200; ++i) {
                double d0 = this.random.nextGaussian() * 0.5;
                double d1 = this.random.nextGaussian() * 0.05;
                double d2 = this.random.nextGaussian() * 0.5;
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d0, d1, d2);
            }
        } else if (p_21375_ == 11) {
            Vec3 particlePos = PositionUtils.getOffsetPos(this, 0.0, (double)this.getBbHeight() * 0.6, (double)this.getBbWidth(), 0.0F, this.yBodyRot);

            Vec3 particleMotion;
            int i;
            for(i = 0; i < 10; ++i) {
                particleMotion = PositionUtils.getOffsetMotion(this, this.random.nextGaussian() * 0.2, this.random.nextGaussian() * 0.2, 0.75 + this.random.nextGaussian() * 0.25, 0.0F, this.yBodyRot);
                this.level().addParticle((ParticleOptions)ParticleTypeInit.WITHER_GAS.get(), particlePos.x, particlePos.y, particlePos.z, particleMotion.x, particleMotion.y, particleMotion.z);
            }

            for(i = 0; i < 20; ++i) {
                particleMotion = PositionUtils.getOffsetMotion(this, this.random.nextGaussian() * 0.2, this.random.nextGaussian() * 0.2, 0.75 + this.random.nextGaussian() * 0.25, 0.0F, this.yBodyRot);
                this.level().addParticle(ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z, particleMotion.x, particleMotion.y, particleMotion.z);
            }
        } else {
            super.handleEntityEvent(p_21375_);
        }

    }

    protected void dropAllDeathLoot(DamageSource source) {
        if (!this.level().isClientSide) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                ItemStack itemstack = this.getItemBySlot(equipmentslot);
                if (!itemstack.isEmpty()) {
                    this.spawnAtLocation(itemstack.copy());
                    this.setItemSlot(equipmentslot, ItemStack.EMPTY);
                }
            }
        }
    }

    protected void dropAllDieLoot(DamageSource source) {
    }


    public ItemEntity spawnAtLocation(ItemStack p_19985_, float p_19986_) {
        if (p_19985_.isEmpty()) {
            return null;
        } else if (this.level().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY() + (double)p_19986_, this.getZ(), p_19985_);
            itementity.setDefaultPickUpDelay();
            itementity.setExtendedLifetime();
            if (this.captureDrops() != null) {
                this.captureDrops().add(itementity);
            } else {
                this.level().addFreshEntity(itementity);
            }

            return itementity;
        }
    }

    public void createBodyPart(int partNumber, float xOffset, float yOffset, float zOffset) {
        if (!this.level().isClientSide) {
            try {
                java.lang.reflect.Constructor<MutantWitherSkeletonBodyPart> constructor = MutantWitherSkeletonBodyPart.class.getDeclaredConstructor(Level.class, int.class);
                constructor.setAccessible(true);
                MutantWitherSkeletonBodyPart part = constructor.newInstance(this.level(), partNumber);
                part.glows = (Boolean)MutantWitherSkeletonCommonConfig.parts_glow.get() || (Boolean)MutantMoreGroupedOptionsCommonConfig.mutant_parts_glow_on.get();
                part.damage = ((Double)MutantWitherSkeletonCommonConfig.part_collision_damage.get()).floatValue();
                part.witherLength = (Integer)MutantWitherSkeletonCommonConfig.part_collision_wither_mobs_length.get();
                part.witherLevel = (Integer)MutantWitherSkeletonCommonConfig.part_collision_wither_mobs_level.get();
                part.despawnTime = (Integer)MutantWitherSkeletonCommonConfig.part_persist_length.get();
                part.moveTo(PositionUtils.getOffsetPos(this, (double)xOffset, (double)yOffset, (double)zOffset, 0.0F, this.yBodyRot));
                part.push(this.random.nextGaussian() * 0.5, this.random.nextGaussian() * 0.5, this.random.nextGaussian() * 0.5);
                this.level().addFreshEntity(part);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public EntityDimensions getDimensions(Pose p_19975_) {
        return this.getAnimation("lunging").isPlaying() ? lungingDimensions : (p_19975_ == Pose.CROUCHING ? crouchingDimensions : super.getDimensions(p_19975_));
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        this.getAnimationTracker().tick();

        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            if (this.tickCount - this.lastHurtTick >= 100) {
                ServantUtil.healServant(this.getTrueOwner(), this);
            }
        }

        int i;
        int j;
        if (this.getDeltaMovement().horizontalDistanceSqr() > 2.500000277905201E-7 && this.random.nextInt(5) == 0) {
            int x = Mth.floor(this.getX());
            int y = Mth.floor(this.getY() - 0.20000000298023224);
            j = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(x, y, j);
            BlockState blockstate = this.level().getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(pos), this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), this.getY() + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.getBbWidth(), 4.0 * ((double)this.random.nextFloat() - 0.5), 0.5, ((double)this.random.nextFloat() - 0.5) * 4.0);
            }
        }

        if (this.isInLava()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            }
        }

        boolean markedCrouching = false;

        for(i = -2; i < 3; ++i) {
            for(j = -2; j < 3; ++j) {
                if (!markedCrouching && this.level().getBlockState(this.blockPosition().above(4).north(i).east(j)).isAir() && this.level().getBlockState(this.blockPosition().above(3).north(i).east(j)).isAir()) {
                    markedCrouching = false;
                } else {
                    markedCrouching = true;
                }
            }
        }

        if (!markedCrouching && !this.isInWall()) {
            this.setPose(Pose.STANDING);
        } else {
            this.setPose(Pose.CROUCHING);
        }

        AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (this.getPose() != Pose.CROUCHING && !this.isInLava()) {
            modifiableattributeinstance.removeModifier(this.SPEED_MODIFIER_CROUCHING);
        } else if (!modifiableattributeinstance.hasModifier(this.SPEED_MODIFIER_CROUCHING)) {
            modifiableattributeinstance.addTransientModifier(this.SPEED_MODIFIER_CROUCHING);
        }

        double xDiff = this.getX() - this.oldPos.x;
        double yDiff = 0.0;
        double zDiff = this.getZ() - this.oldPos.z;
        float distanceTravelled = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
        if (distanceTravelled > 0.0F) {
            this.getAnimation("walk").startLooping(4);
            this.getAnimation("idle").stop(4);
        } else {
            this.getAnimation("walk").stop(4);
            this.getAnimation("idle").startLooping(4);
        }

        if (this.getPose() == Pose.CROUCHING) {
            this.getAnimation("crouching_pose").startLooping(20);
        } else {
            this.getAnimation("crouching_pose").stop(20);
        }

        if (!this.getAnimation("offset_legs").isPlaying()) {
            this.getAnimation("offset_legs").startLooping(0);
        }

        this.oldPos = this.position();
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public boolean doHurtTarget(Entity target) {
        return this.doHurtTarget(target, EquipmentSlot.MAINHAND);
    }

    public boolean doHurtTarget(Entity target, EquipmentSlot slot) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getItemBySlot(slot), ((LivingEntity)target).getMobType());
            f1 += (float)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, this.getItemBySlot(slot));
        }

        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, this.getItemBySlot(slot));
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }

        boolean flag = target.hurt(this.damageSources().mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback((double)(f1 * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof Player) {
                Player player = (Player)target;
                MiscUtils.maybeDisableShield(this, player, this.getItemBySlot(slot), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
            this.leechHealth(((Double)MutantWitherSkeletonCommonConfig.melee_attack_leech_amount.get()).floatValue(), target.position().add(0.0, (double)(target.getBbHeight() / 2.0F), 0.0));
        }

        if (flag && target instanceof LivingEntity) {
            ((LivingEntity)target).addEffect(new MobEffectInstance(MobEffects.WITHER, (Integer)MutantWitherSkeletonCommonConfig.melee_attack_wither_length.get(), (Integer)MutantWitherSkeletonCommonConfig.melee_attack_wither_level.get()), this);
        }

        return flag;
    }

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(p_34192_);
    }

    protected float getStandingEyeHeight(Pose p_34186_, EntityDimensions p_34187_) {
        return this.getAnimationTracker() != null && this.getAnimation("lunging").isPlaying() ? super.getStandingEyeHeight(p_34186_, p_34187_) : (p_34186_ == Pose.CROUCHING ? crouchingDimensions.height - 0.1F : 3.75F);
    }

    public AdvancedAnimationTracker createAnimationTracker() {
        AdvancedAnimationTracker newTracker = new AdvancedAnimationTracker();
        newTracker.addAnimation("attack_left", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("attack_right", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("block", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("rib_crush", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("wither_breath", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("wither_slash", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("crouching_pose", new EntityAdvancedAnimation(this, false));
        newTracker.addAnimation("death", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("idle", new EntityAdvancedAnimation(this, false));
        newTracker.addAnimation("hurt", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("intro", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("offset_legs", new EntityAdvancedAnimation(this, false));
        newTracker.addAnimation("walk", new EntityAdvancedAnimation(this, false));
        newTracker.addAnimation("lunge", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("lunge_land", new EntityAdvancedAnimation(this, true));
        newTracker.addAnimation("lunging", new EntityAdvancedAnimation(this, false));
        newTracker.addAnimation("stunned", new EntityAdvancedAnimation(this, true));
        return newTracker;
    }

    public AdvancedAnimationTracker getAnimationTracker() {
        return this.animationTracker;
    }

    public EntityAdvancedAnimation getAnimation(String name) {
        return (EntityAdvancedAnimation)this.getAnimationTracker().getAnimation(name);
    }

    public void stopAndLockIdleAnimations(int transitionTicks, boolean includeCrouchingPose) {
        this.getAnimation("idle").stop(transitionTicks);
        this.getAnimation("idle").lock();
        this.getAnimation("walk").stop(transitionTicks);
        this.getAnimation("walk").lock();
        if (includeCrouchingPose) {
            this.getAnimation("crouching_pose").stop(transitionTicks);
            this.getAnimation("crouching_pose").lock();
        }

    }

    public void unlockIdleAnimations() {
        this.getAnimation("idle").unlock();
        this.getAnimation("walk").unlock();
        this.getAnimation("crouching_pose").unlock();
    }

    public void stopAndLockAllAnimations(int transitionTicks, List<String> excludedAnimations) {
        this.getAnimationTracker().animations.forEach((name, anim) -> {
            if (excludedAnimations != null && !excludedAnimations.contains(name) && anim instanceof AbstractAdvancedAnimation animation) {
                animation.stop(transitionTicks);
                animation.lock();
            }

        });
    }

    public void unlockAllAnimations() {
        this.getAnimationTracker().animations.forEach((name, anim) -> {
            if (anim instanceof AbstractAdvancedAnimation animation) {
                animation.unlock();
            }

        });
    }

    public void onMutated() {
        this.setPose(Pose.CROUCHING);
        this.stopAndLockIdleAnimations(0, true);
        this.getAnimation("intro").start(1.5F, 0, () -> {
            this.getAnimation("intro").setTransitionTicks(4);
            this.unlockIdleAnimations();
        });
        this.populateDefaultEquipmentSlots(this.random, this.level().getCurrentDifficultyAt(this.blockPosition()));
        this.populateDefaultEquipmentEnchantments(this.random, this.level().getCurrentDifficultyAt(this.blockPosition()));
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemInit.WITHERED_SCIMITAR.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)ItemInit.WITHERED_SCIMITAR.get()));
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, SpawnGroupData p_21437_, CompoundTag p_21438_) {
        this.populateDefaultEquipmentSlots(this.random, this.level().getCurrentDifficultyAt(this.blockPosition()));
        this.populateDefaultEquipmentEnchantments(this.random, this.level().getCurrentDifficultyAt(this.blockPosition()));

        if (p_21436_ == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.MutantWitherSkeletonServantLimit.get()) {
                return null;
            }
        }

        MiscUtils.spawnWithPumpkinOnHalloween(this, p_21434_.getRandom());
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof MutantWitherSkeletonServant servant) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public TagKey<Block> walksThroughTag() {
        return Blocks.MUTANT_WITHER_SKELETON_WALKS_THROUGH;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> showHealthBarConfig() {
        return MutantWitherSkeletonClientConfig.show_health_bar;
    }

    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return MutantWitherSkeletonCommonConfig.despawns;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        public boolean canUse() {
            return MutantWitherSkeletonServant.this.shouldBeStationary();
        }
    }
}
