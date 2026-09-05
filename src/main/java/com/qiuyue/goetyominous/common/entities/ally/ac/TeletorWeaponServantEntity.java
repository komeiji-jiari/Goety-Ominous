package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.google.common.collect.Multimap;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * 磁流灵兵刃(Teletor Servant 的悬浮武器):以 AC 原版 MagneticWeaponEntity 为蓝本移植,
 * 但只保留"由 TeletorServant 操控"的分支——删掉玩家手持伽楞手甲遥控的整套逻辑
 * (挖掘方块、进玩家背包、玩家相关成就/附魔槽等)。
 *
 * <p>工作方式与 AC 原版一致:它是一枚普通的 {@link Entity}(无重力),由主人
 * {@link TeletorServant} 每 tick 通过 {@link #setControllerUUID} 认领。双方各持一套同步数据:
 * 武器把主人的 {@code CONTROLLER_ID}、攻击对象 {@code TARGET_ID} 写进自己的 entityData,
 * 主人端读 {@code WEAPON_ID} 回指武器。客户端经 {@code CONTROLLER_ID}/{@code TARGET_ID}
 * 两个 int 在本地取实体做渲染插值,服务器按 UUID 从 {@link ServerLevel} 查实体。</p>
 */
public class TeletorWeaponServantEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(TeletorWeaponServantEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<UUID>> CONTROLLER_UUID = SynchedEntityData.defineId(TeletorWeaponServantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CONTROLLER_ID = SynchedEntityData.defineId(TeletorWeaponServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(TeletorWeaponServantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IDLING = SynchedEntityData.defineId(TeletorWeaponServantEntity.class, EntityDataSerializers.BOOLEAN);

    private float prevStrikeProgress;
    private float strikeProgress;
    private float prevReturnProgress;
    private float returnProgress;
    private boolean comingBack = false;
    public boolean returnFlag = false;

    /** 自动生成的自带武器会打上此隐藏标记;玩家赠予的工具没有该标记,视为玩家财产,移除时掉回而非销毁。 */
    private static final String GENERATED_TOOL_TAG = "goetyominous_teletor_generated";

    public TeletorWeaponServantEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // 客户端生成(Forge PlayMessages 自定义工厂走这条构造)。
    public TeletorWeaponServantEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.TELETOR_WEAPON_SERVANT.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
        this.entityData.define(CONTROLLER_UUID, Optional.empty());
        this.entityData.define(CONTROLLER_ID, -1);
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(IDLING, true);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevStrikeProgress = this.strikeProgress;
        this.prevReturnProgress = this.returnProgress;
        Entity controller = this.getController();
        Entity target = this.getTarget();
        if (!this.level().isClientSide) {
            this.noPhysics = this.comingBack || controller instanceof TeletorServant && (target == null || !target.isAlive());
            // 落单/空手自毁:先把玩家赠予的工具掉回而不是销毁(主人仆从的维护逻辑会另行补造,见 TeletorServant#tickWeaponMaintenance)。
            if ((controller == null && this.tickCount > 20) || this.getItemStack().isEmpty()) {
                this.dropWeaponStackIfPlayerProvided(null);
                this.remove(Entity.RemovalReason.DISCARDED);
                return;
            }
        }
        if ((target == null || this.comingBack) && this.strikeProgress > 0.0F) {
            this.strikeProgress = Math.max(0.0F, this.strikeProgress - 0.1F);
        }
        if (controller instanceof TeletorServant teletor) {
            this.entityData.set(CONTROLLER_ID, teletor.getId());
            teletor.setWeaponUUID(this.getUUID());
            if (!this.level().isClientSide) {
                LivingEntity e = teletor.getTarget();
                this.entityData.set(TARGET_ID, e != null && e.isAlive() ? e.getId() : -1);
            }
            boolean attacking = !this.comingBack && target != null && target.isAlive();
            Vec3 vec3 = attacking ? target.getEyePosition() : teletor.getWeaponPosition();
            Vec3 want = vec3.subtract(this.position());
            if (target != null && !this.comingBack) {
                this.entityData.set(IDLING, false);
                if (want.length() < (double) (target.getBbWidth() + 1.0F)) {
                    if (this.strikeProgress < 1.0F) {
                        this.strikeProgress = Math.max(0.0F, this.strikeProgress + 0.35F);
                    } else {
                        this.hurtEntity(teletor, target);
                        if (this.isRemoved()) {
                            return;
                        }
                        this.comingBack = true;
                    }
                } else if (want.length() > 32.0D) {
                    this.comingBack = true;
                }
            }
            this.directMovementTowards(vec3, 0.1F);
            if (this.distanceTo(controller) < 2.5F && this.getY() > controller.getY()) {
                this.entityData.set(IDLING, true);
                if (this.comingBack) {
                    this.comingBack = false;
                }
            }
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
    }

    private void hurtEntity(LivingEntity holder, Entity target) {
        ItemStack itemStack = this.getItemStack();
        float f = (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE) + (float) this.getDamageForItem(itemStack);
        float f1 = (float) holder.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(itemStack, ((LivingEntity) target).getMobType());
            f1 += (float) EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, holder);
        }
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, itemStack);
        if (i > 0) {
            target.setSecondsOnFire(i * 4);
        }
        if (target.hurt(this.damageSources().mobAttack(holder), f)) {
            holder.doEnchantDamageEffects(holder, target);
            this.damageItem(1);
            if (this.isRemoved()) {
                return; // 工具在本次挥击中耗尽,兵刃已自毁
            }
            if (f1 > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity) target).knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180.0F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180.0F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
        }
        if (this.isOnFire()) {
            target.setSecondsOnFire(5);
        }
    }

    private void damageItem(int damageAmount) {
        Entity entity = this.getController();
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        ItemStack stack = this.getItemStack();
        if (!stack.isDamageableItem() || stack.isEmpty() || stack.getDamageValue() >= stack.getMaxDamage()) {
            return;
        }
        // 复刻原版 hurtAndBreak 的耐久消耗 + Unbreaking 减免;但耗尽时不销毁物品,
        // 而是把破损工具交还(玩家赠予的才掉)、兵刃自毁,由主人补造默认武器(见 TeletorServant#onWeaponDestroyed)。
        int applied = damageAmount;
        int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (unbreaking > 0) {
            for (int i = 0; i < damageAmount; ++i) {
                if (living.getRandom().nextInt(unbreaking + 1) > 0) {
                    --applied;
                }
            }
        }
        applied = Math.max(0, applied);
        if (applied <= 0) {
            return;
        }
        stack.setDamageValue(stack.getDamageValue() + applied);
        this.setItemStack(stack.copy()); // copy 触发脏检测,把新耐久同步出去
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.setDamageValue(stack.getMaxDamage());
            this.setItemStack(stack.copy());
            living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            this.dropWeaponStackIfPlayerProvided(entity);
            if (entity instanceof TeletorServant teletor) {
                teletor.onWeaponDestroyed();
            }
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    /** 主人仆从死亡/被移除时调用:玩家赠予的工具就地掉回后移除兵刃,不再凭空销毁。 */
    public void handleOwnerDeath() {
        if (this.level().isClientSide || this.isRemoved()) {
            return;
        }
        Entity controller = this.getController();
        this.dropWeaponStackIfPlayerProvided(controller != null ? controller : this);
        this.remove(Entity.RemovalReason.KILLED);
    }

    /**
     * 玩家赠予的工具在兵刃被移除时应掉回给主人附近,而不是销毁(自动生成的自带工具不返还)。
     * 掉落后清空自身物品,避免后续移除路径重复掉落。
     */
    public void dropWeaponStackIfPlayerProvided(@Nullable Entity dropAnchor) {
        if (this.level().isClientSide || this.isRemoved()) {
            return;
        }
        ItemStack stack = this.getItemStack();
        if (stack.isEmpty() || !isPlayerProvided(stack)) {
            return;
        }
        Entity anchor = dropAnchor != null ? dropAnchor : this;
        Containers.dropItemStack(this.level(), anchor.getX(), anchor.getY() + 0.25D, anchor.getZ(), stack.copy());
        this.setItemStack(ItemStack.EMPTY);
    }

    /** 是否为玩家赠予的工具(非"自动生成"自带武器);TeletorServant 换装返还判断也用它。 */
    public static boolean isPlayerProvided(ItemStack stack) {
        return !stack.isEmpty() && (!stack.hasTag() || !stack.getTag().getBoolean(GENERATED_TOOL_TAG));
    }

    /** 给自动生成的自带武器打上隐藏标记。 */
    public static ItemStack markAsGenerated(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.getOrCreateTag().putBoolean(GENERATED_TOOL_TAG, true);
        }
        return stack;
    }

    /** 玩家赠予的工具去掉"自动生成"标记,视为玩家财产。 */
    public static ItemStack markAsPlayerProvided(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag()) {
            stack.getTag().remove(GENERATED_TOOL_TAG);
        }
        return stack;
    }

    private void directMovementTowards(Vec3 moveTo, float speed) {
        Vec3 want = moveTo.subtract(this.position());
        if (want.length() > 1.0D) {
            want = want.normalize();
        }
        float targetXRot = (float) (-(Mth.atan2(want.y, want.horizontalDistance()) * 57.2957763671875));
        float targetYRot = (float) (-Mth.atan2(want.x, want.z) * 57.2957763671875);
        if (this.isIdling()) {
            targetXRot = this.getXRot();
            targetYRot = this.getYRot() + 5.0F;
        }
        this.setXRot(Mth.approachDegrees(this.getXRot(), targetXRot, 5.0F));
        this.setYRot(Mth.approachDegrees(this.getYRot(), targetYRot, 5.0F));
        this.setDeltaMovement(this.getDeltaMovement().add(want.scale(speed)));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("WeaponStack")) {
            this.setItemStack(ItemStack.of(tag.getCompound("WeaponStack")));
        }
        if (tag.hasUUID("ControllerUUID")) {
            this.setControllerUUID(tag.getUUID("ControllerUUID"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItemStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.getItemStack().save(stackTag);
            tag.put("WeaponStack", stackTag);
        }
        if (this.getControllerUUID() != null) {
            tag.putUUID("ControllerUUID", this.getControllerUUID());
        }
    }

    public double getDamageForItem(ItemStack itemStack) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        if (!map.isEmpty()) {
            double d = 0.0D;
            for (AttributeModifier modifier : map.get(Attributes.ATTACK_DAMAGE)) {
                d += modifier.getAmount();
            }
            return d;
        }
        return 0.0D;
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
    }

    public boolean isIdling() {
        return this.entityData.get(IDLING);
    }

    @Nullable
    public UUID getControllerUUID() {
        return this.entityData.get(CONTROLLER_UUID).orElse(null);
    }

    public void setControllerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(CONTROLLER_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public Entity getController() {
        if (!this.level().isClientSide) {
            UUID id = this.getControllerUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(CONTROLLER_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    @Nullable
    public Entity getTarget() {
        int id = this.entityData.get(TARGET_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    public float getStrikeProgress(float partialTick) {
        return this.prevStrikeProgress + (this.strikeProgress - this.prevStrikeProgress) * partialTick;
    }

    public float getReturnProgress(float partialTick) {
        return this.prevReturnProgress + (this.returnProgress - this.prevReturnProgress) * partialTick;
    }
}
