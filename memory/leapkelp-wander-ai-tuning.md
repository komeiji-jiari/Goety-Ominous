---
name: leapkelp-wander-ai-tuning
description: 潮涌之叶水下漫游平滑改造的四条实测根因（转向半径/海平面/novelty 门控/抢目标）与当前保留的实现
metadata: 
  node_type: memory
  type: project
  originSessionId: fb282f87-4346-49ff-8544-2bfd56a35c4b
  modified: 2026-10-30T02:05:45.372Z
---

2026-10-30：为"水下漫游身体别转太大 + 游一会不动了"改造 `Leapkelp.java`（保留在 HEAD 之上的工作区改动，未提交）。实测出四个根因：

1. **绕圈**：`LeapkelpMoveControl` 转向限速 10°/tick + rotlerp 在 0.7 格内返回源角 → 最小转弯半径 v/ω 大于到路点距离，永远绕路点转（见 [[mob-turn-cap-min-radius-orbit]]）。改用 90°/tick 直追 wanted，视觉平滑交给 `SmoothBodyRotationControl`（yBodyRot 每 tick 只追 0.5×误差、限 ±12°/tick）+ tick 里 yHeadRot 限 ±12°/tick。
2. **上浮 goal 空转**：原 `LeapkelpSwimUpGoal` 继承 Drowned 写法，目标是**世界海平面**（seaLevel-1），浅池塘水面比它低，永远 `canUse` 为真却够不到，每 10 tick 重抽随机方向、顶掉游荡。改为本地水面扫描 `getSurfaceY()`（从脚下向上扫水柱）+ 固定 `upYaw` 直上，失败才 +90° 半距离重试；优先级从 6 降到 9。
3. **游荡只跑一段就停**：`RandomStrollGoal.canContinueToUse` 是 `!nav.isDone()`，到点就结束，再等随机重掷。改为在 `canContinueToUse` 里 nav 完成后立刻抽下一个目标（`getPosition()` + `moveTo`）连续游。
4. **novelty 门控吃掉重掷**：HEAD 的游荡 `canUse` 加了 `!isNovelty`，Leapleaf 空闲 10s 后 5%/tick 进 novelty，导致日志里 `goals=[]` 空窗 10~18s。去掉该门控。

5. **游泳蛇形抽动**（日志 `[LeapkelpDbg] t=` 每 10 tick 采样：位置走直线，yRot 却在 170→152→225→239→199… 来回摆 ±25°）：`PathNavigation.tick` 把 `MoveControl.wanted` 设成**下一个路径节点**（离身体 1~2 格），横向误差在近距离上被放大成很大角度，90°/tick 直接追 → 过冲 → 反来过冲，且 `travel()` 的 `moveRelative` 方向直接用 yRot，所以速度矢量每隔几 tick 横向弹一下 = 抽动。改法：`LeapkelpMoveControl.steerPoint()` 沿路径向前取最远不超过 5 格（`SWIM_LOOKAHEAD_SQR`）的节点当转向目标，转向限速从 90°/tick 降到 20°/tick（`SWIM_MAX_TURN_Y`）。注：匿名 goal 的 `getClass().getSimpleName()` 是空串，日志里 `goals=[]` 不代表没 goal 在跑。

目标选取（最新版）：维持稳定航向 `courseYaw`，在 ±20° 窄锥里按半径 16/12 选点，最短段 6 格；每段完成时 `nextLeg()` 计数，走满 1~3 段（`WANDER_LEGS_MAX`）就主动转弯（±45~135° 随机），即"直着游一会→转弯"；锥里没水（池壁）才走兜底换航向（±90° 随机，抽中后把 courseYaw 对齐实际落点避免来回摆）。旧版，找不到再抽随机方位全圆（π）兜底，并用 `findWaterTarget` 连续 3 次校验目标处是水。

**水中待命动画**：`LeapkelpModel` 的游泳分支只用 `limbSwing/limbSwingAmount` 驱动划水，停住时两者归零 → 完全静止。加 `applySwimIdle(ageInTicks, (1 - clamp(limbSwingAmount*4))*swimAmount)`：用 ageInTicks 当相位跑同一套 `applySwimMotion`（gain 0.35）+ root.y 轻微上下浮沉。注意该模型里 `stroke()` 的"度数"常量实际直接加到弧度字段上（0.5 就是 0.5 rad），不要当角度读。

**贴墙/贴底抖**：`isSwimmingPose()` 里 `noCollision(getBoundingBox())` 在贴着水底/方块时会逐 tick 真假跳，`swimPoseAmount` 目标值跟着抖 → 模型在泳姿/站姿之间闪。改成 `canSwimPose()` 原始判断 + `swimmingPose` 状态场、连续 4 tick 不一致才翻（跳 tick 的抖动被吃掉）；同时 `LeapkelpGoToBeachGoal.isValidTarget` 加了 `getFluidState(above).isEmpty()`，不再把**水下**的可站立方块当岸边目标（会一直贴着池底那块方块游/挤）。

**How to apply：** 再调这片代码时一次只动一个变量，加临时日志实测（t/pos/yRot/body/vel/nav/idx/nodes/next/goals、以及 pose 原始判定的翻转）再动下一个；行为确认后删掉调试日志与 `GoetyOminous` import（2026-10-30 已全部删净，用户确认"好了"）。

**坑：** 匿名 goal 的 `getSimpleName()` 是空串，日志里 `goals=[]` 不代表没有 goal 在跑，别据此判断"卡住"；判断只用 t/pos/vel/nav 的不变量。相关背景 [[goety-leapkelp-land-water-leap]]、[[leapkelp-water-smash-resmooth]]。
