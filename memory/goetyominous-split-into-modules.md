---
name: goetyominous-split-into-modules
description: 把第三方派生内容拆成独立 jar 的方案决策：7 个模块合成 5 个 jar（ac+am 一个、ua+sar 一个，各自一个 modId；lm/mm/of 各一个），ias 留守，命名空间保持 goetyominous，core 做 Gradle 子项目，单仓库出多 jar
metadata: 
  node_type: memory
  type: project
  originSessionId: d32db0f4-b36c-414f-bc45-874cd71b4a4c
  modified: 2026-10-19T17:59:39.348Z
---

2026-10-07 评估并决定：goetyominous（1318 文件，其中 711 是第三方派生，约 54%）把联动内容切成独立 jar。

**决策：**
- **不拆**：只剩 `ias`(61 文件) 留在主 mod（**2026-10-08 用户定案：sar 改拆**）。连带省掉：`ModEntityTypes`、`GoetyOminousType` 的 ias 分支、`TwittollagerPhoneLayer` 全不用动。
- **要拆 7 个**（2026-10-08 定案）：`ac`(239) `am`(92) `lm`(187) `mm`(82) `of`(82) `sar`(32) `ua`(20)，合计约 714 个 java 文件（各 modid 目录实际数）。
- **命名空间保持 `goetyominous:`** —— 新 jar 各自 modId 不同（`ominous-ac` 等）但共用注册命名空间。理由：存档兼容 + 免去数千处 RL 改写。仓库已有先例（往 `data/alexscaves/`、`assets/goety/` 塞文件）。
- **一个 GitHub 仓库 + Gradle 多模块**：`core/`（`git mv src core/src`）放共享基座 = utils/config/ai/events/network/主类 + 自研实体(76) + sar/ias；6 个模块 `implementation project(':core')`。

**必须改的 7 个枢纽**（引用多模块，要改成扩展点）：`ModModelLayers`(8 模块) `GoetyOminous`(8) `ModCreativeTab`(8) `TesseractTagHandler`(7) `ClientEvents`(ac/lm/mm) `ModNetwork`(ac/am/mm) `ModEventHandler`(ac/sar/ias)。外加 1 个核心跨界：`compat/curios/CuriosIntegration`（注册 charm/back 槽位 + DARK_ANKH + FUNGUS_PACK，只有 1 行碰 `AcItems.RAYCAT_AMULET`）——**留 core**，那一行改扩展点。
**归位清单（2026-10-07 已完成）**：主 mod 通用桶里 **81 个**文件实际属于某模块，已按 `<桶>/<modid>/` 搬完，`gradlew build` 通过、jar 内路径已核。
**模块间边只有 1 条**：`OfItems → ac`。

**归位是传递闭包，必须迭代到不动点** —— 这条是踩出来的，不是推出来的。分类依据是「引用本仓库模块目录里的类」，但**错位文件本身不在模块目录里**，所以「引用另一个错位文件」的类第一遍完全隐形。首轮只找到 73 个；把 73 搬走后再跑一遍，又冒出 8 个（`RenderIceShard` 等 am 渲染器、`DicerServantLaserSound` 等 of 音效、`RadiationAllyEvents`、`NucleeperExplosionZonePacket`）——它们引用的 `NucleeperNukeProtectionHandler` 之类**本身就在首轮搬运清单里**。合计 81。
`ModNetwork` 就是这么浮出来的第 7 个枢纽：首轮它引用的包全在同包内（无 import），看不见；包一分出去才暴露。
**新增成本**：`:core` 子项目不做则基座分叉；发布 = 7 个包、2⁶=64 种组合要写支持矩阵。

**2026-10-08 复核修正（拆分仍未开工，settings.gradle 还是单模块）：**
- **枢纽是 9 个不是 7 个**。漏掉的 `client/init/ModEntityLayers.java` 把 6 个模块的模型层平铺在一个类里（ias 13 / lm 20 / of 12 / sar 9 / mm 7 / ua 3）。两个脚本都看不见它——里面全是 `new ResourceLocation("mutantmore", "mutant_wither_skeleton")` 这类字符串，没有任何类引用。
- **第 9 个联动项 `goety_revelation`（`com.mega.revelationfix`）完全没有 `<modid>/` 隔离**：`common/mixin/abyss/*`(14 个 mixin)、`common/rlyeh/`、`client/rlyeh/`、`utils/KeyOfRlyehMixinHelper`、`compat/mod/GoetyRevelationCompat`，外加 `common/items/ac/KeyOfRlyeh*` 挂在 **ac 桶里**。`KeyOfRlyehStaffItem extends SecondPhaseStaff` 是硬编译依赖，靠 `AcItems` 里的 lazy holder + `isLoaded()` 门控避免无 mod 时 classload。编译用的是 `libs/[Forge]RevelationFix-*.jar`，而 `libs/` 已 gitignore。
- **资源侧没有按 modid 隔离**：`assets/goetyominous`(1481)+`data/goetyominous`(474) 全平铺，只有 `textures/entity/ac` 一个 modid 目录。按类名 snake_case 猜路径只能归因 316/2657。真正的按 modid 隔离在**别处**：`resources/resourcepacks/<modid>_compat/`（7 个，ac 119/am 154/mm 71/of 70/sar 26/ias 19/ua 18，**缺 lm**），由 `BuiltinPacksRegistry` 按 `isXLoaded()` 注册成内置条件包——拆 jar 后这层门控变冗余，是拆分顺带能简化的一处。拆资源必须走「注册名 → 模块」而非「类名 → 模块」。
- **脚本盲区**：`_verify.mjs`/`_namesweep.mjs`/`_verify2.mjs` 都靠「简单类名 → 所属模块」映射，跨包同名会假阳性。实例：`client/render/ReturnedRenderer.java` 被判成 sar，实际用的是 Goety 自己的 `VillagerArmorModel`（sar 也有同名类）。断言前必须读文件核。
- 三个模块专属文件仍留在通用桶：`common/events/LmAnnihilationBombGuardEvents.java`(lm)、`common/mixin/MixinOssuaryBlockEntity.java`(sar)、`common/mixin/MutantMoreItemMixin.java`(mm)。
- 约定偏差（不影响脚本判定，按路径段匹配仍认）：`common/entities/ally/mobs/mm/`（mm 埋在自研实体通用桶里还带 goals/ 子树）、`client/ac` `client/am` `client/mm` 是一级包、`client/render/curios/ac/`。
- ~~判据冲突~~ **已定案**：用户 2026-10-08 说"我以后只会拆 ua sar of am ac mm lm" → 7 个，`ias` 是唯一留守的。**sar 进拆分只新增 2 处工作**（不是 4 处）：
  - `common/mixin/MixinOssuaryBlockEntity.java` 是**混血**：两个 `@Inject` 分别在 `setVariant` 上——一个给 sar 的 SkeletonVillager 类型，一个给 core 自己的 `BOGGED_SERVANT`。不能整体搬进 sar jar，要把 sar 那半劈出去。
  - `GoetyOminousType` 的 sar 分支要变扩展点（`SarBlocks` 的 SPORE_BOMB/BLAST_PROOF_PLATE/GLOOMY_TILES/CHISELED_GLOOMY_TILES + `SarEntityRegistry` 的 GRIEFER/EXECUTIONER/TRICKSTER 三处）；**ias 分支原地留**（它留守）。
  - `TwittollagerPhoneLayer`、`ModEntityTypes` 因为 ias 留守，仍然不用动。

**2026-10-08 归位收尾（用户："我不拆 只是提前准备"）：**
- 搬完 2 个：`common/events/LmAnnihilationBombGuardEvents.java` → `common/events/lm/`；`common/mixin/MutantMoreItemMixin.java` → `common/mixin/mm/`（同时改 `goetyominous.mixins.json` 条目为 `"mm.MutantMoreItemMixin"`，编译期 refmap 已核出新路径、旧路径 0 残留）。`compileJava` 通过。
- `_verify.mjs`/`_domove.mjs`/`_namesweep.mjs` 的 `SPLIT` 已加 `'sar'`；`_verify.mjs`+`_domove.mjs` 的 `STAY` 加了 `common/mixin/MixinOssuaryBlockEntity.java`（混血，实测删掉它就会报这 1 条残留，是承重的）。
- **判据本身修了一次**：原来靠「简单类名 → owner 映射」，加 `sar` 后立刻误报 3 个 core 文件（`ReturnedRenderer` 用 Goety 的 `VillagerArmorModel`；两个 Disciple 用 `com.Polarice3.Goety...SkeletonVillagerServant`），`_domove` 还真的打算把它们搬走。已改成 **import 感知**：只认「单类 import / 通配 import / FQN 字面量（含 `Class.forName` 的字符串）」，三个脚本里 `refs(src)` 那段是同一份逻辑，改一处记得同步另一处。
- **还剩两个方向的盲区，`_verify` 结构上抓不到**，补了两个脚本：
  - `_prepcheck.mjs`：抓「字符串型引用」——`ModEntityLayers` 那种只有 `new ResourceLocation("mutantmore", ...)`、没有任何类引用的枢纽。
  - `_reverseuse.mjs`：抓**反向**归属——核心类只被单个模块引用（`_verify` 只看「core→模块」方向，漏掉「模块→core」的叶子文件）。
- **新查出的真残留（未搬，待定）**：`client/render/projectile/GiantHellBlastRenderer.java`（注册给 `MmEntityRegistry.GIANT_HELL_BLAST`，是 mm 的）、`client/ForsakenRiderInput.java`（只被 `ally/ac/ForsakenServant` 用，是 ac 的）。`_reverseuse.mjs` 另给出 13 个待人工判的候选（`LicowitchServantHex`/`LicowitchServantPeppermint`/`TremorzillaBreathLoopSoundHandler`/`PureDarkVoid`/`ServantMagmaData`/`IRayMuzzle`/`MountCameraSmoothing` 像 ac，`SmeltHelper`/`ContinuousControllerSpell`/`CreateBlazeBurnerCompat` 像 mm，`MagispellerServantAnimation`/`MysteriousContract`/`TwittollagerPhoneLayer` 是 ias）。

**2026-10-08 第二轮：又搬 17 个（ac 12 / of 4 / mm 1），compileJava 通过，`_verify` 仍 0 残留 / 8 枢纽。**
搬走：`client/ac/ForsakenRiderInput`、`client/sound/ac/TremorzillaBreathLoopSound{,Handler}`、`client/sound/of/VoltServantElectricChargeSound{,Handler}`、`common/events/ac/TremorzillaBreathHandler`+`XRayHandler`、`common/network/ac/TremorzillaBreathPacket`+`XRayPacket`、`common/entities/projectile/ac/LicowitchServantHex`+`Peppermint`、`.../of/VoltServantElectricCharge`+`ElectricExplosion`、`common/entities/util/ac/PureDarkVoid`+`ServantMagmaData`、`compat/ac/GoetyOminousPatchouliIntegration`（原 `compat/patchouli/`）、`client/render/projectile/mm/GiantHellBlastRenderer`。
搬运脚本：**`.scratch/_domove19.mjs`**（显式文件表 + `--dry` 预览；复用 `_domove` 的 package/FQN/补 import 三段改写）。搬后补 import 只有 3 处：`ModModelLayers`(GiantHellBlastRenderer)、`AcEntityRegistry`(Licowitch×2)、`ModNetwork`(TremorzillaBreathPacket+XRayPacket)——因为这三处原来靠 `import <桶>.*` 通配，目标子包不在通配范围内。

**新判据（比之前那些都硬）：`.scratch/_extcore.mjs` —— 核心文件直接 `import` 第三方 mod 自己的包**（`com.github.alexmodguy.alexscaves.*` 等）。之前所有脚本查的都是「核心 → 本仓库模块目录的类」，那个只在**拆 jar** 时碍事；这一条是**连 core.jar 都还得挂着第三方 mod**，才是真卡点。首轮 10 个核心文件中招（ac 7 / of 3），搬完只剩 1 个：**`utils/KeyOfRlyehMixinHelper.java`（硬 import alexscaves ×3）**——它是 **ac × RevelationFix 交叉件**：`applyDeepOneReputation()` 是 ac 侧（KeyOfRlyehItem/StaffItem，都在 ac 桶），`isKeyOfRlyeh()` 被 ac 法术 + `common/mixin/abyss/*`(14 个 mixin) + 核心的 `CataclysmKeySpellHandler` 用。注意 `KeyOfRlyehStaffItem` 本身也硬 extends RevelationFix 的 `SecondPhaseStaff`。
⚠️ **`common/mixin/abyss/*` 不是 RevelationFix 的**（我一度归错）：它们 `@Mixin` 进的是 **Goety 自己的深渊学派法术**（`com.Polarice3.Goety.common.magic.spells.abyss.BioMineSpell` 等 14 个），全部 `remap = false`，一个第三方类都不 import。
判据表要注意：`com.unusualmodding.opposing_force` = **of**，`com.alexander.mutantmore` = **mm**（写反过一次，全表假阳性）。

**`.scratch/_privacy.mjs`：最大不动点，连"核心内部成团"的也抓得到**（`_reverseuse.mjs` 只能抓单文件，团里的会被漏）。两个坑：①**悲观迭代遇互引用会死锁**（`TremorzillaBreathHandler` ↔ `TremorzillaBreathPacket` 谁也判不出）→ 必须改成"候选集从全集开始删"的最大不动点。②**必须先把枢纽排除**，否则枢纽（`ModModelLayers`）被吸进某模块私有集后把它引用的 116 个核心文件全拖进来。结果从 20 → 搬完剩 9：ac 2（`MountCameraSmoothing`/`IRayMuzzle`）、ias 4、mm 3。

**`.scratch/_premove.mjs`：搬前风险审计**（引用者全列 / 同包私有成员访问 / 反射字符串 `"FQN"` / resources 里的引用 / 目标已存在）。这批跑下来 0 硬冲突、0 反射、0 资源引用。
同类工具：`_modedge.mjs`（跨模块边）、`_reverseuse.mjs`、`_reverseuse2.mjs`（零核心用户分组）、`_coreclusters.mjs`（连通块，**太粗**——553 个核心文件连成 1 个 524 的大团，没用，别再用）、`_audit.mjs`、`_inbucket.mjs`、`_resattr.mjs`。

**明确留 core 的（名字/归属像模块但语义是通用能力）：** `MountCameraSmoothing`（纯原版相机推近，`ViewportEvent`）、`IRayMuzzle`（1 方法接口）、`ContinuousControllerSpell`（通用法术框架基类）、`SmeltHelper`（纯原版熔炉配方查询）、`CreateBlazeBurnerCompat`（是 **Create** 的反射垫片，不是 mm 的）。ias 那 4 个不管（ias 留守）。

**顺手印证：** `GoetyOminous.java` 的 `if (AlexCavesCompat.isAlexCavesLoaded())` 块里其余 12 个 handler 全是 `common.events.ac.*`，只有 `TremorzillaBreathHandler` 写成 `common.events.*`——搬完这个块变整齐了。`GoetyOminousPatchouliIntegration.setup` 也被 `isAlexCavesLoaded() && PatchouliCompat.isLoaded()` 门控。
**行尾坑：** 脚本补 import 时用 `split('\n')/join('\n')`，插进去的行会是 LF 而文件其余是 CRLF（3 个文件变混合行尾），要事后按主导行尾补齐。

**2026-10-08 第三轮：Key of Rlyeh 整块归 ac（16 文件 + mixins.json），compileJava 通过，`_extcore` 归零。**
用户否掉了"把 `KeyOfRlyehMixinHelper` 拆成两个类"的方案，理由是「锤子也是 ac 未来需要的，本体反而不需要」——**按功能归属而不是按当前调用者数**。查下来这个功能确实散在 5 处，只有物品那一块已归位：
- 物品 `common/items/ac/KeyOfRlyeh{Item,StaffItem}`（已在 ac 桶；StaffItem 硬 extends RevelationFix `SecondPhaseStaff`）
- **14 个 mixin** `common/mixin/abyss/*` → `common/mixin/ac/abyss/*`（+ `goetyominous.mixins.json` 14 条 `"abyss.X"` → `"ac.abyss.X"`）
- **钩子** `common/events/CataclysmKeySpellHandler` → `common/events/ac/`（只在 `GoetyCataclysmCompat.isLoaded()` 时注册，`isHoldingKey` 又门控 `AlexCavesCompat.isAlexCavesLoaded()`）
- **helper** `utils/KeyOfRlyehMixinHelper` **整个** → `utils/ac/`（不拆；`applyDeepOneReputation` 只被 2 个 ac 物品用，`isKeyOfRlyeh` 被 14 mixin + 3 个 ac 法术 + 上面那个钩子用）
- **第五块（视觉层）也已搬**：`common/rlyeh/RlyehStyled` → `common/rlyeh/ac/`；`client/rlyeh/{RlyehShaders,RlyehFont,RlyehStyledText,RlyehTooltipHandler}` → `client/rlyeh/ac/`；**核心 mixin `common/mixin/MixinClientTextTooltip`** → `common/mixin/ac/`（`@Mixin(ClientTextTooltip.class)`，只在文本用了 `goetyominous:rlyeh_abyss` 字体时才动手）+ mixins.json 的 `"client"` 列表那 1 条改 `"ac.MixinClientTextTooltip"`。同样 all-or-nothing：rlyeh 和这个 mixin 必须一起走，否则 core→ac。脚本 `.scratch/_domove21.mjs`。
  `RlyehStyledText`（→ac）引用核心 mixin `common.mixin.FontAccessor`——ac→core 合法，不用动。
  mixin 进子包后 refmap 复验：旧键 `common/mixin/MixinClientTextTooltip` 0 个、新键 `common/mixin/ac/MixinClientTextTooltip` 2 个，且 `renderText` → `m_142440_` 映射在（证明原版方法重映射没丢）。
- **`GoetyCataclysmCompat` 是反射垫片**（`java.lang.reflect.*` + `ModList`），**零 Cataclysm 硬 import**。所以 `CataclysmKeySpellHandler` 搬进 ac **不增加 ac 的编译依赖**；没装 Cataclysm 也照常加载，只是功能不触发。唯一变化是 `GoetyOminous:123` 的注册 FQN 变成 `common.events.ac.*`，而 `GoetyOminous` 本来就是枢纽。
搬运脚本 **`.scratch/_domove20.mjs`**（在 `_domove19` 基础上加了 **mixins.json 改写段**；`"abyss.` → `"ac.abyss.` 并断言处数 == 14）。实测 21 处 FQN、**补 import 0**（这几处原本就是显式 import，FQN 段直接改掉了）。
**refmap 验证配方**：`build/tmp/compileJava/goetyominous.mixins.refmap.json` 里 grep 旧 `mixin/abyss` 键应为 0、新 `mixin/ac/abyss` 键 >0（只有 `WaterJetSpellKeyOfRlyehMixin` 有键，其余 13 个 `remap=false` 无成员映射）。
**坑**：`./gradlew compileJava` **不依赖 `processResources`**，所以 `build/resources/main/goetyominous.mixins.json` 会留着旧内容——验打包内容要另跑 `./gradlew processResources`。

**2026-10-11 复查（用户问「mm of am ac lm sar ua 这七个准备好拆了吗」）：代码侧归位没有退化，结构性工程一项没动。**
重跑全绿：`_extcore` = 0 核心文件硬 import 第三方、0 错桶；`_verify` = 0 单模块残留。另补了两项脚本**结构上抓不到**的检查：
- **基座库盲区**：`_extcore` 的 `ROOTS` 只列 7+1 个源码 mod 包，**没列 citadel / blueprint / lionfish / revelationfix**。直接枚举核心 553 个文件的全部外来 import 核实：只有 `org.spongepowered.asm`(287) `top.theillusivec4.curios`(25) `snownee.jade.api`(14) `org.joml` `com.electronwill.nightconfig` `vazkii.patchouli` `org.lwjgl` —— **零个联动包、零个基座库**。revelationfix 仅剩 2 处硬 import，都在 `common/items/ac/KeyOfRlyehStaffItem.java`（ac 桶内 ✓）。以后复核照这个枚举法，别只信 ROOTS 表。
- **`src/main/resources/goetyominous.mixins.json` 有 17 条指向模块桶**（`mixins` 节 mm 1 = `mm.MutantMoreItemMixin`；ac 16 = `ac.abyss.*` 14 个 + `client` 节 `ac.GameRendererAccessor`/`ac.MixinClientTextTooltip`）。分节 70/17/0 = 87。**拆 jar 时必须按模块劈成各自的 mixins.json**；危险点在于 `build.gradle` 声明 `defaultRequire=0`，拆完 core 那份会**静默失效**而不是报错（见 build.gradle:302 的注释）。
- 最近 3 个提交（老虎仆从/幻术师象/试炼扩怪）**没有引入新耦合**：TigerServant 三处落位正确；试炼刷怪笼那 21 只全在 `data/goetyominous/structures/trial_chambers/spawner/*.nbt` 里，gzip 解开只有 `goety:`/`goetyominous:`/`minecraft:` 三种 id（解 nbt 用 node `zlib.gunzipSync`，本机没有 python）。
- `_privacy` 从 9 涨到 12：新增 ac 3（`common/mixin/FontAccessor`、`compat/mod/ModernUiTextCompat`、`ModernUiTooltipCompat`，语义上是核心）+ am 1（**`common/entities/ai/SmoothBodyRotationControl`，只被 `ally/am/IllagerElephantServant` 用 → 按约定该进 `common/entities/ai/am/`，`ai/` 下已有 `ac/` 子包**）。这是唯一新冒出的「该搬没搬」。
- 真枢纽 = **9 个**：`_verify` 的 8 个（`ModModelLayers`/`GoetyOminous`/`ModCreativeTab`/`TesseractTagHandler`/`ClientEvents`/`ModNetwork`/`ModEventHandler`/`GoetyOminousType`）+ `_prepcheck` 才看得见的第 9 个 `client/init/ModEntityLayers`（98 个 layer 字符串，零类引用）。
- 仍未动：8+1 个枢纽改扩展点、资源按 modid 隔离（`_resattr` 3055 个资源只有 320 可归因，`resourcepacks/` 7 个包**仍缺 lm**）、`of→ac` 那条边（`common/items/of/OfItems.java`）。

**2026-10-11 下午：用户说「你来做」→ 8+1 个枢纽已改扩展点，`compileJava` 通过（单 jar 状态下原地改造，不真拆）。**
架构：模块贡献搬进**模块自己拥有的类**，不再新增核心注册表/接口（唯二例外 `TrainIllagerExtension`、`CompatItemSource`）。
- 注解范本：common 侧 `@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)`；client 侧必须带 `value = Dist.CLIENT, bus = Bus.MOD`（否则专用服务器会加载 client 类）。每个 handler 内部自己 `if (!XxxCompat.isXxxLoaded()) return;`。
- 落点：模块事件 → `common/events/<modid>/`；模块客户端事件 → `client/events/<modid>/<Modid>ClientEvents`；胶水 → `compat/<modid>/`；`compat/mod/*Compat`（isLoaded 门控本身）留 core。
- **`_privacy.mjs` 现在只剩 1 个枢纽：`GoetyOminous`（= 有意保留的唯一接缝）**。接缝形如 8 个 `if (XxxCompat.isXxxLoaded()) { XxxCompatManager.init(modEventBus); }`，每个模块在 core 里只被提到一次；`addAttributes` 不再列举模块（各 manager 自己 `modEventBus.addListener(XxxCompatManager::setCustomAttributes)`），`onClientSetup`/`commonSetup` 里的 ac/lm 注册也各自归位。
- `client/init/ModEntityLayers` **不是** `_privacy` 意义上的枢纽（零类引用，全是 `new ResourceLocation("mutantmore", ...)` 字符串），本轮**没动**；它仍要靠 `_prepcheck` 那类字符串检查，别以为 `_privacy` 绿了就干净。
- 顺手把两个**错桶的核心件**归位：`client/render/projectile/mm/ServantWindChargeRenderer` + `client/render/model/mm/WindChargeModel` → `client/render/{projectile,model}/`（注册的是核心的 WIND_CHARGE/SERVANT_WIND_CHARGE 实体，与 mm 无关）；`common/entities/ai/SmoothBodyRotationControl` → `ai/am/`。
- **`ModNetwork.PROTOCOL_VERSION` "1" → "2"**（包 id 重新分配：core 占 0-3，模块包排后面）。新旧混装会在连接时干净失败，不会静默错配。
- 有意/顺带的行为差异（要记得）：① `LivingDropsEvent` 里 ac 与 sar 从"piglin 块之后固定顺序"变成两个独立订阅者，只在"我方两只仆从抢同一击杀"时可能换人捡，物品不会复制或消失；② `ModSpellControllers.ENTITY_TYPES`（mm 的 Wither Breath 法器控制器）与 `WITHER_BREATH_CONTROLLER` 渲染器**改为只在 mm 加载时注册** —— 它的唯一用户是 `MmItems` 的 MagicFocus，所以语义正确。
- 完整性核账（搬完必做）：全仓 `registerEntityRenderer|registerBlockEntityRenderer` 230 处、`registerLayerDefinition` 104 处，与 HEAD 逐项相等 —— 拆枢纽最容易丢的就是注册行。
- 新工具：`.scratch/_gen_mml.mjs`（按 `if (XxxCompat.isXxxLoaded()) {` 花括号配对抽出模块块，并解析每个块需要哪些 import，含通配 import 与同包类）、`_asm_mml.mjs`（装配成模块类，保留原 `@SubscribeEvent` 方法）、`_prune.mjs`（删无用 import，通配保留）、`_addimports.mjs`（按锚点插 import，规避 bash 引号与 GBK）。**解析 import 时的坑：`import a.b.*;` 用 `[\w.]+` 匹配会整行失配（`*` 不在字符类里），必须写 `[\w.*]+`。**
- 仍未动（只在真拆时才做，现在做会破坏单 jar 构建）：`goetyominous.mixins.json` 那 17 条模块条目、资源按 modid 隔离、`of→ac` 那条边（`common/items/of/OfItems.java`）。

**2026-10-11 晚：用户改口径 —— 7 个模块合成 5 个 jar（ac+am 一个、ua+sar 一个）。**
- 用户原话："以后我会把 ua 和 sar 放一起，am 和 ac 放一起，都是可选前置 —— ua 和 sar 只需要下任意一个，两个都下也可以"。追问后定案：**一组一个 jar + 一个 modId**（不是两个 jar 互依赖，也不是合进 core），故 **5 个 jar**：`ominous-acam`（ac 239 + am 92 = 331）、`ominous-uasr`（sar 32 + ua 20 = 52）、`lm`(187)、`mm`(82)、`of`(82)。每个 jar 一份 mods.toml，两个上游都声明 `mandatory=false`；modid 仍是各自一个。
- **合并是结构上免费的**：`am↔ac` 与 `ua↔sar` 之间**零交叉引用**（`_modedge` 全仓只报 `of → ac` 一条真边），而且每半边原有的 `isXxxLoaded()` 门控天然支持"只装了其中一个上游"。
- **`.scratch/` 脚本已按 5 分组改完**（用户："把脚本的分组一起改掉"）。做法：新增 **`.scratch/_groups.mjs`**（`MODS`=目录分段、`GROUP` 分段→jar、`JARS`/`SPLIT`/`ALL`(=JARS+ias)、`g()`），各脚本 import 它。**`modOf()` 一律保持返回目录分段**，只在"判定拆开后的 jar 边界"处套 `g()`——历史搬运脚本（`_domove*`/`_relocate*`/`_premove`）按路径办事，**不许套 g()**。
  改了 12 个：`_privacy`(枢纽=≥2 **jar**)/`_verify`/`_verify2`/`_modedge`/`_extcore`(B 段「桶放错」现在比 jar——`am` 桶里 import `alexscaves` 已合法)/`_prepcheck`/`_reverseuse`/`_reverseuse2`/`_inbucket`/`_namesweep`/`_fixpoint`/`_moddep2`(只加了 citadel 的按 jar 汇总)。
  **没改**（有意）：`_moddep`/`_moddep2` 的正表测的是"我们的代码耦合到**上游 mod**多少"，那是逐上游属性、不是 jar 属性，合并分组会毁信息；`_audit`（临时候选探针）、`_annot`（74 归位批次的一次性统计，已执行）、`_coreclusters`（本来就无用）。
- 重跑结论：`_privacy` 13 个私有核心文件（`ac(6)→acam(6)`、`sar(1)→uasr(1)`，**成员一个没变**，只是标签更准）、枢纽仍 1 个 `GoetyOminous`；`_verify` 0 残留；`_extcore` A=0 B=0；`_verify2` = 22 单 jar / 2 枢纽；`_modedge` 跨 jar 边 = `of→acam`(OfItems) + `主mod→ias` 4 + `主mod→uasr`(MixinOssuaryBlockEntity) + `主mod→acam`(CuriosIntegration)；`_reverseuse2` 15+112；`_namesweep` 48→49（多出的那条是原本横跨 ac+am 两桶、现在算同一 jar 特征词）；`_fixpoint` 327 不变；`_inbucket` 45/5 不变。`_moddep2` 按 jar：**citadel 只在 acam 出现 103/383**，其余 jar 全 0 —— 合并后这就是"acam 这一个 jar 的硬前置是 citadel"。
- **顺带查出一件之前漏记的事**：`_verify2`（名字前缀 + `compat.mod.XxxCompat` + `"ns:"` 三路启发式）数出 **2 个枢纽**，除 `GoetyOminous` 外还有 **`client/OminousIconRotation.java`** —— 它硬编码 `new Entry("accompat", AlexCavesCompat::isAlexCavesLoaded, 5)` 这样每个模块一条（含优先级，决定凶兆图标轮转顺序）。`_privacy`/`_verify` 看不见它（Compat 类在核心 `compat/mod/`，不属任何模块桶）。真拆时它也得改扩展点（各 jar 自己登记图标条目）。
- 给用户的两点提醒：**合并 jar 会让"装了一个上游"和"装了两个"共用同一个 mixins/资源/配置门控**，`isXxxLoaded()` 从"jar 在不在"退化成"上游 mod 在不在"，每处门控仍必须逐上游判；`PROTOCOL_VERSION` 之类全局量随 jar 数变少而收敛。

**2026-10-19：游戏配置已按 5 组拆开（仍是单 jar 构建，`compileJava` 通过）。**
- core 4 个类保留原名字与原 TOML 路径：`goetyominous/goetyominous-{attributes,mobs,spells,weapons}.toml`；模块键全部抽走。合计 **1044 键 = core 195**（Attributes 99 / Mobs 38 / Spells 44 / Weapons 14）+ **模块 849**。
- 新增 5 个模块配置类（各含 Attributes / Servant Limits / Spells / Weapon 段合并）：
  - `config/acam/AcamConfig`（ac+am，441）
  - `config/uasr/UasrConfig`（ua+sar，47）
  - `config/lm/LmConfig`（171）
  - `config/mm/MmConfig`（107）
  - `config/of/OfConfig`（83）
  每个对应一份 TOML：`goetyominous/goetyominous-{acam,uasr,lm,mm,of}.toml`。模块文件内的段名去掉了 `(Optional)` / `(Optional - LM)`；core 段名原样保留。
- **归类依据是引用实据，不是名字**：字段 → 引用文件所在模块目录（ac/am→acam、sar/ua→uasr）。1044 个键全部有引用、每个只落一个桶，零 UNUSED。工具链 `.scratch/_cfgparse.mjs`（解析字段/段/表达式）→ `_cfgrefs.mjs`（字段→引用桶）→ `_cfgclassify.mjs`（合并+报告）→ `_cfggen.mjs --dry`（生成+改写，先 dry 到 `.scratch/cfg/out`）→ `_cfgverify.mjs`（键数/重复/声明-赋值配对）。
- **两个名字会骗人的坑**：Rambler / Volt / Dicer / Trembler / Fire Slime / Guzzler / Skyvern / Terror / Frowzy 在 AttributesConfig 里**没有模块标签、实际属于 of**（引用都在 `common/entities/ally/of/`），不是 ac；`WitherScythe*` → mm，`BarbaricAxe*` → acam。核验时务必看引用路径。
- `LM_DAMAGE_MULTIPLIER_COMMENT` 随 LM 伤害倍率键从 `MobsConfig` 移入 `LmConfig`；每个模块类都有自己的 `loadConfig`（风格照 Attributes/Mobs/Spells 的 autosave；core 的 WeaponConfig 保留它原来的 autoreload 版本）。
- **注册**：暂时在 `GoetyOminous` 中和 core 4 份一起**无条件**注册（单 jar 状态等价于"该组 jar 存在"）。真拆 jar 时把这 5 段整段搬到各自 jar 的主类，**不要**用 `isXxxLoaded()` 反过来门控（同组两个上游可能只装一个）。
- **值丢失已接受**：模块键换了 TOML 路径，老用户对这些键的自定义值会退回默认；core 键路径未变，值保留。
- 175 个使用方文件被改写：`AttributesConfig/MobsConfig/SpellConfig/WeaponConfig.<Field>` → 对应模块类；无一个文件同时用到两个模块组，最大改动面是 `Servant Limits` 与 `Spells` 两段。
- 真拆时仍待办：各自 `mods.toml`、各 jar 里的 mixins.json、资源按 modid 隔离、`of→ac`、`OminousIconRotation`。

**2026-10-19：SAR/UA 的 Patchouli 分类已合并为 `goetyabnormal`「Goety Abnormal」（`compileJava` 通过）。**
- 用户要求"合并 sar 和 ua 的 pachouli book，为拆分做准备，两个都装，名字改为 goety abnormal"。做法是**只合并分类，不合并资源包**：两个内置包仍分别由 `isSavageRavageLoaded()` / `isUpgradeAquaticLoaded()` 门控，这样只装其中一个上游时另一个的条目不会出现在书里。
- 两个包各放一份 `categories/goetyabnormal.json`（内容相同，icon 各用自家物品：SAR `creeper_spores`、UA `thrasher_tooth`），高优先级包覆盖低优先级那份；父分类仍是 `goety:goetyominous`。26 个条目的 `"category"` 全部改为 `goety:goetyabnormal`，**条目目录/ID 不动**（`goety:savageandravagecompat/*`、`goety:uacompat/*`），避免读者的 Patchouli 阅读进度清零。
- 条目 sortnum 重新排：SAR 保持 10–90，UA 由 10–40 平移到 100–130，消除同一分类内的并列。
- `OminousIconRotation` 两条 Entry 改成同一路径 `goetyabnormal`（**UA 条目在前**判 index 1，SAR 在后判 0；单装哪个就取哪个，不会引用缺失模组的贴图）。`installedOrder()` 增加按 categoryPath 去重，否则同分类会被轮转两次。
- 用户指定合并分类图标用 `savage_and_ravage:creeper_spores`。两个包都 `required=true`，`PackRepository` 的内部列表**末位 = UI 顶部 = 最高优先级**（`PackSelectionModel` 给 UI 会 reverse，`FallbackResourceManager` 从末位往前找）；必选包按 pack id 的 TreeMap 序插入，同 `Position.TOP` 时后排序者优先。因此把客户端包 id 改成 `goetyominous/uasr_sar_compat`（TOP）与 `goetyominous/uasr_ua_compat`（BOTTOM），SAR 永远压过 UA；改 id 也会让老 options.txt 里的旧 id 失效并被当作未选中必选包重新插入，避免旧顺序残留。
- 结果：两个都装/只装 SAR → creeper_spores；只装 UA → UA 包里的 thrasher_tooth（不会出现缺失贴图）。`OminousIconRotation` 恢复 SAR 条目在前（index 0 优先），与分类图标保持一致。分类名仍是「Goety Abnormal / 诡厄Abnormal」。
- 拆 `ominous-uasr` 时，资源侧仍可保留两个包 + 一份共享分类；分类 JSON 必须在两个包里各留一份，不能只放 core。

**2026-10-19：AC/AM 的 Patchouli 分类合并为 `acamcompat`「Alex's Compat / Alex联动」（用户只指定图标 `alexscaves:tectonic_shard`；`compileJava` 通过）。**
- 和 SAR/UA 一样**只合并分类、不合并资源包**：`ac_compat` / `am_compat` 仍分别由 `isAlexCavesLoaded()` / `isAlexMobsLoaded()` 门控。
- 新增中间层根分类 `goety:acamcompat`（sortnum 5，parent `goety:goetyominous`），两个原根 `accompat` / `amcompat` 的 `parent` 改为 `goety:acamcompat`。AC 的 6 个子分类、AM 的 5 个子分类、134 个条目、条目 ID 和 `"category"` 引用全部不动 → 阅读进度不丢。Patchouli 的 landing 只列无 parent 的分类（`GuiBookLanding`），子分类按钮在父分类页里递归展示，所以四层嵌套可用。
- 图标：两个包各放一份 `acamcompat.json`，AC 包用 `alexscaves:tectonic_shard`、AM 包用 `alexsmobs:blood_sac`；双装时靠包优先级让 AC 那份胜出，只装 AM 时也不会引用缺失贴图。分类名用户定为 `Goety Alex`（zh 写 `诡厄Alex`）。
- 客户端包 id 改为 `goetyominous/acam_ac_compat`（TOP）与 `goetyominous/acam_am_compat`（BOTTOM），数据包 id 不变。**1.20.1 源码核实**：`PackRepository.m_10517_` 只对未选中的 required 包按 TreeMap id 序调用 `Position.m_10470_`；TOP 扫描到末尾后追加（列表末位）、BOTTOM 插到开头；`MultiPackResourceManager` 按选中列表正序加包，`FallbackResourceManager.getResource` 从末位往前找 → **末位 = 最高优先级**。改 id 是为了让 options.txt 里的旧 id 失效、强制按 Position 重新插入（已在选项里的包不会因 Position 变化重排）。
- `OminousIconRotation`：两条 Entry 改为同一路径 `acamcompat`，AC 在前（index 5）、AM 在后（index 4）；`indexOf` 取第一个已加载项，`installedOrder` 的 categoryPath 去重保证只轮转一次。`OminousRotationBookOrder` 从书根 `goetyominous` 的**直接子分类**取 path，合并后直接子分类是 `acamcompat`，用旧路径会得到空轮转。

**Why:** 用户先问"拆了该做什么"→ 澄清为"切出第三方内容、每个来源 mod 一个 jar"→ 又确认要单仓库多 jar。2026-10-07 版把 sar/ias 一起划为留守；2026-10-08 用户先提"会拆分有仆从的联动项"（8 个 modid 全有仆从），复核后定案"只会拆 ua sar of am ac mm lm" → **sar 改拆，只有 ias 留守**；同日又定"先不拆，只做提前准备"。2026-10-11 晚用户再把 7 个收成 5 个 jar（配对捆绑可选前置）。

**How to apply:** 分析脚本：`.scratch/_moddep2.mjs`（各模块耦合度：纯文件/import 数/资源引用/citadel）与 `.scratch/_modedge.mjs`（跨模块边、枢纽文件），改脚本里的 `MODS` 表即可重跑。搬运脚本 `.scratch/_domove.mjs`（带 `--dry`，**重复跑直到报 0 个**才是收敛）、查枢纽/残留 `.scratch/_verify.mjs`、查"名字像某模块"的第二类证据 `.scratch/_namesweep.mjs`。搬运时两类断链都要补 import：**「别人搬走」和「我搬走、对方留守」**——后者首版漏了，编译才暴露。搬运手法见 [[goetyominous-mod-folder-isolation]]；跨 jar 复制常量的禁令见 [[modmobtype-name-collision]]；新 jar 必须用不同 modId，见 [[forge-duplicate-modid-jar-silent-pick]]。
