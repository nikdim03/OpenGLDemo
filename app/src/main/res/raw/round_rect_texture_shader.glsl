in vec2 vTexCoord;
out vec4 fragColor;
vec2 dimensions = vec2(100.0,100.0);
float cornerRadius = 0.6;
void main() {

    vec2 uv = vTexCoord.xy;

    // This creates a normalized coordinate system with the origin at (0.5, 0.5)
    vec2 normCoord = 2.0*uv - 1.0;

    float radius = cornerRadius;

    // Calculate the distance from uv to the nearest corner
    float dist = distance(normCoord, vec2(clamp(normCoord.x, -0.5, 0.5), clamp(normCoord.y, -0.5, 0.5)));

    // If the distance is greater than the radius, discard the fragment
    if(dist > radius)
    discard;
    else
    fragColor=texture(texture_0,vTexCoord);
}
