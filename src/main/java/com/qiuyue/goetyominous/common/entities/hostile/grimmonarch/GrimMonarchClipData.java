package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class GrimMonarchClipData {
    public static final String[] PART_NAMES = {
            "root", "hip", "left_leg", "right_leg", "body", "shoulders",
            "neck", "head", "left_arm", "left_claw", "right_arm", "right_claw"
    };
    private static final Map<String, Integer> PART_INDEX = Map.ofEntries(
            Map.entry("root", 0),
            Map.entry("hip", 1),
            Map.entry("left_leg", 2),
            Map.entry("right_leg", 3),
            Map.entry("body", 4),
            Map.entry("shoulders", 5),
            Map.entry("neck", 6),
            Map.entry("head", 7),
            Map.entry("left_arm", 8),
            Map.entry("left_claw", 9),
            Map.entry("right_arm", 10),
            Map.entry("right_claw", 11)
    );
    private static final Data DATA = load();

    private GrimMonarchClipData() {
    }

    private static Data load() {
        InputStream stream = GrimMonarchClipData.class
                .getResourceAsStream("/assets/goetyominous/grimmonarch/clips.json");
        if (stream == null) {
            throw new IllegalStateException("Missing GrimMonarch clips.json");
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Data data = new Gson().fromJson(reader, Data.class);
            if (data == null || data.clips == null) {
                throw new IllegalStateException("Invalid GrimMonarch clips.json");
            }
            return data;
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot load GrimMonarch clips", exception);
        }
    }

    public static float[][] sample(String name, float seconds) {
        Clip clip = DATA.clips.get(name);
        if (clip == null) {
            throw new IllegalArgumentException("Unknown GrimMonarch clip: " + name);
        }
        float time = clipTime(clip, seconds);
        float[][] pose = new float[PART_NAMES.length][6];
        for (Track track : clip.tracks) {
            Integer index = PART_INDEX.get(track.part);
            if (index == null) {
                continue;
            }
            float[] value = value(track, time);
            if ("position".equals(track.channel)) {
                pose[index][0] += value[0];
                pose[index][1] -= value[1];
                pose[index][2] += value[2];
            } else {
                float scale = (float) (Math.PI / 180.0);
                pose[index][3] += value[0] * scale;
                pose[index][4] += value[1] * scale;
                pose[index][5] += value[2] * scale;
            }
        }
        return pose;
    }

    private static float clipTime(Clip clip, float seconds) {
        if (clip.loop && clip.length > 0.0F) {
            return (float) Math.floorMod((long) (Math.max(0.0F, seconds) * 1000000.0F),
                    (long) (clip.length * 1000000.0F)) / 1000000.0F;
        }
        return Math.max(0.0F, Math.min(clip.length, seconds));
    }

    private static float[] value(Track track, float time) {
        float[][] keys = track.keys;
        if (keys.length == 0) {
            return new float[3];
        }
        if (keys.length == 1 || time <= keys[0][0]) {
            return new float[]{keys[0][1], keys[0][2], keys[0][3]};
        }
        if (time >= keys[keys.length - 1][0]) {
            float[] last = keys[keys.length - 1];
            return new float[]{last[1], last[2], last[3]};
        }
        int low = 0;
        int high = keys.length - 1;
        while (low < high) {
            int middle = low + high + 1 >>> 1;
            if (keys[middle][0] <= time) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        int next = Math.min(low + 1, keys.length - 1);
        float[] a = keys[low];
        float[] b = keys[next];
        float span = b[0] - a[0];
        float weight = span <= 1.0E-6F ? 0.0F : Math.max(0.0F, Math.min(1.0F, (time - a[0]) / span));
        if (smooth(track, low) && next != low) {
            float[] previous = keys[Math.max(0, low - 1)];
            float[] after = keys[Math.min(keys.length - 1, next + 1)];
            return new float[]{
                    catmullRom(previous[1], a[1], b[1], after[1], weight),
                    catmullRom(previous[2], a[2], b[2], after[2], weight),
                    catmullRom(previous[3], a[3], b[3], after[3], weight)
            };
        }
        return new float[]{
                a[1] + (b[1] - a[1]) * weight,
                a[2] + (b[2] - a[2]) * weight,
                a[3] + (b[3] - a[3]) * weight
        };
    }

    private static boolean smooth(Track track, int index) {
        if (track.smooth == null) {
            return false;
        }
        for (int value : track.smooth) {
            if (value == index) {
                return true;
            }
        }
        return false;
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * (2.0F * p1 + (-p0 + p2) * t
                + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t * t
                + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * t * t * t);
    }

    private static final class Data {
        private Map<String, Clip> clips;
    }

    private static final class Clip {
        private float length;
        private boolean loop;
        private Track[] tracks;
    }

    private static final class Track {
        private String part;
        private String channel;
        private float[][] keys;
        private int[] smooth;
    }
}
