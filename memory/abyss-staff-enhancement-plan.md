---
name: abyss-staff-enhancement-plan
description: KeyOfRlyeh（2级深渊杖）对 13 个 abyss 系法术的加强清单与各自的具体数值要求
metadata: 
  node_type: memory
  type: project
  originSessionId: 636e9543-5e18-4936-a377-d96b89787536
---

# Abyss Staff 法术加强计划（用户 2026-09-26 提出）

**目标**：仿照 goety2 `AbyssStaff` 对 abyss 系法术的强化机制，让自研的 2 级深渊杖 `key_of_rlyeh`（见 [[key-of-rlyeh-staff]]）对一批 abyss 法术提供比原版 abyssstaff **更强**的加强。

**关键前提**：加强要放在"**已判定 rightStaff 之后**的路径上，而不是修改 `rightStaff()` 判定本身。原版 abyssstaff 的加强走 `rightStaff(staff)` 分支，我们要在同一个分支里做更进一步的强化（数量 +1、伤害 +N、等级 +1 之类）。**注意区分"在 rightStaff 基础上再加"和"不在基础上叠加"** —— 每条需求都写明了。

## 13 条需求（原样记录，勿改数值）

召唤量类（原版 abyssstaff 让**直接召唤**类法术变 2 个，我们要 **3 个**）：

| # | 法术类名 | 召唤物 | 要求 |
|---|---|---|---|
| 1 | `RottenSpell` | 溺尸仆从（DrownedServant 之类） | 直接召唤，数量 2 → **3** |
| 2 | `SkeletonSpell` | SunkenSkeletonServant | 直接召唤，数量 2 → **3** |
| 3 | `HuntingSpell` | Snapper | 直接召唤，数量 2 → **3** |
| 4 | `MaulingSpell` | Gnasher | 直接召唤，数量 2 → **3** |
| 5 | `SlimySpell` | TropicalSlime 仆从 | 直接召唤，数量 2 → **3** |
| 6 | `GuardianSpell` | （守护者仆从） | 数量 2 → **3** |

其余类：

| # | 法术类名 | 要求 |
|---|---|---|
| 7 | `BioMineSpell` | 单次施法召唤的生物质炸弹 **+8**（abyssstaff 是 +4；**不是在 rightStaff 基础上再 +8**） |
| 8 | `PrismaBeamSpell` | 基础伤害 **+4**（**不叠加 rightStaff**，否则会变成 +6） |
| 9 | `SteamingSpell` | 仿 rightStaff 写法：单轮内**依次连发 4 次**（原 1 次），每次连发 **8 颗**蒸汽泵弹，合计 32 颗 |
| 10 | `TidalSpell` | 单轮内召唤 **5 道**潮汐（分布参考 rightStaff 写法；原版 TidalSpell 的 rightStaff 是 `h = rightStaff ? 1 : 0`，即 1 或 3 道） |
| 11 | `BouncyBubbleSpell` | 单轮内召唤 **3 个**水溅泡（原版 rightStaff 是 2 个，我们再加 1） |
| 12 | `TridentStormSpell` | 三叉戟阵列**数量与 rightStaff 一致不变**，只加伤害：每根三叉戟伤害 **+3** |
| 13 | `WaterWhipSpell` | 参考 rightStaff 生成 2 个水鞭，我们额外再生成 1 个 → **连续 3 个**，且**不要全叠在一起**（看 rightStaff 怎么写的，它是连续的） |
| 14 | `WaterJetSpell` | 喷射水柱对命中目标施加 `goety:sapped` **1 级**、5 秒（原 rightStaff 逻辑为 0 级） |

（实际是 14 条，上表编号 1-14）

## 类名待核实

用户给的是简化名（如 `rottenspell`），Goety 实际类名应该是 `RottenSpell` 这种驼峰。
**动工前先用 `.disasm/goety_src/` 核实每个类名与所在包**，不要凭猜。

## 推荐做法

1. 先读原版对应法术的 `SpellResult`，定位 `rightStaff(staff)` 分支
2. 在该分支内做增量（数量 +1 / 伤害 +N / 循环次数 +N）
3. 不要改动 `rightStaff()` 覆写本身

## 相关

- [[key-of-rlyeh-staff]] —— 这把杖本身的实现（含 revelation 条件注册的坑）
- [[goety2-spell-system]] —— ISpell/Spell 基座契约
- [[goety2-src-decompile]] —— 反编译源码位置
