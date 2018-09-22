package no.agens.bbctransitions.opengl.renderers

import android.content.Context
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20.*
import android.opengl.Matrix.setIdentityM
import android.util.Log
import no.agens.bbctransitions.opengl.gameobject.ViewSurfaceTexture
import no.danielzeller.metaballslib.R
import no.opengl.danielzeller.opengltesting.opengl.meshes.SpriteMesh
import no.opengl.danielzeller.opengltesting.opengl.shaderprograms.TextureShaderProgram
import no.opengl.danielzeller.opengltesting.opengl.util.TextureHelper

interface SurfaceReadyListener {
    fun onSurfaceReady()
}

class MetaBallRenderer(private val context: Context) : Renderer, BaseRenderer() {

    private val projectionMatrixOrtho = FloatArray(16)
    private lateinit var spriteMesh: SpriteMesh
    val surfaceTexture = ViewSurfaceTexture()
    private val fullscreenTextureShader = TextureShaderProgram(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader_no_alpha)
    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        spriteMesh = SpriteMesh()
        fullscreenTextureShader.load(context)
        loadedTexture = TextureHelper.loadTexture(context,R.drawable.gradient_oval)
    }
    var loadedTexture=0
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        glViewport(0, 0, width, height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        val left = -1.0f
        val right = 1.0f
        val bottom = 1.0f
        val top = -1.0f
        val near = -1.0f
        val far = 1.0f

        setIdentityM(projectionMatrixOrtho, 0)
        Matrix.orthoM(projectionMatrixOrtho, 0, left, right, bottom, top, near, far)

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        surfaceTexture.createSurface(width,height,context)
        surfaceReadyListener?.onSurfaceReady()
    }

    override fun onDrawFrame(glUnused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        renderFullscreenRenderTexture()
    }


    private fun renderFullscreenRenderTexture() {

        val left = -1.0f
        val right = 1.0f
        val bottom = 1.0f
        val top = -1.0f
        val near = -1.0f
        val far = 1.0f
        surfaceTexture.updateTexture()
        setIdentityM(projectionMatrixOrtho, 0)
        Matrix.orthoM(projectionMatrixOrtho, 0, left, right, bottom, top, near, far)
        fullscreenTextureShader.useProgram()
        fullscreenTextureShader.setUniforms(projectionMatrixOrtho, surfaceTexture.getTextrueID())
        spriteMesh.bindData(fullscreenTextureShader)
        spriteMesh.draw()
    }
}
