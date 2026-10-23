---
name: mm-zombie-servant-equip-interaction
description: "三个突变僵尸仆从(丛林/尸壳/霜冻)右键交互的落点与坑 —— 装备:客户端早就全通、只缺服务端 mobInteract、setGuaranteedDrop 不能省、灾厄武器判定要用反射;腐肉喂食:heal 2.0F + GENERIC_EAT + 头顶 HEART"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-15T23:50:28.079Z
---

2026-10-15 给 MutantJungleZombieServant / MutantHuskServant / MutantFrozenZombieServant 加了"主人手持装备右键穿上",参照 `MutantWitherSkeletonServant.mobInteract` 的**装备分支**(只抄装备那段;它的 STAR_AMULET / HOWLING_SOUL / UNHOLY_BLOOD / BONE 回血是凋零骷髅专属机制,三个僵尸没有对应能力)。

**新增内容(三个文件各一份,逐字相同):** `mobInteract` + `private static isCataclysmWeapon`。判定链:ArmorItem→`getEquipmentSlot()`;`BlockItem` 且 `getBlock() instanceof SkullBlock`→HEAD;SWORDS/AXES/PICKAXES/SHOVELS/HOES/TOOLS 标签、`PhilosophersMaceItem`/`TieredItem`/`ShieldItem`/灾厄武器→MAINHAND(潜行时 OFFHAND);其余 `super.mobInteract`。装备序列:`playSound(ARMOR_EQUIP_GENERIC)` → `setItemSlot(slot, stack.copy())` → `dropEquipment(slot, 旧)` → **`setGuaranteedDrop(slot)`** → 非创造 `shrink(1)`。owner 门 = `getTrueOwner() != null && pPlayer == getTrueOwner()`。

**`setGuaranteedDrop` 不能省:** 这三个僵尸的掉落走 `dropAllTickDeathLoot`→`dropEquipment()`,按 dropChance 掷骰(默认 0.085),不给 guaranteed 就等于穿上就没了。多命"倒地"那一轮(`tickDeath` 里 `setRemainingLives(-1)`)不走 `dropAllTickDeathLoot`,所以掉命不会脱装备;`addAdditionalSaveData` 三家都调 super → 装备随存档持久。

**客户端早就全通,别再动渲染:** 三个 renderer 已有 `AdvancedArmourLayer.addCustomArmourLayers(this, context, MutantWitherSkeletonServantModel.INNER_ARMOUR, OUTER_ARMOUR)`(借凋零骷髅的 layer location)+ `new ItemInHandLayer(...)`;三个 model 都 `implements ArmedModel, AdvancedArmourWearingModel` 且 `translateArmour` 覆盖 HEAD/BODY/双臂/双腿。所以这次只缺服务端交互,渲染是现成的(从没放过装备,所以一直是死路径,首测要确认盔甲/手持物对位)。

**2026-10-16 追加:同一分支里加了腐肉喂食(用户令"恢复的生命值和 zombieservant 一样,每个腐肉两滴血")。** 位置是三条 `if/else if` 链的**第一支**(装备链之前,`EquipmentSlot equipmentslot;` 声明之后):`itemstack.is(Items.ROTTEN_FLESH) && getHealth() < getMaxHealth()` → 非创造 `shrink(1)` → `playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F)` → `heal(2.0F)` → 7 颗 HEART。数值出处已用 javap 核过 Goety 的 `undead.zombie.ZombieServant.mobInteract`:**就是 `heal(2.0F)` + `GENERIC_EAT`**(它自己发的粒子是 `ModParticleTypes.HEAL_EFFECT` 绿十字,我们按仓库规矩换 `ParticleTypes.HEART`,见 [[goetyominous-feed-particle-heart]])。粒子 Y 用 `getY() + getBbHeight() + 0.3D` 而非 `getRandomY() + 0.5D`,因为这三个体型高、用贴盒子坐标会被模型挡住(见 [[forge-heal-particle-above-bbox]])。

**武器伤害不用另接:** 三个僵尸没覆写 `doHurtTarget`,走原版 `Mob.doHurtTarget` → 本来就读主手 `EnchantmentHelper.getDamageBonus` 和击退/火矢附魔。

**灾厄武器判定只留反射那份:** WS 里另有一句直接 `item instanceof com.github.L_Ender.cataclysm.items.Cataclysm_Weapon_Item`,而 cataclysm 在 build.gradle 是 `compileOnly` → 运行时没装该 mod 时执行到这句会 NoClassDefFoundError。新代码只用 `isCataclysmWeapon`(`Class.forName` + try/catch)。

仓库惯例是每个仆从自带 `mobInteract`(ias 的 ConquillagerServant / EliteZPiglinBruteServant 同款写法),没有抽公共工具类;`isWitherNecromancerSoulJar` 之类也是各文件的 private static helper。相关:[[mm-mutant-husk-servant-port-notes]]、[[mm-jungle-zombie-servant-port-decisions]]、[[mm-servants-no-terrain-placement]]
