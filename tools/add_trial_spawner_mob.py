#!/usr/bin/env python3
"""给试炼密室的刷怪笼家族新增一种怪 —— 纯新增，绝不覆盖已有文件。

用法：
  python tools/add_trial_spawner_mob.py <mob_id> <family> [--base <族/模板>] [--family-config] [--config-from <族/模板>] [--equipment <loot_table> | --no-equipment] [--dry-run]

例：
  # 近战族（contents/melee 是顶层 random 别名 → 追加一个 target）
  python tools/add_trial_spawner_mob.py goetyominous:mired melee --base slow_ranged/poison_skeleton
  # 远程族（contents/ranged 在 random_group 里、与 slow_ranged 成对 → 追加一个整组，并自动建 slow_ranged 姊妹）
  python tools/add_trial_spawner_mob.py goetyominous:dreden ranged --base ranged/stray --no-equipment

family ∈ melee / ranged / slow_ranged / small_melee / breeze

引用链（1.21 试炼密室）：密室拼图 → connectors/<family> → 别名 contents/<family> → 池 <family>/<怪> → 模板 <family>/<怪>.nbt
别名有两种形状：
  * 顶层 `minecraft:random`（melee / small_melee）→ 往它的 targets 追加一条
  * `minecraft:random_group` 里的成对 direct（ranged + slow_ranged）→ 追加一整个新组；
    因为组里同一 alias 不能出现两次（别名表是 ImmutableMap，重复键会抛异常），
    且新组必须把组里所有 alias 都映射齐，否则漏掉的那个 alias 会指向不存在的池。

做的事：
  1) 复制 <base>.nbt → structures/trial_chambers/spawner/<family>/<name>.nbt，
     把 spawn_potentials[*].data.entity.id（及 spawn_data.entity.id，若有）换成 <mob_id>；
     --equipment 换装备表 / --no-equipment 把 equipment 整块删掉（= 不穿盔甲不拿武器，同蜘蛛）
  2) 复制同族（成对时是同一 partner 的）池 json → template_pool/.../spawner/<family>/<name>.json，只改 location
  3) 追加别名（random → 一条 target；random_group → 一个整组，并为组里其他 alias 自动建同款姊妹模板+池）

安全：目标 nbt / 池 json 已存在 → 报错退出，不覆盖任何已有数据。
"""
import argparse
import copy
import gzip
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import port_trial_chambers_nbt as P

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, 'src', 'main', 'resources', 'data', 'goetyominous')
TPL_DIR = os.path.join(RES, 'structures', 'trial_chambers', 'spawner')
POOL_DIR = os.path.join(RES, 'worldgen', 'template_pool', 'trial_chambers', 'spawner')
STRUCT_JSON = os.path.join(RES, 'worldgen', 'structure', 'trial_chambers.json')
FAMILIES = ('melee', 'ranged', 'slow_ranged', 'small_melee', 'breeze')

ENTITY_PATH = re.compile(r'/spawn_potentials\[\d+\]/data/entity/id$|/spawn_data/entity/id$')
EQUIP_PATH = re.compile(r'/spawn_potentials\[\d+\]/data/equipment/loot_table$|/spawn_data/equipment/loot_table$')
EQUIP_PARENT = re.compile(r'/spawn_potentials\[\d+\]/data/equipment$|/spawn_data/equipment$')
seq = lambda x: x.items if isinstance(x, P.NbtList) else (x.values if isinstance(x, P.NbtArray) else None)


def walk(node, path, fn):
    if isinstance(node, dict):
        for k in list(node.keys()):
            fn(node, k, path + '/' + k)
            if k in node:
                walk(node[k], path + '/' + k, fn)
    else:
        for i, v in enumerate(seq(node) or []):
            walk(v, path + '[%d]' % i, fn)


def spawner_be(root):
    """从模板根 NBT 里取出刷怪笼的方块实体（带 normal_config / ominous_config 的那个）"""
    for b in seq(root.get('blocks', [])) or []:
        nbt = b.get('nbt')
        if isinstance(nbt, dict) and isinstance(nbt.get('normal_config'), dict) and isinstance(nbt.get('ominous_config'), dict):
            return nbt
    return None


def first_or_none(d, suffix='.nbt'):
    if not os.path.isdir(d):
        return None
    names = sorted(f[:-len(suffix)] for f in os.listdir(d) if f.endswith(suffix))
    return names[0] if names else None


def find_alias(struct, alias_target):
    """('random', binding, None) 或 ('group', binding, groups_list)"""
    for b in struct.get('pool_aliases', []):
        if b.get('alias') == alias_target and 'targets' in b:
            return 'random', b, None
        groups = b.get('groups')
        if groups:
            for g in groups:
                for d in g.get('data', []) or []:
                    if d.get('alias') == alias_target:
                        return 'group', b, groups
    return None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('mob_id')
    ap.add_argument('family', choices=FAMILIES)
    ap.add_argument('--base', help='周边方块抄哪个模板，默认本族第一个')
    ap.add_argument('--family-config', action='store_true',
                    help='每份模板的 config 抄同族同 partner 的（ranged 快节奏 / slow_ranged 慢节奏，同族参数一致）')
    ap.add_argument('--config-from', help='把某个模板的 normal_config/ominous_config 整块抄过来（实体仍会换成 mob_id）')
    ap.add_argument('--equipment', help='替换 spawn_potentials 里的装备表')
    ap.add_argument('--no-equipment', action='store_true',
                    help='把 equipment 整块删掉（不穿盔甲不拿武器，同蜘蛛/史莱姆）')
    ap.add_argument('--dry-run', action='store_true')
    a = ap.parse_args()
    if a.no_equipment and a.equipment:
        sys.exit('--equipment 和 --no-equipment 只能二选一')

    ns, _, name = a.mob_id.partition(':')
    if not name:
        sys.exit('mob_id 要写成 命名空间:路径，例如 goetyominous:dreden')

    base = a.base or '%s/%s' % (a.family, first_or_none(os.path.join(TPL_DIR, a.family)))
    src_nbt = os.path.join(TPL_DIR, base.replace('/', os.sep) + '.nbt')
    if not os.path.exists(src_nbt):
        sys.exit('底模板不存在：%s' % src_nbt)

    alias_target = 'goetyominous:trial_chambers/spawner/contents/%s' % a.family
    struct = json.load(open(STRUCT_JSON, encoding='utf-8'))
    hit = find_alias(struct, alias_target)
    if not hit:
        sys.exit('别名里找不到 %s —— family 写错了？（breeze 那类真实池的情况本脚本暂不自动处理）' % alias_target)
    mode, binding, groups = hit

    # pairs = [(族目录, 该组里对应的 alias, partner 模板名)]；顶层 random 只有 1 个，成对组要把组里所有 alias 都映射齐
    if mode == 'random':
        pairs = [(a.family, alias_target, None)]
    else:
        pairs = None
        for g in groups:
            for d in g.get('data', []) or []:
                if d.get('alias') != alias_target:
                    continue
                pairs = []
                for dd in g.get('data', []) or []:
                    tid = dd.get('target', '')
                    fam = tid.split('trial_chambers/spawner/')[-1].split('/')[0]
                    pairs.append((fam, dd.get('alias'), tid.split('/')[-1]))
                break
            if pairs:
                break
        if not pairs:
            sys.exit('解析别名组失败，先人工看一眼 %s' % STRUCT_JSON)

    for fam, _, partner in pairs:
        print('将新增   : spawner/%s/%s.nbt + .json   (partner=%s)' % (fam, name, partner or '本族第一个'))
    print('别名形状 : %s%s' % (mode, '（追加一个整组）' if mode == 'group' else '（追加一条 target）'))

    for fam, _, _ in pairs:
        for p in (os.path.join(TPL_DIR, fam, name + '.nbt'), os.path.join(POOL_DIR, fam, name + '.json')):
            if os.path.exists(p):
                sys.exit('已存在，拒绝覆盖：%s' % p)

    if a.dry_run:
        print('(dry-run，不写盘)')
        return

    # ---- 模板 + 池 ----
    tag_name = P.read_nbt(gzip.decompress(open(src_nbt, 'rb').read()))[0]
    for fam, _, partner in pairs:
        root = P.read_nbt(gzip.decompress(open(src_nbt, 'rb').read()))[1]
        if a.family_config:
            ref = os.path.join(TPL_DIR, fam, partner + '.nbt') if partner else src_nbt
            if os.path.exists(ref):
                ref_be, dst_be = spawner_be(P.read_nbt(gzip.decompress(open(ref, 'rb').read()))[1]), spawner_be(root)
                if ref_be is None or dst_be is None:
                    sys.exit('找不到刷怪笼方块实体：%s' % ref)
                for cfg in ('normal_config', 'ominous_config'):
                    dst_be[cfg] = copy.deepcopy(ref_be[cfg])
                print('  已按族内参照 %s 抄入 config（同族节奏/装备一致）' % os.path.relpath(ref, ROOT))
        if a.config_from:
            cfg_path = os.path.join(TPL_DIR, a.config_from.replace('/', os.sep) + '.nbt')
            if not os.path.exists(cfg_path):
                sys.exit('--config-from 模板不存在：%s' % cfg_path)
            src_be, dst_be = spawner_be(P.read_nbt(gzip.decompress(open(cfg_path, 'rb').read()))[1]), spawner_be(root)
            if src_be is None or dst_be is None:
                sys.exit('找不到刷怪笼方块实体：%s' % cfg_path)
            for cfg in ('normal_config', 'ominous_config'):
                dst_be[cfg] = copy.deepcopy(src_be[cfg])
            print('  已从 %s 整块抄入 normal_config/ominous_config' % a.config_from)
        hits = []

        def sub(node, k, path):
            if ENTITY_PATH.search(path) and node[k] != a.mob_id:
                hits.append((path, node[k]))
                node[k] = a.mob_id
            elif a.no_equipment and EQUIP_PARENT.search(path):
                hits.append((path, '%s -> (整块删掉)' % node[k]))
                del node[k]
            elif a.equipment and EQUIP_PATH.search(path):
                hits.append((path, '%s -> %s' % (node[k], a.equipment)))
                node[k] = a.equipment

        walk(root, '', sub)
        if not any(ENTITY_PATH.search(p) for p, _ in hits):
            sys.exit('在 %s 里没找到 spawn_potentials/spawn_data 的实体字段，结构可能变了' % src_nbt)
        raw = gzip.compress(P.write_nbt(tag_name, root))
        if P.typed_repr(P.read_nbt(gzip.decompress(raw))[1]) != P.typed_repr(root):
            sys.exit('往返校验失败，拒绝写盘')
        dst_nbt = os.path.join(TPL_DIR, fam, name + '.nbt')
        os.makedirs(os.path.dirname(dst_nbt), exist_ok=True)
        open(dst_nbt, 'wb').write(raw)
        print('\n写入 %s' % os.path.relpath(dst_nbt, ROOT))
        for p, old in hits:
            print('    %-52s %s -> %s' % (p, old, a.mob_id if ENTITY_PATH.search(p) else ''))

        pool_id = 'goetyominous:trial_chambers/spawner/%s/%s' % (fam, name)
        sibling = partner or first_or_none(os.path.join(POOL_DIR, fam), '.json')
        if not sibling:
            sys.exit('池目录 %s 里没有可参照的池 json' % os.path.join(POOL_DIR, fam))
        src_pool = os.path.join(POOL_DIR, fam, sibling + '.json')
        pool = json.load(open(src_pool, encoding='utf-8'))
        for e in pool.get('elements', []):
            if e.get('element', {}).get('location'):
                e['element']['location'] = pool_id
                break
        else:
            sys.exit('参照池 %s 里没有 location 可改' % src_pool)
        dst_pool = os.path.join(POOL_DIR, fam, name + '.json')
        os.makedirs(os.path.dirname(dst_pool), exist_ok=True)
        json.dump(pool, open(dst_pool, 'w', encoding='utf-8'), indent=2, sort_keys=True)
        print('写入 %s（location -> %s）' % (os.path.relpath(dst_pool, ROOT), pool_id))

    # ---- 别名 ----
    if mode == 'random':
        new_id = 'goetyominous:trial_chambers/spawner/%s/%s' % (pairs[0][0], name)
        if any(t.get('data') == new_id for t in binding['targets']):
            sys.exit('别名里已经有这条了')
        binding['targets'].append({'data': new_id, 'weight': 1})
        note = 'targets 追加 1 条'
    else:
        data = [{'type': 'minecraft:direct', 'alias': al,
                 'target': 'goetyominous:trial_chambers/spawner/%s/%s' % (fam, name)}
                for fam, al, _ in pairs]
        groups.append({'data': data, 'weight': 1})
        note = '追加 1 个新组（%d 个 direct）' % len(data)
    json.dump(struct, open(STRUCT_JSON, 'w', encoding='utf-8'), indent=2, sort_keys=False)
    print('写入 %s（%s）' % (os.path.relpath(STRUCT_JSON, ROOT), note))
    print('\n完成。记得：worldgen 数据要重进世界（或新世界）才生效，且只影响新生成的区块。')


if __name__ == '__main__':
    main()
