---
name: windows-admin-token-and-protected-paths
description: "本机账号是管理员但进程默认未提权;C 盘\"打不开\"的五类设计性成因与提权判据"
metadata: 
  node_type: memory
  type: project
  originSessionId: 88da6943-651d-4a69-8564-827dcc7a6ca7
  modified: 2026-09-30T20:11:08.192Z
---

本机账号 `okra\TangJinbo` **在 Administrators 组内**（`net localgroup Administrators` 可见：Administrator、TangJinbo），但 Claude Code 起的进程跑的是 **UAC 过滤令牌**，未提权——token 组里只有 `S-1-5-32-545`(Users)，**没有 `S-1-5-32-544`**。需要管理员权限的操作必须 `Start-Process -Verb RunAs` 再让用户点 UAC。

**Why:** 判"是不是管理员"用错工具会得出完全相反的结论——我犯过一次：用 `[WindowsIdentity]::GetCurrent().Groups` 找 544，在过滤令牌下返回 NOT-A-MEMBER，于是误报"账号不在管理员组"。正确判据是两条分开看：**账号身份**用 `net localgroup Administrators`；**当前进程是否提权**用 `(New-Object Security.Principal.WindowsPrincipal($id)).IsInRole(Administrator)`。

**How to apply:** C 盘"有保护/打不开"是五类**设计如此**，不是故障，也不是哪次清理弄坏的——(1) UAC 过滤令牌，未提权就拒绝；(2) 兼容 junction + 显式拒绝 Everyone 读取：`C:\Documents and Settings`、`C:\ProgramData\{Application Data,Documents,Templates,开始菜单,模板}`、`C:\Users\Default User`；(3) `NT SERVICE\TrustedInstaller` 所有：`C:\Windows`、`System32`、`WinSxS`、`Program Files*`，须 takeown 才能改；(4) SYSTEM 独占：`System Volume Information`、`Recovery`、`C:\ProgramData\Packages`；(5) 内核独占句柄：`pagefile.sys`、`hiberfil.sys`、`swapfile.sys`、`DumpStack.log.tmp`。**别对整个 `C:\Windows` 做 takeown**——WinSxS/System32 属主一变，Windows 更新与组件服务就报错且难回退；只对具体需要的文件单独提权。相关：[[windows-debloat-avast-xbox]]、[[gitbash-path-poisons-cmd]]。
