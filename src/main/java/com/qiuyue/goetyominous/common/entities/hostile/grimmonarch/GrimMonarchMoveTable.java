package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GrimMonarchMoveTable {
    public static final String IDLE = "idle";
    public static final String SPAWN = "spawn";
    public static final String SWEEP = "sweep";
    public static final String DASH = "dash";
    public static final String DOOM_DIVE_START = "doom_dive_start";
    public static final String DOOM_DIVE_WINDUP = "doom_dive_windup";
    public static final String DOOM_DIVE_RUSH = "doom_dive_rush";
    public static final String DOOM_DIVE_FINISH = "doom_dive_finish";
    public static final String DOOM_DIVE_LAND = "doom_dive_land";
    public static final String SUMMON = "summon";
    public static final String ECHO = "echo";
    public static final String ECHO_SPAWN = "echo_spawn";
    public static final String DEATH = "death";

    private static final Map<String, GrimMonarchMove> MOVES = build();

    private GrimMonarchMoveTable() {
    }

    public static GrimMonarchMove get(String id) {
        GrimMonarchMove move = MOVES.get(id);
        if (move == null) {
            throw new IllegalArgumentException(id);
        }
        return move;
    }

    private static Map<String, GrimMonarchMove> build() {
        Map<String, GrimMonarchMove> moves = new HashMap<>();
        moves.put(IDLE, move(IDLE, 0.0F, List.of(), List.of(), true));
        moves.put(SPAWN, move(SPAWN, 3.4667F, List.of(), List.of(), false));
        moves.put(DEATH, move(DEATH, 3.2F, List.of(), List.of(), false));

        moves.put(SWEEP, move(SWEEP, 2.8333F, List.of(
                phase(0.0F, 2.8333F,
                        List.of(hit(1.0F, 1.6F, 1.2D, GrimMonarchHitWindow.Kind.SWEEP, 5.0D)),
                        List.of(motion(1.0707F, 0.4757F, GrimMonarchMotion.Kind.BACKSTEP, 12.0D, 0.0D, 0.0D)))
        ), List.of(event("soul_ring", 0.5676F)), false));

        moves.put(DASH, move(DASH, 4.137F, List.of(
                phase(0.0F, 1.049F, List.of(), List.of()),
                phase(1.049F, 0.651F,
                        List.of(hit(1.049F, 1.70F, 1.8D, GrimMonarchHitWindow.Kind.PATH, 1.2D)),
                        List.of(motion(1.049F, 0.651F, GrimMonarchMotion.Kind.DASH, 14.0D, 1.1D, 0.0D))),
                phase(1.70F, 2.437F,
                        List.of(hit(2.0018F, 2.1718F, 1.5D, GrimMonarchHitWindow.Kind.CLAW, 0.0D)),
                        List.of())
        ), List.of(), false));

        moves.put(DOOM_DIVE_START, move(DOOM_DIVE_START, 5.3784F, List.of(
                phase(0.0F, 5.3784F,
                        List.of(
                                hit(2.8627F, 3.1833F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(3.1918F, 3.4740F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(3.7033F, 3.9124F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(4.0702F, 4.3208F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(4.4916F, 4.7216F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D)
                        ),
                        List.of(
                                flight(0.5096F, 0.914F, 1.86F, 2.4582F, 2.661F, 5.0D),
                                lunge(3.2073731F, 0.2935579F, 2000.0D),
                                lunge(3.7032666F, 0.15728426F, 2000.0D),
                                lunge(4.4617615F, 0.157284F, 2000.0D)
                        ))
        ), List.of(
                event("soul_ring", 0.030821135F),
                event("focus_off", 0.8439221F),
                event("focus_on", 1.9794018F),
                event("focus_off", 2.5908365F),
                event("focus_on", 3.5300064F),
                event("focus_off", 3.6846647F),
                event("focus_on", 3.9311495F)
        ), false));

        moves.put(DOOM_DIVE_WINDUP, move(DOOM_DIVE_WINDUP, 1.90245F, List.of(
                phase(0.0F, 1.90245F, List.of(), List.of())
        ), List.of(
                event("focus_on", 0.16937992F),
                event("focus_off", 0.48992722F)
        ), false));

        moves.put(DOOM_DIVE_RUSH, move(DOOM_DIVE_RUSH, 0.9318F, List.of(
                phase(0.0F, 0.9318F,
                        List.of(hit(0.0F, 0.9318F, 1.0D, GrimMonarchHitWindow.Kind.PATH, 1.2D)),
                        List.of(motion(0.0F, 0.9318F, GrimMonarchMotion.Kind.DASH, 18.0D, 1.32D, 0.0D)))
        ), List.of(), false));

        moves.put(DOOM_DIVE_FINISH, move(DOOM_DIVE_FINISH, 0.943948F, List.of(
                phase(0.0F, 0.943948F,
                        List.of(hit(0.267398F, 0.524798F, 1.0D, GrimMonarchHitWindow.Kind.CLAW, 0.0D)),
                        List.of())
        ), List.of(
                event("focus_off", 0.3141761F)
        ), false));

        moves.put(DOOM_DIVE_LAND, move(DOOM_DIVE_LAND, 7.2667F, List.of(
                phase(0.0F, 7.2667F,
                        List.of(
                                hit(2.5221508F, 2.860245F, 2.0D, GrimMonarchHitWindow.Kind.RADIAL, 4.5D),
                                hit(3.4246F, 3.5358F, 0.9D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(3.5358F, 3.8053F, 0.9D, GrimMonarchHitWindow.Kind.CLAW, 0.0D),
                                hit(4.1667F, 4.4206F, 0.9D, GrimMonarchHitWindow.Kind.CLAW, 0.0D)
                        ),
                        List.of(
                                sidestep(0.3101914F, 0.5711597F, 3000.0D),
                                flight(0.98975486F, 0.61186534F, 1.7F, 2.3069346F, 2.5221508F, 5.0D),
                                lunge(3.4567952F, 0.06858945F, 2000.0D),
                                lunge(3.6300757F, 0.11762881F, 2500.0D),
                                lunge(4.1951747F, 0.06858921F, 2200.0D)
                        ))
        ), List.of(
                event("focus_on", 0.6816154F),
                event("focus_off", 2.7846563F),
                event("focus_on", 3.365748F),
                event("focus_off", 3.5253522F),
                event("focus_on", 3.6307516F),
                event("focus_off", 3.865641F),
                event("focus_on", 4.219481F),
                event("calamity_ripple", 4.4206F),
                event("focus_off", 4.481473F)
        ), false));

        moves.put(SUMMON, move(SUMMON, 3.2F, List.of(
                phase(0.0F, 3.2F, List.of(), List.of())
        ), List.of(event("summon_wave", 1.0919F)), false));

        moves.put(ECHO, move(ECHO, 3.6411F, List.of(
                phase(0.0F, 3.6411F, List.of(), List.of())
        ), List.of(event("echo_spawn", 1.46F)), false));

        moves.put(ECHO_SPAWN, move(ECHO_SPAWN, 2.30937F, List.of(
                phase(0.0F, 2.30937F, List.of(), List.of())
        ), List.of(), false));

        return Map.copyOf(moves);
    }

    private static GrimMonarchMove move(String id, float length, List<GrimMonarchPhase> phases,
                                        List<GrimMonarchMoveEvent> events, boolean loop) {
        return new GrimMonarchMove(id, length, phases, events, loop);
    }

    private static GrimMonarchPhase phase(float start, float duration, List<GrimMonarchHitWindow> hits,
                                          List<GrimMonarchMotion> motions) {
        return new GrimMonarchPhase(start, duration, hits, motions);
    }

    private static GrimMonarchHitWindow hit(float start, float end, double damageMultiplier,
                                            GrimMonarchHitWindow.Kind kind, double radius) {
        return new GrimMonarchHitWindow(start, end, damageMultiplier, kind, radius);
    }

    private static GrimMonarchMotion motion(float start, float duration, GrimMonarchMotion.Kind kind,
                                            double distance, double speed, double height) {
        return new GrimMonarchMotion(start, duration, kind, distance, speed, height, 0.0F, 0.0F);
    }

    private static GrimMonarchMotion flight(float start, float riseTime, float markAt, float diveAt,
                                            float landAt, double height) {
        return new GrimMonarchMotion(start, landAt - start, GrimMonarchMotion.Kind.FLIGHT,
                riseTime, markAt, height, diveAt, landAt);
    }

    private static GrimMonarchMotion lunge(float start, float duration, double strength) {
        return new GrimMonarchMotion(start, duration, GrimMonarchMotion.Kind.LUNGE,
                strength * 0.01D * duration, 0.0D, 0.0D, 0.0F, 0.0F);
    }

    private static GrimMonarchMotion sidestep(float start, float duration, double strength) {
        return new GrimMonarchMotion(start, duration, GrimMonarchMotion.Kind.SIDESTEP,
                strength * 0.01D * duration, 0.0D, 0.0D, 0.0F, 0.0F);
    }

    private static GrimMonarchMoveEvent event(String key, float time) {
        return new GrimMonarchMoveEvent(key, time);
    }
}
