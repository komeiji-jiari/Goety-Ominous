# Memory Index

## 项目与约定
- [多 jar 拆分](goetyominous-split-into-modules.md) — 判据=核心文件直接 import 第三方包；配置已拆 acam/uasr/lm/mm/of 5 组
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
- 其他仆从件：[飞行 isStableDestination](forge-goety-flyer-isStableDestination.md)、[飞行游荡死区](forge-flying-stroll-deadzone.md)、[训练方块当造兵方块](goety-training-block-base.md)、[FlyingItem 返还](goety-flyingitem-return-semantics.md)、[复用模型私有字段](goety-model-reuse-private-fields.md)
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
