package no.danielzeller.compbat

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION
import android.opengl.EGL14.EGL_OPENGL_ES2_BIT
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Handler
import android.view.TextureView
import no.danielzeller.metaballslib.R
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGL10.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

class TextureViewRenderer(val context: Context) : TextureView.SurfaceTextureListener {

    var width = 0
    var height = 0
    var isCreated = false
    var cutoffFactor = 0.65f
    var onSurfaceTextureCreated: (() -> Unit)? = null
    val surfaceTexture = ViewSurfaceTexture()
    private lateinit var renderer: RendererThread

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {} // called every time when swapBuffers is called

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        renderer.isStopped = true
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        this.width = width
        this.height = height
        renderer = RendererThread(surface)
        renderer.start()
    }

    inner class RendererThread(val surface: SurfaceTexture) : Thread() {

        var isStopped = false
        private val handler = Handler()
        private val projectionMatrixOrtho = FloatArray(16)
        private lateinit var spriteMesh: SpriteMesh
        private val fullscreenTextureShader = TextureShaderProgram(R.raw.vertex_shader, R.raw.fragment_shader_meta_ball)

        private val config = intArrayOf(
                EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_NONE
        )

        private fun chooseEglConfig(egl: EGL10, eglDisplay: EGLDisplay): EGLConfig {
            val configsCount = intArrayOf(0)
            val configs = arrayOfNulls<EGLConfig>(1)
            egl.eglChooseConfig(eglDisplay, config, configs, 1, configsCount)
            return configs[0]!!
        }

        override fun run() {
            super.run()

            val egl = EGLContext.getEGL() as EGL10
            val eglDisplay = egl.eglGetDisplay(EGL_DEFAULT_DISPLAY)
            egl.eglInitialize(eglDisplay, intArrayOf(0, 0))   // getting OpenGL ES 2
            val eglConfig = chooseEglConfig(egl, eglDisplay)
            val eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE))
            val eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null)


            while (!isStopped && egl.eglGetError() == EGL_SUCCESS) {
                egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)

                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                if (!isCreated) {
                    isCreated = true
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
                    spriteMesh = SpriteMesh()
                    fullscreenTextureShader.load(context)
                    GLES20.glViewport(0, 0, width, height)
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
                    val left = -1.0f
                    val right = 1.0f
                    val bottom = 1.0f
                    val top = -1.0f
                    val near = -1.0f
                    val far = 1.0f

                    Matrix.setIdentityM(projectionMatrixOrtho, 0)
                    Matrix.orthoM(projectionMatrixOrtho, 0, left, right, bottom, top, near, far)

                    GLES20.glEnable(GLES20.GL_BLEND)
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                    surfaceTexture.createSurface(width, height, context)
                    val canvas = surfaceTexture.beginDraw()
                    canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    surfaceTexture.endDraw(canvas)
                    handler.post { onSurfaceTextureCreated?.invoke() }

                }

                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
                renderFullscreenRenderTexture()
                egl.eglSwapBuffers(eglDisplay, eglSurface)

                Thread.sleep((1f / 60f * 1000f).toLong()) // in real life this sleep is more complicated
            }

            surface.release()
            egl.eglDestroyContext(eglDisplay, eglContext)
            egl.eglDestroySurface(eglDisplay, eglSurface)
        }

        private fun renderFullscreenRenderTexture() {
            surfaceTexture.updateTexture()
            fullscreenTextureShader.useProgram()
            fullscreenTextureShader.setUniforms(projectionMatrixOrtho, surfaceTexture.getTextureID(),cutoffFactor)
            spriteMesh.bindData(fullscreenTextureShader)
            spriteMesh.draw()
        }
    }
}