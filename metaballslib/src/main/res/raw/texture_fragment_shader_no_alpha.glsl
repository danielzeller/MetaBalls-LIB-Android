#extension GL_OES_EGL_image_external : require
precision mediump float;

      	 				
uniform samplerExternalOES surface_texture;
varying vec2 v_TextureCoordinates;      	   								
varying vec2 v_TextureCoordinates2;
float remap(float value, float inputMin, float inputMax, float outputMin, float outputMax)
{
    return (value - inputMin) * ((outputMax - outputMin) / (inputMax - inputMin)) + outputMin;
}
void main()                    		
{
    vec2 teCoord = v_TextureCoordinates;
    teCoord.y=1.0-teCoord.y;

 	vec4 srcColor = texture2D(surface_texture, teCoord);
    srcColor.rgb += srcColor.rgb*(1.0-srcColor.a)*1.7;
       if(srcColor.a<0.6)
                srcColor.a=0.0;
            else
                srcColor.a=1.0;
    gl_FragColor =  srcColor;

}