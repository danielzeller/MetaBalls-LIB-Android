package no.danielzeller.metaballslib.spinner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.PathInterpolator


class CircularSpinnerDrawableV2(val metaBallGradient: Drawable, val tinColors: IntArray) : Drawable(), SpinnerDrawable {

    private val path = Path()
    private val dropDrawables: ArrayList<DropDrawable> = ArrayList()
    private val animations: ArrayList<ValueAnimator> = ArrayList()
    private val aCoordinates = floatArrayOf(0f, 0f)
    lateinit var pathMeasure: PathMeasure
    private val pathOvalRect = RectF();
    private var circleScale = 0f
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null
    private var rotation = 0f
    private var framerate = FrameRateCounter()
    private val ROTATE_SPEED = 40f
    private val BALL_SIZE = 0.21f

    override fun startAnimations() {
        framerate.timeStep()
        ballSize = (bounds.width() * BALL_SIZE).toInt()
        animateBallSize(0, ballSize, 300, null)
        for (i in 0 until 5) {
            dropDrawables.add(DropDrawable(metaBallGradient))
            animateDrawable(i)
        }
    }

    override fun stopAllAnimations() {
        for (animation in animations)
            animation.cancel()
        animations.clear()
        sizeAnim?.cancel()
    }

    override fun stopAndHide(spinner: View) {
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
            for (dropDrawable in dropDrawables) {
                dropDrawable.ballSize = ballSize
            }
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
        anim.startDelay = (170 * i).toLong()
        anim.interpolator = PathInterpolator(.65f, .14f, .17f, 1f)
        anim.repeatCount = Animation.INFINITE
        anim.addUpdateListener { animation ->
            dropDrawables[i].pathPercent = animation.animatedValue as Float
        }
        animations.add(anim)
        anim.start()
    }

    override fun draw(canvas: Canvas) {
        canvas.rotate(rotation, bounds.width().toFloat() / 2f, bounds.height().toFloat() / 2f)
        rotation += ROTATE_SPEED * framerate.timeStep()

        for (i in 0 until 5) {
            val dropDrawable = dropDrawables.get(i)
            pathMeasure.getPosTan(pathMeasure.length * dropDrawable.pathPercent, aCoordinates, null)
            dropDrawable.setTint(tinColors[i])
            dropDrawable.x = aCoordinates[0]
            dropDrawable.y = aCoordinates[1]
            dropDrawable.draw(canvas)
        }
        invalidateSelf()
    }


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        for (dropDrawable in dropDrawables) {
            dropDrawable.bounds = bounds
        }
        ballSize = (bounds.width() * BALL_SIZE).toInt()
        circleScale = bounds.width() - (ballSize * 1.2f)
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
