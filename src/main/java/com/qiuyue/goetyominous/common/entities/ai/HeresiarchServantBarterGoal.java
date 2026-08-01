package com.qiuyue.goetyominous.common.entities.ai;

import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
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

public class HeresiarchServantBarterGoal extends Goal {
    private int progress = 100;
    public CultistServant heresiarch;

    public HeresiarchServantBarterGoal(CultistServant heresiarch) {
        this.heresiarch = heresiarch;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        return this.heresiarch.getMainHandItem().is(ModTags.Items.WITCH_CURRENCY);
    }

    @Override
    public void start() {
        super.start();
        this.progress = 100;
        if (!this.heresiarch.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.heresiarch.level();
            for (int i = 0; i < 5; ++i) {
                double d0 = this.heresiarch.getRandom().nextGaussian() * 0.02D;
                double d1 = this.heresiarch.getRandom().nextGaussian() * 0.02D;
                double d2 = this.heresiarch.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.heresiarch.getRandomX(1.0D), this.heresiarch.getRandomY() + 1.0D, this.heresiarch.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
            }
        }
    }

    @Override
    public void tick() {
        this.heresiarch.setTarget(null);
        LivingEntity trader = this.heresiarch.getTrader();
        if (--this.progress > 0) {
            this.heresiarch.getNavigation().stop();
            if (trader != null && this.heresiarch.distanceTo(trader) <= 16.0F) {
                this.heresiarch.getLookControl().setLookAt(trader);
            }
        }
        if (this.progress <= 0) {
            Vec3 vec3 = trader != null ? trader.position() : this.heresiarch.position();
            if (!this.heresiarch.level().isClientSide()) {
                if (this.heresiarch.level().getServer() != null) {
                    float luck = 0.0F;
                    if (this.heresiarch.getMainHandItem().is(ModTags.Items.WITCH_BETTER_CURRENCY)) {
                        luck = 1.0F;
                    }

                    LootTable loottable = this.heresiarch.level().getServer().getLootData().getLootTable(
                            new net.minecraft.resources.ResourceLocation("goetyominous", "gameplay/heresiarch_servant_bartering")
                    );

                    List<ItemStack> list = loottable.getRandomItems(
                            (new LootParams.Builder((ServerLevel) this.heresiarch.level()))
                                    .withParameter(LootContextParams.THIS_ENTITY, this.heresiarch)
                                    .withParameter(LootContextParams.ORIGIN, this.heresiarch.position())
                                    .withLuck(luck)
                                    .create(LootContextParamSets.GIFT)
                    );

                    for (ItemStack itemstack : list) {
                        BehaviorUtils.throwItem(this.heresiarch, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                    }
                }
            }
            this.clearTrade();
        }

        if (this.heresiarch.hurtTime != 0) {
            if (this.heresiarch.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.WITCH_CURRENCY)
                    || this.heresiarch.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.WITCH_BETTER_CURRENCY)) {
                this.heresiarch.spawnAtLocation(this.heresiarch.getItemInHand(InteractionHand.MAIN_HAND));
                this.clearTrade();
            }
        }
    }

    protected void clearTrade() {
        this.heresiarch.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.heresiarch.setTrader(null);
        this.progress = 100;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}