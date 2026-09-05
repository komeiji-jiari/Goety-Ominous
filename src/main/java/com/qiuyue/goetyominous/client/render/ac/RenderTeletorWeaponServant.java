package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.ac.TeletorWeaponServantEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 传送使徒持有的武器实体渲染:逐字移植 AC MagneticWeaponRenderer 的“悬浮武器”分支。
 * 与原版仅保留:渲染目标 ItemStack(物品/方块图标,取自 TextureAtlas.LOCATION_BLOCKS)、
 * 悬浮时的上下 bob、按 strikeProgress 倾斜前刺的出手姿态。
 * 已删除原版 Player 第一人称掌心灵能闪电逻辑(该闪电改由 RenderTeletorServant 的头部牵引光弧负责)。
 */
@OnlyIn(Dist.CLIENT)
public class RenderTeletorWeaponServant extends EntityRenderer<TeletorWeaponServantEntity> {

    public RenderTeletorWeaponServant(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(TeletorWeaponServantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        ItemStack itemStack = entity.getItemStack();
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = renderer.getModel(itemStack, entity.level(), null, 0);
        float ageInTicks = entity.tickCount + partialTicks;
        float strikeProgress = entity.getStrikeProgress(partialTicks);
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.1F + Math.sin(ageInTicks * 0.1F) * 0.1F, 0.0D);
        poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.translate(0.0F, strikeProgress * 0.1F, strikeProgress * 0.2F);
        poseStack.mulPose(Axis.XN.rotationDegrees(strikeProgress * 90.0F));
        renderer.render(itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, source, packedLight, OverlayTexture.NO_OVERLAY, bakedModel);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
    }

    @Override
    public boolean shouldRender(TeletorWeaponServantEntity entity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, camera, camX, camY, camZ)) {
            return true;
        }
        Entity controller = entity.getController();
        if (controller != null) {
            Vec3 vec3 = entity.position();
            Vec3 vec31 = controller.position();
            return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
        }
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(TeletorWeaponServantEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
