package no.danielzeller.metaballslib

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView


class CircularMenu : MetaBallMenuBase {

    /**
     *
     * The radius of the menu. Default is 90dp.
     */
    var radius = 0


    /**
     *
     * The rotation angle between each menu item. Default is 45 degrees.
     */
    var angleBetweenMenuItems = 45f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun loadAttributesFromXML(attrs: AttributeSet?) {
        super.loadAttributesFromXML(attrs)
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MetaBallsMenu,
                0, 0)
        try {
            radius = typedArray.getLayoutDimension(R.styleable.MetaBallsMenu_radius, resources.getDimension(R.dimen.default_radius).toInt())
            angleBetweenMenuItems = typedArray.getFloat(R.styleable.MetaBallsMenu_angle_between_menu_items, 45f)
        } finally {
            typedArray.recycle()
        }
    }

    override fun openMenu() {
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
}
