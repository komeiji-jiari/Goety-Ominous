---
name: goety-ritual-percast-state-stale
description: "IRitualType 无 start/interrupt 钩子;静态 map 按 BlockPos 只在 onFinishRitual 清条目 → 中断或同 JVM 世界重载后同 altar 再施法\"每帧触发但一次性视觉永不再放\""
metadata: 
  node_type: memory
  type: project
  originSessionId: b3cd7a2a-17a1-4341-89b3-be28ff5b68e0
  modified: 2026-09-08T13:31:21.748Z
---

Goety 的 `IRitualType.onPerformRitual` **每个 tick 都调用**(Goety `Ritual.update`→onPerformRitual),但接口**没有 start/interrupt 钩子**;`onFinishRitual` 只在仪式**成功完成**时调用(`Ritual.finish`),中断走 `Ritual.interrupt()` 不会回调到类型。

因此别用「静态 map + 按 altar 的 `BlockPos` 键 + 只在 finish 清条目」来标记"本次仪式是否已触发一次性视觉(如 PureDark 的 void cloud)":
- 某次仪式**被中断/半途离开** → 条目残留 → 之后同 JVM 里**同一法阵**再施法,首帧判定"非首次" → 一次性视觉永不重放,而每 tick 的周期性诵经/烟雾/wind 照常 → 表现为"仪式在跑、特效(云)没了",直到整游戏重启。
- **同 JVM 返回标题再进同一世界**也中招:static map 跨世界重载存活,而 `world.getGameTime()` 归零。

可靠做法(PureDarkRitualType 现用):
- 状态键用 **`DarkAltarBlockEntity` 实例**(identity)+ `WeakHashMap`:任何 chunk/世界重载都产生新实例 → 自动当作新仪式;不持强引用防泄漏。
- 再叠加**静默间隔检测**:相邻 `onPerformRitual` 调用相隔 >`RITUAL_RESYNC_GAP`(40 tick≈2s)即视为重新施法,重置 start 时间。

相关:[[goety-command-mode-conflict]];粒子本体 DynamicTexture 寿命末坑见 [[forge-particledynamictexture-lifetime-remove]]。
