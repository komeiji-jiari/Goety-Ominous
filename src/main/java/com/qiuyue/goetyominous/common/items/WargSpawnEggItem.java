package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WargSpawnEggItem extends ServantSpawnEggItem {

    private final Warg.Variant variant;
    private final boolean hostile;

    public WargSpawnEggItem(RegistryObject<? extends EntityType<? extends Mob>> entityTypeSupplier,
                            int backgroundColor, int highlightColor, Item.Properties properties,
                            Warg.Variant variant, boolean hostile) {
        super(entityTypeSupplier, backgroundColor, highlightColor, properties);
        this.variant = variant;
        this.hostile = hostile;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        return this.withEntityTag(context.getItemInHand(), () -> super.useOn(context));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return this.withEntityTag(player.getItemInHand(hand), () -> super.use(level, player, hand));
    }

    private <R> R withEntityTag(ItemStack stack, Supplier<R> action) {
        CompoundTag previous = stack.getTag();
        CompoundTag tag = previous == null ? new CompoundTag() : previous.copy();
        CompoundTag entityTag = tag.contains("EntityTag", Tag.TAG_COMPOUND)
                ? tag.getCompound("EntityTag").copy()
                : new CompoundTag();
        entityTag.putInt("WargVariant", this.variant.ordinal());
        entityTag.putBoolean("isHostile", this.hostile);
        tag.put("EntityTag", entityTag);
        stack.setTag(tag);
        try {
            return action.get();
        } finally {
            stack.setTag(previous);
        }
    }
}
