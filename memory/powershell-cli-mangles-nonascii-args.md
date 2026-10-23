---
name: powershell-cli-mangles-nonascii-args
description: "bash 调 powershell.exe 时命令行里的中文路径会被编码搞坏,产物静默写飞;先写 ASCII 路径再 mv"
metadata: 
  node_type: memory
  type: project
  originSessionId: 3bfa72f4-d8ad-4102-8299-11b083468532
  modified: 2026-10-02T11:46:15.583Z
---

从 Git Bash 调 `powershell.exe -File script.ps1 "<中文路径>"` 时，**中文参数会被编码破坏**（中文 Windows 上按 GBK/ANSI 解读）。脚本本身不报错、还会照常打印 `wrote <路径>`，但文件其实写到了别处或根本没落盘 —— 目录时间戳和字节数都不会变，看起来像"没执行"。

**Why:** bash 到 Windows 的 argv 转换与本机 ANSI 代码页不一致；`-File` 的参数不做 UTF-8 还原。输出行里路径显示成乱码（`[�����ʾ¼]`）就是信号 —— 那说明 PowerShell 收到的已经是坏字符串。

**How to apply:** 别在命令行传非 ASCII 路径。让 PowerShell 只写 ASCII 路径（如 `D:\ark\java\.scratch\out.zip`），校验完再用 bash `mv` 挪到中文名目标；bash 自己的中文路径处理是正常的。验收必须看**产物**（`unzip -l` 的条目数 / 字节数 / mtime），不能只信脚本的"成功"输出。

纯 ASCII 路径的 PowerShell 调用不受影响。相关：[[java-toolchain-location]]（走 PowerShell 绕守卫的前提）、[[gitbash-path-poisons-cmd]]。
