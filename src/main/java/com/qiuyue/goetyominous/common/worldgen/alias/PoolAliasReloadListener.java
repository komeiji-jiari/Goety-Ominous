package com.qiuyue.goetyominous.common.worldgen.alias;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.qiuyue.goetyominous.GoetyOminous;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PoolAliasReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("goetyominous/pool_alias");
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "worldgen/structure";
    private static final Map<ResourceLocation, List<PoolAliasBinding>> BY_START_POOL = new ConcurrentHashMap<>();

    public PoolAliasReloadListener() {
        super(GSON, DIRECTORY);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new PoolAliasReloadListener());
    }

    public static List<PoolAliasBinding> forStartPool(ResourceLocation startPool) {
        if (startPool == null) {
            return List.of();
        }
        return BY_START_POOL.getOrDefault(startPool, List.of());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        BY_START_POOL.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
            JsonElement root = entry.getValue();
            if (!root.isJsonObject()) {
                continue;
            }
            JsonObject object = root.getAsJsonObject();
            JsonElement aliases = object.get("pool_aliases");
            JsonElement startPool = object.get("start_pool");
            if (aliases == null || startPool == null || !startPool.isJsonPrimitive()) {
                continue;
            }
            String startPoolName = startPool.getAsString();
            if (startPoolName.startsWith("#")) {
                LOGGER.warn("goetyominous: {} 的 start_pool 是标签（{}），暂不支持按标签索引别名，已跳过",
                        entry.getKey(), startPoolName);
                continue;
            }
            ResourceLocation startPoolId = ResourceLocation.tryParse(startPoolName);
            List<PoolAliasBinding> bindings = PoolAliasBindings.bindings().listOf()
                    .parse(JsonOps.INSTANCE, aliases)
                    .result()
                    .orElse(null);
            if (startPoolId == null || bindings == null || bindings.isEmpty()) {
                LOGGER.warn("goetyominous: {} 的 pool_aliases 解析失败或为空，已跳过（别名对这些池不生效）",
                        entry.getKey());
                continue;
            }
            BY_START_POOL.put(startPoolId, bindings);
        }
    }
}
