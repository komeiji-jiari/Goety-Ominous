---
name: goety-mount-idle-ai-authority
description: "骑乘仆从\"主人不操控时 AI 接管\"的做法:getControllingPassenger 按输入返回 null/玩家,连带一堆 Goety 门控要改"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 24adf4ac-b3cd-48ba-b0ff-c10585b1db8e
  modified: 2026-09-13T16:18:44.362Z
---

骑乘仆从要做到"被骑乘但主人不操控时照常 AI 战斗",无 mixin 的唯一做法是覆写 `getControllingPassenger()`:`getFirstPassenger() instanceof Player && (zza!=0||xxa!=0)` 才返回玩家,否则 null(样板 `LuxtructosaurusServant`)。

**Why:** 原版 `LivingEntity.aiStep` 尾段用 `getControllingPassenger() instanceof Player` 分派 —— 有操控者走 `travelRidden`(读 `getRiddenInput` 的玩家输入),null 才走 `travel(AI 输入)`,MoveControl/寻路只对后者生效。`Entity.isControlledByLocalInstance()` 默认式是"操控者是本地玩家?true:isEffectiveAi()",所以 null 时服务端权威、客户端只 lerp 跟随(同原版矿车/熔炉矿车,已证明乘客同步没问题)。玩家按 WASD 时 `ServerboundPlayerInputPacket` 把 xxa/zza 同步到服务端,两个端同 tick 判定一致,交接无缝。

**How to apply:**
- 不要改用 `Tremorzilla/ModRavager` 的 `isControlledByLocalInstance(){return isEffectiveAi();}`:那是给 Mob 骑手(没客户端发车辆包)准备的;玩家骑手情况下服务端和客户端会同时 travel,和客户端 `ServerboundMoveVehiclePacket`(服务端只在 `getControllingPassenger()==player` 时接受)抢位。
- 连带要改的 Goety 门控(都以 controllingPassenger 为"被骑"判据):`Summoned.isStaying()` 含 `getControllingPassenger()==null` → 空闲骑乘被判 staying → `IServant.stayingMode()` 上速度 modifier + stopNavigation 锁死移动,要覆写 `isStaying()` 追加 `getFirstPassenger()==null`;`Summoned.WanderGoal` 不看骑乘 → 空闲骑乘会载着主人自己溜达,要 gate;跟随 goal 的 `getControllingPassenger()!=null` 换成 `getFirstPassenger()`;骑乘量表之类"是否被骑"的判定也用 `getFirstPassenger()`。
- 近战/技能 goal 的"未被骑"门控要换成 `isSteeredByPlayer()`(未操控),否则空闲骑乘永远打不了。
- AC 的 `MountedEntityKeyMessage` 处理器**不**查 getControllingPassenger(常返池里没有该字符串),所以手动能力键在 null 期间照样送达 `onKeyPacket`。
- 骑乘时手动技能键若要求 `getAnimation()==NO_ANIMATION`,AI 自动战斗会让它几乎按不出来;改成排除 ROAR/EPIC_DEATH 即可。

相关:[[goety-ac-servant-mount-jump-authority]] [[goety-servant-follow-standby]] [[goety-summon-idle-freeze]] [[goety-servant-riding-mechanism]]
