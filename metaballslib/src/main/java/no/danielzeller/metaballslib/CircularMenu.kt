package no.danielzeller.metaballslib

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView


class CircularMenu : FrameLayout {

    var adapter: MetaBallAdapter = EmptyAdapter()
        set(value) {
            field = value
            addMenuItems(metaBallsContainerFrameLayout)
        }

    enum class PositionGravity {
        CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    var delayBetweenItemsAnimation = 40L
    var openAnimationDuration = 600L
    var closeAnimationDuration = 600L
    var radius = 0
    var angleBetweenMenuItems = 45f
    var mainButtonColor = Color.BLACK;
    var mainButtonIconColor = Color.WHITE;
    var mainButtonIcon: Drawable? = null
    lateinit var openInterpolatorAnimator: Interpolator
    lateinit var closeInterpolatorAnimator: Interpolator
    var positionGravity = PositionGravity.CENTER

    private lateinit var metaBallsContainerFrameLayout: FrameLayout
    private var runningAnimations: ArrayList<ValueAnimator> = ArrayList()
    private var isMenuOpen = false
    private lateinit var openCloseDrawable: OpenCloseDrawable
    var onItemSelectedListener: ((index: Int) -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupBaseViews(context)
        loadAttributesFromXML(attrs)
    }

    fun toggleMenu() {
        if (isMenuOpen) {
            closeMenu()
            openCloseDrawable.openState()
        } else {
            openMenu()
            openCloseDrawable.closeState()
        }

        isMenuOpen = !isMenuOpen
    }

    fun isMenuOpen(): Boolean {
        return isMenuOpen
    }

    private fun loadAttributesFromXML(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsMenu,
                0, 0)
        try {
            delayBetweenItemsAnimation = typedArray.getInteger(R.styleable.MetaBallsMenu_delay_between_items_animation, 40).toLong()
            openAnimationDuration = typedArray.getInteger(R.styleable.MetaBallsMenu_open_animation_duration, 600).toLong()
            closeAnimationDuration = typedArray.getInteger(R.styleable.MetaBallsMenu_close_animation_duration, 600).toLong()
            radius = typedArray.getLayoutDimension(R.styleable.MetaBallsMenu_radius, resources.getDimension(R.dimen.default_radius).toInt())
            openInterpolatorAnimator = AnimationUtils.loadInterpolator(getContext(), typedArray.getResourceId(R.styleable.MetaBallsMenu_open_interpolator_resource, R.anim.default_menu_interpolator)) as Interpolator
            closeInterpolatorAnimator = AnimationUtils.loadInterpolator(getContext(), typedArray.getResourceId(R.styleable.MetaBallsMenu_close_interpolator_resource, R.anim.default_menu_interpolator)) as Interpolator
            angleBetweenMenuItems = typedArray.getFloat(R.styleable.MetaBallsMenu_angle_between_menu_items, 45f)
            mainButtonColor = typedArray.getColor(R.styleable.MetaBallsMenu_main_button_color, Color.BLACK)
            mainButtonIconColor = typedArray.getColor(R.styleable.MetaBallsMenu_main_button_icon_color, Color.WHITE)
            if (typedArray.hasValue(R.styleable.MetaBallsMenu_main_button_icon)) {
                mainButtonIcon = resources.getDrawable(typedArray.getResourceId(R.styleable.MetaBallsMenu_main_button_icon, 0), null)
            }
            positionGravity = convertIntToPositionGravity(typedArray.getInteger(R.styleable.MetaBallsMenu_position_grvity, PositionGravity.CENTER.ordinal))
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupBaseViews(context: Context) {
        metaBallsContainerFrameLayout = FrameLayout(context)
        addView(metaBallsContainerFrameLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
    }

    private fun createMetaBallsPaint(): Paint {
        val metaBallsPaint = Paint()
        metaBallsPaint.setColorFilter(ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 200f, -255 * 128f
        ))))
        return metaBallsPaint
    }

    private fun addMenuItems(frameLayout: FrameLayout) {
        frameLayout.removeAllViews()

        val menuItemsLayoutSize = resources.getDimension(R.dimen.menu_item_size).toInt()
        val mainButtonLayoutSize = resources.getDimension(R.dimen.main_button_size).toInt()
        val menuItemMargins = (mainButtonLayoutSize - menuItemsLayoutSize) / 2
        val marginsToCompensateCutoff = resources.getDimension(R.dimen.margins_to_compensate_cutoff).toInt()
        val layoutGravity = getLayoutGravity()

        val menuItemLayoutParams = FrameLayout.LayoutParams(menuItemsLayoutSize, menuItemsLayoutSize)
        menuItemLayoutParams.setMargins(marginsToCompensateCutoff + menuItemMargins, marginsToCompensateCutoff + menuItemMargins, marginsToCompensateCutoff + menuItemMargins, marginsToCompensateCutoff + menuItemMargins)
        menuItemLayoutParams.gravity = layoutGravity

        val mainButtonLayoutParams = FrameLayout.LayoutParams(mainButtonLayoutSize, mainButtonLayoutSize)
        mainButtonLayoutParams.setMargins(marginsToCompensateCutoff, marginsToCompensateCutoff, marginsToCompensateCutoff, marginsToCompensateCutoff)
        mainButtonLayoutParams.gravity = layoutGravity

        for (i in 0 until adapter.itemsCount()) {
            frameLayout.addView(createMenuItem(i), menuItemLayoutParams)
        }
        frameLayout.addView(createMainButton(), mainButtonLayoutParams)
    }

    private fun createMainButton(): ImageView {
        val menuButton = DecreasedTouchImageView(context, resources.getDimension(R.dimen.main_button_touch_area_size))
        menuButton.setBackgroundResource(R.mipmap.gradient_oval)
        menuButton.background.setTint(mainButtonColor)
        openCloseDrawable = OpenCloseDrawable(mainButtonIcon?.mutate(), mainButtonIconColor, context)
        menuButton.setImageDrawable(openCloseDrawable)
        val padding = resources.getDimension(R.dimen.main_button_padding).toInt()
        menuButton.setPadding(padding, padding, padding, padding)
        menuButton.setOnClickListener({ toggleMenu() })
        menuButton.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.button_scale)
        return menuButton
    }

    private fun createMenuItem(i: Int): ImageView {
        val imageView = DecreasedTouchImageView(context, resources.getDimension(R.dimen.menu_item_touch_area_size))
        imageView.setBackgroundResource(R.mipmap.gradient_oval)
        imageView.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.button_scale)
        imageView.background.setTint(adapter.menuItemBackgroundColor(i))
        imageView.setImageDrawable(adapter.menuItemIcon(i))
        imageView.setColorFilter(adapter.menuItemIconTint(i))
        val padding = resources.getDimension(R.dimen.menu_item_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        imageView.setOnClickListener { onItemSelectedListener?.invoke(i) }
        imageView.isEnabled = false
        return imageView
    }

    private fun getLayoutGravity(): Int {
        if (positionGravity == PositionGravity.BOTTOM_LEFT)
            return Gravity.BOTTOM or Gravity.LEFT
        else if (positionGravity == PositionGravity.BOTTOM_RIGHT)
            return Gravity.BOTTOM or Gravity.RIGHT
        else if (positionGravity == PositionGravity.TOP_RIGHT)
            return Gravity.TOP or Gravity.RIGHT
        else if (positionGravity == PositionGravity.TOP_LEFT)
            return Gravity.TOP or Gravity.LEFT
        return Gravity.CENTER
    }

    private fun startAnimation(animation: ValueAnimator) {
        animation.start()
        runningAnimations.add(animation)
    }

    private fun stopAllRunningAnimations() {
        for (anim in runningAnimations) {
            anim.cancel()
        }
        runningAnimations.clear()
    }

    private fun openMenu() {
        stopAllRunningAnimations()

        val startAngle = getStartAngle()
        var startDelay = 0L;

        for (i in 0 until metaBallsContainerFrameLayout.getChildCount() - 1) {
            var angleDeg = startAngle + i.toFloat() * angleBetweenMenuItems
            val angleRad = (angleDeg * Math.PI / 180f).toFloat()
            val x = radius * Math.cos(angleRad.toDouble()).toFloat()
            val y = radius * Math.sin(angleRad.toDouble()).toFloat()
            val ballView = metaBallsContainerFrameLayout.getChildAt(i)
            animatePosition(ballView, x, y, startDelay, openInterpolatorAnimator, openAnimationDuration)
            val menuItemScaleUpDuration = (openAnimationDuration * 0.125f).toLong()
            val menuItemFadeDuration = (openAnimationDuration * 0.33f).toLong()
            animateScale(ballView, 1.0f, menuItemScaleUpDuration, startDelay)
            fadeIcon((ballView as ImageView).drawable, startDelay + menuItemFadeDuration, menuItemFadeDuration, 255, true)
            startDelay += delayBetweenItemsAnimation;
            ballView.isEnabled = true
        }
    }

    private fun closeMenu() {
        stopAllRunningAnimations()
        var startDelay = 0L;
        for (i in metaBallsContainerFrameLayout.getChildCount() - 2 downTo 0) {

            val ballView = metaBallsContainerFrameLayout.getChildAt(i)
            animatePosition(ballView, 0f, 0f, startDelay, closeInterpolatorAnimator, closeAnimationDuration)
            val menuItemScaleDown = (closeAnimationDuration * 0.33f).toLong()
            animateScale(ballView, 0.1f, menuItemScaleDown, startDelay + menuItemScaleDown, LinearInterpolator())
            fadeIcon((ballView as ImageView).drawable, startDelay, (closeAnimationDuration * 0.16f).toLong(), 0, false)
            startDelay += delayBetweenItemsAnimation;
        }
    }

    private fun fadeIcon(drawable: Drawable, startDelay: Long, duration: Long, toAlpha: Int, animateDrawable: Boolean) {
        val alhpa = ValueAnimator.ofInt(drawable.alpha, toAlpha).setDuration(duration)
        if (animateDrawable) {
            drawable.alpha = 0
        }
        var hasVectorAnimStarted = false
        alhpa.addUpdateListener { animation ->
            drawable.alpha = animation.animatedValue as Int
            if (animateDrawable && drawable is AnimatedVectorDrawable) {

                if (!hasVectorAnimStarted && drawable.alpha > 0) {
                    drawable.start()
                    hasVectorAnimStarted = true
                }
            }
        }

        alhpa.setStartDelay(startDelay)
        startAnimation(alhpa)
    }

    private fun animatePosition(view: View, x: Float, y: Float, startDelay: Long, interpolator: Interpolator, duration: Long) {
        val translationX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.translationX, x).setDuration(duration)
        translationX.interpolator = interpolator
        translationX.setStartDelay(startDelay)
        startAnimation(translationX)
        val translationY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, y).setDuration(duration)
        translationY.setStartDelay(startDelay)
        translationY.interpolator = interpolator
        startAnimation(translationY)
    }

    private fun animateScale(view: View, scale: Float, duration: Long, startDelay: Long, interpolator: Interpolator = PathInterpolator(.95f, 0f, .07f, 1f)): ObjectAnimator {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, view.scaleX, scale).setDuration(duration)
        scaleX.setStartDelay(startDelay)
        scaleX.interpolator = interpolator
        startAnimation(scaleX)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, view.scaleY, scale).setDuration(duration)
        scaleY.setStartDelay(startDelay)
        scaleY.interpolator = interpolator
        startAnimation(scaleY)
        return scaleX
    }

    private fun getStartAngle(): Float {
        if (positionGravity == PositionGravity.BOTTOM_LEFT)
            return 270f
        else if (positionGravity == PositionGravity.BOTTOM_RIGHT)
            return 180f
        else if (positionGravity == PositionGravity.TOP_RIGHT)
            return 90f
        else if (positionGravity == PositionGravity.TOP_LEFT)
            return 0f
        return 270f
    }

    private fun convertIntToPositionGravity(id: Int): PositionGravity {
        for (f in PositionGravity.values()) {
            if (f.ordinal == id) return f
        }
        return PositionGravity.CENTER
    }
}
