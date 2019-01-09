package no.danielzeller.compbat

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import no.danielzeller.metaballslib.R

open class CompBatMBLayout : FrameLayout {

    protected var isPreAndroidPie = false
    protected var useTextureViewOnPrePie = true
    private lateinit var textureView: TextureView
    private lateinit var textureViewRenderer: TextureViewRenderer

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        textureViewRenderer = TextureViewRenderer(context)
        loadAttributesFromXml(attrs)
    }

    private fun loadAttributesFromXml(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsProgressBar,
                0, 0)
        try {
            useTextureViewOnPrePie = typedArray.getBoolean(R.styleable.MetaBallsProgressBar_useTextureViewOnPrePie, true)

        } finally {
            typedArray.recycle()
        }
    }

    fun compBatAddTextureView(frameLayout: FrameLayout) {
        if (isPreAndroidPie) {

            textureView = TextureView(context)
            textureView.surfaceTextureListener = textureViewRenderer
            textureViewRenderer.onSurfaceTextureCreated = {
                drawTextureView()
                val fadeIn = ObjectAnimator.ofFloat(textureView, View.ALPHA, 0f, 1f).setDuration(200)
                fadeIn.start()
            }
            frameLayout.addView(textureView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            textureView.alpha = 0f

        }
    }

    protected fun drawTextureView() {
        if (isPreAndroidPie && textureViewRenderer.isCreated) {
            textureViewRenderer.cutoffFactor = getCutoffFactor()
            val glCanvas = textureViewRenderer.surfaceTexture.beginDraw()
            glCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            if (glCanvas != null) {
                val metaBallContainer = getChildAt(0)
                drawChild(glCanvas, metaBallContainer, drawingTime)
            }
            textureViewRenderer.updateForMilliSeconds(100)
            textureViewRenderer.surfaceTexture.endDraw(glCanvas)
        }
    }

    open fun getCutoffFactor(): Float {
        return 0.65f
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (isPreAndroidPie) {
            drawChild(canvas, textureView, drawingTime)
        } else {
            super.dispatchDraw(canvas)
        }
    }

    open fun setupBaseViews(context: Context) {
        if (useTextureViewOnPrePie) {
            isPreAndroidPie = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P
        } else {
            isPreAndroidPie = false
        }
    }
}