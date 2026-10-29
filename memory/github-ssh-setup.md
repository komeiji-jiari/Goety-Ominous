---
name: github-ssh-setup
description: GitHub 拉取障碍的四层原因及当前解决方案(国内 DNS 污染 + 特殊位置 SSH 密钥)
metadata: 
  node_type: memory
  type: project
  originSessionId: ebfb4bf9-8822-4bed-afcd-91d5491457c5
  modified: 2026-10-29T16:14:43.882Z
---

本项目 `D:\ark\java`(Goety-Ominous 模组)无法 `git pull` 的历史根因是四层叠加问题,2026-08-08 已全部解决:

1. **dubious ownership** — 仓库目录属 Administrators,已加 `safe.directory` 白名单。
2. **SSH 主机密钥** — 已用 `StrictHostKeyChecking=accept-new` 记录 github.com 密钥。
3. **公钥认证** — 生成了新 ed25519 密钥在 **`C:\Users\TangJinbo\github_ed25519`**(非标准 `.ssh` 目录,沙箱禁止写入 `~/.ssh`),公钥已加到 GitHub 账号 **cakeskull**(远程仓库属 komeiji-jiari,但 pulls 正常)。
4. **DNS 污染** — 系统 DNS 无法解析 `github.com`(国内 ISP 常见),但 8.8.8.8 / 223.5.5.5 可解析到 `20.205.243.166`。

**Why:** 以上任何一个问题都会让 git 报错,且错法不同(所有权报错、host key、publickey、DNS 超时)。

**How to apply:** 仓库 `.git/config` 里需写入 `core.sshCommand`(2026-08-09 发现该配置项会丢,丢了就报 `Permission denied (publickey)`,因为密钥不在 `~/.ssh`)。**2026-10-29 起端口 22 整体不通**(DNS 已解析到 20.26.156.215,但 `ssh ... port 22` 直接 connection timed out),改用 SSH-over-443 走 `ssh.github.com`,实测 push 成功:
```
git config core.sshCommand "ssh -o HostKeyAlias=github.com -o Hostname=ssh.github.com -o Port=443 -o ConnectTimeout=15 -o IdentitiesOnly=yes -i C:/Users/TangJinbo/github_ed25519"
```
`HostKeyAlias=github.com` 是为了复用 known_hosts 里 github.com 的条目(两者主机密钥相同)。若再遇拉取失败,先看报错类别对症下药:publickey → 重跑上面命令;连接超时 → 先试 443,再 `nslookup github.com 223.5.5.5` 换 IP。更持久的根治是加 hosts 条目或改系统 DNS 为 223.5.5.5。
