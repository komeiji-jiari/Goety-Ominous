#ifndef GOETY_ASTRO_GLOW_GLSL
#define GOETY_ASTRO_GLOW_GLSL

// ── 星象学 VFX 光晕边缘规范 ────────────────────────────────────────────────────
// THE RULE (learned the hard way, see the space-rift/comet/quasar rounds): an
// additive glow's outer edge MUST decay constructively to zero before the quad
// ends. A gaussian tail that is still bright at d = 1 gets hard-cut by the quad
// boundary and reads as a visible square seam.
//
//   d        0 at the effect's centre, 1 at the quad's edge
//   exponent falloff steepness (2..6 typical)
//   fadeWidth how much of the tail is reserved for the fade-out (>= 0.8 per rule)
//
// At d = 1 the product is exactly 0 — constructively, not by luck of sampling.

float astroGlowFalloff(float d, float exponent, float fadeWidth) {
    float core = pow(clamp(1.0 - d, 0.0, 1.0), exponent);
    return core * (1.0 - smoothstep(clamp(1.0 - fadeWidth, 0.0, 1.0), 1.0, d));
}

/** The faint long tail the rule asks to pad under a main glow, so edges never band. */
float astroGlowTail(float d, float width) {
    return exp(-max(d, 0.0) / max(width, 1e-4)) * (1.0 - smoothstep(0.92, 1.0, d));
}

#endif
