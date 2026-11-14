#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 planeCoord;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
    // UV0 arrives already normalised to −1..1 across the quad, so the fragment stage can talk in
    // "fraction of the blast radius" instead of in pixels or in blocks.
    planeCoord = UV0;
}
