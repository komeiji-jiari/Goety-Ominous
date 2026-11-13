#version 150

uniform vec4 ColorModulator;

in vec4 vTint;
in vec2 vLocal;

out vec4 fragColor;

const float LEN_NOSE = 0.66;
const float LEN_TAIL = 0.26;
const float LEN_WING = 0.32;
const float LEN_SPINE = 0.35;

const float NOTCH = 0.55;

const float EXTENT = 0.93;

const vec3 C_HOT = vec3(0.90, 1.00, 1.00);
const vec3 C_BODY = vec3(0.42, 0.88, 1.00);
const vec3 C_RIM = vec3(0.05, 0.44, 1.00);

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

    float body = 1.0 - smoothstep(0.93, 1.005, d);
    float hot = exp(-d * d * 1.5);
    float halo = exp(-max(d - 1.0, 0.0) * 6.5) * 0.13 * vTint.b;

    vec3 col = mix(C_RIM, C_BODY, 1.0 - smoothstep(0.70, 0.98, d));
    col = mix(col, C_HOT, 1.0 - smoothstep(0.12, 0.60, d));

    float a = clamp(body * (0.92 + 0.08 * hot) + halo * (1.0 - body), 0.0, 1.0);
    a *= vTint.a * (1.0 - smoothstep(0.70, EXTENT, length(vLocal)));

    fragColor = vec4(col * vTint.r * ColorModulator.rgb, a);
}
