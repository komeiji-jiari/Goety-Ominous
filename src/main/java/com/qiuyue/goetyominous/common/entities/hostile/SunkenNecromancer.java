package com.qiuyue.goetyominous.common.entities.hostile;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractSunkenNecromancer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SunkenNecromancer extends AbstractSunkenNecromancer implements Enemy {

    public SunkenNecromancer(EntityType<? extends AbstractSunkenNecromancer> type, Level level) {
        super(type, level);
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        if (this.level() instanceof ServerLevel serverLevel) {
            int boneCount = this.random.nextInt(3) + pLooting;
            for (int i = 0; i < boneCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.BONE));
            }

            int lapisCount = this.random.nextInt(3) + pLooting;
            for (int i = 0; i < lapisCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.LAPIS_LAZULI));
            }

            int coralCount = this.random.nextInt(3) + 1 + pLooting;

            for (int i = 0; i < coralCount; i++) {
                ItemStack coralStack;
                int coralType = this.random.nextInt(5);
                switch (coralType) {
                    case 0:
                        coralStack = new ItemStack(Items.TUBE_CORAL);
                        break;
                    case 1:
                        coralStack = new ItemStack(Items.BRAIN_CORAL);
                        break;
                    case 2:
                        coralStack = new ItemStack(Items.BUBBLE_CORAL);
                        break;
                    case 3:
                        coralStack = new ItemStack(Items.FIRE_CORAL);
                        break;
                    default:
                        coralStack = new ItemStack(Items.HORN_CORAL);
                        break;
                }
                this.spawnAtLocation(coralStack);
            }

            int kelpCount = this.random.nextInt(4) + 2 + pLooting;
            for (int i = 0; i < kelpCount; i++) {
                this.spawnAtLocation(new ItemStack(Items.KELP));
            }
        }
    }
}
