---
name: lm-servant-standby-sleep-timing
description: LM 仆从待命沉睡的时序口径——等待计时器算延迟(bug)，趴下过渡要保留/要加
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 203a69d5-88f9-4972-90f9-9c9625b2bf92
  modified: 2026-09-25T13:58:06.501Z
---

待命沉睡有两段独立的东西，用户口径相反（2026-09-25 澄清）：

1. **进入待命的等待计数 = 延迟，算 bug。** 云筑魔像的 `SLEEP_STANDBY_DELAY = 20`（条件满足后还要等 1 秒才 `setSleep(true)`）被报成"待命沉睡动画有延迟"，用户原话："我说的'延迟'指的是按下待命后要等一秒才开始睡"。两个仆从现在都是条件满足当 tick 就 `setSleep(true)`，`SLEEP_STANDBY_DELAY`/`standbyTicks` 已删干净（云筑魔像 `CloudGolemServant`、湮灭猎影 `AnnihilationPursuerServant`）。
2. **趴下过渡 = 想要的。** 用户随后主动要求"给湮灭猎影加沉睡动画"并选了"要一段慢慢趴下的过渡"，所以 LM 里没有入睡过渡时，自己造一段是合理的（云筑魔像那段被我一并删掉是过度删除）。

**Why:** LM 的 `*Animations.sleep` 是姿势瞬间到位的（云傀儡 `withLength(0)` 静态姿势；湮灭猎影 `withLength(3.0).looping()` 只有呼吸起伏），`awake`/`awaken` 才是唯一有过渡的那条。两者姿势对得上：`sleep` 的 t=0 与 `awake`/`awaken` 的 t=0 逐通道数值相等（两个仆从都核对过），所以**过渡 = 反演 `awake`/`awaken`**，首尾严丝合缝。

**原版的沉睡是死状态（2026-09-25 全 jar 核对）：** LM 里凡是带 sleep 的 boss（`Cloud_GolemEntity`、`Overgrown_colossusEntity`、`AnnihilationPursuerEntity`）`setSleep(` **只有 `readAdditionalSaveData` 一处调用**，没有任何 AI/结构/事件把 attackState 写成睡值；引用这些实体类的类也只有模型/渲染器/弹射物/注册表。所以原版游戏里它们永不入睡，只能靠存档 NBT 或命令进入——仓库里的"待命就睡"触发全是自加的。**蔓生巨像**（`OvergrownColossusServant`）移植时整套没搬：无 `sleepAnimationState`/`awakeAnimationState`、`getAnimationState` 无 "sleep"/"awake" 分支、`onSyncedDataUpdated` switch 无 case 1/2（原版 1=睡 2=醒）、`OvergrownColossusServantModel` 没接那两条通道。它的动画定义在 LM 的 `OCAnims`（仓库直接 import LM 这个类），形态与云傀儡同构：`SLEEP` = `withLength(0)` 静态姿势（body rot 25°、双臂 -27.5°、头 pos z+4/rot 15°、左腿 z-7、右腿 z+6/rot 47.5°），`AWAKE` = `withLength(1.0)`、t=0 与 SLEEP 逐通道相等、0.75 全零 → 反演即得 0.75s 趴下过渡。

**How to apply:** 过渡用 `reverse(animation)`（按 `lastTimestamp` 逐通道重排成 `end - t`，保留 interpolation），播完停在睡姿；醒来必须按"已经趴了多久"反推续播点（`skipped = max(0, SLEEP_IN_MILLIS - sleptMillis)`，`AnimationState.stop()` 保留 accumulatedTime 才读得到；半途被叫醒不复位会跳）。状态长度要和动画对齐：`AWAKE_TICKS` 云傀儡 20(=1.0s)、湮灭猎影 30(=1.5s，LM 原值)。两个模型（`AnnihilationPursuerServantModel.animateSleep/animateAwaken`、`CloudGolemServantModel` 同名方法）都是"过渡段播完切回 LM 真动画"的单状态时间偏移写法。查 LM 动画长度/循环性时读 `.scratch/lm_*/.../*Animations.java`（`Builder.m_232275_(len)`、`m_232280_()`=looping、`f_232250_`=POSITION/`f_232251_`=ROTATION/`f_232252_`=SCALE），别猜。相关联 [[lm-servant-boss-mechanic-stripping]]、[[ac-servant-parity-keep-original]]。
