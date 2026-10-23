---
name: mm-shulker-flight-removed
description: 2026-10-16 用户下令「删除突变潜影贝飞行 以后不飞了」——仆从的飞行整块代码已删，别再按 MM 上游把它加回来；MM 上游的 uses_fly 等配置键故意留成未接线，不是漏接
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f63f6c-bee0-455c-b643-9f301c3041ff
  modified: 2026-10-16T01:59:50.321Z
---

**用户原话**：2026-10-16「删除突变潜影贝飞行 以后不飞了」。这是**彻底删除**，不是加开关禁用。删掉的东西：`MutantShulkerServantFlyGoal.java` 整个文件、`MutantShulkerServant` 上的 `flying`/`flightInterrupted`/`prepareFlyAnimation*`/`spinningAnimation`/`enterSpinAnimation` 字段、goalSelector 里第 2 优先级的 fly goal、`handleEntityEvent` 的 5/6/7（起飞/被打断/落地进壳）、两条 mathAnimateState、`hurt()` 里"受伤取消飞行"整块与传送分支的 `!flying` 守卫、`aiStep` 传送条件里的 `teleports_while_flying` 子句、`teleport()` 的飞行守卫，以及 `MutantShulkerServantModel` 里对应的两行 + `priorityActive` 两条 + `walking = onGround() && !flying` 的 `!flying`。

**没删、也不许删的**：MM 上游 jar 里的配置键 `uses_fly` / `fly_health_threshold` / `stop_flying_time` / `flying_movement_speed` / `fly_cooldown` / `enters_shell_after_flying` / `getting_hit_cancels_flying` / `teleports_while_flying`。它们不是我们的键，现在**故意未接线**——不用，也不清理。见 [[mobsconfig-unwired-keys-are-not-dead]]。

**别混淆的对象**：敌对 MM 实体 `MutantShulker` 仍然会飞，删的只是**仆从**这边。模板类 `MutantShulkerModel` / 上游 `MutantShulker.m_6469_` 里的飞行逻辑照旧。

**连带作废的旧记忆**（已删）：`mm-shulker-hit-cancel-no-shell`（被打断飞行不闭壳）、`mm-shulker-no-teleport-while-flying`（飞行禁瞬移）——它们描述的就是被删掉的代码。壳行为本身还在：`isInBox` / `EnterShellGoal` / `LeaveShellGoal` / `nextEnterShellTime` 与飞行无关，保留。

相关：[[mm-mutant-shulker-anim-gate]]、[[mm-shulker-bullet-trajectory]]、[[mm-shulker-levitation-injection-points]]
