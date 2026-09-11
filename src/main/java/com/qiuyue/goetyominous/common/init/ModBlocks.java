package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.PiglinMerchantSpawnerBlock;
import com.qiuyue.goetyominous.common.blocks.PlushieBlock;
import com.qiuyue.goetyominous.common.blocks.WolfTotemBlock;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.PlushieBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GoetyOminous.MOD_ID);

    public static final RegistryObject<Block> PIGLIN_MERCHANT_SPAWNER =
            BLOCKS.register("piglin_merchant_spawner", PiglinMerchantSpawnerBlock::new);

    public static final RegistryObject<WolfTotemBlock> WOLF_TOTEM =
            BLOCKS.register("wolf_totem", WolfTotemBlock::new);

    public static final List<RegistryObject<PlushieBlock>> PLUSHIES = new ArrayList<>();

    public static final RegistryObject<PlushieBlock> PLUSHIE_ALASJ_XIAONIAO = plushie("plushie_alasj_xiaoniao", "alasj_xiaoniao");
    public static final RegistryObject<PlushieBlock> PLUSHIE_ANT_TENNA_1225 = plushie("plushie_ant_tenna_1225", "ant_tenna_1225");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CLPOM = plushie("plushie_clpom", "clpom");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CRYOBSIDION = plushie("plushie_cryobsidion", "cryobsidion");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CRYSTALSKELETON9 = plushie("plushie_crystalskeleton9", "crystalskeleton9");
    public static final RegistryObject<PlushieBlock> PLUSHIE_HIM = plushie("plushie_him", "him");
    public static final RegistryObject<PlushieBlock> PLUSHIE_JIBINIPONG = plushie("plushie_jibinipong", "jibinipong");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LCW2X = plushie("plushie_lcw2x", "lcw2x");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LECUTEFOX = plushie("plushie_lecutefox", "lecutefox");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MANBA = plushie("plushie_manba", "manba");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MELECHLYON = plushie("plushie_melechlyon", "melechlyon");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MIKUMIKU39SUPER = plushie("plushie_mikumiku39super", "mikumiku39super");
    public static final RegistryObject<PlushieBlock> PLUSHIE_NANDOUAIIYA = plushie("plushie_nandouaiiya", "nandouaiiya");
    public static final RegistryObject<PlushieBlock> PLUSHIE_OLDAZHAI = plushie("plushie_oldazhai", "oldazhai");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SALTEDFISH_TINA = plushie("plushie_saltedfish_tina", "saltedfish_tina");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SAOXINGKE = plushie("plushie_saoxingke", "saoxingke");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SPDISH = plushie("plushie_spdish", "spdish");
    public static final RegistryObject<PlushieBlock> PLUSHIE_TENKENAMAINU = plushie("plushie_tenkenamainu", "tenkenamainu");
    public static final RegistryObject<PlushieBlock> PLUSHIE_UINVE = plushie("plushie_uinve", "uinve");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XIAN_REN2 = plushie("plushie_xian_ren2", "xian_ren2");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XIN_TIRIS = plushie("plushie_xin_tiris", "xin_tiris");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XINJIMU = plushie("plushie_xinjimu", "xinjimu");

    private static RegistryObject<PlushieBlock> plushie(String name, String texture) {
        RegistryObject<PlushieBlock> block = BLOCKS.register(name, () -> new PlushieBlock(texture));
        ModItems.ITEMS.register(name, () -> new PlushieBlockItem(block.get()));
        PLUSHIES.add(block);
        return block;
    }

    public static Block[] plushieBlocks() {
        return PLUSHIES.stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}