package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.CrimsonGuardModel;
import com.qiuyue.goetyominous.common.entities.ally.illager.CrimsonGuardServant;
import com.qiuyue.goetyominous.common.entities.ally.illager.ZCrimsonGuardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrimsonGuardRenderer<T extends Mob> extends MobRenderer<T, CrimsonGuardModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/crimson_guard.png");
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/crimson_guard_servant.png");
    private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/zombie_crimson_guard.png");

    public CrimsonGuardRenderer(EntityRendererProvider.Context context) {
        super(context, new CrimsonGuardModel<>(context.bakeLayer(ModEntityLayers.CRIMSON_GUARD_LAYER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof ZCrimsonGuardServant) {
            return ZOMBIE_TEXTURE;
        }
        return entity instanceof CrimsonGuardServant ? SERVANT_TEXTURE : TEXTURE;
    }

    @Override
    protected boolean isShaking(T entity) {
        return super.isShaking(entity)
                || entity instanceof ZCrimsonGuardServant zombie && zombie.isConverting();
    }
}
