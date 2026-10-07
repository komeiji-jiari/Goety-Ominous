package com.qiuyue.goetyominous.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.qiuyue.goetyominous.common.init.ModLootFunctions;
import com.qiuyue.goetyominous.common.items.OminousBottleItem;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetOminousBottleAmplifierFunction extends LootItemConditionalFunction {
    private final NumberProvider amplifier;

    private SetOminousBottleAmplifierFunction(LootItemCondition[] conditions, NumberProvider amplifier) {
        super(conditions);
        this.amplifier = amplifier;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootFunctions.SET_OMINOUS_BOTTLE_AMPLIFIER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.amplifier.getReferencedContextParams();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        int amplifier = Mth.clamp(this.amplifier.getInt(context), 0, 4);
        OminousBottleItem.setAmplifier(stack, amplifier);
        return stack;
    }

    public NumberProvider amplifier() {
        return this.amplifier;
    }

    public static LootItemConditionalFunction.Builder<?> setAmplifier(NumberProvider amplifier) {
        return simpleBuilder(conditions -> new SetOminousBottleAmplifierFunction(conditions, amplifier));
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetOminousBottleAmplifierFunction> {
        @Override
        public void serialize(JsonObject json, SetOminousBottleAmplifierFunction function, JsonSerializationContext ctx) {
            super.serialize(json, function, ctx);
            json.add("amplifier", ctx.serialize(function.amplifier));
        }

        @Override
        public SetOminousBottleAmplifierFunction deserialize(JsonObject json, JsonDeserializationContext ctx,
                                                             LootItemCondition[] conditions) {
            NumberProvider amplifier = GsonHelper.getAsObject(json, "amplifier", ctx, NumberProvider.class);
            return new SetOminousBottleAmplifierFunction(conditions, amplifier);
        }
    }
}
