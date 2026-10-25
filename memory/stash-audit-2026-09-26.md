---
name: stash-audit-2026-09-26
description: "2026-09-26 核对 6 个 stash：全部无丢失内容，均可安全 drop；stash@{0} 已作为 74ffd75b 提交推送"
metadata: 
  node_type: memory
  type: project
  originSessionId: 304245bf-c7a1-4c6f-8c54-9cc55d2f31af
---

2026-09-26 逐文件核对结论：**仓库里 6 个 stash 都不含未提交的丢失内容，可以安全 drop。**

- `stash@{0}`「圣骑回退前的WIP」：已恢复并作为 `74ffd75b` 提交、推送（HEAD == origin/master）。pop 冲突留在列表里没自动删，是残留。
- `stash@{1}`…`stash@{5}`「本地工作」（2026-08-21 ~ 2026-09-14）：逐文件比对后确认无丢失。

核对时看到"内容不在 HEAD"的三大类，全都是**误报**，下次别再重复排查：

1. **配方/手册迁到了两层 compat 结构** —— 旧路径 `data/goetyominous/recipes/xxx.json` 在 HEAD 里是 `resourcepacks/<abbr>_compat/data/goetyominous/recipes/xxx.json`（如 centipede/voidshot→`am_compat`，compound_z/formula_y→`mm_compat`，dropbear/guster/sand/skelewag/scute 等 UA 相关→`am_compat`）。见 [[goetyominous-compat-architecture]]。
2. **`ModAttributesFelMixin` 被取代** —— HEAD 里 `ModAttributes.java` 改用自己的 `DeferredRegister<Attribute>`（Goety 新版 `ModAttributes.ATTRIBUTES` 私有化），行为等价，是 86fa49ee 提交里写明的刻意改动。
3. **`tmp_midas_decompile/`、`错误本.md` 等 scratch** —— 反编译暂存与笔记，本就不该进版本库（stash@{4} 里 379 个"缺失"全是这个）。

**Why:** 用 `git diff --stat stash@{N} HEAD` 判断"备份有没有提交"会严重误报——这些 stash 的父提交都只是 HEAD 的祖先，HEAD 往前跑了 4 万+ 行，diff 必然巨大。正确做法是逐文件比对"stash 新增的行是否已存在于 HEAD"。

**How to apply:** 再要判断某个 stash 是否已合并，用逐文件行包含比对，不要看整体 diff；并且先按上面三类排除误报，再下"内容丢失"的结论。
