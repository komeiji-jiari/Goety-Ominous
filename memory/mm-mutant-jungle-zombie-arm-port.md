---
name: mm-mutant-jungle-zombie-arm-port
description: MM 丛林突变僵尸手臂道具(玩家侧)曾整套移植、2026-10-15 用户下令删除；备份在 .scratch/_mjz_arm_backup/
metadata:
  node_type: memory
  type: project
  originSessionId: c9861133-b6bd-4efe-ad0a-eeefa263a153
  modified: 2026-10-15T17:57:42.654Z
---

玩家侧「突变丛林僵尸手臂」(`goetyominous:mutant_jungle_zombie_arm`)2026-10-15 先整套移植、同日**用户下令删除**(「MJZ 的 arm 道具那种配套装备 这个删了吧」)。删前 9 个文件已备份到 `D:\ark\java\.scratch\_mjz_arm_backup\<原相对路径>`,要恢复直接从那里取。

删掉的 9 个文件:`MutantJungleZombieArmEvents`(客户端 tick)+ `MutantJungleZombieArmRenderer`(BEWLR)+ `MutantJungleZombieArmSineWaveAnimations` + `MutantJungleZombieArmModel` + `MutantJungleZombieArmItem` + `MutantJungleZombieArmAnimationInstance` + `MutantJungleZombieArmAnimationManager` + `CMutantJungleZombieArmActivateStranglePacket` + `models/item/mutant_jungle_zombie_arm.json`。

连带清掉的 5 处引用:`MmItems` 的注册、`MmPackets` 的包注册与 `MmClientEvents` 的 layer 注册(前两者当时是这两个文件**唯一**的改动,清完与 HEAD 逐字相同)、`accesstransformer.cfg` 末尾的 `public-f net.minecraft.world.item.Item f_41371_`(同上,清完与 HEAD 相同)、choke goal 里那段 `mutant_jungle_zombie_arm_fly` 成就判定(全仓唯一触发点,随之消失)。

**别主动重提**:别再建议把这个道具加回来,也别再给别的 MM 仆从配"玩家侧配套装备"(见 [[mm-mutant-husk-servant-port-notes]])。若将来有物品真要写 `Item.maxDamage`(private final),AT 那条得用 SRG 名 `public-f net.minecraft.world.item.Item f_41371_`——这是本次唯一值得留下的技术点。相关:[[mm-jungle-zombie-servant-port-decisions]]、[[srg-to-mojmap-tsrg-lookup]]
