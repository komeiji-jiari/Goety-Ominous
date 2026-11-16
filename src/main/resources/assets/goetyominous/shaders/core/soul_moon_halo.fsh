#version 150

// 采样的是烘焙好的八相月食图集：方块遮挡与青色轮廓光晕都在贴图里，
// 这里只负责叠加一次很轻的呼吸，避免着色器里再做差分采样（既慢又容易编译失败）。
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec4 texel = texture(Sampler0, texCoord0);
    // GameTime 是一天的进度（0..1），乘 1200 才是近似秒数的流动节奏。
    float time = GameTime * 1200.0;
    float breathe = 0.92 + 0.08 * sin(time * 0.8);
    fragColor = vec4(texel.rgb * breathe, texel.a * ColorModulator.a);
}
