---
name: mc-121-1201-enchant-rename-breaks-loot-table
description: 1.21→1.20.1 附魔改名（sweeping_edge→sweeping）会让 enchant_randomly 整张战利品表报废
metadata: 
  node_type: memory
  type: reference
  originSessionId: 1b64da48-01ac-40c6-b482-0a16204710a0
  modified: 2026-10-09T20:49:10.468Z
---

`minecraft:enchant_randomly` 的 `enchantments` 列表里**只要有一个非法 id，整张战利品表报废**（`Couldn't parse element loot_tables:<id>`），箱子直接是空的——不会只跳过那一项。2026-10-09 实测：`chests/trial_chambers/{corridor,intersection_barrel}` 因为列表里混着 1.21 的名字 `minecraft:sweeping_edge`（1.20.1 叫 `minecraft:sweeping`）而整表挂掉。

**How to apply**：搬 1.21 战利品表时，1.21 用 `"options": "<附魔标签>"`（1.20.5+）而 1.20.1 只认 `"enchantments": [...]` 具体列表，所以要展开标签并**逐个核对 1.20.1 名字**；1.21 新增附魔（density/breach 等）要指向本模组自己注册的同名条目。转换脚本里的常量表（如 `tools/port_trial_chambers_nbt.py` 的 `ON_RANDOM_LOOT`）和产物 JSON 必须一起改，否则重跑脚本又写回错名字。同类"1.20.1 没有 X"：[[forge-1.20.1-no-meat-tag]]。相关：[[forge-1.20.1-missing-biome-tag-no-structure]]
