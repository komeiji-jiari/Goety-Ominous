package com.example.goetyastrology.client;

import com.example.goetyastrology.GoetyAstrologyMod;
import com.example.goetyastrology.astrology.LunarBurstEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;


/**
 * Draws 月华, the splash a 月耀 thread leaves where it lands: a churning ball of cyan light with a
 * white-hot core, two crests of light opening across the ground with a thinner shock front outrunning
 * them, and the flash at the point of contact, which cools from white into the focus icon's own cyan as
 * it dies.
 *
 * <p>Drawn at {@link RenderLevelStageEvent.Stage#AFTER_WEATHER} and handed to {@link LateSkyPass} when a
 * shader pack is loaded, for the same reason the meteor blast is: it is pure emission that writes no
 * depth, and a pack rebuilds every pixel still at the far plane, so anything drawn in the entity pass
 * against open sky is recomputed out of existence.
 *
 * <p>🔴 Both halves are the same quad. The ring is a <em>world-aligned</em> quad on the ground — the
 * model-view stack already carries the camera's rotation by this point, so passing world-space corner
 * offsets is enough and no billboarding is involved — while the ball is camera-facing and built from the
 * camera's own right and up. One program, {@code uMode} deciding which of the two it is drawing; they
 * have to be two separate batches because a uniform is read when the batch is drawn, and setting it
 * mid-batch would apply the last value to every vertex in it.
 */
@Mod.EventBusSubscriber(modid = GoetyAstrologyMod.MODID, value = Dist.CLIENT)
public class LunarBurstRenderer extends EntityRenderer<LunarBurstEntity> {

    /**
     * The ball covers more ground than the ring, because light spills past what it struck: this many
     * blast radii to each side, with the shader keeping the ball itself inside 0.86 of the quad.
     */
    private static final float BALL_SCALE = 1.45F;
    /**
     * How far above the point of contact the ball is centred, in blast radii.
     *
     * <p>An impact point sits on the <em>surface</em> of a block, so a ball centred there would be half
     * underground. A dome of light is the right silhouette for a ground burst, but the churn that makes
     * it read as a ball is wasted if it is buried — lifting the centre a third of a radius leaves only
     * the underside clipped by the ground it struck, which is exactly the shape wanted.
     */
    private static final float BALL_LIFT = 0.30F;
    /** The ring sits this far off the impact height, so it does not z-fight the block it landed on. */
    private static final float RING_LIFT = 0.03F;
    /**
     * Life fraction past which the ink is not drawn at all. The shader's own alpha is zero a little
     * before this; the cut is here so that most of every splash's lifetime is not spent submitting a
     * fully transparent quad.
     */
    private static final float INK_WINDOW = 0.30F;

    public LunarBurstRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(LunarBurstEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    /** Nothing in the entity pass; the world-stage pass below owns the splash entirely. */
    @Override
    public void render(LunarBurstEntity entity, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTick, pose, buffer, packedLight);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        ShaderInstance shader = ModShaders.lunarBurst();
        if (level == null || shader == null) {
            // No program, no splash. A flat cyan disc at the impact point would be worse than nothing.
            return;
        }
        Camera camera = event.getCamera();
        Vec3 eye = camera.getPosition();
        float partialTick = event.getPartialTick();
        boolean defer = LateSkyPass.deferring();
        Vector3f leftVector = camera.getLeftVector();
        Vec3 right = new Vec3(-leftVector.x(), -leftVector.y(), -leftVector.z());
        Vector3f upVector = camera.getUpVector();
        Vec3 up = new Vec3(upVector.x(), upVector.y(), upVector.z());

        // 🔴 Two passes over the bursts, not one. The ink has to leave the screen black, and if each
        // burst drew its own ink straight after its own light, the next burst's light would be added on
        // top of the black that came before it. All the light first, then all the ink: only then does a
        // hole stay a hole.
        for (Entity other : level.entitiesForRendering()) {
            if (!(other instanceof LunarBurstEntity burst)) {
                continue;
            }
            float life = Mth.clamp((burst.tickCount + partialTick) / (float) LunarBurstEntity.LIFE,
                    0.0F, 1.0F);
            // The offset and nothing else: the camera rotation is already on RenderSystem's model-view
            // stack, and LateSkyPass reinstates the same one when the draw is deferred.
            Matrix4f matrix = new Matrix4f().translation(
                    (float) (Mth.lerp(partialTick, burst.xo, burst.getX()) - eye.x),
                    (float) (Mth.lerp(partialTick, burst.yo, burst.getY()) - eye.y),
                    (float) (Mth.lerp(partialTick, burst.zo, burst.getZ()) - eye.z));
            float radius = burst.radius();
            // Every thread of a salvo lands within a couple of blocks of the last, so five splashes are
            // on screen at once. Without a per-burst seed all five would churn in exactly the same
            // shape, which is the one thing the eye picks out immediately.
            float seed = Mth.frac(burst.getId() * 0.618034F);
            if (defer) {
                LateSkyPass.submit(() -> drawSplash(matrix, life, radius, seed, right, up, shader), true);
            } else {
                drawSplash(matrix, life, radius, seed, right, up, shader);
            }
        }

        for (Entity other : level.entitiesForRendering()) {
            if (!(other instanceof LunarBurstEntity burst)) {
                continue;
            }
            float life = Mth.clamp((burst.tickCount + partialTick) / (float) LunarBurstEntity.LIFE,
                    0.0F, 1.0F);
            if (life > INK_WINDOW) {
                continue;
            }
            Matrix4f matrix = new Matrix4f().translation(
                    (float) (Mth.lerp(partialTick, burst.xo, burst.getX()) - eye.x),
                    (float) (Mth.lerp(partialTick, burst.yo, burst.getY()) - eye.y),
                    (float) (Mth.lerp(partialTick, burst.zo, burst.getZ()) - eye.z));
            float radius = burst.radius();
            if (defer) {
                LateSkyPass.submit(() -> drawInk(matrix, radius, right, up, shader), true);
            } else {
                drawInk(matrix, radius, right, up, shader);
            }
        }
    }

    /**
     * The impact frame. The only pass in this file that is not additive, because additive blending can
     * only ever make things brighter — a hole in the light is not something it can produce. Paints black
     * with ordinary alpha blending instead, and the shader fades that alpha out inside the first fifth
     * of the splash, so the cyan underneath is already mid-growth by the time it is revealed.
     */
    private static void drawInk(Matrix4f mat, float radius, Vec3 right, Vec3 up, ShaderInstance shader) {
        if (radius <= 0.0F) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(() -> shader);
        shader.safeGetUniform("uSeed").set(0.0F);
        shader.safeGetUniform("uMode").set(2.0F);
        drawBall(mat, radius * BALL_SCALE, radius * BALL_LIFT, right, up);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static void drawSplash(Matrix4f mat, float life, float radius, float seed, Vec3 right, Vec3 up,
                                   ShaderInstance shader) {
        if (radius <= 0.0F) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(() -> shader);
        shader.safeGetUniform("uSeed").set(seed);

        // 1. the wave on the ground
        shader.safeGetUniform("uLife").set(life);
        shader.safeGetUniform("uMode").set(0.0F);
        drawGroundWave(mat, radius);

        // 2. the ball of light — its own batch, because uMode is read when the batch is drawn, not per
        // vertex.
        shader.safeGetUniform("uMode").set(1.0F);
        drawBall(mat, radius * BALL_SCALE, radius * BALL_LIFT, right, up);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    /**
     * The ring: a square of world axes lying on the ground, one blast radius wide on each side of the
     * impact. The shader is what turns it into a circle — this only has to be a quad the right size and
     * in the right plane.
     */
    private static void drawGroundWave(Matrix4f mat, float radius) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        corner(builder, mat, -radius, RING_LIFT, -radius, -1.0F, -1.0F);
        corner(builder, mat, radius, RING_LIFT, -radius, 1.0F, -1.0F);
        corner(builder, mat, radius, RING_LIFT, radius, 1.0F, 1.0F);
        corner(builder, mat, -radius, RING_LIFT, radius, -1.0F, 1.0F);
        BufferUploader.drawWithShader(builder.end());
    }

    /**
     * The ball: a camera-facing square of {@code half} extent on each side, centred {@code lift} above
     * the impact so the ground clips its underside instead of its waist.
     */
    private static void drawBall(Matrix4f mat, float half, float lift, Vec3 right, Vec3 up) {
        Matrix4f ball = new Matrix4f(mat).translate(0.0F, lift, 0.0F);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        corner(builder, ball, right, up, half, -1.0F, -1.0F);
        corner(builder, ball, right, up, half, 1.0F, -1.0F);
        corner(builder, ball, right, up, half, 1.0F, 1.0F);
        corner(builder, ball, right, up, half, -1.0F, 1.0F);
        BufferUploader.drawWithShader(builder.end());
    }

    /** One corner of the ground quad: absolute world offsets, UV carrying the disc coordinate. */
    private static void corner(BufferBuilder builder, Matrix4f mat, float x, float y, float z,
                               float u, float v) {
        builder.vertex(mat, x, y, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, v).endVertex();
    }

    /** One corner of the flash quad: offsets along the camera's right and up vectors. */
    private static void corner(BufferBuilder builder, Matrix4f mat, Vec3 right, Vec3 up, float half,
                               float x, float y) {
        Vec3 offset = right.scale(x * half).add(up.scale(y * half));
        builder.vertex(mat, (float) offset.x, (float) offset.y, (float) offset.z)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(x, y)
                .endVertex();
    }
}
