---
name: tiger-servant-port
description: AM 老虎仆从移植：AnimalSummon 基类、贴图直接引用 alexsmobs:、刻意丢掉的几块 AM 行为
metadata: 
  node_type: memory
  type: project
  originSessionId: 53462acd-2a0d-4cfe-a5ce-4cdc272ebc43
  modified: 2026-10-11T13:10:25.182Z
---

2026-10-11 移植 AM 老虎为 `TigerServant extends AnimalSummon`（有意不用 `Summoned`），模板是獠牙兽（TusklinServant）那条线。

- 待命坐姿：Goety 没有 `isSitting()`，本地定义 `isSitting() { return isStaying(); }`（RockyRoller/Froststalker 先例），AM 的 sit 姿势在待命时播放，`AIMelee` 用 `!isSitting()` 门控。`shouldMove() { return !isHolding(); }` 只在压制（HOLD）时清零 travel —— 待命不用管，`ServantWanderGoal.canUse()` 本来就 gate 了 `!isStaying() && !isCommanded()`。
- 贴图不复制：渲染器直接指 `alexsmobs:textures/entity/tiger/*.png`（含 eyes 层）。安全性来自"实体类型与渲染器都只在 AM 加载时注册"，先例是 `LayerTusklinServantGear`。
- 刻意丢掉：`NeutralMob`/`AngerGoal`（会反咬主人）、SLEEPING 状态与贴图分支、`ITargetsDroppedItems`、TIGERS_BLESSING 跨 mod 契约、`AttackPlayerGoal`。
- 冲刺不再写死：AM 是 `setBaseValue(0.4f)`，这里改成 `AttributesConfig.TigerServantMovementSpeed.get() + 0.15D`，否则配置改移速会被冲刺覆盖回 0.4。
- 繁殖的白虎概率在 `getBreedOffspring` 里自己摇（双白 0.8 / 单白 0.4 / 其他 0.1），AM 原版靠 `isWhite` 继承+NBT。
- 暗兽(Goety `BlackBeast` = 暗兽,`black_beast_*_armor` 那两只铠甲同名)同款喂食+摸头：**喂肉**（`ItemStack.isEdible() && getFoodProperties(this).isMeat()`，这是 BlackBeast 自己的 `isFood`，不是 TIGER_BREEDABLES）且没满血 → `heal(nutrition)` + 手动 shrink + `GameEvent.EAT` + 7 颗 HEART；**空主手右键** → `happyCool = 40`（tick 里递减）+ `WOLF_AMBIENT`(1.0, 0.5) + 5 颗 HEART + `heal(1.0F)`。暗兽砍掉了三处：不调 `this.eat()`（上游双扣，见 [[forge-livingentity-eat-double-consume.md]]）、粒子用 `ParticleTypes.HEART` 而非 `ModParticleTypes.HEAL_EFFECT`（[[goetyominous-feed-particle-heart.md]]）、音效只发一次（上游 `m_5496_` 与 entity event 102 各放一遍=双声）。
- 摸头分支额外加了 `!this.isFood(itemstack)` 门控：老虎的繁殖食物 `alexsmobs:acacia_blossom`（AM 的 `tiger_breedables` tag 只有这一项）走 `super.mobInteract`，不挡这一条的话"副手拿花 + 空主手右键"会变成摸头而不是繁殖。
- 暗兽的"主人拿着食物靠近就歪头"也搬了：内嵌 `BegGoal`（priority 9、只占 LOOK flag、lookDistance 8、lookTime 40+rand(40)）→ `setInterested(true)` → `INTERESTED` 同步位 → `tick()` 里两侧都跑 `interestedAngle += (目标 - 当前) * 0.4F`（客户端要自己跑，模型读的就是它）→ `getHeadRollAngle(partialTick)` 给模型。模型侧在 `setupAnim` **最后** `this.head.rotateAngleZ += entity.getHeadRollAngle(partialTick)`（用 `+=` 而不是赋值：上面的 walking flap / 压制 flap 已经写过 zRot，赋值会把那些摆动抹掉）。
- 摸头音效先用的是暗兽的 `SoundEvents.WOLF_AMBIENT`(1.0, 0.5)，用户 2026-10-11 否掉："声音不用狼嚎 用老虎自己的" → 改成 `AMSoundRegistry.TIGER_IDLE.get()`(1.0, 0.5)。歪头的"感兴趣物"= 肉（`getMeatFood`）或繁殖花（`isFood`）都要算，因为老虎的喂食物和繁殖物是两样。

- 游荡速度对齐 AM（2026-10-11 用户要求"游荡速度和原版一样"）：`Summoned.WanderGoal<>(this, 1.0D, 60, 0.001F)`。AM 那边是 `AnimalAIWanderRanged(this, 60, 1.0, 14, 7)`，两者都是 `RandomStrollGoal` 语义（第 3 个参数=`random.nextInt(interval)!=0` 的间隔、speed 都是乘在 MOVEMENT_SPEED 上的倍率），所以倍率 1.0 + 间隔 60 就是同速同频。**没对齐的**：Goety `ServantWanderGoal.getPosition()` 把 `landRandomPos(10, 7)` 写死（AM 是 14/7），因此单次游荡距离比 AM 近；要改得覆写 `getPosition()`，会连带复制 guardingArea/水中分支，暂留。
- **主动猎杀：2026-10-11 当天先加后撤，现在没有。** 曾按 AM 原样加了 `targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 220, false, false, AmEntityRegistry.buildPredicateFromTag(AMTagRegistry.TIGER_TARGETS)))`（匿名子类 canUse 补 `!isBaby() && !isStaying() && !isCommanded()`），用户看过之后要求"撤回老虎主动攻击的改动"，已删除（含 import），只留游荡速度那条。**别再自动加回去**，要加先问。留着结论备用：①**不能用 Goety 的 `Summoned.NaturalAttackGoal`/`ServantNaturalAttackGoal`** —— `canUse` 要求 `isNatural() && getTrueOwner() == null`，有主仆从恒 false；②搜索半径取 `TargetingConditions.range(getFollowDistance())` = FOLLOW_RANGE 配置（默认 32），搜索盒横向 ±32/纵向 ±4（[[goety-target-search-area-vertical-4-blocks]]，老虎是地面怪不受影响）；③主人与主人名下宠物天然安全（`TargetingConditions.forCombat()` 的 `canAttack`+`isAlliedTo`，`Owned.isAlliedTo` 已含 owner 及其 `OwnableEntity` 宠物）；④`alexsmobs:tiger_targets` = `#alexsmobs:passive_land_animals`（牛/羊/猪/鸡/狼/猫/马等）+ `#alexsmobs:villagers`（村民、流浪商人）+ 袋鼠/驼鹿/长鼻猴/凯门鳄 —— 会咬自家农场和村民，这大概是用户撤掉它的原因。

**Why:** 这几条都是"看着像漏抄、其实是有意为之"的差异，重读代码时容易被当成 bug 改回去。

**How to apply:** `AnimalSummon extends Summoned`（不是 `Animal`），但继承来的 `AnimalSummon.BreedGoal` 会**遮蔽**同名 import —— 带不带 `import net.minecraft.world.entity.ai.goal.BreedGoal` 都能编过（实测），所以别把它当成"必须有"的 import，现在已删掉。想加回愤怒/睡眠行为或那个歪头 goal 前先问。相关：[[goety-servant-follow-standby.md]]、[[ac-servant-parity-keep-original.md]]。
