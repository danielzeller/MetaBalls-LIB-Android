package no.danielzeller.compbat

import android.content.Context
import android.graphics.Canvas
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log
import android.view.Surface

class ViewSurfaceTexture : SnapshotTexture() {

    var textureWidth: Int = 0
    var textureHeight: Int = 0

    private var isReady: Boolean = false
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    override fun createSurface(width: Int, height: Int, context: Context) {
        if (textureWidth == 0) {
            textureWidth = width
            textureHeight = height
            releaseSurface()
            surfaceTextureID = createTexture()
            if (surfaceTextureID > 0) {
                surfaceTexture = SurfaceTexture(surfaceTextureID)
                surfaceTexture!!.setDefaultBufferSize(textureWidth, textureHeight)
                surface = Surface(surfaceTexture)
            }
        }
    }

    override fun updateTexture() {
        surfaceTexture?.updateTexImage()
    }

    override fun isLoaded(): Boolean {
        return isReady
    }

    override fun releaseSurface() {
        surface?.release()
        surfaceTexture?.release()
        surface = null
        surfaceTexture = null
    }

    override fun beginDraw(): Canvas? {

        if (surface != null) {
            try {
                    return surface?.lockHardwareCanvas()
            } catch (e: Exception) {
                Log.e("GL_ERROR", "error while rendering view to gl: $e")
            }
        }
        return null
    }

    fun endDraw(surfaceCanvas: Canvas?) {
        if (surfaceCanvas != null) {
            surface?.unlockCanvasAndPost(surfaceCanvas)
        }
    }

    private fun createTexture(): Int {
        val textures = IntArray(1)

        // Generate the texture to where android view will be rendered
        glActiveTexture(GL_TEXTURE0)
        glGenTextures(1, textures, 0)
        checkGlError("Texture generate")

        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0])
        checkGlError("Texture bind")

        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        return textures[0]
    }

    private fun checkGlError(op: String) {
        val error: Int = glGetError()
        if (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", op + ": glError " + GLUtils.getEGLErrorString(error))
        }
    }
}