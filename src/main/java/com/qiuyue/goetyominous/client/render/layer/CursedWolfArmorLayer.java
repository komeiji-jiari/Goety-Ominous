package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.CursedWolfArmorModel;
import com.qiuyue.goetyominous.common.items.CursedMetalWolfArmorItem;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.utils.WolfArmorCrackiness;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;

public class CursedWolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private final WolfModel<Wolf> armorModel;

    public CursedWolfArmorLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new CursedWolfArmorModel<>(modelSet.bakeLayer(ModEntityLayers.CURSED_WOLF_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Wolf wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.CHEST);
        if (armor.is(ModItems.CURSED_METAL_WOLF_ARMOR.get())) {
            WolfModel<Wolf> parentModel = this.getParentModel();

            coloredCutoutModelCopyLayerRender(parentModel, this.armorModel, CursedMetalWolfArmorItem.VANILLA_WOLF_TEXTURE,
                    poseStack, buffer, packedLight, wolf,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);

            WolfArmorCrackiness crack = WolfArmorCrackiness.byDamage(armor);

            if (crack != WolfArmorCrackiness.NONE) {
                ResourceLocation crackTexture = CursedMetalWolfArmorItem.getCrackTexture(crack);
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(crackTexture));
                this.armorModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
