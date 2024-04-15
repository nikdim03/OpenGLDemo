in vec2 vTexCoord;
out vec4 fragColor;

float uBendFactor = 0.5;
void main() {

    // Map the y value of texture coordinates to a curve equation
    float curve = uBendFactor * (vTexCoord.y - 0.5);
    float bend = curve * curve * sign(curve);

    // Apply curve deformation to texture coordinates
    vec2 texCoord = vec2(vTexCoord.x, vTexCoord.y + bend);

    // Determine whether to discard fragments based on conditions
    if (texCoord.y < 0.0 || texCoord.y > 1.0) {
        discard;
    }
    // Get color from texture and draw
    fragColor = texture(texture_0, texCoord);
}
