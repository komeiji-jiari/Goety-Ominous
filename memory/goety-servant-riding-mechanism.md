---
name: goety-servant-riding-mechanism
description: Goety 仆从骑上坐骑的完整条件链：命令聚晶上马 + isAbleToRide 两道分支 + canBeRidden 是第 2/3 个乘客的唯一入口
metadata: 
  node_type: memory
  type: reference
  originSessionId: b593d24c-a0cf-48db-85ab-b58ca2e431ec
  modified: 2026-09-13T17:10:52.722Z
---

让仆从骑上某个坐骑，Goety 侧有三道闸门，缺一不可（源码在 `.scratch/goety_iser_decomp/.../IServant.java`，不在本仓库）：

1. **上马入口不是右键**：`命令聚晶`/`敕令聚晶`（command_focus/order_focus）左键仆从绑定 → 右键坐骑。链路 `IServant.servantTick()`：仆从寻路到 `commandPosEntity`，贴身后 `isAbleToRide(target) && owned.startRiding(target)`（IServant.java:609，force=false，所以仍要过 vanilla `canAddPassenger`）。

2. **`IServant.isAbleToRide(LivingEntity target)`（IServant.java:403）两道分支**：
   - 第一段：`骑手 !instanceof PlayerRideable && !instanceof IGolem` && `target instanceof PlayerRideable`（或 EntityType 进 `goety:servant_rideable` tag，该 tag 上游是空的）&& **`target.getFirstPassenger() == null`** → 返回 `MobUtil.getOwner(target) == 骑手.getTrueOwner()`。
   - 第二段（第一段条件不成立时兜底）：`target instanceof IServant` → 返回 **`target.canBeRidden(骑手)`**，`IServant` 默认 **false**。

3. **`canAddPassenger`**（vanilla `Entity.java:2003` 默认 `passengers.isEmpty()`）才是真正的数量上限。

坑：
- 第一段的 `getFirstPassenger() == null` 意味着**空载坐骑只上得了 1 个仆从**。第 2、3 个必然走第二段 → 坐骑必须覆写 `canBeRidden(LivingEntity rider)` 返回 true（同主人判定自己写，因为第二段绕过了 owner 检查）。样板：`IllagerElephantServant.java:291`（战象 2 乘客就是这么过的）。
- 本仓库 `TremorsaurusServant`/`CandicornServant`/`GammaroachServant`/`AtlatitanServant`/`ForsakenServant` 只加了 `canAddPassenger`(婴儿 false + Player 恒真 + `IServant` 且同主人;`ForsakenServant` 2026-09-11 才补齐,之前只放行 Player 导致仆从上不去) + 类声明 `implements PlayerRideable`，**没覆写 `canBeRidden`** → 实际每个只能载 1 个仆从，不能同乘。AC 原版 `DinosaurEntity`/`AtlatitanEntity` 都不 implements PlayerRideable，那行是本仓库自己加的，也是整套机制的钥匙。
- vanilla `Entity.addPassenger`（`Entity.java:1973`）在**服务端**把 Player 插到 index 0，仆从索引整体后移 → 座位表可以直接用 `getPassengers().indexOf()`，玩家会自动占 0 号控制位。`getControllingPassenger()` 只认 Player 就够了，仆从乘客不抢方向盘。
- 想让仆从**驾驶**（而非被运载）是另一套：看 `GammaroachServant.java:384` 的 `DriveRiddenGoal`，靠 `Summoned.updateControlFlags()` 里 `getControllingPassenger() instanceof Mob` 那条分支放行 MOVE flag。
- **禁止某个仆从去骑别人**＝在**骑手侧**覆写 `isAbleToRide(LivingEntity)` 恒返 false，不要动 `canBeRidden`（那是坐骑侧、管别人骑不骑得上它）。四只都加了：`TremorzillaServant`/`HullbreakerServant`（非 PlayerRideable，走第一段）、`AtlatitanServant`/`LuxtructosaurusServant`（implements PlayerRideable，第一段被跳过，但第二段 `target.canBeRidden` 仍会放行它去骑 `IllagerElephantServant` 这类覆写了 canBeRidden 的坐骑，所以同样要覆写）。命令聚晶**照旧接受指令**（DarkWand 里 `setCommandPosEntity` 无闸门），仆从走到目标旁停下 —— 指令本身不会被拒。
- **`isAbleToRide` 只管 Goety 这条 commandMode 链路，不是"不可作为乘客"的完整保证**。它拦不住 vanilla 路径：`Entity.startRiding(vehicle, force=false)`（`Entity.java:1903`）看的是 `this.canRide(vehicle)`（默认 true）+ `vehicle.canAddPassenger(this)`；`force=true` 连这两条都跳过（`ISeat.placeSeat` 就是这么把仆从塞进 SeatEntity 的）。理论上要彻底封死得在骑手侧覆写 `startRiding(Entity, boolean)` 恒返 false（双参版；仓库先例 `ServantCentipedeBody.java:238` 只覆写单参版，挡不住 force）——**但用户 2026-09-14 明确撤回了这个加固**，理由是"`isAbleToRide` 既然已经有了就不改了"：本仓库要的是和各只坐骑**严格一致的最小改动**，不要超出参考实现的额外保险。别再加。

相关：[[goety-ac-servant-mount-jump-authority]]、[[goety-command-mode-conflict]]、[[goety-servant-follow-standby]]
