package no.agens.bbctransitions.opengl.gameobject

import android.content.Context
import android.graphics.Canvas
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Surface
import no.danielzeller.compbat.SnapshotTexture

class ViewSurfaceTexture : SnapshotTexture() {
    var surfaceTexture: SurfaceTexture? = null
    var surface: Surface? = null


    var surfaceCanvas: Canvas? = null

    var textureWidth: Int = 0
    var textureHeight: Int = 0

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
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    surfaceCanvas = surface?.lockHardwareCanvas()
//                } else {
                    surfaceCanvas = surface?.lockCanvas(null)
//                }

            } catch (e: Exception) {
                Log.e("GL_ERROR", "error while rendering view to gl: " + e)
            }
        }
        return surfaceCanvas
    }

    var isReady: Boolean = false
    var handler = Handler()

    override fun endDraw() {
        surface?.unlockCanvasAndPost(surfaceCanvas)
        if (surface != null && !isReady) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    isReady = true
                }
            }, 0)
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


    fun checkGlError(op: String) {
        var error: Int = glGetError()
        if (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", op + ": glError " + GLUtils.getEGLErrorString(error))
        }
    }
}