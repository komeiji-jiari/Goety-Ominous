---
name: windy-update-port-plan
description: Goety-3「Windy Update」(commit 2f424351) 移植计划——范围=1.1 Breeze/1.2 Hurricane/1.3 Whirling Cage/1.5 Wind Shrine+群系(1.4 与试炼密室排除)；关键结论、1.21 缺失 API 清单与复刻策略、移植顺序
metadata: 
  node_type: memory
  type: project
  originSessionId: 2692f880-0ad0-4ed1-a2d2-664239adcf3c
---

**背景**：Goety-3（Vivideru，NeoForge 1.21）提交 `2f424351` "Windy Update"（2026-10-03，父 `5479ff84`，+167 文件）要移植到 1.20.1 Forge 的 goetyominous。**本地对照源码：`E:\java\Goety-3-main\`**（旧 `Goety-3-main_new` 只有 resources、缺 java，已删）；新增代码在 `com.Vivideru.Goety` 命名空间。

**范围（2026-10-03 用户拍板）**：做 1.1 Breeze 一套、1.2 Hurricane 一套、1.3 Whirling Cage 方块、1.5 Wind Shrine 重做+windswept_peaks 群系+宝库/钥匙；**不做** 1.4 Pale Crystal Ball、试炼密室房间/Trial Spawner 整合、其他 Goety 本体更新。顺序：①Breeze 仆从+风弹 → ②Hurricane → ③Whirling Cage → ④Wind Shrine。

**已确认的关键结论（省得重查）**：
- **AI 全是 goal 制，零 brain**：`BreezeServant`（663 行，`registerGoals` + 内部类 `BreezeAttackGoal extends Goal`，五段状态机）与 `AbstractHurricane`（4 个内部攻击 goal）都 `extends Summoned`，无 Brain/Sensor/Activity。`BreezeServant.java` 的 `import ...monster.breeze.Breeze` 是死引用可删。
- **VanillaBackport 帮不上**：libs 里那个 jar 无任何 `net/minecraft` 注入类，也没有 breeze/vault/trial 类，只有素材 → Breeze/风弹/宝库都得自研。
- **`libs/1.21.1.jar` 是原版 1.21.1 客户端 jar**（Mojang 签名）：里面有 breeze 贴图(`textures/entity/breeze/*`)、`rendertype_breeze_wind` 着色器、`wind_charge.png`；**但没有 sounds/**（音效不在 jar 里，要用资源索引下载或找替代）。
- **Goety 2.x（硬前置）已有大量可复用件**：风粒子全家桶(WindParticle/WindTrail/WindShockwave/WindGather/WindBlow)、`wind_blast` 伤害类型(data/goety/damage_type)、Wind Staff、WindBlast/WindHorn/RazorWind 法术、`Summoned/SummonSpell/ReviveServantItem` 基座、`JigsawPlacement`、以及 **wind_shrine 的原版 nbt 组件**（big_tower_floor_1..4 等）→ 新神殿只要补 floor_5/6、roof_5/6、connector/expansion 件。
- 1.20.1 **已有**：`AnimationState`+`startIfStopped`、`Util.toShuffledList`、`RandomSource.nextIntBetweenInclusive`、`Pose.LONG_JUMPING`、`JigsawPlacement`+内部类 `Placer`/`PieceState`、`ResourceKeyArgument`。

**1.21 缺失 API 清单（要精确复刻，不允许行为差异）**：
| 缺失件 | 用途/调用点 | 复刻策略 |
|---|---|---|
| `LongJumpUtil.calculateJumpVectorForAngle` | BreezeServant:560 / AbstractHurricane 跳跃 | 从 1.21 源码逐行复刻（需取原版源码） 
| `ProjectileDeflection`（含 `deflection(Projectile)` 钩子） | BreezeServant:72-75,242-247 弹反 | 1.20.1 无此系统 → 用事件(ProjectileImpactEvent)手写等价反弹 |
| `AbstractWindCharge` | ServantWindCharge 父类 | 用 ThrowableProjectile 重写（爆炸/击退/可命中判定照抄语义） |
| `SimpleExplosionDamageCalculator` | 风弹爆炸(击退系数/免疫块 tag) | 自定义 ExplosionDamageCalculator 复刻 |
| `Pose.SHOOTING/INHALING/SLIDING` | 动画/状态同步 | 1.20.1 Pose 无 → 自建 synced 状态 id + 驱动 AnimationState |
| `Entity.getInBlockState()` / `setDiscardFriction` | 粒子/滑行摩擦 | 手动替代（脚下方块 / 自管 friction） |
| `SoundEvents.BREEZE_*`(12 个) | 仆从与风弹音效 | 自注册 sound event + 素材（jar 里没有 ogg） |
| `Items.WIND_CHARGE` / `Tags.Items.RODS_BREEZE` / `EntityType.BREEZE_WIND_CHARGE` / `EntityType.WIND_CHARGE` / `EntityTypeTags.DEFLECTS_PROJECTILES` | 治疗交互/弹反过滤 | 换成自家物品/tag/实体判定 |
| `ParticleTypes.GUST_EMITTER_*` | 风弹爆炸粒子 | 用 2.x 的 Wind 系列粒子 |
| `damageSources().windCharge()` | 风弹伤害 | 复用 `goety:wind_blast` 伤害类型 |
| `MobUtil.getSpecialAttackDamage`(Goety3) | 风弹伤害计算 | 照 Goety-3 源复制（ATTACK_DAMAGE 超出部分 + BUFF 加成，下限 0） |

**下一步**：① 盘点已完成（见对话）；② 取 1.21 原版源码精确复刻上表方法（LongJumpUtil / AbstractWindCharge / SimpleExplosionDamageCalculator / Breeze 的 Pose 与音效常量）；③ 按 BreezeServant→ServantWindCharge→注册/资源/配置 落地。配置项照 Goety-3：BreezeServantHealth=30.0 / Armor=0.0 / RangeDamage=3.0。

**进度（2026-10-03 夜，playtest 中）**：
- ① 风弹侧已落地：`AbstractWindCharge`/`ServantWindCharge`（SERVANT_WIND_CHARGE 已注册）、`WindChargeItem`、mixin（AbstractHurtingProjectileMixin/ExplosionAccessor/PlayerImpulseMixin）、`SimpleExplosionDamageCalculator`+WindCharge 工具、渲染（WindChargeModel/ServantWindChargeRenderer/WindChargeRenderTypes + rendertype_breeze_wind 着色器）、粒子（GUST + GUST_EMITTER_SMALL/LARGE，gust_0..11 贴图）、breeze/wind_charge 全套音效、breeze_rod/wind_charge 物品与配方、wind_charge 伤害类型 + blocks_wind_charge_explosions tag。
- 坑（已修）：`goetyominous:gust` 走 registerSpriteSet，但源码树漏了 `assets/goetyominous/particles/gust.json` → `MutableSpriteSet.sprites` 恒为 null → `setSpriteFromAge` 里 `size()` NPE。补的 JSON 帧顺序必须 0→11（`setSpriteFromAge` 按 age*(n-1)/lifetime 取帧）；两个 emitter 是 registerSpecial，**不需要** JSON。改完必须重新构建（跑的是 libs 里的旧包）。
- 坑（已修，2026-10-03 深夜）：**1.21 的 shader vsh 不能直接照抄** —— `#moj_import <fog.glsl>` 解析到的是游戏自带 **1.20.1** 的 include，`fog_distance` 是**三参** `(mat4 modelViewMat, vec3 pos, int shape)`，1.21 是两参。照抄两参 → GLSL 编译失败 → `WindChargeShaders` 的 catch 静默吞掉（printStackTrace 只到 stderr，不进 latest.log）→ `breezeWind()==null` → 渲染器首行 return → **实体在但不渲染、日志干净**。修法：vsh 写 `fog_distance(ModelViewMat, Position, FogShape)`。以后移植任何 1.21 shader 都要先对 `libs/1.21.1.jar` 与游戏 1.20.1 include 的签名差。
- 坑（已修，2026-10-03 深夜）：**1.21 `Explosion.BlockInteraction` 比 1.20.1 多第 4 个常量 `TRIGGER_BLOCK`**（KEEP/DESTROY/DESTROY_WITH_DECAY/TRIGGER_BLOCK），`interactsWithBlocks() = blockInteraction != KEEP`。风弹爆炸走 TRIGGER → flag=true → radius≥2 选 largeExplosionParticles(GUST_EMITTER_LARGE)。1.20.1 枚举没有 TRIGGER，`WindChargeExplosion` 构造用 KEEP 会让 flag=false → 粒子被误选成 small（风爆范围远小于 1.21）。修法：WindChargeExplosion 覆写 `interactsWithBlocks()` 返回 true。
- 进度（2026-10-04）：① **实体 + 注册 + 客户端全部落地并编译通过** —— BreezeServant（663 行复刻 + DATA_BREEZE_POSE）、ServantCombatUtil、BreezeModel/BreezeAnimation/BreezeServantModel/BreezeServantRenderer、12 音效 + sounds.json、AttributesConfig 3 项、两个 tag、刷怪蛋、lang、ModModelLayers 三处注册、3 张贴图。**剩余第 3 层**：BreezeSpell + breeze_focus（MagicFocus）+ focus/thrall 配方 + patchouli 两篇（en 有 zh 无）+ SpellConfig 4 项；breeze_rod 来源随敌对 breeze 实体。
- **① 补漏（2026-10-04）**：免摔伤 = Goety-3 datagen 把 `goety:breeze_servant` 加进原版 `minecraft:fall_damage_immune`（1.21 原版 breeze 本体也在该 tag）→ 端口已加 `data/minecraft/tags/entity_types/fall_damage_immune.json`。**还差**：`goety:wind_heal` tag（Goety-3 注释：Wind 套件法术治疗这些仆从并清有限寿命；goety2 已有 `ModTags.WIND_HEAL`）→ 待加 `data/goety/tags/entity_types/wind_heal.json`（含 `goetyominous:breeze_servant`）；② 时再加 hurricane/hurricane_servant + `goety:breezes` tag（转化配方用）。教训：Goety-3 的 **datagen 输出（src/generated/resources + ModEntityTypeTagsProvider 等 provider）** 也是移植面，盘点别只看 src/main/resources。
- **① 完结（2026-10-04）**：敌对旋风人 `BreezeEntity` 落地（Urbhadhach 式独立类 `extends Monster`，按用户要求不抽基类）——独立模型/渲染器（复用仆从的 model layer，贴图 breeze.png/breeze_eyes.png/breeze_wind.png）、刷怪蛋（ForgeSpawnEggItem 0xAF94DF/0x9F5CC0）、掉落 1-2 旋风棒（killed_by_player + 抢夺）、免摔 + 弹反两个 tag 都加了 `goetyominous:breeze`、风弹沿用 `ServantWindCharge`（owner 非 Owned → canHitEntity/shouldAffectEntity 全放行 = 伤害任何生物；同族互免靠 `isInvulnerableTo` **双向**检查，仆从侧也补了）、属性沿用 AttributesConfig。**编译通过**。落地时修掉两个坑：占位符 `BreezeServantRef()`、跨包引用 package-private 的 `BreezeServant.BreezeAttackGoal.Phase`（要写本地 `Phase`）；`ModTags` 的两个 tag 常量在 **ModTags 顶层**（不是嵌套 EntityTypes 里）。
- **② Hurricane 调查结论（2026-10-04）**：上游是**现成的三件套**（`AbstractHurricane` 907 行 neutral 基类 extends Summoned + `Hurricane` 96 行敌对（ModServerBossInfo BossBar + setHostile + 仇恨"玩家的非敌对仆从"）+ `HurricaneServant` 63 行（xp20、HurricaneLimit、死掉按 MobsConfig 掉 Hurricane Core））→ **直接 1:1 拷贝，不用抽基类**。外围件：`HurricanePunch`(187, extends Projectile) / `HurricaneCyclone`(82, **extends goety2 的 `Cyclone`** ✓) / `HurricaneCoreSummon`(143, extends Entity) / `HurricaneCoreItem`(74, extends ReviveServantItem)。客户端：3 model（Hurricane 186 / Punch 40 / Tornado 61）+ 4 renderer（Hurricane 123 敌对仆从共用 / Punch / Cyclone / CoreSummon）+ HurricaneAnimations(142)；渲染只用 `entityTranslucent` + **`RenderType.breezeWind`（我们已有 `WindChargeRenderTypes.breezeWind` ✓）**，无新 shader。
  - **注册数据**：HURRICANE/HURRICANE_SERVANT = MONSTER、1.6×3.4、**eyeHeight 3.0**（→ getStandingEyeHeight 覆写）、tracking 10；PUNCH = MISC、1.0×1.0、eyeHeight 0.5、tracking 6、interval 2；CYCLONE = MISC、1.0×1.5、tracking 4、interval 1；CORE_SUMMON = MISC、fireImmune、noSummon、0.5×0.5、tracking 10、interval 1。
  - **属性/配置 1:1**：Health 110 / Armor 0 / PunchDamage 15 / SmashDamage 8 / CycloneDamage 2（AttributesConfig，键 hurricaneHealth…）；MobsConfig `hurricaneServantCore`=true；召唤上限 3（Goety-3 放 SpellConfig → 端口按惯例放 MobsConfig）。
  - **goety2 全都有**：`Cyclone/AbstractCyclone`、`WindBlowParticleOption`、`ColorUtil`、`ModTags.EntityTypes.UNBLOWABLE_ENTITIES`、`MobUtil.knockBack/push/areAllies`、`ModServerBossInfo`、`MainConfig.SpecialBossBar`、`ReviveServantItem`、`convert_tamed` 仪式 ✓。
  - **1.21 映射清单**：`defineSynchedData(Builder)`→无参；`PathType`→`BlockPathTypes`；`Attributes.STEP_HEIGHT`→构造器 `setMaxUpStep(2.0F)`；`IMobTyped`→删；`EntityTypeTags.DEFLECTS_PROJECTILES`→`ModTags.DEFLECTS_PROJECTILES`；`DamageTypes.WIND_CHARGE`→`AbstractWindCharge.WIND_CHARGE_DAMAGE_TYPE`；`playLocalSound(Entity,…)`→(x,y,z,…)；`SoundEvents.BREEZE_*`→`ModSounds.BREEZE_*` ✓；`ProjectileDeflection`+deflection→我们的 utils ✓；`GUST_EMITTER_*`→`ModParticleTypes.*.get()` ✓；`LongJumpUtil`→我们已复刻 ✓。
  - **待拍板**：①激活/掉落里的 `minecraft:heavy_core` 是 1.21 专属 → 找替代品；②`MACE_SMASH_GROUND_HEAVY`/`MACE_SMASH_AIR` 音效 1.21 专属 → 找替代；③敌对 Hurricane 上游没有自然生成（只进了 `goety:mini_bosses` tag + 击杀进度），随④风神殿或刷怪蛋。
  - 数据面：convert 配方（heavy_core 激活 + `goety:breezes` tag + sky + 5 soul）→ `data/goetyominous/recipes/`；loot（heavy_core + 4-5 旋风棒，需换材质/物品）；`kill_a_hurricane` 进度；patchouli 2 篇（mutations + bestiary）；tag 追加（wind_heal/fall_damage_immune/DEFLECTS_PROJECTILES/`goety:mini_bosses`）；lang ~10 键；贴图缺 **hurricane_heavy_core.png + hurricane_wind.png**（另 3 张端口已有）+ item 贴图 hurricane_core.png。
  - 相关耦合：③ `WhirlingCageBlock` 引用 Hurricane；`SpellTypeDamage`（土系打风系 ×2）在 ② 一并做。
- **Hurricane Core 功能（2026-10-04 核实）**：= **飓风仆从的"复活核心"**（`HurricaneCoreItem extends ReviveServantItem`）。来源：HurricaneServant 死亡掉落（`MobsConfig.hurricaneServantCore` 开关；写入 owner 名 + 仆从数据）。用法：手持右键**野生 Breeze 或玩家自己的 BreezeServant** → 读出核心里的飓风仆从（有数据=复活原仆从；**空核心=现场 new 一只归属玩家的**）→ `RitualRequirements.canSummon` 检查 → 满血落位 + 召唤演出（GUST_EMITTER_LARGE/CLOUD + BREEZE_WIND_CHARGE_BURST + BREEZE_IDLE_GROUND + `spawnAnim()`）→ **销毁那只 breeze**、核心 -1、5 秒冷却（SEHelper）。语义 =「杀飓风得核心→点任意旋风→借壳归来」，也是野生 breeze→飓风仆从的转化途径。**唯一映射**：`target instanceof Breeze`（1.21）→ 我们的 `BreezeEntity`。物品属性：UNCOMMON / noRepair / fireResistant / stacksTo(1)。**注意**：`HurricaneCoreSummon`（上升风柱演出）与核心物品无关，是 **③ WhirlingCageBlock:53** 的释放演出（80 tick 后放出笼里的 Hurricane），属 ③。
- **②③ 进度（2026-10-04 晚）**：② 代码全部交付（基类+Hurricane+HurricaneServant+HurricanePunch+HurricaneCyclone+HurricaneCoreItem+SpellTypeDamage+事件+客户端 7 件，用户贴入中）；数据面落地：loot/进度/5 个 tag/10 lang 键/3 贴图/breeze 双蛋+核心模型。③ 资源落地：whirling_cage 三贴图 + blockstate/方块模型(#4 用 netherite 占位)/物品模型/空池 loot + `block.goetyominous.whirling_cage` lang；`WhirlingCageBlock`/`HurricaneCoreSummon`/其渲染器（`Blocks.NETHERITE_BLOCK` 占位）代码已交付。**待重锤批次回填的 4 处**：转换配方激活物、hurricane loot 的 heavy_core 池、方块模型 `#4` 贴图、CoreSummonRenderer 的方块；`SpellTypeDamage.cast` 若要"施法中"上下文需 mixin goety2 的 DarkWand（可选）。
- ② Hurricane；③ Whirling Cage；④ Wind Shrine。

**① BreezeServant 盘点（2026-10-03）**：
- 上游 4 个主件：`com/Vivideru/Goety/common/entities/ally/BreezeServant.java`(663 行) / `client/render/BreezeServantRenderer.java`(87，含 WindLayer+EyesLayer) / `client/render/model/BreezeServantModel.java`(79) / `common/magic/spells/BreezeSpell.java`(107，extends SummonSpell)。
- 注册点：`VivideruEntityTypes`（`sized(0.6,1.77)` + eyeHeight **1.3452**（1.20.1 用 getEyeHeight 覆写）+ MONSTER + clientTrackingRange 10）/ `Goety.java:712` 属性事件（MOVEMENT_SPEED .63、FOLLOW_RANGE 24）/ `VivideruClientEvents`（BREEZE_SERVANT 32×32 + BREEZE_SERVANT_WIND 128×128 两个 layer + renderer）/ `AttributesConfig`（Health **30.0**/Armor **0.0**/RangeDamage **3.0**）/ `ModItems:383` `breeze_focus = MagicFocus(BreezeSpell)` / `VivideruItems` spawn egg / `SpellTypeDamage`（风系生物被 Geomancy 打 ×**2.0**，LivingIncomingDamage 钩子）。
- 1.21 复刻件（实体依赖）**第零步核对结论（2026-10-03，用项目映射 jar `build/fg_cache/.../47.3.22_mapped_official_1.20.1.jar` javap+CFR 逐项核实）**：
  - `LongJumpUtil`：1.20.1 **确实没有该类** → 复刻到 `utils/LongJumpUtil`（BreezeServant:560 与 ② AbstractHurricane:770 共用；都传 checkClearance=false）。唯一映射差异：1.21 `mob.getGravity()` → 1.20.1 `mob.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get())`。
  - `ProjectileDeflection`（NONE/REVERSE/AIM_DEFLECT/MOMENTUM_DEFLECT）+ `Entity.deflection` 钩子：1.20.1 全无 → 自建接口 4 常量 + `ProjectileDeflector` 接口 + `ProjectileImpactEvent` 等价（取消事件=不走 onHit，服务端执行 deflect+setOwner；1.21 的 `onDeflection` 钩子无对应、无人覆盖，记录为该差异）。REVERSE 语义：速度 ×-0.5、yaw +170~190、yRotO 同步、hasImpulse=true。
  - `getInBlockState`：1.20.1 无 → 实体局部缓存（1.21 在 `Entity.baseTick` 第一行清 + `setPosRaw` 方块坐标变化清；BreezeServant 两处调用都在 tick 开头移动前 → baseTick 清一次行为等价）。
  - `setDiscardFriction`：**1.20.1 已有**（`LivingEntity.setDiscardFriction(boolean)` + `shouldDiscardFriction()`，`travel()` 里 `if (shouldDiscardFriction()) setDeltaMovement(x,d2,z) else setDeltaMovement(x*f3, d2*0.98f, z*f3)` 与 1.21 同构）→ 零代码。
  - `Pose.SHOOTING/INHALING/SLIDING`：1.20.1 Pose 只有 LONG_JUMPING → 自建 synced 状态（entity data）驱动 6 个 AnimationState，属实体实现（第 1 层）。
  - `c:rods/breeze` / `EntityTypeTags.DEFLECTS_PROJECTILES` → 自建 tag（`goetyominous:deflects_projectiles` 放 `data/goetyominous/tags/entity_types/`，1.20.1 是复数目录）。
- **第 1 层落地 + 编译期踩坑（2026-10-04，BreezeServant 已建）**：
  - 1.20.1 `LivingEntity.getEyeHeight(Pose, EntityDimensions)` 是 **final**（内部转调 `getStandingEyeHeight`）→ 设置 eyeHeight 要覆写 **`getStandingEyeHeight`**（SLEEPING 自动 0.2F）。`AbstractWindCharge` 能覆写 getEyeHeight 是因为它不是 LivingEntity。
  - `PathType` 在 1.20.1 叫 **`BlockPathTypes`**，且**没有 DANGER_TRAPDOOR**（用 TRAPDOOR 近似）。
  - `Vec3.closerThan(Vec3,double,double)` 是 1.21 新增；语义 = `水平距离² < 水平阈值² && |Δy| < 垂直阈值`（反编译 `Mth.lengthSquared(dx,dz) < Mth.square(h)`）。
  - `Level.playLocalSound(Entity,…)` 是 1.21 重载 → 1.20.1 用 `(x,y,z,…,false)`。
  - goety2 `Summoned` **有** `followGoal()/getCommandSpeed()/嵌套 WanderGoal/FollowOwnerGoal/xpReward/setConfigurableAttributes`；**没有** `IMobTyped/getGoetyMobType` 与 `MobUtil.getSpecialAttackDamage`（后者复刻进 `utils/ServantCombatUtil`）。
  - goety2 `ModParticleTypes.HEAL_EFFECT`、`GoetyEffects.BUFF` 都是 RegistryObject → 要 `.get()`。
  - 端口 `ModItems` 在 `common.items`（`ModSounds`/`ModTags` 在 `common.init`）。
- 工具坑（CFR 正确用法）：`java -jar tools/cfr-0.152.jar <jar> <类名>` 会整包倾倒；要**先 `unzip -o <jar> <路径>.class -d /tmp/x` 再 `java -jar tools/cfr-0.152.jar /tmp/x/.../X.class`**，才能单类输出（1.20.1 侧用 `build/fg_cache/net/minecraftforge/forge/1.20.1-47.3.22_mapped_official_1.20.1/...jar` = mojmap 可读）。
- 客户端连带 1.21 原版件：`BreezeModel.createBodyLayer(w,h)`（32×32/128×128 两套）+ `BreezeAnimation`（SHOOT/SLIDE/SLIDE_BACK/JUMP 关键帧）→ 都要复刻；`RenderType.breezeEyes` 已核实 = `ENTITY_TRANSLUCENT_EMISSIVE.apply`，1.20.1 直接映射 `RenderType.entityTranslucentEmissive`。
- 资源：贴图 3 张 `assets/goety/textures/entity/servants/breeze/breeze_servant{,_eyes,_wind}.png`（端口现在只有一张 md5 对不上的 vanilla breeze_wind.png）；12 类音效 ogg **已备**（charge3/death2/deflect3/hurt3/idle4/idle_air4/inhale2/jump2/land2/shoot1/slide4/whirl1，注册+sounds.json 待补，现只有 burst）；focus 模型+贴图(`textures/item/focus/breeze_spell.png`)+spawn egg 模型；配方 focus/thrall（thrall 要 3×`c:rods/breeze`+wind_charge）；patchouli `focuses/wind/breeze_focus` + `summons/breeze_servants`（en 有、zh 无要自写）。
- **worldgen 2 件（`structure/mobs/breeze.nbt`+`template_pool/mobs/breeze.json`）属④风神殿**（Goety-3 里唯一引用是风神殿结构 NBT 的 jigsaw），① 不需要。
- 待拍板：1.20.1 无野生 breeze → `breeze_rod` 获取途径（摆在哪/合成？thrall 需要 3 根）。
- goety2(2.5.58) 已有可用：`Summoned`（含 followGoal/getCommandSpeed/嵌套 WanderGoal/FollowOwnerGoal）、`BlockFinder.SummonRadius/SummonWaterRadius`、`MobUtil.setBaseAttributes`、`WandUtil`、`SpellType.WIND` ✓、`MagicFocus`、`IServant`（`com.Polarice3.Goety.api.entities.ally`）、`IOwned.getMasterOwner()`（default 方法）、`ModParticleTypes.HEAL_EFFECT`、`GoetyEffects.BUFF`、`EffectsUtil`。**不存在**（需自复刻）：`MobUtil.getSpecialAttackDamage`（→ utils/ServantCombatUtil）、`MobUtil.getSummonOwner`（→ utils/ServantAllyUtil）、`IMobTyped/getGoetyMobType`。

相关：[[goetyominous-soft-dep-reflection]]、[[goety2-servant-system]]、[[goety2-spell-system]]、[[abyss-enhancement-actual-names]]
