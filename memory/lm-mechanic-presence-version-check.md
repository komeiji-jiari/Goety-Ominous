---
name: lm-mechanic-presence-version-check
description: "判断 LM 机制在装的版本里是否实装——全 jar 常量池双命名风格 grep;2.2.2 的\"方块破坏\"是空壳"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6bc59e20-9141-4318-9ae1-34104b26313d
  modified: 2026-09-24T12:29:04.613Z
---

用户拿 wiki/群里的机制表问"XX 机制是不是被移除了"时，别只读反编译源码——先确认**装的 jar 版本**是否包含该机制。实测实例里只有 `[传奇怪物] legendarymonsters-2.2.2 MC 1.20.1.jar`，而 wiki 描述的"云傀儡受击 3 次破坏周围 y 以上方块（2.1.20 后新增）"在 2.2.2 里**根本不存在**。

**判据（对 `mods/*.jar` 直接跑，不装 CFR）：**

```bash
J="./beta/.minecraft/versions/1.20.1-Forge 重启2/mods/<lm>.jar"
unzip -p "$J" "*.class" | grep -a -c "<名字>"    # 0 = 全 jar 无引用
```

SRG 与 mojmap 两套都要试：`m_46961_`(destroyBlock)、`m_46953_`/`m_7471_`、`m_7740_`、`m_46796_`、`m_7731_`、`m_46680_`、explode 系 `m_254849_`/`m_255391_`/`m_254877_`/`m_255278_`/`m_254951_`，以及字面量 `destroyBlock/removeBlock/setBlock/dropResources/explode`。

**Why:** CFR 反编译出 `if (a || b || c) { // empty if block }` 时无法区分「作者留的空壳」与「反编译失败」；必须回到原始 class 常量池验证，否则会把原版自带的空壳误报成"移植丢了一整个机制"。

**How to apply:** `Cloud_GolemEntity.class` 里唯一方块相关引用是 `m_8055_`(getBlockState)/`m_49966_`(defaultBlockState)，用于 Sphereparticle 取方块粒子。同类需注意：`setSleep(true)` 只在 `readAdditionalSaveData` 被调用（state 1=睡/2=苏醒过渡/0=醒），"有目标解除休眠"是靠 `IAttackGoal(this, 1, 2, 0, 20, 50, 8.0F)` 的 canUse 带 `getTarget()!=null && 距离<8` 驱动的，不写在 tick 里。

相关：[[ac-servant-parity-keep-original]]、[[lm-miniboss-servant-spawn-teleport]]、[[goety-servant-canattacktype-kills-targeting]]
