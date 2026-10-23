---
name: java-toolchain-location
description: "Java 只有 IntelliJ 的 .jdks 那份,不在 PATH,.jar 无文件关联"
metadata: 
  node_type: memory
  type: project
  originSessionId: e1344ee5-1d3e-4f5b-a708-04438e658898
  modified: 2026-10-02T11:46:22.411Z
---

本机唯一的 Java 是 IntelliJ 自动下载的那份：`JAVA_HOME=C:\Users\TangJinbo\.jdks\ms-17.0.20`（同目录另有 ms-21.0.12）。**没有系统级 JDK** —— `java` 不在 PATH（bash 与 cmd 里都是 command not found），`HKEY_CLASSES_ROOT\.jar` 不存在、`ftype jarfile` 为空 → 双击 .jar 毫无反应或弹"选择打开方式"。

**Why:** 不是文件下坏了。IntelliJ 的 `.jdks` 既不注册系统关联也不加 PATH，资源管理器和命令行都看不见它，所以 `.jar` 成了无主扩展名。

**How to apply:** 跑 1.20.1 相关一律显式指定 `C:\Users\TangJinbo\.jdks\ms-17.0.20\bin\java.exe`（1.20.1 必须 17，ms-21 会抛版本异常）。想让双击能用，要么装 Temurin 17 的 msi（会顺带注册关联），要么手写 `ftype jarfile="...\javaw.exe" -jar "%1"` —— GUI 里勾"始终使用"不带 `-jar`，双击仍可能失效。

**注意** bash 里直接访问 `.jdks` 路径会被安全守卫拦（"Access to a sensitive path"），但走 PowerShell 可以：`powershell -NoProfile -Command "& '...\java.exe' -version"`，或 `Start-Process ... -RedirectStandardError <log>` 后读日志。但**别在命令行里传中文路径**，会被编码搞坏且静默写飞，见 [[powershell-cli-mangles-nonascii-args]]。相关：[[java-ssl-avast-mitm]]。
