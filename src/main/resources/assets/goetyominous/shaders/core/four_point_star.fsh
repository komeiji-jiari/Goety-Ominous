#version 150

// The quad star — a four-pointed sparkle with a long nose, drawn entirely analytically.
//
// There is no texture here and no geometry beyond two quads: the star is a *field*. UV0 carries the
// plane's own local coordinates straight through (in blocks, +x being the nose), and the fragment
// shader turns them into a filled shape. Every edge is a computed falloff, so the star has no
// silhouette to catch and nothing that survives being magnified.
//
// The outline is an eight-vertex star polygon, not a curve: four tips on the axes and four notches
// pushed in between them, joined by straight segments. Straight edges are what make the tips read as
// *corners* — a smooth superellipse rounds the tip open and the star stops looking cut. Because the
// polygon is star-shaped about the origin, the boundary radius along any direction is just the ray's
// first hit on those eight segments, which keeps the field cheap and the tip razor-sharp.
//
// Where the notches sit is the single shape knob:
//
//   NOTCH 0.80  the sides barely bow — a fat diamond
//   NOTCH 0.55  notches at ~20% of the nose reach, tips still long and thin
//   NOTCH 0.35  the notches swallow the body and the star falls apart into four blades
//
// Volume comes from drawing two quads crossed at 90 degrees through the flight axis. The primary
// quad carries the full four-pointed shape; the second is squashed across its wings and dimmed, so
// it only thickens the nose and tail instead of competing for the silhouette. Additive blending
// fuses the pair into one body of light.

uniform vec4 ColorModulator;

in vec4 vTint;   // r = brightness, g = wing scale (1 on the main quad, squashed on the cross quad),
                 // b = halo weight, a = intensity
in vec2 vLocal;  // plane-local position in blocks; +x is the nose

out vec4 fragColor;

const float LEN_NOSE = 0.66;
const float LEN_TAIL = 0.26;
const float LEN_WING = 0.32;
const float LEN_SPINE = 0.35;

const float NOTCH = 0.55;

// Where the field is forced to zero. Kept past the longest tip so the quad's own border can never
// clip a point into a blunt end.
const float EXTENT = 0.93;

const vec3 C_HOT = vec3(0.90, 1.00, 1.00);
const vec3 C_BODY = vec3(0.42, 0.88, 1.00);
const vec3 C_RIM = vec3(0.05, 0.44, 1.00);

// Distance from the origin to the segment a-b along the unit direction dir, or a huge number when the
// ray misses. Standard ray/segment cross-product solve, kept in 2D.
float rayHit(vec2 dir, vec2 a, vec2 b) {
    vec2 e = b - a;
    float den = dir.x * e.y - dir.y * e.x;
    if (abs(den) < 1.0E-9) {
        return 1.0E9;
    }
    float along = (a.x * e.y - a.y * e.x) / den;
    float edge = (a.x * dir.y - a.y * dir.x) / den;
    if (along <= 0.0 || edge < 0.0 || edge > 1.0) {
        return 1.0E9;
    }
    return along;
}

// Normalised distance from the core: 0 at the centre, exactly 1 on the silhouette. The point is
// squashed across y first so the cross quad gets a narrower version of the same polygon.
float starDist(vec2 p, float wing) {
    vec2 q = vec2(p.x, p.y / max(wing, 1.0E-4));
    float r = length(q);
    if (r < 1.0E-6) {
        return 0.0;
    }
    vec2 dir = q / r;

    vec2 tipNose = vec2(LEN_NOSE, 0.0);
    vec2 tipWing = vec2(0.0, LEN_WING);
    vec2 tipTail = vec2(-LEN_TAIL, 0.0);
    vec2 tipSpine = vec2(0.0, -LEN_SPINE);

    // Each notch sits on the diagonal between its two tips, pulled in by NOTCH.
    float k = NOTCH * 0.5;
    vec2 nNW = k * (tipNose + tipWing);
    vec2 nWT = k * (tipWing + tipTail);
    vec2 nTS = k * (tipTail + tipSpine);
    vec2 nSN = k * (tipSpine + tipNose);

    float best = 1.0E9;
    best = min(best, rayHit(dir, tipNose, nNW));
    best = min(best, rayHit(dir, nNW, tipWing));
    best = min(best, rayHit(dir, tipWing, nWT));
    best = min(best, rayHit(dir, nWT, tipTail));
    best = min(best, rayHit(dir, tipTail, nTS));
    best = min(best, rayHit(dir, nTS, tipSpine));
    best = min(best, rayHit(dir, tipSpine, nSN));
    best = min(best, rayHit(dir, nSN, tipNose));

    return r / max(best, 1.0E-6);
}

void main() {
    float d = starDist(vLocal, vTint.g);

    // Crisp silhouette, then a short halo outside it. The halo is suppressed on the cross quad,
    // where a long thin shape would otherwise smear its glow into a straight line.
    float body = 1.0 - smoothstep(0.93, 1.005, d);
    float hot = exp(-d * d * 1.5);
    float halo = exp(-max(d - 1.0, 0.0) * 6.5) * 0.13 * vTint.b;

    // Pale through the body, saturated at the rim — the colour is what sells it as light rather than
    // as a coloured plate.
    vec3 col = mix(C_RIM, C_BODY, 1.0 - smoothstep(0.70, 0.98, d));
    col = mix(col, C_HOT, 1.0 - smoothstep(0.12, 0.60, d));

    float a = clamp(body * (0.92 + 0.08 * hot) + halo * (1.0 - body), 0.0, 1.0);
    a *= vTint.a * (1.0 - smoothstep(0.70, EXTENT, length(vLocal)));

    fragColor = vec4(col * vTint.r * ColorModulator.rgb, a);
}
