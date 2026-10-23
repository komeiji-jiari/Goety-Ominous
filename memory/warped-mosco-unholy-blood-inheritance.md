---
name: warped-mosco-unholy-blood-inheritance
description: 诡异蚊鬼仆从的不洁血强化跨死亡传递（2026-10-16 加）—— 标记走类固醇物品 NBT + 绯红蚊子实体字段穿越 160 tick 转化窗，且必须在 finalizeSpawn 之后应用；带 tag 版=同名物品的 NBT 变体，叫「不洁类固醇」
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-16T16:40:03.241Z
---

**机制（2026-10-16 用户令「强化能传下去」后实现）：** 喂过不洁之血（MM `ModItems.UNHOLY_BLOOD`）的 `WarpedMoscoServant` 死亡 → 掉回 `goetyominous:warped_steroids`（诡异类固醇）时带上 NBT → 拿这个带 NBT 的类固醇喂 `CrimsonMosquitoServant` → 160 tick 后转化出的新蚊鬼**自带**不洁血强化，不用重喂血。

**实现前的事实（用户问「有没有特殊 nbt」时的答案=没有）：** 掉落是裸的 `new ItemStack(AmItems.WARPED_STEROIDS.get())`；`WarpedSteroidsItem.interactLivingEntity` 不读 tag；转化只 `setTrueOwner` + `finalizeSpawn`；`setUnholyBlood(true)` 全仓库只有一处调用点（`mobInteract` 喂血）。强化的属性修正（`UNHOLY_BLOOD_HEALTH_UUID` / `_ATTACK_UUID`，ADDITION，`applyEnhancementModifiers`）只挂在**当前活着的个体**上，一死就断。

**三处落点 + 一个顺序坑：**

1. `WarpedMoscoServant.tickDeath()`：`if (this.hasUnholyBlood()) itemStack.getOrCreateTag().putBoolean(...)`。tag 名常量 `WarpedSteroidsItem.UNHOLY_BLOOD_TAG = "UnholyBloodEnhancement"`（与实体自己存档用的同名键**是两回事**：实体那个写在 `addAdditionalSaveData` 里）。
2. `WarpedSteroidsItem.interactLivingEntity`：`mosquito.setInheritUnholyBlood(stack.hasTag() && stack.getTag().getBoolean(UNHOLY_BLOOD_TAG))`。该分支在客户端 `return SUCCESS` 之后，天然只走服务端。
3. `CrimsonMosquitoServant`：新字段 `inheritUnholyBlood`（**普通字段不是 entityData**，客户端不需要知道）+ save/load `"InheritUnholyBlood"`（跨越 160 tick，区块卸载也不能丢）+ setter；转化块里设置。
4. `WarpedMoscoServant.applyUnholyBloodEnhancement()`（新 public，带 `hasUnholyBlood()` 幂等短路）= `setUnholyBlood(true)` + `applyEnhancementModifiers()` + `setHealth(getMaxHealth())`（属性修正不会自动补血，所以必须显式补满）。

**顺序坑（关键）：调用点必须在 `mosco.finalizeSpawn(...)` 之后。** Goety 的 `Summoned.finalizeSpawn` 在 `getTrueOwner() != null` 时会调 `spawnUpgraded()`（定义不在 `Summoned`/`Owned` 里，是再上游的继承），无法确认它会不会重设属性/血量；放最后 = 无论上游怎么动，强化都最后落地、血量也补满。注意 `setTrueOwner` 仍要留在 finalizeSpawn **之前**（见 [[goety-summonspawn-owner-order]]）。

**带 tag 版的外观/名字（2026-10-16 用户给贴图后加）：** 用户给 `unholy_steroids.png`（16x16）并定名「不洁类固醇」。做法是**同一个物品、NBT 变体**，不新注册 item：`WarpedSteroidsItem.isUnholyBlood(ItemStack)` 静态判定（也是 `interactLivingEntity` 现在用的读法）+ 覆写 `Item.getDescriptionId(ItemStack)` → tag 在就返回 `item.goetyominous.unholy_steroids`（覆写 `getDescriptionId(ItemStack)` 而不是 `getName`，inventory/tooltip/JEI 全跟着走）；模型侧 `models/item/warped_steroids.json` 加 `overrides` 谓词 `goetyominous:unholy_steroids`，谓词在 `client/events/am/AmClientEvents.clientSetup`（`FMLClientSetupEvent` + `enqueueWork`，AM 未加载时 return）里 `ItemProperties.register`。照抄的是 [[ominous-icon-rotation-order]] 那套谓词写法，没用 `custom_model_data`。**没加 tooltip 文案**（按用户规矩）。

**未做/待决：** 创造模式喂食不消耗物品 → 一个带 tag 的可以反复刷强化蚊鬼。

相关：[[goetyominous-feed-particle-heart]]、[[goety-configurable-attrs-reset-on-load]]、[[goety-summonspawn-owner-order]]
