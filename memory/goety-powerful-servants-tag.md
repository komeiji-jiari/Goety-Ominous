---
name: goety-powerful-servants-tag
description: "goety:powerful_servants 是纯成员标签(决定仆从判定/免晕/免缠/免击坐/不惧凋灵),而漏写 required:false 会让整条 tag 加载失败并级联拖垮 goety:servants"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 9a4ad94d-844b-4b31-9b76-8dba6723ddcd
  modified: 2026-10-04T20:02:16.407Z
---

`goety:powerful_servants` 本身**没有任何 Java 代码读**——它是纯成员标签,由别的 tag 引用后生效。原版 Goety(2.5.58.4)里引用它的有:

- `goety:servants` → 让实体被 Goety 的通用"仆从"判定认到(`ModTags.EntityTypes.SERVANTS`,读它的类:`api/entities/IOwned`、`common/events/ServantEvents`、`utils/ServantUtil`、各 `AbstractNecromancer`)
- `goety:no_seating` → 不坐 Goety 的 SeatEntity
- `goety:unstunnable` → 免疫 Goety 的 Stunned 效果
- `goety:untangleable` → 不能被 Tangled
- `witherreincarnated:not_scared_of_wither` → 不因凋灵逃跑
- `witherreincarnated:wither_cant_possess` → 凋灵不能附身

**Why:** 原版成员是 drowned/wither_necromancer_servant、wildfire、ravager、brood_mother_servant、black_beast、redstone_golem、grave_golem、redstone_monstrosity —— 即"大只仆从"的统一豁免清单。

**How to apply:** 往这个 tag 加本 mod 实体时,**名字写错且没加 `"required": false` 会让整条 tag 加载失败**(日志报 `Couldn't load tag goety:powerful_servants as it is missing following references: ...`),而失败会沿引用链级联——`goety:servants` 随之整条不加载 → `ModTags.EntityTypes.SERVANTS` 变空 → 全包所有仆从判定静默失效。2026-10-04 实锤:`goetyominous:cerberus_servant` 写成这个(实体注册名其实是 `goetyominous:cerberus`,`ModEntityTypes.java:449`),崩了一整条链。加 mod 间兼容项一律写成 `{"id": "...", "required": false}`。

排查姿势:`grep -rl "TAGNAME" <解包jar>` 只在定义它的类里命中 = 纯成员标签,去 `data/**/tags/**` 里 grep 反查谁引用了它。

相关:[[goety-servant-canattacktype-kills-targeting]] [[goety-servant-follow-standby]] [[forge-duplicate-modid-jar-silent-pick]]
