package no.danielzeller.metaballslib.progressbar.drawables

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.PathInterpolator
import no.danielzeller.metaballslib.progressbar.BrownianMotion
import no.danielzeller.metaballslib.progressbar.FrameRateCounter
import no.danielzeller.metaballslib.progressbar.Vector3


class ProgressBlobDrawable(val metaBall: Drawable, val tinColors: IntArray, val isRotate: Boolean) : ProgressDrawable() {

    private val motion: ArrayList<BrownianMotion> = ArrayList()
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null
    private var rotation = 0f
    private var frameRate = FrameRateCounter()
    private val ROTATE_SPEED = 40f

    init {
        this.rotate = isRotate
        this.tinColorsArray = tinColors
    }

    override fun startAnimations() {
        stopAllAnimations()
        ballSize = (bounds.width() * 0.25f).toInt()
        animateBallSize(0, ballSize, 300, null, null)
        val boundsSizeX = bounds.width().toFloat()
        val boundsSizeY = bounds.height().toFloat()
        motion.clear()
        for (i in 0 until 5) {
            val brownianMotion = BrownianMotion(Vector3(boundsSizeX * 0.8f, boundsSizeY * 0.8f, 1000f))
            brownianMotion.positionFrequency = 0.2f
            motion.add(brownianMotion)
        }
    }

    override fun stopAllAnimations() {
        sizeAnim?.cancel()
    }

    override fun stopAndHide(progressBar: View, progressBarHiddenListener: (() -> Unit)?) {
        animateBallSize(ballSize, 0, 700, progressBar, progressBarHiddenListener)
    }

    private fun animateBallSize(from: Int, to: Int, duration: Long, progressBar: View?, progressBarHiddenListener: (() -> Unit)?) {
        sizeAnim?.cancel()
        sizeAnim = ValueAnimator.ofInt(from, to).setDuration(duration)
        sizeAnim?.interpolator = PathInterpolator(.88f, 0f, .15f, 1f)
        sizeAnim?.addUpdateListener { animation ->
            ballSize = animation.animatedValue as Int
            metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
        }
        if (progressBar != null) {
            sizeAnim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    progressBar.visibility = View.INVISIBLE
                    progressBarHiddenListener?.invoke()
                }
            })
        }
        sizeAnim?.start()
    }


    override fun draw(canvas: Canvas) {
        if (rotate) {
            canvas.rotate(rotation, bounds.width().toFloat() / 2f, bounds.height().toFloat() / 2f)
            rotation += ROTATE_SPEED * frameRate.timeStep()
        }
        canvas.translate((bounds.width() / 2).toFloat(), (bounds.height() / 2).toFloat())
        for (i in 0 until 5) {
            val count = canvas.save()
            val brownianMotion = motion[i]
            brownianMotion.update()
            canvas.translate(brownianMotion.position.x, brownianMotion.position.y)
            metaBall.setTint(tinColors[i])
            metaBall.draw(canvas)
            canvas.restoreToCount(count)
        }
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        startAnimations()
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun setDrop(isDrop: Boolean) {}
}
