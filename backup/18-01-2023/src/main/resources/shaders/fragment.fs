#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragToLightVector;


out vec4 fragColour;

uniform sampler2D textureSampler;
uniform float ambientLight;
uniform vec3 colourLight;
uniform float reflectivity;
uniform float reflectancePow;
uniform vec3 fragToCameraVector;


void main(){

    vec3 nNormal = normalize(fragNormal);
    vec3 nfragToLightVector = normalize(fragToLightVector);

    float ndotD = max(dot(nNormal, nfragToLightVector), ambientLight);
    vec3 diffuse = ndotD * colourLight;

    vec3 nfragToCameraVector = normalize(fragToLightVector);
    vec3 lightDirection = -nfragToLightVector;
    vec3 reflectedLightDirection = normalize(reflect(lightDirection, nNormal));

    float specularFactor = max(dot(nfragToCameraVector, reflectedLightDirection),0.0);
    float dumpedSpecularFactor = pow(specularFactor, reflectancePow);
    vec3 finalSpecular = dumpedSpecularFactor * colourLight * reflectivity;

    if(ndotD <= ambientLight){
        fragColour = texture(textureSampler, fragTextureCoord) * vec4(diffuse,1.0);
    } else {
        fragColour = texture(textureSampler, fragTextureCoord) * vec4(diffuse, 1.0) + (vec4(finalSpecular, 1.0));
    }
}