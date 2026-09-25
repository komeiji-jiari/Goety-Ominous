package com.qiuyue.goetyominous.client.particle.lm;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

/**
 * 幻影匕首的「拖尾飘带」粒子 —— 匕首飞过时身后拉出的那条会旋转的彩色光带。
 *
 * <p>从传奇怪物的同名类逐字照搬，<b>只改了两处</b>：
 * <ol>
 *   <li>贴图路径的命名空间：{@code legendary_monsters} → {@code goetyominous}；</li>
 *   <li>注册表的引用：{@code ModParticles} → {@link LmParticles}。</li>
 * </ol>
 * 逻辑一个字没动。
 *
 * <h2>这个粒子特殊在哪</h2>
 * 绝大多数粒子是「放出去就不管了」。这一个不一样 ——
 * <b>它每 tick 都要去世界里把匕首本体找出来，然后绕着它转</b>。
 * 靠的就是参数里那个 {@code entityid}（{@link OrbData#getentityid()}）：
 * <ul>
 *   <li>{@link #getFromEntity()} —— 拿 ID 换实体对象，换不到（比如匕首已经没了）就返回 null；</li>
 *   <li>{@link #getOrbitPosition()} —— 算出「绕着匕首转的一个点」，
 *       用 {@code initialYRot}（初始角度，随机）和 {@code rotateByAge}（每 tick 转多少度，
 *       也是随机正负）让每颗粒子转得都不一样，看起来才自然；</li>
 *   <li>{@link #tick()} 每 tick 把自己搬到那个点上。</li>
 * </ul>
 *
 * <p>⚠️ 所以匕首 {@code ThrownPhantomDagger} 那边的 {@code x, y, z}
 * （生成时传的坐标）其实<b>立刻就被覆盖了</b>，真正决定位置的是匕首自己在哪。
 *
 * <h2>为什么会自己消失</h2>
 * {@link #tick()} 末尾有一句：找不到匕首就 {@code remove()}。
 * 这样匕首一消失，它留下的拖尾也会很快跟着散掉，不会有一串光带僵在天上。
 *
 * <h2>带参数的粒子是怎么写的</h2>
 * 这是本项目里唯一一个「带参数」的粒子，值得单独说明一下。
 * 无参粒子（比如 {@code GhostlySoul}）游戏直接给个 {@code SimpleParticleType} 就行，
 * 但带参数的必须自己提供三样东西：
 * <ol>
 *   <li><b>{@link OrbData#writeToNetwork}</b> —— 参数怎么写成网络包（发给客户端）；</li>
 *   <li><b>{@link OrbData#DESERIALIZER}</b> —— 参数怎么从网络包 / 命令读回来；</li>
 *   <li><b>{@link OrbData#CODEC}</b> —— 参数怎么用数据包 JSON 写（1.20.1 的数据包格式）。
 *       注册表 {@code LmParticles} 里那个匿名 {@code ParticleType} 子类要的就是它。</li>
 * </ol>
 * 三样缺一样，粒子就传不到客户端 —— 表现是「服务端说放了粒子，客户端什么都没有」，
 * 而且<b>不报错</b>。这是这类粒子最容易踩的坑。
 */
public class PhantomDaggerTrail extends AbstractNotGlowingTrailParticle {

    /** 唯一的改动之一：贴图换成我们自己的命名空间。 */
    private static final ResourceLocation TRAIL_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/particle/trail_soul.png");

    /** 要跟随的实体 ID。-1 表示「不跟随任何东西」。 */
    private final int EntityId;

    /** 轨道「横向」半径，从 {@link OrbData} 传进来。 */
    private final float width;

    /** 轨道「纵向」高度，从 {@link OrbData} 传进来。原版匕首传的是 0.25。 */
    private final float height;

    /** 起始角度（0~360 随机），见类注释。 */
    private final float initialYRot;

    /** 每 tick 转多少度（10~20 随机，正负也是随机）。 */
    private final float rotateByAge;

    public PhantomDaggerTrail(ClientLevel world, double x, double y, double z,
                              float r, float g, float b,
                              float width, float height, int EntityId) {
        super(world, x, y, z, 0.0, 0.0, 0.0, r, g, b);
        this.EntityId = EntityId;
        this.gravity = 0.0F;
        // 寿命 20~39 tick 随机 —— 拖尾有长有短，不会齐刷刷一起消失。
        this.lifetime = 20 + this.random.nextInt(20);
        this.initialYRot = this.random.nextFloat() * 360.0F;
        this.rotateByAge = (10.0F + this.random.nextFloat() * 10.0F) * (this.random.nextBoolean() ? -1.0F : 1.0F);
        this.width = width;
        this.height = height;

        // 一生成就先跳到轨道上，免得第一帧出现在匕首正中心。
        Vec3 vec3 = this.getOrbitPosition();
        this.x = this.xo = vec3.x;
        this.y = this.yo = vec3.y;
        this.z = this.zo = vec3.z;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
    }

    /** 跟随目标的位置。找不到目标时退而求其次，用粒子自己上次的坐标。 */
    public Vec3 getEntityPosition() {
        Entity from = this.getFromEntity();
        return from != null ? from.position() : new Vec3(this.x, this.y, this.z);
    }

    /** 拿实体 ID 换实体对象。ID 是 -1、或者实体已经没了，都返回 null。 */
    public Entity getFromEntity() {
        return this.EntityId == -1 ? null : this.level.getEntity(this.EntityId);
    }

    /** 见类注释：绕着匕首转的那个点。 */
    public Vec3 getOrbitPosition() {
        Vec3 dinoPos = this.getEntityPosition();
        Vec3 vec3 = new Vec3(0.0, this.height, this.width)
                .yRot((float) Math.toRadians(this.initialYRot + this.rotateByAge * this.age));
        return dinoPos.add(vec3);
    }

    @Override
    public void tick() {
        super.tick();
        // 越老越透明，最后消失。fade 从 1 线性掉到 0。
        float fade = 1.0F - (float) this.age / this.lifetime;
        this.trailA = 1.0F * fade;

        // 跟着匕首走 —— 见类注释。注意是直接赋值，不走 move()。
        Vec3 vec3 = this.getOrbitPosition();
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;

        // 匕首没了，我们也没必要留着。
        Entity from = this.getFromEntity();
        if (from == null) {
            this.remove();
        }
    }

    /** ⚠️ 只取 4 个采样点（基类默认 20）。拖尾短、点少，才是一小截光带而不是长飘带。 */
    @Override
    public int sampleCount() {
        return 4;
    }

    @Override
    public int sampleStep() {
        return 1;
    }

    @Override
    public float getTrailHeight() {
        return 0.5F;
    }

    /** 基类本来就是 240，这里再写一遍是原版的冗余，照抄保留。 */
    @Override
    public int getLightColor(float f) {
        return 240;
    }

    @Override
    public ResourceLocation getTrailTexture() {
        return TRAIL_TEXTURE;
    }

    // ==================================================================
    //  参数（颜色 + 轨道 + 跟谁）
    // ==================================================================

    /**
     * 拖尾粒子的参数。构造时六个值，全部要能在网络上传递。
     *
     * @see PhantomDaggerTrail 类注释「带参数的粒子是怎么写的」
     */
    public static class OrbData implements ParticleOptions {

        /** 从网络包还原一份参数。客户端收到粒子包时走这条路。 */
        public static final Deserializer<PhantomDaggerTrail.OrbData> DESERIALIZER =
                new Deserializer<PhantomDaggerTrail.OrbData>() {

                    /** 从聊天栏 {@code /particle} 命令读参数。服务端管理员测试时用。 */
                    @Override
                    public PhantomDaggerTrail.OrbData fromCommand(ParticleType<PhantomDaggerTrail.OrbData> particleTypeIn,
                                                                  StringReader reader) throws CommandSyntaxException {
                        reader.expect(' ');
                        float r = reader.readFloat();
                        reader.expect(' ');
                        float g = reader.readFloat();
                        reader.expect(' ');
                        float b = reader.readFloat();
                        reader.expect(' ');
                        float width = reader.readFloat();
                        reader.expect(' ');
                        float height = reader.readFloat();
                        reader.expect(' ');
                        int EntityId = reader.readInt();
                        return new PhantomDaggerTrail.OrbData(r, g, b, width, height, EntityId);
                    }

                    @Override
                    public PhantomDaggerTrail.OrbData fromNetwork(ParticleType<PhantomDaggerTrail.OrbData> particleTypeIn,
                                                                  FriendlyByteBuf buffer) {
                        return new PhantomDaggerTrail.OrbData(
                                buffer.readFloat(), buffer.readFloat(), buffer.readFloat(),
                                buffer.readFloat(), buffer.readFloat(), buffer.readInt());
                    }
                };

        private final float r;
        private final float g;
        private final float b;
        private final float width;
        private final float height;
        private final int entityid;

        public OrbData(float r, float g, float b, float width, float height, int entityid) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.width = width;
            this.height = height;
            this.entityid = entityid;
        }

        /** ⚠️ 顺序必须和 {@link #DESERIALIZER} 的 {@code fromNetwork} 对得一模一样，否则颜色会串位。 */
        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.r);
            buffer.writeFloat(this.g);
            buffer.writeFloat(this.b);
            buffer.writeFloat(this.width);
            buffer.writeFloat(this.height);
            buffer.writeInt(this.entityid);
        }

        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %d",
                    BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.r, this.g, this.b, this.width, this.height, this.entityid);
        }

        /** 唯一的改动之二：指向我们自己的注册表。 */
        @Override
        public ParticleType<PhantomDaggerTrail.OrbData> getType() {
            return (ParticleType<PhantomDaggerTrail.OrbData>) LmParticles.PHANTOM_DAGGER_TRAIL.get();
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
        public float getWidth() {
            return this.width;
        }

        @OnlyIn(Dist.CLIENT)
        public float getHeight() {
            return this.height;
        }

        @OnlyIn(Dist.CLIENT)
        public int getentityid() {
            return this.entityid;
        }

        public static Codec<PhantomDaggerTrail.OrbData> CODEC(ParticleType<PhantomDaggerTrail.OrbData> particleType) {
            return RecordCodecBuilder.create(
                    codecBuilder -> codecBuilder.group(
                                    Codec.FLOAT.fieldOf("r").forGetter(PhantomDaggerTrail.OrbData::getR),
                                    Codec.FLOAT.fieldOf("g").forGetter(PhantomDaggerTrail.OrbData::getG),
                                    Codec.FLOAT.fieldOf("b").forGetter(PhantomDaggerTrail.OrbData::getB),
                                    Codec.FLOAT.fieldOf("width").forGetter(PhantomDaggerTrail.OrbData::getWidth),
                                    Codec.FLOAT.fieldOf("height").forGetter(PhantomDaggerTrail.OrbData::getHeight),
                                    Codec.INT.fieldOf("entityid").forGetter(PhantomDaggerTrail.OrbData::getentityid)
                            )
                            .apply(codecBuilder, PhantomDaggerTrail.OrbData::new)
            );
        }
    }

    /** 客户端的「生产车间」：游戏要画这个粒子时，就调这里 new 一个出来。 */
    @OnlyIn(Dist.CLIENT)
    public static final class OrbFactory implements ParticleProvider<PhantomDaggerTrail.OrbData> {

        @Override
        public Particle createParticle(PhantomDaggerTrail.OrbData typeIn, ClientLevel worldIn,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new PhantomDaggerTrail(worldIn, x, y, z,
                    typeIn.getR(), typeIn.getG(), typeIn.getB(),
                    typeIn.getWidth(), typeIn.getHeight(), typeIn.getentityid());
        }
    }
}
