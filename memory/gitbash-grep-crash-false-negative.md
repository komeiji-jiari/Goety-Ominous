---
name: gitbash-grep-crash-false-negative
description: 本机 Git Bash 的 grep.exe 会偶发 core dump，输出空结果伪装成『合法的否定』；改用 Grep 工具，并在 git add -A 前清 *.stackdump
metadata: 
  node_type: memory
  type: project
  originSessionId: 2eb3fd28-a8fe-44ad-91be-68ec73433a8e
  modified: 2026-10-07T13:38:38.163Z
---

本机 Git Bash(msys)的 `/usr/bin/grep` **会偶发崩溃**，报 `Aborted (core dumped)`，**并在当前目录留下 `grep.exe.stackdump`**。2026-10-07 一天内踩了三次。

危险的地方不是崩溃本身，是**崩溃时的空输出看起来和"没匹配到"一模一样**：

- `unzip -l <jar> | grep -i -e resourcepack -e patchouli | head` → 无输出，我据此断言"资源包没打进 jar"。**实际是 grep 崩了**；换个前缀重查，jar 里 661 个 black_book 条目都在（真正的路径前缀是顶层 `resourcepacks/`，不是 `assets/`）。
- `git ls-tree -r HEAD | grep stackdump || echo "不在 HEAD"` → 会走 `||` 分支打印"不在"，把崩溃当成否定结论。
- `grep -n -i stackdump .gitignore` → 直接 `Aborted (core dumped)`。

**规矩：**
- 内容搜索一律用 **Grep 工具**(ripgrep 后端)，别在 Bash 里调 `grep`。
- 万一必须用 bash `grep`，**空结果不能当成"不存在"**，换一路机制交叉验证:`git ls-tree`/`git log -- <pathspec>`(git 自带 pathspec，不经过外部 grep)、`find`、`awk`、`sed`。
- **`git add -A` 之前先清 `*.stackdump`**，否则仓库根目录那份会被一起暂存。已经在 `git status --short` 里见过 `A  grep.exe.stackdump` 这种混入。清理时对新文件要 `git rm --cached -f`(不加 `-f` 会报 "staged content different from both the file and the HEAD")。
- `.gitignore` 目前**没有**覆盖 `*.stackdump`。

相关:[[gitbash-path-poisons-cmd]] [[forge-compile-setup]]
