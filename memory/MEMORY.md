# Memory Index

## ⚠️ 本机最高优先级
- [No edit without consent](no-edit-without-consent.md) — 2026-10-29 起已授予长期编辑权限（旧"必须说 edit"规则作废）；仍只改被要求的部分、不擅自新建/提交
- [代码不加注释](no-comments-in-code.md) — 写进工程的代码一律裸代码（无 javadoc/行内注释），"为什么"写在对话或记忆里；已有注释不动
- [⚠️Windy 移植 1:1 铁律](windy-port-1to1-principle.md) — 最高优先级：必须逐签名逐方法复刻 1.21（含常量/数值），不许简化或砍功能；缺的原版件用反编译器读源码后等价实现
- [⚠️移植数据不得捏造](port-data-fidelity-rule.md) — 注册数据(尺寸/eyeHeight/分类/数值)必须与 1.21/Goety-3 逐字段一致，先查再写；1.20.1 缺 API 用等价手段复现同数值（实例：sized 0.3125 + getEyeHeight 覆写）
- [⚠️API 不得猜想](api-must-be-empirically-verified.md) — ⚠️最高优先级：任何方法/字段/SRG 常量/mixin target/codec/行为语义必须查映射、javap、反编译源码、refmap 或实测，禁止按名字猜；实例 BooleanOp.f_82689_=AND 不是 ONLY_FIRST

## 本机补充笔记（E:\java）
- [MutantBlaze 仆从移植进度](mutant-blaze-servant-port-progress.md) — ShieldPart/Firebal
- [Dreden 移植计划](dreden-port-plan.md) — Goety1 Dreden →
- [Axis 的 SRG 映射](srg-axis-mapping.md) — 反编译 goety 还原
- [Goety2 基座源码](goety2-base-source.md) — 移植参考基座是 libs/goety
- [Goety2 反编译源码位置](goety2-src-decompile.md) — Goety 2.5.58.2
- [Goety2 仆从基座](goety2-servant-system.md) — Summoned/Owned/ISe
- [Goety2 法术基座](goety2-spell-system.md) — ISpell/Spell/Summo
- [联动的两层 compat 结构](goetyominous-compat-architecture.md) — goety+curios
- [软前置反射三条铁律](goetyominous-soft-dep-reflection.md) — 能放门槛后就别反射；mixin
- [Stash 核对结论 2026-09-26](stash-audit-2026-09-26.md) — 6 个 stash
- [错题本·编译与构建](cuotiben-build-and-merge.md) — 合并后必须全量编译：两边都加同名符号
- [错题本·Goety 仆从框架](cuotiben-goety-servant-framework.md) — 注册表隔离、goal
- [错题本·LM 移植](cuotiben-lm-port.md) — isAlliedTo/UpdateW
- [错题本·工具与流程](cuotiben-tooling.md) — 别信工具显示（Grep 的
- [Abyss Staff 法术加强计划](abyss-staff-enhancement-plan.md) — key_of_rlyeh 要强化的
- [Abyss 加强·实际类名核实](abyss-enhancement-actual-names.md) — 14 个法术的真实类名/包（Rott
- [拉莱耶 tooltip 移植](key-of-rlyeh-tooltip-port.md) — 朋友示范包移植进本
- [ModernUI 文字管线逆向](modernui-text-pipeline-findings.md) — 共存失败已撤待朋友方案；三条
- [Windy Update 移植计划](windy-update-port-plan.md) — Goety-3 `2f424351`
- [1.21.1 素材提取法](mc-1211-asset-extraction.md) — 本体 jar 无 ogg
- [凶兆/队长掉落实证](trial-omen-captain-drop-findings.md) — 1.21.1 掉瓶=仅掠夺者战利品表
- [1.20.1 原版数据源](mc-1201-vanilla-data-source.md) — 权威 data（战利品表/标签）在
- [宝库移植进度](vault-port-progress.md) — 宝库+不祥宝库已全部落地并编译通过（
- [试炼刷怪笼移植计划](trial-spawner-port-plan.md) — 第四批**已全部落地并编译通过**（
- [试炼密室移植计划](trial-chamber-port-plan.md) — 2026-10-09
- [密室结构 NBT 引用清单](trial-chamber-structure-nbt-refs.md) — 移植密室结构时必须把
- [刷怪笼加怪脚本](trial-spawner-mob-tool.md) — `tools/add_trial_s
- [试炼战利品表](trial-loot-tables.md) — 宝库/刷怪笼战利品的链路与热更边界（
- [新增文件必须 git add](git-add-new-files.md) — 我新建/搬运的 json、贴图、模型

## 徒弟错题本（仓库 memory/ 同步于 2026-10-23）

## 项目与约定
- [多 jar 拆分](goetyominous-split-into-modules.md) — 判据=核心文件直接 import
- [远征系统方案](expedition-system-plan.md) — 核心化旅行家远征/指南针：核心/PP
- [ModMobType](modmobtype-name-collision.md)、[AnnihilationBomb](annihilation-bomb-ac-lm-name-collision.md) — 常量不能跨 jar 复制
- 工作偏好：[不写注释](no-code-comments.md)、[别擅自写配方手册](no-unrequested-recipes-patchouli.md)、[模组名 4 处显示面](goetyominous-name-surfaces-ask-scope.md)、[别自动 build jar](no-auto-build-jar.md) — 文案要就问;用户说构建才构建
- 死代码：[清理偏好](goetyominous-deadcode-cleanup-preference.md)、[脚本](goetyominous-deadcode-analysis-scripts.md) — .scratch/_deadcode
- 配置归属：[该进哪个文件](config-file-split-rule.md)、[LM 伤害](lm-servant-damage-config.md)、[敌对/仆从共用](goety-hostile-servant-shared-config.md)、[无引用键不是死键](mobsconfig-unwired-keys-are-not-dead.md)、[TOML 排序](forge-config-toml-order-historical.md)、[Goety 式命名/分段](config-goety-style-naming.md) — 删前先问；键序是历史插入序
- 落点/收尾：[modid 子包隔离](goetyominous-mod-folder-isolation.md)、[刷怪蛋要补 model](servant-spawn-egg-item-model.md)、[实测实例/日志](goetyominous-test-instance-and-logs.md) — 漏 model 会紫黑块
- 可选依赖注册：[必须放 CompatManager](optional-mod-registration-manager-gated.md) — core 无条件注册
- 移植准则：[表现原版/实现 Goety](ac-servant-parity-keep-original.md)、[飓风分叉是有意的](hurricane-breeze-intentional-divergence.md)、[移植漏单行](ac-goety-port-probability-gates.md) — 冲突时 Goety 优先
- MM 手臂道具：[已按用户令删除](mm-mutant-jungle-zombie-arm-port.md) — 别再主动重提
- 已按用户令删除：[仆从+敌对移速/跟随范围/击退抗性/攻击击退配置](servant-speed-follow-config-removed.md)、[灵魂碎裂开关](soul-fracture-servant-option-removed.md) — 属性硬编码原默认值;别再提重加
- dist 审计：[现状+已修/仍脆弱](client-dist-audit-fragile-spots.md) — HUD 已移 client
- [错题本同步到仓库](memory-notebook-sync-to-repo.md) — memory 整份复制到仓库

## Goety 仆从骨架
- 状态机：[跟随/待命](goety-servant-follow-standby.md)、[空闲冻结](goety-summon-idle-freeze.md)、[能力位vs状态位](goety-iservant-capability-vs-state.md) — 短路 checkDespawn→用
- 目标/flag：[命令模式冲突](goety-command-mode-conflict.md)、[优先级/flag 压制](goety-servant-goal-priority-flags.md)、[无 flag 抢朝向](goety-attackgoal-no-flags-yaw-fight.md)、[canContinueToUse 默认](vanilla-goal-cancontinue-default.md) — 自定义 goal 须 gate
- 解散/上限：[解散二次确认](goety-servant-trykill-confirm.md)、[上限两条通道](goety-servant-limit-two-channels.md)、[canAttackType=false 废索敌](goety-servant-canattacktype-kills-targeting.md)、[多命 vs 解散/寿命](goety-multilife-servant-dismiss-conflict.md) — 覆写 tryKill;多命复活会吃掉
- 召唤法术：[约定](goety-summon-spell-conventions.md)、[王冠/长袍两通道](goety-crown-vs-robe-two-channels.md)、[寿命锚点](summon-spell-lifespan-anchor-pattern.md)、[owner 顺序](goety-summonspawn-owner-order.md)、[属性偏移存 NBT](goety-configurable-attrs-reset-on-load.md) — 先 setTrueOwner 再
- 蛋/移速：[蛋要潜行才有主人](goety-egg-owner-requires-crouch.md)、[移速多个通道](goety-move-speed-two-channels.md)、[AC 冲量式 MoveControl](ac-impulse-movecontrol-speed.md)、[持久冷却用 SEHelper](goety-persistent-item-cooldown.md) — 别改 MovementSpeed
- 骑乘：[条件链](goety-servant-riding-mechanism.md)、[positionRider 两个重载](forge-positionrider-two-overloads.md)、[下马摔伤](mount-dismount-fall-damage.md)、[水中速度/姿势](forge-ridden-mob-water-speed.md)、[服务端 delta 清零](forge-travelridden-server-zero-delta.md)、[键控转向符号](mount-key-steering-xxa-sign.md) — 水里 getRiddenSpeed
- 骑乘者接管：[不操控时 AI 接管](goety-mount-idle-ai-authority.md)、[isImmobile 定身](goety-mount-immobile-anim-root.md)、[AC 双重渲染](ac-rider-layer-double-render.md)、[Mob 骑 Mob 关载具 AI](forge-mob-rider-disables-vehicle-ai.md) — 按 zza/xxa
- 多部件：[肢体 extends Summoned 三坑](goety-multipart-part-as-servant.md)、[肢体被打崩](goety-multipart-part-projectile-crash.md)、[部件晚一拍](sauropod-part-oldpos-teleport-neck.md)、[yBodyRot 客户端算](sauropod-client-ybodyrot-local.md)、[移动时 yBodyRot 硬贴](mob-tickheadturn-bodyrot-snap.md)、[骑乘扭头](goety-sauropod-ridden-neck-lag.md)、[蜈蚣体节抖动](goety-centipede-ringbuffer-freeze-seam.md) — 委派 isCommanded
- 水陆两栖：[导航 goal 规则](goety-water-navigation-goals.md)、[水面弹跳](goety-amphibious-floatgoal-bounce.md)、[泳控对照](vanilla-swim-movecontrol-xxa-reference.md) — 水下自灭用 AirAndWaterR
- 潮涌之叶：[上岸/下水/跳劈](goety-leapkelp-land-water-leap.md)、[泳姿来自珊瑚傀儡](leapkelp-swim-anim-coralssus-port.md)、[水中重砸重排(有意偏离)](leapkelp-water-smash-resmooth.md)、[珊瑚傀儡≠珊瑚巨像](cataclysm-coral-golem-vs-coralssus.md)、[游荡 AI 平滑改造四条根因](leapkelp-wander-ai-tuning.md) — 离水关 wantsToSwim;别再主动重做游荡/上浮/转向
- Leapleaf：[水陆门控](goety-leapleaf-leap-goal-gating.md)、[静止姿势](goety-leapleaf-model-geometry.md)、[restTick 冻结 AI](goety-leapleaf-rest-immobile-freeze.md) — 无 onGround 检查→浅水会跳
- 灵魂鹫：[仆从移植](soul-vulture-servant-port.md) — 栖息三态
- 索敌：[搜索盒 ±4 格](goety-target-search-area-vertical-4-blocks.md) — 高空飞仆锁不上地面怪
- 长尾鲨：[两段式索敌](thrasher-servant-two-stage-targeting.md)、[玩家骑乘](thrasher-servant-player-riding.md) — 声呐目标必须 isInWater
- 移植审计：[OF 三易漏落点](of-servant-port-checklist.md)、[蚀牙兽还原度审计](corrodent-servant-fidelity-audit.md)、[试炼四效果定案](trial-effect-port-1.21.1.md)、[Summoned 已含亡灵点燃](goety-summoned-dohurttarget-duplicate.md)、[Frowzy 有意偏离 OF](frowzy-servant-deliberate-divergences.md) — finalizeSpawn 里的
- 其他仆从件：[自建仆从要 ATTACK_DAMAGE](goety-servant-missing-attack-damage.md) — Aether 减伤会查 source 属性;Mob.createMobAttributes 不含、[飞行 isStableDestination](forge-goety-flyer-isStableDestination.md)、[飞行游荡死区](forge-flying-stroll-deadzone.md)、[训练方块当造兵方块](goety-training-block-base.md)、[FlyingItem 返还](goety-flyingitem-return-semantics.md)、[复用模型私有字段](goety-model-reuse-private-fields.md)
- 陷阱蓝线：[来历与剥离](mm-shulker-trap-texture-blue-outline.md) — 贴图多描的 2px 浅蓝边;改
- 仆从贴图自家化：[共用渲染器一起换](mm-servant-textures-own-copies.md) — 霜冻/尸壳换自家皮;雪球冰锥沙球同源
- 潜影贝染色：[现状](mm-mutant-shulker-dyeing.md) — 本体/陷阱可染,两种导弹明确不染;小
- 转化物品：[Goety 模板三条](goety-revive-item-parity-template.md) — extends Item
- 死亡返还：[飞回复活物品](mm-zombie-servant-death-drop-hook.md) — MM 返还物已删;复活必重置
- 潜影贝浮空：[四处注入点](mm-shulker-levitation-injection-points.md) — 本体两分支/陷阱/法术;已浮空不重刷
- 导弹弹道：[服务端制导+客户端缓动](mm-shulker-bullet-trajectory.md) — 客户端也跑制导会抖;updateIn
- 动画门控：[priorityActive 镜像 + 走路混合出帧](mm-mutant-shulker-anim-gate.md) — resetAll()
- 飞行已删：[仆从不再飞行](mm-shulker-flight-removed.md) — FlyGoal/事件
- 丛林僵尸：[移植定案](mm-jungle-zombie-servant-port-decisions.md) — 多命保留/跳劈去掉/手臂道具做过又删
- 三僵尸动画：[相位突跳+战斗中立正+走站衔接](mm-zombie-sinewave-walk-jitter-fix.md)、[hurt vs die 顺序](mm-hurt-inline-event-die-order.md)、[凋零骷髅立正移动](mm-advanced-animation-client-sync.md) — 跨帧状态放实体
- 正弦脚步：[相位定标](mm-sine-walk-phase-footstep-lock.md) — 每格 8.64;距离只能用
- 宽生物走路晃：[路点角偏移](mm-wide-mob-waypoint-offset-yaw-sway.md) — 疣猪兽 (int)(bbWidth+
- 凋零骷髅走姿：[walk_progress 随帧率](mm-walkmodifiers-framerate-phase.md) — getFrameTime()
- 倒地转向：[倒地冻结朝向](mm-zombie-servant-down-yaw-freeze.md) — downYaw 在
- 假死红：[overlay 不算 deathTime](mm-servant-death-red-overlay.md) — 客户端 tick 复活清零
- 仆从不改地形：[雪删/流沙加回](mm-servants-no-terrain-placement.md) — 尸壳 4 处流沙已恢复
- 突变仆从 AoE：[友军/主人守卫](mm-servant-aoe-ally-guards.md) — canHarm 不排除主人;击退要单
- 突变尸壳：[husk 定案](mm-mutant-husk-servant-port-notes.md) — 不做小海龟/HuskHammer、[地涌扑空锁 AI](mm-husk-geyser-failed-leap-lock.md) — 已加出口，有意偏离上游
- 三僵尸右键：[穿装备+腐肉喂食](mm-zombie-servant-equip-interaction.md) — setGuaranteedDrop
- 移植陷阱：[CFR 全限定名残留](mm-port-cfr-fqn-residue.md) — 旧包名残留不报错但要改
- 方块/容器/仪式：[tesseract 容量 tag](goety-tesseract-capacity-tags.md)、[方匣取出强制跟随](goety-tesseract-eject-forces-follow.md)、[仪式每cast防 stale](goety-ritual-percast-state-stale.md)、[Mold 写死实体](goety-mold-hardcoded-mixin.md) — 占4格 medium/占2格
- 标签：[powerful_servants 纯成员标签](goety-powerful-servants-tag.md)、[联动配方在自己 data/ 下](compat-pack-recipes-in-own-datapack.md) — 漏 required:false
- 弹射物：[换 Goety 基类](goety-projectile-base-choice.md)、[幽灵弹三要素](projectile-no-despawn-three-conditions.md)、[风弹击退=同 tick ADD](wind-charge-knockback-delivery.md)、[友伤免疫吞药箭效果](goety-friendly-fire-suppresses-tipped-arrow-effects.md)、[亲善名单只有 SEHelper 认](goety-goodwill-ally-check-api.md) — 覆写 tick 用
- 波/粒子：[冲击波尺寸换算](goety-shockwave-particle-geometry.md)、[AbstractWave 伤害标定](goety-abstractwave-attackentities-scale.md) — size 是半径,实参序
- 敌对突变怪动画：[渲染器覆盖式修复](mm-hostile-mob-animation-fix.md) — 无 mixin;后注册覆盖 +
- 盟友判定：[areAllies(仆从,原版宠物)=false](goety-areallies-servant-vs-pet.md) — 双向 isAlliedTo 缺宠物→仆从方向;按双方主人比对

## Alex's Caves 联动
- 移植要点：[要点](ac-compat-porting-notes.md)、[不许 mixin](goety-ac-no-mixin.md)、[继承而非重写](goety-ac-dinosaur-spirit-port.md)、[坐骑能力键](goety-ac-servant-keydown-channels.md)、[坐骑跳跃权威](goety-ac-servant-mount-jump-authority.md) — 蛋只认 HATCH+NEEDS_PL
- 机制/伤害：[AoE 友军判定反了](ac-ao-ally-check-direction.md)、[TephraExplosion 三坑](ac-tephra-explosion-quirks.md)、[核爆免疫两半](ac-nuke-immunity-two-halves.md)、[辐照友军免疫](ac-radiation-ally-immunity.md) — 须翻成 this.isAlliedT
- 杂项：[震动 handler 重复](ac-screen-shake-duplicate-handler.md)、[合成 Post 清零头部](ac-synthetic-renderpost-head-zeroing.md)、[甘蔗钩(已回退)](ac-candy-cane-hook-port.md)、[沙虫垂直力](ac-gumworm-vertical-force-split.md)、[螺纹长枪无冷却](ac-ortholance-no-cooldown.md)

## Alex's Mobs 联动
- 老虎仆从：[移植决定](tiger-servant-port.md) — AnimalSummon
- 蚊鬼不洁血：[强化跨死亡传递](warped-mosco-unholy-blood-inheritance.md) — 应用在 finalizeSpawn
- 抹香鲸抓乌贼：[纯 API 复刻](am-cachalot-whale-squid-capture-api.md) — 大王乌贼仆从已删除,别再主动重提

## LM 联动
- 版本/层级：[机制要看 jar 版本](lm-mechanic-presence-version-check.md)、[切除 boss 机制](lm-servant-boss-mechanic-stripping.md)、[选层看真父类](lm-servant-tier-from-class-superclass.md)、[nextInt()*100 门控](lm-nextint-overflow-random-gate.md) — 常量池 SRG+mojmap
- 待命/AI：[待命沉睡时序](lm-servant-standby-sleep-timing.md)、[ModPathNavigation 空壳](lm-modpathnavigation-shortcut-stub.md)、[共享 goal 未覆写 tick 频率](lm-shared-goals-tick-rate-deviation.md) — 趴下用 reverse(awake/
- 弹射物/血量：[弹射物模型借不了](lm-projectile-model-borrow-impossible.md)、[圣骑盟友判定是好的](lm-projectile-ally-check-parity-quirk.md)、[IAnimatedBoss 虚拟血量](lm-ianimatedboss-virtual-health.md)、[The Obliterator 决定](the-obliterator-servant-port-plan.md)、["红"从哪来](lm-red-projectile-sources.md)、[金钻皮肤](lm-knight-skin-variants.md) — 红染料红化已删；T extends
- BK/RS：[脑袋挂 BK 的可行性](bk-rs-head-attach-feasibility.md)、[两套乘客机制](bk-rs-two-passenger-mechanics.md)、[侧位跟随(已删)](bk-rs-follow-slot-formation.md) — 骨架同源三条坑
- 屏幕震动：[LM/MM 各有仓库自有开关](lm-mm-screen-shake-upstream-configs.md)

## 原版/Forge 机制
- 属性/碰撞：[上台阶](forge-maxupstep-mapping.md)、[近战射程](forge-meleeattackgoal-reach.md)、[.sized vs getDimensions](forge-entity-sized-vs-getdimensions.md)、[1.20.1 溺水链](forge-1.20.1-air-supply-drown-chain.md)、[1.20.1 无肉 tag](forge-1.20.1-no-meat-tag.md)、[eat() 双扣](forge-livingentity-eat-double-consume.md) — 溺水在 ForgeHooks.onL
- 私有原版方法：[collide / rotlerp 私有 / 没有 getGravity](mc-1201-entity-collide-private.md) — 要 AT 写 SRG 名
- 注入/mixin：[@Redirect 冲突改 RETURN 包装](mixin-redirect-conflict-return-wrap.md) — 被跳过的 redirect +、[HEAD cancel 吞掉别家护甲穿透](mixin-head-cancel-armor-absorb-breaks-pierce.md) — getDamageAfterArmorAbsorb 整段重写会废掉 ApothicAttributes/RevelationFix
- tick/导航顺序：[baseTick 绕过](forge-bypass-intermediate-tick-baseTick.md)、[MoveControl 晚于 goal](forge-movecontrol-runs-after-goals.md)、[goalSelector 在 aiStep 内](forge-goalselector-in-aistep-ordering.md)、[LookControl 回正](vanilla-lookcontrol-head-forward.md)、[followThePath ULP](forge-followthepath-bbwidth-ulp-freeze.md)、[近战别再喂导航](mc-moveto-every-tick-yaw-whipsaw.md)、[Citadel moveTo 恒 true](citadel-advancedpathnavigate-moveto-always-true.md)、[限转向=绕圈](mob-turn-cap-min-radius-orbit.md) — 顺序 goal→nav→moveCo
- 事件/注册：[RightClickBlock getLevel](forge-rightclickblock-getlevel-pattern.md)、[re-add 发 JoinLevelEvent](forge-entityjoinlevelevent-on-readd.md)、[DeferredRegister 惰性](forge-deferredregister-clinit-lazy-trap.md)、[页签定点插入](forge-creative-tab-insert-position.md)、[dungeonsdelight 并发崩](dungeonsdelight-fastutil-dispenser-race.md)、[1.21 爆炸 null source](goety-1.21-explosion-null-damagesource.md)、[MobEffect 无死亡钩子](forge-1.20.1-mobeffect-no-death-hook.md) — putAfter 顺序调用会反序
- 寻路/同步：[NodeEvaluator 按格钩子](forge-nodeevaluator-per-cell-hooks.md)、[裸 Entity 位置包频率](forge-bare-entity-update-interval-smoothness.md)、[插值同步顺序](mc-1201-client-interp-sync-order.md)、[客户端 getDeltaMovement](mc-client-deltamovement-motion-packets.md) — 按格判定须覆写 getCachedB
- 死亡/掉落：[捕获窗口](forge-death-loot-capturedrops-window.md)、[deathTime 遮蔽倒地旋转](forge-deathtime-shadow-suppresses-death-roll.md) — LM 的 deathTime
- 双格掉落：[提前拆另一半吞掉落](double-block-early-removal-eats-drop.md) — setBlock(AIR)
- 方块/标签：[blockstate 重叠键](forge-blockstate-overlapping-keys.md)、[hot_floor 属 is_fire](forge-hot-floor-fireimmune.md)、[氧化方块逐档](mc-weathering-block-per-tier-props.md)、[requiresCorrectTool 要 mineable tag](forge-requirescorrecttool-needs-mineable-tag.md)、[自定义墙要 walls tag](forge-wall-block-needs-walls-tag.md)、[biomes 指向不存在的 tag](forge-1.20.1-missing-biome-tag-no-structure.md) — 群系 tag 缺失→结构永不生成
- 战利品表：[附魔改名炸表](mc-121-1201-enchant-rename-breaks-loot-table.md) — 一个非法 id 即整张报废
- 渲染/粒子：[未绑定 spriteSet](forge-particle-unbound-spriteset.md)、[寿命末 remove NPE](forge-particledynamictexture-lifetime-remove.md)、[动态纹理被重载关掉](forge-renderer-dynamictexture-reload.md)、[sendParticles 速度语义](forge-sendparticles-velocity-semantics.md)、[模型局部→世界](forge-model-space-to-world.md)、[Oculus 偷换顶点格式](oculus-extends-vanilla-vertexformat-singletons.md) — count=0 才是确定性速度
- 外观/材质：[硬编码模型改贴图](mc-hardcoded-entitymodel-retexture.md)、[换皮复用渲染器](variant-servant-renderer-reuse.md)、[精灵球→秘仪材质包](pokeball-tesseract-pack.md)、[材质包回退模组贴图](mc-resourcepack-texture-revert.md)、[atlas 合并/物品贴图来源](mc-atlas-merge-and-item-texture-source.md) — mob_effect 贴图要补
- 动画/姿势：[姿势过渡](forge-pose-transition-from-pose.md)、[混合权重四条](forge-pose-blend-weight-invariants.md)、[Warg/Cerberus 定标](warg-anim-rotation-calibration.md)、[突变疣猪兽](mutant-hoglin-anim-fix.md)、[巨型仆从走路震荡](ac-servant-walk-shake-camerashake.md)、[吐息音效分工](tremorzilla-breath-sound-ownership.md)、[红石巨兽撞树](goety-monstrosity-foliage-smash.md)、[animateWalk 必须 looping](mc-animatewalk-requires-looping.md) — scale=真权重
- 喂食：[用 HEART](goetyominous-feed-particle-heart.md)、[用头顶坐标](forge-heal-particle-above-bbox.md) — 用 getBbHeight()+0.

## 手册与资源包
- Patchouli：[轮转图标顺序](ominous-icon-rotation-order.md)、[排序先比阅读状态](patchouli-entry-sort-read-state-first.md)、[图标可带 NBT](patchouli-icon-and-ritual-ingredient-nbt.md)、[flag 条件化](patchouli-flag-conditional-content.md)、[mm 7 条被 secret 藏](mm-compat-secret-gated-entries.md)、[类目 locked 是推导的](patchouli-category-lock-is-derived.md)、[id 只由 en_us 定](patchouli-locale-fallback-en-us.md) — 类别加 flag 时条目必须同

## 工具链与环境
- 构建/反编译：[编译配置](forge-compile-setup.md)、[SRG→mojmap](srg-to-mojmap-tsrg-lookup.md)、[原版客户端源码](vanilla-client-jar-lookup.md)、[Goety jar 以谁为准](goety-compile-jar-source-of-truth.md) — build 别加
- 产物/仓库：[同 modid 两份 jar](forge-duplicate-modid-jar-silent-pick.md)、[远端历史 392MB jar](github-history-libs-jars-kept.md)、[脚本别 rmSync 共享目录](resource-gen-script-no-rmdir.md) — FML 按版本号去重
- Windows/网络：[Java 在哪](java-toolchain-location.md)、[PowerShell 毁中文](powershell-cli-mangles-nonascii-args.md)、[Git Bash 污染 cmd](gitbash-path-poisons-cmd.md)、[grep 崩溃伪装空结果](gitbash-grep-crash-false-negative.md)、[GitHub SSH](github-ssh-setup.md)、[Avast 炸 PKIX](java-ssl-avast-mitm.md)、[卸载 Avast+Xbox](windows-debloat-avast-xbox.md)、[管理员令牌/保护路径](windows-admin-token-and-protected-paths.md)、[清 %TEMP% 两坑](windows-temp-cleanup-gotchas.md) — 空输出≠没匹配到
- IDE：[IntelliJ 红字](intellij-red-font-troubleshooting.md)、[打补丁失败根因](intellij-install-and-update-logs.md)、[按时间点回退](intellij-local-history-rollback.md) — 真历史在 LocalHistory
