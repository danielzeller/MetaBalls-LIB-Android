package no.danielzeller.metaballslib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.PathInterpolator

class OpenCloseDrawable(val iconDawable: Drawable?, val iconColor: Int, context: Context) : Drawable() {
    private val paint = Paint()
    private val closeLineRectF = RectF()
    private val iconBounds = Rect()
    private val closeLineHeight: Float
    private var closeLineRotation = 45f
    private lateinit var openCloseAnim: ValueAnimator

    private val xExpandInterpolator = PathInterpolator(.66f,.01f,1f,.7f)
    private val xRotateInterpolator = PathInterpolator(0f,.53f,.4f,1f)
    private val iconScaleInterpolator = PathInterpolator(0f,.67f,.17f,1f)

    private var closeLineWidth = 0f
    private var iconScale = 1f
    private var openProgressPercent = 0f

    init {
        paint.color = iconColor
        closeLineHeight = context.resources.getDimension(R.dimen.close_line_stroke)
        iconDawable?.setTint(iconColor)
    }

    fun closeState() {
        animateOpenPercent(1f)
    }

    fun openState() {
        animateOpenPercent(0f)
    }

    private fun animateOpenPercent(toOpenPercent: Float) {
        if (::openCloseAnim.isInitialized) {
            openCloseAnim.cancel()
        }
        openCloseAnim = ValueAnimator.ofFloat(openProgressPercent, toOpenPercent).setDuration(600)
        openCloseAnim.addUpdateListener { animation ->
            openProgressPercent = animation.animatedValue as Float
            seekAnimation()
            invalidateSelf()
        }
        openCloseAnim.start()
    }

    private fun seekAnimation() {
        var xProgress = 0f
        var iconProgress = 0f
        var rotateProgress = 0f
        if (openProgressPercent > 0.5f) {
            rotateProgress = ((openProgressPercent - 0.5f) * 2f)
            xProgress = 1f
        } else if (openProgressPercent > 0.33f) {
            xProgress = ((openProgressPercent - 0.33f) * 6f)
        } else {
            iconProgress = 1f - (openProgressPercent * 3f)
        }
        closeLineWidth = xExpandInterpolator.getInterpolation(xProgress)*0.8f
        closeLineRotation = 45 * xRotateInterpolator.getInterpolation(rotateProgress)
        iconScale = iconScaleInterpolator.getInterpolation(iconProgress)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        iconDawable?.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT;
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.setColorFilter(colorFilter)
        iconDawable?.colorFilter = colorFilter
    }

    override fun draw(canvas: Canvas) {
        drawXLine(canvas, closeLineRotation)
        drawXLine(canvas, -closeLineRotation)
        iconDawable?.bounds = getIconBounds()
        iconDawable?.draw(canvas)
    }

    private fun getIconBounds(): Rect {
        val centerX = bounds.width().toFloat() / 2f
        val centerY = bounds.width().toFloat() / 2f
        iconBounds.set((centerX - centerX * iconScale).toInt(), (centerY - centerY * iconScale).toInt(), (centerX + centerX * iconScale).toInt(), (centerY + centerY * iconScale).toInt())
        return iconBounds
    }

    private fun drawXLine(canvas: Canvas, rotation: Float) {
        val centerX = bounds.width().toFloat() / 2f
        val centerY = bounds.width().toFloat() / 2f
        canvas.save()
        canvas.rotate(rotation, centerX, centerY)
        canvas.drawRoundRect(getClosLineRect(), closeLineHeight, closeLineHeight, paint)
        canvas.restore()
    }

    private fun getClosLineRect(): RectF {
        val centerX = bounds.width().toFloat() / 2f
        val centerY = bounds.width().toFloat() / 2f
        closeLineRectF.set(centerX - centerX * closeLineWidth, centerY - closeLineHeight / 2f, centerX + centerX * closeLineWidth, centerY + closeLineHeight / 2f)
        return closeLineRectF
    }
}
