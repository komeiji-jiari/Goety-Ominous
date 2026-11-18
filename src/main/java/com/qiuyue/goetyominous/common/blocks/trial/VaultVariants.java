package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class VaultVariants {

    public static final String FORGE_DATA = "ForgeData";
    public static final String NEO_FORGE_DATA = "NeoForgeData";
    public static final String VARIANT = "goety:vault_variant";
    public static final String CRYPT = "crypt";

    @Nullable
    public static String variantOf(BlockEntity blockEntity) {
        if (blockEntity instanceof VaultBlockEntity vault) {
            return vault.getVariant();
        }
        if (blockEntity instanceof TrialSpawnerBlockEntity spawner) {
            return spawner.getVariant();
        }
        return null;
    }

    public static String readVariant(CompoundTag tag) {
        String variant = tag.getCompound(NEO_FORGE_DATA).getString(VARIANT);
        if (variant.isEmpty()) {
            variant = tag.getCompound(FORGE_DATA).getString(VARIANT);
        }
        return variant;
    }

    public static void writeVariant(CompoundTag tag, String variant) {
        if (variant == null || variant.isEmpty()) {
            return;
        }
        CompoundTag neo = tag.getCompound(NEO_FORGE_DATA);
        neo.putString(VARIANT, variant);
        tag.put(NEO_FORGE_DATA, neo);
        CompoundTag forge = tag.getCompound(FORGE_DATA);
        forge.putString(VARIANT, variant);
        tag.put(FORGE_DATA, forge);
    }

    @Nullable
    public static ItemStack keyFor(String variant, BlockState state) {
        if (!CRYPT.equals(variant)) {
            return null;
        }
        boolean ominous = state.hasProperty(VaultBlock.OMINOUS) && state.getValue(VaultBlock.OMINOUS);
        return new ItemStack(ominous ? ModItems.OMINOUS_CRYPT_KEY.get() : ModItems.CRYPT_KEY.get());
    }

    @Nullable
    public static String vaultTextures(String variant) {
        return CRYPT.equals(variant) ? "block/crypt_vault/" : null;
    }

    @Nullable
    public static String spawnerTextures(String variant) {
        return CRYPT.equals(variant) ? "block/crypt_spawner/" : null;
    }

    private VaultVariants() {
    }
}
