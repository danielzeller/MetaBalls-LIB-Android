package no.danielzeller.metaballslib.progressbar.drawables

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import no.danielzeller.metaballslib.progressbar.FrameRateCounter
import no.danielzeller.metaballslib.progressbar.SpinneHiddenListener


class ProgressPathDrawable(val metaBallGradient: Drawable, val tinColors: IntArray, val shapePath: Path, val isDrop: Boolean, isRotate: Boolean, val animationDuration: Long = 1800L, val interpolator: Interpolator = PathInterpolator(.65f, .14f, .17f, 1f)) : ProgressDrawable() {


    private var path = Path()
    private val dropDrawables: ArrayList<DropDrawable> = ArrayList()
    private val animations: ArrayList<ValueAnimator> = ArrayList()
    private val aCoordinates = floatArrayOf(0f, 0f)
    lateinit var pathMeasure: PathMeasure
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null
    private var rotation = 0f
    private var framerate = FrameRateCounter()
    private val ROTATE_SPEED = 40f
    private val BALL_SIZE = 0.21f

    init {
        this.rotate = isRotate
        this.isDropDrawable = isDrop
    }

    override fun setDrop(isDrop: Boolean) {
        for (dropDrawable in dropDrawables) {
            dropDrawable.isDropDrawable = isDrop
        }
        this.isDropDrawable = isDrop
    }

    override fun startAnimations() {
        stopAllAnimations()
        framerate.timeStep()
        ballSize = (bounds.width() * BALL_SIZE).toInt()
        animateBallSize(0, ballSize, 300, null, null)
        dropDrawables.clear()
        for (i in 0 until 5) {
            dropDrawables.add(DropDrawable(metaBallGradient, isDropDrawable))
            animateDrawable(i)
        }
    }

    override fun stopAllAnimations() {
        for (animation in animations) {
            animation.removeAllListeners()
            animation.cancel()
        }
        animations.clear()
        sizeAnim?.cancel()
    }

    override fun stopAndHide(spinner: View, spinnerHiddenListener: SpinneHiddenListener?) {
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
                animateBallSize(ballSize, 0, 700, spinner, spinnerHiddenListener)
            }
        })
    }

    fun animateBallSize(from: Int, to: Int, duration: Long, spinner: View?, spinnerHiddenListener: SpinneHiddenListener?) {
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
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    spinner.visibility = View.GONE
                    spinnerHiddenListener?.onSpinnHidden(spinner)
                }
            })
        sizeAnim?.start()
    }

    fun animateDrawable(i: Int) {
        val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(animationDuration)
        anim.startDelay = (getStartDelay() * i).toLong()
        anim.interpolator = interpolator
        anim.repeatCount = Animation.INFINITE
        anim.addUpdateListener { animation ->
            dropDrawables[i].pathPercent = animation.animatedValue as Float
        }
        animations.add(anim)
        anim.start()
    }

    fun getStartDelay(): Long {
        if (isDropDrawable)
            return 170
        else
            return 130
    }

    override fun draw(canvas: Canvas) {
        if (rotate) {
            canvas.rotate(rotation, bounds.width().toFloat() / 2f, bounds.height().toFloat() / 2f)
            rotation += ROTATE_SPEED * framerate.timeStep()
        }
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
        createPath()
        startAnimations()
    }

    fun createPath() {
        val scale = bounds.width() / 100f
        val scaleY = bounds.height() / 100f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(scale, scaleY)
        shapePath.transform(scaleMatrix, path)
        pathMeasure = PathMeasure(path, false)
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}
