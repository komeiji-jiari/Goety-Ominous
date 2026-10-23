---
name: compat-pack-recipes-in-own-datapack
description: 联动资源包的配方在 pack 自己的 data/ 下，不在主 datapack，只查 src/main/resources/data 会误判成断链
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f2c3e4-6779-4f77-92de-1e7fb24189e9
  modified: 2026-10-08T18:13:30.569Z
---

联动资源包（`src/main/resources/resourcepacks/<mod>_compat/`）是完整的资源包+数据包，配方在 **pack 自己的** `resourcepacks/<mod>_compat/data/goetyominous/recipes/`。主 datapack 的 `src/main/resources/data/goetyominous/recipes/` 里**没有**这些配方。

**Why:** 只 grep 主 datapack 会得出「手册条目引用的仪式配方不存在、是断链」的错误结论。我这样误报过一次，被用户「继续」要求深挖才找到真位置。

**How to apply:** 核对 Patchouli 条目引用的 recipe id 是否存在时，两个位置都要查；am 的 `battle_emu` / `convert_pig_to_tusklin` 等只存在于 pack 内。判「配方是否存在」前先 `ls resourcepacks/<mod>_compat`，看到 `data/` 就说明有独立数据包。相关：[[no-unrequested-recipes-patchouli]]、[[goetyominous-mod-folder-isolation]]。
