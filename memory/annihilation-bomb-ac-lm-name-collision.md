---
name: annihilation-bomb-ac-lm-name-collision
description: "'AnnihilationBomb' 在本仓库指两个不相干的东西(AC 泰坦核弹 vs LM 湮灭弹射物)+一个外部 mod 类,批量重命名必须按包分区并加 \\b 词边界"
metadata: 
  node_type: memory
  type: project
  originSessionId: ec9b5995-7995-4e58-9df8-489c992bbfa0
  modified: 2026-09-29T20:51:21.513Z
---
仓库里三坨同形标识符，**批量改名/搜索替换时必然互相误伤**：
| 归属 | 标识符 | 位置 |
|---|---|---|
| AC（自己的泰坦核弹，2026-09-29 已全改名 `TitanBomb*`） | `AnnihilationBomb{Block,BlockEntity,BlockItem}` / `AnnihilationBombEntity` / `RenderAnnihilationBomb` / 字段 `ANNIHILATION_BOMB` | `common/blocks/ac/`、`common/blocks/entities/ac/`、`common/entities/projectile/`、`common/items/ac/`、`client/render/ac/`、`init/ac/*Registry` |
| LM（L_Ender's Cataclysm 湮灭追猎者的弹射物，**用户明确要求别碰**） | `AnnihilationBomb`、`SmallAnnihilationBomb`、`AnnihilationExplosion`、`AnnihilationFlameStrike`、`AnnihilationPursuerServant*`、字段 `LmEntityRegistry.ANNIHILATION_BOMB` / `SMALL_ANNIHILATION_BOMB` | `common/entities/ally/lm/**`、`common/init/lm/LmEntityRegistry` |
| 外部 mod（Legendary Monsters） | `net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SmallAnnihilationBombEntity`、`...Particle.custom.AnnihilationBombTrail` | 只 import，`common/events/LmAnnihilationBombGuardEvents`、`FlameDrifterServant`、`TheObliteratorCloneArmed` |
**Why:** 两个具体陷阱，2026-09-29 改名时差点踩：
1. `SmallAnnihilationBombEntity` **整段包含** `AnnihilationBombEntity` 子串 → 无脑 `sed 's/AnnihilationBombEntity/TitanBombEntity/'` 会把它改成 `SmallTitanBombEntity`，编译直接炸。
2. `client/render/ModModelLayers.java` 里 LM 的引用（637/743 行）和 AC 的（1013 行）**挨在一起**，通配替换会把 `LmEntityRegistry.ANNIHILATION_BOMB` 一起改掉。
**How to apply:** 改名前先 `grep -rl` 出文件清单、按包分区；外部 mod 类/不归自己管的模块一律排除；替换用 `\b标识符\b` 锚定，或带限定前缀（`AcEntityRegistry.ANNIHILATION_BOMB`）而非裸名。`someClass.ANNIHILATION_BOMB` 这种裸字段名在 `ModModelLayers` 里两种归属并存，只能逐个限定前缀。
**改注册 id 会废掉旧存档**：`annihilation_core` → `titan_bomb` 时用户知情并接受（旧存档方块变空气、物品变 missing、SEHelper 冷却 NBT 对不上）。连带必须改的资源：blockstate/模型/贴图文件名、loot table 文件名 + `name` + `random_sequence`、lang 的 `block./item.` 两条键、以及 `data/alexscaves/tags/blocks/remote_detonator_activates.json`（AC 遥控起爆器）。
相关：[[goetyominous-mod-folder-isolation]]