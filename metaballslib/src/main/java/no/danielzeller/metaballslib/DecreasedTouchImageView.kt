package no.danielzeller.metaballslib

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent

/**
 * The MetaBall effect clips off the edges of the View. In order to make the onclick area
 * match the clipped size, we use this class with a fixed(smaller) touch area.
 */

class DecreasedTouchImageView(context: Context, val touchAreaSize: Float) : android.support.v7.widget.AppCompatImageView(context) {

    private val tempRect = Rect()

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && !getDecreasedHitRect().contains(ev.x.toInt(), ev.y.toInt())) {
            return false
        } else {
            return super.onTouchEvent(ev)
        }
    }

    fun getDecreasedHitRect(): Rect {
        val widtOffset = (width - touchAreaSize) / 2f
        val heightOffset = (height - touchAreaSize) / 2f

        tempRect.set((widtOffset).toInt(), (heightOffset).toInt(), (width - widtOffset).toInt(), (height - heightOffset).toInt())

        return tempRect;
    }
}
