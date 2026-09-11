package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.DarkHatModel;
import com.Polarice3.Goety.client.render.model.DarkRobeModel;
import com.Polarice3.Goety.client.render.model.EternalCauldronModel;
import com.Polarice3.Goety.client.render.model.GloveModel;
import com.Polarice3.Goety.client.render.model.MiscCuriosModel;
import com.Polarice3.Goety.client.render.model.NecroCapeModel;
import com.Polarice3.Goety.client.render.model.UnholyHatModel;
import com.Polarice3.Goety.client.render.model.WitchHatModel;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRenderer {
    public static String folderPath = "textures/models/curios/";

    public static ResourceLocation render(String textureName) {
        return Goety.location(folderPath + textureName);
    }

    public static void register() {
        CuriosRendererRegistry.register((Item)ModItems.DARK_HAT.get(), () -> new WearRenderer(render("dark_hat.png"), new DarkHatModel(bakeLayer(ModModelLayer.DARK_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.GRAND_TURBAN.get(), () -> new WearRenderer(render("grand_turban.png"), new DarkHatModel(bakeLayer(ModModelLayer.GRAND_TURBAN))));
        CuriosRendererRegistry.register((Item)ModItems.FROST_CROWN.get(), () -> new WearRenderer(render("frost_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.WIND_CROWN.get(), () -> new WearRenderer(render("wind_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.STORM_CROWN.get(), () -> new WearRenderer(render("storm_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.WILD_CROWN.get(), () -> new WearRenderer(render("wild_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.ABYSS_CROWN.get(), () -> new WearRenderer(render("abyss_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.VOID_CROWN.get(), () -> new WearRenderer(render("void_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.NETHER_CROWN.get(), () -> new WearRenderer(render("nether_crown.png"), new DarkHatModel(bakeLayer(ModModelLayer.IRON_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.WITCH_HAT.get(), () -> new WearRenderer(render("witch_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.WITCH_HAT_HEDGE.get(), () -> new WearRenderer(render("witch_hat_hedge.png"), new WitchHatModel(bakeLayer(ModModelLayer.WITCH_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.CRONE_HAT.get(), () -> new WearRenderer(render("crone_hat.png"), new WitchHatModel(bakeLayer(ModModelLayer.CRONE_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.UNHOLY_HAT.get(), () -> new WearRenderer(render("unholy_hat.png"), new UnholyHatModel(bakeLayer(ModModelLayer.UNHOLY_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.UNHOLY_HAT_HALO.get(), () -> new WearRenderer(render("unholy_hat_halo.png"), new UnholyHatModel(bakeLayer(ModModelLayer.UNHOLY_HAT))));
        CuriosRendererRegistry.register((Item)ModItems.DARK_ROBE.get(), () -> new WearRenderer(render("dark_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.DARK_ROBE_FANCY.get(), () -> new WearRenderer(render("dark_robe_fancy.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.GRAND_ROBE.get(), () -> new WearRenderer(render("grand_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.GRAND_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.ILLUSION_ROBE.get(), () -> new WearRenderer(render("illusion_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.ILLUSION_ROBE_MIRROR.get(), () -> new WearRenderer(render("illusion_robe_mirror.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.GEO_ROBE.get(), () -> new WearRenderer(render("geo_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.FROST_ROBE.get(), () -> new WearRenderer(render("frost_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.FROST_ROBE_CRYO.get(), () -> new WearRenderer(render("frost_robe_cryo.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WIND_ROBE.get(), () -> new WearRenderer(render("wind_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.STORM_ROBE.get(), () -> new WearRenderer(render("storm_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WILD_ROBE.get(), () -> new WearRenderer(render("wild_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.ABYSS_ROBE.get(), () -> new WearRenderer(render("abyss_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.VOID_ROBE.get(), () -> new WearRenderer(render("void_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.NECRO_CROWN.get(), () -> new WearRenderer(render("necro_cape.png"), new NecroCapeModel(bakeLayer(ModModelLayer.NECRO_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.NECRO_CAPE.get(), () -> new WearRenderer(render("necro_cape.png"), new NecroCapeModel(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register((Item)ModItems.NAMELESS_CROWN.get(), () -> new WearRenderer(render("nameless_cape.png"), new NecroCapeModel(bakeLayer(ModModelLayer.NAMELESS_CROWN))));
        CuriosRendererRegistry.register((Item)ModItems.NAMELESS_CAPE.get(), () -> new WearRenderer(render("nameless_cape.png"), new NecroCapeModel(bakeLayer(ModModelLayer.NECRO_CAPE))));
        CuriosRendererRegistry.register((Item)ModItems.WITCH_ROBE.get(), () -> new WearRenderer(render("witch_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WITCH_ROBE_HEDGE.get(), () -> new WearRenderer(render("witch_robe_hedge.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WARLOCK_ROBE.get(), () -> new WearRenderer(render("warlock_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WARLOCK_ROBE_DARK.get(), () -> new WearRenderer(render("warlock_robe_dark.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.WARLOCK_SASH.get(), () -> new WearRenderer(render("warlock_sash.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.NETHER_ROBE.get(), () -> new WearRenderer(render("nether_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.NETHER_ROBE_WARPED.get(), () -> new WearRenderer(render("nether_robe_warped.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.UNHOLY_ROBE.get(), () -> new WearRenderer(render("unholy_robe.png"), new DarkRobeModel(bakeLayer(ModModelLayer.DARK_ROBE))));
        CuriosRendererRegistry.register((Item)ModItems.PENDANT_OF_HUNGER.get(), () -> new WearRenderer(render("pendant_of_hunger.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register((Item)ModItems.SEA_AMULET.get(), () -> new WearRenderer(render("sea_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register((Item)ModItems.STAR_AMULET.get(), () -> new WearRenderer(render("star_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register((Item)ModItems.WAYFARERS_BELT.get(), () -> new WearRenderer(render("wayfarers_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register((Item)ModItems.FELINE_AMULET.get(), () -> new WearRenderer(render("feline_amulet.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMULET))));
        CuriosRendererRegistry.register((Item)ModItems.SPITEFUL_BELT.get(), () -> new WearRenderer(render("spiteful_belt.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BELT))));
        CuriosRendererRegistry.register((Item)ModItems.FOCUS_BAG.get(), () -> new WearRenderer(render("focus_bag.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.FOCUS_BAG))));
        CuriosRendererRegistry.register((Item)ModItems.BREW_BAG.get(), () -> new WearRenderer(render("brew_bag.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.BREW_BAG))));
        CuriosRendererRegistry.register((Item)ModItems.AMETHYST_NECKLACE.get(), () -> new WearRenderer(render("amethyst_necklace.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.AMETHYST_NECKLACE))));
        CuriosRendererRegistry.register((Item)ModItems.TARGETING_MONOCLE.get(), () -> new WearRenderer(render("targeting_monocle.png"), new MiscCuriosModel(bakeLayer(ModModelLayer.MONOCLE))));
        CuriosRendererRegistry.register((Item)ModItems.GRAVE_GLOVE.get(), () -> new WearRenderer(render("grave_glove.png"), new GloveModel(bakeLayer(ModModelLayer.GLOVE))));
        CuriosRendererRegistry.register((Item)ModItems.THRASH_GLOVE.get(), () -> new WearRenderer(render("thrash_glove.png"), new GloveModel(bakeLayer(ModModelLayer.GLOVE))));
        CuriosRendererRegistry.register((Item)ModItems.ETERNAL_CAULDRON.get(), () -> new WearRenderer(render("eternal_cauldron.png"), new EternalCauldronModel(bakeLayer(ModModelLayer.ETERNAL_CAULDRON))));
        ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach((item) -> {
            if (item instanceof BlockItem blockItem) {
                if (blockItem.m_40614_() instanceof PlushieBlock) {
                    CuriosRendererRegistry.register(item, PlushieCurioRenderer::new);
                }
            }

        });
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.m_91087_().m_167973_().m_171103_(layerLocation);
    }
}
