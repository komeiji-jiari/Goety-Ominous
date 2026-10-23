---
name: goetyominous-deadcode-analysis-scripts
description: .scratch/_deadcode.mjs 与 _deadfields.mjs 的判据与两个必须手工复核的坑
metadata: 
  node_type: memory
  type: project
  originSessionId: 637df696-01ad-4b07-a897-ce5989e56df2
  modified: 2026-10-04T15:05:48.806Z
---
`.scratch/_deadcode.mjs <实体文件>`:抓 goal 构造 `new XxxGoal(this, from, to, end)` 的三角 + 直接 `setAttackState(N)`,算出可达状态集合,再标出所有 `getAttackState() == N` 守卫和 `onSyncedDataUpdated` 的 `case N` 里 N 不可达的。判据成立的前提:**所有 goal 参数和 `setAttackState` 实参都是字面量**——用 `grep -E "setAttackState\(" | grep -vE "setAttackState\([0-9]+\)"` 先验一遍(Goety 实体里通常只剩 `setSleep` 的 `sleep ? 1 : 0`,两个都可达)。
`.scratch/_deadfields.mjs <目标文件> <src/main/java>`:统计每个字段/方法在"自身 vs 全仓库其他文件"的引用数。
**三个坑:**
1. 路径要用 `norm()` 把 `\` 转 `/` 才能和 walk 出来的路径比,否则目标文件会把自己算成"外部引用",所有字段都显示活着。
2. 字段正则会误匹配**方法内局部变量**(`knockbackStrength2`/`cosAngle`/`deltaY` 之类),输出的"只写不读"列表里这些是假阳性,必须逐个 grep 复核再动手。
3. **SRG 名的原版重载不是死代码**:`_deadfields.mjs`/`_methodref.mjs` 只扫 mod 自己的 jar,原版调用点看不见,所以 `m_7337_(Entity){return true;}` 这类"只定义一次"的方法会被误判。凡 `m_xxxx_` 一律用 tsrg 反查,查到名字基本就是活的重载(实例:`m_7337_=canCollideWith`、`m_8028_=shouldDespawnInPeaceful`、`m_6457_/m_6452_=startSeenByPlayer/stopSeenByPlayer`、`m_6518_=finalizeSpawn`);只有方法体是裸 `super.xxx()` 的才够格叫"冗余重载"。
4. **`AnimationState` 的死活判据 = 全仓有没有 `start(` / `startIfStopped(` / `animateWhen(`**(`stop()` 不算启动,`stopAllAnimationStates()` 里那一行列什么都不证明)。LM 那套 `getAnimationState(String)` **字符串查表**会让字段看起来像在用 —— 字符串键、模型 `getAnimationState("xxx")`、`stopAll` 那行三处都齐,但没人 start,状态号也从未被 `setAttackState` 设过,就是死的。一行 bash 就能全扫:`for n in $(grep -o "AnimationState [A-Za-z0-9_]*" 实体.java | awk '{print $2}' | sort -u); do echo "$n $(grep -c "$n\.start\|$n\.animateWhen" 实体.java)"; done`,输出 0 的基本就是死账(圣骑实例:`deathAnimationState`/`BackflipDoubleAnimationState` 整字段死,`DoubleSlashSlamEnd`/`FlyAwaySlash`/`BackflipEnd` 只有不可达 case 里的启动点)。清理时字段/构造器/getAnimationState 键/stopAll 行/**模型 animate 行**五处一起删,LM 自带的 `*Animations` 类动不了。
5. **同名状态被 animate 两次**是另一类残留(不算死代码,每帧都在跑,删了会改观感):圣骑模型里 `getAnimationState("death")` 被 `FlamebornGuardAnimations.death2` 和 `PossessedPaladinAnimations4.death2` 各应用一遍,前者是照抄 `FlamebornGuardModel` 带过来的 —— 顺带把那条 import 也清掉(2026-09-26 已按用户确认删除 Flameborn 那条)。
另有 `.scratch/_anim_dump.mjs`(LM 反编译的 `*Animations.java` → 逐骨骼关键帧表),用来核对"哪个 AnimationDefinition 才是举手召唤"这类问题,别靠猜。三条判据都是实测过的:keyframe 标记是裸的 `AnimationDefinition `(没有 `public static final` 前缀),关键帧参数要非贪婪 `(.*?)` 锚在 `), AnimationChannel.Interpolations` 上(否则 `(float)0.0f` 的括号会截断),bone 是正则匹配数组不是字符串。
**配置项死活的批量判据(2026-10-04)**:对 `*/config/*.java` 和"非 config 源码"各出一份 `grep -rhoE "[A-Za-z_][A-Za-z0-9_]*" | sort -u` 的 token 集合,再 `comm -23` 求差 —— 一次 grep 扫完 946 项,比逐项 `grep -rl`(946 次全树扫描,2 分钟超时)快两个数量级。删除用 `.scratch/_stripdead.mjs <配置文件> <名字...>`:同时删声明行和 `NAME = BUILDER … .defineInRange(…)` 整块(含尾随空行),每个名字必须正好命中 2 处否则打 WARN,文件按 latin1 读写保字节不变。当天据此清掉 16 项(长尾鲨 2 + 大怪 limit 11 + CultistSpawn 3),余 0 死。

用法见 [[goetyominous-deadcode-cleanup-preference]]。