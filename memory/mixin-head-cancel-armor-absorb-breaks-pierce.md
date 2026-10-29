---
name: mixin-head-cancel-armor-absorb-breaks-pierce
description: "在 LivingEntity#getDamageAfterArmorAbsorb 的 HEAD 处 cancellable 注入会吞掉其它模组的护甲穿透/撕裂（2026-10-29 修复重锤 Breach）"
metadata: 
  node_type: memory
  type: project
  originSessionId: 9d2e8172-48bb-4658-acc5-9a2b0a9e2149
  modified: 2026-10-29T16:10:35.232Z
---

不要在 `LivingEntity#getDamageAfterArmorAbsorb` 用 `@Inject(at=HEAD, cancellable=true)` 整段重写原版逻辑。原版方法体一旦被取消，其它模组注入到**方法体内**的钩子全部不执行：

- ApothicAttributes（神化前置）：`@Redirect` 掉方法体里的 `CombatRules.getDamageAfterAbsorb` 调用 → 盔甲穿透 `ARMOR_PIERCE` / 盔甲撕裂 `ARMOR_SHRED`（以及它 `@Overwrite` 的 a/(a+armor) 护甲公式）。
- RevelationFix 4.8（GoetyRevelation 2.3.5 的 jarjar）：用 ASM 在同一个 `CombatRules.getDamageAfterAbsorb` 调用点后插 `EntityRedirectUtils.quietusArmorAbility` → `armor_penetration` 属性。

本工程曾因此让 MaceBreachMixin（重锤 Breach 附魔）吞掉上述两者。

**Why:** HEAD cancel 跳过的是整段原版指令流，不是只替换返回值；mixin 注入点顺序上也看不到。

**How to apply:** 要叠加伤害修正就注入 `@At("RETURN")`，用 `cir.getReturnValue()` 拿到已被所有模组修正后的结果，再在其上做增量（Breach 的等价式：`clamp(result + damage*0.15*lv, 0, damage)`；由 `ratio=1-result/damage` 反推）。只有确实要替换整套护甲公式时才 HEAD cancel，且要接受其它模组钩子全失效。相关：[[mixin-redirect-conflict-return-wrap]]、[[goety-1.21-explosion-null-damagesource]]
