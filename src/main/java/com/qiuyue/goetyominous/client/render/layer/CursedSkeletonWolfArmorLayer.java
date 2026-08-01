package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.client.render.model.SkeletonWolfModel;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.items.CursedMetalWolfArmorItem;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class CursedSkeletonWolfArmorLayer extends RenderLayer<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> {
    private final SkeletonWolfModel<SkeletonWolf> armorModel;

    public CursedSkeletonWolfArmorLayer(RenderLayerParent<SkeletonWolf, SkeletonWolfModel<SkeletonWolf>> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new SkeletonWolfModel<>(modelSet.bakeLayer(ModEntityLayers.CURSED_WOLF_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SkeletonWolf skeletonWolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = skeletonWolf.getItemBySlot(EquipmentSlot.CHEST);
        if (armor.is(ModItems.CURSED_METAL_WOLF_ARMOR.get())) {
            SkeletonWolfModel<SkeletonWolf> parentModel = this.getParentModel();
            coloredCutoutModelCopyLayerRender(parentModel, this.armorModel, CursedMetalWolfArmorItem.VANILLA_WOLF_TEXTURE,
                    poseStack, buffer, packedLight, skeletonWolf,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}
