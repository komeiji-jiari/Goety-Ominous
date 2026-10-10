package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
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

    @Inject(method = "readPoolName", at = @At("RETURN"), cancellable = true, require = 1)
    private static void goetyominous$aliasPoolName(StructureTemplate.StructureBlockInfo info,
                                                   CallbackInfoReturnable<ResourceKey<StructureTemplatePool>> cir) {
        cir.setReturnValue(PoolAliasContext.current().lookup(cir.getReturnValue()));
    }
}
