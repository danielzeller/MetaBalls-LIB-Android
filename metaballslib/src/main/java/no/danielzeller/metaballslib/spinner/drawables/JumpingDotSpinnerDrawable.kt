package no.danielzeller.metaballslib.spinner.drawables

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.PathInterpolator


class JumpingDotSpinnerDrawable(val metaBall: Drawable, val tinColors: IntArray) : Drawable(), SpinnerDrawable {

    private val path = Path()
    private val animations: ArrayList<ValueAnimator> = ArrayList()
    private lateinit var pathMeasure: PathMeasure
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null
    private var dropDrawable: DropDrawable
    private val aCoordinates = floatArrayOf(0f, 0f)
    private val BALLSIZE = 0.23f

    init {
        dropDrawable = DropDrawable(metaBall)
        dropDrawable.easeSpeed = 15f
        dropDrawable.easeSpeedLast = 11f
    }

    override fun startAnimations() {
        ballSize = (bounds.width() *BALLSIZE).toInt()
        stopAllAnimations()
        animateBallSize(0, ballSize, 300, null)
        jumpinAnimationGroup()
    }

    fun jumpinAnimationGroup() {
        animateJumping(0, 600, 0f, 0.5f)
        animateJumping(600, 600, 0.5f, 1f)
        animateJumping(1200, 600, 1f, 0.5f)
        animateJumping(1800, 600, 0.5f, 0f).addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                for (animation in animations)
                    animation.cancel()
                animations.clear()
                jumpinAnimationGroup()
            }
        })
    }

    private fun animateJumping(startDelay: Long, duration: Long, from: Float, to: Float): ValueAnimator {
        val anim = ValueAnimator.ofFloat(from, to).setDuration(duration)
        anim.startDelay = startDelay
        anim.interpolator = PathInterpolator(0f, .6f, 1f, .5f)
        anim.addUpdateListener { animation -> dropDrawable.pathPercent = animation.animatedValue as Float }
        anim.start()
        return anim
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
            metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
            dropDrawable.ballSize = (ballSize * 0.75f).toInt()
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

    }

    override fun draw(canvas: Canvas) {



        pathMeasure.getPosTan(pathMeasure.length * dropDrawable.pathPercent, aCoordinates, null)
        dropDrawable.setTint(tinColors[3])
        dropDrawable.x = aCoordinates[0]
        dropDrawable.y = aCoordinates[1]
        dropDrawable.draw(canvas)
        metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
        drawDot(startX, startY, 0, canvas)
        drawDot(centerX, startY, 1, canvas)
        drawDot(endX, startY, 2, canvas)

        invalidateSelf()
    }

    fun drawDot(xPos: Float, yPos: Float, tindIndex: Int, canvas: Canvas) {
        val count = canvas.save()
        canvas.translate(xPos, yPos)
        metaBall.setTint(tinColors[tindIndex])
        metaBall.draw(canvas)
        canvas.restoreToCount(count)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        ballSize = (bounds.width() * BALLSIZE).toInt()
        createPath()
        startAnimations()
    }

    var startX = 0f
    var startY = 0f
    var centerX = 0f
    var endX = 0f

    fun createPath() {
        path.reset()
        startY = (bounds.height() - ballSize).toFloat()
        val endY = bounds.width() * 0.5f
        startX = ballSize.toFloat()
        centerX = bounds.centerX().toFloat()
        endX = (bounds.width() - ballSize).toFloat()
        val halfWidthBetwenPoints = (centerX - startX) / 2f
        val halfHeightBetwenPoints = startY - ((startY - endY) / 2f)

        path.moveTo(startX, startY)
        path.cubicTo(startX, halfHeightBetwenPoints, startX + halfWidthBetwenPoints / 2f, endY, startX + halfWidthBetwenPoints, endY)
        path.cubicTo(centerX - halfWidthBetwenPoints / 2f, endY, centerX, halfHeightBetwenPoints, centerX, startY)
        path.cubicTo(centerX, halfHeightBetwenPoints, centerX + halfWidthBetwenPoints / 2f, endY, centerX + halfWidthBetwenPoints, endY)
        path.cubicTo(endX - halfWidthBetwenPoints / 2f, endY, endX, halfHeightBetwenPoints, endX, startY)

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
