package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class EliteZPiglinHunterServant extends StrongZPiglinHunterServant {

    public EliteZPiglinHunterServant(EntityType<? extends StrongZPiglinHunterServant> type, Level level) {
        super(type, level);
        this.refreshDimensions();
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack crossbow = this.getMainHandItem();
        int shots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow) > 0 ? 3 : 1;

        for (int i = 0; i < shots; ++i) {
            HeavyArrow arrow = new HeavyArrow(this.level(), this);
            arrow.setPierceLevel((byte) 4);
            arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            arrow.setShotFromCrossbow(true);

            float angle = i == 1 ? -10.0F : (i == 2 ? 10.0F : 0.0F);
            this.shootCrossbowProjectile(target, crossbow, arrow, angle);

            crossbow.hurtAndBreak(1, this, e -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        this.onCrossbowAttackPerformed();
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float angle) {
        float velocity = 4.8F;
        double dx = target.getX() - this.getX();
        double dy = target.getY() + (double) target.getBbHeight() / 2.0D - this.getEyeY();
        double dz = target.getZ() - this.getZ();
        Vector3f dir = new Vec3(dx, dy, dz).toVector3f();
        if (angle != 0.0F) {
            Vec3 up = this.getUpVector(1.0F);
            dir.rotate(new Quaternionf().setAngleAxis(angle * ((float) Math.PI / 180F), up.x, up.y, up.z));
        }
        projectile.shoot((double) dir.x(), (double) dir.y(), (double) dir.z(), velocity, 0.0F);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(projectile);
    }

    @Override
    public float getScale() {
        return 1.25F;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        return EntityDimensions.scalable(base.width * 1.25F, base.height * 1.25F - 0.5F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean isSunBurnTick() { return false; }
}
