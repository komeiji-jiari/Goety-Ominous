---
name: hurricane-breeze-intentional-divergence
description: 飓风与旋风人的 11 项机制分叉是逐项确认过的有意设计，不是移植漏抄；别自动对齐
metadata: 
  node_type: memory
  type: project
  originSessionId: b6b5ba33-15fb-4b3d-942b-8a1b5846ab4d
  modified: 2026-10-10T13:56:58.199Z
---

`AbstractHurricane` 与 `BreezeEntity` / `BreezeServant` 有大量同名同源但实现不同的方法。2026-10-04 逐项确认过：**11 项差异里 10 项是有意保留的分叉**，只有 1 项是对齐的。

保持分叉的 10 项（不要以"和旋风人一致"为理由改回去）：
- `hasLineOfSight` 用 `getEyePosition()` 起点（旋风人用脚底 `new Vec3(getX,getY,getZ)`）
- 脚下游走的 `CLOUD` 粒子（`nextInt(4)==0`，旋风人没有）
- 低频低沉音色：覆写 `getSoundVolume()`=1.4F、`getVoicePitch()`=0.45F，影响**全部** `playVoice` 音效
- 旋风音用 `getSoundVolume()*0.8F`=1.12 固定 / `getVoicePitch()`，不用旋风人的 `0.8+0.2r` / `0.7+0.4r`
- 反弹音 `1.2F, 0.6F`（旋风人 `1.0F, 1.0F`）
- `isInvulnerableTo` 额外免疫箭矢 / `WIND_CHARGE_DAMAGE_TYPE` / `IS_FALL`
- 跳跃初速固定 `MAX_JUMP_VELOCITY = 3.0F`，不吃跳跃提升药水
- 跳跃角度按落点高度差分三组（FLAT/CLIMB/STEEP）且不打乱（旋风人单组 5 角 + `toShuffledList`）
- `randomPointBehindTarget` 距离 8~15（`MIN_HOP`/`MAX_HOP`，旋风人 4~8）；`randomPointAwayFrom` 共用这两个常量
- 不实现 `BreezeLike`、不挂 `BreezeIdleSlideGoal`，没有滑行机制（移动靠 `KeepDistanceGoal` 绕 7 格）

唯一对齐项：`SmashJumpGoal.canJumpFromCurrentPosition()` 补了蜂蜜块判断（站在蜂蜜块上不起跳），检查范围仍是 `ceil(bbHeight)~+5`。

**2026-10-10 改（用户要求"旋风人和飓风在水上也照样能跳起来"）**：两边都放开了水上起跳，这是新增的有意行为，别当"分叉/漂移"改回去。
- 飓风：`canUse()` 的 `onGround()` 放宽成 `onGround() || isInWater()`；`canJumpFromCurrentPosition()` 的"只认空气"改成空气或水（对齐旋风人原有写法）；`snapToSurface()` 上/下 clip 都 miss 时回退取该点的水方块（否则深水 >20 格 clip 找不到落点，同旋风人 10 格）；新增 `leftWater` 字段——从水里起跳时 `isInWater()` 会持续到离水，原 `timer > 3 && isInWater()` 会在第 4 tick 把刚起跳判成落地，所以入水落地要等"离过水之后"（从陆地起跳时该字段初始为 true，落地水里的旧行为不变）。跳跃速度/角度/8~15 格落点这些**参数一个没动**。
- 旋风人（`BreezeServant` + 野生 `BreezeEntity`）：`tickReposition()` 里 `stuck` 去掉了 `isInWater()`——原来一进水里就把 `shootWindow` 顶满 100，抢在 `grounded && tryStartJump` 前面无限循环射击，根本走不到跳跃分支；`snapToSurface()` 同样加水方块回退。它本来就有水容忍（`grounded` 含 `isInWater`、`canJumpFromCurrentPosition` 认水、`landed` 认水），所以这才是唯一堵点。

**Why:** 这些看着像移植漂移，实际是体型/定位差异的刻意调节（飓风三格高、高抛滑翔砸地型）。盲目对齐会改掉平衡、音色和跳跃手感。

**How to apply:** 要动飓风这些参数先问，别自作主张"统一"。分析同类拷贝时的坑：`BreezeEntity` 的 idle 粒子写的是 `IDLE_PARTICLES_AMOUNT + getRandom().nextInt(1)`，`nextInt(1)` 恒返回 0，等价于固定 1 个——我误报过一次"有随机抖动差异"。相关：[[ac-servant-parity-keep-original]]、[[goety-servant-goal-priority-flags]]
