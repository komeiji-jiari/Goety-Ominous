---
name: summon-spell-lifespan-anchor-pattern
description: 召唤法术必须无条件 setLimitedLife + setPersistenceRequired;写 if(王冠) 分支会让仆从永久
metadata: 
  node_type: memory
  type: project
  originSessionId: 32403d76-827f-4982-aecf-dbf281e3ede4
  modified: 2026-10-04T14:32:46.415Z
---

本 mod 所有召唤法术(以及 Goety 原版 `ZombieSpell`/`SkeletonSpell` 等)的锚点写法是**两句都写、无条件**:

```java
summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
summonedentity.setPersistenceRequired();
```

**Why:** 深渊/亡灵等王冠的"永久仆从"不是法术给的,而是 Goety 的 `IServant.healServant()` 给的 —— 它检测 `ServantUtil.isAbyssHeal(self) && CuriosFinder.hasAbyssCrown(owner)` 后 `setHasLifespan(false)`,只是**把计时器停住**;摘下王冠后 `if (getLifespan() > 0) setHasLifespan(true)` 会把**剩余寿命**接着跑。所以法术必须先写下一个 >0 的 lifespan。

**How to apply:** 别写 `if (hasXXXCrown(caster)) setPersistenceRequired(); else setLimitedLife(...)` —— 王冠分支不写 lifespan,`getLifespan()` 恒为 0,摘王冠也恢复不了,仆从**永久**。`ThrasherSpell` 曾是全 mod 唯一这么写的(2026-10-04 已改回锚点写法)。查证要点:`getMobType()==MobType.WATER` 的仆从一律吃 `isAbyssHeal`,天生就是王冠受益者,不需要法术额外照顾。相关: [[goety-servant-follow-standby]]、[[config-file-split-rule]]
