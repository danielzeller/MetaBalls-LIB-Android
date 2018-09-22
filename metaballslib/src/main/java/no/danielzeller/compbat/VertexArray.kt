package no.opengl.danielzeller.opengltesting.opengl.transforms

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glVertexAttribPointer

class VertexArray(vertexData: FloatArray) {

    private val floatBuffer: FloatBuffer
    val BYTES_PER_FLOAT = 4

    init {
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData)
    }

    fun setVertexAttribPointer(dataOffset: Int, attributeLocation: Int, componentCount: Int, stride: Int) {

        floatBuffer.position(dataOffset)
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, stride, floatBuffer)
        glEnableVertexAttribArray(attributeLocation)

        floatBuffer.position(0)
    }
}
