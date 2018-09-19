package no.danielzeller.metaballslib.progressbar

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
import no.danielzeller.metaballslib.progressbar.drawables.ProgressBlobDrawable
import no.danielzeller.metaballslib.progressbar.drawables.ProgressDrawable
import no.danielzeller.metaballslib.progressbar.drawables.ProgressJumpingDotDrawable
import no.danielzeller.metaballslib.progressbar.drawables.ProgressPathDrawable


enum class MBProgressBarType {
    CIRCULAR, BLOBS, DOTS, EIGHT, SQUARE, LONG_PATH
}

class MBProgressBar : FrameLayout {


    /**
     * Sets the ProgressBar drawable type
     */
    var mbProgressBarType: MBProgressBarType = MBProgressBarType.CIRCULAR
        set(value) {
            if (progressDrawable != null && field != value) {
                field = value
                rebuildDrawable()
            } else {
                field = value
            }
        }


    /**
     * With this enabled the moving circles will look like a water drop(with a tail)
     */
    var isDropDrawable: Boolean = true
        set(value) {
            field = value
            progressDrawable?.setDrop(field)
        }


    /**
     * Should the ProgressBar Drawable rotate. Note: Width and Height of the View should be equal in order to
     * get the correct result.
     */
    var isRotate: Boolean = true
        set(value) {
            field = value
            progressDrawable?.rotate = field
        }


    /**
     * Sets the colors of each circle
     */
    var colorArray = intArrayOf()
        set(value) {
            field = value
            progressDrawable?.tinColorsArray = value
        }

    private lateinit var spinnerImageView: ImageView
    private var progressDrawable: ProgressDrawable? = null

    private val EIGHT_PATH_DATA = floatArrayOf(85f, 50.934f, 85f, 58.16f, 81.419f, 67f, 70.09f, 67f, 58.761f, 67f, 51.776f, 53.948f, 48.5f, 50.5f, 45.224f, 47.052f, 37.252f, 34f, 26.717f, 34f, 16.181f, 34f, 12f, 43.123f, 12f, 50.934f, 12f, 58.744f, 16.256f, 67f, 25.354f, 67f, 34.451f, 67f, 44.12f, 55.534f, 48.5f, 50.934f, 52.88f, 46.334f, 59.597f, 34f, 70.77f, 34f, 81.943f, 34f, 85f, 43.708f, 85f, 50.934f)
    private val CIRCLE_PATH_DATA = floatArrayOf(51.243f, 12.001f, 69.013f, 12.001f, 88.112f, 27.121f, 88.112f, 50f, 88.112f, 72.879f, 67.671f, 87.084f, 51.243f, 87.084f, 34.815f, 87.084f, 13.04f, 75.041f, 13.04f, 49.679f, 13.04f, 24.318f, 33.473f, 12.001f, 51.243f, 12.001f)
    private val SQUARE_PATH_DATA = floatArrayOf(50.554f, 12.523f, 50.554f, 12.523f, 88.638f, 50.647f, 88.638f, 50.647f, 88.638f, 50.647f, 50.554f, 88.58f, 50.554f, 88.58f, 50.554f, 88.58f, 12.365f, 50.647f, 12.365f, 50.647f, 12.365f, 50.647f, 50.554f, 12.523f, 50.554f, 12.523f)
    private val LONG_PATH_DATA = floatArrayOf(15f, 53.44f, 15f, 49.571f, 13.841f, 13.301f, 25.976f, 13.301f, 38.112f, 13.301f, 38.229f, 45.513f, 38.229f, 53.44f, 38.229f, 61.368f, 39.76f, 89.087f, 50f, 89.087f, 60.24f, 89.087f, 60.797f, 60.835f, 60.797f, 53.44f, 60.797f, 46.045f, 61.581f, 13.301f, 73.29f, 13.301f, 84.999f, 13.301f, 84.999f, 40.987f, 84.999f, 53.44f, 84.999f, 65.894f, 84.999f, 90.718f, 73.29f, 90.718f, 61.581f, 90.718f, 60.797f, 64.684f, 60.797f, 53.44f, 60.797f, 42.197f, 61.771f, 13.301f, 50f, 13.301f, 38.229f, 13.301f, 38.229f, 42.623f, 38.229f, 53.44f, 38.229f, 64.257f, 37.07f, 89.087f, 25.976f, 89.087f, 14.883f, 89.087f, 15f, 57.31f, 15f, 53.44f)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadAttributesFromXml(attrs)
        setupBaseViews(context)
    }

    /**
     * Stops the spinner animated, and sets View.GONE after an exit animation.
     * @param spinnerHiddenListener  Callback for when the Spinner is hidden.
     */
    fun stopAnimated( onProgressBarHiddenListener: (() -> Unit)? = null) {
        progressDrawable?.stopAndHide(this, onProgressBarHiddenListener)
    }


    private fun loadAttributesFromXml(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsProgressBar,
                0, 0)
        try {
            val colorsArrayID = typedArray.getResourceId(R.styleable.MetaBallsProgressBar_colors_array_id, R.array.default_spinner_colors)
            colorArray = resources.getIntArray(colorsArrayID)
            mbProgressBarType = convertIntToSpinnertype(typedArray.getInteger(R.styleable.MetaBallsProgressBar_progressbar_type, MBProgressBarType.CIRCULAR.ordinal))
            isDropDrawable = typedArray.getBoolean(R.styleable.MetaBallsProgressBar_drop_drawable, isDropDrawable)
            isRotate = typedArray.getBoolean(R.styleable.MetaBallsProgressBar_rotate, false)

        } finally {
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (width != height && isRotate) {
            Log.w("SPINNER", "Warning: width and height are not equal. This may lead to unexpected results when rotation is enabled.")
        }
    }

    private fun setupBaseViews(context: Context) {
        spinnerImageView = ImageView(context)
        spinnerImageView.setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
        progressDrawable = createSpinnerDrawable()
        spinnerImageView.setImageDrawable(progressDrawable as Drawable)
        addView(spinnerImageView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    private fun createSpinnerDrawable(): ProgressDrawable {
        if (mbProgressBarType == MBProgressBarType.CIRCULAR) {
            return ProgressPathDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, parsePath(CIRCLE_PATH_DATA), isDropDrawable, isRotate)
        } else if (mbProgressBarType == MBProgressBarType.EIGHT) {
            return ProgressPathDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, parsePath(EIGHT_PATH_DATA), isDropDrawable, isRotate, 900, LinearInterpolator())
        } else if (mbProgressBarType == MBProgressBarType.BLOBS) {
            return ProgressBlobDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, isRotate)
        } else if (mbProgressBarType == MBProgressBarType.SQUARE) {
            return ProgressPathDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, parsePath(SQUARE_PATH_DATA), isDropDrawable, isRotate)
        } else if (mbProgressBarType == MBProgressBarType.LONG_PATH) {
            return ProgressPathDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, parsePath(LONG_PATH_DATA), isDropDrawable, isRotate, 1300, LinearInterpolator())
        }
        return ProgressJumpingDotDrawable(resources.getDrawable(R.drawable.gradient_oval, null), colorArray, isDropDrawable)
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
            progressDrawable?.startAnimations()
        } else {
            progressDrawable?.stopAllAnimations()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressDrawable?.stopAllAnimations()
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

    private fun convertIntToSpinnertype(id: Int): MBProgressBarType {
        for (f in MBProgressBarType.values()) {
            if (f.ordinal == id) return f
        }
        return MBProgressBarType.CIRCULAR
    }

    private fun rebuildDrawable() {
        progressDrawable?.stopAllAnimations()
        progressDrawable = createSpinnerDrawable()
        spinnerImageView.setImageDrawable(progressDrawable)
    }
}
