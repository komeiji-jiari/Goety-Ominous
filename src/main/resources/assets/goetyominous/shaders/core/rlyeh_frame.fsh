#version 150

// The 星象学 tooltip frame — a carved-crystal border, and the clearest place the author's SDF technique
// pays off: a rectangle has a clean *analytic* signed distance (unlike glyphs, where the SDF had to be
// faked from coverage), so the carved bevel and the inner glass read crisply. The border band is found
// from a rounded-box SDF; the SDF's screen-space gradient is its outward normal, which lights the frame
// from the top-left into an embossed bevel; the same faceted jade material (domain-warped fbm + sine
// facets), a vein of light orbiting the perimeter, and a slow breath ride on top, in each theme's palette:
//   uTheme 0 天体 — indigo/violet-cyan nebula.   1 深空 — cold teal/ice.   2 虚无 — violet/magenta void.

uniform vec4 ColorModulator;
uniform float uTime;
uniform float uTheme;
uniform vec2 uSize;     // panel size in pixels (the drawn quad)
uniform float uBorder;  // border band width in pixels
uniform float uRadius;  // corner radius in pixels
uniform float uIntro;   // 0 → 1 hover entrance: the border draws itself round the perimeter

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
    float v = noise(p) * 0.667;
    v += noise(p * 2.03 + vec2(17.1, 9.2)) * 0.333;
    return v;
}

// Interfering sine planes over a domain-warped field — the jade shader's facet trick.
float facet(vec2 uv, float seed, float t) {
    vec2 p = uv * vec2(6.0, 6.0) + vec2(t * 0.05, -t * 0.03);
    float warp = fbm(p * 0.72 + vec2(seed * 0.037, seed * 0.023));
    float planeA = 0.5 + 0.5 * sin(p.x * 2.15 + p.y * 1.45 + warp * 1.30);
    float planeB = 0.5 + 0.5 * sin(p.x * 1.10 - p.y * 2.05 - warp * 0.85);
    return clamp(warp * 0.42 + planeA * 0.40 + planeB * 0.18, 0.0, 1.0);
}

// A soft band centred on the fractional phase — the jade shader's moving vein.
float vein(float phase, float width) {
    float d = abs(fract(phase) - 0.5);
    return 1.0 - smoothstep(width * 0.45, width, d);
}

// Irregular breath (two out-of-phase sines) — the jade shader's living pulse.
float breath(float t) {
    float slow = sin(t * 0.72);
    float drift = sin(t * 0.43 + sin(t * 0.17) * 0.80);
    return clamp(0.5 + 0.5 * (slow * 0.64 + drift * 0.36), 0.0, 1.0);
}

// Signed distance to a rounded box (negative inside).
float sdRoundBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

// PLACEHOLDER_MAIN
void main() {
    vec2 P = (texCoord0 - 0.5) * uSize;      // pixels from the panel centre
    vec2 halfSize = uSize * 0.5;
    float d = sdRoundBox(P, halfSize - vec2(0.5), uRadius);   // ~0 at the outer edge, < 0 inside
    float depth = -d;                                     // grows inward from the edge

    // The border band: inside the outer edge, but not so deep it reaches the interior.
    float inside = smoothstep(0.75, -0.75, d);
    float hole = smoothstep(-uBorder + 0.75, -uBorder - 0.75, d);
    float band = inside * (1.0 - hole);
    if (band <= 0.002) {
        discard;                                          // interior: let the nebula show through
    }
    float u = clamp(depth / uBorder, 0.0, 1.0);           // 0 at the outer lip, 1 at the inner lip

    // The SDF's screen-space gradient is the outward normal; light it from the top-left for an
    // embossed bevel (carved rim): the top and left of the frame catch light, the bottom and right fall
    // into shadow, exactly the author's carvedRim role but from a real distance field.
    vec2 n = normalize(vec2(dFdx(d), dFdy(d)) + vec2(1e-5));
    vec2 L = normalize(vec2(-0.55, -0.83));
    float lam = dot(n, L);
    float hi = smoothstep(0.10, 0.92, lam);
    float lo = smoothstep(0.10, 0.92, -lam);

    // Per-theme crystal palette.
    vec3 body, high, spirit;
    float veinSpeed, breathDepth;
    if (uTheme < 0.5) {              // 天体: indigo nebula
        body = vec3(0.15, 0.10, 0.34);
        high = vec3(0.55, 0.78, 1.0);
        spirit = vec3(0.96, 0.92, 1.0);
        veinSpeed = 0.20;
        breathDepth = 0.26;
    } else if (uTheme < 1.5) {       // 深空: cold ice
        body = vec3(0.05, 0.20, 0.34);
        high = vec3(0.35, 0.86, 0.96);
        spirit = vec3(0.86, 0.98, 1.0);
        veinSpeed = 0.16;
        breathDepth = 0.28;
    } else if (uTheme < 2.5) {       // 虚无: violet void
        body = vec3(0.21, 0.06, 0.35);
        high = vec3(0.82, 0.34, 1.0);
        spirit = vec3(0.97, 0.80, 1.0);
        veinSpeed = 0.24;
        breathDepth = 0.42;
    } else {                         // 拉莱耶: deep blue water with a cold breathing glow
        body = vec3(0.012, 0.055, 0.130);
        high = vec3(0.28, 0.58, 0.98);
        spirit = vec3(0.78, 0.93, 1.0);
        veinSpeed = 0.05;
        breathDepth = 0.36;
    }

    bool drowned = uTheme > 2.5;
    float pulse = breath(uTime);
    float ang = atan(P.y, P.x) / 6.2831853;
    vec3 col;

    if (drowned) {
        // The abyss frame is no crystal: it is the dark edge of deep water. Almost black, with one
        // faint band of drowned light welling slowly around the rim and a soft glow at the inner lip
        // where the water meets the panel — no facets, no glints, no carved bevel.
        float current = vein(ang * 2.0 - uTime * veinSpeed, 0.24);
        col = body * 0.85;
        col = mix(col, high, current * 0.42);                 // slow luminous current
        col = mix(col, col * 0.30, smoothstep(0.22, 0.0, u)); // deep dark outer edge
        col += high * smoothstep(0.55, 1.0, u) * 0.12;        // faint inner-lip glow
        // A drowned surge breathing across the whole border, deep and slow.
        float surge = fbm(gl_FragCoord.xy / 46.0 + vec2(uTime * 0.04, -uTime * 0.026));
        col *= 0.66 + 0.5 * surge;
    } else {
        // Faceted crystal body (screen space so it flows across the whole frame).
        vec2 sp = gl_FragCoord.xy / 40.0;
        float facets = facet(sp, 11.7, uTime);
        float facetLight = smoothstep(0.45, 0.92, facets);

        // A vein of light orbiting the perimeter (angle around the centre as the arc parameter).
        float orbit = vein(ang * 3.0 - uTime * veinSpeed, 0.12);

        col = body * (0.50 + facetLight * 0.62);
        col = mix(col, high, orbit * 0.60);           // orbiting vein glow
        col += high * hi * 0.55;                      // bevel highlight (top-left)
        col *= 1.0 - lo * 0.52;                        // bevel shadow (bottom-right)
        col = mix(col, col * 0.28, smoothstep(0.18, 0.0, u));   // dark outer lip, for definition
        col += spirit * smoothstep(0.55, 1.0, u) * (0.14 + facetLight * 0.12); // inner glass sheen

        // Faceted corner nodes: a steady glint where the two edges meet.
        vec2 cornerReach = halfSize - vec2(uRadius + 9.0);
        float corner = smoothstep(0.0, 9.0, abs(P.x) - cornerReach.x)
                     * smoothstep(0.0, 9.0, abs(P.y) - cornerReach.y);
        col += spirit * corner * (0.30 + 0.45 * pulse);
    }

    col *= 1.0 - breathDepth + breathDepth * pulse;

    // The border is always fully drawn — an entrance "draw-on" here made every frame blink out of
    // existence during the hover entrance, which read as the tooltips losing their border.
    float alpha = band * ColorModulator.a;
    fragColor = vec4(col * ColorModulator.rgb, alpha);
}

