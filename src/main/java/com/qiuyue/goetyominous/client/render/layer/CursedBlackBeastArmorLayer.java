package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.CursedBlackBeastArmorModel;
import com.qiuyue.goetyominous.common.items.CursedBlackBeastArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class CursedBlackBeastArmorLayer<T extends BlackBeast, M extends BlackBeastModel<T>> extends RenderLayer<T, M> {
    private final BlackBeastModel<T> armorModel;

    public CursedBlackBeastArmorLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedBlackBeastArmorModel<>(modelSet.bakeLayer(ModEntityLayers.CURSED_BLACK_BEAST_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T blackBeast, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = blackBeast.getItemBySlot(EquipmentSlot.CHEST);
        if (armor.getItem() instanceof CursedBlackBeastArmorItem armorItem) {
            BlackBeastModel<T> parentModel = this.getParentModel();
            coloredCutoutModelCopyLayerRender(parentModel, this.armorModel, armorItem.getTexture(),
                    poseStack, buffer, packedLight, blackBeast,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}