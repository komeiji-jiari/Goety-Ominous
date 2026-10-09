package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasLookup;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 别名的应用点：注入 Placer 里唯一的「池名 → ResourceKey」入口 readPoolName 的 RETURN，
 * 在注册表解析之前换键（别名本来就是注册表里不存在的名字）。
 */
@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public abstract class JigsawPlacementPlacerMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("goetyominous/pool_alias");
    private static final Set<ResourceLocation> REPORTED = ConcurrentHashMap.newKeySet();
    private static volatile boolean reportedEmptyContext;

    @Inject(method = "readPoolName", at = @At("RETURN"), cancellable = true, require = 1)
    private static void goetyominous$aliasPoolName(StructureTemplate.StructureBlockInfo info,
                                                   CallbackInfoReturnable<ResourceKey<StructureTemplatePool>> cir) {
        ResourceKey<StructureTemplatePool> original = cir.getReturnValue();
        PoolAliasLookup lookup = PoolAliasContext.current();
        ResourceKey<StructureTemplatePool> resolved = lookup.lookup(original);
        if (original != null && original.location().getPath().contains("contents/")
                && REPORTED.add(original.location())) {
            LOGGER.info("goetyominous: readPoolName 收到 {}（ThreadLocal {}），翻译后 {}",
                    original.location(), lookup == PoolAliasLookup.EMPTY ? "空" : "有表", resolved.location());
        }
        cir.setReturnValue(resolved);
    }
}
