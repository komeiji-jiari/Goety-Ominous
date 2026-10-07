package com.qiuyue.goetyominous.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.qiuyue.goetyominous.GoetyOminous;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.qiuyue.goetyominous.common.init.ModLootFunctions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
    private static final TagKey<Enchantment> ON_RANDOM_LOOT =
            TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(GoetyOminous.MOD_ID, "on_random_loot"));

    private final NumberProvider levels;
    private final Optional<HolderSet<Enchantment>> options;

    private EnchantWithLevelsFunction(LootItemCondition[] conditions, NumberProvider levels,
                                      Optional<HolderSet<Enchantment>> options) {
        super(conditions);
        this.levels = levels;
        this.options = options;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootFunctions.ENCHANT_WITH_LEVELS.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.levels.getReferencedContextParams();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        RandomSource random = context.getRandom();
        HolderSet<Enchantment> pool = this.options.orElseGet(
                () -> BuiltInRegistries.ENCHANTMENT.getOrCreateTag(ON_RANDOM_LOOT));
        List<EnchantmentInstance> enchantments =
                EnchantPoolHelper.selectEnchantment(random, stack, this.levels.getInt(context), pool);
        if (stack.is(Items.BOOK)) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentInstance instance : enchantments) {
            stack.enchant(instance.enchantment, instance.level);
        }
        return stack;
    }

    public NumberProvider levels() {
        return this.levels;
    }

    public Optional<HolderSet<Enchantment>> options() {
        return this.options;
    }

    public static LootItemConditionalFunction.Builder<?> enchantWithLevels(NumberProvider levels) {
        return simpleBuilder(conditions -> new EnchantWithLevelsFunction(conditions, levels, Optional.empty()));
    }

    private static HolderSet<Enchantment> parseOptions(JsonElement element) {
        Registry<Enchantment> registry = BuiltInRegistries.ENCHANTMENT;
        if (element.isJsonArray()) {
            List<Holder<Enchantment>> holders = Lists.newArrayList();
            for (JsonElement entry : element.getAsJsonArray()) {
                registry.getHolder(ResourceKey.create(Registries.ENCHANTMENT,
                        new ResourceLocation(entry.getAsString()))).ifPresent(holders::add);
            }
            return HolderSet.direct(holders);
        }
        String value = element.getAsString();
        ResourceLocation id = new ResourceLocation(value.startsWith("#") ? value.substring(1) : value);
        return registry.getOrCreateTag(TagKey.create(Registries.ENCHANTMENT, id));
    }

    private static JsonElement serializeOptions(HolderSet<Enchantment> options) {
        if (options instanceof HolderSet.Named<Enchantment> named) {
            return new JsonPrimitive("#" + named.key().location());
        }
        JsonArray array = new JsonArray();
        for (Holder<Enchantment> holder : options) {
            holder.unwrapKey().ifPresent(key -> array.add(key.location().toString()));
        }
        return array;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
        @Override
        public void serialize(JsonObject json, EnchantWithLevelsFunction function, JsonSerializationContext ctx) {
            super.serialize(json, function, ctx);
            json.add("levels", ctx.serialize(function.levels));
            function.options.ifPresent(options -> json.add("options", serializeOptions(options)));
        }

        @Override
        public EnchantWithLevelsFunction deserialize(JsonObject json, JsonDeserializationContext ctx,
                                                     LootItemCondition[] conditions) {
            NumberProvider levels = GsonHelper.getAsObject(json, "levels", ctx, NumberProvider.class);
            Optional<HolderSet<Enchantment>> options = json.has("options")
                    ? Optional.of(parseOptions(json.get("options")))
                    : Optional.empty();
            return new EnchantWithLevelsFunction(conditions, levels, options);
        }
    }
}
