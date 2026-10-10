#version 150

// Procedural deep-space panels for the 星象学 tooltips, one scene per theme, all animated by uTime:
//   uTheme 0  天体 celestial — a domain-warped violet/cyan/gold nebula.
//   uTheme 1  深空 deep space — the Pillars of Creation: dark columns, ionized rims, star-cluster backlight.
//   uTheme 2  虚无 void — a Gargantua-style black hole: shadow, Doppler-beamed disk, lensed halo arc.
// UV0 is 0..1 across the panel; uAspect keeps circles round and noise unstretched.
//
// ⚠ uPanelSize is in GUI UNITS, not physical pixels, and NOTHING in this file should be sized with
// it. GameRenderer#renderLevel builds the GUI ortho as
//     setOrtho(0, window.getWidth() / guiScale, window.getHeight() / guiScale, 0, 1000, far)
// so GUI coordinates ARE shader coordinates with no separate pose scale, and the numbers handed over
// are the tooltip's own extent in that space — around 113 x 45 for a real three-line tooltip, not the
// 522 x 138 a GUI-scale-3 reading would give. Anything measured in those units therefore changes
// APPARENT size whenever the tooltip gets longer or shorter, which is how 拉莱耶's three lamp glows
// came to cover half the panel and wash it out — until they were themselves deleted on 10-10 for
// exactly that. Size things with `uv * uAspect` instead — the short
// side is 1.0, so a radius there is a fraction of the panel and survives every tooltip.

uniform vec4 ColorModulator;
uniform float uTime;
uniform vec2 uAspect;
uniform vec2 uPanelSize;   // GUI units — read the warning above before using this for anything
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

// ---- Entrance vocabulary, shared by all four scenes --------------------------------------------
// Each scene arrives on its OWN choreography — a nebula whose star ignites, a structure lit from
// behind, a hole that opens, deep water that fades up — so these are only the two primitives they have
// in common: a stage that comes up over a slice of the entrance, and a strike that peaks and dies.
//
// THE CLOCK IS THE RAW ONE. `intro` here is the un-eased 0→1 hover progress, so a stage's two numbers
// are fractions of the whole entrance: at the shipped 780 ms, 0.36 is 280 ms. That matters and was
// worth getting wrong once to find out — the first version read these stages off the eased value that
// main() uses for the camera, and a cubic-out ease reaches 0.80 by the time the raw clock says 0.42,
// so every late beat collapsed into the first third of the entrance and the last two thirds of the
// panel's arrival had nothing left in it.
float stageUp(float intro, float a, float b) {
    return smoothstep(a, b, intro);
}

// A one-shot: 1 at `at`, falling away to nothing within `width` either side. Every scene's turning
// point is one of these — the star's ignition, the horizon's snap, a star lighting.
float strike(float intro, float at, float width) {
    float d = (intro - at) / width;
    return exp(-d * d);
}

// ---- 天体: a domain-warped nebula with a primary star at its heart. ----
vec3 sceneCelestial(vec2 uv, float t, float intro) {
    vec2 p = uv * uAspect * 3.0;
    float tt = t * 0.06;
    vec2 warp = vec2(fbm(p + vec2(tt, -tt * 0.7)), fbm(p + vec2(-tt * 0.8, tt) + 5.2));
    float neb = fbm(p * 1.4 + warp * 1.6 + vec2(tt * 0.5, 0.0));
    float neb2 = fbm(p * 2.7 - warp * 1.1 - vec2(0.0, tt * 0.4));

    // THE ENTRANCE. 天体 holds the only subject in the four panels that is itself a LIGHT SOURCE, so
    // it is the only one that can arrive by switching on. The gas condenses out of black first; then
    // the star IGNITES — a flare that overshoots and decays, four spikes that shoot out of the core
    // from nothing, and the shell of lit gas expanding from the star outward to its resting radius,
    // which is what makes the ring read as something the star did rather than a shape that was always
    // there. The companion lights last, so the panel gains a second subject instead of arriving with
    // two at once and having nowhere to go.
    float gas    = stageUp(intro, 0.00, 0.55);
    float ignite = stageUp(intro, 0.28, 0.50);
    // Narrow: a wider strike left the panel a flat washed blob for a third of the entrance, which is
    // the same read as no entrance at all. An ignition is a snap, not a sunrise.
    float flare  = strike(intro, 0.36, 0.11);
    float spikeT = stageUp(intro, 0.30, 0.78);
    float shellT = stageUp(intro, 0.33, 0.86);
    float mate   = stageUp(intro, 0.62, 0.92);

    vec3 col = vec3(0.030, 0.020, 0.070) * gas;
    col = mix(col, vec3(0.10, 0.16, 0.42), smoothstep(0.35, 0.75, neb) * gas);
    col = mix(col, vec3(0.34, 0.16, 0.62), smoothstep(0.45, 0.95, neb) * 0.85 * gas);
    col += vec3(0.55, 0.24, 0.60) * smoothstep(0.6, 1.0, neb2) * 0.35 * gas;

    // The panel's SUBJECT. It used to be gas and nothing else — a fog with no focal point, which
    // read as thin next to the pillars, the hole and the deep. So: one primary star, hung off
    // centre and drifting, with four diffraction spikes and a face-on shell of gas it has lit.
    //
    // The shell is what keeps this from being mistaken for 虚无: this ring is LIT through the middle
    // and sits FACE-ON, the hole's is a dark disc ringed by a blazing disk. Same vocabulary of
    // "a ring", two completely different objects. (This used to say the hole's disk was EDGE-ON and
    // black through the middle — true of the revision that was reverted on 10-09, false of the disk
    // that is there now, which is the original concentric one.)
    vec2 sp = vec2(0.63 + 0.045 * sin(t * 0.043), 0.40 + 0.035 * cos(t * 0.037));
    vec2 d = (uv - sp) * uAspect;
    float r = length(d);
    // The wide bloom the nebula is lit by, then the tight bead of the star inside it. Both ride the
    // ignition; the flare rides on top of them, and the white wash lights the gas the star is sitting
    // in — which is the whole point of the beat: for a fifth of a second the panel has a sun in it.
    col += vec3(0.45, 0.42, 0.75) * exp(-r * r * 7.0) * 0.55 * ignite;
    col += vec3(1.00, 0.96, 0.88) * exp(-r * r * 900.0) * (1.30 * ignite + 3.00 * flare);
    col += vec3(0.72, 0.78, 1.00) * flare * 0.62 * (1.0 - smoothstep(0.0, 0.55, r));
    // Diffraction spikes: bright along the two axes, gone within a couple of pixels of them, and
    // killed by distance so they read as a star's flare and not as a cross drawn on the panel. The
    // falloff constant is divided by the growth so they EXTEND out of the core rather than fading in
    // at full length — a spike shooting out, not a cross appearing.
    float spikeGrow = 0.22 + 0.78 * spikeT;
    float spike = (exp(-abs(d.x) * 110.0 / spikeGrow) + exp(-abs(d.y) * 110.0 / spikeGrow))
                * exp(-r * 9.0) * spikeT;
    col += vec3(0.74, 0.86, 1.00) * spike * 0.60;
    // The shell: a soft face-on ring of gas, brighter where the nebula behind it is thickest. It
    // expands out of the star and settles, and it is brightest on the way out — a wavefront, then a
    // ring of lit gas.
    float shellR = 0.012 + 0.078 * shellT;
    float shell = 1.0 - smoothstep(0.0, 0.034, abs(r - shellR));
    col += vec3(0.52, 0.38, 0.96) * shell * (0.30 + 0.18 * smoothstep(0.4, 0.9, neb))
         * (0.55 + 0.45 * shellT) * shellT;
    // A fainter companion, so the eye has somewhere else to go and the pair reads as a sky. It
    // lights with a flare of its own, small and a beat later.
    vec2 d2 = (uv - vec2(0.27 + 0.030 * cos(t * 0.05), 0.70 + 0.030 * sin(t * 0.06))) * uAspect;
    float r2 = dot(d2, d2);
    col += vec3(0.80, 0.86, 1.00) * exp(-r2 * 1600.0) * 0.75 * mate;
    col += vec3(0.80, 0.86, 1.00) * exp(-r2 * 260.0) * 0.40 * strike(intro, 0.72, 0.16);
    return col;
}

// ---- 深空: the Pillars of Creation — high-contrast columns backlit by a young star cluster. ----
vec3 scenePillars(vec2 uv, float t, float intro) {
    vec2 drift = vec2(0.0, -t * 0.015);

    // THE ENTRANCE. This is the one panel whose subject is not a light source but a STRUCTURE, and a
    // backlit structure arrives the way a photograph of one does: the backlight goes up first and the
    // frame is briefly nothing but glow, THEN the columns climb up into it out of the bottom of the
    // frame, and only after that do their ionization rims catch — rim light is the last thing to
    // appear on something being lit. The stars come last of all, because they are the light source
    // and a light source has no business being the first thing you see.
    float glowT = stageUp(intro, 0.00, 0.45);
    float rise  = stageUp(intro, 0.12, 0.72);      // the reveal front, travelling bottom → top
    float rimT  = stageUp(intro, 0.50, 0.92);
    float knotT = stageUp(intro, 0.62, 0.95);

    // Glowing emission backdrop: teal body, amber and rust highlights.
    vec2 pg = uv * vec2(2.6, 1.9) + drift;
    vec2 w = vec2(fbm(pg + t * 0.05), fbm(pg + 5.0 - t * 0.04));
    float body = fbm(pg * 1.2 + w * 1.5);
    float hi = fbm(pg * 2.4 - w * 1.0 + 2.0);
    vec3 col = vec3(0.015, 0.03, 0.05) * glowT;
    col = mix(col, vec3(0.05, 0.36, 0.34), smoothstep(0.30, 0.85, body) * glowT);
    col = mix(col, vec3(0.66, 0.46, 0.16), smoothstep(0.55, 0.98, hi) * 0.9 * glowT);
    col += vec3(0.42, 0.13, 0.08) * smoothstep(0.62, 1.0, body) * 0.40 * glowT;
    // A young-cluster backlight glowing down from the top — the first thing in the panel to exist.
    vec2 lightPos = vec2(0.34, 0.04);
    float bl = exp(-dot((uv - lightPos) * vec2(1.3, 1.7), (uv - lightPos) * vec2(1.3, 1.7)) * 2.0);
    col += vec3(0.85, 0.70, 0.46) * bl * 0.8 * glowT;
    // Dark molecular columns: Y-stretched noise, denser toward the base so they rise like pillars.
    vec2 cp = vec2(uv.x * 3.2, uv.y * 0.85) + vec2(fbm(vec2(uv.x * 2.0, uv.y * 0.6) + t * 0.02) * 0.5, -t * 0.01);
    float dens = fbm(cp) + (1.0 - uv.y) * 0.16;
    // The rising front: uv.y grows downward, so the columns exist BELOW it and the front starts off
    // the bottom edge of the frame and climbs past the top. Ascending edges only — the reversed form
    // is undefined per the GLSL spec.
    float front = 1.12 - 1.38 * rise;
    float grow  = smoothstep(front - 0.10, front + 0.10, uv.y);
    float pil = smoothstep(0.50, 0.63, dens) * grow;
    col = mix(col, vec3(0.035, 0.02, 0.045), pil);
    // Bright ionization rim along the lit edge of each column — lit after the column itself is there,
    // and only on the part of it that has risen into frame.
    float rim = clamp(smoothstep(0.46, 0.51, dens) - smoothstep(0.51, 0.60, dens), 0.0, 1.0);
    col += vec3(0.95, 0.62, 0.32) * rim * 1.5 * rimT * grow;
    // Glowing knots (EGGs) near the illuminated tips.
    float knot = smoothstep(0.58, 0.64, dens) * smoothstep(0.35, 0.0, uv.y);
    col += vec3(1.0, 0.82, 0.55) * knot * 0.6 * knotT * grow;
    return col;
}

// ⚠ TOMBSTONE: `stormArc` and its three filaments were DELETED on 10-09, by request —
// "黑洞我讨厌的是这个 … 会不断闪烁的", with the pale flickering chunks on the disk circled.
//
// It was called "the cosmic storm: electric arcs crackling around the hole". It did not read as
// lightning, and the reason is arithmetic, not taste, which is why it is written down here instead of
// simply removed:
//
//   1. THE FILAMENT WAS THINNER THAN THE NOISE THAT DISPLACED IT. The radial profile was a band of
//      half-width 0.020 (`smoothstep(0.020, 0.0, abs(r - rr))`), while `rr = baseR + wob * 0.060 +
//      wob2 * 0.024` let the noise move it by up to ±0.030 — a band and a half. Wherever the noise
//      sloped steeply, neighbouring angular samples landed more than a band-width apart and the line
//      BROKE INTO DISCONNECTED PIECES. That is the "奇形怪状的斑": not arcs, shredded debris.
//   2. THE DEBRIS STROBED. The on/off gate sampled its fbm at `ang * 5.0` — about 31 cells round the
//      circumference, so the chunks were small — and its second coordinate was `floor(t * 9.0)`, so
//      the ENTIRE pattern re-rolled nine times a second. Nothing about the shape survived a frame.
//   3. IT WAS PALE AND BRIGHT, at ×2.1 and ×1.15 on top of a violet disk, so it was the first thing
//      the eye landed on.
//
// The same trap still applies elsewhere in this file: `fbm` is VALUE noise, so sampling it at a
// multiple of an angle is NOT periodic in that angle — integer multiple or not. `ang` wraps at ±π and
// floor(kπ + c) ≠ floor(−kπ + c), so there is a step down one side of the panel. Only sin/cos of
// integer harmonics close properly — the abyss lamp loop that was the standing example of this is
// gone, the rule is not. `turb` in sceneBlackHole is sampled at `ang * 2.6` and still carries that
// seam; it is invisible because the fbm there only nudges brightness rather than displacing a line.
//
// If lightning is ever wanted back, it has to be built the other way round: a band WIDER than its own
// displacement, harmonics instead of fbm, and a gate that fades rather than re-rolls.

// ---- 虚无: a large purple-black hole set in violet cosmic gas, with a fierce Doppler-beamed disk. ----
//
// ⚠ RESTORED VERBATIM AND IN FULL — disk, storm, motes and all.
// User, 10-09, in two steps: first "虚无那个还不如我要你改之前的样子" (the entrance), then
// "你做的新盘丑的要死" (the disk). Both halves are now the pre-choreography drawing again.
//
// What this replaces, so it is not re-invented: the disk had been rebuilt as a thin EDGE-ON ellipse —
// its near side crossing in front of the shadow, far-side/near-side occlusion as an explicit order,
// Keplerian shear (ω ∝ R^-1.5 per filament ring), and red/blue Doppler tinting, with all distances
// measured in screen units so the bands stayed an even width round the ellipse. All of that is gone.
// What is here is the original: every element a function of SCREEN RADIUS from the hole's centre, so
// disk, lensed arc, photon ring, motes and storm are concentric — a broad violet ring round a dark
// middle, with one directional brightness term (`doppler`) rather than a colour shift.
//
// THE ONLY EDIT IS THE CLOCK. The old main() handed this function the EASED progress `e`, because that
// is what drove the single beat it has. This file's standing rule is that every scene function takes the
// RAW clock (see stageUp), so the ease is computed here instead: `open` below is exactly what `intro`
// was when the old main() called it. Nothing else is touched.
vec3 sceneBlackHole(vec2 uv, float t, float intro) {
    // This scene's eased clock. Single beat ⇒ nothing for an ease to collapse, which is why it may
    // ease at all while the other three may not.
    float open = 1.0 - pow(1.0 - intro, 3.0);

    vec2 c = (uv - 0.5) * uAspect;    // keep the hole round at ANY panel shape, tall or wide
    float r = length(c);
    float ang = atan(c.y, c.x);

    // The shadow opens out of nothing as the tooltip arrives, then holds at its full (larger) size.
    float rs = 0.215 * mix(0.22, 1.0, open);

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
    // Gas dims as it nears the hole — light bends away from the shadow. On the ANIMATING radius, so the
    // dimming travels outward with the shadow during the zoom.
    col *= mix(0.32, 1.0, smoothstep(rs, rs + 0.30, r));

    // ---- Accretion disk: turbulent spiral arms, Doppler-beamed and blazing hot toward the centre. ----
    float lr = log(max(r, 0.001));
    float spin = t * 2.4 + (1.0 - open) * 6.5;                  // spins up, then settles
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
    float boost = 1.0 + (1.0 - open) * 2.2;                     // flares on entry
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

    // ---- The breath of the disk: a soft haze hanging just outside the horizon. ----
    // ⚠ THE COSMIC STORM IS GONE, by request (10-09): "黑洞我讨厌的是这个 … 会不断闪烁的", with the
    // pale chunks on the disk circled. Those circles were the three arcs below — see the tombstone
    // where `stormArc` used to be for exactly what was wrong with them. Deleted, not toned down.
    //
    // This haze is a different animal and stays: it is a smooth wash with no edges, it breathes
    // slowly (6 rad/s against a 3-fold azimuth term), and it is dim. Measured, not assumed — with the
    // arcs neutralised and the haze left running, the disk has no blotches on it at all.
    float surge = 1.0 + (1.0 - open) * 2.5;                 // flares on entry, then settles
    float haze = smoothstep(rs, rs + 0.02, r) * (1.0 - smoothstep(0.30, 0.5, r));
    col += vec3(0.35, 0.30, 0.72) * haze * (0.14 + 0.14 * sin(t * 6.0 + ang * 3.0)) * surge;

    // Event horizon: pure void.
    col *= smoothstep(rs - 0.006, rs + 0.006, r);
    return col;
}

// ---- 拉莱耶: the abyss — deep blue water, sunken light, rising bubbles. ----
// uv is the settled (sink-shifted) coordinate the scene is drawn in; uv0 is the raw panel coordinate,
// which the bubbles use so their climb never slides when the panel sinks in.
//
// The level of the whole scene. See the note at the bottom of sceneAbyss for where it came from and
// why it is applied inside the scene rather than after the readability guard.
const float ABYSS_DIM = 0.80;

// The short side of the panel this scene's glows and bubbles were DRAWN against, in the units their
// radii are written in. Multiplying `uAspect` by it turns a short-side-normalised vector back into
// the space those numbers assume, on a panel of any shape.
//
// ⚠ This constant exists because of a real bug, so do not quietly delete it. The bubble columns used to
// be measured in uPanelSize, which is GUI units — see the warning at the top of the file. (So were the
// three lamp glows, until they were deleted on 10-10; see the tombstone in sceneAbyss.)
//
// The scene was composed against a preview canvas 522 x 138 units wide, and the panel that actually
// arrives is about 113 x 45, so on screen the glows came out 4.6x too large relative to the panel:
// each one spanned roughly half the panel's height and all three together roughly DOUBLED the settled
// brightness (mean luma 0.0804 -> 0.1668 at the real size). That is what
// "拉莱耶做了动画之后的太亮了" and the second screenshot were.
const float ABYSS_REF_SHORT = 138.0;

vec3 sceneAbyss(vec2 uv, float t, vec2 uv0, float intro) {
    // THE ENTRANCE. Nothing in this panel "arrives" — the water was always there and the panel just
    // stops hiding it: the water fades in and a pressure wave crosses it. (The bioluminescence along
    // the bottom that used to notice one light at a time is gone — see the tombstone below.)
    //
    // ⚠ THE CREATURE IS GONE, by request: "这个下面的黑影和他的眼睛就不需要了，不要再弄这个了".
    // What was cut: a black silhouette drifting across the bottom of the panel (mass / ridge / fin),
    // the pair of cold eyes inside it (eyes / lid / blink / open), and the wide cold bloom that burst
    // out of the dark the instant they opened. With them went `surfaced` and `massY`, which existed
    // only to carry that silhouette up out of the bottom edge.
    //
    // The pressure wave STAYS. It used to be the creature's wake; it is now just the water moving,
    // which is a fine thing for a deep-sea panel to do on its own, and the request was for the
    // creature and its eyes rather than for the depth to go quiet. The lamps that stayed with it that
    // day were themselves removed the next morning — see the tombstone further down this function.
    float envT     = stageUp(intro, 0.0, 0.55);   // the water and everything living in it

    // The water itself: deep blue, dying to a blue so dark it is nearly black with depth. It is
    // kept BLUE the whole way down instead of fading to neutral black — the colour draining out
    // of the water is most of what reads as "a mile down".
    float depth = smoothstep(0.0, 1.0, uv.y);
    vec3 col = mix(vec3(0.022, 0.078, 0.165), vec3(0.001, 0.006, 0.026), depth * 0.90) * envT;

    // Thermoclines: a few soft bands of haze hanging in the column. Water this deep is not one
    // smooth gradient, it is layered, and the layers are what give the panel a sense of scale —
    // you can see how far down you are, and that there is more below.
    for (int b = 0; b < 3; b++) {
        float by = 0.24 + 0.25 * float(b) + 0.02 * sin(t * 0.06 + float(b) * 2.1);
        // Squared by multiplication, NOT pow(): the difference is signed, and GLSL leaves pow()
        // undefined for a negative base — which is exactly what a band below the current row is.
        float bd = (uv.y - by) * 9.0;
        col += vec3(0.05, 0.17, 0.36) * exp(-bd * bd) * 0.24;
    }

    // Water-caustic shimmer, warped like the surface seen from far below.
    vec2 wp = uv * uAspect * vec2(3.4, 2.4) + vec2(0.0, t * 0.021);
    vec2 w = vec2(fbm(wp + vec2(t * 0.024, 0.0)), fbm(wp + vec2(0.0, -t * 0.017) + 4.7));
    float caustic = fbm(wp * 1.7 + w * 1.9);
    col += vec3(0.14, 0.46, 0.88) * smoothstep(0.44, 0.90, caustic) * (1.0 - depth) * 0.62;

    // Sunken light: slow swaying shafts pouring down from a surface you will never reach. Sharper
    // than they were (higher powers) and stronger — a shaft is a thin bright thing, and at the old
    // amplitudes the whole top of the panel was one even wash with no shafts in it at all.
    float ang = uv.x * 7.0 + sin(t * 0.13) * 0.8 + w.x * 2.0;
    float rays = pow(0.5 + 0.5 * sin(ang * 2.3 + t * 0.19), 4.0)
            + 0.6 * pow(0.5 + 0.5 * sin(ang * 4.7 - t * 0.11 + 1.7), 6.0);
    col += vec3(0.30, 0.62, 1.10) * rays * (1.0 - depth) * (1.0 - depth) * 0.30;

    // Marine snow: two layers of pale motes drifting down through the blue. Denser and brighter
    // than before — this is the single thing that says "water" fastest, and at the old threshold
    // (h > 0.93) a panel this size held about six motes in total.
    for (int layer = 0; layer < 2; layer++) {
        float scale = layer == 0 ? 24.0 : 42.0;
        vec2 g = uv * uAspect * scale;
        g.y += t * (layer == 0 ? 0.34 : 0.58);
        g.x += sin(t * 0.21 + g.y * 0.9) * 0.5;
        vec2 cell = floor(g);
        float h = hash(cell + float(layer) * 17.0);
        if (h > 0.86) {
            float d = length(fract(g) - 0.5);
            float tw = 0.35 + 0.65 * sin(t * (0.7 + h * 2.4) + h * 60.0);
            col += vec3(0.62, 0.84, 1.0) * (1.0 - smoothstep(0.0, 0.30, d)) * tw
                    * (layer == 0 ? 0.30 : 0.20);
        }
    }

    // Bubbles: three columns letting beads go, climbing and wobbling toward a surface they never
    // reach. Distances are taken in short-side units, as the lamps' were — true circles at any
    // panel shape, never stretched by the panel's aspect, and the same apparent size however long the
    // tooltip gets (they were in uPanelSize, which is GUI units and grows with the text; see
    // ABYSS_REF_SHORT) — and their phase runs on its own clock, so the sink-in never slides them.
    // Small: a bubble is a bead of air, a few pixels across, not a balloon.
    vec2 ssu = uAspect * ABYSS_REF_SHORT;
    for (int bc = 0; bc < 3; bc++) {
        float seed = float(bc) * 12.7 + 5.0;
        // float() on the loop counter everywhere it meets a float: desktop GLSL converts int→float
        // implicitly and GLSL ES does not, so `0.30 * bc` compiles in Minecraft and is a hard error
        // under a strict ES compiler. Costless, and it keeps the whole file portable.
        float colX = 0.18 + 0.30 * float(bc) + 0.05 * sin(seed);
        float speed = 0.05 + 0.03 * hash(vec2(seed, 1.0));
        float yy = fract(t * speed + hash(vec2(seed, 2.0)));
        float bx = colX + 4.0 * sin(t * (0.8 + seed * 0.1) + yy * 14.0) / ssu.x;
        vec2 bp = (uv0 - vec2(bx, 1.0 - yy)) * ssu;
        float d = length(bp);
        float bead = 1.0 - smoothstep(0.9, 2.5, d);
        float ring = (1.0 - smoothstep(1.8, 2.8, d)) * smoothstep(1.0, 1.8, d);
        float life = sin(yy * 3.14159);
        col += vec3(0.60, 0.85, 1.0) * (bead * 0.34 + ring * 0.52) * life * envT;
    }

    // Nothing above this line exists until the water does. It used to be that only the base colour
    // rode the ramp and every other layer was already at full strength from the first frame, which is
    // why this panel measured as the flattest entrance of the four: the water "arrived" at 16% of a
    // colour that was already fully populated. One gate on the whole environment instead.
    col *= mix(0.05, 1.0, envT);

    // ⚠ TOMBSTONE: the three "distant bioluminescence" lamps were DELETED here on 10-10, by request —
    // "在比较下面位置的亮光你去掉吧", with the offending wash circled on a screenshot of the lower panel.
    //
    // What they were: three points of living light sitting at uv.y = 0.62 + 0.16 * hash, i.e. 62–81% of
    // the panel's height — the lower half, which is where the tooltip's numbers live — each one a tight
    // core inside a wide halo, breathing on its own clock, and waking one at a time during the entrance
    // so the deep read as inhabited rather than switched on.
    //
    // WHY THEY HAD TO GO, in numbers rather than taste. The halo e-folds at sqrt(500) = 22.4 short-side
    // units, which is 16% of the panel's SHORT side — and a tooltip panel is taller than it is wide only
    // when it is short, so on the shipped ~113 x 45 panel the short side is the height and that 16% is
    // 16% of the HEIGHT: a soft blob of radius 7.3 GUI units, i.e. about 15 units ACROSS, a full third
    // of the panel's height, out of a light that was meant to be a distant point. Measured back off the
    // two screenshots the request came with, the blob's centre sat at uv.y ~ 0.68 and its area worked
    // out to a radius of 7.4 units against the formula's 7.3 — and it had moved between the two frames,
    // because it drifts. On three separate breathing clocks (`0.35 + 0.65 * breathe` each) the three
    // together swung the lower panel's own 90th-percentile luma between 0.20 and 0.36 over one cycle of
    // the GIF that came with the report. A pulsing wash that size is not a light seen through water, it
    // is glare — and it was eating the contrast of every line of text drawn over the bottom half.
    //
    // If lights are ever wanted down here again they have to be POINTS: keep the exp(-d2 / 90.0) core,
    // which is 6.9% of the short side and is the part that reads as a bead of light seen through water,
    // and DO NOT bring the exp(-d2 / 500.0) halo back "for depth". The depth this panel needs is carried
    // by the water gradient, the thermoclines, the caustics, the sunken shafts, the marine snow and the
    // three bubble columns — all of them still here, and none of them swells to a third of the panel.

    // The pressure wave. A front running outward from low in the frame, refracting the water as it
    // passes. Drawn as the DERIVATIVE of a Gaussian — bright on its leading edge, dark on its trailing
    // one — because a wave is a density CHANGE, and a single bright ring just reads as a lamp. This is
    // the cheap honest version of "the water moved": the scene is modulated rather than resampled, so
    // nothing smears. (It used to be timed as the wake of the creature that came up from below; that
    // creature is gone, and this now simply starts early and runs once.)
    float wrad   = length((uv - vec2(0.5, 0.86)) * vec2(1.0, 1.45));
    float wFront = 1.45 * stageUp(intro, 0.18, 0.95);
    float wd     = (wrad - wFront) * 5.0;
    float wave   = wd * exp(-wd * wd);                      // bipolar: + leads, − trails
    col *= 1.0 + 0.90 * wave * (1.0 - stageUp(intro, 0.55, 1.0) * 0.45);
    col += vec3(0.24, 0.52, 0.90) * abs(wave) * 0.20;

    // The whole scene, brought down as one, by request: "拉莱耶做了动画之后的太亮了". One factor rather
    // than a trim on each layer, and that is deliberate — what reads as WATER here is the structure
    // (the caustics, the shafts, the layered haze), and structure is exactly what a single multiplier
    // leaves alone. Only the level moves.
    //
    // The figure is not a guess: it was picked off a four-step comparison render of this exact panel
    // (x1.00 / x0.80 / x0.62 / x0.48), and 0.80 is the lightest of the three candidates — enough to
    // take the glare off the mid-blues while the caustics and the bubble columns stay legible.
    //
    // ⚠ It goes HERE, inside the scene, and not after main()'s readability guard. That guard only ever
    // scales DOWN and only above its knee, so a dim applied after it would do nothing to the bright
    // areas and everything to the dark ones — precisely backwards.
    col *= ABYSS_DIM;
    return col;
}

// ---- Readability guard ------------------------------------------------------------------------
// These panels are a BACKGROUND. The tooltip's own text is drawn over them afterwards, in near-
// white, and vanilla gives it no shadow — so anywhere the scene outshines the text, the text is
// simply gone. Every other term in this file is art; this one is not negotiable.
//
// It is a soft knee and not a clamp: below KNEE nothing is touched at all, above it the excess is
// squeezed towards a ceiling, so a highlight still reads as a highlight — it just stops climbing.
// The ceiling sits deliberately BELOW white (KNEE + 1/KNEE_K ≈ 0.74) so letters keep an edge on
// every theme, including over 虚无's disk mid-flare and 深空's ionisation rim.
//
// Applied LAST, after the vignette and both entrance bursts, so what it guarantees is the value
// actually written out. It only ever scales by 1.0 or less — it can never brighten anything.
const vec3  LUMA_W = vec3(0.2126, 0.7152, 0.0722);
const float KNEE   = 0.38;   // untouched below this luma
const float KNEE_K = 2.80;   // squeeze rate above it

vec3 readable(vec3 col) {
    float luma = max(dot(col, LUMA_W), 1.0e-4);
    float over = max(luma - KNEE, 0.0);
    float target = (luma - over) + over / (1.0 + over * KNEE_K);
    return col * (target / luma);
}

void main() {
    // Hover entrance. Each theme now arrives on its own choreography, and each is a small story about
    // its own subject rather than a fade:
    //   天体    the gas condenses, the primary star IGNITES (flare, spikes, shell expanding), companion last
    //   深空    the backlight goes up first, the columns climb into frame, the rims catch, the stars last
    //   虚无    the hole OPENS — one beat, a zoom, over the ORIGINAL concentric disk (both reverted;
    //           see sceneBlackHole. Nothing of the 10-09 edge-on rework survives.)
    //   拉莱耶  the panel stops hiding the water and a pressure wave crosses it (the creature, its eyes
    //           and the three lamps that used to close the show are all gone)
    // The three panel-wide beats live here; the per-scene staging lives in each scene function.
    bool voidFx = uTheme > 1.5 && uTheme < 2.5;
    bool abyssFx = uTheme > 2.5;
    float intro = clamp(uIntro, 0.0, 1.0);
    float e = 1.0 - pow(1.0 - intro, 3.0);
    // The void's camera pushes IN — a lens, not an interpolation. Back to the plain ease: the
    // overshoot-and-settle that was added here went out with the same revert as the scene's snap.
    float zoom = mix(0.84, 1.0, e);
    vec2 uv = voidFx ? 0.5 + (texCoord0 - 0.5) * zoom : texCoord0;
    // The sink: the whole scene drifts down a short way into place, so the water appears to rise
    // past the panel as it settles — kept small, so the scene reads as sinking, not sliding.
    if (abyssFx) {
        uv.y -= (1.0 - e) * 0.045;
    }
    // The scenes get the RAW clock; `e` is only the camera's curve (and the void's own flash and ring,
    // which are panel-wide). Every scene stage is a fraction of the entrance as a whole, so at 780 ms
    // a stage boundary at 0.36 is a real 280 ms — read off the eased value it would land a fifth of a
    // second early, which is the whole of the bug that line is there to prevent. The void eases its own
    // copy of the clock inside sceneBlackHole; it is the one scene with a single beat, so it can.
    float t = uTime;
    vec3 col;
    if (uTheme < 0.5) {
        col = sceneCelestial(uv, t, intro);
    } else if (uTheme < 1.5) {
        col = scenePillars(uv, t, intro);
    } else if (uTheme < 2.5) {
        col = sceneBlackHole(uv, t, intro);
    } else {
        col = sceneAbyss(uv, t, texCoord0, intro);
    }

    // Twinkling foreground stars for the nebula and the pillars (the black hole and the abyss draw
    // their own snow). They are the last thing to arrive on both panels, and on 深空 that is a beat of
    // its own: a star cluster is the light source, so the light lands before the lights are visible.
    if (uTheme < 1.5) {
        float starsT = uTheme < 0.5 ? stageUp(e, 0.15, 0.60) : stageUp(e, 0.72, 1.00);
        for (int layer = 0; layer < 2; layer++) {
            float scale = layer == 0 ? 60.0 : 110.0;
            vec2 g = uv * uAspect * scale;
            vec2 cell = floor(g);
            float h = hash(cell + float(layer) * 37.0);
            if (h > 0.90) {
                float d = length(fract(g) - 0.5);
                float tw = 0.5 + 0.5 * sin(uTime * (1.5 + h * 3.0) + h * 40.0);
                col += vec3(0.85, 0.90, 1.0) * smoothstep(0.32, 0.0, d) * tw
                     * (layer == 0 ? 0.9 : 0.55) * starsT;
            }
        }
    }

    // Vignette beds the panel into the frame instead of ending flat. (Written as 1 - smoothstep
    // with ascending edges: the old reversed form is UNDEFINED per the GLSL spec — every driver
    // anyone tests on happens to evaluate it as this, but it is exactly the sort of thing a strict
    // compiler is entitled to differ on. Same curve, now defined.)
    float vig = 1.0 - smoothstep(0.35, 1.15, length((uv - 0.5) * vec2(2.0, 1.0)));
    col *= mix(0.40, 1.0, vig);

    // The void's two panel-wide beats, back on the clock and back at t = 0. The revision this replaces
    // timed both off the horizon's snap at 0.36 (a flash that WAS the horizon opening, and a wavefront
    // that waited for it). That beat no longer exists, so neither can its timing: the flash is the
    // arrival itself and the ring runs from the first frame, exactly as they did before.
    if (voidFx) {
        float rad = length((uv - 0.5) * uAspect);
        col += vec3(0.62, 0.52, 1.0) * exp(-intro * 5.5) * 0.55;
        float shock = smoothstep(0.06, 0.0, abs(rad - e * 0.95)) * (1.0 - e);
        col += vec3(0.82, 0.72, 1.0) * shock * 0.8;
    }

    // The abyss: out of total black, a drowned light wakes. Just the wake now — the two guttering
    // pulses that used to play over the panel were a screen effect with nothing behind them, and the
    // pressure wave in the scene itself does the job properly. What is left is the rise: a cold glow
    // welling up from below while the water is still mostly dark, which is the light that wakes it.
    if (abyssFx) {
        float wake = stageUp(intro, 0.0, 0.55);
        col *= mix(0.16, 1.0, wake);
        col += vec3(0.05, 0.17, 0.34) * smoothstep(1.0, 0.25, texCoord0.y) * (1.0 - wake) * 1.10;
    }

    // The panel's own opacity comes up faster than any of the choreography, so what the eye sees is
    // the SCENE arriving rather than a rectangle fading in — except on the void, where the black gap
    // before the flash is part of the beat. The void's span is back at 0.20: its flash has no
    // half-second runway any more, it is brightest at t = 0, so the panel has to be up to catch it.
    float introSpan = voidFx ? 0.20 : (abyssFx ? 0.20 : 0.15);
    float alpha = 0.96 * smoothstep(0.0, introSpan, intro);
    fragColor = vec4(readable(col), alpha) * ColorModulator * vertexColor;
}

