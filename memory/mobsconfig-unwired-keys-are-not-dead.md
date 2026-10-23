---
name: mobsconfig-unwired-keys-are-not-dead
description: "MobsConfig 里声明了但全工程无引用的键不是死键，是预留坑位；删了用户会后悔——删配置键前必须先问"
metadata:
  type: feedback
  originSessionId: current
  modified: 2026-10-05T11:27:38.854Z
---

2026-10-04 的提交 `7abce2fd` 以"从声明到删除都没有任何引用（全工程 grep 无命中）"为由，删掉了 `MobsConfig` 里 13 组 `*ServantLimit`。2026-10-05 用户说后悔了，要求恢复并接线（提交 `98e97e09`）。

**Why:** 判断依据本身没错——这些键确实没有任何代码读——但**结论错了**。它们不是死键，是"声明了、生成了 config、还没接线"的坑位：写的人（之前的我）先把键和注释立好，打算之后给对应仆从类补 `getSummonLimit`。这类键在 `MobsConfig` 里存在本身就是它的价值（config 里看得见、以后接线是现成的），grep 无引用恰恰是它的**正常状态**而不是死亡证据。

**How to apply:**
- 删 `MobsConfig`/`AttributesConfig`/`SpellConfig` 里的键之前**先问用户**，别只凭 grep。要问的话顺带报清楚：这键的消费通道有哪些、现在活的是哪条。
- 区分两类"无引用"：键是**被取代**了（如 `cultistSpawnWeight/MinCount/MaxCount` 被 Beldam/Fanatic/Zealot 各自的 `Spawn*` 键取代，`ModMobSpawnBiomeModifier` 读拆开后那套）——这种是真死账，删没问题；键是**预留未接线**——这种别删。
- 与 [[goetyominous-deadcode-cleanup-preference]] 的边界：那条说的"死账全删"指**移植进来的实体内部**死代码（永不进入的攻击状态、零引用字段）。**配置键不适用**——那是用户自己立的产品面，删了等于删配置项。判定键属于哪一类看 [[goety-servant-limit-two-channels]]。

**同一个提交里删的东西别一起回退**：那次还改了 rods/breeze 标签命名空间和 `powerful_servants` 里的 cerberus id（都是对的），恢复时只挑配置键，别 `git revert` 整个提交。
