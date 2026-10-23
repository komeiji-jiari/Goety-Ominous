---
name: forge-1-20-1-mobeffect-no-death-hook
description: 1.20.1 MobEffect 没有 onMobRemoved/onMobHurt，要打 mixin 到 LivingEntity.remove(RemovalReason)；顺带 3018 事件与 onMobHurt 的对应通道
metadata: 
  node_type: memory
  type: reference
  originSessionId: 97e87718-bd88-49e3-8284-e2cd365e56cc
  modified: 2026-10-07T18:29:45.004Z
---

1.21 的 `MobEffect` 有 `onMobRemoved(entity, amplifier, RemovalReason)` / `onMobHurt(...)` / `onEffectStarted`，**1.20.1 一个都没有**（只有 `applyEffectTick` / `isDurationEffectTick` / 属性修饰符）。所以移植渗浆/盘丝/蓄风/寄生这类「死亡触发」效果必须自建通道：

- **死亡触发**：`@Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))` 打到 `LivingEntity`（1.20.1 `LivingEntity.remove` 存在，`tickDeath()` 与 1.21 逐行一致：`deathTime>=20` 时 `remove(KILLED)`）。判据 `reason == KILLED || DISCARDED`，与 1.21 的 `triggerOnDeathMobEffects` 一致。
  - 用 `@Unique` 字段防重入是多余的：`@At("HEAD")` 里 `self.isRemoved()`（= `removalReason != null && shouldDestroy()`）就能挡掉第二次 `remove`。
  - **要门控 `!level().isClientSide`**：1.21 原版不门控，但客户端也会走 `tickDeath → remove(KILLED)`，不门控会在客户端凭空 `addFreshEntity` 出幽灵实体。
- **受击触发**：没有对应 mixin，直接 `LivingHurtEvent`（Forge 事件）即可，进门时用 `hasEffect()` 短路。
- 备选 `LivingDeathEvent` 在 `die()` 触发，比原版**早 20 tick**（尸体倒地动画刚开头就出效果）且不覆盖 `DISCARDED` —— 只有明确不在乎时机时才用。

另外两条同批的 1.20.1 缺口：
- **`levelEvent(3018, pos, 0)` 在 1.20.1 是静默空操作**：`LevelEvent` 常量最高只到 3009，`SoundEvents` 也没有 `COBWEB_PLACE`（1.21 才加）。盘丝铺网因此没声音。
- `ParticleTypes.ITEM_SLIME` / `ITEM_COBWEB` 不存在，等价物是 `new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(...))`。

见 [[goety-1.21-explosion-null-damagesource]]、[[trial-effect-port-1.21.1]]、[[no-code-comments]]。
