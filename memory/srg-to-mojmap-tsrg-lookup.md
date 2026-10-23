---
name: srg-to-mojmap-tsrg-lookup
description: "反编译的 AC/Goety 源码里是 SRG 名(f_20883_/m_110470_),查 tsrg 表翻译成 mojmap，别靠猜"
metadata: 
  node_type: memory
  type: reference
  originSessionId: fe7f1c9a-71f5-49e8-9ae5-09e896579efd
  modified: 2026-10-15T17:44:16.614Z
---

`.scratch/**/_acdec`、`_goetydec` 里的反编译源码全是 SRG 名(`f_20883_`、`m_110470_`、`m_7695_`),直接照抄会写错 API。翻译表在:

`C:/Users/TangJinbo/.gradle/caches/forge_gradle/minecraft_user_repo/de/oceanlabs/mcp/mcp_config/1.20.1-20230612.114412/srg_to_official_1.20.1.tsrg`

查法(类行顶格、成员行一个 tab):

```bash
T="<上面的路径>"
grep -A 14 "^net/minecraft/world/entity/WalkAnimationState " "$T"
grep -n "entityCutoutNoCull" "$T"
```

**别用 awk 的 `/^cls /,/^[^ \t]/` 区间**——类行同时匹配起止，区间立刻结束、什么都不输出。

踩过的坑：`m_110470_` 看着像 `entityCutoutNoCull`，实际是 **`entityTranslucentCull`**（AC 的传送幻影靠它的 alpha 混合）。Axis 的 SRG：`f_252529_`=XP、`f_252436_`=YP、`f_252403_`=ZP、`f_252392_`=**YN**（AC 落点幻影用的是 YN，不是 YP）。

**同名 id 会跨类重复**（`m_6469_`=hurt 出现在几十个类里），grep 全局会看花眼。查单条时按类限定：

```bash
awk '/^[^ \t]/ {cls=$1} /^\t(m_82556_|f_232229_) / {print cls"  "$0}' "$T"
```

**列数是两套**：字段行 `\tf_xxx_ 名字`（2 列），方法行 `\tm_xxx_ 描述符 名字`（3 列）——批量打印时若写 `$1" -> "$2`，方法只会打出**描述符**、名字根本没看见，会误以为查过了。要么 `print $0` 要么 `$3`。

**同名 id 跨类重复的反面**：`m_6883_` 在 `Item`/`MapItem`/`CompassItem` 都是 `(ItemStack,Level,Entity,IZ)V`，看着像 `releaseUsing`，实际是 **`inventoryTick`**（`releaseUsing` 是 `m_6881_`）。1.20.1 的 `releaseUsing` 第三参是 **`LivingEntity`**、`inventoryTick` 才是 `Entity`。判据：描述符里带 `Entity` 且尾部有 `Z` → `inventoryTick`；`isOverriding` 编译报错时先回表核对名字，别顺手改签名硬凑。

几个反直觉的（已实测，别按字面猜）：`m_82556_` = **`Vec3.lengthSqr()` 不是 `length()`**（MM 的 `getDeltaMovement().m_82556_() * 50f` 就是把平方当速度用）；`f_232229_` = **`Interpolations.LINEAR`**、`f_232230_` = **`CATMULLROM`**（和"230 在前"的直觉相反，逐字抄 400+ 处插值前务必先验）；`f_19596_` = **`MOVEMENT_SPEED`**、`f_19597_` = `MOVEMENT_SLOWDOWN`（MM 的手臂道具那边写的是 `f_19596_`，见 [[mm-mutant-jungle-zombie-arm-port]]）。

相关：[[ac-compat-porting-notes]]、[[lm-mechanic-presence-version-check]]、[[mm-mutant-jungle-zombie-arm-port]]
