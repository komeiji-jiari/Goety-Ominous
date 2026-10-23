---
name: cataclysm-coral-golem-vs-coralssus
description: "cataclysm 里珊瑚傀儡(Coral Golem, coral_golem)和珊瑚巨像(Coralssus, coralssus)是两只不同的怪,数据/骨架/动画机制都不同;别按中文名猜"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0672aaf0-77b6-46d7-81bf-27f28135f10d
  modified: 2026-10-03T17:26:37.023Z
---

中文名只差一个字,是**两只完全不同的怪**,2026-10-03 因此差点做错方向:

| 中文 | 注册名 | 实体 / 仆从类 | 骨架 | 动画机制 |
|---|---|---|---|---|
| 珊瑚傀儡 | `coral_golem` | `Coral_Golem_Entity extends AbstractDeepling` / `goety_cataclysm` 的 `CoralGolemServant` | `root`/`lower_body`/`upper_body`/`helmet`/`chest`/`right_fist`… | 泳姿=**程序化** `flap`/`walk`;攻击=`ModelAnimator` 叠加窗口 |
| 珊瑚巨像 | `coralssus` | `Coralssus_Entity extends Internal_Animation_Monster` / `CoralssusServant` | 另有 `eye`/`body`/`arm2` 等 | `AdvancedAnimationDefinition` 关键帧(与原版 `KeyframeAnimations` 同形) |

**判据**:查 cataclysm 的 `zh_cn.json`(`entity.cataclysm.coral_golem` = 珊瑚傀儡,`entity.cataclysm.coralssus` = 珊瑚巨像)。**不要按中文名的字面感觉猜**,也不要用上一轮会话里说过的名字推断这一轮 —— 用户在潮涌之叶上先说过"珊瑚巨像"、后改口"珊瑚傀儡",这是**换目标**,不是同义表述。

反编译产物分别在 `.scratch/coral_golem_decomp/`(傀儡)与 `.scratch/coral_decomp/` / `.scratch/_cata_coral/`(巨像)。

相关:[[leapkelp-swim-anim-coralssus-port]]
