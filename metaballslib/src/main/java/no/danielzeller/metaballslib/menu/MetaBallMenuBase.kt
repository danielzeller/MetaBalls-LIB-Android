package no.danielzeller.metaballslib.menu

import android.animation.*
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
import no.danielzeller.metaballslib.R


enum class PositionGravity {
    CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

abstract class MetaBallMenuBase : FrameLayout {

    /**
     * Sets the data behind this Menu.
     */
    var adapter: MetaBallAdapter = EmptyAdapter()
        set(value) {
            field = value
            addMenuItems(metaBallsContainerFrameLayout)

        }


    /**
     * The delay between each menu item is animated.
     */
    var delayBetweenItemsAnimation = 40L


    /**
     * The duration of the open animation for each menu item. Note delayBetweenItemsAnimation adds
     * to the total duration. Default value is 600ms.
     */
    var openAnimationDuration = 600L


    /**
     * The duration of the close animation for each menu item. Note delayBetweenItemsAnimation adds
     * to the total duration. Default value is 600ms.
     */
    var closeAnimationDuration = 600L


    /**
     * The background color for the main menu button.
     */
    var mainButtonColor = Color.BLACK
        set(value) {
            field = value
            menuButton?.background?.setTint(value)
        }


    /**
     * The tint color for the main menu icon.
     */
    var mainButtonIconColor = Color.WHITE
        set(value) {
            field = value
            openCloseDrawable?.iconDawable?.setTint(value)
        }


    /**
     * The main button icon.
     */
    var mainButtonIcon: Drawable? = null
        set(value) {
            field = value
            openCloseDrawable?.iconDawable = value
            openCloseDrawable?.iconDawable?.setTint(mainButtonIconColor)
        }


    /**
     * The interpolator(easing) used for the open animation.
     */
    lateinit var openInterpolatorAnimator: Interpolator


    /**
     * The interpolator(easing) used for the close animation.
     */
    lateinit var closeInterpolatorAnimator: Interpolator


    /**
     * Sets the origin position og the main menu button and its items.
     * Example: PositionGravity.BOTTOM_RIGHT will place the buttons in the bottom right corner
     * of the View.
     */
    var positionGravity = PositionGravity.CENTER
        set(value) {
            field = value
            rebuildView()
        }


    /**
     * Has the menu been initialized with Views?
     */
    fun isInitialized() = metaBallsContainerFrameLayout.childCount != 0


    /**
     *  Callback for when a menu item is clicked.
     *  @param index the index of the View from the MetaBallAdapter data.
     */
    var onItemSelectedListener: ((index: Int) -> Unit)? = null


    /**
     * Opens or closes the menu dpending on if the menu is open or closed
     */
    fun toggleMenu() {
        if (isMenuOpen) {
            closeMenu()
            openCloseDrawable?.openState()
        } else {
            openMenu()
            openCloseDrawable?.closeState()
        }

        isMenuOpen = !isMenuOpen
        onMenuToggled?.invoke(isMenuOpen)
    }


    /**
     * Is the menu open or closed?
     */
    fun isMenuOpen(): Boolean {
        return isMenuOpen
    }


    /**
     *
     * Callback for when menu is the main menu button is clicked
     */
    var onMenuToggled: ((isOpen: Boolean) -> Unit)? = null


    /**
     *
     * Callback for when menu is fully closed
     */
    var onMenuClosed: (() -> Unit)? = null

    protected lateinit var metaBallsContainerFrameLayout: FrameLayout
    protected var menuButton: ImageView? = null
    private var runningAnimations: ArrayList<ValueAnimator> = ArrayList()
    private var isMenuOpen = false
    private var openCloseDrawable: OpenCloseDrawable? = null


    constructor(context: Context) : super(context) {
        setupBaseViews(context)
        loadAttributesFromXML(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupBaseViews(context)
        loadAttributesFromXML(attrs)
    }


    open fun loadAttributesFromXML(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsMenu,
                0, 0)
        try {
            delayBetweenItemsAnimation = typedArray.getInteger(R.styleable.MetaBallsMenu_delay_between_items_animation, 40).toLong()
            openAnimationDuration = typedArray.getInteger(R.styleable.MetaBallsMenu_open_animation_duration, 600).toLong()
            closeAnimationDuration = typedArray.getInteger(R.styleable.MetaBallsMenu_close_animation_duration, 600).toLong()
            openInterpolatorAnimator = AnimationUtils.loadInterpolator(getContext(), typedArray.getResourceId(R.styleable.MetaBallsMenu_open_interpolator_resource, R.anim.default_menu_interpolator)) as Interpolator
            closeInterpolatorAnimator = AnimationUtils.loadInterpolator(getContext(), typedArray.getResourceId(R.styleable.MetaBallsMenu_close_interpolator_resource, R.anim.default_menu_interpolator)) as Interpolator
            mainButtonColor = typedArray.getColor(R.styleable.MetaBallsMenu_main_button_color, Color.BLACK)
            mainButtonIconColor = typedArray.getColor(R.styleable.MetaBallsMenu_main_button_icon_color, Color.WHITE)
            if (typedArray.hasValue(R.styleable.MetaBallsMenu_main_button_icon)) {
                mainButtonIcon = resources.getDrawable(typedArray.getResourceId(R.styleable.MetaBallsMenu_main_button_icon, 0), null)
            }
            positionGravity = convertIntToPositionGravity(typedArray.getInteger(R.styleable.MetaBallsMenu_position_gravity, PositionGravity.CENTER.ordinal))
        } finally {
            typedArray.recycle()
        }
    }

    protected fun setupBaseViews(context: Context) {
        metaBallsContainerFrameLayout = FrameLayout(context)
        addView(metaBallsContainerFrameLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        metaBallsContainerFrameLayout.setLayerType(View.LAYER_TYPE_HARDWARE, createMetaBallsPaint())
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

    private fun rebuildView() {
        if (isInitialized()) {
            addMenuItems(metaBallsContainerFrameLayout)
        }
    }

    private fun addMenuItems(frameLayout: FrameLayout) {
        isMenuOpen = false
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
        menuButton = createMainButton()
        frameLayout.addView(menuButton, mainButtonLayoutParams)
    }

    private fun createMainButton(): ImageView {
        val menuButton = DecreasedTouchImageView(context, resources.getDimension(R.dimen.main_button_touch_area_size))
        menuButton.setBackgroundResource(R.drawable.gradient_oval)
        menuButton.background.setTint(mainButtonColor)
        openCloseDrawable = OpenCloseDrawable(mainButtonIcon?.mutate(), mainButtonIconColor, context)
        menuButton.setImageDrawable(openCloseDrawable)
        val padding = resources.getDimension(R.dimen.main_button_padding).toInt()
        menuButton.setPadding(padding, padding, padding, padding)
        menuButton.setOnClickListener({ toggleMenu() })
//        menuButton.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.button_scale)
        return menuButton
    }

    private fun createMenuItem(i: Int): ImageView {
        val imageView = DecreasedTouchImageView(context, resources.getDimension(R.dimen.menu_item_touch_area_size))
        imageView.setBackgroundResource(R.drawable.gradient_oval)
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

    protected fun stopAllRunningAnimations() {
        for (anim in runningAnimations) {
            anim.removeAllListeners()
            anim.cancel()
        }
        runningAnimations.clear()
    }

    /**
     * Diffent implementations in CircularMenu and DirectionalMenu
     */
    open fun openMenu() {

    }

    private fun closeMenu() {
        stopAllRunningAnimations()
        var startDelay = 0L;
        for (i in metaBallsContainerFrameLayout.getChildCount() - 2 downTo 0) {

            val ballView = metaBallsContainerFrameLayout.getChildAt(i)
            val positionAnim = animatePosition(ballView, 0f, 0f, startDelay, closeInterpolatorAnimator, closeAnimationDuration)
            val menuItemScaleDown = (closeAnimationDuration * 0.33f).toLong()
            animateScale(ballView, 0.1f, menuItemScaleDown, startDelay + menuItemScaleDown, LinearInterpolator())
            fadeIcon((ballView as ImageView).drawable, startDelay, (closeAnimationDuration * 0.16f).toLong(), 0, false)
            startDelay += delayBetweenItemsAnimation;
            if (i == 0) {
                positionAnim.addListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        onMenuClosed?.invoke()
                    }
                })
            }
        }
        animateScale(menuButton as View, 1f, 300, 0)
    }

    protected fun fadeIcon(drawable: Drawable, startDelay: Long, duration: Long, toAlpha: Int, animateDrawable: Boolean) {
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

    protected fun animatePosition(view: View, x: Float, y: Float, startDelay: Long, interpolator: Interpolator, duration: Long): ValueAnimator {
        val translationX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, view.translationX, x).setDuration(duration)
        translationX.interpolator = interpolator
        translationX.setStartDelay(startDelay)
        startAnimation(translationX)
        val translationY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, y).setDuration(duration)
        translationY.setStartDelay(startDelay)
        translationY.interpolator = interpolator
        startAnimation(translationY)
        return translationY
    }

    protected fun animateScale(view: View, scale: Float, duration: Long, startDelay: Long, interpolator: Interpolator = PathInterpolator(.95f, 0f, .07f, 1f)): ObjectAnimator {
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

    private fun convertIntToPositionGravity(id: Int): PositionGravity {
        for (f in PositionGravity.values()) {
            if (f.ordinal == id) return f
        }
        return PositionGravity.CENTER
    }
}
