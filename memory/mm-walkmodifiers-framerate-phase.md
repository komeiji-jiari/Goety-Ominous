---
name: mm-walkmodifiers-framerate-phase
description: "MM 的 AdvancedAnimationUtils.addWalkModifiers 用 getFrameTime()(子 tick 余数)当\"每帧推进量\"累加 walk_progress → 走路相位随帧率线性变化(≈FPS/40 倍,40fps 才是 1.0);MM 里唯一使用者是 MutantWitherSkeletonModel"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-15T22:12:25.314Z
---

**MM `AdvancedAnimationUtils.addWalkModifiers`(2026-10-15 定位,用户报「突变凋零骷髅走路有时候不流畅」):**

```java
animation.getModifiers().put("walk_progress",
    Mth.lerp(Minecraft.getFrameTime(), prev, prev + distanceTravelled / 300.0f * walkProgressMultiplier));
animation.getModifiers().put("ground_speed", distanceTravelled * groundSpeedMultiplier);
```

`setupAnim` **每渲染帧**调一次,而 `Minecraft.getFrameTime()` 是 `Timer.partialTick` —— **当前 tick 的子 tick 余数 [0,1),不是本帧流逝时间**;它在一个 tick 内的 n 帧上取值平均 0.5 → 每 tick 累加量 ≈ n/2 = FPS/40,而作者的本意显然是每 tick 累加 1 倍(40fps 才等于 1.0)。所以 `walk_progress` 推进速率 ∝ 帧率:**20fps 半速 / 60fps 1.5× / 144fps 3.6×**,帧率一波动步频就跟着变 → 观感就是「有时候不流畅」(高帧率下腿摆成一团模糊)。`Timer.tickDelta`(`getDeltaFrameTime()`)才是每帧流逝的 tick 数,一 tick 内 Σ=1;`ageInTicks`(tickCount+partialTick)同样 Σ=1 且跟游戏时间而非墙钟,更适合。

**判据/易漏点:**
- 整个 MM 只有这一个模型用 `addWalkModifiers`(`grep -rln` 过 models/entities,只有 `MutantWitherSkeletonModel`);MM 其他怪走路是 `AdvancedAnimator` 的 tick 化 `amount`/`progress`,或正弦路径,都不受影响。所以「只有突变凋零骷髅走路怪」是符合预期的。
- `walk` 这套 AdvancedAnimation 的 `AdvancedAnimationDefinition.withLength(0.0f)` + 全部键帧写在 t=0 用 lambda 取值 → 姿势**完全由 `walk_progress`/`ground_speed` 两个 modifier 决定**,`loopTime`/`progress()` 不参与(`f = lerpProgress() % 0.0f` = NaN,反而让二分查找稳定落在 keyframe 0,没问题)。
- `ground_speed = distanceTravelled * 3.0f` 是**每 tick 量**(`getX()-xo`,xo 在 `tickNonPassenger` 里 tick 前刷新)→ 只当振幅用、不累加,所以它不随帧率漂移,保持上游原样。
- 同文件 `addLegHeightOffsetModifier` 也是 `Mth.lerp(getFrameTime()*lerpSpeed, ...)` 的每帧平滑(帧率越高贴地越快),实测响应 0.1/tick@20fps → 0.24@60fps → 0.52@144fps;量级只有几厘米(0.83 model unit /16 ×1.2 缩放)。已改(见下)。
- `MutantWitherSkeletonServantRenderer.setupRotations` 里 `lungingXRot = Mth.lerp(partialTick, lungingXRot, ...)` 是同一个 idiom,还写在实体字段上(每帧渲染改状态)→ 高帧率下 tween 被压缩成直跟 `getDeltaMovement().y*30`,扑击时机身俯仰会抖。已改(见下)。MM 上游 `MutantWitherSkeletonRenderer:54` 逐字相同 → 继承性缺陷,不是移植漏抄。

**本仓库修法(2026-10-15,三处一起改,未构建):**
- **相位**(`MutantWitherSkeletonServantModel`):不再调 `AdvancedAnimationUtils.addWalkModifiers`,自己写同名私有 helper,照 [[mm-zombie-sinewave-walk-jitter-fix]] 的定案——`deltaTicks = clamp(ageInTicks - entity.walkPhaseAge, 0, 2)`(首帧 1.0),`entity.walkPhase += distanceTravelled/300 * deltaTicks`,`walkPhaseAge = ageInTicks`。跨帧状态放实体(`AbstractMutantServant.walkPhase/walkPhaseAge` 现成的,凋零骷髅自己没用过这两个字段)。
- **`deltaTicks` 只在 `setupAnim` 顶部算一次**,三个 helper 共用同一个 `entity.walkPhaseAge`;否则走路 helper 先写了 `walkPhaseAge = ageInTicks`,后面两个贴地 helper 再算一次必然得 0(改这块时的头号坑)。
- **贴地**:同文件加私有 `addLegHeightOffsetModifier(..., float deltaTicks, ...)`,**复用 MM 的 public static `getLegHeightOffset` / `modifierIfPresent`**(不用抄它们),只把平滑系数换成 tick 化 `Math.min(deltaTicks * lerpSpeed, 1.0F)`(= 作者本意的 0.2/tick)。
- **扑击俯仰**:`MutantWitherSkeletonServant` 加 `lungingXRotO`,覆写 `updateAnimations()`(客户端每 tick:`O = 当前值;当前值 = isPlaying ? clamp(dy*30,-90,90) : 0`),渲染器只留 `Mth.lerp(partialTick, lungingXRotO, lungingXRot)` 且**不再在渲染里写实体状态**。这等于把它做成标准的 `xRotO/xRot` 平滑,比上游"partialTick 当 tween 系数"更贴意图(该文件的 `lerpSpeed=0.2` 说明作者认为系数就是"每 tick 比例")。

**视觉后果要跟用户讲清楚:** 帧率越高改完越慢(走路 144fps 会慢 3.6 倍),这是回到设计速率而不是变卡;扑击俯仰现在晚一 tick(标准插值)。同世界里 MM 原版敌对突变凋零骷髅仍是旧行为,可直接对比验证。

相关:[[mm-advanced-animation-client-sync]]、[[mm-zombie-sinewave-walk-jitter-fix]]、[[goety-model-reuse-private-fields]]
