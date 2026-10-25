---
name: expedition-system-plan
description: 旅行家远征/指南针系统最终方案（2026-10-24 定案并已落盘，含 aaacat 1.1.8 交付/会话机制；UI 简洁版+新版配色）；编译/服务器启动通过，待游戏内实测
metadata: 
  node_type: memory
  type: project
  originSessionId: 21309521-f6fc-4592-a346-c7c2e499f33a
---

2026-10-24 定案。把 aaacat 的 explorer 移植成 goetyominous **核心系统**；当前指南针只对 PP 的猪灵旅行家开放，未来本体新指南针复用同一套核心。aaacat 参照已换为 `libs/aaacat-1.20.1-1.1.8.jar`，旧反编译在 `tmp_aaacat_decomp`。

**当前状态（2026-10-25）**：远征主体已于 2026-10-24 落盘并全部 `git add`（未提交），compileJava/processResources 通过，dev dedicated server 启动成功（`Structure loot cache rebuilt: structures 55, templates 1462, pools 324, loot-bearing structures 43`），客户端到主菜单无 traveler_compass 报错。2026-10-25 已再落盘“战利品存旅行家背包/空手交付/箱子自动存入”改动并通过编译。**无 PP 验证：dev run 的运行时只加载 Goety+Curios+本模组（PP 不在 mods/classpath）；最新改动后 dedicated server 仍 `Done (10.278s)`，核心结构缓存正常初始化，无本模组 NoClassDefFoundError/ClassNotFoundException；客户端此前也在无 PP 环境到主菜单。** 游戏内绑定、派遣、Claim、箱子/空手交付尚未人工实测。

## 已定案（覆盖旧方案）
- **返还 UUID**：全部换新（跟 1.1.8）。交付创建时给每只返还旅行家分配新 UUID，并把原 UUID 作为 origin 一起存进交付记录；实际成功放回世界后写 old→new remap，指南针 tick/UI 自动同步。旧 UUID 在探险记录未删前仍受 active snapshots 保护。
- **Recall 折损**：跟 1.1.8 的 `recallLosesCats`，默认 **false**。新增核心布尔配置 `RecallLosesTravelers`（默认 false）；为 true 时才按出发时记录的 deathChance 追加一次死亡判定。Open 包把该布尔发给 UI 以显示提示。
- **UI**：不做 1.1.8 的双 Tab/搜索/职业筛选/大卡片完整移植；保留简洁版布局（维度、模式、群系/遗迹、绑定旅行家、Start、队伍列表）。但视觉风格跟新版一致：深色面板、顶部 accent 线、TEXT/MUTED/ACCENT/ACTIVE_GREEN 配色、小号说明文字、队伍进度条、Retry/Claim/Recall 状态、服务端 feedback 状态行。无新增 GUI 贴图。
- **交付机制**：完整采纳 1.1.8 的 ExploreDelivery/ExploreDeliveryService 思路，记录**不再先删**；交付未完成就保留，UI 显示 Retry。奖励在交付创建时只抽一次并持久化，猫和奖励逐只/逐件恢复，已存在的按 UUID 跳过，背包塞不下的掉落脚下并持久化。全部完成才删探险记录。
- **UI 会话**：采纳 1.1.8 的 ExploreViewSessions + 心跳 + 进度包。会话租约 120 tick，客户端每 40 tick 心跳，服务端每 20 tick 只给打开界面的玩家推 Progress（绑定旅行家 + 队伍），关闭/登出发 false/清理。打开走 authoritative Open，操作反馈走非 authoritative Open + feedback。
- **包数量**：7 个（Open / Request / Start / Claim / Recall / Progress / View）。

## 架构
- 核心=本体，无 PP import：
  - `common/explore/`：ExpeditionConfig 之外的 ExpeditionKind / TravelerStatus / ExpeditionData / ExpeditionDelivery / ExpeditionDeliveryService / ExpeditionManager / ExpeditionViewSessions / StructureLootCache / StructureIndex / DimensionLootPool / LootResolver / RuinVisitTracker / ExploreEvents / ExpeditionSnapshot。
  - `common/explore/network/`：ExpeditionOpenPacket / Request / Start / Claim / Recall / Progress / View + ExpeditionNetwork。
  - `common/items/explore/ExpeditionCompassItem`：抽象基类，**不注册**。
  - `client/gui/explore/ExpeditionCompassScreen`：简洁版 UI。
  - `config/ExpeditionConfig` + `goetyominous/goetyominous-expeditions.toml`。
- PP 专属：
  - `common/items/pp/TravelerCompassItem extends ExpeditionCompassItem`，`acceptsTraveler` 只接受 `PiglinTravelerServant`（Z 自动拒绝）。
  - `PpItems` 注册 `goetyominous:traveler_compass`；模型用 `piglinproliferation:item/travelers_compass_16`；无新配方/获取途径。
  - `PiglinTravelerServant.isAcceptedWeapon`（文件第 134 行已有覆写）加 `PickaxeItem`；不要改 `AbstractPiglinServant:1101`。
- 核心探索事件/网络**无条件注册**；PP 只注册物品。无 PP 时无指南针可触发，系统休眠。
- 基类管绑定 NBT / 上限 / isFoil / 服务端 UUID 清理 / 绑定反馈 / 打开 UI / 通用资格判定；子类只覆写 `acceptsTraveler`。UI 与 Start 都用当前手持指南针的同一套 evaluate。

## 绑定成功反馈
在基类成功 addBound 分支、服务端执行，作用在旅行家实体上，**只在绑定成功时**：
- `SoundEvents.ARROW_HIT_PLAYER`，1.0F，0.45F
- 7 次 `ParticleTypes.HAPPY_VILLAGER`，坐标为 `getRandomX(1.0)` / `getRandomY()+0.5` / `getRandomZ(1.0)`，count=0，三轴 `nextGaussian()*0.02`，速度参数 0.5
- 来源 `AbstractPiglinServant.java:1017-1025`（linkToShrine）
解绑、失败不播。

## 配置（核心 ExpeditionConfig，17 项）
`goetyominous-expeditions.toml`，默认：
- `CompassBindLimit` 10
- `MinTravelers` 1
- `MaxTravelers` 10
- `BaseDraws` 3
- `ExtraDrawChancePerTraveler` 0.25
- `BiomeTicks` 18000
- `RuinTicks` 36000
- `DeathChance` 0.20
- `DeathDecreaseStart` 7
- `DeathDecreasePerTraveler` 0.05
- `DeathChanceFloor` 0.0
- `BaseOres` 0
- `OresPerPickaxe` 2
- `BaseMobDraws` 0
- `MobDrawsPerCrossbow` 2
- `DispatchRadius` 16
- `RecallLosesTravelers` false（新增，跟 1.1.8）

## 数据
`ExpeditionData(SavedData)` 名称建议 `goetyominous_expeditions`：
- 每玩家：访问维度、进入遗迹、`Map<UUID, ActiveExpedition>`、`Map<UUID, ExpeditionDelivery>`。
- 每 ActiveExpedition：id/owner/postedAt/dimension/kind/start/end/biome/structure/memberUuids/snapshots/dead/structureDraws/ores/mobDraws/deathChance/settled。
- 每 ExpeditionDelivery：recall/lost/returned/rewardCount/dropped、`cats`（新 UUID 的快照）、`origins`（对齐的旧 UUID）、`rewards`（UUID + ItemStack）、complete。
- NBT 按字段独立 key；交付存进对应 ActiveExpedition 的 `Delivery`，全部完成才随记录一起删。
- 全局 remap old→new 与 deadBindings 仍保留。

## 核心规则
- 绑定上限/解绑/光效/清理、Start 资格、目标模式/解锁、群系/遗迹校验、矿石/生物池、时长、默认值按原最终规则。
- 汇率：死亡率 = max(floor, base - per * max(0, n - (start - 1)))；结构抽取 = base + 超出 min 的每只 25%；矿石 = baseOres + 持镐数*oresPerPickaxe；生物抽数 = baseMobDraws + 持弩数*mobDrawsPerCrossbow；时长固定。
- 装备/额外抽取按**所有被选中的旅行家**计算，包括出发时掷死的（出发时读取，存入记录）。
- Start：先写 ActiveExpedition + deadBindings + 保存，再 discard 所有选中旅行家（1.1.8 顺序）。
- Claim/Recall：
  - 无 delivery 时创建：返回猫分配新 UUID + origin；Claim 抽奖励并存；Recall 若 `RecallLosesTravelers` 则按记录 deathChance 追加 death；lost=出发死者+召回死者；召回死者 origin 立即 mark deadBinding。
  - 逐只猫/逐件奖励恢复；已存在按 UUID 跳过；掉落物用带 UUID、持久化的 ItemEntity；checkpoint。
  - 有未完成 delivery 时 Claim/Retry 继续原交付，不重新抽奖；全部完成才删记录。
  - 目标维度缺失时矿石/生物池回退玩家当前维度，结构表也用回退维度。
- View sessions：open 时存 dimension+hand 并发 authoritative Open；Request 只换 dimension；Progress 用会话 hand 构建绑定列表和队伍；会话无指南针/手切换时 Start 服务端拒绝。
- 周期 Progress 只更新绑定旅行家状态/队伍，不重开 UI。

## aaacat 1.1.8 已采纳项
- ExploreDelivery/Service：交付重试、checkpoint、奖励只抽一次、记录保留至完成。
- ExploreViewSessions + Progress/View 包、120 tick 租约、40 tick 心跳、20 tick 推送。
- Open 的 tuning / feedback / 队伍 dimension / durationTicks / delivering。
- Start 先保存再 discard。
- 客户端不再读本地配置，服务端在 Open 里下发 tuning。
- 不采纳其完整 GUI 布局；只采纳视觉风格（面板/配色/accent/进度条/状态）。
- 不采纳其“返还实体新 UUID 且无 remap”缺绑定处理；本机在新 UUID 外再补 origin→new remap。
- 不采纳其猫职业/守卫/法师等加成规则，保留本系统的镐/弩规则。

## aaacat 复用
- 直接搬：StructureLootCache / StructureIndex / DimensionLootPool / LootResolver / RuinVisitTracker。
- 改：BequestData→ExpeditionData（加 delivery/origins/remap/deadBindings）；ExploreManager 猫资格换指南针 evaluate；CatComposition 规则换本系统；UI 简化。
- 丢弃：TrainedCats、猫职业相关配置、ScrollList 大改版、双 Tab/搜索/筛选。

## 2026-10-25 战利品携带改动（已落盘，编译通过）
- 数值：矿石固定基础 0、每只**归来且持镐**旅行家 +2；生物固定基础 0、每只**归来且持弩**旅行家 +2。装备计数改为只算存活者；出发死者不贡献。
- 分配 B：结构战利品在所有归来旅行家间轮流；矿石只分给归来且持镐者轮流；生物只分给归来且持弩者轮流；无对应持有者则该类为 0；全员死亡则战利品直接丢失（不掷/不发放）。
- 旅行家新增独立无限 `ExpeditionLoot` NBT 列表（`IExpeditionLootHolder` + `ExpeditionLootStorage`），**不动原 8 格食物 SimpleContainer**；食物/杀怪战利品旧逻辑不变。Claim 把战利品写进归来旅行家的快照 NBT，不再进玩家背包。
- 空手右键：主人、无可用箱子绑定、身上有远征战利品时，把远征战利品用 `BehaviorUtils.throwItem` 丢给玩家（食物不丢）。
- 箱子自动存入：新增 `ExpeditionLootChestGoal`；只处理远征战利品；排除 `isFollowing()` 和 `isStaying()`（其他模式都会去）；需要 Waystone 绑定且同维度仍是 ChestBlockEntity。箱子失效/被拆/跨维度 -> 视为无可用箱子，允许空手右键领取。
- `ExpeditionDelivery` 新格式不再存 Rewards 列表，RewardCount 在创建时写入；旧 delivery 的 Rewards 会被忽略（旧待交付建议清档）。
- 旧 `goetyominous-expeditions.toml` 不会自动覆盖已有值，测试实例必须手改四项或删配置。
- 新文件：`IExpeditionLootHolder` / `ExpeditionLootStorage` / `ExpeditionLootChestGoal`；改 `PiglinTravelerServant`、`AbstractPiglinServant`(hasUsableChestLink)、`ExpeditionDelivery`、`ExpeditionDeliveryService`、`ExpeditionManager`、`ExpeditionConfig`、语言。

## 2026-10-25 UI 对齐 + lang 清理（已落盘，编译通过）
- 新增 `client/gui/explore/ScrollList.java`（移植 aaacat 滚动列表：滚轮/拖动条/悬停/可选中/禁用/空文本/自定义行渲染）。
- `ExpeditionCompassScreen` 改成 aaacat 风格：740×430 面板、Dispatch / My Teams 两个 Tab、维度列表覆盖、群系/遗迹搜索、滚动目标/旅行家列表、旅行家生命 tooltip、全选筛选结果/清空、3 行摘要（目标/时长+每只折损/预期宝箱抽取）、Start；队伍 Tab 有 24px 高的可滚动卡片（48px 行高）、进度条、每卡右侧 Recall/Claim/Retry 动作格、全部/可领取或重试/探索中筛选、Team 计数、pending 超时。
- 不添加 aaacat 没有的选项；不做猫职业筛选。队伍动作只点卡片右侧动作格生效，点卡片其它区域不会误触发。
- Lang：删除未使用的 `message.goetyominous.expedition.inventoryFull`、`screen...noTeamAction`、`noBiomes/noRuins/noTravelers/noTeams`、`biomes/ruins`；增加 tab/search/selectFiltered/filter/empty/summary/health 键。动态 status/marker/fail/filter 键保留。中英 JSON 校验通过。
- 滚动列表相关方法名：ScrollList 使用 mojmap；如需改 UI 先看它和 screen 的 bounds/rowTop/contentRight。
- 2026-10-25 追加：搜索框改为 aaacat 精确布局（modeW=min(66,leftW/2)，x=left+modeW+4，width=leftW-modeW-4，右边缘对齐左列），不再伸入右列。
- 2026-10-25 追加：My Teams 的筛选按钮右侧新增“召集旅行家/Gather Travelers”按钮；新包 `ExpeditionGatherPacket`（协议版本升 6）；`ExpeditionViewSessions.gather` 用会话手持指南针，把已绑定且已加载的 READY/TOO_FAR/LIMITED_LIFE/PENDING_LOOT 旅行家传送到玩家周围 1.5 格，跳过远征中/未加载/死亡/非主人/BUSY；成功/失败通过 feedback 回界面和聊天栏。队伍卡仍只有右侧动作格触发 Recall/Claim/Retry。
- 2026-10-25 修正箱子满死锁：`ExpeditionLootStorage.hasRoom` 检查“任意一件远征物品是否能进箱”；`ExpeditionLootChestGoal` 只在至少一件能进时启动，`chestInteract` 会尽量塞、放不下的继续留在身上（不再因第一件放不下就整队停）。`PiglinTravelerServant.canAutoStoreExpeditionLoot()` = 有可用箱子 + 不跟随/待命/无目标 + 箱子有任意空间；只有返回 false 时空手右键才把全部远征物品丢给玩家。因此箱子满、箱子失效、跟随/待命/战斗中都可手动取回。新增 `TravelerStatus.PENDING_LOOT`（尚有未交付物品），`evaluate` 对带远征物品的实体返回该状态，UI 置灰，Start 返回 `fail.pendingLoot`；Gather 仍允许传送该状态。

## 实施顺序
1. 核心 ExpeditionConfig（17 项）+ ExpeditionData/ExpeditionDelivery + TravelerStatus + 枚举。
2. ExpeditionCompassItem 基类 + PP TravelerCompassItem + 绑定反馈 + 镐判定 + 模型/语言。
3. StructureLootCache / StructureIndex / DimensionLootPool / LootResolver + ExploreEvents 重载监听。
4. ExpeditionManager + ExpeditionDeliveryService + ExpeditionViewSessions + RuinVisitTracker + 事件。
5. 7 个网络包 + 简洁版 ExpeditionCompassScreen（新版配色）+ Open/Progress/View 接线。
6. 编译与实测。

相关：[[no-edit-without-consent]] [[no-comments-in-code]] [[goetyominous-compat-architecture]] [[goetyominous-split-into-modules]] [[goety2-servant-system]] [[git-add-new-files]]
