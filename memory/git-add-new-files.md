---
name: git-add-new-files
description: 新建/搬运的文件（json、贴图、模型、语言、代码）落盘后必须立即 git add——用户是 IDE 工作流，我建的文件不会被自动暂存
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 7533bf00-ff71-4c78-8f0b-44374dd78647
---

用户要求：**以后新增的 json 或者搬运过来的资源必须 add 上去**。

**Why:** 本仓库是用户用 IDE 暂存自己的改动；我用 Write 新建的文件（战利品表、tag、模型、贴图、语言、Java…）不会被自动 add，会一直躺在 git status 里显示为未跟踪，提交时容易漏掉、甚至丢失。2026-10-07 宝库批次就漏了 `OminousVaultBlockItem.java` 和 `ominous_vault.json` 两件，被用户当场抓到。

**How to apply:** 每轮用 Write 建了新文件、或从 1.21.1 jar 里 unzip 提取了资源（贴图/音效 ogg 等）之后，立刻对这批新文件 `git add <路径...>`（可按目录整批 add）；随后复查 `git status --porcelain | grep '^??'` 为空。对既有文件的修改不用我管（用户自己暂存），**commit 仍要等用户明说**。与 [[no-edit-without-consent]] 配套：先拿到 edit 授权才建文件，建完必须 add。
