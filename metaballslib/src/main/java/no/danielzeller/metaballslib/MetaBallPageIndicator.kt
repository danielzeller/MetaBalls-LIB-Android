package no.danielzeller.metaballslib

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import no.danielzeller.metaballslib.progressbar.drawables.DropDrawable

class MetaBallPageIndicator : FrameLayout, ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    /**
     *
     * Size of the dots. Note: Since the MetaBall ColorMatric cuts off the transparent edges of
     * the drawable, the size will appear smaller than it actually is.
     */
    var dotSize = 0
        set(value) {
            field = value
            pageIndicatorDrawable?.calculateCoordinates()
        }


    /**
     *
     * Margin beteween dots
     */
    var dotsMargin = 0f
        set(value) {
            field = value
            pageIndicatorDrawable?.calculateCoordinates()
        }


    /**
     *
     * Default color of the unselected page indicator dots. If dotColorsOverrideArray is set,
     * those colors will be usen instead.
     */
    var unSelectedDotColor = 0
        set(value) {
            field = value
            pageIndicatorDrawable?.invalidateSelf()
        }


    /**
     *
     * Color of the selected page indicator.
     */
    var selectedDotColor = 0
        set(value) {
            field = value
            pageIndicatorDrawable?.invalidateSelf()
        }

    /**
     *
     * Can be used to in order have different colors for each dot.
     */
    var dotColorsOverrideArray: IntArray? = null
        set(value) {
            field = value
            pageIndicatorDrawable?.invalidateSelf()
        }


    /**
     *
     * If true the selected page indicator dot will be rendered as a water drop(with a tail).
     */
    var isDropDrawable: Boolean = true
        set(value) {
            field = value
            pageIndicatorDrawable?.isFirstRender = true
        }


    private var viewPager: ViewPager? = null
    private var currentPageIndex: Int = 0
    private var positionOffset: Float = 0f
    private var pageIndicatorDrawable: PageIndicatorDrawable? = null
    private lateinit var pageIndicatorRenderView: ImageView


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadAttributesFromXML(attrs)
        setupView(context)
    }

    /**
     *
     * Connect the View to the ViewPager
     */
    fun attachToViewPager(viewPager: ViewPager) {
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(this)
        viewPager.addOnAdapterChangeListener(this)
    }

    /**
     *
     * Detach from the ViewPager
     */
    fun detachFromViewPager(viewPager: ViewPager) {
        viewPager.removeOnPageChangeListener(this)
        viewPager.removeOnAdapterChangeListener(this)
        this.viewPager = null
    }

    override fun onAdapterChanged(p0: ViewPager, p1: PagerAdapter?, p2: PagerAdapter?) {
        pageIndicatorDrawable?.calculateCoordinates()
    }

    override fun onPageScrollStateChanged(scrollState: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.currentPageIndex = position
        this.positionOffset = positionOffset
        pageIndicatorDrawable?.invalidateSelf()
    }

    override fun onPageSelected(position: Int) {}

    private fun loadAttributesFromXML(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsPageIndicator,
                0, 0)
        try {
            val colorsArrayID = typedArray.getResourceId(R.styleable.MetaBallsPageIndicator_dot_colors_override_array_id, -1)
            if (colorsArrayID != -1) {
                dotColorsOverrideArray = resources.getIntArray(colorsArrayID)
            }
            selectedDotColor = typedArray.getColor(R.styleable.MetaBallsPageIndicator_selected_dot_color, Color.WHITE)
            unSelectedDotColor = typedArray.getColor(R.styleable.MetaBallsPageIndicator_un_selected_dot_color, Color.WHITE)
            dotSize = typedArray.getDimension(R.styleable.MetaBallsPageIndicator_dot_size, resources.getDimension(R.dimen.default_page_indicator_dot_size)).toInt()
            dotsMargin = typedArray.getDimension(R.styleable.MetaBallsPageIndicator_dot_margin, resources.getDimension(R.dimen.default_page_indicator_dot_margin))
            isDropDrawable = typedArray.getBoolean(R.styleable.MetaBallsPageIndicator_selected_indicator_is_drop_drawable, true)

        } finally {
            typedArray.recycle()
        }
    }

    private fun setupView(context: Context) {

        pageIndicatorRenderView = ImageView(context)
        pageIndicatorRenderView.setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
        addView(pageIndicatorRenderView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val gradientDrawable = resources.getDrawable(R.mipmap.gradient_oval, null)
        val dropDrawable = DropDrawable(gradientDrawable, true)
        pageIndicatorDrawable = PageIndicatorDrawable(gradientDrawable, dropDrawable)
        pageIndicatorRenderView.setImageDrawable(pageIndicatorDrawable)
    }

    private fun createMetaBallsPaint(): Paint {
        val metaBallsPaint = Paint()
        metaBallsPaint.setColorFilter(ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 160f, -255 * 128f
        ))))
        return metaBallsPaint
    }

    inner class PageIndicatorDrawable(val gradientDrawable: Drawable, val dropDrawable: DropDrawable) : Drawable() {

        var isFirstRender = true
        private var dotPositions = FloatArray(0)
        private var centerY = 0f
        private var combinedDotsWidth = 0f
        private var dotsCount = 0
        private var startXCood = 0f
        private var distanceBetweenDots = 0f
        private val DROP_SCALE = 0.79f

        init {
            gradientDrawable.setBounds(-dotSize, -dotSize, dotSize, dotSize)
        }

        internal fun calculateCoordinates() {
            centerY = bounds.height() / 2f

            if (dotsCount != null && viewPager?.adapter != null) {

                dotsCount = viewPager?.adapter?.getCount()!!
                combinedDotsWidth = getTotalWidth(dotsCount)
                startXCood = bounds.width() / 2f - combinedDotsWidth / 2f + dotSize / 2f

                dotPositions = FloatArray(dotsCount)
                for (i in 0 until dotsCount) {
                    dotPositions[i] = startXCood + dotsMargin * i + dotSize * i
                }

                distanceBetweenDots = dotsMargin + dotSize
                invalidateSelf()
            }
        }

        override fun draw(canvas: Canvas) {
            val selectedDotXCoord = dotPositions[currentPageIndex] + positionOffset * distanceBetweenDots
            if (dotsCount != 0) {
                for (i in 0 until dotsCount) {
                    drawDotScaled(canvas, dotPositions[i], getUnselectedColor(i), selectedDotXCoord)
                }
                if (isDropDrawable) {
                    drawDrop(selectedDotXCoord, canvas)
                } else {
                    drawDot(canvas, selectedDotXCoord, selectedDotColor)
                }
            }
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            calculateCoordinates()
            dropDrawable.setBounds(bounds)
            dropDrawable.ballSize = (dotSize * DROP_SCALE).toInt()
        }

        override fun setAlpha(alpha: Int) {
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
        }

        private fun drawDrop(selectedDotXCoord: Float, canvas: Canvas) {
            dropDrawable.setTint(selectedDotColor)
            dropDrawable.x = selectedDotXCoord
            dropDrawable.y = centerY
            if (isFirstRender) {
                dropDrawable.setAllCoordinatesToStart()
                isFirstRender = false
            }
            dropDrawable.draw(canvas)
            gradientDrawable.setBounds(-dotSize, -dotSize, dotSize, dotSize)
        }

        private fun getUnselectedColor(index: Int): Int {
            if (dotColorsOverrideArray != null && index < dotColorsOverrideArray!!.size) {
                return dotColorsOverrideArray!![index]
            }
            return unSelectedDotColor
        }


        /**
         *
         * Since the selected dot will appear larger than the others when it's drawn on top of
         * another dot, we scale down the dot behind the selected dot so that the size is correct.
         */
        private fun drawDotScaled(canvas: Canvas, xPosition: Float, color: Int, selectedDotXCoord: Float) {
            val distanceToSelectedIndicator = Math.abs(xPosition - selectedDotXCoord)

            val halfDotSize = dotSize / 2f
            val quarterDotSize = halfDotSize / 2f
            val threeQuarters = halfDotSize + quarterDotSize
            if (distanceToSelectedIndicator < threeQuarters) {
                val distancePercent = (distanceToSelectedIndicator / threeQuarters)

                val currentScale = (quarterDotSize + distancePercent * (quarterDotSize + halfDotSize)).toInt()
                gradientDrawable.setBounds(-currentScale, -currentScale, currentScale, currentScale)
            }
            drawDot(canvas, xPosition, color)
            gradientDrawable.setBounds(-dotSize, -dotSize, dotSize, dotSize)
        }

        private fun drawDot(canvas: Canvas, xPosition: Float, color: Int) {
            val canvasCount = canvas.save()
            canvas.translate(xPosition, centerY)
            gradientDrawable.setTint(color)
            gradientDrawable.draw(canvas)
            canvas.restoreToCount(canvasCount)
        }


        private fun getTotalWidth(count: Int): Float {
            return (dotSize * count + (count - 1) * dotsMargin).toFloat()
        }
    }
}

