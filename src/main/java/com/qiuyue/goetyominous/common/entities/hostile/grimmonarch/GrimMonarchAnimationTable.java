package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import java.util.List;
import java.util.Map;

public final class GrimMonarchAnimationTable {
    public record Sample(String clip, float time) {
    }

    private record Segment(float start, float end, String clip, float clipStart, float rate) {
    }

    private static final List<Segment> DASH_STRIKE = List.of(
            new Segment(0.0F, 1.0F, "sprint_slash", 0.0F, 0.5F),
            new Segment(1.0F, 2.0F, "sprint_slash", 0.5F, 1.0F),
            new Segment(2.0F, 2.6944444F, "dash_rest_loop", 0.0F, 1.2F),
            new Segment(2.6944444F, 3.8944444F, "sprint_slash_stop", 0.0F, 0.5F)
    );
    private static final List<Segment> DOOM_DIVE_START = List.of(
            new Segment(0.0F, 1.2F, "combo_start", 0.0F, 1.0F),
            new Segment(1.2F, 2.1666667F, "hover_loop", 0.0F, 1.0F),
            new Segment(2.1666667F, 2.6609678F, "combo_1_flying", 0.105699F, 1.0F),
            new Segment(2.6609678F, 3.384935F, "combo_2_slashing", 0.0F, 1.0F),
            new Segment(3.384935F, 4.984935F, "combo_3_multiple_attack", 0.0F, 1.0F),
            new Segment(4.984935F, 5.378402F, "combo_5_block", 0.0F, 1.0F)
    );
    private static final List<Segment> DOOM_DIVE_RUSH = List.of(
            new Segment(0.0F, 1.633333F, "lunge", 0.0F, 1.0F),
            new Segment(1.633333F, 1.90245F, "dash_rest_loop", 0.0F, 1.0F)
    );
    private static final List<Segment> DOOM_DIVE_LAND = List.of(
            new Segment(0.0F, 1.3F, "combo_starting_move", 0.0F, 1.0F),
            new Segment(1.3F, 2.1999998F, "hover_loop_2", 0.0F, 1.0F),
            new Segment(2.1999998F, 3.0666666F, "heel_stomp", 0.0F, 1.0F),
            new Segment(3.0666666F, 4.9333334F, "combo_4_spinning_slash", 0.0F, 1.0F),
            new Segment(4.9333334F, 7.266667F, "stop", 0.0F, 0.6F)
    );
    private static final Map<GrimMonarchAction, List<Segment>> SEGMENTS = Map.ofEntries(
            Map.entry(GrimMonarchAction.SPAWN,
                    List.of(new Segment(0.0F, 3.4666667F, "spawn", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.SWEEP,
                    List.of(new Segment(0.0F, 2.8333333F, "swing", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.DASH_WINDUP, DASH_STRIKE),
            Map.entry(GrimMonarchAction.DASH_RUN, DASH_STRIKE),
            Map.entry(GrimMonarchAction.DASH_FINISH, DASH_STRIKE),
            Map.entry(GrimMonarchAction.CALAMITY_ONE, DOOM_DIVE_START),
            Map.entry(GrimMonarchAction.CALAMITY_WINDUP, DOOM_DIVE_RUSH),
            Map.entry(GrimMonarchAction.CALAMITY_RUN, DOOM_DIVE_RUSH),
            Map.entry(GrimMonarchAction.CALAMITY_FINISH, DOOM_DIVE_RUSH),
            Map.entry(GrimMonarchAction.CALAMITY_THREE, DOOM_DIVE_LAND),
            Map.entry(GrimMonarchAction.SUMMON,
                    List.of(new Segment(0.0F, 3.2F, "skill", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.CLONE,
                    List.of(new Segment(0.0F, 3.641114F, "summon_echo", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.ECHO_SPAWN,
                    List.of(new Segment(0.0F, 2.30937F, "summon_echo", 1.49063F, 1.0F))),
            Map.entry(GrimMonarchAction.DEATH,
                    List.of(new Segment(0.0F, 3.2F, "die", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.SLICE_START,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_START_SECONDS,
                            "sprint_slash", 1.07F, 0.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIT_1,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIT_SECONDS,
                            "sprint_slash", 1.07F, 1.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIDE_SPRINT,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIDE_SECONDS,
                            "sprint_slash", 1.07F, 0.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIT_2,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIT_SECONDS,
                            "sprint_slash", 1.07F, 1.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIDE_NORMAL,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIDE_SECONDS,
                            "sprint_slash", 1.07F, 0.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIT_3,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIT_SECONDS,
                            "sprint_slash", 1.07F, 1.0F))),
            Map.entry(GrimMonarchAction.SLICE_HIDE_SWING,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_HIDE_SECONDS,
                            "swing", 0.0F, 0.0F))),
            Map.entry(GrimMonarchAction.SLICE_FINISH,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_FINISH_SECONDS,
                            "spawn", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.SLICE_SUMMON,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_SUMMON_SECONDS,
                            "skill", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.COUNTER_LEFT,
                    List.of(new Segment(0.0F, GrimMonarchConstants.COUNTER_EVADE_SECONDS,
                            "evade_left", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.COUNTER_RIGHT,
                    List.of(new Segment(0.0F, GrimMonarchConstants.COUNTER_EVADE_SECONDS,
                            "evade_right", 0.0F, 1.0F))),
            Map.entry(GrimMonarchAction.COUNTER_HIDE,
                    List.of(new Segment(0.0F, GrimMonarchConstants.COUNTER_HIDE_SECONDS,
                            "idle", 0.0F, 0.0F))),
            Map.entry(GrimMonarchAction.COUNTER_SLAM,
                    List.of(new Segment(0.0F, GrimMonarchConstants.SLICE_FINISH_SECONDS,
                            "spawn", 0.0F, 1.0F)))
    );

    private GrimMonarchAnimationTable() {
    }

    public static Sample sample(GrimMonarchAction action, float seconds) {
        List<Segment> segments = SEGMENTS.get(action);
        if (segments == null) {
            return new Sample("idle", seconds);
        }
        float time = montageSeconds(action, seconds);
        Segment chosen = segments.get(segments.size() - 1);
        for (Segment segment : segments) {
            if (time < segment.end()) {
                chosen = segment;
                break;
            }
        }
        float clipTime = chosen.clipStart() + Math.max(0.0F, time - chosen.start()) * chosen.rate();
        return new Sample(chosen.clip(), clipTime);
    }

    private static float montageSeconds(GrimMonarchAction action, float seconds) {
        return switch (action) {
            case DASH_RUN -> 1.049F + seconds % 0.38F;
            case DASH_FINISH -> 1.457232F + seconds;
            case CALAMITY_RUN -> 0.700531F + seconds % 0.22F;
            case CALAMITY_FINISH -> 0.958502F + seconds;
            default -> seconds;
        };
    }
}
