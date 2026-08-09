package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelTusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import net.minecraft.client.renderer.MultiBufferSource;
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
        // 触发模型里已移植的 young 分支：幼崽显示 AM 式大头/厚肢/隐藏獠牙外观，
        // 尺寸由模型 young 分支自带的 0.45 缩放控制。
        // 注意不能在这里再叠加 0.5 缩放，否则会与模型内 0.45 相乘缩到 0.225（太小，
        // 且与幼崽碰撞箱 0.5 倍严重不匹配）。
        this.model.young = entity.isBaby();
    }

    @Override
    public void render(TusklinServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        // 阴影半径随碰撞箱一起收小：原 1.0 对 1.3 宽的新碰撞箱过大，0.65≈箱宽一半
        this.shadowRadius = entity.isBaby() ? 0.29F : 0.65F;
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TusklinServant entity) {
        return TEXTURE;
    }
}
