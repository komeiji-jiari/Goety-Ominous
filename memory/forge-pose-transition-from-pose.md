---
name: forge-pose-transition-from-pose
description: 瞬间切换姿态动画(趴下/起身)可用 grounded 定义程序化生成过渡;tick 数必须 = 动画秒数×20 才能无缝交接
metadata: 
  node_type: memory
  type: reference
  originSessionId: 49986137-b92f-438f-b999-807482b0966a
  modified: 2026-09-19T06:45:23.070Z
---
原版 `AnimationState.start()` **会清零 accumulatedTime**(`lastTime = tick*1000/20; accumulatedTime = 0`),`startIfStopped` 只在未启动时 start,`stop()` 不清累计值——所以"停→再启"永远从 0 播,不需要手动 reset。
姿势过渡的标准做法(样板 `WargAnimations.sit_down` / `stand_up` + `PoseTransition.build(grounded, 0.6F, true)`,用于座狼/地狱三头犬):
- 遍历姿势定义的 `boneAnimations()`,取 `channel.keyframes()[0].target()` 当把手值;**首关键帧是单位向量((0,0,0) 的 posVec/degreeVec、scaleVec 存的是 v-1)的通道直接跳过**,否则会把手部强制拉回 rest。
- 入座 = 0.0s 全零 → length 全 held;起身反过来。LINEAR 插值。
**交接数学(关键,错了会有一帧跳变)**:定义非循环时播到头会保持末帧,所以状态机 `SIT_DOWN_TICKS` 必须等于 `lengthInSeconds * 20`(12 tick ↔ 0.6s、10 tick ↔ 0.5s)。渲染时 `ageInTicks = tickCount + partialTick`,ON 的第 N tick 末帧累计 = `(N-1+partialTick)*50ms`,partialTick→1 时正好 = 定义长度,下一 tick 起始的 grounded@t=0 与过渡末帧同姿势 → 零跳变。
**已知残留**:过渡期间 `idle` 被门控关掉,只由 idle 驱动的通道(尾巴/fur scaling 等,因为首帧为零被跳过)会从 idle 当前相位瞬回 rest——量级 ≤16°/3%,不值得处理。
相关:、[[goety-mount-immobile-anim-root]]