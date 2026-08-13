package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.client.render.model.BlackBeastModel;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.qiuyue.goetyominous.common.items.CursedBlackBeastArmorItem;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlackBeastModel.class)
public abstract class MixinBlackBeastModel {

    @Shadow(remap = false)
    @Final
    private ModelPart torso;

    @Inject(method = "setupAnim", at = @At("HEAD"), remap = false)
    private void goetyominous$hideFurWhenArmored(BlackBeast entity, float limbSwing, float limbSwingAmount,
                                                 float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack armor = entity.getItemBySlot(EquipmentSlot.CHEST);
        this.torso.getChild("fur").visible = !(armor.getItem() instanceof CursedBlackBeastArmorItem);
    }
}
