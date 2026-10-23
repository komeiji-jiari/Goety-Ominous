---
name: goety-training-block-base
description: Goety 的「训练方块」体系（TrainingBlock/TrainingBlockEntity）可直接继承做造兵方块，唯一抽象方法是 maxTrainAmount()
metadata: 
  node_type: memory
  type: reference
  originSessionId: aff51502-c231-47ab-af01-d1beb79bae75
  modified: 2026-09-26T19:08:07.463Z
---

Goety 的造兵方块体系可以整条继承，不用从 `BaseEntityBlock` 重写。样板是原版 `SpiderNestBlock` / `SpiderNestBlockEntity`（反编译件在 `build/cfrgoety9/com/Polarice3/Goety/common/blocks/`）。

关键结论：

- **父类链**：`TrainingBlockEntity extends OwnedBlockEntity extends ChunkLoadBlockEntity`，三层全 public abstract，构造器都是 `(BlockEntityType<?>, BlockPos, BlockState)`；方块侧 `TrainingBlock extends BaseEntityBlock`，构造器收 `BlockBehaviour.Properties`。
- **`predicate()` 那坨 Curios/SEHelper/Lichdom 索敌判定全是 public static 方法**（`CuriosFinder.validXxxMob`、`SEHelper.isGrudged`、`MobUtil.isWitchType`…），**可直接继承，一行都不用抄**。同理 `tick` 主体、`readNetwork/writeNetwork`、owner 存取、`WorldlyContainer`、`TrainingBlock.use` 的 IWand/GroundGrimoire 交互也都免费。
- **唯一必须自己实现的抽象方法**：`ITrainingBlock.maxTrainAmount()`。另外必须自己写 `newBlockEntity` + `getTicker`（照 SpiderNestBlock 的 `createTickerHelper(type, 你的BE类型.get(), level.isClientSide ? TrainingBlockEntity::clientTick : TrainingBlockEntity::serverTick)`，三元表达式能编译，不用像 CFR 那样加裸类型 cast）。
- **可选覆写点**（全 public 非 final）：`setVariant(ItemStack, Level, BlockPos)`（空实现，是选变体的地方）、`isFuel(ItemStack)`（默认 true）、`summonLimit()`（默认 false）、`startTraining(int, ItemStack)`、`playSpawnSound()`、`tick(Level, BlockPos, BlockState, TrainingBlockEntity)`。
- **字段全 public**：`trainTime` / `trainTimeTotal` / `trainAmount` / `showArea` / `sensorSensitive` / `guarding` / `grounding` / `reachedLimit` / `itemStack` / `entityToSpawn`；`RANGE` 也是 public static（但非 final，别全局改）。唯一 private 是 `blockPosSource`，有 public `getListenerSource()` 兜底。
- **`tick` 的第 4 个参数就是自己**（为静态 ticker 服务），基类内部混用 `blockEntity.` 与 `this.`，覆写时必须 `super.tick(level, pos, state, this)`。
- **`setVariant` 被三处调用**：`tick`（`updateVariant > 0` 时连调 5 次）、`TrainingBlock.setPlacedBy`（放置当下）、`startTraining`。加 `getTrainMob() != 目标类型` 守卫可让它幂等。
- **BER 可直接复用** `com.Polarice3.Goety.client.render.block.TrainingBlockRenderer`（public 泛型 `<T extends TrainingBlockEntity>`），注册后继承来的 showArea 开关才画得出范围框。
- **`TrainingBlock.use()` 整条交互链 gate 在 `blockEntity.getPlayer() == pPlayer`** 上；`getPlayer()` 服务端走 `getTrueOwner()`（`EntityFinder` 解 UUID），能拿到同一个 ServerPlayer 实例，引用相等成立。
- **性能坑**：基类 `tick` 每 tick 调一次 `summonLimit()`。SpiderNest 的实现是 `serverLevel.getAllEntities()` 全量扫描 + 全局计数（不是 AABB 局部计数），照抄的话每个方块每 tick 扫一遍。改局部计数会变成「每个方块一份上限」，语义不同。

另注：`TrainingBlockEntity` 里 `markUpdated()` = `setChanged()` + `level.sendBlockUpdated(pos, state, state, 3)`，且自带 `this.level != null` 守卫，所以 `setVariant` 里不必再加 `level instanceof ServerLevel` 判空。

判断「编译期 API 是否等于运行期 API」的手法：比对 `libs/goety-2.5.56.0.jar` 与 `build/goety583.jar` 里同名 class 的 md5，一致就说明反编译产物可用。见 [[forge-compile-setup]]。

仓库内已有的同类实现：`WolfTotemBlockEntity`（狼图腾，extends TrainingBlockEntity，多材料合成式投喂）。AC 联动版见 [[goetyominous-mod-folder-isolation]]。
