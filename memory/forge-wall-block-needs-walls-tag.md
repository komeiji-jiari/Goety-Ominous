---
name: forge-wall-block-needs-walls-tag
description: 自定义 WallBlock 不进 minecraft:walls tag → 自己人之间不连墙(却会连原版墙)
metadata: 
  node_type: memory
  type: reference
  originSessionId: b29fbf94-157e-4dd3-bc76-bd093f70660d
  modified: 2026-10-08T18:03:22.533Z
---

`WallBlock.canAttachTo` 的连墙判定:

```java
return state.is(BlockTags.WALLS)                        // 唯一对"邻居也是墙"有效的分支
    || !isExceptionForConnection(state) && isSideSolid // 墙的碰撞形状不是整块 → 恒 false
    || block instanceof IronBarsBlock || flag;
```

`isSideSolid` → `SupportType.FULL` → `Block.isFaceFull(getCollisionShape(), dir)`,而墙的形状是柱(8×16×8)+ 臂(6×16×8),**永远不是整块面**,所以第二条对墙恒 false。于是自定义墙只要不在 `minecraft:walls` 里:

- 自家墙 ⟷ 自家墙:**不连**(`WallSide.NONE`,并排两面墙各自独立成柱)
- 自家墙 ⟷ 原版墙:**连**(原版墙在 tag 里 → 第一条命中)

不对称,而且方块属性/模型/掉落全对,只看代码查不出来。`FenceGateBlock`、`IronBarsBlock`、`WalkNodeEvaluator`、`BlockAgeProcessor` 也读这个 tag。

同类漏项还有 `minecraft:doors`(消费方 `MoveControl`)、`minecraft:trapdoors`(`WalkNodeEvaluator`、`FallLocation` 摔死文案)、`features_cannot_replace`(`LakeFeature`/`MonsterRoomFeature` 防覆盖;`lava_pool_stone_cannot_replace` 只是 `#features_cannot_replace`+树叶+原木,补前者即自动覆盖,别重复建文件)。

**移植方块族时把原版 tag 归属整个反查一遍**(只补 pickaxe/needs_stone_tool 会漏掉这一整类):

1. 从原版 client jar 抽 `data/minecraft/tags/block/*.json` 和 `item/*.json`(1.20.1 是 `blocks/`+`items/` 复数目录,1.21 是单数);
2. 递归展开 `#` 子 tag,列出含本族方块名的所有 tag;
3. **再用 1.20.1 的 tag 目录过滤一次** —— `incorrect_for_*_tool`、`mob_interactable_doors` 是 1.21 才有,1.20.1 没这些文件;
4. 逐 tag 判断有没有真消费方:只被 `Vanilla*TagsProvider`/`BlockAgeProcessor`(世界生成)引用的(如方块 tag `slabs`/`stairs`、物品 tag `slabs`/`stairs`/`walls`)按需补,不必全加。

查消费方的办法(比读源码快):反编译 `BlockTags`/`ItemTags` 拿到 SRG 字段名(`f_13032_` = `walls`),再 `grep -rl f_13032_` 扫整个 `-srg.jar` 解出的 class 目录。

相关:[[forge-requirescorrecttool-needs-mineable-tag]](同一类"属性和资源全对、就是漏个数据文件")
