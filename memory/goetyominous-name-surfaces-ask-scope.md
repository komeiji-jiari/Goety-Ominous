---
name: goetyominous-name-surfaces-ask-scope
description: 本仓库「模组名」有 4 处独立显示面，用户说「改名字」时必须先问改哪个，别默认全改
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 713a849e-2817-4eda-8e1a-4073c4d975d5
  modified: 2026-10-07T11:17:53.376Z
---

「诡厄巫法 凶兆 / Goety Ominous」这个名字在本仓库有 4 处**互不相干**的显示面，改一处不影响其他：

1. `gradle.properties` 的 `mod_name` → mod 列表显示名（经 build.gradle 注入 mods.toml 的 `displayName`）
2. `assets/goetyominous/lang/{en_us,zh_cn,pt_br}.json` 的 `itemGroup.goetyominous` → 创造模式物品栏页签名
3. 同文件的 `key.categories.goetyominous` → 按键绑定分类名
4. `assets/goety/patchouli_books/black_book/{en_us,zh_cn}/categories/goetyominous.json` 的 `name` → Patchouli 手册里的分类名；**每个资源包各有一份**（ac/am/ias/mm/of/ua + 本体 = 各 7 份）

语料里「项目名字」「模组名字」都可能只指其中一处。

**Why:** 2026-10-07 用户说「把诡厄巫法 凶兆的联动项目的名字改为诡厄凶兆：联动」，我按整个模组改名处理，把 1/2/3 也改了；用户回「我只希望 patchouli 手册里面显示这个名字 你怎么把模组名改了」，要求回退 1/2/3，只保留 4。

**How to apply:** 收到改名的请求，先问「改哪一面」再动手，别拿「模组名」当整体。真要回退时**不能用 `git checkout`/`git restore`**——这些文件在会话开始时就带着 stat-dirty 的 ` M` 标记（mtime 变了但内容 == HEAD），`git checkout` 有清掉用户未提交工作的风险；用反向 sed 逐串还原，再用 `git show HEAD:<file> | diff - <file>` 逐字节验收（`git diff` 无输出 + `git status` 仍显示 ` M` 是 stat 缓存假象，不代表还有改动）。参见 [[intellij-local-history-rollback]]、[[no-unrequested-recipes-patchouli]]。
