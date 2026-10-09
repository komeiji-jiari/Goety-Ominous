#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
试炼密室移植：把 1.21.1 原版数据（NBT 结构模板 + worldgen JSON + 战利品表）重写成 1.20.1 模组数据。

铁律：**不做静默漏改**。
  - NBT 里的每个 `minecraft:` 字符串都必须落在「保留表」或「改名表」里，否则报错停机；
  - 改完后断言：产物里不允许再出现 `minecraft:trial_chamber*`；
  - 校验改名目标在模组里确实存在（方块查 blockstates、物品查 item 模型）。

用法：
    python tools/port_trial_chambers_nbt.py --scan     # 只扫描打印，不写任何文件
    python tools/port_trial_chambers_nbt.py            # 正式转换 + 落盘 + 报告
"""

import gzip
import io
import json
import os
import re
import struct
import sys
import zipfile
from collections import Counter, OrderedDict

# ---------------------------------------------------------------- 常量

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAR = os.path.join(ROOT, 'libs', '1.21.1.jar')
VANILLA_1201 = r'E:\.minecraft\versions\1.20.1-Forge_47.3.12\1.20.1-Forge_47.3.12.jar'
OUT_DATA = os.path.join(ROOT, 'src', 'main', 'resources', 'data', 'goetyominous')
MOD_ASSETS = os.path.join(ROOT, 'src', 'main', 'resources', 'assets', 'goetyominous')
REPORT = os.path.join(ROOT, 'tools', 'port_trial_chambers_report.txt')

SRC_STRUCT = 'data/minecraft/structure/trial_chambers/'          # 1.21 是单数 structure/
DST_STRUCT = os.path.join(OUT_DATA, 'structures', 'trial_chambers')  # 1.20.1 是复数 structures/
SRC_POOL = 'data/minecraft/worldgen/template_pool/trial_chambers/'
DST_POOL = os.path.join(OUT_DATA, 'worldgen', 'template_pool', 'trial_chambers')

# ---------------------------------------------------------------- 白名单

# 保留策略：只有下面这些「显式改名」和「trial_chamber 路径」会被改成 goetyominous:，
# 其余 minecraft: 引用一律原样保留 —— 但保留项会拿 1.20.1 原版资源反查（blockstates / item 模型），
# 查不到的就报出来等人工定性（用来抓「1.21 新增却忘了改名」的漏网之鱼）。
#
# ⚠️ 例外：jigsaw 方块 nbt 里的 name / target 是「拼图接口名」（minecraft:decor、minecraft:quadrant…），
#    不是注册表条目，一律不动、也不参与校验。

# 实体名（原版那 8 种 + 我们的 2 种），用于反查 SpawnData 里的实体 id
VANILLA_ENTITIES = {
    'zombie', 'husk', 'spider', 'cave_spider', 'silverfish', 'slime', 'stray', 'skeleton',
    'zombified_piglin', 'breeze', 'bogged',
}

# 确实存在但没有独立 assets（blockstates/item 模型）的原版条目，跳过反查
IGNORED_VANILLA = {'air', 'cave_air', 'void_air', 'structure_void', 'empty', 'this', 'player'}

# 改名：1.21 新增 / 本模组的条目 → goetyominous:<同名>
RENAME = {
    # 凝灰岩系（1.20.1 只有 tuff 本体）
    'polished_tuff', 'polished_tuff_stairs', 'polished_tuff_slab', 'polished_tuff_wall',
    'chiseled_tuff', 'chiseled_tuff_bricks', 'tuff_bricks', 'tuff_brick_stairs',
    'tuff_brick_slab', 'tuff_brick_wall', 'tuff_stairs', 'tuff_slab', 'tuff_wall',
    'chiseled_tuff_bricks_wall', 'chiseled_tuff_bricks_stairs', 'chiseled_tuff_bricks_slab',
    # 铜系 1.21 新增
    'chiseled_copper', 'exposed_chiseled_copper', 'weathered_chiseled_copper', 'oxidized_chiseled_copper',
    'waxed_chiseled_copper', 'waxed_exposed_chiseled_copper', 'waxed_weathered_chiseled_copper',
    'waxed_oxidized_chiseled_copper',
    'copper_grate', 'exposed_copper_grate', 'weathered_copper_grate', 'oxidized_copper_grate',
    'waxed_copper_grate', 'waxed_exposed_copper_grate', 'waxed_weathered_copper_grate',
    'waxed_oxidized_copper_grate',
    'copper_bulb', 'exposed_copper_bulb', 'weathered_copper_bulb', 'oxidized_copper_bulb',
    'waxed_copper_bulb', 'waxed_exposed_copper_bulb', 'waxed_weathered_copper_bulb',
    'waxed_oxidized_copper_bulb',
    'copper_door', 'exposed_copper_door', 'weathered_copper_door', 'oxidized_copper_door',
    'waxed_copper_door', 'waxed_exposed_copper_door', 'waxed_weathered_copper_door',
    'waxed_oxidized_copper_door',
    'copper_trapdoor', 'exposed_copper_trapdoor', 'weathered_copper_trapdoor', 'oxidized_copper_trapdoor',
    'waxed_copper_trapdoor', 'waxed_exposed_copper_trapdoor', 'waxed_weathered_copper_trapdoor',
    'waxed_oxidized_copper_trapdoor',
    # 试炼密室本体
    'vault', 'trial_spawner',
    # 实体
    'breeze', 'bogged',
    # 物品
    'trial_key', 'ominous_trial_key', 'wind_charge', 'flow_pottery_sherd',
    'guster_pottery_sherd', 'scrape_pottery_sherd', 'flow_armor_trim_smithing_template',
    'bolt_armor_trim_smithing_template', 'mace', 'heavy_core', 'ominous_bottle',
    'music_disc_creator', 'music_disc_creator_music_box', 'music_disc_precipice',
    'flow_banner_pattern', 'guster_banner_pattern', 'wolf_armor', 'ominous_banner',
}

# 别名池：1.21 里不是文件（靠 pool_aliases 绑定），1.20.1 侧由别名机制在 readPoolName 处换键
ALIAS_POOLS = {
    'minecraft:trial_chambers/spawner/contents/melee',
    'minecraft:trial_chambers/spawner/contents/ranged',
    'minecraft:trial_chambers/spawner/contents/slow_ranged',
    'minecraft:trial_chambers/spawner/contents/small_melee',
}

TRIAL_RE = re.compile(r'^minecraft:(.*trial_chamber.*)$')

# ---------------------------------------------------------------- 战利品表格式修正
#
# 1.21 的 minecraft:enchant_randomly 支持 "options": "<附魔标签>"（1.20.5+），1.20.1 只认具体的
# "enchantments": [...] 列表；而 1.21 的 #minecraft:on_random_loot 标签（= #non_treasure + 4 个）
# 在 1.20.1 里并不存在（non_treasure 标签是 1.21 才加的）。所以这里把标签展开成具体列表，
# 其中 1.21 新增的锤子系附魔（density/breach）指向本模组自己的同名附魔。
ON_RANDOM_LOOT = [
    'minecraft:protection', 'minecraft:fire_protection', 'minecraft:feather_falling',
    'minecraft:blast_protection', 'minecraft:projectile_protection', 'minecraft:respiration',
    'minecraft:aqua_affinity', 'minecraft:thorns', 'minecraft:depth_strider',
    'minecraft:sharpness', 'minecraft:smite', 'minecraft:bane_of_arthropods',
    'minecraft:knockback', 'minecraft:fire_aspect', 'minecraft:looting', 'minecraft:sweeping_edge',
    'minecraft:efficiency', 'minecraft:silk_touch', 'minecraft:unbreaking', 'minecraft:fortune',
    'minecraft:power', 'minecraft:punch', 'minecraft:flame', 'minecraft:infinity',
    'minecraft:luck_of_the_sea', 'minecraft:lure', 'minecraft:loyalty', 'minecraft:impaling',
    'minecraft:riptide', 'minecraft:channeling', 'minecraft:multishot', 'minecraft:quick_charge',
    'minecraft:piercing', 'goetyominous:density', 'goetyominous:breach',
    'minecraft:binding_curse', 'minecraft:vanishing_curse', 'minecraft:frost_walker', 'minecraft:mending',
]

# 1.21 有、1.20.1 没有的战利品表构造（改完后断言一个都不许剩）
FORBIDDEN_LOOT_KEYS = ('options', 'components')

# ---------------------------------------------------------------- 批 4：Goety-3 的 hurricane 房间
#
# 来源：Goety-3 的 data/goety/structure/trial_chambers/hurricane_chamber.nbt（1.21 数据）。
# 与 1.21 原版模板同一套改写规则，另外两点：
#   * goety:whirling_cage → goetyominous:whirling_cage：这是 Windy 更新里**已移植进本模组**的方块，
#     Goety 2 前置里没有（已在 goety_src 反编译资源里核实）；其余 goety: 引用一律保持原样（那是前置的内容）。
#   * 房间追加进 chambers/end 池当第 5 项。Goety-3 原本是**整池替换**（把原版 4 间挪去自己的 fallback），
#     按拍板改成追加，原版 4 间保持不动、fallback 也用原版的。
GOETY3_HURRICANE = os.path.join(ROOT, 'Goety-3-main', 'src', 'main', 'resources',
                                'data', 'goety', 'structure', 'trial_chambers', 'hurricane_chamber.nbt')
GOETY2_ASSETS = os.path.join(ROOT, '.disasm', 'goety_src', 'assets', 'goety')
OTHER_NS_RENAME = {'goety:whirling_cage': 'goetyominous:whirling_cage'}
HURRICANE_ELEMENT = {
    'element': {
        'element_type': 'minecraft:single_pool_element',
        'location': 'goetyominous:trial_chambers/hurricane_chamber',
        'processors': 'goetyominous:trial_chambers_copper_bulb_degradation',
        'projection': 'rigid',
    },
    'weight': 1,
}


def fix_loot(node, path=''):
    """就地修正 1.21 专属的战利品表构造。返回 (修正次数, 剩余的可疑键)。"""
    fixed = 0
    if isinstance(node, dict):
        if node.get('function') == 'minecraft:enchant_randomly' and node.get('options') == '#minecraft:on_random_loot':
            node.pop('options')
            node['enchantments'] = list(ON_RANDOM_LOOT)
            fixed += 1
        for v in node.values():
            fixed += fix_loot(v, path)
    elif isinstance(node, list):
        for v in node:
            fixed += fix_loot(v, path)
    return fixed

# ---------------------------------------------------------------- NBT 编解码

TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG = 0, 1, 2, 3, 4
TAG_FLOAT, TAG_DOUBLE, TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST = 5, 6, 7, 8, 9
TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY = 10, 11, 12


class NbtList:
    def __init__(self, tag_type, items):
        self.tag_type = tag_type
        self.items = items


# ⚠️ 标量必须带类型：Python 只有 int/float，直接写回去会把 byte/short/long 全变成 int、
#    float 全变成 double（NBT 读取端严格按类型取值，字段会被静默丢弃）。
class Byte(int):
    pass


class Short(int):
    pass


class Int(int):
    pass


class Long(int):
    pass


class Float32(float):
    pass


class NbtArray:
    def __init__(self, kind, values):      # kind: 'b' | 'i' | 'l'
        self.kind = kind
        self.values = values


class NbtReader:
    def __init__(self, buf):
        self.buf = buf
        self.pos = 0

    def u1(self):
        v = self.buf[self.pos]
        self.pos += 1
        return v

    def i1(self):
        v = struct.unpack_from('>b', self.buf, self.pos)[0]
        self.pos += 1
        return v

    def i2(self):
        v = struct.unpack_from('>h', self.buf, self.pos)[0]
        self.pos += 2
        return v

    def i4(self):
        v = struct.unpack_from('>i', self.buf, self.pos)[0]
        self.pos += 4
        return v

    def i8(self):
        v = struct.unpack_from('>q', self.buf, self.pos)[0]
        self.pos += 8
        return v

    def f4(self):
        v = struct.unpack_from('>f', self.buf, self.pos)[0]
        self.pos += 4
        return v

    def f8(self):
        v = struct.unpack_from('>d', self.buf, self.pos)[0]
        self.pos += 8
        return v

    def string(self):
        n = struct.unpack_from('>H', self.buf, self.pos)[0]
        self.pos += 2
        s = self.buf[self.pos:self.pos + n].decode('utf-8')
        self.pos += n
        return s

    def payload(self, tag):
        if tag == TAG_BYTE:
            return Byte(self.i1())
        if tag == TAG_SHORT:
            return Short(self.i2())
        if tag == TAG_INT:
            return Int(self.i4())
        if tag == TAG_LONG:
            return Long(self.i8())
        if tag == TAG_FLOAT:
            return Float32(self.f4())
        if tag == TAG_DOUBLE:
            return self.f8()
        if tag == TAG_BYTE_ARRAY:
            n = self.i4()
            v = self.buf[self.pos:self.pos + n]
            self.pos += n
            return NbtArray('b', v)
        if tag == TAG_STRING:
            return self.string()
        if tag == TAG_LIST:
            item_tag = self.u1()
            n = self.i4()
            return NbtList(item_tag, [self.payload(item_tag) for _ in range(n)])
        if tag == TAG_COMPOUND:
            out = OrderedDict()
            while True:
                t = self.u1()
                if t == TAG_END:
                    return out
                key = self.string()
                out[key] = self.payload(t)
        if tag == TAG_INT_ARRAY:
            n = self.i4()
            v = [self.i4() for _ in range(n)]
            return NbtArray('i', v)
        if tag == TAG_LONG_ARRAY:
            n = self.i4()
            v = [self.i8() for _ in range(n)]
            return NbtArray('l', v)
        raise ValueError('unknown tag %d' % tag)


def read_nbt(raw):
    reader = NbtReader(raw)
    root_tag = reader.u1()
    name = reader.string()
    return name, reader.payload(root_tag)


def write_nbt(name, value):
    out = io.BytesIO()
    out.write(struct.pack('>b', TAG_COMPOUND))
    payload = name.encode('utf-8')
    out.write(struct.pack('>H', len(payload)) + payload)
    write_payload(out, value)
    return out.getvalue()


def write_string(out, s):
    payload = s.encode('utf-8')
    out.write(struct.pack('>H', len(payload)) + payload)


def write_payload(out, value):
    if isinstance(value, OrderedDict) or isinstance(value, dict):
        for key, child in value.items():
            out.write(struct.pack('>b', tag_of(child)))
            write_string(out, key)
            write_payload(out, child)
        out.write(struct.pack('>b', TAG_END))
    elif isinstance(value, NbtList):
        out.write(struct.pack('>b', value.tag_type))
        out.write(struct.pack('>i', len(value.items)))
        for item in value.items:
            write_payload(out, item)
    elif isinstance(value, NbtArray):
        if value.kind == 'b':
            out.write(value.values)
        elif value.kind == 'i':
            out.write(struct.pack('>i', len(value.values)))
            for v in value.values:
                out.write(struct.pack('>i', v))
        else:
            out.write(struct.pack('>i', len(value.values)))
            for v in value.values:
                out.write(struct.pack('>q', v))
    elif isinstance(value, str):
        write_string(out, value)
    # 注意顺序：Long/Byte/Short/Int 都是 int 的子类，必须先判具体类型
    elif isinstance(value, Long):
        out.write(struct.pack('>q', value))
    elif isinstance(value, Byte):
        out.write(struct.pack('>b', value))
    elif isinstance(value, Short):
        out.write(struct.pack('>h', value))
    elif isinstance(value, Int):
        out.write(struct.pack('>i', value))
    elif isinstance(value, Float32):
        out.write(struct.pack('>f', value))
    elif isinstance(value, float):
        out.write(struct.pack('>d', value))
    elif isinstance(value, int):
        out.write(struct.pack('>i', value))
    else:
        raise ValueError('unsupported %r' % (value,))


def tag_of(value):
    if isinstance(value, (OrderedDict, dict)):
        return TAG_COMPOUND
    if isinstance(value, NbtList):
        return TAG_LIST
    if isinstance(value, NbtArray):
        return {'b': TAG_BYTE_ARRAY, 'i': TAG_INT_ARRAY, 'l': TAG_LONG_ARRAY}[value.kind]
    if isinstance(value, str):
        return TAG_STRING
    if isinstance(value, Long):
        return TAG_LONG
    if isinstance(value, Byte):
        return TAG_BYTE
    if isinstance(value, Short):
        return TAG_SHORT
    if isinstance(value, Int):
        return TAG_INT
    if isinstance(value, Float32):
        return TAG_FLOAT
    if isinstance(value, float):
        return TAG_DOUBLE
    if isinstance(value, int):
        return TAG_INT
    raise ValueError('unsupported %r' % (value,))


def typed_repr(value):
    """带类型名的结构快照，用于往返一致性校验（类型变了就算不一致）。"""
    if isinstance(value, dict):
        return '{%s}' % ','.join('%s:%s' % (k, typed_repr(v)) for k, v in value.items())
    if isinstance(value, NbtList):
        return '[%d:%s]' % (value.tag_type, ','.join(typed_repr(v) for v in value.items))
    if isinstance(value, NbtArray):
        return '%s[%d]' % (value.kind, len(value.values))
    if isinstance(value, Long):
        return 'L%d' % value
    if isinstance(value, Byte):
        return 'B%d' % value
    if isinstance(value, Short):
        return 'S%d' % value
    if isinstance(value, Int):
        return 'I%d' % value
    if isinstance(value, Float32):
        return 'F%r' % value
    if isinstance(value, float):
        return 'D%r' % value
    if isinstance(value, str):
        return 's%s' % value
    return repr(value)

# ---------------------------------------------------------------- 改写规则


class UnknownId(Exception):
    pass


class Stats:
    def __init__(self, scan=False):
        self.scan = scan               # 扫描模式：不动文件，只统计
        self.seen = Counter()          # 未白名单的引用（保留策略下应为 0）
        self.kept = Counter()          # 保留的 minecraft: 引用
        self.renamed = Counter()       # 改名的引用
        self.jigsaw_names = Counter()  # 拼图接口名（不动）
        self.kept_goety = set()        # 保持 goety: 的引用（前置内容，最后核对存在性）
        self.kept_kind = {k: set() for k in ('block', 'item', 'entity', 'block_entity', 'other')}
        self.renamed_kind = {k: set() for k in ('block', 'item', 'entity', 'block_entity', 'other')}
        self.paths = {}

    def note(self, path, value):
        self.paths.setdefault(value, set()).add(path)


RE_ENTITY = re.compile(r'/entities\[\d+\](/nbt)?/id$|^entities\[\d+\](/nbt)?/id$'
                       r'|/spawn_potentials\[\d+\]/data/entity/id$')
RE_BLOCK_ENTITY = re.compile(r'/nbt/id$')
RE_BLOCK = re.compile(r'/palette\[\d+\]/Name$|/final_state$|/state$')
RE_ITEM = re.compile(r'/Items\[\d+\]/id$|/sherds\[\d+\]$|/item$')


def kind_of(path):
    """按路径判断这是哪一类引用 —— 校验时各类各查各的（战利品表的 type、生物群系 id 之类不查）。"""
    if RE_ENTITY.search(path):
        return 'entity'
    if RE_BLOCK_ENTITY.search(path):
        return 'block_entity'      # 方块实体 id（chest/vault/… ）以及偶尔的残留字段
    if RE_BLOCK.search(path):
        return 'block'
    if RE_ITEM.search(path):
        return 'item'
    return 'other'


def split_state(value):
    """'minecraft:waxed_copper_bulb[lit=true]' → ('minecraft:waxed_copper_bulb', '[lit=true]')"""
    i = value.find('[')
    return (value, '') if i < 0 else (value[:i], value[i:])


def rewrite_string(stats, path, value, opaque=False):
    """按「显式改名表 + trial_chamber 规则」改写；其余原样保留（保留项稍后反查 1.20.1 资源）。"""
    if not isinstance(value, str):
        return value
    if value.startswith('#'):
        # 标签引用（如 structure json 里的 "biomes": "#minecraft:has_structure/trial_chambers"）
        # —— 剥离 # 再按普通引用处理，否则会整个漏改
        inner = rewrite_string(stats, path, value[1:], opaque)
        return '#' + inner
    if value in OTHER_NS_RENAME:
        stats.renamed[value] += 1
        return OTHER_NS_RENAME[value]
    if value.startswith('goety:'):
        stats.kept_goety.add(value)
        return value
    if not value.startswith('minecraft:'):
        return value
    if opaque:
        stats.jigsaw_names[value] += 1
        return value
    base, props = split_state(value)
    name = base[len('minecraft:'):]
    stats.note(path, base)
    kind = kind_of(path)
    if 'trial_chamber' in name or name in RENAME:
        stats.renamed[base] += 1
        stats.renamed_kind[kind].add(name)
        return 'goetyominous:' + name + props
    stats.kept[base] += 1
    stats.kept_kind[kind].add(name)
    return value


def walk_nbt(stats, node, path):
    if isinstance(node, dict):
        # jigsaw 方块 nbt 里的 name/target 是拼图接口名，不参与注册表解析，一律不动
        jigsaw = node.get('id') == 'minecraft:jigsaw'
        for key, child in node.items():
            opaque = jigsaw and key in ('name', 'target')
            if isinstance(child, str):
                node[key] = rewrite_string(stats, path + '/' + key, child, opaque)
            else:
                walk_nbt(stats, child, path + '/' + key)
    elif isinstance(node, NbtList):
        for i, child in enumerate(node.items):
            if isinstance(child, str):
                node.items[i] = rewrite_string(stats, path + '[%d]' % i, child)
            else:
                walk_nbt(stats, child, path + '[%d]' % i)


def walk_json(stats, node, path, allow_unknown=False):
    """JSON 用宽松模式：只改该改的，其余记录留待人工核对（生物群系 id 这类本来就是原版）。"""
    if isinstance(node, dict):
        for key in list(node):
            node[key] = walk_json(stats, node[key], path + '/' + key, allow_unknown)
    elif isinstance(node, list):
        for i, child in enumerate(node):
            node[i] = walk_json(stats, child, path + '[%d]' % i, allow_unknown)
    elif isinstance(node, str):
        if allow_unknown:
            try:
                return rewrite_string(stats, path, node)
            except UnknownId:
                stats.untouched_vanilla.add(node)
                return node
        return rewrite_string(stats, path, node)
    return node

# ---------------------------------------------------------------- 校验


def mod_block_ids():
    ids = set()
    d = os.path.join(MOD_ASSETS, 'blockstates')
    if os.path.isdir(d):
        for f in os.listdir(d):
            if f.endswith('.json'):
                ids.add(f[:-5])
    return ids


def mod_item_ids():
    ids = set()
    for sub in ('models/item', 'blockstates'):
        d = os.path.join(MOD_ASSETS, *sub.split('/'))
        if os.path.isdir(d):
            for f in os.listdir(d):
                if f.endswith('.json'):
                    ids.add(f[:-5])
    return ids


def vanilla_1201_assets(prefix):
    """从 1.20.1 原版客户端 jar 里取某一类资源名（如 assets/minecraft/blockstates/）。"""
    if not os.path.exists(VANILLA_1201):
        return None
    with zipfile.ZipFile(VANILLA_1201) as z:
        return {n[len(prefix):-5] for n in z.namelist()
                if n.startswith(prefix) and n.endswith('.json') and '/' not in n[len(prefix):]}

# ---------------------------------------------------------------- 主流程


def iter_jar(jar, prefix, suffix=None):
    with zipfile.ZipFile(jar) as z:
        for name in z.namelist():
            if name.startswith(prefix) and (suffix is None or name.endswith(suffix)):
                yield name, z.read(name)


def main():
    scan_only = '--scan' in sys.argv
    stats = Stats(scan=scan_only)
    stats.untouched_vanilla = set()
    report = []

    def say(line):
        print(line)
        report.append(line)

    say('== 试炼密室移植 %s ==' % ('（扫描模式，不写文件）' if scan_only else ''))

    # ---- 1. NBT 结构模板
    nbt_count = 0
    outputs = []
    nbt_rt_failures = []

    def encode_checked(root_name, root, label):
        """编码后再解一次，要求与原树**逐类型**相同（标量类型退化的 bug 就是这么抓出来的）。"""
        payload = gzip.compress(write_nbt(root_name, root))
        _, back = read_nbt(gzip.decompress(payload))
        if typed_repr(back) != typed_repr(root):
            nbt_rt_failures.append(label)
        return payload

    for name, raw in sorted(iter_jar(JAR, SRC_STRUCT, '.nbt')):
        nbt_count += 1
        root_name, root = read_nbt(gzip.decompress(raw))
        walk_nbt(stats, root, name[len(SRC_STRUCT):])
        rel = name[len(SRC_STRUCT):]
        outputs.append((os.path.join(DST_STRUCT, rel), encode_checked(root_name, root, rel)))
    say('NBT 模板：读取 %d 个，改写后待写 %d 个（往返一致性：%d/%d 通过）'
        % (nbt_count, len(outputs), nbt_count - len(nbt_rt_failures), nbt_count))

    # ---- 1b. 批 4：Goety-3 的 hurricane 房间
    if not os.path.exists(GOETY3_HURRICANE):
        say('[!] 找不到 Goety-3 的 hurricane_chamber.nbt：%s' % GOETY3_HURRICANE)
    else:
        raw = open(GOETY3_HURRICANE, 'rb').read()
        root_name, root = read_nbt(gzip.decompress(raw) if raw[:2] == b'\x1f\x8b' else raw)
        walk_nbt(stats, root, 'hurricane_chamber.nbt')
        outputs.append((os.path.join(DST_STRUCT, 'hurricane_chamber.nbt'),
                        encode_checked(root_name, root, 'hurricane_chamber.nbt')))
        say('批 4：hurricane_chamber.nbt 已转换（往返一致性：%d/%d 通过）'
            % (1 - len(nbt_rt_failures), 1))

    # ---- 2. worldgen JSON（池 / 结构 / 结构集 / 处理器 / 群系 tag）
    json_jobs = [
        ('data/minecraft/worldgen/template_pool/trial_chambers/', os.path.join(OUT_DATA, 'worldgen', 'template_pool', 'trial_chambers')),
        ('data/minecraft/worldgen/structure/', os.path.join(OUT_DATA, 'worldgen', 'structure')),
        ('data/minecraft/worldgen/structure_set/', os.path.join(OUT_DATA, 'worldgen', 'structure_set')),
        ('data/minecraft/worldgen/processor_list/', os.path.join(OUT_DATA, 'worldgen', 'processor_list')),
        ('data/minecraft/tags/worldgen/biome/has_structure/', os.path.join(OUT_DATA, 'tags', 'worldgen', 'biome', 'has_structure')),
    ]
    json_outputs = []
    for prefix, dst_dir in json_jobs:
        for name, raw in sorted(iter_jar(JAR, prefix, '.json')):
            if 'trial_chamber' not in name:
                continue
            rel = name[len(prefix):]          # 保留目录层级：模板池的路径就是它的 id，绝不能拍平
            if not rel:
                continue
            doc = json.loads(raw.decode('utf-8'))
            walk_json(stats, doc, name, allow_unknown=True)
            if rel == 'chambers/end.json' and prefix.endswith('template_pool/trial_chambers/'):
                # 批 4：追加 hurricane 房间（Goety-3 是整池替换，我们按拍板改成追加，原版 4 间不动）
                doc.setdefault('elements', []).append(HURRICANE_ELEMENT)
                say('批 4：chambers/end 池追加 hurricane 房间 → 共 %d 项（原版 4 间 + hurricane）'
                    % len(doc['elements']))
            json_outputs.append((os.path.join(dst_dir, rel),
                                 json.dumps(doc, indent=2, ensure_ascii=False) + '\n'))
    say('worldgen JSON：%d 个' % len(json_outputs))

    # ---- 3. 战利品表（1.21 路径是 loot_table 单数；只补我们没有的）
    # ⚠️ 已存在的表一律不动：里面有几张是当初手工做过格式适配的（equipment/* 用的是 1.20.1 的
    # "minecraft:entity" + 子表引用，reward_* 的 enchant_randomly 是展开过的列表），
    # 本脚本的重写只覆盖「从 1.21 原样搬 + 改名」这一种情况，强推会把手工成果冲掉。
    loot_outputs = []
    loot_skipped = []
    loot_fixes = 0
    loot_suspects = []
    with zipfile.ZipFile(JAR) as z:
        names = [n for n in z.namelist() if 'loot_table' in n and 'trial_chamber' in n and n.endswith('.json')]
        for name in sorted(names):
            rel = name.split('loot_table/', 1)[1]
            dst = os.path.join(OUT_DATA, 'loot_tables', rel)
            if os.path.exists(dst):
                loot_skipped.append(rel)
                continue
            doc = json.loads(z.read(name).decode('utf-8'))
            loot_fixes += fix_loot(doc, rel)
            walk_json(stats, doc, name, allow_unknown=True)
            text = json.dumps(doc, indent=2, ensure_ascii=False) + '\n'
            for bad_key in FORBIDDEN_LOOT_KEYS:
                if '"%s"' % bad_key in text:
                    loot_suspects.append('%s 里仍有 1.21 专属字段 "%s"' % (rel, bad_key))
            loot_outputs.append((dst, text))
    say('战利品表：新增 %d 张（其中修正 1.21 专属构造 %d 处），已存在跳过 %d 张'
        % (len(loot_outputs), loot_fixes, len(loot_skipped)))

    # ---- 4. 校验
    #   a) 改名目标必须在模组里真的存在（方块/物品查 assets）
    #   b) 保留项必须在 1.20.1 原版资源里真的存在 —— 用来抓「1.21 新增却忘了改名」的漏网之鱼
    #      （拼图接口名已在扫描时排除，不参与）
    problems = list(loot_suspects)
    for rel in nbt_rt_failures:
        problems.append('NBT 往返后类型/结构变了：%s' % rel)
    mod_blocks = mod_block_ids()
    mod_items = mod_item_ids()
    vanilla_blocks = vanilla_1201_assets('assets/minecraft/blockstates/')
    vanilla_items = vanilla_1201_assets('assets/minecraft/models/item/')

    # a) 改名目标必须在模组里真的存在
    for name in sorted(stats.renamed_kind['block']):
        if name not in mod_blocks and name not in mod_items:
            problems.append('改名的方块在模组资源里找不到：minecraft:%s' % name)
    for name in sorted(stats.renamed_kind['item']):
        if name not in mod_items and name not in mod_blocks:
            problems.append('改名的物品在模组资源里找不到：minecraft:%s' % name)
    for name in sorted(stats.renamed_kind['entity']):
        if name not in VANILLA_ENTITIES:
            problems.append('改名的实体不在预期名单里：minecraft:%s' % name)

    # b) 保留项必须在 1.20.1 原版资源里真的存在（抓「1.21 新增却忘了改名」）
    for name in sorted(stats.kept_kind['block']):
        if name in IGNORED_VANILLA or (vanilla_blocks is not None and name in vanilla_blocks):
            continue
        problems.append('保留的方块在 1.20.1 原版 blockstates 里找不到（疑似漏改）：minecraft:%s' % name)
    for name in sorted(stats.kept_kind['item']):
        if name in IGNORED_VANILLA or (vanilla_items is not None and name in vanilla_items):
            continue
        if vanilla_blocks is not None and name in vanilla_blocks:
            continue
        problems.append('保留的物品在 1.20.1 原版 item 模型里找不到（疑似漏改）：minecraft:%s' % name)
    for name in sorted(stats.kept_kind['entity']):
        if name in VANILLA_ENTITIES:
            continue
        problems.append('保留的实体不在预期名单里：minecraft:%s' % name)

    # c) 保持 goety: 的引用应当是 Goety 2 前置里存在的（goety_src 是反编译脚手架，按警告报出）
    goety_missing = []
    if os.path.isdir(GOETY2_ASSETS):
        for value in sorted(stats.kept_goety):
            name = value.split(':', 1)[1]
            if os.path.exists(os.path.join(GOETY2_ASSETS, 'blockstates', name + '.json')):
                continue
            if os.path.exists(os.path.join(GOETY2_ASSETS, 'models', 'item', name + '.json')):
                continue
            goety_missing.append(value)
    if goety_missing:
        say('  ⚠ 这些 goety: 引用在 goety_src 资源里没找到（手工确认一下）：%s' % goety_missing)
    say('保持 goety: 的引用：%s' % (sorted(stats.kept_goety) or '无'))

    say('校验：改名 %d 种（方块 %d / 物品 %d / 实体 %d），保留 %d 种，问题 %d 条'
        % (len(stats.renamed), len(stats.renamed_kind['block']), len(stats.renamed_kind['item']),
           len(stats.renamed_kind['entity']), len(stats.kept), len(problems)))
    if problems:
        for p in problems:
            say('    ✗ ' + p)
    say('未改动的拼图接口名 %d 种（示例）：%s'
        % (len(stats.jigsaw_names), ', '.join(sorted(stats.jigsaw_names)[:12])))

    # ---- 5. 统计
    say('\n改名统计（top 40）：')
    for value, count in stats.renamed.most_common(40):
        say('    %-58s x%d' % (value, count))
    say('保留的原版引用 %d 种' % len(stats.kept))
    if stats.untouched_vanilla:
        say('JSON 里未改动的 minecraft: 引用 %d 种（应全是原版物品/群系/标签，需人工扫一眼）：'
            % len(stats.untouched_vanilla))
        say('    ' + ', '.join(sorted(stats.untouched_vanilla)[:60]))

    # ---- 6. 落盘
    if not scan_only:
        written = 0
        for path, data in outputs + json_outputs + loot_outputs:
            os.makedirs(os.path.dirname(path), exist_ok=True)
            mode = 'wb' if isinstance(data, bytes) else 'w'
            kwargs = {} if isinstance(data, bytes) else {'encoding': 'utf-8', 'newline': '\n'}
            with open(path, mode, **kwargs) as fh:
                fh.write(data)
            written += 1
        say('\n已写出 %d 个文件' % written)
        with open(REPORT, 'w', encoding='utf-8', newline='\n') as fh:
            fh.write('\n'.join(report) + '\n')
        say('报告: %s' % REPORT)
    return 1 if (problems or stats.seen) else 0


if __name__ == '__main__':
    sys.exit(main())
