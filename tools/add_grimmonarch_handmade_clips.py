import copy
import json
from bisect import bisect_right
from pathlib import Path

TARGET = Path("src/main/resources/assets/goetyominous/grimmonarch/clips.json")
PARTS = [
    "hip", "left_leg", "right_leg", "body", "shoulders",
    "neck", "left_arm", "left_claw", "right_arm", "right_claw",
]


def value(track, time):
    keys = track["keys"]
    smooth = set(track.get("smooth", ()))
    if len(keys) == 1 or time <= keys[0][0]:
        return keys[0][1:4]
    if time >= keys[-1][0]:
        return keys[-1][1:4]
    low = bisect_right([key[0] for key in keys], time) - 1
    low = max(0, min(low, len(keys) - 2))
    a = keys[low]
    b = keys[low + 1]
    span = b[0] - a[0]
    weight = 0.0 if span <= 1.0e-6 else (time - a[0]) / span
    if low in smooth:
        before = keys[max(0, low - 1)]
        after = keys[min(len(keys) - 1, low + 2)]
        return [
            catmull(before[1], a[1], b[1], after[1], weight),
            catmull(before[2], a[2], b[2], after[2], weight),
            catmull(before[3], a[3], b[3], after[3], weight),
        ]
    return [
        a[1] + (b[1] - a[1]) * weight,
        a[2] + (b[2] - a[2]) * weight,
        a[3] + (b[3] - a[3]) * weight,
    ]


def catmull(p0, p1, p2, p3, t):
    return 0.5 * (2.0 * p1 + (-p0 + p2) * t
                  + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t * t
                  + (-p0 + 3.0 * p1 - 3.0 * p2 + p3) * t * t * t)


def empty_pose():
    return {part: {"pos": [0.0, 0.0, 0.0], "rot": [0.0, 0.0, 0.0]} for part in PARTS}


def sample(data, clip_name, time):
    clip = data["clips"][clip_name]
    if clip["loop"] and clip["length"] > 0.0:
        time %= clip["length"]
    else:
        time = max(0.0, min(clip["length"], time))
    pose = empty_pose()
    for track in clip["tracks"]:
        part = track["part"]
        if part not in pose:
            continue
        channel = "pos" if track["channel"] == "position" else "rot"
        values = value(track, time)
        for index in range(3):
            pose[part][channel][index] += values[index]
    return pose


def blend(first, second, weight):
    pose = empty_pose()
    for part in PARTS:
        for channel in ("pos", "rot"):
            pose[part][channel] = [
                first[part][channel][index] + (second[part][channel][index] - first[part][channel][index]) * weight
                for index in range(3)
            ]
    return pose


def offset(pose, part, channel, x=0.0, y=0.0, z=0.0):
    pose[part][channel][0] += x
    pose[part][channel][1] += y
    pose[part][channel][2] += z
    return pose


def set_rotation(pose, part, x, y, z):
    pose[part]["rot"] = [x, y, z]
    return pose


def normalize(pose, reference):
    result = copy.deepcopy(pose)
    for part in PARTS:
        for index in range(3):
            while result[part]["rot"][index] - reference[part]["rot"][index] > 180.0:
                result[part]["rot"][index] -= 360.0
            while result[part]["rot"][index] - reference[part]["rot"][index] < -180.0:
                result[part]["rot"][index] += 360.0
    return result


def sequence(keys):
    result = []
    previous = None
    for time, pose in keys:
        if previous is not None:
            pose = normalize(pose, previous)
        result.append((time, pose))
        previous = pose
    return result


def make_clip(length, loop, keys):
    tracks = []
    for part in PARTS:
        for channel, name in (("pos", "position"), ("rot", "rotation")):
            frames = []
            for time, pose in keys:
                values = pose[part][channel]
                frames.append([round(time, 5), round(values[0], 5), round(values[1], 5), round(values[2], 5)])
            tracks.append({"part": part, "channel": name, "keys": frames})
    return {"length": round(length, 5), "loop": loop, "tracks": tracks}


def build(data):
    hover = sample(data, "combo_1_flying", 0.1057)
    start_one = sample(data, "combo_start", 1.2)
    start_two = sample(data, "combo_starting_move", 1.3)
    attack = sample(data, "combo_4_spinning_slash", 0.0)
    dash = sample(data, "sprint_slash", 1.5)

    hover_one = normalize(hover, start_one)
    hover_two = normalize(hover, start_two)

    one_a = offset(blend(start_one, hover_one, 0.35), "hip", "pos", y=1.2)
    offset(one_a, "body", "rot", x=-1.5)
    offset(one_a, "neck", "rot", x=1.0)
    offset(one_a, "left_arm", "rot", z=2.0)
    offset(one_a, "right_arm", "rot", z=-2.0)
    one_b = offset(blend(start_one, hover_one, 0.68), "hip", "pos", y=-0.8)
    offset(one_b, "body", "rot", x=1.0)
    offset(one_b, "neck", "rot", x=-1.0)
    offset(one_b, "left_arm", "rot", z=-2.0)
    offset(one_b, "right_arm", "rot", z=2.0)
    data["clips"]["hover_loop"] = make_clip(0.9667, False, [
        (0.0, start_one), (0.35, one_a), (0.7, one_b), (0.9667, hover_one),
    ])

    two_a = offset(blend(start_two, hover_two, 0.32), "hip", "pos", y=1.5)
    offset(two_a, "body", "rot", x=-1.5)
    offset(two_a, "left_arm", "rot", z=3.0)
    offset(two_a, "right_arm", "rot", z=-3.0)
    two_b = offset(blend(start_two, hover_two, 0.66), "hip", "pos", y=-1.0)
    offset(two_b, "body", "rot", x=1.2, y=2.0)
    offset(two_b, "neck", "rot", x=-1.0)
    offset(two_b, "left_arm", "rot", z=-3.0)
    offset(two_b, "right_arm", "rot", z=3.0)
    data["clips"]["hover_loop_2"] = make_clip(0.9, False, [
        (0.0, start_two), (0.3, two_a), (0.6, two_b), (0.9, hover_two),
    ])

    base = hover_two
    stomp_a = copy.deepcopy(base)
    set_rotation(stomp_a, "body", base["body"]["rot"][0] - 8.0, 0.0, 0.0)
    set_rotation(stomp_a, "neck", base["neck"]["rot"][0] + 2.0, 0.0, 0.0)
    set_rotation(stomp_a, "right_leg", base["right_leg"]["rot"][0] - 18.0,
                 base["right_leg"]["rot"][1], base["right_leg"]["rot"][2])
    set_rotation(stomp_a, "left_leg", base["left_leg"]["rot"][0] + 10.0,
                 base["left_leg"]["rot"][1], base["left_leg"]["rot"][2])
    set_rotation(stomp_a, "left_arm", base["left_arm"]["rot"][0] - 45.0,
                 base["left_arm"]["rot"][1] - 30.0, base["left_arm"]["rot"][2] + 10.0)
    set_rotation(stomp_a, "right_arm", base["right_arm"]["rot"][0] - 45.0,
                 base["right_arm"]["rot"][1] + 30.0, base["right_arm"]["rot"][2] - 10.0)
    offset(stomp_a, "hip", "pos", y=3.0)
    offset(stomp_a, "body", "pos", y=2.0)

    stomp_b = copy.deepcopy(base)
    set_rotation(stomp_b, "body", base["body"]["rot"][0] + 26.0, 0.0, 0.0)
    set_rotation(stomp_b, "neck", base["neck"]["rot"][0] + 5.0, 0.0, 0.0)
    set_rotation(stomp_b, "right_leg", base["right_leg"]["rot"][0] + 28.0,
                 base["right_leg"]["rot"][1], base["right_leg"]["rot"][2])
    set_rotation(stomp_b, "left_leg", base["left_leg"]["rot"][0] - 6.0,
                 base["left_leg"]["rot"][1], base["left_leg"]["rot"][2])
    set_rotation(stomp_b, "left_arm", base["left_arm"]["rot"][0] + 30.0,
                 base["left_arm"]["rot"][1] + 15.0, base["left_arm"]["rot"][2] - 20.0)
    set_rotation(stomp_b, "right_arm", base["right_arm"]["rot"][0] + 30.0,
                 base["right_arm"]["rot"][1] - 15.0, base["right_arm"]["rot"][2] + 20.0)
    offset(stomp_b, "hip", "pos", y=-5.0)
    offset(stomp_b, "body", "pos", y=-2.0)

    stomp_c = copy.deepcopy(base)
    set_rotation(stomp_c, "body", base["body"]["rot"][0] + 12.0, 40.0, 20.0)
    set_rotation(stomp_c, "neck", base["neck"]["rot"][0] - 5.0, -25.0, -10.0)
    set_rotation(stomp_c, "right_leg", base["right_leg"]["rot"][0] - 15.0,
                 base["right_leg"]["rot"][1], base["right_leg"]["rot"][2] - 5.0)
    set_rotation(stomp_c, "left_leg", base["left_leg"]["rot"][0] + 8.0,
                 base["left_leg"]["rot"][1] - 10.0, base["left_leg"]["rot"][2])
    set_rotation(stomp_c, "left_arm", base["left_arm"]["rot"][0] + 5.0,
                 base["left_arm"]["rot"][1] + 10.0, base["left_arm"]["rot"][2] + 5.0)
    set_rotation(stomp_c, "right_arm", base["right_arm"]["rot"][0] + 5.0,
                 base["right_arm"]["rot"][1] - 10.0, base["right_arm"]["rot"][2] - 5.0)
    offset(stomp_c, "hip", "pos", y=-2.0)

    stomp_d = blend(stomp_c, attack, 0.5)
    offset(stomp_d, "hip", "pos", y=-1.0)
    data["clips"]["heel_stomp"] = make_clip(0.8667, False, [
        (0.0, base), (0.12, stomp_a), (0.32, stomp_b), (0.45, stomp_c), (0.62, stomp_d), (0.8667, attack),
    ])

    rest_a = copy.deepcopy(dash)
    offset(rest_a, "body", "rot", x=1.5)
    offset(rest_a, "neck", "rot", x=1.0)
    offset(rest_a, "hip", "pos", y=0.6)
    offset(rest_a, "left_arm", "rot", z=2.0)
    offset(rest_a, "right_arm", "rot", z=-2.0)
    rest_b = copy.deepcopy(dash)
    data["clips"]["dash_rest_loop"] = make_clip(0.8333, False, [
        (0.0, dash), (0.42, rest_a), (0.8333, rest_b),
    ])
    return data


def main():
    data = json.loads(TARGET.read_text(encoding="utf-8"))
    data = build(data)
    TARGET.write_text(json.dumps(data, ensure_ascii=False, sort_keys=True, separators=(",", ":")),
                      encoding="utf-8", newline="\n")
    print(f"handmade clips -> {TARGET}")


if __name__ == "__main__":
    main()
