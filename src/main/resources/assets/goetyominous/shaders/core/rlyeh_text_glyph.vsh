#version 150

// Vertex stage for the 星象学 glyph material shader. Geometry handling identical to vanilla's
// rendertype_text (position through ModelViewMat/ProjMat, atlas UV passed through for the fragment
// stage). The vertex colour is passed through UNMULTIPLIED: the reference's effect-font vertex stage
// skips the lightmap for encoded glyphs, and our material pass is full-bright by contract — the old
// `Color * texelFetch(Sampler2, ...)` silently zeroed the alpha under Modern UI, whose render-type
// state stack does not bind the lightmap the way vanilla's does (every fragment discarded).

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}
