---
name: intellij-local-history-rollback
description: "要回退到某个时间点的代码时,git 常给不了;IntelliJ Local History 才是可靠来源(附路径)"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 14d3f3b9-0b38-4179-93de-762d32787889
  modified: 2026-09-30T01:32:10.967Z
---

用户经常一次工作好几小时不提交(2026-09-30 实测:45 个文件 / 862+ 行未提交,跨越一整天)。这种"回退到本地时间 X 点的状态"的请求,**git 大概率给不了**:

- 没有对应的 commit/stash/tag。
- `git ls-files --debug` 的 stat cache mtime(`ctime`/`mtime`)是"最后一次 add 时文件的 mtime",**不等于 index 里那份 blob 的年龄** —— 2026-09-30 实测就被它误导过:cache 显示 02:19:07,但 index 里的 `BeheadedKnightServant.java` 其实比 00:31 还老(没有 `isEnhanced()`、还在调旧的 7 参 `ThrownKnight` 构造器),`git restore` 出来直接编译失败。
- 所以判断"这份快照是不是目标状态"要**用内容特征交叉验证**(某个方法/字段/构造器签名在不在),别只看时间戳。

可靠来源是 IntelliJ Local History,而且它是活的:

```
C:\Users\TangJinbo\AppData\Local\JetBrains\IntelliJIdea2026.2\LocalHistory\
    changes.storageData       (30MB+,二进制私有格式)
    changes.storageRecordIndex
```

GUI 路线(推荐):项目视图右键文件或目录 → `Local History` → `Show History` → 挑时间点 → `Revert`。
二进制直读没试过,格式私有,不可靠。

回退前先备份到 `.scratch/`(已在 .gitignore 第 36 行)。
