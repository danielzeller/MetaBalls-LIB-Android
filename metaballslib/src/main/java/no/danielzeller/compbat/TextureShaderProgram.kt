package no.danielzeller.compbat

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.GLES30

class TextureShaderProgram(vertexShaderResourceId: Int, fragmentShaderResourceId: Int) : ShaderProgram(vertexShaderResourceId, fragmentShaderResourceId) {

    private var uMatrixLocation: Int = 0
    private var uTextureUnitLocation: Int = 0

    override fun load(context: Context) {

        super.load(context)
        uMatrixLocation = glGetUniformLocation(program, ShaderProgram.U_MATRIX)
        uTextureUnitLocation = glGetUniformLocation(program, ShaderProgram.U_TEXTURE_UNIT)

        positionAttributeLocation = glGetAttribLocation(program, ShaderProgram.A_POSITION)
        textureCoordinatesAttributeLocation = glGetAttribLocation(program, ShaderProgram.A_TEXTURE_COORDINATES)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int, cutoffFactor: Float) {

        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GLES30.GL_TEXTURE0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        glUniform1i(glGetUniformLocation(program, "surface_texture"), 0)
        glUniform1f(glGetUniformLocation(program, "cutoff"), cutoffFactor)
    }
}
