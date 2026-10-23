---
name: ac-goety-port-probability-gates
description: "移植 AC 实体到 Goety 仆从时易整行漏掉副作用(随机门控/状态赋值),分支概率或表现被扭曲"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6986f809-7b47-4aaa-8abf-b8c937a1fe7f
  modified: 2026-09-14T02:47:57.370Z
---

把 AC 敌对实体(如 Licowitch)的攻击方法移植到 Goety `Summoned` 仆从时,搬运 if/else 分支常会**丢掉行内的概率掷币**,使某分支从"一半概率"变成"条件满足即必中",观感与敌对版完全不同。

**Why:** LicowitchServant.hexAttack 移植时删了 `random.nextBoolean()` 门控,只剩 `召唤上限未满 && 找到召唤点`。上限通常未满、开阔地总能找到召唤点 → ANIMATION_SPELL_1 几乎必走召唤,地面法阵 LicowitchServantHex(对应敌对 SugarStaffHexEntity 的"法阵")几乎永不触发,用户反馈"该用法阵的地方变成召唤仆从"。敌对版是 50% 召唤 / 50% 法阵。

**How to apply:** 先 CFR 反编译敌对版攻击 goal 到 `.scratch/`,逐行对照;注意 `&& random.nextBoolean()` 这类短路径概率门控、以及"召唤数上限"、SoundSource(HOSTILE vs 己方 getSoundSource)等参数必须原样保留。本例已修:`hexAttack` = `summonSpace && nextBoolean()` 掷中才召唤,否则 `if (!summoned)` 放法阵。

同一类失误还会**漏掉单行状态赋值**而不报错:AC `TremorzillaEntity.tick` 走路落足分支里 `if (Math.abs(f) < 0.2f) { ...; this.screenShakeAmount = 2.0f; }`,移植到 TremorzillaServant 时只留下了 `playSound(TREMORZILLA_STOMP)` 和 `stompEffect(...)`,赋值没了 → 走路有踩地声/粒子但屏幕完全不抖;而且 `screenShakeAmount <= 0.3` 这道节流门因数值恒为 0 而永远放行,踩地声比原版还密。同一批移植的 TremorsaurusServant 保留了 `screenShakeAmount = 1F`,可作对照。TremorzillaServant.tick 落足分支一度补回过 `this.screenShakeAmount = 2.0F;`,但 2026-09-14 用户改主意:**走路震荡不要 AC 那套 2.0 的大抖**,该处已改发 Goety CameraShake(见 [[ac-servant-walk-shake-camerashake]]),落足节流改由新增的 `stompSoundCooldown` 负责——补漏行前先确认用户要的幅度,别照抄上游数值。

**How to apply(补充):** 对照反编译源码时别只看 if/else 结构与分支条件,逐行比对**块内每一句副作用**(字段赋值、粒子、音效、screenShake);发现"表现少了但逻辑对"的症状先怀疑漏行。这类漏行编译器与 IDE 都不会提示。

关联:[[goety-servant-goal-priority-flags]](同文件 idle 目标 priority 也移植偏过)、[[goety-summonspawn-owner-order]]、[[ac-compat-porting-notes]]
