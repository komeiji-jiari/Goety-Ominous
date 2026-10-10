#version 150

// The 星象学 glyph material shader. ALL FOUR themes (天体/深空/虚无/拉莱耶) render through a VERBATIM
// port of Goetic Legacy's jade font renderer — by user decree, after our hand-drifted astroFacet/
// astroBreath ports never matched the reference. VERBATIM means the whole call path, not just the
// function body: the fragment stage mirrors the reference's own effect_font.fsh (vanilla atlases) and
// modernui_effect_font_sdf.fsh (Modern UI SDF atlases) line for line — same GameTime*1200 clock,
// same ordinal-from-vertex-green protocol, same coverage/discard math, same final composition.
//
// The ONLY edits anywhere:
//   1) the five palette constants ride in as arguments, so each theme wears its own colours through
//      the same body — 深空's five are exactly the user-specified deep-space blue, the others keep
//      the reference's internal ratios (dark → lit → core) with the dominant hue rotated;
//   2) the flux layer and the hover-entrance fade the older hand-built pipeline had are gone — the
//      reference has neither, and the black outline was removed by user decree (10-07).
//
//   uTheme 0  天体 — night indigo body, ion-cyan vein, star-gold core.
//   uTheme 1  深空 — the user-specified deep-space blue.
//   uTheme 2  虚无 — amethyst violet.
//   uTheme 3  拉莱耶 — deep-sea blue.
//
// Sampler0 is the glyph atlas; texCoord0 is the atlas UV of this fragment. The reference's
// fragmentPosition is raw screen pixels (gl_FragCoord) — its facet/vein scales are absolute and
// intentionally NOT rescaled per GUI size.

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime; // vanilla fraction-of-day clock — the reference's time base (×1200 = seconds)
uniform float uTheme;   // 0 天体, 1 深空, 2 虚无, 3 拉莱耶
uniform float uModernUi;  // 1 when riding Modern UI's glyphs — its atlas stores an SDF, not raw coverage

// ⚠ A hover entrance was bolted on here on 10-09 and REMOVED the same day. It did not work, and the
// reason is worth keeping: a glyph line is drawn in THREE passes — the dark outline, the darkened
// drop shadow, and the material itself — and the entrance gated the shadow from Java (one alpha per
// line) while gating the material from here. Two mechanisms, three passes, one of them dark: as the
// gate closed the bright material vanished first and the DARK shadow stayed, so every tooltip's text
// turned black for the last third of a second of the hover. If this is ever wanted again, do it the
// way the hotbar name already does it — ONE alpha threaded through all three passes in Java, which is
// what {@code ModernUiStyledText.draw}'s own {@code alpha} parameter is — and leave this file alone.

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

// ── Goetic Legacy jade font, verbatim (palette rides in as arguments) ──────────────────────────

float glFontHash21(vec2 value) {
    vec3 p3 = fract(vec3(value.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float glFontValueNoise2(vec2 value) {
    vec2 cell = floor(value);
    vec2 local = fract(value);
    local = local * local * (3.0 - 2.0 * local);
    float a = glFontHash21(cell);
    float b = glFontHash21(cell + vec2(1.0, 0.0));
    float c = glFontHash21(cell + vec2(0.0, 1.0));
    float d = glFontHash21(cell + vec2(1.0));
    return mix(mix(a, b, local.x), mix(c, d, local.x), local.y);
}

float glFontFbm2(vec2 value) {
    float result = glFontValueNoise2(value) * 0.667;
    result += glFontValueNoise2(value * 2.03 + vec2(17.1, 9.2)) * 0.333;
    return result;
}

float glFontIrregularBreath(float time, float phase) {
    float slow = sin(time * 0.72 + phase);
    float drift = sin(time * 0.43 + sin(time * 0.17 + phase * 0.37) * 0.80);
    return clamp(0.5 + 0.5 * (slow * 0.64 + drift * 0.36), 0.0, 1.0);
}

float glFontFacetPattern(vec2 position, float seed, float time) {
    vec2 drift = vec2(time * 0.018, -time * 0.012);
    vec2 p = position * vec2(0.105, 0.145) + drift;
    float warp = glFontFbm2(p * 0.72 + vec2(seed * 0.037, seed * 0.023));
    float planeA = 0.5 + 0.5 * sin(p.x * 2.15 + p.y * 1.45 + warp * 1.30);
    float planeB = 0.5 + 0.5 * sin(p.x * 1.10 - p.y * 2.05 - warp * 0.85);
    return clamp(warp * 0.42 + planeA * 0.40 + planeB * 0.18, 0.0, 1.0);
}

float glFontSoftBand(float phase, float width) {
    float distanceToCenter = abs(fract(phase) - 0.5);
    return 1.0 - smoothstep(width * 0.45, width, distanceToCenter);
}

vec3 glRenderFontJade(vec2 fragmentPosition, float time, float ordinal,
                      float sdfDistance, float hasSdfDistance,
                      vec3 blackJade, vec3 deepJade, vec3 livingJade, vec3 jadeLight, vec3 spiritCore) {
    float breath = glFontIrregularBreath(time, ordinal * 0.071);
    float facets = glFontFacetPattern(fragmentPosition, ordinal * 11.7, time);
    float opposingFacet = glFontFacetPattern(
        vec2(-fragmentPosition.y, fragmentPosition.x) + vec2(31.0, 17.0),
        ordinal * 7.3 + 19.0, -time * 0.73);
    float facetBody = clamp(facets * 0.72 + opposingFacet * 0.28, 0.0, 1.0);
    float facetLight = smoothstep(0.48, 0.86, facetBody);
    float facetShadow = 1.0 - smoothstep(0.18, 0.48, facetBody);

    float veinPhase = fragmentPosition.x * 0.0105
                    - fragmentPosition.y * 0.0065 - time / 5.8;
    float movingVein = glFontSoftBand(veinPhase, 0.105);
    float fineVein = glFontSoftBand(veinPhase * 1.93 + 0.27, 0.052);

    vec3 color = mix(deepJade, livingJade, 0.22 + facetLight * 0.68);
    color = mix(color, blackJade, facetShadow * 0.70);
    color = mix(color, jadeLight, movingVein * (0.30 + facetLight * 0.28));
    color = mix(color, spiritCore, fineVein * movingVein * 0.30);
    color *= 0.91 + breath * 0.13;

    float carvedRim = (1.0 - smoothstep(0.018, 0.125, abs(sdfDistance)))
                    * hasSdfDistance;
    float innerGlass = smoothstep(0.105, 0.315, sdfDistance)
                     * hasSdfDistance;
    color = mix(color, blackJade, carvedRim * 0.74);
    color = mix(color, mix(livingJade, spiritCore, movingVein * 0.72),
                innerGlass * (0.18 + breath * 0.18));
    return color;
}

// ── End of the verbatim reference block ─────────────────────────────────────────────────────────

void main() {
    // ── the reference's own call path, verbatim, shared by all four themes ──
    // modernui_effect_font_sdf.fsh on Modern UI's SDF atlases (true distance passed raw, the glyph's
    // stored RGB folded into the material), effect_font.fsh on vanilla bitmap atlases (no distance,
    // hard 0.1 discard). ordinal arrives in vertex green exactly as the reference protocol encodes
    // it; time is the reference's GameTime clock.
    float seconds = GameTime * 1200.0;
    float ordinal = floor(vertexColor.g * 255.0 + 0.5);
    float dist;
    float hasSdf;
    float alpha;
    vec4 glyph;
    if (uModernUi > 0.5) {
        glyph = textureLod(Sampler0, texCoord0, 0.0);
        dist = glyph.a - 127.0 / 255.0 + 0.04;
        hasSdf = 1.0;
        alpha = clamp(dist / fwidth(dist) + 0.5, 0.0, 1.0) * vertexColor.a * ColorModulator.a;
    } else {
        glyph = texture(Sampler0, texCoord0);
        dist = 0.0;
        hasSdf = 0.0;
        alpha = glyph.a * vertexColor.a * ColorModulator.a;
    }
    if (alpha < (uModernUi > 0.5 ? 0.01 : 0.1)) {
        discard;
    }

    // The five palette constants, per theme. Hue is each school's own; the BRIGHTNESS is not: the
    // reference's jade green carries its luminance in the G channel (weight 0.72 to the eye), and the
    // hue rotations alone left blue/violet/indigo palettes at half (or quarter) that luminance — the
    // material read as near-invisible. So every slot is rescaled to the reference's own slot luminance
    // (0.053/0.178/0.420/0.762/0.930), hue untouched.
    vec3 blackJade, deepJade, livingJade, jadeLight, spiritCore;
    if (uTheme < 0.5) {          // 天体: night indigo, ion-cyan vein, star-gold core
        blackJade = vec3(0.059, 0.037, 0.203);
        deepJade = vec3(0.204, 0.130, 0.583);
        livingJade = vec3(0.490, 0.313, 1.000);
        jadeLight = vec3(0.372, 0.859, 0.954);
        spiritCore = vec3(1.000, 0.928, 0.570);
    } else if (uTheme < 1.5) {   // 深空: the user-specified deep-space blue, luminance-matched
        blackJade = vec3(0.007, 0.061, 0.119);
        deepJade = vec3(0.023, 0.198, 0.443);
        livingJade = vec3(0.057, 0.464, 1.000);
        jadeLight = vec3(0.383, 0.816, 1.000);
        spiritCore = vec3(0.818, 0.943, 1.000);
    } else if (uTheme < 2.5) {   // 虚无: amethyst-magenta violet
        blackJade = vec3(0.103, 0.021, 0.232);
        deepJade = vec3(0.367, 0.059, 0.808);
        livingJade = vec3(0.927, 0.129, 1.000);
        jadeLight = vec3(1.000, 0.550, 1.000);
        spiritCore = vec3(1.000, 0.861, 1.000);
    } else {                     // 拉莱耶: deep-sea blue
        blackJade = vec3(0.012, 0.058, 0.131);
        deepJade = vec3(0.031, 0.196, 0.432);
        livingJade = vec3(0.066, 0.474, 0.926);
        jadeLight = vec3(0.408, 0.800, 1.000);
        spiritCore = vec3(0.772, 0.963, 1.000);
    }

    vec3 col = glRenderFontJade(gl_FragCoord.xy, seconds, ordinal, dist, hasSdf,
                                blackJade, deepJade, livingJade, jadeLight, spiritCore);
    // The reference's own composition: the Modern UI path folds the glyph's stored RGB and
    // ColorModulator into the material; the vanilla path (shadowFactor is 1 on encoded effects)
    // leaves the material alone.
    vec3 outColor = (uModernUi > 0.5) ? col * glyph.rgb * ColorModulator.rgb : col;
    fragColor = vec4(outColor, alpha);
}
