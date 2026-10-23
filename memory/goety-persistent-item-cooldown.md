---
name: goety-persistent-item-cooldown
description: "长冷却(如核弹/复活)必须用 Goety SEHelper.addCooldown,原版 ItemCooldowns 退出重进即清空"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 620f35fe-071e-4c00-8478-a36f47716524
  modified: 2026-09-30T13:47:25.726Z
---

`player.getCooldowns()`(原版 `ItemCooldowns`)只活在 Player 对象内存里,不写 NBT、死亡重建也丢 —— 任何"一天一次"这类限流用它会**退出重进即可刷新**(玩家实测反馈的刷核弹冷却 bug)。

持久做法用 Goety 的:`SEHelper.addCooldown(player, item, ticks)` 写进 SE 能力(`FocusCooldown`,按 `Item` 键),`SEHelper.isOnCooldown(player, stack)` 查。该能力由 `SEProvider`(ICapabilitySerializable)存进玩家 NBT → 重进保留;`ModEvents` 里 `PlayerEvent.Clone` + `reviveCaps` 拷到新玩家 → 死亡重生也保留;`FocusCooldown.tick` 在 `TickEvent.PlayerTickEvent` 每 tick 减 1(在线才走)。仓库先例:`ExtinctionCatalyst`。

**`addCooldown` 是覆盖不是取 max**:`FocusCooldown.addCooldown` 直接 `cooldowns.put(item, new CooldownInstance(ticks))`,所以**再调一次会把计时器重置为满**,不会因为「剩余时间更长」而保留旧值。想「重置/刷新冷却」就在想刷新的那一刻重调即可(仓库先例:`TremorzillaServant.dropEquipment` 在核弹物品飞回主人时重调,把 20 分钟冷却从**返还时刻**重新起算,否则仆从活得比冷却久就能无限刷)。

渲染侧:Goety 的 `GuiGraphicsMixin` 注入 `GuiGraphics.renderItemDecorations`,对**任意**物品读这个冷却并画一层深色遮罩 —— 换成 SEHelper 后物品栏不再有原版白色扇形扫过,改成 Goety 式黑罩;按 `Item` 键的冷却不可用换 NBT 的副本绕开("specific" 冷却才是按 stack 键的,别用)。

相关:[[ac-goety-port-probability-gates]]、[[goety-crown-vs-robe-two-channels]]
