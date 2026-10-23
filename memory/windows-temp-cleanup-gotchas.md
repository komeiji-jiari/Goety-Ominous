---
name: windows-temp-cleanup-gotchas
description: 清 %TEMP% 的两个坑——会删掉 Claude Code 自己的输出文件、以及用户解包 jar 的工作目录
metadata: 
  node_type: memory
  type: project
  originSessionId: 88da6943-651d-4a69-8564-827dcc7a6ca7
  modified: 2026-09-30T20:21:04.034Z
---

清 `%TEMP%` 时有两个坑，2026-09-30 清理时都踩到过：

**坑一：Claude Code 的任务输出就存在 `%TEMP%\claude\<project>\<session>\tasks\*.output`。** 把 Temp 内容整个删掉会连它一起删，工具回显随即报 `ENOENT ... could not be read`，命令其实跑成功了但看不到输出。所以清理 Temp 时要排除 `claude*` 目录（harness 会重建该目录，无实际损害）。

**坑二：`%TEMP%` 下有用户自己的解包工作目录，不是垃圾。** 包括 `ac_jdk`、`jdk`（解包的 JDK）、`goety_jar`、`ac_check`、`ac_jar`、`acjar`、`lmjar`（解包的 mod jar），合计约 1.2 GB。是移植 AC / LM / Goety 时逐个解包出来的对照物。仓库里没有任何脚本引用它们（grep `acjar|ac_jdk|lmjar|goety_jar` 无匹配），属于 ad-hoc 产物——可重建，但重新解包要花时间。2026-09-30 那次清理**特意全部保留**了，以后别再当垃圾扫掉。

**How to apply:** 清 Temp 的正确做法是 `Get-ChildItem $env:TEMP -Force | Where-Object { $_.Name -notin $keep } | Remove-Item -Recurse -Force`，`$keep` 至少含上述 7 个工作目录名。另：`C:\$SysReset`（Windows 重置失败残留）里 `Scratch\winpeshl.exe` 是删不掉的 WinPE 残留，165 MB 清完会剩 0.1 MB，属正常。相关：[[windows-admin-token-and-protected-paths]]、[[windows-debloat-avast-xbox]]。
