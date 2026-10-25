---
name: trial-spawner-mob-tool
description: 给试炼密室刷怪笼家族新增怪物的脚本（tools/add_trial_spawner_mob.py）+ 引用链/两个轴/坑；以后加更多密室怪直接用
metadata: 
  node_type: memory
  type: project
  originSessionId: 3cdcb961-240f-4035-939e-38af37edc283
---

**脚本**：`E:\java\tools\add_trial_spawner_mob.py`（复用 `port_trial_chambers_nbt.py` 的 NBT 读写 + `typed_repr` 往返校验）

```
python tools/add_trial_spawner_mob.py <mob_id> <family> [--base <族/模板>] [--family-config] [--config-from <族/模板>] [--equipment <loot_table> | --no-equipment] [--dry-run]
# family ∈ melee / ranged / slow_ranged / small_melee / breeze
# 实例（2026-10-10 已跑）：
#   python tools/add_trial_spawner_mob.py goetyominous:mired  melee  --base slow_ranged/poison_skeleton
#   python tools/add_trial_spawner_mob.py goetyominous:dreden ranged --base ranged/stray --no-equipment
```

**别名有两种形状，脚本都支持**（这是最容易踩的一处）：
- **顶层 `minecraft:random`**（`contents/melee`、`contents/small_melee`）→ 往它的 `targets` 追加一条 target ⇒ 只建 1 份模板/池
- **`minecraft:random_group` 里的成对 `direct`**（`contents/ranged` + `contents/slow_ranged` 同组）→ **必须追加一整个新组**：组里同一个 alias 不能出现两次（别名表是 ImmutableMap，重复键直接抛异常），而且新组要把组里**所有** alias 都映射齐，否则漏掉的那个 alias 会指向不存在的池 ⇒ 那间密室的整片 slow_ranged 刷怪笼全空。脚本因此会**自动多建一份姊妹模板/池**（如加 ranged 的怪时会连 `slow_ranged/<怪>` 一起建）
- `breeze` 那类不是别名而是真实池文件（`spawner/contents/breeze.json`），脚本暂不自动处理成长对组，走它时会报错提示

**⚠️ `ranged` 与 `slow_ranged` 的差别（2026-10-10 实测，三档一致）**：两种族的方块/调色板/拼图、以及 **`ominous_config` 完全相同**；差别只在 `normal_config` 的节奏三元组：

| 字段 | ranged | slow_ranged |
|---|---|---|
| `ticks_between_spawn` | 20（1 秒） | **160（8 秒）** |
| `simultaneous_mobs` | 3.0 | 4.0 |
| `simultaneous_mobs_added_per_player` | 0.5 | **2.0** |

⇒ ranged＝快节奏小队、slow_ranged＝慢节奏怪潮（名字里的 "slow" 指刷新间隔，不是怪物攻击慢）。**同族内部各怪的 config 也必须一致** —— 加远程怪时如果只按 `--base` 抄（比如 base 用了 `ranged/stray` 而没管 slow_ranged 那边），姊妹模板会继承错误的节奏/装备（本次踩过：dreden/wraith/border_wraith 的 slow_ranged 曾带快节奏 20/3/0.5，已修成 160/4/2；mired 则因整体抄 zombie 配置而正确）。

**`--family-config`** = 每份模板的 config 抄**同族同 partner** 的（远程族会自动取对的那档节奏），以后加怪建议一律带上 ✓（它管"同族节奏/装备一致"，`--config-from` 管"整体照抄某只怪"）。

**`--config-from <族/模板>`** = 把那个模板的 `normal_config` + `ominous_config` **整块抄过来**（连装备表、`loot_tables_to_eject` 一起），然后再按 `mob_id` 重盖实体。想"新怪的配置和老怪完全相同"就用它（例：mired 的配置应与僵尸完全相同 → `--config-from melee/zombie`）。⚠️ **只加 `--equipment` 只换装备表**、`loot_tables_to_eject` 等还是底稿那份 —— 要"完全相同"必须用 `--config-from`。

`--no-equipment` = 把 `spawn_potentials[*].data.equipment` **整块删掉**（不是只删内层 `loot_table` —— 空壳过不了 `EquipmentTable.CODEC`）= 刷出来的怪不穿盔甲不拿武器（同蜘蛛/史莱姆）。注意原版数据里 spider/slime 更进一步：**整个没有 `ominous_config`**（`TrialSpawner.ominousConfig` 是 `optionalFieldOf(..., DEFAULT)`，可省）。想让不祥形态跟蜘蛛一模一样就连 `ominous_config` 一起删；只是"不穿装备"的话删 `equipment` 即可、不祥形态照常刷。

它做三件事（**纯新增**：目标 nbt / 池 json 已存在就报错退出，别名只追加）：
1. 复制 `<base>.nbt` → `structures/trial_chambers/spawner/<family>/<name>.nbt`，把 `normal_config`/`ominous_config` 里 `spawn_potentials[N].data.entity.id`（以及 `spawn_data.entity.id`，若有）改成新怪
2. 抄本族任一个池 → `worldgen/template_pool/trial_chambers/spawner/<family>/<name>.json`，只改 `location`
3. 在 `worldgen/structure/trial_chambers.json` 的 `pool_aliases` 里给 `contents/<family>` **追加**一条 `target`；该族不是别名而是真实池时（**breeze** 就是）改往 `spawner/contents/<family>.json` 追加一个 element

**引用链**（换怪只需碰最后一环）：密室模板拼图 → `spawner/connectors/<family>.nbt`（它的拼图 `pool = goetyominous:trial_chambers/spawner/contents/<family>`）→ `contents/<family>`（别名或真实池）→ 池 `spawner/<family>/<怪>` → 模板 `spawner/<family>/<怪>.nbt`（内含 trial_spawner 方块实体）。

**两个轴别混**：`family` = 进哪一档/哪个别名；`--base` = **刷怪笼周边方块抄谁**。18 个刷怪笼模板**结构完全相同**（18 个方块 + 同一个 `(minecraft:spawner → minecraft:empty)` 拼图），差异只在（a）调色板、（b）方块实体的 `spawn_potentials`。调色板公共部分 = `waxed_chiseled_copper + waxed_copper_block + air + jigsaw + trial_spawner`，其余"环境方块"共 **10 种**（2026-10-10 全量实测）：`mossy_cobblestone`=zombie/frayed/bone_spider/vindicator/**muck_wraith**；`chiseled_sandstone`=**husk**；`stone+cobweb`=spider/web_spider；`podzol(snowy=false)+bone_block(axis=y)+red_mushroom`=mired/poison_skeleton；`packed_ice`=stray/dreden/wraith/border_wraith/icy_spider/**reaper**；`bone_block`=skeleton/rattled/pillager；`moss_block`=slime/crypt_slime/**black_wolf**；`stone_bricks`=silverfish；`cobblestone+mossy_cobblestone`=baby_zombie；`podzol+stone+red_mushroom+cobweb`=cave_spider。⚠️ 旧版记忆把"沼骸风"记成 podzol+bone_block —— 那其实是 mired/poison_skeleton 的样式，**husk 实际是 chiseled_sandstone**（已修正）。⇒ 整套抄 base 安全，拼图一个字不用动。

**坑**：
- ⚠️ **结构 json 的 dump 必须 `sort_keys=False`**：`trial_chambers.json` 的键序是逻辑序（`type, alias, targets`）不是字母序，`sort_keys=True` 会把整文件重排（假 diff 26 增 22 删）。脚本已改 False。
- 别手改二进制 NBT —— 用脚本（自带往返校验）。
- **worldgen 不能 `/reload`**：要重进世界（或新世界），且只影响**新生成**区块；老密室的刷怪笼不变。
- 别名加一项**不污染世界生成随机流**（加权掷选仍是一次 `nextInt`，只是取值域变了）⇒ 其他结构/其他族不受影响。
- 装备在 `spawn_potentials[N].data.equipment.loot_table`（普通/不祥各一份）：照搬沼骸 ⇒ 新怪普通形态**无装备**、不祥形态穿 `goetyominous:equipment/trial_chamber_ranged`。要换近战就得手改这一处（脚本拒绝覆盖已存在文件）。怪自己的掉落走 `loot_tables/entities/<name>.json`，与本机制无关。
- 怪必须是 `MobCategory.MONSTER` 才走"只查难度≠和平"那条路（`mired` ✓ = `ModEntityTypes:471`）；非 MONSTER 走 `SpawnPlacements.checkSpawnRules`，可能因高度/光照刷不出来（见 [[trial-spawner-port-plan]] 的 `checkSpawnRulesForTrialSpawner`）。

**⚠️ 工具自身的坑（2026-10-10 踩过三次）**：
- **`--family-config` 的参照规则**：成对族用**同名 partner**（`ranged`↔`slow_ranged`，节奏才会对）✓；**非成对族（melee / small_melee）必须用 `--base` 本身**，绝不能取"族内第一个" ✗ —— 曾因此把 `crypt_slime` 的 config 抄成 `baby_zombie` 的，丢掉了史莱姆的两条 `Size` 变体、`simultaneous_mobs` 变 2.0、不祥形态还挂上了近战盔甲表 ✗✗（已修 + 已删掉重做）。**"照抄某只怪"时，`--base` 指定的那只就是 config 的权威来源。**
- 池源那里 `first_or_none(POOL_DIR/fam)` **必须传 `'.json'`**（默认后缀是 `.nbt`）。初版漏了 ⇒ 返回 None ⇒ `None + '.json'` 抛 TypeError ⇒ **单族（melee）路径在写池时崩**；成对族因为 `partner` 有值走了 `or` 短路才没暴露。已修 + 加了"池目录没有可参照 json"的守卫。
- **`--dry-run` 只覆盖到"要建哪些文件/别名形状"，不跑模板改写、池和别名三步** ⇒ 不能用它当冒烟测试。
- **教训：冒烟测试必须跑完整路径，并且看完整输出** —— 我曾用 `grep -E "写入|entity"` 过滤输出，把 `zzz_check` 那次的 traceback 滤掉了、误判为成功（其实崩在写池、别名也没加）；随后只验证了 config（碰巧全对）就撤了，掩盖了问题。**验收要覆盖"文件是否真写出来 + 别名是否真加了"**（`git status` 里应能看到池 json 是 `A`、结构 json 的 diff 增加）。

**⚠️ 校验脚本自身的坑**：做"新旧 config 是否相同"的对比时，**mob 归一化集要写全**（最省事：`id` 里带 `:` 就归一成 `MOB`）。我手写白名单漏项，先后两次把 `muck_wraith`/`ranged/rattled` 误报成"异"，白查一轮。

**⚠️ 校验要"结构 vs base、config vs config-from"分开比**（2026-10-10 踩）：拿整根 NBT 跟参照模板对比会因为 **palette 合法地不同**而假报 FAIL（icy/bone 的 palette 就是要换）。正确比法：(a) 把刷怪笼方块实体的 `nbt` 剥成 None 后与 `--base` 比 ⇒ 应逐字段一致；(b) `normal_config`/`ominous_config` 与 `--config-from` 比（实体 id 归一化）⇒ 应只差实体。最后再看 `git diff` 结构 json = 只多 3 条 target、键序没被动。

**换 palette（给已落地模板改周边方块，2026-10-10 实例：black_wolf 的 stone_bricks→moss_block）**：不用删档重做。所有模板的方块引用的是 **palette 下标**，25 份 palette 槽位一一对应（公共部分同序，只有"环境方块"那 1~4 个槽不同）⇒ **原地改 `palette[i]['Name']` 即可，等价于换 `--base` 重做**。前置验证两条：(1) 两份模板剥掉方块实体后 diff，确认只差 palette 那几项；(2) 目标方块名在文件里**只出现一次**（就在 palette）。写盘照 `add_trial_spawner_mob.py` 的姿势：`P.write_nbt(tag,root)` + **`typed_repr` 往返校验** + `assert 旧值` 防呆。改完**必须重新 `git add`**（文件已是 A 状态，改后变 AM，不重 add 提交的还是旧版）。

**已加**：
- `goetyominous:mired` → melee 族（2026-10-10），周边方块抄沼骸（podzol/bone_block/red_mushroom），与 zombie/husk/spider 各 25%。**config 已整体对齐 `melee/zombie`**（`normal_config`/`ominous_config` 逐字段相同，只有实体是 mired）：不祥形态 = `equipment/trial_chamber_melee`（刀甲），弹射表也同僵尸。⚠️ 最初照搬沼骸底稿导致不祥 mired 拿弓（`trial_chamber_ranged`）—— 修法是把整块 config 换成僵尸那份再重盖实体（现在用 `--config-from` 一步到位）。
- `goetyominous:dreden` → ranged 族（2026-10-10），周边方块抄 stray（`packed_ice`），`--no-equipment` ⇒ 普通与不祥形态都不带盔甲武器；按成对组规则同时建了 `slow_ranged/dreden`（**节奏已修为慢档 160/4/2**），新组 = `ranged→dreden + slow_ranged→dreden`。原版 stray 的不祥装备是 `{slot_drop_chances: 0.0, loot_table: goetyominous:equipment/trial_chamber_ranged}`（`slot_drop_chances: 0.0` = 装备不掉落）——被 `--no-equipment` 整块删掉了。
- `goety:wraith` + `goety:border_wraith` → ranged 族（2026-10-10），**完全照抄 dreden**（`--base ranged/stray --no-equipment`）：各带 `slow_ranged` 姊妹、周边方块 `packed_ice`、普通/不祥都不带装备；四份模板的 `normal_config`/`ominous_config` 与 dreden **逐字段相同**（已用规范化对比证明）。别名组现为 6 组（skeleton/stray/poison_skeleton/dreden/wraith/border_wraith，各 1/6）。
  ⚠️ 这两只是**前置模组 Goety2 的实体**（不是本模组的）—— `ModEntityType.java:446-447` 实证：`register("wraith"/"border_wraith", …, MobCategory.MONSTER, sized 0.6×1.99)`。引用硬前置 goety 的实体 id 合法 ✓（走 `EntityType.by(tag)` 解析）。**换怪的 id 前务必先核实注册名**：写错时刷怪笼不报错、只是静默不刷（`TrialSpawner.spawnMob` 里 `EntityType.by` 为空即 `Optional.empty()`）。
- `goety:frayed` → melee 族（2026-10-10），照抄 `melee/zombie`（方块 mossy_cobblestone + 僵尸那套 config，不祥形态拿 `trial_chamber_melee`）。
- `goety:rattled` → ranged 族（2026-10-10），照抄 `ranged/skeleton`（方块 bone_block）+ `--family-config` ⇒ `ranged/rattled` 快节奏、`slow_ranged/rattled` 慢节奏 160/4/2 ✓。两只都经 `ModEntityType.java:441-442` 核实为 `MobCategory.MONSTER`（0.6×1.95 / 0.6×1.99）。
- `goety:muck_wraith` → ranged 族（2026-10-10），照抄其他 wraith（`--base ranged/stray --no-equipment --family-config`）⇒ 与 `wraith`/`border_wraith` 逐字段一致 ✓。（**同日用户又改 palette：packed_ice → mossy_cobblestone，与僵尸同款**，两份模板都改；其余仍与 wraith 一致）
- `goety:crypt_slime` → small_melee 族（2026-10-10），照抄 `small_melee/slime`（**含两条 `Size:1/2` 变体**，无装备 ✓）。⚠️ 第一次因 `--family-config` 的参照 bug 抄成了 baby_zombie（错），已删除重做 ✓。
- `goety:reaper` → melee 族（2026-10-10），照抄 `melee/zombie` **但 `--no-equipment`** ⇒ 与僵尸唯一差别就是 `spawn_potentials` 里的 `equipment` 被删掉（普通/不祥都裸装 ✓）。（**同日用户又改 palette：mossy_cobblestone → packed_ice，与冰蜘蛛同款**）
- **蜘蛛三兄弟（2026-10-10）**：`goety:web_spider` / `goety:icy_spider` / `goety:bone_spider` → **melee 族**，`--config-from melee/spider` 三只同一份；`--base` 只管周边方块 ⇒ web=`melee/spider`（palette = stone + **cobweb**，和僵尸不同！）、icy=`ranged/stray`（packed_ice，与 dreden 一致）、bone=`melee/zombie`（mossy_cobblestone）。注册在 Goety2 `ModEntityType.java:433-435`（均 MONSTER、1.4×0.9）。
  **⚠️ 蜘蛛模板的特性（与僵尸完全不同）**：normal_config **无 equipment**；**`ominous_config` 连 `spawn_potentials` 都没有**（只有 loot_tables_to_eject + simultaneous_mobs + total_mobs）⇒ "照抄蜘蛛" = 不祥形态也不穿装备、且刷的仍是同一只怪。想让别的怪也这样，`--config-from melee/spider` 一步到位。
- **三只 hostile_ 系列（2026-10-10，收尾批次，均已实测验收）**：
  - `goety:hostile_black_wolf` → **small_melee**，抄蠹虫：`--base small_melee/silverfish --no-equipment`。蠹虫模板和蜘蛛同款（normal 无 equipment、ominous 无 spawn_potentials）⇒ 天然零装备，`--no-equipment` 在此是 no-op 保险。**palette 后被用户改掉：stone_bricks → `moss_block`**（同 slime/crypt_slime 那款），改法是原地换 palette[0].Name（见下面「换 palette」）。
  - `goety:hostile_zombie_vindicator` → **melee**，`--base melee/zombie --no-equipment`（palette = mossy_cobblestone）⇒ 与僵尸唯一差别 = ominous 的 `trial_chamber_melee` 装备表被整块删（自带斧头、不穿盔甲）。
  - `goety:hostile_skeleton_pillager` → **ranged 成对族**，`--base ranged/skeleton --family-config --no-equipment` ⇒ **"抄骷髅但不要装备"的标准解法**：`--family-config` 让 ranged 半份抄 ranged/skeleton（快 20/3/0.5）、slow_ranged 半份抄 slow_ranged/skeleton（慢 160/4/2），`--no-equipment` 再统一删 ominous 的 `trial_chamber_ranged`。⚠️ **不能用 `--config-from ranged/skeleton` 干这事**——脚本里 `--config-from` 跑在 `--family-config` 之后会覆盖它，姊妹份会带错快节奏（老坑重演）。
  - 注册实证 `ModEntityType.java:423/426/427`，均 MONSTER（0.6×0.85 / 0.6×1.95 / 0.6×1.99）。"自带武器/不能装备"类需求一律 `--no-equipment`；成对族记得叠加 `--family-config`。
- 三只分别经 `ModEntityType.java:445/448/449` 核实为 `MobCategory.MONSTER`（reaper 0.6×1.99 / muck_wraith 0.6×1.99 / crypt_slime 2.04×2.04）。

**当前档位**：melee **10 项**（zombie/husk/spider/mired/frayed/reaper/web_spider/icy_spider/bone_spider/hostile_zombie_vindicator）、small_melee **6 项**（slime/cave_spider/silverfish/baby_zombie/crypt_slime/hostile_black_wolf）、远程 **9 组**（skeleton/stray/poison_skeleton/dreden/wraith/border_wraith/rattled/muck_wraith/hostile_skeleton_pillager，每组 = ranged+slow_ranged 成对）。

相关：[[trial-spawner-port-plan]] [[trial-chamber-port-plan]] [[git-add-new-files]]
