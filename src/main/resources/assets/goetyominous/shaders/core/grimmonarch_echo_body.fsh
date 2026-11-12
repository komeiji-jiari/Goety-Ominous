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
    if (texel.a < 0.05) {
        discard;
    }
    if (Dissolve > 0.0) {
        float n = gmHash(floor(texCoord0 * 128.0));
        if (n < Dissolve * 1.1 - 0.05) {
            discard;
        }
    }
    vec4 color = texel * vertexColor * ColorModulator;
    color.a *= 1.0 - smoothstep(0.75, 1.0, Dissolve);
    float fog = clamp((FogEnd - vertexDistance) / max(0.0001, FogEnd - FogStart), 0.0, 1.0);
    color.a *= fog;
    if (color.a < 0.001) {
        discard;
    }
    fragColor = color;
}
