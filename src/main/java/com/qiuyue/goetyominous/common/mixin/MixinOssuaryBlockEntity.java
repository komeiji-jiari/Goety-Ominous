package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.blocks.entities.OssuaryBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.TrainingBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.BlockFinder;
import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OssuaryBlockEntity.class)
public class MixinOssuaryBlockEntity {

    @Inject(method = "setVariant", at = @At("RETURN"), remap = false)
    private void goetyominous$onSetVariant(ItemStack stack, Level level, BlockPos pos, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        TrainingBlockEntity self = (TrainingBlockEntity) (Object) this;
        if (self.getTrainMob() != ModEntityType.SKELETON_SERVANT.get()) return;

        if (!BlockFinder.findVillageSize(serverLevel, pos.above(), 3)
                && !self.getBlocks(s -> s.is(Blocks.DIRT_PATH), 14)) return;

        EntityType<?> villagerType = getSkeletonVillagerType();
        if (villagerType == null) return;

        self.setEntityType(villagerType);
        self.markUpdated();
    }

    private static EntityType<?> getSkeletonVillagerType() {
        if (!SavageRavageCompat.isSavageRavageLoaded()) return null;
        try {
            Class<?> clazz = Class.forName(
                    "com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry");
            java.lang.reflect.Field field = clazz.getDeclaredField("SKELETON_VILLAGER_SERVANT");
            RegistryObject<?> reg = (RegistryObject<?>) field.get(null);
            if (reg.isPresent()) {
                return (EntityType<?>) reg.get();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
