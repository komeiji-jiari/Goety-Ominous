---
name: ac-servant-parity-keep-original
description: 移植准则——游戏表现(特效/伤害/机制)严格对齐原版,实现层与 Goety 冲突时 Goety 优先;别自作主张改触发条件或加默认空闲行为
metadata:
  node_type: memory
  type: feedback
  originSessionId: 0b71a688-4cc5-4580-9f79-8430a28f2e3d
  modified: 2026-09-28T19:53:50.490Z
---

## 基准：逐行对齐原版

遗迹恐手龙仆从（RelicheirusServant）的砍树曾被改成"没目标也砍"：`canUse` 多了 `|| getTarget() == null`、没喂汤时用 `MoveToBlockGoal` 默认冷却（100~200t）。用户 2026-09-21 要求改回原版语义——只有喂过原始汤（`getPushingTreesFor() > 0`）才砍，`nextStartTick` 恒为 `reducedTickDelay(10 + rand(20))`。

**Why:** 用户把移植来的仆从视为原版生物的移植，行为差异算 bug 不算特性（此前发现 `TremorzillaServant` 丢 `screenShakeAmount`、`Licowitch` 丢 `random.nextBoolean()` 也是同类问题）。

**How to apply:** 逐行对照反编译源码；原版写死的数值/触发条件不要用属性值或"更友好的默认值"替换；确有必要偏离时先问。

## 边界（用户 2026-09-26 明确划分）

> "偏离 lm 吧 我只要技能的特效 伤害 机制等和 lm 一样 做的是 goety 的 servant，如果 lm 原版会干涉 goety 的机制 那就 goety 优先"

拆开是两条不同的尺子：

- **游戏表现层**（招式特效、命中判定、伤害数值、AI 决策、状态机时序）→ **必须和 LM 原版一致**，这里不许"顺手优化"。
- **实现/接线层**（基类、配置归属、NBT、血量模型、事件接法）→ **Goety 优先**。LM 原版的实现方式若会和 Goety 的机制打架（共用配置键、虚拟血量、boss 专属机制），就换成 Goety 侧的做法，别为了 parity 硬扛。

判据：改动会不会让**玩家看到的**数字/时机变？会 → 别动；只是同一效果的另一种实现 → 按 Goety 来，并在回复里说明这是主动偏离。

## 别给 AC 仆从加 Vizier 式限伤（2026-09-28）

三个 AC 蜥脚类仆从（撼地斯拉 TremorzillaServant / 暝煌龙 LuxtructosaurusServant / 擎天龙 AtlatitanServant）曾被加上"照抄 Goety Vizier"的 `actuallyHurt` 限伤（`Math.min(amount, *DamageCap)`，默认 25/25/30 + 对应配置键）。用户发现后要求全部删除——AC 原版这三个实体**没有任何限伤**，属于违反上面"游戏表现层必须对齐原版"的自加改动。已连配置键一起清掉。

**Why:** 限伤正是"玩家看得到的数字"，属于表现层，不许按实现层理由（"仿 Vizier"）自作主张加。

同日还删掉 `AtlatitanServant.setCustomAttributes()` 里的 `Attributes.ARMOR` 与配套配置键（原为仆从自加的 10 点护甲，AC 的 `AtlatitanEntity.createAttributes()` 只有 速0.325/血400/膝抗1.0/伤害8，无护甲无 follow range）。**注意**：这种情况要**删键**而不是"把默认值改成 0"——`forge-config-defaults-dont-reapply`，实例里已存在的 toml 会保留 10.0。

**How to apply:** 往 `ally/ac/` 下移植实体时，别顺手补限伤/减伤/免疫/护甲之类的"boss 化"待遇；要加先问。属性一律对着 AC 的 `createAttributes()` / `m_22268_` 链子核。

**未决：** 仆从的 follow/target range 比 AC 小（撼地斯拉 follow 32 vs AC 128；暝煌龙有独立的 `targetRange` 32，AC 是 `MobTarget3DGoal(Player.class)` 走 follow 256 的 3D 盒）。已告知用户，等他定要不要按 AC 调。

相关联 [[ac-goety-port-probability-gates]]、[[lm-servant-damage-config]]、[[no-unrequested-recipes-patchouli]]、[[goetyominous-deadcode-cleanup-preference]]。
