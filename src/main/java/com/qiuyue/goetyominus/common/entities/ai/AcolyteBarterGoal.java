package com.qiuyue.goetyominus.common.entities.ai;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.WitchBarterHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class AcolyteBarterGoal<T extends Mob> extends Goal {
    private int progress = 100;
    public final T mob;
    private final String lootTablePath;

    public AcolyteBarterGoal(T mob, String lootTablePath) {
        this.mob = mob;
        this.lootTablePath = lootTablePath;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean isInterruptable() { return false; }

    @Override
    public boolean canUse() {
        return this.mob.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY);
    }

    @Override
    public void start() {
        super.start();
        this.progress = 100;
        if (!this.mob.level().isClientSide()) {
            this.mob.playSound(ModSounds.HERETIC_CELEBRATE.get(), 1.0F, 1.0F);
            ServerLevel serverLevel = (ServerLevel) this.mob.level();
            for (int i = 0; i < 5; ++i) {
                double d0 = this.mob.getRandom().nextGaussian() * 0.02D;
                double d1 = this.mob.getRandom().nextGaussian() * 0.02D;
                double d2 = this.mob.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.mob.getRandomX(1.0D), this.mob.getRandomY() + 1.0D,
                        this.mob.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    @Override
    public void tick() {
        this.mob.setTarget(null);
        LivingEntity trader = getTrader();
        if (--this.progress > 0) {
            this.mob.getNavigation().stop();
            if (trader != null && this.mob.distanceTo(trader) <= 16.0F) {
                this.mob.getLookControl().setLookAt(trader);
            }
        }
        if (this.progress <= 0) {
            Vec3 vec3 = trader != null ? trader.position() : this.mob.position();
            if (!this.mob.level().isClientSide() && this.mob.level().getServer() != null) {
                float luck = this.mob.getMainHandItem().is(ModTags.Items.WITCH_BETTER_CURRENCY) ? 1.0F : 0.0F;

                LootTable loottable = this.mob.level().getServer().getLootData().getLootTable(
                        new net.minecraft.resources.ResourceLocation("goetyominus", lootTablePath));

                List<ItemStack> list = loottable.getRandomItems(
                        (new LootParams.Builder((ServerLevel) this.mob.level()))
                                .withParameter(LootContextParams.THIS_ENTITY, this.mob)
                                .withParameter(LootContextParams.ORIGIN, this.mob.position())
                                .withLuck(luck)
                                .create(LootContextParamSets.GIFT));

                for (ItemStack itemstack : list) {
                    BehaviorUtils.throwItem(this.mob, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                }
            }
            clearTrade();
        }

        if (this.mob.hurtTime != 0) {
            if (this.mob.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.WITCH_CURRENCY)
                    || this.mob.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.WITCH_BETTER_CURRENCY)) {
                this.mob.spawnAtLocation(this.mob.getItemInHand(InteractionHand.MAIN_HAND));
                clearTrade();
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    private LivingEntity getTrader() {
        if (this.mob instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant servant) {
            return servant.getTrader();
        }
        if (this.mob instanceof net.minecraft.world.entity.raid.Raider raider) {
            return WitchBarterHelper.getTrader(raider);
        }
        return null;
    }

    private void clearTrade() {
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        if (this.mob instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant servant) {
            servant.setTrader(null);
        }
        if (this.mob instanceof net.minecraft.world.entity.raid.Raider raider) {
            WitchBarterHelper.setTimer(raider, 0);
        }
        this.progress = 100;
    }
}