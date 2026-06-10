package com.qiuyue.someillagerservants.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.someillagerservants.client.render.model.InquillagerServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.illager.InquillagerServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 巡查官仆从渲染器
 * 负责在客户端渲染巡查官仆从实体
 * 复制自北极冰的巡查官渲染器
 * 继承自 MobRenderer，使用 InquillagerServantModel 进行渲染
 */
@OnlyIn(Dist.CLIENT)
public class InquillagerServantRenderer<T extends InquillagerServant>
        extends MobRenderer<T, InquillagerServantModel<T>> {

    public InquillagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new InquillagerServantModel<>(renderManagerIn.bakeLayer(ModModelLayer.INQUILLAGER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {// 手持物品渲染层
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                    T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                    float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn.isAggressive()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount,
                            partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
        this.addLayer(
                new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));// 头部装饰渲染层，用于旗帜渲染
        this.addLayer(new HumanoidArmorLayer<>(this,
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
                renderManagerIn.getModelManager()));// 护甲渲染层
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);// 实体模型缩放
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw,
            float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }

    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getResourceLocation();// 获取美术资源，这里在实体类中定义了。一般会在渲染器类中定义纹理。
    }
}
