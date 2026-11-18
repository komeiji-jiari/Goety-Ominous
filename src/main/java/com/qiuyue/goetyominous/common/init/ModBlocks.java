package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.*;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.Polarice3.Goety.init.ModSoundTypes;
import com.qiuyue.goetyominous.common.items.PlushieBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
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

    public static final RegistryObject<Block> WHIRLING_CAGE =
            BLOCKS.register("whirling_cage", WhirlingCageBlock::new);

    public static final RegistryObject<Block> HEAVY_CORE = BLOCKS.register("heavy_core",
            () -> new HeavyCoreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .instrument(NoteBlockInstrument.SNARE)
                    .sound(HeavyCoreBlock.HEAVY_CORE_SOUND)
                    .strength(10.0F, 1200.0F)));

    public static final RegistryObject<Block> CURSED_METAL_GRATE = BLOCKS.register("cursed_metal_grate",
            () -> new HalfTransparentBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .noOcclusion()
                    .dynamicShape()
                    .sound(ModSoundTypes.MOD_METAL)));

    public static final RegistryObject<PolarCrystalBlock> POLAR_CRYSTAL = BLOCKS.register("polar_crystal",
            () -> new PolarCrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER)
                    .lightLevel(state -> state.getValue(PolarCrystalBlock.POWERED) ? 10 : 2)));

    public static final RegistryObject<WolfTotemBlock> WOLF_TOTEM =
            BLOCKS.register("wolf_totem", WolfTotemBlock::new);

    public static final RegistryObject<Block> CULT_STATUE =
            BLOCKS.register("cult_statue", CultStatueBlock::new);

    public static final RegistryObject<Block> GOLDEN_PIGLIN_STATUE =
            BLOCKS.register("golden_piglin_statue", GoldenPiglinStatueBlock::new);

    public static final List<RegistryObject<PlushieBlock>> PLUSHIES = new ArrayList<>();

    public static final RegistryObject<PlushieBlock> PLUSHIE_1122XIAOLD = plushie("plushie_1122xiaold", "1122xiaold");
    public static final RegistryObject<PlushieBlock> PLUSHIE_ADVCAGE = plushie("plushie_advcage", "advcage");
    public static final RegistryObject<PlushieBlock> PLUSHIE_ALASJ_XIAONIAO = plushie("plushie_alasj_xiaoniao", "alasj_xiaoniao");
    public static final RegistryObject<PlushieBlock> PLUSHIE_ANT_TENNA_1225 = plushie("plushie_ant_tenna_1225", "ant_tenna_1225");
    public static final RegistryObject<PlushieBlock> PLUSHIE_AWA_114514 = plushie("plushie_awa_114514", "awa_114514");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CLPOM = plushie("plushie_clpom", "clpom");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CRYOBSIDION = plushie("plushie_cryobsidion", "cryobsidion");
    public static final RegistryObject<PlushieBlock> PLUSHIE_CRYSTALSKELETON9 = plushie("plushie_crystalskeleton9", "crystalskeleton9");
    public static final RegistryObject<PlushieBlock> PLUSHIE_D0TH34 = plushie("plushie_d0th34", "d0th34");
    public static final RegistryObject<PlushieBlock> PLUSHIE_DH1003 = plushie("plushie_dh1003", "dh1003");
    public static final RegistryObject<PlushieBlock> PLUSHIE_DREAM = plushie("plushie_dream", "dream");
    public static final RegistryObject<PlushieBlock> PLUSHIE_EUNICE_CARRILL = plushie("plushie_eunice_carrill", "eunice_carrill");
    public static final RegistryObject<PlushieBlock> PLUSHIE_GUZHENGXING = plushie("plushie_guzhengxing", "guzhengxing");
    public static final RegistryObject<PlushieBlock> PLUSHIE_HELLHUNTER134 = plushie("plushie_hellhunter134", "hellhunter134");
    public static final RegistryObject<PlushieBlock> PLUSHIE_HIM = plushie("plushie_him", "him");
    public static final RegistryObject<PlushieBlock> PLUSHIE_HSZSDRX = plushie("plushie_hszsdrx", "hszsdrx");
    public static final RegistryObject<PlushieBlock> PLUSHIE_JIBINIPONG = plushie("plushie_jibinipong", "jibinipong");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LCW2X = plushie("plushie_lcw2x", "lcw2x");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LECUTEFOX = plushie("plushie_lecutefox", "lecutefox");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LGDLY = plushie("plushie_lgdly", "lgdly");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LLL152 = plushie("plushie_lll152", "lll152");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LUNATIC_RING = plushie("plushie_lunatic_ring", "lunatic_ring");
    public static final RegistryObject<PlushieBlock> PLUSHIE_LYUEZHUO = plushie("plushie_lyuezhuo", "lyuezhuo");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MAGICCAKE = plushie("plushie_magiccake", "magiccake");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MANBA = plushie("plushie_manba", "manba");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MELECHLYON = plushie("plushie_melechlyon", "melechlyon");
    public static final RegistryObject<PlushieBlock> PLUSHIE_MIKUMIKU39SUPER = plushie("plushie_mikumiku39super", "mikumiku39super");
    public static final RegistryObject<PlushieBlock> PLUSHIE_NANDOUAIIYA = plushie("plushie_nandouaiiya", "nandouaiiya");
    public static final RegistryObject<PlushieBlock> PLUSHIE_NOTCH = plushie("plushie_notch", "notch");
    public static final RegistryObject<PlushieBlock> PLUSHIE_OLDAZHAI = plushie("plushie_oldazhai", "oldazhai");
    public static final RegistryObject<PlushieBlock> PLUSHIE_OLDAZHAI_2 = plushie("plushie_oldazhai_2", "oldazhai_2");
    public static final RegistryObject<PlushieBlock> PLUSHIE_PIXCIEL15Y = plushie("plushie_pixciel15y", "pixciel15y");
    public static final RegistryObject<PlushieBlock> PLUSHIE_PLAYER_7 = plushie("plushie_player_7", "player_7");
    public static final RegistryObject<PlushieBlock> PLUSHIE_PLAYER_SEVEN = plushie("plushie_player_seven", "player_seven");
    public static final RegistryObject<PlushieBlock> PLUSHIE_PYKO01 = plushie("plushie_pyko01", "pyko01");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SALTEDFISH_TINA = plushie("plushie_saltedfish_tina", "saltedfish_tina");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SANWENYUREN = plushie("plushie_sanwenyuren", "sanwenyuren");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SAOXINGKE = plushie("plushie_saoxingke", "saoxingke");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SGDAYE = plushie("plushie_sgdaye", "sgdaye");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SKILLUPPER = plushie("plushie_skillupper", "skillupper");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SPDISH = plushie("plushie_spdish", "spdish");
    public static final RegistryObject<PlushieBlock> PLUSHIE_SYHXGOLDEN = plushie("plushie_syhxgolden", "syhxgolden");
    public static final RegistryObject<PlushieBlock> PLUSHIE_TAOSU233 = plushie("plushie_taosu233", "taosu233");
    public static final RegistryObject<PlushieBlock> PLUSHIE_TECHNOBLADE = plushie("plushie_technoblade", "technoblade");
    public static final RegistryObject<PlushieBlock> PLUSHIE_TENKENAMAINU = plushie("plushie_tenkenamainu", "tenkenamainu");
    public static final RegistryObject<PlushieBlock> PLUSHIE_UINVE = plushie("plushie_uinve", "uinve");
    public static final RegistryObject<PlushieBlock> PLUSHIE_WU1WU2 = plushie("plushie_wu1wu2", "wu1wu2");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XIAN_REN2 = plushie("plushie_xian_ren2", "xian_ren2");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XIN_TIRIS = plushie("plushie_xin_tiris", "xin_tiris");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XINJIMU = plushie("plushie_xinjimu", "xinjimu");
    public static final RegistryObject<PlushieBlock> PLUSHIE_XUEYUEFENG = plushie("plushie_xueyuefeng", "xueyuefeng");

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