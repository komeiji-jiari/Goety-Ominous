---
name: forge-1-20-1-air-supply-drown-chain
description: "Forge 1.20.1 溺水逻辑搬进 ForgeHooks.onLivingBreathe,陆地每 tick 补满氧气→海龟式 handleAirSupply 干涸伤害是死代码"
metadata: 
  node_type: memory
  type: reference
  originSessionId: cbb0bb01-e4b1-49d0-aaf8-d63c99c52faa
  modified: 2026-09-10T12:01:34.963Z
---

Forge 1.20.1 里 `LivingEntity.baseTick()` 的整段溺水/回氧代码被包进 `if (false) // Forge: Handled in ForgeHooks#onLivingBreathe`，真实逻辑在 `ForgeHooks.onLivingBreathe`：

```java
boolean isAir = entity.getEyeInFluidType().isAir() || blockState.is(Blocks.BUBBLE_COLUMN);
boolean canBreathe = !entity.canDrownInFluidType(eyeType) || hasWaterBreathing || invuln;
LivingBreatheEvent e = new LivingBreatheEvent(entity, isAir || canBreathe, consume, increaseAirSupply(air)-air, isAir);
if (e.canBreathe()) { if (e.canRefillAir()) entity.setAirSupply(min(air + e.getRefillAirAmount(), getMaxAirSupply())); }
else { entity.setAirSupply(air - e.getConsumeAirAmount()); }
if (entity.getAirSupply() <= 0) { /* LivingDrownEvent，air <= -20 才 hurt(drown, 2.0F) */ }
```

`FluidType.isAir()` 只在 `== ForgeMod.EMPTY_TYPE` 时为 true，所以**站在陆地/水里但眼睛在空气中时 `isAir = true` → `canRefillAir = true` → 每 tick 把氧气补回 max**（`refillAirAmount = increaseAirSupply(air) - air`，即 +4 封顶）。

**后果：** Turtle 式的 `handleAirSupply`（`aiStep` 里 `setAirSupply(air - 1)`，到 -20 给 `dryOut` 伤害）在 1.20.1 Forge **永远不会触发**：`baseTick()` 先跑并补满到 300，`aiStep()` 再减 1 停在 299，下 tick 又补回 300 —— 恒在 299/300 振荡，`getAirSupply() == -20` 不可达。

- Goety 的 `Wavewhisperer` 就是这段死代码（全 Goety 源码里没有任何 `increaseAirSupply` 覆写），它"陆地快速掉血"实际不生效。
- 想让它真的生效：覆写 `protected int increaseAirSupply(int airSupply) { return airSupply; }`，把 `refillAirAmount` 置 0、氧气交给 `handleAirSupply` 全权接管。该方法的**唯一活调用点**就是 `ForgeHooks.onLivingBreathe` 这一行（另一处 `LivingEntity:386` 在 `if(false)` 里），所以覆写是安全且局部的。
- 副作用：氧气跌到 <= 0 后 `LivingDrownEvent` 每 tick 都会 post 一次，但 `isDrowning` 参数是 `air <= -20`，而 `handleAirSupply` 一碰到 -20 就重置为 0，所以永远为 false → 只有我们的 `dryOut` 生效，不会和原版 `drown` 伤害叠加。

**溺水免疫那块：** `canBreatheUnderwater()` 在 1.20.1 已标 `@Deprecated //FORGE: Use canDrownInFluidType instead`，但 `IForgeLivingEntity.canDrownInFluidType` 的默认实现是
`if (type == ForgeMod.WATER_TYPE.get()) return !self().canBreatheUnderwater(); return type.canDrownIn(self());`
——即**水**这一路仍然桥接到 `canBreatheUnderwater()`。所以 `canBreatheUnderwater() → true` 的实体本来就水中不溺水，再覆写 `canDrownInFluidType(type){ return type != WATER; }`（Wavewhisperer 的写法）对水是纯冗余。

已应用在 `Leapkelp`（潮涌之叶）上，见 [[goety-amphibious-floatgoal-bounce]]。
