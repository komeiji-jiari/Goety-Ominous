package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class HelmetlessArmorLayer<T extends LivingEntity, M extends EntityModel<T> & HierarchicalArmor>
        extends HierarchicalArmorLayer<T, M> {

    public HelmetlessArmorLayer(RenderLayerParent<T, M> renderer, EntityRendererProvider.Context context) {
        super(renderer, context);
    }

    @Override
    protected void setModelSlotVisible(HumanoidModel model, EquipmentSlot slot) {
        super.setModelSlotVisible(model, slot);
        if (slot == EquipmentSlot.HEAD) {
            model.head.visible = false;
            model.hat.visible = false;
        }
    }
}
