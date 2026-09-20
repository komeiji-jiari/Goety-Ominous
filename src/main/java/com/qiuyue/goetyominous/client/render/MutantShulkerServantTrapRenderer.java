package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantTrapModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantShulkerServantTrapRenderer extends MobRenderer<MutantShulkerServantTrap, MutantShulkerServantTrapModel<MutantShulkerServantTrap>> {
    public MutantShulkerServantTrapRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantShulkerServantTrapModel<>(context.bakeLayer(ModEntityLayers.MUTANT_SHULKER_SERVANT_TRAP_LAYER)), 0.75F);
    }

    protected float getFlipDegrees(MutantShulkerServantTrap pLivingEntity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantShulkerServantTrap entity) {
        return entity.getColor() == null
                ? new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_shulker_trap_servant.png")
                : new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_shulker_trap_servant_" + entity.getColor().getName() + ".png");
    }
}
