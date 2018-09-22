package no.opengl.danielzeller.opengltesting.opengl.meshes

import no.opengl.danielzeller.opengltesting.opengl.transforms.VertexArray
import no.opengl.danielzeller.opengltesting.opengl.shaderprograms.ShaderProgram

import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays

class SpriteMesh {

    var vertexArray: VertexArray
    var vertexArrayScreenSpace: VertexArray? = null

    init {
        vertexArray = VertexArray(VERTEX_DATA)
    }

    fun bindData(textureProgram: ShaderProgram) {

        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.positionAttributeLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE)

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.textureCoordinatesAttributeLocation,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE)
        vertexArrayScreenSpace?.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.textureCoordinatesAttributeLocation2,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE)
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }

    companion object {

        val BYTES_PER_FLOAT = 4
        private val POSITION_COMPONENT_COUNT = 2
        private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT


        val VERTEX_DATA = floatArrayOf(
                // Order of coordinates: X, Y, S, T
                // Triangle Fan
                0.0f, 0.0f, 0.5f, 0.5f,
                -1.0f, -1.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f, 0.0f,
                -1.0f, -1.0f, 0.0f, 1.0f)
    }
}
