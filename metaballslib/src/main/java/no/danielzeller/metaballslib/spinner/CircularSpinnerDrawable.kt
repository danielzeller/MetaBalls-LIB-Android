package no.danielzeller.metaballslib.spinner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.PathInterpolator


class CircularSpinnerDrawable(val metaBall: Drawable, val tinColors: IntArray) : Drawable() {

    private val path = Path()
    private val renderables: ArrayList<Renderable> = ArrayList()
    private val animations: ArrayList<ValueAnimator> = ArrayList()
    private val aCoordinates = floatArrayOf(0f, 0f)
    lateinit var pathMeasure: PathMeasure
    private val pathOvalRect = RectF();
    private var circleScale = 0f
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null


    fun startAnimations() {
        ballSize = (bounds.width() * 0.19f).toInt()
        animateBallSize(0, ballSize, 300, null)
        for (i in 0 until 5) {
            renderables.add(Renderable(0f, 0f))
            animateDrawable(i)
        }
    }

    fun stopAllAnimations() {
        for (animation in animations)
            animation.cancel()
        animations.clear()
        sizeAnim?.cancel()
    }

    fun stopAndHide(spinner: View) {
        var animatedFractionMin = 100000f
        var lastRunningAnimIndex = 0
        for (i in 0 until animations.count()) {
            animations[i].addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    animations[i].cancel()
                }
            })
            if (animations[i].animatedFraction > animatedFractionMin) {
                animatedFractionMin = animations[i].animatedFraction
                lastRunningAnimIndex = i
            }
        }
        animations[lastRunningAnimIndex].addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                animateBallSize(ballSize, 0, 700, spinner)
            }
        })

    }

    fun animateBallSize(from: Int, to: Int, duration: Long, spinner: View?) {
        sizeAnim?.cancel()
        sizeAnim = ValueAnimator.ofInt(from, to).setDuration(duration)
        sizeAnim?.interpolator = PathInterpolator(.88f, 0f, .15f, 1f)
        sizeAnim?.addUpdateListener { animation ->
            ballSize = animation.animatedValue as Int
            metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
        }
        if (spinner != null)
            sizeAnim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    spinner.visibility = View.GONE
                }
            })
        sizeAnim?.start()
    }

    fun animateDrawable(i: Int) {
        val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(1800)
        anim.startDelay = (130 * i).toLong()
        anim.interpolator = PathInterpolator(.65f, .14f, .17f, 1f)
        anim.repeatCount = Animation.INFINITE
        anim.addUpdateListener { animation ->
            renderables[i].pathPercent = animation.animatedValue as Float
        }
        animations.add(anim)
        anim.start()
    }

    override fun draw(canvas: Canvas) {
        canvas.rotate(-90f, bounds.width().toFloat() / 2f, bounds.height().toFloat() / 2f)
        for (i in 0 until 5) {
            val renderable = renderables.get(i)
            pathMeasure.getPosTan(pathMeasure.length * renderable.pathPercent, aCoordinates, null)
            val count = canvas.save()
            canvas.translate(aCoordinates[0], aCoordinates[1])
            metaBall.setTint(tinColors[i])
            metaBall.draw(canvas)
            canvas.restoreToCount(count)
        }
        invalidateSelf()
    }


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        ballSize = (bounds.width() * 0.19f).toInt()
        circleScale = bounds.width() - (ballSize * 1.5f)
        createPath()
        startAnimations()
    }

    fun createPath() {
        pathOvalRect.set(bounds.centerX() - circleScale / 2f, bounds.centerY() - circleScale / 2f, bounds.centerX() + circleScale / 2f, bounds.centerY() + circleScale / 2f)
        path.reset()
        path.addOval(pathOvalRect, Path.Direction.CW)
        pathMeasure = PathMeasure(path, false)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }
}

class Renderable(var pathPercent: Float, var scale: Float)