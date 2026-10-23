---
name: forge-renderer-dynamictexture-reload
description: "实体渲染器单例持 DynamicTexture 并 register 进 TextureManager,F3+T/进存档重载会把它 close 掉(NativeImage+GL id 全释放,getPixels() 归 null 且不会自动重建)"
metadata: 
  node_type: memory
  type: reference
  originSessionId: a2c6bb8f-0405-48c8-9629-48e92d905565
  modified: 2026-09-08T15:48:28.868Z
---

实体渲染器(EntityRenderer)是**单例**,生命周期跨世界/跨资源重载。若它在构造时 `new DynamicTexture(...)` 并 `textureManager.register(RL, tex)`,一旦 TextureManager 重载(F3+T、进存档/世界切换等)该已注册动态纹理会被 `close()`:NativeImage 释放 + GL id 删除,`getPixels()` 归 **null**,此后**不会自动重建** → 云/图永久失效或花屏/上传已删纹理。

对照:香草 `MapRenderer` 能活过 F3+T,正因为它是 reload listener、重载时**重建**自己的 MapInstance 纹理;普通 register 进 byPath 的非 listener 持有者没人替它重建。

修法(纯渲染线程自愈,无需事件):把字段改非 final,每次 render 先检查
```java
private void ensureCloudTexture() {
    if (this.cloudTexture == null || this.cloudTexture.getPixels() == null) {
        this.cloudTexture = new DynamicTexture(W, H, true);
        Minecraft.getInstance().textureManager.register(CLOUD_LOCATION, this.cloudTexture);
        this.lastUploadedAge = -1; // 强制下一帧重新 upload
    }
}
```
render() 每帧先 `ensureCloudTexture()`(健康时只是 null 检查,廉价);recreate 后同一 RL 重注册会顶掉已 close 的旧条目(close 幂等,无害)。RenderType 只存 RL 不持有纹理对象,无需重建。`getPixels()==null` 是 DynamicTexture 被 close 的可靠信号(DynamicTexture.close() 置 null + glTextureId 复位)。

要点:
- 别把云纹理注册 RL 依赖实体数量/循环复用 id(和粒子同款坑,见 [[forge-particledynamictexture-lifetime-remove]])。
- 与粒子版(寿命末 super.tick→remove 关纹理)不同,这里是**资源重载从外部 close**,检测点不是 tick 内 removed,而是渲染前 pixels==null;两者可各自独立修。

实例:PureDarkVoidRenderer 的 `goetyominous:ritual_void_cloud_live` DynamicTexture。相关:[[forge-particledynamictexture-lifetime-remove]]
