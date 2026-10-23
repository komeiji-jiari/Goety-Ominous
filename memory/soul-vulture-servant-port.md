---
name: soul-vulture-servant-port
description: AM 灵魂鹫仆从的既定行为：跟随=绕主人编队环；游荡/警戒=AM 原版巡回(乱飞→绕 perch→落→歇→再飞)；仅待命"落或原地降且不起飞"；法术/聚晶暂缓；手册条目去 amcompat/summons/
metadata:
  node_type: memory
  type: project
  originSessionId: 3cef97c8-4947-456b-ae5a-ded0755aeeea
  modified: 2026-10-09T18:15:45.015Z
---

2026-10-08 落地 AM 灵魂鹫仆从（`common/entities/ally/am/SoulVultureServant.java` + 注册/配置/模型/渲染器/刷怪蛋/语言/nether_heal）。

用户明确拍板、**别当移植漏抄去"修正"** 的偏离：

1. **法术 / 聚晶（MagicFocus + SummonSpell + SpellConfig）暂缓，本次不写。**
2. **手册条目归 `amcompat/summons/`**（本次也没写，等要做时去那儿）。
3. **栖息按状态分三套**（AM 是野生生物，只有"游荡"一种）：
   - **游荡 / 警戒**（`isStationed() && !isStaying()`）：**AM 原版巡回原样搬回** —— 没 perch 就 `FlyRandomGoal` 随机乱飞（`getBlockInView` 取前方 10–19 格、地面上方 5–9 格的点）+ 每 20–40t 重搜 perch；搜到就 `PerchGoal` 绕 `perchPos`（骨头/原木）半径 5–10、高度 +1+yLevel(0–2) 盘旋 `maxCirclingTime`(360–439t)，然后俯冲落到 perch+1.1 并写 `landingCooldown = 400+rand(1200)`；落地期间不起飞，冷却一归零就 `shouldTakeOff()` 再次起飞，绕**同一个** perch 再来一轮
   - **待命**（`isStaying()`）：**完全不管 perch**——tick 里 `stationed && !isStaying()` 才走搜点/校验/清点那一段，所以待命态 `perchPos` 恒为 null（连带 `PerchGoal` 永不启动，`FlyRandomGoal.canRoam()` 也被 `!isStaying()` 挡掉）。直接**原地垂直降落**：`getGroundBelow()` 找脚下第一块"有碰撞且非液体"的方块，moveControl 以 `DESCEND_SPEED=0.4` 降到其上方 1.1 格，进 0.5 格内才 `setFlying(false)`；液体或虚空上方不降、只悬停。**落地后永不起飞**（`shouldTakeOff()` 里 `isStaying()` 直接 false），也不写 `landingCooldown`，所以切走当刻就能起飞
   - **跟随**（`canCircleOwner()`）：**编队环**（`FollowFormationGoal`，优先级 1）——共享时钟 `gameTime * ORBIT_SPEED(0.022) * orbitScale` 出角度，加 `2π * index/size` 分槽，所以无同步包；槽位索引每 10t 按 `owner` 16 格内同主人秃鹫**实体 ID 排序**重扫（确定性）；半径在 `FORMATION_RADIUS_MOVING=2.5`（主人在走，贴头顶）与 `IDLE=6.0`（主人停，成圈）之间以 `Mth.lerp(0.08F, ...)` 过渡，高度 `OWNER_CIRCLE_HEIGHT=6`，速度 `1.4/0.8`
     - `start()` 里按 `getId()` 定三样、重启不变：`orbitScale = 0.88 + (id%7)*0.04`（0.88–1.12，**每只角速度不同**→互相追越，用户明确要"不是均匀分布、有的快有的慢"）、`radiusOffset = (id%4)*0.6`（0/0.6/1.2/1.8 加到半径）、`heightOffset = (id%3) - 1`（-1/0/+1 加到高度）。后两项是**为了解"互相挤压"加的立体偏置**：角度撞上时一外一内/一高一低错过，不重叠
     - 副作用：`gameTime` 数量级很大（万级 tick），乘上各自的 `orbitScale` 后那只 `2π*index/size` 分槽项被淹没 → **实际起始相位就是各随机的**，不会整齐排开。这是想要的效果，别当 bug 修
     - AM 的四处随机（半径 2.5–5、顺时针、速度 0.8–1.2、`yLevel` 0–2）和单只各自 18–22s 收敛到中心仍然没有，永不落地
   - 三套互斥靠两根开关：`isStaying()` 分待命/巡回（待命的 `perchPos` 恒 null，于是 perch 段与乱飞段自然全落空），`perchPos == null` 再分 perch 段/乱飞段；乱飞那条额外加了 `isFlying()` 门（AM 只查 `perchPos != null || shouldSwoop()`），免得刚落地那一帧用陆用 MoveControl 走路
4. AM 原版那条"有目标时 `setPerchPos(target.above(7))`"（战斗中绕目标盘旋）**没有照抄**，改成独立的 `CircleTargetGoal`（优先级 2）做等价的事 —— 理由见下面"扑击冷却空窗"那条。
5. **扑击冷却空窗（2026-10-08 用户报"为了成环不去打敌人"）**：`TackleGoal.canUse()` 只看 `shouldSwoop()`（`getTarget() != null && tackleCooldown == 0`），命中后 tick 里把 `tackleCooldown = 100+rand(200)` 并当场 `stop()`，于是**冷却那 5–15 秒里没有任何 MOVE goal 是活的**（FollowFormation 要 `getTarget()==null`、Perch/FlyRandom 要 `isStationed()`、FlyToOwner 也写了 `getTarget() != null → false`）。而 AM 的 `FlyMoveControl` 是**粘滞**的——只在 `d5 < 0.3` 时才把 `operation` 清成 WAIT，不像原版每 tick 清——所以它会一直朝最后一次那个 wanted 位置（编队环的 slot）飞，然后停在主人头顶。补法就是 `CircleTargetGoal`：`getTarget() != null && !shouldSwoop()` 时绕目标半径 4–6、高 +2..4 盘旋，冷却一归零它自己 `canUse()` 变 false 让位给 TackleGoal。AM 用 `PURSUIT_ORBIT_SPEED` 对应它 AICirclePerch 里的 `0.05235988f`。

除上述外按 AM 逐条对齐：3 个 sync 数据（FLYING/TACKLING/PERCH_POS）、四个 public progress 浮点、双导航器切换（飞=DirectPathNavigator+自研 FlyMoveControl，陆=GroundPathNavigatorWide+原版 MoveControl）、免摔落伤害、`setNoGravity(isFlying())`、火免、nether 王冠免寿命。AM 的目标表（打玩家/猪灵/村民）整块不移植，索敌交给 Goety `Summoned`。

**6. 灵魂等级 / 攻击回血整块删除（2026-10-09，用户拍板）。** AM 那条 `TackleGoal` 里的吸血——`target.hurt()` 返回真 && `getHealth() < getMaxHealth() - dmg` && `soulLevel < 5` → `setSoulLevel(+1)` + `heal(dmg)` + `broadcastEntityEvent(68)`——是 **soulLevel 唯一增长点**，用户确认"按原诉求全删"，连下游一起清干净：

   - 删：`SOUL_LEVEL` sync 数据、NBT 的 `"SoulLevel"` 存取、`get/setSoulLevel`、`hasSoulHeart()`、`handleEntityEvent` 的 68 分支（SOUL 粒子）、渲染器 `SoulVultureGlowLayer` 里 `hasSoulHeart()` 触发的 flames 重绘 + `getFlames()` + `TEXTURE_FLAMES_0/1/2` 三个常量、`tick()` 里 `isClientSide && hasSoulHeart()` 门控的翼尖 `SOUL_FIRE_FLAME` 粒子、模型里 `bloatScale` 驱动的 `heart.setScale(...)`
   - **保留**：`heart` 这个 ModelPart 本身（AM 原始几何，写在 `body` 下 z=-6 的 2×2×2 盒，`parts()` 里常驻）——它不属于 soulLevel 机制，删了会改鸟的默认外观；`TEXTURE_GLOW` 那层 ghost 重绘也不是魂火，照旧保留
   - 后果：这鸟不再冒蓝火、没有"魂心"形态
   - 拿 `heal` 时顺带作废的条件：`getHealth() < getMaxHealth() - dmg`（"不溢出"）与 `soulLevel < 5`（层数上限）——用户中途提过"无上限"，确认是"全删"而非"去掉 5 层上限"
   - **最终形态（2026-10-09 定稿）：扑击命中改为给主人灵魂能量，不回血。** 用户先说要"打多少回多少的吸血"（回血 = `ATTACK_DAMAGE` 属性值 4.0，**明确选属性值而非实际伤害**——打高护甲目标会"多回"），随后改口"和 goety 的收割者仆从一样，每次攻击窃取5灵魂能量"，并拍板**换掉**回血、**照收割者结构**发放。现在 `TackleGoal` 命中块是：

     ```java
     if (SoulVultureServant.this.getMasterOwner() instanceof Player master) {
         SEHelper.increaseSouls(master, 5);            // 第一道：进招无条件
     }
     float dmg = (float) SoulVultureServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
     if (target.hurt(...) 
             && !(target instanceof IOwned owned && owned.getMasterOwner() == SoulVultureServant.this)
             && SoulVultureServant.this.getMasterOwner() instanceof Player master) {
         SEHelper.increaseSouls(master, 5);            // 第二道：命中且非自家仆从
     }
     ```
     单目标一次扑击实得 **10**。无 heal、无粒子、无外观联动。

   - **灵魂能量的数从哪来**：Goety 收割者是 `SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * 5)`，该键默认 **1** → 每次 5 点（`.class` 常量池核实：`ItemConfig.java:281`）。**我们写死 5，不读 `ItemConfig.DarkScytheSouls`** —— 读它会让灵魂鹫跟"暗影镰刀"联动，正是 [[lm-servant-damage-config]] 那轮解耦掉的同类坑
   - 收割者结构的两道发放（`AbstractReaper$MeleeGoal.attackMobs`，反编译在 `.scratch/reaper/out/`）：第一道在进招时无条件给，第二道在命中循环里按每个有效目标给，且 `victim instanceof IOwned && owned.getMasterOwner() == reaper` 时**跳过**第二道。收割者本身**不回血**，所以"和它一样"= 不回血
   - 已过 `./gradlew compileJava`（BUILD SUCCESSFUL）

- 覆写了 `followGoal()`（自带 `FlyToOwnerGoal`，因为 DirectPathNavigator 不产生 path、`isDone()` 恒 true，Goety 默认 FollowOwnerGoal 会立刻放弃）与 `commandMode()`（同因，改用 moveControl 驱动）。
- 简化掉 AM 的死代码 `shouldLeavePerch()`：外层已要求 `landingCooldown == 0`，里面的 `dist > 13 || landingCooldown == 0` **恒 true** → AM 那条实际就等价于「落地且 `landingCooldown == 0` 就起飞」，dist>13 那半永远轮不到。别再照抄回来。
- 落地加了 `isPerchBlock` 门（AM 无门，中心是空气时会从高空 `setFlying(false)` 直坠再扑回来）。
- `shouldTakeOff()` 四段：有目标→起飞；非跟随→起飞；待命→**不起飞**；其余（游荡/警戒）→ `landingCooldown == 0` 就起飞。
- 命名坑：状态谓词不能用 `canStay`/`canWander`/`canGuardArea`/`canFollow`（IServant 能力位同名会编译失败），也不能叫 `isStaying`；用的 `isStationed`。
- **仆从互推只在待命时关掉**：`Summoned.push/doPush/canCollideWith` 都是 `if (!isStaying())` 才放行（`Summoned.java:616/623/630`），所以跟随态一旦两只 bounding box 重叠就会被反复弹开——"编队互相挤压"就是这么来的，靠让 slot 在三维上错开解决，不要去关 push。
- 编队环的数值是按"线速度不变"配的：半径 3.5→6.0 的同时 `ORBIT_SPEED` 0.035→0.022，`FlyMoveControl` 的寻的式增量（`delta += vector3d * speedModifier * 0.05 / d5`，水平每 tick ×0.91，终端 ≈ `speedModifier * 0.556` 格/tick）远没到饱和，所以加速度不是瓶颈、抖动来自寻的过冲。
- **编队环已进游戏实测调过一轮**（2026-10-08：用户反馈"环更高更宽、鸟不要均匀分布"）。其余（待命原地降、游荡/警戒 AM 巡回）仍未验证。
- 游荡/警戒盘旋时长用的是 `maxCirclingTime`(360+rand(80))，而 AM 源码里那句是硬编码 `circlingTime > 360.0f` —— `maxCirclingTime` 在 AM 是**只赋值从不读**的死字段。按"AM 想随机"的意图取了生成值。

7. **召唤时按主人灵魂能量放大血量与攻击（2026-10-09，用户拍板）。** `finalizeSpawn` 里快照一个 `soulScale`：

   - `soulScale = 1.0F + (2.5F - 1.0F) * clamp(getSoulAmountInt(player) / MaxArcaSouls, 0, 1)` —— 0 点 → **1.0×**，十万 → **2.5×**，中间线性
   - **用 `finalizeSpawn` 而非 `setConfigurableAttributes()`**：后者只在读档时被调（见 [[goety-configurable-attrs-reset-on-load]]），既是错的时机又会把倍率抹掉。主人此时已就位（召唤法术与刷怪蛋都是 `setTrueOwner` → `finalizeSpawn`；蛋会 finalize 两次，第二次才带主人，正好补上）
   - 只放大 **MAX_HEALTH 与 ATTACK_DAMAGE**（基础值 × soulScale），FOLLOW_RANGE 不动。`finalizeSpawn` 里补了 `setHealth(getMaxHealth())`——实体构造期已按旧上限填满血，不补的话召唤出来是 12/30
   - 倍率**冻结在召唤那一刻**，存 NBT 的 `"SoulScale"`（`readAdditionalSaveData` 里在 `super` **之前**读回），所以重载/进方匣再取出都不会重算
   - `SEHelper.getSoulAmountInt(player)` 是统一读数：SE 开启时返回方舟池，否则返回灵魂图腾当前量，都没有则 0。**注意图腾上限只有 `MainConfig.MaxSouls` = 10000**，所以纯图腾玩家最多吃到 `1 + 1.5×0.1 = 1.15×`；2.5× 只有方舟满（10 万）才可能
   - 读 Goety 的 `MainConfig.MaxArcaSouls`（默认 100000，可配）**不是**跨 mod 配置耦合的违规——仓库里 `BrainEaterSpell.java:114` 已经这么读了，它属于"世界常量"而非我们要调的自有旋钮
   - `getMasterOwner()` 能解析出玩家：`IOwned.getMasterOwner()` 是向上走 owner 链的 default 方法，玩家既非 `IOwned` 也非 `OwnableEntity`，第一轮就 break 返回玩家本体
   - 已过 `./gradlew compileJava`（BUILD SUCCESSFUL）

8. **索敌搜索盒纵向撑开（2026-10-09 定稿，实测通过）。** 用户报「战斗时一部分鸟宁可绕着我飞，只有低空掠过的那几只上去打；我自己动手打怪则全体扑上去」。根因是原版 `getTargetSearchArea` 纵向只有 ±4 格，悬停在主人头顶 6~10 格的鸟压根罩不到地面怪（详见 [[goety-target-search-area-vertical-4-blocks]]）。修法：覆写 `targetSelectGoal()` 注册 `HoverTargetGoal extends SummonTargetGoal`，把 `getTargetSearchArea(distance)` 改成三轴都 `inflate(distance)`。**别再改回去**，也别忘了这条钩子是 Goety 注册原版 `SummonTargetGoal` 的地方（同 `followGoal()`/`ownerHurtGoals()`）。
   - 配套：`AttributesConfig.SoulVultureServantFollowRange` 默认值 **18.0 → 32.0**（18 是从 AM 抄来的，作为径向 leash 对「绕主人 6~10 格 + 够主人的敌人」太短）。live 实例的 toml 已同步（`config/goetyominous/goetyominous-attributes.toml`，游戏会重写该文件但保留值）。
   - 旁支未解：日志里每只鸟 `getAttributeValue(FOLLOW_RANGE)` 实测各不相同（30.26 / 31.93 / 33.73 / 34.78），而 `setConfigurableAttributes()` 写死配置值。不影响（都远大于所需），未追。

相关：[[goety-servant-follow-standby]]、[[vanilla-goal-cancontinue-default]]、[[goety-iservant-capability-vs-state]]、[[goetyominous-mod-folder-isolation]]、[[goety-configurable-attrs-reset-on-load]]、[[goety-target-search-area-vertical-4-blocks]]
