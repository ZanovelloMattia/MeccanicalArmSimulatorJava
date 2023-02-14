#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragToLightVector;
in vec3 fragToCameraVector;


out vec4 fragColour;

struct Material {
//vec4 ambient;
//vec4 diffuse;
//vec4 specular;
    float reflectance;
    float reflectancePow;
};

struct SunLight{
    vec3 colour;
    //vec3 pos;
    float intensity;
};


uniform Material material;
uniform SunLight sunlight;
uniform sampler2D textureSampler;






void main(){
    vec3 nNormal = normalize(fragNormal);
    vec3 nfragToLightVector = normalize(fragToLightVector);

    float ndotD = max(dot(nNormal, nfragToLightVector), sunlight.intensity);
    vec3 diffuse = ndotD * sunlight.colour;
    vec3 nfragToCameraVector = normalize(fragToCameraVector);
    vec3 lightDirection = -nfragToLightVector;
    vec3 reflectedLightDirection = normalize(reflect(lightDirection, nNormal));
    float specularFactor = max(dot(nfragToCameraVector, reflectedLightDirection),0.0);
    float dumpedSpecularFactor = pow(specularFactor, material.reflectancePow);
    vec3 finalSpecular = dumpedSpecularFactor * sunlight.colour * material.reflectance;

    if(ndotD <= sunlight.intensity){
        fragColour = texture(textureSampler, fragTextureCoord) * vec4(diffuse,1.0);
    } else {
        fragColour = texture(textureSampler, fragTextureCoord) * vec4(diffuse, 1.0) + (vec4(finalSpecular, 1.0));
    }
}