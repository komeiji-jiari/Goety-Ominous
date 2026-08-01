package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.client.render.model.BlackWolfModel;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.CursedBlackWolfArmorModel;
import com.qiuyue.goetyominous.common.items.CursedMetalWolfArmorItem;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class CursedBlackWolfArmorLayer<T extends BlackWolf, M extends BlackWolfModel<T>> extends RenderLayer<T, M> {
    private final BlackWolfModel<T> armorModel;

    public CursedBlackWolfArmorLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedBlackWolfArmorModel<>(modelSet.bakeLayer(ModEntityLayers.CURSED_BLACK_WOLF_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T blackWolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = blackWolf.getItemBySlot(EquipmentSlot.CHEST);
        if (armor.is(ModItems.CURSED_METAL_WOLF_ARMOR.get())) {
            BlackWolfModel<T> parentModel = this.getParentModel();
            coloredCutoutModelCopyLayerRender(parentModel, this.armorModel, CursedMetalWolfArmorItem.BLACK_WOLF_TEXTURE,
                    poseStack, buffer, packedLight, blackWolf,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}
