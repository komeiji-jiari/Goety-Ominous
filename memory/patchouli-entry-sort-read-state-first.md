---
name: patchouli-entry-sort-read-state-first
description: Patchouli 条目排序先比阅读状态再比 sortnum，未读永远排在已读前面
metadata: 
  node_type: memory
  type: reference
  originSessionId: e4b76898-3af6-4684-8f93-5bc80137cb77
  modified: 2026-10-07T11:32:37.558Z
---

Patchouli 1.20.1（85）`vazkii.patchouli.client.book.BookEntry.compareTo` 的真实键序（CFR 反编译自 gradle 缓存 `decomp_dependencies/curse/maven/patchouli-306770/7731017_mapped_official_1.20.1`）：

locked 最后 → `getReadState()` 比 `EntryDisplayState`（枚举序 UNREAD(0) < PENDING(1) < NEUTRAL(2) < COMPLETED(3)，即**未读最前、读过的沉到后面**）→ priority → sortnum → 本地化名字。

`computeReadState()`：`!read_by_default && !isLocked && !viewedEntries.contains(id)` → UNREAD；`turnin` 未完成 → PENDING；有 `patchouli:quest` 页且完成 → COMPLETED；否则 NEUTRAL。`read_by_default: true` 的条目**永远不会是 UNREAD**，所以给同分类所有条目都加上它 = 全部恒为 NEUTRAL = sortnum 顺序完全固定（代价是列表里不再有未读标记/动画）。

**Why:** 只写 sortnum 时，玩家读过的条目会整体下沉到未读条目之后，看起来像"改了没生效"，这是原版书的通用行为，不是配置错了。

**How to apply:** 给联动包条目排序就用 `"sortnum": 10,20,30…`（zh_cn 与 en_us 两份都要写，顺序必须一致）；要绝对稳定再加 `read_by_default`。分类按钮的顺序是另一条通道（`BookCategory.compareTo`，无阅读状态），见 [[ominous-icon-rotation-order]]。

**要插进别的模组条目之间（2026-10-07 实测）：** 对方条目无 `sortnum` 时默认 0，**连成一组**，我们的条目只能整体排在它们前（<0）或后（>0），插不进组内。办法是**整文件覆盖**对方条目：资源包放同路径文件即可整体替换（`ias_compat` 已对诡厄溢出的 `ominous_pyre_additions.json` 这么干）。用 `<modFile>/resourcepacks/<pack>` + `BuiltinPacksRegistry` 的 `Pack.Position.TOP` 注册，资源包天然压过 mod 自带资源。覆盖文件要带 `"flag": "mod:<对方modid>"`，并且**原样复制正文再插 sortnum**（别手打中文）。注意覆盖 = 正文也被冻结，对方更新后你的旧文案会持续生效。

**踩坑：sortnum 相同的条目按「本地化名字」比 `String.compareTo`（UTF-16 码元序）**，所以 zh_cn 和 en_us 的默认组内顺序**不一样**（如 zh:`僵尸伤害吸收者<工程师的机械<被绑定的工程师<被绑定的怪人<鳄牙兽`，en:`Bound Engineer<Bound Freakager<Crocofang<Engineer's Creations<Zombie Absorber`）。想让某条目"紧贴"另一条之下，靠"名字恰好排最后 + 自己 sortnum>0"只在**该语言**成立；要两种语言都稳，就给两个锚点都钉 sortnum（如 被绑定的怪人=20、友方魔法师=30、鳄牙兽=40）。

联动包条目分组规则（用户 2026-10-05 定，已用于 am/of/ua）：**仆从在前**，每个仆从下面紧跟与它相关的条目 —— 召唤用的聚晶、由该仆从技能衍生的攻击法术（如切削者→切削激光聚晶、伏特鳐→电球聚晶、绯红蚊子→呕血聚晶）、变异/取得物品（诡异类固醇→诡异蚊鬼）；**没有对应仆从的聚晶**排在所有仆从之后；**魔法配方/附魔类**最后。组内按条目 id 字母序（zh/en 顺序才一致）。配对证据去 `resourcepacks/<pack>/data/goetyominous/recipes/` 查：仆从仪式 `entity_to_summon`、聚晶仪式的 `activation_item`/材料与另一只聚晶是否同源、以及正文是否点名该生物。
