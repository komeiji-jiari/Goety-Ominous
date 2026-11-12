#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float Dissolve;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in vec2 texCoord0;
in float vertexDistance;

out vec4 fragColor;

float gmHash(vec2 p) {
    p = fract(p * vec2(0.1031, 0.1030));
    p += dot(p, p.yx + 33.33);
    return fract((p.x + p.y) * p.x);
}

void main() {
    vec4 texel = texture(Sampler0, texCoord0);
    float lum = dot(texel.rgb, vec3(0.299, 0.587, 0.114));
    float maxc = max(max(texel.r, texel.g), texel.b);
    float minc = min(min(texel.r, texel.g), texel.b);
    float sat = maxc > 0.0001 ? (maxc - minc) / maxc : 0.0;
    float mask = smoothstep(0.72, 0.95, lum) * (1.0 - smoothstep(0.18, 0.45, sat));
    if (mask <= 0.001) {
        discard;
    }
    if (Dissolve > 0.0) {
        float n = gmHash(floor(texCoord0 * 128.0));
        if (n < Dissolve * 1.1 - 0.05) {
            discard;
        }
    }
    float alpha = texel.a * mask * vertexColor.a * ColorModulator.a;
    alpha *= 1.0 - smoothstep(0.75, 1.0, Dissolve);
    float fog = clamp((FogEnd - vertexDistance) / max(0.0001, FogEnd - FogStart), 0.0, 1.0);
    alpha *= fog;
    if (alpha < 0.001) {
        discard;
    }
    vec3 glow = texel.rgb * 1.6 * vertexColor.rgb * ColorModulator.rgb;
    fragColor = vec4(glow * alpha, alpha);
}
