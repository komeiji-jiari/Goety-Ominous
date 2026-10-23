---
name: patchouli-icon-and-ritual-ingredient-nbt
description: Patchouli 条目 icon 字符串支持 NBT 标签；Goety 仪式配方 activation_item 同样支持 forge:nbt
metadata: 
  node_type: memory
  type: reference
  originSessionId: e4b76898-3af6-4684-8f93-5bc80137cb77
  modified: 2026-10-05T19:25:18.383Z
---

两条已反编译确认的写法（1.20.1）：

- **Patchouli 条目 `"icon"` 带 NBT 是合法的**：`BookIcon.from(String)` → `ItemStackUtil.parseItemStackString`，它会以第一个 `{` 切成「id + 计数(#n)」与「NBT 串」，NBT 走 `TagParser.parseTag` 后 `stack.setTag`。所以药水/旗帜这类无独立 id 的物品可以直接写成 `"icon": "minecraft:potion{Potion:\"upgrade_aquatic:insomnia\"}"`（JSON 里双引号要转义；解析器也接受单引号写法）。
- **Goety `RitualRecipe` 的 `activation_item` 与 `ingredients` 走同一条码路**（都是 `Ingredient.fromJson`），因此激活物也能用 Forge 的 `{"type": "forge:nbt", "item": "minecraft:potion", "nbt": "{\"Potion\": \"...\"}"}`。

**Why:** 像「失眠药水」在 UA 里根本没有独立物品 id（lang 只有 `item.minecraft.potion.effect.insomnia`），只能靠 NBT 区分；不知道这两点就会以为「换个带 NBT 的图标/材料」做不到。

**How to apply:** 改联动包条目图标就照上面写；改仪式材料用 `forge:nbt`。工程里 `convert_pig_to_piglin`、`overgrown_colossus_servant` 是现成样板。相关：[[patchouli-entry-sort-read-state-first]]
