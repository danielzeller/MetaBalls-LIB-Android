package no.opengl.danielzeller.opengltesting.opengl.shaderprograms

import android.content.Context

import no.opengl.danielzeller.opengltesting.opengl.util.ShaderHelper
import no.opengl.danielzeller.opengltesting.opengl.util.TextResourceReader

import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30

abstract class ShaderProgram(protected var vertexShaderResourceId: Int, protected var fragmentShaderResourceId: Int) {

    var isLoaded: Boolean = false

    var program: Int = 0
        protected set

    var positionAttributeLocation: Int = 0
        set
        get

    var textureCoordinatesAttributeLocation: Int = 0
        set
        get
    var textureCoordinatesAttributeLocation2: Int = 0
        set
        get


    open fun load(context: Context) {

        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(
                        context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderResourceId))
        isLoaded = true
    }

    fun useProgram() {
        glUseProgram(program)
    }
    fun useProgram3() {
        GLES30.glUseProgram(program)
    }

    fun bindData(textureProgram: ShaderProgram) {

    }

    companion object {
        val U_MATRIX = "u_Matrix"
        val U_TEXTURE_UNIT = "u_TextureUnit"

        val A_POSITION = "a_Position"
        val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
        val A_TEXTURE_COORDINATES2 = "a_TextureCoordinates2"
    }
}
