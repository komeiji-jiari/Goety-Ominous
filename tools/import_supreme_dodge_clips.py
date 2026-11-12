import json
import sys
import zipfile
from pathlib import Path

JAR = Path("libs/goety_supreme-1.1.6.4.jar")
TARGET = Path("src/main/resources/assets/goetyominous/grimmonarch/clips.json")
SOURCE = "assets/goety_supreme/clips/ifrit.json"
CLIPS = {
    "combo_start_dodge": "combo_start_dodge",
    "dash": "dash",
    "rush": "rush",
    "dash_recovery": "dash_recovery",
}
PARTS = {
    "hip",
    "left_leg",
    "right_leg",
    "body",
    "shoulders",
    "neck",
    "left_arm",
    "left_claw",
    "right_arm",
    "right_claw",
    "head",
}


def round5(value):
    value = round(value, 5)
    return 0.0 if value == -0.0 else value


def convert_clip(clip):
    length = round5(float(clip["len"]) / 20.0)
    tracks = []
    for part, frames in clip.get("bones", {}).items():
        if part not in PARTS:
            continue
        rotation = []
        position = []
        smooth_rotation = []
        smooth_position = []
        for frame in frames:
            seconds = round5(float(frame[0]) / 20.0)
            rotation.append([seconds, round5(frame[1]), round5(frame[2]), round5(frame[3])])
            position.append([seconds, round5(frame[4]), round5(frame[5]), round5(frame[6])])
            if len(frame) > 7 and frame[7] != 0:
                smooth_rotation.append(len(rotation) - 1)
                smooth_position.append(len(position) - 1)
        if rotation:
            track = {"part": part, "channel": "rotation", "keys": rotation}
            if smooth_rotation:
                track["smooth"] = smooth_rotation
            tracks.append(track)
        if position:
            track = {"part": part, "channel": "position", "keys": position}
            if smooth_position:
                track["smooth"] = smooth_position
            tracks.append(track)
    return {"length": length, "loop": bool(clip.get("loop", False)), "tracks": tracks}


def main():
    with zipfile.ZipFile(JAR) as jar:
        source = json.loads(jar.read(SOURCE).decode("utf-8"))["clips"]
    data = json.loads(TARGET.read_text(encoding="utf-8"))
    added = []
    for name, clip_id in CLIPS.items():
        if name not in source:
            raise SystemExit(f"missing clip {name!r}")
        data["clips"][clip_id] = convert_clip(source[name])
        added.append(clip_id)
    TARGET.write_text(
        json.dumps(data, ensure_ascii=False, sort_keys=True, separators=(",", ":")) + "\n",
        encoding="utf-8",
        newline="\n",
    )
    print(f"added {', '.join(added)}; total {len(data['clips'])} clips")


if __name__ == "__main__":
    sys.exit(main())
