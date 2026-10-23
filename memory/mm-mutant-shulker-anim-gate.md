---
name: mm-mutant-shulker-anim-gate
description: 潜影贝仆从模型做两件门的活：priorityActive 必须逐条镜像实体 updateAnimations 的动画链（漏一条会被走路分支的 resetAll 抹掉）；走路混合的状态更新必须放在 priorityActive 之外，否则门内期间权重被冻结、出帧那一下满权重贴走路
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f63f6c-bee0-455c-b643-9f301c3041ff
  modified: 2026-10-16T01:59:54.385Z
---

**坑一（2026-10-16 修复）：`priorityActive` 是实体动画链的手抄镜像。** `MutantShulkerServantModel.setupAnim` 里 `priorityActive == false` 时才跑走/站分支，而那个分支开头就是 `this.resetAll()`，会把链里已贴上去的姿态整帧抹掉。所以**链里每有一条 `animateWhen` 条件成立的动画，模型侧列表就必须有对应的 `isStarted()`**（判据：动 `MutantShulkerServant` 的链时同步核 `priorityActive`）。当初用户报的"飞行动画有时候用的是行走"就是 `spinningAnimation`（飞行恒播）漏在列表外——飞行时 `walkAnimation.speed()` 过 0.075 就切走路。上游不会出这问题是因为它的实体链里 `shulkerWalkAnimation.animateWhen(… && !somethingAnimating)` 排在 spinning **之后**，前序压制天然成立；移植版把走路挪到模型侧程序化混合，那份压制只能靠 priorityActive 手工表达。同理可推 [[mm-jungle-zombie-servant-port-decisions]] 里那几个"模型侧写死"的分支。

**坑二（2026-10-16 二次修复）：走路的混合权重状态原本也在 `if (!priorityActive)` 里面。** 后果：**门成立期间 `walkAmount`/`wasWalking` 被冻结在进门前的值**（通常是走路走过去、walkAmount≈1），一旦门失效，模型下一帧就以满权重贴走路姿态。当时的触发源是飞行（起飞时 walkAmount 还是 1），但这是**门本身的普遍问题**，换成任何长动画都一样。

**修法**：把状态更新搬到 `if (!priorityActive)` **外面**（每帧都跑）。落地后从 0 重新爬升（`tickAmountMultiplierChange(value, shouldPlay, transitionSpeed)` 是线性逼近，`shouldPlay=false` 就往 0 走）。

**现状**：飞行已整块删除（[[mm-shulker-flight-removed]]），`walking` 的条件现在只剩 `pEntity.onGround()`。

相关：[[mm-sine-walk-phase-footstep-lock]]、[[goety-model-reuse-private-fields]]
