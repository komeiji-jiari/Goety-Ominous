package com.qiuyue.goetyominus.common.items;

import com.qiuyue.goetyominus.common.init.ModTags;
import com.qiuyue.goetyominus.common.items.handler.FungusPackItemHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class FungusPackHelper {

    public static boolean hasMatchingFungus(LivingEntity entity, Item fungusType) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.is(ModTags.FUNGUS_PACKS)) {
            FungusPackItemHandler handler = FungusPackItemHandler.get(chestStack);
            ItemStack fungus = handler.getFungus();
            if (!fungus.isEmpty() && fungus.is(fungusType)) return true;
        }

        Optional<SlotResult> result = CuriosApi.getCuriosInventory(entity)
                .map(inv -> inv.findFirstCurio(s -> s.is(ModTags.FUNGUS_PACKS)))
                .orElse(Optional.empty());
        if (result.isPresent()) {
            FungusPackItemHandler handler = FungusPackItemHandler.get(result.get().stack());
            ItemStack fungus = handler.getFungus();
            return !fungus.isEmpty() && fungus.is(fungusType);
        }

        return false;
    }
}
