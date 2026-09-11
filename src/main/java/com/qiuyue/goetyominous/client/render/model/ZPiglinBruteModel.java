package com.qiuyue.goetyominous.client.render.model;

import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinBruteModel extends ZPiglinModel<Mob> {

    public ZPiglinBruteModel(ModelPart root) {
        super(root);
    }
}
