---
name: goety-leapleaf-rest-immobile-freeze
description: "Goety Leapleaf 的 restTick>0(4s) 让 isImmobile() 为真 → LivingEntity.baseTick 直接跳过 serverAiStep → goal+target 选择器全停;表现为\"有时不索敌/挨打不还击\""
metadata: 
  node_type: memory
  type: reference
  originSessionId: fbf5c252-d6e9-419a-bc0b-63ff4b170a9a
  modified: 2026-09-10T14:52:46.049Z
---

Goety `Leapleaf`(`libs/goety-2.5.56.0.jar`)两个覆写是**全局 AI 冻结**的开关:

```java
protected boolean m_6107_()        { return super.m_6107_() || this.restTick > 0; }   // isImmobile
public boolean m_142582_(Entity e) { return super.m_142582_(e) && this.restTick <= 0; } // hasLineOfSight
```

`restTick` 只在 `Leapleaf$LeapGoal.tick()` 落地分支里赋值:**跳跃落地那一帧砸中至少一个生物 → `restTick = REST_TIME`(4s)**。`Leapleaf.tick()` 里递减,减到 `REST_TIME-1` 时放 `LEAPLEAF_REST` 音效。

冻结链(反编译 `forge-1.20.1-47.3.22-decomp.jar` 实测):`LivingEntity.baseTick()` 里
```java
if (this.isImmobile()) { jumping=false; xxa=0; zza=0; }
else if (this.isEffectiveAi()) { this.serverAiStep(); }
```
而 `Mob.m_6140_`(serverAiStep) 才是跑 `targetSelector.tick()` / `goalSelector.tick()` 的地方。

**所以 restTick>0 期间:不索敌(SummonTargetGoal 不 tick)、不还击(ServantHurtByTargetGoal 不 tick)、不动(xxa/zza 清零)。** 深水里 `onGround()` 为假跳不起来 → 没硬直,所以只有岸上/浅水会犯。

修法(`Leapkelp` 侧,`restTick` 是 public 字段,不用 mixin):在 `Leapkelp.tick()` 的 `super.tick()` 之后封顶
```java
if (this.level().isClientSide) return;
if (this.restTick > MAX_REST_TICKS) this.restTick = MAX_REST_TICKS;   // 20 tick
```
**必须在 `super.tick()` 之后**:音效在 `Leapleaf.tick()` 里于 goal 之后同帧触发放过,封顶放前面会把 80 直接压掉、音效永不播;放后面就一次都不用补播。改的是父类 `Leapleaf` 的字段所以不要动上游。

相关:[[goety-leapleaf-leap-goal-gating]]、[[goety-servant-goal-priority-flags]]
