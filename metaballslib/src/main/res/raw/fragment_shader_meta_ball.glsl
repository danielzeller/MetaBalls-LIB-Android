#extension GL_OES_EGL_image_external : require
precision highp float;

uniform samplerExternalOES surface_texture;
uniform float cutoff;
varying vec2 v_TextureCoordinates;

float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax)
{
    return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;
}

void main()                    		
{
    vec2 teCoord = v_TextureCoordinates;
    teCoord.y=1.0-teCoord.y;

 	vec4 srcColor = texture2D(surface_texture, teCoord);

    srcColor.rgb += srcColor.rgb*(1.4-(srcColor.a*1.4));

    if(srcColor.a<0.75){
        srcColor.rgb += srcColor.rgb*(1.0-(srcColor.a*1.0))*0.07;
    }

    if(srcColor.a<cutoff){
        srcColor.a=0.0;
    } else {
        srcColor.a=1.0;
    }

    gl_FragColor =  srcColor;

}