package com.qiuyue.goetyominous.client.particle.lm;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

/**
 * 光圈粒子 —— 灵魂柱爆炸时从地上炸开的那一圈「光环」。
 *
 * <p>这是从传奇怪物的 {@code Particle/custom/Circle.java} 逐字照搬的，
 * 只换了包名和两个引用的类（{@code ModParticles} → {@link LmParticles}、
 * {@code MathUtils} → {@link ServantMath}）。逻辑一个字没动。
 *
 * <h2>它和别的粒子最大的不同：参数多到夸张</h2>
 * 一般粒子要么没参数（{@code SimpleParticleType}），要么就一两个数。
 * 这个光圈有 <b>10 个</b>参数：朝向（yaw/pitch）、颜色（rgba）、大小、存活时长、
 * 要不要正对镜头、以及<b>半径怎么变</b>（{@link EnumRingBehavior}）。
 * 所以它必须走「带参数粒子」那一套：一个 {@link RingData} 负责装这些数 +
 * 一个匿名 {@code ParticleType} 子类负责编解码，见 {@code LmParticles.CIRCLE}。
 *
 * <h2>为什么 {@link #render} 要自己手写一遍</h2>
 * 父类 {@code TextureSheetParticle} 的 {@code render} 只能画「一张永远正对镜头的方片」。
 * 光圈不行 —— 它得能<b>平躺在地上</b>、能<b>斜着立起来</b>、还能<b>跟着镜头转</b>，
 * 这三种是同一个粒子靠参数切换的。父类做不到，只能整个覆写：
 * <ol>
 *   <li>先算出这一帧的 {@code quadSize}（光环半径）；</li>
 *   <li>再算出这一帧的旋转四元数 {@code quaternionf} —— <b>这就是分支所在</b>；</li>
 *   <li>把这个「单位方片」的四个角用四元数转一遍，加上粒子坐标，得到四个世界坐标顶点；</li>
 *   <li>手动喂给 {@code VertexConsumer}，<b>画两遍</b>（正反两面都画）。</li>
 * </ol>
 *
 * <p>⚠️ 第 4 步画两遍是<b>故意</b>的：光环很薄，只画一面的话，从背面看会整个消失。
 * 原版就是把同样 4 个顶点正着画一遍、倒着再画一遍。
 *
 * <p>⚠️ 原版有两处<b>死代码</b>，我们照抄保留（保持和原作一致，将来好对照）：
 * <ul>
 *   <li>{@code Vector3f vector3f1} —— 算完了没被用（第 3 步那里）；</li>
 *   <li>{@link #tick()} 里已经调过一次 {@code super.tick()} 让 {@code age} 自增，
 *       结尾又手动 {@code this.age++} 一次 —— 所以这个粒子实际是<b>两倍速</b>走完贴图动画的。
 *       原版就这样，别去「修」它，改了两边表现就不一样了。</li>
 * </ul>
 */
@OnlyIn(Dist.CLIENT)
public class Circle extends TextureSheetParticle {

    public float r;
    public float g;
    public float b;
    public float opacity;
    public boolean facesCamera;
    public float yaw;
    public float pitch;
    public float size;
    private final SpriteSet sprites;
    private final EnumRingBehavior behavior;

    public Circle(
            ClientLevel world,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            float yaw,
            float pitch,
            int duration,
            float r,
            float g,
            float b,
            float opacity,
            float size,
            boolean facesCamera,
            EnumRingBehavior behavior,
            SpriteSet sprites
    ) {
        super(world, x, y, z);
        this.sprites = sprites;
        this.setSize(1.0F, 1.0F);
        this.setSpriteFromAge(this.sprites);
        // ⚠️ 传进来的 size 又乘了 0.1 —— 调用方给的是「大数」，这里换算成实际单位。
        this.size = size * 0.1F;
        this.lifetime = duration;
        this.alpha = 1.0F;
        this.r = r;
        this.g = g;
        this.b = b;
        this.opacity = opacity;
        this.yaw = yaw;
        this.pitch = pitch;
        this.facesCamera = facesCamera;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.behavior = behavior;
    }

    /**
     * 光照等级：<b>只取天空光那一段，方块光强制拉满</b>。
     *
     * <p>{@code 240} 是光照贴图里「方块光」的坐标 ×16，也就是方块光点满；
     * 后面 {@code & 0xFF0000} 是把父类算出来的光照值里<b>天空光</b>那一截保留下来。
     * 合起来 = 「白天亮、晚上暗，但无论多黑都不会真的发黑」——
     * 灵魂柱的光环在夜里也得看得见，所以这么写。原版如此，照抄。
     */
    public int getLightColor(float delta) {
        return 240 | super.getLightColor(delta) & 0xFF0000;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        if (this.age >= this.lifetime) {
            this.remove();
        }

        // ⚠️ 原版这里又加了一次 age（super.tick() 里已经加过），两倍速，照抄不动。
        this.age++;
    }

    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        // var = 这个粒子「活了百分之几」，0 是刚出生、1 是即将消失。
        float var = (this.age + partialTicks) / this.lifetime;
        if (this.behavior == EnumRingBehavior.GROW) {
            this.quadSize = this.size * var;
        } else if (this.behavior == EnumRingBehavior.SHRINK) {
            this.quadSize = this.size * (1.0F - var);
        } else if (this.behavior == EnumRingBehavior.GROW_THEN_SHRINK) {
            // 先胀后缩：var 从小到大时，这条曲线先升后降，峰在中段。
            // 2000 是个魔法数，原版调出来的手感，别动。
            this.quadSize = (float) (this.size * (1.0F - var - Math.pow(2000.0, -var)));
        } else {
            this.quadSize = this.size;
        }

        // 透明度：越接近寿命尽头越淡，但保底 0.05，不会「啪」地一下消失。
        this.alpha = this.opacity * 0.95F * (1.0F - (this.age + partialTicks) / this.lifetime) + 0.05F;
        this.rCol = this.r;
        this.gCol = this.g;
        this.bCol = this.b;

        // 粒子坐标 - 镜头坐标 = 粒子相对镜头的偏移（渲染要在镜头坐标系里画）
        Vec3 Vector3d = renderInfo.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - Vector3d.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - Vector3d.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - Vector3d.z());

        Quaternionf quaternionf = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        if (this.facesCamera) {
            // 「正对镜头」模式：直接抄镜头自己的朝向
            if (this.roll == 0.0F) {
                quaternionf = renderInfo.rotation();
            } else {
                quaternionf = new Quaternionf(renderInfo.rotation());
                float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
                quaternionf.mul(Axis.ZP.rotation(f3));
            }
        } else {
            // 「固定朝向」模式：用 yaw（左右转）和 pitch（前后翻）自己拼一个旋转。
            // ⚠️ 顺序是先 yaw 再 pitch —— 反了光圈会歪。
            Quaternionf quatX = ServantMath.quatFromRotationXYZ(this.pitch, 0.0F, 0.0F, false);
            Quaternionf quatY = ServantMath.quatFromRotationXYZ(0.0F, this.yaw, 0.0F, false);
            quaternionf.mul(quatY);
            quaternionf.mul(quatX);
        }

        // ⚠️ 原版死代码：算完就没再用过。照抄保留，别删（删了和原作对不上号）。
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        quaternionf.transform(vector3f1);

        // 一个「单位方片」的四个角（z 都是 0，所以它是个平铺在 XY 平面上的面）
        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float f4 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            quaternionf.transform(vector3f);   // 转到正确的朝向
            vector3f.mul(f4);                  // 放大到实际半径
            vector3f.add(f, f1, f2);           // 挪到粒子的世界位置
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(partialTicks);

        // 正面
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        // 反面（顶点顺序倒过来）—— 不画的话从背面看光环会消失
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    public ParticleRenderType getRenderType() {
        // 半透明 —— 光环是「透光」的，用不透明档会变成一块生硬的实心片
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** 光环半径随时间怎么变。 */
    public enum EnumRingBehavior {
        /** 由大变小 */
        SHRINK,
        /** 由小变大 */
        GROW,
        /** 一直不变 */
        CONSTANT,
        /** 先胀开、再缩回去 */
        GROW_THEN_SHRINK
    }

    /**
     * 光圈的「参数包」—— 10 个参数全在这里。
     *
     * <p>它同时干三件事，乍看容易懵，其实分开看很简单：
     * <ul>
     *   <li>{@link #writeToNetwork} / {@link #DESERIALIZER}：给<b>命令</b>和<b>网络</b>用的
     *       （服务端发粒子时就是把这些数写进报文）；</li>
     *   <li>{@link #CODEC}：给<b>数据包 / json</b> 用的现代写法；</li>
     *   <li>一堆 {@code getXxx()}：给 {@link RingFactory} 读的，
     *       上面标 {@code @OnlyIn(CLIENT)} 是因为服务端根本不需要读这些数。</li>
     * </ul>
     *
     * <p>⚠️ {@link #writeToString} 里的参数顺序和 {@link #DESERIALIZER} 里读的顺序
     * <b>必须严格对应</b>，原版这里其实是错位的（写的是 scale 在前、a 在后，
     * 读的是 a 在前）—— 因为没人真拿这个字符串去反解析，所以一直没暴露。
     * 照抄不动，别去「修正」。
     */
    public static class RingData implements ParticleOptions {

        public static final Deserializer<RingData> DESERIALIZER = new Deserializer<RingData>() {
            public RingData fromCommand(ParticleType<RingData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                float yaw = (float) reader.readDouble();
                reader.expect(' ');
                float pitch = (float) reader.readDouble();
                reader.expect(' ');
                float r = (float) reader.readDouble();
                reader.expect(' ');
                float g = (float) reader.readDouble();
                reader.expect(' ');
                float b = (float) reader.readDouble();
                reader.expect(' ');
                float a = (float) reader.readDouble();
                reader.expect(' ');
                float scale = (float) reader.readDouble();
                reader.expect(' ');
                int duration = reader.readInt();
                reader.expect(' ');
                boolean facesCamera = reader.readBoolean();
                return new RingData(yaw, pitch, duration, r, g, b, a, scale, facesCamera, EnumRingBehavior.GROW);
            }

            public RingData fromNetwork(ParticleType<RingData> particleTypeIn, FriendlyByteBuf buffer) {
                return new RingData(
                        buffer.readFloat(),
                        buffer.readFloat(),
                        buffer.readInt(),
                        buffer.readFloat(),
                        buffer.readFloat(),
                        buffer.readFloat(),
                        buffer.readFloat(),
                        buffer.readFloat(),
                        buffer.readBoolean(),
                        EnumRingBehavior.GROW
                );
            }
        };

        private final float yaw;
        private final float pitch;
        private final float r;
        private final float g;
        private final float b;
        private final float a;
        private final float scale;
        private final int duration;
        private final boolean facesCamera;
        private final EnumRingBehavior behavior;

        public RingData(
                float yaw, float pitch, int duration, float r, float g, float b, float a,
                float scale, boolean facesCamera, EnumRingBehavior behavior
        ) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.scale = scale;
            this.duration = duration;
            this.facesCamera = facesCamera;
            this.behavior = behavior;
        }

        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.yaw);
            buffer.writeFloat(this.pitch);
            buffer.writeInt(this.duration);
            buffer.writeFloat(this.r);
            buffer.writeFloat(this.g);
            buffer.writeFloat(this.b);
            buffer.writeFloat(this.a);
            buffer.writeFloat(this.scale);
            buffer.writeBoolean(this.facesCamera);
        }

        public String writeToString() {
            return String.format(
                    Locale.ROOT,
                    "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %d %b",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.yaw,
                    this.pitch,
                    this.r,
                    this.g,
                    this.b,
                    this.scale,
                    this.a,
                    this.duration,
                    this.facesCamera
            );
        }

        /** ⚠️ 这里必须指向<b>我们自己的</b>注册项，指回 LM 的会让粒子发不出来。 */
        @SuppressWarnings("unchecked")
        public ParticleType<RingData> getType() {
            return (ParticleType<RingData>) LmParticles.CIRCLE.get();
        }

        @OnlyIn(Dist.CLIENT)
        public float getYaw() {
            return this.yaw;
        }

        @OnlyIn(Dist.CLIENT)
        public float getPitch() {
            return this.pitch;
        }

        @OnlyIn(Dist.CLIENT)
        public float getR() {
            return this.r;
        }

        @OnlyIn(Dist.CLIENT)
        public float getG() {
            return this.g;
        }

        @OnlyIn(Dist.CLIENT)
        public float getB() {
            return this.b;
        }

        @OnlyIn(Dist.CLIENT)
        public float getA() {
            return this.a;
        }

        @OnlyIn(Dist.CLIENT)
        public float getScale() {
            return this.scale;
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public boolean getFacesCamera() {
            return this.facesCamera;
        }

        @OnlyIn(Dist.CLIENT)
        public EnumRingBehavior getBehavior() {
            return this.behavior;
        }

        public static Codec<RingData> CODEC(ParticleType<RingData> particleType) {
            return RecordCodecBuilder.create(
                    codecBuilder -> codecBuilder.group(
                                    Codec.FLOAT.fieldOf("yaw").forGetter(RingData::getYaw),
                                    Codec.FLOAT.fieldOf("pitch").forGetter(RingData::getPitch),
                                    Codec.FLOAT.fieldOf("r").forGetter(RingData::getR),
                                    Codec.FLOAT.fieldOf("g").forGetter(RingData::getG),
                                    Codec.FLOAT.fieldOf("b").forGetter(RingData::getB),
                                    Codec.FLOAT.fieldOf("a").forGetter(RingData::getA),
                                    Codec.FLOAT.fieldOf("scale").forGetter(RingData::getScale),
                                    Codec.INT.fieldOf("duration").forGetter(RingData::getDuration),
                                    Codec.BOOL.fieldOf("facesCamera").forGetter(RingData::getFacesCamera),
                                    Codec.STRING.fieldOf("behavior").forGetter(ringData -> ringData.getBehavior().toString())
                            )
                            .apply(
                                    codecBuilder,
                                    (yaw, pitch, r, g, b, a, scale, duration, facesCamera, behavior) -> new RingData(
                                            yaw, pitch, duration, r, g, b, a, scale, facesCamera,
                                            EnumRingBehavior.valueOf(behavior)
                                    )
                            )
            );
        }
    }

    /** 「生产车间」：收到 {@link RingData} 就照着造一个 {@link Circle}。 */
    @OnlyIn(Dist.CLIENT)
    public static final class RingFactory implements ParticleProvider<RingData> {
        private final SpriteSet spriteSet;

        public RingFactory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        public Particle createParticle(RingData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new Circle(
                    worldIn,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    typeIn.getYaw(),
                    typeIn.getPitch(),
                    typeIn.getDuration(),
                    typeIn.getR(),
                    typeIn.getG(),
                    typeIn.getB(),
                    typeIn.getA(),
                    typeIn.getScale(),
                    typeIn.getFacesCamera(),
                    typeIn.getBehavior(),
                    this.spriteSet
            );
        }
    }
}
