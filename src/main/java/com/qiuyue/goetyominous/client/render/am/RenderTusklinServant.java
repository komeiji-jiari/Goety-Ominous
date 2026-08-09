package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelTusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 猪灵战獠仆从（獠牙兽仆从）渲染器。
 *
 * 注意：不能像其他复用方案那样继承 AlexMobs 原版 RenderTusklin——它是
 * MobRenderer{@code <EntityTusklin, ModelTusklin>}，而 TusklinServant 继承 Goety 的
 * AnimalSummon、并非 EntityTusklin。原版渲染器/模型里编译器生成的桥接方法会对传入实体
 * 无条件 checkcast EntityTusklin，一旦注册给 TusklinServant，每次渲染都会抛 ClassCastException。
 *
 * 这里改为标准的 MobRenderer{@code <TusklinServant, ModelTusklinServant>}，模型与装备层分别
 * 复用 ModelTusklin / LayerTusklinGear 移植后的版本，纹理仍用 AlexMobs 原版 tusklin.png。
 */
@OnlyIn(Dist.CLIENT)
public class RenderTusklinServant extends MobRenderer<TusklinServant, ModelTusklinServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tusklin.png");

    public RenderTusklinServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTusklinServant(), 1.0F);
        this.addLayer(new LayerTusklinServantGear(this));
    }

    @Override
    protected boolean isShaking(TusklinServant entity) {
        return entity.isInNether();
    }

    @Override
    protected void scale(TusklinServant entity, PoseStack matrixStack, float partialTicks) {
        if (entity.isBaby()) {
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TusklinServant entity) {
        return TEXTURE;
    }
}
