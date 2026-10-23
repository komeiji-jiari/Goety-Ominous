---
name: goety-move-speed-two-channels
description: Goety 仆从移速=修正值×MOVEMENT_SPEED（乘积<1 时还要平方），战斗/跟随/命令/游荡是四条独立通道，游荡漏调会让低属性仆从走路变小碎步
metadata: 
  node_type: memory
  type: reference
  originSessionId: 01ffd930-d6cf-4f1b-99cf-31b05089c667
  modified: 2026-09-26T20:51:06.083Z
---

名义移速 = **速度修正值 × MOVEMENT_SPEED 属性**（`MoveControl.m_8126_()` 里就是 `f_24978_(speedModifier) * m_21133_(Attributes.MOVEMENT_SPEED)`），但**乘积 < 1.0 时实际位移是它的平方**（见下）。而"战斗追击""跟随主人""命令""游荡"走的是互不相干的修正值通道：

- 战斗追击：`IMoveGoal(entity, flag, speed)` 里写死的 speed，由各实体自己注册。
- 跟随主人：Goety `Summoned.followGoal()` → `IServant.getFollowSpeed()`，**默认 1.0**。
- 命令模式：`IServant.getCommandSpeed()`，默认 1.25。
- **游荡：`Summoned.WanderGoal(entity, speedModifier)`，Goety 惯例写死 1.0** —— 第三条容易被漏的通道。

**平方关系的推导（2026-09-25 查实）：** `Mob.m_21566_(setSpeed)` 除了写 `speed` 还会 `setZza(speed)`，而 `travel` → `moveRelative(getFrictionInfluencedSpeed(), new Vec3(xxa,0,zza))` 里 `Entity.getInputVector` 只在 `|input| > 1` 时才归一化。所以修正值×属性 = s 时，每 tick 冲量 = `s × min(1, s)`，稳态速度 ≈ 1.5×冲量：

- s < 1：**速度 ∝ s²**（僵尸 s=0.23 → 0.079 格/tick，对得上）
- s ≥ 1：速度 ∝ s

后果就是**低属性实体（0.1）配 1.0 修正值会被平方惩罚**：0.1 → 0.015 格/tick（0.3 格/秒，爬），而 3.0 → 0.135 格/tick。属性 0.25 的普通仆从配 1.0 是 0.094（1.9 格/秒，正常）——所以仓库里满地的 `WanderGoal(this, 1.0D)` 只坑属性 0.1 的那批。

所以从 LM/AC 移植巨型实体时，如果按原版把 `IMoveGoal` 的速度调高了、却没同步覆写 `getFollowSpeed()`，跟随速度就会比战斗速度慢好几倍——**属性值本身没问题，别去改 `AttributesConfig.*MovementSpeed`**。修正值才是落差点。

**实例：** 云铸魔像 `IMoveGoal(..., 3.0D)` + 属性 0.1 → 战斗 0.3、跟随只有 0.1（慢 3 倍），原版 boss 只用得到战斗那条通道所以不显慢。2026-09-24 覆写 `getFollowSpeed() → 3.0D` 修复。

**参考值：** Goety 自家重装仆从（`ImperialGuardServant` / `RoyalGuardServant` / `VanguardServant`）统一 `getFollowSpeed() → 1.25`。

**仓库里两种自洽写法**（对照 LM 原版实测值，三者原版都是 `IMoveGoal 3.0` + 属性 `0.1` = 有效移速 0.3）：

- **保持原版常数**：`IMoveGoal` 3.0 + 属性 0.1 + 覆写 `getFollowSpeed/getCommandSpeed → 3.0`。`CloudGolemServant`、`HoveringHurricaneServant` 走这条。好处是修正值×属性，属性随便玩家怎么调两者都同步。
- **化简有效移速**：`IMoveGoal` 1.0 + 属性 0.3，跟随用默认 1.0 就自然等于战斗速度，**一个方法都不用加**。`OvergrownColossusServant` 走这条，所以它从没出过问题。

**2026-09-24 修复：** 云铸魔像补 `getFollowSpeed()`/`getCommandSpeed() → 3.0D`。`HoveringHurricaneServant` 除缺通道外，属性 config 还误配成 0.3（原版 0.1）导致战斗移速 `3.0×0.3=0.9` 快 3 倍、把跟随问题掩盖了——已一并补两个覆写 + 把 config 默认值改回 0.1（该 config 条目当时尚未提交，无迁移问题）。

**2026-09-26 湮灭构造体左右晃：** 它属性 0.36 + `IMoveGoal 3.0` → s = 1.08 **越过 1.0 进了线性区**，实际 1.62 格/tick（32 格/秒），比 LM 原版（0.12×3.0 = 0.36 → 0.194 格/tick ≈ 3.9 格/秒）快 8 倍；一个 tick 跨过 1.6 个路径点，下一个路径点永远落在身后 → `MoveControl` 每 tick 把 yaw 翻 180° = 左右晃。顺带把 `ServantMoveControl` 的转向限速打穿（`limit = degrees(速度/半径)` = 106° → 被 `min(maxChange=90)` 吃掉 = 等于没限速），而其他 LM 仆从 s=0.3 时限到 ~24°/tick 所以不晃。修法按下面的**化简式**：`IMoveGoal 3.0D → 1.0D`。**s ≥ 1 是重灾区**，比 s<1 被平方惩罚更隐蔽。

**2026-09-25 补第三通道：** 用户报"LM 仆从没敌人走路时小碎步"。根因是 `WanderGoal(this, 1.0D)` 对属性 0.1 的四个 LM 仆从（湮灭追猎者/云铸魔像/飓旋/潜影拟态者）只给出 0.015 格/tick，而跟随/战斗是 3.0 → 0.135 格/tick（9 倍差）。**而且 `animateWalk` 的摆幅和相位都锁在真实位移上**（`limbSwingAmount = min(4×每tick位移, 1)`，相位每 tick 加 `limbSwingAmount`），所以走得越慢腿摆幅越小、周期越慢 —— 摆幅掉到 15% 就正是"小碎步"的观感，改动画参数治不了，得改移速。统一改成 `new Summoned.WanderGoal<>(this, this.getFollowSpeed())`：LM 五个都换，属性 0.3 的巨像取到默认 1.0 等于没变。

**排查手法：** 想知道某个 LM/AC 仆从移速对不对，翻 `.scratch/lm_*_out/` 里的反编译原版实体，比 `IMoveGoal` 的 speed 参数和 `createAttributes` 里的 `Attributes.f_22279_`（MOVEMENT_SPEED）。相关联 [[goety-servant-follow-standby]]、[[ac-servant-parity-keep-original]]。
