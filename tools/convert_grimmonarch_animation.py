import json
import re
import sys
from pathlib import Path

SOURCE = Path("tmp_omen_decompile/help/GrimMonarchAnimation.java")
TARGET = Path("src/main/resources/assets/goetyominous/grimmonarch/clips.json")

PARTS = {
    "整体": "hip",
    "左腿": "left_leg",
    "右腿": "right_leg",
    "上半身": "body",
    "上半身2": "shoulders",
    "头": "neck",
    "脑袋": "head",
    "左手": "left_arm",
    "左爪": "left_claw",
    "右手": "right_arm",
    "右爪": "right_claw",
}

CLIP_IDS = {
    "spawn": "spawn",
    "idle": "idle",
    "walk": "walk",
    "evade to right": "evade_right",
    "evade to left": "evade_left",
    "normal attack": "normal_attack",
    "swing": "swing",
    "combo start": "combo_start",
    "combo starting move": "combo_starting_move",
    "combo 1 flying": "combo_1_flying",
    "combo 2 slashing": "combo_2_slashing",
    "combo 3 multiple attack": "combo_3_multiple_attack",
    "combo 4 spinning slash": "combo_4_spinning_slash",
    "combo 5 block": "combo_5_block",
    "stop": "stop",
    "sprint slash": "sprint_slash",
    "lunge": "lunge",
    "sprint slash stop": "sprint_slash_stop",
    "skill": "skill",
    "summon echo": "summon_echo",
    "die": "die",
}

TARGETS = {"ROTATION": "rotation", "POSITION": "position"}

HEADER = re.compile(
    r"public static final AnimationDefinition ([\w ]+?) = AnimationDefinition\.Builder\.withLength\(([\d.]+)F\)(\.looping\(\))?"
)
CHANNEL = re.compile(r'addAnimation\("([^"]+)", new AnimationChannel\(AnimationChannel\.Targets\.([A-Z]+),')
KEY = re.compile(
    r"new Keyframe\(([\d.]+)F, KeyframeAnimations\.(?:degreeVec|posVec)\(([-\d.]+)F, ([-\d.]+)F, ([-\d.]+)F\), "
    r"AnimationChannel\.Interpolations\.(\w+)\)"
)


def round4(value):
    value = round(value, 5)
    return 0.0 if value == -0.0 else value


def parse(source):
    heads = list(HEADER.finditer(source))
    clips = {}
    for index, head in enumerate(heads):
        name = head.group(1).strip()
        if name not in CLIP_IDS:
            raise SystemExit(f"unmapped clip {name!r}")
        end = heads[index + 1].start() if index + 1 < len(heads) else len(source)
        block = source[head.end():end]
        channels = list(CHANNEL.finditer(block))
        tracks = []
        for channel_index, channel in enumerate(channels):
            part_name = channel.group(1)
            target = channel.group(2)
            if part_name not in PARTS:
                raise SystemExit(f"unmapped part {part_name!r} in {name!r}")
            if target not in TARGETS:
                raise SystemExit(f"unmapped target {target!r} in {name!r}")
            body_end = channels[channel_index + 1].start() if channel_index + 1 < len(channels) else len(block)
            body = block[channel.end():body_end]
            entries = []
            for key in KEY.finditer(body):
                entries.append((
                    round4(float(key.group(1))),
                    round4(float(key.group(2))),
                    round4(float(key.group(3))),
                    round4(float(key.group(4))),
                    key.group(5) == "CATMULLROM",
                ))
            if not entries:
                continue
            entries.sort(key=lambda value: value[0])
            keys = [[entry[0], entry[1], entry[2], entry[3]] for entry in entries]
            smooth = [index for index, entry in enumerate(entries) if entry[4]]
            track = {"part": PARTS[part_name], "channel": TARGETS[target], "keys": keys}
            if smooth:
                track["smooth"] = smooth
            tracks.append(track)
        clips[CLIP_IDS[name]] = {
            "length": round4(float(head.group(2))),
            "loop": bool(head.group(3)),
            "tracks": tracks,
        }
    return {"clips": clips}


def main():
    source = SOURCE.read_text(encoding="utf-8")
    data = parse(source)
    from add_grimmonarch_handmade_clips import build as build_handmade
    data = build_handmade(data)
    TARGET.parent.mkdir(parents=True, exist_ok=True)
    TARGET.write_text(json.dumps(data, ensure_ascii=False, sort_keys=True, separators=(",", ":")),
                      encoding="utf-8", newline="\n")
    print(f"{len(data['clips'])} clips -> {TARGET} ({TARGET.stat().st_size} bytes)")


if __name__ == "__main__":
    sys.exit(main())
