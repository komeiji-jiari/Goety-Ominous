# Memory Index

## ⚠️ 本机最高优先级
- [No edit without consent](no-edit-without-consent.md) — ⚠️最高优先级：除非用户明确说"edit"这个词，否则绝不修改任何语段/创建任何文件，只给代码和行号
- [代码不加注释](no-comments-in-code.md) — 写进工程的代码一律裸代码（无 javadoc/行内注释），"为什么"写在对话或记忆里；已有注释不动
- [⚠️Windy 移植 1:1 铁律](windy-port-1to1-principle.md) — 最高优先级：必须逐签名逐方法复刻 1.21（含常量/数值），不许简化或砍功能；缺的原版件用反编译器读源码后等价实现
- [⚠️移植数据不得捏造](port-data-fidelity-rule.md) — 注册数据(尺寸/eyeHeight/分类/数值)必须与 1.21/Goety-3 逐字段一致，先查再写；1.20.1 缺 API 用等价手段复现同数值（实例：sized 0.3125 + getEyeHeight 覆写）

## 本机补充笔记（E:\java）
- [MutantBlaze 仆从移植进度](mutant-blaze-servant-port-progress.md) — ShieldPart/Fireball/Rod 已交付；RodlingServant 待办（裁剪已定、反编译源/关键方法速查）
- [Dreden 移植计划](dreden-port-plan.md) — Goety1 Dreden → 1.20.1/goety2 基座核对与文件面；基座=goety2 AbstractWraith，FrostBall/两 goal 需自移植，法术最后
- [Axis 的 SRG 映射](srg-axis-mapping.md) — 反编译 goety 还原 `Axis.f_252392_` 时踩过坑：它是 YN 不是 YP，猜错模型就转向反了
- [Goety2 基座源码](goety2-base-source.md) — 移植参考基座是 libs/goety-2.5.56.5.jar（deobf，用 javap 反汇编），不是仓库里的 Goety-3-main/Goety-master 源码树
- [Goety2 反编译源码位置](goety2-src-decompile.md) — Goety 2.5.58.2 源码已反编译在 `.disasm/goety_src/`，读前置模组直接看它；含重生成命令与"根目录 Goety-3 不能当参考"的坑
- [Goety2 仆从基座](goety2-servant-system.md) — Summoned/Owned/IServant 继承、stance/命令/巡逻、registerGoals 自动装的 goal 与优先级、goal 清单与坑、最小模板
- [Goety2 法术基座](goety2-spell-system.md) — ISpell/Spell/SummonSpell 契约、startSpell→useSpell→SpellResult→stopSpell 生命周期、SpellStat、召唤模板、无注册表只能用 MagicFocus、坑
- [联动的两层 compat 结构](goetyominous-compat-architecture.md) — goety+curios 硬前置、软前置清单、compat/mod 门槛层 vs compat/<abbr> 注册层、每个联动一套目录、mods.toml 缺声明的不一致
- [软前置反射三条铁律](goetyominous-soft-dep-reflection.md) — 能放门槛后就别反射；mixin 绝不 @Mixin 软前置类（用注册名字符串或 Class.forName）；惰性缓存+吞异常写法
- [Stash 核对结论 2026-09-26](stash-audit-2026-09-26.md) — 6 个 stash 全部无丢失内容可安全 drop；别用整体 diff 判断已合并（三类误报清单）
- [错题本·编译与构建](cuotiben-build-and-merge.md) — 合并后必须全量编译：两边都加同名符号/一边删一边没动的两个静默坑；clean 砸 fg_cache 的假错误；别凭记忆写原版 API
- [错题本·Goety 仆从框架](cuotiben-goety-servant-framework.md) — 注册表隔离、goal 优先级双向陷阱（太低被抢 / 0 号挤光近战）、导航器别在构造里抄死、toml 覆盖默认值
- [错题本·LM 移植](cuotiben-lm-port.md) — isAlliedTo/UpdateWithAttack/最小距离招三查点、限伤放最上游、移速=基础速×倍率（圣骑三档 0.30 不覆写）、语言键前缀、别人有公开 API 别抄
- [错题本·工具与流程](cuotiben-tooling.md) — 别信工具显示（Grep 的 `//`、Edit 缩进）、中文 Windows javac 是 GBK、移植前先和原版同名类对比、伤害类型 tag、SRG 混淆名、mixin 的 @Shadow 目标 javac 不校验、**跨版本同名 API 必须看 1.20.1 本体实现（createArrow(null) 崩服 / setEffectsFromItem 只认 tipped_arrow / getBaseClass 假 API / 光照旁路只有 1.21 有）**
- [Abyss Staff 法术加强计划](abyss-staff-enhancement-plan.md) — key_of_rlyeh 要强化的 14 个 abyss 法术及各自数值要求（召唤 2→3、伤害 +N、连发次数等），防遗忘用
- [Abyss 加强·实际类名核实](abyss-enhancement-actual-names.md) — 14 个法术的真实类名/包（Rotten→ZombieSpell、WaterWhip→GulfTentacleSpell、Steaming→SteamSpell）；且 rightStaff 无法区分 key_of_rlyeh
- [拉莱耶 tooltip 移植](key-of-rlyeh-tooltip-port.md) — 朋友示范包移植进本 mod：资源已落地、Java 待用户自填；不写跨 mod 兼容层；三项设计决议与两个静默坑
- [ModernUI 文字管线逆向](modernui-text-pipeline-findings.md) — 共存失败已撤待朋友方案；三条 shader 路径/图集语义相反/反射点/反编译命令，回来直接用
- [Windy Update 移植计划](windy-update-port-plan.md) — Goety-3 `2f424351` 移植：范围(Breeze/Hurricane/Whirling Cage/Wind Shrine+群系，排除 Pale Crystal Ball 与试炼密室)、对照源码在 `E:\java\Goety-3-main`、AI 全 goal 制、1.21 缺失 API 复刻清单与顺序；CFR 反编译器在 `E:\java\tools\cfr-0.152.jar`
- [1.21.1 素材提取法](mc-1211-asset-extraction.md) — 本体 jar 无 ogg；sounds 在 E: 启动器 assets 对象库按 SHA1 提；1.21.2 起部分音效被重编码（按基准版本取）；**大 ogg 走资产索引+官方 CDN（镜像会截断/第三方会转码）**；mappings 解混淆对照表
- [凶兆/队长掉落实证](trial-omen-captain-drop-findings.md) — 1.21.1 掉瓶=仅掠夺者战利品表(is_captain,无击杀门)；1.20.1 die 内联坏兆块+精确注入签名;1.20.1 官方映射 jar 路径与常用 CFR 手法；1.21.1 jar 无 zh_cn
- [1.20.1 原版数据源](mc-1201-vanilla-data-source.md) — 权威 data（战利品表/标签）在 PCL Forge 客户端 jar：E:/.minecraft/versions/1.20.1-Forge_47.3.12/；附 1.20.1 loot 格式与 API 等价件实证（name 字段、无 Codec.validate、ClipContext 传 null、NumberProvider、BuiltInRegistries 取 tag）
- [宝库移植进度](vault-port-progress.md) — 宝库+不祥宝库已全部落地并编译通过（loot 函数在 utils 包、不祥宝库物品、lenient/LinkedHashSet 收尾）；仅剩进游戏实测
- [试炼刷怪笼移植计划](trial-spawner-port-plan.md) — 第四批**已全部落地并编译通过**（2026-10-08/09）：交付清单 + 1.20.1 无法消除的差异 + **⚠️有意偏离 1.21 的 6 条（和平/禁刷怪三处、MONSTER 光照旁路、装备槽位、药箭表）** + SRG 坑（fR=setPersistenceRequired、Direction.a=DOWN、Axis a.d=YP、**Vec3 c=atBottomCenterOf**、**Mth lerp/rotLerp 是两个函数**）
- [试炼密室移植计划](trial-chamber-port-plan.md) — 2026-10-09 **已落地、批 5 实测中**：数据 170 模板/45 池 + 陶片 + 别名机制（⚠️ 懒执行血案：别名必须靠闭包捕获）+ 陶罐战利品（1.20.1 陶罐 BE 无 lootTable，用 `BlockBehaviour.onRemove` HEAD 钩子补上）；含 size上限/pool_aliases/二进制 NBT 三大 1.20.1 障碍的解法与全部实证
- [密室结构 NBT 引用清单](trial-chamber-structure-nbt-refs.md) — 移植密室结构时必须把 `minecraft:` 引用改成 `goetyominous:`（5 张刷怪笼表 + 6 张装备表的完整对照表），漏改会静默失效
- [刷怪笼加怪脚本](trial-spawner-mob-tool.md) — `tools/add_trial_spawner_mob.py`：给密室刷怪笼家族纯新增怪（family=哪一档 / --base=周边方块抄谁）；含引用链、两个轴、6 个坑（结构 json 必须 sort_keys=False、worldgen 不能 /reload 等）；已加 mired 进 melee
- [试炼战利品表](trial-loot-tables.md) — 宝库/刷怪笼战利品的链路与热更边界（内容可 /reload、接线在 worldgen/代码里）；2026-10-11 给普通宝库加了 18 条 goety 条目；`enchant_randomly` 指定列表可绕过 `isAllowedOnBooks` 产书
- [新增文件必须 git add](git-add-new-files.md) — 我新建/搬运的 json、贴图、模型、语言等落盘后立刻 add 并复查无未跟踪；2026-10-07 漏过两件被用户抓

## 徒弟错题本（仓库 memory/ 同步于 2026-10-23）

## 项目与约定
- [多 jar 拆分](goetyominous-split-into-modules.md) — 判据=核心文件直接 import 第三方包；配置已拆 acam/uasr/lm/mm/of 5 组
- [远征系统方案](expedition-system-plan.md) — 核心化旅行家远征/指南针：核心/PP 分层、独立 ExpeditionConfig、绑定反馈；已并入 aaacat 1.1.8 交付/会话机制，UI 双 Tab/滚动列表/队伍卡片对齐 aaacat，含“召集旅行家”；战利品改存旅行家背包/空手交付/箱子自动存入，箱满/跟随/待命可空手取回，未交付物品禁止再派遣；**已落盘/编译+无PP启动通过，待游戏内实测**
- [ModMobType](modmobtype-name-collision.md)、[AnnihilationBomb](annihilation-bomb-ac-lm-name-collision.md) — 常量不能跨 jar 复制
- 工作偏好：[不写注释](no-code-comments.md)、[别擅自写配方手册](no-unrequested-recipes-patchouli.md)、[模组名 4 处显示面](goetyominous-name-surfaces-ask-scope.md)、[别自动 build jar](no-auto-build-jar.md) — 文案要就问;用户说构建才构建
- 死代码：[清理偏好](goetyominous-deadcode-cleanup-preference.md)、[脚本](goetyominous-deadcode-analysis-scripts.md) — .scratch/_deadcode.mjs + _deadfields.mjs
- 配置归属：[该进哪个文件](config-file-split-rule.md)、[LM 伤害](lm-servant-damage-config.md)、[敌对/仆从共用](goety-hostile-servant-shared-config.md)、[无引用键不是死键](mobsconfig-unwired-keys-are-not-dead.md)、[TOML 排序](forge-config-toml-order-historical.md) — 删前先问；键序是历史插入序，重建才按源码
- 落点/收尾：[modid 子包隔离](goetyominous-mod-folder-isolation.md)、[刷怪蛋要补 model](servant-spawn-egg-item-model.md)、[实测实例/日志](goetyominous-test-instance-and-logs.md) — 漏 model 会紫黑块
- 可选依赖注册：[必须放 CompatManager](optional-mod-registration-manager-gated.md) — core 无条件注册 extends 第三方类型会缺模启动崩
- 移植准则：[表现原版/实现 Goety](ac-servant-parity-keep-original.md)、[飓风分叉是有意的](hurricane-breeze-intentional-divergence.md)、[移植漏单行](ac-goety-port-probability-gates.md) — 冲突时 Goety 优先
- MM 手臂道具：[已按用户令删除](mm-mutant-jungle-zombie-arm-port.md) — 别再主动重提
- dist 审计：[现状+已修/仍脆弱](client-dist-audit-fragile-spots.md) — HUD 已移 client 包;LM 粒子嵌套类仍须保持无类级注解
- [错题本同步到仓库](memory-notebook-sync-to-repo.md) — memory 整份复制到仓库 memory/，独立提交后推送

## Goety 仆从骨架
- 状态机：[跟随/待命](goety-servant-follow-standby.md)、[空闲冻结](goety-summon-idle-freeze.md)、[能力位vs状态位](goety-iservant-capability-vs-state.md) — 短路 checkDespawn→用 Summoned.WanderGoal
- 目标/flag：[命令模式冲突](goety-command-mode-conflict.md)、[优先级/flag 压制](goety-servant-goal-priority-flags.md)、[无 flag 抢朝向](goety-attackgoal-no-flags-yaw-fight.md)、[canContinueToUse 默认](vanilla-goal-cancontinue-default.md) — 自定义 goal 须 gate isCommanded
- 解散/上限：[解散二次确认](goety-servant-trykill-confirm.md)、[上限两条通道](goety-servant-limit-two-channels.md)、[canAttackType=false 废索敌](goety-servant-canattacktype-kills-targeting.md)、[多命 vs 解散/寿命](goety-multilife-servant-dismiss-conflict.md) — 覆写 tryKill;多命复活会吃掉 DISMISSED
- 召唤法术：[约定](goety-summon-spell-conventions.md)、[王冠/长袍两通道](goety-crown-vs-robe-two-channels.md)、[寿命锚点](summon-spell-lifespan-anchor-pattern.md)、[owner 顺序](goety-summonspawn-owner-order.md)、[属性偏移存 NBT](goety-configurable-attrs-reset-on-load.md) — 先 setTrueOwner 再 finalizeSpawn
- 蛋/移速：[蛋要潜行才有主人](goety-egg-owner-requires-crouch.md)、[移速多个通道](goety-move-speed-two-channels.md)、[AC 冲量式 MoveControl](ac-impulse-movecontrol-speed.md)、[持久冷却用 SEHelper](goety-persistent-item-cooldown.md) — 别改 MovementSpeed 属性
- 骑乘：[条件链](goety-servant-riding-mechanism.md)、[positionRider 两个重载](forge-positionrider-two-overloads.md)、[下马摔伤](mount-dismount-fall-damage.md)、[水中速度/姿势](forge-ridden-mob-water-speed.md)、[服务端 delta 清零](forge-travelridden-server-zero-delta.md) — 水里 getRiddenSpeed 失效;单参 super=崩服
- 骑乘者接管：[不操控时 AI 接管](goety-mount-idle-ai-authority.md)、[isImmobile 定身](goety-mount-immobile-anim-root.md)、[AC 双重渲染](ac-rider-layer-double-render.md)、[Mob 骑 Mob 关载具 AI](forge-mob-rider-disables-vehicle-ai.md) — 按 zza/xxa 返回玩家/null
- 多部件：[肢体 extends Summoned 三坑](goety-multipart-part-as-servant.md)、[肢体被打崩](goety-multipart-part-projectile-crash.md)、[部件晚一拍](sauropod-part-oldpos-teleport-neck.md)、[yBodyRot 客户端算](sauropod-client-ybodyrot-local.md)、[移动时 yBodyRot 硬贴](mob-tickheadturn-bodyrot-snap.md)、[骑乘扭头](goety-sauropod-ridden-neck-lag.md)、[蜈蚣体节抖动](goety-centipede-ringbuffer-freeze-seam.md) — 委派 isCommanded 不委派 getCommandPos→NPE
- 水陆两栖：[导航 goal 规则](goety-water-navigation-goals.md)、[水面弹跳](goety-amphibious-floatgoal-bounce.md)、[泳控对照](vanilla-swim-movecontrol-xxa-reference.md) — 水下自灭用 AirAndWaterRandomPos
- 潮涌之叶：[上岸/下水/跳劈](goety-leapkelp-land-water-leap.md)、[泳姿来自珊瑚傀儡](leapkelp-swim-anim-coralssus-port.md)、[珊瑚傀儡≠珊瑚巨像](cataclysm-coral-golem-vs-coralssus.md) — 离水关 wantsToSwim
- Leapleaf：[水陆门控](goety-leapleaf-leap-goal-gating.md)、[静止姿势](goety-leapleaf-model-geometry.md)、[restTick 冻结 AI](goety-leapleaf-rest-immobile-freeze.md) — 无 onGround 检查→浅水会跳
- 灵魂鹫：[仆从移植](soul-vulture-servant-port.md) — 栖息三态
- 索敌：[搜索盒 ±4 格](goety-target-search-area-vertical-4-blocks.md) — 高空飞仆锁不上地面怪
- 长尾鲨：[两段式索敌](thrasher-servant-two-stage-targeting.md)、[玩家骑乘](thrasher-servant-player-riding.md) — 声呐目标必须 isInWater
- 移植审计：[OF 三易漏落点](of-servant-port-checklist.md)、[蚀牙兽还原度审计](corrodent-servant-fidelity-audit.md)、[试炼四效果定案](trial-effect-port-1.21.1.md)、[Summoned 已含亡灵点燃](goety-summoned-dohurttarget-duplicate.md)、[Frowzy 有意偏离 OF](frowzy-servant-deliberate-divergences.md) — finalizeSpawn 里的 elite 掷骰;doHurtTarget 别重复搬
- 其他仆从件：[自建仆从要 ATTACK_DAMAGE](goety-servant-missing-attack-damage.md) — Aether 减伤会查 source 属性;Mob.createMobAttributes 不含、[飞行 isStableDestination](forge-goety-flyer-isStableDestination.md)、[飞行游荡死区](forge-flying-stroll-deadzone.md)、[训练方块当造兵方块](goety-training-block-base.md)、[FlyingItem 返还](goety-flyingitem-return-semantics.md)、[复用模型私有字段](goety-model-reuse-private-fields.md)
- 陷阱蓝线：[来历与剥离](mm-shulker-trap-texture-blue-outline.md) — 贴图多描的 2px 浅蓝边;改 PNG 别用 GDI+,用 .scratch/_strip_trap_outline.mjs
- 仆从贴图自家化：[共用渲染器一起换](mm-servant-textures-own-copies.md) — 霜冻/尸壳换自家皮;雪球冰锥沙球同源贴图要一起改;husk 沙型只两种
- 潜影贝染色：[现状](mm-mutant-shulker-dyeing.md) — 本体/陷阱可染,两种导弹明确不染;小导弹用自家实体
- 转化物品：[Goety 模板三条](goety-revive-item-parity-template.md) — extends Item 才有冷却门;宿主三段式含 convert tag;**对齐只动冷却/宿主/tag,写法(返回值/音效/粒子)保持原样**
- 死亡返还：[飞回复活物品](mm-zombie-servant-death-drop-hook.md) — MM 返还物已删;复活必重置 DeathTime 否则下次死立刻真死
- 潜影贝浮空：[四处注入点](mm-shulker-levitation-injection-points.md) — 本体两分支/陷阱/法术;已浮空不重刷;阈值键现 100
- 导弹弹道：[服务端制导+客户端缓动](mm-shulker-bullet-trajectory.md) — 客户端也跑制导会抖;updateInterval(1);解锁 60→8、每 tick 重瞄
- 动画门控：[priorityActive 镜像 + 走路混合出帧](mm-mutant-shulker-anim-gate.md) — resetAll() 抹姿态;混合状态要放门外
- 飞行已删：[仆从不再飞行](mm-shulker-flight-removed.md) — FlyGoal/事件 5-7/spin 动画全删,MM 配置键留成未接线
- 丛林僵尸：[移植定案](mm-jungle-zombie-servant-port-decisions.md) — 多命保留/跳劈去掉/手臂道具做过又删
- 三僵尸动画：[相位突跳+战斗中立正+走站衔接](mm-zombie-sinewave-walk-jitter-fix.md)、[hurt vs die 顺序](mm-hurt-inline-event-die-order.md)、[凋零骷髅立正移动](mm-advanced-animation-client-sync.md) — 跨帧状态放实体
- 正弦脚步：[相位定标](mm-sine-walk-phase-footstep-lock.md) — 每格 8.64;距离只能用 getX()-xo
- 宽生物走路晃：[路点角偏移](mm-wide-mob-waypoint-offset-yaw-sway.md) — 疣猪兽 (int)(bbWidth+1)*0.5=1.5 格;MM 导航已改中心点
- 凋零骷髅走姿：[walk_progress 随帧率](mm-walkmodifiers-framerate-phase.md) — getFrameTime() 当每帧增量→FPS/40
- 倒地转向：[倒地冻结朝向](mm-zombie-servant-down-yaw-freeze.md) — downYaw 在 die/event 7 捕获
- 假死红：[overlay 不算 deathTime](mm-servant-death-red-overlay.md) — 客户端 tick 复活清零 deathTime;mixin 已删
- 仆从不改地形：[雪删/流沙加回](mm-servants-no-terrain-placement.md) — 尸壳 4 处流沙已恢复
- 突变仆从 AoE：[友军/主人守卫](mm-servant-aoe-ally-guards.md) — canHarm 不排除主人;击退要单独跳过
- 突变尸壳：[husk 定案](mm-mutant-husk-servant-port-notes.md) — 不做小海龟/HuskHammer、[地涌扑空锁 AI](mm-husk-geyser-failed-leap-lock.md) — 已加出口，有意偏离上游
- 三僵尸右键：[穿装备+腐肉喂食](mm-zombie-servant-equip-interaction.md) — setGuaranteedDrop 不能省
- 移植陷阱：[CFR 全限定名残留](mm-port-cfr-fqn-residue.md) — 旧包名残留不报错但要改
- 方块/容器/仪式：[tesseract 容量 tag](goety-tesseract-capacity-tags.md)、[方匣取出强制跟随](goety-tesseract-eject-forces-follow.md)、[仪式每cast防 stale](goety-ritual-percast-state-stale.md)、[Mold 写死实体](goety-mold-hardcoded-mixin.md) — 占4格 medium/占2格 small
- 标签：[powerful_servants 纯成员标签](goety-powerful-servants-tag.md)、[联动配方在自己 data/ 下](compat-pack-recipes-in-own-datapack.md) — 漏 required:false 会级联炸
- 弹射物：[换 Goety 基类](goety-projectile-base-choice.md)、[幽灵弹三要素](projectile-no-despawn-three-conditions.md)、[风弹击退=同 tick ADD](wind-charge-knockback-delivery.md)、[友伤免疫吞药箭效果](goety-friendly-fire-suppresses-tipped-arrow-effects.md) — 覆写 tick 用 baseTick();友方辅助弹射物别依赖 hurt
- 波/粒子：[冲击波尺寸换算](goety-shockwave-particle-geometry.md)、[AbstractWave 伤害标定](goety-abstractwave-attackentities-scale.md) — size 是半径,实参序 (originSize,size,speed)
- 敌对突变怪动画：[渲染器覆盖式修复](mm-hostile-mob-animation-fix.md) — 无 mixin;后注册覆盖 + 仆从模型当零件架;6 种（含潜影贝，须留两段飞行动画）

## Alex's Caves 联动
- 移植要点：[要点](ac-compat-porting-notes.md)、[不许 mixin](goety-ac-no-mixin.md)、[继承而非重写](goety-ac-dinosaur-spirit-port.md)、[坐骑能力键](goety-ac-servant-keydown-channels.md)、[坐骑跳跃权威](goety-ac-servant-mount-jump-authority.md) — 蛋只认 HATCH+NEEDS_PLAYER
- 机制/伤害：[AoE 友军判定反了](ac-ao-ally-check-direction.md)、[TephraExplosion 三坑](ac-tephra-explosion-quirks.md)、[核爆免疫两半](ac-nuke-immunity-two-halves.md)、[辐照友军免疫](ac-radiation-ally-immunity.md) — 须翻成 this.isAlliedTo(living)
- 杂项：[震动 handler 重复](ac-screen-shake-duplicate-handler.md)、[合成 Post 清零头部](ac-synthetic-renderpost-head-zeroing.md)、[甘蔗钩(已回退)](ac-candy-cane-hook-port.md)、[沙虫垂直力](ac-gumworm-vertical-force-split.md)、[螺纹长枪无冷却](ac-ortholance-no-cooldown.md)

## Alex's Mobs 联动
- 老虎仆从：[移植决定](tiger-servant-port.md) — AnimalSummon 基类;主动猎杀加过又撤回
- 蚊鬼不洁血：[强化跨死亡传递](warped-mosco-unholy-blood-inheritance.md) — 应用在 finalizeSpawn 之后
- 抹香鲸抓乌贼：[纯 API 复刻](am-cachalot-whale-squid-capture-api.md) — 大王乌贼仆从已删除,别再主动重提

## LM 联动
- 版本/层级：[机制要看 jar 版本](lm-mechanic-presence-version-check.md)、[切除 boss 机制](lm-servant-boss-mechanic-stripping.md)、[选层看真父类](lm-servant-tier-from-class-superclass.md)、[nextInt()*100 门控](lm-nextint-overflow-random-gate.md) — 常量池 SRG+mojmap 双风格 grep 判
- 待命/AI：[待命沉睡时序](lm-servant-standby-sleep-timing.md)、[ModPathNavigation 空壳](lm-modpathnavigation-shortcut-stub.md)、[共享 goal 未覆写 tick 频率](lm-shared-goals-tick-rate-deviation.md) — 趴下用 reverse(awake/awaken)
- 弹射物/血量：[弹射物模型借不了](lm-projectile-model-borrow-impossible.md)、[圣骑盟友判定是好的](lm-projectile-ally-check-parity-quirk.md)、[IAnimatedBoss 虚拟血量](lm-ianimatedboss-virtual-health.md)、[The Obliterator 决定](the-obliterator-servant-port-plan.md)、["红"从哪来](lm-red-projectile-sources.md)、[金钻皮肤](lm-knight-skin-variants.md) — 红染料红化已删；T extends LM实体 + private 堵死继承
- BK/RS：[脑袋挂 BK 的可行性](bk-rs-head-attach-feasibility.md)、[两套乘客机制](bk-rs-two-passenger-mechanics.md)、[侧位跟随(已删)](bk-rs-follow-slot-formation.md) — 骨架同源三条坑
- 屏幕震动：[LM/MM 各有仓库自有开关](lm-mm-screen-shake-upstream-configs.md)

## 原版/Forge 机制
- 属性/碰撞：[上台阶](forge-maxupstep-mapping.md)、[近战射程](forge-meleeattackgoal-reach.md)、[.sized vs getDimensions](forge-entity-sized-vs-getdimensions.md)、[1.20.1 溺水链](forge-1.20.1-air-supply-drown-chain.md)、[1.20.1 无肉 tag](forge-1.20.1-no-meat-tag.md)、[eat() 双扣](forge-livingentity-eat-double-consume.md) — 溺水在 ForgeHooks.onLivingBreathe
- 私有原版方法：[collide / rotlerp 私有 / 没有 getGravity](mc-1201-entity-collide-private.md) — 要 AT 写 SRG 名
- tick/导航顺序：[baseTick 绕过](forge-bypass-intermediate-tick-baseTick.md)、[MoveControl 晚于 goal](forge-movecontrol-runs-after-goals.md)、[goalSelector 在 aiStep 内](forge-goalselector-in-aistep-ordering.md)、[LookControl 回正](vanilla-lookcontrol-head-forward.md)、[followThePath ULP](forge-followthepath-bbwidth-ulp-freeze.md)、[近战别再喂导航](mc-moveto-every-tick-yaw-whipsaw.md)、[Citadel moveTo 恒 true](citadel-advancedpathnavigate-moveto-always-true.md)、[限转向=绕圈](mob-turn-cap-min-radius-orbit.md) — 顺序 goal→nav→moveControl
- 事件/注册：[RightClickBlock getLevel](forge-rightclickblock-getlevel-pattern.md)、[re-add 发 JoinLevelEvent](forge-entityjoinlevelevent-on-readd.md)、[DeferredRegister 惰性](forge-deferredregister-clinit-lazy-trap.md)、[页签定点插入](forge-creative-tab-insert-position.md)、[dungeonsdelight 并发崩](dungeonsdelight-fastutil-dispenser-race.md)、[1.21 爆炸 null source](goety-1.21-explosion-null-damagesource.md)、[MobEffect 无死亡钩子](forge-1.20.1-mobeffect-no-death-hook.md) — putAfter 顺序调用会反序
- 寻路/同步：[NodeEvaluator 按格钩子](forge-nodeevaluator-per-cell-hooks.md)、[裸 Entity 位置包频率](forge-bare-entity-update-interval-smoothness.md)、[插值同步顺序](mc-1201-client-interp-sync-order.md)、[客户端 getDeltaMovement](mc-client-deltamovement-motion-packets.md) — 按格判定须覆写 getCachedBlockType
- 死亡/掉落：[捕获窗口](forge-death-loot-capturedrops-window.md)、[deathTime 遮蔽倒地旋转](forge-deathtime-shadow-suppresses-death-roll.md) — LM 的 deathTime 遮蔽原版
- 双格掉落：[提前拆另一半吞掉落](double-block-early-removal-eats-drop.md) — setBlock(AIR) 只能放创造里
- 方块/标签：[blockstate 重叠键](forge-blockstate-overlapping-keys.md)、[hot_floor 属 is_fire](forge-hot-floor-fireimmune.md)、[氧化方块逐档](mc-weathering-block-per-tier-props.md)、[requiresCorrectTool 要 mineable tag](forge-requirescorrecttool-needs-mineable-tag.md)、[自定义墙要 walls tag](forge-wall-block-needs-walls-tag.md)、[biomes 指向不存在的 tag](forge-1.20.1-missing-biome-tag-no-structure.md) — 群系 tag 缺失→结构永不生成
- 战利品表：[附魔改名炸表](mc-121-1201-enchant-rename-breaks-loot-table.md) — 一个非法 id 即整张报废
- 渲染/粒子：[未绑定 spriteSet](forge-particle-unbound-spriteset.md)、[寿命末 remove NPE](forge-particledynamictexture-lifetime-remove.md)、[动态纹理被重载关掉](forge-renderer-dynamictexture-reload.md)、[sendParticles 速度语义](forge-sendparticles-velocity-semantics.md)、[模型局部→世界](forge-model-space-to-world.md)、[Oculus 偷换顶点格式](oculus-extends-vanilla-vertexformat-singletons.md) — count=0 才是确定性速度
- 外观/材质：[硬编码模型改贴图](mc-hardcoded-entitymodel-retexture.md)、[换皮复用渲染器](variant-servant-renderer-reuse.md)、[精灵球→秘仪材质包](pokeball-tesseract-pack.md)、[材质包回退模组贴图](mc-resourcepack-texture-revert.md)、[atlas 合并/物品贴图来源](mc-atlas-merge-and-item-texture-source.md) — mob_effect 贴图要补 single source
- 动画/姿势：[姿势过渡](forge-pose-transition-from-pose.md)、[混合权重四条](forge-pose-blend-weight-invariants.md)、[Warg/Cerberus 定标](warg-anim-rotation-calibration.md)、[突变疣猪兽](mutant-hoglin-anim-fix.md)、[巨型仆从走路震荡](ac-servant-walk-shake-camerashake.md)、[吐息音效分工](tremorzilla-breath-sound-ownership.md)、[红石巨兽撞树](goety-monstrosity-foliage-smash.md)、[animateWalk 必须 looping](mc-animatewalk-requires-looping.md) — scale=真权重
- 喂食：[用 HEART](goetyominous-feed-particle-heart.md)、[用头顶坐标](forge-heal-particle-above-bbox.md) — 用 getBbHeight()+0.3F

## 手册与资源包
- Patchouli：[轮转图标顺序](ominous-icon-rotation-order.md)、[排序先比阅读状态](patchouli-entry-sort-read-state-first.md)、[图标可带 NBT](patchouli-icon-and-ritual-ingredient-nbt.md)、[flag 条件化](patchouli-flag-conditional-content.md)、[mm 7 条被 secret 藏](mm-compat-secret-gated-entries.md)、[类目 locked 是推导的](patchouli-category-lock-is-derived.md)、[id 只由 en_us 定](patchouli-locale-fallback-en-us.md) — 类别加 flag 时条目必须同 flag

## 工具链与环境
- 构建/反编译：[编译配置](forge-compile-setup.md)、[SRG→mojmap](srg-to-mojmap-tsrg-lookup.md)、[原版客户端源码](vanilla-client-jar-lookup.md)、[Goety jar 以谁为准](goety-compile-jar-source-of-truth.md) — build 别加 --offline;GBK 要 iconv
- 产物/仓库：[同 modid 两份 jar](forge-duplicate-modid-jar-silent-pick.md)、[远端历史 392MB jar](github-history-libs-jars-kept.md)、[脚本别 rmSync 共享目录](resource-gen-script-no-rmdir.md) — FML 按版本号去重
- Windows/网络：[Java 在哪](java-toolchain-location.md)、[PowerShell 毁中文](powershell-cli-mangles-nonascii-args.md)、[Git Bash 污染 cmd](gitbash-path-poisons-cmd.md)、[grep 崩溃伪装空结果](gitbash-grep-crash-false-negative.md)、[GitHub SSH](github-ssh-setup.md)、[Avast 炸 PKIX](java-ssl-avast-mitm.md)、[卸载 Avast+Xbox](windows-debloat-avast-xbox.md)、[管理员令牌/保护路径](windows-admin-token-and-protected-paths.md)、[清 %TEMP% 两坑](windows-temp-cleanup-gotchas.md) — 空输出≠没匹配到
- IDE：[IntelliJ 红字](intellij-red-font-troubleshooting.md)、[打补丁失败根因](intellij-install-and-update-logs.md)、[按时间点回退](intellij-local-history-rollback.md) — 真历史在 LocalHistory
