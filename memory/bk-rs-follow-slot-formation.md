---
name: bk-rs-follow-slot-formation
description: BK/RS 左右侧位跟随 + 命令聚晶配对——2026-09-30 做完又被用户要求全删；留下的是 Goety 侧几条别再重推的结论
metadata:
  node_type: memory
  type: project
  originSessionId: 9a8eed06-9e03-4f5b-9777-466810a2f025
  modified: 2026-09-30T09:18:08.735Z
---

2026-09-30 上午做过一整套「BK/RS 一起跟随时各走主人左右 2 格、走路不互相挤压」：`KnightFollowOwnerGoal`（抄了 `ServantFollowOwnerGoal.tick()` 再换落点）、BK/RS 覆写 `followGoal()`、配对靠「左键把 A 绑进聚晶 → 右键 B」由 `KnightPairEvents` 记进两只的 `getPersistentData()`。**同日下午用户说「算了 绑定和寻路的修改删了吧」，全部已删除**（三个新文件 git rm、BK/RS 的 `followGoal()`/`KNIGHT_SIDE_OFFSET`/import 撤掉）。**别再主动重做，要做得等用户再提。**

留下的结论（重推一遍很贵，所以记着）：

- **Goety 没有仆从↔仆从绑定**。命令聚晶 `CommandFocus` 一枚只装一只（NBT `TAG_ENTITY="Servant"` 单 UUID + `TAG_ENTITY_CLIENT`，且只在 `!hasServant` 时写）；「先绑一个再右键另一个」在 Goety 侧**不落任何持久状态**——右键走 `DarkWand.interactLivingEntity` → `IServant.setCommandPosEntity(target)`，只是个 `commandPos` + 10 秒 `commandTick` 的临时巡逻目标，落地/上骑/卡住/超时即清。想让那个手势留下痕迹只能自己存。
- 聚晶取用要看两层：手里可能是裸 `CommandFocus`，也可能是 `IWand`（用 `IWand.getFocus(手持物)` 取槽里那颗）。`CommandFocus.getServant(ItemStack)` 是服务端按 UUID 查实体。
- `Level` 只有 `getEntity(int)`；**按 UUID 查实体必须 `((ServerLevel)level).getEntity(uuid)`**，直接传 UUID 编译报「UUID无法转换为int」。
- `Summoned.FollowOwnerGoal<T>` 的泛型上界是 `T extends Mob & IServant`（CFR 反编译只打 `Mob`，照抄会编译错）。
- `Level.getEntitiesOfClass` 扫同伴 + 每 tick 问一次，不如直接在实体上存 UUID 查表。

相关：[[goety-servant-follow-standby]]、[[goety-move-speed-two-channels]]、[[goety-compile-jar-source-of-truth]]、[[bk-rs-two-passenger-mechanics]]
