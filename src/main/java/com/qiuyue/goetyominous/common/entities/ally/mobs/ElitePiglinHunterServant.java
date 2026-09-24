package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ElitePiglinHunterServant extends StrongPiglinHunterServant {

    public ElitePiglinHunterServant(EntityType<? extends StrongPiglinHunterServant> type, Level level) {
        super(type, level);
        this.refreshDimensions();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get()
                        + AttributesConfig.PiglinHunterServantEvolvedHealthBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2HealthBonus.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinHunterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinHunterServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinHunterServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get()
                        + AttributesConfig.PiglinHunterServantEvolvedDamageBonus.get()
                        + AttributesConfig.PiglinHunterServantEvolved2DamageBonus.get());
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
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        return EntityDimensions.scalable(base.width * 1.25F, base.height * 1.25F - 0.5F);
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        EliteZPiglinHunterServant zombified = new EliteZPiglinHunterServant(
                ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) zombified.setItemSlot(slot, stack.copy());
        }
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }
}
