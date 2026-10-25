---
name: trial-chamber-port-plan
description: 试炼密室（trial chambers）移植：Java 侧（size/encapsulate/别名机制）与批 1 数据侧均已落地，含脚本、验证结论、踩坑与剩余项
metadata:
  node_type: memory
  type: project
  originSessionId: 0a67d0e5-b114-477d-a06a-cd45bdc57deb
---

**状态**：Java 侧 + 批 1（原版数据）+ 批 4（Goety-3 hurricane 房间）**已落地并编译通过**（2026-10-09）；**批 5 实测进行中**：别名机制已修好（见下"懒执行血案"），改后编译 + refmap 验证通过，**待进游戏复测刷怪笼是否出现**。
别名机制走 **reload listener** 路线（包裹 `JigsawStructure.CODEC` 的方案进游戏直接崩，见下"⚠️⚠️ 血案"）。

## ⚠️⚠️ 血案 2026-10-09：不要自己 `implements Codec` 去包 1.20.1 的原版 codec

症状：进世界时**所有 jigsaw 结构**（原版古城/村庄/堡垒、Goety、irons、cataclysm、我们自己的… **跨全部 mod**）解析失败，报
`Failed to parse <ns>:worldgen/structure/xxx.json` → `Caused by: Not a JSON object: null`；**非 jigsaw 结构全部正常**（矿井/要塞/海底神殿/末地城/沉船…），这是最关键的判别特征。

排查路线（可复用）：
1. 我在包装器的 `decode` 里加了首帧日志 + `input == null` 时打调用栈 —— 一眼看到 **`input=**null** ops=RegistryOps`，而"原版实为 `MapCodec$MapCodecCodec`"** ⇒ 我拿到的输入就是 null、被包住的对象确实是原版，**所以罪在"我重写了 decode"这件事本身**。
2. 环境只有 Curios + Goety2 + 本模组 ⇒ 逐个 grep mod jar 的 mixin（Goety 只有 `StructureProcessorAccessor`/`StructureTemplateMixin`，不碰 codec；Curios 没有结构 mixin）⇒ 排除第三方。
3. 真因：**本工程编译用的 DFU 比游戏运行的 `datafixerupper@6.0.8` 新**（编译期 `Codec` 有 `decode(DynamicOps, MapLike)` 重载、`DataResult.error(Supplier)`；6.0.8 里 `error` 只认 `String`、没有 MapLike 重载）。**⇒ 自写的 `Codec` 实现与 6.0.8 的 dispatch 调用约定对不上，dispatch 传进来的就是 null。**

**铁律**：
- 1.20.1 里**别包原版 codec**（`@Shadow @Mutable static CODEC` + 自写 `Codec`）。要改行为就用 **`@ModifyArg`/`@Redirect` 打在被调用的构造点上**，或**改用 `MapCodec`/reload listener**。
- 本工程里 **`DataResult.error(...)` 也是雷**（编译期 vs 运行期签名不一致）——要么用两侧都在的写法，要么别用。
- 自写 mixin 的 `decode`/`@Inject` 目标**必须带描述符**：`@ModifyArg(method = "lambda$static$7", …)` 只写名字，AP 报 `Unable to determine descriptor` 且**refmap 里不会有该条目**（生产环境定位失败 → `require = 1` 崩）。写成
  `method = "lambda$static$7(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;"` 后 refmap 才有 `lambda$static$7 => m_227639_`（已实测）。排查/验收：跑 `gradlew compileJava --rerun-tasks` 后看 `build/tmp/compileJava/compileJava-refmap.json`。

## 宝库（vault）展示物品为空 —— 已修（2026-10-09）

症状：所有结构里的宝库都没有物品在转（不只是试炼密室，老结构里的也一样）。
根因：**把 1.21 客户端的判断搬到服务端循环里了**。1.21 的 `VaultBlockEntity.Server.tick` 里刷新展示物品那句**只看 `gameTime % 20 == 0`**（`state == ACTIVE` 是**客户端**决定要不要渲染旋转用的，在别处）；我们的 port 写成了 `shouldCycleDisplayItem(gameTime, state) = gameTime % 20 == 0 && state == ACTIVE`，并且 `cycleDisplayItemFromLootTable` 里又加了一道 `canEjectReward(...)`（含 `state != INACTIVE`）⇒ vault 处于 INACTIVE（附近没玩家）时展示物品被清空 ⇒ "什么都不转"。
修法：两处状态过滤都去掉，回到 1.21 的"每 20 tick 无条件刷新"（loot 表为空时 `getRandomItems` 自然返回空 → EMPTY）。

## 现状（size 与别名）

- **size 7→20**：回到 `@ModifyArg` 打在 `JigsawStructure.lambda$static$7` 里的 `Codec.intRange`（**选择器带描述符**，refmap 已确认有映射）。`AliasAwareJigsawCodec` 里那套"压 size + 记真值"已弃用。
- **pool_aliases（已生效）**：`AliasAwareJigsawCodec` 的 CODEC 包装仍停用（类还在，`wrap()` 没被调用）。实走 Forge `AddReloadListenerEvent` + `PoolAliasReloadListener` 读 `data/*/worldgen/structure/*.json` 的 `pool_aliases`、按 `start_pool` 索引，`findGenerationPoint` 的 `@Redirect` 里建 `PoolAliasLookup` —— 全程不碰 `decode`。

### ⚠️ 懒执行血案（2026-10-09 实测）：别名必须靠**闭包**活到放置阶段，ThreadLocal 作用域包不住

症状：密室能生成、刷怪笼一个都没有，日志全是 `Empty or non-existent pool: goetyominous:trial_chambers/spawner/contents/{ranged,slow_ranged,melee,small_melee}`，而我们的注入日志显示 `readPoolName ...（ThreadLocal **空**）`。

根因：**1.20.1 的 `JigsawPlacement.addPieces` 是懒执行的** —— 公开的那个只造起始块，把真正的 `Placer.tryPlacingChildren → readPoolName` 塞进返回的 `GenerationStub` 的 consumer 里，由 `Structure.generate` 调 `getPiecesBuilder()` 时才跑（反编译实证：`JigsawPlacement.java:102-112`、`Structure.java:107` + record `GenerationStub.getPiecesBuilder()`）。所以"`with(...)` 包住 addPieces 调用"这种作用域写法，到放置时 ThreadLocal 早就 `remove()` 了 → 拿到原样键 → 池不存在 → `continue` 整块拼图跳过。

1.21 的正确姿势（`tools/mappings-1.21.1.txt:119496/119502/119529` 实证）：`PoolAliasLookup` 是 `addPieces` 的**参数**，被那个延迟 lambda（`lambda$addPieces$2(...PoolAliasLookup...)`）**闭包捕获**，一路传到 `readPoolKey`。

修法（已落地）：`JigsawStructureMixin` 的 `@Redirect` 里把 lookup 捕获进包一层的 consumer —— `stub.map(s -> s.generator().left().map(original -> new Structure.GenerationStub(s.position(), builder -> PoolAliasContext.run(lookup, () -> original.accept(builder)))).orElse(s))`。`Structure.GenerationStub` 是 **record**，`position()`/`generator()` 是 public 访问器，不用加 @Accessor；`generator()` 是 `Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder>`，jigsaw 必走 left。`PoolAliasContext` 新增 `run(lookup, Runnable)`（`Supplier` 版要求 lambda 有返回值，void 上下文用不了）。`readPoolName` 的注入点（`PlacerMixin`）不用动。
**通用教训**：1.20.1 里凡是 `GenerationStub`/`GenerationContext` 相关的注入，先确认"这个动作是不是被推迟到 `Structure.generate` 了"。数据侧已核对无误（1.21.1 jar 的 `trial_chambers.json` 别名与我们的逐字段一致；breeze 原版也没别名）。

### 陶罐战利品（2026-10-09 已落地 + 编译通过）

1.21 的陶罐 BE **有** `lootTable`/`lootTableSeed`（mappings 实证，是容器）；1.20.1 的陶罐 BE 只 `extends BlockEntity`（javap 实证，连 `getLootTable` 都没有）⇒ 结构 NBT 里那 4 个装饰罐的 `LootTable: goetyominous:pots/trial_chambers/corridor` 原本是**死数据、罐子敲开是空的**。已补三件：

- `common/blocks/trial/PotLootHolder`：duck 接口，`goetyominous$unpackLoot(ServerLevel, BlockPos, Player)`
- `common/mixin/trial/DecoratedPotLootMixin`（`@Mixin(DecoratedPotBlockEntity)`）：两个 `@Unique` 字段 + `load`/`saveAdditional` 的 TAIL 注入读写 `LootTable`/`LootTableSeed`（**沿用原版容器键名**；`StructureTemplate.placeInWorld:226-231` 对所有带 NBT 的 BE 都会 `load(nbt)`，只给 `RandomizableContainerBlockEntity` 另注 seed）；`unpackLoot` 复刻 `RandomizableContainerBlockEntity.unpackLootTable` 的写法：`level.getServer().getLootData().getLootTable(ResourceLocation)`（default 方法在 `LootDataResolver` 上，**参数是 ResourceLocation 不是 ResourceKey**）+ `LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))` + `withLuck/THIS_ENTITY` + `.create(LootContextParamSets.CHEST)`（**在 `...loot.parameters` 包**）+ `LootTable.getRandomItems(params, seed)` + `Containers.dropItemStack`。
- `common/mixin/trial/PotLootOnRemoveMixin`（**`@Mixin(BlockBehaviour.class)` + `onRemove` 的 HEAD 注入**）：覆盖玩家破坏/爆炸/活塞/`/setblock`/`/fill`/创造；**不覆盖区块卸载**（javap 计数实证：只有 `LevelChunk` 调 `onRemove`，`ChunkAccess`=0、`Level`=0、`WorldGenRegion`=0 ⇒ 世界生成与卸载都不触发）。守卫照抄 `ChestBlock.onRemove`：`state.is(newState.getBlock())` 跳过"同方块只换状态"（斧头敲裂那条路）。
- ⚠️ **不要用 `playerWillDestroy` 版本**（给过一版、文件已按新方案删掉）：它漏爆炸/活塞/setblock，而且和 onRemove 版共存会**双倍掉落**。
- ⚠️ 注入必须 **HEAD**：`BlockBehaviour.onRemove` 的默认体里才做 `level.removeBlockEntity(pos)`（`BlockBehaviour.java:168-171`），HEAD 时 BE 仍可读（`ChestBlock` 同款）。
- 不会刷战利品：`unpackLoot` 先清字段再掉落 ⇒ 掉出来的罐子物品不带 `LootTable` 键，重放无 loot。
- ⚠️ mixins.json 的 `package` 是 `com.qiuyue.goetyominous.common.mixin` ⇒ 子包条目**必须带前缀**（`trial.DecoratedPotLootMixin`）；漏前缀 = 坏引用，启动崩。
- 剩余偏离：① seed 恒 0 ⇒ 每次敲当场随机（`LootContext.java:188-193` 实证 0 = 不落种子；1.21 里罐子会被注入 seed ⇒ 同世界同罐 loot 固定），玩家体感无差；② 1.20.1 没有 `CriteriaTriggers.GENERATE_LOOT`（javap 实证）。

### 盔甲纹饰（2026-10-09 已修）

症状：刷怪笼刷出的怪物盔甲**没有纹饰**（1.21 里是 `minecraft:copper` + `bolt`/`flow`）。
根因：装备表 `loot_tables/equipment/trial_chamber/{chainmail,iron,diamond}.json` 里 `set_nbt` 写的是 `pattern:"minecraft:bolt"/"flow"` —— 这两个图案 **1.21 才有**，1.20.1 的 TRIM_PATTERN 注册表里不存在 ⇒ `ArmorTrim.getTrim`（`ArmorTrim.java:115-122`）codec 解析失败 → `resultOrPartial(LOGGER::error)` → **静默返回空 ⇒ 无纹饰**（装备表其余部分正常，所以盔甲本身还在）。
修法：6 处改成 `goetyominous:bolt`（chainmail ×2）/ `goetyominous:flow`（iron ×2、diamond ×2）。
注册侧本来就是对的（别重做）：`data/goetyominous/trim_pattern/{bolt,flow}.json` 用 1.20.1 字段（`asset_id`/`template_item`/`description`，`TrimPattern` 是 `(assetId, templateItem, description)` 三段）✓、4 张贴图（`assets/goetyominous/textures/trims/models/armor/{bolt,flow}[_leggings].png`）与 1.21.1 **逐字节相同** ✓、`assets/minecraft/atlases/armor_trims.json` 已加我们的纹素 + `copper` 等调色板 ✓、`minecraft:copper` 材质 1.20.1 自带 ✓、lang 有 `trim_pattern.minecraft.bolt/flow` ✓。
⚠️ **`ModItems` 里 `SmithingTemplateItem.createArmorTrimTemplate(new ResourceLocation("bolt"))` 看着像漏了命名空间，但对**：1.20.1 只拿它拼 tooltip 翻译键（`SmithingTemplateItem.java:78-80` → `Util.makeDescriptionId("trim_pattern", id)`）；真正的图案由 smithing_trim 配方经 `TrimPatterns.getFromTemplate`（按 `template_item` 反查 `trim_pattern/*.json`）决定（`SmithingTrimRecipe.java:56`）—— **别改成 `goetyominous:bolt`**，那会把 tooltip 键改成 `trim_pattern.goetyominous.bolt` 而与 lang/数据不一致。

### 有意偏离 & 新增（2026-10-09，用户拍板）

- **待落地（已给代码、用户尚未贴）**：不祥强化个体的**掉落 ×3 + 额外 3~5 钻石、重核保持 1 个** —— 做法：`Hurricane` 覆写 `dropFromLootTable`，`super` 一次 + 再补两轮（每轮过滤掉 `ModBlocks.HEAVY_CORE.get().asItem()`），最后 `spawnAtLocation(new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)))`；LootParams 照 vanilla `LivingEntity.dropFromLootTable:1308-1317` 抄（含 `lastHurtByPlayer` 的 luck，`lastHurtByPlayer` 是 protected、可直接用）。

- **稀有度 ×4（2026-10-10 现值；2026-10-09 曾是 6×）**：`worldgen/structure_set/trial_chambers.json` 的 `spacing 68 / separation 24`（原版 34/12）。**换算公式：密度 ∝ 1/spacing² ⇒ 想要 k× 稀有就把 spacing 与 separation 各 ×√k** —— 4× → ×2 = 68/24（整数无歧义）；6× → ×√6 = 83/29（取整）。推导量级：最近密室中位距离 ~440 格、密室间最小间隔 ~400 格（≈(separation+1)×16；6× 时 ~540 / ~480，原版 ~220 / ~208）。**这不是 1:1，是用户的加料**，别再按原版数值"修"回去；改倍率只动这两个数，且只在**新区块**生效（老世界已生成的密室不变）。
- **飓风房出现率 50%（2026-10-10）**：`worldgen/template_pool/trial_chambers/chambers/end.json` 里 `hurricane_chamber` 元素的 `weight` 1→4（该池 5 项等权时是 1/5=20%，现在 4/8=50%，其余 4 间各 12.5%）。**调率公式 k → k/(4+k)**，只改这一个数。**每座密室最多一间飓风房**（实测证据链：起始池 `chamber/end` 只作 start_pool 用一次、不被任何拼图引用；二选一的 `corridor/end_1`/`end_2` 各只有 **1** 个拼图的 `pool` 指向 `chambers/end`，且这两件只被起始池引用 ⇒ 该池每座密室恰好经过一次）。worldgen 老规矩：只影响新生成区块、不能 `/reload`。
- **不祥强化 = 只加血量 ×2（2026-10-09）**：`WhirlingCageBlock.playerDestroy` 里对 bad_omen/trial_omen 玩家破坏的 cage 所召唤的 Hurricane 加 `MULTIPLY_BASE 1.0` 的 **permanent** `MAX_HEALTH` 修饰符（+ 补 `setHealth(getMaxHealth())`）。**攻击 ×1.5 原本有、因玩家反馈太强已删**（`OMINOUS_ATTACK_ID` 与那段一起删掉了）。⚠️ 两个坑：① 用 `AttributeModifier` 而不是改基值 —— Goety 的 `Owned.readAdditionalSaveData`（`.disasm/goety_src/.../neutral/Owned.java:233-237`）**每次读档都无条件调 `setConfigurableAttributes()`** 把 MAX_HEALTH/ARMOR/ATTACK_DAMAGE 的**基值**按配置重设，改基值的 buff 会被抹掉（实测症状：玩家死亡传送回来血量上限回到 110）；permanent 修饰符随 NBT 存（`AttributeInstance.save():171-183` 只写 `Base` + `permanentModifiers`）且不受基值重设影响 ✓。② 已经刷出来的强化个体，攻击修饰符还存在它自己的 NBT 里，删代码不会回收 ⇒ 测试要打死重刷或去新区域。
- **试炼密室探险家地图（新功能，0 行 Java）**：复用 Goety2 的 `goety:ritual` + `ritual_type: goety:locate`（占卜 divination 仪式，激活物 = `minecraft:map`）。三件套：
  - `data/goetyominous/recipes/locate/trial_chambers.json`（字段顺序照 Goety2 自己的 `data/goety/recipes/locate/*.json`；1.20.1 要 `"result": {"item": ...}` 而不是 1.21 的 `"id"`；铜块用 **`forge:storage_blocks/copper`**（1.21 的 `c:` 标签在 1.20.1 不存在））
  - `data/goetyominous/tags/worldgen/structure/explorer_maps/trial_chambers.json` → `["goetyominous:trial_chambers"]`（配方 `structure_to_locate.tag` 指向它；Goety2 的 `RitualRecipe.java:236-240` 只读 `tag` + `display_name` 两个字段）
  - lang 的 `filled_map.goetyominous.trial_chambers`（= 地图物品名，Goety 惯例 `filled_map.<ns>.<name>`）
  - 参考：Goety-3 `data/goety/recipe/locate/trial_chambers.json` + `tags/.../explorer_maps/trial_chambers.json`（它写 `minecraft:trial_chambers`，我们写自己的）。

### 陶片/装饰罐（2026-10-09 核对结论：1:1 无误，不是 bug）

装饰罐链六环全过：NBT `sherds` 已是 `goetyominous:*_pottery_sherd`（**1.20.1 要物品 id，不是图案 id**）✓ 物品 ✓ 图案注册 ✓ `DecoratedPotPatternsMixin` 打在 `getResourceKey`（refmap `m_271696_`）✓ 三张图案贴图与 1.21.1 **逐字节相同** ✓ `decor.json` 权重与 1.21.1 一致、168 模板拼图接口零差异 ✓。**"密室里罐子都没陶片"是原版分布**：decor 池 45 权重里空 22、undecorated_pot 10、flow/guster/scrape 各 **1** ⇒ 每个陶片罐只 2.2%，生成出来的罐子里 5 个才有 1 个带陶片。确定性验证用 `/place template goetyominous:trial_chambers/decor/flow_pot ~ ~ ~` + `/data get block`。
- 数据侧没动的还有：`#` 前缀标签漏改已修（`#goetyominous:has_structure/trial_chambers`）、170 模板往返校验 170/170、hurricane 追加为 `chambers/end` 第 5 项。

## 一、范围与决策（已拍板）

1.21.1 原版试炼密室 + Goety-3 的 hurricane 房间（**追加为 chambers/end 第 5 项**，不替换原版 4 间）。
别名走 **(β) 1:1 复刻机制**（不是并集池）。命名空间统一 `goetyominous:`。4 个 `spawner/contents/{melee,ranged,slow_ranged,small_melee}` 别名池**刻意不建文件**——1.21 里它们也不是文件，靠别名机制在注册表解析前换键。

## 二、Java 侧（`common/mixin/trial/` + `common/worldgen/alias/`）

- **`TerrainAdjustmentMixin`**：1.20.1 只有 4 个常量（无 `encapsulate`）→ 补第 5 个。⚠️ **禁止反射建枚举**（`Constructor.newInstance` 抛 "Cannot reflectively create enum objects"，`MethodHandles` 同样被拒）→ 必须 `@Invoker("<init>")`（生成 `new` 字节码）。改 `values()` 而不是 `$VALUES`：`StringRepresentable.fromEnum` 是**急求值**，`values()` 在 `<clinit>` 中途就被调用。
- **`JigsawStructureMixin`**：① `CODEC` 包一层（静态块 append 到目标 `<clinit>` 末尾已实证；`StructureType.JIGSAW` 读 `JigsawStructure.CODEC` 时必然先完成 clinit）② size 上限 7→20 **不能**用 `@ModifyArg` 打在 `lambda$static$7` 上（见下面的 AP 坑）→ 改为在包装器里把交给原版的 size 压到 7、真值记表，在 `findGenerationPoint` 的 addPieces @Redirect 里换回，>20 直接报错（合法区间与 1.21 的 `Codec.intRange(0,20)` 一致）③ `verifyRange` 自己复刻（原版 switch 表里没有 encapsulate，走 tableswitch default 会抛/AIOOBE）④ addPieces @Redirect 里建别名查找表放进 ThreadLocal。
- **`JigsawPlacementPlacerMixin`**：注入 **`readPoolName` 的 RETURN**（一处顶两处调用点，位置恰等于 1.21 的 `Placer.resolvePool`）——⚠️ 不要对 `lambda$tryPlacingChildren$4` 单独 @Redirect（原因同下）。
- **别名机制**（`alias/` 8 个类）：`PoolAliasBinding`(direct/random/random_group) + `PoolAliasLookup`（`RandomSource.create(seed).forkPositional().at(pos)`，seed=世界种子、pos=结构起点 ← "一室一种怪"的实现）+ `PoolAliasContext`(ThreadLocal) + `AliasAwareJigsawCodec`（读 pool_aliases + 记真实 size）。类型派发用 id→codec 表（JSON 里的 `minecraft:direct/random/random_group` 原样 ✓，注意 RandomGroup.CODEC 与 bindings() 的静态初始化环要用 Supplier 断）。

### ⚠️ Mixin 注解处理器看不见合成方法（本次最大坑）

AP 的 `TypeHandle.findMethod` **找不到 lambda 方法**（`lambda$static$7`、`lambda$tryPlacingChildren$4`）→ 无论选择器写名字还是补描述符，都会持续打印
`Cannot find target method ...` / `Unable to determine descriptor for @ModifyArg target method`。
**这类消息是警告不是错误**（Gradle 照样 BUILD SUCCESSFUL，IDE 却渲染成报错），而且**实测 refmap 里的映射照样导出了**（`m_227639_` / `m_254828_` 都在）→ 能用，但依赖"AP 对没解析成功的目标也导出映射"这一未文档化行为，换个 AP 版本就可能变静默失效。
**做法**：注入点一律选**普通方法**。排查时用
`./gradlew compileJava --rerun-tasks --console=plain 2>&1 | grep -a "Cannot find target\|Unable to locate method mapping"`，输出为空才算干净（只 BUILD SUCCESSFUL 会被 up-to-date 骗）。
另外 `@Redirect` 要求**处理器静态性与目标方法严格一致**（`Injector.checkTargetModifiers`，exactMatch）：`tryPlacingChildren` 是实例、`lambda$tryPlacingChildren$4` 是实例（捕获 this.pools）、`lambda$static$7` 是静态。

### 验证结论（离线可复现的部分全过了）

- 4 个原版 beard 模式 + encapsulate：**我的复刻 vs 1.21.1 原版 `dyl`，5 模式各 160 万采样点逐位一致**（`E:\tmp\v3` 的 Harness，需 jdk-22）。
- 别名：RNG 种子链 6400 点 0 差异；加权掷选 20000 组（含全零权重）**逐字节一致**；`pool_aliases` 真实 JSON 解析 + 掷选语义（4 别名都有解、ranged/slow_ranged 成对、随 pos 变化）✓。
- 1.20.1 侧仍有**两处无法离线验证**：`@Invoker`/`@Redirect`/`@Shadow CODEC` 的**运行时**行为；`encapsulate` + size>7 的真实解析。都带 `require = 1`，落空会硬报错。

## 三、批 1 数据（已落盘）

- **脚本**：`tools/port_trial_chambers_nbt.py`（`--scan` 只扫描；`.gitignore` 已放行 `/tools/*.py`；报告 `tools/port_trial_chambers_report.txt`）。
  设计：自写最小 NBT 编解码**整树重序列化**；显式改名表 + `trial_chamber` 路径规则，其余保留；**反查校验**——改名目标必须在模组 assets 里存在、保留项必须在 **1.20.1 原版 jar** 的 blockstates/item 模型里存在（抓"1.21 新增却漏改"）。
- **产出**：170 NBT → `data/goetyominous/**structures**/trial_chambers/`（⚠️ 1.20.1 是复数 structures；1.21 源是单数 structure）+ 45 池 + structure/structure_set/processor/群系 tag + **9 张新战利品表**（原以为 7 张）。
- **踩坑**：① 落盘 JSON 时曾用 basename **把池的目录层级拍平**（模板池路径就是它的 id，绝不可拍平）→ 已修，落盘后务必交叉校验"模板引用的池 ⊆ 已落盘池 ∪ 4 别名"。② **`rm -rf` 整个 worldgen/{structure,structure_set,processor_list} 目录删掉了模组原有 5 套结构的 json**（17 个文件）→ `git checkout HEAD --` 还原。清理时只删自己新建的文件。③ 加过 `--force-loot` 开关，一跑就把 14 张**手工做过格式适配**的战利品表冲掉 → 已删除该开关（脚本默认"已存在即跳过"）。
- **1.21→1.20.1 战利品表格式差异**（手工移植的 6 张 `equipment/*` 早已适配；新写的 9 张是 plain chest 表，只有 2 张踩到）：`minecraft:enchant_randomly` 的 `"options": "#minecraft:on_random_loot"`（1.20.5+）→ 展开成 `"enchantments": [...]`；展开时**锤子系附魔指向模组自己的 `goetyominous:density`/`breach`**（1.20.1 没有 `non_treasure` 附魔标签）。脚本里已有 `ON_RANDOM_LOOT` 列表 + `FORBIDDEN_LOOT_KEYS` 断言。
- **NBT 里的拼图接口名**（jigsaw 方块的 `name`/`target`，如 `minecraft:decor`/`quadrant`/`ominous_vault`）**不是注册表条目**，绝不能改名、也不能当"未白名单"报错；`blocks[*]/nbt/id` 里的 `minecraft:bed` 之类是残留字段，别管。
- ⚠️ 结构 NBT 里的 `trial_spawner`/`vault` 方块实体 id **要跟着改成 `goetyominous:`**（得与方块注册名一致），`breeze`/`bogged` 也一样；`minecraft:jigsaw`/`structure_block`/`tuff`/`copper_block`/`waxed_*cut_copper*` 这些 1.20.1 已有的保持原样。
- `.gitattributes` 加了 `*.nbt binary`（gzip 二进制，绝不能走换行转换）。

## 四、批 4（Goety-3 hurricane 房间，已落地）

Goety-3 的做法是**整池替换** `chambers/end`（把原版 4 间挪进它自己的 `end_fallback` 当兜底）；按拍板改成**追加**：我们的 `chambers/end` = 原版 4 间 + `goetyominous:trial_chambers/hurricane_chamber`（权重各 1，即 1/5 概率），fallback 保持原版的 `hallway/fallback`，**不需要** Goety-3 的 `end_fallback`。
- 源：`Goety-3-main/src/main/resources/data/goety/structure/trial_chambers/hurricane_chamber.nbt`（gzip）。脚本里已内建这一段（`GOETY3_HURRICANE` + 往 end 池追加 `HURRICANE_ELEMENT`），重跑脚本不会丢追加内容。
- ⚠️ **`goety:whirling_cage` → `goetyominous:whirling_cage`**：Whirling Cage 是 Windy 更新里**已移植进本模组**的方块，Goety 2 前置里没有（在 `.disasm/goety_src` 资源里核实过）；而 `goety:potted_windswept_sapling` 是 Goety 2 的，**保持不动**。脚本里有 `OTHER_NS_RENAME` 表 + 对保留的 `goety:` 引用做存在性核对。
- ⚠️ **自写 NBT 编解码的标量类型退化（严重，已修）**：Python 只有 int/float，我最初把 byte/short/int/long 一律解成 `int`、float/double 一律解成 `float` → 写回去时 byte/short/long 全变 TAG_Int、float 全变 TAG_Double（**NBT 读取端严格按类型取字段，类型错了字段会被静默丢弃**）。原版 170 个模板里有 `Float×50 / Byte×10 / Long×1` 共 61 个这种字段。修法：加 `Byte/Short/Int/Long/Float32` 包装类，**并加一道往返一致性校验**（解码→编码→再解码，`typed_repr` 必须逐类型相同，现在 170/170 + hurricane 1/1 全过）。教训与"别用坏工具自己验证自己"同源——第一版的"NBT 合法 ✓"就是用自己的 reader 读回来的，掩盖了类型损坏。

## 五、剩余

- **批 5**：进游戏实测（`/locate structure goetyominous:trial_chambers`、`/place structure`、刷怪笼/宝库/不祥链/装备/陶片/别名一室一种怪；还有那 4 个 mixin 的运行时首测）。
- 有意偏离清单：① 未实现 `PoolAliasBindings.registerTargetsAsPools`（目标池不存在的畸形数据里，1.21 放空池、我们打警告跳过）；② hurricane 房间按追加处理（Goety-3 原版是替换，见上）。

相关：[[trial-chamber-structure-nbt-refs]] [[trial-spawner-port-plan]] [[key-of-rlyeh-tooltip-port]] [[cuotiben-tooling]] [[mc-1211-asset-extraction]]
