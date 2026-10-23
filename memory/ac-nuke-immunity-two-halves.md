---
name: ac-nuke-immunity-two-halves
description: "AC 核爆对撼地斯拉的免疫是 NuclearExplosionEntity 里硬编码的 instanceof,且击飞在 hurt 之后逐 tick 无条件执行 — 只 cancel 伤害拦不住,速度必须另拦"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7f16c69a-7953-4573-885b-66c38efba6d1
  modified: 2026-09-15T01:01:14.055Z
---

`NuclearExplosionEntity` 的伤害循环(每 tick 跑,不在挖方块分支里)对每个 killBox 内实体做两件事,免疫需要**两半都复刻**:

1. `entity.hurt(causeNukeDamage / causeIntentionalGameDesign, damage)` —— 只有 `damage > 0` 才调
2. `entity.setDeltaMovement(vec3.scale(damage * 0.1f * playerFling))` —— **在 hurt 之后、且在 `damage > 0` 判断之外无条件执行**,每个 tick 一次

AC 对撼地斯拉的免疫两行都在同一个循环里:`instanceof TremorzillaEntity` → `damage = 0.0f`(跳过 hurt)+ `playerFling = 0.0f`(置零速度)。Raycat 只归零伤害不归零击飞。

坑:
- **免疫不是 tag 驱动**。`#alexscaves:resists_radiation` 只给 ×0.25 伤害 + 击飞 ×0.1,真正归零的是硬编码的 `instanceof TremorzillaEntity`。`TremorzillaServant extends AnimalSummon` 两个分支都不沾 → 默认 size 3.0 时 baseDmg 700、击飞速度 ≈69 格/tick。
- **只 cancel 伤害是坏的**,仆从会带着 69 格/tick 飞出去。非 mixin 的做法:`hurt()` 覆写里识别 NUKE/INTENTIONAL_GAME_DESIGN → 返回 false 并置一个自减 guard,在 `tick()` 开头(必须在 `super.tick()` 之前,否则 `aiStep`→`travel` 已经消费掉了)按 guard 把 deltaMovement 置零。因为核爆的 set 永远由实体的**下一个** tick 的 travel 消费,所以无论核爆和实体谁先 tick,顶部置零都能拦到。
- AC 里**只有** `NuclearExplosionEntity` 用这两种伤害类型(class 文件全量 grep 确认),所以拦这两种类型不会误伤别的东西。
- 辐射那一半不用管:IRRADIATED 效果是无条件 addEffect 的,而撼地斯拉(AC 原版与仆从)本来就在 tick 里"吃 IRRADIATED 回 12×等级 血并清除"。

相关:[[ac-tephra-explosion-quirks]] [[ac-ao-ally-check-direction]]
