---
name: goety2-base-source
description: 移植参考基座是 libs/goety-2.5.56.5.jar（deobf，用 javap 反汇编），不是仓库里的 Goety-3-main/Goety-master 源码树
metadata: 
  node_type: memory
  type: project
  originSessionId: 93a56fd4-fbcd-4852-b0b1-41f93ca95397
---

移植 Goety 内容时，**唯一正确的基座**是 `libs/goety-2.5.56.5.jar`（`build.gradle` 里 `curse.maven:goety-586095:8736784`）。这个 jar 是 deobf 过的：类名可读、成员名是 SRG（`m_xxxxx_` / `f_xxxxx_`），可以直接 `unzip` + `javap -p -c` 读实现。

**仓库根目录下的这三个源码树都不是这个基座，不要拿来当实现参考：**
- `Goety-3-main`（mod_version=3.1.0）、`Goety-3-main_new`（3.1.1）— NeoForge 版，`net.neoforged` 包名
- `Goety-master`（1.37.0）— 1.16 时代老版，类名和包结构都不一样

**Why:** Goety 3 是 NeoForge + 新 MC，API 有实质差异。踩过的坑：Goety 3 的 `JungleZombieServant` 用 `Holder<MobEffect>` 和 `MobEffectInstance(Holder, int)`，而 1.20.1 的 Goety 2 / 原版里 `MobEffects.POISON` 就是 `MobEffect`。照抄 Goety 3 源码会编译不过。

**How to apply:** 要读某个 Goety 类的真实实现：

```bash
cd /tmp && mkdir -p gj && cd gj
unzip -o -q /e/java/libs/goety-2.5.56.5.jar "com/Polarice3/Goety/想要/的/路径/*.class"
javap -p com/Polarice3/Goety/想要/的/路径/类名.class          # 看成员签名
javap -p -c com/Polarice3/Goety/想要/的/路径/类名.class | head -60   # 看实现
```

SRG→mojmap 对照：`~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client_mappings.txt` 是 mojmap→obf，`mcp_mappings.tsrg` 是 obf→SRG，两段接起来就能反查。

`libs/temp_decompile` 和 `tmp_goety_decompile` 是以前零散抽出来的 .class，不是完整源码树。

相关：[[dreden-port-plan]]
