---
name: mm-sine-walk-phase-footstep-lock
description: 正弦走路相位怎么跟脚步对齐:一个腿周期(speed 250)= 7200/250 = 28.8 相位单位 = 左右腿各迈一步 = 2 个脚步声 = 3.3333 格 → 每格 8.64 相位单位;距离必须用位置差不能用 getDeltaMovement;2026-10-15 落地→当天回退→2026-10-16 问过后重新落地(现状)
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-15T23:06:00.188Z
---

**定标公式(2026-10-15,用户报"三种突变僵尸跟随/游荡时移动动作太慢且对不上脚步声"):**

1. MM 正弦腿的周期:`addSineWaveMotionToModelPart(part, amount, speed, tick, …)` 里角度 = `sin(tick × speedMultiplier × speed + offset)`,而 `tick = walkPhase × (π/180)/20` → **一个完整腿周期 = 7200/(speed × speedMultiplier) 相位单位**。三个僵尸的腿都是 `speed = 250.0F`、调用时 `speedMultiplier = 1.0F` → **28.8**。(疣猪兽 walk 传 1.2 → 24;它的 trot/charge 只是同一相位的不同倍率。)
2. 左右腿是 `∓amount × sin(θ)`(offset 都是 0)= 严格反相 → **一个周期 = 每条腿迈一步 = 2 个脚步声**。
3. 原版脚步声间隔:`Entity.move`(`Entity.java:693-694`)`moveDist += 3D 位移 × 0.6F`,越限(`moveDist > nextStep()`,`968-969` `nextStep() = (int)moveDist + 1`)就 `playStepSound` → **每 1.6667 格一声**,与生物种类/速度无关。
4. ⇒ 腿周期应覆盖 2 步 = 3.3333 格 ⇒ **k = 28.8 / 3.3333 = 8.64 相位单位/格**,相位按 `walkPhase += 每 tick 位移 × k` 积分即自动与任何速度下的脚步锁死(跟随的加速倍率不用管)。

**距离必须用位置差**:`sqrt((entity.getX() - entity.xo)² + (entity.getZ() - entity.zo)²)` —— 与喂脚步的 `moveDist` 同源(MM 自己的 `AdvancedAnimationUtils.addWalkModifiers` 43-46 行也是这么算的,`yDiff` 固定 0)。**不能用 `getDeltaMovement()`**:它是 travel 末尾乘过摩擦的存量值(普通地面 0.546× 位移,冰面 0.89,空中 0.91),直接当"走了多远"会把相位压到 55% ≈ 慢一倍 —— 见 [[mc-client-deltamovement-motion-packets]]。

**沿革:** 2026-10-15 首次落地 → **当天用户令回退**(原因未说明)→ **2026-10-16 用户又报同一症状("动作太慢 脚步声太快 一直在移动 而不是走一步是一步"),这次先问过、用户选了"改回位置差 ×8.64 版"**,于是重新落地。三个模型(`Mutant{FrozenZombie,Husk,JungleZombie}ServantModel`)现在都是下面这版。

**⚠ 别整套贴 `.scratch/_mmz_walkphase_full_backup.patch`:** 名字里的 full 是**整文件快照**,取自 walk/idle 平滑混合那一轮**之前** —— 贴上去会把 entity 上的 `walkPhase`/三个 amount、互补平滑权重、键帧姿态快照 lerp 全部退回"模型单例存状态 + 二值 `? 1.0F : 0.0F`"的老版本。只从它里面取那三行算术。

**落地(现状,只改了那一行):** `Mutant{JungleZombie,Husk,FrozenZombie}ServantModel.setupAnim` 由
`Math.min((float) entity.getDeltaMovement().horizontalDistance() * 7.0F, 6.0F)` 改为
`Math.min((float) Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 8.64F, 6.0F)`,`xDiff/zDiff` 就在上面两行(`getX() - xo` / `getZ() - zo`)。**诊断口径(两次报的都是它):** 旧的 `getDeltaMovement().horizontalDistance() * 7.0F`,7.0 × 0.546 ≈ 3.8/格,只到锁定值 8.64 的 **44%** → 腿摆动只有脚步声速率的 44%,听感就是"动作慢、脚步快、一直在滑不落脚"。`6.0F` 上限 = 6 相位单位/tick,约 0.69 格/tick 以上才吃,顺带截住"仆从瞬移到主人身边"那一下的相位暴涨。
**同族一直没动:** `MutantHoglinServantModel:89` 是 `getDeltaMovement().horizontalDistance() * 8.5F`(它 walk 还乘 1.2、trot/charge 共用同一相位,要动得一起看);`MutantBlazeServantModel:148`(×15)、`RodlingServantModel:150`(×7.5)同理未查。凋零骷髅走的是 MM `addWalkModifiers` 那套(`distanceTravelled / 300.0F * walkProgressMultiplier`),本来就是位置差,与本题无关。
**脚声音色**:frozen zombie 没有 `playStepSound` 覆写(= 上游 MM 也没有 `MUTANT_FROZEN_ZOMBIE_STEP` 这个音效),走的是 `Entity.playStepSound` 的方块踩踏声 0.15 音量;jungle/husk 才覆写成 `SoundEventInit.MUTANT_*_STEP` 0.75。时值(节奏)三者一致,只是音色不同,别当移植漏抄去"修"。

相关:[[mm-zombie-sinewave-walk-jitter-fix]]、[[mc-client-deltamovement-motion-packets]]、[[mm-walkmodifiers-framerate-phase]]、[[mutant-hoglin-anim-fix]]
