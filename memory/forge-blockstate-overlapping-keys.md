---
name: forge-blockstate-overlapping-keys
description: "Forge 1.20.1 blockstate 不能混用部分键和完整键,否则 Overlapping definition 异常导致整个 variant 被拒、对应状态渲染紫黑块"
metadata: 
  node_type: memory
  type: project
  originSessionId: 9c31ffc1-768a-420a-b4b8-11868e0bed53
  modified: 2026-08-29T17:11:24.190Z
---

**症状**:方块渲染成紫黑块(missing model),但资源文件齐全、latest.log 无报错。

**根因**(1.20.1 ModelBakery 反汇编确认):blockstate JSON 里若同时存在部分键 `"hatch=0"` 和完整键 `"hatch=0,needs_player=true"`:
1. 完整键先处理,抢占状态 (hatch=0,needs_player=true)。
2. 处理部分键 `"hatch=0"` 时命中已被占状态 → 抛 `Overlapping definition with: ...` 异常,整个 variant **被放弃**,该状态写回 missing model。
3. 默认状态 `hatch=0,needs_player=false`(BooleanProperty 迭代 true 在前,部分键碰到的第一个状态就是已被占的 (0,true))→ 永远没被处理 → missing model → **每个新放置的方块都紫黑**。

**Why**:ModelBakery 遍历 blockstate 文件里的每个 variant key,用谓词(仅检查 key 中指定的属性)过滤 possibleStates 分配模型;重叠(两个 key 匹配同一状态)会抛异常并整体拒绝后处理的 key。这是"每个变体匹配状态"方向,部分键天然成立——但**不允许部分键与更具体的键重叠**。

**How to apply**:blockstate 只写部分键(`"hatch=0"` 即可覆盖 needs_player=false/true 全部组合),**不要**再叠加 `"hatch=X,needs_player=true"` 之类完整键。AC 原版 tremorsaurus/grottoceratops 蛋就只写 `hatch=0/1/2`。涉及块:4 个恐龙仆从蛋 blockstate(grottoceratops/tremorsaurus/tremorzilla/vallumraptor_servant_egg.json)。

**排错提示**:此类 WARN 只在 `logs/debug.log` 里(`Exception loading blockstate definition: ... for variant: '...': Overlapping definition with: ...`),`latest.log` 是过滤版会漏掉。相关类:[[forge-compile-setup]]。
