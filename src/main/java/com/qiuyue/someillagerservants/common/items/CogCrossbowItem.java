package com.qiuyue.someillagerservants.common.items;

import com.google.common.collect.Lists;
import com.qiuyue.someillagerservants.common.init.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public class CogCrossbowItem extends CrossbowItem implements Vanishable {

    private static final String TAG_CHARGED = "Charged";
    private static final String TAG_CHARGED_PROJECTILES = "ChargedProjectiles";
    private static final String TAG_CHARGED_SHOTS = "ChargedShots";
    private static final int MAX_CHARGE_DURATION = 25;
    protected static final float ARROW_POWER = 3.15F;
    protected static final float FIREWORK_POWER = 1.6F;
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public CogCrossbowItem() {
        this(930);
    }

    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    public CogCrossbowItem(int durability) {
        super(new Properties().durability(durability));
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.GOLD_INGOT) || super.isValidRepairItem(stack, repairCandidate);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isCharged(stack)) {
            int shotsLeft = getChargeShots(stack) - 1;
            setChargedShots(stack, shotsLeft);

            float velMul = getVelocityMultiplier();
            int extraPierce = getExtraPiercing();
            SoundEvent[] shootSounds = getShootSounds();
            performShooting(level, player, hand, stack, getShootingPower(stack) * velMul, 1.0F, extraPierce, shootSounds);

            if (shotsLeft <= 0) {
                setCharged(stack, false);
                stack.getOrCreateTag().putLong("LastUseTime", player.tickCount);
            }
            return InteractionResultHolder.consume(stack);
        } else {
            if (!isCharged(stack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                stack.getOrCreateTag().putLong("LastUseTime", player.tickCount);
                player.startUsingItem(hand);
            }
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof Player player) {
            stack.getOrCreateTag().putLong("LastUseTime", player.tickCount);
        }
        int used = this.getUseDuration(stack) - timeLeft;
        float f = getPowerForTime(used, stack);
        if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(living, stack)) {
            setCharged(stack, true);
            int msBonusShots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0 ? 1 : 0;
            setChargedShots(stack, 5 + msBonusShots);
            SoundSource source = living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, living.getX(), living.getY(), living.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, source,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    protected static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        if (!(shooter instanceof Player player)) return false;

        boolean creative = player.getAbilities().instabuild;
        int msBonusShots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow) > 0 ? 1 : 0;
        int shots = 5 + msBonusShots;
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(Items.FIREWORK_ROCKET) || offhand.getItem() instanceof ArrowItem) {
            clearChargedProjectiles(crossbow);
            addChargedProjectile(crossbow, creative ? offhand.copy() : offhand.split(shots));
            return true;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ArrowItem) {
                clearChargedProjectiles(crossbow);
                addChargedProjectile(crossbow, creative ? stack.copy() : stack.split(shots));
                return true;
            }
        }

        if (creative) {
            clearChargedProjectiles(crossbow);
            addChargedProjectile(crossbow, new ItemStack(Items.ARROW));
            return true;
        }

        return false;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int count) {
        if (level.isClientSide) return;
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        float f = (float) (stack.getUseDuration() - count) / getChargeDuration(stack);
        if (f < 0.2F) { startSoundPlayed = false; midLoadSoundPlayed = false; }
        if (f >= 0.2F && !startSoundPlayed) {
            startSoundPlayed = true;
            level.playSound(null, living.getX(), living.getY(), living.getZ(),
                    getStartSound(i), SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        if (f >= 0.5F && i == 0 && !midLoadSoundPlayed) {
            midLoadSoundPlayed = true;
            level.playSound(null, living.getX(), living.getY(), living.getZ(),
                    SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public static void performShooting(Level level, LivingEntity shooter, InteractionHand hand,
                                       ItemStack crossbow, float velocity, float inaccuracy) {
        performShooting(level, shooter, hand, crossbow, velocity, inaccuracy, 0, null);
    }

    public static void performShooting(Level level, LivingEntity shooter, InteractionHand hand,
                                       ItemStack crossbow, float velocity, float inaccuracy,
                                       int extraPiercing, SoundEvent[] shootSounds) {
        if (shooter instanceof Player player &&
                ForgeEventFactory.onArrowLoose(crossbow, level, player, 1, true) < 0) return;

        List<ItemStack> list = getChargedProjectiles(crossbow);
        if (list.isEmpty()) return;

        int shots = 1;
        float[] spreadAngles = new float[]{0.0F};

        ItemStack ammo = list.get(0);
        float[] pitches = getShotPitches(shooter.getRandom());
        boolean creative = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;

        for (int i = 0; i < shots; i++) {
            shootProjectile(level, shooter, hand, crossbow, ammo,
                    pitches[Math.min(i, pitches.length - 1)], creative, velocity, inaccuracy,
                    spreadAngles[Math.min(i, spreadAngles.length - 1)], extraPiercing, shootSounds, shots);
        }

        onCrossbowShot(level, shooter, crossbow);
    }

    protected static void shootProjectile(Level level, LivingEntity shooter, InteractionHand hand,
                                        ItemStack crossbow, ItemStack ammo, float pitch,
                                        boolean creative, float velocity, float inaccuracy, float angle) {
        shootProjectile(level, shooter, hand, crossbow, ammo, pitch, creative, velocity, inaccuracy, angle, 0, null, 1);
    }

    protected static void shootProjectile(Level level, LivingEntity shooter, InteractionHand hand,
                                        ItemStack crossbow, ItemStack ammo, float pitch,
                                        boolean creative, float velocity, float inaccuracy, float angle,
                                        int extraPiercing, SoundEvent[] shootSounds, int shotCount) {
        if (level.isClientSide) return;

        boolean isFirework = ammo.is(Items.FIREWORK_ROCKET);
        Projectile projectile;

        if (isFirework) {
            projectile = new FireworkRocketEntity(level, ammo, shooter,
                    shooter.getX(), shooter.getEyeY() - 0.15, shooter.getZ(), true);
        } else {
            ArrowItem arrowItem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
            AbstractArrow arrow = arrowItem.createArrow(level, ammo, shooter);
            if (creative) arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            arrow.setShotFromCrossbow(true);
            int piercing = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
            if (piercing > 0 || extraPiercing > 0) arrow.setPierceLevel((byte) Math.max(piercing, extraPiercing));
            arrow.getPersistentData().putBoolean("someillagerservants:no_invul", true);
            projectile = arrow;
        }

        if (shooter instanceof CrossbowAttackMob cbm && cbm.getTarget() != null) {
            cbm.shootCrossbowProjectile(cbm.getTarget(), crossbow, projectile, angle);
        } else {
            Vec3 up = shooter.getUpVector(1.0F);
            Quaternionf q = new Quaternionf().setAngleAxis(angle * 0.017453292F, up.x, up.y, up.z);
            Vec3 view = shooter.getViewVector(1.0F);
            Vector3f dir = view.toVector3f().rotate(q);
            projectile.shoot(dir.x(), dir.y(), dir.z(), velocity, inaccuracy);
        }

        crossbow.hurtAndBreak(isFirework ? 3 : 1, shooter, p -> p.broadcastBreakEvent(hand));
        level.addFreshEntity(projectile);

        if (shootSounds != null && shootSounds.length > 0) {
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    shootSounds[level.random.nextInt(shootSounds.length)], SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    public static boolean isCharged(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_CHARGED);
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        stack.getOrCreateTag().putBoolean(TAG_CHARGED, charged);
    }

    public static int getChargeShots(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_CHARGED_SHOTS) : 0;
    }

    public static void setChargedShots(ItemStack stack, int shots) {
        stack.getOrCreateTag().putInt(TAG_CHARGED_SHOTS, shots);
    }

    protected static void addChargedProjectile(ItemStack stack, ItemStack ammo) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list;
        if (tag.contains(TAG_CHARGED_PROJECTILES, 9)) {
            list = tag.getList(TAG_CHARGED_PROJECTILES, 10);
        } else {
            list = new ListTag();
        }
        CompoundTag ct = new CompoundTag();
        ammo.save(ct);
        list.add(ct);
        tag.put(TAG_CHARGED_PROJECTILES, list);
    }

    protected static List<ItemStack> getChargedProjectiles(ItemStack stack) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_CHARGED_PROJECTILES, 9)) {
            ListTag lt = tag.getList(TAG_CHARGED_PROJECTILES, 10);
            for (int i = 0; i < lt.size(); i++) {
                list.add(ItemStack.of(lt.getCompound(i)));
            }
        }
        return list;
    }

    protected static void clearChargedProjectiles(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_CHARGED_PROJECTILES);
        }
    }

    public static boolean containsChargedProjectile(ItemStack stack, Item item) {
        return getChargedProjectiles(stack).stream().anyMatch(s -> s.is(item));
    }

    protected static float getShootingPower(ItemStack stack) {
        return containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? FIREWORK_POWER : ARROW_POWER;
    }

    protected static void onCrossbowShot(Level level, LivingEntity shooter, ItemStack crossbow) {
        if (shooter instanceof ServerPlayer sp) {
            if (!level.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(sp, crossbow);
            }
            sp.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
        if (getChargeShots(crossbow) <= 0) {
            clearChargedProjectiles(crossbow);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return getChargeDuration(stack) + 3;
    }

    public static int getChargeDuration(ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? MAX_CHARGE_DURATION : MAX_CHARGE_DURATION - 5 * i;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    protected static float getPowerForTime(int time, ItemStack stack) {
        float f = (float) time / getChargeDuration(stack);
        return Math.min(f, 1.0F);
    }

    protected float getVelocityMultiplier() {
        return 1.0F;
    }

    protected int getExtraPiercing() {
        return 0;
    }

    protected SoundEvent[] getShootSounds() {
        return new SoundEvent[]{ModSounds.COG_CROSSBOW_SHOOT_1.get(),
                ModSounds.COG_CROSSBOW_SHOOT_2.get(), ModSounds.COG_CROSSBOW_SHOOT_3.get()};
    }

    private SoundEvent getStartSound(int quickCharge) {
        return switch (quickCharge) {
            case 1 -> SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            case 2 -> SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            case 3 -> SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            default -> SoundEvents.CROSSBOW_LOADING_START;
        };
    }

    protected static float[] getShotPitches(RandomSource random) {
        boolean b = random.nextBoolean();
        return new float[]{1.0F, getRandomPitch(b, random), getRandomPitch(!b, random)};
    }

    protected static float getRandomPitch(boolean high, RandomSource random) {
        float f = high ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (!selected && !(player.getOffhandItem().getItem() instanceof CogCrossbowItem)) return;
        if (isCharged(stack)) return;

        long lastUse = stack.getOrCreateTag().getLong("LastUseTime");
        if (player.tickCount - lastUse < 100) return;

        if (player.getAbilities().instabuild) {
            ItemStack creativeAmmo = new ItemStack(Items.ARROW);
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() instanceof ArrowItem) {
                    creativeAmmo = invStack.copy();
                    break;
                }
                if (invStack.is(Items.FIREWORK_ROCKET)) {
                    creativeAmmo = invStack.copy();
                    break;
                }
            }
            addChargedProjectile(stack, creativeAmmo);
            setCharged(stack, true);
            int msBonusShots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0 ? 1 : 0;
            setChargedShots(stack, 5 + msBonusShots);
            stack.getOrCreateTag().putLong("LastUseTime", player.tickCount);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
            return;
        }

        int totalTaken = 0;
        int ammoSlot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack ammo = player.getInventory().getItem(i);
            if (ammo.getItem() instanceof ArrowItem) {
                ammoSlot = i;
                break;
            }
        }

        if (ammoSlot == -1) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack ammo = player.getInventory().getItem(i);
                if (ammo.is(Items.FIREWORK_ROCKET)) {
                    ammoSlot = i;
                    break;
                }
            }
        }

        if (ammoSlot >= 0) {
            ItemStack ammo = player.getInventory().getItem(ammoSlot);
            int msBonusShots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0 ? 1 : 0;
            int toTake = Math.min(ammo.getCount(), 5 + msBonusShots);
            addChargedProjectile(stack, ammo.split(toTake));
            totalTaken = toTake;
        }

        if (totalTaken > 0) {
            setCharged(stack, true);
            int msBonusShots = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0 ? 1 : 0;
            setChargedShots(stack, totalTaken);
            stack.getOrCreateTag().putLong("LastUseTime", player.tickCount);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }
}