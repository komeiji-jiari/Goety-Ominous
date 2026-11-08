package com.qiuyue.goetyominous.client.events.mm;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantBlazeKeyframeAnimations;
import com.alexander.mutantmore.animation.math_animation.AnimationData;
import com.alexander.mutantmore.animation.math_animation.MathAnim;
import com.alexander.mutantmore.animation.math_animation.definition.MutantBlazeMathAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.mm.MutantBlazeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantBlazeServant;
import com.qiuyue.goetyominous.common.magic.spells.mm.RodStrikeSpell;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RodStrikeCastRenderEvents {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mutantmore", "textures/entities/mutant_blaze.png");
    private static final float MODEL_SCALE = 1.5F;
    private static final float LOWEST_RING_HEIGHT = 15.0F * MODEL_SCALE / 16.0F;
    private static final float RING_CLEARANCE = 0.25F;
    private static final float SHOT_START_SECONDS = 3.55F;
    private static final float SHOT_END_SECONDS = 4.25F;
    private static final float SHOT_TICKS = (SHOT_END_SECONDS - SHOT_START_SECONDS) * 20.0F;
    private static final Vector3f ANIMATION_CACHE = new Vector3f();
    private static final Map<UUID, BurstState> BURSTS = new HashMap<>();

    private RodStrikeCastRenderEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            BURSTS.clear();
            return;
        }
        Set<UUID> present = new HashSet<>();
        for (Player player : level.players()) {
            UUID uuid = player.getUUID();
            present.add(uuid);
            if (player.isUsingItem() && WandUtil.getSpellOnHand(player, player.getUsedItemHand()) instanceof RodStrikeSpell) {
                BurstState state = BURSTS.computeIfAbsent(uuid, key -> new BurstState());
                if (!state.using) {
                    state.useStartTick = player.tickCount;
                    state.useDuration = Math.max(1, player.getUseItem().getUseDuration());
                    state.shotStartTick = -1;
                    state.finished = false;
                }
                state.using = true;
            } else {
                BurstState state = BURSTS.get(uuid);
                if (state != null && state.using) {
                    state.using = false;
                    if (player.tickCount - state.useStartTick >= state.useDuration - 3) {
                        state.finished = true;
                        state.shotStartTick = player.tickCount;
                    } else {
                        BURSTS.remove(uuid);
                    }
                }
            }
        }
        BURSTS.keySet().retainAll(present);
        BURSTS.entrySet().removeIf(entry -> {
            BurstState state = entry.getValue();
            Player player = level.getPlayerByUUID(entry.getKey());
            return !state.using && player != null && player.tickCount - state.shotStartTick > SHOT_TICKS;
        });
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        Player player = event.getEntity();
        if (player.isCrouching()) {
            return;
        }
        UUID uuid = player.getUUID();
        float ticks = player.tickCount + event.getPartialTick();
        float chargeSeconds = -1.0F;
        float shotElapsed = -1.0F;
        if (player.isUsingItem()) {
            ItemStack stack = player.getUseItem();
            if (!(stack.getItem() instanceof IWand)) {
                BURSTS.remove(uuid);
                return;
            }
            ISpell spell = WandUtil.getSpellOnHand(player, player.getUsedItemHand());
            if (!(spell instanceof RodStrikeSpell)) {
                BURSTS.remove(uuid);
                return;
            }
            float duration = Math.max(1.0F, (float) stack.getUseDuration());
            float progress = Mth.clamp(1.0F - (float) player.getUseItemRemainingTicks() / duration, 0.0F, 1.0F);
            chargeSeconds = progress * SHOT_START_SECONDS;
        } else {
            BurstState state = BURSTS.get(uuid);
            if (state == null || state.using) {
                return;
            }
            if (!state.finished) {
                BURSTS.remove(uuid);
                return;
            }
            float sinceShot = player.tickCount + event.getPartialTick() - state.shotStartTick;
            if (sinceShot > SHOT_TICKS) {
                BURSTS.remove(uuid);
                return;
            }
            shotElapsed = Math.max(0.0F, sinceShot);
        }

        float elapsed = chargeSeconds >= 0.0F ? chargeSeconds : SHOT_START_SECONDS + shotElapsed / 20.0F;
        float walkSpeed = (float) player.getDeltaMovement().length() * 15.0F;

        ModelPart rods = ServantRings.RODS;
        rods.getAllParts().forEach(ModelPart::resetPose);
        MathAnim.animate(ServantRings.PARTS::get,
                new AnimationData(null, 0, 0.0F, walkSpeed, 0.0F, 0.0F, ticks, 0.0F, 0.0F),
                MutantBlazeMathAnimations.IDLE);
        ServantRings.PARTS.get("rodLayer1").yRot = 0.0F;
        KeyframeAnimations.animate(ServantRings.MODEL, MutantBlazeKeyframeAnimations.ROD_SHOT_KEYFRAME,
                (long)(elapsed * 1000.0F), 1.0F, ANIMATION_CACHE);
        MathAnim.animate(ServantRings.PARTS::get,
                new AnimationData(null, 0, elapsed, 1.0F, 0.0F, 0.0F, ticks, 0.0F, 0.0F),
                MutantBlazeMathAnimations.ROD_SHOT_MATH);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        poseStack.pushPose();
        poseStack.translate(0.0D, player.getBbHeight() + RING_CLEARANCE - LOWEST_RING_HEIGHT, 0.0D);
        poseStack.scale(-MODEL_SCALE, -MODEL_SCALE, MODEL_SCALE);
        rods.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        rods.render(poseStack, buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static final class BurstState {
        private int useStartTick;
        private int useDuration = 1;
        private int shotStartTick = -1;
        private boolean using;
        private boolean finished;
    }

    private static final class ServantRings {
        private static final MutantBlazeServantModel<MutantBlazeServant> MODEL =
                new MutantBlazeServantModel<>(MutantBlazeServantModel.createBodyLayer().bakeRoot());
        private static final ModelPart RODS = MODEL.root().getChild("everything").getChild("rods");
        private static final ModelPart LAYER1 = RODS.getChild("rodLayer1Rot").getChild("rodLayer1");
        private static final Map<String, ModelPart> PARTS = createParts();

        private static Map<String, ModelPart> createParts() {
            Map<String, ModelPart> parts = new HashMap<>();
            ModelPart layer1Rot = RODS.getChild("rodLayer1Rot");
            ModelPart layer1 = LAYER1;
            ModelPart layer2Rot = RODS.getChild("rodLayer2Rot");
            ModelPart layer3Rot = RODS.getChild("rodLayer3Rot");
            layer2Rot.visible = false;
            layer3Rot.visible = false;
            parts.put("rodLayer1Rot", layer1Rot);
            parts.put("rodLayer1", layer1);
            parts.put("rodLayer2Rot", layer2Rot);
            parts.put("rodLayer2", layer2Rot.getChild("rodLayer2"));
            parts.put("rodLayer3Rot", layer3Rot);
            parts.put("rodLayer3", layer3Rot.getChild("rodLayer3"));
            parts.put("rodRotation", layer1.getChild("rod").getChild("rodRotation"));
            for (int i = 2; i <= 15; ++i) {
                String rod = "rod" + i;
                parts.put("rodRotation" + i, layer1.getChild(rod).getChild("rodRotation" + i));
            }
            return parts;
        }
    }
}
