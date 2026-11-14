#version 150

#moj_import <goety_astrology:astro_noise.glsl>

// astro_glow.glsl is deliberately not imported here: its astroGlowFalloff is a symmetric bell, and both
// halves of this effect are crests and shells with hard, travelling edges. The one part of its rule that
// still applies — the outer edge reaches zero before the quad does — is done by hand below.

// 月华 — where a 月耀 thread touches down.
//
// A bang is read from three things at once, and all three have to be present or the eye stops believing
// it: a BALL of light expanding out of the point of contact, something CHURNING inside that ball, and a
// RING running away from it across the ground faster than the ball grows. A ring on its own is a circle
// somebody drew; a ball on its own is a lamp switched on. So uMode picks between the two pictures that
// are pure per-pixel shape, and the ball carries its own churn.
//
//   0 — the ground wave: two crests opening outward at different speeds, laced with radial streaks,
//       led by a thinner shock front, over a wash that marks the ground as struck.
//   1 — the ball: a churning sphere with a white-hot core that shrinks and cools, bursting a beat after
//       the ink lifts and cooling from white into the icon's cyan by the time it is gone.
//   2 — the ink: black, over everything, for the first few frames. This is the one pass that is not
//       additive — it has to <em>remove</em> light, which additive blending cannot do — and it is what
//       turns the contact into an impact frame: black first, then the cyan out of it.
//
// The colour is not a choice. 月耀聚晶's own icon is a hard electric cyan (#09FFFA, the 20 brightest
// pixels of it), so everything here is that cyan with the middle over-driven toward white — which is
// also what a genuinely bright source looks like, rather than a flat cyan disc.

uniform vec4 ColorModulator;
uniform float uLife;   // 0 at the touch, 1 when the splash is over
uniform float uMode;   // 0 = the wave on the ground, 1 = the ball
uniform float uSeed;   // per burst, so five threads landing together do not churn in unison

in vec4 vertexColor;
in vec2 planeCoord;    // −1..1 across the quad

out vec4 fragColor;

/** #09FFFA, straight out of lunar_flare_focus.png. */
const vec3 MOON_CYAN = vec3(0.035, 1.0, 0.98);
/**
 * The same cyan drifted a little toward green. Five splashes land together, and five identical cyans
 * read as one shade stamped five times — uSeed picks a point between the two per burst.
 */
const vec3 MOON_CYAN_PALE = vec3(0.120, 1.0, 0.860);
/** The same light over-driven: white with a cyan fringe, which is how the icon's core reads too. */
const vec3 MOON_WHITE = vec3(0.90, 1.0, 1.0);
/** The ball's skirt, where it has cooled furthest from the core. */
const vec3 MOON_DEEP = vec3(0.012, 0.30, 0.46);

float gauss(float offset, float width) {
    float k = offset / width;
    return exp(-k * k);
}

void main() {
    float d = length(planeCoord);
    // Outside the inscribed circle there is nothing to draw. Cheaper than shading it and multiplying
    // it by zero, and it keeps the quad's corners out of the additive blend entirely.
    if (d > 1.0) {
        discard;
    }

    float t = clamp(uLife, 0.0, 1.0);
    float fade = 1.0 - t;
    // Five splashes land within a tick of each other; a cyan that drifts with the burst's own seed is
    // the difference between five forks of the same light and one colour stamped five times.
    vec3 cyan = mix(MOON_CYAN, MOON_CYAN_PALE, uSeed);
    vec3 color;
    // The additive passes ignore alpha; only the ink pass, which cannot be additive at all, writes it.
    float outAlpha = 1.0;

    if (uMode < 0.5) {
        // ── the wave on the ground ───────────────────────────────────────────────
        // Ease-out: the wave leaves fast and spends itself slowing down, the way a splash does. Linear
        // growth reads as a dial being turned.
        float grow = 1.0 - (1.0 - t) * (1.0 - t);
        // Radial streaks. Sampled off the *direction* rather than off the angle, so there is no seam
        // where atan wraps: the noise argument walks round the circle and comes back to its start.
        float streak = 0.55 + 0.45 * noise(planeCoord / max(d, 1.0E-3) * 3.0 + uSeed);
        // The crest's radius as a fraction of the quad, which is why the quad is the blast radius: what
        // the player sees open out is exactly what the blast reaches.
        float rim = 0.06 + 0.82 * grow;
        // The leading edge thins as it travels — a crest that keeps its sharpness is a hoop.
        float edge = gauss(d - rim, 0.040 + 0.075 * t) * streak;
        // A tighter second crest just inside it, riding along with the first.
        float crest = gauss(d - rim * 0.78, 0.022 + 0.042 * t) * 0.50;
        // The shock front: a thinner, faster ring that leaves a beat late and outruns both. This is the
        // one thing that separates "a wave" from "a blast" — the pressure front is not the light.
        float late = clamp((t - 0.14) / 0.86, 0.0, 1.0);
        float front = gauss(d - (0.10 + 0.78 * (1.0 - (1.0 - late) * (1.0 - late))),
                            0.018 + 0.026 * late) * (1.0 - late) * 0.80;
        // The wash the crests leave behind: barely there, and now cut back hard (0.18 to 0.10, squared
        // away instead of linear). It was the part of the splash that stayed on screen longest after the
        // bang was over, which is precisely what made the whole effect read as lingering fog.
        float wash = (1.0 - smoothstep(0.0, max(rim, 0.12), d)) * 0.10;
        float body = (edge + crest) * fade * fade + front + wash * fade * fade;
        // 🔴 The outer edge has to reach zero BEFORE the quad ends, or the additive pass cuts the glow
        // off at the quad's edge and the splash is a visible square (astro_glow.glsl's rule).
        body *= 1.0 - smoothstep(0.86, 1.0, d);
        color = mix(MOON_WHITE, cyan, clamp(d / max(rim, 0.20), 0.0, 1.0)) * body;
    } else if (uMode < 1.5) {
        // ── the ball of light ───────────────────────────────────────────────────
        // It opens on a hard ease-out: an explosion's light is not a balloon being inflated, it is the
        // air giving up — three quarters of its size in the first third of its life.
        float grow = 1.0 - pow(1.0 - t, 2.4);
        float ballR = 0.26 + 0.58 * grow;
        // Churn: the silhouette is displaced by noise instead of being a circle of a stated radius, so
        // the edge boils while it expands. It rolls upward (the −y drift) the way hot gas does.
        float churn = fbm(planeCoord * 2.4 + vec2(uSeed * 9.0, uSeed * 4.0 - t * 1.1)) - 0.5;
        float edgeR = ballR * (1.0 + 0.34 * churn);
        float shell = 1.0 - smoothstep(edgeR * 0.58, edgeR, d);
        // The core: white-hot, and it gives up its size far faster than the ball gives up its own — by
        // the end there is a broad cold ball and no hot middle left in it at all.
        float coreR = 0.30 * (1.0 - 0.62 * t);
        float core = 1.0 - smoothstep(coreR * 0.28, coreR, d);
        // Filaments pulled radially through the ball: a second noise term in polar coordinates,
        // stretched outward and dragged inward over the life, so the inside is visibly moving.
        float fil = smoothstep(0.40, 0.90,
                fbm(vec2(atan(planeCoord.y, planeCoord.x) * 2.6, d * 3.4 - t * 2.2) + uSeed));
        float body = shell * (0.34 + 0.86 * fil);
        vec3 hue = mix(MOON_DEEP, cyan, smoothstep(0.0, 0.70, shell * 0.85 + fil * 0.40));
        hue = mix(hue, MOON_WHITE, clamp(core * 1.4, 0.0, 1.0));
        // The burst: held back until the ink has lifted, then fired. The flash belongs *after* the black,
        // not under it — a pop on the contact frame would be spent before anyone could see it, and the
        // whole point of the ink is that the two are read in that order.
        float burst = smoothstep(0.12, 0.24, t) * exp(-max(t - 0.24, 0.0) * 14.0);
        // ^2.4 on the tail: a splash that keeps glowing after the bang has finished is not a bang, it is
        // fog. This is the curve that makes it clear the screen before the next salvo arrives.
        float lit = (body + core * 2.6 + burst * 1.6) * pow(1.0 - t, 2.4);
        lit *= 1.0 - smoothstep(0.88, 1.0, d);
        color = mix(hue, MOON_WHITE, clamp(burst, 0.0, 1.0) * 0.55) * lit;
    } else {
        // ── the ink ─────────────────────────────────────────────────────────────
        // The impact frame: for the first instants the point of contact is not light but a hole in the
        // light, and the cyan arrives out of it. The renderer draws this pass with ordinary alpha
        // blending (the only one in this file that is not additive) and last, so what it paints is
        // nothing at all — which is the only way a shader can take light away.
        float inkR = 0.30 + 0.28 * (1.0 - (1.0 - t) * (1.0 - t));
        float ink = 1.0 - smoothstep(inkR * 0.80, inkR, d);
        // Gone within the first fifth of the splash, and with a soft shoulder: two or three frames of
        // black is an impact, ten is a glitch.
        outAlpha = ink * (1.0 - smoothstep(0.04, 0.26, t));
        color = vec3(0.0);
    }

    // Additive (ONE, ONE) for two of the three passes: brightness rides in the colour and alpha is only
    // the caller's gain. The ink pass is blended normally, and there alpha is the whole effect.
    fragColor = vec4(color, outAlpha) * ColorModulator * vertexColor.a;
}
