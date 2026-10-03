package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderNuclearGuardianServant extends RenderMineGuardianServant {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/ac/elder_mine_guardian.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/ac/elder_mine_guardian_sleeping.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/ac/elder_mine_guardian_glow.png");

    public RenderNuclearGuardianServant(EntityRendererProvider.Context context) {
        super(context, TEXTURE, TEXTURE_SLEEPING, TEXTURE_GLOW);
        this.shadowRadius = 1.2F;
    }

    @Override
    protected float getModelScale() {
        return DEFAULT_MODEL_SCALE * 1.5F;
    }
}
