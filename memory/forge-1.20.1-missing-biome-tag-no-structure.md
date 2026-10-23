---
name: forge-1-20-1-missing-biome-tag-no-structure
description: "结构 biomes 指向 1.20.1 原版没有的 minecraft: 标签 → 空集 → 永不生成；判据与修法"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 1b64da48-01ac-40c6-b482-0a16204710a0
  modified: 2026-10-09T20:51:49.445Z
---

1.20.1 原版**没有** `minecraft:has_structure/trial_chambers`（1.21 才加）。把 1.21 的结构 JSON 原样搬过来、`biomes` 写 `#minecraft:has_structure/trial_chambers` → 标签没人定义 → HolderSet 为空 → **任何群系都不匹配，结构连"尝试生成"都不进**。注意它**不报解析错**，只在启动时留一条：
`MappedRegistry: Not all defined tags for registry ResourceKey[minecraft:root / minecraft:worldgen/biome] are present in data pack: minecraft:has_structure/trial_chambers`

**判据（比静态检查硬）**：本模组的别名监听器在每个真正走到 `findGenerationPoint` 的结构上打一行 `findGenerationPoint start_pool=<池> → 别名 N 条`。2026-10-09 实测两次会话：39 个其它结构（deep_altar / ancient_city / trail_ruins / 5 种村庄…）全部出现，`goetyominous:trial_chambers/chamber/end` **0 次**。

**How to apply**：两条路都通——(A) 往 `data/minecraft/tags/...` 补一份同 id 的 tag（Forge 会并进原版命名空间），结构 JSON 与 1.21 原版逐字一致；(B) 结构 JSON 的 `biomes` 改指模组自己的 `#goetyominous:has_structure/<名>`，tag 文件本来就在仓库里（如 trial_chambers 的 52 条群系），净新增数据文件为 0。**2026-10-09 用户最终选 B**：与同目录其它结构（fiery_bulwark / heretic_watch_tower / goety:has_structure/blighted_shack）风格统一，且 `tools/port_trial_chambers_nbt.py` 的 `rewrite_string()` 本来就会把带 `trial_chamber` 的名字改写成 `goetyominous:`，重跑脚本自然产出 B 的形态。同类：[[forge-requirescorrecttool-needs-mineable-tag]]、[[forge-wall-block-needs-walls-tag]]。相关：[[goetyominous-test-instance-and-logs]]
