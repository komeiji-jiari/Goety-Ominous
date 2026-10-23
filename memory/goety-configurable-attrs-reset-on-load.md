---
name: goety-configurable-attrs-reset-on-load
description: "Goety 的 setConfigurableAttributes() 每次 NBT 加载都会按 config 重置属性基础值——任何\"召唤时快照\"的属性偏移必须存 NBT 并在该方法里读回，否则重载即失效"
metadata: 
  node_type: memory
  type: project
  originSessionId: fe08c6f8-3da9-48f5-8514-b3e24973e0d6
  modified: 2026-10-09T11:13:31.042Z
---

`Owned.setConfigurableAttributes()`（`goety_owned/.../Owned.java:129`，空实现）在仓库里被各仆从覆写成"按 `AttributesConfig` 重设 Health/Damage/FollowRange 的基础值"。它的**唯一调用点是 `Owned.readAdditionalSaveData` 的末尾**（`Owned.java:247`，在 `readOwnedData` 之后）。

**两个后果：**

1. **它不在"新召唤"路径上**。新建实体时属性来自 `setCustomAttributes()`（EntityType 注册用的静态 builder，值 = config）。所以想给"刚召唤出来"的仆从加一次性属性偏移，得自己找钩子（用 `finalizeSpawn`），不能指望这个方法。
2. **它在"每次读档"路径上，且会覆盖 NBT 里存的基础值**。vanilla `LivingEntity.readAdditionalSaveData` 先恢复属性基础值，紧接着 Goety 这个方法又把它们按 config 抹回去。所以**任何召唤时算出来的属性倍率，只写在属性上是不持久的**——存档一读就回到 config 默认。

**How to apply:** 需要"召唤时快照"的属性偏移时，三步：① 在 `finalizeSpawn` 里算好倍率并存进字段；② 覆写的 `setConfigurableAttributes()` 改成 `config 值 * 该字段`；③ 该字段写进 `addAdditionalSaveData`，并在 `readAdditionalSaveData` 里**在 `super` 之前**读回来（因为 super 链上的 `Owned` 会立刻调用 `setConfigurableAttributes()`，晚读就白读）。留 `contains` 兜底默认 1.0，老存档才不会被打回。

改了 MAX_HEALTH 基础值记得补 `setHealth(getMaxHealth())`：实体是在构造期按旧上限填满血的，`finalizeSpawn` 提高上限不会自动加血，否则召唤出来是"12/30"。

实例见 [[soul-vulture-servant-port]]（按主人灵魂能量 ×1.0–2.5）。相关：[[config-file-split-rule]]、[[goety-summonspawn-owner-order]]。
