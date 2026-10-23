---
name: goety-mount-immobile-anim-root
description: "用覆写 isImmobile() 让动作(咆哮)定身可行,但不影响 tick()/AnimationHandler;骑乘坐骑必须用 !isRiddenByPlayer() 排除,否则 areLegsMoving() 里的 !isImmobile() 会让 getRiddenSpeed 归零"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6e05f794-1069-466c-b853-be81b3148e3e
  modified: 2026-09-14T02:17:14.730Z
---

要让某个动画期间"站住不放"（瞑煌龙 ANIMATION_ROAR 60 tick 定身），Goety 惯例是覆写 `isImmobile()`（Leapleaf `restTick > 0`、RedstoneGolem `isSummoning()||isActivating()`）。原理：`LivingEntity.aiStep()` 里 `if (isImmobile()) {jumping=false; xxa=zza=0;} else if (isEffectiveAi()) serverAiStep();` —— 只跳过 serverAiStep（goal/target 选择器、navigation、lookControl、moveControl 全停），**`tick()` 照常跑**，所以 `AnimationHandler.updateAnimations()`、屏幕震动、粒子、`tickRoarFallback()` 都还在（注意 `tickRoarFallback` 是写在 `aiStep()` 里 `super.aiStep()` 之前，定身时也照样执行）。

**Why:** 瞑煌龙的周期性咆哮原来会在"腿还在迈步"时播放，头和腿姿势打架；只把触发条件改成"脚步停下来才吼"不够——跟随目标每 tick 都会 `navigation.moveTo` 让腿重新迈起来，必须把整只龙定住 3 秒才是"站定咆哮"。同时骑乘时按 K 键的咆哮不能定身（玩家要能继续操控坐骑）。

**How to apply:** `return this.getAnimation() == ANIMATION_ROAR && !this.isRiddenByPlayer() || super.isImmobile();`
- `!isRiddenByPlayer()` 是硬性要求：同类里 `areLegsMoving()` = `(WALKING || 鞭尾) && !isImmobile() && !isNoAi()`，而定身会让 `getRiddenSpeed()` 直接 0 → 坐骑原地卡死。骑乘时 `tickRidden` 也不再拦 ROAR（见 [[goety-mount-idle-ai-authority]]、[[forge-travelridden-server-zero-delta]]）。
- 定身期间 `moveControl` 不 tick，`WALKING` 会保持陈旧值，但 `areLegsMoving()` 被 `!isImmobile()` 挡掉、`walkAnimSpeed` 以 0.025/tick 自然衰减，腿会平滑收势而不是硬切姿势。
