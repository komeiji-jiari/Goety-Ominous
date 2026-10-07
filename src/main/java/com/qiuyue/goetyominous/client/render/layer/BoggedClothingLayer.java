package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.entities.util.BoggedLike;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.function.Function;

public class BoggedClothingLayer<T extends Mob & RangedAttackMob & BoggedLike, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final SkeletonModel<T> layerModel;
    private final Function<T, ResourceLocation> texture;

    public BoggedClothingLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet,
                               Function<T, ResourceLocation> texture) {
        super(renderer);
        this.layerModel = new SkeletonModel<>(modelSet.bakeLayer(ModEntityLayers.BOGGED_OUTER_LAYER));
        this.texture = texture;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, this.texture.apply(entity),
                poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                partialTicks, 1.0F, 1.0F, 1.0F);
    }
}
