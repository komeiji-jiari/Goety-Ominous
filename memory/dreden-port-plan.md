---
name: dregen-port-plan
description: "Goety1 Dreden 实体移植计划(到 goetyominous 1.20.1):文件面/基座核对/顺序,法术最后移植"
metadata: 
  node_type: memory
  type: project
  originSessionId: 5bc4a2af-1dd1-4c6b-9650-86e59ccd0702
---

# Dreden 移植计划(2026-09)

源:repo 内 `E:\java\Goety-master`(Goety1,1.16 旧映射)里的 Dreden 全家。目标:goetyominous(依赖 goety2 2.5.56.5)。

## 已核实的关键事实(勿再假设)
- goety1 文件:`neutral/AbstractDredenEntity`(281 行,全部行为)+ `hostile/DredenEntity`(壳)+ `ally/DredenMinionEntity`(壳+WanderGoal)+ `magic/spells/necromancy/DredenSpell`(156 行,最后移植)。
- 支撑:`AbstractWraithEntity`(goety1,545 行)、`projectiles/FrostBallEntity`、`entities/ai/FrostBallGoal`、`entities/ai/SpewingAttackGoal`、`client/render/FrostBallRenderer`、`ModDamageSource.frostBreath/directFrost`、`IBreathing`、`EffectsUtil.amplifyEffect/resetDuration`。
- ⚠️ **goety2 里没有 `AbstractWraithEntity`**(用户原先以为有)。goety2 只有 `common/entities/neutral/AbstractWraith`(extends **Summoned** 的现代版:fire/teleport/breathing 各 flag + AnimationState + summonPredicate/getSummonLimit)。→ 基座 = goety2 AbstractWraith,需逐方法对照 goety1 父类能力(传送/飞行/敌对),缺口由我们自己补。
- ⚠️ goety2 无 `FrostBallEntity`/`FrostBallGoal`/`SpewingAttackGoal`(projectiles 里只有 BlossomBall/Lavaball/ModFireball…)→ 弹射物与两个攻击 goal 需从 goety1 移植到本项目注册。
- goety2 有:`api/entities/IBreathing`(isBreathing/setBreathing/doBreathing)、`utils/EffectsUtil`、`utils/ModDamageSource.frostBreath` 均已确认存在(名字 1.20.1)。

## Dreden 行为规格(移植须逐条对齐,摘自 goety1 源码)
- 注册:DREDEN/DREDEN_MINION `MONSTER sized(0.6,1.99) tracking 8`;属性 HP30/攻4/速0.25/索敌35(goety2 风格 attrs 另设);敌对自然权重 20(ModEvents)。
- goals:FrostBallGoal(4,射程10)+ SpewingAttackGoal(4,20范围/20t/0.025)+ Melee(5,1.25)。
- 吐息:SPEWING data;吐息中每 tick 面部 0.9 云粒子+PLAYER_BREATH;`doBreathing` = frostBreath 伤(ATTACK_DAMAGE)+ 300t 缓慢(已有→2.5% 升级否则重置)。
- 远程:FrostBall 从 y+0.75 射(DREDEN_SHOOT+挥主手)。
- aiStep:热群系白天→WEAKNESS II;着火每 50t 清火;雨雪+雪群系→抗性 II+每 20t 回 1;全身云粒子。
- 免疫缓慢;对 Stray(敌对)/StrayMinion(友方)不互伤且免疫其伤害(goety2 对应名需找:如 StrayServant)。
- 近战 directFrost + 击退;事件 100 云粒子/101 传送音(父类传送)。
- 音效:AMBIENT/HURT/DEATH/FLY/放 SHOT;贴图 dregen.png + minion_bands。

## 移植顺序(法术最后)
1. (本会话开始)翻译 `AbstractDredenEntity` → 继承 goety2 `com.Polarice3.Goety.common.entities.neutral.AbstractWraith`,注册进项目(实体+属性+蛋先可后);
2. 移植 `FrostBallEntity` + `FrostBallGoal` + `SpewingAttackGoal` + `FrostBallRenderer`(弹+渲染先行);
3. 渲染器/模型 DredenModel/Renderer/Bands 稍后;
4. hostile DredenEntity + 友方 Minion 壳 + 自然生成(spawn 权重);
5. DredenSpell(法术)最后。

## 备注
- goety1 源码全是旧映射(World/EntityClassification/DataManager),必须翻 1.20.1 名;
- 需要 goety2 侧再核对:AbstractWraith 是否含 isHostile/传送回调(事件100/101 语义)、Stray 系 1.20.1 对应类、frost 伤害源在 goety2 用法。
- 相关记忆:[[mutant-blaze-servant-port-progress]] 同类移植节奏(先调查→逐文件交付→用户粘贴)。
