---
name: goety-egg-owner-requires-crouch
description: "Goety ServantSpawnEggItem 只有潜行放置才 setTrueOwner;所有按 owner 门控的交互(剪链子等)会静默失效,看着像功能没做"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 4c9d4684-d3af-4bca-9856-70ab807d125b
  modified: 2026-10-03T14:03:05.910Z
---

`ServantSpawnEggItem`（Goety）在 `useOn` / `use` 里都是：

```java
if (entity instanceof IOwned owned && !owned.isHostile() && player.isCrouching()) {
    owned.setTrueOwner(player);
    ForgeEventFactory.onFinalizeSpawn(...);   // 也只有这个分支才 finalizeSpawn
}
```

`Owned.HOSTILE` 默认 false（`Owned.java:240`），所以 `isHostile()` 不挡路，**挡路的是 `isCrouching()`**。

**Why:** 走 `/summon` 或者站着放蛋 → 无主人。所有 `if (getTrueOwner() != null && player == getTrueOwner())` 门控的交互全部静默失效。典型症状：链子/锚照常渲染（那部分不看 owner），但拿剪刀、锁链右键完全没反应 → 报告成"这机制没做"。`MineGuardianServant.mobInteract` 的剪链/接链就是这个结构。

对照：自家 ritual 召唤路径（如 `MineGuardianSummonHandler`）里是**无条件** `setTrueOwner(player)`，所以那条路出来的仆从一定有主人。

**How to apply:** 用户说"某个交互没反应"时，先问/先查获取途径。验收时一律**潜行放蛋**。要绕开就只在子类放开 owner 判据，别改基类。

相关：[[goety-ac-servant-keydown-channels]]、[[goety-servant-riding-mechanism]]。
