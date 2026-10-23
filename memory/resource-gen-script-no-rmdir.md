---
name: resource-gen-script-no-rmdir
description: 资源生成脚本别对 src/main/resources 下的共享目录 rmSync，会连原有内容一起删掉
metadata: 
  node_type: memory
  type: project
  originSessionId: 5850c515-ab9a-4d80-b4d6-9483a063d382
  modified: 2026-10-05T13:44:47.016Z
---

给 `src/main/resources/data/goetyominous/recipes/` 这类**共享目录**写生成脚本时，开头不要 `fs.rmSync(dir, { recursive: true, force: true })` 清空目录 —— 目录里同时装着 mod 原有内容（recipes 下有 69 个手写配方，含 `brewing/`、`cauldron/` 子目录）。2026-10-05 移植试炼密室配方时这么干，删掉 69 个原有配方，靠 `git checkout -- <dir>` 才恢复。

**Why:** 新文件名与旧文件不重名时根本不需要清理；`rmSync` 只带来风险没有收益。

**How to apply:** 直接 `mkdirSync(recursive)` 后逐个覆盖写；要保证幂等就只删「本次生成清单里」的文件。脚本跑完用 `git status --short | grep -c "^ D"` 确认没有意外删除。相关：[[no-unrequested-recipes-patchouli]]
