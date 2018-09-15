package no.danielzeller.metaballslib.spinner

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import no.danielzeller.metaballslib.R


enum class SpinnerType {
    CIRCULAR, CIRCULAR_V2, BLOBS, DOTS
}

class Spinner : FrameLayout {

    private lateinit var colorArray: IntArray
    private lateinit var circularSpinnerDrawable: SpinnerDrawable
    private lateinit var spinnerType: SpinnerType

    constructor(context: Context) : super(context) {
        loadAttributesFromXml(null)
        setupBaseViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadAttributesFromXml(attrs)
        setupBaseViews(context)
    }

    private fun loadAttributesFromXml(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsSpinner,
                0, 0)
        try {
            val colorsArrayID = typedArray.getResourceId(R.styleable.MetaBallsSpinner_colors_array_id, R.array.default_spinner_colors)
            colorArray = resources.getIntArray(colorsArrayID)
            spinnerType = convertIntToSpinnertype(typedArray.getInteger(R.styleable.MetaBallsSpinner_spinner_type, SpinnerType.CIRCULAR.ordinal))
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupBaseViews(context: Context) {
        val spinnerImageView = ImageView(context)
        spinnerImageView.setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
        circularSpinnerDrawable = createSpinnerDrawable()
        spinnerImageView.setImageDrawable(circularSpinnerDrawable as Drawable)
        addView(spinnerImageView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
//        Handler().postDelayed({
//            stopAnimated()
//        }, 9000)
    }

    fun createSpinnerDrawable(): SpinnerDrawable {
        if (spinnerType == SpinnerType.CIRCULAR) {
            return CircularSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray)
        } else if (spinnerType == SpinnerType.CIRCULAR_V2) {
            return CircularSpinnerDrawableV2(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray)
        } else if (spinnerType == SpinnerType.BLOBS) {
            return BlobSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray)
        }
        return CircularSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            circularSpinnerDrawable.startAnimations()
        } else {
            circularSpinnerDrawable.stopAllAnimations()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        circularSpinnerDrawable.stopAllAnimations()
    }

    fun stopAnimated() {
        circularSpinnerDrawable.stopAndHide(this)
    }

    private fun createMetaBallsPaint(): Paint {
        val metaBallsPaint = Paint()
        metaBallsPaint.setColorFilter(ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 150f, -255 * 128f
        ))))
        return metaBallsPaint
    }

    private fun convertIntToSpinnertype(id: Int): SpinnerType {
        for (f in SpinnerType.values()) {
            if (f.ordinal == id) return f
        }
        return SpinnerType.CIRCULAR
    }
}