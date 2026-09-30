#version 150

// Procedural deep-space panels for the 星象学 tooltips, one scene per theme, all animated by uTime:
//   uTheme 0  天体 celestial — a domain-warped violet/cyan/gold nebula.
//   uTheme 1  深空 deep space — the Pillars of Creation: dark columns, ionized rims, star-cluster backlight.
//   uTheme 2  虚无 void — a Gargantua-style black hole: shadow, Doppler-beamed disk, lensed halo arc.
// UV0 is 0..1 across the panel; uAspect keeps circles round and noise unstretched.

uniform vec4 ColorModulator;
uniform float uTime;
uniform vec2 uAspect;
uniform vec2 uPanelSize;   // the panel's size in screen pixels — the space bubbles are drawn round in
uniform float uTheme;
uniform float uIntro;   // 0 → 1 hover entrance progress; 1 once the tooltip has fully arrived

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = p * 2.02 + vec2(19.3, 7.7);
        a *= 0.5;
    }
    return v;
}

// ---- 天体: the original domain-warped nebula. ----
vec3 sceneCelestial(vec2 uv, float t) {
    vec2 p = uv * uAspect * 3.0;
    float tt = t * 0.06;
    vec2 warp = vec2(fbm(p + vec2(tt, -tt * 0.7)), fbm(p + vec2(-tt * 0.8, tt) + 5.2));
    float neb = fbm(p * 1.4 + warp * 1.6 + vec2(tt * 0.5, 0.0));
    float neb2 = fbm(p * 2.7 - warp * 1.1 - vec2(0.0, tt * 0.4));
    vec3 col = vec3(0.030, 0.020, 0.070);
    col = mix(col, vec3(0.10, 0.16, 0.42), smoothstep(0.35, 0.75, neb));
    col = mix(col, vec3(0.34, 0.16, 0.62), smoothstep(0.45, 0.95, neb) * 0.85);
    col += vec3(0.55, 0.24, 0.60) * smoothstep(0.6, 1.0, neb2) * 0.35;
    vec2 core = vec2(0.5 + 0.18 * sin(t * 0.11), 0.5 + 0.10 * cos(t * 0.09));
    float glow = exp(-dot((uv - core) * uAspect, (uv - core) * uAspect) * 6.0);
    col += vec3(0.45, 0.42, 0.75) * glow * 0.5;
    return col;
}

// ---- 深空: the Pillars of Creation — high-contrast columns backlit by a young star cluster. ----
vec3 scenePillars(vec2 uv, float t) {
    vec2 drift = vec2(0.0, -t * 0.015);
    // Glowing emission backdrop: teal body, amber and rust highlights.
    vec2 pg = uv * vec2(2.6, 1.9) + drift;
    vec2 w = vec2(fbm(pg + t * 0.05), fbm(pg + 5.0 - t * 0.04));
    float body = fbm(pg * 1.2 + w * 1.5);
    float hi = fbm(pg * 2.4 - w * 1.0 + 2.0);
    vec3 col = vec3(0.015, 0.03, 0.05);
    col = mix(col, vec3(0.05, 0.36, 0.34), smoothstep(0.30, 0.85, body));
    col = mix(col, vec3(0.66, 0.46, 0.16), smoothstep(0.55, 0.98, hi) * 0.9);
    col += vec3(0.42, 0.13, 0.08) * smoothstep(0.62, 1.0, body) * 0.40;
    // A young-cluster backlight glowing down from the top.
    vec2 lightPos = vec2(0.34, 0.04);
    float bl = exp(-dot((uv - lightPos) * vec2(1.3, 1.7), (uv - lightPos) * vec2(1.3, 1.7)) * 2.0);
    col += vec3(0.85, 0.70, 0.46) * bl * 0.8;
    // Dark molecular columns: Y-stretched noise, denser toward the base so they rise like pillars.
    vec2 cp = vec2(uv.x * 3.2, uv.y * 0.85) + vec2(fbm(vec2(uv.x * 2.0, uv.y * 0.6) + t * 0.02) * 0.5, -t * 0.01);
    float dens = fbm(cp) + (1.0 - uv.y) * 0.16;
    float pil = smoothstep(0.50, 0.63, dens);
    col = mix(col, vec3(0.035, 0.02, 0.045), pil);
    // Bright ionization rim along the lit edge of each column.
    float rim = clamp(smoothstep(0.46, 0.51, dens) - smoothstep(0.51, 0.60, dens), 0.0, 1.0);
    col += vec3(0.95, 0.62, 0.32) * rim * 1.5;
    // Glowing knots (EGGs) near the illuminated tips.
    float knot = smoothstep(0.58, 0.64, dens) * smoothstep(0.35, 0.0, uv.y);
    col += vec3(1.0, 0.82, 0.55) * knot * 0.6;
    return col;
}

// A jagged electric filament wrapping the hole at a wobbling radius, crackling on and off in bursts.
// Deliberately prominent — the user asked for the storm to READ. (This is the TOOLTIP black hole only;
// the summoned hole has no storm at all.)
float stormArc(float ang, float r, float baseR, float t, float seed) {
    float wob = fbm(vec2(ang * 3.0 + seed * 10.0, t * 1.6 + seed)) - 0.5;
    float wob2 = fbm(vec2(ang * 8.0 - seed * 4.0, t * 2.9 + seed * 2.0)) - 0.5;
    float rr = baseR + wob * 0.060 + wob2 * 0.024;
    float core = smoothstep(0.020, 0.0, abs(r - rr));       // the bright filament
    float wisp = smoothstep(0.050, 0.0, abs(r - rr)) * 0.30; // a soft sheath so it reads at a glance
    float flick = fbm(vec2(ang * 5.0 + seed * 20.0, floor(t * 9.0) * 1.31 + seed));
    float gate = smoothstep(0.38, 0.70, flick);             // on often enough to read as a storm
    return (core + wisp) * gate;
}

// ---- 虚无: a large purple-black hole set in violet cosmic gas, with a fierce Doppler-beamed disk. ----
vec3 sceneBlackHole(vec2 uv, float t, float intro) {
    vec2 c = uv - 0.5;
    c.x *= uAspect.x;                 // keep the hole round on wide panels
    float r = length(c);
    float ang = atan(c.y, c.x);

    // The shadow opens out of nothing as the tooltip arrives, then holds at its full (larger) size.
    float rs = 0.215 * mix(0.22, 1.0, intro);

    // ---- Nebula backdrop: thick violet gas, domain-warped and drifting. ----
    vec2 p = uv * uAspect * 3.2;
    float tt = t * 0.05;
    vec2 warp = vec2(fbm(p + vec2(tt, -tt * 0.7)), fbm(p + vec2(-tt * 0.6, tt) + 4.0));
    float neb = fbm(p * 1.3 + warp * 1.7);
    float neb2 = fbm(p * 2.6 - warp * 1.0 + 3.0);
    float neb3 = fbm(p * 5.1 + warp * 0.6 - vec2(0.0, tt));
    vec3 col = vec3(0.020, 0.008, 0.045);
    col = mix(col, vec3(0.11, 0.03, 0.24), smoothstep(0.28, 0.80, neb));
    col = mix(col, vec3(0.36, 0.11, 0.58), smoothstep(0.48, 0.98, neb) * 0.85);
    col += vec3(0.58, 0.22, 0.80) * smoothstep(0.60, 1.0, neb2) * 0.34;
    col += vec3(0.42, 0.16, 0.66) * smoothstep(0.66, 1.0, neb3) * 0.22;   // fine wisps
    // Gas dims as it nears the hole — light bends away from the shadow.
    col *= mix(0.32, 1.0, smoothstep(rs, rs + 0.30, r));

    // ---- Accretion disk: turbulent spiral arms, Doppler-beamed and blazing hot toward the centre. ----
    float lr = log(max(r, 0.001));
    float spin = t * 2.4 + (1.0 - intro) * 6.5;                 // spins up, then settles
    float arms = 0.5 + 0.5 * sin(2.0 * ang + lr * 10.0 - spin);
    float arms2 = 0.5 + 0.5 * sin(3.0 * ang + lr * 16.0 - spin * 1.3 + 1.7);
    float turb = fbm(vec2(ang * 2.6, lr * 4.2) - vec2(spin * 0.15, 0.0));
    float diskR = smoothstep(rs, rs + 0.014, r) * (1.0 - smoothstep(0.33, 0.60, r));
    float band = pow(mix(arms, arms2, 0.4), 1.5) * (0.55 + 0.45 * turb) * diskR;
    float doppler = 0.5 + 0.85 * (0.5 + 0.5 * cos(ang - 0.6));  // one side races toward us, far brighter
    band *= doppler;
    float heat = smoothstep(0.52, rs, r);
    vec3 diskCool = vec3(0.28, 0.08, 0.44);
    vec3 diskHot = vec3(1.10, 0.58, 1.20);                      // near-white violet core
    vec3 disk = mix(diskCool, diskHot, clamp(heat * 0.7 + band * 0.4, 0.0, 1.0));
    float boost = 1.0 + (1.0 - intro) * 2.2;                    // flares on entry
    col += disk * band * 1.55 * boost;

    // Lensed halo arc a little further out — the Gargantua ring over the top.
    float halo = smoothstep(0.020, 0.0, abs(r - (rs + 0.085)));
    col += vec3(0.55, 0.25, 0.88) * halo * (0.45 + 0.55 * arms) * 0.95 * boost;

    // Photon ring hugging the shadow — bright, the hole's burning rim — plus a hot inner breath.
    col += vec3(1.10, 0.78, 1.40) * smoothstep(0.016, 0.0, abs(r - (rs + 0.004))) * 2.00 * boost;
    float rr = (r - rs) * 8.0;
    col += vec3(0.45, 0.18, 0.74) * exp(-rr * rr) * 0.90;

    // ---- Devouring matter: motes of light spiralling in and dying at the horizon. ----
    for (int shell = 0; shell < 2; shell++) {
        float cells = 36.0 + 20.0 * float(shell);
        float cell = floor(fract(ang / 6.2831853 + 1.0) * cells);
        float h = hash(vec2(cell, float(shell) * 7.31 + 2.0));
        float h2 = hash(vec2(cell * 1.7, float(shell) * 3.7 + 9.0));
        float ft = fract(t * (0.22 + 0.26 * h) + h2);          // one fall per cycle
        float swirl = ft * ft * (2.6 + 1.8 * h2);              // twists faster as it nears the hole
        float pa = (cell + 0.30 + 0.40 * h) / cells * 6.2831853 - 3.14159265 + swirl;
        // The mote and two fading ghosts further up its path: a short trail of falling light.
        vec3 acc = vec3(0.0);
        for (int g = 0; g < 3; g++) {
            float ftg = fract(ft - float(g) * 0.050);
            float pg = pa - ftg * ftg * (2.6 + 1.8 * h2);
            float radg = mix(rs + 0.17, rs + 0.0035, ftg);
            float d = length(c - vec2(cos(pg), sin(pg)) * radg);
            acc += vec3(0.95, 0.80, 1.10) * smoothstep(0.048, 0.0, d) * (1.0 - float(g) * 0.38);
        }
        col += acc * (0.55 + 1.60 * ft) * boost;               // burns brighter the deeper it falls
    }

    // ---- Cosmic storm: electric arcs crackling around the hole, surging on entry. ----
    float storm = 0.0;
    storm += stormArc(ang, r, rs + 0.075, t, 1.0);
    storm += stormArc(ang, r, rs + 0.130, t, 5.0);
    storm += stormArc(ang, r, rs + 0.195, t, 9.0);
    storm *= 1.0 - smoothstep(0.34, 0.52, r);               // fade the storm out toward the edges
    float surge = 1.0 + (1.0 - intro) * 2.5;                // a burst of lightning as it arrives
    col += vec3(0.55, 0.48, 1.0) * storm * 2.1 * surge;     // violet electric glow
    col += vec3(0.90, 0.96, 1.0) * storm * 1.15 * surge;    // hot blue-white core
    // A faint electric haze breathing over the disk region.
    float haze = smoothstep(rs, rs + 0.02, r) * (1.0 - smoothstep(0.30, 0.5, r));
    col += vec3(0.35, 0.30, 0.72) * haze * (0.14 + 0.14 * sin(t * 6.0 + ang * 3.0)) * surge;

    // Event horizon: pure void.
    col *= smoothstep(rs - 0.006, rs + 0.006, r);
    return col;
}

// ---- 拉莱耶: the abyss — deep blue water, sunken light, rising bubbles, and something vast asleep below. ----
// uv is the settled (sink-shifted) coordinate the scene is drawn in; uv0 is the raw panel coordinate,
// which the bubbles use so their climb never slides when the panel sinks in.
vec3 sceneAbyss(vec2 uv, float t, vec2 uv0) {
    // The water itself: deep blue, dying to black with depth.
    float depth = smoothstep(0.0, 1.0, uv.y);
    vec3 col = mix(vec3(0.020, 0.070, 0.150), vec3(0.002, 0.008, 0.022), depth * 0.88);

    // Water-caustic shimmer, warped like the surface seen from far below.
    vec2 wp = uv * vec2(uAspect.x, 1.0) * vec2(3.4, 2.4) + vec2(0.0, t * 0.021);
    vec2 w = vec2(fbm(wp + vec2(t * 0.024, 0.0)), fbm(wp + vec2(0.0, -t * 0.017) + 4.7));
    float caustic = fbm(wp * 1.7 + w * 1.9);
    col += vec3(0.10, 0.36, 0.72) * smoothstep(0.52, 0.94, caustic) * (1.0 - depth) * 0.38;

    // Sunken light: slow swaying shafts pouring down from a surface you will never reach.
    float ang = uv.x * 7.0 + sin(t * 0.13) * 0.8 + w.x * 2.0;
    float rays = pow(0.5 + 0.5 * sin(ang * 2.3 + t * 0.19), 3.0)
            + 0.6 * pow(0.5 + 0.5 * sin(ang * 4.7 - t * 0.11 + 1.7), 4.0);
    col += vec3(0.24, 0.52, 0.98) * rays * (1.0 - depth) * (1.0 - depth) * 0.13;

    // Marine snow: two layers of pale motes drifting down through the blue.
    for (int layer = 0; layer < 2; layer++) {
        float scale = layer == 0 ? 24.0 : 42.0;
        vec2 g = uv * vec2(uAspect.x, 1.0) * scale;
        g.y += t * (layer == 0 ? 0.34 : 0.58);
        g.x += sin(t * 0.21 + g.y * 0.9) * 0.5;
        vec2 cell = floor(g);
        float h = hash(cell + float(layer) * 17.0);
        if (h > 0.93) {
            float d = length(fract(g) - 0.5);
            float tw = 0.35 + 0.65 * sin(t * (0.7 + h * 2.4) + h * 60.0);
            col += vec3(0.55, 0.78, 1.0) * smoothstep(0.28, 0.0, d) * tw * (layer == 0 ? 0.16 : 0.10);
        }
    }

    // Bubbles: sparse columns letting beads go, climbing and wobbling toward a surface they never
    // reach. Distances are taken in PANEL PIXELS — true circles at any panel shape, never stretched
    // by the panel's aspect — and their phase runs on its own clock, so the sink-in never slides
    // them. Small: a bubble is a bead of air, a few pixels across, not a balloon.
    for (int bc = 0; bc < 2; bc++) {
        float seed = float(bc) * 12.7 + 5.0;
        float colX = 0.24 + 0.38 * bc + 0.05 * sin(seed);
        float speed = 0.05 + 0.03 * hash(vec2(seed, 1.0));
        float yy = fract(t * speed + hash(vec2(seed, 2.0)));
        float bx = colX + 4.0 * sin(t * (0.8 + seed * 0.1) + yy * 14.0) / uPanelSize.x;
        vec2 bp = (uv0 - vec2(bx, 1.0 - yy)) * uPanelSize;
        float d = length(bp);
        float bead = smoothstep(2.2, 0.8, d);
        float ring = smoothstep(2.7, 1.8, d) - smoothstep(1.8, 1.0, d);
        float life = sin(yy * 3.14159);
        col += vec3(0.55, 0.80, 1.0) * (bead * 0.22 + ring * 0.34) * life;
    }

    // Faint deep glows: cold patches of light that breathe far below, where the lanterns drift.
    for (int gl = 0; gl < 2; gl++) {
        float seed = float(gl) * 21.3 + 9.0;
        vec2 gp = vec2(0.28 + 0.44 * gl + 0.05 * sin(t * 0.07 + seed),
                       0.62 + 0.16 * hash(vec2(seed, 3.0)) + 0.03 * sin(t * 0.11));
        vec2 gd = (uv - gp) * uPanelSize;
        float breathe = 0.5 + 0.5 * sin(t * (0.35 + gl * 0.12) + seed);
        col += vec3(0.16, 0.40, 0.80) * exp(-dot(gd, gd) / 900.0) * breathe * 0.55;
    }

    // The shape below: a vast silhouette drifting across the deep, its wake bending the light.
    float drift = sin(t * 0.045) * 0.30;
    vec2 sc = uv - vec2(0.5 + drift, 0.88);
    sc.x *= uAspect.x;
    float mass = smoothstep(0.55, 0.16, length(sc * vec2(1.0, 2.8)));
    float ridge = 0.030 * sin(sc.x * 11.0 + t * 0.28) * smoothstep(0.55, 0.0, abs(sc.x));
    float fin = smoothstep(0.05, 0.0, abs(sc.y - ridge)) * smoothstep(0.50, 0.12, abs(sc.x));
    col = mix(col, vec3(0.0), clamp(mass * 0.92 + fin * 0.55, 0.0, 1.0));

    // And in the dark of it: a pair of cold blue eyes, breathing slow — seen, never named. Their
    // spacing is in panel pixels too, so they stay round and the same size on every tooltip.
    vec2 eo = (uv0 - vec2(0.5 + drift, 0.845)) * uPanelSize;
    float eyes = 0.0;
    for (int i = 0; i < 2; i++) {
        float side = i == 0 ? -1.0 : 1.0;
        eyes += smoothstep(3.2, 0.6, length(eo - vec2(side * 5.0, 0.0)));
    }
    float blink = pow(0.5 + 0.5 * sin(t * 0.45), 6.0);
    col += vec3(0.45, 0.75, 1.0) * eyes * blink * 0.9;

    return col;
}

void main() {
    // Hover entrance: 虚无 keeps its show — the zoom home, the flash and the expanding shockwave
    // ring. 拉莱耶 has its own: the panel settles downward as if sinking to rest, out of total black,
    // with a faint light waking below. The rest appear plainly (a hair's fade so they never pop).
    bool voidFx = uTheme > 1.5 && uTheme < 2.5;
    bool abyssFx = uTheme > 2.5;
    float intro = clamp(uIntro, 0.0, 1.0);
    float e = 1.0 - pow(1.0 - intro, 3.0);
    vec2 uv = voidFx ? 0.5 + (texCoord0 - 0.5) * mix(0.84, 1.0, e) : texCoord0;
    // The sink: the whole scene drifts down a short way into place, so the water appears to rise
    // past the panel as it settles — kept small, so the scene reads as sinking, not sliding.
    if (abyssFx) {
        uv.y -= (1.0 - e) * 0.045;
    }
    float t = uTime;
    vec3 col;
    if (uTheme < 0.5) {
        col = sceneCelestial(uv, t);
    } else if (uTheme < 1.5) {
        col = scenePillars(uv, t);
    } else if (uTheme < 2.5) {
        col = sceneBlackHole(uv, t, e);
    } else {
        col = sceneAbyss(uv, t, texCoord0);
    }

    // Twinkling foreground stars for the nebula and the pillars (the black hole and the abyss draw
    // their own snow).
    if (uTheme < 1.5) {
        for (int layer = 0; layer < 2; layer++) {
            float scale = layer == 0 ? 60.0 : 110.0;
            vec2 g = uv * uAspect * scale;
            vec2 cell = floor(g);
            float h = hash(cell + float(layer) * 37.0);
            if (h > 0.90) {
                float d = length(fract(g) - 0.5);
                float tw = 0.5 + 0.5 * sin(uTime * (1.5 + h * 3.0) + h * 40.0);
                col += vec3(0.85, 0.90, 1.0) * smoothstep(0.32, 0.0, d) * tw * (layer == 0 ? 0.9 : 0.55);
            }
        }
    }

    // Vignette beds the panel into the frame instead of ending flat.
    float vig = smoothstep(1.15, 0.35, length((uv - 0.5) * vec2(2.0, 1.0)));
    col *= mix(0.40, 1.0, vig);

    // The void's entrance burst: a flash and an expanding shockwave ring, fading as the scene settles.
    if (voidFx) {
        float rad = length((uv - 0.5) * vec2(uAspect.x, 1.0));
        col += vec3(0.62, 0.52, 1.0) * exp(-intro * 5.5) * 0.55;
        float shock = smoothstep(0.06, 0.0, abs(rad - e * 0.95)) * (1.0 - e);
        col += vec3(0.82, 0.72, 1.0) * shock * 0.8;
    }

    // The abyss entrance: out of total black, a drowned light wakes. The panel brightens as it
    // settles — the wake is smooth, and only a pair of brief guttering pulses plays over it, like
    // the deep answering a light a heartbeat at a time. Never dark enough to read as a glitch.
    // (texCoord0 is the raw panel coordinate — the pulse sweeps the panel, not the sunk scene.)
    if (abyssFx) {
        float wake = smoothstep(0.0, 0.85, intro);                // black → lit
        col *= mix(0.30, 1.0, wake);
        // Two soft pulses of pale light sweeping up while the panel is still waking.
        float pulsePhase = fract(intro * 1.6);
        float pulse = smoothstep(0.25, 0.0, abs(texCoord0.y - (1.0 - pulsePhase)))
                * (1.0 - wake) * 0.7;
        col += vec3(0.10, 0.26, 0.44) * pulse;
        // A cold glow welling up from the deep below as it lights, brightest while still mostly dark.
        float rise = smoothstep(1.0, 0.35, texCoord0.y) * (1.0 - wake);
        col += vec3(0.04, 0.14, 0.30) * rise * 0.9;
    }

    float introSpan = voidFx ? 0.20 : (abyssFx ? 0.24 : 0.08);
    float alpha = 0.96 * smoothstep(0.0, introSpan, intro);
    fragColor = vec4(col, alpha) * ColorModulator * vertexColor;
}

