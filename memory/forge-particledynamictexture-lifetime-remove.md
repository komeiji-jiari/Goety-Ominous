---
name: forge-particledynamictexture-lifetime-remove
description: "覆写 remove() 关闭 DynamicTexture 的粒子,寿命末 tick 在 super.tick() 后继续触碰已关纹理 → NPE;注册 RL 复用计数坑"
metadata: 
  node_type: memory
  type: reference
  originSessionId: b3cd7a2a-17a1-4341-89b3-be28ff5b68e0
  modified: 2026-09-08T13:07:27.736Z
---

自定义粒子持有 `DynamicTexture`(构造时 register 进 textureManager),并在覆写 `remove()` 里 `close()` 时:

- 香草 `Particle.tick()`(super.tick)在 `if (this.age++ >= this.lifetime) this.remove();` 里触发 remove,而**不是**由引擎在 tick 之后再删。
- 因此粒子覆写 tick() 里 `super.tick();` 之后的代码,会在寿命末最后一 tick 拿到 `removed==true` 且纹理已 `close()` → `DynamicTexture.getPixels()` 返回 null → `updateTexture()` 处 NPE。
- 崩溃报告会显示粒子 Age == lifetime+1(本案例 Age 381 / LIFETIME 380),是**必然崩溃**不是偶发。

修法:RitualVoidCloudParticle.tick()
```java
super.tick();
if (this.removed) return; // remove() 已关闭 DynamicTexture
```
外加 updateTexture() 开头 `NativeImage px = dynamicTexture.getPixels(); if (px==null) return;` 兜底 render 路径。

另坑:别用"当前存活数"做注册 ResourceLocation 的 id 再在 remove() 里递减——两个同时存活的粒子会拿到同一 RL,一个 remove() 释放贴图会毁掉另一个正在绑定的纹理(灰黑/花屏)。id 应单调递增(`nextCloudTexture++`,永不递减)。相关:[[forge-particle-unbound-spriteset]]
