---
name: patchouli-locale-fallback-en-us
description: Patchouli 的类目/条目 id 只由 en_us 决定，其余语言逐文件回退 en_us，所以缺翻译不掉条目、多出文件不生效
metadata: 
  node_type: memory
  type: reference
  originSessionId: 42520be8-9208-491c-85ed-016403ffbce1
  modified: 2026-10-15T14:38:27.403Z
---

Patchouli 1.20.1（`libs/Patchouli-1.20.1-85-FORGE.jar`，CFR 反编译见 `.scratch/patchouli/`）的语言解析是**两段式**：

1. `BookContentsBuilder.load()` 先调 `contentLoader.findFiles(book, "categories"|"entries", list)` —— `BookContentResourceListenerLoader.findFiles` 里有一句 `!"en_us".equals(matcher.group("lang")) → continue`。**id 集合只由 en_us 文件决定**，返回的 id 是不带语言的 `namespace:path`。
2. 对每个 id，`loadLocalizedJson` 拿 `String.format(".../%s/%s.json", book.id, DEFAULT_LANG=en_us, thing, id)` 再 `replaceAll("en_us", ClientBookRegistry.currentLang)` 加载；该文件为 null 时**回退加载 en_us 原文件**，两者都没有才抛 `" does not exist."`。

**两条推论**：
- 其他语言**缺**文件不掉内容：只要 en_us 有该类目/条目，玩家切到别的语言也能看到（显示英文原文）。所以给 pt_br 条目改 `"category": "goety:ominous_xxx"` 而 pt_br 下没有对应类目文件是**安全的**，只是类目名显示英文。
- 其他语言**多**文件等于没写：en_us 里没有的类目/条目永远不会被加载 —— 新增类目/条目**必须同时放进 en_us**，只放 zh_cn 不生效。

**Why:** 做手册多语言时最容易把这两条搞反 —— 要么以为缺翻译会掉条目而白写一堆语言副本，要么以为只写中文就能上线。
**How to apply:** 校验「条目引用的类目是否存在」时，只查 en_us 那份类目目录即可（`findFiles` 的视野）；反过来查「某语言有没有漏翻译」才需要逐语言比对。related: [[patchouli-category-lock-is-derived]]、[[patchouli-flag-conditional-content]]
