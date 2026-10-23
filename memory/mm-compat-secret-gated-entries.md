---
name: mm-compat-secret-gated-entries
description: "mm 手册有 7 条条目被 advancement+secret 藏住,是作者有意,别误判成被删"
metadata: 
  node_type: memory
  type: project
  originSessionId: df99bef6-a35f-48b9-a11e-f97df5e44588
  modified: 2026-10-13T17:24:15.542Z
---

`mm_compat` 的手册有 7 条带 `"advancement": "goety:goety/read_bygone_scroll"` + `"secret": true`，没拿到该进度前在书里完全不显示：聚晶 `rod_strike` / `wither_breath` / `wither_slash`、召唤 `mutant_blaze_servant` / `mutant_wither_skeleton_servant` / `rodling_servant`、`crafts/wither_scythe`。所以 mm 召唤页实际只看得到 突变疣猪兽(20) 和 突变潜影贝(30)。

2026-10-13 用户报「你缺了突变凋零骷髅和突变烈焰人的 patchouli book」，实为这道门控所致，文件从未被删（`git log --diff-filter=D` 干净）；查清后用户答「那没事了」，门控保留、不动。

**Why:** `secret` 隐藏条目的症状和「文件被删」一模一样，光看游戏内手册会冤枉上一次改动。
**How to apply:** 再遇到「手册缺条目」的报告，先 grep `"secret"` 再查 git 删除历史，别急着补内容。相关：[[patchouli-flag-conditional-content]]、[[goetyominous-unrequested-recipes-patchouli]]
