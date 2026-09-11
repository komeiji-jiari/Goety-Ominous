package com.Polarice3.Goety.client.events;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.entities.IBarrack;
import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.Polarice3.Goety.api.blocks.entities.ITrainingBlock;
import com.Polarice3.Goety.api.blocks.entities.IWaystoneBlock;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.client.audio.BossLoopMusic;
import com.Polarice3.Goety.client.audio.GuardianAttackSound;
import com.Polarice3.Goety.client.audio.GuardianLaserSound;
import com.Polarice3.Goety.client.audio.ItemLoopSound;
import com.Polarice3.Goety.client.audio.LoopSound;
import com.Polarice3.Goety.client.audio.PreBossLoopMusic;
import com.Polarice3.Goety.client.audio.SummonNoveltySound;
import com.Polarice3.Goety.client.audio.WightLoopSound;
import com.Polarice3.Goety.client.events.ClientEvents.1;
import com.Polarice3.Goety.client.gui.screen.inventory.BrewRadialMenuScreen;
import com.Polarice3.Goety.client.gui.screen.inventory.FocusRadialMenuScreen;
import com.Polarice3.Goety.client.render.BurrowingLaserRenderer;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.PrismaBeamRenderer;
import com.Polarice3.Goety.client.render.WaterJetRenderer;
import com.Polarice3.Goety.client.render.WearRenderer;
import com.Polarice3.Goety.client.render.item.CustomItemsRenderer;
import com.Polarice3.Goety.client.render.model.LichModeModel;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.OminousIdolBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SculpturedStatueBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity.Mode;
import com.Polarice3.Goety.common.crafting.CauldronSusStewRecipe;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.GuardianServant;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.entities.ally.illager.CrusherServant;
import com.Polarice3.Goety.common.entities.ally.illager.StormCasterServant;
import com.Polarice3.Goety.common.entities.ally.illager.WindCallerServant;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneGolem;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneMonstrosity;
import com.Polarice3.Goety.common.entities.hostile.illagers.StormCaster;
import com.Polarice3.Goety.common.entities.hostile.servants.Inferno;
import com.Polarice3.Goety.common.entities.neutral.ApostleShade;
import com.Polarice3.Goety.common.entities.neutral.CarrionFly;
import com.Polarice3.Goety.common.entities.neutral.InsectSwarm;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.items.curios.GloveItem;
import com.Polarice3.Goety.common.items.curios.TargetingMonocleItem;
import com.Polarice3.Goety.common.magic.spells.abyss.PrismaBeamSpell;
import com.Polarice3.Goety.common.magic.spells.abyss.WaterJetSpell;
import com.Polarice3.Goety.common.magic.spells.geomancy.BurrowingSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.client.CActivateCurioKeyPacket;
import com.Polarice3.Goety.common.network.client.CAutoRideablePacket;
import com.Polarice3.Goety.common.network.client.CBagKeyPacket;
import com.Polarice3.Goety.common.network.client.CDismissServantsPacket;
import com.Polarice3.Goety.common.network.client.CExtractPotionKeyPacket;
import com.Polarice3.Goety.common.network.client.CMagnetPacket;
import com.Polarice3.Goety.common.network.client.CMultiJumpPacket;
import com.Polarice3.Goety.common.network.client.CRavagerRoarPacket;
import com.Polarice3.Goety.common.network.client.CSetLichMode;
import com.Polarice3.Goety.common.network.client.CSetLichNightVisionMode;
import com.Polarice3.Goety.common.network.client.CStopAttackPacket;
import com.Polarice3.Goety.common.network.client.CTargetPlayerPacket;
import com.Polarice3.Goety.common.network.client.CWandKeyPacket;
import com.Polarice3.Goety.common.network.client.CWitchRobePacket;
import com.Polarice3.Goety.common.network.client.brew.CBrewBagKeyPacket;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModKeybindings;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags.EntityTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.RenderBlockUtils;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TotemFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@EventBusSubscriber(
    modid = "goety",
    value = {Dist.CLIENT}
)
public class ClientEvents {
    public static float PARTIAL_TICK = 0.0F;
    public static AbstractTickableSoundInstance PRE_BOSS_MUSIC;
    public static AbstractTickableSoundInstance BOSS_MUSIC;
    public static ColorUtil CUBE_COLOR = new ColorUtil(ChatFormatting.GOLD);
    private static final ResourceLocation CUSTOM_HEARTS = Goety.location("textures/gui/custom_hearts.png");
    private static int lastHealth;
    private static int displayHealth;
    private static long lastHealthTime;
    private static long healthBlinkTime;
    private static boolean prevJumpBindState = false;
    public static boolean lockedOn;
    public static Entity target;
    public static List<LivingEntity> targetList = new ArrayList();
    private static final Predicate<LivingEntity> ENTITY_PREDICATE = (entity) -> entity.m_6084_() && entity.m_5789_() && !isFriendly(entity);
    private static int cycle = -1;
    private static boolean toolMenuKeyWasDown = false;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ClientLevel) {
            Minecraft minecraft = Minecraft.m_91087_();
            SoundManager soundHandler = minecraft.m_91106_();
            if (entity instanceof CorruptedBeam) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.CORRUPT_BEAM_LOOP.get(), entity));
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.CORRUPT_BEAM_SOUL.get(), entity));
            }

            if (entity instanceof ApostleShade) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.APOSTLE_SHADE.get(), entity));
            }

            if (entity instanceof Inferno) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.INFERNO_LOOP.get(), entity));
            }

            if (entity instanceof Wildfire) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.WILDFIRE_LOOP.get(), entity));
            }

            if (entity instanceof WindCallerServant || entity instanceof StormCaster || entity instanceof StormCasterServant) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.FLIGHT.get(), 1.0F, 1.3F, entity));
            }

            if (entity instanceof InsectSwarm) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.INSECT_SWARM.get(), entity));
            }

            if (entity instanceof CarrionFly) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.FLY_LOOP.get(), 0.4F, 2.0F, entity));
            }

            if (entity instanceof Wight) {
                Wight wight = (Wight)entity;
                if (!wight.isHallucination()) {
                    soundHandler.m_120367_(new WightLoopSound(wight));
                }
            }

            if (entity instanceof IceStorm) {
                soundHandler.m_120367_(new LoopSound((SoundEvent)ModSounds.ICE_STORM_LOOP.get(), entity));
            }
        }

    }

    @SubscribeEvent
    public static void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        Player player = Minecraft.m_91087_().f_91074_;
        float delta = Minecraft.m_91087_().m_91296_();
        if (player != null) {
            float ticksExistedDelta = (float)player.f_19797_ + delta;
            if (MainConfig.CameraShake.get() && !Minecraft.m_91087_().m_91104_()) {
                float shakeAmplitude = 0.0F;

                for(CameraShake cameraShake : player.f_19853_.m_45976_(CameraShake.class, player.m_20191_().m_82400_(20.0D))) {
                    if (cameraShake.m_20270_(player) < cameraShake.getRadius()) {
                        shakeAmplitude += cameraShake.getShakeAmount(player, delta);
                    }
                }

                if (shakeAmplitude > 1.0F) {
                    shakeAmplitude = 1.0F;
                }

                event.setPitch((float)((double)event.getPitch() + (double)shakeAmplitude * Math.cos((double)ticksExistedDelta * 3.0D + 2.0D) * 25.0D));
                event.setYaw((float)((double)event.getYaw() + (double)shakeAmplitude * Math.cos((double)ticksExistedDelta * 5.0D + 1.0D) * 25.0D));
                event.setRoll((float)((double)event.getRoll() + (double)shakeAmplitude * Math.cos((double)ticksExistedDelta * 4.0D) * 25.0D));
            }
        }

    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == Phase.START) {
            PARTIAL_TICK = event.renderTickTime;
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (SEHelper.hasCamera(event.player)) {
            event.player.m_19884_(0.0D, 0.0D);
            event.player.f_20900_ = 0.0F;
            event.player.f_20902_ = 0.0F;
            event.player.m_6862_(false);
        }

    }

    @SubscribeEvent
    public static void onInputInteract(InputEvent.InteractionKeyMappingTriggered event) {
        AbstractClientPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null && SEHelper.hasCamera(player) && (event.isAttack() || event.isPickBlock() || event.isUseItem())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity().f_19853_ instanceof ClientLevel) {
            Minecraft minecraft = Minecraft.m_91087_();
            SoundManager soundHandler = minecraft.m_91106_();
            if (WandUtil.getSpell(event.getEntity()) != null && event.getItem().m_41720_() instanceof IWand) {
                ISpell spells = WandUtil.getSpell(event.getEntity());
                if (spells != null) {
                    if (spells.loopSound(event.getEntity()) != null) {
                        soundHandler.m_120367_(new ItemLoopSound(spells.loopSound(event.getEntity()), event.getEntity()));
                    } else if (spells instanceof PrismaBeamSpell) {
                        soundHandler.m_120367_(new GuardianLaserSound(event.getEntity()));
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity.f_19853_ instanceof ClientLevel) {
            Minecraft minecraft = Minecraft.m_91087_();
            SoundManager soundHandler = minecraft.m_91106_();
            if (entity instanceof SquallGolem) {
                SquallGolem squallGolem = (SquallGolem)entity;
                if (squallGolem.noveltyTick == 1) {
                    soundHandler.m_120367_(new SummonNoveltySound(squallGolem, (SoundEvent)ModSounds.SQUALL_GOLEM_ALERT.get()));
                }
            }

            LivingEntity ownable = event.getEntity();
            if (ownable instanceof Leapleaf) {
                Leapleaf leapleaf = (Leapleaf)ownable;
                if (leapleaf.noveltyTick == 1) {
                    soundHandler.m_120367_(new SummonNoveltySound(leapleaf, (SoundEvent)ModSounds.LEAPLEAF_ALERT.get()));
                }
            }

            ownable = event.getEntity();
            if (ownable instanceof GuardianServant) {
                GuardianServant guardianServant = (GuardianServant)ownable;
                if (guardianServant.playAttackSound) {
                    soundHandler.m_120367_(new GuardianAttackSound(guardianServant));
                    guardianServant.playAttackSound = false;
                }
            }

            if (MainConfig.BossMusic.get() && entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (entity instanceof Wight) {
                    Wight wight = (Wight)entity;
                    if (!wight.m_21525_()) {
                        playPreBossMusic((SoundEvent)ModSounds.ENDERMAN_THEME_PRE.get(), (SoundEvent)ModSounds.ARENA_END.get(), wight, 0.75F, 1.0F, 64, true);
                    }
                }

                if (!(MiscCapHelper.getMobTarget(livingEntity) instanceof Player)) {
                    label79: {
                        Entity wight = MiscCapHelper.getMobTarget(livingEntity);
                        if (wight instanceof OwnableEntity) {
                            OwnableEntity ownable = (OwnableEntity)wight;
                            if (ownable.m_269323_() instanceof Player) {
                                break label79;
                            }
                        }

                        if (!entity.m_6095_().m_204039_(EntityTypes.GLOBAL_MUSIC_BOSS)) {
                            return;
                        }
                    }
                }

                if (entity instanceof Apostle) {
                    Apostle apostle = (Apostle)entity;
                    if (!apostle.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.APOSTLE_THEME.get(), (SoundEvent)ModSounds.APOSTLE_THEME_POST.get(), apostle);
                    }
                }

                if (entity instanceof Vizier) {
                    Vizier vizier = (Vizier)entity;
                    if (!vizier.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.VIZIER_THEME.get(), vizier);
                    }
                }

                if (entity instanceof HostileRedstoneMonstrosity) {
                    HostileRedstoneMonstrosity rm = (HostileRedstoneMonstrosity)entity;
                    if (!rm.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.RM_THEME.get(), (SoundEvent)ModSounds.BOSS_POST_2.get(), rm, 0.75F, 1.0F);
                    }
                }

                if (entity instanceof EnderKeeper) {
                    EnderKeeper enderKeeper = (EnderKeeper)entity;
                    if (!enderKeeper.m_21525_() && !enderKeeper.isIntro()) {
                        playBossMusic((SoundEvent)ModSounds.ENDER_KEEPER_THEME.get(), (SoundEvent)ModSounds.ENDER_KEEPER_THEME_POST.get(), enderKeeper, 0.75F, 0.825F);
                    }
                }

                if (entity instanceof HostileRedstoneGolem) {
                    HostileRedstoneGolem rm = (HostileRedstoneGolem)entity;
                    if (!rm.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.RG_THEME.get(), (SoundEvent)ModSounds.BOSS_POST_2.get(), rm, 0.75F, 1.0F);
                    }
                }

                if (entity instanceof Endersent) {
                    Endersent endersent = (Endersent)entity;
                    if (!endersent.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.ENDERSENT_THEME.get(), (SoundEvent)ModSounds.ARENA_END.get(), endersent, 0.75F, 1.0F);
                    }
                }

                if (entity instanceof Wight) {
                    Wight wight = (Wight)entity;
                    if (!wight.m_21525_()) {
                        playBossMusic((SoundEvent)ModSounds.ENDERMAN_THEME.get(), (SoundEvent)ModSounds.ARENA_END.get(), wight, 0.75F, 1.0F);
                    }
                }
            }
        }

    }

    public static void playPreBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob) {
        playPreBossMusic(soundEvent, postBossMusic, mob, 1.0F, 1.0F, 0);
    }

    public static void playPreBossMusic(SoundEvent soundEvent, Mob mob, int withinRange) {
        playPreBossMusic(soundEvent, (SoundEvent)ModSounds.BOSS_POST.get(), mob, 1.0F, 1.0F, withinRange);
    }

    public static void playPreBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob, int withinRange) {
        playPreBossMusic(soundEvent, postBossMusic, mob, 1.0F, 1.0F, withinRange);
    }

    public static void playPreBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob, float volume, float pitch, int withinRange) {
        playPreBossMusic(soundEvent, postBossMusic, mob, volume, pitch, withinRange, false);
    }

    public static void playPreBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob, float volume, float pitch, int withinRange, boolean mustSee) {
        if (MainConfig.BossMusic.get()) {
            Minecraft minecraft = Minecraft.m_91087_();
            boolean flag = true;
            if (mustSee) {
                Player player = Goety.PROXY.getPlayer();
                if (player != null && !MobUtil.hasVisualLineOfSight(player, mob)) {
                    flag = false;
                }
            }

            if (soundEvent != null && mob.m_6084_()) {
                if (PRE_BOSS_MUSIC == null && flag) {
                    PRE_BOSS_MUSIC = new PreBossLoopMusic(soundEvent, postBossMusic, mob, volume, pitch, withinRange, mustSee);
                }
            } else {
                PRE_BOSS_MUSIC = null;
            }

            if (PRE_BOSS_MUSIC != null && !minecraft.m_91106_().m_120403_(PRE_BOSS_MUSIC)) {
                Minecraft.m_91087_().m_91106_().m_120367_(PRE_BOSS_MUSIC);
            }
        }

    }

    public static void playBossMusic(SoundEvent soundEvent, Mob mob) {
        playBossMusic(soundEvent, mob, 1.0F, 1.0F);
    }

    public static void playBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob) {
        playBossMusic(soundEvent, postBossMusic, mob, 1.0F, 1.0F);
    }

    public static void playBossMusic(SoundEvent soundEvent, Mob mob, float volume, float pitch) {
        playBossMusic(soundEvent, (SoundEvent)ModSounds.BOSS_POST.get(), mob, volume, pitch);
    }

    public static void playBossMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mob, float volume, float pitch) {
        if (MainConfig.BossMusic.get()) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (soundEvent != null && mob.m_6084_()) {
                if (BOSS_MUSIC == null) {
                    BOSS_MUSIC = new BossLoopMusic(soundEvent, postBossMusic, mob, volume, pitch);
                }
            } else {
                BOSS_MUSIC = null;
            }

            if (BOSS_MUSIC != null && !minecraft.m_91106_().m_120403_(BOSS_MUSIC)) {
                Minecraft.m_91087_().m_91106_().m_120367_(BOSS_MUSIC);
            }
        }

    }

    @SubscribeEvent
    public static void renderGlove(RenderArmEvent event) {
        if (!event.isCanceled() && ItemConfig.FirstPersonGloves.get()) {
            Optional<SlotResult> slotResult = (Optional)CuriosApi.getCuriosInventory(event.getPlayer()).map((inv) -> inv.findFirstCurio((itemStack) -> itemStack.m_41720_() instanceof GloveItem)).orElse(Optional.empty());
            if (slotResult.isPresent()) {
                ItemStack itemStack = ((SlotResult)slotResult.get()).stack();
                if (((SlotResult)slotResult.get()).slotContext().visible()) {
                    WearRenderer renderer = WearRenderer.getRenderer(itemStack);
                    if (renderer != null) {
                        renderer.renderFirstPersonArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer(), event.getArm(), itemStack.m_41790_());
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public static void renderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        if (!player.m_5833_() && LichdomHelper.isInLichMode(player)) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.m_85836_();
            int i = OverlayTexture.m_118093_(OverlayTexture.m_118088_(0.0F), OverlayTexture.m_118096_(false));
            ResourceLocation texture = Goety.location("textures/entity/lich.png");
            LichModeModel<?> lichModeModel = new LichModeModel(Minecraft.m_91087_().m_167973_().m_171103_(ModModelLayer.LICH));
            if (event.getArm() == HumanoidArm.RIGHT) {
                lichModeModel.f_102811_.m_104301_(poseStack, event.getMultiBufferSource().m_6299_(RenderType.m_110473_(texture)), event.getPackedLight(), i);
                event.setCanceled(true);
            } else if (event.getArm() == HumanoidArm.LEFT) {
                lichModeModel.f_102812_.m_104301_(poseStack, event.getMultiBufferSource().m_6299_(RenderType.m_110473_(texture)), event.getPackedLight(), i);
                event.setCanceled(true);
            }

            poseStack.m_85849_();
        }

        if (event.getPlayer().m_21023_((MobEffect)GoetyEffects.SHADOW_WALK.get())) {
            if (event.getPlayer().m_21205_().m_41619_() && event.getArm() == event.getPlayer().m_5737_()) {
                event.setCanceled(true);
            } else if (event.getPlayer().m_21206_().m_41619_() && event.getArm() != event.getPlayer().m_5737_()) {
                event.setCanceled(true);
            }
        }

        if (SEHelper.hasCamera(event.getPlayer())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        AbstractClientPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null && SEHelper.hasCamera(player)) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.m_21023_((MobEffect)GoetyEffects.SHADOW_WALK.get())) {
            event.setCanceled(true);
        }

        if (player.m_20145_() && CuriosFinder.hasIllusionRobe(player)) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void renderLichHUD(RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.m_91087_();
        Player player = minecraft.f_91074_;
        if (LichdomHelper.isLich(player) && event.getOverlay().id() == VanillaGuiOverlay.FOOD_LEVEL.id()) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void renderArcaAmount(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.m_91087_();
        Player player = minecraft.f_91074_;
        if (player != null && event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            HitResult hitResult = minecraft.f_91077_;
            Font fontRenderer = minecraft.f_91062_;
            PoseStack poseStack = event.getGuiGraphics().m_280168_();
            if (minecraft.f_91073_ != null && hitResult instanceof BlockHitResult) {
                BlockHitResult blockRayTraceResult = (BlockHitResult)hitResult;
                BlockEntity blockEntity = minecraft.f_91073_.m_7702_(blockRayTraceResult.m_82425_());
                int width = minecraft.m_91268_().m_85445_();
                int height = minecraft.m_91268_().m_85446_();
                if (blockEntity instanceof ArcaBlockEntity) {
                    ArcaBlockEntity arcaTile = (ArcaBlockEntity)blockEntity;
                    if (player.m_6144_() || player.m_6047_()) {
                        if (arcaTile.getPlayer() == player && SEHelper.getSEActive(player)) {
                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            int SoulEnergy = SEHelper.getSESouls(player);
                            int SoulEnergyTotal = MainConfig.MaxArcaSouls.get();
                            String s = Component.m_237115_("tooltip.goety.blockSoul").getString() + SoulEnergy + "/" + SoulEnergyTotal;
                            int l = fontRenderer.m_92895_(s);
                            event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                        } else if (arcaTile.getPlayer() != null) {
                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 60), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.m_237115_("tooltip.goety.blockOwner").getString() + arcaTile.getPlayer().m_5446_().getString();
                            int l = fontRenderer.m_92895_(s);
                            event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                        }
                    }
                } else {
                    if (blockEntity instanceof IOwnedBlock) {
                        IOwnedBlock ownedBlock = (IOwnedBlock)blockEntity;
                        if (ownedBlock.getPlayer() != null && ownedBlock.screenView()) {
                            Player owner = ownedBlock.getPlayer();
                            if (owner != null) {
                                if (blockEntity instanceof ITrainingBlock) {
                                    ITrainingBlock trainingBlock = (ITrainingBlock)blockEntity;
                                    poseStack.m_85836_();
                                    poseStack.m_252880_((float)(width / 2), (float)(height - 58), 0.0F);
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                    String s = Component.m_237115_("tooltip.goety.blockOwner").getString() + owner.m_5446_().getString();
                                    int l = fontRenderer.m_92895_(s);
                                    event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                                    RenderSystem.disableBlend();
                                    poseStack.m_85849_();
                                    if (owner == player) {
                                        if (trainingBlock.reachedLimit()) {
                                            poseStack.m_85836_();
                                            poseStack.m_252880_((float)(width / 2), (float)(height - 116), 0.0F);
                                            RenderSystem.enableBlend();
                                            RenderSystem.defaultBlendFunc();
                                            String s0 = Component.m_237115_("info.goety.summon.limit").getString();
                                            int l0 = fontRenderer.m_92895_(s0);
                                            event.getGuiGraphics().m_280488_(fontRenderer, s0, -l0 / 2, -4, 16777215);
                                            RenderSystem.disableBlend();
                                            poseStack.m_85849_();
                                        }

                                        poseStack.m_85836_();
                                        poseStack.m_252880_((float)(width / 2), (float)(height - 100), 0.0F);
                                        RenderSystem.enableBlend();
                                        RenderSystem.defaultBlendFunc();
                                        String mode = Component.m_237115_("tooltip.goety.blockGuard").getString();
                                        if (!trainingBlock.isGuarding()) {
                                            mode = Component.m_237115_("tooltip.goety.blockFollow").getString();
                                        }

                                        int length = fontRenderer.m_92895_(mode);
                                        event.getGuiGraphics().m_280488_(fontRenderer, mode, -length / 2, -4, 16777215);
                                        RenderSystem.disableBlend();
                                        poseStack.m_85849_();
                                        if (trainingBlock.isSensorSensitive()) {
                                            poseStack.m_85836_();
                                            poseStack.m_252880_((float)(width / 2), (float)(height - 90), 0.0F);
                                            RenderSystem.enableBlend();
                                            RenderSystem.defaultBlendFunc();
                                            String s0 = Component.m_237115_("tooltip.goety.blockSense").getString();
                                            int l0 = fontRenderer.m_92895_(s0);
                                            event.getGuiGraphics().m_280488_(fontRenderer, s0, -l0 / 2, -4, 16777215);
                                            RenderSystem.disableBlend();
                                            poseStack.m_85849_();
                                        }

                                        if (trainingBlock.isGrounding()) {
                                            poseStack.m_85836_();
                                            poseStack.m_252880_((float)(width / 2), (float)(height - 46), 0.0F);
                                            RenderSystem.enableBlend();
                                            RenderSystem.defaultBlendFunc();
                                            String s0 = Component.m_237115_("tooltip.goety.blockGrounded").getString();
                                            int l0 = fontRenderer.m_92895_(s0);
                                            event.getGuiGraphics().m_280488_(fontRenderer, s0, -l0 / 2, -4, 16777215);
                                            RenderSystem.disableBlend();
                                            poseStack.m_85849_();
                                        }

                                        poseStack.m_85836_();
                                        poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                                        RenderSystem.enableBlend();
                                        RenderSystem.defaultBlendFunc();
                                        String s1 = Component.m_237115_("tooltip.goety.blockTrain").getString() + trainingBlock.amountTrainLeft() + "/" + trainingBlock.maxTrainAmount() + " " + trainingBlock.getTrainMob().m_20676_().getString();
                                        int l1 = fontRenderer.m_92895_(s1);
                                        event.getGuiGraphics().m_280488_(fontRenderer, s1, -l1 / 2, -4, 16777215);
                                        RenderSystem.disableBlend();
                                        poseStack.m_85849_();
                                        poseStack.m_85836_();
                                        int train = 64;
                                        train = (int)((double)train * ((double)trainingBlock.getTrainingTime() / (double)trainingBlock.getMaxTrainTime()));
                                        event.getGuiGraphics().m_280163_(Goety.location("textures/gui/train_bar.png"), (width - 64) / 2, height - 86, 0.0F, 0.0F, 64, 16, 64, 32);
                                        event.getGuiGraphics().m_280163_(Goety.location("textures/gui/train_bar.png"), (width - 64) / 2, height - 86, 0.0F, 16.0F, train, 16, 64, 32);
                                        poseStack.m_85849_();
                                        return;
                                    }

                                    return;
                                } else if (blockEntity instanceof IBarrack) {
                                    IBarrack barrack = (IBarrack)blockEntity;
                                    poseStack.m_85836_();
                                    poseStack.m_252880_((float)(width / 2), (float)(height - 58), 0.0F);
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                    String s = Component.m_237115_("tooltip.goety.blockOwner").getString() + owner.m_5446_().getString();
                                    int l = fontRenderer.m_92895_(s);
                                    event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                                    RenderSystem.disableBlend();
                                    poseStack.m_85849_();
                                    poseStack.m_85836_();
                                    poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                    String s1 = Component.m_237115_("tooltip.goety.blockTrainType").getString() + Component.m_237115_(barrack.getCurrentMob()).getString();
                                    int l1 = fontRenderer.m_92895_(s1);
                                    event.getGuiGraphics().m_280488_(fontRenderer, s1, -l1 / 2, -4, 16777215);
                                    RenderSystem.disableBlend();
                                    poseStack.m_85849_();
                                    poseStack.m_85836_();
                                    poseStack.m_252880_((float)(width / 2), (float)(height - 78), 0.0F);
                                    RenderSystem.enableBlend();
                                    RenderSystem.defaultBlendFunc();
                                    String s2 = Component.m_237115_("tooltip.goety.brew.capacity").getString() + barrack.getCurrentAmount() + "/" + barrack.trainLimit();
                                    int l2 = fontRenderer.m_92895_(s2);
                                    event.getGuiGraphics().m_280488_(fontRenderer, s2, -l2 / 2, -4, 16777215);
                                    RenderSystem.disableBlend();
                                    poseStack.m_85849_();
                                    return;
                                } else {
                                    if (blockEntity instanceof OminousIdolBlockEntity) {
                                        OminousIdolBlockEntity idol = (OminousIdolBlockEntity)blockEntity;
                                        poseStack.m_85836_();
                                        poseStack.m_252880_((float)(width / 2), (float)(height - 58), 0.0F);
                                        RenderSystem.enableBlend();
                                        RenderSystem.defaultBlendFunc();
                                        String s = Component.m_237115_("tooltip.goety.blockOwner").getString() + owner.m_5446_().getString();
                                        int l = fontRenderer.m_92895_(s);
                                        event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                                        RenderSystem.disableBlend();
                                        poseStack.m_85849_();
                                        poseStack.m_85836_();
                                        poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                                        RenderSystem.enableBlend();
                                        RenderSystem.defaultBlendFunc();
                                        String s2 = Component.m_237115_("tooltip.goety.idol.count").getString() + idol.getClientCount() + "/" + MainConfig.OminousIdolLimit.get();
                                        int l2 = fontRenderer.m_92895_(s2);
                                        event.getGuiGraphics().m_280488_(fontRenderer, s2, -l2 / 2, -4, 16777215);
                                        RenderSystem.disableBlend();
                                        poseStack.m_85849_();
                                    } else if ((player.m_6144_() || player.m_6047_()) && ownedBlock.getPlayer() != null) {
                                        poseStack.m_85836_();
                                        poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                                        RenderSystem.enableBlend();
                                        RenderSystem.defaultBlendFunc();
                                        String s = Component.m_237115_("tooltip.goety.blockOwner").getString() + owner.m_5446_().getString();
                                        int l = fontRenderer.m_92895_(s);
                                        event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                                        RenderSystem.disableBlend();
                                        poseStack.m_85849_();
                                        return;
                                    }

                                    return;
                                }
                            }

                            return;
                        }
                    }

                    if (blockEntity instanceof CursedCageBlockEntity) {
                        CursedCageBlockEntity cageBlockEntity = (CursedCageBlockEntity)blockEntity;
                        if (player.m_6144_() || player.m_6047_() && !cageBlockEntity.getItem().m_41619_()) {
                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.m_237115_("tooltip.goety.blockSoul").getString() + cageBlockEntity.getSouls();
                            int l = fontRenderer.m_92895_(s);
                            event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                        }
                    } else if (blockEntity instanceof BrewCauldronBlockEntity) {
                        BrewCauldronBlockEntity cauldronBlock = (BrewCauldronBlockEntity)blockEntity;
                        if (player.m_6144_() || player.m_6047_()) {
                            if (cauldronBlock.mode == Mode.BREWING) {
                                poseStack.m_85836_();
                                poseStack.m_252880_((float)(width / 2), (float)(height - 60), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s1 = Component.m_237115_("tooltip.goety.brew.capacity").getString() + cauldronBlock.getCapacityUsed() + "/" + cauldronBlock.getCapacity();
                                int l2 = fontRenderer.m_92895_(s1);
                                event.getGuiGraphics().m_280488_(fontRenderer, s1, -l2 / 2, -4, 16777215);
                                RenderSystem.disableBlend();
                                poseStack.m_85849_();
                            } else if (cauldronBlock.mode == Mode.CRAFTING || cauldronBlock.mode == Mode.CRAFTED) {
                                poseStack.m_85836_();
                                poseStack.m_252880_((float)(width / 2), (float)(height - 60), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s1 = Component.m_237115_("tooltip.goety.crafting").getString() + Component.m_237115_(cauldronBlock.getCraftedItem().m_41778_()).getString();
                                int l2 = fontRenderer.m_92895_(s1);
                                event.getGuiGraphics().m_280488_(fontRenderer, s1, -l2 / 2, -4, 16777215);
                                RenderSystem.disableBlend();
                                poseStack.m_85849_();
                            }

                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.m_237115_("tooltip.goety.blockSoulCost").getString() + (cauldronBlock.getBrewCost() - cauldronBlock.soulTime);
                            int l = fontRenderer.m_92895_(s);
                            event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                        }
                    } else if (blockEntity instanceof IWaystoneBlock) {
                        IWaystoneBlock waystoneBlock = (IWaystoneBlock)blockEntity;
                        GlobalPos globalPos = waystoneBlock.getPosition();
                        if ((player.m_6144_() || player.m_6047_()) && globalPos != null) {
                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 60), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            BlockPos blockPos = globalPos.m_122646_();
                            String s1 = Component.m_237110_("tooltip.goety.arcaCoords", new Object[]{blockPos.m_123341_(), blockPos.m_123342_(), blockPos.m_123343_()}).getString();
                            int l2 = fontRenderer.m_92895_(s1);
                            event.getGuiGraphics().m_280488_(fontRenderer, s1, -l2 / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                            poseStack.m_85836_();
                            poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            String s = Component.m_237110_("tooltip.goety.arcaDimension", new Object[]{globalPos.m_122640_().m_135782_().toString()}).getString();
                            int l = fontRenderer.m_92895_(s);
                            event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                            RenderSystem.disableBlend();
                            poseStack.m_85849_();
                            if (waystoneBlock.getSoulCost() > 0) {
                                poseStack.m_85836_();
                                poseStack.m_252880_((float)(width / 2), (float)(height - 76), 0.0F);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                String s0 = Component.m_237115_("tooltip.goety.blockSoulCost").getString() + waystoneBlock.getSoulCost();
                                int l0 = fontRenderer.m_92895_(s0);
                                event.getGuiGraphics().m_280488_(fontRenderer, s0, -l0 / 2, -4, 16777215);
                                RenderSystem.disableBlend();
                                poseStack.m_85849_();
                            }
                        }
                    } else if (blockEntity instanceof SculpturedStatueBlockEntity) {
                        SculpturedStatueBlockEntity statueBlock = (SculpturedStatueBlockEntity)blockEntity;
                        poseStack.m_85836_();
                        poseStack.m_252880_((float)(width / 2), (float)(height - 68), 0.0F);
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        String s = statueBlock.m_58900_().m_60734_().m_49954_().getString();
                        int l = fontRenderer.m_92895_(s);
                        event.getGuiGraphics().m_280488_(fontRenderer, s, -l / 2, -4, 16777215);
                        RenderSystem.disableBlend();
                        poseStack.m_85849_();
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void RenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft minecraft = Minecraft.m_91087_();
            Player player = minecraft.f_91074_;
            Level level = minecraft.f_91073_;
            if (level != null) {
                List<AbstractClientPlayer> players = minecraft.f_91073_.m_6907_();
                if (player != null) {
                    Level world = player.f_19853_;
                    ItemStack stack = player.m_21205_();
                    Map<BlockPos, ColorUtil> renderCubes = new HashMap();
                    if (stack.m_41720_() instanceof WaystoneItem && stack.m_41783_() != null) {
                        GlobalPos loc = WaystoneItem.getPosition(stack);
                        if (loc != null && loc.m_122640_() == world.m_46472_()) {
                            renderCubes.put(loc.m_122646_(), CUBE_COLOR);
                        }
                    }

                    if (!renderCubes.keySet().isEmpty()) {
                        PoseStack matrix = event.getPoseStack();
                        Vec3 view = Minecraft.m_91087_().f_91063_.m_109153_().m_90583_();
                        RenderBlockUtils.renderColourCubes(matrix, view, renderCubes, 1.0F, 1.0F);
                    }

                    for(Player player1 : players) {
                        if (!(player1.m_20280_(player) > 500.0D) && player1.m_6117_()) {
                            if (WandUtil.getSpell(player1) instanceof BurrowingSpell) {
                                BurrowingLaserRenderer.renderLaser(event, player1, Minecraft.m_91087_().m_91296_());
                            } else if (WandUtil.getSpell(player1) instanceof PrismaBeamSpell) {
                                PrismaBeamRenderer.renderLaser(event, player1, Minecraft.m_91087_().m_91296_());
                            } else if (WandUtil.getSpell(player1) instanceof WaterJetSpell) {
                                WaterJetRenderer.renderWaterJet(event, player1, Minecraft.m_91087_().m_91296_());
                            }
                        }
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public static void RenderHealthBarPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id() == VanillaGuiOverlay.PLAYER_HEALTH.id()) {
            Minecraft minecraft = Minecraft.m_91087_();
            Player player = minecraft.f_91074_;
            if (player != null) {
                Gui var4 = minecraft.f_91065_;
                if (var4 instanceof ForgeGui) {
                    ForgeGui gui = (ForgeGui)var4;
                    if (!minecraft.f_91066_.f_92062_ && gui.shouldDrawSurvivalElements() && (player.m_21023_((MobEffect)GoetyEffects.SPASMS.get()) || player.m_21023_((MobEffect)GoetyEffects.CURSED.get()) || player.m_21023_((MobEffect)GoetyEffects.ACID_VENOM.get()) || player.m_21023_((MobEffect)GoetyEffects.NECROSIS.get()))) {
                        setHearts(event);
                    }
                }

            }
        }
    }

    private static void setHearts(RenderGuiOverlayEvent.Pre event) {
        Player player = Minecraft.m_91087_().f_91074_;
        Minecraft mc = Minecraft.m_91087_();
        if (player != null) {
            ForgeGui gui = (ForgeGui)mc.f_91065_;
            GuiGraphics stack = event.getGuiGraphics();
            gui.setupOverlayRenderState(true, false);
            int width = event.getWindow().m_85445_();
            int height = event.getWindow().m_85446_();
            event.setCanceled(true);
            RenderSystem.setShaderTexture(0, CUSTOM_HEARTS);
            RenderSystem.enableBlend();
            int health = Mth.m_14167_(player.m_21223_());
            int tickCount = gui.m_93079_();
            boolean highlight = healthBlinkTime > (long)tickCount && (healthBlinkTime - (long)tickCount) / 3L % 2L == 1L;
            if (health < lastHealth && player.f_19802_ > 0) {
                lastHealthTime = Util.m_137550_();
                healthBlinkTime = (long)(tickCount + 20);
            } else if (health > lastHealth && player.f_19802_ > 0) {
                lastHealthTime = Util.m_137550_();
                healthBlinkTime = (long)(tickCount + 10);
            }

            if (Util.m_137550_() - lastHealthTime > 1000L) {
                lastHealth = health;
                displayHealth = health;
                lastHealthTime = Util.m_137550_();
            }

            lastHealth = health;
            int healthLast = displayHealth;
            float healthMax = (float)player.m_21133_(Attributes.f_22276_);
            int absorption = Mth.m_14167_(player.m_6103_());
            int healthRows = Mth.m_14167_((healthMax + (float)absorption) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);
            Random random = new Random();
            random.setSeed((long)tickCount * 312871L);
            int left = width / 2 - 91;
            int top = height - gui.leftHeight;
            gui.leftHeight += healthRows * rowHeight;
            if (rowHeight != 10) {
                gui.leftHeight += 10 - rowHeight;
            }

            int regen = -1;
            if (player.m_21023_(MobEffects.f_19605_)) {
                regen = tickCount % Mth.m_14167_(healthMax + 5.0F);
            }

            int TOP = player.m_9236_().m_6106_().m_5466_() ? 9 : 0;
            if (highlight) {
                TOP = player.m_9236_().m_6106_().m_5466_() ? 27 : 18;
            }

            int BACKGROUND = highlight ? 25 : 16;
            int heartX = 0;
            if (player.m_21023_((MobEffect)GoetyEffects.CURSED.get())) {
                heartX = 52;
            } else if (player.m_21023_((MobEffect)GoetyEffects.ACID_VENOM.get())) {
                heartX = 70;
            } else if (player.m_21023_((MobEffect)GoetyEffects.SPASMS.get())) {
                heartX = 34;
            } else if (player.m_21023_((MobEffect)GoetyEffects.NECROSIS.get())) {
                heartX = 88;
            }

            float absorptionRemaining = (float)absorption;

            for(int i = Mth.m_14167_((healthMax + (float)absorption) / 2.0F) - 1; i >= 0; --i) {
                int row = Mth.m_14167_((float)(i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += random.nextInt(2);
                }

                if (i == regen) {
                    y -= 2;
                }

                stack.m_280218_(CUSTOM_HEARTS, x, y, BACKGROUND, 0, 9, 9);
                if (highlight) {
                    if (i * 2 + 1 < healthLast) {
                        stack.m_280218_(CUSTOM_HEARTS, x, y, heartX, TOP, 9, 9);
                    } else if (i * 2 + 1 == healthLast) {
                        stack.m_280218_(CUSTOM_HEARTS, x, y, heartX + 9, TOP, 9, 9);
                    }
                }

                if (absorptionRemaining > 0.0F) {
                    if (absorptionRemaining == (float)absorption && (float)absorption % 2.0F == 1.0F) {
                        stack.m_280218_(CUSTOM_HEARTS, x, y, heartX + 9, TOP, 9, 9);
                        --absorptionRemaining;
                    } else {
                        stack.m_280218_(CUSTOM_HEARTS, x, y, heartX, TOP, 9, 9);
                        absorptionRemaining -= 2.0F;
                    }
                } else if (i * 2 + 1 < health) {
                    stack.m_280218_(CUSTOM_HEARTS, x, y, heartX, TOP, 9, 9);
                } else if (i * 2 + 1 == health) {
                    stack.m_280218_(CUSTOM_HEARTS, x, y, heartX + 9, TOP, 9, 9);
                }
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderTexture(0, CUSTOM_HEARTS);
        }
    }

    @SubscribeEvent
    public static void TickEvents(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.START) {
            CustomItemsRenderer.incrementTick();
        } else {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91074_ != null) {
                Player player = minecraft.f_91074_;
                Wight wight = Wight.findWight(player);
                if (wight != null) {
                    if (MobUtil.isPlayerLookingTowards(player, ((Integer)minecraft.f_91066_.m_231837_().m_231551_()).floatValue(), wight) && MobUtil.hasVisualLineOfSight(player, wight)) {
                        ++wight.lookTime;
                        if (wight.lookTime >= MathHelper.secondsToTicks(3) && wight.lookTime % 20 == 0 && wight.m_217043_().m_188503_(8) == 0) {
                            wight.lookTime = 0;
                            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CTargetPlayerPacket(wight));
                        }
                    } else if (wight.lookTime > 0) {
                        --wight.lookTime;
                    }
                }

                if (minecraft.f_91066_.f_92089_.m_90857_() && !prevJumpBindState && !player.m_20069_() && SEHelper.getTicksInAir(player) > 2 && !player.m_7500_() && !player.m_5833_() && !player.m_20159_()) {
                    ModNetwork.sendToServer(new CMultiJumpPacket());
                    SEHelper.doubleJump(player);
                }

                prevJumpBindState = minecraft.f_91066_.f_92089_.m_90857_();
            }
        }

    }

    @SubscribeEvent
    public static void TargetMonocleEvents(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (event.phase == Phase.START) {
            boolean leave = false;
            if (minecraft.f_91074_ != null) {
                if (CuriosFinder.hasCurio(minecraft.f_91074_, (Item)ModItems.TARGETING_MONOCLE.get())) {
                    ItemStack itemStack = CuriosFinder.findCurio(minecraft.f_91074_, (Item)ModItems.TARGETING_MONOCLE.get());
                    if (TargetingMonocleItem.isActive(itemStack)) {
                        if (!lockedOn) {
                            attemptEnterLockOn(Minecraft.m_91087_().f_91074_);
                        }

                        if (!minecraft.f_91074_.m_6047_() && ModKeybindings.useCurios() != null) {
                            while(ModKeybindings.useCurios().m_90859_()) {
                                tabToNextEnemy(Minecraft.m_91087_().f_91074_);
                            }
                        }
                    } else {
                        leave = lockedOn;
                    }
                } else {
                    leave = lockedOn;
                }
            } else {
                leave = lockedOn;
            }

            if (leave) {
                leaveLockOn();
            }

            tickLockedOn();
        }

    }

    public static boolean handleKeyPress(Player player) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (player != null && !minecraft.m_91104_() && target != null) {
            Vec3 targetPos = target.m_20182_().m_82520_(0.0D, (double)target.m_20206_() / 2.0D, 0.0D);
            Vec3 targetVec = targetPos.m_82546_(player.m_20182_().m_82520_(0.0D, (double)player.m_20192_(), 0.0D)).m_82541_();
            double targetAngleX = Mth.m_14175_(Math.atan2(-targetVec.f_82479_, targetVec.f_82481_) * 180.0D / Math.PI);
            double targetAngleY = Math.atan2(targetVec.f_82480_, targetVec.m_165924_()) * 180.0D / Math.PI;
            double xRot = (double)Mth.m_14177_(player.m_146909_());
            double yRot = (double)Mth.m_14177_(player.m_146908_());
            double toTurnX = Mth.m_14175_(yRot - targetAngleX);
            double toTurnY = Mth.m_14175_(xRot + targetAngleY);
            player.m_19884_(-toTurnX, -toTurnY);
            return true;
        } else {
            return false;
        }
    }

    @SubscribeEvent
    public static void logOff(ClientPlayerNetworkEvent.LoggingOut event) {
        leaveLockOn();
    }

    @SubscribeEvent
    public static void onDying(LivingDeathEvent event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ != null && event.getEntity() == minecraft.f_91074_) {
            leaveLockOn();
        }

    }

    private static void attemptEnterLockOn(Player player) {
        tabToNextEnemy(player);
        if (target != null) {
            lockedOn = true;
        }

    }

    private static void tickLockedOn() {
        targetList.removeIf((livingEntity) -> !livingEntity.m_6084_());
        if (target != null && !target.m_6084_()) {
            target = null;
            lockedOn = false;
        }

    }

    private static boolean isFriendly(LivingEntity entity) {
        Player player = Minecraft.m_91087_().f_91074_;
        return player != null ? MobUtil.areAllies(player, entity) : false;
    }

    public static Entity findNearby(Player player) {
        int range = 16;
        TargetingConditions selector = TargetingConditions.m_148352_().m_26883_((double)range).m_26888_(ENTITY_PREDICATE);
        List<LivingEntity> entities = player.m_9236_().m_45971_(LivingEntity.class, selector, player, player.m_20191_().m_82400_((double)range)).stream().filter(player::m_142582_).toList();
        if (lockedOn) {
            ++cycle;

            for(LivingEntity entity : entities) {
                if (!targetList.contains(entity)) {
                    targetList.add(entity);
                    return entity;
                }
            }

            if (cycle >= targetList.size()) {
                cycle = 0;
            }

            return (Entity)targetList.get(cycle);
        } else if (!entities.isEmpty()) {
            LivingEntity first = (LivingEntity)entities.get(0);
            targetList.add(first);
            return (Entity)entities.get(0);
        } else {
            return null;
        }
    }

    private static void tabToNextEnemy(Player player) {
        if (target != findNearby(player)) {
            player.m_5496_((SoundEvent)ModSounds.TOCK.get(), 1.0F, 1.0F);
        }

        target = findNearby(player);
    }

    private static void leaveLockOn() {
        target = null;
        lockedOn = false;
        targetList.clear();
    }

    @SubscribeEvent
    public static void FogEvents(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ != null) {
            Player player = minecraft.f_91074_;
            Wight wight = Wight.findWight(player, EntitySelector.f_20402_::test);
            if (wight != null) {
                float f = minecraft.f_91063_.m_109152_();
                event.setNearPlaneDistance(f * 0.05F);
                event.setFarPlaneDistance(Math.min(f, 192.0F) * 0.5F);
                event.setCanceled(true);
            }
        }

    }

    public static void wipeOpen() {
        if (ModKeybindings.wandCircle() != null) {
            while(ModKeybindings.wandCircle().m_90859_()) {
            }
        }

        if (ModKeybindings.brewCircle() != null) {
            while(ModKeybindings.brewCircle().m_90859_()) {
            }
        }

    }

    @SubscribeEvent
    public static void handleKeys(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.START) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91080_ == null && ModKeybindings.wandCircle() != null && ModKeybindings.brewCircle() != null) {
                boolean toolMenuKeyIsDown = ModKeybindings.wandCircle().m_90857_() || ModKeybindings.brewCircle().m_90857_();
                boolean wandCircle = ModKeybindings.wandCircle().m_90857_();
                boolean brewCircle = ModKeybindings.brewCircle().m_90857_();
                if (toolMenuKeyIsDown && !toolMenuKeyWasDown) {
                    if (wandCircle) {
                        while(ModKeybindings.wandCircle().m_90859_()) {
                            if (minecraft.f_91080_ == null && minecraft.f_91074_ != null) {
                                ItemStack inHand = WandUtil.findWand(minecraft.f_91074_);
                                if (!inHand.m_41619_() && TotemFinder.canOpenWandCircle(minecraft.f_91074_)) {
                                    minecraft.m_91152_(new FocusRadialMenuScreen());
                                }
                            }
                        }
                    } else if (brewCircle) {
                        while(ModKeybindings.brewCircle().m_90859_()) {
                            if (minecraft.f_91080_ == null && minecraft.f_91074_ != null && CuriosFinder.hasBrewInBag(minecraft.f_91074_)) {
                                minecraft.m_91152_(new BrewRadialMenuScreen());
                            }
                        }
                    }
                }

                toolMenuKeyWasDown = toolMenuKeyIsDown;
            } else {
                toolMenuKeyWasDown = true;
            }

        }
    }

    public static boolean isKeyDown0(KeyMapping keybind) {
        if (keybind.m_90862_()) {
            return false;
        } else {
            boolean var10000;
            switch (1.$SwitchMap$com$mojang$blaze3d$platform$InputConstants$Type[keybind.getKey().m_84868_().ordinal()]) {
                case 1:
                    var10000 = InputConstants.m_84830_(Minecraft.m_91087_().m_91268_().m_85439_(), keybind.getKey().m_84873_());
                    break;
                case 2:
                    var10000 = GLFW.glfwGetMouseButton(Minecraft.m_91087_().m_91268_().m_85439_(), keybind.getKey().m_84873_()) == 1;
                    break;
                default:
                    var10000 = false;
            }

            return var10000;
        }
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if (keybind.m_90862_()) {
            return false;
        } else {
            return isKeyDown0(keybind) && keybind.getKeyConflictContext().isActive() && keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
        }
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        Input input = event.getInput();
        if (player instanceof LocalPlayer localPlayer) {
            if (MainConfig.WheelGuiMovement.get() && (Minecraft.m_91087_().f_91080_ instanceof FocusRadialMenuScreen || Minecraft.m_91087_().f_91080_ instanceof BrewRadialMenuScreen)) {
                Options settings = Minecraft.m_91087_().f_91066_;
                input.f_108568_ = isKeyDown0(settings.f_92085_);
                input.f_108569_ = isKeyDown0(settings.f_92087_);
                input.f_108570_ = isKeyDown0(settings.f_92086_);
                input.f_108571_ = isKeyDown0(settings.f_92088_);
                input.f_108567_ = input.f_108568_ == input.f_108569_ ? 0.0F : (input.f_108568_ ? 1.0F : -1.0F);
                input.f_108566_ = input.f_108570_ == input.f_108571_ ? 0.0F : (input.f_108570_ ? 1.0F : -1.0F);
                input.f_108572_ = isKeyDown0(settings.f_92089_);
                input.f_108573_ = isKeyDown0(settings.f_92090_);
                if (localPlayer.m_108635_()) {
                    input.f_108566_ = (float)((double)input.f_108566_ * 0.3D);
                    input.f_108567_ = (float)((double)input.f_108567_ * 0.3D);
                }
            }

            if (SpellConfig.FullStopCast.get() && localPlayer.m_6117_() && !localPlayer.m_20159_() && MobUtil.isSpellCasting(localPlayer)) {
                input.f_108566_ = 0.0F;
                input.f_108567_ = 0.0F;
                input.f_108572_ = false;
            }
        }

    }

    @SubscribeEvent
    public static void KeyInputs(InputEvent.Key event) {
        Minecraft MINECRAFT = Minecraft.m_91087_();
        if (MainConfig.WheelGuiMovement.get() && (MINECRAFT.f_91080_ instanceof FocusRadialMenuScreen || MINECRAFT.f_91080_ instanceof BrewRadialMenuScreen)) {
            InputConstants.Key inputconstants$key = InputConstants.m_84827_(event.getKey(), event.getScanCode());
            if (event.getAction() == 0) {
                KeyMapping.m_90837_(inputconstants$key, false);
                if (event.getKey() == 292) {
                    MINECRAFT.f_91066_.f_92063_ = !MINECRAFT.f_91066_.f_92063_;
                    MINECRAFT.f_91066_.f_92064_ = MINECRAFT.f_91066_.f_92063_ && Screen.m_96638_();
                    MINECRAFT.f_91066_.f_92065_ = MINECRAFT.f_91066_.f_92063_ && Screen.m_96639_();
                }
            } else {
                if (event.getKey() == 293 && MINECRAFT.f_91063_ != null) {
                    MINECRAFT.f_91063_.m_109130_();
                }

                boolean flag3 = InputConstants.m_84830_(Minecraft.m_91087_().m_91268_().m_85439_(), 292);
                if (event.getKey() == 256) {
                    boolean flag2 = InputConstants.m_84830_(Minecraft.m_91087_().m_91268_().m_85439_(), 292);
                    MINECRAFT.m_91358_(flag2);
                }

                if (event.getKey() == 290) {
                    MINECRAFT.f_91066_.f_92062_ = !MINECRAFT.f_91066_.f_92062_;
                }

                if (flag3) {
                    KeyMapping.m_90837_(inputconstants$key, false);
                } else {
                    KeyMapping.m_90837_(inputconstants$key, true);
                    KeyMapping.m_90835_(inputconstants$key);
                }

                if (MINECRAFT.f_91066_.f_92064_ && event.getKey() >= 48 && event.getKey() <= 57) {
                    MINECRAFT.m_91111_(event.getKey() - 48);
                }
            }
        }

        if (ModKeybindings.keyBindings[0].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CWandKeyPacket());
        }

        if (ModKeybindings.keyBindings[2].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CBagKeyPacket());
        }

        if (ModKeybindings.keyBindings[3].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CWitchRobePacket());
        }

        if (ModKeybindings.keyBindings[4].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CStopAttackPacket());
        }

        if (ModKeybindings.keyBindings[5].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CMagnetPacket());
        }

        if (ModKeybindings.keyBindings[6].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CSetLichNightVisionMode());
            if (MINECRAFT.f_91074_ != null && MainConfig.LichNightVision.get() && LichdomHelper.isLich(MINECRAFT.f_91074_)) {
                MINECRAFT.f_91074_.m_216990_(SoundEvents.f_11859_);
            }
        }

        if (ModKeybindings.keyBindings[7].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CExtractPotionKeyPacket());
        }

        if (ModKeybindings.keyBindings[8].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CBrewBagKeyPacket());
        }

        if (ModKeybindings.keyBindings[10].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CRavagerRoarPacket());
        }

        if (ModKeybindings.keyBindings[11].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CAutoRideablePacket());
        }

        if (ModKeybindings.keyBindings[12].m_90857_() && MINECRAFT.m_91302_() && MINECRAFT.f_91074_ != null && LichdomHelper.isLich(MINECRAFT.f_91074_)) {
            LichdomHelper.setLichMode(MINECRAFT.f_91074_, !LichdomHelper.isInLichMode(MINECRAFT.f_91074_));
            if (!LichdomHelper.isInLichMode(MINECRAFT.f_91074_)) {
                MINECRAFT.f_91074_.m_216990_(SoundEvents.f_12616_);
            } else {
                if (MINECRAFT.f_91073_ != null) {
                    for(int i = 0; i < 5; ++i) {
                        double d0 = MINECRAFT.f_91073_.f_46441_.m_188583_() * 0.02D;
                        double d1 = MINECRAFT.f_91073_.f_46441_.m_188583_() * 0.02D;
                        double d2 = MINECRAFT.f_91073_.f_46441_.m_188583_() * 0.02D;
                        MINECRAFT.f_91073_.m_7106_(ParticleTypes.f_235898_, MINECRAFT.f_91074_.m_20208_(1.0D), MINECRAFT.f_91074_.m_20187_() + 1.0D, MINECRAFT.f_91074_.m_20262_(1.0D), d0, d1, d2);
                    }
                }

                MINECRAFT.f_91074_.m_5496_((SoundEvent)ModSounds.SOUL_EXPLODE.get(), 1.0F, 0.75F);
            }

            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CSetLichMode());
        }

        if (ModKeybindings.keyBindings[13].m_90857_() && MINECRAFT.m_91302_() && MINECRAFT.f_91074_ != null && LichdomHelper.isLich(MINECRAFT.f_91074_) && LichdomHelper.isInLichMode(MINECRAFT.f_91074_)) {
            MINECRAFT.f_91074_.f_19853_.m_7785_(MINECRAFT.f_91074_.m_20185_(), MINECRAFT.f_91074_.m_20186_(), MINECRAFT.f_91074_.m_20189_(), (SoundEvent)ModSounds.LICH_LAUGH.get(), MINECRAFT.f_91074_.m_5720_(), 2.0F, MINECRAFT.f_91074_.m_6100_(), false);
        }

        if (ModKeybindings.keyBindings[14].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CActivateCurioKeyPacket());
        }

        if (ModKeybindings.keyBindings[15].m_90857_() && MINECRAFT.m_91302_()) {
            ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CDismissServantsPacket());
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public static void InteractionKeyEvent(InputEvent.InteractionKeyMappingTriggered event) {
        Player player = Minecraft.m_91087_().f_91074_;
        if (player != null && event.isAttack()) {
            HitResult gameMode = Minecraft.m_91087_().f_91077_;
            if (gameMode instanceof EntityHitResult) {
                EntityHitResult result = (EntityHitResult)gameMode;
                Entity var7 = result.m_82443_();
                if (var7 instanceof IOwned) {
                    IOwned owned = (IOwned)var7;
                    if (owned.getTrueOwner() == player) {
                        MultiPlayerGameMode gameMode = Minecraft.m_91087_().f_91072_;
                        ClientPacketListener listener = Minecraft.m_91087_().m_91403_();
                        if (gameMode != null && listener != null) {
                            ItemStack stack = player.m_21205_();
                            if (stack.m_41720_().onLeftClickEntity(stack, player, result.m_82443_())) {
                                listener.m_104955_(ServerboundInteractPacket.m_179605_(result.m_82443_(), player.m_6144_()));
                                if (gameMode.m_105295_() != GameType.SPECTATOR) {
                                    player.m_5706_(result.m_82443_());
                                }

                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }

    }

    public <T extends LivingEntity> void followBodyRotations(T livingEntity, HumanoidModel<T> model) {
        EntityRenderer<? super T> render = Minecraft.m_91087_().m_91290_().m_114382_(livingEntity);
        if (render instanceof LivingEntityRenderer<T, EntityModel<T>> livingRenderer) {
            EntityModel<T> entityModel = livingRenderer.m_7200_();
            if (entityModel instanceof HumanoidModel<T> humanoidModel) {
                humanoidModel.m_102872_(model);
            }
        }

    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        CrusherServant.invalidateRecipeCache();
        CauldronSusStewRecipe.invalidateFlowerCache();
    }
}
