package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.Goety;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ModLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
    private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
    public static final ResourceLocation EMPTY = new ResourceLocation("empty");
    public static final ResourceLocation CRYPT_TOMB = register("chests/crypt_tomb");
    public static final ResourceLocation TALL_SKULL = register("entities/tall_skull_mobs");
    public static final ResourceLocation PLAYER_WITCH = register("entities/player_witch");
    public static final ResourceLocation CULTISTS = register("entities/cultist_extra");
    public static final ResourceLocation CRYPT_SLIME = register("entities/crypt_slime");
    public static final ResourceLocation TROPICAL_SLIME = register("entities/tropical_slime");
    public static final ResourceLocation INFERNO = register("entities/inferno_extra");
    public static final ResourceLocation APOSTLE_HARD = register("entities/apostle_2");
    public static final ResourceLocation NATURAL_SIGNALER = register("entities/natural/signaler");
    public static final ResourceLocation NATURAL_VINDICATOR_CHEF = register("entities/natural/vindicator_chef");
    public static final ResourceLocation NATURAL_MOUNTAINEER = register("entities/natural/mountaineer");
    public static final ResourceLocation NATURAL_GEOMANCER = register("entities/natural/geomancer");
    public static final ResourceLocation NATURAL_ICEOLOGER = register("entities/natural/iceologer");
    public static final ResourceLocation NATURAL_WIND_CALLER = register("entities/natural/wind_caller");
    public static final ResourceLocation WITCH_BARTER = register("gameplay/witch_bartering");
    public static final ResourceLocation WARLOCK_BARTER = register("gameplay/warlock_bartering");
    public static final ResourceLocation MAVERICK_BARTER = register("gameplay/maverick_bartering");
    public static final ResourceLocation REPROBATE_BARTER = register("gameplay/reprobate_bartering");
    public static final ResourceLocation HERETIC_BARTER = register("gameplay/heretic_bartering");
    public static final ResourceLocation CRONE_BARTER = register("gameplay/crone_bartering");
    public static final ResourceLocation HERESIARCH_BARTER = register("gameplay/heresiarch_bartering");
    public static final ResourceLocation TREASURE_POUCH = register("gameplay/treasure_pouch");
    public static final ResourceLocation VOID_SPAWNER_LOOT = register("gameplay/void_spawner_loot");
    public static final ResourceLocation VOID_SPAWNER_KEY = register("gameplay/void_spawner_key");
    public static final ResourceLocation VOID_VAULT_REWARD = register("gameplay/void_vault_reward");

    private static ResourceLocation register(String pId) {
        return register(Goety.location(pId));
    }

    private static ResourceLocation register(ResourceLocation pId) {
        if (LOCATIONS.add(pId)) {
            return pId;
        } else {
            throw new IllegalArgumentException(pId + " is already a registered built-in loot table");
        }
    }

    public static LootParams.Builder createLootParams(LivingEntity target, boolean checkPlayerKill, DamageSource source) {
        LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel)target.m_9236_())).m_287286_(LootContextParams.f_81455_, target).m_287286_(LootContextParams.f_81460_, target.m_20182_()).m_287286_(LootContextParams.f_81457_, source).m_287289_(LootContextParams.f_81458_, source.m_7639_()).m_287289_(LootContextParams.f_81459_, source.m_7640_());
        if (checkPlayerKill) {
            LivingEntity var5 = target.m_21232_();
            if (var5 instanceof Player) {
                Player player = (Player)var5;
                lootcontext$builder = lootcontext$builder.m_287286_(LootContextParams.f_81456_, player).m_287239_(player.m_36336_());
            }
        }

        return lootcontext$builder;
    }

    public static void shuffleAndSplitItems(ObjectArrayList<ItemStack> p_230925_, int p_230926_, RandomSource p_230927_) {
        List<ItemStack> list = Lists.newArrayList();
        Iterator<ItemStack> iterator = p_230925_.iterator();

        while(iterator.hasNext()) {
            ItemStack itemstack = (ItemStack)iterator.next();
            if (itemstack.m_41619_()) {
                iterator.remove();
            } else if (itemstack.m_41613_() > 1) {
                list.add(itemstack);
                iterator.remove();
            }
        }

        while(p_230926_ - p_230925_.size() - list.size() > 0 && !list.isEmpty()) {
            ItemStack itemstack2 = (ItemStack)list.remove(Mth.m_216271_(p_230927_, 0, list.size() - 1));
            int i = Mth.m_216271_(p_230927_, 1, itemstack2.m_41613_() / 2);
            ItemStack itemstack1 = itemstack2.m_41620_(i);
            if (itemstack2.m_41613_() > 1 && p_230927_.m_188499_()) {
                list.add(itemstack2);
            } else {
                p_230925_.add(itemstack2);
            }

            if (itemstack1.m_41613_() > 1 && p_230927_.m_188499_()) {
                list.add(itemstack1);
            } else {
                p_230925_.add(itemstack1);
            }
        }

        p_230925_.addAll(list);
        Util.m_214673_(p_230925_, p_230927_);
    }

    public static void createLootChest(LivingEntity target, BlockState blockState, BlockPos blockPos, DamageSource cause) {
        if (target.f_19853_.m_7654_() != null) {
            target.f_19853_.m_46597_(blockPos, blockState);
            LootParams lootParams = createLootParams(target, true, cause).m_287235_(LootContextParamSets.f_81415_);
            LootTable table = target.f_19853_.m_7654_().m_278653_().m_278676_(target.m_5743_());
            ObjectArrayList<ItemStack> lootItems = table.m_287195_(lootParams);
            List<Integer> availableSlots = getAvailableSlots(target.m_217043_());
            shuffleAndSplitItems(lootItems, availableSlots.size(), target.m_217043_());
            NonNullList<ItemStack> finalLoot = NonNullList.m_122780_(27, ItemStack.f_41583_);
            ObjectListIterator container = lootItems.iterator();

            while(container.hasNext()) {
                ItemStack itemstack = (ItemStack)container.next();
                if (!availableSlots.isEmpty()) {
                    if (itemstack.m_41619_()) {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), ItemStack.f_41583_);
                    } else {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), itemstack);
                    }
                }
            }

            BlockEntity var12 = target.f_19853_.m_7702_(blockPos);
            if (var12 instanceof Container) {
                Container container = (Container)var12;

                for(int i = 0; i < container.m_6643_(); ++i) {
                    container.m_6836_(i, (ItemStack)finalLoot.get(i));
                }
            }
        }

    }

    public static List<Integer> getAvailableSlots(RandomSource random) {
        ObjectArrayList<Integer> arrayList = new ObjectArrayList();

        for(int i = 0; i < 27; ++i) {
            arrayList.add(i);
        }

        Util.m_214673_(arrayList, random);
        return arrayList;
    }

    public static Set<ResourceLocation> all() {
        return IMMUTABLE_LOCATIONS;
    }
}
