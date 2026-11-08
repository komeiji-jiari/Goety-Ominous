package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.BastionGuardModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZBastionGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BastionGuardRenderer<T extends Mob> extends MobRenderer<T, BastionGuardModel<T>> {
    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/bastion_guard.png");
    private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zbastion_guard.png");

    public BastionGuardRenderer(EntityRendererProvider.Context context) {
        super(context, new BastionGuardModel<>(context.bakeLayer(BastionGuardModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    protected boolean isShaking(T entity) {
        return super.isShaking(entity)
                || entity instanceof AbstractPiglinServant piglin && piglin.isConverting();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof ZBastionGuardServant ? ZOMBIE_TEXTURE : NORMAL_TEXTURE;
    }
}
