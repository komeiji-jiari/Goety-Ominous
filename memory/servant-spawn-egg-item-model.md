---
name: servant-spawn-egg-item-model
description: "新增仆从实体时必须同时补刷怪蛋的 item model json,否则蛋渲染成紫黑块(不用画 PNG)"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: dd37dc3f-55b7-4a34-ada8-77eb42b62b7a
  modified: 2026-09-24T10:15:35.655Z
---

注册 `ServantSpawnEggItem` 时,除了 Java 注册 + lang,还必须写
`src/main/resources/assets/goetyominous/models/item/<刷怪蛋注册名>.json`,内容就是
`{"parent": "minecraft:item/template_spawn_egg"}`。文件名 = 注册名(如
`hovering_hurricane_servant_spawn_egg.json`),不是实体 id。

**Why:** Goety 的 `ServantSpawnEggItem` 继承 `ForgeSpawnEggItem`,蛋的两色由构造参数
`new ServantSpawnEggItem(type, 背景色, 高光色, egg())` 自己 tint,底图走原版
`minecraft:item/spawn_egg` + tintindex,所以**不需要**自定义 PNG —— 但缺了 model json 就
没有模型,游戏里直接渲染成紫黑方块。2026-09-24 的飓旋仆从刷怪蛋就是漏了这个文件,被用户点出。

**How to apply:** 把这份 json 当成实体注册的一部分一起交,别等用户发现。校验手法:把
`src/main/java` 里所有 `"*_spawn_egg"` 注册名和 `models/item/*_spawn_egg.json` 取差集,
应当为空。仓库里 119 个刷怪蛋模型全是 `template_spawn_egg` 父级,只有 `tremorsaurus_servant_egg.png`
这类**方块蛋**才有独立贴图,别混为一谈。

相关:[[no-unrequested-recipes-patchouli]]、[[goety-ac-dinosaur-spirit-port]]
