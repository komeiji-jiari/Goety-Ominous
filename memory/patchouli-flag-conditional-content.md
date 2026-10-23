---
name: patchouli-flag-conditional-content
description: Patchouli 类别/条目的 flag 字段是条件化手册内容的正规通道；往别的模组条目里追加内容只能整文件覆盖；诡厄溢出=goety_spillage、灾厄溢出=illageandspillage
metadata: 
  node_type: memory
  type: project
  originSessionId: b4ccd523-2418-4294-a5b0-d05872100c70
  modified: 2026-10-07T11:14:04.666Z
---

Patchouli 的类别/条目 JSON 都支持 `"flag"` 字段（`BookCategory.canAdd()` / `BookEntry.canAdd()`），取值查 `PatchouliConfig.getConfigFlag()`：支持 `mod:<id>`（Patchouli 给每个已加载模组自动置真，`mod:` 前缀查不到也不刷警告）、`!name`（取反）、`&a,b`（与）、`|a,b`（或）。flag 为假时该条目/类别**根本不进 BookContents**。注意 `BookRegistry.reloadContents()` 每次重载都先调 `reloadBuiltinFlags()`，所以标志一定在构建之前就绪。条件化手册内容走这条通道，不需要 Java/mixin。

坑：`BookEntry.initCategory()` 找不到 category 会 `throw new RuntimeException`（整本书报错）——所以给类别加 flag 时，指向它的条目必须加同样的 flag。

**往别的模组的现有条目里追加内容，只能整文件覆盖**：Patchouli 没有追加页的 API。`BookEntry.getPages()` 在开成就时返回 `realPages` 的**过滤副本**（改它不生效），关成就时直接返回 private final 的 `realPages` 本身——两条路都没有稳定的写入口，反射改 private final 不值当。做法 = 在 `<book>/<lang>/entries/<原路径>.json` 放一份「对方原文 + 我们的追加页」的副本，靠我们 `Pack.Position.TOP` 的资源包盖住对面 jar（同一 id 先入者胜）。**覆盖文件必须带 `"flag": "mod:<对方模组id>"`**：不加的话对方模组没装时，我们会凭空造出一条指向不存在的 `对方:...` 类别的条目 → `initCategory()` 抛异常整本书报错。代价是对方原文被复制进来，对方更新那一条时会被我们静默盖掉。

模组中文名对照：**诡厄溢出** = goety_spillage（根类别 `goety:spillage`，自带 制作/设备(artifice)/聚晶/仆从/变异 五个子类别；mandatory 依赖 illageandspillage）；**灾厄溢出** = illageandspillage（我们自己的 `goety:iascompat` 类别「灾厄溢出联动」）。别把两者混起来读用户需求。

相关：[[ominous-icon-rotation-order]]、[[goetyominous-mod-folder-isolation]]
