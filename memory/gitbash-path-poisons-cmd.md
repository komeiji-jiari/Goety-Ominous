---
name: gitbash-path-poisons-cmd
description: "从 Git Bash 里启动 .cmd/.bat 会继承 PATH,/usr/bin 在前导致 Windows 内建命令(find 等)被 Git coreutils 顶替"
metadata: 
  node_type: memory
  type: project
  originSessionId: 8ad70a7f-a4ff-4a3c-aa58-89bf1fba91d3
  modified: 2026-09-12T00:19:07.440Z
---

本机 Windows + Git Bash 环境的一个坑,2026-09-12 踩过一次实的:

**从 Git Bash 里 `Start-Process cmd.exe /c xxx.cmd` 或直接 `cmd.exe /c xxx.cmd`,子 cmd 继承的是 Git Bash 的 PATH,`/usr/bin` 排在 `System32` 前面。** 于是 .cmd 里的 `find` 解析到 **GNU find**(Git 的 coreutils),不是 Windows 的 `find.exe`。

后果不是报错退出那么简单,而是**逻辑反转**:GNU find 把 `/I` 当文件名 → 报 `find: '/I': No such file or directory` 并返回 exit code 1,而 .cmd 里写的 `find /I "xxx" >nul` + `if errorlevel 1 goto 未找到` 会把"命令失败"误判成"没找到"。当时一个"等 IDEA 退出"的看门狗因此瞬间以为进程已退出,提前执行了后续动作。

**写 .cmd/.bat 时的规矩:**
- 用 `findstr` 代替 `find`,或直接写全路径 `%SystemRoot%\System32\find.exe` / `%SystemRoot%\System32\tasklist.exe`
- 别指望"命令报错"和"结果为否定"能靠 `errorlevel` 区分,先显式判 `if not exist` / 用 `where` 确认解析到哪个可执行文件
- 反过来:纯 bash 里跑 `tasklist /FI ...` 会被 MSYS 把 `/FI` 转成 `C:/Program Files/Git/FI`(需 `//FI` 或 `MSYS_NO_PATHCONV=1`),跨 cmd/bash 的命令一律用 PowerShell 的 `Get-CimInstance Win32_Process` 查进程最稳

相关:[[intellij-install-and-update-logs]] [[forge-compile-setup]]
