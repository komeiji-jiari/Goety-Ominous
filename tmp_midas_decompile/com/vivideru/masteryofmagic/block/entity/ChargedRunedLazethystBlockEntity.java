/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  com.Polarice3.Goety.api.entities.ally.IServant
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.api.magic.IChargingSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.ISummonSpell
 *  com.Polarice3.Goety.common.blocks.ModBlocks
 *  com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity
 *  com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModAttributes
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.SEHelper
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.commands.arguments.EntityAnchorArgument$Anchor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.Connection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.common.util.FakePlayerFactory
 */
package com.vivideru.masteryofmagic.block.entity;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.ISummonSpell;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModAttributes;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.authlib.GameProfile;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import com.vivideru.masteryofmagic.magic.RunedLazethystHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class ChargedRunedLazethystBlockEntity
extends RandomizableContainerBlockEntity {
    private static final int RANGE = 16;
    private static final int CONTAINER_SIZE = 9;
    private static final int SUMMON_COOLDOWN_MIN = 140;
    private static final UUID NO_OWNER_UUID = new UUID(0L, 776L);
    private static final int SE_CAPACITY = 1000;
    private static final int SE_DRAIN_PER_SECOND = 20;
    private static final int SE_DRAIN_TICKS = 20;
    public boolean ISDUNGEON = false;
    private static final boolean DEBUG = false;
    private int storedSE = 0;
    private int seDrainTicker = 0;
    private SpellStat storedStats;
    private int castCooldownTicks = 0;
    private int activeTicks = 0;
    private int maxActiveTicks = 0;
    private ItemStack storedFocus = ItemStack.f_41583_;
    public UUID ownerUUID;
    private NonNullList<ItemStack> items = NonNullList.m_122780_((int)9, (Object)ItemStack.f_41583_);
    private FakePlayer caster;
    private ItemStack wand = ItemStack.f_41583_;
    private TargetMode targetMode = TargetMode.SHOOT;

    public ChargedRunedLazethystBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)GoetyMasteryOfMagicModBlockEntities.CHARGED_RUNED_LAZETHYST_BLOCK.get(), pos, state);
    }

    protected ChargedRunedLazethystBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargedRunedLazethystBlockEntity be) {
        IChargingSpell charging;
        Item item;
        if (level.f_46443_) {
            return;
        }
        be.pullSoulEnergy();
        be.m_6596_();
        if (!level.m_276867_(pos)) {
            be.resetActive();
            return;
        }
        if (be.castCooldownTicks > 0) {
            --be.castCooldownTicks;
            return;
        }
        if (be.storedFocus.m_41619_()) {
            be.resetActive();
            return;
        }
        ServerLevel server = (ServerLevel)level;
        be.caster = null;
        FakePlayer caster = be.getCaster(server, null);
        LivingEntity target = be.findTarget(server, pos, (LivingEntity)caster);
        if (target == null) {
            be.resetActive();
            return;
        }
        caster = be.getCaster(server, target);
        be.syncCasterFromOwner(server, caster);
        be.applyStatsToCaster(caster);
        caster.m_7618_(EntityAnchorArgument.Anchor.EYES, target.m_146892_());
        ItemStack wand = be.getWand();
        if (wand.m_41619_() || !((item = wand.m_41720_()) instanceof IWand)) {
            be.resetActive();
            return;
        }
        IWand iwand = (IWand)item;
        ISpell spell = iwand.getSpell(wand);
        if (spell == null) {
            be.resetActive();
            return;
        }
        be.applyStatsToCaster(caster);
        caster.m_7618_(EntityAnchorArgument.Anchor.EYES, target.m_146892_());
        int seCost = be.getSECost((LivingEntity)caster, wand, spell);
        boolean isSummon = spell instanceof ISummonSpell;
        if (!isSummon) {
            if (!caster.m_142582_((Entity)target)) {
                target = be.findTarget(server, pos, (LivingEntity)caster);
                if (target == null) {
                    be.resetActive();
                    return;
                }
                caster = be.getCaster(server, target);
                be.syncCasterFromOwner(server, caster);
                be.applyStatsToCaster(caster);
            }
        } else {
            caster = be.getCaster(server, target);
            be.syncCasterFromOwner(server, caster);
            be.applyStatsToCaster(caster);
            Player owner = server.m_46003_(be.ownerUUID);
            if (owner == null || !spell.conditionsMet(server, (LivingEntity)owner)) {
                be.resetActive();
                return;
            }
        }
        if (!iwand.isNotInstant(spell, (LivingEntity)caster, wand)) {
            if (!be.tryConsumeSE(seCost)) {
                return;
            }
            be.castSpell(server, caster, wand, spell);
            if (isSummon) {
                be.setSummonsWandering(server, 5);
            }
            be.castCooldownTicks = be.getCooldownForSpell((LivingEntity)caster, spell);
            be.markUpdated();
            return;
        }
        if (spell instanceof IChargingSpell && (charging = (IChargingSpell)spell).everCharge()) {
            if (be.activeTicks % 20 == 0 && !be.tryConsumeSE(seCost)) {
                return;
            }
            be.castSpell(server, caster, wand, spell);
            if (isSummon) {
                be.setSummonsWandering(server, 5);
            }
            ++be.activeTicks;
            if (be.activeTicks >= 20) {
                be.resetActive();
                be.castCooldownTicks = be.getCooldownForSpell((LivingEntity)caster, spell);
            }
            be.markUpdated();
            return;
        }
        if (be.activeTicks == 0) {
            be.maxActiveTicks = Math.max(1, spell.castDuration((LivingEntity)caster, wand));
        }
        ++be.activeTicks;
        if (be.activeTicks >= be.maxActiveTicks) {
            if (!be.tryConsumeSE(seCost)) {
                return;
            }
            be.castSpell(server, caster, wand, spell);
            if (isSummon) {
                be.setSummonsWandering(server, 5);
            }
            be.castCooldownTicks = be.getCooldownForSpell((LivingEntity)caster, spell);
            be.resetActive();
            be.markUpdated();
        }
    }

    private void pullSoulEnergy() {
        if (this.storedSE >= 1000) {
            return;
        }
        if (this.ISDUNGEON) {
            if (++this.seDrainTicker >= 20) {
                this.seDrainTicker = 0;
                this.storedSE = Math.min(1000, this.storedSE + 1);
                this.markUpdated();
            }
            return;
        }
        if (++this.seDrainTicker < 20) {
            return;
        }
        this.seDrainTicker = 0;
        int need = 1000 - this.storedSE;
        int toPull = Math.min(20, need);
        CursedCageBlockEntity cage = this.findAdjacentCage();
        if (cage != null && cage.getSouls() >= toPull) {
            cage.decreaseSouls(toPull);
            this.storedSE += toPull;
            this.markUpdated();
        }
    }

    private CursedCageBlockEntity findAdjacentCage() {
        for (Direction dir : Direction.values()) {
            BlockEntity blockEntity;
            BlockPos p = this.f_58858_.m_121945_(dir);
            if (!this.f_58857_.m_8055_(p).m_60713_((Block)ModBlocks.CURSED_CAGE_BLOCK.get()) || !((blockEntity = this.f_58857_.m_7702_(p)) instanceof CursedCageBlockEntity)) continue;
            CursedCageBlockEntity cage = (CursedCageBlockEntity)blockEntity;
            return cage;
        }
        return null;
    }

    public int getStoredSE() {
        return this.storedSE;
    }

    public int getSECapacity() {
        return 1000;
    }

    public boolean hasStoredSpell() {
        return !this.storedFocus.m_41619_();
    }

    private boolean tryConsumeSE(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (this.storedSE < amount) {
            return false;
        }
        this.storedSE -= amount;
        this.markUpdated();
        return true;
    }

    private int invokeInt(Object obj, String methodName, Object ... args) {
        try {
            for (Method m : obj.getClass().getMethods()) {
                Object r;
                if (!m.getName().equals(methodName) || m.getParameterCount() != args.length || !((r = m.invoke(obj, args)) instanceof Number)) continue;
                Number n = (Number)r;
                return n.intValue();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    private int getSECost(LivingEntity caster, ItemStack wand, ISpell spell) {
        Item item;
        int cost = 0;
        cost = this.invokeInt(spell, "SoulCost", caster, wand);
        if (cost <= 0) {
            cost = this.invokeInt(spell, "soulCost", caster, wand);
        }
        if (cost <= 0) {
            cost = this.invokeInt(spell, "getSoulCost", caster, wand);
        }
        if (cost <= 0 && (item = wand.m_41720_()) instanceof IWand) {
            IWand iwand = (IWand)item;
            cost = iwand.SoulUse(caster, wand);
        }
        return Math.max(0, cost);
    }

    public void onLoad() {
        super.onLoad();
        if (this.f_58857_ != null && !this.f_58857_.f_46443_) {
            this.m_6596_();
            this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
        }
    }

    private LivingEntity findTarget(ServerLevel level, BlockPos pos, LivingEntity caster) {
        Player owner = this.getOwner(level);
        boolean ignoreHostile = this.ownerUUID != null && this.ownerUUID.equals(NO_OWNER_UUID);
        List list = level.m_6443_(LivingEntity.class, new AABB(pos).m_82400_((double)this.getTargetRange()), arg_0 -> ChargedRunedLazethystBlockEntity.lambda$findTarget$0((LivingEntity)owner, ignoreHostile, arg_0));
        if (list.isEmpty()) {
            return null;
        }
        list.sort((a, b) -> Double.compare(caster.m_20280_((Entity)a), caster.m_20280_((Entity)b)));
        for (LivingEntity e : list) {
            if (!caster.m_142582_((Entity)e)) continue;
            return e;
        }
        return (LivingEntity)list.get(0);
    }

    private FakePlayer getCaster(ServerLevel level, LivingEntity target) {
        double dz;
        double dy;
        double dx;
        double len;
        Player owner;
        if (this.caster == null) {
            UUID id = this.ownerUUID != null ? this.ownerUUID : UUID.nameUUIDFromBytes(("gmm_laze_" + this.f_58858_).getBytes());
            this.caster = FakePlayerFactory.get((ServerLevel)level, (GameProfile)new GameProfile(id, "[GMM_Lazethyst]"));
            this.caster.m_143403_(GameType.CREATIVE);
        }
        if (this.ownerUUID != null && (owner = level.m_46003_(this.ownerUUID)) != null) {
            SEHelper.addAllyEntity((Player)owner, (LivingEntity)this.caster);
            SEHelper.addAllyEntity((Player)this.caster, (LivingEntity)owner);
            AABB box = new AABB(this.f_58858_).m_82400_(32.0);
            for (LivingEntity entity : level.m_6443_(LivingEntity.class, box, e -> e != null && e.m_6084_())) {
                LivingEntity entityOwner;
                OwnableEntity ownable;
                LivingEntity entityOwner2;
                boolean shouldBeAlly = false;
                if (entity instanceof OwnableEntity && (entityOwner2 = (ownable = (OwnableEntity)entity).m_269323_()) != null && entityOwner2.m_20148_().equals(this.ownerUUID)) {
                    shouldBeAlly = true;
                }
                if (!shouldBeAlly && (entityOwner = MobUtil.getOwner((Entity)entity)) != null && entityOwner.m_20148_().equals(this.ownerUUID)) {
                    shouldBeAlly = true;
                }
                if (!shouldBeAlly && SEHelper.getAllyEntities((Player)owner).contains(entity)) {
                    shouldBeAlly = true;
                }
                if (!shouldBeAlly && SEHelper.getAllyEntityTypes((Player)owner).contains(entity.m_6095_())) {
                    shouldBeAlly = true;
                }
                if (!shouldBeAlly) continue;
                SEHelper.addAllyEntity((Player)this.caster, (LivingEntity)entity);
            }
        }
        double x = (double)this.f_58858_.m_123341_() + 0.5;
        double y = (double)this.f_58858_.m_123342_() - 1.0;
        double z = (double)this.f_58858_.m_123343_() + 0.5;
        if (target != null && (len = Math.sqrt((dx = target.m_20185_() - x) * dx + (dy = target.m_20188_() + 1.0 - y) * dy + (dz = target.m_20189_() - z) * dz)) > 0.0) {
            x += dx / len;
            y += dy / len * 2.0;
            z += dz / len;
        }
        this.caster.m_7678_(x, y, z, 0.0f, 0.0f);
        this.caster.m_21219_();
        return this.caster;
    }

    private void syncCasterFromOwner(ServerLevel level, FakePlayer caster) {
        int i;
        Player owner = this.getOwner(level);
        if (owner == null) {
            return;
        }
        for (AttributeInstance src : owner.m_21204_().m_22170_()) {
            AttributeInstance dst = caster.m_21051_(src.m_22099_());
            if (dst == null) continue;
            dst.m_22100_(src.m_22115_());
            dst.m_22132_();
            for (AttributeModifier m : src.m_22122_()) {
                dst.m_22118_(m);
            }
        }
        for (i = 0; i < caster.m_150109_().f_35975_.size(); ++i) {
            caster.m_150109_().f_35975_.set(i, (Object)((ItemStack)owner.m_150109_().f_35975_.get(i)).m_41777_());
        }
        for (i = 0; i < caster.m_150109_().f_35976_.size(); ++i) {
            caster.m_150109_().f_35976_.set(i, (Object)((ItemStack)owner.m_150109_().f_35976_.get(i)).m_41777_());
        }
        this.applyStatsToCaster(caster);
    }

    private Player getOwner(ServerLevel level) {
        return this.ownerUUID != null ? level.m_46003_(this.ownerUUID) : null;
    }

    private ItemStack getWand() {
        if (this.wand.m_41619_() && this.f_58857_ != null) {
            ResourceLocation blockId = BuiltInRegistries.f_256975_.m_7981_((Object)this.f_58857_.m_8055_(this.f_58858_).m_60734_());
            RunedLazethystHandler.School school = RunedLazethystHandler.getSchoolFromBlock(blockId);
            this.wand = RunedLazethystHandler.createWandForSchool(school);
            SoulUsingItemHandler.get((ItemStack)this.wand).insertItem(0, this.storedFocus.m_41777_(), false);
        }
        return this.wand;
    }

    public void applyFocus(ItemStack focus, Player player) {
        this.storedFocus = focus.m_41777_();
        this.ownerUUID = player.m_20148_();
        this.caster = null;
        this.wand = ItemStack.f_41583_;
        this.castCooldownTicks = 0;
        this.resetActive();
        this.markUpdated();
    }

    protected void m_183515_(CompoundTag tag) {
        super.m_183515_(tag);
        ContainerHelper.m_18973_((CompoundTag)tag, this.items);
        tag.m_128405_("TargetMode", this.targetMode.ordinal());
        tag.m_128405_("StoredSE", this.storedSE);
        tag.m_128379_("ISDUNGEON", this.ISDUNGEON);
        if (!this.storedFocus.m_41619_()) {
            tag.m_128365_("StoredFocus", (Tag)this.storedFocus.m_41739_(new CompoundTag()));
        }
        if (this.ownerUUID != null) {
            tag.m_128362_("OwnerUUID", this.ownerUUID);
        }
        if (this.storedStats != null) {
            tag.m_128365_("StoredStats", (Tag)this.saveStats(this.storedStats));
        }
    }

    public void m_142466_(CompoundTag tag) {
        super.m_142466_(tag);
        this.items = NonNullList.m_122780_((int)9, (Object)ItemStack.f_41583_);
        ContainerHelper.m_18980_((CompoundTag)tag, this.items);
        int m = tag.m_128451_("TargetMode");
        TargetMode[] v = TargetMode.values();
        this.targetMode = m >= 0 && m < v.length ? v[m] : TargetMode.SHOOT;
        this.storedSE = tag.m_128451_("StoredSE");
        this.ISDUNGEON = tag.m_128471_("ISDUNGEON");
        this.storedFocus = tag.m_128441_("StoredFocus") ? ItemStack.m_41712_((CompoundTag)tag.m_128469_("StoredFocus")) : ItemStack.f_41583_;
        this.ownerUUID = tag.m_128403_("OwnerUUID") ? tag.m_128342_("OwnerUUID") : null;
        this.storedStats = tag.m_128441_("StoredStats") ? this.loadStats(tag.m_128469_("StoredStats")) : null;
        this.caster = null;
        this.wand = ItemStack.f_41583_;
        this.resetActive();
    }

    private CompoundTag saveStats(SpellStat stat) {
        CompoundTag t = new CompoundTag();
        t.m_128405_("Potency", stat.getPotency());
        t.m_128405_("Duration", stat.getDuration());
        t.m_128405_("Range", stat.getRange());
        t.m_128347_("Radius", stat.getRadius());
        t.m_128405_("Burning", stat.getBurning());
        t.m_128350_("Velocity", stat.getVelocity());
        return t;
    }

    private SpellStat loadStats(CompoundTag t) {
        return new SpellStat(t.m_128451_("Potency"), t.m_128451_("Duration"), t.m_128451_("Range"), t.m_128459_("Radius"), t.m_128451_("Burning"), t.m_128457_("Velocity"));
    }

    protected NonNullList<ItemStack> m_7086_() {
        return this.items;
    }

    protected void m_6520_(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public int m_6643_() {
        return 9;
    }

    protected Component m_6820_() {
        return Component.m_237113_((String)"Charged Runed Lazethyst");
    }

    protected AbstractContainerMenu m_6555_(int id, Inventory inv) {
        return ChestMenu.m_39255_((int)id, (Inventory)inv);
    }

    public void setSummonsWandering(ServerLevel level, int ticksWindow) {
        if (this.ownerUUID == null) {
            return;
        }
        Player owner = level.m_46003_(this.ownerUUID);
        if (owner == null) {
            return;
        }
        for (Entity entity : level.m_8583_()) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity)entity;
            if (!(entity instanceof IOwned)) continue;
            IOwned owned = (IOwned)entity;
            if (!(entity instanceof IServant)) continue;
            IServant servant = (IServant)entity;
            if (owned.getTrueOwner() != owner || !living.m_6084_() || living.f_19797_ > ticksWindow) continue;
            servant.setWandering(true);
        }
    }

    private int getCooldownForSpell(LivingEntity caster, ISpell spell) {
        int cd = spell.spellCooldown(caster);
        if (spell instanceof ISummonSpell) {
            ISummonSpell s = (ISummonSpell)spell;
            cd = Math.max(cd, s.SummonDownDuration());
            cd = Math.max(cd, 140);
        }
        return Math.max(1, cd);
    }

    private void resetActive() {
        this.activeTicks = 0;
        this.maxActiveTicks = 0;
    }

    private void markUpdated() {
        if (this.f_58857_ != null && !this.f_58857_.f_46443_) {
            this.m_6596_();
            this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 2);
        }
    }

    private void log(String s) {
    }

    public CompoundTag m_5995_() {
        CompoundTag tag = super.m_5995_();
        tag.m_128405_("StoredSE", this.storedSE);
        tag.m_128405_("TargetMode", this.targetMode.ordinal());
        if (!this.storedFocus.m_41619_()) {
            tag.m_128365_("StoredFocus", (Tag)this.storedFocus.m_41739_(new CompoundTag()));
        }
        if (this.ownerUUID != null) {
            tag.m_128362_("OwnerUUID", this.ownerUUID);
        }
        if (this.storedStats != null) {
            tag.m_128365_("StoredStats", (Tag)this.saveStats(this.storedStats));
        }
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.storedSE = tag.m_128451_("StoredSE");
        int m = tag.m_128451_("TargetMode");
        TargetMode[] v = TargetMode.values();
        this.targetMode = m >= 0 && m < v.length ? v[m] : TargetMode.SHOOT;
        this.storedFocus = tag.m_128441_("StoredFocus") ? ItemStack.m_41712_((CompoundTag)tag.m_128469_("StoredFocus")) : ItemStack.f_41583_;
        this.ownerUUID = tag.m_128403_("OwnerUUID") ? tag.m_128342_("OwnerUUID") : null;
        this.storedStats = tag.m_128441_("StoredStats") ? this.loadStats(tag.m_128469_("StoredStats")) : null;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.m_131708_();
        if (tag != null) {
            this.handleUpdateTag(tag);
        }
    }

    private void applyStatsToCaster(FakePlayer caster) {
        if (this.storedStats == null) {
            return;
        }
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_DURATION.get(), this.storedStats.getDuration());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_RANGE.get(), 20 + this.storedStats.getRange());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_RADIUS.get(), this.storedStats.getRadius());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_BURNING.get(), this.storedStats.getBurning());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.SPELL_VELOCITY.get(), this.storedStats.getVelocity());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.ABYSS_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.FROST_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.GEOMANCY_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.NECROMANCY_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.NETHER_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.STORM_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.VOID_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.WILD_POTENCY.get(), this.storedStats.getPotency());
        this.addBase((LivingEntity)caster, (Attribute)ModAttributes.WIND_POTENCY.get(), this.storedStats.getPotency());
    }

    private void addBase(LivingEntity e, Attribute attr, double value) {
        AttributeInstance inst = e.m_21051_(attr);
        if (inst != null && value != 0.0) {
            inst.m_22100_(inst.m_22115_() + value);
        }
    }

    private void prepareWand(ServerLevel level, FakePlayer caster, ItemStack wand) {
        caster.m_8061_(EquipmentSlot.MAINHAND, wand);
        wand.m_41720_().m_6883_(wand, (Level)level, (Entity)caster, 0, true);
        Item item = wand.m_41720_();
        if (item instanceof DarkWand) {
            DarkWand darkWand = (DarkWand)item;
            darkWand.setSpellConditions(darkWand.getSpell(wand), wand, (LivingEntity)caster);
        }
    }

    private void castSpell(ServerLevel server, FakePlayer caster, ItemStack wand, ISpell spell) {
        this.prepareWand(server, caster, wand);
        ((DarkWand)wand.m_41720_()).MagicResults(wand, (Level)server, (LivingEntity)caster, spell);
    }

    public void onPlaced() {
        Level level = this.f_58857_;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        for (Player player : server.m_6907_()) {
            CompoundTag tag = player.getPersistentData().m_128469_("gmm_pending_lazethyst");
            if (tag.m_128456_()) continue;
            ItemStack focus = ItemStack.m_41712_((CompoundTag)tag.m_128469_("Focus"));
            this.applyFocus(focus, player);
            player.getPersistentData().m_128473_("gmm_pending_lazethyst");
            break;
        }
    }

    public TargetMode getTargetMode() {
        return this.targetMode;
    }

    public int getTargetRange() {
        return this.targetMode.range;
    }

    public void cycleTargetMode() {
        this.targetMode = this.targetMode.next();
        this.markUpdated();
    }

    private static /* synthetic */ boolean lambda$findTarget$0(LivingEntity owner, boolean ignoreHostile, LivingEntity e) {
        Player p;
        return !(!e.m_6084_() || !e.m_6097_() || e instanceof Player && ((p = (Player)e).m_7500_() || p.m_5833_()) || owner != null && MobUtil.areAllies((Entity)owner, (Entity)e) || ignoreHostile && e instanceof Enemy);
    }

    public static enum TargetMode {
        SHOOT(16),
        CLOSE(4),
        AREA(32);

        public final int range;

        private TargetMode(int range) {
            this.range = range;
        }

        public TargetMode next() {
            TargetMode[] v = TargetMode.values();
            return v[(this.ordinal() + 1) % v.length];
        }
    }
}

