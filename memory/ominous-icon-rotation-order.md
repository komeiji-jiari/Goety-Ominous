---
name: ominous-icon-rotation-order
description: 凶兆按钮的轮转图标与书页联动按钮是两条独立通道，用户要求顺序一致
metadata: 
  node_type: memory
  type: project
  originSessionId: e4b76898-3af6-4684-8f93-5bc80137cb77
  modified: 2026-10-07T12:16:17.000Z
---

「诡厄巫法：凶兆」那一页有两套互不相干的联动顺序：

- **书页按钮**：顺序来自 `resourcepacks/<pack>/.../categories/*.json` 的 `sortnum`（页面只显示资源包已启用的项，按 sortnum 排；locked 会排到最后）。
- **按钮上的轮转图标**：来自 `client/OminousIconRotation.java` —— 给 `goetyominous:ominous_icon` 注册 `goetyominous:ominous_icon_index` 物品谓词，20 tick 换一张，`models/item/ominous_icon.json` 的 override 值 0..6 一一对应贴图。原来只看"联动模组装了没"，与页面无关。

2026-10-05 用户要求两者顺序一致，改成运行时从 Patchouli 已加载的书里读顺序：`compat/patchouli/OminousRotationBookOrder`（BookRegistry → `goety:black_book` → 根分类 `goetyominous` 的子分类，照 `GuiBookCategory.addSubcategoryButtons` 同款 `removeIf(parent/shouldHide)` + `Collections.sort` 排序），再按模组是否安装过滤（贴图来自各联动模组，缺席会紫黑）。模型 override 值已按页面顺序重编：sar 0 / ua 1 / mm 2 / ias 3 / am 4 / ac 5 / of 6。

**Why:** 联动资源包可以逐个启停，只按"模组装了没"排会和页面错位（用户实例里 `am_compat` 没启用，Alex生物图标却在轮转）。

**How to apply:** 以后加联动项要同步改三处：分类 JSON 的 sortnum、`OminousIconRotation.ENTRIES`、`ominous_icon.json` 的 override 值。相关：[[no-unrequested-recipes-patchouli]]

**改动边界（2026-10-07 重构 mm/am 手册分类时确认）：** `OminousRotationBookOrder.build()` 只取根分类 `goetyominous` 的**直接子类目**（`getParentCategory() != root` 的会被 removeIf 掉），所以往 `<x>compat` 底下加几层子类目都不会影响轮转——但**根类目 id 与 `parent: goety:goetyominous` 的直挂关系不能动**（`OminousIconRotation.indexOf` 按 id path 精确匹配）。
