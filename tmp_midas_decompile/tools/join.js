const fs = require('fs');
const BASE = 'C:/Users/MECHREVO/AppData/Local/Temp/midas_cfr/';

// Convert a JVM type descriptor element to Java source form, remapping obfuscated class names to official.
const PRIMS = {Z:'boolean',B:'byte',C:'char',D:'double',F:'float',I:'int',J:'long',S:'short',V:'void'};
function typeToSource(d, remap) {
  let depth = 0;
  while (d[depth] === '[') depth++;
  let idx = depth;
  let base;
  const c = d[idx];
  if (c === 'L') { const end = d.indexOf(';', idx); base = d.slice(idx + 1, end).replace(/\//g, '.'); idx = end + 1; }
  else { base = PRIMS[c] || c; idx++; }
  if (remap && !PRIMS[c]) {
    const off = remap.get(base);
    if (off) base = off;
  }
  return base + '[]'.repeat(depth);
}
// Convert JVM method descriptor like (Lnet/...;F)Z to {params:'net...,float', ret:'boolean'}
function descToSource(desc, remap) {
  if (!desc) return { params: '', ret: 'void' };
  const m = desc.match(/^\((.*)\)(.*)$/);
  if (!m) return { params: '', ret: 'void' };
  const args = m[1], ret = m[2];
  const params = [];
  let i = 0;
  while (i < args.length) {
    if (args[i] === '[') { let j = i; while (args[j] === '[') j++; if (args[j] === 'L') { j = args.indexOf(';', j) + 1; } else j++; params.push(typeToSource(args.slice(i, j), remap)); i = j; }
    else if (args[i] === 'L') { const end = args.indexOf(';', i) + 1; params.push(typeToSource(args.slice(i, end), remap)); i = end; }
    else { params.push(typeToSource(args[i], remap)); i++; }
  }
  return { params: params.join(','), ret: ret ? typeToSource(ret, remap) : 'void' };
}

// client.txt: official -> obf
const clientLines = fs.readFileSync(BASE + 'client.txt', 'utf8').split('\n');
const classObfToOfficial = {};
const fieldObfToOfficial = {};   // class|obfField -> officialField
const methodObfToOfficial = {};  // class|obfMethod|desc -> officialMethod
let curObfClass = null;
for (const line of clientLines) {
  const cls = line.match(/^([\w/$\.]+) -> ([\w/$]+):$/);
  if (cls) { curObfClass = cls[2]; classObfToOfficial[curObfClass] = cls[1]; continue; }
  if (!curObfClass) continue;
  const meth = line.match(/^\s+\d+:\d+:\S+ ([\w$<>]+)\((.*)\) -> ([\w$]+)$/);
  if (meth) { methodObfToOfficial[curObfClass + '|' + meth[3] + '|' + meth[2]] = meth[1]; continue; }
  const fld = line.match(/^\s+\S+ ([\w$<>]+) -> ([\w$]+)$/);
  if (fld) { fieldObfToOfficial[curObfClass + '|' + fld[2]] = fld[1]; continue; }
}

// joined.tsrg: obf -> srg
const tsrgLines = fs.readFileSync(BASE + 'config/joined.tsrg', 'utf8').split('\n');
const srgFieldToObf = {};    // srgField -> obfClass|obfField
const srgMethodToObf = {};   // srgMethod -> obfClass|obfMethod|desc
let curTsrgClass = null;
for (const line of tsrgLines) {
  if (!line.startsWith('\t')) {
    const cls = line.match(/^([\w/$]+) ([\w/$]+) \d+$/);
    if (cls) { curTsrgClass = cls[1]; continue; }
  } else if (!line.startsWith('\t\t')) {
    const t = line.trim();
    const m = t.match(/^([\w$<>]+) \(([^)]*)\)(\S+) ([\w$]+) \d+$/);
    const f = t.match(/^([\w$<>]+) ([\w$]+) \d+$/);
    if (m && curTsrgClass) {
      const k = String(m[4]);
      if (!Array.isArray(srgMethodToObf[k])) srgMethodToObf[k] = [];
      srgMethodToObf[k].push(curTsrgClass + '|' + m[1] + '|(' + m[2] + ')' + m[3]);
    }
    else if (f && curTsrgClass) srgFieldToObf[f[2]] = curTsrgClass + '|' + f[1];
  }
}

const fieldSrgToOfficial = new Map();
for (const [srg, key] of Object.entries(srgFieldToObf)) {
  const [c, obf] = key.split('|');
  const off = fieldObfToOfficial[c + '|' + obf];
  if (off) fieldSrgToOfficial.set(srg, off);
}
const methodSrgToOfficial = new Map();
const remap = new Map(Object.entries(classObfToOfficial));
for (const [srg, keys] of Object.entries(srgMethodToObf)) {
  let found = null;
  for (const key of keys) {
    const [c, obf, desc] = key.split('|');
    const src = descToSource(desc, remap);
    let off = methodObfToOfficial[c + '|' + obf + '|' + desc];
    if (!off) off = methodObfToOfficial[c + '|' + obf + '|' + src.params];
    if (off) { found = off; break; }
  }
  if (found) methodSrgToOfficial.set(srg, found);
}
console.log('fields joined:', fieldSrgToOfficial.size, ' methods joined:', methodSrgToOfficial.size);

const fields = [
  'f_22276_','f_22277_','f_22278_','f_22279_','f_22280_','f_22281_','f_22284_',
  'f_268612_','f_268722_','f_268464_','f_268566_','f_268511_',
  'f_268745_','f_276146_','f_268524_','f_268415_','f_44978_','f_21641_'
];
const methods = [
  'm_21233_','m_21223_','m_5634_','m_6469_','m_6475_','m_142535_','m_6673_','m_21153_',
  'm_142687_','m_6336_','m_5448_','m_6710_','m_21530_','m_20242_','m_8615_','m_8606_',
  'm_269533_','m_276093_'
];
console.log('--- fields ---');
for (const t of fields) console.log(t, '=>', fieldSrgToOfficial.get(t) || 'NOT FOUND');
console.log('--- methods ---');
for (const t of methods) console.log(t, '=>', methodSrgToOfficial.get(t) || 'NOT FOUND');
