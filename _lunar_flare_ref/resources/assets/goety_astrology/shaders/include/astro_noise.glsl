#ifndef GOETY_ASTRO_NOISE_GLSL
#define GOETY_ASTRO_NOISE_GLSL

// ── 星象学 VFX 共享噪声库 ──────────────────────────────────────────────────────
// All 星象学 shaders import this instead of carrying private copies, so one fix
// fixes everywhere. Names are unprefixed on purpose (call sites read naturally);
// a shader that defines its OWN hash()/noise() must NOT import this file.

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

/** Two octaves — enough for drift and wisps; more just costs fill-rate here. */
float fbm(vec2 p) {
    float v = noise(p) * 0.667;
    v += noise(p * 2.03 + vec2(17.1, 9.2)) * 0.333;
    return v;
}

#endif
