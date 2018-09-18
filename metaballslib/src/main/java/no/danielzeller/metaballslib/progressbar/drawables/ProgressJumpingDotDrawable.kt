package no.danielzeller.metaballslib.progressbar.drawables

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.animation.PathInterpolator


class ProgressJumpingDotDrawable(val metaBall: Drawable, val tinColors: IntArray, val isDrop: Boolean) : ProgressDrawable() {


    private val path = Path()
    private val animations: ArrayList<ValueAnimator> = ArrayList()
    private lateinit var pathMeasure: PathMeasure
    private var ballSize = 0
    private var sizeAnim: ValueAnimator? = null
    private var dropDrawable: DropDrawable
    private val aCoordinates = floatArrayOf(0f, 0f)
    private val BALLSIZE = 0.23f
    private val JUMP_DURATION = 600L
    private var pathStartX = 0f
    private var pathStartY = 0f
    private var pathCenterX = 0f
    private var pathEndX = 0f

    init {
        this.isDropDrawable = isDrop
        dropDrawable = DropDrawable(metaBall, isDropDrawable)
        dropDrawable.easeSpeed = 15f
        dropDrawable.easeSpeedLast = 11f
        this.tinColorsArray=tinColors
    }

    override fun setDrop(isDrop: Boolean) {
        dropDrawable.isDropDrawable = isDrop
    }

    override fun startAnimations() {
        ballSize = (bounds.width() * BALLSIZE).toInt()
        stopAllAnimations()
        animateBallSize(0, ballSize, 300, null, null)
        startJumpingSequence()
    }

    fun startJumpingSequence() {
        animateJumping(0, JUMP_DURATION, 0f, 0.5f)
        animateJumping(JUMP_DURATION, JUMP_DURATION, 0.5f, 1f)
        animateJumping(JUMP_DURATION * 2, JUMP_DURATION, 1f, 0.5f)
        animateJumping(JUMP_DURATION * 3, JUMP_DURATION, 0.5f, 0f).addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                stopJumpingAnimations()
                startJumpingSequence()
            }
        })
    }

    private fun animateJumping(startDelay: Long, duration: Long, from: Float, to: Float): ValueAnimator {
        val anim = ValueAnimator.ofFloat(from, to).setDuration(duration)
        anim.startDelay = startDelay
        anim.interpolator = PathInterpolator(0f, .6f, 1f, .5f)
        anim.addUpdateListener { animation -> dropDrawable.pathPercent = animation.animatedValue as Float }
        animations.add(anim)
        anim.start()
        return anim
    }

    override fun stopAllAnimations() {
        stopJumpingAnimations()
        sizeAnim?.cancel()
    }

    fun stopJumpingAnimations() {
        for (animation in animations) {
            animation.removeAllListeners()
            animation.cancel()
        }
        animations.clear()
    }

    override fun stopAndHide(spinner: View, spinnerHiddenListener: (() -> Unit)?) {
        animateBallSize(ballSize, 0, 700, spinner, spinnerHiddenListener)
    }

    fun animateBallSize(from: Int, to: Int, duration: Long, spinner: View?, spinnerHiddenListener: (() -> Unit)?) {
        sizeAnim?.cancel()
        sizeAnim = ValueAnimator.ofInt(from, to).setDuration(duration)
        sizeAnim?.interpolator = PathInterpolator(.88f, 0f, .15f, 1f)
        sizeAnim?.addUpdateListener { animation ->
            ballSize = animation.animatedValue as Int
            metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
            dropDrawable.ballSize = (ballSize * getDropScale()).toInt()
        }
        if (spinner != null)
            sizeAnim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    spinner.visibility = View.GONE
                    spinnerHiddenListener?.invoke()
                }
            })
        sizeAnim?.start()
    }

    fun getDropScale(): Float {
        if (isDropDrawable) {
            return 0.75f
        }
        return 1f
    }

    override fun draw(canvas: Canvas) {

        pathMeasure.getPosTan(pathMeasure.length * dropDrawable.pathPercent, aCoordinates, null)
        dropDrawable.setTint(tinColors[3])
        dropDrawable.x = aCoordinates[0]
        dropDrawable.y = aCoordinates[1]
        dropDrawable.draw(canvas)
        metaBall.setBounds(-ballSize, -ballSize, ballSize, ballSize)
        drawDot(pathStartX, pathStartY, 0, canvas)
        drawDot(pathCenterX, pathStartY, 1, canvas)
        drawDot(pathEndX, pathStartY, 2, canvas)

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

    fun createPath() {
        path.reset()
        pathStartY = (bounds.height() - ballSize).toFloat()

        pathStartX = ballSize.toFloat()
        pathCenterX = bounds.centerX().toFloat()
        pathEndX = (bounds.width() - ballSize).toFloat()
        val endY = pathStartY - (pathCenterX - pathStartX)
        val halfWidthBetwenPoints = (pathCenterX - pathStartX) / 2f
        val halfHeightBetwenPoints = pathStartY - ((pathStartY - endY) / 2f)

        path.moveTo(pathStartX, pathStartY)
        path.cubicTo(pathStartX, halfHeightBetwenPoints, pathStartX + halfWidthBetwenPoints / 2f, endY, pathStartX + halfWidthBetwenPoints, endY)
        path.cubicTo(pathCenterX - halfWidthBetwenPoints / 2f, endY, pathCenterX, halfHeightBetwenPoints, pathCenterX, pathStartY)
        path.cubicTo(pathCenterX, halfHeightBetwenPoints, pathCenterX + halfWidthBetwenPoints / 2f, endY, pathCenterX + halfWidthBetwenPoints, endY)
        path.cubicTo(pathEndX - halfWidthBetwenPoints / 2f, endY, pathEndX, halfHeightBetwenPoints, pathEndX, pathStartY)

        pathMeasure = PathMeasure(path, false)
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}
