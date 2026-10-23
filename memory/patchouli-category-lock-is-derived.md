---
name: patchouli-category-lock-is-derived
description: Patchouli 类目没有 advancement 字段，locked 由条目推导；secret 才是隐藏开关
metadata: 
  node_type: memory
  type: reference
  originSessionId: 1c1f2d5c-15a3-48c5-aa73-e34ae2e4aa2c
  modified: 2026-10-15T12:24:03.395Z
---

Patchouli 的 `BookCategory` 构造函数只读 `name / description / icon / parent / flag / sortnum / secret` —— **没有 `advancement`**，写了也被忽略。

`locked` 是**推导**出来的（`updateLockStatus`）：类目有 entries 或 children，且它们**全部** locked 时，该类目才 locked。

- `shouldHide() = secret && locked`
- `canBeOpened() = !locked`
- `GuiBookCategory.addSubcategoryButtons` 过滤式：`removeIf(cat -> cat.getParentCategory() != this || cat.shouldHide())`
- `compareTo`：先按 locked 排（locked 沉底），再 sortnum，再名字

**推论（做「父栏目常驻 + 子栏目解锁后才出现」时的唯一写法）**：父栏目**留至少一条无门控条目**就永远可见；子栏目里全是带 `advancement` 的条目 + 子栏目自己写 `secret: true`，解锁前整栏消失、解锁后自动出现。想反过来「类目级门控」是做不到的。

`description` 和 `icon` 都是**无默认值的必填**（`GsonHelper.getAsString(root,"description")`），缺了直接崩构建。

相关：[[patchouli-flag-conditional-content]]、[[patchouli-entry-sort-read-state-first]]、[[patchouli-icon-and-ritual-ingredient-nbt]]
