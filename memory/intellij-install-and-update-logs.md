---
name: intellij-install-and-update-logs
description: IDEA 是 zip 安装在 D:\ark\IntelliJ IDEA 2026.2.0.1;更新打补丁失败根因=更新期间又启动了一个 IDEA 实例
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8ad70a7f-a4ff-4a3c-aa58-89bf1fba91d3
  modified: 2026-09-12T00:15:01.086Z
---

IDEA 环境(排查 IDE 问题先看这里):

- 安装目录:`D:\ark\IntelliJ IDEA 2026.2.0.1`(zip 包安装,非 Toolbox/非 exe 安装器)
- 数据目录:`C:\Users\TangJinbo\AppData\Local\JetBrains\IntelliJIdea2026.2\`
- 日志:`<数据目录>\log\` 下 `idea.log`(IDE 自身)、`idea_updater.log`(更新器全量)、`idea_updater_error.log`(更新失败栈)、`restarter.log`(重启器进程编排)
- 更新载荷:`<数据目录>\tmp\patch-update\`(patch-<旧>-<新>.jar + jre)

**IDEA 更新(应用补丁)失败的根因 2026-09-12:**

`Apply Patch`/`Restart to Update` 走 restarter.exe → `com.intellij.updater.Runner batch-install`。若更新期间**又从 explorer 启动了第二个 IDEA 实例**(进程父级=explorer.exe),该实例会:
1. 锁住安装目录全部 jar/exe(updater 日志里几百条 `UPDATE ...: 被以下对象锁定: [pid] IntelliJ IDEA`,resolution 全 KILL_PROCESS);
2. 启动时跑 `UpdateCheckerProjectActivity` → `UpdateInstaller.cleanupPatch` 递归删 `tmp\patch-update\`,**把仍在运行的 updater 自己解出来的 `patch0.tmp.0` 删掉**;
3. updater 随后 `PatchFileCreator.apply` → `new ZipFile(patch0.tmp.0)` → `NoSuchFileException` → `batch failed`,exit code 1。

于是 restarter 重新拉起旧版 idea64.exe,版本不变(build.txt 仍是 IU-262.8665.337),两个 patch jar 白下载。**教训:更新时别手动启动 IDEA,让它自己重启。** 重试前先删 `tmp\patch-update\` 强制重下。zip 安装的补丁升级本就容易碎,更稳的是下整包 zip 覆盖。

相关:[[intellij-red-font-troubleshooting]] [[intellij-red-font-troubleshooting]]
