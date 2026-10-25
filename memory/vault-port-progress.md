---
name: vault-port-progress
description: 宝库（1.21.1 Vault）移植进度：已交付件、待办件、编译阻塞现状与参考件路径
metadata: 
  node_type: memory
  type: project
  originSessionId: 7533bf00-ff71-4c78-8f0b-44374dd78647
---

**已交付**（2026-10-07 起）：
- 骨架：`common/blocks/trial/{VaultBlockEntity,VaultState,VaultSharedData,VaultServerData,VaultClientData}.java` + `config/VaultConfig.java`（曾有两坑已修：`CODEC` 改 public；`.validate` 换 `flatXmap`，见 [[mc-1201-vanilla-data-source]]）
- `config/PlayerDetector.java`：从 1.21.1 `trialspawner.PlayerDetector` 1:1 移植（之前整类缺失，是编译根阻塞；含 SHEEP 彩蛋常量）
- 资源：blockstates/8 个 block 模型/item 模型/`particles/vault_connection.json`/粒子贴图/lang/SoundType 音效键
- 战利品表（方案 A，8 张，1:1 结构）：`data/goetyominous/loot_tables/chests/trial_chambers/{reward,reward_common,reward_rare,reward_unique,reward_ominous,reward_ominous_common,reward_ominous_rare,reward_ominous_unique}.json`；引用条目字段用 1.20.1 的 `"name"`；9 处 enchant_with_levels 用自造 `goetyominous:enchant_with_levels` + `options:"#goetyominous:on_random_loot"`
- `data/goetyominous/tags/enchantment/on_random_loot.json`（枚举 38 项 = 1.21 on_random_loot 逐项对应：non_treasure + 双 curse + frost_walker + mending；1.21 的 sweeping_edge → 1.20.1 的 `minecraft:sweeping`）
- `models/item/{trial_key,ominous_trial_key}.json` + 两张 1.21.1 官方贴图 + lang 两行

**待办（方案 1-15 Java 侧，用户自写中）**：~~全部完成~~ → **2026-10-07 已全部落地并编译通过（BUILD SUCCESSFUL）**：
- 用户侧的 VaultBlock/VaultRenderer 按方案实现；`FlyTowardsPositionParticle`(140 行)/`ModLootFunctions`(27 行) 均已实现
- 两个 loot function **落在 `com.qiuyue.goetyominous.utils`**（不是方案的 common/loot）：`EnchantPoolHelper` / `EnchantWithLevelsFunction` / `SetOminousBottleAmplifierFunction`；JSON 契约已核对一致（`levels`/`options`/`amplifier`、tag `goetyominous:on_random_loot`、`BuiltInRegistries.ENCHANTMENT.getOrCreateTag`）
- 接线全通：ModBlockEntities.VAULT / TrialBlocks.VAULT / TrialSoundTypes.VAULT / ModItems 两把钥匙 / ClientEvents（BER+粒子）/ GoetyOminous(ModLootFunctions) / TrialCreativeTabs
- `VaultBlockEntity.emitConnectionParticlesForPlayer` 已修正（位置=keyhole、速度=offsetRandom）✓

**2026-10-07 收尾（均已落盘、编译通过）**：
- 新增"不祥宝库"物品：`common/items/OminousVaultBlockItem`（`placeBlock` 里置 `OMINOUS=true`；`getDescriptionId` 覆盖为 `item.goetyominous.ominous_vault`，否则会沿用方块的"宝库"）+ `models/item/ominous_vault.json`（parent `block/vault_ominous`）+ TrialBlocks 注册 `OMINOUS_VAULT_ITEM` + TrialCreativeTabs 跟随普通宝库 + lang 两行。
- 两处毫厘差已修：`UUID_SET` 改 `Sets::newLinkedHashSet`（保 FIFO 淘汰顺序）；新增 `VaultServerData.lenientOptionalFieldOf`（1.21 DFU 的 lenientOptionalFieldOf 在 1.20.1 DFU 6.0.8 **不存在**，用 `MapCodec.mapResult` + `ResultFunction` 匿名类等价实现，编码仍走原 optionalFieldOf），ServerData 4 处 + SharedData 3 处已替换。
- `VaultConfig` 的 4 个 optional 字段仍是 `optionalFieldOf`（vanilla 是 lenient；未动，要动需把 helper 提成 public 或复制）。

**2026-10-07 实测三处修复（已落盘、编译通过）**：
- 全黑不透明 → `models/block/template_vault.json` 补 `"render_type": "minecraft:cutout"`（8 个变体全 parent 它 ✓；irons 实证 1.20.1 走模型 JSON 而非 setRenderLayer，见 [[cuotiben-tooling]] 第 31 条）
- 不祥宝库物品（同方块双 BlockItem 打乱 `asItem()`，见第 30 条）→ **已删除该物品**：`OminousVaultBlockItem.java`、`models/item/ominous_vault.json`、注册项、创造页项、lang 行全清；宝库现在与铜块一致，只在原版建筑方块页签
- 不祥宝库改为指令放置：`/setblock ~ ~ ~ goetyominous:vault[ominous=true]`（facing/vault_state 走默认）

**2026-10-08 动画接线（已落盘、编译通过）**：1.21 的宝库动画事件（`ANIMATION_VAULT_ACTIVATE/DEACTIVATE/EJECT_ITEM` = **3015/3016/3017**，处理点在 **`LevelRenderer.levelEvent`**）= 1.20.1 客户端不识别的 id（实证空闲）→ 新建 `common/mixin/VaultLevelRendererMixin`（同 `TrialOmenLevelRendererMixin` 同款写法：`@Inject` HEAD + `@Shadow ClientLevel level` + `require=1`）承接三者；`VaultState` 的 INACTIVE/ACTIVE onEnter 已把 playSound 换成 `level.levelEvent(3016/3015, pos, ominous?1:0)`（= vanilla 原句，音效改由客户端 local 播、带随机音高）。3017 的粒子内联了 `TrialSpawner.b` 逻辑（第四批复用，见 [[trial-spawner-port-plan]]）。

**2026-10-09 插钥匙相关修复（已落盘 + 编译通过）**：
- ⚠️ **`VaultConfig.CODEC` 的 `key_item` 用 1.20.1 的 `ItemStack.CODEC` 会整段解析失败**：1.20.1 的 `ItemStack.CODEC`（`ItemStack.java:114`）= `id` + **`Count`（大写、必填、无默认值）** + 可选 `tag`；而结构 NBT 是从 1.21 搬的 `{count:1,id:"goetyominous:trial_key"}`（小写、组件式）⇒ 必失败 ⇒ `VaultBlockEntity.load` 只在日志记一条 error，然后**保留构造函数默认配置**（`defaultFor(ominous)`）⇒ 宝库的 `key_item`/`loot_table` 事实上从不从数据读。修法：`VaultConfig` 新增 `KEY_ITEM_CODEC`（`{id, count?}`，`count` 缺省 1）替换第 25 行的 `ItemStack.CODEC`。用 1.20.1 真类 + 真 NBT 实测：修前 `Optional.empty`、修后 parse/encode 均正常。
  - `VaultSharedData.display_item` / `VaultServerData.items_to_eject` 仍用 `ItemStack.CODEC`（运行时自写自读、`Count` 往返一致）✓ 未动。
  - 同类陷阱：1.20.1 的 `Registry.byNameCodec()`（`DefaultedRegistry`）对**未注册的 id 静默回退默认值（air）**，不报错 —— 排查"物品变空"时要想到这条。
- **插钥匙的闸门**（`VaultBlock.use:47`）：`stack.isEmpty() || state != ACTIVE` → 直接 `PASS`（**完全没反应、没声音**）。所以"插不进"先分清三种：①状态不是 ACTIVE（无反馈）②钥匙不匹配（有 fail 音）③该玩家已领过（reject 音）。**普通宝库要 `trial_key`、不祥宝库（`ominous=true`）要 `ominous_trial_key`**；槽位上 `reward/all`（普通）远多于 `reward/ominous_vault`。
- 宝库/奖励链全量核对通过：模板池 45/45、模板 171/171（187 条引用零悬空、零孤立）、chest 表 13/13、146 张战利品表零个 1.21 的 `value` 字段（都用 1.20.1 的 `name` ✓）、reward 子表物品齐全无异常条件。

**仅剩运行期验证**：启动无 datapack 红字、8 表+tag 加载、粒子轨迹、插钥匙喷物、凶兆 amplifier 2–4、附魔池能出 mending/frost_walker 且不出 swift_sneak/soul_speed/wind_burst、镂空渲染正确、激活/失活/喷出三簇粒子与随机音高；外加插钥匙三探针：`/execute if block <vault> goetyominous:vault[vault_state=active]`、`[ominous=true]`、`/data get block <vault> config`。

参考件：`.disasm/vault_src/{dou,dsk,ghu,gca}.java`、`.disasm/vault2_src/{dsl,dsm,dsn,dso,dse}.java`、`.disasm/itemer_src/glf.java`、`.disasm/particle_src/gcn.java`；irons_spellbooks 1.20.1 有整套 `api/backwards_compat/blocks/vault/` 先例（其 Renderer 与我们的内联方案同构；其连接粒子是空实现）。

相关：[[mc-1201-vanilla-data-source]] [[port-data-fidelity-rule]] [[trial-omen-captain-drop-findings]]
