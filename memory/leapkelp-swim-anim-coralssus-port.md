---
name: leapkelp-swim-anim-coralssus-port
description: "潮涌之叶水中表现=cataclysm 珊瑚傀儡(Coral Golem)移植,2026-10-03 起取代珊瑚巨像版:泳姿/划水写死在 LeapkelpModel.setupAnim(程序化 flap/walk,不是关键帧),水中近战=左右重砸36tick/命中第16tick;含 LionfishAPI flap/walk+ModelAnimator 语义、骨架映射、head是空骨的坑"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0672aaf0-77b6-46d7-81bf-27f28135f10d
  modified: 2026-10-03T17:26:34.886Z
---

潮涌之叶(`Leapkelp`)在水里的泳姿 / 划水 / 近战,2026-10-03 从 **珊瑚巨像 Coralssus** 改成了 **珊瑚傀儡 Coral Golem**(两只不同的怪,见 [[cataclysm-coral-golem-vs-coralssus]])。范围**仅限水里**,陆地砸地 `LeapkelpSmashGoal` 不动。

## 两种上游动画机制(决定了实现形状)

cataclysm/Goety-Cataclysm 用 LionfishAPI,和原版 `KeyframeAnimations` 是**两套**东西:

- **程序化正弦**(`AdvancedModelBox`):`calculateRotation(speed, degree, invert, offset, weight, f, f1) = cos(f*speed + offset) * degree * f1 + weight * f1`(`movementScale` 默认 1.0,`invert` 取负)。`walk` 加 `rotateAngleX`、`flap` 加 `rotateAngleZ`、`swing` 加 `rotateAngleY`、`bob` 加 `rotationPointY`。
  **珊瑚傀儡的泳姿+划水就是这一套** —— 不是关键帧,用原版关键帧只能采样逼近。做法:在 `LeapkelpModel.setupAnim` 里按同一公式直接写 `ModelPart`(逐行同构 = 精确复刻),`SWIM_POSE`/`SWIM` 两个 AnimationDefinition 因此成了死账,**已删**。
- **`ModelAnimator` 叠加式关键帧**:`startKeyframe(d)` 开窗口 → `rotate/move` 累加进当前窗口 → `endKeyframe()` 用 `inc = sin(tick*π/2)`(`tick=(animTick-prevTick+frameTime)/d`)在「上一窗口姿态」(`1-inc`)与「本窗口」(`inc`)间混合;`setStaticKeyframe(d)` = 保持上一姿态整段;`resetKeyframe(d)` = 衰减回 0。**珊瑚傀儡的左右重砸是这一套**,要烘成原版关键帧(新写了 `.scratch/_gen_golem.mjs` + `_verify_golem.mjs`)。

**关键:两边都是加法,所以数据可以「原样搬增量」。** 原版 `AnimationChannel.Targets.ROTATION = ModelPart::offsetRotation`(加法)、`POSITION = offsetPos`(加法),配合每帧开头 `resetPose()` 恢复烘焙角 —— 这和 `ModelAnimator` 的 `+=` 语义**完全一致**。于是 `ModelAnimator` 的增量直接就是原版关键帧值,**不用**把烘焙角/泳姿偏置折进去。

## 珊瑚傀儡的水中数据(反编译自 `CoralGolemServantModel`,goety_cataclysm)

- **泳姿偏置**(`progressRotationPrev(m, swim, rx, ry, rz, 1.0)` → `+= swim * toRadians(r)`):`root` Z **−10**、`lower_body` X **+17.5**、`head` X −22.5、`right_arm` X **+35** / Z **+50**、`left_arm` X +35 / Z **−50**、`right_leg` X **+90**、`left_leg` X **+117.5**。
  ⚠ 那条 `head` 的偏置写在**空骨**上(见下),视觉上等于没有 → **我们的 head 不加泳姿偏置**。
- **划水**(`swimSpeed=0.25f`, `swimDegree=0.5f`, 权重参数用 `limbSwing, limbSwingAmount*swim`):`flap(root, .25, .5, true, 0, 0)`、`flap(left_arm, .25, 1.375, true, -0.5, 1.5)`、`flap(right_arm, .25, 1.375, false, -0.5, 1.5)`、`walk(right_leg, .375, .5, true, 2.0, 0)`、`walk(left_leg, .375, .5, false, 2.0, 0)`。
  **没有 `alert || canAnimateMove()` 门控**(珊瑚傀儡无条件应用);静止时 `limbSwingAmount→0` 自然归零。
- **水中近战 = 左右重砸** `LEFT_SMASH`/`RIGHT_SMASH`,各 **36 tick**,命中在 `getAnimationTick() == 16`,震屏 `ScreenShake(10.0f, 0.15f, 0, 20)`(与 Goety `CameraShake.cameraShake(level, pos, 10.0F, 0.15F, 0, 20)` 逐字同构),AoE `getBoundingBox().inflate(3.0f)` 跳友军 → `hurt` **然后** `launch`(`living.push(dx/d2*2.0, 0.5, dz/d2*2.0)`,`d2` 是**平方距离** `max(dx²+dz², 0.001)`,别「修」成 sqrt)。选左右是**随机**(`getRandomAttack`),不是交替。
- 它的跳劈 `CORAL_GOLEM_LEAP` 和 23 tick 的 `CORAL_GOLEM_SMASH` **没搬**(按用户「仅限水里」,跳劈本来就是陆地/非泳态才触发)。

## 骨架映射(逐骨,`_gen_golem.mjs` 里的 `RENAME`/`DROP`)

`lower_body→body`、`upper_body→main`、`helmet→head`、`right_fist→right_wrist`、`left_fist→left_wrist`;`root`/`right_arm`/`left_arm`/`right_leg`/`left_leg` 同名。

- ⚠ **`CoralGolemServantModel` 的 `head` 是零 cube 的空骨**(只有 `setRotationPoint`,没有 `addBox`)—— `faceTarget` 和那条 −22.5 泳姿偏置写它全是**惰性的**,视觉头是 `helmet`。所以 `helmet→我们的 head`,`head` **丢弃**。DROP 判定必须按**原始名**做,否则改名后的 `head` 会被连带丢掉。
- 丢弃的其它:同名 `head`、`table_coral`/`blue_table_coral`/`red_table_coral`/`right_coral`/`left_coral`/`coral_shoulder`(我们骨架没这些珊瑚装饰骨)。
- 我们比它多:`pelvis`、`petal_*`/`inner_*`(珊瑚傀儡不碰,保持不动)。
- **绝对角必定有差**:它 `upper_body` 烘焙 +40° / 我们 `main` +50°;我们 `chest` +22.5° / 它 0°;它手臂挂在 `chest` 下、我们挂在 **`body`** 下(跳过 main);我们 `head` 另有 +15° 烘焙。策略 = **姿态增量一致**,绝对观感进游戏调。
- `move(box,x,y,z)` → `rotationPointX/Y/Z += x/y/z`(正 y = 向下),原版 `posVec(a,b,c)` 给的是 `(a,-b,c)` → 生成器要传 **`posVec(x, -y, z)`**。
- **`root` 用名字找不到**:`getAnyDescendantWithName` 特判 `"root"` 返回 `this.root()`,但 `LionfishAPI` 的 `flap(root,...)` 在生成器里要映射成 Java 侧的 `this.root.zRot += ...`(我们在 `LeapkelpModel` 构造里存了 `root` 参数)。砸地动画不碰 root,所以关键帧里不需要它。

## 落地的实现(2026-10-03)

- `LeapkelpAnimations` 现在**只剩** `RIGHT_SMASH`/`LEFT_SMASH`(`withLength(1.8F)`,36 tick,8 个采样点 `t∈{0,5,10,15,16,26,31,36}`,LINEAR)。
- `LeapkelpModel` 里 `applySwimPose(swim)` + `applySwimMotion(limbSwing, limbSwingAmount*swimAmount)` 取代了 `applyStatic(SWIM_POSE)` + `animateWalk(SWIM)`;`stroke()` 就是 `calculateRotation` 的直译。泳姿常量是 `LeapkelpModel` 的 private static final(不再是 AnimationDefinition)。
- `Leapkelp`:状态常量 `SMASH_STATE_RIGHT/LEFT`(仍 8/9)、`SMASH_HIT_TICK=16`、`SMASH_END_TICK=36`、字符串 `RIGHT_SMASH`/`LEFT_SMASH`;`AnimationState right/leftSmashAnimationState`、字段 `smashAnimation`。goal 改名 `LeapkelpWaterSmashGoal`(原 `LeapkelpPunchGoal`),`ATTACK_RANGE 4.5→3.75`、`HIT_INFLATE 3.5→3.0`,左右改随机。
- `hit()` 顺序改成 **先 `doHurtTarget` 后 `launch`**(对齐 `EarthQuake`),`launch` 用 `push` 不用 `setDeltaMovement`。音效仍 Goety `ModSounds.HAMMER_IMPACT`、粒子仍 `SmashParticleOption`+`SPLASH`(不引 cataclysm 的 `CataclysmSounds.EXPLOSION` / 方块碎片环 —— 后者的 `Makeparticle` 在水里会被 `getRenderShape()==INVISIBLE` 跳过,等于没有)。

## 仍然活着的旧结论(珊瑚巨像时代就有,继续有效)

- **36 tick 窗口靠「每 tick 把 `attackTick` 打回 0」**:Goety 父类 `Leapleaf.tick()` 里 `attackTick >= secondsToTicks(1.29f)`(=25)会硬清 `setMeleeAttacking(false)`。顺序是 `goalSelector.tick()`(在 `aiStep` 内)→ `super.tick()` 的自增块,所以 goal 在 `tick()` 末尾写 `attackTick = 0` 即可永远够不到 25。已核实客户端/模型/渲染都不读 `attackTick`,清零安全。
- **计时用实体时钟 `elapsedTicks() = this.leapkelp.tickCount - this.startTick`**,别用 goal 自持计数器(与 goal 何时被 tick 无关,且 `tickCount` 就是动画系统自己的时钟)。
- **新状态 8/9 要补三处覆写**:`getAnimationState(String)`(**String 版**,Goety 的 `setAnimationState(String)` 虚调用它)、`getAllAnimations()`(父类返回新建 ArrayList,直接 add)、`onSyncedDataUpdated`(先 `super`,再按 id start + `stopMostAnimation`)。
- **Goety `Leapleaf$AttackGoal`(陆地 SMASH,射程 2.5)必须按类名移除**(包私有内部类),`registerGoals()` 里 `type.getEnclosingClass()==Leapleaf.class && type.getSimpleName().equals("AttackGoal")`。**全案唯一脆弱点**,Goety 改类名就静默失效。复刻的 `LeapkelpSmashGoal` 必须带 `|| isMeleeAttacking()` 门控(它照搬原版所以**不带 flag**,无 flag=永不冲突,同优先级能同时跑)。
- **不再需要「同 id 连击重启动画」兜底**:两次出拳之间父类必定插入 IDLE/REST(每 tick 覆盖 ANIM_STATE),`onSyncedDataUpdated` 那次回调会把 `smashAnimation` 归 0。
- **`ATTACK_RANGE` 的判据要用实体注册处的 `.sized()`**(`sized(1.9,1.9)` → 半宽 0.95),不是别处的 `bbWidth` 记忆。这次的 3.75 是珊瑚傀儡原值 + 命中箱 3.0 反推的,`3.0+0.95+0.5≈4.45 > 3.75` 有余量。
- 水里不用方块粒子(见 [[goety-leapkelp-land-water-leap]] 第 8 条),用 `SmashParticleOption` + `ParticleTypes.SPLASH`。
- **三处刻意不照抄**(取舍,别当漏做):①门控保留自家的严格版(视线 + 目标在水 + 自己悬水未踩底);②窗口靠清零而非改动画长度本身(长度正好=窗口);③不引 cataclysm 的声音/粒子。
- **仍未验证的风险**:`LeapkelpMoveControl` 在水里绝不落到原版后,从深水上浮/爬岸的能力是否还在。

相关:[[cataclysm-coral-golem-vs-coralssus]]、[[goety-leapkelp-land-water-leap]]、[[goety-model-reuse-private-fields]]、[[goety-servant-goal-priority-flags]]、[[ac-ao-ally-check-direction]]
