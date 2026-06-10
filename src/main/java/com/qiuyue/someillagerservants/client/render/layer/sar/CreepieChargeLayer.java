package com.qiuyue.someillagerservants.client.render.layer.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.sar.CreepieServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.sar.CreepieServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreepieChargeLayer extends EnergySwirlLayer<CreepieServant, CreepieServantModel> {
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final CreepieServantModel creepieModel;

    public CreepieChargeLayer(RenderLayerParent<CreepieServant, CreepieServantModel> entityRenderer, EntityModelSet modelSet) {
        super(entityRenderer);
        this.creepieModel = new CreepieServantModel(modelSet.bakeLayer(ModEntityLayers.CREEPIE_SERVANT_LAYER));
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, CreepieServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isPowered()) {
            super.render(stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    protected float xOffset(float p_225634_1_) {
        return p_225634_1_ * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return LIGHTNING_TEXTURE;
    }

    @Override
    protected EntityModel<CreepieServant> model() {
        return this.creepieModel;
    }
}
