---
name: goety-ac-no-mixin
description: "Alex's Caves 兼容不许用 mixin；改走方块状态读写 / Forge 事件这两条纯 API 路线"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: f9629217-d45e-4fb1-9e1f-138a63788b4a
  modified: 2026-09-13T15:41:28.878Z
---

用户明确要求：**给 Alex's Caves 做的兼容机制不许用 mixin 实现**（原话"不许用mixin实现"，2026-09-13，瞑煌龙仆从原始岩浆那条）。当时我已经把 `AcPrimalMagmaBlockMixin` / `AcFissurePrimalMagmaBlockMixin` / `GoetyOminousMixinPlugin` 三个类写进仓库并通过编译，被要求全部撤掉，`goetyominous.mixins.json` 也回滚。

**Why:** 本仓库 `goetyominous.mixins.json` 是 `"required": true`，且 30 条 mixin 全部只打 MC 原生类或 Goety 硬依赖；给 `compileOnly` 的可选依赖加 mixin 需要额外引入 `IMixinConfigPlugin` 门控，仓库里零先例，风险与维护成本都由用户承担。宁可功能打折也不要这条耦合。

**How to apply:** 需要改可选依赖的行为时，先穷举纯 API 手段再考虑功能缩水，别默认上 mixin：
- 直接读写它的方块状态 / 调用它的 public static（瞑煌龙那条就是 `setBlockAndUpdate(pos, state.setValue(ACTIVE, true))` 激活原始岩浆，配合"把仆从脚下保持 ACTIVE=false"来换回完整碰撞形状）
- 用它的公开静态判定别人要不要动手（`PrimalMagmaBlock.isBossActive(level)`），别去注入它
- Forge 事件（`LivingAttackEvent` / `EntityMobGriefingEvent` 等）几乎能覆盖原版钩子需求
- 实在绕不开的（如 `FissurePrimalMagmaBlock` 无条件陷落）就**如实告诉用户这块做不到**，不要偷偷写 mixin 绕过

相关：[[ac-compat-porting-notes]]、[[forge-hot-floor-fireimmune]]、[[goety-ac-dinosaur-spirit-port]]
