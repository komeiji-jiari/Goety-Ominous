package com.qiuyue.goetyominous.client.render.layer;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.CerberusArmorModel;
import com.qiuyue.goetyominous.client.render.model.CerberusModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Cerberus;
import com.qiuyue.goetyominous.common.items.CursedWargArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class CerberusArmorLayer extends RenderLayer<Cerberus, CerberusModel> {
    private final CerberusArmorModel armorModel;

    public CerberusArmorLayer(RenderLayerParent<Cerberus, CerberusModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CerberusArmorModel(modelSet.bakeLayer(ModEntityLayers.CERBERUS_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Cerberus cerberus, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = cerberus.getItemBySlot(EquipmentSlot.CHEST);
        if (armor.getItem() instanceof CursedWargArmorItem armorItem) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.armorModel, armorItem.getTexture(), poseStack,
                    buffer, packedLight, cerberus, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}
