---
name: lm-ianimatedboss-virtual-health
description: "LM 的 IAnimatedBoss 血量是虚拟的(totalDamageTaken),伤害由全局 Forge LivingHurtEvent 按 instanceof 喂进去;剥掉这层继承=仆从永久满血打不死"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 6a57ba39-21f0-4b79-99c1-e80b3d1212e3
  modified: 2026-09-26T13:56:06.335Z
---

Legendary Monsters 的 boss 基类 `IAnimatedBoss`（`OriginClasses/IAnimatedBoss.java`）有一整套**虚拟血量**，真正决定生死的是 `float totalDamageTaken` 而不是属性血量：

```java
getHealth()  = clamp(maxHealth - totalDamageTaken, 0, maxHealth)   // 覆写
setHealth(h) → 只写 vanilla 属性值，不碰 totalDamageTaken
heal(a)      → if (totalDamageTaken <= 0) return;   // 满血时回血被吞
tick()       → if (getHealth() > 0) super.setHealth(getHealth());  // 每 tick 把虚拟值刷回属性
```

**灌伤害的不是 `hurt()`，是 LM 的全局 Forge 事件**（`event/ForgeEvents.class`，我反编译确认）：

```java
@SubscribeEvent public static void addDamage(LivingHurtEvent e) {
    if (!(e.getEntity() instanceof IAnimatedBoss)) return;
    ((IAnimatedBoss) e.getEntity()).addDamage(e.getAmount(), e.getSource());
}
```

`addDamage` 里做 damageCap 钳制 → 护甲/魔抗吸收 → `totalDamageTaken += finalDamage` → `super.setHealth(getHealth())` → 血量 ≤0 时手动 `die()`。事件不 cancel、不改 amount，vanilla `hurt()` 那条路照走但它的结果被 `getHealth()` 覆写忽略。

**为什么是坑：** 全 jar 扫过（`.scratch/_findstr.mjs` 手法，见 [[goetyominous-deadcode-analysis-scripts]]），`addDamage` 只出现在两处——`IAnimatedBoss` 定义处和 `ForgeEvents` 调用处。仓库的仆从基类（[[lm-servant-boss-mechanic-stripping]] 那条链）继承的是 Goety 的 `Summoned`，**不是** LM 的 `IAnimatedBoss`，所以 LM 那个事件永远不会命中。照抄 `getHealth/setHealth/heal` 三个覆写 → `totalDamageTaken` 恒 0 → `getHealth()` 恒 = maxHealth → 仆从永久满血、`isDeadOrDying()` 恒 false、主人喂食/治疗全废。

**How to apply:** 移植 LM boss 时**不要移植虚拟血量**，用 vanilla 血量。阶段判定读的 `getCrackiness()`（`getHealth()/getMaxHealth()`）本来就是纯读，换 vanilla 后语义不变。

顺带两条**已确认为死字段**的：`hurtCD`（`addDamage` 里置 30、`tick()` 里递减，**没有任何地方读它**）；`IS_SECOND_PHASE`/`IS_THIRD_PHASE` 两个同步 bool（getter 读的是 `PHASE`，bool 只写不读）。

**两条易混 SRG（子 agent 在这上面栽过，我按 `build/fg_cache/.../srg_to_official_1.20.1.tsrg` 核过）：**
- `m_5997_(DDD)` = **`push(double,double,double)`**，不是 `setDeltaMovement`（后者是 `m_20256_(Vec3)` / `m_20334_(DDD)`）
- `m_21532_()` = **`isPersistenceRequired`**，不是 `canChangeDimensions`
- `m_6063_()` = `isPushedByFluid`；`m_8028_()` = `shouldDespawnInPeaceful`；`m_142079_()` = `canFreeze`

相关：[[lm-servant-boss-mechanic-stripping]]、[[lm-mechanic-presence-version-check]]、[[ac-servant-parity-keep-original]]
