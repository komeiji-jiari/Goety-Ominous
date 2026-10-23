---
name: forge-goalselector-in-aistep-ordering
description: "goalSelector.tick 在 LivingEntity.aiStep 内部:想在 goal 决策前改动画/状态,必须放在 super.aiStep() 之前"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 24adf4ac-b3cd-48ba-b0ff-c10585b1db8e
  modified: 2026-10-07T11:49:58.213Z
---

`Mob.serverAiStep()`(内含 `goalSelector.tick()`/`targetSelector.tick()`)是被 `LivingEntity.aiStep()` 调的(`.scratch/mcsrc` LivingEntity:2548)。所以实体自定义 `aiStep()` 里写在 `super.aiStep()` **之后**的逻辑,本 tick 内晚于所有 goal 的 `tick()`。

**How to apply:** 要让 goal 在同 tick 就看到新状态(典型:抢先 `setAnimation(ROAR)` 免得近战 goal 因 `getAnimation()==NO_ANIMATION` 先起一个攻击动画、音效已经播出去才被覆盖),把这段逻辑放在 `super.aiStep()` 之前(样板 `LuxtructosaurusServant.aiStep()` 开头的 `tickEnrage()`)。放在 `tick()` 里 `super.tick()` 之后也能抢先,但那是下一 tick 的 goal。副作用:放在前面意味着读到的 `getTarget()` 是上一 tick 的,对"目标存在即触发"这类判定无影响,对需要本 tick 受击/目标变更的判定要留意。

相关:[[goety-mount-idle-ai-authority]] [[forge-bypass-intermediate-tick-baseTick]] [[goety-servant-goal-priority-flags]]
