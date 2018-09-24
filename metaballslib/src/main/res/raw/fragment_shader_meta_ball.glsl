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
    vec2 textureCoordinates = v_TextureCoordinates;
    textureCoordinates.y = 1.0 - textureCoordinates.y;

 	vec4 surfaceTextureColor = texture2D(surface_texture, textureCoordinates);

 	//Adding some more color based on texture alpha to get a more flat look
    surfaceTextureColor.rgb += surfaceTextureColor.rgb*(1.4-(surfaceTextureColor.a*1.4));
    if(surfaceTextureColor.a<0.75){
        surfaceTextureColor.rgb += surfaceTextureColor.rgb*(1.0-(surfaceTextureColor.a*1.0))*0.07;
    }

    //Chop away a alpha lower than cutoff value
    if(surfaceTextureColor.a<cutoff){
        discard;
    } else {
        surfaceTextureColor.a=1.0;
    }

    gl_FragColor =  surfaceTextureColor;
}