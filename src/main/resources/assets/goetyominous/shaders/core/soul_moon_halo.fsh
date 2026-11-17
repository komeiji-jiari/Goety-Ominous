#version 150

// Soul moon atlas: rows 0-1 = normal blue moon, rows 2-3 = eclipse.
// texCoord0 is the 0..1 UV inside the current vanilla phase cell (col 0-3, row 0-1).
// Eclipse (0..1) selects which half of the atlas to sample and cross-fades.
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;
uniform float Eclipse;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    // Atlas is 4x4 cells; each state occupies a 4x2 band.
    vec2 uvNormal = vec2(texCoord0.x, texCoord0.y * 0.5);
    vec2 uvEclipse = vec2(texCoord0.x, 0.5 + texCoord0.y * 0.5);
    vec4 normal = texture(Sampler0, uvNormal);
    vec4 eclipse = texture(Sampler0, uvEclipse);
    // Both states have an opaque disc; taking max keeps stars blocked mid-fade.
    vec4 texel = mix(normal, eclipse, Eclipse);
    texel.a = max(normal.a, eclipse.a);

    float time = GameTime * 1200.0;
    float breathe = 0.94 + 0.06 * sin(time * 0.8);
    fragColor = vec4(texel.rgb * breathe, texel.a * ColorModulator.a);
}
