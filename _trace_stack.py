import zipfile, struct

z = zipfile.ZipFile('E:/java/libs/alexscaves-2.0.2.jar')
data = z.read('com/github/alexmodguy/alexscaves/server/item/SeaStaffItem.class')

# ---- 常量池 ----
pos = 10
cp_count = struct.unpack('>H', data[8:10])[0]
cp = {}
i = 1
while i < cp_count:
    tag = data[pos]
    if tag == 1:
        ln = struct.unpack('>H', data[pos+1:pos+3])[0]
        cp[i] = data[pos+3:pos+3+ln].decode('utf-8', 'replace')
        pos += 3 + ln
    elif tag in (7, 8, 16, 19, 20):
        cp[i] = ('R', struct.unpack('>H', data[pos+1:pos+3])[0]); pos += 3
    elif tag == 15:
        cp[i] = None; pos += 4
    elif tag == 3:
        cp[i] = struct.unpack('>i', data[pos+1:pos+5])[0]; pos += 5
    elif tag == 4:
        cp[i] = struct.unpack('>f', data[pos+1:pos+5])[0]; pos += 5
    elif tag in (5, 6):
        cp[i] = None; pos += 9; i += 1
    elif tag == 9:   # Fieldref
        cp[i] = ('NT', struct.unpack('>H', data[pos+1:pos+3])[0], struct.unpack('>H', data[pos+3:pos+5])[0]); pos += 5
    elif tag in (10, 11):   # Methodref / InterfaceMethodref
        cp[i] = ('NT', struct.unpack('>H', data[pos+1:pos+3])[0], struct.unpack('>H', data[pos+3:pos+5])[0]); pos += 5
    elif tag in (12, 17, 18):  # NameAndType / Dynamic / InvokeDynamic
        cp[i] = ('NT', struct.unpack('>H', data[pos+1:pos+3])[0], struct.unpack('>H', data[pos+3:pos+5])[0]); pos += 5
    else:
        pos += 3
    i += 1

def resolve(idx):
    e = cp.get(idx)
    while isinstance(e, tuple) and e[0] == 'R':
        e = cp.get(e[1])
    return e

def arg_slots(desc):
    p = desc[1:desc.index(')')]
    n = 0; i = 0
    while i < len(p):
        c = p[i]
        if c == '[':
            while p[i] == '[': i += 1
            i += 1; n += 1
        elif c == 'L':
            i = p.index(';', i) + 1; n += 1
        elif c in 'DJ':
            n += 2; i += 1
        else:
            n += 1; i += 1
    return n

# ---- 定位 use() 的 Code ----
pos = 8 + 2 + 2          # magic(4) + minor(2) + major(2) + cp_count(2)
pos += 2                 # access_flags
pos += 2                 # this_class
pos += 2                 # super_class
interfaces = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2 + interfaces*2
fields = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
for _f in range(fields):
    pos += 6
    _n = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2 + _n*2
    cnt = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
    for _a in range(cnt):
        alen = struct.unpack('>I', data[pos+2:pos+6])[0]; pos += 6 + alen

methods = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
code = None
for _ in range(methods):
    name_i = struct.unpack('>H', data[pos+2:pos+4])[0]
    nm = cp.get(name_i)
    pos += 6
    cnt = struct.unpack('>H', data[pos:pos+2])[0]; pos += 2
    for _a in range(cnt):
        aname = resolve(struct.unpack('>H', data[pos:pos+2])[0])
        alen = struct.unpack('>I', data[pos+2:pos+6])[0]
        if aname == 'Code' and nm == 'm_7203_':
            code = data[pos+6:pos+6+alen]
        pos += 6 + alen

assert code is not None, "use() Code not found"
print("use() Code len:", len(code), "max_stack/max_locals:", struct.unpack('>HH', code[0:4]))
print()

SIMPLE = {
    0x2a: ('aload_0', 1, 1), 0x2b: ('aload_1', 1, 1), 0x2c: ('aload_2', 1, 1), 0x2d: ('aload_3', 1, 1),
    0x1a: ('iload_0', 1, 1), 0x1b: ('iload_1', 1, 1), 0x1c: ('iload_2', 1, 1), 0x1d: ('iload_3', 1, 1),
    0x22: ('fload_0', 1, 1), 0x23: ('fload_1', 1, 1), 0x24: ('fload_2', 1, 1), 0x25: ('fload_3', 1, 1),
    0x26: ('dload_0', 2, 2), 0x27: ('dload_1', 2, 2), 0x28: ('dload_2', 2, 2), 0x29: ('dload_3', 2, 2),
    0x3b: ('istore_0', 1, 0), 0x3c: ('istore_1', 1, 0), 0x3d: ('istore_2', 1, 0), 0x3e: ('istore_3', 1, 0),
    0x4b: ('astore_0', 1, 0), 0x4c: ('astore_1', 1, 0), 0x4d: ('astore_2', 1, 0), 0x4e: ('astore_3', 1, 0),
    0x43: ('fstore_0', 1, 0), 0x44: ('fstore_1', 1, 0), 0x45: ('fstore_2', 1, 0), 0x46: ('fstore_3', 1, 0),
    0x57: ('pop', 1, 0), 0x58: ('pop2', 2, 0), 0x59: ('dup', 1, 2), 0x5a: ('dup_x1', 1, 3),
    0x60: ('iadd', 2, 1), 0x62: ('fadd', 2, 1), 0x63: ('dadd', 4, 2),
    0x64: ('isub', 2, 1), 0x66: ('fsub', 2, 1), 0x67: ('dsub', 4, 2),
    0x68: ('imul', 2, 1), 0x6a: ('fmul', 2, 1), 0x6b: ('dmul', 4, 2),
    0x6e: ('fdiv', 2, 1), 0x6f: ('ddiv', 4, 2),
    0x74: ('fneg', 1, 1), 0x77: ('dneg', 2, 2),
    0x86: ('i2f', 1, 1), 0x87: ('i2d', 1, 2), 0x8b: ('f2i', 1, 1), 0x8d: ('f2d', 1, 2), 0x90: ('d2f', 2, 1),
    0x99: ('ifeq', 1, 0), 0x9a: ('ifne', 1, 0), 0x9b: ('iflt', 1, 0), 0x9c: ('ifge', 1, 0),
    0x9d: ('ifgt', 1, 0), 0x9e: ('ifle', 1, 0),
    0xa1: ('if_icmplt', 2, 0), 0xa2: ('if_icmpge', 2, 0), 0xa3: ('if_icmpgt', 2, 0),
    0xa4: ('if_icmple', 2, 0), 0xa5: ('if_icmpne', 2, 0), 0xa6: ('if_icmpeq', 2, 0),
    0xac: ('ireturn', 1, 0), 0xb0: ('areturn', 1, 0), 0xb1: ('return', 0, 0),
    0x01: ('aconst_null', 0, 1), 0x02: ('iconst_m1', 0, 1), 0x03: ('iconst_0', 0, 1), 0x04: ('iconst_1', 0, 1),
    0x05: ('iconst_2', 0, 1), 0x06: ('iconst_3', 0, 1), 0x07: ('iconst_4', 0, 1), 0x08: ('iconst_5', 0, 1),
    0x0b: ('fconst_0', 0, 1), 0x0c: ('fconst_1', 0, 1), 0x0d: ('fconst_2', 0, 1),
    0x0e: ('dconst_0', 0, 2), 0x0f: ('dconst_1', 0, 2),
}

i = 0
depth = 0
while i < len(code):
    op = code[i]
    if op == 0x12:
        v = cp.get(code[i+1]); print("%5d: ldc %s   -> %d" % (i, v, depth+1)); depth += 1; i += 2; continue
    if op == 0x13:
        idx = struct.unpack('>H', code[i+1:i+3])[0]; v = cp.get(idx)
        print("%5d: ldc_w %s   -> %d" % (i, v, depth+1)); depth += 1; i += 3; continue
    if op == 0x14:
        idx = struct.unpack('>H', code[i+1:i+3])[0]; v = cp.get(idx)
        print("%5d: ldc2_w %s   -> %d" % (i, v, depth+2)); depth += 2; i += 3; continue
    if op == 0x10:
        v = struct.unpack('>b', code[i+1:i+2])[0]
        print("%5d: bipush %d   -> %d" % (i, v, depth+1)); depth += 1; i += 2; continue
    if op in (0x15, 0x17, 0x19, 0x36, 0x38, 0x3a):
        idx = code[i+1]
        nm = {0x15: 'iload', 0x17: 'fload', 0x19: 'aload', 0x36: 'istore', 0x38: 'fstore', 0x3a: 'astore'}[op]
        d = 1 if op in (0x15, 0x17, 0x19) else -1
        print("%5d: %s %d   -> %d" % (i, nm, idx, depth+d)); depth += d; i += 2; continue
    if op == 0x84:
        i += 3; continue
    if op in (0xb6, 0xb7, 0xb8, 0xb9):
        idx = struct.unpack('>H', code[i+1:i+3])[0]
        e = cp.get(idx); nm = desc = cls = None
        if isinstance(e, tuple) and e[0] == 'NT':
            cls = resolve(e[1]); nt = cp.get(e[2])
            nm = cp.get(nt[1]); desc = cp.get(nt[2])
        tag = 'INVOKE' if op != 0xb8 else 'INVOKESTATIC'
        print("%5d: %s %s.%s%s   depth_BEFORE=%d" % (i, tag, cls, nm, desc, depth))
        if desc:
            n = arg_slots(desc) + (0 if op == 0xb8 else 1)
            r = 0 if desc.endswith('V') else 1
            depth = depth - n + r
        i += 3; continue
    if op in (0xb4, 0xb5):
        idx = struct.unpack('>H', code[i+1:i+3])[0]
        e = cp.get(idx); nm = desc = None
        if isinstance(e, tuple) and e[0] == 'NT':
            nt = cp.get(e[2]); nm = cp.get(nt[1]); desc = cp.get(nt[2])
        sz = 2 if desc and desc[0] in 'DJ' else 1
        if op == 0xb4:
            print("%5d: getfield %s%s   -> %d" % (i, nm, desc, depth)); depth += 0
        else:
            print("%5d: putfield %s%s   depth_BEFORE=%d" % (i, nm, desc, depth)); depth -= (1 + sz)
        i += 3; continue
    if op in (0xb2, 0xb3):
        idx = struct.unpack('>H', code[i+1:i+3])[0]
        e = cp.get(idx); desc = None
        if isinstance(e, tuple) and e[0] == 'NT':
            desc = cp.get(cp.get(e[2])[2])
        sz = 2 if desc and desc[0] in 'DJ' else 1
        if op == 0xb2: depth += sz
        else: depth -= sz
        i += 3; continue
    if op in (0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e):
        i += 3; depth -= 1; continue
    if op in (0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6):
        i += 3; depth -= 2; continue
    if op in (0xc6, 0xc7):
        i += 3; depth -= 1; continue
    if op in SIMPLE:
        nm, pop, push = SIMPLE[op]
        print("%5d: %s   -> %d" % (i, nm, depth - pop + push)) if nm.startswith(('f', 'd', 'pop', 'dup')) else None
        depth = depth - pop + push
        i += 1; continue
    if op in (0xbb, 0xbd, 0xc0, 0xc1):
        i += 3; depth += 1 if op in (0xbb, 0xbd, 0xc0) else 0; continue
    if op == 0xc5:
        i += 4; depth += 1; continue
    if op == 0xba:
        i += 5; depth = 0; continue
    if op == 0xaa or op == 0xab:
        i += 1
        pad = (4 - (i % 4)) % 4
        i += pad + 8
        continue
    i += 1
