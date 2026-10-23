---
name: github-history-libs-jars-kept
description: "origin 历史里压着 392MB 前置模组 jar(commit 10a087eb),2026-10-06 用户选择不清历史"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0867ceb4-db38-4b2e-a60c-a858974b6a50
  modified: 2026-10-06T16:30:35.915Z
---

`D:\ark\java` 的远端 `origin`(github.com:komeiji-jiari/Goety-Ominous) 历史里有 **392 MB 前置模组 jar**:

- 提交 `10a087eb`（2026-07-27「重命名模组ID: someillagerservants → goetyominus」）一次性提交 29 个 jar 到 `libs/`（411,182,395 字节）。`2b06d517`「从仓库移除libs/前置模组jar文件（本地保留）」只把它们从**工作树**删掉，历史里仍在。
- 该提交被 **三条远端分支同时可达**：`origin/master`、`origin/rlyeh-modernui-fix`、`origin/backup-pre-goetyominous-rename`。GitHub 上按 commit SHA 即可逐个下载。
- 2026-10-06 用户明确选择「**A. 不动历史**」：只提交并推送文件树层面的删除（`fda5fad5` 移除黑暗金属长矛 dark_spear + spear_compat），不做 `filter-repo` 强推。要重提清理需先问——强推会让所有 SHA 变化，协作者 LunaRing / TenkenAmainu / cakeskull / kloati 都得重新 clone。
- `libs/` 现已被 `.gitignore` 第 52 行整体忽略（本地 39 个 jar 不再进版本库）。**前端模组 jar 从此不会进远端**，所以同样的审计别重复做：`git log --all --diff-filter=A --name-only -- libs/` 一次就能看清。

查证配方（此仓库每次都要用）：`git grep -i -n <关键词> origin/master -- .` 看远端树；`git log --all --name-only --pretty=format:` 看历史；`git ls-tree -r origin/master` 看顶层，注意 `origin/backup-pre-goetyominous-rename` 这种备份分支也带内容。相关：[[forge-compile-setup]]、[[github-ssh-setup]]
