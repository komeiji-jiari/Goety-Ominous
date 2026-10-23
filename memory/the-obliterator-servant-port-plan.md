---
name: the-obliterator-servant-port-plan
description: "The Obliterator 移植为 LM 仆从已拍板的决定 + 要剥的 boss 机制清单 + 死代码清单 + A 段进度(2026-09-26)"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6a57ba39-21f0-4b79-99c1-e80b3d1212e3
  modified: 2026-09-26T16:40:36.806Z
---

2026-09-26 用户决定把 LM 的 boss **The Obliterator** 移植成 goetyominous 的仆从。

## 拍板的决定

1. **基类 = `extends IAnimatedBossServant`** —— 与 LM 原版同层。
   ⚠️ 这条**推翻过一次**：原先定的是 `IAnimatedMiniBossServant`，理由是"仓库已有 4 个 LM 仆从都用它、`IAnimatedBossServant` 是凭空发明的层级"。**前半句错了** —— `IAnimatedBossServant.java` 早就在仓库里（2026-09-25 为圣骑建的，`PossessedPaladinServant:109` 就继承它），我上一轮没查全就下的结论。后半句也不成立：仓库的规矩是**照抄 LM 原版的层级**（用 `.class` 常量池解 super_class 核过 8/9 一致，唯一例外 `CloudGolemServant` 是因为它比 `IAnimatedBossServant` 早一天提交）。LM 的 `TheObliteratorEntity extends IAnimatedBoss` ⇒ 仆从也用 boss 层。判据见 [[lm-servant-tier-from-class-superclass]]。
2. **血量 = vanilla 血量**，不移植 `totalDamageTaken` 虚拟血量三件套。原因见 [[lm-ianimatedboss-virtual-health]]（照抄 = 永久满血打不死的仆从）。
3. **硬免疫**：保留 摔落 / 溺水 / 火焰 / 冰冻 免伤（`IAnimatedBossServant.hurt` 已自带）、保留一阶段箭矢/投掷物免疫、保留 `push(DDD)` 空实现（击退免疫）。**砍掉反逃课免伤**（`antiCheeseDistance` 默认 15 格 + 目标在距离外免伤，是 boss 关场地用的，仆从会变成主人拉不动）。

## 要剥掉的 boss 专属机制

- `breakCheeseBlocks()` —— **是真实现不是空壳**：AABB 膨胀 1.0、吃 `mobGriefing` 门控、调 `level().destroyBlock()`。每挨 2 次打就砸一圈，仆从放家里会拆房。
- `LMBossInfoServer` + `bossInfo` 字段（含 darkenSky、每 tick 广播血量）+ boss 音乐 `OBLITERATOR_STEREO`。
- `die()` 里 `ModSavedData.get(level).setDefeatedObliterator(true)` —— **污染存档进度**。
- 出生点归位传送（`ticksWithoutTarget` 40 / 脱战回巢）、`regainHealthWithoutTarget(20,15)`、`canBossesResetPhases` 阶段重置。
- `TheObliteratorPart` / `tickPart(...)`（定义了从不调用，`isMultipartEntity()` 直接 `return false`）。

## 死代码，别费劲移植

- **整条格挡链是死的**：`ParryGoal.canUse` 要求 `getPhase() >= 4`，而全包 `setPhase(` 调用只有 1/2/3（含 NBT 读），**没有任何地方 setPhase(4)**。~~用户明确说不用搬~~ —— 见下方第六轮，2026-09-26 已整条删除。
- `hurtCD`（写 30 减 30，无读者）、`IS_SECOND_PHASE`/`IS_THIRD_PHASE` 两个同步 bool（getter 读的是 `PHASE`，bool 只写不读）。
- `AmbushSwapGoal` 不可达（`randomizeNextAmbushType()` 两个分支都赋 2）。
- `getNextDimensionalShootType()` 返回错字段。
- `m_7822_(byte)` 覆写里 if/else 两支都调 super。

保留 `randomizeNextAmbushType` 里 `getRandom().nextInt() * 100 < 50` 那类有符号溢出写法——按 parity 惯例照搬，见 [[lm-nextint-overflow-random-gate]]。

## 主体结构备注

`UpdateWithAttack()` 横跨 ~1750 行（源文件 2372–4119），完全按 `getAttackState()` 0–62 + `attackTicks` 硬编码分支。`registerGoals()` 795 行 / 29 个 goal 类（圣骑只有 15）。客户端借得到的只有 `TheObliteratorModel.createBodyLayer()`（`public static`）和 `TheObliteratorAnimations*` 那 15 个类的 `public static final AnimationDefinition`；模型本身是 `HierarchicalModel<T extends TheObliteratorEntity>` 泛型+私有字段，继承不了，只能写薄适配器（照 `AnnihilationPursuerServantModel` 样板）。

配置依赖约 12 条 `ModConfig.MOB_CONFIG.*` —— 硬编码数值不要换成属性值（[[ac-servant-parity-keep-original]]）。

版本：编译依赖解析到 `legendarymonsters-2.1.20`，实测实例是 2.2.2；`.class` 大小 134048 vs 134042、内部类都是 59 个，结构等价。判据见 [[lm-mechanic-presence-version-check]]。

## 进度

- **A 段已完成（2026-09-26，`./gradlew build` 过）**：实体骨架（全部 72 个 AnimationState + 冷却/RNG 字段 + `getAnimationState`/`stopAllAnimationStates` + tick/aiStep + 死亡 102 tick + 一阶段投掷物免疫 + `push` 空实现）、模型适配器（借 LM 的 `createBodyLayer` + 66 条 `animate`）、渲染器（3 张阶段贴图 + `poseStack.scale(0.8)` + partXRot/partYRot 回写）、注册/属性/配置/lang 全套。
  刷怪蛋**故意注释掉**（沿用圣骑的 WIP 惯例），`/summon goetyominous:the_obliterator_servant` 可用。
- **B 段已完成**：29 个 goal 类 + `registerGoals`（56 项，逐参数与 LM 对齐过）。
- **C 段已完成**：`UpdateWithAttack` 状态机 + `onSyncedDataUpdated` + 全部 helper。
- **D 段已完成**：5 个渲染 layer + 粒子 + 分身（`TheObliteratorClone*`）。
- **四路并行审查 + 修复已完成（2026-09-26，`compileJava` 过）**，六处：分身 AoE 盟友判定（`isAlliedTo` 是原版纯队伍判定 → 打主人）、`hurt()` 补格挡入口、`positionRider`/`getDismountLocationForPassenger`/两处 `stopRiding`、`runAway` 补 `this.position()`、`tryKill` 二次确认。
  - ⚠️ 其中 ③「补回 `canParry()` 让 `gambitParry`/`canParry()` 复活」**是错的**（2026-09-26 第六轮推翻）：`canParry()` 的门是 `getAttackState() == 57 || (== 56 && attackTicks > 20)`，而 56/57 同样只由 `ParryGoal` 产出 → 那段还是死代码。当时只查了 `hurt` 的入口，没往上追状态 56/57 的来源。
  - 两处**主动偏离 LM**：① `positionRider` 的 C 段在 LM 里 gate 自相矛盾（外 `50||49` 内 `50||39`）→ state 49 算了不应用、39 根本进不去；仓库实际有 4 个上人状态（18/37/39/49），照抄会让两个继续坏，故统一成 `50||49||39` 且无条件 move。② 那些"格挡链是死的"结论**只对 LM 成立**：LM 里 `hurt` 确实没有格挡入口，但仓库的 `:1259` 有 `state==52` 的 `isDuringTeleportation` 窗口 → 状态机侧预期 52 可达 → 补入口。
- **第二轮全量复查 + 修复已完成（2026-09-26，`compileJava` 过）**。复查用脚本 `.scratch/oblit_c/_statemachine.mjs`（写入状态集 / OSD case 集 / `UpdateWithAttack` 处理集三方对照，仓库 vs LM）与 `_animcheck.mjs`。结论：**状态机与 LM 零漂移**（四组差异全空），动画无孤儿。
  - 修复：① 删掉 `canChangeDimensions()` 覆写（返回 false 是移植引入的异常，LM 与全部同级仆从都没有）；② `hurt()` 免疫表的 `&&`/`||` 优先级 —— LM `:4544` 逐字如此，`!BYPASSES_INVULNERABILITY` 只绑定了 `isDuringTeleportation` 一项，改成外层包裹，让 bypass 伤害（含 `/kill`、虚空、解散）穿透全部免疫项（**主动偏离 LM**）；③ 补回 LM `:4547-4553` 的 `canParry()` → `setAttackState(59)` + `gambitParry = true` + CameraShake + 音效，`gambitParry`/`canParry()` 因此复活；④ 补 `canReduceDamageDuringPhaseTransition()`（crackiness MEDIUM/HIGH 时 `hurt` 里 `amount *= 0.5F`，LM `:4537`）—— 注意 `Crackiness` 枚举与 `getCrackiness()` 仓库**早就有了**（:124-152，被两个 phase goal 用着），只缺这个方法本身；⑤ 删 6 个 parity 死字段（`sleepAnimationState`/`awakeAnimationState`/`smashGroundKickAnimationState`/`KickAnimationState`/`KickSmashAnimationState`/`KickByeAnimationState`）+ 对应的 6 行 `stop()` + `getAnimationState` 里 `awake`/`sleep` 两个死分支。删后 66 字段 / 67 分支 / 67 模型键，`_animcheck.mjs` 五项全"无"。
  - **`vertex3` 的符号疑点已排除**（挂了两轮的假警报）：LM 的 `vertex2` 与 `vertex3` 本来就逐字相同，是反编译器给 `vertex2` 的形参起了**反序**的混淆名（第3个叫 `p_253704_`、第4个叫 `p_253701_`），肉眼像两样。代入调用点 `(f3,f4)` 后两者都产出 `(-HALF_SQRT_3*f4, f3, -0.5*f4)`。
  - **仍未动**：命令模式 `isCommanded()` 门控（family 一致）；`UpdateWithAttack` 里状态 7/8/16/25/26/27 不可达（LM 同样不可达，低置信度，未删）。
- **第三轮修复：`aiStep` 的 invulnerable 看门狗**（2026-09-26，`compileJava` 过）。移植时把 LM `:614` 的 `if (!isDuringTeleportation && isInvulnerable() && !cs) setInvulnerable(false)` **静默换成了清 glowing 标记**（`isCurrentlyGlowing()`/`setGlowingTag(false)`）—— LM 全库从没在实体本身上用过 `setGlowingTag`（只在掉落物上），这个分支清的是个没人设的标记。
  - 为什么必须有：`AmbushSwapGoal`（状态 14）在 tp1/tp3 置 `setInvulnerable(true)`，而**所有清除点都带 `getTarget() != null` 守卫**（:104/:126/:164）。突袭途中目标死了/被清掉 → 永久 `invulnerable = true`。且该标志**会写进 NBT**（`Entity.saveWithoutId` 的 "Invulnerable"），跨存档永久免疫普通伤害。LM 那条看门狗就是兜它的。
  - 安全性已核：全 Goety jar（3125 个 class）只有 3 个类碰 `setInvulnerable` —— `SlimeServant`（专属）、`Vizier`（boss 自管）、`MobUtil.copy`（拷贝实体标志的辅助方法）。`Summoned` 基类完全不碰，所以看门狗不会误清 Goety 的状态。
  - **教训**：这个文件的 `aiStep`/`tick` 段已出过 3 处「看着合理但和 LM 对不上」的静默替换，移植时别信印象，逐行 diff LM。

- **第四轮：伤害配置改归 Goety 侧**（2026-09-26，`compileJava` 过）。用户拍板「偏离 LM」并划线（[[ac-servant-parity-keep-original]]）。
  - 新增 `MobsConfig.TheObliteratorServantDamageMultiplier`（默认 1.0）+ `common/events/TheObliteratorServantEvents`（`LivingHurtEvent` 按施暴实体缩放）；删掉 `TheObliteratorServant` 里 8 处 LM 的 `TheObliteratorDamageMutliplier`。默认值下伤害与 LM 逐位相同。
  - 用事件而非就地乘，是为了覆盖投射物/分身的伤害（那些在别的类里结算且被多个仆从共用）。细节与遗留见 [[lm-servant-damage-config]]。
- **第六轮：整条格挡链已删**（2026-09-26，用户「死代码删了」，`compileJava` 过）。用户问我三选一（放宽到 `>= THIRD_PHASE` / 保持 parity / 整条删）后直接按仓库「死账全删」的惯例（[[goetyominous-deadcode-cleanup-preference]]）选了删。
  - 删掉：`goals/obliterator/ParryGoal.java` + `ParryFrameStateGoal.java` 两个文件；`registerGoals` 里那 4 项（ParryGoal 匿名子类、ParryFrameStateGoal、state 58/59 两个 `IStateGoal`）；`canParry()` / `isParrying()`；`hurt()` 里的 `|| isParrying()` 与 `canParry()` 分支；`gambitParry` / `parry_cooldown` / `parryRandom` / `PARRY_COOLDOWN` 四个字段与 tick 递减；`UpdateWithAttack` 的 state 56 与 59 两块；`SideAreaAttack`/`SideGrab` 里 state 59 才命中的 `heal(20)`；`isDuringTeleportation`/`isHittingWithBlades` 里的 56/59 子句；OSD 的 case 56–59；4 个 `Parry*AnimationState` 字段 + `stopAll` + `getAnimationState` 分支 + 模型 4 行 `animate`；渲染层 `UltimateFlameLayer` 的 `== 53 || == 56 || == 59 || == 58 || == 57` 化简成 `== 53`。
  - 验收：`_animcheck.mjs` 五项全「无」（62 字段 / 63 分支 / 63 模型键），`_statemachine.mjs` 与 LM 的差异**只剩这 4 个状态**（E/H 两栏 56–59），没有连带漂移。LM 的 `TheObliteratorAnimations14.parryPre2/parryFrame2/parryFail/parrySuccess4` 在 LM jar 里，删不掉也不该删，只是不再被 animate。
- **第五轮：同一改造推广到全部 10 个 LM 仆从**（2026-09-26，用户「lm其他的也改」，`compileJava` 过）。挂钩点改成公共祖先 `IAnimatedMonsterServant.damageMultiplier()`（默认 1.0），事件类改名 `common/events/LmServantDamageEvents`（按 `instanceof IAnimatedMonsterServant` 门控）。`MobsConfig` 新增 9 条 `<x>ServantDamageMultiplier` + `theObliteratorCloneBurstHpDamage`（默认 7）；清掉 LM 键读取点共 27 处（含 3 个共用投射物和分身那处 HP 伤害）。默认值下伤害与 LM 逐位相同。**未动**的是 `MiniBossDamageCap`/`CloudGolemDamageCap`（受伤上限，防御侧）与那批行为开关，已列在 [[lm-servant-damage-config]] 里等拍板。

- **第七轮：非"倍率"类 LM 键也迁走**（2026-09-26，用户「单独定默认值语义 一并迁到 MobsConfig」，`compileJava` 过）。10 条键（2 个受伤上限 + 8 个行为开关）改成仓库自有配置，默认值逐条对齐 LM。落点按上游规矩：**Cap 进 `AttributesConfig`、开关进 `MobsConfig`**（用户让我参考 goety_cataclysm 后核实）。**全仓库至此零 `ModConfig.MOB_CONFIG` 引用**。清单、默认值来源与"哪个键该进哪个文件"的判据见 [[lm-servant-damage-config]]。

交付面按 [[no-unrequested-recipes-patchouli]]：只做 注册/属性/模型/lang/config，配方与帕秋莉手册不碰。
