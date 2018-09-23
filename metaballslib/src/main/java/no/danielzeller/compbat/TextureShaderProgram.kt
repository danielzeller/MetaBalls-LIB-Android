package no.opengl.danielzeller.opengltesting.opengl.shaderprograms

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES20.*

import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log

class TextureShaderProgram(vertexShaderResourceId: Int, fragmentShaderResourceId: Int) : ShaderProgram(vertexShaderResourceId, fragmentShaderResourceId) {

    private var uMatrixLocation: Int = 0
    private var uTextureUnitLocation: Int = 0

    override fun load(context: Context) {

        super.load(context)
        uMatrixLocation = glGetUniformLocation(program, ShaderProgram.Companion.U_MATRIX)
        uTextureUnitLocation = glGetUniformLocation(program, ShaderProgram.Companion.U_TEXTURE_UNIT)

        positionAttributeLocation = glGetAttribLocation(program, ShaderProgram.Companion.A_POSITION)
        textureCoordinatesAttributeLocation = glGetAttribLocation(program, ShaderProgram.Companion.A_TEXTURE_COORDINATES)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int, cutoffFactor: Float) {

        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GLES30.GL_TEXTURE0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        glUniform1i(glGetUniformLocation(program, "surface_texture"), 0)
        glUniform1f(glGetUniformLocation(program, "cutoff"), cutoffFactor)
    }

    fun checkGlError(op: String) {
        var error: Int = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("GL_ERROR", op + ": glError " + GLUtils.getEGLErrorString(error))
        }
    }
}
