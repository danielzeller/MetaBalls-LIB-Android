package no.danielzeller.metaballslib.spinner

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import no.danielzeller.metaballslib.R
import no.danielzeller.metaballslib.spinner.drawables.BlobSpinnerDrawable
import no.danielzeller.metaballslib.spinner.drawables.PathSpinnerDrawable
import no.danielzeller.metaballslib.spinner.drawables.JumpingDotSpinnerDrawable
import no.danielzeller.metaballslib.spinner.drawables.SpinnerDrawable


enum class SpinnerType {
    CIRCULAR, BLOBS, DOTS, EIGHT, SQUARE, LONG_PATH
}

interface SpinneHiddenListener {
    fun onSpinnHidden(spinner: View)
}

class Spinner : FrameLayout {

    private lateinit var colorArray: IntArray
    private lateinit var circularSpinnerDrawable: SpinnerDrawable
    private lateinit var spinnerType: SpinnerType
    private var isDropDrawable: Boolean = true
    private var isRotate: Boolean = true

    constructor(context: Context) : super(context) {
        loadAttributesFromXml(null)
        setupBaseViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadAttributesFromXml(attrs)
        setupBaseViews(context)
    }

    /**
     * Stops the spinner animated, and sets View.GONE after an exit animation.
     * @param spinnerHiddenListener
     * Callback for when the Spinner is hidden
     */
    fun stopAnimated(spinnerHiddenListener: SpinneHiddenListener? = null) {
        circularSpinnerDrawable.stopAndHide(this, spinnerHiddenListener)
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
            isDropDrawable = typedArray.getBoolean(R.styleable.MetaBallsSpinner_drop_drawable, true)
            isRotate = typedArray.getBoolean(R.styleable.MetaBallsSpinner_rotate, false)

        } finally {
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (width != height && isRotate) {
            Log.w("SPINNER view", "Warning: width and height are not equal. This may lead to unexpected results when rotation is enabled.")
        }
    }

    private fun setupBaseViews(context: Context) {
        val spinnerImageView = ImageView(context)
        spinnerImageView.setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
        circularSpinnerDrawable = createSpinnerDrawable()
        spinnerImageView.setImageDrawable(circularSpinnerDrawable as Drawable)
        addView(spinnerImageView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    private fun createSpinnerDrawable(): SpinnerDrawable {
        if (spinnerType == SpinnerType.CIRCULAR) {
            return PathSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, parsePath(CIRCLE_PATH_DATA), isDropDrawable, isRotate)
        } else if (spinnerType == SpinnerType.EIGHT) {
            return PathSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, parsePath(EIGHT_PATH_DATA), isDropDrawable, isRotate, 900, LinearInterpolator())
        } else if (spinnerType == SpinnerType.BLOBS) {
            return BlobSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, isRotate)
        } else if (spinnerType == SpinnerType.SQUARE) {
            return PathSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, parsePath(SQUARE_PATH_DATA), isDropDrawable, isRotate)
        } else if (spinnerType == SpinnerType.LONG_PATH) {
            return PathSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, parsePath(LONG_PATH_DATA), isDropDrawable, isRotate, 1300, LinearInterpolator())
        }
        return JumpingDotSpinnerDrawable(resources.getDrawable(R.mipmap.gradient_oval, null), colorArray, isDropDrawable)
    }

    private fun parsePath(pathData: FloatArray): Path {
        val path = Path()
        path.moveTo(pathData[0], pathData[1])
        for (i in 2 until pathData.size step 6) {
            path.cubicTo(pathData[i], pathData[i + 1], pathData[i + 2], pathData[i + 3], pathData[i + 4], pathData[i + 5])
        }
        return path
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

    val EIGHT_PATH_DATA = floatArrayOf(85f, 50.934f, 85f, 58.16f, 81.419f, 67f, 70.09f, 67f, 58.761f, 67f, 51.776f, 53.948f, 48.5f, 50.5f, 45.224f, 47.052f, 37.252f, 34f, 26.717f, 34f, 16.181f, 34f, 12f, 43.123f, 12f, 50.934f, 12f, 58.744f, 16.256f, 67f, 25.354f, 67f, 34.451f, 67f, 44.12f, 55.534f, 48.5f, 50.934f, 52.88f, 46.334f, 59.597f, 34f, 70.77f, 34f, 81.943f, 34f, 85f, 43.708f, 85f, 50.934f)
    val CIRCLE_PATH_DATA = floatArrayOf(51.243f, 12.001f, 69.013f, 12.001f, 88.112f, 27.121f, 88.112f, 50f, 88.112f, 72.879f, 67.671f, 87.084f, 51.243f, 87.084f, 34.815f, 87.084f, 13.04f, 75.041f, 13.04f, 49.679f, 13.04f, 24.318f, 33.473f, 12.001f, 51.243f, 12.001f)
    val SQUARE_PATH_DATA = floatArrayOf(50.554f, 12.523f, 50.554f, 12.523f, 88.638f, 50.647f, 88.638f, 50.647f, 88.638f, 50.647f, 50.554f, 88.58f, 50.554f, 88.58f, 50.554f, 88.58f, 12.365f, 50.647f, 12.365f, 50.647f, 12.365f, 50.647f, 50.554f, 12.523f, 50.554f, 12.523f)
    val LONG_PATH_DATA = floatArrayOf(15f, 53.44f, 15f, 49.571f, 13.841f, 13.301f, 25.976f, 13.301f, 38.112f, 13.301f, 38.229f, 45.513f, 38.229f, 53.44f, 38.229f, 61.368f, 39.76f, 89.087f, 50f, 89.087f, 60.24f, 89.087f, 60.797f, 60.835f, 60.797f, 53.44f, 60.797f, 46.045f, 61.581f, 13.301f, 73.29f, 13.301f, 84.999f, 13.301f, 84.999f, 40.987f, 84.999f, 53.44f, 84.999f, 65.894f, 84.999f, 90.718f, 73.29f, 90.718f, 61.581f, 90.718f, 60.797f, 64.684f, 60.797f, 53.44f, 60.797f, 42.197f, 61.771f, 13.301f, 50f, 13.301f, 38.229f, 13.301f, 38.229f, 42.623f, 38.229f, 53.44f, 38.229f, 64.257f, 37.07f, 89.087f, 25.976f, 89.087f, 14.883f, 89.087f, 15f, 57.31f, 15f, 53.44f)
}