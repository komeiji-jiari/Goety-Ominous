/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.client.model.MasterStaffModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MasterStaffItemRenderer
extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];
    private static final ResourceLocation[] COLOR_MASKS = new ResourceLocation[8];
    private MasterStaffModel model;

    public MasterStaffItemRenderer() {
        super(Minecraft.m_91087_().m_167982_(), Minecraft.m_91087_().m_167973_());
    }

    public void m_108829_(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.model == null) {
            this.model = new MasterStaffModel(Minecraft.m_91087_().m_167973_().m_171103_(MasterStaffModel.LAYER_LOCATION));
        }
        poseStack.m_85836_();
        this.applyTransform(context, poseStack);
        float animationTime = Minecraft.m_91087_().f_91073_ == null ? 0.0f : (float)Minecraft.m_91087_().f_91073_.m_46467_() + Minecraft.m_91087_().m_91296_();
        this.model.animate(animationTime);
        int skin = MasterStaffHelper.getSkin(stack);
        ResourceLocation texture = TEXTURES[skin];
        RenderType renderType = RenderType.m_110458_((ResourceLocation)texture);
        VertexConsumer consumer = ItemRenderer.m_115222_((MultiBufferSource)bufferSource, (RenderType)renderType, (boolean)false, (boolean)stack.m_41790_());
        this.model.renderBase(poseStack, consumer, packedLight, OverlayTexture.f_118083_);
        int color = MasterStaffItemRenderer.getWandColor(MasterStaffHelper.getSelectedWand(stack));
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        VertexConsumer maskConsumer = bufferSource.m_6299_(RenderType.m_234338_((ResourceLocation)COLOR_MASKS[skin]));
        this.model.renderAccents(poseStack, maskConsumer, 0xF000F0, OverlayTexture.f_118083_, red, green, blue);
        poseStack.m_85849_();
    }

    private void applyTransform(ItemDisplayContext context, PoseStack poseStack) {
        poseStack.m_85837_(0.5, 0.5, 0.5);
        float scale = switch (context) {
            case ItemDisplayContext.GUI -> {
                poseStack.m_85837_(-0.04, 0.105, 0.0);
                poseStack.m_252781_(Axis.f_252436_.m_252977_(-45.0f));
                poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0f));
                yield 0.58f;
            }
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND -> {
                poseStack.m_85837_(0.0, 0.0, 0.02);
                yield 0.88f;
            }
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND -> {
                poseStack.m_85837_(0.0, 0.0, 0.02);
                yield 0.88f;
            }
            case ItemDisplayContext.THIRD_PERSON_LEFT_HAND -> {
                poseStack.m_85837_(0.0, 0.25, 0.04);
                yield 0.88f;
            }
            case ItemDisplayContext.THIRD_PERSON_RIGHT_HAND -> {
                poseStack.m_85837_(0.0, 0.25, 0.04);
                yield 0.88f;
            }
            case ItemDisplayContext.GROUND -> {
                poseStack.m_85837_(0.0, 0.08, 0.0);
                yield 0.42f;
            }
            case ItemDisplayContext.FIXED -> {
                poseStack.m_252781_(Axis.f_252436_.m_252977_(-180.0f));
                poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0f));
                yield 0.62f;
            }
            default -> 0.52f;
        };
        poseStack.m_85841_(-scale, -scale, scale);
    }

    private static int getWandColor(ItemStack selectedWand) {
        Item item = selectedWand.m_41720_();
        if (!(item instanceof IWand)) {
            return 0xFFFFFF;
        }
        IWand wand = (IWand)item;
        ResourceLocation wandId = BuiltInRegistries.f_257033_.m_7981_((Object)selectedWand.m_41720_());
        switch (wandId.toString()) {
            case "goety:nameless_staff": {
                return 3797082;
            }
            case "goety:nether_staff": {
                return 7471129;
            }
            case "goety:wild_staff": {
                return 2382389;
            }
            case "goety:wind_staff": {
                return 7898507;
            }
            case "goety:geo_staff": {
                return 7419816;
            }
            case "goety:necro_staff": {
                return 1319217;
            }
            case "goety:abyss_staff": {
                return 738232;
            }
            case "goety:dark_wand": {
                return 5574784;
            }
            case "goety:ominous_staff": {
                return 36693;
            }
            case "goety:storm_staff": {
                return 14719488;
            }
        }
        SpellType type = wand.getSpellType();
        if (type == SpellType.FROST) {
            return 39876;
        }
        if (type == SpellType.NETHER) {
            return 7471129;
        }
        if (type == SpellType.WILD) {
            return 2382389;
        }
        if (type == SpellType.STORM) {
            return 14719488;
        }
        if (type == SpellType.WIND) {
            return 7898507;
        }
        if (type == SpellType.GEOMANCY) {
            return 7419816;
        }
        if (type == SpellType.VOID) {
            return 5970589;
        }
        if (type == SpellType.ABYSS) {
            return 738232;
        }
        if (type == SpellType.NECROMANCY) {
            return 1319217;
        }
        if (type == SpellType.ILL) {
            return 36693;
        }
        return 5574784;
    }

    static {
        for (int skin = 0; skin < TEXTURES.length; ++skin) {
            MasterStaffItemRenderer.TEXTURES[skin] = new ResourceLocation("goety_mastery_of_magic", "textures/item/master_staff/master_staff_" + (skin + 1) + ".png");
            MasterStaffItemRenderer.COLOR_MASKS[skin] = new ResourceLocation("goety_mastery_of_magic", "textures/item/master_staff/master_staff_mask_" + (skin + 1) + ".png");
        }
    }
}

