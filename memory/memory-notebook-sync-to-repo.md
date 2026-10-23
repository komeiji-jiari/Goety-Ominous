---
name: memory-notebook-sync-to-repo
description: 用户的「错题本」=Claude memory 目录，要求同步到仓库 memory/ 并推送 GitHub
metadata: 
  node_type: memory
  type: project
  originSessionId: 6e16a120-e5a0-4c58-ad22-7c270511b03d
  modified: 2026-10-23T00:58:53.488Z
---

用户把 Claude 的 memory 目录称作「错题本」。2026-10-23 首次要求把它随代码一起推送：整份复制到 `D:\ark\java\memory\`，作为独立提交推到 `komeiji-jiari/Goety-Ominous` 的 master（用户选择放在公开模组仓库的 `memory/` 目录，未建独立仓库）。

**Why:** 用户要用 GitHub 给开发笔记做备份/多机同步，不希望只存本地 `.claude` 里。

**How to apply:** 用户要求推送工作区改动时，顺带把 memory 目录同步到仓库 `memory/`；记忆有新增/修改后，重新复制并单独提交（提交信息风格「错题本：…」），再 push。与 [[no-auto-build-jar]] 一致——只在用户明确说推送时才做。
