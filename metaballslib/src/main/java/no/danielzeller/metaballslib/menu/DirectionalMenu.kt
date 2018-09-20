package no.danielzeller.metaballslib.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import no.danielzeller.metaballslib.R

enum class ExpandDirection {
    EXPAND_DIRECTION_HORIZONTAL, EXPAND_DIRECTION_VERTICAL
}

class DirectionalMenu : MetaBallMenuBase {


    /**
     *
     * In what direction should the menu expand?
     */
    lateinit var expandDirection : ExpandDirection


    /**
     *
     * The margin(spacing) between each menu item
     */
    var marginBetweenMenuItems = 0


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun loadAttributesFromXML(attrs: AttributeSet?) {
        super.loadAttributesFromXML(attrs)
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsMenu,
                0, 0)
        try {
            val intValue = typedArray.getInteger(R.styleable.MetaBallsMenu_expand_direction, 1)
            expandDirection = convertToEnum(intValue)
            marginBetweenMenuItems = typedArray.getLayoutDimension(R.styleable.MetaBallsMenu_margin_between_menu_items, resources.getDimension(R.dimen.default_margin_between_menu_items).toInt())
        } finally {
            typedArray.recycle()
        }
    }

    fun convertToEnum(intValue: Int): ExpandDirection {
        if (ExpandDirection.EXPAND_DIRECTION_HORIZONTAL.ordinal == intValue) {
            return ExpandDirection.EXPAND_DIRECTION_HORIZONTAL
        }

        return ExpandDirection.EXPAND_DIRECTION_VERTICAL
    }


    override fun openMenu() {
        stopAllRunningAnimations()

        val startAngle = getStartAngle()
        var startDelay = 0L
        var marginIncrease = 1
        for (i in 0 until metaBallsContainerFrameLayout.childCount - 1) {
            val angleDeg = startAngle
            val angleRad = (angleDeg * Math.PI / 180f).toFloat()

            //Used when Position_Gravity = CENTER
            val layoutDirection = isNegativeDirection(i)
            if (i > 0 && layoutDirection > 0)
                marginIncrease += 1

            val x = (marginBetweenMenuItems * marginIncrease * layoutDirection) * Math.cos(angleRad.toDouble()).toFloat()
            val y = (marginBetweenMenuItems * marginIncrease * layoutDirection) * Math.sin(angleRad.toDouble()).toFloat()

            val ballView = metaBallsContainerFrameLayout.getChildAt(i)

            val menuItemScaleUpDuration = (openAnimationDuration * 0.125f).toLong()
            val menuItemFadeDuration = (openAnimationDuration * 0.33f).toLong()

            animatePosition(ballView, x, y, startDelay, openInterpolatorAnimator, openAnimationDuration)
            animateScale(ballView, 1.0f, menuItemScaleUpDuration, startDelay)
            fadeIcon((ballView as ImageView).drawable, startDelay + menuItemFadeDuration, menuItemFadeDuration, 255, true)

            startDelay += delayBetweenItemsAnimation
            ballView.isEnabled = true
        }

        animateScale(menuButton as View, 0.78f, 300, 0)
    }

    private fun isNegativeDirection(i: Int): Float {
        if (positionGravity != PositionGravity.CENTER) return 1f

        return if (i % 2 == 0) 1f else -1f
    }

    private fun getStartAngle(): Float {
        if (positionGravity == PositionGravity.BOTTOM_LEFT && expandDirection == ExpandDirection.EXPAND_DIRECTION_VERTICAL)
            return 270f
        else if (positionGravity == PositionGravity.BOTTOM_LEFT && expandDirection == ExpandDirection.EXPAND_DIRECTION_HORIZONTAL)
            return 0f
        else if (positionGravity == PositionGravity.BOTTOM_RIGHT && expandDirection == ExpandDirection.EXPAND_DIRECTION_HORIZONTAL)
            return 180f
        else if (positionGravity == PositionGravity.BOTTOM_RIGHT && expandDirection == ExpandDirection.EXPAND_DIRECTION_VERTICAL)
            return 270f
        else if (positionGravity == PositionGravity.TOP_RIGHT && expandDirection == ExpandDirection.EXPAND_DIRECTION_VERTICAL)
            return 90f
        else if (positionGravity == PositionGravity.TOP_RIGHT && expandDirection == ExpandDirection.EXPAND_DIRECTION_HORIZONTAL)
            return 180f
        else if (positionGravity == PositionGravity.TOP_LEFT && expandDirection == ExpandDirection.EXPAND_DIRECTION_HORIZONTAL)
            return 0f
        else if (positionGravity == PositionGravity.TOP_LEFT && expandDirection == ExpandDirection.EXPAND_DIRECTION_VERTICAL)
            return 90f
        else if (positionGravity == PositionGravity.CENTER && expandDirection == ExpandDirection.EXPAND_DIRECTION_VERTICAL)
            return 90f
        else return 0f
    }
}
