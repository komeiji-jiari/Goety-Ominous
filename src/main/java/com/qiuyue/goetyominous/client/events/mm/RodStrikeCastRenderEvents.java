package com.qiuyue.goetyominous.client.events.mm;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.animation.math_animation.AnimationData;
import com.alexander.mutantmore.animation.math_animation.MathAnim;
import com.alexander.mutantmore.animation.math_animation.definition.MutantBlazeMathAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.mm.MutantBlazeServantModel;
import com.qiuyue.goetyominous.common.magic.spells.mm.RodStrikeSpell;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.Minecraft;
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
    private static final float SHOT_END_SECONDS = 4.2F;
    private static final int SHOT_TICKS = 13;
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
                    state.finished = player.tickCount - state.useStartTick >= state.useDuration - 3;
                }
            }
        }
        BURSTS.keySet().retainAll(present);
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
            if (state == null) {
                return;
            }
            if (!state.finished) {
                BURSTS.remove(uuid);
                return;
            }
            if (state.shotStartTick < 0) {
                state.shotStartTick = player.tickCount;
            }
            float sinceShot = player.tickCount + event.getPartialTick() - state.shotStartTick;
            if (sinceShot > SHOT_TICKS) {
                BURSTS.remove(uuid);
                return;
            }
            shotElapsed = Math.max(0.0F, sinceShot);
        }

        ModelPart rods = ServantRings.RODS;
        rods.getAllParts().forEach(ModelPart::resetPose);
        for (ModelPart rod : ServantRings.LAYER1_RODS) {
            rod.visible = true;
        }
        MathAnim.animate(ServantRings.PARTS::get,
                new AnimationData(null, 0, 0.0F, 0.0F, 0.0F, 0.0F, ticks, 0.0F, 0.0F),
                MutantBlazeMathAnimations.IDLE);
        if (chargeSeconds >= 0.0F) {
            float rotSpeed = chargeSeconds < 1.5F ? 150.0F : (chargeSeconds < 3.5F ? Mth.lerp((chargeSeconds - 1.5F) / 2.0F, 300.0F, 600.0F) : (chargeSeconds < 4.5F ? 300.0F : 150.0F));
            ServantRings.PARTS.get("rodLayer1").yRot = (float) Math.toRadians(ticks / 20.0F * rotSpeed);
        } else if (shotElapsed >= 0.0F) {
            float elapsed = SHOT_START_SECONDS + shotElapsed / 20.0F;
            if (elapsed <= SHOT_END_SECONDS) {
                ServantRings.PARTS.get("rodLayer1").yRot = 0.0F;
                MathAnim.animate(ServantRings.PARTS::get,
                        new AnimationData(null, 0, elapsed, 1.0F, 0.0F, 0.0F, ticks, 0.0F, 0.0F),
                        MutantBlazeMathAnimations.ROD_SHOT_MATH);
            }
            int thrown = Math.min((int) shotElapsed + 1, ServantRings.LAYER1_RODS.length);
            for (int i = 0; i < thrown; ++i) {
                ServantRings.LAYER1_RODS[i].visible = false;
            }
        }

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
        private static final ModelPart RODS = MutantBlazeServantModel.createBodyLayer().bakeRoot().getChild("everything").getChild("rods");
        private static final ModelPart LAYER1 = RODS.getChild("rodLayer1Rot").getChild("rodLayer1");
        private static final Map<String, ModelPart> PARTS = createParts();
        private static final ModelPart[] LAYER1_RODS = createLayer1Rods();

        private static ModelPart[] createLayer1Rods() {
            ModelPart[] rods = new ModelPart[15];
            for (int i = 0; i < rods.length; ++i) {
                rods[i] = LAYER1.getChild(i == 0 ? "rod" : "rod" + (i + 1));
            }
            return rods;
        }

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
