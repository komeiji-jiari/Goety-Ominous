package com.qiuyue.goetyominous.common.items.mm;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.mm.HogChargeSpell;
import com.qiuyue.goetyominous.common.magic.spells.mm.WitherBreathSpell;
import com.qiuyue.goetyominous.common.magic.spells.mm.WitherSlashSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
/**
 * MM 联动物品注册类
 * 负责注册所有 MM 仆从相关的物品（如刷怪蛋）
 * 注意：这个类只在 MM 模组加载时才会被调用
 */
public class MmItems {

    /**
     * UA 物品延迟注册表
     */
    public static final DeferredRegister<Item> MM_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_WITHER_SKELETON_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_wither_skeleton_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT, 0x2C3E50, 0x8E44AD, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MUTANT_HOGLIN_SERVANT_SPAWN_EGG = MM_ITEMS.register(
            "mutant_hoglin_servant_spawn_egg",
            () -> new ServantSpawnEggItem(MmEntityRegistry.MUTANT_HOGLIN_SERVANT, 0x4F2A1D, 0xC8A082, egg()));

    public static final RegistryObject<MagicFocus> WITHER_BREATH_FOCUS = MM_ITEMS.register(
            "wither_breath_focus",
            () -> new MagicFocus(new WitherBreathSpell()));

    public static final RegistryObject<MagicFocus> WITHER_SLASH_FOCUS = MM_ITEMS.register(
            "wither_slash_focus",
            () -> new MagicFocus(new WitherSlashSpell()));

    public static final RegistryObject<MagicFocus> HOG_CHARGE_FOCUS = MM_ITEMS.register(
            "hog_charge_focus",
            () -> new MagicFocus(new HogChargeSpell()));

    public static final RegistryObject<WitherScytheItem> WITHER_SCYTHE = MM_ITEMS.register(
            "wither_scythe", WitherScytheItem::new);




    /**
     * 物品属性配置方法
     * @return 基础物品属性配置
     */
    public static Item.Properties egg() {
        return new Item.Properties();
    }

    /**
     * 注册 MM 物品到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        MM_ITEMS.register(modEventBus);
    }
}
