---
name: mm-zombie-servant-death-drop-hook
description: 三僵尸仆从死亡返还：MM 返还物已删，改成飞回的复活物品(三件，对着 goety 基础僵尸仆从右键)；ReviveServantItem 存的 NBT 带 DeathTime=35，复活必须重置 deathTime
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-16T16:51:06.784Z
---

**钩子点(不变):** 三个突变僵尸仆从(丛林/尸壳/霜冻)是**多命**的 —— `dropAllDeathLoot(DamageSource)` 覆写成空方法(挡掉 `die()` 那次),真掉落只发生在 `tickDeath()` 里 `deathTime >= 35 && !isClientSide()` 且 `getRemainingLives() <= 0 || isRealDeath(...)` 那一支 → `dropAllTickDeathLoot(killedBy)`(`captureDrops` 窗口)。`/kill` 与掉虚空走另一支(deathTime==20 直接 remove,无掉落)。

**2026-10-16 用户改主意,方向掉头:** 原来挂在 `dropCustomDeathLoot` 里返还的三件 MM 物品(`MUTANT_HUSK_VOCAL_CORDS`/`MUTANT_FROZEN_ZOMBIE_SPIKES`/`MUTANT_JUNGLE_ZOMBIE_VINE`)**已全部删掉**,三个类里的 `dropCustomDeathLoot` 覆写整个删除(连带 `com.alexander.mutantmore.init.ItemInit` import)。三张战利品表和装备掉落**保留**(用户明确只删 MM 返还物——玩家给的甲/武器要能拿回来)。

**换成「飞回来的复活物品」,参考暗兽(BlackBeast→HowlingSoul):**

- 实体侧:`AbstractMutantServant.returnReviveItem(Item)` —— `ReviveServantItem.setOwnerName/setSummon` + Goety `FlyingItem`(`ModEntityType.FLYING_ITEM`) + `ParticleTypes.SOUL` + `ItemConfig.ReviveSecondsCool`。**owner 为 null 直接不返**(照抄 BlackBeast,没做 `spawnAtLocation` 兜底),所以 `/kill`、无主蛋放的仆从都不会给。
- 物品侧:`com.qiuyue.goetyominous.common.items.mm.MutantZombieReviveItem`(一个参数化类,构造传 `(servantType, hostType)`),注册 `mutant_jungle_zombie_revive` / `mutant_husk_revive` / `mutant_frozen_zombie_revive`。**对着对应基础仆从右键转化**,目标被 `discard()` 吃掉、存 NBT 的仆从原地回来:丛林→`goety:jungle_zombie_servant`、尸壳→`goety:husk_servant`、霜冻→`goety:frozen_zombie_servant`(要求 `getTrueOwner() == player`,野生的不吃)。继承 `ReviveServantItem` 所以自动进创意栏 revive 桶(`CompatItemSource.isRevive`)。

**⚠ DeathTime 陷阱(这条最容易漏):** `LivingEntity` 存档里有 `"DeathTime"`(public 字段 `deathTime`)。我们的真死判定在 `deathTime >= 35`,所以 `setSummon` 存进物品 NBT 的就是 **DeathTime=35**。复活后 `tickDeath` 只在 `isDeadOrDying()`(血<=0)时才跑,所以不会立刻掉命,但**下一次死亡时 deathTime 从 35 起跳 → 第一 tick 就撞上 `>= 35` 直接真死,跳过倒地动画和剩余所有命**。因此复活必须 `deathTime = 0` —— 落在 `AbstractMutantServant.prepareRevived()`,三个僵尸各自覆写再补 `downAnimationTick = 0` + 命数回满(`*CommonConfig.get_up_times.get()`,丛林/霜冻另 `setLivesLost(0)`)。物品在 `addFreshEntity` 前调它。

**转化音效(2026-10-16 用户定调):** 三个 item 统一播 `SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F` —— 用户指名「用黑狼被咆哮之魂转化成暗兽时候的爆炸声音」,即 `HowlingSoul.interactLivingEntity` 里那两行中的第一行(第二行是 `ModSounds.BLACK_BEAST_ROAR`,**没要**,所以没加)。我先按"各僵尸自己的嗓子"配过一版(MM 只有 `MUTANT_HUSK_REVIVE`,丛林僵尸只有 idle、霜冻僵尸**一个音效都没有**),被用户否掉 → 不要再往 per-type 音效上走。

**未定项:** 注册名和贴图都是占位(model 指 `minecraft:item/rotten_flesh`;`*_revive` 这个 id 是临时起的;中英 lang 已加)。**没加配置开关**(飓风那边有 `MobsConfig.HurricaneServantCore`);命数回满是按用户"复活物品"语义做的猜测,要改成"回来还剩死前那些命"就删 `prepareRevived` 里的 `setRemainingLives`。

相关:[[mm-jungle-zombie-servant-port-decisions]]、[[goety-flyingitem-return-semantics]]、[[forge-death-loot-capturedrops-window]]、[[goety-multilife-servant-dismiss-conflict]]
