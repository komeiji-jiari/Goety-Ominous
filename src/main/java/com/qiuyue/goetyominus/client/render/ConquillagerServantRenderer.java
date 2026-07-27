package com.qiuyue.goetyominus.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.model.ConquillagerServantModel;
import com.qiuyue.goetyominus.common.entities.ally.illager.ConquillagerServant;
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
 * 征服者仆从渲染器
 */
@OnlyIn(Dist.CLIENT)
public class ConquillagerServantRenderer<T extends ConquillagerServant>
        extends MobRenderer<T, ConquillagerServantModel<T>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/conquillager.png");// 纹理路径，Goety.MOD_ID是从Goety主类中引用了MOD_ID，也可以改成goety（直接定义）而不引用

    public ConquillagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ConquillagerServantModel<>(renderManagerIn.bakeLayer(ModModelLayer.CONQUILLAGER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
        this.addLayer(
                new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(this,
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
                renderManagerIn.getModelManager()));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
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
        return TEXTURE;// 与巡查官不同，这里在渲染器中定义了纹理资源
    }
}
