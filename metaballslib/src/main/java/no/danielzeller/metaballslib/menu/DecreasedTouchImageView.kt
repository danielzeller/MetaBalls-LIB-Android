package no.danielzeller.metaballslib.menu

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent

/**
 * The MetaBall effect clips off the edges of the View. In order to make the onclick area
 * match the clipped size, we use this class with a fixed(smaller) touch area.
 */

class DecreasedTouchImageView(context: Context, private val touchAreaSize: Float) : android.support.v7.widget.AppCompatImageView(context) {

    private val tempRect = Rect()

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (ev.action == MotionEvent.ACTION_DOWN && !getDecreasedHitRect().contains(ev.x.toInt(), ev.y.toInt())) {
            false
        } else {
            super.onTouchEvent(ev)
        }
    }

    private fun getDecreasedHitRect(): Rect {
        val widthOffset = (width - touchAreaSize) / 2f
        val heightOffset = (height - touchAreaSize) / 2f

        tempRect.set((widthOffset).toInt(), (heightOffset).toInt(), (width - widthOffset).toInt(), (height - heightOffset).toInt())

        return tempRect
    }
}
