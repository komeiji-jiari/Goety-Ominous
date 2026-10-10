package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.items.ModItems;
import java.util.Optional;

import com.qiuyue.goetyominous.config.PlayerDetector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange,
                          ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay,
                          PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {

    static final String TAG_NAME = "config";
    public static final ResourceLocation DEFAULT_LOOT =
            new ResourceLocation("goetyominous", "chests/trial_chambers/reward");
    public static final ResourceLocation DEFAULT_OMINOUS_LOOT =
            new ResourceLocation("goetyominous", "chests/trial_chambers/reward_ominous");

    private static final Codec<ItemStack> KEY_ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));

    public static final Codec<VaultConfig> CODEC = RecordCodecBuilder.<VaultConfig>create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("loot_table", DEFAULT_LOOT).forGetter(VaultConfig::lootTable),
            Codec.DOUBLE.optionalFieldOf("activation_range", 4.0D).forGetter(VaultConfig::activationRange),
            Codec.DOUBLE.optionalFieldOf("deactivation_range", 4.5D).forGetter(VaultConfig::deactivationRange),
            KEY_ITEM_CODEC.optionalFieldOf("key_item").xmap(
                    opt -> opt.orElse(ItemStack.EMPTY),
                    stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)).forGetter(VaultConfig::keyItem),
            ResourceLocation.CODEC.optionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::overrideLootTableToDisplay)
    ).apply(instance, VaultConfig::new)).flatXmap(config -> config.validate(), config -> config.validate());

    public static VaultConfig defaultFor(boolean ominous) {
        return new VaultConfig(
                ominous ? DEFAULT_OMINOUS_LOOT : DEFAULT_LOOT,
                4.0D, 4.5D,
                new ItemStack(ominous ? ModItems.OMINOUS_TRIAL_KEY.get() : ModItems.TRIAL_KEY.get()),
                Optional.empty(),
                PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    public VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange,
                       ItemStack keyItem, Optional<ResourceLocation> overrideLootTableToDisplay) {
        this(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay,
                PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    private DataResult<VaultConfig> validate() {
        return this.activationRange > this.deactivationRange
                ? DataResult.error(() -> "Activation range must (" + this.activationRange
                + ") be less or equal to deactivation range (" + this.deactivationRange + ")")
                : DataResult.success(this);
    }
}
